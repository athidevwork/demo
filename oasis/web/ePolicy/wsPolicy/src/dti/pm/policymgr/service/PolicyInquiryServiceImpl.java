package dti.pm.policymgr.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import com.delphi_tech.ows.common.ExtendedStatusType;
import com.delphi_tech.ows.common.MessageStatusType;
import com.delphi_tech.ows.policyinquiryservice.PolicyInquiryResultType;
import dti.oasis.app.ApplicationContext;
import dti.oasis.request.service.WebServiceHelper;
import dti.ows.common.MessageStatusHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * PolicyInquiryServiceImpl class implements web service endpoint interface PolicyInquiryService */

@WebService(
    name="PolicyInquiryService",
    serviceName="PolicyInquiryService",
    targetNamespace="http://www.delphi-tech.com/ows/PolicyInquiryService",
    wsdlLocation="/wsdls/PolicyInquiryService.wsdl")
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL,parameterStyle=SOAPBinding.ParameterStyle.BARE)
public class PolicyInquiryServiceImpl {

    public static final String SERVICE_MANAGER_BEAN_NAME = "PolicyInquiryServiceManager";

  public PolicyInquiryServiceImpl() {
  
  }

    @WebMethod(    action="policyInquiryRequest",
        operationName="policyInquiryRequest")
    @WebResult(name="PolicyInquiryResult", targetNamespace="http://www.delphi-tech.com/ows/PolicyInquiryService")
    public com.delphi_tech.ows.policyinquiryservice.PolicyInquiryResultType policyInquiryRequest(@WebParam(name = "PolicyInquiryRequest", targetNamespace = "http://www.delphi-tech.com/ows/PolicyInquiryService") com.delphi_tech.ows.policyinquiryservice.PolicyInquiryRequestType policyInquiryRequest)
     
  {

      PolicyInquiryResultType policyInquiryResult = new PolicyInquiryResultType();

      if (policyInquiryRequest.getUserId() == null || policyInquiryRequest.getUserId().length() == 0) {
          MessageStatusType messageStatusType = new MessageStatusType();
          messageStatusType.setMessageStatusCode(MessageStatusHelper.STATUS_CODE_REJECTED);
          List<ExtendedStatusType> extendedStatusTypes = new ArrayList<ExtendedStatusType>();
          ExtendedStatusType extendedStatusType = new ExtendedStatusType();
          extendedStatusType.setExtendedStatusDescription("UserId is required.");
          extendedStatusTypes.add(extendedStatusType);
          messageStatusType.getExtendedStatus().addAll(extendedStatusTypes);
          policyInquiryResult.setMessageStatus(messageStatusType);
      }
      else {
          WebServiceHelper.getInstance().setWebServiceUser(policyInquiryRequest.getUserId());

          PolicyInquiryServiceManager policyInquiryServiceManager = (PolicyInquiryServiceManager) ApplicationContext.getInstance().getBean(SERVICE_MANAGER_BEAN_NAME);
          policyInquiryResult = policyInquiryServiceManager.loadPolicy(policyInquiryRequest);
      }

      return policyInquiryResult;

  }
}  