package dti.pm.policymgr;

import dti.oasis.recordset.Record;
import dti.oasis.busobjs.YesNoFlag;
import dti.pm.busobjs.PolicyCycleCode;
import dti.pm.busobjs.QuoteCycleCode;

/**
 * Getter/setter and helper fields for policy management operations.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 29, 2007
 *
 * @author jmpotosky
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 02/22/2011       wfu         113063 - Added QUOTE_VERSION for multiple quote versions
 * 08/25/2011       dzhang      121130 - Added RISK.
 * 09/23/2011       wfu         125369 - Added renamed several fields duplicate in policy header and policy page.
 * 09/04/2012       adeng       135972 - Added one more field "hasRisk" in order it can be set
 *                                       it into inputRecord to do further processing.
 * 10/05/2012       xnie        133766 - Added fields and get/set functions.
 * 01/02/2013       tcheng      139862 - Added a field "warning" and set function.
 * 04/16/2014       awu         150201 - Added orgHierId, orgHierRootId.
 * 08/13/2015       wdang       157211 - Added method setTermEffFromDate, setTermEffToDate.
 * 11/05/2015       tzeng       165790 - Added policyPhaseCode.
 * 01/07/2016       tzeng       166924 - Added policyRetroDate.
 * 08/26/2016       wdang       167534 - Added quoteCycleCode.
 * 12/15/2016       tzeng       166929 - Added softValidationB field, and add its set/get method.
 * 09/21/2017       eyin        169483 - Added field FROM_EXPOSURE_B and get functions.
 * ---------------------------------------------------
 */

public class PolicyFields {
    public static final String ROLLING_IBNR_DATE = "rollingIbnrDate";
    public static final String ORIGINAL_ROLLING_IBNR_DATE = "originalRollingIbnrDate";
    public static final String POLICY_CYCLE_CODE = "policyCycleCode";
    public static final String QUOTE_CYCLE_CODE = "quoteCycleCode";
    public static final String POLICY_TYPE_CODE = "policyTypeCode";
    public static final String ISSUE_STATE_CODE = "issueStateCode";
    public static final String REGIONAL_OFFICE = "regionalOffice";
    public static final String ISSUE_COMPANY_ID = "issueCompanyId";
    public static final String POLICY_ID = "policyId";
    public static final String TERM_BASE_RECORD_ID = "termBaseRecordId";
    public static final String TERM_LIST = "termList";
    public static final String EFFECTIVE_FROM_DATE = "effectiveFromDate";
    public static final String EFFECTIVE_TO_DATE = "effectiveToDate";
    public static final String NEW_ADMIN = "newAdmin";
    public static final String CURRENT_ADMIN = "currentAdmin";
    public static final String NEW_TERM_EFF_DATE = "newTermEffectiveFromDate";
    public static final String NEW_TERM_EXP_DATE = "newTermEffectiveToDate";
    public static final String TERM_EFFECTIVE_FROM_DATE = "termEffectiveFromDate";
    public static final String TERM_EFFECTIVE_TO_DATE = "termEffectiveToDate";
    public static final String ISSUE_COMPANY_ENTITY_ID = "issueCompanyEntityId";
    public static final String POLICY_HOLDER_ENTITY_ID = "policyHolderEntityType";
    public static final String ACCOUNTING_DATE = "accountingDate";
    public static final String OOSE_TERM_EXP_DATE = "ooseTermExpDate";
    public static final String SUBMIT_AS = "submitAs";
    public static final String REQUEST_ID = "requestId";
    public static final String SUBMIT_AS_CODE = "submitAsCode";
    public static final String POLICY_PHASE_CODE = "policyPhaseCode";
    public static final String POLICY_RETRO_DATE = "policyRetroDate";
    public static final String SOFT_VALIDATION_B = "softValidationB";

    // for select address
    public static final String PROSPECT = "PROSPECT";
    public static final String ROLE_TYPE_CODE = "roleTypeCode";
    public static final String ENTITY_ROLE_ID = "entityRoleId";
    public static final String SOURCE_ID = "sourceId";
    public static final String SELECTED_ADDRESS_B = "selectedAddressB";
    public static final String FROM_EXPOSURE_B = "fromExposureB";
    public static final String PRIMARY_ADDRESS_B = "primaryAddressB";
    public static final String ADDRESS_ROLE_XREF_ID = "addressRoleXrefId";
    public static final String ADDRESS_ID = "addressId";
    public static final String SOURCE_RECORD_ID = "SourceRecordId";

    // for quote version
    public static final String QUOTE_VERSION = "quoteVersion";
    // rename several duplicated fields
    public static final String TERM_EFF_FROM_DATE = "termEffFromDate";
    public static final String TERM_EFF_TO_DATE = "termEffToDate";
    public static final String HAS_RISK = "hasRisk";

    // for warning
    public static final String WARING = "warning";

    public static final String ORG_HIER_ROOT_ID = "orgHierRootId";
    public static final String ORG_HIER_ID = "orgHierId";

    public static String getRollingIbnrDate(Record record) {
        return record.getStringValue(ROLLING_IBNR_DATE);
    }

    public static String getOriginalRollingIbnrDate(Record record) {
        return record.getStringValue(ORIGINAL_ROLLING_IBNR_DATE);
    }

    public static void setOriginalRollingIbnrDate(Record record, String originalRollingIbnrDate) {
        record.setFieldValue(ORIGINAL_ROLLING_IBNR_DATE, originalRollingIbnrDate);
    }

    public static PolicyCycleCode getPolicyCycleCode(Record record) {
        Object value = record.getFieldValue(POLICY_CYCLE_CODE);
        PolicyCycleCode result = null;
        if (value == null || value instanceof PolicyCycleCode) {
            result = (PolicyCycleCode) value;
        }
        else {
            result = PolicyCycleCode.getInstance(value.toString());
        }
        return result;
    }

    public static void setPolicyCycleCode(Record record, PolicyCycleCode policyCycleCode) {
        record.setFieldValue(POLICY_CYCLE_CODE, policyCycleCode);
    }

    public static QuoteCycleCode getQuoteCycleCode(Record record) {
        Object value = record.getFieldValue(QUOTE_CYCLE_CODE);
        QuoteCycleCode result = null;
        if (value == null || value instanceof QuoteCycleCode) {
            result = (QuoteCycleCode) value;
        }
        else {
            result = QuoteCycleCode.getInstance(value.toString());
        }
        return result;
    }

    public static void setQuoteCycleCode(Record record, QuoteCycleCode quoteCycleCode) {
        record.setFieldValue(QUOTE_CYCLE_CODE, quoteCycleCode);
    }

    public static void setPolicyTypeCode(Record record, String policyTypeCode) {
        record.setFieldValue(POLICY_TYPE_CODE, policyTypeCode);
    }
    public static void setIssueStateCode(Record record, String issueStateCode) {
        record.setFieldValue(ISSUE_STATE_CODE,issueStateCode);
    }
    public static void setRegionalOffice(Record record, String regionalOffice) {
        record.setFieldValue(REGIONAL_OFFICE,regionalOffice);
    }
    public static void setIssueCompanyId(Record record, String issueCompanyId) {
        record.setFieldValue(ISSUE_COMPANY_ID,issueCompanyId);
    }
    public static void setPolicyId(Record record, String policyId) {
        record.setFieldValue(POLICY_ID,policyId);
    }
    public static void setTermBaseRecordId(Record record, String termBaseRecordId) {
        record.setFieldValue(TERM_BASE_RECORD_ID,termBaseRecordId);
    }
    public static String getTermList(Record record) {
        return record.getStringValue(TERM_LIST);
    }
    public static void setTermList(Record record, String termList) {
        record.setFieldValue(TERM_LIST, termList);
    }
    public static String getEffectiveFromDate(Record record) {
        return record.getStringValue(EFFECTIVE_FROM_DATE);
    }
    public static void setEffectiveFromDate(Record record, String effectiveFromDate) {
        record.setFieldValue(EFFECTIVE_FROM_DATE,effectiveFromDate);
    }
    public static void setEffectiveToDate(Record record, String effectiveToDate) {
        record.setFieldValue(EFFECTIVE_TO_DATE,effectiveToDate);
    }
    public static String getEffectiveToDate(Record record) {
        return record.getStringValue(EFFECTIVE_TO_DATE);
    }
    public static void setNewAdmin(Record record, String newAdmin) {
        record.setFieldValue(NEW_ADMIN,newAdmin);
    }
    public static void setCurrentAdmin(Record record, String currentAdmin) {
        record.setFieldValue(CURRENT_ADMIN,currentAdmin);
    }
    public static String getNewTermEffDate(Record record) {
        return record.getStringValue(NEW_TERM_EFF_DATE);
    }

    public static void setNewTermEffDate(Record record, String termEffDate) {
        record.setFieldValue(NEW_TERM_EFF_DATE, termEffDate);
    }

    public static String getNewTermExpDate(Record record) {
        return record.getStringValue(NEW_TERM_EXP_DATE);
    }

    public static void setNewTermExpDate(Record record, String termExpDate) {
        record.setFieldValue(NEW_TERM_EXP_DATE, termExpDate);
    }
    public static void setTermEffectiveFromDate(Record record, String termEffectiveFromDate){
        record.setFieldValue(TERM_EFFECTIVE_FROM_DATE,termEffectiveFromDate);
    }
    public static String getTermEffectiveFromDate(Record record){
        return record.getStringValue(TERM_EFFECTIVE_FROM_DATE);
    }
    public static void setTermEffectiveToDate(Record record, String termEffectiveToDate){
        record.setFieldValue(TERM_EFFECTIVE_TO_DATE,termEffectiveToDate);
    }
    public static String getTermEffectiveToDate(Record record){
        return record.getStringValue(TERM_EFFECTIVE_TO_DATE);
    }
    public static void setIssueCompanyEntityId(Record record, String issueCompanyEntityId) {
        record.setFieldValue(ISSUE_COMPANY_ENTITY_ID,issueCompanyEntityId);
    }
    public static void setPolicyHolderEntityType(Record record, String policyHolderEntityType) {
        record.setFieldValue(POLICY_HOLDER_ENTITY_ID,policyHolderEntityType);
    }
    public static void setAccountingDate(Record record, String accountingDate) {
        record.setFieldValue(ACCOUNTING_DATE,accountingDate);
    }
    public static String getOoseTermExpDate(Record record){
        return record.getStringValue(OOSE_TERM_EXP_DATE);
    }
    public static void setOoseTermExpDate(Record record, String ooseTermExpDate) {
        record.setFieldValue(OOSE_TERM_EXP_DATE,ooseTermExpDate);
    }

    public static void setRoleTypeCode(Record record, String roleTypeCode) {
        record.setFieldValue(ROLE_TYPE_CODE, roleTypeCode);
    }

    public static String getEntityRoleId(Record record) {
        return record.getStringValue(ENTITY_ROLE_ID);
    }

    public static void setEntityRoleId(Record record, String entityRoleId) {
        record.setFieldValue(ENTITY_ROLE_ID, entityRoleId);
    }

    public static void setSourceId(Record record, String sourceId) {
        record.setFieldValue(SOURCE_ID, sourceId);
    }

    public static void setSourceRecordId(Record record, String sourceRecordId) {
        record.setFieldValue(SOURCE_RECORD_ID, sourceRecordId);
    }

    public static YesNoFlag getSelectedAddressB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(SELECTED_ADDRESS_B));
    }

    public static YesNoFlag getFromExposureB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(FROM_EXPOSURE_B));
    }

    public static YesNoFlag getPrimaryAddressB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(PRIMARY_ADDRESS_B));
    }

    public static String getAddressRoleXrefId(Record record) {
        return record.getStringValue(ADDRESS_ROLE_XREF_ID);
    }

    public static void setAddressRoleXrefId(Record record, String addressRoleXrefId) {
        record.setFieldValue(ADDRESS_ROLE_XREF_ID, addressRoleXrefId);
    }

    public static String getAddressId(Record record) {
        return record.getStringValue(ADDRESS_ID);
    }

    public static void setAddressId(Record record, String addressId) {
        record.setFieldValue(ADDRESS_ID, addressId);
    }

    public static String getQuoteVersion(Record record) {
        return record.getStringValue(QUOTE_VERSION);
    }

    public static void setQuoteVersion(Record record, String quoteVersion) {
        record.setFieldValue(QUOTE_VERSION, quoteVersion);
    }

    public static String getTermEffFromDate(Record record){
        return record.getStringValue(TERM_EFF_FROM_DATE);
    }

    public static void setTermEffFromDate(Record record, String termEffFromDate){
        record.setFieldValue(TERM_EFF_FROM_DATE, termEffFromDate);
    }

    public static String getTermEffToDate(Record record){
        return record.getStringValue(TERM_EFF_TO_DATE);
    }

    public static void setTermEffToDate(Record record, String termEffToDate){
        record.setFieldValue(TERM_EFF_TO_DATE, termEffToDate);
    }

    public static void setHasRisk(Record record, YesNoFlag hasRisk) {
        record.setFieldValue(HAS_RISK, hasRisk);
    }

    public static YesNoFlag getHasRisk(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(HAS_RISK));
    }

    public static void setSubmitAs(Record record, String submitAs) {
        record.setFieldValue(SUBMIT_AS, submitAs);
    }

    public static String getRequestId(Record record) {
        return record.getStringValue(REQUEST_ID);
    }

    public static void setRequestId(Record record, String requestId) {
        record.setFieldValue(REQUEST_ID, requestId);
    }

    public static String getPolicyPhaseCode(Record record) {
        return record.getStringValue(POLICY_PHASE_CODE);
    }

    public static void setPolicyPhaseCode(Record record, String policyPhaseCode) {
        record.setFieldValue(POLICY_PHASE_CODE, policyPhaseCode);
    }

    public static String getPolicyRetroDate(Record record) {
        return record.getStringValue(POLICY_RETRO_DATE);
    }

    public static void setPolicyRetroDate(Record record, String policyRetroDate) {
        record.setFieldValue(POLICY_RETRO_DATE, policyRetroDate);
    }

    public static String getSoftValidationB(Record record) {
        return record.getStringValue(SOFT_VALIDATION_B);
    }

    public static void setSoftValidationB(Record record, String softValidationB) {
        record.setFieldValue(SOFT_VALIDATION_B, softValidationB);
    }

    public static void setWarning(Record record, String warning) {
        record.setFieldValue(WARING, warning);
    }

    public static String getOrgHierRootId(Record record) {
        return record.getStringValue(ORG_HIER_ROOT_ID);
    }

    public static void setOrgHierRootId(Record record, String orgHierRootId) {
        record.setFieldValue(ORG_HIER_ROOT_ID, orgHierRootId);
    }

    public static String getOrgHierId(Record record) {
        return record.getStringValue(ORG_HIER_ID);
    }

    public static void setOrgHierId(Record record, String orgHierId) {
        record.setFieldValue(ORG_HIER_ID, orgHierId);
    }

    public class SubmitAsCodeValues {
        public static final String ON_DEMAND = "ONDEMAND";
        public static final String BATCH = "BATCH";
    }

    public class RoleTypeValues {
        public static final String POLICYHOLDER = "POLICYHOLDER";
        public static final String COIHOLDER = "COIHOLDER";
        public static final String RISK = "RISK";
    }
}
