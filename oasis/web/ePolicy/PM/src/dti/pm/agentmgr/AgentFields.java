package dti.pm.agentmgr;

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
 * Apr 18, 2008     James       Issue#81847 CIS -> Agent Page ->Agent Contract Commission
 *                              part: Set the NB (RN, ERE) Comm Basis to be persent, set
 *                              the NB (RN, ERE) to be less than 0 and save. No message
 *                              returned and changes will be saved
 *
 * Feb 01, 2013       skommi    Issue# 111565 Added Accounting From and To dates.
 * 11/25/2014         kxiang    Issue# 158853 Added fields PRODUCER_AGENT_NAME_GH and PRODUCER_AGENT_NAME_HREF to get
 *                              href value in WebWB.
 * ---------------------------------------------------
 */
public class AgentFields {

    public static final String IS_PRODUCER_AGENT_LIC_ID_AVAILABLE = "isProducerAgentLicIdAvailable";
    public static final String IS_ROW_ELIGIBLE_FOR_DELETE = "isRowEligibleForDelete";
    public static final String IS_COMM_PAY_CODE_AVAILABLE = "isCommPayCodeAvailable";
    public static final String IS_ADD_AVAILABLE = "isAddAvailable";
    public static final String IS_CHANGE_AVAILABLE = "isChangeAvailable";
    public static final String IS_AUTHORIZATION_CODE_AVAILABLE = "isAuthorizationCodeAvailable";
    public static final String IS_OUTPUT_AVAILABLE = "isOutputAvailable";

    public static final String AGENT_ID = "AgentId";

    public static final String SITUATION_CODE = "situationCode";
    public static final String EFFECTIVE_START_DATE = "effectiveStartDate";
    public static final String EFFECTIVE_END_DATE = "effectiveEndDate";
    public static final String ACCOUNTING_FROM_DATE = "accountingFromDate";
    public static final String ACCOUNTING_TO_DATE = "accountingToDate";

    public static final String SUSPEND_STATUS = "suspendStatus";

    public static final String PAY_COMMISSION_ID = "AgentPayCommissionId";
    public static final String PAY_COMMISSION_PAY_CODE = "commissionPayCode";
    public static final String PAY_COMMISSION_EFFECTIVE_START_DATE = "effectiveStartDate1";
    public static final String PAY_COMMISSION_EFFECTIVE_END_DATE = "effectiveEndDate1";

    public static final String CONTRACT_ID = "AgentLicenseId";
    public static final String CONTRACT_EFFECTIVE_START_DATE = "effectiveStartDate2";
    public static final String CONTRACT_EFFECTIVE_END_DATE = "effectiveEndDate2";
    public static final String CONTRACT_APPOINTMENT_START_DATE = "appointmentStartDate";
    public static final String CONTRACT_APPOINTMENT_END_DATE = "appointmentEndDate";
    public static final String CONTRACT_CLASS_CODE = "licenseClassCode";
    public static final String CONTRACT_NUMBER = "licenseNumber";
    public static final String CONTRACT_TYPE = "licenseTypeCode";
    public static final String CONTRACT_STATE_CODE = "stateCode";

    public static final String CONTRACT_COMMISSION_ID = "AgentLicenseCommissionId";
    public static final String CONTRACT_COMMISSION_POLICY_TYPE_CODE = "policyTypeCode";
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

    public static final String PRODUCER_AGENT_NAME_GH = "producerAgentName_GH";
    public static final String PRODUCER_AGENT_NAME_HREF = "producerAgentNameHref";

    /**
     * get id
     * @param record
     * @return
     */
    public static String getId(Record record){
        return record.getStringValue(ID);
    }

    /**
     * set id
     * @param record
     * @param id
     */
    public static void setId(Record record, String id) {
        record.setFieldValue(ID, id);
    }

    /**
     * get agent license ID
     * @param record
     * @return
     */
    public static String getAgentLicenseId(Record record){
        return record.getStringValue(CONTRACT_ID);
    }

    /**
     * set agent license ID
     * @param record
     * @param agentLicenseId
     */
    public static void setAgentLicenseId(Record record, String agentLicenseId) {
        record.setFieldValue(CONTRACT_ID, agentLicenseId);
    }
    /**
     * get agent ID
     * @param record
     * @return
     */
    public static String getAgentId(Record record){
        return record.getStringValue(AGENT_ID);
    }

    /**
     * set agent ID
     * @param record
     * @param agentId
     */
    public static void setAgentId(Record record, String agentId) {
        record.setFieldValue(AGENT_ID, agentId);
    }

    /**
     * get passed parameter license number
     * @param record
     * @return
     */
    public static String getPassLicenseNumber(Record record){
        return record.getStringValue(PASS_LICENSE_NUMBER);
    }
    /**
     * get search by
     * @param record
     * @return
     */
    public static String getSearchBy(Record record){
        return record.getStringValue(SEARCH_BY);
    }
    /**
     * set search by
     * @param record
     * @param searchBy
     */
    public static void setSearchBy(Record record, String searchBy) {
        record.setFieldValue(SEARCH_BY, searchBy);
    }
    /**
     * get search string
     * @param record
     * @return
     */
    public static String getSearchString(Record record){
        return record.getStringValue(SEARCH_STRING);
    }
    /**
     * set agent ID
     * @param record
     * @param searchString
     */
    public static void setSearchString(Record record, String searchString) {
        record.setFieldValue(SEARCH_STRING, searchString);
    }

    /**
     * get effective Start Date
     * @param record
     * @return
     */
    public static Date getEffectiveStartDate(Record record){
        return record.getDateValue(EFFECTIVE_START_DATE);
    }
    /**
     * set effective Start Date
     * @param record
     * @param effectiveStartDate
     */
    public static void setEffectiveStartDate(Record record, String effectiveStartDate) {
        record.setFieldValue(EFFECTIVE_START_DATE, effectiveStartDate);
    }

    /**
     * get effective End Date
     * @param record
     * @return
     */
    public static Date getEffectiveEndDate(Record record){
        return record.getDateValue(EFFECTIVE_END_DATE);
    }
    /**
     * get effective End Date string
     * @param record
     * @return
     */
    public static String getEffectiveEndDateString(Record record){
        return record.getStringValue(EFFECTIVE_END_DATE);
    }
    /**
     * set effective End Date
     * @param record
     * @param effectiveEndDate
     */
    public static void setEffectiveEndDate(Record record, String effectiveEndDate) {
        record.setFieldValue(EFFECTIVE_END_DATE, effectiveEndDate);
    }

    /**
     * get accounting From Date
     * @param record
     * @return
     */
    public static String getAccountingFromDate(Record record){
        return record.getStringValue(ACCOUNTING_FROM_DATE);
    }

    /**
     * set accounting From Date
     * @param record
     * @param accountingFromDate
     */
    public static void setAccountingFromDate(Record record, String accountingFromDate) {
        record.setFieldValue(ACCOUNTING_FROM_DATE, accountingFromDate);
    }

    /**
     * get accounting To Date
     * @param record
     * @return
     */
    public static String getAccountingToDate(Record record){
        return record.getStringValue(ACCOUNTING_TO_DATE);
    }

    /**
     * set accounting To Date
     * @param record
     * @param accountingToDate
     */
    public static void setAccountingToDate(Record record, String accountingToDate) {
        record.setFieldValue(ACCOUNTING_TO_DATE, accountingToDate);
    }

    /**
     * get AgentPayCommissionId
     * @param record
     * @return
     */
    public static String getAgentPayCommissionId(Record record){
        return record.getStringValue(PAY_COMMISSION_ID);
    }
    /**
     * get pay commission effective Start Date
     * @param record
     * @return
     */
    public static Date getPayCommissionEffectiveStartDate(Record record){
        return record.getDateValue(PAY_COMMISSION_EFFECTIVE_START_DATE);
    }
    /**
     * set pay commission effective Start Date
     * @param record
     * @param payCommissionEffectiveStartDate
     */
    public static void setPayCommissionEffectiveStartDate(Record record, String payCommissionEffectiveStartDate) {
        record.setFieldValue(PAY_COMMISSION_EFFECTIVE_START_DATE, payCommissionEffectiveStartDate);
    }

    /**
     * get pay commission effective End Date
     * @param record
     * @return
     */
    public static Date getPayCommissionEffectiveEndDate(Record record){
        return record.getDateValue(PAY_COMMISSION_EFFECTIVE_END_DATE);
    }

    /**
     * get pay commission effective End Date string
     * @param record
     * @return
     */
    public static String getPayCommissionEffectiveEndDateString(Record record){
        return record.getStringValue(PAY_COMMISSION_EFFECTIVE_END_DATE);
    }
    /**
     * set pay commission effective End Date
     * @param record
     * @param payCommissionEffectiveEndDate
     */
    public static void setPayCommissionEffectiveEndDate(Record record, String payCommissionEffectiveEndDate) {
        record.setFieldValue(PAY_COMMISSION_EFFECTIVE_END_DATE, payCommissionEffectiveEndDate);
    }


    /**
     * get contract effective Start Date
     * @param record
     * @return
     */
    public static Date getContractEffectiveStartDate(Record record){
        return record.getDateValue(CONTRACT_EFFECTIVE_START_DATE);
    }
    /**
     * set Contract effective Start Date
     * @param record
     * @param contractEffectiveStartDate
     */
    public static void setContractEffectiveStartDate(Record record, String contractEffectiveStartDate) {
        record.setFieldValue(CONTRACT_EFFECTIVE_START_DATE, contractEffectiveStartDate);
    }

    /**
     * get Contract effective End Date
     * @param record
     * @return
     */
    public static Date getContractEffectiveEndDate(Record record){
        return record.getDateValue(CONTRACT_EFFECTIVE_END_DATE);
    }

    /**
     * get Contract effective End Date string
     * @param record
     * @return
     */
    public static String getContractEffectiveEndDateString(Record record){
        return record.getStringValue(CONTRACT_EFFECTIVE_END_DATE);
    }

    /**
     * set Contract effective End Date
     * @param record
     * @param contractEffectiveEndDate
     */
    public static void setContractEffectiveEndDate(Record record, String contractEffectiveEndDate) {
        record.setFieldValue(CONTRACT_EFFECTIVE_END_DATE, contractEffectiveEndDate);
    }



    /**
     * get contract appointment Start Date
     * @param record
     * @return
     */
    public static Date getAppointmentStartDate(Record record){
        return record.getDateValue(CONTRACT_APPOINTMENT_START_DATE);
    }
    /**
     * set appointment Start Date
     * @param record
     * @param appointmentStartDate
     */
    public static void setAppointmentStartDate(Record record, String appointmentStartDate) {
        record.setFieldValue(CONTRACT_APPOINTMENT_START_DATE, appointmentStartDate);
    }

    /**
     * get Contract appointment End Date
     * @param record
     * @return
     */
    public static Date getAppointmentEndDate(Record record){
        return record.getDateValue(CONTRACT_APPOINTMENT_END_DATE);
    }

    /**
     * get Contract appointment End Date string
     * @param record
     * @return
     */
    public static String getAppointmentEndDateString(Record record){
        return record.getStringValue(CONTRACT_APPOINTMENT_END_DATE);
    }

    /**
     * set Contract appointment End Date
     * @param record
     * @param appointmentEndDate
     */
    public static void setAppointmentEndDate(Record record, String appointmentEndDate) {
        record.setFieldValue(CONTRACT_APPOINTMENT_END_DATE, appointmentEndDate);
    }

    /**
     * get licenseClassCode
     * @param record
     * @return
     */
    public static String getLicenseClassCode(Record record) {
        return record.getStringValue(CONTRACT_CLASS_CODE);
    }

    /**
     * get AgentLicenseCommissionId
     * @param record
     * @return
     */
    public static String getAgentLicenseCommissionId(Record record) {
        return record.getStringValue(CONTRACT_COMMISSION_ID);
    }

    /**
     * get policyTypeCode
     * @param record
     * @return
     */
    public static String getPolicyTypeCode(Record record) {
        return record.getStringValue(CONTRACT_COMMISSION_POLICY_TYPE_CODE);
    }

    /**
     * get contract commission Start Date
     * @param record
     * @return
     */
    public static Date getContractCommissionStartDate(Record record){
        return record.getDateValue(CONTRACT_COMMISSION_EFFECTIVE_START_DATE);
    }
    /**
     * set contract commission Start Date
     * @param record
     * @param contractCommissionStartDate
     */
    public static void setContractCommissionStartDate(Record record, String contractCommissionStartDate) {
        record.setFieldValue(CONTRACT_COMMISSION_EFFECTIVE_START_DATE, contractCommissionStartDate);
    }

    /**
     * get Contract commission End Date
     * @param record
     * @return
     */
    public static Date getContractCommissionEndDate(Record record){
        return record.getDateValue(CONTRACT_COMMISSION_EFFECTIVE_END_DATE);
    }
    /**
     * get Contract commission End Date string
     * @param record
     * @return
     */
    public static String getContractCommissionEndDateString(Record record){
        return record.getStringValue(CONTRACT_COMMISSION_EFFECTIVE_END_DATE);
    }

    /**
     * set Contract commission End Date
     * @param record
     * @param contractCommissionEndDate
     */
    public static void setContractCommissionEndDate(Record record, String contractCommissionEndDate) {
        record.setFieldValue(CONTRACT_COMMISSION_EFFECTIVE_END_DATE, contractCommissionEndDate);
    }

    /**
     * get newbusCommBasis
     * @param record
     * @return
     */
    public static String getNewbusCommBasis(Record record) {
        return record.getStringValue(NB_COMM_BASIS);
    }
    /**
     * get newbusRate
     * @param record
     * @return
     */
    public static String getNewbusRate(Record record) {
        return record.getStringValue(NB_COMM_RATE);
    }
    /**
     * get newbusCommLimit
     * @param record
     * @return
     */
    public static String getNewbusCommLimit(Record record) {
        return record.getStringValue(NB_COMM_LIMIT);
    }

    /**
     * get newbusFlatAmt
     * @param record
     * @return
     */
    public static String getNewbusFlatAmt(Record record) {
        return record.getStringValue(NB_COMM_FLAT_AMOUNT);
    }

    /**
     * get renewalCommBasis
     * @param record
     * @return
     */
    public static String getRenewalCommBasis(Record record) {
        return record.getStringValue(RN_COMM_BASIS);
    }
    /**
     * get renewalRate
     * @param record
     * @return
     */
    public static String getRenewalRate(Record record) {
        return record.getStringValue(RN_COMM_RATE);
    }
    /**
     * get renewalCommLimit
     * @param record
     * @return
     */
    public static String getRenewalCommLimit(Record record) {
        return record.getStringValue(RN_COMM_LIMIT);
    }

    /**
     * get renewalFlatAmt
     * @param record
     * @return
     */
    public static String getRenewalFlatAmt(Record record) {
        return record.getStringValue(RN_COMM_FLAT_AMOUNT);
    }

    /**
     * get ereCommBasis
     * @param record
     * @return
     */
    public static String getEreCommBasis(Record record) {
        return record.getStringValue(ERE_COMM_BASIS);
    }
    /**
     * get ereRate
     * @param record
     * @return
     */
    public static String getEreRate(Record record) {
        return record.getStringValue(ERE_COMM_RATE);
    }

    /**
     * get ereCommLimit
     * @param record
     * @return
     */
    public static String getEreCommLimit(Record record) {
        return record.getStringValue(ERE_COMM_LIMIT);
    }


    /**
     * get ereFlatAmt
     * @param record
     * @return
     */
    public static String getEreFlatAmt(Record record) {
        return record.getStringValue(ERE_COMM_FLAT_AMOUNT);
    }

    /**
     * get ProducerAgentNameHref
     * @param record
     * @return
     */
    public static String getProducerAgentNameHref(Record record) {
        return record.getStringValue(PRODUCER_AGENT_NAME_HREF);
    }

    /**
     * set producer agent name href
     * @param record
     * @param producerAgentNameHref
     */
    public static void setProducerAgentNameHref(Record record, String producerAgentNameHref) {
        record.setFieldValue(PRODUCER_AGENT_NAME_HREF, producerAgentNameHref);
    }

}
