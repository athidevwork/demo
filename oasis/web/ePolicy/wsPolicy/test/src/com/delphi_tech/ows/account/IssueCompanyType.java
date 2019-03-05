
package com.delphi_tech.ows.account;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for IssueCompanyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IssueCompanyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ReferredParty" type="{http://www.delphi-tech.com/ows/Account}ReferredPartyType"/>
 *         &lt;element name="ControllingStateOrProvinceCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IssueCompanyType", propOrder = {
    "referredParty",
    "controllingStateOrProvinceCode"
})
public class IssueCompanyType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "ReferredParty", required = true)
    protected ReferredPartyType referredParty;
    @XmlElement(name = "ControllingStateOrProvinceCode")
    protected String controllingStateOrProvinceCode;

    /**
     * Gets the value of the referredParty property.
     * 
     * @return
     *     possible object is
     *     {@link ReferredPartyType }
     *     
     */
    public ReferredPartyType getReferredParty() {
        return referredParty;
    }

    /**
     * Sets the value of the referredParty property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferredPartyType }
     *     
     */
    public void setReferredParty(ReferredPartyType value) {
        this.referredParty = value;
    }

    /**
     * Gets the value of the controllingStateOrProvinceCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getControllingStateOrProvinceCode() {
        return controllingStateOrProvinceCode;
    }

    /**
     * Sets the value of the controllingStateOrProvinceCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setControllingStateOrProvinceCode(String value) {
        this.controllingStateOrProvinceCode = value;
    }

}
