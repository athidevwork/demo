
package com.delphi_tech.ows.policy;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClaimMadeLiabilityPolicyInformationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClaimMadeLiabilityPolicyInformationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CurrentRetroactiveDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SplitRetroactiveDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ClaimsMadeDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClaimMadeLiabilityPolicyInformationType", propOrder = {
    "currentRetroactiveDate",
    "splitRetroactiveDate",
    "claimsMadeDate"
})
public class ClaimMadeLiabilityPolicyInformationType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "CurrentRetroactiveDate")
    protected String currentRetroactiveDate;
    @XmlElement(name = "SplitRetroactiveDate")
    protected String splitRetroactiveDate;
    @XmlElement(name = "ClaimsMadeDate")
    protected String claimsMadeDate;

    /**
     * Gets the value of the currentRetroactiveDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCurrentRetroactiveDate() {
        return currentRetroactiveDate;
    }

    /**
     * Sets the value of the currentRetroactiveDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCurrentRetroactiveDate(String value) {
        this.currentRetroactiveDate = value;
    }

    /**
     * Gets the value of the splitRetroactiveDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSplitRetroactiveDate() {
        return splitRetroactiveDate;
    }

    /**
     * Sets the value of the splitRetroactiveDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSplitRetroactiveDate(String value) {
        this.splitRetroactiveDate = value;
    }

    /**
     * Gets the value of the claimsMadeDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClaimsMadeDate() {
        return claimsMadeDate;
    }

    /**
     * Sets the value of the claimsMadeDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClaimsMadeDate(String value) {
        this.claimsMadeDate = value;
    }

}
