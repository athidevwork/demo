
package com.delphi_tech.ows.party;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RelationshipType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RelationshipType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RelationshipNumberId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RelationshipType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EffectivePeriod" type="{http://www.delphi-tech.com/ows/Party}EffectivePeriodType" minOccurs="0"/>
 *         &lt;element name="ReferredParty" type="{http://www.delphi-tech.com/ows/Party}ReferredPartyType" minOccurs="0"/>
 *         &lt;element name="PrimaryIndicator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ReverseRelationshipIndicator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="key" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RelationshipType", propOrder = {
    "relationshipNumberId",
    "relationshipType",
    "effectivePeriod",
    "referredParty",
    "primaryIndicator",
    "reverseRelationshipIndicator"
})
public class RelationshipType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "RelationshipNumberId")
    protected String relationshipNumberId;
    @XmlElement(name = "RelationshipType")
    protected String relationshipType;
    @XmlElement(name = "EffectivePeriod")
    protected EffectivePeriodType effectivePeriod;
    @XmlElement(name = "ReferredParty")
    protected ReferredPartyType referredParty;
    @XmlElement(name = "PrimaryIndicator")
    protected String primaryIndicator;
    @XmlElement(name = "ReverseRelationshipIndicator")
    protected String reverseRelationshipIndicator;
    @XmlAttribute(name = "key")
    protected String key;

    /**
     * Gets the value of the relationshipNumberId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRelationshipNumberId() {
        return relationshipNumberId;
    }

    /**
     * Sets the value of the relationshipNumberId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRelationshipNumberId(String value) {
        this.relationshipNumberId = value;
    }

    /**
     * Gets the value of the relationshipType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRelationshipType() {
        return relationshipType;
    }

    /**
     * Sets the value of the relationshipType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRelationshipType(String value) {
        this.relationshipType = value;
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

    /**
     * Gets the value of the reverseRelationshipIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReverseRelationshipIndicator() {
        return reverseRelationshipIndicator;
    }

    /**
     * Sets the value of the reverseRelationshipIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReverseRelationshipIndicator(String value) {
        this.reverseRelationshipIndicator = value;
    }

    /**
     * Gets the value of the key property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the value of the key property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKey(String value) {
        this.key = value;
    }

}
