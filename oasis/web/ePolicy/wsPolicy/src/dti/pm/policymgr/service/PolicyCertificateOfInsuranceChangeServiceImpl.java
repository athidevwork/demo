package dti.pm.policymgr.service;

import com.delphi_tech.ows.common.ExtendedStatusType;
import com.delphi_tech.ows.common.MessageStatusType;
import com.delphi_tech.ows.policycertificateofinsurancechangeservice.MedicalMalpracticeCertificateOfLiabilityChangeResultType;
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
 * PolicyCertificateOfInsuranceChangeServiceImpl class implements web service endpoint interface
 * PolicyCertificateOfInsuranceChangeService */

@WebService(
    name="PolicyCertificateOfInsuranceChangeService",
    serviceName="PolicyCertificateOfInsuranceChangeService",
    targetNamespace="http://www.delphi-tech.com/ows/PolicyCertificateOfInsuranceChangeService",
    wsdlLocation="/wsdls/PolicyCertificateOfInsuranceChangeService.wsdl")
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL,parameterStyle=SOAPBinding.ParameterStyle.BARE)
public class PolicyCertificateOfInsuranceChangeServiceImpl {

    public static final String SERVICE_MANAGER_BEAN_NAME = "PolicyCertificateOfInsuranceChangeServiceManager";

  public PolicyCertificateOfInsuranceChangeServiceImpl() {

  }

    @WebMethod(    action="generateCoi",
        operationName="generateCoi")
    @WebResult(name="MedicalMalpracticeCertificateOfLiabilityChangeResult", targetNamespace="http://www.delphi-tech.com/ows/PolicyCertificateOfInsuranceChangeService")
    public MedicalMalpracticeCertificateOfLiabilityChangeResultType generateCoi(@WebParam(name = "MedicalMalpracticeCertificateOfLiabilityChangeRequest",
        targetNamespace = "http://www.delphi-tech.com/ows/PolicyCertificateOfInsuranceChangeService") com.delphi_tech.ows.policycertificateofinsurancechangeservice.MedicalMalpracticeCertificateOfLiabilityChangeRequestType medicalMalpracticeCertificateOfLiabilityChangeRequest)
    {

    MedicalMalpracticeCertificateOfLiabilityChangeResultType medicalMalpracticeCertificateOfLiabilityChangeResultType = new MedicalMalpracticeCertificateOfLiabilityChangeResultType();

      if (medicalMalpracticeCertificateOfLiabilityChangeRequest.getUserId() == null || medicalMalpracticeCertificateOfLiabilityChangeRequest.getUserId().length() == 0) {
          MessageStatusType messageStatusType = new MessageStatusType();
          messageStatusType.setMessageStatusCode(MessageStatusHelper.STATUS_CODE_REJECTED);
          List<ExtendedStatusType> extendedStatusTypes = new ArrayList<ExtendedStatusType>();
          ExtendedStatusType extendedStatusType = new ExtendedStatusType();
          extendedStatusType.setExtendedStatusDescription("UserId is required.");
          extendedStatusTypes.add(extendedStatusType);
          messageStatusType.getExtendedStatus().addAll(extendedStatusTypes);
          medicalMalpracticeCertificateOfLiabilityChangeResultType.setMessageStatus(messageStatusType);
      }
      else {
          WebServiceHelper.getInstance().setWebServiceUser(medicalMalpracticeCertificateOfLiabilityChangeRequest.getUserId());

          PolicyCertificateOfInsuranceChangeServiceManager policyCertificateOfInsuranceChangeServiceManager = (PolicyCertificateOfInsuranceChangeServiceManager) ApplicationContext.getInstance().getBean(SERVICE_MANAGER_BEAN_NAME);
          medicalMalpracticeCertificateOfLiabilityChangeResultType = policyCertificateOfInsuranceChangeServiceManager.generateCoi(medicalMalpracticeCertificateOfLiabilityChangeRequest);
      }

      return medicalMalpracticeCertificateOfLiabilityChangeResultType;

  }
}  