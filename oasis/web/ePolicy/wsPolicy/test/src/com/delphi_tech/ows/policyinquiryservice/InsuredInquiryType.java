
package com.delphi_tech.ows.policyinquiryservice;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for InsuredInquiryType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InsuredInquiryType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Insured" type="{http://www.delphi-tech.com/ows/PolicyInquiryService}InsuredType" minOccurs="0"/>
 *         &lt;element name="PrimaryIndicator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InsuredInquiryType", propOrder = {
    "insured",
    "primaryIndicator"
})
public class InsuredInquiryType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "Insured")
    protected InsuredType insured;
    @XmlElement(name = "PrimaryIndicator")
    protected String primaryIndicator;

    /**
     * Gets the value of the insured property.
     * 
     * @return
     *     possible object is
     *     {@link InsuredType }
     *     
     */
    public InsuredType getInsured() {
        return insured;
    }

    /**
     * Sets the value of the insured property.
     * 
     * @param value
     *     allowed object is
     *     {@link InsuredType }
     *     
     */
    public void setInsured(InsuredType value) {
        this.insured = value;
    }

    /**
     * Gets the value of the primaryIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrimaryIndicator() {
        return primaryIndicator;
    }

    /**
     * Sets the value of the primaryIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrimaryIndicator(String value) {
        this.primaryIndicator = value;
    }

}
