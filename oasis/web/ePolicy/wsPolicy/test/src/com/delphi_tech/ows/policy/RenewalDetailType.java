
package com.delphi_tech.ows.policy;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RenewalDetailType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RenewalDetailType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RenewalIndicator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="NonRenewalReason" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RenewalDetailType", propOrder = {
    "renewalIndicator",
    "nonRenewalReason"
})
public class RenewalDetailType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "RenewalIndicator")
    protected String renewalIndicator;
    @XmlElement(name = "NonRenewalReason")
    protected String nonRenewalReason;

    /**
     * Gets the value of the renewalIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRenewalIndicator() {
        return renewalIndicator;
    }

    /**
     * Sets the value of the renewalIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRenewalIndicator(String value) {
        this.renewalIndicator = value;
    }

    /**
     * Gets the value of the nonRenewalReason property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNonRenewalReason() {
        return nonRenewalReason;
    }

    /**
     * Sets the value of the nonRenewalReason property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNonRenewalReason(String value) {
        this.nonRenewalReason = value;
    }

}
