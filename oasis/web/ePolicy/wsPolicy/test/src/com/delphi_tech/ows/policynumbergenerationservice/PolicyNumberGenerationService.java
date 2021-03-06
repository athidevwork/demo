
package com.delphi_tech.ows.policynumbergenerationservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.8
 * Generated source version: 2.2
 * 
 */
@WebService(name = "PolicyNumberGenerationService", targetNamespace = "http://www.delphi-tech.com/ows/PolicyNumberGenerationService")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    com.delphi_tech.ows.common.ObjectFactory.class,
    ObjectFactory.class
})
public interface PolicyNumberGenerationService {


    /**
     * 
     * @param policyNumberGenerationRequest
     * @return
     *     returns com.delphi_tech.ows.PolicyNumberGenerationService.PolicyNumberGenerationResultType
     */
    @WebMethod(action = "generatePolicyNumber")
    @WebResult(name = "PolicyNumberGenerationResult", targetNamespace = "http://www.delphi-tech.com/ows/PolicyNumberGenerationService", partName = "PolicyNumberGenerationResult")
    public PolicyNumberGenerationResultType generatePolicyNumber(
        @WebParam(name = "PolicyNumberGenerationRequest", targetNamespace = "http://www.delphi-tech.com/ows/PolicyNumberGenerationService", partName = "PolicyNumberGenerationRequest")
        PolicyNumberGenerationRequestType policyNumberGenerationRequest);

}
