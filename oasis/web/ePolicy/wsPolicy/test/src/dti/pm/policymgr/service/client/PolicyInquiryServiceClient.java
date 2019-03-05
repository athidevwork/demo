package dti.pm.policymgr.service.client;

import com.delphi_tech.ows.policyinquiryservice.PolicyInquiryRequestParametersType;
import com.delphi_tech.ows.policyinquiryservice.PolicyInquiryRequestType;
import com.delphi_tech.ows.policyinquiryservice.PolicyInquiryResultType;
import com.delphi_tech.ows.policyinquiryservice.PolicyInquiryService;
import com.delphi_tech.ows.policyinquiryservice.PolicyInquiryService_Service;
import com.delphi_tech.ows.policyinquiryservice.PolicyInquiryType;

import javax.xml.ws.BindingProvider;
import java.util.Map;

/**
 * Test Policy Inquiry Web Service
 */
public class PolicyInquiryServiceClient {

    public static void main(String[] s){
        testPolicyInquiry();
    }

    public static void testPolicyInquiry() {
        PolicyInquiryServiceClient serviceClient = new PolicyInquiryServiceClient();

        PolicyInquiryRequestType request = new PolicyInquiryRequestType();
        request.setUserId(userName);
        PolicyInquiryRequestParametersType requestParameters = new PolicyInquiryRequestParametersType();
        PolicyInquiryType policy = new PolicyInquiryType();
        policy.setPolicyId("MP0713443");
        request.getPolicyInquiryRequestParameters().add(requestParameters);
        requestParameters.setPolicyInquiry(policy);

        PolicyInquiryResultType resultType = serviceClient.policyInquiryRequest(request, userName, password, wsdlLocation);
        System.out.println("Address size: " + resultType.getAddress().size());
        System.out.println("Person size: " + resultType.getPerson().size());
        System.out.println("Organization size: " + resultType.getOrganization().size());
        System.out.println("Policy size: " + resultType.getMedicalMalpracticePolicy().size());
        if (resultType.getMedicalMalpracticePolicy().size()>0) {
            System.out.println("Policy Inquiry Web Service invoked successfully.");
        }
    }

    public PolicyInquiryResultType policyInquiryRequest(PolicyInquiryRequestType policyInquiryRequestType,
                                                        String userName, String password, String wsdlLocation){
        PolicyInquiryService_Service policyInquiryService = new PolicyInquiryService_Service();
        PolicyInquiryService port = policyInquiryService.getPolicyInquiryServicePort();
        Map<String, Object> requestContext = ((BindingProvider) port).getRequestContext();

        String endPointAddress = wsdlLocation.trim();
        if (endPointAddress.toUpperCase().endsWith("?WSDL")) {
            endPointAddress = endPointAddress.substring(0, endPointAddress.length() - 5);
        }
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endPointAddress);

        requestContext.put(BindingProvider.USERNAME_PROPERTY, userName);
        requestContext.put(BindingProvider.PASSWORD_PROPERTY, password);

        PolicyInquiryResultType policyInquiryResultType = port.policyInquiryRequest(policyInquiryRequestType);

        return policyInquiryResultType;
    }

    static String userName = "odev20161";
    static String password = "p@ssword";
    static String wsdlLocation = "http://nj-odev2015.ad.dti:8084/odev20161/ePolicy/wsPolicy/PolicyInquiryService?WSDL";
}
