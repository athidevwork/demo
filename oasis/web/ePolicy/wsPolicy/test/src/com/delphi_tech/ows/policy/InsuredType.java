
package com.delphi_tech.ows.policy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for InsuredType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InsuredType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="InsuredNumberId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ReferredParty" type="{http://www.delphi-tech.com/ows/Policy}ReferredPartyType" minOccurs="0"/>
 *         &lt;element name="CurrentTermAmount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="InsuredVersion" type="{http://www.delphi-tech.com/ows/Policy}InsuredVersionType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="key" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="personReference" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="organizationReference" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="propertyReference" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InsuredType", propOrder = {
    "insuredNumberId",
    "referredParty",
    "currentTermAmount",
    "insuredVersion"
})
public class InsuredType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "InsuredNumberId")
    protected String insuredNumberId;
    @XmlElement(name = "ReferredParty")
    protected ReferredPartyType referredParty;
    @XmlElement(name = "CurrentTermAmount")
    protected String currentTermAmount;
    @XmlElement(name = "InsuredVersion")
    protected List<InsuredVersionType> insuredVersion;
    @XmlAttribute(name = "key")
    protected String key;
    @XmlAttribute(name = "personReference")
    protected String personReference;
    @XmlAttribute(name = "organizationReference")
    protected String organizationReference;
    @XmlAttribute(name = "propertyReference")
    protected String propertyReference;

    /**
     * Gets the value of the insuredNumberId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInsuredNumberId() {
        return insuredNumberId;
    }

    /**
     * Sets the value of the insuredNumberId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInsuredNumberId(String value) {
        this.insuredNumberId = value;
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
     * Gets the value of the currentTermAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCurrentTermAmount() {
        return currentTermAmount;
    }

    /**
     * Sets the value of the currentTermAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCurrentTermAmount(String value) {
        this.currentTermAmount = value;
    }

    /**
     * Gets the value of the insuredVersion property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the insuredVersion property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInsuredVersion().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link InsuredVersionType }
     * 
     * 
     */
    public List<InsuredVersionType> getInsuredVersion() {
        if (insuredVersion == null) {
            insuredVersion = new ArrayList<InsuredVersionType>();
        }
        return this.insuredVersion;
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

    /**
     * Gets the value of the propertyReference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPropertyReference() {
        return propertyReference;
    }

    /**
     * Sets the value of the propertyReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPropertyReference(String value) {
        this.propertyReference = value;
    }

}
