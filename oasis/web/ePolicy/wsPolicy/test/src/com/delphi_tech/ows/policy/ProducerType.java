
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
 * <p>Java class for ProducerType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProducerType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ProducerNumberId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ReferredParty" type="{http://www.delphi-tech.com/ows/Policy}ReferredPartyType" minOccurs="0"/>
 *         &lt;element name="License" type="{http://www.delphi-tech.com/ows/Policy}LicenseType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Commission" type="{http://www.delphi-tech.com/ows/Policy}CommissionType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ProducerChangeTypeCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ProducerAuthorizationCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "ProducerType", propOrder = {
    "producerNumberId",
    "referredParty",
    "license",
    "commission",
    "producerChangeTypeCode",
    "producerAuthorizationCode"
})
public class ProducerType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "ProducerNumberId")
    protected String producerNumberId;
    @XmlElement(name = "ReferredParty")
    protected ReferredPartyType referredParty;
    @XmlElement(name = "License")
    protected List<LicenseType> license;
    @XmlElement(name = "Commission")
    protected List<CommissionType> commission;
    @XmlElement(name = "ProducerChangeTypeCode")
    protected String producerChangeTypeCode;
    @XmlElement(name = "ProducerAuthorizationCode")
    protected String producerAuthorizationCode;
    @XmlAttribute(name = "personReference")
    protected String personReference;
    @XmlAttribute(name = "organizationReference")
    protected String organizationReference;

    /**
     * Gets the value of the producerNumberId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProducerNumberId() {
        return producerNumberId;
    }

    /**
     * Sets the value of the producerNumberId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProducerNumberId(String value) {
        this.producerNumberId = value;
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
     * Gets the value of the license property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the license property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLicense().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LicenseType }
     * 
     * 
     */
    public List<LicenseType> getLicense() {
        if (license == null) {
            license = new ArrayList<LicenseType>();
        }
        return this.license;
    }

    /**
     * Gets the value of the commission property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the commission property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCommission().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CommissionType }
     * 
     * 
     */
    public List<CommissionType> getCommission() {
        if (commission == null) {
            commission = new ArrayList<CommissionType>();
        }
        return this.commission;
    }

    /**
     * Gets the value of the producerChangeTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProducerChangeTypeCode() {
        return producerChangeTypeCode;
    }

    /**
     * Sets the value of the producerChangeTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProducerChangeTypeCode(String value) {
        this.producerChangeTypeCode = value;
    }

    /**
     * Gets the value of the producerAuthorizationCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProducerAuthorizationCode() {
        return producerAuthorizationCode;
    }

    /**
     * Sets the value of the producerAuthorizationCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProducerAuthorizationCode(String value) {
        this.producerAuthorizationCode = value;
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
