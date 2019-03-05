package dti.ci.demographic.clientmgr.specialhandlingmgr.impl;

import dti.ci.demographic.clientmgr.specialhandlingmgr.SpecialHandlingManager;
import dti.ci.demographic.clientmgr.specialhandlingmgr.dao.SpecialHandlingDAO;
import dti.ci.validationmgr.impl.ContinuityRecordSetValidator;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.struts.AddSelectIndLoadProcessor;
import dti.oasis.util.DateUtils;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * <p/>
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 30, 2008
 *
 * @author
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/15/2010       Kenney      Issue#106087: Load initial values when adding special handling
 * 09/11/2013       Elvin       Issue 144342: allow lapse date inputs to keep the same with C/S version
 * ---------------------------------------------------
 */
public class SpecialHandlingManagerImpl implements SpecialHandlingManager {


    public RecordSet loadSpecialHandlingsByEntity(long entityFK) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadSpecialHandlingsByEntity");
        }

        Record input = new Record();
        input.setFieldValue("entityId", new Long(entityFK));

        /* Setup the entitlements load processor */
        RecordLoadProcessor entitlementRLP = AddSelectIndLoadProcessor.getInstance();

        /* Gets special handling record set */
        RecordSet rs = getSpecialHandlingDAO().loadSpecialHandlingsByEntity(input, entitlementRLP);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadSpecialHandlingsByEntity", rs);
        }
        return rs;
    }

    /**
     * Save all input records with UPDATE_IND set to 'Y' - updated, 'I' - inserted, or 'D' - deleted.
     *
     * @param strUserId
     * @param inputRecords a set of Records, each with the updated SpecialHandling info
     * @return the number of rows updated.
     */
    public int saveAllSpecialHandlings(String strUserId, RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllSpecialHandlings", new Object[]{inputRecords});

        // get the initial values from wb bench
        Record outputRecord = getWorkbenchConfiguration().getDefaultValues(MAINTAIN_SPECIAL_HANDLING_ACTION_CLASS_NAME);

        int updateCount = 0;

        // Determine if anything has changed
        RecordSet insertedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.INSERTED));
        RecordSet updatedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));
        RecordSet deletedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.DELETED));

        // If a change has occurred
        if ((insertedRecords.getSize() + updatedRecords.getSize() + deletedRecords.getSize()) > 0) {

            // Validate the input SpecialHandlings prior to saving them.
            validateAllSpecialHandlings(inputRecords);

            if (insertedRecords.getSize() > 0) {
                insertedRecords.setFieldValueOnAll("rowStatus", "NEW");
                insertedRecords.setFieldValueOnAll("handlingUserid", strUserId);
                updateCount += getSpecialHandlingDAO().addAllSpecialHandlings(insertedRecords);
            }

            if (updatedRecords.getSize() > 0) {
                updatedRecords.setFieldValueOnAll("rowStatus", "MODIFIED");
                updateCount += getSpecialHandlingDAO().updateAllSpecialHandlings(updatedRecords);
            }

            if (deletedRecords.getSize() > 0) {
                deletedRecords.setFieldValueOnAll("rowStatus", "DELETED");
                updateCount += getSpecialHandlingDAO().updateAllSpecialHandlings(deletedRecords);
            }
        }

        l.exiting(getClass().getName(), "saveAllSpecialHandlings", new Integer(updateCount));
        return updateCount;
    }

    /**
     * method to get the initial value when adding special handling
     *
     * @param inputRecord
     * @return record
     */
    public Record getInitialValuesForAddSpecialHandling(Record inputRecord){
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForAddSpecialHandling", new Object[]{inputRecord});
        Record outRecord = new Record();

        // get the default values from the workbench configuration for this page
        Record defaultValuesRecord = getWorkbenchConfiguration().getDefaultValues(MAINTAIN_SPECIAL_HANDLING_ACTION_CLASS_NAME);

        //start with the default record
        outRecord.setFields(defaultValuesRecord);

        //overlay it with inputRecord
        outRecord.setFields(inputRecord);

        l.exiting(getClass().toString(), "getInitialValuesForAddSpecialHandling", outRecord);
        return outRecord;
    }

    protected void validateAllSpecialHandlings(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAllSpecialHandlings", new Object[]{inputRecords});
        }

        Iterator iter = inputRecords.getRecords();

        Date lastEndDate = DateUtils.parseDate("01/01/3000");        

        while (iter.hasNext()) {
            Record rec = (Record) iter.next();
            String rowNum = String.valueOf(rec.getRecordNumber() + 1);
            String rowId = rec.getStringValue("specialHandlingId");

            // If modified perform first set of validations
            if (rec.isUpdateIndicatorInserted() || rec.isUpdateIndicatorUpdated()) {

                if (StringUtils.isBlank(rec.getStringValue("handlingCategoryCode")) || "-1".equals(rec.getStringValue("handlingCategoryCode"))) {
                    MessageManager.getInstance().addErrorMessage("ci.maintainSpecialHandling.invalidCategory.error",
                            new String[]{rowNum, rec.getStringValue("handlingCategoryCodeLOVLABEL")}, "handlingCategoryCode", rowId);
                }

                if (!FormatUtils.isDate(rec.getStringValue("effectiveFromDate"))) {
                    MessageManager.getInstance().addErrorMessage("ci.maintainSpecialHandling.invalidEffectiveFromDate.error",
                            new String[]{rowNum, rec.getStringValue("effectiveFromDate")}, "effectiveFromDate", rowId);
                    rec.setFieldValue("effectiveFromDate", "");
                    //throw new ValidationException("Invalid effective from date.");
                }

                if (StringUtils.isBlank(rec.getStringValue("effectiveToDate"))) {
                    rec.setFieldValue("effectiveToDate", lastEndDate);
                } else if (!FormatUtils.isDate(rec.getStringValue("effectiveToDate"))) {
                    MessageManager.getInstance().addErrorMessage("ci.maintainSpecialHandling.invalidEffectiveToDate.error",
                            new String[]{rowNum, rec.getStringValue("effectiveToDate")}, "effectiveToDate", rowId);
                    rec.setFieldValue("effectiveToDate", "");
                    //throw new ValidationException("Invalid effective to date.");
                }

                if (MessageManager.getInstance().hasErrorMessages())
                    throw new ValidationException("Invalid Data in Special Handling Grid.");

                // Validation:  End Date must be greater than or equal to Start Date
                if (rec.getDateValue("effectiveToDate").before(rec.getDateValue("effectiveFromDate"))) {
                    MessageManager.getInstance().addErrorMessage("ci.maintainSpecialHandling.EndDateBeforeStartDate.error",
                            new String[]{rowNum}, "effectiveToDate", rowId);
                    throw new ValidationException("End Date before Start Date.");
                }
            }
            // stop validating the remaining records if we found problem(s) already
            if (MessageManager.getInstance().hasErrorMessages())
                break;
        }

        // Validation :  Validate continuity
        ContinuityRecordSetValidator continuityValidator = new ContinuityRecordSetValidator(
                "effectiveFromDate", "effectiveToDate", "specialHandlingId", "ci.maintainSpecialHandling.invalidContinuity.error", new String[]{"handlingCategoryCode"}, new String[]{"handlingCategoryCode"}
                , false);
        continuityValidator.validate(inputRecords);

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid SpecialHandling data.");
        }

        l.exiting(getClass().getName(), "validateAllSpecialHandlings");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getSpecialHandlingDAO() == null) {
            throw new ConfigurationException("The required property 'SpecialHandlingDAO' is missing.");
        }
        if (getWorkbenchConfiguration() == null)
            throw new ConfigurationException("The required property 'workbenchConfiguration' is missing.");
    }

    public SpecialHandlingDAO getSpecialHandlingDAO() {
        return m_specialHandlingDAO;
    }

    public void setSpecialHandlingDAO(SpecialHandlingDAO specialHandlingDAO) {
        m_specialHandlingDAO = specialHandlingDAO;
    }

    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return m_workbenchConfiguration;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration) {
        m_workbenchConfiguration = workbenchConfiguration;
    }

    private SpecialHandlingDAO m_specialHandlingDAO;

    private WorkbenchConfiguration m_workbenchConfiguration;

    private final String MAINTAIN_SPECIAL_HANDLING_ACTION_CLASS_NAME = "dti.ci.demographic.clientmgr.specialhandlingmgr.struts.MaintainSpecialHandlingAction";
}
