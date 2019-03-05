package dti.pm.coveragemgr.vlcoveragemgr.impl;

import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.DisplayIndicator;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.DisplayIndicatorRecordFilter;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordLoadProcessorChainManager;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.PMRecordSetHelper;
import dti.pm.busobjs.PMStatusCode;
import dti.pm.busobjs.PolicyCycleCode;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.busobjs.SysParmIds;
import dti.pm.busobjs.TransactionCode;
import dti.cs.data.dbutility.DBUtilityManager;
import dti.pm.coveragemgr.CoverageFields;
import dti.pm.coveragemgr.vlcoveragemgr.VLCoverageFields;
import dti.pm.coveragemgr.vlcoveragemgr.VLCoverageManager;
import dti.pm.coveragemgr.vlcoveragemgr.dao.VLCoverageDAO;
import dti.pm.entitymgr.EntityManager;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.riskmgr.RiskManager;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.validationmgr.impl.ContinuityRecordSetValidator;

import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Class provides the implementation details of VLCoverageManager Interface.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 7, 2008
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class VLCoverageManagerImpl implements VLCoverageManager {

    /**
     * load all VL risk info
     *
     * @param policyHeader policy header
     * @param inputRecord  input paramenters
     * @return recordset contains all VL Risk info
     */
    public RecordSet loadAllVLRisk(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllVLRisk", new Object[]{policyHeader, inputRecord});
        }

        RecordSet rs;
        RecordMode recordModeCode;

        //retrieve VL coverage status
        PMStatusCode vlCoverageStatus = getVLCoverageStatus(policyHeader, inputRecord);
        VLCoverageFields.setVlCoverageStatus(inputRecord, vlCoverageStatus);

        // recordModeCode - OFFCIAL if the policy screen mode is VIEW_POLICY,
        // or ENDQUOTE if the policy screen mode is VIEW_ENDQUOTE, or TEMP otherwise
        ScreenModeCode vlScreenMode = getVLScreenMode(policyHeader, inputRecord);
        if (vlScreenMode.isViewPolicy()) {
            recordModeCode = RecordMode.OFFICIAL;
        }
        else if (vlScreenMode.isViewEndquote()) {
            recordModeCode = RecordMode.ENDQUOTE;
        }
        else {
            recordModeCode = RecordMode.TEMP;
        }
        PMCommonFields.setRecordModeCode(inputRecord, recordModeCode);
        VLCoverageFields.setVLScreenModeCode(inputRecord, vlScreenMode);
        YesNoFlag inUpdateMode = isInUpdateMode(vlScreenMode);
        VLCoverageFields.setInUpdateMode(inputRecord, inUpdateMode);

        //prepare record load processor
        RecordLoadProcessor lp = new VLCoverageRecordLoadProcessor(policyHeader, inputRecord);
        lp = RecordLoadProcessorChainManager.getRecordLoadProcessor(lp,
            new VLCoverageEntitlementRecordLoadProcessor(policyHeader, inputRecord));
        //call DAO to load all vl risk info
        inputRecord.setFields(policyHeader.toRecord(), false);
        rs = getVlCoverageDAO().loadAllVLRisk(inputRecord, lp);


        Date vlCovgEff = DateUtils.parseDate(CoverageFields.getCoverageEffectiveFromDate(inputRecord));
        Date transEff = DateUtils.parseDate(policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        //add VL Coverage Effective Date Reminder
        if (inUpdateMode.booleanValue()) {
            if (vlCovgEff.after(transEff)) {
                MessageManager.getInstance().addInfoMessage("pm.maintainVLCoverage.effGreaterTransEff.error");
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllVLRisk", rs);
        }

        return rs;
    }

    /**
     * return screen mode code for current selected VL coverage
     *
     * @param policyHeader policy header
     * @param inputRecord  input paramenters
     * @return screen mode code
     */
    protected ScreenModeCode getVLScreenMode(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getVLScreenMode", new Object[]{policyHeader, inputRecord});
        }

        ScreenModeCode screenModeCode = policyHeader.getScreenModeCode();
        PMStatusCode vlCoverageStatus = VLCoverageFields.getVlCoverageStatus(inputRecord);
        //If the policy screen mode is not VIEW_POLICY, VIEW_ENDQUOTE, CANCELWIP, REINSTATEWIP, or MANUAL_ENTRY,
        // and the VL coverage status is not ACTIVE or PENDING, reset the current screen mode to VIEW_POLICY.
        if ((!(screenModeCode.isViewPolicy() || screenModeCode.isViewEndquote() ||
            screenModeCode.isCancelWIP() || screenModeCode.isResinstateWIP() || screenModeCode.isManualEntry()))
            && (!(vlCoverageStatus.isActive() || vlCoverageStatus.isPending()))) {
            screenModeCode = ScreenModeCode.VIEW_POLICY;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getVLScreenMode", screenModeCode);
        }
        return screenModeCode;
    }

    /**
     * return status code for current selected VL coverage
     *
     * @param policyHeader policy header
     * @param inputRecord  input paramenters
     * @return coverage status code
     */
    protected PMStatusCode getVLCoverageStatus(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getVLCoverageStatus", new Object[]{policyHeader, inputRecord});
        }
        PMStatusCode covgStatusCode;
        PolicyCycleCode policyCycleCode = policyHeader.getPolicyCycleCode();
        ScreenModeCode screenModeCode = policyHeader.getScreenModeCode();

        if (policyCycleCode.isQuote() || (!(screenModeCode.isViewPolicy() || screenModeCode.isViewEndquote()))) {
            covgStatusCode = CoverageFields.getCoverageStatus(inputRecord);
        }
        else {
            covgStatusCode = CoverageFields.getCoverageBaseStatus(inputRecord);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getVLCoverageStatus", covgStatusCode);
        }
        return covgStatusCode;
    }

    /**
     * return the YesNoFlag instance to indicate if it is in Update mode
     *
     * @param vlScreenMode reseted VL screen mode code
     * @return YesNoFlag instance to indicate if it is in Update mode
     */
    protected YesNoFlag isInUpdateMode(ScreenModeCode vlScreenMode) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isInUpdateMode", new Object[]{vlScreenMode});
        }
        YesNoFlag isInUpdateMode;
        if (vlScreenMode.isViewPolicy() || vlScreenMode.isViewEndquote() || vlScreenMode.isResinstateWIP() || vlScreenMode.isCancelWIP()) {
            isInUpdateMode = YesNoFlag.N;
        }
        else {
            isInUpdateMode = YesNoFlag.Y;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isInUpdateMode", isInUpdateMode
            );
        }
        return isInUpdateMode;
    }


    /**
     * save all VL risk info
     *
     * @param policyHeader policy header
     * @param inputRecord  input paramenters
     * @param inputRecords input records
     * @return process count
     */
    public int saveAllVLRisk(PolicyHeader policyHeader, Record inputRecord, RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "intSaveAllVLRisk", new Object[]{policyHeader, inputRecord, inputRecords});
        }
        inputRecords.setFieldsOnAll(policyHeader.toRecord(), false);

        int processCount = 0;
        /* Create an new RecordSet to include all added and modified records */
        RecordSet allModifedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter
            (new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));
        RecordSet allDeletedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter
            (new String[]{UpdateIndicator.DELETED}));

        //set last transaction log id
        inputRecords.setFieldValueOnAll("lastTransactionLogId", policyHeader.getLastTransactionId());

        if (allDeletedRecords.getSize() > 0) {
            //company insured
            RecordSet companyInsuredRecords = new RecordSet();
            RecordSet nonCompanyInsuredRecords = new RecordSet();
            Iterator delRecIter = allDeletedRecords.getRecords();
            while (delRecIter.hasNext()) {
                Record delRec = (Record) delRecIter.next();
                boolean companyInsured = VLCoverageFields.getCompanyInsuredB(delRec).booleanValue();
                String officialRecordId = VLCoverageFields.getOfficialRecordId(delRec);
                if (companyInsured || StringUtils.isBlank(officialRecordId) || officialRecordId.equals("0")) {
                    companyInsuredRecords.addRecord(delRec);
                }
                else if (!companyInsured) {
                    nonCompanyInsuredRecords.addRecord(delRec);
                }
            }
            //delete company insured
            processCount += getVlCoverageDAO().deleteAllVLRisk(companyInsuredRecords);
            //delete non company insured
            processCount += getRiskManager().deleteAllRisk(policyHeader, nonCompanyInsuredRecords);
        }

        if (allModifedRecords.getSize() > 0) {
            /* validate the input records prior save them */
            validateSaveAllVLRisk(policyHeader, inputRecord, inputRecords);
            PMRecordSetHelper.setRowStatusOnModifiedRecords(allModifedRecords);

            //iterate through every record and save the changes
            Iterator riskIter = allModifedRecords.getRecords();
            while (riskIter.hasNext()) {
                Record riskRec = (Record) riskIter.next();

                //save non insured VL Risk data
                if (!VLCoverageFields.getCompanyInsuredB(riskRec).booleanValue()) {
                    //call DAO method to save non insured VL risk data
                    getVlCoverageDAO().saveNonInsuredVLRisk(riskRec);
                    //if is non insured, set current policy No as vl policy No
                    VLCoverageFields.setVlPolicyNo(riskRec, PolicyHeaderFields.getPolicyNo(riskRec));
                }
                //save all VL Risk data
                getVlCoverageDAO().saveVLRisk(riskRec);
                processCount++;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "intSaveAllVLRisk", String.valueOf(processCount));
        }

        return processCount;
    }


    /**
     * validate save all VL risk info
     *
     * @param policyHeader policy header
     * @param inputRecord  input paramenters
     * @param inputRecords input records
     */
    protected void validateSaveAllVLRisk(PolicyHeader policyHeader, Record inputRecord, RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateSaveAllVLRisk", new Object[]{policyHeader, inputRecord, inputRecords});
        }
        RecordSet allModifedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter
            (new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));
        Iterator recIter = allModifedRecords.getRecords();
        while (recIter.hasNext()) {
            Record record = (Record) recIter.next();
            Date effDate = DateUtils.parseDate(VLCoverageFields.getEffectiveFromDate(record));
            Date expDate = DateUtils.parseDate(VLCoverageFields.getEffectiveToDate(record));
            Date startDate = DateUtils.parseDate(VLCoverageFields.getStartDate(record));
            Date endDate = DateUtils.parseDate(VLCoverageFields.getEndDate(record));
            YesNoFlag companyInsuredB = VLCoverageFields.getCompanyInsuredB(record);
            TransactionCode transCode = policyHeader.getLastTransactionInfo().getTransactionCode();
            String msgRowId = record.getStringValue("rownum");
            String ratingBasis = VLCoverageFields.getRatingBasis(record);
            Date termEff = DateUtils.parseDate(policyHeader.getTermEffectiveFromDate());
            Date termExp = DateUtils.parseDate(policyHeader.getTermEffectiveToDate());

            if (expDate.before(effDate)) {
                MessageManager.getInstance().addErrorMessage("pm.maintainVLCoverage.expGreaterThaneff.error",
                    VLCoverageFields.EFFECTIVE_FROM_DATE, msgRowId);
            }

            if (!companyInsuredB.booleanValue() && (StringUtils.isBlank(ratingBasis) ||
                Double.parseDouble(ratingBasis) < 0 || Double.parseDouble(ratingBasis) > 100)) {
                MessageManager.getInstance().addErrorMessage("pm.maintainVLCoverage.rateBiasRange.error",
                    VLCoverageFields.RATING_BASIS, msgRowId);
            }

            if (!companyInsuredB.booleanValue()) {
                if ((!transCode.isOosEndorsement()) && (startDate.before(termEff) || endDate.after(termExp))) {
                    if (startDate.before(termEff)) {
                        MessageManager.getInstance().addErrorMessage("pm.maintainVLCoverage.relationDateRange.error",
                            VLCoverageFields.START_DATE, msgRowId);
                    }
                    else if (endDate.after(termExp)) {
                        MessageManager.getInstance().addErrorMessage("pm.maintainVLCoverage.relationDateRange.error",
                            VLCoverageFields.END_DATE, msgRowId);
                    }
                }
                else {
                    if (startDate.after(effDate)) {
                        MessageManager.getInstance().addErrorMessage("pm.maintainVLCoverage.ratingDateRange.error",
                            VLCoverageFields.START_DATE, msgRowId);
                    }
                    else if (endDate.before(expDate)) {
                        MessageManager.getInstance().addErrorMessage("pm.maintainVLCoverage.ratingDateRange.error",
                            VLCoverageFields.END_DATE, msgRowId);
                    }
                }


                if (!record.hasStringValue(VLCoverageFields.PRACTICE_STATE_CODE)) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainVLCoverage.practiceState.required.error",
                        VLCoverageFields.PRACTICE_STATE_CODE, msgRowId);
                }

                if (!record.hasStringValue(VLCoverageFields.COUNTY_CODE_USED_TO_RATE)) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainVLCoverage.county.required.error",
                        VLCoverageFields.COUNTY_CODE_USED_TO_RATE, msgRowId);
                }

                if (!record.hasStringValue(VLCoverageFields.RISK_CLS_USED_TO_RATE)) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainVLCoverage.specialty.required.error",
                        VLCoverageFields.RISK_CLS_USED_TO_RATE, msgRowId);
                }
            }

            if (MessageManager.getInstance().hasErrorMessages()) {
                throw new ValidationException("the VL risk data is invalid");
            }
        }

        //check duplicate
        ContinuityRecordSetValidator continuityRecordSetValidator =
            new VLCoverageContinuityRecordSetValidator(VLCoverageFields.START_DATE, VLCoverageFields.END_DATE,
                "rownum", "pm.maintainVLCoverage.recordDuplicate.error",
                new String[]{VLCoverageFields.ENTITY_ID, VLCoverageFields.COMPANY_INSURED_B}, new String[]{});
        RecordSet visibleRecords = inputRecords.getSubSet(
            new DisplayIndicatorRecordFilter(DisplayIndicator.VISIBLE));
        //remove flat records
        Iterator finalIter = visibleRecords.getRecords();
        RecordSet finalRecords = new RecordSet();
        while (finalIter.hasNext()) {
            Record record = (Record) finalIter.next();
            Date effDate = DateUtils.parseDate(VLCoverageFields.getStartDate(record));
            Date expDate = DateUtils.parseDate(VLCoverageFields.getEndDate(record));
            if (effDate.before(expDate)) {
                finalRecords.addRecord(record);
            }
        }

        continuityRecordSetValidator.validate(finalRecords);

        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("VL risk data invalid");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateSaveAllVLRisk");
        }
    }

    /**
     * Initial values defaults for a new VL risk record
     *
     * @param policyHeader contains policy header information
     * @param inputRecord  input parameters
     * @return Record contains initial values
     */
    public Record getInitialValuesForVLRisk(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForVLRisk", new Object[]{policyHeader, inputRecord});
        }


        Record outputRecord = new Record();
        String tranEff = policyHeader.getLastTransactionInfo().getTransEffectiveFromDate();
        String termExp = policyHeader.getTermEffectiveToDate();
        YesNoFlag companyInsuredB = VLCoverageFields.getCompanyInsuredB(inputRecord);
        inputRecord.setFields(policyHeader.toRecord(), false);
        String seledEntityId = VLCoverageFields.getEntityId(inputRecord);

        //if add insured VL employee
        if (companyInsuredB.booleanValue()) {
            VLCoverageFields.setRiskId(outputRecord, "0");
            Record vlRiskRec = getVlCoverageDAO().getLastVLRiskInfo(inputRecord);
            VLCoverageFields.setCompanyInsuredB(outputRecord, companyInsuredB);
            VLCoverageFields.setVlPolicyNo(outputRecord, inputRecord.getStringValue("externalNo"));
            VLCoverageFields.setStartDate(outputRecord, tranEff);
            VLCoverageFields.setEndDate(outputRecord, termExp);
            VLCoverageFields.setRiskBaseId(outputRecord, vlRiskRec.getStringValue("riskBaseId"));
            VLCoverageFields.setEffectiveFromDate(outputRecord, vlRiskRec.getStringValue("riskEff"));
            VLCoverageFields.setEffectiveToDate(outputRecord, vlRiskRec.getStringValue("riskExp"));
            VLCoverageFields.setPracticeStateCode(outputRecord, vlRiskRec.getStringValue("practiceState"));
            VLCoverageFields.setCountyCodeUsedToRate(outputRecord, vlRiskRec.getStringValue("county"));
            VLCoverageFields.setRiskClsUsedToRate(outputRecord, vlRiskRec.getStringValue("riskClass"));
            VLCoverageFields.setRatingBasis(outputRecord, vlRiskRec.getStringValue("ratingBasis"));
            VLCoverageFields.setRiskTypeCode(outputRecord, vlRiskRec.getStringValue("riskTypeCode"));
        }
        //if add non-insured VL employee
        else {

            VLCoverageFields.setRiskId(outputRecord, getDbUtilityManager().getNextSequenceNo().toString());
            inputRecord.setFieldValue("riskTypeCode", "VLRISKNML");
            VLCoverageFields.setRiskProcessCode(outputRecord, "VLRISKNML");
            //retrieve risk base Id
            String riskBaseId = getRiskManager().getRiskBaseId(inputRecord);
            VLCoverageFields.setRiskBaseId(outputRecord, riskBaseId);
            VLCoverageFields.setCompanyInsuredB(outputRecord, companyInsuredB);
            VLCoverageFields.setEffectiveFromDate(outputRecord, tranEff);
            VLCoverageFields.setEffectiveToDate(outputRecord, termExp);
            VLCoverageFields.setStartDate(outputRecord, tranEff);
            VLCoverageFields.setEndDate(outputRecord, termExp);
            VLCoverageFields.setRatingBasis(outputRecord, "100");
        }
        //common ininital values
        TransactionFields.setTransactionCode(outputRecord, policyHeader.getLastTransactionInfo().getTransactionCode());
        TransactionFields.setEndorsementQuoteId(
            outputRecord, policyHeader.getLastTransactionInfo().getEndorsementQuoteId());
        VLCoverageFields.setCovRelatedEntityId(outputRecord, getDbUtilityManager().getNextSequenceNo().toString());
        CoverageFields.setCoverageId(outputRecord, CoverageFields.getCoverageBaseRecordId(inputRecord));
        VLCoverageFields.setEntityId(outputRecord, seledEntityId);
        VLCoverageFields.setRiskName(outputRecord, getEntityManager().getEntityName(seledEntityId));
        VLCoverageFields.setRiskTypeCode(outputRecord,
            SysParmProvider.getInstance().getSysParm(SysParmIds.PM_VL_RISK_TYPE, "PHYSICIAN"));
        VLCoverageFields.setAfterImageRecordB(outputRecord, YesNoFlag.N);
        VLCoverageFields.setStatus(outputRecord, PMStatusCode.PENDING);
        PMCommonFields.setRecordModeCode(outputRecord, RecordMode.TEMP);

        //set page entitlements
        VLCoverageEntitlementRecordLoadProcessor lp = new VLCoverageEntitlementRecordLoadProcessor(policyHeader, inputRecord);
        lp.postProcessRecord(outputRecord, true);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForVLRisk", outputRecord);
        }
        return outputRecord;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getVlCoverageDAO() == null)
            throw new ConfigurationException("The required property 'vlCoverageDAO' is missing.");
        if (getRiskManager() == null)
            throw new ConfigurationException("The required property 'riskManager' is missing.");
        if (getDbUtilityManager() == null)
            throw new ConfigurationException("The required property 'dbUtilityManager' is missing.");
        if (getEntityManager() == null)
            throw new ConfigurationException("The required property 'entityManager' is missing.");
    }

    public VLCoverageDAO getVlCoverageDAO() {
        return m_vlCoverageDAO;
    }

    public void setVlCoverageDAO(VLCoverageDAO vlCoverageDAO) {
        m_vlCoverageDAO = vlCoverageDAO;
    }


    public RiskManager getRiskManager() {
        return m_riskManager;
    }

    public void setRiskManager(RiskManager riskManager) {
        m_riskManager = riskManager;
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

    private EntityManager m_entityManager;
    private DBUtilityManager m_dbUtilityManager;
    private VLCoverageDAO m_vlCoverageDAO;
    private RiskManager m_riskManager;
}
