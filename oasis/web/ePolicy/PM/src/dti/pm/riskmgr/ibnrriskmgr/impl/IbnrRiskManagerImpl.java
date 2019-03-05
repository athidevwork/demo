package dti.pm.riskmgr.ibnrriskmgr.impl;


import dti.cs.data.dbutility.DBUtilityManager;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.OasisRecordSetHelper;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordLoadProcessorChainManager;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.workflowmgr.WorkflowAgent;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.busobjs.TransactionCode;
import dti.pm.entitymgr.EntityManager;
import dti.pm.policymgr.PolicyFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.premiummgr.PremiumAccountingFields;
import dti.pm.riskmgr.RiskFields;
import dti.pm.riskmgr.ibnrriskmgr.IbnrRiskManager;
import dti.pm.riskmgr.ibnrriskmgr.InactiveRiskFields;
import dti.pm.riskmgr.ibnrriskmgr.dao.IbnrRiskDAO;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;

import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Class provides the implementation details of IbnrRiskManager Interface.
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 07, 2011
 *
 * @author Dzhang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/04/2012       wfu         127802 - Modified getInitialValuesForAddInactiveRisk to set initial values for inactive risk entity.
 * ---------------------------------------------------
 */
public class IbnrRiskManagerImpl implements IbnrRiskManager {

    /**
     * Returns a RecordSet loaded with list of available associate risk types for the provided
     * policy information.
     *
     * @param inputRecord Record contains input values
     * @return RecordSet a RecordSet loaded with list of available risks.
     */
    public RecordSet loadAllIbnrRiskType(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllIbnrRiskType", new Object[]{inputRecord});
        }

        RecordSet rs = getIbnrRiskDAO().loadAllIbnrRiskType(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllIbnrRiskType", rs);
        }
        return rs;
    }

    /**
     * Change associated risk
     *
     * @param inputRecord Record contains input values
     */
    public void processChangeAssociatedRisk(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processChangeAssociatedRisk", new Object[]{inputRecord});
        }

        getIbnrRiskDAO().processChangeAssociatedRisk(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "processChangeAssociatedRisk");
        }
    }

    /**
     * load all the risks exist in a given policy that have IBNR inactive risks associated to them
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  input records that contains key information
     * @return a RecordSet loaded with list of available associated risk data.
     */
    public RecordSet loadAllAssociatedRisk(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAssociatedRisk", new Object[]{inputRecord});
        }

        inputRecord.setFields(policyHeader.toRecord());
        TransactionFields.setTransactionEffectiveFromDate(inputRecord,policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        PMCommonFields.setRecordModeCode(inputRecord, getInactiveRecordMode(policyHeader));
        RecordLoadProcessor loadProcessor = new AssociatedRiskEntitlementRecordLoadProcessor(policyHeader);
        RecordSet outRecordSet = getIbnrRiskDAO().loadAllAssociatedRisk(inputRecord, loadProcessor);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAssociatedRisk", outRecordSet);
        }
        return outRecordSet;
    }

    /**
     * load all the risks exist in a given policy that have IBNR inactive risks associated to them
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  input records that contains key information
     * @return a RecordSet loaded with list of available IBNR Inactive risk data.
     */
    public RecordSet loadAllIbnrInactiveRisk(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllIbnrInactiveRisk", new Object[]{inputRecord});
        }

        inputRecord.setFields(policyHeader.toRecord());
        TransactionFields.setTransactionEffectiveFromDate(inputRecord,policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        PMCommonFields.setRecordModeCode(inputRecord, getInactiveRecordMode(policyHeader));
        RecordLoadProcessor entitlementRLP = new InactiveRiskEntitlementRecordLoadProcessor(policyHeader);
        RecordLoadProcessor rowStyleLp = new InactiveRiskRowStyleRecordLoadProcessor();
        RecordLoadProcessor loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(rowStyleLp, entitlementRLP);

        RecordSet outRecordSet = getIbnrRiskDAO().loadAllIbnrInactiveRisk(inputRecord, loadProcessor);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllIbnrInactiveRisk", outRecordSet);
        }
        return outRecordSet;
    }

    /**
     * load all the risks exist in a given policy that have IBNR inactive risks associated to them
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  input records that contains key information
     * @return a RecordSet loaded with list of available associated risk for inactive risk data.
     */
    public RecordSet loadAllAssociatedRiskForInactiveRisk(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAssociatedRiskForInactiveRisk", new Object[]{inputRecord});
        }
        
        inputRecord.setFields(policyHeader.toRecord());
        PMCommonFields.setRecordModeCode(inputRecord, getInactiveRecordMode(policyHeader));
        RecordLoadProcessor entitlementRLP = new AssociatedRiskForInactiveRiskEntitlementRecordLoadProcessor(policyHeader);
        RecordSet outRecordSet = getIbnrRiskDAO().loadAllAssociatedRiskForIbnrInactiveRisk(inputRecord, entitlementRLP);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAssociatedRiskForInactiveRisk", outRecordSet);
        }
        return outRecordSet;
    }

    /**
     * Get initial values for associated risk
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a record loaded with user entered data
     * @return a Record loaded with initial values
     */
    public Record getInitialValuesForAddAssociatedRisk(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForAddAssociatedRisk", new Object[]{policyHeader, inputRecord});
        }

        Record outputRecord = new Record();
        InactiveRiskFields.setRiskBaseRecordId(outputRecord, InactiveRiskFields.getRiskBaseRecordId(inputRecord));
        InactiveRiskFields.setRiskEffectiveFromDate(outputRecord, InactiveRiskFields.getRiskEffectiveFromDate(inputRecord));
        InactiveRiskFields.setRiskEffectiveToDate(outputRecord, InactiveRiskFields.getRiskEffectiveToDate(inputRecord));
        InactiveRiskFields.setAssociatedEntityId(outputRecord, InactiveRiskFields.getAssociatedEntityId(inputRecord));
        InactiveRiskFields.setAssociatedRiskName(outputRecord, InactiveRiskFields.getAssociatedRiskName(inputRecord));
        InactiveRiskFields.setProductCoverageCode(outputRecord, InactiveRiskFields.getProductCoverageCode(inputRecord));
        PMCommonFields.setRecordModeCode(outputRecord, RecordMode.TEMP);
        AssociatedRiskEntitlementRecordLoadProcessor.setInitialEntitlementValuesForAssoRisk(policyHeader, outputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForAddAssociatedRisk", outputRecord);
        }
        return outputRecord;
    }


    /**
     * Get initial values for IBNR Inactive risk
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a record loaded with user entered data
     * @return a Record loaded with initial values
     */
    public Record getInitialValuesForAddInactiveRisk(PolicyHeader policyHeader, Record inputRecord, String anchorColumnName) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForInactiveRisk", new Object[]{policyHeader, inputRecord});
        }

        Record outputRecord = new Record();
        InactiveRiskFields.setIbnrInactiveId(outputRecord, getDbUtilityManager().getNextSequenceNo().toString());
        InactiveRiskFields.setAssociatedRiskId(outputRecord, InactiveRiskFields.getAssociatedRiskId(inputRecord));
        String inactiveEntityId = InactiveRiskFields.getInactiveEntityId(inputRecord);
        InactiveRiskFields.setInactiveEntityId(outputRecord, inactiveEntityId);
        InactiveRiskFields.setInactiveRiskName(outputRecord, getEntityManager().getEntityName(inactiveEntityId));
        PMCommonFields.setRecordModeCode(outputRecord, RecordMode.TEMP);
        InactiveRiskFields.setClosingTransLogId(outputRecord, "");
        InactiveRiskEntitlementRecordLoadProcessor.setInitialEntitlementValuesForInacRisk(policyHeader, outputRecord);
        //Set entity type
        if (inputRecord.hasStringValue(InactiveRiskFields.INACTIVE_ENTITY_ID) &&
            "O".equals(getEntityManager().getEntityType(InactiveRiskFields.getInactiveEntityId(inputRecord)))) {
           InactiveRiskFields.setEntityType(outputRecord, "O");
        } else {
           InactiveRiskFields.setEntityType(outputRecord, "P"); 
        }
        // replace anchorColumn name with ID for presentation layer
        if (outputRecord.hasField(anchorColumnName)) {
            outputRecord.setFieldValue("ID", outputRecord.getStringValue(anchorColumnName));
            outputRecord.remove(anchorColumnName);
        }
        // Setup initial row style
        InactiveRiskRowStyleRecordLoadProcessor.setInitialEntitlementValuesForRowStyle(outputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForInactiveRisk", outputRecord);
        }
        return outputRecord;
    }

    /**
     * Save all data in IBNR Inactive Risk page
     *
     * @param policyHeader    policy header that contains all key policy information.
     * @param inputRecordSets a record set with data to be saved
     */
    public void saveAllInactiveRisk(PolicyHeader policyHeader, RecordSet[] inputRecordSets) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllInactiveRisk", new Object[]{inputRecordSets});
        }

        // Validate data
        validateAllInactiveRisk(inputRecordSets);

        // Get changedRecords
        RecordSet changedInactiveRecords = OasisRecordSetHelper.setRowStatusOnModifiedRecords(inputRecordSets[1]);

        // Insert/Update/Delete IBNR Inactive Risk
        RecordSet changedRecords = changedInactiveRecords.getSubSet(
            new UpdateIndicatorRecordFilter((new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED, UpdateIndicator.DELETED})));

        changedRecords.setFieldValueOnAll(TransactionFields.TRANSACTION_LOG_ID, policyHeader.getLastTransactionId());
        changedRecords.setFieldValueOnAll(PolicyFields.POLICY_ID, policyHeader.getPolicyId());
        getIbnrRiskDAO().saveAllInactiveRisk(changedRecords);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllInactiveRisk");
        }
    }

    /**
     * To cancel active IBNR risk
     *
     * @param policyHeader
     * @param inputRecord
     */
    public void performCancellation(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "performCancellation",
            new Object[]{policyHeader, inputRecord});

        WorkflowAgent wa = WorkflowAgentImpl.getInstance();
        String policyNo = policyHeader.getPolicyNo();
        if (wa.hasWorkflow(policyNo)) {
            // process cancel
            TransactionFields.setTransactionLogId(inputRecord, policyHeader.getLastTransactionInfo().getTransactionLogId());
            InactiveRiskFields.setCancelIbnrRiskPkList(inputRecord, wa.getWorkflowAttribute(policyNo, RiskFields.RISK_ID).toString());
            PremiumAccountingFields.setTransEffDate(inputRecord, policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
            getIbnrRiskDAO().processCancelActiveIbnrRisk(inputRecord);

            wa.setWorkflowAttribute(policyNo, PolicyFields.POLICY_ID, policyHeader.getPolicyId());
            wa.setWorkflowAttribute(policyNo, TransactionFields.TRANSACTION_EFFECTIVE_FROM_DATE,
                policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
            wa.setWorkflowAttribute(policyNo, PMCommonFields.RECORD_MODE_CODE, getInactiveRecordMode(policyHeader));
        }

        l.exiting(getClass().getName(), "performCancellation");
    }

    /**
     * Validate all data in IBNR Inactive Risk
     *
     * @param inputRecords a record set with data to be validated
     */
    protected void validateAllInactiveRisk(RecordSet[] inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAllInactiveRisk", new Object[]{inputRecords});
        }

        RecordSet insertAssociatedRecords = inputRecords[0].getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.INSERTED}));
        if (insertAssociatedRecords.getSize() > 0) {
            Iterator inAssoIter = insertAssociatedRecords.getRecords();
            while (inAssoIter.hasNext()) {
                Record associatedRec = (Record) inAssoIter.next();
                if (inputRecords[1].getSubSet(
                    new RecordFilter(InactiveRiskFields.ASSOCIATED_RISK_ID, RiskFields.getRiskBaseRecordId(associatedRec))).getSize() < 1) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainInactive.detail.required.error");
                    throw new ValidationException("Invalid Inactive risk data.");
                }
            }
        }

        RecordSet inactiveRecords = inputRecords[1].getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));


        Iterator inactiveIt = inactiveRecords.getRecords();
        while (inactiveIt.hasNext()) { // loop for forms
            Record inactiveRec = (Record) inactiveIt.next();
            String rowNum = String.valueOf(inactiveRec.getRecordNumber() + 1);

            // Validate the required fields.
            if (!inactiveRec.hasField(InactiveRiskFields.INACTIVE_RISK_NAME) || StringUtils.isBlank(InactiveRiskFields.getInactiveRiskName(inactiveRec))) {
                MessageManager.getInstance().addErrorMessage("pm.maintainInactive.nameRequried.error", new String[]{rowNum}, InactiveRiskFields.INACTIVE_RISK_NAME, rowNum);
            }

            if (!inactiveRec.hasField(InactiveRiskFields.IBNR_EFF_FROM_DATE) || StringUtils.isBlank(InactiveRiskFields.getIbnrEffFromDate(inactiveRec))) {
                MessageManager.getInstance().addErrorMessage("pm.maintainInactive.fromDateRequried.error", new String[]{rowNum}, InactiveRiskFields.IBNR_EFF_FROM_DATE, rowNum);
            }

            if (!inactiveRec.hasField(InactiveRiskFields.IBNR_EFF_TO_DATE) || StringUtils.isBlank(InactiveRiskFields.getIbnrEffToDate(inactiveRec))) {
                MessageManager.getInstance().addErrorMessage("pm.maintainInactive.toDateRequried.error", new String[]{rowNum}, InactiveRiskFields.IBNR_EFF_TO_DATE, rowNum);
            }

            if (!inactiveRec.hasField(RiskFields.RISK_TYPE_CODE) || StringUtils.isBlank(RiskFields.getRiskTypeCode(inactiveRec))) {
                MessageManager.getInstance().addErrorMessage("pm.maintainInactive.riskTypeRequried.error", new String[]{rowNum}, RiskFields.RISK_TYPE_CODE, rowNum);
            }

            if (!inactiveRec.hasField(InactiveRiskFields.PRACTICE_STATE_CODE) || StringUtils.isBlank(InactiveRiskFields.getPracticeStateCode(inactiveRec))) {
                MessageManager.getInstance().addErrorMessage("pm.maintainInactive.practiceStatetRequried.error", new String[]{rowNum}, InactiveRiskFields.PRACTICE_STATE_CODE, rowNum);
            }

            if (!inactiveRec.hasField(InactiveRiskFields.COUNTY_CODE) || StringUtils.isBlank(InactiveRiskFields.getCountyCode(inactiveRec))) {
                MessageManager.getInstance().addErrorMessage("pm.maintainInactive.countyRequried.error", new String[]{rowNum}, InactiveRiskFields.COUNTY_CODE, rowNum);
            }

            if (!inactiveRec.hasField(InactiveRiskFields.RISK_CLASS) || StringUtils.isBlank(InactiveRiskFields.getRiskClass(inactiveRec))) {
                MessageManager.getInstance().addErrorMessage("pm.maintainInactive.specialtyRequried.error", new String[]{rowNum}, InactiveRiskFields.RISK_CLASS, rowNum);
            }

            if (!inactiveRec.hasField(InactiveRiskFields.COVERAGE_LIMIT_CODE) || StringUtils.isBlank(InactiveRiskFields.getCoverageLimitCode(inactiveRec))) {
                MessageManager.getInstance().addErrorMessage("pm.maintainInactive.limitRequried.error", new String[]{rowNum}, InactiveRiskFields.COVERAGE_LIMIT_CODE, rowNum);
            }

            // Validate form's effective form/to date.
            Date ibnrEffDate = inactiveRec.getDateValue(InactiveRiskFields.IBNR_EFF_FROM_DATE);
            Date ibnrExpDate = inactiveRec.getDateValue(InactiveRiskFields.IBNR_EFF_TO_DATE);
            if (ibnrEffDate != null && ibnrExpDate != null && ibnrEffDate.after(ibnrExpDate)) {
                MessageManager.getInstance().addErrorMessage("pm.maintainInactive.ibnrFromDateBeforeIbnrToDate.error",
                    new String[]{rowNum}, InactiveRiskFields.IBNR_EFF_TO_DATE, rowNum);
            }

            // Break if there is any error message.
            if (MessageManager.getInstance().hasErrorMessages()) {
                break;
            }
        } // end of loop

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid Inactive risk data.");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateAllInactiveRisk");
        }
    }

    /**
     * get recordMode according to screenMode
     *
     * @param policyHeader
     * @return recordMode
     */
    protected RecordMode getInactiveRecordMode(PolicyHeader policyHeader) {
        RecordMode recordMode;
        ScreenModeCode screenMode = policyHeader.getScreenModeCode();

        if (screenMode.isWIP() || screenMode.isRenewWIP() || screenMode.isManualEntry() ||
            (screenMode.isCancelWIP() && TransactionCode.RISKCANCEL.equals(policyHeader.getLastTransactionInfo().getTransactionCode()))) {
            recordMode = RecordMode.TEMP;
        }
        else {
            recordMode = RecordMode.OFFICIAL;
        }

        return recordMode;
    }

    public IbnrRiskDAO getIbnrRiskDAO() {
        return m_ibnrRiskDAO;
    }

    public void setIbnrRiskDAO(IbnrRiskDAO ibnrRiskDAO) {
        m_ibnrRiskDAO = ibnrRiskDAO;
    }

    public DBUtilityManager getDbUtilityManager() {
        return m_dbUtilityManager;
    }

    public void setDbUtilityManager(DBUtilityManager dbUtilityManager) {
        m_dbUtilityManager = dbUtilityManager;
    }

    public EntityManager getEntityManager() {
        return m_entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        m_entityManager = entityManager;
    }

    public void verifyConfig() {
        if (getIbnrRiskDAO() == null)
            throw new ConfigurationException("The required property 'ibnrRiskDAO' is missing.");
        if (getDbUtilityManager() == null)
            throw new ConfigurationException("The required property 'dbUtilityManager' is missing.");
    }

    private IbnrRiskDAO m_ibnrRiskDAO;
    private DBUtilityManager m_dbUtilityManager;
    private EntityManager m_entityManager;
}
