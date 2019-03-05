package dti.pm.policymgr.service.client;

import com.delphi_tech.ows.policynumbergenerationservice.PolicyNumberGenerationService;
import com.delphi_tech.ows.policynumbergenerationservice.PolicyNumberGenerationService_Service;
import com.delphi_tech.ows.policynumbergenerationservice.IssueCompanyType;
import com.delphi_tech.ows.policynumbergenerationservice.PolicyNumberGenerationRequestParametersType;
import com.delphi_tech.ows.policynumbergenerationservice.PolicyNumberGenerationRequestType;
import com.delphi_tech.ows.policynumbergenerationservice.PolicyNumberGenerationResultType;
import com.delphi_tech.ows.policynumbergenerationservice.ReferredPartyType;

import javax.xml.ws.BindingProvider;
import java.util.Map;

/**
 * Test Policy Number Generation service
 */
public class PolicyNumberGenerationServiceClient {

    public static void main(String[] s){
        testPolicyNumberGeneration();
    }

    public static void testPolicyNumberGeneration() {
        PolicyNumberGenerationServiceClient serviceClient = new PolicyNumberGenerationServiceClient();

        PolicyNumberGenerationRequestType request = new PolicyNumberGenerationRequestType();
        request.setUserId(userName);
        PolicyNumberGenerationRequestParametersType requestParameters = new PolicyNumberGenerationRequestParametersType();
        requestParameters.setPolicyTypeCode("MP");
        requestParameters.setPolicyCycleCode("POLICY");

        IssueCompanyType issueCompany = new IssueCompanyType();

        ReferredPartyType referredPartyType = new ReferredPartyType();
        referredPartyType.setPartyNumberId("1");

        issueCompany.setReferredParty(referredPartyType);
        issueCompany.setControllingStateOrProvinceCode("NY");
        issueCompany.setProcessLocationCode("NEWYORKNY");

        requestParameters.setIssueCompany(issueCompany);

        request.setPolicyNumberGenerationRequestParameters(requestParameters);


        PolicyNumberGenerationResultType resultType = serviceClient.generatePolicyNumber(request, userName, password, wsdlLocation);
        System.out.println("New policy number: " + resultType.getPolicyNumber());
    }

    public PolicyNumberGenerationResultType generatePolicyNumber(PolicyNumberGenerationRequestType policyNumberGenerationRequestType,
                                                        String userName, String password, String wsdlLocation){
        PolicyNumberGenerationService_Service policyNumberGenerationService = new PolicyNumberGenerationService_Service();
        PolicyNumberGenerationService port = policyNumberGenerationService.getPolicyNumberGenerationServicePort();
        Map<String, Object> requestContext = ((BindingProvider) port).getRequestContext();

        String endPointAddress = wsdlLocation.trim();
        if (endPointAddress.toUpperCase().endsWith("?WSDL")) {
            endPointAddress = endPointAddress.substring(0, endPointAddress.length() - 5);
        }
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endPointAddress);

        requestContext.put(BindingProvider.USERNAME_PROPERTY, userName);
        requestContext.put(BindingProvider.PASSWORD_PROPERTY, password);

        PolicyNumberGenerationResultType policyNumberGenerationResultType = port.generatePolicyNumber(policyNumberGenerationRequestType);

        return policyNumberGenerationResultType;
    }

    static String userName = "eyin";
    static String password = "p@ssword";
    static String wsdlLocation = "http://10.195.13.73:6001/odev20171/ePolicy/wsPolicy/PolicyNumberGenerationService?WSDL";
}
