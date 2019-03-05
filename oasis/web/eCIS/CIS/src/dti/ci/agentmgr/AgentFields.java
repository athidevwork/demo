package dti.ci.agentmgr;

import dti.oasis.recordset.Record;

import java.util.Date;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 1, 2007
 *
 * @author gjlong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * Apr 17, 2008     James       Issue#75265 Modify code according to code review
 * Apr 18, 2008     James       Issue#81847 CIS -> Agent Page ->Agency Contract Commission
 *                              part: Set the NB (RN, ERE) Comm Basis to be persent, set
 *                              the NB (RN, ERE) to be less than 0 and save. No message
 *                              returned and changes will be saved
 * Jul 15, 2016     Iwang       Issue 177546: Add new fields for Agents, Agent Overrides.
 * Apr 26, 2016     Iwang       Issue 167601: Add new fields for Agency Contract.
 * 09/27/2016       htwang      Issue 178227 - 1) Add new fields agentStaffEntityName and agentStaffEntityId
 *                                             2) Add new field agentStaffListGrid
 * ---------------------------------------------------
 */
public class AgentFields {

    public static final String IS_PRODUCER_AGENT_LIC_ID_AVAILABLE = "isProducerAgentLicIdAvailable";
    public static final String IS_ROW_ELIGIBLE_FOR_DELETE = "isRowEligibleForDelete";
    public static final String IS_COMM_PAY_CODE_AVAILABLE = "isCommPayCodeAvailable";
    public static final String IS_ADD_AVAILABLE = "isAddAvailable";

    public static final String AGENT_ID = "AgentId";
    public static final String AGENT_STAFF_GRID_ID = "agentStaffListGrid";

    public static final String SITUATION_CODE = "situationCode";
    public static final String EFFECTIVE_START_DATE = "effectiveStartDate";
    public static final String EFFECTIVE_END_DATE = "effectiveEndDate";


    public static final String PAY_COMMISSION_ID = "AgentPayCommissionId";
    public static final String PAY_COMMISSION_PAY_CODE = "commissionPayCode";
    public static final String PAY_COMMISSION_EFFECTIVE_START_DATE = "effectiveStartDate1";
    public static final String PAY_COMMISSION_EFFECTIVE_END_DATE = "effectiveEndDate1";

    public static final String CONTRACT_ID = "AgentLicenseId";
    public static final String CONTRACT_EFFECTIVE_START_DATE = "effectiveStartDate2";
    public static final String CONTRACT_EFFECTIVE_END_DATE = "effectiveEndDate2";
    public static final String CONTRACT_APPOINTMENT_START_DATE = "appointmentStartDate";
    public static final String CONTRACT_APPOINTMENT_END_DATE = "appointmentEndDate";
    public static final String CONTRACT_ADDL_LICENSE_START_DATE = "addlLicenseStartDate";
    public static final String CONTRACT_ADDL_LICENSE_END_DATE = "addlLicenseEndDate";
    public static final String CONTRACT_NIPN_START_DATE = "nipnStartDate";
    public static final String CONTRACT_NIPN_END_DATE = "nipnEndDate";
    public static final String CONTRACT_CLASS_CODE = "licenseClassCode";
    public static final String CONTRACT_NUMBER = "licenseNumber";
    public static final String CONTRACT_TYPE = "licenseTypeCode";
    public static final String CONTRACT_STATE_CODE = "stateCode";
    public static final String CONTRACT_ISSUE_COMPANY_ENTITY_FK_LIST = "issueCompanyEntityFkList";

    public static final String CONTRACT_COMMISSION_ID = "AgentLicenseCommissionId";
    public static final String CONTRACT_COMMISSION_POLICY_TYPE_CODE = "policyTypeCode";
    public static final String CONTRACT_COMMISSION_PRIMARY_RISK_TYPE_CODE = "primaryRiskTypeCode";
    public static final String CONTRACT_COMMISSION_ISSUE_COMPANY_ENTITY_ID = "issueCompanyEntityId";
    public static final String IS_PRIMARY_RISK_TYPE_CODE_VISIBLE = "isPrimaryRiskTypeCodeVisible";
    public static final String CONTRACT_COMMISSION_EFFECTIVE_START_DATE = "effectiveStartDate3";
    public static final String CONTRACT_COMMISSION_EFFECTIVE_END_DATE = "effectiveEndDate3";
    public static final String NB_COMM_BASIS = "newbusCommBasis";
    public static final String NB_COMM_RATE = "newbusRate";
    public static final String NB_COMM_LIMIT = "newbusCommLimit";
    public static final String NB_COMM_FLAT_AMOUNT = "newbusFlatAmt";
    public static final String NB_COMM_RATE_SCHEDULE = "newbusCommRateSchedId";
    public static final String RN_COMM_BASIS = "renewalCommBasis";
    public static final String RN_COMM_RATE = "renewalRate";
    public static final String RN_COMM_LIMIT = "renewalCommLimit";
    public static final String RN_COMM_FLAT_AMOUNT = "renewalFlatAmt";
    public static final String RN_COMM_RATE_SCHEDULE = "renewalCommRateSchedId";
    public static final String ERE_COMM_BASIS = "ereCommBasis";
    public static final String ERE_COMM_RATE = "ereRate";
    public static final String ERE_COMM_LIMIT = "ereCommLimit";
    public static final String ERE_COMM_FLAT_AMOUNT = "ereFlatAmt";
    public static final String ERE_COMM_RATE_SCHEDULE = "ereCommRateSchedId";

    public static final String NEW_BUSINESS = "New Business";
    public static final String RENEWWAL = "Renewal";
    public static final String ERE = "ERE";

    public static final String PASS_LICENSE_NUMBER = "passLicenseNumber";
    public static final String SEARCH_BY = "searchBy";
    public static final String SEARCH_STRING = "searchString";

    public static final String ID = "ID";
    public static final String ENTITY_ID = "entityId";

    public static final String EFFECTIVE_FROM_DATE = "effectiveFromDate";
    public static final String EFFECTIVE_TO_DATE = "effectiveToDate";
    public static final String POLICY_ID = "policyId";
    public static final String FORM_BUCKET_CODE = "formBucketCode";
    public static final String COPY_TYPE_CODE = "copyTypeCode";
    public static final String ISSUE_COMPANY_ENTITY_ID = "issueCompanyEntityId";
    public static final String ACTION = "action";
    public static final String AGENT_OUTPUT_OPTION_ID = "agentOutputOptionId";
    public static final String ADDRESS_ID = "addressId";
    public static final String ADDRESS_OPTION_CODE = "addressOptionCode";
    public static final String ADDRESS_OPTION_CODE_NA = "NA^^";

    public static final String AGENT_STAFF_ID = "agentStaffId";
    public static final String AGENT_STAFF_ENTITY_NAME = "agentStaffEntityName";
    public static final String AGENT_STAFF_ENTITY_ID = "agentStaffEntityId";
    public static final String BRANCH_OFFICE_ADDRESS_ID = "branchOfficeAddressId";
    public static final String STAFF_EFF_START_DATE = "staffEffStartDate";
    public static final String STAFF_EFF_END_DATE = "staffEffEndDate";

    public static final String AGENT_STAFF_OVERRIDE_ID = "agentStaffOverrideId";
    public static final String OVERRIDE_POLICY_TYPE = "overridePolicyTypeCode";
    public static final String OVERRIDE_STATE_CODE = "overrideStateCode";
    public static final String OVERRIDE_ISSUE_COMPANY = "issueCompanyEntityId";
    public static final String OVERRIDE_NB_COMM_RATE = "overrideNewbusRate";
    public static final String OVERRIDE_RN_COMM_RATE = "overrideRenewalRate";
    public static final String OVERRIDE_ERE_COMM_RATE = "overrideEreRate";
    public static final String OVERRIDE_EFF_START_DATE = "overrideEffStartDate";
    public static final String OVERRIDE_EFF_END_DATE = "overrideEffEndDate";

    /**
     * get id
     * @param record
     * @return
     */
    public static String getId(Record record){
        return record.getStringValue(AgentFields.ID);
    }

    /**
     * set id
     * @param record
     * @param id
     */
    public static void setId(Record record, String id) {
        record.setFieldValue(AgentFields.ID, id);
    }

    /**
     * get agent license ID
     * @param record
     * @return
     */
    public static String getAgentLicenseId(Record record){
        return record.getStringValue(AgentFields.CONTRACT_ID);
    }

    /**
     * set agent license ID
     * @param record
     * @param agentLicenseId
     */
    public static void setAgentLicenseId(Record record, String agentLicenseId) {
        record.setFieldValue(AgentFields.CONTRACT_ID, agentLicenseId);
    }
    /**
     * get agent ID
     * @param record
     * @return
     */
    public static String getAgentId(Record record){
        return record.getStringValue(AgentFields.AGENT_ID);
    }

    /**
     * set agent ID
     * @param record
     * @param agentId
     */
    public static void setAgentId(Record record, String agentId) {
        record.setFieldValue(AgentFields.AGENT_ID, agentId);
    }

    /**
     * get passed parameter license number
     * @param record
     * @return
     */
    public static String getPassLicenseNumber(Record record){
        return record.getStringValue(AgentFields.PASS_LICENSE_NUMBER);
    }
    /**
     * get search by
     * @param record
     * @return
     */
    public static String getSearchBy(Record record){
        return record.getStringValue(AgentFields.SEARCH_BY);
    }
    /**
     * set search by
     * @param record
     * @param searchBy
     */
    public static void setSearchBy(Record record, String searchBy) {
        record.setFieldValue(AgentFields.SEARCH_BY, searchBy);
    }
    /**
     * get search string
     * @param record
     * @return
     */
    public static String getSearchString(Record record){
        return record.getStringValue(AgentFields.SEARCH_STRING);
    }
    /**
     * set agent ID
     * @param record
     * @param searchString
     */
    public static void setSearchString(Record record, String searchString) {
        record.setFieldValue(AgentFields.SEARCH_STRING, searchString);
    }

    /**
     * get effective Start Date
     * @param record
     * @return
     */
    public static Date getEffectiveStartDate(Record record){
        return record.getDateValue(AgentFields.EFFECTIVE_START_DATE);
    }
    /**
     * set effective Start Date
     * @param record
     * @param effectiveStartDate
     */
    public static void setEffectiveStartDate(Record record, String effectiveStartDate) {
        record.setFieldValue(AgentFields.EFFECTIVE_START_DATE, effectiveStartDate);
    }

    /**
     * get effective End Date
     * @param record
     * @return
     */
    public static Date getEffectiveEndDate(Record record){
        return record.getDateValue(AgentFields.EFFECTIVE_END_DATE);
    }
    /**
     * get effective End Date string
     * @param record
     * @return
     */
    public static String getEffectiveEndDateString(Record record){
        return record.getStringValue(AgentFields.EFFECTIVE_END_DATE);
    }
    /**
     * set effective End Date
     * @param record
     * @param effectiveEndDate
     */
    public static void setEffectiveEndDate(Record record, String effectiveEndDate) {
        record.setFieldValue(AgentFields.EFFECTIVE_END_DATE, effectiveEndDate);
    }
    /**
     * get AgentPayCommissionId
     * @param record
     * @return
     */
    public static String getAgentPayCommissionId(Record record){
        return record.getStringValue(AgentFields.PAY_COMMISSION_ID);
    }
    /**
     * get pay commission effective Start Date
     * @param record
     * @return
     */
    public static Date getPayCommissionEffectiveStartDate(Record record){
        return record.getDateValue(AgentFields.PAY_COMMISSION_EFFECTIVE_START_DATE);
    }
    /**
     * set pay commission effective Start Date
     * @param record
     * @param payCommissionEffectiveStartDate
     */
    public static void setPayCommissionEffectiveStartDate(Record record, String payCommissionEffectiveStartDate) {
        record.setFieldValue(AgentFields.PAY_COMMISSION_EFFECTIVE_START_DATE, payCommissionEffectiveStartDate);
    }

    /**
     * get pay commission effective End Date
     * @param record
     * @return
     */
    public static Date getPayCommissionEffectiveEndDate(Record record){
        return record.getDateValue(AgentFields.PAY_COMMISSION_EFFECTIVE_END_DATE);
    }

    /**
     * get pay commission effective End Date string
     * @param record
     * @return
     */
    public static String getPayCommissionEffectiveEndDateString(Record record){
        return record.getStringValue(AgentFields.PAY_COMMISSION_EFFECTIVE_END_DATE);
    }
    /**
     * set pay commission effective End Date
     * @param record
     * @param payCommissionEffectiveEndDate
     */
    public static void setPayCommissionEffectiveEndDate(Record record, String payCommissionEffectiveEndDate) {
        record.setFieldValue(AgentFields.PAY_COMMISSION_EFFECTIVE_END_DATE, payCommissionEffectiveEndDate);
    }


    /**
     * get contract effective Start Date
     * @param record
     * @return
     */
    public static Date getContractEffectiveStartDate(Record record){
        return record.getDateValue(AgentFields.CONTRACT_EFFECTIVE_START_DATE);
    }

    /**
     * get Contract effective Start Date string
     * @param record
     * @return
     */
    public static String getContractEffectiveStartDateString(Record record){
        return record.getStringValue(AgentFields.CONTRACT_EFFECTIVE_START_DATE);
    }

    /**
     * set Contract effective Start Date
     * @param record
     * @param contractEffectiveStartDate
     */
    public static void setContractEffectiveStartDate(Record record, String contractEffectiveStartDate) {
        record.setFieldValue(AgentFields.CONTRACT_EFFECTIVE_START_DATE, contractEffectiveStartDate);
    }

    /**
     * get Contract effective End Date
     * @param record
     * @return
     */
    public static Date getContractEffectiveEndDate(Record record){
        return record.getDateValue(AgentFields.CONTRACT_EFFECTIVE_END_DATE);
    }

    /**
     * get Contract effective End Date string
     * @param record
     * @return
     */
    public static String getContractEffectiveEndDateString(Record record){
        return record.getStringValue(AgentFields.CONTRACT_EFFECTIVE_END_DATE);
    }

    /**
     * set Contract effective End Date
     * @param record
     * @param contractEffectiveEndDate
     */
    public static void setContractEffectiveEndDate(Record record, String contractEffectiveEndDate) {
        record.setFieldValue(AgentFields.CONTRACT_EFFECTIVE_END_DATE, contractEffectiveEndDate);
    }



    /**
     * get contract appointment Start Date
     * @param record
     * @return
     */
    public static Date getAppointmentStartDate(Record record){
        return record.getDateValue(AgentFields.CONTRACT_APPOINTMENT_START_DATE);
    }
    /**
     * set appointment Start Date
     * @param record
     * @param appointmentStartDate
     */
    public static void setAppointmentStartDate(Record record, String appointmentStartDate) {
        record.setFieldValue(AgentFields.CONTRACT_APPOINTMENT_START_DATE, appointmentStartDate);
    }

    /**
     * get Contract appointment End Date
     * @param record
     * @return
     */
    public static Date getAppointmentEndDate(Record record){
        return record.getDateValue(AgentFields.CONTRACT_APPOINTMENT_END_DATE);
    }

    /**
     * get Contract appointment End Date string
     * @param record
     * @return
     */
    public static String getAppointmentEndDateString(Record record){
        return record.getStringValue(AgentFields.CONTRACT_APPOINTMENT_END_DATE);
    }

    /**
     * set Contract appointment End Date
     * @param record
     * @param appointmentEndDate
     */
    public static void setAppointmentEndDate(Record record, String appointmentEndDate) {
        record.setFieldValue(AgentFields.CONTRACT_APPOINTMENT_END_DATE, appointmentEndDate);
    }

    /**
     * get Contract Additional License Start Date
     *
     * @param record
     * @return
     */
    public static Date getContractAddlLicenseStartDate(Record record) {
        return record.getDateValue(AgentFields.CONTRACT_ADDL_LICENSE_START_DATE);
    }

    /**
     * get Contract Additional License End Date
     *
     * @param record
     * @return
     */
    public static Date getContractAddlLicenseEndDate(Record record) {
        return record.getDateValue(AgentFields.CONTRACT_ADDL_LICENSE_END_DATE);
    }

    /**
     * get Contract NIPN Start Date
     *
     * @param record
     * @return
     */
    public static Date getContractNipnStartDate(Record record) {
        return record.getDateValue(AgentFields.CONTRACT_NIPN_START_DATE);
    }

    /**
     * get Contract NIPN End Date
     *
     * @param record
     * @return
     */
    public static Date getContractNipnEndDate(Record record) {
        return record.getDateValue(AgentFields.CONTRACT_NIPN_END_DATE);
    }

    /**
     * get licenseClassCode
     * @param record
     * @return
     */
    public static String getLicenseClassCode(Record record) {
        return record.getStringValue(AgentFields.CONTRACT_CLASS_CODE);
    }

    /**
     * get AgentLicenseCommissionId
     * @param record
     * @return
     */
    public static String getAgentLicenseCommissionId(Record record) {
        return record.getStringValue(AgentFields.CONTRACT_COMMISSION_ID);
    }

    /**
     * get policyTypeCode
     * @param record
     * @return
     */
    public static String getPolicyTypeCode(Record record) {
        return record.getStringValue(AgentFields.CONTRACT_COMMISSION_POLICY_TYPE_CODE);
    }

    public static void setPolicyTypeCode(Record record, String code) {
        record.setFieldValue(CONTRACT_COMMISSION_POLICY_TYPE_CODE, code);
    }

    /**
     * get contract commission Start Date
     * @param record
     * @return
     */
    public static Date getContractCommissionStartDate(Record record){
        return record.getDateValue(AgentFields.CONTRACT_COMMISSION_EFFECTIVE_START_DATE);
    }
    /**
     * set contract commission Start Date
     * @param record
     * @param contractCommissionStartDate
     */
    public static void setContractCommissionStartDate(Record record, String contractCommissionStartDate) {
        record.setFieldValue(AgentFields.CONTRACT_COMMISSION_EFFECTIVE_START_DATE, contractCommissionStartDate);
    }

    /**
     * get Contract commission End Date
     * @param record
     * @return
     */
    public static Date getContractCommissionEndDate(Record record){
        return record.getDateValue(AgentFields.CONTRACT_COMMISSION_EFFECTIVE_END_DATE);
    }
    /**
     * get Contract commission End Date string
     * @param record
     * @return
     */
    public static String getContractCommissionEndDateString(Record record){
        return record.getStringValue(AgentFields.CONTRACT_COMMISSION_EFFECTIVE_END_DATE);
    }

    /**
     * set Contract commission End Date
     * @param record
     * @param contractCommissionEndDate
     */
    public static void setContractCommissionEndDate(Record record, String contractCommissionEndDate) {
        record.setFieldValue(AgentFields.CONTRACT_COMMISSION_EFFECTIVE_END_DATE, contractCommissionEndDate);
    }

    /**
     * get newbusCommBasis
     * @param record
     * @return
     */
    public static String getNewbusCommBasis(Record record) {
        return record.getStringValue(AgentFields.NB_COMM_BASIS);
    }
    /**
     * get newbusRate
     * @param record
     * @return
     */
    public static String getNewbusRate(Record record) {
        return record.getStringValue(AgentFields.NB_COMM_RATE);
    }
    /**
     * get newbusCommLimit
     * @param record
     * @return
     */
    public static String getNewbusCommLimit(Record record) {
        return record.getStringValue(AgentFields.NB_COMM_LIMIT);
    }

    /**
     * get newbusFlatAmt
     * @param record
     * @return
     */
    public static String getNewbusFlatAmt(Record record) {
        return record.getStringValue(AgentFields.NB_COMM_FLAT_AMOUNT);
    }

    /**
     * get renewalCommBasis
     * @param record
     * @return
     */
    public static String getRenewalCommBasis(Record record) {
        return record.getStringValue(AgentFields.RN_COMM_BASIS);
    }
    /**
     * get renewalRate
     * @param record
     * @return
     */
    public static String getRenewalRate(Record record) {
        return record.getStringValue(AgentFields.RN_COMM_RATE);
    }
    /**
     * get renewalCommLimit
     * @param record
     * @return
     */
    public static String getRenewalCommLimit(Record record) {
        return record.getStringValue(AgentFields.RN_COMM_LIMIT);
    }

    /**
     * get renewalFlatAmt
     * @param record
     * @return
     */
    public static String getRenewalFlatAmt(Record record) {
        return record.getStringValue(AgentFields.RN_COMM_FLAT_AMOUNT);
    }

    /**
     * get ereCommBasis
     * @param record
     * @return
     */
    public static String getEreCommBasis(Record record) {
        return record.getStringValue(AgentFields.ERE_COMM_BASIS);
    }
    /**
     * get ereRate
     * @param record
     * @return
     */
    public static String getEreRate(Record record) {
        return record.getStringValue(AgentFields.ERE_COMM_RATE);
    }

    /**
     * get ereCommLimit
     * @param record
     * @return
     */
    public static String getEreCommLimit(Record record) {
        return record.getStringValue(AgentFields.ERE_COMM_LIMIT);
    }


    /**
     * get ereFlatAmt
     * @param record
     * @return
     */
    public static String getEreFlatAmt(Record record) {
        return record.getStringValue(AgentFields.ERE_COMM_FLAT_AMOUNT);
    }

    public static String getEntityId(Record record) {
        return record.getStringValue(AgentFields.ENTITY_ID, "");
    }

    public static void setEntityId(Record record, String entityId) {
        record.setFieldValue(AgentFields.ENTITY_ID, entityId);
    }

    public static String getPolicyId(Record record) {
        return record.getStringValue(POLICY_ID);
    }

    public static String getCopyTypeCode(Record record) {
        return record.getStringValue(COPY_TYPE_CODE);
    }

    public static void setCopyTypeCode(Record record, String code){
        record.setFieldValue(COPY_TYPE_CODE, code);
    }

    public static String getFormBucketCode(Record record) {
        return record.getStringValue(FORM_BUCKET_CODE);
    }

    public static void setFormBucketCode(Record record, String code){
        record.setFieldValue(FORM_BUCKET_CODE, code);
    }

    public static String getIssueCompanyEntityId(Record record) {
        return record.getStringValue(ISSUE_COMPANY_ENTITY_ID);
    }

    public static void setIssueCompanyEntityId(Record record, String id) {
        record.setFieldValue(ISSUE_COMPANY_ENTITY_ID, id);
    }

    public static Date getEffectiveToDate(Record record) {
        return record.getDateValue(EFFECTIVE_TO_DATE);
    }

    public static void setEffectiveToDate(Record record, Date date) {
        record.setFieldValue(EFFECTIVE_TO_DATE, date);
    }

    public static Date getEffectiveFromDate(Record record) {
        return record.getDateValue(EFFECTIVE_FROM_DATE);
    }

    public static void setEffectiveFromDate(Record record, Date date) {
        record.setFieldValue(EFFECTIVE_FROM_DATE, date);
    }

    public static String getAction(Record record) {
        return record.getStringValue(ACTION);
    }

    public static void setAction(Record record, String action){
        record.setFieldValue(ACTION, action);
    }

    public static String getAgentOutputOptionId(Record record) {
        return record.getStringValue(AGENT_OUTPUT_OPTION_ID);
    }

    public static String getAddressId(Record record) {
        return record.getStringValue(ADDRESS_ID);
    }

    public static void setAddressId(Record record, String addressId) {
        record.setFieldValue(ADDRESS_ID, addressId);
    }

    public static String getAddressOptionCode(Record record) {
        return record.getStringValue(ADDRESS_OPTION_CODE);
    }

    public static void setAddressOptionCode(Record record, String code) {
        record.setFieldValue(ADDRESS_OPTION_CODE, code);
    }

    public static String getIssueCompanyEntityFkList(Record record) {
        return record.getStringValue(CONTRACT_ISSUE_COMPANY_ENTITY_FK_LIST);
    }

    public static void setIssueCompanyEntityFkList(Record record, String issueCompanyEntityFkList) {
        record.setFieldValue(CONTRACT_ISSUE_COMPANY_ENTITY_FK_LIST, issueCompanyEntityFkList);
    }


    public static String getContractStateCode(Record record) {
        return record.getStringValue(CONTRACT_STATE_CODE);
    }

    public static void setContractStateCode(Record record, String contractStateCode) {
        record.setFieldValue(CONTRACT_STATE_CODE, contractStateCode);
    }

    public static String getContractNumber(Record record) {
        return record.getStringValue(CONTRACT_NUMBER);
    }

    public static void setContractNumber(Record record, String contractNumber) {
        record.setFieldValue(CONTRACT_NUMBER, contractNumber);
    }

    public static String getContractType(Record record) {
        return record.getStringValue(CONTRACT_TYPE);
    }

    public static void setContractType(Record record, String contractType) {
        record.setFieldValue(CONTRACT_TYPE, contractType);
    }

    public static Date getStaffEffStartDate(Record record) {
        return record.getDateValue(STAFF_EFF_START_DATE);
    }

    public static Date getStaffEffEndDate(Record record) {
        return record.getDateValue(STAFF_EFF_END_DATE);
    }

    public static Date getOverrideEffStartDate(Record record) {
        return record.getDateValue(OVERRIDE_EFF_START_DATE);
    }

    public static Date getOverrideEffEndDate(Record record) {
        return record.getDateValue(OVERRIDE_EFF_END_DATE);
    }

    public static String getBranchOfficeAddressId(Record record) {
        return record.getStringValue(BRANCH_OFFICE_ADDRESS_ID);
    }

    public static String getAgentStaffId(Record record) {
        return record.getStringValue(AGENT_STAFF_ID);
    }

    public static String getAgentStaffOverrideId(Record record) {
        return record.getStringValue(AGENT_STAFF_OVERRIDE_ID);
    }

    public static String getOverrideNBRate(Record record) {
        return record.getStringValue(OVERRIDE_NB_COMM_RATE);
    }

    public static String getOverrideRNRate(Record record) {
        return record.getStringValue(OVERRIDE_RN_COMM_RATE);
    }

    public static String getOverrideERERate(Record record) {
        return record.getStringValue(OVERRIDE_ERE_COMM_RATE);
    }

}
