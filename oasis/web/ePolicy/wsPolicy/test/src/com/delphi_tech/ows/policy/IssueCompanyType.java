
package com.delphi_tech.ows.policy;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
 *         &lt;element name="ReferredParty" type="{http://www.delphi-tech.com/ows/Policy}ReferredPartyType" minOccurs="0"/>
 *         &lt;element name="ControllingStateOrProvinceCode" type="{http://www.delphi-tech.com/ows/Policy}ControllingStateOrProvinceCodeType" minOccurs="0"/>
 *         &lt;element name="ReferralCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ProcessLocationCode" type="{http://www.delphi-tech.com/ows/Policy}ProcessLocationCodeType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="organizationReference" type="{http://www.w3.org/2001/XMLSchema}string" />
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
    "controllingStateOrProvinceCode",
    "referralCode",
    "processLocationCode"
})
public class IssueCompanyType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "ReferredParty")
    protected ReferredPartyType referredParty;
    @XmlElement(name = "ControllingStateOrProvinceCode")
    protected ControllingStateOrProvinceCodeType controllingStateOrProvinceCode;
    @XmlElement(name = "ReferralCode")
    protected String referralCode;
    @XmlElement(name = "ProcessLocationCode")
    protected ProcessLocationCodeType processLocationCode;
    @XmlAttribute(name = "organizationReference")
    protected String organizationReference;

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
     *     {@link ControllingStateOrProvinceCodeType }
     *     
     */
    public ControllingStateOrProvinceCodeType getControllingStateOrProvinceCode() {
        return controllingStateOrProvinceCode;
    }

    /**
     * Sets the value of the controllingStateOrProvinceCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link ControllingStateOrProvinceCodeType }
     *     
     */
    public void setControllingStateOrProvinceCode(ControllingStateOrProvinceCodeType value) {
        this.controllingStateOrProvinceCode = value;
    }

    /**
     * Gets the value of the referralCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReferralCode() {
        return referralCode;
    }

    /**
     * Sets the value of the referralCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReferralCode(String value) {
        this.referralCode = value;
    }

    /**
     * Gets the value of the processLocationCode property.
     * 
     * @return
     *     possible object is
     *     {@link ProcessLocationCodeType }
     *     
     */
    public ProcessLocationCodeType getProcessLocationCode() {
        return processLocationCode;
    }

    /**
     * Sets the value of the processLocationCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProcessLocationCodeType }
     *     
     */
    public void setProcessLocationCode(ProcessLocationCodeType value) {
        this.processLocationCode = value;
    }

    /**
     * Gets the value of the organizationReference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrganizationReference() {
        return organizationReference;
    }

    /**
     * Sets the value of the organizationReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrganizationReference(String value) {
        this.organizationReference = value;
    }

}
