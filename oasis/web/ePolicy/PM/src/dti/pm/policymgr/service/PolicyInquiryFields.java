package dti.pm.policymgr.service;

/**
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * User: fcbibire
 * Date: Feb 22, 2012
 */
/*
 *
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 02/12/2013       fcb         141942: added TERM_WRITTEN_PREMIUM.
 * 07/18/2015       fcb         165221: added fields for Insured (Underwriter).
 * 01/11/2016       eyin        168589: added new fields to make their name match with the output fields' name returned
 *                                      by procedure Pm_Sel_Policy_Info.
 *                                      as fields POLICY_TYPE_CODE/POLICY_CYCLE_CODE/ISSUE_COMPANY_ENTITY_FK and
 *                                      ISSUE_STATE_CODE are also used for creating policy, we can't change their value.
 * 01/21/2016       eyin        166395: added new fields 'ORIGINAL_POLICY_NO' and 'ORIGINAL_POLICY_CYCLE_CODE'.
 * 03/04/2016       wdang       169005: added new fields.
 * 08/26/2016       tzeng       179151: added new field UW_TYPE_CODE.
 * 01/19/2017       wrong       166929: added new field IGNORE_SOFT_VALIDATION_ACTION_CODE.
 * 04/12/2017       tzeng       166929: added new field POL_NUMBER_ID.
 * 02/06/2017       lzhang      190834: added new field TRANSACTION_STATUS_CODE/POLICY_NOS/
 *                                      TERM_BASE_RECORD_IDS/INVALID_POLICY_NOS/INVALID_TERM_BASE_RECORD_IDS
 * 11/23/2018       wrong       195308: added new field PRODUCER_LICENSE_CLASS/SUBPRODUCER_LIC_TYPE_CODE/
 *                                      COUNTERSIGNER_AGENT_LIC_ID/AUTHORIZEDREP_AGENT_LIC_ID/ERE.
 * ---------------------------------------------------
 */

public class PolicyInquiryFields {
    public static final String POLICY_VIEW_MODE = "policyViewMode";
    public static final String POLICY_NUMBER_ID = "polId";
    public static final String POL_NUMBER_ID = "policyNumberId";
    public static final String POL_ID = "polId";
    public static final String POLICY_ID = "policyId";
    public static final String ORIGINAL_POLICY_NO = "originalPolicyNo";
    public static final String POLICY_NO = "policyNo";
    public static final String POLICY_NO_ID = "policyNoEdit";
    public static final String TERM_BASE_RECORD_ID = "termBaseRecordId";
    public static final String TERM_BASE_ID = "termBaseId";
    public static final String POLICY_TERM_NUMBER_ID = "PolicyTermNumberId";
    public static final String POLICY_TERM_HISTORY_ID = "policyTermHistoryId";
    public static final String FULL_NAME = "FullName";
    public static final String TERM_EFF_FROM_DATE = "termEffFromDate";
    public static final String EFF_FROM_DATE = "effFromDate";
    public static final String TERM_EFFECTIVE_FROM_DATE = "termEffectiveFromDate";
    public static final String TERM_EFF_TO_DATE = "termEffToDate";
    public static final String TERM_EXP_DATE = "termExpDate";
    public static final String EXP_DATE = "expDate";
    public static final String TERM_EFFECTIVE_TO_DATE = "termEffectiveToDate";
    public static final String SHORT_TERM_B = "shortTermB";
    public static final String SHORT_RATE_B = "shortRateB";
    public static final String LAST_TRANSACTION_ID = "lastTransactionId";
    public static final String TRANSACTION_LOG_ID = "transactionLogId";
    public static final String TRANSACTION_CODE = "transactionCode";
    public static final String TRANS_CODE = "transCode";
    public static final String NON_RENEWAL_REASON_CODE = "nonRenewalReasonCode";
    public static final String NON_REN_RSN = "nonRenRsn";
    public static final String RENEWAL_INDICATOR_CODE = "renewalIndicatorCode";
    public static final String REN_INDICATOR = "renIndicator";
    public static final String POLICY_HOLDER_NAME_ENTITY_ID = "policyHolderNameEntityId";
    public static final String POLICY_HOLDER_ENTITY_ID = "entityId";
    public static final String POLICY_HOLDER_NAME = "polHolderName";
    public static final String LEGACY_POLICY_NO = "legacyPolicyNo";
    public static final String INSURER_ENTITY_ID = "entityId";
    public static final String INSURER_NUMBER_ID = "entityRoleId";
    public static final String PRODUCER_ENTITY_ID = "entityId";
    public static final String POLICY_TYPE_CODE = "policyTypeCode";
    public static final String POL_TYPE_CODE = "polTypeCode";
    public static final String POLICY_TYPE = "policyType";
    public static final String POLICY_FORM_CODE = "policyPolicyFormCode";
    public static final String POLICY_STATUS = "policyStatus";
    public static final String STATUS_CODE = "statusCode";
    public static final String POLICY_CYCLE_CODE = "policyCycleCode";
    public static final String ORIGINAL_POLICY_CYCLE_CODE = "originalPolicyCycleCode";
    public static final String POLICY_CYCLE = "policyCycle";
    public static final String POLICY_LAYER_CODE = "policyLayerCode";
    public static final String GUARANTEE_DATE = "guaranteeDate";
    public static final String DECLINATION_DATE = "declinationDate";
    public static final String ROLLING_IBNR_DATE = "rollingIbnrDate";
    public static final String ISSUE_COMPANY_ENTITY_FK = "issueCompanyEntityId";
    public static final String QUOTE_CYCLE_CODE = "quoteCycleCode";
    public static final String QUOTE_CYCLE = "quoteCycle";
    public static final String ISS_COMPANY_ENTITY_FK = "issCompanyEntityId";
    public static final String ISSUE_COMPANY_FK = "issueCompanyId";
    public static final String ISSUE_STATE_CODE = "issueStateCode";
    public static final String ISS_STATE_CODE = "issStateCode";
    public static final String PROCESS_LOCATION_CODE = "processLocationCode";
    public static final String PROCESS_LOC = "processLoc";
    public static final String ORGANIZATION_TYPE_CODE = "organizationTypeCode";
    public static final String ORG_TYPE_CODE = "orgTypeCode";
    public static final String BINDER_B = "binderB";
    public static final String COLLATERAL_B = "collateralB";
    public static final String PROGRAM_CODE = "programCode";
    public static final String CATEGORY_CODE = "categoryCode";
    public static final String HOSPITAL_TIER = "hospTier";
    public static final String CM_YEAR = "cmYear";
    public static final String PEER_GROUP_CODE = "peerGroupsCode";
    public static final String FIRST_POTENTIAL_CANC = "potentialCancel1";
    public static final String SECOND_POTENTIAL_CANC = "potentialCancel2";
    public static final String PAYMENT_PLAN = "paymentPlanLst";
    public static final String ACCOUNT_NO = "accountNo";
    public static final String ACCOUNT_IDENTIFIER = "accountIdentifier";
    public static final String PL_AGGREGATE = "plAggregate";
    public static final String GL_AGGREGATE = "glAggregate";
    public static final String DATE1 = "date1";
    public static final String DATE2 = "date2";
    public static final String DATE3 = "date3";
    public static final String NUM1 = "num1";
    public static final String NUM2 = "num2";
    public static final String NUM3 = "num3";
    public static final String CHAR1 = "char1";
    public static final String CHAR2 = "char2";
    public static final String CHAR3 = "char3";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String ACCOUNTING_DATE = "accountingDate";
    public static final String TERM_TYPE_CODE = "termTypeCode";
    public static final String USER_TRANSACTION_CODE = "userTransactionCode";
    public static final String REGIONAL_OFFICE_IS_VISIBLE = "regionalOfficeIsVisible";
    public static final String REGIONAL_OFFICE = "regionalOffice";
    public static final String REQUEST_CONTEXT = "requestContext";
    public static final String IS_TERM_EFFECTIVE_TO_DATE_CHANGED = "isTermEffectiveToDateChanged";
    public static final String TRANSACTION_EFFECTIVE_DATE = "transactionEffectiveDate";
    public static final String ROW_STATUS = "rowStatus";
    public static final String RECORD_MODE_CODE = "recordModeCode";
    public static final String NEW_SAVE_OPTION = "newSaveOption";
    public static final String WIP = "WIP";
    public static final String TEMP = "TEMP";
    public static final String OFFICIAL = "OFFICIAL";
    public static final String TRANSACTION_COMMENT = "transactionComment";
    public static final String PROCESS = "process";
    public static final String OFFICIAL_RECORD_ID = "officialRecordId";
    public static final String POLICY_SCREEN_MODE = "policyScreenMode";
    public static final String AFTER_IMAGE_RECORD_B = "afterImageRecordB";
    public static final String TERM_WRITTEN_PREMIUM = "termWrittenPremium";
    public static final String PARTY_NUMBER_ID = "partyNumberId";
    public static final String RATE_ACTION_CODE = "rate";
    public static final String ISSUE_ACTION_CODE = "issue";
    public static final String IGNORE_SOFT_VALIDATION_ACTION_CODE = "ignoreSoftValidations";
    public static final String DELETE_WIP_ON_FAILURE = "deleteWipOnFailure";

    public static final String BILLING_ACC_HOLDER_IS_POL_HOLDER = "acctHolderIsPolHolderB";
    public static final String BILLING_ENABLE_MORE_OPTION = "enableMoreOptionB";
    public static final String BILLING_BASE_BILL_MONTH_DAY = "baseBillMonthDay";
    public static final String BILLING_FREQUENCY = "billingFrequency";
    public static final String BILLING_SHOW_MORE_FLAG = "showMoreFlag";
    public static final String BILLING_PAYMENT_PLANID = "paymentPlanId";
    public static final String ISSUE_POLICY_RELATED_ERROR = "relatedErrorMsg";
    public static final String ISSUE_POLICY_RELATED_POLICY_NO = "relatedPolicyNo";
    public static final String PRODUCER = "PRODUCER";
    public static final String PRODUCER_AGENT_LIC_ID = "producerAgentLicId";
    public static final String PRODUCER_LICENSE_CLASS = "producerLicenseClass";
    public static final String SUBPRODUCER_LIC_TYPE_CODE = "subproducerLicTypeCode";
    public static final String COUNTERSIGNER_AGENT_LIC_ID = "countersignerAgentLicId";
    public static final String AUTHORIZEDREP_AGENT_LIC_ID = "authorizedrepAgentLicId";
    public static final String AGENT_LICENSE_ID = "agentLicenseId";
    public static final String LICENSE_CLASS_CODE = "licenseClassCode";
    public static final String SUB_PROD = "SUB_PROD";
    public static final String SUB_PROD_AGENT_LIC_ID = "subproducerAgentLicId";
    public static final String COUNT_SIGN = "COUNT_SIGN";
    public static final String COUNT_SIGN_AGENT_LIC_ID = "countersignerAgentLicId";
    public static final String AUTH_REP = "AUTH_REP";
    public static final String AUTH_REP_AGENT_LIC_ID = "authorizedrepAgentLicId";
    public static final String IS_COMM_PAY_CODE_AVAILABLE = "isCommPayCodeAvailable";
    public static final String NEWBUS = "NEWBUS";
    public static final String RENEWAL = "RENEWAL";
    public static final String TAIL = "TAIL";
    public static final String NEW_BUS_COMM_BASIS = "newbusCommBasis";
    public static final String NEW_BUS_COMM_FLAG_AMOUNT = "newbusCommFlatAmount";
    public static final String NEW_BUS_COMM_PAYCODE = "newbusCommPayCode";
    public static final String NEW_BUS_COMM_RATE = "newbusCommRate";
    public static final String NEW_BUS_COMM_LIMIT = "newbusCommLimit";
    public static final String NEW_BUS_COMM_RATE_SCHED_ID = "newbusCommRateScheduleId";
    public static final String RENEWAL_COMM_BASIS = "renewalCommBasis";
    public static final String RENEWAL_COMM_FLAG_AMOUNT = "renewalCommFlatAmount";
    public static final String RENEWAL_COMM_PAY_CODE = "renewalCommPayCode";
    public static final String RENEWAL_COMM_RATE = "renewalCommRate";
    public static final String RENEWAL_COMM_LIMIT = "renewalCommLimit";
    public static final String RENEWAL_BUS_COMM_RATE_SCHED_ID = "renewalCommRateScheduleId";
    public static final String ERE = "ERE";
    public static final String ERE_COMM_BASIS = "ereCommBasis";
    public static final String ERE_COMM_FLAT_AMOUNT = "ereCommFlatAmount";
    public static final String ERE_COMM_PAY_CODE = "ereCommPayCode";
    public static final String ERE_COMM_RATE = "ereCommRate";
    public static final String ERE_COMM_LIMIT = "ereCommLimit";
    public static final String ERE_BUS_COMM_RATE_SCHED_ID = "ereCommRateScheduleId";
    public static final String POLICY_AGENT_ID = "policyAgentId";
    public static final String AUTHORIZATION_CODE = "authorizationCode";
    public static final String CHANGE_TYPE = "changeType";
    public static final String EFFECTIVE_FROM_DATE = "effectiveFromDate";
    public static final String EFFECTIVE_TO_DATE = "effectiveToDate";
    public static final String RENEWAL_B = "renewalB";
    public static final String ENTITY_ROLE_ID = "entityRoleId";
    public static final String ADDL_POLICY_INFO_CHANGED_B = "addlPolicyInfoChangedB";
    public static final String UNDERWRITER_ENTITY_ID = "underwriterEntityId";
    public static final String BILLING_ACCOUNT_ID = "billingAccountId";
    public static final String BILLING_ACCOUNT_PREFIX = "billingAccountPrefix";

    public static final String POLICY_PHASE_CODE = "policyPhaseCode";
    public static final String BINDER_EFFECTIVE_TO_DATE = "binderEffectiveToDate";
    public static final String FORM_OF_BUSINESS = "formOfBusiness";
    public static final String RATING_METHOD = "ratingMethod";
    public static final String RATING_DEVIATION = "ratingDeviation";
    public static final String NUMBER_OF_PHYSICIANS = "numberOfPhysicians";
    public static final String NUMBER_OF_EMPLOYEES = "numberOfEmployees";
    public static final String DISCOVERY_PERIOD_RATING = "discoveryPeriodRating";
    public static final String COMPANY_INSURED_B = "companyInsuredB";
    public static final String POLICY_RETRO_DATE = "policyRetroDate";
    public static final String UW_TYPE_CODE = "uwTypeCode";
    public static final String TRANSACTION_STATUS_CODE = "transactionStatusCode";
    public static final String POLICY_NOS = "policyNos";
    public static final String TERM_BASE_RECORD_IDS = "termBaseRecordIds";
    public static final String INVALID_POLICY_NOS = "invalidPolicyNos";
    public static final String INVALID_TERM_BASE_RECORD_IDS = "invalidTermBaseRecordIds";
}
