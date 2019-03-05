
package com.delphi_tech.ows.policyinquiryservice;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PolicyInquiryType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PolicyInquiryType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PolicyTermNumberId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PolicyId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PolicyHolder" type="{http://www.delphi-tech.com/ows/PolicyInquiryService}PolicyHolderType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PolicyInquiryType", propOrder = {
    "policyTermNumberId",
    "policyId",
    "policyHolder"
})
public class PolicyInquiryType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "PolicyTermNumberId")
    protected String policyTermNumberId;
    @XmlElement(name = "PolicyId")
    protected String policyId;
    @XmlElement(name = "PolicyHolder")
    protected PolicyHolderType policyHolder;

    /**
     * Gets the value of the policyTermNumberId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPolicyTermNumberId() {
        return policyTermNumberId;
    }

    /**
     * Sets the value of the policyTermNumberId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPolicyTermNumberId(String value) {
        this.policyTermNumberId = value;
    }

    /**
     * Gets the value of the policyId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPolicyId() {
        return policyId;
    }

    /**
     * Sets the value of the policyId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPolicyId(String value) {
        this.policyId = value;
    }

    /**
     * Gets the value of the policyHolder property.
     * 
     * @return
     *     possible object is
     *     {@link PolicyHolderType }
     *     
     */
    public PolicyHolderType getPolicyHolder() {
        return policyHolder;
    }

    /**
     * Sets the value of the policyHolder property.
     * 
     * @param value
     *     allowed object is
     *     {@link PolicyHolderType }
     *     
     */
    public void setPolicyHolder(PolicyHolderType value) {
        this.policyHolder = value;
    }

}
