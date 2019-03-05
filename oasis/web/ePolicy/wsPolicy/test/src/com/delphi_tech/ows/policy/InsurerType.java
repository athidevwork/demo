
package com.delphi_tech.ows.policy;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for InsurerType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InsurerType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ReferredParty" type="{http://www.delphi-tech.com/ows/Policy}ReferredPartyType" minOccurs="0"/>
 *         &lt;element name="EffectivePeriod" type="{http://www.delphi-tech.com/ows/Policy}EffectivePeriodType" minOccurs="0"/>
 *         &lt;element name="InsurerNumberId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RenewalIndicator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="personReference" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="organizationReference" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InsurerType", propOrder = {
    "referredParty",
    "effectivePeriod",
    "insurerNumberId",
    "renewalIndicator"
})
public class InsurerType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "ReferredParty")
    protected ReferredPartyType referredParty;
    @XmlElement(name = "EffectivePeriod")
    protected EffectivePeriodType effectivePeriod;
    @XmlElement(name = "InsurerNumberId")
    protected String insurerNumberId;
    @XmlElement(name = "RenewalIndicator")
    protected String renewalIndicator;
    @XmlAttribute(name = "personReference")
    protected String personReference;
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
     * Gets the value of the effectivePeriod property.
     * 
     * @return
     *     possible object is
     *     {@link EffectivePeriodType }
     *     
     */
    public EffectivePeriodType getEffectivePeriod() {
        return effectivePeriod;
    }

    /**
     * Sets the value of the effectivePeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link EffectivePeriodType }
     *     
     */
    public void setEffectivePeriod(EffectivePeriodType value) {
        this.effectivePeriod = value;
    }

    /**
     * Gets the value of the insurerNumberId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInsurerNumberId() {
        return insurerNumberId;
    }

    /**
     * Sets the value of the insurerNumberId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInsurerNumberId(String value) {
        this.insurerNumberId = value;
    }

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
     * Gets the value of the personReference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPersonReference() {
        return personReference;
    }

    /**
     * Sets the value of the personReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPersonReference(String value) {
        this.personReference = value;
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
