package dti.pm.transactionmgr.batchrenewalprocessmgr;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;

/**
 * Constants for Batch Renewal.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 5, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/01/2011       wfu         125309 - Added fields Status and Complete.
 * 09/14/2012       tcheng      137096 - Added fields policyNoFilter.
 * 03/13/2013       adeng       138243 - Added fields isAllExcluded.
 * 08/05/2014       AWU         156019 - Added new processCodeValue 'RELHOLDEOD'.
 * 08/27/2014       kxiang      156446 - a.Added field SELECT_IND.
 *                                       b.Modified getExcludeB method.
 * ---------------------------------------------------
 */
public class BatchRenewalFields {
    public static final String RENEWAL_EVENT_ID = "renewalEventId";
    public static final String RENEWAL_EVENT_IDS = "renewalEventIds";
    public static final String EXCLUDE_B = "excludeB";
    public static final String PROCESS_CODE = "processCode";
    public static final String START_SEARCH_DATE = "startSearchDateFilter";
    public static final String END_SEARCH_DATE = "endSearchDateFilter";
    public static final String TERM_TYPE = "termTypeFilter";
    public static final String POLICY_TYPE = "policyType";
    public static final String POLICY_TYPE_CODE = "policyTypeCode";
    public static final String POLICY_EXP_FROM_DATE = "policyExpFrom";
    public static final String POLICY_EXP_TO_DATE = "policyExpTo";
    public static final String EFFECTIVE_FROM_DATE = "effectiveFromDate";
    public static final String EFFECTIVE_TO_DATE = "effectiveToDate";
    public static final String TYPE = "type";
    public static final String POLICY_TERM_TYPE_CODE = "policyTermTypeCode";
    public static final String EVENT_NUMBERS = "eventNumbers";
    public static final String PRINT_DEVICE = "printDevice";
    public static final String POLICY_EXP_FROM = "policyExpFrom";
    public static final String POLICY_EXP_TO = "policyExpTo";
    public static final String ISSUE_STATE_LIST_DESC = "issueStateListDesc";
    public static final String POLICY_TYPE_CODE_DESC = "policyTypeCodeDesc";
    public static final String PRACTICE_STATE_LIST_DESC = "practiceStateListDesc";
    public static final String AGENT_LIST_DESC = "agentListDesc";
    public static final String UNDERWRITER_LIST_DESC = "underwriterListDesc";
    public static final String STATUS = "Status";
    public static final String POLICY_NO_FILTER = "policyNoFilter";
    public static final String IS_ALL_EXCLUDED = "isAllExcluded";
    public static final String SELECT_IND = "SELECT_IND";

    public static String getRenewalEventId(Record record) {
        return record.getStringValue(RENEWAL_EVENT_ID);
    }

    public static void setRenewalEventId(Record record, String renewalEventId) {
        record.setFieldValue(RENEWAL_EVENT_ID, renewalEventId);
    }

    public static void setRenewalEventIds(Record record, String renewalEventIds) {
        record.setFieldValue(RENEWAL_EVENT_IDS, renewalEventIds);
    }

    public static String getExcludeB(Record record) {
        return record.getStringValue(EXCLUDE_B);
    }

    public static void setExcludeB(Record record, String excludeB) {
        record.setFieldValue(EXCLUDE_B, excludeB);
    }

    public static String getProcessCode(Record record) {
        return record.getStringValue(PROCESS_CODE);
    }

    public static void setProcessCode(Record record, String processCode) {
        record.setFieldValue(PROCESS_CODE, processCode);
    }

    public static String getStartSearchDate(Record record) {
        return record.getStringValue(START_SEARCH_DATE);
    }

    public static String getEndSearchDate(Record record) {
        return record.getStringValue(END_SEARCH_DATE);
    }

    public static String getTermType(Record record) {
        return record.getStringValue(TERM_TYPE);
    }

    public static void setTermType(Record record, String termType) {
        record.setFieldValue(TERM_TYPE, termType);
    }

    public static void setPolicyType(Record record, String policyType) {
        record.setFieldValue(POLICY_TYPE, policyType);
    }

    public static String getPolicyTypeCode(Record record) {
        return record.getStringValue(POLICY_TYPE_CODE);
    }

    public static void setPolicyExpFromDate(Record record, String policyExpFromDate) {
        record.setFieldValue(POLICY_EXP_FROM_DATE, policyExpFromDate);
    }

    public static void setPolicyExpToDate(Record record, String policyExpToDate) {
        record.setFieldValue(POLICY_EXP_TO_DATE, policyExpToDate);
    }

    public static String getEffectiveFromDate(Record record) {
        return record.getStringValue(EFFECTIVE_FROM_DATE);
    }

    public static String getEffectiveToDate(Record record) {
        return record.getStringValue(EFFECTIVE_TO_DATE);
    }

    public static void setType(Record record, String type) {
        record.setFieldValue(TYPE, type);
    }

    public static String getPolicyTermTypeCode(Record record) {
        return record.getStringValue(POLICY_TERM_TYPE_CODE);
    }

    public static void setEventNumbers(Record record, String eventNumbers) {
        record.setFieldValue(EVENT_NUMBERS, eventNumbers);
    }

    public static String getPolicyExpFrom(Record record) {
        return record.getStringValue(POLICY_EXP_FROM);
    }

    public static String getPolicyExpTo(Record record) {
        return record.getStringValue(POLICY_EXP_TO);
    }

    public static String getIssueStateListDesc(Record record) {
        return record.getStringValue(ISSUE_STATE_LIST_DESC);
    }

    public static void setIssueStateListDesc(Record record, String issueStateListDesc) {
        record.setFieldValue(ISSUE_STATE_LIST_DESC, issueStateListDesc);
    }

    public static String getPolicyTypeCodeDesc(Record record) {
        return record.getStringValue(POLICY_TYPE_CODE_DESC);
    }

    public static void setPolicyTypeCodeDesc(Record record, String policyTypeCodeDesc) {
        record.setFieldValue(POLICY_TYPE_CODE_DESC, policyTypeCodeDesc);
    }

    public static String getPracticeStateListDesc(Record record) {
        return record.getStringValue(PRACTICE_STATE_LIST_DESC);
    }

    public static void setPracticeStateListDesc(Record record, String policyTypeCodeDesc) {
        record.setFieldValue(PRACTICE_STATE_LIST_DESC, policyTypeCodeDesc);
    }

    public static String getAgentListDesc(Record record) {
        return record.getStringValue(AGENT_LIST_DESC);
    }

    public static void setAgentListDesc(Record record, String policyTypeCodeDesc) {
        record.setFieldValue(AGENT_LIST_DESC, policyTypeCodeDesc);
    }

    public static String getUnderwriterListDesc(Record record) {
        return record.getStringValue(UNDERWRITER_LIST_DESC);
    }

    public static void setUnderwriterListDesc(Record record, String policyTypeCodeDesc) {
        record.setFieldValue(UNDERWRITER_LIST_DESC, policyTypeCodeDesc);
    }

    public static String getStatus(Record record) {
        return record.getStringValue(STATUS);
    }

    public static void setStatus(Record record, String status) {
        record.setFieldValue(STATUS, status);
    }

    public static String getPolicyNoFilter(Record record) {
        return record.getStringValue(POLICY_NO_FILTER);
    }

    public static void setPolicyNoFilter(Record record, String policyNoFilter) {
        record.setFieldValue(POLICY_NO_FILTER, policyNoFilter);
    }

    public static YesNoFlag getSelectInd(Record record) {
        String selectInd = record.getStringValue(SELECT_IND);
        if (selectInd.equals("-1")) {
            selectInd = "Y";
        }
        else if (selectInd.equals("0")) {
            selectInd = "N";
        }
        return YesNoFlag.getInstance(selectInd);
    }

    public static void setSelectInd(Record record, String selectInd) {
        record.setFieldValue(SELECT_IND, selectInd);
    }

    public class ProcessCodeValues {
        public static final String PRERENEWAL = "PRERENEWAL";
        public static final String PRINT = "PRINT";
        public static final String RELHOLDEOD = "RELHOLDEOD";
    }

    public class PolicyTermTypeCodeValues {
        public static final String COMMON = "COMMON";
        public static final String NON_COMMON = "NON_COMMON";
    }

    public class StatusCodeValues {
        public static final String COMPLETE = "COMPLETE";
    }

    public class BatchRenewalTypeValues {
        public static final String DELETE_WIP = "DELETE_WIP";
        public static final String RERATE = "RERATE";
        public static final String ISSUE = "ISSUE";
    }

    public class BatchRenewalErrorTypeValues {
        public static final String ISSUE_ERR_TYPE = "Issue";
        public static final String BATCH_PRINT_ERR_TYPE = "Batch Print";
        public static final String DELETE_WIP_ERR_TYPE = "Delete WIP";
        public static final String RERATE_ERR_TYPE = "Rerate";
    }
}
