package dti.pm.policymgr.service.impl;

import com.delphi_tech.ows.account.LinkedPolicyType;
import com.delphi_tech.ows.account.PaymentOptionType;
import com.delphi_tech.ows.common.CustomStatusCodeType;
import com.delphi_tech.ows.common.ExtendedStatusType;
import com.delphi_tech.ows.common.MessageStatusType;
import com.delphi_tech.ows.party.AddressType;
import com.delphi_tech.ows.party.OrganizationType;
import com.delphi_tech.ows.party.PersonType;
import com.delphi_tech.ows.party.PropertyType;
import com.delphi_tech.ows.partyinquiryservice.PartyInquiryRequestParametersType;
import com.delphi_tech.ows.partyinquiryservice.PartyInquiryRequestType;
import com.delphi_tech.ows.partyinquiryservice.PartyInquiryResultParametersType;
import com.delphi_tech.ows.partyinquiryservice.PartyInquiryResultType;
import com.delphi_tech.ows.partyinquiryservice.PartyInquiryType;
import com.delphi_tech.ows.policy.*;
import com.delphi_tech.ows.account.PrincipalBillingAccountInformationType;
import com.delphi_tech.ows.policyinquiryservice.FilterType;
import com.delphi_tech.ows.policyinquiryservice.InsuredInquiryType;
import com.delphi_tech.ows.policyinquiryservice.PartyNameType;
import com.delphi_tech.ows.policyinquiryservice.PartyType;
import com.delphi_tech.ows.policyinquiryservice.PolicyInquiryRequestParametersType;
import com.delphi_tech.ows.policyinquiryservice.PolicyInquiryRequestType;
import com.delphi_tech.ows.policyinquiryservice.PolicyInquiryResultParametersType;
import com.delphi_tech.ows.policyinquiryservice.PolicyInquiryResultType;
import com.delphi_tech.ows.policypremiuminquiryservice.PolicyPremiumInquiryRequestParametersType;
import dti.ci.entitymgr.service.PartyInquiryServiceManager;
import dti.oasis.accesstrailmgr.OwsAccessTrailManager;
import dti.oasis.accesstrailmgr.OwsLogRequest;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoEmptyFlag;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.concurrent.OasisExecutorServiceManager;
import dti.oasis.concurrent.OasisTaskInfo;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.XMLUtils;
import dti.ows.common.MessageStatusAppException;
import dti.ows.common.MessageStatusHelper;
import dti.pm.agentmgr.AgentManager;
import dti.pm.busobjs.ComponentOwner;
import dti.pm.busobjs.PolicyViewMode;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.TransactionStatus;
import dti.pm.componentmgr.ComponentManager;
import dti.pm.componentmgr.impl.ComponentRowStyleRecordLoadprocessor;
import dti.pm.core.request.RequestStorageIds;
import dti.pm.coverageclassmgr.CoverageClassManager;
import dti.pm.coveragemgr.CoverageManager;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyManager;
import dti.pm.policymgr.service.ComponentInquiryFields;
import dti.pm.policymgr.service.CoverageInquiryFields;
import dti.pm.policymgr.service.PolicyInquiryFields;
import dti.pm.policymgr.service.PolicyInquiryServiceHelper;
import dti.pm.policymgr.service.PolicyInquiryServiceManager;
import dti.pm.policymgr.service.RiskInquiryFields;
import dti.pm.policymgr.underwritermgr.UnderwriterManager;
import dti.pm.riskmgr.RiskManager;
import dti.pm.riskmgr.addtlexposuremgr.RiskAddtlExposureManager;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   2/24/12
 *
 * @author fcbibire
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 02/12/2013       fcb         141942: set CurrentTermAmount.
 * 05/06/2014       lxh         154019: To make use of PartyNumberID in partyType.
 *                                      Remove partyNumberID from PartyNameType.
 * 08/14/2014       fcb         154159: Bug fixed.
 * 08/29/2014       parker      Issue 138227. Enhancement to add the ows logs.
 * 10/30/2014       awu         145137: Import PrincipalBillingAccountInformationType from account package.
 * 01/16/2015       awu         160475: Modified setMedicalMalpracticeCoverageVersion to set the correct status code.
 * 07/18/2015       fcb         165221: Logic for Insured (Underwriter) added.
 * 29/10/2015       lzhang      166995: Modify getPolicyHolder method:
 *                                      set PolicyInquiryFields.POLICY_HOLDER_ENTITY_ID to PartyNumberId element.
 * 01/11/2016       eyin        168589: 1)Remove the logic where convert the value of Renewal Indicator into true/false.
 *                                      2)Set the value for fields policy type code, policy cycle code, issue company
 *                                      entity fk and issue state code with correct name according to the output fields
 *                                      returned by procedure Pm_Sel_Policy_Info.
 * 01/25/2016       eyin        168882: Added loadPolicyBillingAccountInfo method,
 *                                      Modified getPrincipalBillingAccountInformation() to correct the value for
 *                                      billing account information and support to return multiple
 *                                      PrincipalBillingAccountInformation elements in case of multiple billing accounts
 *                                      linked to one policy.
 * 02/03/2016       wdang       169198 - Modified getMedicalMalpracticePolicy() to correct the name of
 *                                       MedicalMalpracticePolicyType.setPrincipalBillingAccountInformation().
 * 03/02/2016       wdang       169197 - Remove loadPolicyInformation and getTermPolicyList, instead, use PolicyInquiryServiceHelper.
 * 03/04/2016       wdang       169005 - Support new created fields of issue 165790,166924,166922.
 * 06/19/2016       cesar       issue #176679 - changed loadPolicy to use OasisCallable.
 * 08/04/2016       fcb         177135 - new field added.
 * 06/19/2016       eyin        178255 - Modified loadPolicy() and loadPolicyMinimalViewSingleThreading(), to pass the
 *                                       value of view name correctly.
 * 08/23/2016      lzhang       178818: Modified getInsurer to add <TypeCode> element
 * 11/07/2016      jyang2       181092: Modified setCoverage to set policyFormCode to coverage.
 * 04/12/2017       tzeng       166929: Modified getInsurer to format its Oasis date to XML date.
 * 06/15/2017       wrong       186163: Modified setMedicalMalpracticeCoverageVersion to set claim process code value
 *                                      to inquiry result.
 * 07/19/2017       wrong       168374: Modified setInsuredVersion to set PCF county code/class code value to inquiry
 *                                      result.
 * 09/21/2017       eyin        169483: Modified to support retrieve risk additional exposure info.
 * 01/29/2017       lzhang      191116: Modified getTransactionDetail:setTransactionEffectiveDate
 * 02/06/2017       lzhang      190834: 1) Modified loadPolicy: set request transactionStatusCode to inputRecord and
 *                                      display NoMatchingResult msg
 *                                      2) Modified loadPolicySingleThreading() to pass blank as transactionStatusCode parameter
 *                                      when call getPolicyInquiryServiceHelper().loadPolicyInformation/
 *                                      getPolicyInquiryServiceHelper().buildPolicyHeader
 *                                      3) Add getTransactionStatusCode()/requestParametersValidation()/
 *                                      validatePolicyNosExist()/validateTermBaseRecordIdsExist()
 * 03/27/2018       wli         192247: Modified getTransactionStatusCode to consider null case.
 * 04/12/2018       lzhang      191379: Modified loadPolicySingleThreading: Add loadPolicyHeader.
 * 04/24/2018       wrong       192347: Modified setComponent to make multi versions belong to the same coverage
 *                                      component according to polCovCompBaseRecId.
 * 04/26/2018       wrong       192351: 1) Added changeCxlWipTermOffExpDate() to change effective_to_date in official
 *                                         record for cancel wip case.
 *                                      2) Modified loadRiskInformation/loadCoverageInformation/loadComponentInformation
 *                                         /loadCoverageClassInformation/loadAllRiskAddtlExposure to invoke
 *                                         changeCxlWipTermOffExpDate().
 * 10/25/2018       tyang        196304 Modified TransactionDetailType:change the format of TransactionEffectiveDate
 *                                      with the standard ACORD format yyyy-MM-dd.
 * 11/23/2018       wrong       195308: Modified getProducer() to add more agent information.
 * 11/28/2018       eyin        197179: Modified to include policy detail section for TransactionStatusCode.
 * ---------------------------------------------------
 */
public class PolicyInquiryServiceManagerImpl implements PolicyInquiryServiceManager {

    public final static QName _PolicyInquiryRequest_QNAME = new QName("http://www.delphi-tech.com/ows/PolicyInquiryService", "PolicyInquiryRequest");
    public final static QName _PolicyInquiryResult_QNAME = new QName("http://www.delphi-tech.com/ows/PolicyInquiryService", "PolicyInquiryResult");

    /**
     * Constructor.
     */
    public PolicyInquiryServiceManagerImpl() {
    }

    public PolicyInquiryResultType loadPolicy (PolicyInquiryRequestType inquiryRequest) {
        Logger l = LogUtils.getLogger(getClass());
        Date startDate = new Date();

        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadPolicy", new Object[]{inquiryRequest});
            startDate = new Date();
        }

        OwsLogRequest owsLogRequest = null;
        if (l.isLoggable(Level.FINEST)) {
            String xmlResult = XMLUtils.marshalJaxbToXML(inquiryRequest, _PolicyInquiryRequest_QNAME);
            owsLogRequest = OwsAccessTrailManager.getInstance().addOwsAccessTrailLogger(xmlResult,
                inquiryRequest.getMessageId(), inquiryRequest.getCorrelationId(), inquiryRequest.getUserId(), _PolicyInquiryRequest_QNAME.getLocalPart());
            l.logp(Level.FINEST, getClass().getName(), "loadPolicy", xmlResult);
        } else {
            owsLogRequest = OwsAccessTrailManager.getInstance().addOwsAccessTrailLogger(inquiryRequest, _PolicyInquiryRequest_QNAME,
                inquiryRequest.getMessageId(), inquiryRequest.getCorrelationId(), inquiryRequest.getUserId());
        }

        PolicyInquiryResultType inquiryResult = new PolicyInquiryResultType();
        inquiryResult.setMessageId(inquiryRequest.getMessageId());
        inquiryResult.setCorrelationId(inquiryRequest.getCorrelationId());

        String viewName = getViewName(inquiryRequest);
        String transactionStatusCode = getTransactionStatusCode(inquiryRequest).toUpperCase();
        MessageStatusType mst = new MessageStatusType();
        mst.setMessageStatusCode(SUCCESS_MESSAGE_STATUS);
        inquiryResult.setMessageStatus(mst);

        List<String> partyList = new ArrayList<>();
        List<PolicyInquiryCallable> tasks =  new ArrayList<>();
        List<PolicyInquiryMinimalViewCallable> tasksMinimalView =  new ArrayList<>();
        List<PolicyInquiryItemResultMT>policyInquiryResultTypesMT = new ArrayList<>();

        List<PolicyInquiryRequestParametersType> policyRequestParameters = inquiryRequest.getPolicyInquiryRequestParameters();
        Iterator it = policyRequestParameters.iterator();
        Map<String, String> policyTermMap = new HashMap<>();

        try {
            Record invalidRec = new Record();
            invalidRec = requestParametersValidation(policyRequestParameters);
            String invalidPolNos = invalidRec.getStringValue(PolicyInquiryFields.INVALID_POLICY_NOS,"");
            String invalidPolicyNos = StringUtils.isBlank(invalidPolNos) ? "" : "," + invalidPolNos + ",";
            String invalidTermIds = invalidRec.getStringValue(PolicyInquiryFields.INVALID_TERM_BASE_RECORD_IDS,"");
            String invalidTermBaseRecIds = StringUtils.isBlank(invalidTermIds) ? "" : "," + invalidTermIds + ",";

            while (it.hasNext()) {
                PolicyInquiryRequestParametersType requestParam = (PolicyInquiryRequestParametersType) it.next();
                String polNo = "";
                String termBaseRecId ="";
                String policyNoFormat = "";
                String termBaseRecIdFormat = "";
                if (requestParam.getPolicyInquiry() != null){
                    if(requestParam.getPolicyInquiry().getPolicyId() != null){
                        polNo = requestParam.getPolicyInquiry().getPolicyId().trim();
                        if(!StringUtils.isBlank(polNo)){
                            policyNoFormat = "," + polNo + ",";
                        }
                    }
                    if(requestParam.getPolicyInquiry().getPolicyTermNumberId() != null){
                        termBaseRecId = requestParam.getPolicyInquiry().getPolicyTermNumberId().trim();
                        if(!StringUtils.isBlank(termBaseRecId)){
                            termBaseRecIdFormat = "," + termBaseRecId + ",";
                        }
                    }
                }
                boolean invalidRequestParameter = false;
                if (!StringUtils.isBlank(policyNoFormat)&& invalidPolicyNos.contains(policyNoFormat)
                    || !StringUtils.isBlank(termBaseRecIdFormat) && invalidTermBaseRecIds.contains(termBaseRecIdFormat)){
                    invalidRequestParameter = true;
                }
                if (!invalidRequestParameter){
                    Record inputRecord = setInputRecord(requestParam);
                    inputRecord.setFieldValue(VIEW_NAME, viewName);
                    inputRecord.setFieldValue(PolicyInquiryFields.TRANSACTION_STATUS_CODE, transactionStatusCode);
                    if (MINIMAL_VIEW_NAME.equalsIgnoreCase(viewName)) {
                        String threadName = "MinimalView";
                        PolicyInquiryMinimalViewCallable policyInquiryMinimalViewCallable = new PolicyInquiryMinimalViewCallable(threadName, inputRecord);
                        tasksMinimalView.add(policyInquiryMinimalViewCallable);
                    } else {
                        //iterate through terms
                        List<String[]> termPolicyList = getPolicyInquiryServiceHelper().getTermPolicyList(inputRecord);
                        Iterator termIt = termPolicyList.iterator();
                        String policyNo = "";

                        while (termIt.hasNext()) {
                            String[] pair = (String[]) termIt.next();
                            String termBaseId = pair[0];
                            policyNo = pair[1];
                            String threadName = policyNo + "_" + termBaseId;

                            if (!policyTermMap.containsKey(threadName)) {
                                policyTermMap.put(threadName, termBaseId);
                                PolicyInquiryCallable policyInquiryCallable = new PolicyInquiryCallable(threadName,
                                    requestParam,
                                    inquiryRequest,
                                    policyNo,
                                    termBaseId);
                                tasks.add(policyInquiryCallable);
                            }
                        }
                    }
                }
            }

            if (MINIMAL_VIEW_NAME.equalsIgnoreCase(viewName)) {
                policyInquiryResultTypesMT = getOasisExecutorServiceManager().submit(getThreadPoolCategoryName(), tasksMinimalView);
            } else {
                policyInquiryResultTypesMT = getOasisExecutorServiceManager().submit(getThreadPoolCategoryName(), tasks);
            }

            //load all the item result to inquiryResult to be sent back
            for(PolicyInquiryItemResultMT inquiryResultType: policyInquiryResultTypesMT) {
                PolicyInquiryResultType itemsPolicy = inquiryResultType.getPolicyInquiryResult();
                addItemToResultObject(inquiryResult, itemsPolicy);

                List<String> partyLists = inquiryResultType.getPartyList();
                for (String p : partyLists) {
                    cacheClientId(partyList, p);
                }
            }

            if ( (DEFAULT_VIEW_NAME.equalsIgnoreCase(viewName) || POLICY_VIEW_NAME.equalsIgnoreCase(viewName)) && partyList.size()>0) {
                loadParty(inquiryRequest, inquiryResult, partyList);
                setPartyReferences(inquiryResult);
            }

            int resultPolCnt = inquiryResult.getMedicalMalpracticePolicy().size();
            if(resultPolCnt > 0){
                owsLogRequest.setSourceTableName("POLICY_TERM_HISTORY");
                owsLogRequest.setSourceRecordFk(inquiryResult.getMedicalMalpracticePolicy().get(0).getPolicyTermNumberId());
                owsLogRequest.setSourceRecordNo(inquiryResult.getMedicalMalpracticePolicy().get(0).getPolicyId());
            }

            partyList.clear();

            addInfoNoMatchResultMsg(invalidPolNos, invalidTermIds, transactionStatusCode, resultPolCnt);

            String msgCode = inquiryResult.getMessageStatus().getMessageStatusCode();
            if (MessageStatusHelper.STATUS_CODE_SUCCESS.equals(msgCode)){
                inquiryResult.setMessageStatus(MessageStatusHelper.getInstance().getSuccessMessageStatus());
            }

        } catch (MessageStatusAppException wsae) {
            inquiryResult.setMessageStatus(wsae.getMessageStatus());
        } catch(Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR, "Failure invoking the PolicyInquiryServiceManagerImpl", e);
            inquiryResult.setMessageStatus(MessageStatusHelper.getInstance().getRejectedMessageStatus(ae));
            l.logp(Level.SEVERE, getClass().getName(), "loadPolicy", ae.getMessage(), ae);
        }

        owsLogRequest.setMessageStatusCode(inquiryResult.getMessageStatus().getMessageStatusCode());

        if (l.isLoggable(Level.FINEST)) {
            String xmlResult = XMLUtils.marshalJaxbToXML(inquiryResult, _PolicyInquiryResult_QNAME);
            owsLogRequest.setResultXML(xmlResult);
            l.logp(Level.FINEST, getClass().getName(), "loadPolicy", xmlResult);
        } else {
            owsLogRequest.setServiceResult(inquiryResult);
            owsLogRequest.setServiceResultQName(_PolicyInquiryResult_QNAME);
        }
        owsLogRequest.setRequestName(_PolicyInquiryRequest_QNAME.getLocalPart());
        OwsAccessTrailManager.getInstance().updateOwsAccessTrailLogger(owsLogRequest);

        if (l.isLoggable(Level.FINER)) {
            String MessageId = inquiryRequest.getMessageId();
            String CorrelationId = inquiryRequest.getCorrelationId();
            Date endDate = new Date();

            l.logp(Level.FINER, getClass().getName(), "loadPolicy", "***   Policy Inquiry Service " + MessageId + "-" + CorrelationId + ": start time: " + startDate.toString() + "   ***");
            l.logp(Level.FINER, getClass().getName(), "loadPolicy", "***   Policy Inquiry Service " + MessageId + "-" + CorrelationId + ": end time: " + endDate.toString() + "   ***");
            l.logp(Level.FINER, getClass().getName(), "loadPolicy", "***   Policy Inquiry Service " + MessageId + "-" + CorrelationId + ": time elapsed: " + (endDate.getTime() - startDate.getTime()) / 1000 + " seconds   ***");
            l.logp(Level.FINER, getClass().getName(), "loadPolicy", "***   Policy Inquiry Service " + MessageId + "-" + CorrelationId + ": total policy inquiry request parameters: " +  inquiryRequest.getPolicyInquiryRequestParameters().size() + "  ***");
            l.logp(Level.FINER, getClass().getName(), "loadPolicy", "***   Policy Inquiry Service " + MessageId + "-" + CorrelationId + ": total thread count: " +  policyInquiryResultTypesMT.size() + "  ***");

            for(PolicyInquiryItemResultMT inquiryResultType: policyInquiryResultTypesMT) {
                OasisTaskInfo oasisTaskInfo = inquiryResultType.getOasisTaskInfo();
                l.logp(Level.FINER, getClass().getName(), "loadPolicy", "***   Policy Inquiry Service " + MessageId + "-" + CorrelationId +
                    ": task name: " + oasisTaskInfo.getTaskName());
                l.logp(Level.FINER, getClass().getName(), "loadPolicy", "***   Policy Inquiry Service " + MessageId + "-" + CorrelationId +
                    ":             task start time: " + oasisTaskInfo.getStartDate().toString());
                l.logp(Level.FINER, getClass().getName(), "loadPolicy", "***   Policy Inquiry Service " + MessageId + "-" + CorrelationId +
                    ":             task end time: " + oasisTaskInfo.getEndDate().toString());

            }
            l.exiting(getClass().getName(), "loadPolicy", inquiryResult);
        }

        return inquiryResult;
    }

    public void loadPolicySingleThreading (PolicyInquiryRequestType inquiryRequest,
                                           PolicyInquiryRequestParametersType requestParam,
                                           String policyNo,
                                           String termBaseId,
                                           PolicyInquiryItemResultMT policyInquiryItemResultMT) {
        Logger l = LogUtils.getLogger(getClass());

        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadPolicySingleThreading", new Object[]{inquiryRequest, requestParam, policyNo, termBaseId});
        }
        String viewName = getViewName(inquiryRequest);

        List<String> partyList = new ArrayList<String>();

        PolicyInquiryResultType item = new PolicyInquiryResultType();

        try {
            String filterInsured = getFilterInsured(inquiryRequest);
            String filterPrimaryInsured = getFilterPrimaryInsured(inquiryRequest);
            String transactionStatusCode = getTransactionStatusCode(inquiryRequest).toUpperCase();
            Record inputRecord = setInputRecord(requestParam);

            Record policyRec = null;
            RecordSet underwriterRs = null, polCompRs = null, agentRs = null, riskRs = null, covgRs = null,
                covgClsRs = null, compRs = null, billingAccountRs = null, riskAddtlExpRs = null;

            if (hasFilterPrimaryInsured(filterPrimaryInsured)) {
                filterInsured = getPrimaryInsured(termBaseId);
            }

            PolicyHeader policyHeader = getPolicyInquiryServiceHelper().loadPolicyHeader(policyNo, termBaseId, transactionStatusCode);

            if (policyHeader != null){
                RecordSet policyDetailList = getPolicyInquiryServiceHelper().loadPolicyDetailList(policyNo, termBaseId, transactionStatusCode);

                if(policyDetailList.getSize() != 0){
                    policyRec = policyDetailList.getFirstRecord();
                    Record summaryRecord = policyDetailList.getSummaryRecord();
                    policyRec.setFields(summaryRecord);
                }

                if (policyRec.getSize() != 0) {
                    policyHeader = getPolicyInquiryServiceHelper().buildPolicyHeader(policyHeader, policyRec);

                    policyRec.setFieldValue(VIEW_NAME, viewName);

                    billingAccountRs = loadPolicyBillingAccountInfo(policyHeader);

                    if (!MINIMAL_VIEW_NAME.equalsIgnoreCase(viewName)) {
                        underwriterRs = loadUnderwriterInformation(policyHeader);
                        agentRs = loadAgentInformation(inputRecord, policyHeader);
                    }

                    if (DEFAULT_VIEW_NAME.equalsIgnoreCase(viewName)) {
                        riskRs = loadRiskInformation(policyHeader, filterInsured);
                        covgRs = loadCoverageInformation(policyHeader, filterInsured);
                        covgClsRs = loadCoverageClassInformation(policyHeader, filterInsured);
                        compRs = loadComponentInformation(policyHeader, filterInsured);
                        polCompRs = loadPolicyComponentInformation(inputRecord, policyHeader);
                        riskAddtlExpRs = loadAllRiskAddtlExposure(policyHeader, filterInsured);
                    }

                    item = resultToObject(policyDetailList, policyRec, underwriterRs, agentRs, polCompRs, riskRs,
                        covgRs, covgClsRs, compRs, partyList, billingAccountRs, riskAddtlExpRs);
                }
            }
        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR, "Failure invoking the loadPolicySingleThreading", e);
            throw ae;
        }

        policyInquiryItemResultMT.setPolicyInquiryResult(item);
        policyInquiryItemResultMT.setPartyList(partyList);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadPolicySingleThreading", policyInquiryItemResultMT);
        }
    }


    public void loadPolicyMinimalViewSingleThreading (Record inputRecord, PolicyInquiryItemResultMT policyInquiryItemResultMT) {
        Logger l = LogUtils.getLogger(getClass());

        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadPolicyMinimalViewSingleThreading", new Object[]{inputRecord});
        }
        List<String> partyList = new ArrayList<>();

        PolicyInquiryResultType inquiryResult = new PolicyInquiryResultType();

        try {
            RecordSet underwriterRs = null, polCompRs = null, agentRs = null, riskRs = null, covgRs = null,
                covgClsRs = null, compRs = null, billingAccountRs = null, riskAddtlExpRs = null;

            RecordSet policyRecordSet = findAllPolicyMinimalInformation(inputRecord);
            Iterator policyIt = policyRecordSet.getRecords();
            String viewName = inputRecord.getStringValue(VIEW_NAME);
            RecordSet policyList = new RecordSet();
            while (policyIt.hasNext()) {
                Record policyRec = (Record)policyIt.next();
                policyRec.setFieldValue(VIEW_NAME, viewName);
                PolicyInquiryResultType item = resultToObject(policyList, policyRec, underwriterRs, agentRs, polCompRs, riskRs,
                    covgRs, covgClsRs, compRs, partyList, billingAccountRs, riskAddtlExpRs);

                addItemToResultObject(inquiryResult, item);
            }

        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR, "Failure invoking the loadPolicySingleThreading", e);
            throw ae;
        }

        policyInquiryItemResultMT.setPolicyInquiryResult(inquiryResult);
        policyInquiryItemResultMT.setPartyList(partyList);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadPolicyMinimalViewSingleThreading", policyInquiryItemResultMT);
        }
    }

    public void setPartyReferences (PolicyInquiryResultType inquiryResult) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setPartyReferences", new Object[]{inquiryResult});
        }

        List<PersonType> persons = inquiryResult.getPerson();
        List<OrganizationType> organizations = inquiryResult.getOrganization();
        List<PropertyType> properties = inquiryResult.getProperty();

        List<MedicalMalpracticePolicyType> policies = inquiryResult.getMedicalMalpracticePolicy();
        for (MedicalMalpracticePolicyType medicalMalpracticePolicy : policies) {
            PolicyHolderType policyHolder = medicalMalpracticePolicy.getPolicyHolder();
            for (PersonType person : persons) {
                if (policyHolder!=null && policyHolder.getPersonReference()==null && person.getPersonNumberId().equalsIgnoreCase(policyHolder.getReferredParty().getPartyNumberId())) {
                    policyHolder.setPersonReference(person.getKey());
                    break;
                }
            }
            for (OrganizationType organization : organizations) {
                if (policyHolder!=null && policyHolder.getOrganizationReference()==null && organization.getOrganizationNumberId().equalsIgnoreCase(policyHolder.getReferredParty().getPartyNumberId())) {
                    policyHolder.setOrganizationReference(organization.getKey());
                    break;
                }
            }

            List <InsurerType> insurers = medicalMalpracticePolicy.getInsurer();
            for (InsurerType insurer : insurers) {
                for (PersonType person : persons) {
                    if (insurer != null && insurer.getPersonReference() == null && person.getPersonNumberId().equalsIgnoreCase(insurer.getReferredParty().getPartyNumberId())) {
                        insurer.setPersonReference(person.getKey());
                        break;
                    }
                }
                for (OrganizationType organization : organizations) {
                    if (insurer != null && insurer.getOrganizationReference() == null && organization.getOrganizationNumberId().equalsIgnoreCase(insurer.getReferredParty().getPartyNumberId())) {
                        insurer.setOrganizationReference(organization.getKey());
                        break;
                    }
                }
            }

            ProducerType producer = medicalMalpracticePolicy.getProducer();
            for (PersonType person : persons) {
                if (producer!=null && producer.getPersonReference()==null && person.getPersonNumberId().equalsIgnoreCase(producer.getReferredParty().getPartyNumberId())) {
                    producer.setPersonReference(person.getKey());
                    break;
                }
            }
            for (OrganizationType organization : organizations) {
                if (producer!=null && producer.getOrganizationReference()==null && organization.getOrganizationNumberId().equalsIgnoreCase(producer.getReferredParty().getPartyNumberId())) {
                    producer.setOrganizationReference(organization.getKey());
                    break;
                }
            }

            IssueCompanyType issueCompany = medicalMalpracticePolicy.getFirstPolicyDetail().getIssueCompany();
            for (OrganizationType organization : organizations) {
                if (issueCompany!=null && issueCompany.getOrganizationReference()==null && organization.getOrganizationNumberId().equalsIgnoreCase(issueCompany.getReferredParty().getPartyNumberId())) {
                    issueCompany.setOrganizationReference(organization.getKey());
                    break;
                }
            }

            List<InsuredType> insureds = medicalMalpracticePolicy.getInsured();
            for (InsuredType insured : insureds) {
                for (PersonType person : persons) {
                    if (insured!=null && insured.getPersonReference()==null && person.getPersonNumberId().equalsIgnoreCase(insured.getReferredParty().getPartyNumberId())) {
                        insured.setPersonReference(person.getKey());
                        break;
                    }
                }
                for (OrganizationType organization : organizations) {
                    if (insured!=null && insured.getOrganizationReference()==null && organization.getOrganizationNumberId().equalsIgnoreCase(insured.getReferredParty().getPartyNumberId())) {
                        insured.setOrganizationReference(organization.getKey());
                        break;
                    }
                }
                for (PropertyType property : properties) {
                    if (insured!=null && insured.getPropertyReference()==null && property.getPropertyNumberId().equalsIgnoreCase(insured.getReferredParty().getPartyNumberId())) {
                        insured.setPropertyReference(property.getKey());
                        break;
                    }
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setPartyReferences");
        }

    }

    public Record setInputRecord (PolicyInquiryRequestParametersType requestParam) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setInputRecord", new Object[]{requestParam});
        }

        Record record = new Record();
        String policyNo = requestParam.getPolicyInquiry().getPolicyId();
        String termBaseRecordId = requestParam.getPolicyInquiry().getPolicyTermNumberId();
        String fullName = null;
        String partyNumberId = null;

        if (termBaseRecordId!=null && policyNo==null) {
            MessageManager mm = MessageManager.getInstance();
            mm.addErrorMessage("ws.policy.inquiry.policy.id.element.required");
            throw new AppException("<PolicyId> element is required when <PolicyTermNumberId> element is provided for Policy Inquiry Web Service.");
        }

        com.delphi_tech.ows.policyinquiryservice.PolicyHolderType pht = requestParam.getPolicyInquiry().getPolicyHolder();
        if (pht!=null) {
            PartyType pt = pht.getParty();
            if (pt!=null) {
                PartyNameType pnt = pt.getPartyName();
                if (!StringUtils.isBlank(pt.getPartyNumberId())) {
                    partyNumberId = pt.getPartyNumberId();
                }
                if (pnt!=null) {
                    if (pnt.getFullName() != null) {
                        fullName = pnt.getFullName().trim();
                    }
                }
            }
        }

        if (StringUtils.isBlank(policyNo) && StringUtils.isBlank(termBaseRecordId) &&
            StringUtils.isBlank(fullName) && StringUtils.isBlank(partyNumberId)) {
            MessageManager mm = MessageManager.getInstance();
            mm.addErrorMessage("ws.policy.inquiry.one.id.element.required");
            throw new AppException("At least one element is required for Policy Inquiry Web Service.");
        }

        record.setFieldValue(PolicyInquiryFields.POLICY_NO, policyNo);
        record.setFieldValue(PolicyInquiryFields.POLICY_TERM_NUMBER_ID, termBaseRecordId);
        record.setFieldValue(PolicyInquiryFields.FULL_NAME, fullName);
        record.setFieldValue(PolicyInquiryFields.PARTY_NUMBER_ID, partyNumberId);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setInputRecord", record);
        }

        return record;
    }

    public String getViewName (PolicyInquiryRequestType inquiryRequest) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getViewName", new Object[]{inquiryRequest});
        }

        PolicyInquiryResultParametersType policyInquiryResultParameters = inquiryRequest.getPolicyInquiryResultParameters();
        String viewName = DEFAULT_VIEW_NAME;
        if (policyInquiryResultParameters != null) {
            List<String> views = inquiryRequest.getPolicyInquiryResultParameters().getViewName();
            if (views != null) {
                Iterator viewIt = views.iterator();
                while (viewIt.hasNext()) {
                    String view = ((String)viewIt.next()).trim();
                    if (view!=null && view.trim().length()>0) {
                        if (MINIMAL_VIEW_NAME.equalsIgnoreCase(view)) {
                            viewName = view;
                            break;
                        }
                        else if (POLICY_VIEW_NAME.equalsIgnoreCase(view)) {
                            viewName = view;
                        }
                    }
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getViewName", viewName);
        }

        return viewName;
    }

    public String getPrimaryInsured (String termBaseId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPrimaryInsured", new Object[]{termBaseId});
        }

        Record record = new Record();
        record.setFieldValue(PolicyInquiryFields.TERM_BASE_RECORD_ID, termBaseId);

        Record output = getPolicyManager().getPrimaryRisk(record);
        String primaryInsuredId = output.getStringValue(RiskInquiryFields.RISK_NUMBER_ID);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPrimaryInsured", primaryInsuredId);
        }

        return primaryInsuredId;
    }

    public void addItemToResultObject (PolicyInquiryResultType result, PolicyInquiryResultType item) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addItemToResultObject", new Object[]{result, item});
        }

        result.getAddress().addAll(item.getAddress());
        result.getPerson().addAll(item.getPerson());
        result.getOrganization().addAll(item.getOrganization());
        result.getProperty().addAll(item.getProperty());
        result.getMedicalMalpracticePolicy().addAll(item.getMedicalMalpracticePolicy());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addItemToResultObject", result);
        }
    }

    public PolicyHeader loadPolicyHeader(String policyNo, String termBaseRecordId, PolicyViewMode desiredViewMode) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadPolicyHeader", new Object[]{policyNo, termBaseRecordId, desiredViewMode});
        }

        String requestId = "dti.pm.policymgr.struts.MaintainPolicyAction&process=loadPolicyDetail";
        String endQuoteId = null;

        if (getRequestStorageManager().has(RequestStorageIds.POLICY_HEADER)) {
            getRequestStorageManager().remove(RequestStorageIds.POLICY_HEADER);
        }

        PolicyHeader policyHeader = getPolicyManager().loadPolicyHeader(policyNo, termBaseRecordId, desiredViewMode, endQuoteId, requestId, "PolicyInquiryService: loadPolicyHeader", false);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadPolicyHeader", policyHeader);
        }

        return policyHeader;
    }

    public RecordSet findAllPolicyMinimalInformation(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "findAllPolicyMinimalInformation", new Object[]{inputRecord});
        }

        RecordSet output = getPolicyManager().findAllPolicyMinimalInformationForWs(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "findAllPolicyMinimalInformation", output);
        }

        return output;
    }

    public RecordSet loadUnderwriterInformation(PolicyHeader policyHeader) {
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

    public RecordSet loadAgentInformation(Record inputRecord, PolicyHeader policyHeader) {
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

    public RecordSet loadPolicyComponentInformation(Record inputRecord, PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadPolicyComponentInformation", new Object[]{inputRecord, policyHeader});
        }

        ComponentOwner owner = ComponentOwner.POLICY;
        RecordSet rs = new RecordSet();
        ComponentRowStyleRecordLoadprocessor compRowStyleLp = new ComponentRowStyleRecordLoadprocessor();
        RecordSet compRs = getComponentManager().loadAllComponentForWs(policyHeader, "", ComponentOwner.POLICY);
        if (compRs.getSize() > 1 && policyHeader.getCxlWipTermOffExpDate() != null) {
            compRs = changeCxlWipTermOffExpDate(policyHeader, compRs, ComponentInquiryFields.COMPONENT_EFF_TO_DATE);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadPolicyComponentInformation", compRs);
        }

        return compRs;
    }

    public RecordSet loadRiskInformation(PolicyHeader policyHeader, String filterInsured) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadRiskInformation", new Object[]{policyHeader, filterInsured});
        }

        RecordSet riskRs = getRiskManager().loadAllRiskForWs(policyHeader, filterInsured);
        if (riskRs.getSize() > 1 && policyHeader.getCxlWipTermOffExpDate() != null) {
            riskRs = changeCxlWipTermOffExpDate(policyHeader, riskRs, RiskInquiryFields.EFFECTIVE_TO_DATE);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadRiskInformation", riskRs);
        }

        return riskRs;
    }

    public RecordSet loadAllRiskAddtlExposure(PolicyHeader policyHeader, String filterInsured) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRiskAddtlExposure", new Object[]{policyHeader, filterInsured});
        }

        RecordSet riskAddtlExpRs = getRiskAddtlExposureManager().loadAllRiskAddtlExposureForWS(policyHeader, filterInsured);
        if (riskAddtlExpRs.getSize() > 1 && policyHeader.getCxlWipTermOffExpDate() != null) {
            riskAddtlExpRs = changeCxlWipTermOffExpDate(policyHeader, riskAddtlExpRs, PolicyInquiryFields.EFFECTIVE_TO_DATE);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRiskAddtlExposure", riskAddtlExpRs);
        }

        return riskAddtlExpRs;
    }

    public RecordSet loadCoverageInformation(PolicyHeader policyHeader, String  filterInsured) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadCoverageInformation", new Object[]{policyHeader, filterInsured});
        }

        RecordSet coverageRs = getCoverageManager().loadAllCoverageForWs(policyHeader, filterInsured);
        if (coverageRs.getSize() > 1 && policyHeader.getCxlWipTermOffExpDate() != null) {
            coverageRs = changeCxlWipTermOffExpDate(policyHeader, coverageRs, CoverageInquiryFields.EFFECTIVE_TO_DATE);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadCoverageInformation", coverageRs);
        }

        return coverageRs;
    }

    public RecordSet loadCoverageClassInformation(PolicyHeader policyHeader, String filterInsured) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadCoverageClassInformation", new Object[]{policyHeader, filterInsured});
        }

        RecordSet coverageClsRs = getCoverageClassManager().loadAllCoverageClassForWs(policyHeader, filterInsured);
        if (coverageClsRs.getSize() > 1 && policyHeader.getCxlWipTermOffExpDate() != null) {
            coverageClsRs = changeCxlWipTermOffExpDate(policyHeader, coverageClsRs, CoverageInquiryFields.CLASS_EFFECTIVE_TO_DATE);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadCoverageClassInformation", coverageClsRs);
        }

        return coverageClsRs;
    }

    public RecordSet loadComponentInformation(PolicyHeader policyHeader, String filterInsured) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadComponentInformation", new Object[]{policyHeader, filterInsured});
        }

        RecordSet componentRs = getComponentManager().loadAllComponentForWs(policyHeader, filterInsured, ComponentOwner.COVERAGE);
        if (componentRs.getSize() > 1 && policyHeader.getCxlWipTermOffExpDate() != null) {
            componentRs = changeCxlWipTermOffExpDate(policyHeader, componentRs, ComponentInquiryFields.COMPONENT_EFF_TO_DATE);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadComponentInformation", componentRs);
        }

        return componentRs;
    }

    public PolicyInquiryResultType resultToObject(RecordSet policyDetailList, Record policyRec, RecordSet underwriterRs,
                                                  RecordSet agentRs, RecordSet polCompRs, RecordSet riskRs, RecordSet covgRs,
                                                  RecordSet covgClsRs, RecordSet compRs, List<String> partyList,
                                                  RecordSet billingAccountRs, RecordSet riskAddtlExpRs) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "resultToObject", new Object[]{policyDetailList, policyRec, underwriterRs,
                agentRs, polCompRs, riskRs, covgRs, covgClsRs, compRs, billingAccountRs, riskAddtlExpRs});
        }

        PolicyInquiryResultType policyInquiryResultType = new PolicyInquiryResultType();
        setPolicyInformation(policyInquiryResultType, policyDetailList, policyRec, underwriterRs, agentRs, polCompRs, riskRs, covgRs,
            covgClsRs, compRs, partyList, billingAccountRs, riskAddtlExpRs);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "resultToObject", policyInquiryResultType);
        }

        return policyInquiryResultType;

    }

    public void setPolicyInformation(PolicyInquiryResultType policyInquiryResultType, RecordSet policyDetailList, Record policyRec,
                                     RecordSet underwriterRs, RecordSet agentRs, RecordSet polCompRs, RecordSet riskRs,
                                     RecordSet covgRs, RecordSet covgClsRs, RecordSet compRs, List<String> partyList,
                                     RecordSet billingAccountRs, RecordSet riskAddtlExpRs) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setPolicyInformation", new Object[]{policyInquiryResultType, policyDetailList, policyRec,
                underwriterRs, agentRs, polCompRs, riskRs, covgRs, covgClsRs, compRs, partyList, billingAccountRs, riskAddtlExpRs});
        }

        MedicalMalpracticePolicyType medicalMalpracticePolicyType = getMedicalMalpracticePolicy(policyDetailList, policyRec,
            underwriterRs, agentRs, polCompRs, riskRs, covgRs, covgClsRs, compRs, partyList, billingAccountRs, riskAddtlExpRs);
        policyInquiryResultType.getMedicalMalpracticePolicy().add(medicalMalpracticePolicyType);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setPolicyInformation", policyInquiryResultType);
        }
    }

    public MedicalMalpracticePolicyType getMedicalMalpracticePolicy(RecordSet policyDetailList, Record policyRecord, RecordSet underwriterRs,
                                                                    RecordSet agentRs, RecordSet polCompRs, RecordSet riskRs, RecordSet covgRs, RecordSet covgClsRs,
                                                                    RecordSet compRs, List<String> partyList,RecordSet billingAccountRs, RecordSet riskAddtlExpRs) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getMedicalMalpracticePolicy",
                new Object[]{policyRecord, underwriterRs, agentRs, polCompRs, riskRs, covgRs, covgClsRs, compRs, riskAddtlExpRs});
        }

        MedicalMalpracticePolicyType medicalMalpracticePolicyType = new MedicalMalpracticePolicyType();

        medicalMalpracticePolicyType.setPolicyId(policyRecord.getStringValue(PolicyInquiryFields.POLICY_NO_ID));
        medicalMalpracticePolicyType.setPolicyNumberId(policyRecord.getStringValue(PolicyInquiryFields.POL_ID));
        medicalMalpracticePolicyType.setPolicyTermNumberId(policyRecord.getStringValue(PolicyInquiryFields.TERM_BASE_RECORD_ID));
        medicalMalpracticePolicyType.setCurrentTermAmount(policyRecord.getStringValue(PolicyInquiryFields.TERM_WRITTEN_PREMIUM));

        PrintNameType printName = new PrintNameType();
        printName.setFullName(policyRecord.getStringValue(PolicyInquiryFields.POLICY_HOLDER_NAME, ""));
        medicalMalpracticePolicyType.setPrintName(printName);

        medicalMalpracticePolicyType.setContractPeriod(getContractPeriod(policyRecord));
        String viewName = policyRecord.getStringValue(VIEW_NAME);

        if (DEFAULT_VIEW_NAME.equalsIgnoreCase(viewName) || POLICY_VIEW_NAME.equalsIgnoreCase(viewName)) {
            medicalMalpracticePolicyType.setTransactionDetail(getTransactionDetail(policyRecord));
            medicalMalpracticePolicyType.setRenewalDetail(getRenewalDetail(policyRecord));
            medicalMalpracticePolicyType.setPolicyHolder(getPolicyHolder(policyRecord, partyList));
            medicalMalpracticePolicyType.getInsurer().addAll(getInsurer(underwriterRs, partyList));
            medicalMalpracticePolicyType.setProducer(getProducer(agentRs, partyList));
            medicalMalpracticePolicyType.setPolicyDetail(getPolicyDetailList(policyDetailList, policyRecord, partyList));
        }

        if (DEFAULT_VIEW_NAME.equalsIgnoreCase(viewName)) {
            setPrincipalBillingAccountInformation(medicalMalpracticePolicyType.getPrincipalBillingAccountInformation(), policyRecord, billingAccountRs, riskRs, covgRs);
            setComponent(medicalMalpracticePolicyType.getCreditSurchargeDeductible(), polCompRs, true);

            setInsured(medicalMalpracticePolicyType.getInsured(), policyRecord, riskRs, partyList, riskAddtlExpRs);
            MedicalMalpracticeLineOfBusinessType medicalMalpracticeLineOfBusiness = new MedicalMalpracticeLineOfBusinessType();
            medicalMalpracticePolicyType.setMedicalMalpracticeLineOfBusiness(medicalMalpracticeLineOfBusiness);
            setCoverage(medicalMalpracticeLineOfBusiness, covgRs);
            setCoverageClass(medicalMalpracticeLineOfBusiness, covgClsRs, covgRs);
            setComponent(medicalMalpracticePolicyType.getCreditSurchargeDeductible(), compRs, false);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getMedicalMalpracticePolicy", medicalMalpracticePolicyType);
        }

        return medicalMalpracticePolicyType;
    }

    public ContractPeriodType getContractPeriod(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getContractPeriod", new Object[]{record});
        }

        ContractPeriodType contractPeriodType =  new ContractPeriodType();
        contractPeriodType.setStartDate(DateUtils.parseOasisDateToXMLDate(record.getStringValue(PolicyInquiryFields.TERM_EFF_FROM_DATE, "")));
        contractPeriodType.setEndDate(DateUtils.parseOasisDateToXMLDate(record.getStringValue(PolicyInquiryFields.TERM_EFF_TO_DATE, "")));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getContractPeriod", contractPeriodType);
        }

        return contractPeriodType;
    }

    public TransactionDetailType getTransactionDetail(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getTransactionDetail", new Object[]{record});
        }

        TransactionDetailType transactionDetailType = new TransactionDetailType();
        transactionDetailType.setTransactionNumberId(record.getStringValue(PolicyInquiryFields.LAST_TRANSACTION_ID, ""));

        TransactionCodeType transactionCodeType = new TransactionCodeType();
        transactionCodeType.setValue(record.getStringValue(PolicyInquiryFields.TRANSACTION_CODE, ""));
        transactionDetailType.setTransactionCode(transactionCodeType);

        transactionDetailType.setTransactionEffectiveDate(DateUtils.parseOasisDateToXMLDate(record.getStringValue(PolicyInquiryFields.TRANSACTION_EFFECTIVE_DATE, "")));

        transactionDetailType.setTransactionCode(new TransactionCodeType());
        transactionDetailType.getTransactionCode().setValue(record.getStringValue(PolicyInquiryFields.TRANSACTION_CODE, ""));
        String transactionStatusCode = "";
        String transStatusFromRec = record.getStringValue(PolicyInquiryFields.TRANSACTION_STATUS_CODE, "");
        if (String.valueOf(TransactionStatus.INPROGRESS).equals(transStatusFromRec)){
            transactionStatusCode = String.valueOf(RecordMode.WIP);
        }
        else if(String.valueOf(TransactionStatus.COMPLETE).equals(transStatusFromRec)){
            transactionStatusCode = String.valueOf(RecordMode.OFFICIAL);
        }
        else{
            transactionStatusCode = transStatusFromRec;
        }
        transactionDetailType.setTransactionStatusCode(new TransactionStatusCodeType());
        transactionDetailType.getTransactionStatusCode().setValue(transactionStatusCode);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getTransactionDetail", transactionDetailType);
        }

        return transactionDetailType;
    }

    public RenewalDetailType getRenewalDetail(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRenewalDetail", new Object[]{record});
        }

        RenewalDetailType renewalDetailType = new RenewalDetailType();
        renewalDetailType.setNonRenewalReason(record.getStringValue(PolicyInquiryFields.NON_RENEWAL_REASON_CODE, ""));
        renewalDetailType.setRenewalIndicator(record.getStringValue(PolicyInquiryFields.RENEWAL_INDICATOR_CODE, ""));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRenewalDetail", renewalDetailType);
        }

        return renewalDetailType;
    }

    public PolicyHolderType getPolicyHolder(Record record, List<String> partyList) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPolicyHolder", new Object[]{record, partyList});
        }

        PolicyHolderType policyHolderType = new PolicyHolderType();
        String partyId = record.getStringValue(PolicyInquiryFields.POLICY_HOLDER_ENTITY_ID, "");
        if (partyId!=null && !"".equalsIgnoreCase(partyId)) {
            cacheClientId(partyList, partyId);
        }
        ReferredPartyType referredParty = new ReferredPartyType();
        referredParty.setPartyNumberId(partyId);
        policyHolderType.setReferredParty(referredParty);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPolicyHolder", policyHolderType);
        }

        return policyHolderType;
    }

    public List <InsurerType> getInsurer(RecordSet recordSet, List<String> partyList) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInsurer", new Object[]{recordSet});
        }

        List <InsurerType> insurers = new ArrayList<InsurerType>();

        String partyNumberId = "";

        Iterator it = recordSet.getRecords();
        while (it.hasNext()) {
            Record record = (Record)it.next();
            partyNumberId = record.getStringValue(PolicyInquiryFields.INSURER_ENTITY_ID, "");
            cacheClientId(partyList, partyNumberId);

            InsurerType insurer = new InsurerType();
            ReferredPartyType referredParty = new ReferredPartyType();
            referredParty.setPartyNumberId(partyNumberId);
            insurer.setReferredParty(referredParty);
            insurer.setInsurerNumberId(record.getStringValue(PolicyInquiryFields.INSURER_NUMBER_ID, ""));

            EffectivePeriodType effectivePeriod = new EffectivePeriodType();
            effectivePeriod.setStartDate(DateUtils.parseOasisDateToXMLDate(record.getStringValue(PolicyInquiryFields.EFFECTIVE_FROM_DATE, "")));
            effectivePeriod.setEndDate(DateUtils.parseOasisDateToXMLDate(record.getStringValue(PolicyInquiryFields.EFFECTIVE_TO_DATE, "")));
            insurer.setEffectivePeriod(effectivePeriod);
            insurer.setRenewalIndicator(YesNoEmptyFlag.getInstance(record.getStringValue(PolicyInquiryFields.RENEWAL_B, "")).trueFalseEmptyValue());
            insurer.setTypeCode(record.getStringValue(PolicyInquiryFields.UW_TYPE_CODE, ""));

            insurers.add(insurer);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInsurer", insurers);
        }

        return insurers;
    }

    public ProducerType getProducer(RecordSet agentRecordSet, List<String> partyList) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getProducer", new Object[]{agentRecordSet, partyList});
        }

        ProducerType producerType = null;

        Iterator it = agentRecordSet.getRecords();
        if (!it.hasNext()) {
            producerType = new ProducerType();
            ReferredPartyType referredParty = new ReferredPartyType();
            referredParty.setPartyNumberId("");
            producerType.setReferredParty(referredParty);
        }
        else {
            while (it.hasNext()) {
                List<CommissionType> commTypeList = new ArrayList<CommissionType>();
                List<LicenseType> licenseTypeList = new ArrayList<LicenseType>();
                CommissionType commType = null;
                LicenseType licenseType = null;
                Record record = (Record)it.next();
                String partyNumberId = record.getStringValue(PolicyInquiryFields.PRODUCER_ENTITY_ID, "");
                cacheClientId(partyList, partyNumberId);
                producerType = new ProducerType();
                ReferredPartyType referredParty = new ReferredPartyType();
                referredParty.setPartyNumberId(partyNumberId);
                producerType.setProducerNumberId(record.getStringValue(PolicyInquiryFields.POLICY_AGENT_ID, ""));
                producerType.setProducerChangeTypeCode("N/A");
                producerType.setProducerAuthorizationCode(record.getStringValue(PolicyInquiryFields.AUTHORIZATION_CODE, ""));

                commType = new CommissionType();
                commType.setCommissionTypeCode(PolicyInquiryFields.NEWBUS);
                commType.setCommissionAmount(record.getStringValue(PolicyInquiryFields.NEW_BUS_COMM_FLAG_AMOUNT, ""));
                commType.setCommissionBasisCode(record.getStringValue(PolicyInquiryFields.NEW_BUS_COMM_BASIS, ""));
                commType.setCommissionPayCode(record.getStringValue(PolicyInquiryFields.NEW_BUS_COMM_PAYCODE, ""));
                commType.setCommissionRatePercent(record.getStringValue(PolicyInquiryFields.NEW_BUS_COMM_RATE, ""));
                commType.setRateScheduleCode(record.getStringValue(PolicyInquiryFields.NEW_BUS_COMM_RATE_SCHED_ID, ""));
                commType.setLimitChargeAmount(record.getStringValue(PolicyInquiryFields.NEW_BUS_COMM_LIMIT, ""));
                commTypeList.add(commType);
                commType = new CommissionType();
                commType.setCommissionTypeCode(PolicyInquiryFields.ERE);
                commType.setCommissionAmount(record.getStringValue(PolicyInquiryFields.ERE_COMM_FLAT_AMOUNT, ""));
                commType.setCommissionBasisCode(record.getStringValue(PolicyInquiryFields.ERE_COMM_BASIS, ""));
                commType.setCommissionPayCode(record.getStringValue(PolicyInquiryFields.ERE_COMM_PAY_CODE, ""));
                commType.setCommissionRatePercent(record.getStringValue(PolicyInquiryFields.ERE_COMM_RATE, ""));
                commType.setRateScheduleCode(record.getStringValue(PolicyInquiryFields.ERE_BUS_COMM_RATE_SCHED_ID, ""));
                commType.setLimitChargeAmount(record.getStringValue(PolicyInquiryFields.ERE_COMM_LIMIT, ""));
                commTypeList.add(commType);
                commType = new CommissionType();
                commType.setCommissionTypeCode(PolicyInquiryFields.RENEWAL);
                commType.setCommissionAmount(record.getStringValue(PolicyInquiryFields.RENEWAL_COMM_FLAG_AMOUNT, ""));
                commType.setCommissionBasisCode(record.getStringValue(PolicyInquiryFields.RENEWAL_COMM_BASIS, ""));
                commType.setCommissionPayCode(record.getStringValue(PolicyInquiryFields.RENEWAL_COMM_PAY_CODE, ""));
                commType.setCommissionRatePercent(record.getStringValue(PolicyInquiryFields.RENEWAL_COMM_RATE, ""));
                commType.setRateScheduleCode(record.getStringValue(PolicyInquiryFields.RENEWAL_BUS_COMM_RATE_SCHED_ID, ""));
                commType.setLimitChargeAmount(record.getStringValue(PolicyInquiryFields.RENEWAL_COMM_LIMIT, ""));
                commTypeList.add(commType);

                licenseType = new LicenseType();
                if (!StringUtils.isBlank(record.getStringValue(PolicyInquiryFields.PRODUCER_AGENT_LIC_ID, ""))) {
                    licenseType.setLicenseClassCode(record.getStringValue(PolicyInquiryFields.PRODUCER_LICENSE_CLASS, ""));
                    licenseType.setLicenseOrPermitNumberId(record.getStringValue(PolicyInquiryFields.PRODUCER_AGENT_LIC_ID, ""));
                    licenseTypeList.add(licenseType);
                }
                licenseType = new LicenseType();
                if (!StringUtils.isBlank(record.getStringValue(PolicyInquiryFields.SUBPRODUCER_LIC_TYPE_CODE, ""))) {
                    licenseType.setLicenseClassCode(record.getStringValue(PolicyInquiryFields.PRODUCER_LICENSE_CLASS, ""));
                    licenseType.setLicenseOrPermitNumberId(record.getStringValue(PolicyInquiryFields.SUB_PROD_AGENT_LIC_ID, ""));
                    licenseTypeList.add(licenseType);
                }
                licenseType = new LicenseType();
                if (!StringUtils.isBlank(record.getStringValue(PolicyInquiryFields.COUNTERSIGNER_AGENT_LIC_ID, ""))) {
                    licenseType.setLicenseClassCode(record.getStringValue(PolicyInquiryFields.PRODUCER_LICENSE_CLASS, ""));
                    licenseType.setLicenseOrPermitNumberId(record.getStringValue(PolicyInquiryFields.COUNTERSIGNER_AGENT_LIC_ID, ""));
                    licenseTypeList.add(licenseType);
                }
                licenseType = new LicenseType();
                if (!StringUtils.isBlank(record.getStringValue(PolicyInquiryFields.AUTHORIZEDREP_AGENT_LIC_ID, ""))) {
                    licenseType.setLicenseClassCode(record.getStringValue(PolicyInquiryFields.PRODUCER_LICENSE_CLASS, ""));
                    licenseType.setLicenseOrPermitNumberId(record.getStringValue(PolicyInquiryFields.AUTHORIZEDREP_AGENT_LIC_ID, ""));
                    licenseTypeList.add(licenseType);
                }
                producerType.getCommission().addAll(commTypeList);
                producerType.getLicense().addAll(licenseTypeList);
                producerType.setReferredParty(referredParty);
                break;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getProducer", producerType);
        }

        return producerType;
    }

    public void setPrincipalBillingAccountInformation(List<PrincipalBillingAccountInformationType> principalBillingAccountInformationTypeList,
                                                      Record policyRecord,
                                                      RecordSet billingAccountRs,
                                                      RecordSet riskRs,
                                                      RecordSet covgRs) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setPrincipalBillingAccountInformation", new Object[]{principalBillingAccountInformationTypeList, policyRecord, billingAccountRs, riskRs, covgRs});
        }

        PrincipalBillingAccountInformationType principalBillingAccountInformationType = null;

        String termEffFromDate = policyRecord.getStringValue(PolicyInquiryFields.TERM_EFF_FROM_DATE);
        String termEffToDate = policyRecord.getStringValue(PolicyInquiryFields.TERM_EFF_TO_DATE);
        String billingSetupEffFromDate = null;
        String billingSetupEffToDate = null;
        String RiskId = "";
        String CoverageId = "";
        String riskEffFromDate = null;
        String riskEffToDate = null;
        String covgEffFromDate = null;
        String covgEffToDate = null;
        Record billingAccountRec = null;
        boolean policyExistsB = false;
        boolean riskExistsB = false;
        boolean covgExistsB = false;

        Iterator it = billingAccountRs.getRecords();
        while (it.hasNext()) {
            billingAccountRec = (Record) it.next();

            billingSetupEffFromDate = billingAccountRec.getStringValue(PolicyInquiryFields.EFFECTIVE_FROM_DATE);
            billingSetupEffToDate = billingAccountRec.getStringValue(PolicyInquiryFields.EFFECTIVE_TO_DATE);

            //1st level filtering, compare term effective from/to date with billing setup effective from/to dates
            if((!StringUtils.isBlank(billingSetupEffFromDate) && billingSetupEffFromDate.equalsIgnoreCase(billingSetupEffToDate)
                && billingSetupEffFromDate.equalsIgnoreCase(termEffFromDate))
                || (DateUtils.dateRangeOverlap(termEffFromDate, termEffToDate, billingSetupEffFromDate, billingSetupEffToDate))){
                principalBillingAccountInformationType = new PrincipalBillingAccountInformationType();

                principalBillingAccountInformationType.setBillingAccountId(billingAccountRec.getStringValue(PolicyInquiryFields.ACCOUNT_NO, ""));
                LinkedPolicyType linkedPolicy = new LinkedPolicyType();
                linkedPolicy.setPolicyId(policyRecord.getStringValue(PolicyInquiryFields.POLICY_NO_ID));
                PaymentOptionType paymentOption = null;
                RiskId = billingAccountRec.getStringValue(RISK_ID);
                CoverageId = billingAccountRec.getStringValue(COVERAGE_ID);
                if(!"-1".equalsIgnoreCase(CoverageId) && !"-1".equalsIgnoreCase(RiskId)){
                    //set billing setup info at coverage level
                    linkedPolicy.setCoverageId(CoverageId);
                    linkedPolicy.setInsuredId(RiskId);

                    String preCovgEffFromDate = null;
                    String preCovgEffToDate = null;

                    Iterator covgIt = covgRs.getRecords();
                    while (covgIt.hasNext()) {
                        Record covgRec = (Record)covgIt.next();

                        //2nd level filtering, check if coverage itself exists in current term
                        if(CoverageId.equalsIgnoreCase(covgRec.getStringValue(CoverageInquiryFields.COVERAGE_NUMBER_ID))){
                            covgExistsB = true;
                            covgEffFromDate = covgRec.getStringValue(CoverageInquiryFields.EFFECTIVE_FROM_DATE, "");
                            covgEffToDate = covgRec.getStringValue(CoverageInquiryFields.EFFECTIVE_TO_DATE, "");
                            if(linkedPolicy.getPaymentOption().size()==0){
                                preCovgEffFromDate = covgEffFromDate;
                                preCovgEffToDate = covgEffToDate;

                                paymentOption = new PaymentOptionType();
                                setEffectivePeriodForPaymentOption(paymentOption, covgEffFromDate, covgEffToDate, billingSetupEffFromDate, billingSetupEffToDate);
                                paymentOption.setPaymentPlanId(billingAccountRec.getStringValue(PolicyInquiryFields.BILLING_PAYMENT_PLANID));
                                linkedPolicy.getPaymentOption().add(paymentOption);
                            }
                            else{
                                //process when multiple coverage version has the same payment plan fk.
                                preCovgEffFromDate = getMinEffOrMaxExpDate(preCovgEffFromDate, covgEffFromDate, true);
                                preCovgEffToDate = getMinEffOrMaxExpDate(preCovgEffToDate, covgEffToDate, false);

                                setEffectivePeriodForPaymentOption(linkedPolicy.getPaymentOption().get(0), preCovgEffFromDate, preCovgEffToDate, billingSetupEffFromDate, billingSetupEffToDate);
                            }
                        }
                    }
                }
                else if("-1".equalsIgnoreCase(CoverageId) && !"-1".equalsIgnoreCase(RiskId)){
                    //set billing setup info at risk level
                    linkedPolicy.setInsuredId(RiskId);

                    String preRiskEffFromDate = null;
                    String preRiskEffToDate = null;

                    Iterator riskIt = riskRs.getRecords();
                    while (riskIt.hasNext()) {
                        Record insRec = (Record)riskIt.next();
                        //2nd level filtering, check if risk itself exists in current term
                        if(RiskId.equalsIgnoreCase(insRec.getStringValue(RiskInquiryFields.RISK_NUMBER_ID))){
                            riskExistsB = true;
                            riskEffFromDate = insRec.getStringValue(RiskInquiryFields.EFFECTIVE_FROM_DATE, "");
                            riskEffToDate = insRec.getStringValue(RiskInquiryFields.EFFECTIVE_TO_DATE, "");

                            if(linkedPolicy.getPaymentOption().size()==0){
                                preRiskEffFromDate = riskEffFromDate;
                                preRiskEffToDate = riskEffToDate;

                                paymentOption = new PaymentOptionType();
                                setEffectivePeriodForPaymentOption(paymentOption, riskEffFromDate, riskEffToDate, billingSetupEffFromDate, billingSetupEffToDate);
                                paymentOption.setPaymentPlanId(billingAccountRec.getStringValue(PolicyInquiryFields.BILLING_PAYMENT_PLANID));
                                linkedPolicy.getPaymentOption().add(paymentOption);
                            }
                            else{
                                //process when multiple risk version has the same payment plan fk.
                                preRiskEffFromDate = getMinEffOrMaxExpDate(preRiskEffFromDate, riskEffFromDate, true);
                                preRiskEffToDate = getMinEffOrMaxExpDate(preRiskEffToDate, riskEffToDate, false);

                                setEffectivePeriodForPaymentOption(linkedPolicy.getPaymentOption().get(0), preRiskEffFromDate, preRiskEffToDate, billingSetupEffFromDate, billingSetupEffToDate);
                            }
                        }
                    }
                }
                else if("-1".equalsIgnoreCase(CoverageId) && "-1".equalsIgnoreCase(RiskId)){
                    //set billing setup info at policy level
                    policyExistsB = true;
                    paymentOption = new PaymentOptionType();
                    setEffectivePeriodForPaymentOption(paymentOption, termEffFromDate, termEffToDate, termEffFromDate, termEffToDate);
                    paymentOption.setPaymentPlanId(billingAccountRec.getStringValue(PolicyInquiryFields.BILLING_PAYMENT_PLANID));
                    linkedPolicy.getPaymentOption().add(paymentOption);
                }

                if(policyExistsB || riskExistsB || covgExistsB){
                    policyExistsB = false;
                    riskExistsB = false;
                    covgExistsB = false;
                }else{
                    //the risk/coverage itself doesn't exist in current term.
                    continue;
                }

                boolean diffPayIdExistB = false;
                LinkedPolicyType currentLinkedPolicy = null;
                //process in case of a given policy, risk or coverage has multiple different paymentplanID
                for(int prinBillAcctIndx = 0; prinBillAcctIndx < principalBillingAccountInformationTypeList.size(); prinBillAcctIndx++){
                    if(principalBillingAccountInformationType.getBillingAccountId().equalsIgnoreCase(principalBillingAccountInformationTypeList.get(prinBillAcctIndx).getBillingAccountId())
                        && principalBillingAccountInformationTypeList.get(prinBillAcctIndx).getLinkedPolicy()!= null &&
                        principalBillingAccountInformationTypeList.get(prinBillAcctIndx).getLinkedPolicy().get(0)!= null){
                        currentLinkedPolicy = principalBillingAccountInformationTypeList.get(prinBillAcctIndx).getLinkedPolicy().get(0);

                        //Process when current billing setup info is at Policy level
                        if(!StringUtils.isBlank(linkedPolicy.getPolicyId()) && linkedPolicy.getPolicyId().equalsIgnoreCase(currentLinkedPolicy.getPolicyId())
                            && StringUtils.isBlank(linkedPolicy.getInsuredId()) && StringUtils.isBlank(currentLinkedPolicy.getInsuredId())
                            && StringUtils.isBlank(linkedPolicy.getCoverageId()) && StringUtils.isBlank(currentLinkedPolicy.getCoverageId())
                            && currentLinkedPolicy.getPaymentOption()!=null){
                            diffPayIdExistB = true;
                            principalBillingAccountInformationTypeList.get(prinBillAcctIndx).getLinkedPolicy().get(0).getPaymentOption().add(paymentOption);
                        }
                        //Process when current billing setup info is at risk level
                        else if(!StringUtils.isBlank(linkedPolicy.getPolicyId()) && linkedPolicy.getPolicyId().equalsIgnoreCase(currentLinkedPolicy.getPolicyId())
                            && !StringUtils.isBlank(linkedPolicy.getInsuredId()) && linkedPolicy.getInsuredId().equalsIgnoreCase(currentLinkedPolicy.getInsuredId())
                            && StringUtils.isBlank(linkedPolicy.getCoverageId()) && StringUtils.isBlank(currentLinkedPolicy.getCoverageId())
                            && currentLinkedPolicy.getPaymentOption()!=null){
                            diffPayIdExistB = true;
                            principalBillingAccountInformationTypeList.get(prinBillAcctIndx).getLinkedPolicy().get(0).getPaymentOption().add(paymentOption);
                        }
                        //Process when current billing setup info is at coverage level
                        else if(!StringUtils.isBlank(linkedPolicy.getPolicyId()) && linkedPolicy.getPolicyId().equalsIgnoreCase(currentLinkedPolicy.getPolicyId())
                            && !StringUtils.isBlank(linkedPolicy.getInsuredId()) && linkedPolicy.getInsuredId().equalsIgnoreCase(currentLinkedPolicy.getInsuredId())
                            && !StringUtils.isBlank(linkedPolicy.getCoverageId()) && linkedPolicy.getCoverageId().equalsIgnoreCase(currentLinkedPolicy.getCoverageId())
                            && currentLinkedPolicy.getPaymentOption()!=null ){
                            diffPayIdExistB = true;
                            principalBillingAccountInformationTypeList.get(prinBillAcctIndx).getLinkedPolicy().get(0).getPaymentOption().add(paymentOption);
                        }
                    }
                }

                if(!diffPayIdExistB){
                    principalBillingAccountInformationType.getLinkedPolicy().add(linkedPolicy);

                    principalBillingAccountInformationTypeList.add(principalBillingAccountInformationType);
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setPrincipalBillingAccountInformation", principalBillingAccountInformationTypeList);
        };
    }

    public String getMinEffOrMaxExpDate(String dateStr1, String dateStr2, boolean flag) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getMinEffOrMaxExpDate", new Object[]{dateStr1, dateStr2});
        }
        String minEffOrMaxExpDateStr = "";
        Date   date1 = new Date();
        Date   date2   = new Date();

        if (StringUtils.isBlank(dateStr1)) {
            date1 = DateUtils.parseDate("01/01/1900");
        }
        else{
            date1  = DateUtils.parseDate(dateStr1);
        }

        if (StringUtils.isBlank(dateStr2)) {
            date2 = DateUtils.parseDate("01/01/3000");
        }
        else{
            date2 = DateUtils.parseDate(dateStr2);
        }

        if(flag){
            //Take the earliest effective from date in the risk/coverage versions
            minEffOrMaxExpDateStr = date1.compareTo(date2) <= 0 ? dateStr1 : dateStr2;
        }
        else{
            //Take the biggest expiration date in the risk/coverage versions
            minEffOrMaxExpDateStr = date1.compareTo(date2) >= 0 ? dateStr1 : dateStr2;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getMinEffOrMaxExpDate");
        }
        return minEffOrMaxExpDateStr;
    }

    public void setEffectivePeriodForPaymentOption(PaymentOptionType paymentOption, String effectiveDate, String expirationDate,
                                                   String billingSetupEffFromDate, String billingSetupEffToDate) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setEffectivePeriodForPaymentOption", new Object[]{paymentOption, effectiveDate,
                expirationDate, billingSetupEffFromDate, billingSetupEffToDate});
        }

        Date   fromDate1 = new Date();
        Date   toDate1   = new Date();
        Date   fromDate2 = new Date();
        Date   toDate2   = new Date();

        if (StringUtils.isBlank(effectiveDate)) {
            fromDate1 = DateUtils.parseDate("01/01/1900");
        }
        else{
            fromDate1  = DateUtils.parseDate(effectiveDate);
        }

        if (StringUtils.isBlank(expirationDate)) {
            toDate1 = DateUtils.parseDate("01/01/3000");
        }
        else{
            toDate1 = DateUtils.parseDate(expirationDate);
        }

        if (StringUtils.isBlank(billingSetupEffFromDate)) {
            fromDate2 = DateUtils.parseDate("01/01/1900");
        }
        else{
            fromDate2 = DateUtils.parseDate(billingSetupEffFromDate);
        }

        if (StringUtils.isBlank(billingSetupEffToDate)) {
            toDate2 = DateUtils.parseDate("01/01/3000");
        }
        else{
            toDate2 = DateUtils.parseDate(billingSetupEffToDate);
        }

        if(paymentOption.getEffectivePeriod() == null){
            com.delphi_tech.ows.account.EffectivePeriodType effectivePeriodType = new com.delphi_tech.ows.account.EffectivePeriodType();

            //take max(risk/coverage version inception date, billing set up effective date) as start date
            effectivePeriodType.setStartDate(fromDate1.compareTo(fromDate2) >= 0 ?
                DateUtils.parseOasisDateToXMLDate(effectiveDate) : DateUtils.parseOasisDateToXMLDate(billingSetupEffFromDate));
            //take min (risk/coverage version expiration date, billing set up expiration date) as start date
            effectivePeriodType.setEndDate(toDate1.compareTo(toDate2) <= 0 ?
                DateUtils.parseOasisDateToXMLDate(expirationDate) : DateUtils.parseOasisDateToXMLDate(billingSetupEffToDate));

            paymentOption.setEffectivePeriod(effectivePeriodType);
        }
        //Reset the effectivePeriod for paymentOption when risk/coverage has mutiple versions for same paymentplanID
        else{
            //take max(risk/coverage version inception date, billing set up effective date) as start date
            paymentOption.getEffectivePeriod().setStartDate(fromDate1.compareTo(fromDate2) >= 0 ?
                DateUtils.parseOasisDateToXMLDate(effectiveDate) : DateUtils.parseOasisDateToXMLDate(billingSetupEffFromDate));
            //take min (risk/coverage version expiration date, billing set up expiration date) as start date
            paymentOption.getEffectivePeriod().setEndDate(toDate1.compareTo(toDate2) <= 0 ?
                DateUtils.parseOasisDateToXMLDate(expirationDate) : DateUtils.parseOasisDateToXMLDate(billingSetupEffToDate));
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setEffectivePeriodForPaymentOption");
        }
    }

    public List<PolicyDetailType> getPolicyDetailList(RecordSet policyDetailList, Record policyRecord, List<String> partyList) {
        List<PolicyDetailType> rs = new ArrayList<PolicyDetailType>();

        for(int i = 0; i < policyDetailList.getSize(); i++) {
            Record tempRec = policyDetailList.getRecord(i);
            Record summaryRecord = policyDetailList.getSummaryRecord();
            tempRec.setFields(summaryRecord);

            rs.add(getPolicyDetail(tempRec, partyList));
        }

        return rs;
    }

    public PolicyDetailType getPolicyDetail(Record policyRecord, List<String> partyList) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPolicyDetail", new Object[]{policyRecord, partyList});
        }

        PolicyDetailType policyDetailType = new PolicyDetailType();
        PolicyTypeCodeType policyTypeCodeType = new PolicyTypeCodeType();
        policyTypeCodeType.setValue(policyRecord.getStringValue(PolicyInquiryFields.POL_TYPE_CODE, ""));
        policyDetailType.setPolicyTypeCode(policyTypeCodeType);
        policyDetailType.setPolicyPhaseCode(policyRecord.getStringValue(PolicyInquiryFields.POLICY_PHASE_CODE, ""));
        policyDetailType.setBinderEndDate(DateUtils.parseOasisDateToXMLDate(policyRecord.getStringValue(PolicyInquiryFields.BINDER_EFFECTIVE_TO_DATE, "")));

        ClaimMadeLiabilityPolicyInformationType claimMadeLiabilityPolicyInformation = new ClaimMadeLiabilityPolicyInformationType();
        claimMadeLiabilityPolicyInformation.setCurrentRetroactiveDate(DateUtils.parseOasisDateToXMLDate(policyRecord.getStringValue(PolicyInquiryFields.POLICY_RETRO_DATE, "")));
        policyDetailType.setClaimMadeLiabilityPolicyInformation(claimMadeLiabilityPolicyInformation);

        policyDetailType.setShortTermIndicator(YesNoEmptyFlag.getInstance(policyRecord.getStringValue(PolicyInquiryFields.SHORT_TERM_B, "")).trueFalseEmptyValue());

        PolicyFormCodeType policyFormCodeType = new PolicyFormCodeType();
        policyFormCodeType.setValue(policyRecord.getStringValue(PolicyInquiryFields.POLICY_FORM_CODE, ""));
        policyDetailType.setPolicyFormCode(policyFormCodeType);

        PolicyStatusCodeType policyStatusCodeType = new PolicyStatusCodeType();
        policyStatusCodeType.setValue(policyRecord.getStringValue(PolicyInquiryFields.POLICY_STATUS, ""));
        policyDetailType.setPolicyStatusCode(policyStatusCodeType);

        PolicyCycleCodeType policyCycleCodeType = new PolicyCycleCodeType();
        policyCycleCodeType.setValue(policyRecord.getStringValue(PolicyInquiryFields.POLICY_CYCLE, ""));
        policyDetailType.setPolicyCycleCode(policyCycleCodeType);

        QuoteCycleCodeType quoteCycleCodeType = new QuoteCycleCodeType();
        quoteCycleCodeType.setValue(policyRecord.getStringValue(PolicyInquiryFields.QUOTE_CYCLE, ""));
        policyDetailType.setQuoteCycleCode(quoteCycleCodeType);

        policyDetailType.setPolicyLayerCode(policyRecord.getStringValue(PolicyInquiryFields.POLICY_LAYER_CODE, ""));

        PriorPolicyType priorPolicyType = new PriorPolicyType();
        priorPolicyType.setPolicyId(policyRecord.getStringValue(PolicyInquiryFields.LEGACY_POLICY_NO, ""));
        policyDetailType.setPriorPolicy(priorPolicyType);

        policyDetailType.setGuaranteeDate(DateUtils.parseOasisDateToXMLDate(policyRecord.getStringValue(PolicyInquiryFields.GUARANTEE_DATE, "")));
        policyDetailType.setDeclinationDate(DateUtils.parseOasisDateToXMLDate(policyRecord.getStringValue(PolicyInquiryFields.DECLINATION_DATE, "")));
        policyDetailType.setIbnrDate(DateUtils.parseOasisDateToXMLDate(policyRecord.getStringValue(PolicyInquiryFields.ROLLING_IBNR_DATE, "")));

        policyDetailType.setIssueCompany(getIssueCompany(policyRecord, partyList));

        policyDetailType.setOrganizationType(policyRecord.getStringValue(PolicyInquiryFields.ORGANIZATION_TYPE_CODE, ""));
        policyDetailType.setBinderIndicator(YesNoEmptyFlag.getInstance(policyRecord.getStringValue(PolicyInquiryFields.BINDER_B, "")).trueFalseEmptyValue());
        policyDetailType.setCollateralIndicator(YesNoEmptyFlag.getInstance(policyRecord.getStringValue(PolicyInquiryFields.COLLATERAL_B, "")).trueFalseEmptyValue());
        policyDetailType.setInsuredByCompanyIndicator(YesNoEmptyFlag.getInstance(policyRecord.getStringValue(PolicyInquiryFields.COMPANY_INSURED_B, "")).trueFalseEmptyValue());
        policyDetailType.setProgramCode(policyRecord.getStringValue(PolicyInquiryFields.PROGRAM_CODE, ""));
        policyDetailType.setCategoryCode(policyRecord.getStringValue(PolicyInquiryFields.CATEGORY_CODE, ""));
        policyDetailType.setHospitalTier(policyRecord.getStringValue(PolicyInquiryFields.HOSPITAL_TIER, ""));
        policyDetailType.setClaimsMadeYear(policyRecord.getStringValue(PolicyInquiryFields.CM_YEAR, ""));
        policyDetailType.setPeerGroupCode(policyRecord.getStringValue(PolicyInquiryFields.PEER_GROUP_CODE, ""));
        policyDetailType.setFirstPotentialCancelDate(DateUtils.parseOasisDateToXMLDate(policyRecord.getStringValue(PolicyInquiryFields.FIRST_POTENTIAL_CANC, "")));
        policyDetailType.setSecondPotentialCancelDate(DateUtils.parseOasisDateToXMLDate(policyRecord.getStringValue(PolicyInquiryFields.SECOND_POTENTIAL_CANC, "")));
        policyDetailType.setPlAggregatCode(policyRecord.getStringValue(PolicyInquiryFields.PL_AGGREGATE, ""));
        policyDetailType.setGlAggregateCode(policyRecord.getStringValue(PolicyInquiryFields.GL_AGGREGATE, ""));

        policyDetailType.setAdditionalInformation(getAdditionalPolicyInformation(policyRecord));
        policyDetailType.setAdditionalRatingInformation(getAdditionalRatingInformation(policyRecord));
        policyDetailType.setExposureInformation(getExposureInformation(policyRecord));
        policyDetailType.setPolicyVersionDetail(getPolicyVersionDetail(policyRecord));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPolicyDetail", policyDetailType);
        }

        return policyDetailType;
    }

    public IssueCompanyType getIssueCompany(Record policyRecord, List<String> partyList) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getIssueCompany", new Object[]{policyRecord, partyList});
        }

        IssueCompanyType issueCompanyType = null;
        String issueCompany = policyRecord.getStringValue(PolicyInquiryFields.ISS_COMPANY_ENTITY_FK, "");
        cacheClientId(partyList, issueCompany);

        issueCompanyType = new IssueCompanyType();
        ReferredPartyType referredPartyType = new ReferredPartyType();
        referredPartyType.setPartyNumberId(issueCompany);
        issueCompanyType.setReferredParty(referredPartyType);

        ControllingStateOrProvinceCodeType controllingStateOrProvinceCodeType = new ControllingStateOrProvinceCodeType();
        controllingStateOrProvinceCodeType.setValue(policyRecord.getStringValue(PolicyInquiryFields.ISS_STATE_CODE, ""));
        issueCompanyType.setControllingStateOrProvinceCode(controllingStateOrProvinceCodeType);

        ProcessLocationCodeType processLocationCodeType = new ProcessLocationCodeType();
        processLocationCodeType.setValue(policyRecord.getStringValue(PolicyInquiryFields.PROCESS_LOCATION_CODE, ""));
        issueCompanyType.setProcessLocationCode(processLocationCodeType);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getIssueCompany", issueCompanyType);
        }

        return issueCompanyType;
    }

    public AdditionalInformationType getAdditionalPolicyInformation(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAdditionalPolicyInformation", new Object[]{record});
        }

        AdditionalInformationType additionalInformationType = new AdditionalInformationType();

        AdditionalDataType additionalDataType1 = new AdditionalDataType();
        additionalDataType1.setValue(record.getStringValue(PolicyInquiryFields.CHAR1, ""));
        additionalDataType1.setKey("1");
        additionalInformationType.getAdditionalData().add(additionalDataType1);
        AdditionalDataType additionalDataType2 = new AdditionalDataType();
        additionalDataType2.setValue(record.getStringValue(PolicyInquiryFields.CHAR2, ""));
        additionalDataType2.setKey("2");
        additionalInformationType.getAdditionalData().add(additionalDataType2);
        AdditionalDataType additionalDataType3 = new AdditionalDataType();
        additionalDataType3.setValue(record.getStringValue(PolicyInquiryFields.CHAR3, ""));
        additionalDataType3.setKey("3");
        additionalInformationType.getAdditionalData().add(additionalDataType3);
        AdditionalDateTimeType additionalDateTimeType1 = new AdditionalDateTimeType();
        additionalDateTimeType1.setValue(DateUtils.parseOasisDateToXMLDate(record.getStringValue(PolicyInquiryFields.DATE1, "")));
        additionalDateTimeType1.setKey("1");
        additionalInformationType.getAdditionalDateTime().add(additionalDateTimeType1);
        AdditionalDateTimeType additionalDateTimeType2 = new AdditionalDateTimeType();
        additionalDateTimeType2.setValue(DateUtils.parseOasisDateToXMLDate(record.getStringValue(PolicyInquiryFields.DATE2, "")));
        additionalDateTimeType2.setKey("2");
        additionalInformationType.getAdditionalDateTime().add(additionalDateTimeType2);
        AdditionalDateTimeType additionalDateTimeType3 = new AdditionalDateTimeType();
        additionalDateTimeType3.setValue(DateUtils.parseOasisDateToXMLDate(record.getStringValue(PolicyInquiryFields.DATE3, "")));
        additionalDateTimeType3.setKey("3");
        additionalInformationType.getAdditionalDateTime().add(additionalDateTimeType3);
        AdditionalNumberType additionalNumberType1 = new AdditionalNumberType();
        additionalNumberType1.setValue(record.getStringValue(PolicyInquiryFields.NUM1, ""));
        additionalNumberType1.setKey("1");
        additionalInformationType.getAdditionalNumber().add(additionalNumberType1);
        AdditionalNumberType additionalNumberType2 = new AdditionalNumberType();
        additionalNumberType2.setValue(record.getStringValue(PolicyInquiryFields.NUM2, ""));
        additionalNumberType2.setKey("2");
        additionalInformationType.getAdditionalNumber().add(additionalNumberType2);
        AdditionalNumberType additionalNumberType3 = new AdditionalNumberType();
        additionalNumberType3.setValue(record.getStringValue(PolicyInquiryFields.NUM3, ""));
        additionalNumberType3.setKey("3");
        additionalInformationType.getAdditionalNumber().add(additionalNumberType3);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAdditionalPolicyInformation", additionalInformationType);
        }

        return additionalInformationType;
    }

    private AdditionalRatingInformationType getAdditionalRatingInformation(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAdditionalRatingInformation", new Object[]{record});
        }

        AdditionalRatingInformationType additionalRatingInformation = new AdditionalRatingInformationType();
        additionalRatingInformation.setMethodCode(record.getStringValue(PolicyInquiryFields.RATING_METHOD, ""));
        additionalRatingInformation.setDeviationPercent(record.getStringValue(PolicyInquiryFields.RATING_DEVIATION, ""));
        additionalRatingInformation.setDiscoveryPeriodCode(record.getStringValue(PolicyInquiryFields.DISCOVERY_PERIOD_RATING, ""));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAdditionalRatingInformation", additionalRatingInformation);
        }

        return additionalRatingInformation;
    }

    private ExposureInformationType getExposureInformation(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getExposureInformation", new Object[]{record});
        }

        ExposureInformationType exposureInformation = new ExposureInformationType();
        exposureInformation.setDoctorCount(record.getStringValue(PolicyInquiryFields.NUMBER_OF_PHYSICIANS, ""));
        exposureInformation.setEmployeeCount(record.getStringValue(PolicyInquiryFields.NUMBER_OF_EMPLOYEES, ""));
        exposureInformation.setFormOfBusiness(record.getStringValue(PolicyInquiryFields.FORM_OF_BUSINESS, ""));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getExposureInformation", exposureInformation);
        }

        return exposureInformation;
    }

    public PolicyVersionDetailType getPolicyVersionDetail(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPolicyVersionDetail", new Object[]{record});
        }

        PolicyVersionDetailType policyVersionDetail = new PolicyVersionDetailType();
        policyVersionDetail.setVersionModeCode(record.getStringValue(PolicyInquiryFields.RECORD_MODE_CODE, ""));
        policyVersionDetail.setParentVersionNumberId(record.getStringValue(PolicyInquiryFields.OFFICIAL_RECORD_ID, ""));
        policyVersionDetail.setAfterImageIndicator(YesNoEmptyFlag.N.trueFalseEmptyValue());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPolicyVersionDetail", policyVersionDetail);
        }

        return policyVersionDetail;
    }

    public void setInsured(List<InsuredType> insureds, Record policyRec, RecordSet riskRs, List<String> partyList, RecordSet riskAddtlExpRs) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setInsured", new Object[]{policyRec, riskRs, partyList, riskAddtlExpRs});
        }

        Iterator it = riskRs.getRecords();
        String prevInsuredNumberId = null;
        String preInsuredNumberIdForInsAddlExp = null;
        InsuredType insured = null;
        while (it.hasNext()) {
            Record insRec = (Record)it.next();

            String insuredNumberId = insRec.getStringValue(RiskInquiryFields.RISK_NUMBER_ID);
            if (prevInsuredNumberId==null || !insuredNumberId.equalsIgnoreCase(prevInsuredNumberId)) {
                prevInsuredNumberId = insuredNumberId;
                insured = new InsuredType();
                insured.setInsuredNumberId(insuredNumberId);
                insured.setKey(insuredNumberId);
                ReferredPartyType referredParty = new ReferredPartyType();
                String partyId = insRec.getStringValue(RiskInquiryFields.ENTITY_ID);
                cacheClientId(partyList, partyId);
                referredParty.setPartyNumberId(partyId);
                insured.setReferredParty(referredParty);

                insureds.add(insured);
            }

            setInsuredVersion(insured, insRec);
            InsuredAdditionalExposureType insuredAdditionalExposureType =
                setInsuredAdditionalExposure(insured, insRec, riskAddtlExpRs, preInsuredNumberIdForInsAddlExp, insuredNumberId);
            insured.getInsuredAdditionalExposure().add(insuredAdditionalExposureType);
            preInsuredNumberIdForInsAddlExp = insuredNumberId;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setInsured");
        }
    }

    public void setCoverage(MedicalMalpracticeLineOfBusinessType medMalCoverages, RecordSet covgRs) {

        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setCoverage", new Object[]{covgRs});
        }

        Iterator it = covgRs.getRecords();
        List<MedicalMalpracticeCoverageType> coverages = medMalCoverages.getMedicalMalpracticeCoverage();

        String prevCovgNumberId = null;
        MedicalMalpracticeCoverageType medicalMalpracticeCoverage = null;
        while (it.hasNext()) {
            Record covgRec = (Record)it.next();

            String coverageNumberId = covgRec.getStringValue(CoverageInquiryFields.COVERAGE_NUMBER_ID);
            if (prevCovgNumberId==null || !coverageNumberId.equalsIgnoreCase(prevCovgNumberId)) {
                prevCovgNumberId = coverageNumberId;
                medicalMalpracticeCoverage = new MedicalMalpracticeCoverageType();
                medicalMalpracticeCoverage.setCoverageNumberId(coverageNumberId);
                medicalMalpracticeCoverage.setKey(coverageNumberId);
                MedicalMalpracticeCoverageCodeType medicalMalpracticeCoverageCode = new MedicalMalpracticeCoverageCodeType();
                medicalMalpracticeCoverageCode.setValue(covgRec.getStringValue(CoverageInquiryFields.PRODUCT_COVERAGE_CODE));
                medicalMalpracticeCoverage.setParentCoverageNumberId("");

                medicalMalpracticeCoverage.setMedicalMalpracticeCoverageCode(medicalMalpracticeCoverageCode);
                medicalMalpracticeCoverage.setPolicyFormCode(covgRec.getStringValue(CoverageInquiryFields.POLICY_FORM_CODE));

                ReferredInsuredType referredInsured = new ReferredInsuredType();
                String riskId = covgRec.getStringValue(CoverageInquiryFields.RISK_BASE_RECORD_ID);
                referredInsured.setInsuredReference(riskId);
                medicalMalpracticeCoverage.setReferredInsured(referredInsured);

                coverages.add(medicalMalpracticeCoverage);
            }

            setMedicalMalpracticeCoverageVersion(medicalMalpracticeCoverage, covgRec);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setCoverage");
        }
    }

    public void setCoverageClass(MedicalMalpracticeLineOfBusinessType medMalCoverages, RecordSet covgClsRs, RecordSet covgRs) {

        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setCoverageClass", new Object[]{covgClsRs});
        }

        Iterator it = covgClsRs.getRecords();
        List<MedicalMalpracticeCoverageType> coverages = medMalCoverages.getMedicalMalpracticeCoverage();

        String prevCovgClassNumberId = null;
        MedicalMalpracticeCoverageType medicalMalpracticeCoverage = null;
        while (it.hasNext()) {
            Record covgRec = (Record)it.next();
            String coverageClassNumberId = covgRec.getStringValue(CoverageInquiryFields.COVERAGE_CLASS_NUMBER_ID);
            String parentCovgNumberId = covgRec.getStringValue(CoverageInquiryFields.PARENT_COVERAGE_NUMBER_ID);
            if (prevCovgClassNumberId==null || !coverageClassNumberId.equalsIgnoreCase(prevCovgClassNumberId)) {
                prevCovgClassNumberId = coverageClassNumberId;
                medicalMalpracticeCoverage = new MedicalMalpracticeCoverageType();
                medicalMalpracticeCoverage.setCoverageNumberId(coverageClassNumberId);
                medicalMalpracticeCoverage.setKey(coverageClassNumberId);
                medicalMalpracticeCoverage.setParentCoverageNumberId(parentCovgNumberId);
                MedicalMalpracticeCoverageCodeType medicalMalpracticeCoverageCode = new MedicalMalpracticeCoverageCodeType();
                medicalMalpracticeCoverageCode.setValue(covgRec.getStringValue(CoverageInquiryFields.PRODUCT_COVERAGE_CLASS_CODE));
                medicalMalpracticeCoverage.setMedicalMalpracticeCoverageCode(medicalMalpracticeCoverageCode);

                ReferredInsuredType referredInsured = new ReferredInsuredType();
                Iterator cvgIt = covgRs.getRecords();
                String riskNumberId = null;
                while (cvgIt.hasNext()) {
                    Record currentCovgRec = (Record)cvgIt.next();
                    if (parentCovgNumberId.equalsIgnoreCase(currentCovgRec.getStringValue(CoverageInquiryFields.COVERAGE_NUMBER_ID))) {
                        riskNumberId = currentCovgRec.getStringValue(CoverageInquiryFields.RISK_BASE_RECORD_ID);
                        break;
                    }
                }
                referredInsured.setInsuredReference(riskNumberId);
                medicalMalpracticeCoverage.setReferredInsured(referredInsured);

                coverages.add(medicalMalpracticeCoverage);
            }

            setMedicalMalpracticeCoverageVersion(medicalMalpracticeCoverage, covgRec);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setCoverageClass");
        }
    }

    public void setInsuredVersion(InsuredType insured, Record insRec) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setInsuredVersion", new Object[]{insured, insRec});
        }

        InsuredVersionType insuredVersion = new InsuredVersionType();
        insuredVersion.setInsuredVersionNumberId(insRec.getStringValue(RiskInquiryFields.RISK_ID));
        insuredVersion.setPrimaryIndicator(YesNoEmptyFlag.getInstance(insRec.getStringValue(RiskInquiryFields.PRIMARY_RISK_B)).trueFalseEmptyValue());
        EffectivePeriodType effectivePeriod = new EffectivePeriodType();
        effectivePeriod.setStartDate(DateUtils.parseOasisDateToXMLDate(insRec.getStringValue(RiskInquiryFields.EFFECTIVE_FROM_DATE)));
        effectivePeriod.setEndDate(DateUtils.parseOasisDateToXMLDate(insRec.getStringValue(RiskInquiryFields.EFFECTIVE_TO_DATE)));
        insuredVersion.setEffectivePeriod(effectivePeriod);

        PracticeStateOrProvinceCodeType practiceStateOrProvinceCode = new PracticeStateOrProvinceCodeType();
        practiceStateOrProvinceCode.setValue(insRec.getStringValue(RiskInquiryFields.PRACTICE_STATE_CODE));
        insuredVersion.setPracticeStateOrProvinceCode(practiceStateOrProvinceCode);


        PracticeCountyCodeType practiceCountyCodeType = new PracticeCountyCodeType();
        practiceCountyCodeType.setValue(insRec.getStringValue(RiskInquiryFields.RISK_COUNTY, ""));
        insuredVersion.setPracticeCountyCode(practiceCountyCodeType);

        insuredVersion.setInsuredStatusCode(insRec.getStringValue(RiskInquiryFields.RISK_STATUS, ""));

        InsuredTypeCodeType insuredTypeCodeType = new InsuredTypeCodeType();
        insuredTypeCodeType.setValue(insRec.getStringValue(RiskInquiryFields.RISK_TYPE_CODE, ""));
        insuredVersion.setInsuredTypeCode(insuredTypeCodeType);

        InsuredClassCodeType insuredClassType = new InsuredClassCodeType();
        insuredClassType.setValue(insRec.getStringValue(RiskInquiryFields.RISK_CLASS_CODE, ""));
        insuredVersion.setInsuredClassCode(insuredClassType);

        PCFPracticeCountyCodeType pcfPracticeCountyCodeType = new PCFPracticeCountyCodeType();
        pcfPracticeCountyCodeType.setValue(insRec.getStringValue(RiskInquiryFields.PCF_RISK_COUNTY, ""));
        insuredVersion.setPCFPracticeCountyCode(pcfPracticeCountyCodeType);

        PCFInsuredClassCodeType pcfInsuredClassCodeType = new PCFInsuredClassCodeType();
        pcfInsuredClassCodeType.setValue(insRec.getStringValue(RiskInquiryFields.PCF_RISK_CLASS_CODE, ""));
        insuredVersion.setPCFInsuredClassCode(pcfInsuredClassCodeType);

        insuredVersion.setInsuredSubClassCode(insRec.getStringValue(RiskInquiryFields.RISK_SUB_CLASS_CODE, ""));
        insuredVersion.setInsuredAlternateSpecialtyCode(insRec.getStringValue(RiskInquiryFields.ALTERNATE_SPECIALTY_CODE, ""));
        insuredVersion.setInsuredClaimsDeductibleNumberId(insRec.getStringValue(RiskInquiryFields.RISK_DEDUCTIBLE_ID, ""));
        insuredVersion.setInsuredAlternateMethodCode(insRec.getStringValue(RiskInquiryFields.ALTERNATIVE_RATING_METHOD, ""));
        insuredVersion.setInsuredRevenueBandAmount(insRec.getStringValue(RiskInquiryFields.REVENUE_BAND, ""));
        insuredVersion.setInsuredRatingTier(insRec.getStringValue(RiskInquiryFields.RATING_TIER, ""));
        insuredVersion.setTeachingIndicator(YesNoEmptyFlag.getInstance(insRec.getStringValue(RiskInquiryFields.TEACHING_B, "")).trueFalseEmptyValue());
        insuredVersion.setInsuredProcedureCode(insRec.getStringValue(RiskInquiryFields.PROCEDURE_CODES, ""));
        insuredVersion.setInsuredMatureIndicator(YesNoEmptyFlag.getInstance(insRec.getStringValue(RiskInquiryFields.RATE_MATURE_B, "")).trueFalseEmptyValue());
        insuredVersion.setInsuredMoonlightingIndicator(YesNoEmptyFlag.getInstance(insRec.getStringValue(RiskInquiryFields.MOONLIGHTING_B, "")).trueFalseEmptyValue());
        insuredVersion.setClaimsMadeYear(insRec.getStringValue(RiskInquiryFields.CM_YEAR, ""));
        insuredVersion.setIbnrIndicator(YesNoEmptyFlag.getInstance(insRec.getStringValue(RiskInquiryFields.IBNR_B, "")).trueFalseEmptyValue());
        insuredVersion.setIbnrStatus(insRec.getStringValue(RiskInquiryFields.IBNR_STATUS, ""));
        insuredVersion.setScorecardEligibilityIndicator(YesNoEmptyFlag.getInstance(insRec.getStringValue(RiskInquiryFields.SCORECARD_B, "")).trueFalseEmptyValue());
        insuredVersion.setInsuredCityCode(insRec.getStringValue(RiskInquiryFields.CITY_CODE, ""));
        insuredVersion.setAdditionalNotes(insRec.getStringValue(RiskInquiryFields.NOTE, ""));

        FullTimeEquivalencyInformationType fullTimeEquivalencyInformation = getFullTimeEquivalencyInformation(insRec);
        insuredVersion.setFullTimeEquivalencyInformation(fullTimeEquivalencyInformation);

        MalpracticeLiabilityExposureInformationType malpracticeLiabilityExposureInformation = getMalpracticeLiabilityExposureInformation(insRec);
        insuredVersion.setMalpracticeLiabilityExposureInformation(malpracticeLiabilityExposureInformation);

        BuildingInformationType buildingInformation = getBuildingInformation(insRec);
        insuredVersion.setBuildingInformation(buildingInformation);

        VehiclesOperatedInformationType vehiclesOperatedInformation = getVehiclesOperatedInformation(insRec);
        insuredVersion.setVehiclesOperatedInformation(vehiclesOperatedInformation);

        AdditionalInformationType additionalInformation = getAdditionalRiskInformation(insRec);
        insuredVersion.setAdditionalInformation(additionalInformation);

        InsuredVersionDetailType insuredVersionDetail = getInsuredVersionDetail(insRec);
        insuredVersion.setInsuredVersionDetail(insuredVersionDetail);

        insured.getInsuredVersion().add(insuredVersion);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setInsuredVersion", insuredVersion);
        }

    }

    public FullTimeEquivalencyInformationType getFullTimeEquivalencyInformation(Record insuredRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getFullTimeEquivalencyInformation", new Object[]{insuredRecord});
        }

        FullTimeEquivalencyInformationType fullTimeEquivalencyInformation = new FullTimeEquivalencyInformationType();
        fullTimeEquivalencyInformation.setFullTimeEquivalency(insuredRecord.getStringValue(RiskInquiryFields.FTE_EQUIVALENT, ""));
        fullTimeEquivalencyInformation.setFullTimeHours(insuredRecord.getStringValue(RiskInquiryFields.FTE_FULL_TIME, ""));
        fullTimeEquivalencyInformation.setPartTimeHours(insuredRecord.getStringValue(RiskInquiryFields.FTE_PART_TIME, ""));
        fullTimeEquivalencyInformation.setPerDiemHours(insuredRecord.getStringValue(RiskInquiryFields.FTE_PER_DIEM, ""));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getFullTimeEquivalencyInformation", fullTimeEquivalencyInformation);
        }

        return fullTimeEquivalencyInformation;
    }

    public MalpracticeLiabilityExposureInformationType getMalpracticeLiabilityExposureInformation(Record insuredRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getMalpracticeLiabilityExposureInformation", new Object[]{insuredRecord});
        }

        MalpracticeLiabilityExposureInformationType malpracticeLiabilityExposureInformation = new MalpracticeLiabilityExposureInformationType();
        malpracticeLiabilityExposureInformation.setExposureUnit(insuredRecord.getStringValue(RiskInquiryFields.EXPOSURE_UNIT, ""));
        malpracticeLiabilityExposureInformation.setExposureBasisCode(insuredRecord.getStringValue(RiskInquiryFields.EXPOSURE_BASIS, ""));
        malpracticeLiabilityExposureInformation.setDoctorCount(insuredRecord.getStringValue(RiskInquiryFields.NUMBER_OF_EMPLOYED_DOCTOR, ""));
        malpracticeLiabilityExposureInformation.setSquareFootage(insuredRecord.getStringValue(RiskInquiryFields.SQUARE_FOOTAGE, ""));
        malpracticeLiabilityExposureInformation.setVapCount(insuredRecord.getStringValue(RiskInquiryFields.NUMBER_VAP, ""));
        malpracticeLiabilityExposureInformation.setBedCount(insuredRecord.getStringValue(RiskInquiryFields.NUMBER_BED, ""));
        malpracticeLiabilityExposureInformation.setExtendedBedCount(insuredRecord.getStringValue(RiskInquiryFields.NUMBER_EXT_BED, ""));
        malpracticeLiabilityExposureInformation.setSkillBedCount(insuredRecord.getStringValue(RiskInquiryFields.NUMBER_SKILL_BED, ""));
        malpracticeLiabilityExposureInformation.setCensusCount(insuredRecord.getStringValue(RiskInquiryFields.AVERAGE_DAILY_CENSUS, ""));
        malpracticeLiabilityExposureInformation.setOutpatientVisitCount(insuredRecord.getStringValue(RiskInquiryFields.ANNUAL_PATIENT_VISIT, ""));
        malpracticeLiabilityExposureInformation.setDeliveryCount(insuredRecord.getStringValue(RiskInquiryFields.NUMBER_QB_DELIVERY, ""));
        malpracticeLiabilityExposureInformation.setImpatientSurgeryCount(insuredRecord.getStringValue(RiskInquiryFields.NUMBER_IMPATIENT_SURG, ""));
        malpracticeLiabilityExposureInformation.setOutpatientVisitCount(insuredRecord.getStringValue(RiskInquiryFields.ANNUAL_OUTPATIENT_VISIT, ""));
        malpracticeLiabilityExposureInformation.setEmergencyRoomVisitCount(insuredRecord.getStringValue(RiskInquiryFields.NUMBER_ER_VISIT, ""));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getMalpracticeLiabilityExposureInformation", malpracticeLiabilityExposureInformation);
        }

        return malpracticeLiabilityExposureInformation;
    }

    public BuildingInformationType getBuildingInformation(Record insuredRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getBuildingInformation", new Object[]{insuredRecord});
        }

        BuildingInformationType buildingInformation = new BuildingInformationType();
        buildingInformation.setBuildingClassCode(insuredRecord.getStringValue(RiskInquiryFields.BUILDING_CLASS, ""));
        buildingInformation.setBuildingValue(insuredRecord.getStringValue(RiskInquiryFields.BUILDING_VALUE, ""));
        buildingInformation.setBuildingTypeCode(insuredRecord.getStringValue(RiskInquiryFields.BUILDING_TYPE, ""));
        buildingInformation.setBuildingUseTypeCode(insuredRecord.getStringValue(RiskInquiryFields.USE_TYPE, ""));
        buildingInformation.setFrameTypeCode(insuredRecord.getStringValue(RiskInquiryFields.FRAME_TYPE, ""));
        buildingInformation.setProtectionClassCode(insuredRecord.getStringValue(RiskInquiryFields.PROTECTION_CLASS, ""));
        buildingInformation.setSprinklerIndicator(YesNoEmptyFlag.getInstance(insuredRecord.getStringValue(RiskInquiryFields.SPRINKLER_B, "")).trueFalseEmptyValue());
        buildingInformation.setConstructionTypeCode(insuredRecord.getStringValue(RiskInquiryFields.CONSTRUCTION_TYPE, ""));
        buildingInformation.setRoofTypeCode(insuredRecord.getStringValue(RiskInquiryFields.ROOF_TYPE, ""));
        buildingInformation.setFloorTypeCode(insuredRecord.getStringValue(RiskInquiryFields.FLOOR_TYPE, ""));
        buildingInformation.setProtectionTypeCode(insuredRecord.getStringValue(RiskInquiryFields.PROTECTION_TYPE, ""));
        buildingInformation.setFireServiceTypeCode(insuredRecord.getStringValue(RiskInquiryFields.FIRE_SERVICE_TYPE, ""));
        buildingInformation.setHydrantTypeCode(insuredRecord.getStringValue(RiskInquiryFields.HYDRANTS_TYPE, ""));
        buildingInformation.setSecurityTypeCode(insuredRecord.getStringValue(RiskInquiryFields.SECURITY_TYPE, ""));
        buildingInformation.setLocationCode(insuredRecord.getStringValue(RiskInquiryFields.LOCATION, ""));
        buildingInformation.setLocationDescription(insuredRecord.getStringValue(RiskInquiryFields.LOCATION_DESCRIPTION, ""));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getBuildingInformation", buildingInformation);
        }

        return buildingInformation;
    }

    public VehiclesOperatedInformationType getVehiclesOperatedInformation(Record insuredRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getVehiclesOperatedInformation", new Object[]{insuredRecord});
        }

        VehiclesOperatedInformationType vehiclesOperatedInformation = new VehiclesOperatedInformationType();
        vehiclesOperatedInformation.setFleetIndicator(YesNoEmptyFlag.getInstance(insuredRecord.getStringValue(RiskInquiryFields.FLEET_B, "")).trueFalseEmptyValue());
        vehiclesOperatedInformation.setVehicleManufacturerCode(insuredRecord.getStringValue(RiskInquiryFields.MAKE_OF_VEHICLE, ""));
        vehiclesOperatedInformation.setVehicleManufacturerSubclassCode(insuredRecord.getStringValue(RiskInquiryFields.VEHICLE_SUBCLASS, ""));
        vehiclesOperatedInformation.setVehicleModelCode(insuredRecord.getStringValue(RiskInquiryFields.MODEL_OF_VEHICLE, ""));
        vehiclesOperatedInformation.setVehicleYear(insuredRecord.getStringValue(RiskInquiryFields.YEAR_OF_VEHICLE, ""));
        vehiclesOperatedInformation.setVehicleOriginalCost(insuredRecord.getStringValue(RiskInquiryFields.ORIGINAL_COST_NEW, ""));
        vehiclesOperatedInformation.setVehicleVin(insuredRecord.getStringValue(RiskInquiryFields.VIN, ""));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getVehiclesOperatedInformation", vehiclesOperatedInformation);
        }

        return vehiclesOperatedInformation;
    }

    public AdditionalInformationType getAdditionalRiskInformation(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAdditionalRiskInformation", new Object[]{record});
        }

        AdditionalInformationType additionalInformationType = null;
        additionalInformationType = new AdditionalInformationType();

        AdditionalDataType additionalDataType1 = new AdditionalDataType();
        additionalDataType1.setValue(record.getStringValue(RiskInquiryFields.CHAR1, ""));
        additionalDataType1.setKey("1");
        additionalInformationType.getAdditionalData().add(additionalDataType1);
        AdditionalDataType additionalDataType2 = new AdditionalDataType();
        additionalDataType2.setValue(record.getStringValue(RiskInquiryFields.CHAR2, ""));
        additionalDataType2.setKey("2");
        additionalInformationType.getAdditionalData().add(additionalDataType2);
        AdditionalDataType additionalDataType3 = new AdditionalDataType();
        additionalDataType3.setValue(record.getStringValue(RiskInquiryFields.CHAR3, ""));
        additionalDataType3.setKey("3");
        additionalInformationType.getAdditionalData().add(additionalDataType3);
        AdditionalDataType additionalDataType4 = new AdditionalDataType();
        additionalDataType4.setValue(record.getStringValue(RiskInquiryFields.CHAR4, ""));
        additionalDataType4.setKey("4");
        additionalInformationType.getAdditionalData().add(additionalDataType4);
        AdditionalDateTimeType additionalDateTimeType1 = new AdditionalDateTimeType();
        additionalDateTimeType1.setValue(DateUtils.parseOasisDateToXMLDate(record.getStringValue(RiskInquiryFields.DATE1, "")));
        additionalDateTimeType1.setKey("1");
        additionalInformationType.getAdditionalDateTime().add(additionalDateTimeType1);
        AdditionalDateTimeType additionalDateTimeType2 = new AdditionalDateTimeType();
        additionalDateTimeType2.setValue(DateUtils.parseOasisDateToXMLDate(record.getStringValue(RiskInquiryFields.DATE2, "")));
        additionalDateTimeType2.setKey("2");
        additionalInformationType.getAdditionalDateTime().add(additionalDateTimeType2);
        AdditionalDateTimeType additionalDateTimeType3 = new AdditionalDateTimeType();
        additionalDateTimeType3.setValue(DateUtils.parseOasisDateToXMLDate(record.getStringValue(RiskInquiryFields.DATE3, "")));
        additionalDateTimeType3.setKey("3");
        additionalInformationType.getAdditionalDateTime().add(additionalDateTimeType3);
        AdditionalDateTimeType additionalDateTimeType4 = new AdditionalDateTimeType();
        additionalDateTimeType4.setValue(DateUtils.parseOasisDateToXMLDate(record.getStringValue(RiskInquiryFields.DATE4, "")));
        additionalDateTimeType4.setKey("4");
        additionalInformationType.getAdditionalDateTime().add(additionalDateTimeType4);
        AdditionalNumberType additionalNumberType1 = new AdditionalNumberType();
        additionalNumberType1.setValue(record.getStringValue(RiskInquiryFields.NUM1, ""));
        additionalNumberType1.setKey("1");
        additionalInformationType.getAdditionalNumber().add(additionalNumberType1);
        AdditionalNumberType additionalNumberType2 = new AdditionalNumberType();
        additionalNumberType2.setValue(record.getStringValue(RiskInquiryFields.NUM2, ""));
        additionalNumberType2.setKey("2");
        additionalInformationType.getAdditionalNumber().add(additionalNumberType2);
        AdditionalNumberType additionalNumberType3 = new AdditionalNumberType();
        additionalNumberType3.setValue(record.getStringValue(RiskInquiryFields.NUM3, ""));
        additionalNumberType3.setKey("3");
        additionalInformationType.getAdditionalNumber().add(additionalNumberType3);
        AdditionalNumberType additionalNumberType4 = new AdditionalNumberType();
        additionalNumberType4.setValue(record.getStringValue(RiskInquiryFields.NUM4, ""));
        additionalNumberType4.setKey("4");
        additionalInformationType.getAdditionalNumber().add(additionalNumberType4);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAdditionalRiskInformation", additionalInformationType);
        }

        return additionalInformationType;
    }

    public InsuredVersionDetailType getInsuredVersionDetail(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInsuredVersionDetail", new Object[]{record});
        }

        InsuredVersionDetailType insuredVersionDetail = new InsuredVersionDetailType();
        insuredVersionDetail.setVersionModeCode(record.getStringValue(RiskInquiryFields.RECORD_MODE_CODE, ""));
        insuredVersionDetail.setParentVersionNumberId(record.getStringValue(RiskInquiryFields.OFFICIAL_RECORD_ID, ""));
        insuredVersionDetail.setAfterImageIndicator(YesNoEmptyFlag.getInstance(record.getStringValue(RiskInquiryFields.AFTER_IMAGE_RECORD_B, "")).trueFalseEmptyValue());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInsuredVersionDetail", insuredVersionDetail);
        }

        return insuredVersionDetail;
    }


    public void setMedicalMalpracticeCoverageVersion(MedicalMalpracticeCoverageType medicalMalpracticeCoverage, Record covgRec) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setMedicalMalpracticeCoverageVersion", new Object[]{medicalMalpracticeCoverage, covgRec});
        }

        MedicalMalpracticeCoverageVersionType medicalMalpracticeCoverageVersion = new MedicalMalpracticeCoverageVersionType();
        if (covgRec.hasStringValue(CoverageInquiryFields.COVERAGE_ID)) {
            medicalMalpracticeCoverageVersion.setMedicalMalpracticeCoverageVersionId(covgRec.getStringValue(CoverageInquiryFields.COVERAGE_ID, ""));
        }
        else {
            medicalMalpracticeCoverageVersion.setMedicalMalpracticeCoverageVersionId(covgRec.getStringValue(CoverageInquiryFields.COVERAGE_CLASS_ID, ""));
        }
        if (covgRec.hasStringValue(CoverageInquiryFields.PRIMARY_COVERAGE_INDICATOR)) {
            medicalMalpracticeCoverageVersion.setPrimaryIndicator(YesNoEmptyFlag.getInstance(covgRec.getStringValue(CoverageInquiryFields.PRIMARY_COVERAGE_INDICATOR, "")).trueFalseEmptyValue());
        }
        else {
            medicalMalpracticeCoverageVersion.setPrimaryIndicator(YesNoEmptyFlag.getInstance("N").trueFalseEmptyValue());
        }
        if (covgRec.hasField(CoverageInquiryFields.PARENT_COVERAGE_NUMBER_ID)
            && covgRec.hasStringValue(CoverageInquiryFields.PARENT_COVERAGE_NUMBER_ID)) {
            medicalMalpracticeCoverageVersion.setMedicalMalpracticeCoverageStatusCode(covgRec.getStringValue(CoverageInquiryFields.COVERAGE_CLASS_STATUS, ""));
        }
        else {
            medicalMalpracticeCoverageVersion.setMedicalMalpracticeCoverageStatusCode(covgRec.getStringValue(CoverageInquiryFields.COVERAGE_STATUS, ""));
        }
        medicalMalpracticeCoverageVersion.setEffectivePeriod(getCoverageEffectivePeriod(medicalMalpracticeCoverageVersion, covgRec));
        medicalMalpracticeCoverageVersion.setLimit(getLimit(medicalMalpracticeCoverageVersion, covgRec));
        medicalMalpracticeCoverageVersion.setClaimMadeLiabilityPolicyInformation(getClaimMadeLiabilityPolicyInformation(medicalMalpracticeCoverageVersion, covgRec));
        medicalMalpracticeCoverageVersion.setPayorCode(covgRec.getStringValue(CoverageInquiryFields.RATE_PAYOR_DEPEND_CODE, ""));
        medicalMalpracticeCoverageVersion.setCancellationMethodCode(covgRec.getStringValue(CoverageInquiryFields.CANCELLATION_METHOD_CODE, ""));
        medicalMalpracticeCoverageVersion.setAnnualBaseRate(covgRec.getStringValue(CoverageInquiryFields.ANNUAL_BASE_RATE, ""));
        medicalMalpracticeCoverageVersion.setDefaultAmountOfInsurance(covgRec.getStringValue(CoverageInquiryFields.DEFAULT_AMOUNT_OF_INSURANCE, ""));
        medicalMalpracticeCoverageVersion.setAdditionalAmountOfInsurance(covgRec.getStringValue(CoverageInquiryFields.ADDL_AMOUNT_OF_INSURANCE, ""));
        medicalMalpracticeCoverageVersion.setLossOfIncomeDays(covgRec.getStringValue(CoverageInquiryFields.LOSS_OF_INCOME_DAYS, ""));
        medicalMalpracticeCoverageVersion.setExposureUnit(covgRec.getStringValue(CoverageInquiryFields.EXPOSURE_UNIT, ""));
        medicalMalpracticeCoverageVersion.setBuildingRate(covgRec.getStringValue(CoverageInquiryFields.BUILDING_RATE, ""));
        medicalMalpracticeCoverageVersion.setForecastIndicator(YesNoEmptyFlag.getInstance(covgRec.getStringValue(CoverageInquiryFields.USED_FOR_FORECAST_B, "")).trueFalseEmptyValue());
        medicalMalpracticeCoverageVersion.setDirectPrimaryIndicator(YesNoEmptyFlag.getInstance(covgRec.getStringValue(CoverageInquiryFields.DIRECT_PRIMARY_B, "")).trueFalseEmptyValue());
        medicalMalpracticeCoverageVersion.setAdditionalSymbolCode(covgRec.getStringValue(CoverageInquiryFields.SYMBOL, ""));

        CoverageConversionInformationType coverageConversionInformation = getCoverageConversionInformation(covgRec);
        medicalMalpracticeCoverageVersion.setCoverageConversionInformation(coverageConversionInformation);

        PcfType pcf = getPcf(covgRec);
        medicalMalpracticeCoverageVersion.setPcf(pcf);

        medicalMalpracticeCoverageVersion.setDeductible(covgRec.getStringValue(CoverageInquiryFields.DEDUCTIBLE_COMPONENT_ID, ""));
        medicalMalpracticeCoverageVersion.setManualDeductibleSIRCode(covgRec.getStringValue(CoverageInquiryFields.MANUAL_DED_SIR_CODE, ""));
        medicalMalpracticeCoverageVersion.setManualDeductibleSIRIncidentAmount(covgRec.getStringValue(CoverageInquiryFields.MANUAL_DED_SIR_INC_VALUE, ""));
        medicalMalpracticeCoverageVersion.setManualDeductibleSIRAggregateAmount(covgRec.getStringValue(CoverageInquiryFields.MANUAL_DED_SIR_AGG_VALUE, ""));
        medicalMalpracticeCoverageVersion.setDeductibleSIRIndemnityTypeCode(covgRec.getStringValue(CoverageInquiryFields.INDEMNITY_TYPE, ""));
        medicalMalpracticeCoverageVersion.setClaimProcessCode(covgRec.getStringValue(CoverageInquiryFields.CLAIM_PROCESS_CODE, ""));

        AdditionalInformationType additionalInformation = getAdditionalCoverageInformation(covgRec);
        medicalMalpracticeCoverageVersion.setAdditionalInformation(additionalInformation);

        MedicalMalpracticeCoverageVersionDetailType coverageVersionDetail = getMedicalMalpracticeCoverageVersionDetail(covgRec);
        medicalMalpracticeCoverageVersion.setMedicalMalpracticeCoverageVersionDetail(coverageVersionDetail);

        medicalMalpracticeCoverage.getMedicalMalpracticeCoverageVersion().add(medicalMalpracticeCoverageVersion);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setMedicalMalpracticeCoverageVersion");
        }
    }

    public EffectivePeriodType getCoverageEffectivePeriod(MedicalMalpracticeCoverageVersionType medicalMalpracticeCoverageVersion,  Record covgRec) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getCoverageEffectivePeriod", new Object[]{medicalMalpracticeCoverageVersion, covgRec});
        }

        EffectivePeriodType effectivePeriod = new EffectivePeriodType();
        if (covgRec.hasStringValue(CoverageInquiryFields.EFFECTIVE_FROM_DATE)) {
            effectivePeriod.setStartDate(DateUtils.parseOasisDateToXMLDate(covgRec.getStringValue(CoverageInquiryFields.EFFECTIVE_FROM_DATE, "")));
            effectivePeriod.setEndDate(DateUtils.parseOasisDateToXMLDate(covgRec.getStringValue(CoverageInquiryFields.EFFECTIVE_TO_DATE, "")));
        }
        else {
            effectivePeriod.setStartDate(DateUtils.parseOasisDateToXMLDate(covgRec.getStringValue(CoverageInquiryFields.CLASS_EFFECTIVE_FROM_DATE, "")));
            effectivePeriod.setEndDate(DateUtils.parseOasisDateToXMLDate(covgRec.getStringValue(CoverageInquiryFields.CLASS_EFFECTIVE_TO_DATE, "")));
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getCoverageEffectivePeriod", effectivePeriod);
        }

        return effectivePeriod;
    }

    public LimitType getLimit(MedicalMalpracticeCoverageVersionType medicalMalpracticeCoverageVersion,  Record covgRec) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getLimit", new Object[]{medicalMalpracticeCoverageVersion, covgRec});
        }

        LimitType limit = new LimitType();
        LimitTypeCodeType limitTypeCode = new LimitTypeCodeType();
        limitTypeCode.setValue(covgRec.getStringValue(CoverageInquiryFields.COVERAGE_LIMIT_CODE, ""));
        limit.setLimitTypeCode(limitTypeCode);
        limit.setSharedLimitIndicator(YesNoEmptyFlag.getInstance(covgRec.getStringValue(CoverageInquiryFields.SHARED_LIMIT_B, "")).trueFalseEmptyValue());
        IncidentLimitTypeCodeType incidentLimitTypeCode = new IncidentLimitTypeCodeType();
        incidentLimitTypeCode.setValue(covgRec.getStringValue(CoverageInquiryFields.INCIDENT_LIMIT, ""));
        limit.setIncidentLimitTypeCode(incidentLimitTypeCode);

        AgregateLimitTypeCodeType agregateLimitTypeCode = new AgregateLimitTypeCodeType();
        agregateLimitTypeCode.setValue(covgRec.getStringValue(CoverageInquiryFields.AGGREGATE_LIMIT, ""));
        limit.setAgregateLimitTypeCode(agregateLimitTypeCode);
        limit.setSubLimitIndicator(YesNoEmptyFlag.getInstance(covgRec.getStringValue(CoverageInquiryFields.PRODUCT_SUBLIMIT_B, "")).trueFalseEmptyValue());
        limit.setErosionTypeCode(covgRec.getStringValue(CoverageInquiryFields.LIMIT_EROSION_CODE, ""));

        ManualIncidentLimitTypeCodeType manualIncidentLimitTypeCode = new ManualIncidentLimitTypeCodeType();
        manualIncidentLimitTypeCode.setValue(covgRec.getStringValue(CoverageInquiryFields.MANUAL_INCIDENT_LIMIT, ""));
        limit.setManualIncidentLimitTypeCode(manualIncidentLimitTypeCode);

        ManualAggregateLimitTypeCodeType manualAggregateLimitTypeCode = new ManualAggregateLimitTypeCodeType();
        manualAggregateLimitTypeCode.setValue(covgRec.getStringValue(CoverageInquiryFields.MANUAL_AGGREGATE_LIMIT, ""));
        limit.setManualAggregateLimitTypeCode(manualAggregateLimitTypeCode);

        limit.setDailyLimitAmount(covgRec.getStringValue(CoverageInquiryFields.PER_DAY_LIMIT, ""));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getLimit", limit);
        }

        return limit;
    }


    public ClaimMadeLiabilityPolicyInformationType getClaimMadeLiabilityPolicyInformation(
        MedicalMalpracticeCoverageVersionType medicalMalpracticeCoverageVersion,  Record covgRec ) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getClaimMadeLiabilityPolicyInformation", new Object[]{medicalMalpracticeCoverageVersion, covgRec});
        }

        ClaimMadeLiabilityPolicyInformationType claimMadeLiabilityPolicyInformation = new ClaimMadeLiabilityPolicyInformationType();
        claimMadeLiabilityPolicyInformation.setCurrentRetroactiveDate(DateUtils.parseOasisDateToXMLDate(covgRec.getStringValue(CoverageInquiryFields.RETROACTIVE_DATE, "")));
        claimMadeLiabilityPolicyInformation.setSplitRetroactiveDate(DateUtils.parseOasisDateToXMLDate(covgRec.getStringValue(CoverageInquiryFields.SPLIT_RETROACTIVE_DATE, "")));
        claimMadeLiabilityPolicyInformation.setClaimsMadeDate(DateUtils.parseOasisDateToXMLDate(covgRec.getStringValue(CoverageInquiryFields.CLAIMS_MADE_DATE, "")));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getClaimMadeLiabilityPolicyInformation", claimMadeLiabilityPolicyInformation);
        }

        return claimMadeLiabilityPolicyInformation;
    }

    public CoverageConversionInformationType getCoverageConversionInformation (Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getCoverageConversionInformation", new Object[]{record});
        }

        CoverageConversionInformationType coverageConversionInformation = new CoverageConversionInformationType();
        coverageConversionInformation.setClaimsMadeDate(DateUtils.parseOasisDateToXMLDate(record.getStringValue(CoverageInquiryFields.CM_CONV_DATE, "")));
        coverageConversionInformation.setClaimsMadeOverrideDate(DateUtils.parseOasisDateToXMLDate(record.getStringValue(CoverageInquiryFields.CM_CONV_OVERRIDE_DATE, "")));
        coverageConversionInformation.setOccurenceDate(DateUtils.parseOasisDateToXMLDate(record.getStringValue(CoverageInquiryFields.OC_CONV_DATE, "")));
        coverageConversionInformation.setOccurenceOverrideDate(DateUtils.parseOasisDateToXMLDate(record.getStringValue(CoverageInquiryFields.OC_CONV_OVERRIDE_DATE, "")));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getCoverageConversionInformation", coverageConversionInformation);
        }

        return coverageConversionInformation;

    }

    public PcfType getPcf (Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPcf", new Object[]{record});
        }

        PcfType pcf = new PcfType();
        pcf.setPracticeCountyCode(record.getStringValue(CoverageInquiryFields.PCF_COUNTY_CODE, ""));
        pcf.setStartDate(DateUtils.parseOasisDateToXMLDate(record.getStringValue(CoverageInquiryFields.PCF_PARTICIPATION_DATE, "")));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPcf", pcf);
        }

        return pcf;
    }

    public AdditionalInformationType getAdditionalCoverageInformation (Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAdditionalInformation", new Object[]{record});
        }

        AdditionalInformationType additionalInformationType = new AdditionalInformationType();

        AdditionalDataType additionalDataType1 = new AdditionalDataType();
        additionalDataType1.setValue(record.getStringValue(PolicyInquiryFields.CHAR1, ""));
        additionalDataType1.setKey("1");
        additionalInformationType.getAdditionalData().add(additionalDataType1);

        AdditionalDataType additionalDataType2 = new AdditionalDataType();
        additionalDataType2.setValue(record.getStringValue(PolicyInquiryFields.CHAR2, ""));
        additionalDataType2.setKey("2");
        additionalInformationType.getAdditionalData().add(additionalDataType2);

        AdditionalDataType additionalDataType3 = new AdditionalDataType();
        additionalDataType3.setValue(record.getStringValue(PolicyInquiryFields.CHAR3, ""));
        additionalDataType3.setKey("3");
        additionalInformationType.getAdditionalData().add(additionalDataType3);

        AdditionalDateTimeType additionalDateTimeType1 = new AdditionalDateTimeType();
        additionalDateTimeType1.setValue(DateUtils.parseOasisDateToXMLDate(record.getStringValue(PolicyInquiryFields.DATE1, "")));
        additionalDateTimeType1.setKey("1");
        additionalInformationType.getAdditionalDateTime().add(additionalDateTimeType1);

        AdditionalDateTimeType additionalDateTimeType2 = new AdditionalDateTimeType();
        additionalDateTimeType2.setValue(DateUtils.parseOasisDateToXMLDate(record.getStringValue(PolicyInquiryFields.DATE2, "")));
        additionalDateTimeType2.setKey("2");
        additionalInformationType.getAdditionalDateTime().add(additionalDateTimeType2);

        AdditionalDateTimeType additionalDateTimeType3 = new AdditionalDateTimeType();
        additionalDateTimeType3.setValue(DateUtils.parseOasisDateToXMLDate(record.getStringValue(PolicyInquiryFields.DATE3, "")));
        additionalDateTimeType3.setKey("3");
        additionalInformationType.getAdditionalDateTime().add(additionalDateTimeType3);

        AdditionalNumberType additionalNumberType1 = new AdditionalNumberType();
        additionalNumberType1.setValue(record.getStringValue(PolicyInquiryFields.NUM1, ""));
        additionalNumberType1.setKey("1");
        additionalInformationType.getAdditionalNumber().add(additionalNumberType1);

        AdditionalNumberType additionalNumberType2 = new AdditionalNumberType();
        additionalNumberType2.setValue(record.getStringValue(PolicyInquiryFields.NUM2, ""));
        additionalNumberType2.setKey("2");
        additionalInformationType.getAdditionalNumber().add(additionalNumberType2);

        AdditionalNumberType additionalNumberType3 = new AdditionalNumberType();
        additionalNumberType3.setValue(record.getStringValue(PolicyInquiryFields.NUM3, ""));
        additionalNumberType3.setKey("3");
        additionalInformationType.getAdditionalNumber().add(additionalNumberType3);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAdditionalInformation", additionalInformationType);
        }

        return additionalInformationType;
    }

    public MedicalMalpracticeCoverageVersionDetailType getMedicalMalpracticeCoverageVersionDetail(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getMedicalMalpracticeCoverageVersionDetail", new Object[]{record});
        }

        MedicalMalpracticeCoverageVersionDetailType coverageVersionDetail = new MedicalMalpracticeCoverageVersionDetailType();
        coverageVersionDetail.setVersionModeCode(record.getStringValue(RiskInquiryFields.RECORD_MODE_CODE, ""));
        coverageVersionDetail.setParentVersionNumberId(record.getStringValue(RiskInquiryFields.OFFICIAL_RECORD_ID, ""));
        coverageVersionDetail.setAfterImageIndicator(YesNoEmptyFlag.getInstance(record.getStringValue(RiskInquiryFields.AFTER_IMAGE_RECORD_B, "")).trueFalseEmptyValue());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getMedicalMalpracticeCoverageVersionDetail", coverageVersionDetail);
        }

        return coverageVersionDetail;
    }

    public void setComponent(List<CreditSurchargeDeductibleType> components, RecordSet compRs, boolean policyCompFlag) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setComponent", new Object[]{components, compRs, policyCompFlag});
        }

        Iterator it = compRs.getRecords();
        Map<String, CreditSurchargeDeductibleType> componentMap = new HashMap<>();
        while (it.hasNext()) {
            Record compRec = (Record)it.next();

            String compNumberId = compRec.getStringValue(ComponentInquiryFields.COMPONENT_NUMBER_ID, "");
            if (!componentMap.containsKey(compNumberId)) {
                CreditSurchargeDeductibleType component = new CreditSurchargeDeductibleType();
                component.setCreditSurchargeDeductibleNumberId(compNumberId);
                component.setKey(compNumberId);

                ReferredMedicalMalpracticeCoverageType referredCoverage = new ReferredMedicalMalpracticeCoverageType();
                String coverageId = compRec.getStringValue(ComponentInquiryFields.COVERAGE_BASE_RECORD_ID, "");
                if (policyCompFlag) {
                    referredCoverage.setMedicalMalpracticeCoverageReference("");
                }
                else {
                    referredCoverage.setMedicalMalpracticeCoverageReference(coverageId);
                }

                component.setReferredMedicalMalpracticeCoverage(referredCoverage);
                component.setCreditSurchargeDeductibleCodeNumberId(compRec.getStringValue(ComponentInquiryFields.PRODUCT_COV_COMPONENT_ID, ""));

                CreditSurchargeDeductibleCodeType creditSurchargeDeductibleCode = new CreditSurchargeDeductibleCodeType();
                creditSurchargeDeductibleCode.setValue(compRec.getStringValue(ComponentInquiryFields.CODE, ""));
                component.setCreditSurchargeDeductibleCode(creditSurchargeDeductibleCode);

                CreditSurchargeDeductibleTypeCodeType creditSurchargeDeductibleTypeCode = new CreditSurchargeDeductibleTypeCodeType();
                creditSurchargeDeductibleTypeCode.setValue(compRec.getStringValue(ComponentInquiryFields.COMPONENT_TYPE_CODE, ""));
                component.setCreditSurchargeDeductibleTypeCode(creditSurchargeDeductibleTypeCode);

                components.add(component);
                componentMap.put(compNumberId, component);
            }

            setCreditSurchargeDeductibleVersion(componentMap.get(compNumberId), compRec, policyCompFlag);

        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setComponent");
        }
    }


    public void setCreditSurchargeDeductibleVersion (CreditSurchargeDeductibleType component, Record record, boolean policyCompFlag) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setCreditSurchargeDeductibleVersion", new Object[]{component, record, policyCompFlag});
        }

        CreditSurchargeDeductibleVersionType version = new CreditSurchargeDeductibleVersionType();

        version.setCreditSurchargeDeductibleVersionId(record.getStringValue(ComponentInquiryFields.POLICY_COV_COMPONENT_ID, ""));
        version.setEffectivePeriod(getComponentEffectivePeriod(record));
        version.setNumericValue(record.getStringValue(ComponentInquiryFields.COMPONENT_VALUE, ""));
        version.setIncidentDeductibleNumericValue(record.getStringValue(ComponentInquiryFields.INCIDENT_VALUE, ""));
        version.setAggregateDeductibleNumericValue(record.getStringValue(ComponentInquiryFields.AGGREGATE_VALUE, ""));
        version.setCycleDate(DateUtils.parseOasisDateToXMLDate(record.getStringValue(ComponentInquiryFields.COMPONENT_CYCLE_DATE, "")));
        version.setProrateIndicator(YesNoEmptyFlag.getInstance(record.getStringValue(ComponentInquiryFields.TO_PRORATE_B, "")).trueFalseEmptyValue());
        version.setClassificationCode(record.getStringValue(ComponentInquiryFields.CLASSIFICATION_CODE, ""));
        version.setAdditionalNotes(record.getStringValue(ComponentInquiryFields.NOTE, ""));
        version.setAdditionalInformation(getAddtionalComponentInformation(record, policyCompFlag));
        version.setCreditSurchargeDeductibleVersionDetail(getCreditSurchargeDeductibleVersionDetail(record));

        component.getCreditSurchargeDeductibleVersion().add(version);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setCreditSurchargeDeductibleVersion");
        }
    }

    public InsuredAdditionalExposureType setInsuredAdditionalExposure(InsuredType insured, Record insRec, RecordSet riskAddtlExpRs, String preInsuredNumberIdForInsAddlExp, String insuredNumberId) {
        Logger l = LogUtils.getLogger(getClass());
        if(l.isLoggable(Level.FINE)) {
            l.entering(getClass().getName(), "setInsuredAdditionalExposure", new Object[]{insured, insRec, riskAddtlExpRs, preInsuredNumberIdForInsAddlExp, insuredNumberId});
        }

        InsuredAdditionalExposureType insAddlExpType = null;
        if (riskAddtlExpRs != null) {
            RecordSet singleRiskAddtlExpRs = riskAddtlExpRs.getSubSet(new RecordFilter(RiskInquiryFields.RISK_NUMBER_ID, insuredNumberId));
            List<InsuredAdditionalExposureType> insAddlExpTypeList = new ArrayList<InsuredAdditionalExposureType>();
            Iterator<Record> it = singleRiskAddtlExpRs.getRecords();
            String preInsAddlExpBaseId = null;

            while (it.hasNext()) {
                Record record = it.next();
                String InsAddlExpBaseId = record.getStringValue(RiskInquiryFields.RISK_ADDTL_EXP_BASE_RECORD_ID, "");
                if (preInsAddlExpBaseId == null || !InsAddlExpBaseId.equalsIgnoreCase(preInsAddlExpBaseId)) {
                    if (preInsAddlExpBaseId != null) {
                        insAddlExpTypeList.add(insAddlExpType);
                    }
                    preInsAddlExpBaseId = InsAddlExpBaseId;
                    insAddlExpType = new InsuredAdditionalExposureType();
                    insAddlExpType.setInsuredAdditionalExposureNumberId(InsAddlExpBaseId);
                }

                InsuredAdditionalExposureVersionType insAddExpVerType = new InsuredAdditionalExposureVersionType();
                insAddExpVerType.setInsuredAdditionalExposureVersionNumberId(record.getStringValue(RiskInquiryFields.RISK_ADDTL_EXPOSURE_ID, ""));
                EffectivePeriodType effectivePeriodType = new EffectivePeriodType();
                effectivePeriodType.setStartDate(DateUtils.parseOasisDateToXMLDate(record.getStringValue(PolicyInquiryFields.EFFECTIVE_FROM_DATE, "")));
                effectivePeriodType.setEndDate(DateUtils.parseOasisDateToXMLDate(record.getStringValue(PolicyInquiryFields.EFFECTIVE_TO_DATE, "")));
                insAddExpVerType.setEffectivePeriod(effectivePeriodType);
                insAddExpVerType.setPracticeStateOrProvinceCode(record.getStringValue(RiskInquiryFields.PRACTICE_STATE_CODE, ""));
                insAddExpVerType.setProductCoverageCode(record.getStringValue(RiskInquiryFields.PRODUCT_COVERAGE_CODE, ""));
                insAddExpVerType.setCoverageLimitCode(record.getStringValue(RiskInquiryFields.COVERAGE_LIMIT_CODE, ""));
                insAddExpVerType.setPracticeCountyCode(record.getStringValue(RiskInquiryFields.RISK_COUNTY, ""));
                insAddExpVerType.setInsuredClassCode(record.getStringValue(RiskInquiryFields.RISK_CLASS_CODE, ""));
                insAddExpVerType.setPracticeValue(record.getStringValue(RiskInquiryFields.PERCENT_PRACTICE, ""));
                insAddExpVerType.setAddressNumberId(record.getStringValue(RiskInquiryFields.ADDRESS_Id, ""));
                AdditionalInformationType additionalInformationType = new AdditionalInformationType();
                AdditionalDataType dataType = new AdditionalDataType();
                dataType.setKey("1");
                dataType.setValue(record.getStringValue(RiskInquiryFields.CHAR1, ""));
                additionalInformationType.getAdditionalData().add(dataType);
                dataType = new AdditionalDataType();
                dataType.setKey("2");
                dataType.setValue(record.getStringValue(RiskInquiryFields.CHAR2, ""));
                additionalInformationType.getAdditionalData().add(dataType);
                dataType = new AdditionalDataType();
                dataType.setKey("3");
                dataType.setValue(record.getStringValue(RiskInquiryFields.CHAR3, ""));
                additionalInformationType.getAdditionalData().add(dataType);
                AdditionalDateTimeType dateTimeType = new AdditionalDateTimeType();
                dateTimeType.setKey("1");
                dateTimeType.setValue(DateUtils.parseOasisDateToXMLDate(record.getStringValue(RiskInquiryFields.DATE1, "")));
                additionalInformationType.getAdditionalDateTime().add(dateTimeType);
                dateTimeType = new AdditionalDateTimeType();
                dateTimeType.setKey("2");
                dateTimeType.setValue(DateUtils.parseOasisDateToXMLDate(record.getStringValue(RiskInquiryFields.DATE2, "")));
                additionalInformationType.getAdditionalDateTime().add(dateTimeType);
                dateTimeType = new AdditionalDateTimeType();
                dateTimeType.setKey("3");
                dateTimeType.setValue(DateUtils.parseOasisDateToXMLDate(record.getStringValue(RiskInquiryFields.DATE3, "")));
                additionalInformationType.getAdditionalDateTime().add(dateTimeType);
                AdditionalNumberType numberType = new AdditionalNumberType();
                numberType.setKey("1");
                numberType.setValue(record.getStringValue(RiskInquiryFields.NUM1, ""));
                additionalInformationType.getAdditionalNumber().add(numberType);
                numberType = new AdditionalNumberType();
                numberType.setKey("2");
                numberType.setValue(record.getStringValue(RiskInquiryFields.NUM2, ""));
                additionalInformationType.getAdditionalNumber().add(numberType);
                numberType = new AdditionalNumberType();
                numberType.setKey("3");
                numberType.setValue(record.getStringValue(RiskInquiryFields.NUM3, ""));
                additionalInformationType.getAdditionalNumber().add(numberType);
                insAddExpVerType.setAdditionalInformation(additionalInformationType);
                InsuredAdditionalExposureVersionDetailType insAddlExpVerDetType = new InsuredAdditionalExposureVersionDetailType();
                insAddlExpVerDetType.setAfterImageIndicator(record.getStringValue(RiskInquiryFields.AFTER_IMAGE_RECORD_B, ""));
                insAddlExpVerDetType.setParentVersionNumberId(record.getStringValue(RiskInquiryFields.OFFICIAL_RECORD_ID, ""));
                insAddlExpVerDetType.setVersionModeCode(record.getStringValue(RiskInquiryFields.RECORD_MODE_CODE, ""));
                insAddExpVerType.setInsuredAdditionalExposureVersionDetail(insAddlExpVerDetType);
                insAddlExpType.getInsuredAdditionalExposureVersion().add(insAddExpVerType);
                riskAddtlExpRs.removeRecord(record, false);
            }
            insured.getInsuredAdditionalExposure().addAll(insAddlExpTypeList);
        }

        if (l.isLoggable(Level.FINE)) {
            l.exiting(getClass().getName(), "setInsuredAdditionalExposure");
        }

        return insAddlExpType;
    }

    public EffectivePeriodType getComponentEffectivePeriod (Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getComponentEffectivePeriod", new Object[]{record});
        }

        EffectivePeriodType effectivePeriod= new EffectivePeriodType();
        effectivePeriod.setStartDate(DateUtils.parseOasisDateToXMLDate(record.getStringValue(ComponentInquiryFields.COMPONENT_EFF_FROM_DATE, "")));
        effectivePeriod.setEndDate(DateUtils.parseOasisDateToXMLDate(record.getStringValue(ComponentInquiryFields.COMPONENT_EFF_TO_DATE, "")));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getComponentEffectivePeriod", effectivePeriod);
        }

        return effectivePeriod;
    }

    public AdditionalInformationType getAddtionalComponentInformation(Record record, boolean policyCompFlag) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAdditionalComponentInformation", new Object[]{record, policyCompFlag});
        }

        AdditionalInformationType additionalInformationType = new AdditionalInformationType();
        String char1, char2, char3, num1, num2, num3, date1, date2, date3;
        if (policyCompFlag) {
            char1 = ComponentInquiryFields.POLICY_CHAR1;
            char2 = ComponentInquiryFields.POLICY_CHAR2;
            char3 = ComponentInquiryFields.POLICY_CHAR3;
            num1 = ComponentInquiryFields.POLICY_NUM1;
            num2 = ComponentInquiryFields.POLICY_NUM2;
            num3 = ComponentInquiryFields.POLICY_NUM3;
            date1 = ComponentInquiryFields.POLICY_DATE1;
            date2 = ComponentInquiryFields.POLICY_DATE2;
            date3 = ComponentInquiryFields.POLICY_DATE3;
        }
        else {
            char1 = ComponentInquiryFields.CHAR1;
            char2 = ComponentInquiryFields.CHAR2;
            char3 = ComponentInquiryFields.CHAR3;
            num1 = ComponentInquiryFields.NUM1;
            num2 = ComponentInquiryFields.NUM2;
            num3 = ComponentInquiryFields.NUM3;
            date1 = ComponentInquiryFields.DATE1;
            date2 = ComponentInquiryFields.DATE2;
            date3 = ComponentInquiryFields.DATE3;
        }
        AdditionalDataType additionalDataType1 = new AdditionalDataType();
        additionalDataType1.setValue(record.getStringValue(char1, ""));
        additionalDataType1.setKey("1");
        additionalInformationType.getAdditionalData().add(additionalDataType1);

        AdditionalDataType additionalDataType2 = new AdditionalDataType();
        additionalDataType2.setValue(record.getStringValue(char2, ""));
        additionalDataType2.setKey("2");
        additionalInformationType.getAdditionalData().add(additionalDataType2);

        AdditionalDataType additionalDataType3 = new AdditionalDataType();
        additionalDataType3.setValue(record.getStringValue(char3, ""));
        additionalDataType3.setKey("3");
        additionalInformationType.getAdditionalData().add(additionalDataType3);

        AdditionalDateTimeType additionalDateTimeType1 = new AdditionalDateTimeType();
        additionalDateTimeType1.setValue(DateUtils.parseOasisDateToXMLDate(record.getStringValue(date1, "")));
        additionalDateTimeType1.setKey("1");
        additionalInformationType.getAdditionalDateTime().add(additionalDateTimeType1);

        AdditionalDateTimeType additionalDateTimeType2 = new AdditionalDateTimeType();
        additionalDateTimeType2.setValue(DateUtils.parseOasisDateToXMLDate(record.getStringValue(date2, "")));
        additionalDateTimeType2.setKey("2");
        additionalInformationType.getAdditionalDateTime().add(additionalDateTimeType2);

        AdditionalDateTimeType additionalDateTimeType3 = new AdditionalDateTimeType();
        additionalDateTimeType3.setValue(DateUtils.parseOasisDateToXMLDate(record.getStringValue(date3, "")));
        additionalDateTimeType3.setKey("3");
        additionalInformationType.getAdditionalDateTime().add(additionalDateTimeType3);

        AdditionalNumberType additionalNumberType1 = new AdditionalNumberType();
        additionalNumberType1.setValue(record.getStringValue(num1, ""));
        additionalNumberType1.setKey("1");
        additionalInformationType.getAdditionalNumber().add(additionalNumberType1);

        AdditionalNumberType additionalNumberType2 = new AdditionalNumberType();
        additionalNumberType2.setValue(record.getStringValue(num2, ""));
        additionalNumberType2.setKey("2");
        additionalInformationType.getAdditionalNumber().add(additionalNumberType2);

        AdditionalNumberType additionalNumberType3 = new AdditionalNumberType();
        additionalNumberType3.setValue(record.getStringValue(num3, ""));
        additionalNumberType3.setKey("3");
        additionalInformationType.getAdditionalNumber().add(additionalNumberType3);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAdditionalComponentInformation", additionalInformationType);
        }

        return additionalInformationType;
    }

    public CreditSurchargeDeductibleVersionDetailType getCreditSurchargeDeductibleVersionDetail(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getCreditSurchargeDeductibleVersionDetail", new Object[]{record});
        }

        CreditSurchargeDeductibleVersionDetailType version = new CreditSurchargeDeductibleVersionDetailType();
        version.setVersionModeCode(record.getStringValue(RiskInquiryFields.RECORD_MODE_CODE, ""));
        version.setParentVersionNumberId(record.getStringValue(RiskInquiryFields.OFFICIAL_RECORD_ID, ""));
        version.setAfterImageIndicator(YesNoEmptyFlag.getInstance(record.getStringValue(RiskInquiryFields.AFTER_IMAGE_RECORD_B, "")).trueFalseEmptyValue());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getCreditSurchargeDeductibleVersionDetail", version);
        }

        return version;
    }

    public void loadParty(PolicyInquiryRequestType inquiryRequest, PolicyInquiryResultType inquiryResult, List<String> partyList) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadParty", new Object[]{inquiryRequest, inquiryResult, partyList});
        }

        PartyInquiryResultType partyInquiryResult = null;

        PartyInquiryRequestType partyInquiryRequest = new PartyInquiryRequestType();
        partyInquiryRequest.setCorrelationId(inquiryRequest.getCorrelationId());
        partyInquiryRequest.setMessageId(inquiryRequest.getMessageId());
        partyInquiryRequest.setUserId(inquiryRequest.getUserId());

        PartyInquiryResultParametersType partyInquiryResultParameters = new PartyInquiryResultParametersType();
        List<String> partyViewNames = partyInquiryResultParameters.getViewName();
        PolicyInquiryResultParametersType inquiryResultParameters = inquiryRequest.getPolicyInquiryResultParameters();
        if (inquiryResultParameters != null) {
            List<String> policyViewNames = inquiryRequest.getPolicyInquiryResultParameters().getViewName();
            Iterator policyViewIt = policyViewNames.iterator();
            while (policyViewIt.hasNext()) {
                String viewName = ((String)policyViewIt.next()).trim();
                if (!DEFAULT_VIEW_NAME.equalsIgnoreCase(viewName) && !POLICY_VIEW_NAME.equalsIgnoreCase(viewName)) {
                    partyViewNames.add(viewName);
                }
            }
        }
        partyInquiryRequest.setPartyInquiryResultParameters(partyInquiryResultParameters);

        Iterator it = partyList.iterator();
        while (it.hasNext()) {
            String clientId = (String)it.next();
            com.delphi_tech.ows.partyinquiryservice.PartyType party = new com.delphi_tech.ows.partyinquiryservice.PartyType();
            party.setPartyNumberId(clientId);
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
            inquiryResult.getOrganization().add((OrganizationType)orgIt.next());
        }

        Iterator persIt = partyInquiryResult.getPerson().iterator();
        while (persIt.hasNext()) {
            inquiryResult.getPerson().add((PersonType)persIt.next());
        }

        Iterator propIt = partyInquiryResult.getProperty().iterator();
        while (propIt.hasNext()) {
            inquiryResult.getProperty().add((PropertyType)propIt.next());
        }

        Iterator addIt = partyInquiryResult.getAddress().iterator();
        while (addIt.hasNext()) {
            inquiryResult.getAddress().add((AddressType)addIt.next());
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadParty");
        }
    }

    public RecordSet loadPolicyBillingAccountInfo(PolicyHeader policyHeader){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadPolicyBillingAccountInfo", new Object[]{policyHeader});
        }

        RecordSet billingAccountRs = null;
        Record inputRecord = policyHeader.toRecord();
        inputRecord.setFieldValue(SUBSYSTEM_CODE, PM);
        billingAccountRs = getPolicyManager().loadPolicyBillingAccountInfoForWS(inputRecord);

        if(l.isLoggable(Level.FINE)){
            l.entering(getClass().getName(), "loadPolicyBillingAccountInfo", new Object[]{billingAccountRs});
        }

        return billingAccountRs;
    }

    public Record requestParametersValidation(List<PolicyInquiryRequestParametersType> policyRequestParameters){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "requestParametersValidation", new Object[]{policyRequestParameters});
        }
        Record r = new Record();
        String allPolicyNos = "";
        String allTermBaseRecordIds = "";
        String invalidPolicyNos = "";
        String invalidTermBaseRecordIds = "";
        Iterator it = policyRequestParameters.iterator();
        while (it.hasNext()){
            PolicyInquiryRequestParametersType requestParam = (PolicyInquiryRequestParametersType) it.next();
            String policyNo = "";
            String termBaseRecordId ="";
            if (requestParam.getPolicyInquiry() != null){
                if(requestParam.getPolicyInquiry().getPolicyId() != null){
                    policyNo = requestParam.getPolicyInquiry().getPolicyId().trim();
                }
                if(requestParam.getPolicyInquiry().getPolicyTermNumberId() != null){
                    termBaseRecordId = requestParam.getPolicyInquiry().getPolicyTermNumberId().trim();
                }

                if (!StringUtils.isBlank(policyNo)) {
                    if (StringUtils.isBlank(allPolicyNos)) {
                        allPolicyNos = policyNo;
                    }
                    else {
                        allPolicyNos = allPolicyNos + "," + policyNo;
                    }
                }

                if (!StringUtils.isBlank(termBaseRecordId)){
                    if (StringUtils.isBlank(allTermBaseRecordIds)){
                        allTermBaseRecordIds = termBaseRecordId;
                    }
                    else{
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
            l.exiting(getClass().getName(), "requestParametersValidation", policyRequestParameters);
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
            l.entering(getClass().getName(), "validatePolicyNosExist", new Object[]{termBaseRecordIds});
        }
        String invalidTermBaseRecordIds = getPolicyInquiryServiceHelper().validateTermBaseRecordIdsExist(termBaseRecordIds);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validatePolicyNosExist", termBaseRecordIds);
        }
        return invalidTermBaseRecordIds;
    }

    public void addInfoNoMatchResultMsg(String invalidPolNos, String invalidTermIds, String transactionStatusCode, int resultPolCnt){
        if (!StringUtils.isBlank(invalidPolNos)){
            String polIdInvalid = "ws.policy.inquiry.policy.id.invalid";
            MessageManager.getInstance().addInfoNoMatchResultMessage(polIdInvalid, new String[]{invalidPolNos});
        }

        if (!StringUtils.isBlank(invalidTermIds)){
            String polTermIdInvalid = "ws.policy.inquiry.policy.term.id.invalid";
            MessageManager.getInstance().addInfoNoMatchResultMessage(polTermIdInvalid, new String[]{invalidTermIds});
        }

        if (resultPolCnt == 0 && !StringUtils.isBlank(transactionStatusCode)){
            String transStatusCodeFilterInvalid = "ws.policy.inquiry.transaction.status.code.filter.invalid";
            MessageManager.getInstance().addInfoNoMatchResultMessage(transStatusCodeFilterInvalid);
        }
    }

    //Issue 192351: change risk/risk additional exposure/coverage(class)/component effective_to_date in official record
    //              for cancel wip case.
    public RecordSet changeCxlWipTermOffExpDate(PolicyHeader policyHeader, RecordSet recordSet, String effToDateName) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "changeCxlWipTermOffExpDate", new Object[]{policyHeader, recordSet, effToDateName});
        }

        RecordSet rs = new RecordSet();
        for (int i = 0; i < recordSet.getSize(); i++) {
            Record record = recordSet.getRecord(i);
            if (record.getFieldValue(PolicyInquiryFields.RECORD_MODE_CODE).equals(RecordMode.OFFICIAL.getName())) {
                record.setFieldValue(effToDateName, policyHeader.getCxlWipTermOffExpDate());
                rs.addRecord(record);
                continue;
            }
            rs.addRecord(record);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "changeCxlWipTermOffExpDate", rs);
        }

        return rs;
    }

    public void verifyConfig() {
        if (getPolicyManager() == null)
            throw new ConfigurationException("The required property 'policyManager' is missing.");
        if (getRiskManager() == null)
            throw new ConfigurationException("The required property 'riskManager' is missing.");
        if (getCoverageManager() == null)
            throw new ConfigurationException("The required property 'coverageManager' is missing.");
        if (getCoverageClassManager() == null)
            throw new ConfigurationException("The required property 'coverageClassManager' is missing.");
        if (getComponentManager() == null)
            throw new ConfigurationException("The required property 'componentManager' is missing.");
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

    public CoverageManager getCoverageManager() {
        return m_coverageManager;
    }

    public void setCoverageManager(CoverageManager coverageManager) {
        m_coverageManager = coverageManager;
    }

    public CoverageClassManager getCoverageClassManager() {
        return m_coverageClassManager;
    }

    public void setCoverageClassManager(CoverageClassManager coverageClassManager) {
        m_coverageClassManager = coverageClassManager;
    }

    public ComponentManager getComponentManager() {
        return m_componentManager;
    }

    public void setComponentManager(ComponentManager componentManager) {
        m_componentManager = componentManager;
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

    public PolicyInquiryServiceHelper getPolicyInquiryServiceHelper() {
        return m_policyInquiryServiceHelper;
    }

    public void setPolicyInquiryServiceHelper(PolicyInquiryServiceHelper policyInquiryServiceHelper) {
        m_policyInquiryServiceHelper = policyInquiryServiceHelper;
    }

    public String getFilterInsured(PolicyInquiryRequestType inquiryRequest) {
        String insuredId = null;
        PolicyInquiryResultParametersType policyInquiryResultParameters = inquiryRequest.getPolicyInquiryResultParameters();
        if (policyInquiryResultParameters!=null) {
            FilterType filter = policyInquiryResultParameters.getFilter();
            if (filter!=null) {
                InsuredInquiryType insuredInquiry = filter.getInsuredInquiry();
                if (insuredInquiry!=null) {
                    com.delphi_tech.ows.policyinquiryservice.InsuredType insured = insuredInquiry.getInsured();
                    if (insured!=null) {
                        insuredId = insured.getInsuredNumberId();
                        if (insuredId!=null) {
                            insuredId = insuredId.trim();
                        }
                    }
                }
            }
        }
        return insuredId;
    }

    public String getFilterPrimaryInsured(PolicyInquiryRequestType inquiryRequest) {
        String insuredPrimary = null;
        PolicyInquiryResultParametersType policyInquiryResultParameters = inquiryRequest.getPolicyInquiryResultParameters();
        if (policyInquiryResultParameters!=null) {
            FilterType filter = policyInquiryResultParameters.getFilter();
            if (filter!=null) {
                InsuredInquiryType insuredInquiry = filter.getInsuredInquiry();
                if (insuredInquiry!=null) {
                    insuredPrimary = insuredInquiry.getPrimaryIndicator();
                    if (insuredPrimary != null) {
                        insuredPrimary = insuredPrimary.trim();
                    }
                }
            }
        }
        return insuredPrimary;
    }

    public boolean hasFilterPrimaryInsured(String filterPrimaryInsured) {
        return YesNoFlag.getInstance(filterPrimaryInsured).booleanValue();
    }

    public void cacheClientId(List<String> partyList, String clientId) {
        if (clientId==null || "".equalsIgnoreCase(clientId)) {
            return;
        }
        boolean hasParty = false;
        Iterator it = partyList.iterator();
        while (it.hasNext()) {
            String party = (String)it.next();
            if (party.equalsIgnoreCase(clientId)) {
                hasParty = true;
                break;
            }
        }
        if(!hasParty) {
            partyList.add(clientId);
        }
    }

    public String getTransactionStatusCode (PolicyInquiryRequestType inquiryRequest){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getTransactionStatusCode", new Object[]{inquiryRequest});
        }

        PolicyInquiryResultParametersType policyInquiryResultParameters = inquiryRequest.getPolicyInquiryResultParameters();
        String transactionStatusCode = "";
        if (policyInquiryResultParameters != null) {
            FilterType filter = policyInquiryResultParameters.getFilter();
            if(filter != null) {
                String code = filter.getTransactionStatusCode();
                transactionStatusCode = (code==null ? "" : code.trim());
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getTransactionStatusCode", transactionStatusCode);
        }

        return transactionStatusCode;
    }

    public String getThreadPoolCategoryName() {
        return m_threadPoolCategoryName;
    }

    public void setThreadPoolCategoryName(String threadPoolCategoryName) {
        this.m_threadPoolCategoryName = threadPoolCategoryName;
    }


    public RequestStorageManager getRequestStorageManager() {
        return m_requestStorageManager;
    }

    public void setRequestStorageManager(RequestStorageManager requestStorageManager) {
        m_requestStorageManager = requestStorageManager;
    }

    public OasisExecutorServiceManager getOasisExecutorServiceManager() {
        return m_OasisExecutorServiceManager;
    }

    public void setOasisExecutorServiceManager(OasisExecutorServiceManager oasisExecutorServiceManager) {
        m_OasisExecutorServiceManager = oasisExecutorServiceManager;
    }

    public RiskAddtlExposureManager getRiskAddtlExposureManager() {
        return m_RiskAddtlExposureManager;
    }

    public void setRiskAddtlExposureManager(RiskAddtlExposureManager riskAddtlExposureManager) {
        m_RiskAddtlExposureManager = riskAddtlExposureManager;
    }

    private PolicyManager m_policyManager;
    private RiskManager m_riskManager;
    private CoverageManager m_coverageManager;
    private CoverageClassManager m_coverageClassManager;
    private ComponentManager m_componentManager;
    private UnderwriterManager m_underwriterManager;
    private AgentManager m_agentManager;
    private PartyInquiryServiceManager m_partyInquiryServiceManager;
    private PolicyInquiryServiceHelper m_policyInquiryServiceHelper;
    private OasisExecutorServiceManager m_OasisExecutorServiceManager;
    private RiskAddtlExposureManager m_RiskAddtlExposureManager;

    private RequestStorageManager m_requestStorageManager;

    private final String DEFAULT_VIEW_NAME = "";
    private final String VIEW_NAME = "viewName";
    private final String POLICY_VIEW_NAME = "Policy";
    private final String MINIMAL_VIEW_NAME = "Minimal";
    private final String SUCCESS_MESSAGE_STATUS = "Success";
    private final String REJECTED_MESSAGE_STATUS = "Rejected";
    private final String SUBSYSTEM_CODE = "subSystemCode";
    private final String PM = "PM";
    private final String RISK_ID = "SOURCERECORDFK2";
    private final String COVERAGE_ID = "SOURCERECORDFK3";

    private String m_threadPoolCategoryName;

//    public PolicyInquiryResultType loadPolicy (PolicyInquiryRequestType inquiryRequest) {
//
//        Date startDate = new Date();
//        long startTime = startDate.getTime();
//        System.out.println("");
//        System.out.println("***********                 *****************************");
//        System.out.println("***********   start: " + startDate.toString());
//        System.out.println("***********        threadpoolTest: " + "aaaa");
//
//
//        Logger l = LogUtils.getLogger(getClass());
//        if (l.isLoggable(Level.FINER)) {
//            l.entering(getClass().getName(), "loadPolicy", new Object[]{inquiryRequest});
//        }
//        OwsLogRequest owsLogRequest = null;
//        if (l.isLoggable(Level.FINEST)) {
//            String xmlResult = XMLUtils.marshalJaxbToXML(inquiryRequest, _PolicyInquiryRequest_QNAME);
//            owsLogRequest = OwsAccessTrailManager.getInstance().addOwsAccessTrailLogger(xmlResult,
//                inquiryRequest.getMessageId(), inquiryRequest.getCorrelationId(), inquiryRequest.getUserId(), _PolicyInquiryRequest_QNAME.getLocalPart());
//            l.logp(Level.FINEST, getClass().getName(), "loadPolicy", xmlResult);
//        } else {
//            owsLogRequest = OwsAccessTrailManager.getInstance().addOwsAccessTrailLogger(inquiryRequest, _PolicyInquiryRequest_QNAME,
//                inquiryRequest.getMessageId(), inquiryRequest.getCorrelationId(), inquiryRequest.getUserId());
//        }
//
////        RequestSession session = RequestLifecycleAdvisor.getInstance().getRequestState();
////        setRequestSession(session);
////        RequestLifecycleAdvisor.getInstance().initializeFromRequestState(getRequestSession());
//
//        String viewName = getViewName(inquiryRequest);
//        String filterInsured = getFilterInsured(inquiryRequest);
//        String filterPrimaryInsured = getFilterPrimaryInsured(inquiryRequest);
//
//        PolicyInquiryResultType inquiryResult = new PolicyInquiryResultType();
//        inquiryResult.setMessageId(inquiryRequest.getMessageId());
//        inquiryResult.setCorrelationId(inquiryRequest.getCorrelationId());
//
//        MessageStatusType mst = new MessageStatusType();
//        mst.setMessageStatusCode(SUCCESS_MESSAGE_STATUS);
//        inquiryResult.setMessageStatus(mst);
//
//        List<PolicyInquiryRequestParametersType> policyRequestParameters = inquiryRequest.getPolicyInquiryRequestParameters();
//        List<String> partyList = new ArrayList<String>();
//        Iterator it = policyRequestParameters.iterator();
//
//        try {
//            partyList.clear();
//
//
//            while (it.hasNext()) {
//                PolicyInquiryRequestParametersType requestParam = (PolicyInquiryRequestParametersType)it.next();
//                Record inputRecord = setInputRecord(requestParam);
//
//                Record policyRec = null;
//                RecordSet underwriterRs = null, polCompRs = null, agentRs = null, riskRs = null, covgRs = null,
//                    covgClsRs = null, compRs = null, billingAccountRs = null;
//
//                if (MINIMAL_VIEW_NAME.equalsIgnoreCase(viewName)) {
//                    RecordSet policyRecordSet = findAllPolicyMinimalInformation(inputRecord);
//                    Iterator policyIt = policyRecordSet.getRecords();
//                    while (policyIt.hasNext()) {
//                        policyRec = (Record)policyIt.next();
//                        policyRec.setFieldValue(VIEW_NAME, viewName);
//                        PolicyInquiryResultType item = resultToObject(policyRec, underwriterRs, agentRs, polCompRs, riskRs,
//                            covgRs, covgClsRs, compRs, partyList, billingAccountRs);
//                        addItemToResultObject(inquiryResult, item);
//                    }
//                }
//                else {
//                    List<String[]> termPolicyList = getPolicyInquiryServiceHelper().getTermPolicyList(inputRecord);
//                    Iterator termIt = termPolicyList.iterator();
//
//                    while (termIt.hasNext()) {
//                        String[] pair = (String[])termIt.next();
//                        String termBaseId = pair[0];
//                        String policyNo = pair[1];
//
//                        if (hasFilterPrimaryInsured(filterPrimaryInsured)) {
//                            filterInsured = getPrimaryInsured(termBaseId);
//                        }
//
//                        policyRec = getPolicyInquiryServiceHelper().loadPolicyInformation(policyNo, termBaseId);
//                        PolicyHeader policyHeader = getPolicyInquiryServiceHelper().buildPolicyHeader(policyRec);
//                        policyRec.setFieldValue(VIEW_NAME, viewName);
//
//                        billingAccountRs = loadPolicyBillingAccountInfo(policyHeader);
//
//                        if (!MINIMAL_VIEW_NAME.equalsIgnoreCase(viewName)) {
//                            underwriterRs = loadUnderwriterInformation(policyHeader);
//                            agentRs = loadAgentInformation(inputRecord, policyHeader);
//                        }
//
//                        if (DEFAULT_VIEW_NAME.equalsIgnoreCase(viewName)) {
//                            riskRs = loadRiskInformation(policyHeader, filterInsured);
//                            covgRs = loadCoverageInformation(policyHeader, filterInsured);
//                            covgClsRs = loadCoverageClassInformation(policyHeader, filterInsured);
//                            compRs = loadComponentInformation(policyHeader, filterInsured);
//                            polCompRs = loadPolicyComponentInformation(inputRecord, policyHeader);
//                        }
//
//                        PolicyInquiryResultType item = resultToObject(policyRec, underwriterRs, agentRs, polCompRs, riskRs,
//                            covgRs, covgClsRs, compRs, partyList, billingAccountRs);
//                        addItemToResultObject(inquiryResult, item);
//
//                    }
//                }
//            }
//
//
//            if ( (DEFAULT_VIEW_NAME.equalsIgnoreCase(viewName) || POLICY_VIEW_NAME.equalsIgnoreCase(viewName)) && partyList.size()>0) {
//                loadParty(inquiryRequest, inquiryResult, partyList);
//                setPartyReferences(inquiryResult);
//            }
//
//            if(inquiryResult.getMedicalMalpracticePolicy().size()>0){
//                owsLogRequest.setSourceTableName("POLICY_TERM_HISTORY");
//                owsLogRequest.setSourceRecordFk(inquiryResult.getMedicalMalpracticePolicy().get(0).getPolicyTermNumberId());
//                owsLogRequest.setSourceRecordNo(inquiryResult.getMedicalMalpracticePolicy().get(0).getPolicyId());
//            }
//
//            partyList.clear();
//
//        } catch (MessageStatusAppException wsae) {
//            inquiryResult.setMessageStatus(wsae.getMessageStatus());
//        } catch(Exception e) {
//            AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR, "Failure invoking the PolicyInquiryServiceManagerImpl", e);
//            l.logp(Level.SEVERE, getClass().getName(), "loadPolicy", ae.getMessage(), ae);
//            inquiryResult.setMessageStatus(MessageStatusHelper.getInstance().getRejectedMessageStatus(ae));
//        }
//
//        owsLogRequest.setMessageStatusCode(inquiryResult.getMessageStatus().getMessageStatusCode());
//        if (l.isLoggable(Level.FINEST)) {
//            String xmlResult = XMLUtils.marshalJaxbToXML(inquiryResult, _PolicyInquiryResult_QNAME);
//            owsLogRequest.setResultXML(xmlResult);
//            l.logp(Level.FINEST, getClass().getName(), "loadPolicy", xmlResult);
//        } else {
//            owsLogRequest.setServiceResult(inquiryResult);
//            owsLogRequest.setServiceResultQName(_PolicyInquiryResult_QNAME);
//        }
//        OwsAccessTrailManager.getInstance().updateOwsAccessTrailLogger(owsLogRequest);
//
//        if (l.isLoggable(Level.FINER)) {
//            l.exiting(getClass().getName(), "loadPolicy", inquiryResult);
//        }
//
//        Date endDate = new Date();
//        long endTime = endDate.getTime();
//        System.out.println("");
//        System.out.println("*******************************************************************************");
//        System.out.println("***********                                                         ***********");
//        System.out.println("***********   end: " + endDate.toString() + "                       ***********");
//        System.out.println("***********    diff: " + (endTime - startTime) / 1000 + " sec       ***********");
//        System.out.println("***********                                                         ***********");
//        System.out.println("*******************************************************************************");
//        System.out.println("");
//
//        return inquiryResult;
//    }

}
