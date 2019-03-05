package dti.pm.policymgr.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import com.delphi_tech.ows.common.ExtendedStatusType;
import com.delphi_tech.ows.common.MessageStatusType;
import com.delphi_tech.ows.policychangeservice.MedicalMalpracticePolicyChangeResultType;
import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.request.service.WebServiceHelper;
import dti.oasis.util.LogUtils;
import dti.ows.common.MessageStatusAppException;
import dti.ows.common.MessageStatusHelper;
import dti.pm.policymgr.service.impl.PolicyChangeServiceSaveProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PolicyChangeServiceImpl class implements web service endpoint interface PolicyChangeService */

@WebService(
    name="PolicyChangeService",
    serviceName="PolicyChangeService",
    targetNamespace="http://www.delphi-tech.com/ows/PolicyChangeService",
    wsdlLocation="/wsdls/PolicyChangeService.wsdl")
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL,parameterStyle=SOAPBinding.ParameterStyle.BARE)
public class PolicyChangeServiceImpl {
    public static final String SERVICE_MANAGER_BEAN_NAME = "PolicyChangeServiceManager";

  public PolicyChangeServiceImpl() {
  
  }

    @WebMethod(    action="policyChangeRequest",
        operationName="policyChangeRequest")
    @WebResult(name="MedicalMalpracticePolicyChangeResult", targetNamespace="http://www.delphi-tech.com/ows/PolicyChangeService")
    public com.delphi_tech.ows.policychangeservice.MedicalMalpracticePolicyChangeResultType policyChangeRequest(@WebParam(name = "MedicalMalpracticePolicyChangeRequest", targetNamespace = "http://www.delphi-tech.com/ows/PolicyChangeService") com.delphi_tech.ows.policychangeservice.MedicalMalpracticePolicyChangeRequestType policyChangeRequest)
     
  {
      Logger l = LogUtils.getLogger(getClass());
      if (l.isLoggable(Level.FINER)) {
          l.entering(getClass().getName(), "policyChangeRequest", new Object[]{policyChangeRequest});
      }

      MedicalMalpracticePolicyChangeResultType policyChangeResult = new MedicalMalpracticePolicyChangeResultType();
      if (policyChangeRequest.getUserId() == null || policyChangeRequest.getUserId().length() == 0) {
          MessageStatusType messageStatusType = new MessageStatusType();
          messageStatusType.setMessageStatusCode(MessageStatusHelper.STATUS_CODE_REJECTED);
          List<ExtendedStatusType> extendedStatusTypes = new ArrayList<ExtendedStatusType>();
          ExtendedStatusType extendedStatusType = new ExtendedStatusType();
          extendedStatusType.setExtendedStatusDescription("UserId is required.");
          extendedStatusTypes.add(extendedStatusType);
          messageStatusType.getExtendedStatus().addAll(extendedStatusTypes);
          policyChangeResult.setMessageStatus(messageStatusType);
      }
      else {
          WebServiceHelper.getInstance().setWebServiceUser(policyChangeRequest.getUserId());

          try {
              PolicyChangeServiceSaveProcessor policyChangeServiceSaveProcessor = (PolicyChangeServiceSaveProcessor) ApplicationContext.getInstance().getBean(SERVICE_MANAGER_BEAN_NAME);
              policyChangeServiceSaveProcessor.changePolicy(policyChangeRequest, policyChangeResult);
          } catch (MessageStatusAppException wsae) {
              policyChangeResult.setMessageStatus(wsae.getMessageStatus());
              l.logp(Level.SEVERE, getClass().getName(), "policyChangeRequest", wsae.getMessage(), wsae);
          } catch (Exception e) {
              AppException ae = ExceptionHelper.getInstance().handleException("Failure invoking the PolicyChangeServiceImpl. ", e);
              //policyChangeResult.setMessageStatus(MessageStatusHelper.getInstance().getRejectedMessageStatus(ae));
              l.logp(Level.SEVERE, getClass().getName(), "policyChangeRequest", ae.getMessage(), ae);
          }
      }

      if (l.isLoggable(Level.FINER)) {
          l.exiting(getClass().getName(), "policyChangeRequest", policyChangeResult);
      }

      return policyChangeResult;
  }
}  