package dti.pm.billingmgr.service;

import com.delphi_tech.ows.account.PrincipalBillingAccountInformationType;
import com.delphi_tech.ows.billingaccountinquiryservice.BillingAccountInquiryRequestParametersType;
import com.delphi_tech.ows.billingaccountinquiryservice.BillingAccountInquiryRequestType;
import com.delphi_tech.ows.billingaccountinquiryservice.BillingAccountInquiryResultParametersType;
import com.delphi_tech.ows.billingaccountinquiryservice.BillingAccountInquiryResultType;
import com.delphi_tech.ows.billingaccountinquiryservice.BillingAccountInquiryService;
import com.delphi_tech.ows.billingaccountinquiryservice.BillingAccountInquiryService_Service;
import com.delphi_tech.ows.billingaccountinquiryservice.BillingAccountInquiryType;
import com.delphi_tech.ows.billingaccountinquiryservice.FilterType;
import com.delphi_tech.ows.billingaccountinquiryservice.PolicyInquiryType;
import dti.oasis.recordset.Record;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.request.service.WebServiceClientHelper;
import dti.oasis.session.UserSessionManager;
import dti.oasis.struts.IOasisAction;
import dti.oasis.util.LogUtils;
import dti.oasis.util.OasisUser;
import dti.pm.billingmgr.BillingAccountInquiryWSClientManager;
import dti.pm.billingmgr.BillingFields;
import dti.pm.policymgr.PolicyHeader;

import javax.xml.namespace.QName;
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
 * 05/29/2018      fhuang       193406 - use minimal and excludeparty view and filterpolicyno to return acoount info only.
 * ---------------------------------------------------
 */

public class BillingAccountInquiryWSClientManagerImpl implements BillingAccountInquiryWSClientManager {
    public static final QName SERVICE_NAME = new QName("http://www.delphi-tech.com/ows/BillingAccountInquiryService", "BillingAccountInquiryService");
    /**
     * Call webService to load the billing data.
     * @param policyHeader
     * @param inputRecord
     * @return
     */
    public boolean isBillingExists(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(this.getClass(), "isBillingExists", new Object[]{policyHeader, inputRecord});

        BillingAccountInquiryRequestType requestType = recordToXML(policyHeader, inputRecord);
        String accountNumber = inputRecord.getStringValue("accountNo");
        BillingAccountInquiryResultType resultType = billingAccountInquiryRequest(requestType);
        List<PrincipalBillingAccountInformationType> billingAccountList = resultType.getPrincipalBillingAccountInformation();
        for (PrincipalBillingAccountInformationType billingAccount : billingAccountList) {
            if (accountNumber.equals(billingAccount.getBillingAccountId())) {
                if (billingAccount.getBillingAccountDetail() == null) {
                    return false;
                }
                else {
                    RequestStorageManager.getInstance().set("InquiryBilling", billingAccount);
                    break;
                }
            }
        }

        l.exiting(getClass().getName(), "getInitialValuesForBilling", new Object[]{});
        return true;
    }

    /**
     * Call BillingAccountInquiryService.
     * @param billingAccountInquiryRequestType
     * @return
     */
    public BillingAccountInquiryResultType billingAccountInquiryRequest
        (BillingAccountInquiryRequestType billingAccountInquiryRequestType) {
        Logger l = LogUtils.enterLog(this.getClass(), "billingAccountInquiryRequest", new Object[]{billingAccountInquiryRequestType});

        String endPointURL = RequestStorageManager.getInstance().get("FM_SERVICE_PATH") + "/BillingAccountInquiryService";
        String userName = WebServiceClientHelper.getInstance().getOWSUserName();
        String password = WebServiceClientHelper.getInstance().getOWSPassword();

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "billingAccountInquiryRequest", endPointURL);
            l.exiting(getClass().getName(), "billingAccountInquiryRequest", userName + "/" + password);
        }

        BillingAccountInquiryService_Service service = new BillingAccountInquiryService_Service();
        BillingAccountInquiryService port = service.getBillingAccountInquiryServicePort();
        Map<String, Object> requestContext = ((BindingProvider) port).getRequestContext();
        requestContext.put(BindingProvider.USERNAME_PROPERTY, userName);
        requestContext.put(BindingProvider.PASSWORD_PROPERTY, password);
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endPointURL);

        BillingAccountInquiryResultType result = port.billingAccountInquiryRequest(billingAccountInquiryRequestType);

        l.exiting(getClass().getName(), "billingAccountInquiryRequest", new Object[]{result});
        return result;
    }

    /**
     * Map the data from OASIS records to XML objects.
     * @param policyHeader
     * @param inputRecord
     * @return
     */
    public BillingAccountInquiryRequestType recordToXML(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(this.getClass(), "recordToXML", new Object[]{policyHeader, inputRecord});

        OasisUser user = (OasisUser) UserSessionManager.getInstance().getUserSession().get(IOasisAction.KEY_OASISUSER);
        String userId = user.getUserId();
        BillingAccountInquiryRequestType requestType = new BillingAccountInquiryRequestType();
        requestType.setUserId(userId);

        BillingAccountInquiryType accountInquiryType = new BillingAccountInquiryType();
        accountInquiryType.setBillingAccountId(inputRecord.getStringValue("accountNo"));

        BillingAccountInquiryRequestParametersType requestParametersType = new BillingAccountInquiryRequestParametersType();
        requestParametersType.setBillingAccountInquiry(accountInquiryType);
        requestType.getBillingAccountInquiryRequestParameters().add(requestParametersType);
        BillingAccountInquiryResultParametersType resultParametersType = new BillingAccountInquiryResultParametersType();
        resultParametersType.getViewName().add(BillingFields.MINIMAL_VIEW);
        resultParametersType.getViewName().add(BillingFields.EXCLUDE_PARTY_VIEW);
        PolicyInquiryType policyInquiryType = new PolicyInquiryType();
        policyInquiryType.setPolicyId(policyHeader.getPolicyNo());
        FilterType filterType = new FilterType();
        filterType.setPolicyInquiry(policyInquiryType);
        resultParametersType.setFilter(filterType);
        requestType.setBillingAccountInquiryResultParameters(resultParametersType);

        l.exiting(getClass().getName(), "billingAccountInquiryRequest", new Object[]{requestType});
        return requestType;
    }
}
