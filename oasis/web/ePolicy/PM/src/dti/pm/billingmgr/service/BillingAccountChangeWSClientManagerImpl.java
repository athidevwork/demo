package dti.pm.billingmgr.service;

import com.delphi_tech.ows.account.AccountHolderType;
import com.delphi_tech.ows.account.BillingAccountDetailType;
import com.delphi_tech.ows.account.EffectivePeriodType;
import com.delphi_tech.ows.account.IssueCompanyType;
import com.delphi_tech.ows.account.LinkedPolicyType;
import com.delphi_tech.ows.account.PaymentOptionType;
import com.delphi_tech.ows.account.PrincipalBillingAccountInformationType;
import com.delphi_tech.ows.account.ReferredPartyType;
import com.delphi_tech.ows.billingaccountchangeservice.BillingAccountChangeRequestType;
import com.delphi_tech.ows.billingaccountchangeservice.BillingAccountChangeResultType;
import com.delphi_tech.ows.billingaccountchangeservice.BillingAccountChangeService;
import com.delphi_tech.ows.billingaccountchangeservice.BillingAccountChangeService_Service;
import com.delphi_tech.ows.common.ExtendedStatusType;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.request.service.WebServiceClientHelper;
import dti.oasis.session.UserSessionManager;
import dti.oasis.struts.IOasisAction;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.OasisUser;
import dti.oasis.util.StringUtils;
import dti.ows.common.MessageStatusHelper;
import dti.pm.billingmgr.BillingAccountChangeWSClientManager;
import dti.pm.billingmgr.BillingFields;
import dti.pm.policymgr.PolicyHeader;
import javax.xml.ws.BindingProvider;
import java.util.List;
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
 * 06/23/2014      jli          163761 - Modified the method recordToXML to get the issueCompanyEntityId from user selected
 *                                       value when it is not null, else get from policy header.
 * 07/28/2015      jli          164847 - Modified the method recordToXML to set the right singlePolicyB and issueCompanyEntityId.
 *                                       The policy from create policy should have the same behavior as Quote.
 * ---------------------------------------------------
 */

public class BillingAccountChangeWSClientManagerImpl implements BillingAccountChangeWSClientManager {

    /**
     * Call webService to save the billing data. This method is using in ePolicy.
     *
     * @param policyHeader
     * @param inputRecord
     * @param isBillingExists
     * @return
     */
    public Record saveBillingForEPolicy(PolicyHeader policyHeader, Record inputRecord, boolean isBillingExists) {
        Logger l = LogUtils.enterLog(this.getClass(), "saveBillingForEPolicy", new Object[]{policyHeader, inputRecord, isBillingExists});

        BillingAccountChangeRequestType requestType = recordToXML(policyHeader, inputRecord, isBillingExists);
        BillingAccountChangeResultType result = billingAccountChangeRequest(requestType);

        Record outputRecord = handleOnMessages(result, policyHeader);

        l.exiting(getClass().getName(), "saveBillingForEPolicy", new Object[]{outputRecord});
        return outputRecord;
    }

    /**
     * Call webService to save the billing data. This method is using in PolicyChangeService.
     *
     * @param principalBillingAccountInformation
     *
     * @param policyHeader
     */
    public void saveBillingForPolicyChangeService
    (PrincipalBillingAccountInformationType principalBillingAccountInformation, PolicyHeader policyHeader) {
        Logger l = LogUtils.enterLog(this.getClass(), "saveBillingForPolicyChangeService",
            new Object[]{principalBillingAccountInformation, policyHeader});

        BillingAccountChangeRequestType requestType = new BillingAccountChangeRequestType();
        OasisUser user = (OasisUser) UserSessionManager.getInstance().getUserSession().get(IOasisAction.KEY_OASISUSER);
        String userId = user.getUserId();
        requestType.setUserId(userId);

        requestType.getPrincipalBillingAccountInformation().add(principalBillingAccountInformation);
        BillingAccountChangeResultType resultType = billingAccountChangeRequest(requestType);
        handleOnMessages(resultType, policyHeader);

        l.exiting(getClass().getName(), "saveBillingForPolicyChangeService", new Object[]{});
    }

    /**
     * Call FM webService BillingAccountChangeService.
     *
     * @param billingAccountChangeRequest
     */
    public BillingAccountChangeResultType billingAccountChangeRequest
    (BillingAccountChangeRequestType billingAccountChangeRequest) {
        Logger l = LogUtils.enterLog(this.getClass(), "billingAccountChangeRequest",
            new Object[]{billingAccountChangeRequest});

        String userName = WebServiceClientHelper.getInstance().getOWSUserName();
        String password = WebServiceClientHelper.getInstance().getOWSPassword();
        String endPointURL = RequestStorageManager.getInstance().get("FM_SERVICE_PATH") + "/BillingAccountChangeService";

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "billingAccountChangeRequest", endPointURL);
            l.exiting(getClass().getName(), "billingAccountChangeRequest", userName + "/" + password);
        }

        BillingAccountChangeService_Service service = new BillingAccountChangeService_Service();
        BillingAccountChangeService port = service.getBillingAccountChangeServicePort();
        Map<String, Object> requestContext = ((BindingProvider) port).getRequestContext();
        requestContext.put(BindingProvider.USERNAME_PROPERTY, userName);
        requestContext.put(BindingProvider.PASSWORD_PROPERTY, password);
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endPointURL);

        BillingAccountChangeResultType result = port.billingAccountChangeRequest(billingAccountChangeRequest);

        l.exiting(getClass().getName(), "billingAccountChangeRequest", new Object[]{result});
        return result;
    }

    /**
     * Map the data from OASIS records to XML objects.
     *
     * @param policyHeader
     * @param inputRecord
     * @param isBillingExists
     * @return
     */
    public BillingAccountChangeRequestType recordToXML(PolicyHeader policyHeader, Record inputRecord, boolean isBillingExists) {
        Logger l = LogUtils.enterLog(this.getClass(), "recordToXML",
            new Object[]{policyHeader, inputRecord, isBillingExists});

        BillingAccountChangeRequestType requestType = new BillingAccountChangeRequestType();
        OasisUser user = (OasisUser) UserSessionManager.getInstance().getUserSession().get(IOasisAction.KEY_OASISUSER);
        String userId = user.getUserId();
        requestType.setUserId(userId);
        PrincipalBillingAccountInformationType principalBillingAccountInformationType = new PrincipalBillingAccountInformationType();
        if (isBillingExists) {
            PrincipalBillingAccountInformationType existedPillingAccountInformation =
                (PrincipalBillingAccountInformationType) RequestStorageManager.getInstance().get("InquiryBilling");
            principalBillingAccountInformationType = existedPillingAccountInformation;

        }
        else {
            principalBillingAccountInformationType.setBillingAccountId(BillingFields.getAccountNo(inputRecord));
            principalBillingAccountInformationType.setBillingAccountNumberId(BillingFields.getBillingAccountId(inputRecord));

            BillingAccountDetailType accountDetailType = new BillingAccountDetailType();
            accountDetailType.setBaseMonthDate(BillingFields.getBaseBillMonthDay(inputRecord));
            accountDetailType.setBillFrequencyCode(BillingFields.getBillingFrequency(inputRecord));
            accountDetailType.setBillingAccountingMode(BillingFields.getAccountingMode(inputRecord));
            accountDetailType.setBillingAccountType(BillingFields.getAccountType(inputRecord));
            accountDetailType.setBillLeadDays(BillingFields.getBillLeadDays(inputRecord));
            accountDetailType.setBillOverdueRuleCode(BillingFields.getOverdueRule(inputRecord));
            accountDetailType.setIndividualAccountIndicator(BillingFields.getSinglePolicyB(inputRecord));
            accountDetailType.setNextBillDate(BillingFields.getNextBillingDate(inputRecord));

            IssueCompanyType issueCompanyType = new IssueCompanyType();
            issueCompanyType.setControllingStateOrProvinceCode(policyHeader.getIssueStateCode());
            ReferredPartyType referredPartyType = new ReferredPartyType();
            referredPartyType.setPartyNumberId(BillingFields.getIssueCompanyEntityId(inputRecord));

            issueCompanyType.setReferredParty(referredPartyType);
            accountDetailType.setIssueCompany(issueCompanyType);

            principalBillingAccountInformationType.setBillingAccountDetail(accountDetailType);

            AccountHolderType accountHolder = new AccountHolderType();
            ReferredPartyType accountParty = new ReferredPartyType();
            accountParty.setPartyNumberId(policyHeader.getPolicyHolderNameEntityId());
            accountHolder.setReferredParty(accountParty);
            EffectivePeriodType effectivePeriod = new EffectivePeriodType();
            effectivePeriod.setStartDate(DateUtils.parseOasisDateToXMLDate(policyHeader.getTermEffectiveFromDate()));
            effectivePeriod.setEndDate(DateUtils.parseOasisDateToXMLDate(policyHeader.getTermEffectiveToDate()));
            accountHolder.setEffectivePeriod(effectivePeriod);
            principalBillingAccountInformationType.getAccountHolder().add(accountHolder);
        }

        if (principalBillingAccountInformationType.getLinkedPolicy() != null) {
            principalBillingAccountInformationType.getLinkedPolicy().clear();
        }

        LinkedPolicyType policyType = new LinkedPolicyType();
        policyType.setPolicyId(policyHeader.getPolicyNo());
        PaymentOptionType paymentOption = new PaymentOptionType();
        paymentOption.setPaymentPlanId(BillingFields.getPaymentPlanId(inputRecord));
        policyType.getPaymentOption().add(paymentOption);
        principalBillingAccountInformationType.getLinkedPolicy().add(policyType);

        requestType.getPrincipalBillingAccountInformation().add(principalBillingAccountInformationType);

        l.exiting(getClass().getName(), "recordToXML", new Object[]{requestType});
        return requestType;
    }

    /**
     * setup the messages after calling webservice.
     * @param result
     * @param policyHeader
     * @return
     */
    public Record handleOnMessages(BillingAccountChangeResultType result, PolicyHeader policyHeader) {
        Logger l = LogUtils.enterLog(this.getClass(), "handleOnMessages", new Object[]{policyHeader, result});

        Record outputRecord = new Record();
        if (result.getMessageStatus() != null) {
            if (MessageStatusHelper.STATUS_CODE_SUCCESS.equals(result.getMessageStatus().getMessageStatusCode()) ||
                MessageStatusHelper.STATUS_CODE_SUCCESS_WITH_INFO.equals(result.getMessageStatus().getMessageStatusCode())) {
                String billingRelationCreatedKey = "pm.maintainBilling.save.billingRelationCreated";
                MessageManager.getInstance().addInfoMessage(billingRelationCreatedKey, new Object[]{policyHeader.getPolicyNo()});
                outputRecord.setFieldValue("rc", "1");
            }
            else if (result.getMessageStatus().getExtendedStatus() != null
                && result.getMessageStatus().getExtendedStatus().size() > 0) {
                List<ExtendedStatusType> messageList = result.getMessageStatus().getExtendedStatus();
                for (int i = 0; i < messageList.size(); i++) {
                    ExtendedStatusType extendedStatus = messageList.get(i);
                    if ("Error".equals(extendedStatus.getExtendedStatusCode())) {
                        MessageManager.getInstance().addErrorMessage("pm.common.invalid.data",
                            new String[]{extendedStatus.getExtendedStatusDescription()}, i + "", "");
                    }
                    else if ("Warning".equals(extendedStatus.getExtendedStatusCode())) {
                        MessageManager.getInstance().addWarningMessage("pm.common.warning.message",
                            new String[]{extendedStatus.getExtendedStatusDescription()}, i + "", "");
                    }
                    else if ("Information".equals(extendedStatus.getExtendedStatusCode())) {
                        MessageManager.getInstance().addInfoMessage("pm.common.warning.message",
                            new String[]{extendedStatus.getExtendedStatusDescription()});
                    }
                }
            }
        }

        l.exiting(getClass().getName(), "handleOnMessages", new Object[]{outputRecord});
        return outputRecord;
    }
}
