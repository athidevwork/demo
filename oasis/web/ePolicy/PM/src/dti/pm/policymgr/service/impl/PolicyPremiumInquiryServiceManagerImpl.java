package dti.pm.policymgr.service.impl;


import com.delphi_tech.ows.party.AddressType;
import com.delphi_tech.ows.party.OrganizationType;
import com.delphi_tech.ows.party.PersonType;
import com.delphi_tech.ows.party.PropertyType;
import com.delphi_tech.ows.partyinquiryservice.PartyInquiryRequestParametersType;
import com.delphi_tech.ows.partyinquiryservice.PartyInquiryRequestType;
import com.delphi_tech.ows.partyinquiryservice.PartyInquiryResultType;
import com.delphi_tech.ows.partyinquiryservice.PartyInquiryType;
import com.delphi_tech.ows.policy.CreditSurchargeDeductibleCodeType;
import com.delphi_tech.ows.policy.CreditSurchargeDeductibleType;
import com.delphi_tech.ows.policy.InsuredType;
import com.delphi_tech.ows.policy.MedicalMalpracticeCoverageCodeType;
import com.delphi_tech.ows.policy.MedicalMalpracticeCoverageType;
import com.delphi_tech.ows.policy.MedicalMalpracticeLineOfBusinessType;
import com.delphi_tech.ows.policy.MedicalMalpracticePolicyType;
import com.delphi_tech.ows.policy.ReferredInsuredType;
import com.delphi_tech.ows.policy.ReferredMedicalMalpracticeCoverageType;
import com.delphi_tech.ows.policy.TransactionCodeType;
import com.delphi_tech.ows.policy.TransactionDetailType;
import com.delphi_tech.ows.policy.TransactionStatusCodeType;
import com.delphi_tech.ows.policypremiuminquiryservice.FilterType;
import com.delphi_tech.ows.policypremiuminquiryservice.PolicyPremiumInquiryRequestParametersType;
import com.delphi_tech.ows.policypremiuminquiryservice.PolicyPremiumInquiryRequestType;
import com.delphi_tech.ows.policypremiuminquiryservice.PolicyPremiumInquiryResultParametersType;
import com.delphi_tech.ows.policypremiuminquiryservice.PolicyPremiumInquiryResultType;
import dti.ci.entitymgr.service.PartyInquiryServiceManager;
import dti.oasis.accesstrailmgr.OwsAccessTrailManager;
import dti.oasis.accesstrailmgr.OwsLogRequest;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordBeanMapper;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.XMLUtils;
import dti.ows.common.MessageStatusAppException;
import dti.ows.common.MessageStatusHelper;
import dti.pm.agentmgr.AgentManager;
import dti.pm.busobjs.PolicyViewMode;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.TransactionStatus;
import dti.pm.core.request.RequestStorageIds;
import dti.pm.entitymgr.EntityManager;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyManager;
import dti.pm.policymgr.Term;
import dti.pm.policymgr.premiummgr.PremiumFields;
import dti.pm.policymgr.premiummgr.PremiumManager;
import dti.pm.policymgr.service.PolicyInquiryFields;
import dti.pm.policymgr.service.PolicyInquiryServiceHelper;
import dti.pm.policymgr.service.PolicyPremiumInquiryServiceManager;
import dti.pm.policymgr.underwritermgr.UnderwriterManager;
import dti.pm.riskmgr.RiskManager;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   01/15/2013
 *
 * @author awu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *  05/06/2014      lxh         154019: Modify getFilterInsuredList to not treat the filter as list.
 *                              Modify loadPremium to have MessageStatus element.
 * 08/29/2014       parker      Issue 138227. Enhancement to add the ows logs.
 * 11/19/2015       eyin        167171 - Modified loadPremium(), remove duplicate validation that recordSet is not null.
 * 03/02/2016       wdang       169197 - Refactor loadPremium() to imitate the retrieval logic with various term of Policy Inquiry WS.
 * 02/06/2017       lzhang      190834 - 1) Modified loadPremium() to pass blank as transactionStatusCode parameter
 *                                       when call getPolicyInquiryServiceHelper().loadPolicyInformation/
 *                                       getPolicyInquiryServiceHelper().buildPolicyHeader and dislay NoMatchingResult msg
 *                                       2) Add getTransactionStatusCode()/requestParametersValidation()/
 *                                       validatePolicyNosExist()/validateTermBaseRecordIdsExist()
 * 04/03/2018       tzeng       192334 - Modified loadUnderwriterInformation to call loadUnderwritersByTermForWS.
 * 04/12/2018       lzhang      191379: Modified loadPremium: Add loadPolicyHeader.
 * 09/17/2018       fcb         195895: Fixed getTransactionStatusCode.
 * ---------------------------------------------------
 */

public class PolicyPremiumInquiryServiceManagerImpl implements PolicyPremiumInquiryServiceManager {

    public final static QName _PolicyPremiumInquiryRequest_QNAME = new QName("http://www.delphi-tech.com/ows/PolicyPremiumInquiryService", "PolicyPremiumInquiryRequest");
    public final static QName _PolicyPremiumInquiryResult_QNAME = new QName("http://www.delphi-tech.com/ows/PolicyPremiumInquiryService", "PolicyPremiumInquiryResult");

    public PolicyPremiumInquiryServiceManagerImpl() {

    }

    public PolicyPremiumInquiryResultType loadPremium(PolicyPremiumInquiryRequestType policyPremiumInquiryRequest) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadPremium", new Object[]{policyPremiumInquiryRequest});
        }

        OwsLogRequest owsLogRequest = null;
        if (l.isLoggable(Level.FINEST)) {
            String xmlResult = XMLUtils.marshalJaxbToXML(policyPremiumInquiryRequest, _PolicyPremiumInquiryRequest_QNAME);
            owsLogRequest = OwsAccessTrailManager.getInstance().addOwsAccessTrailLogger(xmlResult,
                policyPremiumInquiryRequest.getMessageId(), policyPremiumInquiryRequest.getCorrelationId(), policyPremiumInquiryRequest.getUserId(), _PolicyPremiumInquiryRequest_QNAME.getLocalPart());
            l.logp(Level.FINEST, getClass().getName(), "loadPremium", xmlResult);
        } else {
            owsLogRequest = OwsAccessTrailManager.getInstance().addOwsAccessTrailLogger(policyPremiumInquiryRequest, _PolicyPremiumInquiryRequest_QNAME,
                policyPremiumInquiryRequest.getMessageId(), policyPremiumInquiryRequest.getCorrelationId(), policyPremiumInquiryRequest.getUserId());
        }

        PolicyPremiumInquiryResultType policyPremiumInquiryResult = new PolicyPremiumInquiryResultType();
        policyPremiumInquiryResult.setMessageStatus(MessageStatusHelper.getInstance().getSuccessMessageStatus());

        List<String> partyList = new ArrayList<String>();
        List<MedicalMalpracticePolicyType> policyList = new ArrayList<MedicalMalpracticePolicyType>();
        String transactionStatusCode = "";

        List<FilterType> filterList = new ArrayList<FilterType>();
        if (policyPremiumInquiryRequest.getPolicyPremiumInquiryResultParameters() != null) {
            filterList = getFilterInsuredList(policyPremiumInquiryRequest.getPolicyPremiumInquiryResultParameters());
            transactionStatusCode = getTransactionStatusCode(policyPremiumInquiryRequest.getPolicyPremiumInquiryResultParameters()).toUpperCase();
        }

        String viewName = getViewName(policyPremiumInquiryRequest);
        try {
            MessageManager mm = MessageManager.getInstance();
            List<PolicyPremiumInquiryRequestParametersType> requestParameters =
                policyPremiumInquiryRequest.getPolicyPremiumInquiryRequestParameters();
            Iterator requestParameterIte = requestParameters.iterator();
            Record invalidRec = new Record();
            invalidRec = requestParametersValidation(requestParameters);
            String invalidPolNos = invalidRec.getStringValue(PolicyInquiryFields.INVALID_POLICY_NOS,"");
            String invalidPolicyNos = StringUtils.isBlank(invalidPolNos) ? "" : "," + invalidPolNos + ",";
            String invalidTermIds = invalidRec.getStringValue(PolicyInquiryFields.INVALID_TERM_BASE_RECORD_IDS,"");
            String invalidTermBaseRecIds = StringUtils.isBlank(invalidTermIds) ? "" : "," + invalidTermIds + ",";

            while (requestParameterIte.hasNext()) {
                PolicyPremiumInquiryRequestParametersType requestParameter =
                    (PolicyPremiumInquiryRequestParametersType) requestParameterIte.next();

                String policyNo = "";
                String policyTermNumberId = "";
                String transactionId = "";
                String policyNoFormat = "";
                String termBaseRecIdFormat = "";
                if (requestParameter.getPolicyPremiumInquiry() != null) {
                    if (requestParameter.getPolicyPremiumInquiry().getPolicyId() != null) {
                        policyNo = requestParameter.getPolicyPremiumInquiry().getPolicyId().trim();
                        if(!StringUtils.isBlank(policyNo)){
                            policyNoFormat = "," + policyNo + ",";
                        }
                    }
                    if (requestParameter.getPolicyPremiumInquiry().getPolicyTermNumberId() != null) {
                        policyTermNumberId = requestParameter.getPolicyPremiumInquiry().getPolicyTermNumberId().trim();
                        if(!StringUtils.isBlank(policyTermNumberId)){
                            termBaseRecIdFormat = "," + policyTermNumberId + ",";
                        }
                    }
                    if (requestParameter.getPolicyPremiumInquiry().getTransactionDetail() != null) {
                        if (requestParameter.getPolicyPremiumInquiry().getTransactionDetail().getTransactionNumberId() != null) {
                            transactionId = requestParameter.getPolicyPremiumInquiry().getTransactionDetail().getTransactionNumberId().trim();
                        }
                    }
                }

                if (StringUtils.isBlank(policyNo) && StringUtils.isBlank(policyTermNumberId)) {
                    mm.addErrorMessage("ws.policy.premium.inquiry.policy.element.required");
                    throw new AppException("Either Policy number or policy term history ID is required");
                }

                boolean invalidRequestParameter = false;
                if (!StringUtils.isBlank(policyNoFormat)&& invalidPolicyNos.contains(policyNoFormat)
                    || !StringUtils.isBlank(termBaseRecIdFormat) && invalidTermBaseRecIds.contains(termBaseRecIdFormat)) {
                    invalidRequestParameter = true;
                }
                if (!invalidRequestParameter){
                    Record record = new Record();
                    record.setFieldValue(PolicyInquiryFields.POLICY_NO, org.apache.commons.lang3.StringUtils.trimToEmpty(requestParameter.getPolicyPremiumInquiry().getPolicyId()));
                    record.setFieldValue(PolicyInquiryFields.POLICY_TERM_NUMBER_ID, org.apache.commons.lang3.StringUtils.trimToEmpty(requestParameter.getPolicyPremiumInquiry().getPolicyTermNumberId()));
                    List<String[]> termPolicyList = getPolicyInquiryServiceHelper().getTermPolicyList(record);
                    for (String[] pair : termPolicyList) {
                        policyTermNumberId = pair[0];
                        policyNo = pair[1];

                        PolicyHeader policyHeader = getPolicyInquiryServiceHelper().loadPolicyHeader(policyNo, policyTermNumberId, transactionStatusCode);
                        if (policyHeader != null){
                            Record policyRecord = getPolicyInquiryServiceHelper().loadPolicyInformation(policyNo, policyTermNumberId, transactionStatusCode);
                            if (policyRecord.getSize() != 0) {
                                policyHeader = getPolicyInquiryServiceHelper().buildPolicyHeader(policyHeader, policyRecord);
                                // build policy term information list
                                RecordSet termRs = getPolicyInquiryServiceHelper().loadPolicyTermList(policyHeader.getPolicyId());
                                for (Record r : termRs.getRecordList()) {
                                    Term term = new Term();
                                    RecordBeanMapper termRecBeanMapper = new RecordBeanMapper();
                                    termRecBeanMapper.map(r, term);
                                    policyHeader.addPolicyTerm(term);
                                }

                                Record input = new Record();
                                if (StringUtils.isBlank(transactionId)) {
                                    input.setFieldValue("transactionLogId", policyHeader.getLastTransactionId());
                                }
                                else {
                                    input.setFieldValue("transactionLogId", transactionId);
                                }
                                input.setFieldValue("termBaseRecordId", policyHeader.getTermBaseRecordId());

                                if (filterList.size() == 1) {
                                    String filterInsuredId = getFilterInsuredId(filterList.get(0));
                                    if (!StringUtils.isBlank(filterInsuredId)) {
                                        input.setFieldValue("riskBaseRecordId", filterInsuredId);
                                    }
                                }

                                RecordSet rs = getPremiumManager().loadAllPremium(policyHeader, input);
                                MedicalMalpracticePolicyType policy = new MedicalMalpracticePolicyType();
                                setMalpracticePolicy(rs, policyHeader, policy, filterList, partyList, viewName);
                                policyList.add(policy);
                            }
                        }
                    }
                }
            }
            if (partyList != null && partyList.size() > 0) {
                loadParty(policyPremiumInquiryRequest, policyPremiumInquiryResult, partyList);
            }
            policyPremiumInquiryResult.getMedicalMalpracticePolicy().addAll(policyList);

            List<MedicalMalpracticePolicyType> policy = policyPremiumInquiryResult.getMedicalMalpracticePolicy();
            int resultPolCnt = policy.size();
            if (resultPolCnt > 0) {
                owsLogRequest.setSourceTableName("POLICY_TERM_HISTORY");
                owsLogRequest.setSourceRecordFk(policy.get(0).getPolicyTermNumberId());
                owsLogRequest.setSourceRecordNo(policy.get(0).getPolicyId());
            }
            addInfoNoMatchResultMsg(invalidPolNos, invalidTermIds, transactionStatusCode, resultPolCnt);
            String msgCode = policyPremiumInquiryResult.getMessageStatus().getMessageStatusCode();
            if (MessageStatusHelper.STATUS_CODE_SUCCESS.equals(msgCode)){
                policyPremiumInquiryResult.setMessageStatus(MessageStatusHelper.getInstance().getSuccessMessageStatus());
            }
        }
        catch (MessageStatusAppException wsae) {
            policyPremiumInquiryResult.setMessageStatus(wsae.getMessageStatus());
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR, "Failure invoking the PolicyPremiumInquiryResultType", e);
            l.logp(Level.SEVERE, getClass().getName(), "loadPremium", ae.getMessage(), ae);
            policyPremiumInquiryResult.setMessageStatus(MessageStatusHelper.getInstance().getRejectedMessageStatus(ae));
        }

        owsLogRequest.setMessageStatusCode(policyPremiumInquiryResult.getMessageStatus().getMessageStatusCode());
        if (l.isLoggable(Level.FINEST)) {
            String xmlResult = XMLUtils.marshalJaxbToXML(policyPremiumInquiryResult, _PolicyPremiumInquiryResult_QNAME);
            owsLogRequest.setResultXML(xmlResult);
            l.logp(Level.FINEST, getClass().getName(), "loadPremium", xmlResult);
        } else {
            owsLogRequest.setServiceResult(policyPremiumInquiryResult);
            owsLogRequest.setServiceResultQName(_PolicyPremiumInquiryResult_QNAME);
        }
        OwsAccessTrailManager.getInstance().updateOwsAccessTrailLogger(owsLogRequest);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadPremium", policyPremiumInquiryResult);
        }
        return policyPremiumInquiryResult;
    }

    /*
     * Map result to MedicalMalpracticePolicy
    */
    private void setMalpracticePolicy(RecordSet rs, PolicyHeader policyHeader, MedicalMalpracticePolicyType policy, List<FilterType> filterList, List<String> partyList, String viewName) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setMalpracticePolicy", new Object[]{policyHeader.toString(), rs.toString(" || ", " , ")});
        }
        policy.setPolicyTermNumberId(policyHeader.getTermBaseRecordId());
        policy.setPolicyId(policyHeader.getPolicyId());
        policy.setPolicyNumberId(policyHeader.getPolicyNo());

        List<InsuredType> insuredList = new ArrayList<InsuredType>();
        List<MedicalMalpracticeCoverageType> coverageList = new ArrayList<MedicalMalpracticeCoverageType>();
        List<MedicalMalpracticeCoverageType> grossCoverageList = new ArrayList<MedicalMalpracticeCoverageType>();
        List<CreditSurchargeDeductibleType> componentList = new ArrayList<CreditSurchargeDeductibleType>();
        for (Record record : rs.getRecordList()) {
            String riskId = PremiumFields.getRiskId(record);

            if (filterList.size() == 0 || isInsuredFiltered(filterList, riskId)) {
                if (RISK_TOTAL_TITLE.equals(PremiumFields.getComponentCode(record))) {
                    setInsured(record, policyHeader, insuredList);
                }
                else if (COVERAGE_TOTAL_TITLE.equals(PremiumFields.getComponentCode(record))) {
                    setCoverage(record, policyHeader, coverageList);
                }
                else if (GROSS_COVERAGE_CODE.equals(PremiumFields.getComponentCode(record))) {
                    setGrossCoverage(record, grossCoverageList);
                }
                else if (!TRANSACTION_TOTAL_TITLE.equals(PremiumFields.getComponentCode(record))) {
                    setComponent(record, policyHeader, componentList);
                }
            }
            if (TRANSACTION_TOTAL_TITLE.equals(PremiumFields.getComponentCode(record))) {
                if (filterList.size() == 0) {
                    String writtenPremium = PremiumFields.getWrittenPremium(record);
                    policy.setCurrentTermAmount(writtenPremium);
                }
            }
            if (!EXCLUDE_PARTY.equalsIgnoreCase(viewName)) {
                policyHeader = getRiskManager().loadRiskHeader(policyHeader, riskId);
                String entityId = policyHeader.getRiskHeader().getRiskEntityId();
                if (!partyList.contains(entityId)) {
                    partyList.add(entityId);
                }
            }
        }

        if (filterList.size() > 0) {
            policy.setCurrentTermAmount("");
        }
        setPartyList(partyList, policyHeader, viewName);
        policy.getInsured().addAll(insuredList);
        setGrossTermAmount(grossCoverageList, coverageList);
        MedicalMalpracticeLineOfBusinessType lineOfBusinessType = new MedicalMalpracticeLineOfBusinessType();
        lineOfBusinessType.getMedicalMalpracticeCoverage().addAll(coverageList);
        policy.setMedicalMalpracticeLineOfBusiness(lineOfBusinessType);

        policy.getCreditSurchargeDeductible().addAll(componentList);

        //Set transaction
        TransactionDetailType transaction = new TransactionDetailType();
        transaction.setTransactionEffectiveDate(policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        TransactionCodeType transactionCode = new TransactionCodeType();
        transactionCode.setValue(policyHeader.getLastTransactionInfo().getTransactionCode().getName());
        transaction.setTransactionCode(transactionCode);
        transaction.setTransactionNumberId(policyHeader.getLastTransactionId());
        String transactionStatusCode = "";
        TransactionStatus transStatus = policyHeader.getLastTransactionInfo().getTransactionStatusCode();
        if (TransactionStatus.INPROGRESS.equals(transStatus)){
            transactionStatusCode = String.valueOf(RecordMode.WIP);
        }else if(TransactionStatus.COMPLETE.equals(transStatus)){
            transactionStatusCode = String.valueOf(RecordMode.OFFICIAL);
        }else{
            transactionStatusCode = String.valueOf(transStatus);
        }
        transaction.setTransactionStatusCode(new TransactionStatusCodeType());
        transaction.getTransactionStatusCode().setValue(transactionStatusCode);
        policy.setTransactionDetail(transaction);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setMalpracticePolicy", policy);
        }
    }

    /*
     * Map result to Insured
    */
    private void setInsured(Record record, PolicyHeader policyHeader, List<InsuredType> insuredList) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setInsured", new Object[]{policyHeader.toString(), insuredList});
        }

        InsuredType insured = new InsuredType();

        String riskId = PremiumFields.getRiskId(record);
        insured.setInsuredNumberId(riskId);
        insured.setKey(riskId);
        //set risk total premium
        insured.setCurrentTermAmount(PremiumFields.getWrittenPremium(record));
        insuredList.add(insured);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setInsured", insured);
        }
    }

    /*
     * Map result to MedicalMalpracticeCoverage
    */
    private void setCoverage(Record record, PolicyHeader policyHeader, List<MedicalMalpracticeCoverageType> coverageList) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setCoverage", new Object[]{policyHeader.toString(), coverageList});
        }
        MedicalMalpracticeCoverageType coverage = new MedicalMalpracticeCoverageType();

        //Set Referred Insured Information.
        ReferredInsuredType rInsured = new ReferredInsuredType();
        rInsured.setInsuredReference(PremiumFields.getRiskId(record));
        rInsured.setValue(PremiumFields.getRiskId(record));
        coverage.setReferredInsured(rInsured);
        coverage.setKey(PremiumFields.getCoverageId(record));
        coverage.setCoverageNumberId(PremiumFields.getCoverageId(record));
        coverage.setParentCoverageNumberId(PremiumFields.getParentCovgId(record));

        coverage.setCurrentTermAmount(PremiumFields.getWrittenPremium(record));

        //Set Coverage Code
        MedicalMalpracticeCoverageCodeType coverageCode = new MedicalMalpracticeCoverageCodeType();
        coverageCode.setValue(PremiumFields.getProductCoverageCode(record));
        coverageCode.setDescription(record.getStringValue("hiddenCovgCode"));
        coverage.setMedicalMalpracticeCoverageCode(coverageCode);

        coverageList.add(coverage);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setCoverage", coverage);
        }
    }

    /*
     * Set gross premium to a list, and this list will be used in setGrossTermAmount
     */
    private void setGrossCoverage(Record record, List<MedicalMalpracticeCoverageType> grossCoverageList) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setGrossCoverage", new Object[]{record.toString(" , "), grossCoverageList});
        }
        MedicalMalpracticeCoverageType coverage = new MedicalMalpracticeCoverageType();
        coverage.setCurrentGrossTermAmount(PremiumFields.getWrittenPremium(record));
        coverage.setCoverageNumberId(PremiumFields.getCoverageId(record));
        grossCoverageList.add(coverage);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setGrossCoverage", coverage);
        }
    }

    /*
     * Set gross premium to related result object.
     */
    private void setGrossTermAmount(List<MedicalMalpracticeCoverageType> grossCoverageList, List<MedicalMalpracticeCoverageType> coverageList) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setGrossTermAmount", new Object[]{grossCoverageList, coverageList});
        }
        for (MedicalMalpracticeCoverageType coverage : coverageList) {
            for (MedicalMalpracticeCoverageType grossCoverage : grossCoverageList) {
                if (StringUtils.isSame(coverage.getCoverageNumberId(), grossCoverage.getCoverageNumberId())) {
                    coverage.setCurrentGrossTermAmount(grossCoverage.getCurrentGrossTermAmount());
                }
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setGrossTermAmount", coverageList);
        }
    }

    /*
     * Map result to CreditSurchargeDeductible
     */
    private void setComponent(Record record, PolicyHeader policyHeader, List<CreditSurchargeDeductibleType> componentList) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setComponent", new Object[]{policyHeader.toString(), componentList});
        }
        CreditSurchargeDeductibleType component = new CreditSurchargeDeductibleType();

        ReferredMedicalMalpracticeCoverageType coverageType = new ReferredMedicalMalpracticeCoverageType();
        coverageType.setMedicalMalpracticeCoverageReference(PremiumFields.getCoverageId(record));
        coverageType.setValue(PremiumFields.getCoverageId(record));
        component.setReferredMedicalMalpracticeCoverage(coverageType);
        component.setKey(record.getStringValue("coverageComponentCodeKey"));
        component.setCurrentTermAmount(PremiumFields.getWrittenPremium(record));

        CreditSurchargeDeductibleCodeType deductibleCode = new CreditSurchargeDeductibleCodeType();
        deductibleCode.setValue(PremiumFields.getCoverageComponentCode(record));
        deductibleCode.setDescription(record.getStringValue("componentCode"));
        component.setCreditSurchargeDeductibleCode(deductibleCode);

        componentList.add(component);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setComponent", component);
        }
    }

    /*
     * Load policy header
     */
    private PolicyHeader loadPolicyHeader(String policyNo, String termBaseRecordId, PolicyViewMode desiredViewMode) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadPolicyHeader", new Object[]{policyNo, termBaseRecordId, desiredViewMode});
        }

        String requestId = "dti.pm.policymgr.struts.MaintainPolicyAction&process=loadPolicyDetail";
        String endQuoteId = null;

        if (RequestStorageManager.getInstance().has(RequestStorageIds.POLICY_HEADER)) {
            RequestStorageManager.getInstance().remove(RequestStorageIds.POLICY_HEADER);
        }

        PolicyHeader policyHeader = getPolicyManager().loadPolicyHeader(policyNo, termBaseRecordId, desiredViewMode, endQuoteId, requestId, "PolicyPremiumInquirySevice: loadPolicyHeader", false);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadPolicyHeader", policyHeader);
        }

        return policyHeader;
    }

    /*
     * Load Policy Detail.
     */
    private Record loadPolicyInformation(Record inputRecord, PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadPolicyInformation", new Object[]{inputRecord, policyHeader});
        }

        Record output = getPolicyManager().loadPolicyDetail(policyHeader, inputRecord);
        output.setFields(policyHeader.toRecord());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadPolicyInformation", output);
        }

        return output;
    }

    /*
     * Load Under writer
     */
    private RecordSet loadUnderwriterInformation(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadUnderwriterInformation", new Object[]{policyHeader});
        }

        RecordSet underwriterRecordSet = getUnderwriterManager().loadUnderwritersByTermForWS(policyHeader);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadUnderwriterInformation", underwriterRecordSet);
        }

        return underwriterRecordSet;
    }

    /*
     * Load Agent info.
     */
    private RecordSet loadAgentInformation(Record inputRecord, PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAgentInformation", new Object[]{policyHeader});
        }

        RecordSet agentRecordSet = getAgentManager().loadAllPolicyAgent(policyHeader, inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAgentInformation", agentRecordSet);
        }

        return agentRecordSet;
    }

    /*
     * Collect all the entity to party list, and then this list will be used to invoke Party service.
     */
    private void setPartyList(List<String> partyList, PolicyHeader policyHeader, String viewName) {
        if (!EXCLUDE_PARTY.equalsIgnoreCase(viewName)) {
            RecordSet underWriterRS = loadUnderwriterInformation(policyHeader);
            RecordSet agentRecordSet = loadAgentInformation(new Record(), policyHeader);
            Record policyRec = loadPolicyInformation(new Record(), policyHeader);

            Iterator underWriterIte = underWriterRS.getRecords();
            String partyNumberId = "";
            while (underWriterIte.hasNext()) {
                Record record = (Record) underWriterIte.next();
                partyNumberId = record.getStringValue("entityId", "");
                if (!partyList.contains(partyNumberId)) {
                    partyList.add(partyNumberId);
                }
            }

            Iterator agentRSIte = agentRecordSet.getRecords();
            while (agentRSIte.hasNext()) {
                Record record = (Record) agentRSIte.next();
                partyNumberId = record.getStringValue("entityId", "");
                if (!partyList.contains(partyNumberId)) {
                    partyList.add(partyNumberId);
                }
            }

            String issueCompany = policyRec.getStringValue("issueCompanyEntityId", "");
            if (!partyList.contains(issueCompany)) {
                partyList.add(issueCompany);
            }

            String policyHolderEntityId = policyHeader.getPolicyHolderNameEntityId();
            if (!partyList.contains(policyHolderEntityId)) {
                partyList.add(policyHolderEntityId);
            }
        }
    }

    private String getViewName(PolicyPremiumInquiryRequestType policyPremiumInquiryRequest) {
        String viewName = "";
        if (policyPremiumInquiryRequest.getPolicyPremiumInquiryResultParameters() != null) {
            List<String> viewNameList = policyPremiumInquiryRequest.getPolicyPremiumInquiryResultParameters().getViewName();
            for (String view : viewNameList) {
                if (!StringUtils.isBlank(view)) {
                    viewName = view;
                    return viewName.trim();
                }
            }
        }
        return viewName.trim();
    }

    /*
     * Invoke Party Service to load party information.
     */
    private void loadParty(PolicyPremiumInquiryRequestType policyPremiumInquiryRequest, PolicyPremiumInquiryResultType policyPremiumInquiryResult, List<String> partyList) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadParty", new Object[]{policyPremiumInquiryRequest, policyPremiumInquiryResult, partyList});
        }

        PartyInquiryResultType partyInquiryResult = null;

        PartyInquiryRequestType partyInquiryRequest = new PartyInquiryRequestType();
        partyInquiryRequest.setCorrelationId(policyPremiumInquiryRequest.getCorrelationId());
        partyInquiryRequest.setMessageId(policyPremiumInquiryRequest.getMessageId());
        partyInquiryRequest.setUserId(policyPremiumInquiryRequest.getUserId());

        Iterator it = partyList.iterator();
        while (it.hasNext()) {
            String entityId = (String) it.next();
            com.delphi_tech.ows.partyinquiryservice.PartyType party = new com.delphi_tech.ows.partyinquiryservice.PartyType();
            party.setPartyNumberId(entityId);
            PartyInquiryType partyInquiry = new PartyInquiryType();
            partyInquiry.setParty(party);
            PartyInquiryRequestParametersType partyInquiryRequestParameters = new PartyInquiryRequestParametersType();
            partyInquiryRequestParameters.setPartyInquiry(partyInquiry);
            partyInquiryRequest.getPartyInquiryRequestParameters().add(partyInquiryRequestParameters);
        }

        partyInquiryResult = getPartyInquiryServiceManager().loadParty(partyInquiryRequest);

        if (MessageStatusHelper.getInstance().isRejected(partyInquiryResult.getMessageStatus())) {
            MessageStatusAppException wsae = MessageStatusHelper.getInstance().handleRejectedServiceCall("Failure invoking the PolicyInquiryServiceManagerImpl",
                partyInquiryResult.getMessageStatus());
            l.logp(Level.SEVERE, getClass().getName(), "loadParty", wsae.getMessage(), wsae);
            throw wsae;
        }

        Iterator orgIt = partyInquiryResult.getOrganization().iterator();
        while (orgIt.hasNext()) {
            policyPremiumInquiryResult.getOrganization().add((OrganizationType) orgIt.next());
        }

        Iterator persIt = partyInquiryResult.getPerson().iterator();
        while (persIt.hasNext()) {
            policyPremiumInquiryResult.getPerson().add((PersonType) persIt.next());
        }

        Iterator propIt = partyInquiryResult.getProperty().iterator();
        while (propIt.hasNext()) {
            policyPremiumInquiryResult.getProperty().add((PropertyType) propIt.next());
        }

        Iterator addIt = partyInquiryResult.getAddress().iterator();
        while (addIt.hasNext()) {
            policyPremiumInquiryResult.getAddress().add((AddressType) addIt.next());
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadParty");
        }
    }

    private Boolean isInsuredFiltered(List<FilterType> filterList, String insuredId) {
        Boolean emptyFilter = true;
        for (FilterType filter : filterList) {
            String filterInsuredId = getFilterInsuredId(filter);
            if (!StringUtils.isBlank(filterInsuredId)) {
                if (StringUtils.isSame(filterInsuredId, insuredId)) {
                    return true;
                }
            }
        }
        for (FilterType filter : filterList) {
            String filterInsuredId = getFilterInsuredId(filter);
            if (!StringUtils.isBlank(filterInsuredId)) {
                emptyFilter = false;
            }
        }
        return emptyFilter;
    }

    private String getFilterInsuredId(FilterType filter) {
        String filterInsuredId = null;
        if (filter.getInsuredInquiry() != null) {
            if (filter.getInsuredInquiry().getInsured() != null) {
                filterInsuredId = filter.getInsuredInquiry().getInsured().getInsuredNumberId();
            }
        }
        filterInsuredId = filterInsuredId != null ? filterInsuredId.trim() : null;
        return filterInsuredId;
    }

    /*
     * Add all the filter which has insuredId to filterList
     */
    private List<FilterType> getFilterInsuredList(PolicyPremiumInquiryResultParametersType policyPremiumInquiryResultParametersType) {
        List<FilterType> filterList = new ArrayList<FilterType>();
        FilterType filter = policyPremiumInquiryResultParametersType.getFilter();
        if (!StringUtils.isBlank(getFilterInsuredId(filter))) {
            filterList.add(filter);
        }
        return filterList;
    }

    public String getTransactionStatusCode (PolicyPremiumInquiryResultParametersType policyPremiumInquiryResultParameters) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getTransactionStatusCode", new Object[]{policyPremiumInquiryResultParameters});
        }

        String transactionStatusCode = "";
        if (policyPremiumInquiryResultParameters.getFilter() != null &&
            policyPremiumInquiryResultParameters.getFilter().getTransactionStatusCode() != null) {
            String code = policyPremiumInquiryResultParameters.getFilter().getTransactionStatusCode().trim();
            if (code != null) {
                transactionStatusCode = code;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getTransactionStatusCode", transactionStatusCode);
        }

        return transactionStatusCode;
    }

    public Record requestParametersValidation(List<PolicyPremiumInquiryRequestParametersType> requestParameters){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "requestParametersValidation", new Object[]{requestParameters});
        }

        Record r = new Record();
        String allPolicyNos = "";
        String allTermBaseRecordIds = "";
        String invalidPolicyNos = "";
        String invalidTermBaseRecordIds = "";
        Iterator requestParameterIte = requestParameters.iterator();
        while (requestParameterIte.hasNext()) {
            PolicyPremiumInquiryRequestParametersType requestParam = (PolicyPremiumInquiryRequestParametersType) requestParameterIte.next();
            String policyNo = "";
            String termBaseRecordId = "";
            if (requestParam.getPolicyPremiumInquiry() != null) {
                if (requestParam.getPolicyPremiumInquiry().getPolicyId() != null) {
                    policyNo = requestParam.getPolicyPremiumInquiry().getPolicyId().trim();
                }
                if (requestParam.getPolicyPremiumInquiry().getPolicyTermNumberId() != null) {
                    termBaseRecordId = requestParam.getPolicyPremiumInquiry().getPolicyTermNumberId().trim();
                }

                if (!StringUtils.isBlank(policyNo)) {
                    if (StringUtils.isBlank(allPolicyNos)) {
                        allPolicyNos = policyNo;
                    }
                    else {
                        allPolicyNos = allPolicyNos + "," + policyNo;
                    }
                }
                if (!StringUtils.isBlank(termBaseRecordId)) {
                    if (StringUtils.isBlank(allTermBaseRecordIds)) {
                        allTermBaseRecordIds = termBaseRecordId;
                    }
                    else {
                        allTermBaseRecordIds = allTermBaseRecordIds + "," + termBaseRecordId;
                    }
                }
            }
        }

        if(!StringUtils.isBlank(allPolicyNos)){
            invalidPolicyNos = validatePolicyNosExist(allPolicyNos);
        }

        if(!StringUtils.isBlank(allTermBaseRecordIds)){
            invalidTermBaseRecordIds = validateTermBaseRecordIdsExist(allTermBaseRecordIds);
        }

        r.setFieldValue(PolicyInquiryFields.INVALID_POLICY_NOS, invalidPolicyNos);
        r.setFieldValue(PolicyInquiryFields.INVALID_TERM_BASE_RECORD_IDS, invalidTermBaseRecordIds);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "requestParametersValidation", requestParameters);
        }
        return r;
    }

    public String validatePolicyNosExist(String allPolicyNos){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validatePolicyNosExist", new Object[]{allPolicyNos});
        }
        String invalidPolicyNos = getPolicyInquiryServiceHelper().validatePolicyNosExist(allPolicyNos);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validatePolicyNosExist", allPolicyNos);
        }
        return invalidPolicyNos;
    }

    public String validateTermBaseRecordIdsExist(String termBaseRecordIds){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateTermBaseRecordIdsExist", new Object[]{termBaseRecordIds});
        }
        String invalidTermBaseRecordIds = getPolicyInquiryServiceHelper().validateTermBaseRecordIdsExist(termBaseRecordIds);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateTermBaseRecordIdsExist", termBaseRecordIds);
        }
        return invalidTermBaseRecordIds;
    }

    public void addInfoNoMatchResultMsg(String invalidPolNos, String invalidTermIds, String transactionStatusCode, int resultPolCnt){
        if (!StringUtils.isBlank(invalidPolNos)){
            String polIdInvalid = "ws.policy.premium.inquiry.policy.id.invalid";
            MessageManager.getInstance().addInfoNoMatchResultMessage(polIdInvalid, new String[]{invalidPolNos});
        }

        if (!StringUtils.isBlank(invalidTermIds)){
            String polTermIdInvalid = "ws.policy.premium.inquiry.policy.term.id.invalid";
            MessageManager.getInstance().addInfoNoMatchResultMessage(polTermIdInvalid, new String[]{invalidTermIds});
        }

        if (resultPolCnt == 0 && !StringUtils.isBlank(transactionStatusCode)){
            String transStatusCodeFilterInvalid = "ws.policy.premium.inquiry.transaction.status.code.filter.invalid";
            MessageManager.getInstance().addInfoNoMatchResultMessage(transStatusCodeFilterInvalid);
        }
    }

    public void verifyConfig() {
        if (getPolicyManager() == null)
            throw new ConfigurationException("The required property 'policyManager' is missing.");
        if (getRiskManager() == null)
            throw new ConfigurationException("The required property 'riskManager' is missing.");
        if (getUnderwriterManager() == null)
            throw new ConfigurationException("The required property 'underwriterManager' is missing.");
        if (getAgentManager() == null)
            throw new ConfigurationException("The required property 'agentManager' is missing.");
        if (getPartyInquiryServiceManager() == null)
            throw new ConfigurationException("The required property 'partyInquiryServiceManager' is missing.");
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

    public UnderwriterManager getUnderwriterManager() {
        return m_underwriterManager;
    }

    public void setUnderwriterManager(UnderwriterManager underwriterManager) {
        m_underwriterManager = underwriterManager;
    }

    public AgentManager getAgentManager() {
        return m_agentManager;
    }

    public void setAgentManager(AgentManager agentManager) {
        m_agentManager = agentManager;
    }

    public PartyInquiryServiceManager getPartyInquiryServiceManager() {
        return m_partyInquiryServiceManager;
    }

    public void setPartyInquiryServiceManager(PartyInquiryServiceManager partyInquiryServiceManager) {
        m_partyInquiryServiceManager = partyInquiryServiceManager;
    }

    public PremiumManager getPremiumManager() {
        return m_premiumManager;
    }

    public void setPremiumManager(PremiumManager premiumManager) {
        m_premiumManager = premiumManager;
    }

    public EntityManager getEntityManager() {
        return m_entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        m_entityManager = entityManager;
    }

    public PolicyInquiryServiceHelper getPolicyInquiryServiceHelper() {
        return m_policyInquiryServiceHelper;
    }

    public void setPolicyInquiryServiceHelper(PolicyInquiryServiceHelper policyInquiryServiceHelper) {
        m_policyInquiryServiceHelper = policyInquiryServiceHelper;
    }

    private PolicyManager m_policyManager;
    private RiskManager m_riskManager;
    private UnderwriterManager m_underwriterManager;
    private AgentManager m_agentManager;
    private PremiumManager m_premiumManager;
    private EntityManager m_entityManager;
    private PartyInquiryServiceManager m_partyInquiryServiceManager;
    private PolicyInquiryServiceHelper m_policyInquiryServiceHelper;

    private static final String COVERAGE_TOTAL_TITLE = "Coverage Total";
    private static final String GROSS_COVERAGE_CODE = "Gross";
    private static final String RISK_TOTAL_TITLE = "Risk Total";
    private static final String TRANSACTION_TOTAL_TITLE = "Transaction Total";
    private static final String EXCLUDE_PARTY = "ExcludeParty";
}
