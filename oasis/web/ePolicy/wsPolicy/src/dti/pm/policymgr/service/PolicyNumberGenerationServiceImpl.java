package dti.pm.policymgr.service;

import com.delphi_tech.ows.common.ExtendedStatusType;
import com.delphi_tech.ows.common.MessageStatusType;
import com.delphi_tech.ows.policynumbergenerationservice.PolicyNumberGenerationResultType;
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
 * PolicyNumberGenerationServiceImpl class implements web service endpoint interface PolicyNumberGenerationService */

@WebService(
    name="PolicyNumberGenerationService",
    serviceName="PolicyNumberGenerationService",
    targetNamespace="http://www.delphi-tech.com/ows/PolicyNumberGenerationService",
    wsdlLocation="/wsdls/PolicyNumberGenerationService.wsdl")
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL,parameterStyle=SOAPBinding.ParameterStyle.BARE)
public class PolicyNumberGenerationServiceImpl {

    public static final String SERVICE_MANAGER_BEAN_NAME = "PolicyNumberGenerationServiceManager";

  public PolicyNumberGenerationServiceImpl() {

  }

    @WebMethod(    action="generatePolicyNumber",
        operationName="generatePolicyNumber")
    @WebResult(name="PolicyNumberGenerationResult", targetNamespace="http://www.delphi-tech.com/ows/PolicyNumberGenerationService")
    public PolicyNumberGenerationResultType generatePolicyNumber(@WebParam(name = "PolicyNumberGenerationRequest", targetNamespace = "http://www.delphi-tech.com/ows/PolicyNumberGenerationService") com.delphi_tech.ows.policynumbergenerationservice.PolicyNumberGenerationRequestType policyNumberGenerationRequest)
     
  {

      PolicyNumberGenerationResultType policyNumberGenerationResult = new PolicyNumberGenerationResultType();

      if (policyNumberGenerationRequest.getUserId() == null || policyNumberGenerationRequest.getUserId().length() == 0) {
          MessageStatusType messageStatusType = new MessageStatusType();
          messageStatusType.setMessageStatusCode(MessageStatusHelper.STATUS_CODE_REJECTED);
          List<ExtendedStatusType> extendedStatusTypes = new ArrayList<ExtendedStatusType>();
          ExtendedStatusType extendedStatusType = new ExtendedStatusType();
          extendedStatusType.setExtendedStatusDescription("UserId is required.");
          extendedStatusTypes.add(extendedStatusType);
          messageStatusType.getExtendedStatus().addAll(extendedStatusTypes);
          policyNumberGenerationResult.setMessageStatus(messageStatusType);
      }
      else {
          WebServiceHelper.getInstance().setWebServiceUser(policyNumberGenerationRequest.getUserId());

          PolicyNumberGenerationServiceManager policyNumberGenerationServiceManager = (PolicyNumberGenerationServiceManager) ApplicationContext.getInstance().getBean(SERVICE_MANAGER_BEAN_NAME);
          policyNumberGenerationResult = policyNumberGenerationServiceManager.generatePolicyNumber(policyNumberGenerationRequest);
      }

      return policyNumberGenerationResult;

  }
}  