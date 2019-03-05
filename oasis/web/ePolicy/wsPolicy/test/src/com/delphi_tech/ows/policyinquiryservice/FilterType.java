
package com.delphi_tech.ows.policyinquiryservice;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FilterType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FilterType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="InsuredInquiry" type="{http://www.delphi-tech.com/ows/PolicyInquiryService}InsuredInquiryType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FilterType", propOrder = {
    "insuredInquiry"
})
public class FilterType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "InsuredInquiry")
    protected InsuredInquiryType insuredInquiry;

    /**
     * Gets the value of the insuredInquiry property.
     * 
     * @return
     *     possible object is
     *     {@link InsuredInquiryType }
     *     
     */
    public InsuredInquiryType getInsuredInquiry() {
        return insuredInquiry;
    }

    /**
     * Sets the value of the insuredInquiry property.
     * 
     * @param value
     *     allowed object is
     *     {@link InsuredInquiryType }
     *     
     */
    public void setInsuredInquiry(InsuredInquiryType value) {
        this.insuredInquiry = value;
    }

}
