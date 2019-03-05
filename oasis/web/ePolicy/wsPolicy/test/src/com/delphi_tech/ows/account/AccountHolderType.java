
package com.delphi_tech.ows.account;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AccountHolderType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AccountHolderType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AccountHolderName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ReferredParty" type="{http://www.delphi-tech.com/ows/Account}ReferredPartyType"/>
 *         &lt;element name="EffectivePeriod" type="{http://www.delphi-tech.com/ows/Account}EffectivePeriodType" minOccurs="0"/>
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
@XmlType(name = "AccountHolderType", propOrder = {
    "accountHolderName",
    "referredParty",
    "effectivePeriod"
})
public class AccountHolderType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "AccountHolderName", required = true)
    protected String accountHolderName;
    @XmlElement(name = "ReferredParty", required = true)
    protected ReferredPartyType referredParty;
    @XmlElement(name = "EffectivePeriod")
    protected EffectivePeriodType effectivePeriod;
    @XmlAttribute(name = "personReference")
    protected String personReference;
    @XmlAttribute(name = "organizationReference")
    protected String organizationReference;

    /**
     * Gets the value of the accountHolderName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccountHolderName() {
        return accountHolderName;
    }

    /**
     * Sets the value of the accountHolderName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccountHolderName(String value) {
        this.accountHolderName = value;
    }

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
