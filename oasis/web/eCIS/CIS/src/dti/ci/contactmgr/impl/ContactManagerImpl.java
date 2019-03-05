package dti.ci.contactmgr.impl;

import dti.ci.contactmgr.ContactFields;
import dti.ci.contactmgr.ContactManager;
import dti.ci.contactmgr.dao.ContactDAO;
import dti.ci.core.CIFields;
import dti.ci.validationmgr.impl.DuplicationRecordSetValidator;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.OasisRecordSetHelper;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Business Object to handle Contact.
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 22, 2005
 *
 * @author Hong Yuan
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/25/2012       bzhu        135841 - format fax number for saving.
 * 12/25/2012       Elvin       140162 - format social security no for saving
 * 03/17/2014       Elvin       Issue 151772: add duplicate checking when saving contact
 * 03/28/2014       Elvin       Issue 151772: use getUpperCaseFieldNameSet instead of getFieldNameList
 * ---------------------------------------------------
*/

public class ContactManagerImpl implements ContactManager {
    /**
     * Load the contacts of an entity.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public RecordSet loadAllContact(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllContact", new Object[]{inputRecord,});
        }

        Record record = new Record();
        CIFields.setEntityId(record, CIFields.getPk(inputRecord));

        RecordSet rs = getContactDAO().loadAllContact(record, new ContactListLoadProcessor());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllContact", rs);
        }

        return rs;
    }

    /**
     * Save the contacts of an entity.
     *
     * @param rs
     * @return
     */
    @Override
    public int saveAllContact(RecordSet rs) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllContact", new Object[]{rs});
        }
        
        validateInput(rs);

        RecordSet changedRecordSet = OasisRecordSetHelper.setRowStatusOnModifiedRecords(rs);
        // remove '-' character for fax number and social security no if any before saving.
        convertFaxNumberAndSocialSecurityNo(changedRecordSet);

        int count = 0;
        if (changedRecordSet.getSize() > 0) {
            count = getContactDAO().saveAllContact(changedRecordSet);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllContact", count);
        }

        return count;
    }
    
    private void validateInput(RecordSet rs) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateInput", new Object[]{rs});
        }

        //duplicate checking base on CIS_CONTACT_CHK_FLD
        String allFields = SysParmProvider.getInstance().getSysParm("CIS_CONTACT_CHK_FLD", "");
        if (!StringUtils.isBlank(allFields)) {
            String[] originalFields = allFields.split(",");
            List<String> fieldsList = new ArrayList<String>();
            //confirm field id in RecordSet
            for (int i = 0; i< originalFields.length; i++) {
                if (!StringUtils.isBlank(originalFields[i]) && rs.getUpperCaseFieldNameSet(true).contains(originalFields[i].toUpperCase())) {
                    fieldsList.add(originalFields[i]);
                }
            }

            String[] checkFields = fieldsList.toArray(new String[fieldsList.size()]);
            if (checkFields.length > 0) {
                DuplicationRecordSetValidator dupValidator = new DuplicationRecordSetValidator(checkFields, "contactId", "ci.entity.message.contact.duplicate", false);
                dupValidator.setCaseSensitive(false);
                dupValidator.validate(rs);

                if (MessageManager.getInstance().hasErrorMessages()) {
                    throw new ValidationException("Input validation error.");
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateInput");
        }
    }

    private void convertFaxNumberAndSocialSecurityNo(RecordSet rs)
    {
        Iterator itor = rs.getRecords();
        while (itor.hasNext()) {
            Record record = (Record) itor.next();
            Object obj = record.getField(ContactFields.FAX_PHONE_NUMBER).getValue();
            if (obj != null)
            {
                record.getField(ContactFields.FAX_PHONE_NUMBER).setValue(obj.toString().replace("-", ""));
            }
            obj = record.getField(ContactFields.SOCIAL_SECURITY_NUMBER).getValue();
            if (obj != null)
            {
                record.getField(ContactFields.SOCIAL_SECURITY_NUMBER).setValue(obj.toString().replace("-", ""));
            }
        }
    }

    /**
     * Save contact number
     *
     * @param inputRecord
     */
    public Record saveContact(Record inputRecord){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveContact", new Object[]{inputRecord});
        }

        Record recResult = getContactDAO().saveContact(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveContact");
        }
        return recResult;
    }

    public void verifyConfig() {
        if (getContactDAO() == null) {
            throw new ConfigurationException("The required property 'contactDAO' is missing.");
        }
    }

    public ContactDAO getContactDAO() {
        return m_contactDAO;
    }

    public void setContactDAO(ContactDAO contactDAO) {
        m_contactDAO = contactDAO;
    }

    private ContactDAO m_contactDAO;
}
