package dti.ci.entityaddlemailmgr.impl;

import dti.ci.entityaddlemailmgr.EntityAddlEmailFields;
import dti.ci.entityaddlemailmgr.EntityAddlEmailManager;
import dti.ci.entityaddlemailmgr.dao.EntityAddlEmailDAO;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/11/13
 *
 * @author bzhu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class EntityAddlEmailManagerImpl implements EntityAddlEmailManager {

    /**
     * Load the entity additional email list.
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadEntityAddlEmailList(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadEntityAddlEmailList", new Object[]{inputRecord});
        }

        // Load entity additional email
        RecordSet rs = getEntityAddlEmailDAO().loadEntityAddlEmailList(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadEntityAddlEmailList", rs);
        }
        return rs;
    }

    /**
     * Save the entity additional email list.
     *
     * @param inputRecords
     * @return
     */
    public int updateEntityAddlEmailList(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "updateEntityAddlEmailList", new Object[]{inputRecords});
        }

        int updateCount = 0;

        RecordSet insertedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.INSERTED));
        RecordSet updatedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));
        RecordSet deletedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.DELETED));

        if (deletedRecords.getSize() > 0) {
            updateCount += getEntityAddlEmailDAO().deleteEntityAddlEmail(deletedRecords);
        }
        // If a change has occurred
        if ((insertedRecords.getSize() + updatedRecords.getSize()) > 0) {

            validateAllEntityAddlEmail(inputRecords);

            if (updatedRecords.getSize() > 0) {
                updateCount += getEntityAddlEmailDAO().updateEntityAddlEmail(updatedRecords);
            }

            if (insertedRecords.getSize() > 0) {
                updateCount += getEntityAddlEmailDAO().insertEntityAddlEmail(insertedRecords);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "updateEntityAddlEmailList", new Integer(updateCount));
        }

        return updateCount;
    }

    /**
     * validate additional email for entities.
     *
     * @param inputRecords
     * @return
     */
    private void validateAllEntityAddlEmail(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAllEntityAddlEmail", new Object[]{inputRecords});
        }

        for(Record record : inputRecords.getRecordList()) {
            if (record.isUpdateIndicatorInserted() || record.isUpdateIndicatorUpdated()) {
                if (StringUtils.isBlank(EntityAddlEmailFields.getEmailAddress(record))) {
                    MessageManager.getInstance().addErrorMessage("ci.entity.addlemail.error.isRequired",
                            new String[]{"Email Address"});
                    break;
                }
                if (!StringUtils.isValidEmailAddress(EntityAddlEmailFields.getEmailAddress(record))) {
                    MessageManager.getInstance().addErrorMessage("ci.common.error.format.email",
                            new String[]{"Email Address", "username@domain.topleveldomain"});
                    break;
                }
            }
        }

        validateDuplicateModule(inputRecords);

        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid Data in Entity Additional Email Grid.");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateAllEntityAddlEmail", "validated");
        }
    }

    /**
     * validate duplicate module.
     *
     * @param inputRecords
     * @return
     */
    private void validateDuplicateModule (RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateDuplicateModule", new Object[]{inputRecords});
        }

        List<String> list = new ArrayList<String>();
        for (int i = 0; i < inputRecords.getSize(); i++) {
            Record source = inputRecords.getRecord(i);
            String moduleCode = EntityAddlEmailFields.getModuleCode(source);
            if (!source.isUpdateIndicatorDeleted()) {
                if (list.contains(moduleCode)){
                    MessageManager.getInstance().addErrorMessage("ci.entity.addlemail.error.isUnique",
                            new String[]{moduleCode});
                    break;
                }
                list.add(moduleCode);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateDuplicateModule", "no duplicate module");
        }
    }
    /**
     * verifyConfig
     */
    public void verifyConfig() {
        if (getEntityAddlEmailDAO() == null)
            throw new ConfigurationException("The required property 'EntityAddlEmailDAO' is missing.");
    }

    public EntityAddlEmailDAO getEntityAddlEmailDAO() {
        return m_entityAddlEmailDAO;
    }

    public void setEntityAddlEmailDAO(EntityAddlEmailDAO entityAddlEmailDAO) {
        this.m_entityAddlEmailDAO = entityAddlEmailDAO;
    }

    private EntityAddlEmailDAO m_entityAddlEmailDAO;
}
