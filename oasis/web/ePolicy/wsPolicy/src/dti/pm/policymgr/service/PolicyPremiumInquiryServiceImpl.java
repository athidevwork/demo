package dti.pm.policymgr.service;

import com.delphi_tech.ows.common.ExtendedStatusType;
import com.delphi_tech.ows.common.MessageStatusType;
import com.delphi_tech.ows.policypremiuminquiryservice.PolicyPremiumInquiryResultType;
import dti.oasis.app.ApplicationContext;
import dti.oasis.request.service.WebServiceHelper;
import dti.ows.common.MessageStatusHelper;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * This class was generated by <I>wsdlc</I> on <I>Wed Jan 16 13:43:17 CST 2013</I>.
 *
 */

@WebService(
    name="PolicyPremiumInquiryService",
    serviceName="PolicyPremiumInquiryService",
    targetNamespace="http://www.delphi-tech.com/ows/PolicyPremiumInquiryService",
    wsdlLocation="/wsdls/PolicyPremiumInquiryService.wsdl")
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL,parameterStyle=SOAPBinding.ParameterStyle.BARE)
public class PolicyPremiumInquiryServiceImpl {

    public static final String SERVICE_MANAGER_BEAN_NAME = "PolicyPremiumInquiryServiceManager";

    public PolicyPremiumInquiryServiceImpl(){

    }

    /**
     * <B>policyPremiumInquiryRequest</B> is exposed as a Web Service Operation
     *
     */
    @WebMethod(    action="policyPremiumInquiryRequest",
        operationName="policyPremiumInquiryRequest")
    @WebResult(name="PolicyPremiumInquiryResult", targetNamespace="http://www.delphi-tech.com/ows/PolicyPremiumInquiryService")
    public com.delphi_tech.ows.policypremiuminquiryservice.PolicyPremiumInquiryResultType policyPremiumInquiryRequest(@WebParam(name="PolicyPremiumInquiryRequest", targetNamespace="http://www.delphi-tech.com/ows/PolicyPremiumInquiryService", partName="policyPremiumInquiryRequest") com.delphi_tech.ows.policypremiuminquiryservice.PolicyPremiumInquiryRequestType policyPremiumInquiryRequest){

        PolicyPremiumInquiryResultType policyPremiumInquiryResult = new PolicyPremiumInquiryResultType();

        if (policyPremiumInquiryRequest.getUserId() == null || policyPremiumInquiryRequest.getUserId().length() == 0) {
            MessageStatusType messageStatusType = new MessageStatusType();
            messageStatusType.setMessageStatusCode(MessageStatusHelper.STATUS_CODE_REJECTED);
            List<ExtendedStatusType> extendedStatusTypes = new ArrayList<ExtendedStatusType>();
            ExtendedStatusType extendedStatusType = new ExtendedStatusType();
            extendedStatusType.setExtendedStatusDescription("UserId is required.");
            extendedStatusTypes.add(extendedStatusType);
            messageStatusType.getExtendedStatus().addAll(extendedStatusTypes);
            policyPremiumInquiryResult.setMessageStatus(messageStatusType);
        }
        else {
            WebServiceHelper.getInstance().setWebServiceUser(policyPremiumInquiryRequest.getUserId());

            PolicyPremiumInquiryServiceManager policyPremiumInquiryServiceManager = (PolicyPremiumInquiryServiceManager) ApplicationContext.getInstance().getBean(SERVICE_MANAGER_BEAN_NAME);
            policyPremiumInquiryResult = policyPremiumInquiryServiceManager.loadPremium(policyPremiumInquiryRequest);
        }

        return policyPremiumInquiryResult;
    }
}
