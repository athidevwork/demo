package dti.pm.notesmgr.impl;

import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.busobjs.PMRecordSetHelper;
import dti.pm.notesmgr.NotesManager;
import dti.pm.notesmgr.dao.NotesDAO;
import dti.pm.policymgr.PolicyManager;
import dti.pm.riskmgr.RiskManager;
import dti.pm.validationmgr.impl.ContinuityRecordSetValidator;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Class provides the implementation details of NotesManager.
 * <p/>
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 24, 2008
 *
 * @author Bhong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class NotesManagerImpl implements NotesManager {
    /**
     * Validate search criteria
     *
     * @param inputRecord
     */
    public void validateSearchCriteria(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateSearchCriteria", new Object[]{inputRecord});
        }
        String policyNo = inputRecord.getStringValue("policyNumber");
        String riskBaseRecordId = inputRecord.getStringValue("riskBaseRecordId");
        String occupant = "";
        if (inputRecord.hasStringValue("occupant")) {
            occupant = inputRecord.getStringValue("occupant");

        }

        if (StringUtils.isBlank(policyNo)) {
            // Check if policy no is empty
            MessageManager.getInstance().addErrorMessage("pm.partTimeNotes.emptyPolicyNo.error");
        }
        else if (StringUtils.isBlank(riskBaseRecordId)) {
            // Check if risk is empty
            MessageManager.getInstance().addErrorMessage("pm.partTimeNotes.emptyRisk.error");
        }
        else {
            // Check if selected has generic risk type SLOT and occupant is empty
            String genericType = getRiskManager().getGenericRiskType(inputRecord);
            if (SLOT.equals(genericType) && StringUtils.isBlank(occupant)) {
                MessageManager.getInstance().addErrorMessage("pm.partTimeNotes.emptyOccupant.error");
            }
        }

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid search criteria.");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateSearchCriteria");
        }
    }

    /**
     * Load all part time notes
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllPartTimeNotes(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllPartTimeNotes", new Object[]{inputRecord});
        }

        // Setup the entitlements load processor
        RecordLoadProcessor lp = new NotesEntitlementRecordLoadProcessor();
        RecordSet rs = getNotesDAO().loadAllPartTimeNotes(inputRecord, lp);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllPartTimeNotes", rs);
        }
        return rs;
    }

    /**
     * Validate policy no
     *
     * @param inputRecord
     * @return Record
     */
    public Record validatePolicyNo(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validatePolicyNo", new Object[]{inputRecord});
        }
        Record result = new Record();
        String policyId = "";
        boolean isValid = true;
        // Check if policy no exists
        String policyNo = inputRecord.getStringValue("policyNumber");
        if (StringUtils.isBlank(policyNo)) {
            isValid = false;
        }
        else {
            Record record = new Record();
            record.setFieldValue("policyNo", policyNo);
            policyId = getPolicyManager().getPolicyId(record);
            if ("-1".equals(policyId)) {
                isValid = false;
            }
            else {
                result.setFieldValue("policyId", policyId);
            }
        }

        if (!isValid) {
            MessageManager.getInstance().addErrorMessage("pm.partTimeNotes.invalidPolicyNo");
        }
        else {
            // Get policy holder
            Record record = new Record();
            record.setFieldValue("policyId", policyId);
            String policyHolder = getPolicyManager().getPolicyHolder(record);
            result.setFieldValue("policyHolder", policyHolder);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validatePolicyNo", result);
        }
        return result;
    }

    /**
     * Get initial values
     *
     * @param inputRecord
     * @return Record
     */
    public Record getInitialValues(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValues", new Object[]{inputRecord});
        }
        Record initialValues = new Record();
        // Default values for new row
        initialValues.setFieldValue("effectiveToDate", OPEN_DATE);
        String policyNo = inputRecord.getStringValue("policyNumber");
        Record record = new Record();
        record.setFieldValue("policyNo", policyNo);
        String policyId = getPolicyManager().getPolicyId(record);
        initialValues.setFieldValue("policyId", policyId);
        initialValues.setFieldValue("riskBaseRecordId", inputRecord.getStringValue("riskBaseRecordId"));
        String entityId;
        if (SLOT.equals(inputRecord.getStringValue("riskTypeCode"))) {
            entityId = inputRecord.getStringValue("occupant");
        }
        else {
            entityId = "0";
        }
        initialValues.setFieldValue("entityId", entityId);

        // Default pageEntitlement initialValues for new added row.
        initialValues.setFieldValue("isEffectiveFromDateEditable", "Y");
        initialValues.setFieldValue("isEffectiveToDateEditable", "Y");
        initialValues.setFieldValue("isNotesEditable", "Y");

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValues", initialValues);
        }
        return initialValues;
    }

    /**
     * Save all part time notes
     *
     * @param inputRecords
     */
    public void saveAllPartTimeNotes(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllPartTimeNotes", new Object[]{inputRecords});
        }
        // Validate all part time notes
        validateAllPartTimeNotes(inputRecords);

        // Save all changes
        RecordSet modifiedRecords = PMRecordSetHelper.setRowStatusOnModifiedRecords(inputRecords);
        getNotesDAO().saveAllPartTimeNotes(modifiedRecords);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllPartTimeNotes");
        }
    }

    /**
     * Validate all part time information
     *
     * @param inputRecords
     */
    protected void validateAllPartTimeNotes(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAllPartTimeNotes", new Object[]{inputRecords});
        }

        // Check date for new/modified rows
        Iterator iter = inputRecords.getRecords();
        while (iter.hasNext()) {
            Record rec = (Record) iter.next();
            String rowNum = String.valueOf(rec.getRecordNumber() + 1);
            String rowId = rec.getStringValue("renewPartTimeId");
            if (rec.isUpdateIndicatorInserted() || rec.isUpdateIndicatorUpdated()) {
                if (rec.getDateValue("effectiveToDate").before(rec.getDateValue("effectiveFromDate"))) {
                    MessageManager.getInstance().addErrorMessage("pm.partTimeNotes.invalidDate.error",
                        new String[]{rowNum}, "effectiveToDate", rowId);
                    throw new ValidationException("Invalid effective to date.");
                }
            }
        }

        // Set open date to the effective from date of new record if exists
        RecordSet insertedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.INSERTED));
        boolean isDateChanged = false;
        Record rec = inputRecords.getRecord(0);
        if (insertedRecords.getSize() == 1) {
            String sEffDate = insertedRecords.getRecord(0).getStringValue("effectiveFromDate");
            String sExpDate = rec.getStringValue("effectiveToDate");
            if (OPEN_DATE.equals(sExpDate)) {
                isDateChanged = true;
                rec.setFieldValue("effectiveToDate", sEffDate);
            }
        }

        // Check if new added row has overlap with existing rows
        ContinuityRecordSetValidator continuityValidator = new ContinuityRecordSetValidator(
            "effectiveFromDate", "effectiveToDate", "renewPartTimeId",
            "pm.partTimeNotes.overlapTimePeriod.error");
        continuityValidator.validate(inputRecords);
        // rollback changes in recordSet
        if (isDateChanged) {
            rec.setFieldValue("effectiveToDate", OPEN_DATE);
        }

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages())
            throw new ValidationException("Invalid part time notes data.");

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateAllPartTimeNotes");
        }
    }

    /**
     * verifyConfig
     */
    public void verifyConfig() {
        if (getNotesDAO() == null) {
            throw new ConfigurationException("The required property 'notesDAO' is missing.");
        }
        if (getPolicyManager() == null) {
            throw new ConfigurationException("The required property 'policyManager' is missing.");
        }
        if (getRiskManager() == null) {
            throw new ConfigurationException("The required property 'riskManager' is missing.");
        }
    }

    public PolicyManager getPolicyManager() {
        return m_policyManager;
    }

    public void setPolicyManager(PolicyManager policyManager) {
        m_policyManager = policyManager;
    }

    public RiskManager getRiskManager() {
        return m_riskManager;
    }

    public void setRiskManager(RiskManager riskManager) {
        m_riskManager = riskManager;
    }

    public NotesDAO getNotesDAO() {
        return m_notesDAO;
    }

    public void setNotesDAO(NotesDAO notesDAO) {
        m_notesDAO = notesDAO;
    }

    private static final String SLOT = "SLOT";
    private static final String OPEN_DATE = "01/01/3000";

    private NotesDAO m_notesDAO;
    private PolicyManager m_policyManager;
    private RiskManager m_riskManager;
}
