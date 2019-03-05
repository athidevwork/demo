package dti.pm.transactionmgr.cancelprocessmgr;

import dti.oasis.recordset.Record;
import dti.oasis.busobjs.YesNoFlag;

/**
 * Fields for Cancel Process.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 4, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 02/18/2009       yhyang      #84592 Add CLAIMS_ACCESS_INDICATOR for amalgamation.
 * 01/07/2011       ryzhao      #113558 Add CARRIER, IS_NEW_CARRIER_AVAILABLE for new carrier.
 * 01/14/2011       ryzhao      #113558 Add TRANS_CODE, PARMS for new carrier.Updated IS_NEW_CARRIER_AVAILABLE type.
 * 01/17/2011       ryzhao      #113558 Add CANCEL_TRANS_CODE_CANCEL, CANCEL_TRANS_CODE_RISKCANCEL constants.
 * 01/18/20101      syang       105832 - Added some new fields for discipline decline list.
 * 06/01/2011       ryzhao      103808 - Added NUM_AGE_OVRD_RISKS.
 * 08/18/2011       syang       121201 - Added some fields for multi cancellation.
 * 10/19/2011       wfu         125007 - Added a field for multiple cancellation.
 * 03/22/2012       xnie        130643 - Added some fields for purge policy and flat cancel short term policy.
 * 05/23/2012       xnie        132862 - Added field/get/set function for risk name.
 * 05/24/2012       xnie        132862 - Roll backed prior modification.
 * 09/10/2012       ryzhao      133360 - Added WARNING.
 * 05/09/2014       awu         152675 - Added termBaseId, type, reason, covBaseId.
 * 06/17/2014       jyang2      149970 - Added a field cancel description for cancel screen.
 * 07/15/2014       wdang       154953 - Added policyCovComponentId, componentRecordModeCode, componentOfficialRecordId.
 * 12/24/2014       awu         159339 - Added isExtendB.
 * 03/17/2016       eyin        169939 - Added field/get/set function for Process.
 * 05/17/2016       wdang       176804 - Removed isNewCarrierAvailable and added carrierB.
 * 07/11/2016       eyin        176476 - Added futureCancellationExistB.
 * ---------------------------------------------------
 */

public class CancelProcessFields {
    public static final String CANCELLATION_LEVEL = "cancellationLevel";
    public static final String CANCELLATION_ADD_OCCUPANT = "cancellationAddOccupant";
    public static final String CANCELLATION_COMMENTS = "cancellationComments";
    public static final String CANCELLATION_DATE = "cancellationDate";
    public static final String CANCELLATION_METHOD = "cancellationMethod";
    public static final String CANCELLATION_REASON = "cancellationReason";
    public static final String CANCELLATION_TYPE = "cancellationType";
    public static final String ACCOUNTING_DATE = "accountingDate";
    public static final String CANCEL_ITEM_EFF_DATE = "cancelItemEffDate";
    public static final String CANCEL_ITEM_EXP_DATE = "cancelItemExpDate";
    public static final String TAIL_B = "tailB";
    public static final String CANCEL_DESC = "cancelDesc";

    public static final String CANCELLATION_VALIDATION_DATE_STR = "cancellationValidationDateStr";

    public static final String RISK_NAME_DISPLAY = "riskNameDisplay";
    public static final String COVERAGE_DISPLAY = "coverageDisplay";
    public static final String COMPONENT_DISPLAY = "componentDisplay";
    public static final String SUBCOVERAGE_DISPLAY = "subcoverageDisplay";
    public static final String MSG = "msg";
    public static final String STATUS = "status";
    public static final String FUTURECANCELLATIONEXISTB = "futureCancellationExistB";
    public static final String TAIL_CREATE_B = "tailCreateB";
    public static final String PROCESS_RECORDS = "processRecords";
    public static final String PROCESS_CODE = "processCode";
    public static final String AMALGAMATION_METHOD = "amalgamationMethod";
    public static final String AMALGAMATE_TO = "amalgamationTo";
    public static final String CLAIMS_ACCESS_INDICATOR = "claimsAccessIndicator";

    public static final String TRANS_CODE = "transCode";
    public static final String CARRIER = "carrier";
    public static final String CARRIER_B = "carrierB";
    public static final String PARMS = "parms";

    public static final String MARK_AS_DDL = "markAsDdl";
    public static final String SELECT_TO_DDL = "selectToDdl";
    public static final String DDL_STATUS = "ddlStatus";
    public static final String DDL_REASON = "ddlReason";
    public static final String DDL_COMMENTS = "ddlComments";
    public static final String IS_MULTI_CANCEL_B = "isMultiCancelB";
    public static final String DDL_REASON_FOR_RISK = "ddlReasonForRisk";
    public static final String DDL_COMMENTS_FOR_RISK = "ddlCommentsForRisk";
    public static final String CANCEL_DATE = "cancelDate";
    public static final String CANCEL_COMMENT = "cancelComment";
    public static final String CANCEL_TYPE = "cancelType";
    public static final String CANCEL_REASON = "cancelReason";
    public static final String CANCEL_METHOD = "cancelMethod";
    public static final String CANCEL_DISTINCT_ID = "cancelDistinctId";
    public static final String IS_INIT_TRANS_B = "isInitTransB";
    public static final String SAVE_OFFICIAL_B = "saveOfficialB";
    public static final String CONFIRMATION_RECORDS = "confirmationRecords";
    public static final String CONFIRM_TRANSACTION_LIST = "confirmTransactionList";

    public static final String NEW_TRANSACTION_COMMENT = "newTransactionComment";
    public static final String ROW_NUM = "rowNum";
    public static final String IS_CONTINUE_AVAILABLE = "isContinueAvailable";
    public static final String AMALGAMATE_B = "amalgamationB";

    public static final String IS_IBNR_RISK = "isIbnrRisk";
    public static final String METHOD_CODE = "methodCode";
    public static final String NUM_AGE_OVRD_RISKS = "numAgeOvrdRisks";
    public static final String AGE_OVRD_RISKS = "ageOvrdRisks";
    public static final String IS_PROCESS_AVAILABLE = "isProcessAvailable";
    
    public static final String TERM_BASE_ID = "termBaseId";
    public static final String TYPE = "type";
    public static final String REASON = "reason";
    public static final String COV_BASE_ID = "covBaseId";
    
    public static final String POLICY_COV_COMPONENT_ID = "policyCovComponentId";
    public static final String COMP_REC_MODE_CODE = "componentRecordModeCode";
    public static final String COMP_OFF_REC_ID = "componentOfficialRecordId";
    
    public static final String IS_EXTEND_B = "isExtendB";

    public static final String PROCESS = "Process";

    public static String getProcessCode(Record record) {
        return record.getStringValue(PROCESS_CODE);
    }

    public static void setProcessCode(Record record, String processCode) {
        record.setFieldValue(PROCESS_CODE, processCode);
    }

    public static YesNoFlag getTailCreateB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(TAIL_CREATE_B));
    }

    public static void setTailCreateB(Record record, YesNoFlag tailCreateB) {
        record.setFieldValue(TAIL_CREATE_B, tailCreateB);
    }

    public static String getStatus(Record record) {
        return record.getStringValue(STATUS);
    }

    public static void setStatus(Record record, String status) {
        record.setFieldValue(STATUS, status);
    }

    public static String getMsg(Record record) {
        return record.getStringValue(MSG);
    }

    public static void setMsg(Record record, String msg) {
        record.setFieldValue(MSG, msg);
    }

    public static String getFutureCancellationExistB(Record record) {
        return record.getStringValue(FUTURECANCELLATIONEXISTB);
    }

    public static void setFutureCancellationExistB(Record record, YesNoFlag value) {
        record.setFieldValue(FUTURECANCELLATIONEXISTB, value);
    }

    public static String getRiskNameDisplay(Record record) {
        return record.getStringValue(RISK_NAME_DISPLAY);
    }

    public static void setRiskNameDisplay(Record record, String riskNameDisplay) {
        record.setFieldValue(RISK_NAME_DISPLAY, riskNameDisplay);
    }

    public static String getCoverageDisplay(Record record) {
        return record.getStringValue(COVERAGE_DISPLAY);
    }

    public static void setCoverageDisplay(Record record, String coverageDisplay) {
        record.setFieldValue(COVERAGE_DISPLAY, coverageDisplay);
    }

    public static String getComponentDisplay(Record record) {
        return record.getStringValue(COMPONENT_DISPLAY);
    }

    public static void setComponentDisplay(Record record, String componentDisplay) {
        record.setFieldValue(COMPONENT_DISPLAY, componentDisplay);
    }

    public static String getSubcoverageDisplay(Record record) {
        return record.getStringValue(SUBCOVERAGE_DISPLAY);
    }

    public static void setSubcoverageDisplay(Record record, String subcoverageDisplay) {
        record.setFieldValue(SUBCOVERAGE_DISPLAY, subcoverageDisplay);
    }

    public static String getCancellationValidationDateStr(Record record) {
        return record.getStringValue(CANCELLATION_VALIDATION_DATE_STR);
    }

    public static void setCancellationValidationDateStr(Record record, String cancellationValidationDateStr) {
        record.setFieldValue(CANCELLATION_VALIDATION_DATE_STR, cancellationValidationDateStr);
    }

    public static YesNoFlag getTailB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(TAIL_B));
    }

    public static void setTailB(Record record, YesNoFlag tailB) {
        record.setFieldValue(TAIL_B, tailB.getName());
    }

    public static String getCancelItemEffDate(Record record) {
        return record.getStringValue(CANCEL_ITEM_EFF_DATE);
    }

    public static void setCancelItemEffDate(Record record, String cancelItemEffDate) {
        record.setFieldValue(CANCEL_ITEM_EFF_DATE, cancelItemEffDate);
    }


    public static String getCancelItemExpDate(Record record) {
        return record.getStringValue(CANCEL_ITEM_EXP_DATE);
    }

    public static void setCancelItemExpDate(Record record, String cancelItemExpDate) {
        record.setFieldValue(CANCEL_ITEM_EXP_DATE, cancelItemExpDate);
    }

    public static String getAccountingDate(Record record) {
        return record.getStringValue(ACCOUNTING_DATE);
    }

    public static void setAccountingDate(Record record, String accountingDate) {
        record.setFieldValue(ACCOUNTING_DATE, accountingDate);
    }

    public static String getCancellationLevel(Record record) {
        return record.getStringValue(CANCELLATION_LEVEL);
    }

    public static void setCancellationLevel(Record record, String cancellationLevel) {
        record.setFieldValue(CANCELLATION_LEVEL, cancellationLevel);
    }

    public static YesNoFlag getCancellationAddOccupant(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(CANCELLATION_ADD_OCCUPANT));
    }

    public static void setCancellationAddOccupant(Record record, YesNoFlag cancellationAddOccupant) {
        record.setFieldValue(CANCELLATION_ADD_OCCUPANT, cancellationAddOccupant);
    }

    public static String getCancellationComments(Record record) {
        return record.getStringValue(CANCELLATION_COMMENTS);
    }

    public static void setCancellationComments(Record record, String cancellationComments) {
        record.setFieldValue(CANCELLATION_COMMENTS, cancellationComments);
    }

    public static String getCancellationDate(Record record) {
        return record.getStringValue(CANCELLATION_DATE);
    }

    public static void setCancellationDate(Record record, String cancellationDate) {
        record.setFieldValue(CANCELLATION_DATE, cancellationDate);
    }

    public static String getCancellationMethod(Record record) {
        return record.getStringValue(CANCELLATION_METHOD);
    }

    public static void setCancellationMethod(Record record, String cancellationMethod) {
        record.setFieldValue(CANCELLATION_METHOD, cancellationMethod);
    }

    public static String getCancellationReason(Record record) {
        return record.getStringValue(CANCELLATION_REASON);
    }

    public static void setCancellationReason(Record record, String cancellationReason) {
        record.setFieldValue(CANCELLATION_REASON, cancellationReason);
    }

    public static String getCancellationType(Record record) {
        return record.getStringValue(CANCELLATION_TYPE);
    }

    public static void setCancellationType(Record record, String cancellationType) {
        record.setFieldValue(CANCELLATION_TYPE, cancellationType);
    }

    public static String getAmalgamationMethod(Record record) {
        return record.getStringValue(AMALGAMATION_METHOD);
    }

    public static String getAmalgamateTo(Record record) {
        return record.getStringValue(AMALGAMATE_TO);
    }

    public static String getTransCode(Record record) {
        return record.getStringValue(TRANS_CODE);
    }

    public static void setTransCode(Record record, String transCode) {
        record.setFieldValue(TRANS_CODE, transCode);
    }

    public static String getCarrier(Record record) {
        return record.getStringValue(CARRIER);
    }

    public static void setCarrier(Record record, String carrier) {
        record.setFieldValue(CARRIER, carrier);
    }

    public static YesNoFlag getCarrierB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(CARRIER_B));
    }

    public static void setCarrierB(Record record, YesNoFlag carrierB) {
        record.setFieldValue(CARRIER_B, carrierB);
    }

    public static String getParms(Record record) {
        return record.getStringValue(PARMS);
    }

    public static void setParms(Record record, String parms) {
        record.setFieldValue(PARMS, parms);
    }

    public static String getMarkAsDdl(Record record) {
        return record.getStringValue(MARK_AS_DDL);
    }

    public static void setMarkAsDdl(Record record, String markAsDdl) {
        record.setFieldValue(MARK_AS_DDL, markAsDdl);
    }

    public static String getSelectToDdl(Record record) {
        return record.getStringValue(SELECT_TO_DDL);
    }

    public static void setSelectToDdl(Record record, String selectToDdl) {
        record.setFieldValue(SELECT_TO_DDL, selectToDdl);
    }

    public static String getDdlStatus(Record record) {
        return record.getStringValue(DDL_STATUS);
    }

    public static void setDdlStatus(Record record, String ddlStatus) {
        record.setFieldValue(DDL_STATUS, ddlStatus);
    }

    public static String getDdlReason(Record record) {
        return record.getStringValue(DDL_REASON);
    }

    public static void setDdlReason(Record record, String ddlReason) {
        record.setFieldValue(DDL_REASON, ddlReason);
    }

    public static String getDdlComments(Record record) {
        return record.getStringValue(DDL_COMMENTS);
    }

    public static void setDdlComments(Record record, String ddlComments) {
        record.setFieldValue(DDL_COMMENTS, ddlComments);
    }

    public static YesNoFlag getIsMultiCancelB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(IS_MULTI_CANCEL_B));
    }

    public static void setIsMultiCancelB(Record record, YesNoFlag isMultiCancelB) {
        record.setFieldValue(IS_MULTI_CANCEL_B, isMultiCancelB);
    }

    public static String getDdlReasonForRisk(Record record) {
        return record.getStringValue(DDL_REASON_FOR_RISK);
    }

    public static void setDdlReasonForRisk(Record record, String ddlReasonForRisk) {
        record.setFieldValue(DDL_REASON_FOR_RISK, ddlReasonForRisk);
    }

    public static String getDdlCommentsForRisk(Record record) {
        return record.getStringValue(DDL_COMMENTS_FOR_RISK);
    }

    public static void setDdlCommentsForRisk(Record record, String ddlCommentsForRisk) {
        record.setFieldValue(DDL_COMMENTS_FOR_RISK, ddlCommentsForRisk);
    }

    public static YesNoFlag isIbnrRisk(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(IS_IBNR_RISK));
    }

    public static String getNumAgeOvrdRisks(Record record) {
        return record.getStringValue(NUM_AGE_OVRD_RISKS);
    }

    public static void setNumAgeOvrdRisks(Record record, String numAgeOvrdRisks) {
        record.setFieldValue(NUM_AGE_OVRD_RISKS, numAgeOvrdRisks);
    }

    public static String getAgeOvrdRisks(Record record) {
        return record.getStringValue(AGE_OVRD_RISKS);
    }

    public static void setAgeOvrdRisks(Record record, String ageOvrdRisks) {
        record.setFieldValue(AGE_OVRD_RISKS, ageOvrdRisks);
    }

    public static String getCancelComment(Record record) {
        return record.getStringValue(CANCEL_COMMENT);
    }

    public static void setCancelComment(Record record, String cancelComment) {
        record.setFieldValue(CANCEL_COMMENT, cancelComment);
    }

    public static String getCancelDate(Record record) {
        return record.getStringValue(CANCEL_DATE);
    }

    public static void setCancelDate(Record record, String cancelDate) {
        record.setFieldValue(CANCEL_DATE, cancelDate);
    }

    public static String getCancelMethod(Record record) {
        return record.getStringValue(CANCEL_METHOD);
    }

    public static void setCancelMethod(Record record, String cancelMethod) {
        record.setFieldValue(CANCEL_METHOD, cancelMethod);
    }

    public static String getCancelReason(Record record) {
        return record.getStringValue(CANCEL_REASON);
    }

    public static void setCancelReason(Record record, String cancelReason) {
        record.setFieldValue(CANCEL_REASON, cancelReason);
    }

    public static String getMethodCode(Record record) {
        return record.getStringValue(METHOD_CODE);
    }

    public static void setMethodCode(Record record, String cancelReason) {
        record.setFieldValue(METHOD_CODE, cancelReason);
    }

    public static String getCancelType(Record record) {
        return record.getStringValue(CANCEL_TYPE);
    }

    public static void setCancelType(Record record, String cancelType) {
        record.setFieldValue(CANCEL_TYPE, cancelType);
    }

    public static String getCancelDistinctId(Record record) {
        return record.getStringValue(CANCEL_DISTINCT_ID);
    }

    public static void setCancelDistinctId(Record record, String cancelDistinctId) {
        record.setFieldValue(CANCEL_DISTINCT_ID, cancelDistinctId);
    }

    public static String getIsInitTransB(Record record) {
        return record.getStringValue(IS_INIT_TRANS_B);
    }

    public static void setIsInitTransB(Record record, String isInitTransB) {
        record.setFieldValue(IS_INIT_TRANS_B, isInitTransB);
    }

    public static String getSaveOfficialB(Record record) {
        return record.getStringValue(SAVE_OFFICIAL_B);
    }

    public static void setSaveOfficialB(Record record, String saveOfficialB) {
        record.setFieldValue(SAVE_OFFICIAL_B, saveOfficialB);
    }

    public static String getNewTransactionComment(Record record) {
        return record.getStringValue(NEW_TRANSACTION_COMMENT);
    }

    public static void setNewTransactionComment(Record record, String newTransactionComment) {
        record.setFieldValue(NEW_TRANSACTION_COMMENT, newTransactionComment);
    }

    public static String getRowNum(Record record) {
        return record.getStringValue(ROW_NUM);
    }

    public static void setRowNum(Record record, String rowNum) {
        record.setFieldValue(ROW_NUM, rowNum);
    }

    public static String getIsContinueAvailable(Record record) {
        return record.getStringValue(IS_CONTINUE_AVAILABLE);
    }

    public static void setIsContinueAvailable(Record record, String isContinueAvailable) {
        record.setFieldValue(IS_CONTINUE_AVAILABLE, isContinueAvailable);
    }

    public static String getAmalgamationB(Record record) {
        return record.getStringValue(AMALGAMATE_B);
    }

    public static void setAmalgamationB(Record record, String amalgamationB) {
        record.setFieldValue(AMALGAMATE_B, amalgamationB);
    }
    
    public static String getCancelDesc(Record record){
        return record.getStringValue(CANCEL_DESC);
    }
    
    public static void setCancelDesc(Record record, String cancelDesc){
        record.setFieldValue(CANCEL_DESC, cancelDesc);
    }

    public static void setTermBaseId(Record record, String termBaseId) {
        record.setFieldValue(TERM_BASE_ID, termBaseId);
    }

    public static String getTermBaseId(Record record) {
        return record.getStringValue(TERM_BASE_ID);
    }

    public static void setType(Record record, String type) {
        record.setFieldValue(TYPE, type);
    }

    public static String getType(Record record) {
        return record.getStringValue(TYPE);
    }

    public static void setReason(Record record, String reason) {
        record.setFieldValue(REASON, reason);
    }

    public static String getReason(Record record) {
        return record.getStringValue(REASON);
    }

    public static void setCovBaseId(Record record, String covBaseId) {
        record.setFieldValue(COV_BASE_ID, covBaseId);
    }

    public static String getCovBaseId(Record record) {
        return record.getStringValue(COV_BASE_ID);
    }
    
    public static String getPolCovCompId(Record record) {
        return record.getStringValue(POLICY_COV_COMPONENT_ID);
    }
    
    public static void setPolCovCompId(Record record, String polCovCompId) {
        record.setFieldValue(POLICY_COV_COMPONENT_ID, polCovCompId);
    }
    
    public static String getCompRecModeCode(Record record) {
        return record.getStringValue(COMP_REC_MODE_CODE);
    }
    
    public static void setCompRecModeCode(Record record, String compRecModeCode) {
        record.setFieldValue(COMP_REC_MODE_CODE, compRecModeCode);
    }
    
    public static String getCompOffRecId(Record record) {
        return record.getStringValue(COMP_OFF_REC_ID);
    }
    
    public static void setCompOffRecId(Record record, String compOffRecId) {
        record.setFieldValue(COMP_OFF_REC_ID, compOffRecId);
    }

    public static String getIsExtendB(Record record) {
        return record.getStringValue(IS_EXTEND_B);
    }

    public static void setIsExtendB(Record record, YesNoFlag isExtendB) {
        record.setFieldValue(IS_EXTEND_B, isExtendB);
    }

    public static String getProcess(Record record) {
        return record.getStringValue(PROCESS);
    }

    public static void setProcess(Record record, String process) {
        record.setFieldValue(PROCESS, process);
    }

    public class StatusCodeValues {
        public static final String NEW = "NEW";
        public static final String INVALID = "INVALID";
        public static final String WARNING = "WARNING";
    }

    //Possible code value for transCode field
    public class CancelTransCodeValues {
        public static final String CANCEL = "CANCEL";
        public static final String RISKCANCEL = "RISKCANCEL";
    }

    public class CancelReasonCodeValues {
        public static final String CANCUNDWR = "CANCUNDWR";
        public static final String SHORTTERM = "SHORTTERM";
        public static final String CANCPURGE = "CANCPURGE";
    }

    public class CancelMethodCodeValues {
        public static final String SHORTRATE = "SHORTRATE";
        public static final String PRORATE = "PRORATE";
    }

    public class CancelProcessCodeValues {
        public static final String CONTINUE = "CONTINUE";
    }

    public class CancelTypeCodeValues {
        public static final String PURGE = "PURGE";
        public static final String FLAT_CANCEL = "FLAT_CANCEL";
    }

}
