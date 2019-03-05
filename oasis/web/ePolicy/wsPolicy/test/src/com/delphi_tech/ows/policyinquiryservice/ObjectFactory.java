
package com.delphi_tech.ows.policyinquiryservice;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.delphi_tech.ows.policyinquiryservice package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _PolicyInquiryRequest_QNAME = new QName("http://www.delphi-tech.com/ows/PolicyInquiryService", "PolicyInquiryRequest");
    private final static QName _PolicyInquiryResult_QNAME = new QName("http://www.delphi-tech.com/ows/PolicyInquiryService", "PolicyInquiryResult");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.delphi_tech.ows.policyinquiryservice
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PolicyInquiryRequestType }
     * 
     */
    public PolicyInquiryRequestType createPolicyInquiryRequestType() {
        return new PolicyInquiryRequestType();
    }

    /**
     * Create an instance of {@link PolicyInquiryResultType }
     * 
     */
    public PolicyInquiryResultType createPolicyInquiryResultType() {
        return new PolicyInquiryResultType();
    }

    /**
     * Create an instance of {@link InsuredType }
     * 
     */
    public InsuredType createInsuredType() {
        return new InsuredType();
    }

    /**
     * Create an instance of {@link PolicyInquiryResultParametersType }
     * 
     */
    public PolicyInquiryResultParametersType createPolicyInquiryResultParametersType() {
        return new PolicyInquiryResultParametersType();
    }

    /**
     * Create an instance of {@link FilterType }
     * 
     */
    public FilterType createFilterType() {
        return new FilterType();
    }

    /**
     * Create an instance of {@link PartyNameType }
     * 
     */
    public PartyNameType createPartyNameType() {
        return new PartyNameType();
    }

    /**
     * Create an instance of {@link PolicyInquiryType }
     * 
     */
    public PolicyInquiryType createPolicyInquiryType() {
        return new PolicyInquiryType();
    }

    /**
     * Create an instance of {@link PolicyHolderType }
     * 
     */
    public PolicyHolderType createPolicyHolderType() {
        return new PolicyHolderType();
    }

    /**
     * Create an instance of {@link PolicyInquiryRequestParametersType }
     * 
     */
    public PolicyInquiryRequestParametersType createPolicyInquiryRequestParametersType() {
        return new PolicyInquiryRequestParametersType();
    }

    /**
     * Create an instance of {@link InsuredInquiryType }
     * 
     */
    public InsuredInquiryType createInsuredInquiryType() {
        return new InsuredInquiryType();
    }

    /**
     * Create an instance of {@link PartyType }
     * 
     */
    public PartyType createPartyType() {
        return new PartyType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PolicyInquiryRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.delphi-tech.com/ows/PolicyInquiryService", name = "PolicyInquiryRequest")
    public JAXBElement<PolicyInquiryRequestType> createPolicyInquiryRequest(PolicyInquiryRequestType value) {
        return new JAXBElement<PolicyInquiryRequestType>(_PolicyInquiryRequest_QNAME, PolicyInquiryRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PolicyInquiryResultType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.delphi-tech.com/ows/PolicyInquiryService", name = "PolicyInquiryResult")
    public JAXBElement<PolicyInquiryResultType> createPolicyInquiryResult(PolicyInquiryResultType value) {
        return new JAXBElement<PolicyInquiryResultType>(_PolicyInquiryResult_QNAME, PolicyInquiryResultType.class, null, value);
    }

}
