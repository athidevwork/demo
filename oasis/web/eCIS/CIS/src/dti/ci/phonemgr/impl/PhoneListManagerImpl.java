package dti.ci.phonemgr.impl;

import dti.ci.addressmgr.dao.AddressDAO;
import dti.ci.phonemgr.PhoneListManager;
import dti.ci.phonemgr.dao.PhoneListDAO;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.util.*;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.recordset.Record;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.OasisRecordSetHelper;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.app.ApplicationContext;
import dti.oasis.messagemgr.MessageManager;
import org.apache.struts.util.LabelValueBean;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Helper class for Phone Number List.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author Gerald C. Carney
 *         Date:   Mar 22, 2004
 *         <p/>
 *         Revision Date    Revised By  Description
 *         -------------------------------------------------------------------
 *         04/01/2005       HXY         Removed sigleton implementation.
 *         04/14/2005       HXY         Added transaction commit logic.
 *         04/18/2005       HXY         Created one instance DAO.
 *         09/24/2008       Larry       Issue 86826 DB connection leakage change
 *         7/2/2010         Blake       Add All source function for issue 103463
 *         -------------------------------------------------------------------
 */

public class PhoneListManagerImpl implements PhoneListManager {

    /**
     * future cis refactoring consideration:
     *  logic can be getting from CI_WEB_DEMOGRAPHIC.get_source_list_for_phone
     *
     * Creates a list of values of possible sources for phone numbers.
     *
     * @param inputRecord  param of the entity who is the source of the phone numbers.
     * @return ArrayList - ArrayList of LabelValueBean.
     * @throws Exception
     */
    public ArrayList createSourceRecordLOV(Record inputRecord){
        ArrayList resultList = new ArrayList();
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPhoneNumberList", new Object[]{inputRecord});
        }

        RecordSet rs = getPhoneListDAO().createSourceRecordLOV(inputRecord);
        if(rs.getSize()>0) {
            for(int i = 0;i < rs.getSize(); i++){
                Record r = rs.getRecordList().get(i);
                resultList.add(new LabelValueBean(r.getStringValue("sourceDescription"), r.getStringValue("sourceId")));
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPhoneNumberList", rs);
        }
        return resultList;
    }

    /**
     * Retrieve a list of phone numbers for a given source.
     *
     * @param conn     Connection object.
     * @param entityPK PK of the entity who is either the source of the phone numbers or
     *                 the source of the addresses that are the sources of the phone numbers.
     * @param srcFK    The source record FK of the phone numbers (entity or address).
     * @return DisconnectedResultSet - The result set with the phone numbers.
     * @throws Exception
     */
    public DisconnectedResultSet retrievePhoneNumberList(Connection conn,
                                                         String entityPK, String srcFK)
            throws Exception {
        String methodName = "retrievePhoneNumberList";
        Logger lggr = LogUtils.enterLog(this.getClass(),
                methodName, new Object[]{conn, entityPK, srcFK});
        srcFK = this.transformSourceFK(srcFK);
        try {
            return null;//getPhoneListDAO().retrieveDataResultSet(conn, entityPK, srcFK);
        } catch (Exception e) {
            try {
                lggr.throwing(this.getClass().getName(), methodName, e);
            } catch (Throwable ignore) {
            }
            throw e;
        } finally {
            lggr.exiting(this.getClass().getName(), methodName);
        }
    }

    /**
     * Retrieve a list of all phone numbers for all addresses for a given entity.
     *
     * @param conn     Connection object.
     * @param entityPK PK of the entity who is either the source of the addresses that are the sources of the phone numbers.
     * @return DisconnectedResultSet - The result set with the phone numbers.
     * @throws Exception
     */
    public DisconnectedResultSet retrievePhoneNumberListForEntityAddresses(Connection conn, String entityPK)
            throws Exception {
        String methodName = "retrievePhoneNumberListForEntityAddresses";
        Logger lggr = LogUtils.enterLog(this.getClass(),
                methodName, new Object[]{conn, entityPK});
        try {
            return null ;//getPhoneListDAO().retrieveAllNumsForEntAddresses(conn, entityPK);
        } catch (Exception e) {
            try {
                lggr.throwing(this.getClass().getName(), methodName, e);
            } catch (Throwable ignore) {
            }
            throw e;
        } finally {
            lggr.exiting(this.getClass().getName(), methodName);
        }
    }

    /**
     * Indicates whether or not a source record FK for a phone number represents an
     * expired source (string will begin with "X").
     *
     * @param srcRecFK The source record FK.
     * @return boolean - True if expired, false if not.
     */
    public boolean isSourceExpired(String srcRecFK) {
        if (!StringUtils.isBlank(srcRecFK) &&
                srcRecFK.substring(0, 1).equalsIgnoreCase(EXPIRED_SOURCE_PREFIX)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Removes the one character ("X") at the beginning of a source record FK that
     * indicates that the source is expired.
     *
     * @param srcRecFK The soruce record FK.
     * @return String
     */
    public String transformSourceFK(String srcRecFK) {
        if (!this.isSourceExpired(srcRecFK)) {
            return srcRecFK;
        } else {
            return srcRecFK.substring(1);
        }
    }

    /**
     * Save all phone number
     *
     * @param inputRecords
     */
    public void saveAllPhoneNumber(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllPhoneNumber", new Object[]{inputRecords,});
        }

        RecordSet changedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
                new String[]{UpdateIndicator.DELETED, UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));

        changedRecords = OasisRecordSetHelper.setRowStatusOnModifiedRecords(changedRecords);
        try {
            getPhoneListDAO().saveAllPhoneNumber(changedRecords);
        } catch (AppException ae) {
            l.throwing(getClass().getName(), "Initialize DAO fail.", ae);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllPhoneNumber");
        }
    }

    /** for now, we use ApplicationContext.getBean to get the reference to WorkbenchConfiguration
     *  Once  we fully refactor the page, we can actually move it to be part of the spring configuration
     * @param input
     * @return
     */
    public Record getIntialValuesForAddingPhoneNumber(Record input){
        Logger l = LogUtils.enterLog(getClass(), "getIntialValuesForAddingPhoneNumber", input);
        Record output = new Record();
        if (ApplicationContext.getInstance().hasBean("WorkbenchConfiguration")) {
            WorkbenchConfiguration config = (WorkbenchConfiguration) ApplicationContext.getInstance().getBean("WorkbenchConfiguration");
            Record initValues = config.getDefaultValues(input.getStringValue("className"));
            output = removeGridSuffixForInitialValues(initValues);

            output.setFieldValue("sourceRecordId", input.getStringValue("sourceRecordId"));
            if (input.getStringValue("id").equalsIgnoreCase(input.getStringValue("sourceRecordId"))) {
                output.setFieldValue("sourceTableName", "ENTITY");
            } else {
                output.setFieldValue("sourceTableName", "ADDRESS");
            }
        } else {
            MessageManager.getInstance().addErrorMessage("ci.phoneNumberList.initValue.error", new String[]{"missing configuration for bean: WorkbenchConfiguration"});
            l.severe(getClass().getName() + ".getIntialValuesForAddingPhoneNumber: missing configuration for bean: WorkbenchConfiguration");
        }
        l.exiting(getClass().getName(), "getIntialValuesForAddingPhoneNumber", output);
        return output;
    }

    /**
     * for the fields with id ending with _GH, remove it.
     * @param initValues
     * @return
     */
    private Record removeGridSuffixForInitialValues(Record initValues) {
        Logger l = LogUtils.enterLog(getClass(), "getIntialValuesForAddingPhoneNumber", initValues);
        Record outRecord = new Record();
        for (Iterator it = initValues.getFieldNames(); it.hasNext();) {
            String fieldId = (String) it.next();
            if (fieldId.endsWith("_GH")) {
                outRecord.setFieldValue(fieldId.replaceAll("_GH", ""), initValues.getField(fieldId));
            } else {
                outRecord.setFieldValue(fieldId, initValues.getField(fieldId));
            }

        }
        l.exiting(getClass().getName(), "removeGridSuffixForInitialValues", outRecord);
        return outRecord;
    }

         /**
     * To load PhoneNumberList
     *
     * @param inputRecord
     * @return
     */
     public RecordSet getPhoneNumberList(Record inputRecord){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPhoneNumberList", new Object[]{inputRecord});
        }

        RecordSet rs = getPhoneListDAO().getPhoneNumberList(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPhoneNumberList", rs);
        }
        return rs;
     }

    /**
     * Save phone number
     *
     * @param inputRecord
     */
    public Record savePhoneNumber(Record inputRecord){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "savePhoneNumber", new Object[]{inputRecord});
        }

        Record recResult = getPhoneListDAO().savePhoneNumber(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "savePhoneNumber");
        }
        return recResult;
    }

    /**
     * Save phone number for web service
     *
     * @param inputRecord
     */
    public Record savePhoneNumberWs(Record inputRecord){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "savePhoneNumberWs", new Object[]{inputRecord});
        }

        Record recResult = getPhoneListDAO().savePhoneNumberWs(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "savePhoneNumberWs");
        }
        return recResult;
    }

    public void verifyConfig() {
        if (getPhoneListDAO() == null) {
            throw new ConfigurationException("The required property 'getPhoneListDAO' is missing.");
        }
    }

    public PhoneListDAO getPhoneListDAO() {
        return phoneListDAO;
    }

    public void setPhoneListDAO(PhoneListDAO phoneListDAO) {
        this.phoneListDAO = phoneListDAO;
    }

    private PhoneListDAO phoneListDAO;
  }
