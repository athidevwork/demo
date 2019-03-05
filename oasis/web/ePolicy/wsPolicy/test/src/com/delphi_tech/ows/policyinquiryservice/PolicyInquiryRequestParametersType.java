
package com.delphi_tech.ows.policyinquiryservice;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PolicyInquiryRequestParametersType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PolicyInquiryRequestParametersType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PolicyInquiry" type="{http://www.delphi-tech.com/ows/PolicyInquiryService}PolicyInquiryType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PolicyInquiryRequestParametersType", propOrder = {
    "policyInquiry"
})
public class PolicyInquiryRequestParametersType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "PolicyInquiry", required = true)
    protected PolicyInquiryType policyInquiry;

    /**
     * Gets the value of the policyInquiry property.
     * 
     * @return
     *     possible object is
     *     {@link PolicyInquiryType }
     *     
     */
    public PolicyInquiryType getPolicyInquiry() {
        return policyInquiry;
    }

    /**
     * Sets the value of the policyInquiry property.
     * 
     * @param value
     *     allowed object is
     *     {@link PolicyInquiryType }
     *     
     */
    public void setPolicyInquiry(PolicyInquiryType value) {
        this.policyInquiry = value;
    }

}
