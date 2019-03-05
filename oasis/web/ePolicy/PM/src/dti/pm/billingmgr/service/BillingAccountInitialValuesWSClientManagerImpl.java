package dti.pm.billingmgr.service;

import com.delphi_tech.ows.account.BillingAccountDetailType;
import com.delphi_tech.ows.account.ContractPeriodType;
import com.delphi_tech.ows.account.IssueCompanyType;
import com.delphi_tech.ows.account.PrincipalBillingAccountInformationType;
import com.delphi_tech.ows.billingaccountinitialvaluesservice.BillingAccountInitialValuesRequestParametersType;
import com.delphi_tech.ows.billingaccountinitialvaluesservice.BillingAccountInitialValuesRequestType;
import com.delphi_tech.ows.billingaccountinitialvaluesservice.BillingAccountInitialValuesResultType;
import com.delphi_tech.ows.billingaccountinitialvaluesservice.BillingAccountInitialValuesService;
import com.delphi_tech.ows.billingaccountinitialvaluesservice.BillingAccountInitialValuesService_Service;
import com.delphi_tech.ows.billingaccountinitialvaluesservice.BillingAccountInitialValuesType;
import com.delphi_tech.ows.account.PolicyHolderType;
import com.delphi_tech.ows.account.ReferredPartyType;
import dti.oasis.recordset.Record;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.request.service.WebServiceClientHelper;
import dti.oasis.session.UserSessionManager;
import dti.oasis.struts.IOasisAction;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.OasisUser;
import dti.oasis.util.StringUtils;
import dti.pm.billingmgr.BillingAccountInitialValuesWSClientManager;
import dti.pm.billingmgr.BillingFields;
import dti.pm.policymgr.PolicyHeader;
import javax.xml.ws.BindingProvider;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 20, 2014
 *
 * @author awu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/27/2014      jyang        159974 - Modified billingAccountInquiryRequest to get userId and password based on role.
 * 06/15/2015      cv           163222 - added log entries before calling FM Webservice.
 * 06/23/2014      jli          163761 - Modified the getInitialValuesForBilling. issueCompanyEntityId should be
 *                                       initialized from the interface of FM,but if null get from policy header.
 * 07/28/2015      jli          164847 - Modified the getInitialValuesForBilling. issueCompanyEntityId should be
 *                                       initialized from the interface of FM whatever the value of it.
 * 01/09/2018      kmv          187541 - Modified getInitialValuesForBilling() to set the policy header issue company
 *                                       as the default account issue company when its default value is null in ACCOUNT_TYPE
 * ---------------------------------------------------
 */

public class BillingAccountInitialValuesWSClientManagerImpl implements BillingAccountInitialValuesWSClientManager {

    /**
     * Call webservice to load the initial data and set them to output record.
     *
     * @param policyHeader
     * @param inputRecord
     * @return
     * @throws MalformedURLException
     */
    public Record getInitialValuesForBilling(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(this.getClass(), "getInitialValuesForBilling", new Object[]{policyHeader, inputRecord});

        BillingAccountInitialValuesRequestType requestType = recordToXml(policyHeader, inputRecord);
        BillingAccountInitialValuesResultType result = billingAccountInitialValuesRequest(requestType);
        PrincipalBillingAccountInformationType principalBillingAccountInfo = result.getPrincipalBillingAccountInformation();

        Record accountRecord = null;
        if (principalBillingAccountInfo != null) {
            accountRecord = new Record();
            BillingAccountDetailType billingAccountDetail = principalBillingAccountInfo.getBillingAccountDetail();
            BillingFields.setAccountNo(accountRecord, principalBillingAccountInfo.getBillingAccountId());
            BillingFields.setAccountingMode(accountRecord, billingAccountDetail.getBillingAccountingMode());
            BillingFields.setAccountType(accountRecord, billingAccountDetail.getBillingAccountType());
            BillingFields.setBillLeadDays(accountRecord, billingAccountDetail.getBillLeadDays());
            BillingFields.setOverdueRule(accountRecord, billingAccountDetail.getBillOverdueRuleCode());
            BillingFields.setSinglePolicyB(accountRecord, billingAccountDetail.getIndividualAccountIndicator());
            String issueCompanyEntityId = billingAccountDetail.getIssueCompany().getReferredParty().getPartyNumberId();
            if (StringUtils.isBlank(issueCompanyEntityId)) {
                issueCompanyEntityId = policyHeader.getIssueCompanyEntityId();
            }
            BillingFields.setIssueCompanyEntityId(accountRecord, issueCompanyEntityId);
        }

        l.exiting(getClass().getName(), "getInitialValuesForBilling", new Object[]{accountRecord});
        return accountRecord;
    }

    /**
     * Call BillingAccountInitialValuesService.
     *
     * @param request
     * @return
     */
    private BillingAccountInitialValuesResultType billingAccountInitialValuesRequest
    (BillingAccountInitialValuesRequestType request) {
        Logger l = LogUtils.enterLog(this.getClass(), "billingAccountInitialValuesRequest", new Object[]{request});

        String userName = WebServiceClientHelper.getInstance().getOWSUserName();
        String password = WebServiceClientHelper.getInstance().getOWSPassword();
        String endPointURL = RequestStorageManager.getInstance().get("FM_SERVICE_PATH") + "/BillingAccountInitialValuesService";

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "billingAccountInitialValuesRequest", endPointURL);
            l.exiting(getClass().getName(), "billingAccountInitialValuesRequest", userName + "/" + password);
        }

        BillingAccountInitialValuesService_Service service = new BillingAccountInitialValuesService_Service();
        BillingAccountInitialValuesService port = service.getBillingAccountInitialValuesServicePort();
        Map<String, Object> requestContext = ((BindingProvider) port).getRequestContext();
        requestContext.put(BindingProvider.USERNAME_PROPERTY, userName);
        requestContext.put(BindingProvider.PASSWORD_PROPERTY, password);
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endPointURL);

        BillingAccountInitialValuesResultType result = port.billingAccountInitialValuesRequest(request);

        l.exiting(getClass().getName(), "billingAccountInitialValuesRequest", new Object[]{result});
        return result;
    }

    /**
     * Map the data from OASIS record to XML objects.
     *
     * @param policyHeader
     * @param inputRecord
     * @return
     */
    private BillingAccountInitialValuesRequestType recordToXml(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(this.getClass(), "recordToXml", new Object[]{policyHeader, inputRecord});

        BillingAccountInitialValuesRequestType requestType = new BillingAccountInitialValuesRequestType();

        OasisUser user = (OasisUser) UserSessionManager.getInstance().getUserSession().get(IOasisAction.KEY_OASISUSER);
        String userId = user.getUserId();
        requestType.setUserId(userId);

        BillingAccountInitialValuesRequestParametersType requestParametersType = new BillingAccountInitialValuesRequestParametersType();
        BillingAccountInitialValuesType initialValuesType = new BillingAccountInitialValuesType();
        initialValuesType.setPolicyId(policyHeader.getPolicyNo());
        initialValuesType.setPolicyTypeCode(policyHeader.getPolicyTypeCode());
        initialValuesType.setDerivedPolicyId(BillingFields.getDerivedPolicyNo(inputRecord));

        PolicyHolderType policyHolderType = new PolicyHolderType();
        ReferredPartyType partyType = new ReferredPartyType();
        partyType.setPartyNumberId(policyHeader.getPolicyHolderNameEntityId());
        policyHolderType.setReferredParty(partyType);
        initialValuesType.setPolicyHolder(policyHolderType);

        ContractPeriodType contractPeriodType = new ContractPeriodType();
        contractPeriodType.setStartDate(DateUtils.parseOasisDateToXMLDate(policyHeader.getTermEffectiveFromDate()));
        initialValuesType.setContractPeriod(contractPeriodType);

        IssueCompanyType issueCompanyType = new IssueCompanyType();
        ReferredPartyType companyPartyType = new ReferredPartyType();
        companyPartyType.setPartyNumberId(policyHeader.getIssueCompanyEntityId());
        issueCompanyType.setReferredParty(companyPartyType);
        issueCompanyType.setControllingStateOrProvinceCode(policyHeader.getIssueStateCode());
        initialValuesType.setIssueCompany(issueCompanyType);

        requestParametersType.setBillingAccountInitialValues(initialValuesType);
        requestType.setBillingAccountInitialValuesRequestParameters(requestParametersType);

        l.exiting(getClass().getName(), "recordToXml", new Object[]{requestType});
        return requestType;
    }

}
