package dti.pm.policymgr.service.client;


import com.delphi_tech.ows.policyCertificateOfInsuranceChangeService.PolicyCertificateOfInsuranceChangeService;
import com.delphi_tech.ows.policyCertificateOfInsuranceChangeService.PolicyCertificateOfInsuranceChangeService_Service;
import com.delphi_tech.ows.policycertificateofinsurancechangeservice.CertificateHolderType;
import com.delphi_tech.ows.policycertificateofinsurancechangeservice.MedicalMalpracticeCertificateInformationType;
import com.delphi_tech.ows.policycertificateofinsurancechangeservice.MedicalMalpracticeCertificateOfLiabilityChangeRequestType;
import com.delphi_tech.ows.policycertificateofinsurancechangeservice.MedicalMalpracticeCertificateOfLiabilityChangeResultType;
import com.delphi_tech.ows.policycertificateofinsurancechangeservice.ReferredCertificateHolderType;

import javax.xml.ws.BindingProvider;
import java.util.Map;

/**
 * <p>(C) 2017 Delphi Technology, inc. (dti)</p>
 * Date:   9/13/2017
 *
 * @author wrong
 * Test Coi Generation service
 */
public class PolicyCertificateOfInsuranceChangeServiceClient {

    public static void main(String[] args) {
        testPolicyCoiGenerateChangeService();
    }

    public static void testPolicyCoiGenerateChangeService() {
        PolicyCertificateOfInsuranceChangeServiceClient coiChangeServiceClient = new PolicyCertificateOfInsuranceChangeServiceClient();

        MedicalMalpracticeCertificateOfLiabilityChangeRequestType request = new MedicalMalpracticeCertificateOfLiabilityChangeRequestType();
        request.setUserId(userName);

        request.setTransactionEffectiveDate("2018-01-01");
        CertificateHolderType coiType = new CertificateHolderType();
        coiType.setCertificateHolderNumberId("64411915");
        coiType.setKey("CertHold1");

        MedicalMalpracticeCertificateInformationType coiInfo = new MedicalMalpracticeCertificateInformationType();
        ReferredCertificateHolderType referredCoi = new ReferredCertificateHolderType();
        referredCoi.setCertificateHolderReference("CertHold1");
        coiInfo.setReferredCertificateHolder(referredCoi);
        coiInfo.setPolicyTermNumberId("64356812");
        coiInfo.setGenerate("Y");

        request.setMedicalMalpracticeCertificateInformation(coiInfo);
        request.getCertificateHolder().add(coiType);

        MedicalMalpracticeCertificateOfLiabilityChangeResultType result = coiChangeServiceClient.generateCoi(request, userName, password, wsdlLocation);
        System.out.println("Coi Generate Web Service: " + result.getMessageStatus().getMessageStatusCode());


    }

    public MedicalMalpracticeCertificateOfLiabilityChangeResultType generateCoi(MedicalMalpracticeCertificateOfLiabilityChangeRequestType request,
                                                                                String userName, String password, String wsdlLocation) {
        PolicyCertificateOfInsuranceChangeService_Service policyCertificateOfInsuranceChangeService = new PolicyCertificateOfInsuranceChangeService_Service();
        PolicyCertificateOfInsuranceChangeService port = policyCertificateOfInsuranceChangeService.getPolicyCertificateOfInsuranceChangeServicePort();
        Map<String, Object> requestContext = ((BindingProvider) port).getRequestContext();

        String endPointAddress = wsdlLocation.trim();
        if (endPointAddress.toUpperCase().endsWith("?WSDL")) {
            endPointAddress = endPointAddress.substring(0, endPointAddress.length() - 5);
        }
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endPointAddress);
        requestContext.put(BindingProvider.USERNAME_PROPERTY, userName);
        requestContext.put(BindingProvider.PASSWORD_PROPERTY, password);

        MedicalMalpracticeCertificateOfLiabilityChangeResultType result = port.generateCoi(request);

        return result;
    }

    static String userName = "wayne";
    static String password = "p@ssword";
    static String wsdlLocation = "http://10.195.13.119:7202/odev20181/ePolicy/wsPolicy/PolicyCertificateOfInsuranceChangeService?WSDL";

}
