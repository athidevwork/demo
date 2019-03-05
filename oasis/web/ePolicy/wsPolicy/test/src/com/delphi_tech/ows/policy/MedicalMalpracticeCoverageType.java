
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
 * <p>Java class for MedicalMalpracticeCoverageType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MedicalMalpracticeCoverageType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CoverageNumberId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ParentCoverageNumberId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CurrentTermAmount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CurrentGrossTermAmount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MedicalMalpracticeCoverageCode" type="{http://www.delphi-tech.com/ows/Policy}MedicalMalpracticeCoverageCodeType" minOccurs="0"/>
 *         &lt;element name="ReferredInsured" type="{http://www.delphi-tech.com/ows/Policy}ReferredInsuredType" minOccurs="0"/>
 *         &lt;element name="MedicalMalpracticeCoverageVersion" type="{http://www.delphi-tech.com/ows/Policy}MedicalMalpracticeCoverageVersionType" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "MedicalMalpracticeCoverageType", propOrder = {
    "coverageNumberId",
    "parentCoverageNumberId",
    "currentTermAmount",
    "currentGrossTermAmount",
    "medicalMalpracticeCoverageCode",
    "referredInsured",
    "medicalMalpracticeCoverageVersion"
})
public class MedicalMalpracticeCoverageType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "CoverageNumberId")
    protected String coverageNumberId;
    @XmlElement(name = "ParentCoverageNumberId")
    protected String parentCoverageNumberId;
    @XmlElement(name = "CurrentTermAmount")
    protected String currentTermAmount;
    @XmlElement(name = "CurrentGrossTermAmount")
    protected String currentGrossTermAmount;
    @XmlElement(name = "MedicalMalpracticeCoverageCode")
    protected MedicalMalpracticeCoverageCodeType medicalMalpracticeCoverageCode;
    @XmlElement(name = "ReferredInsured")
    protected ReferredInsuredType referredInsured;
    @XmlElement(name = "MedicalMalpracticeCoverageVersion")
    protected List<MedicalMalpracticeCoverageVersionType> medicalMalpracticeCoverageVersion;
    @XmlAttribute(name = "key")
    protected String key;

    /**
     * Gets the value of the coverageNumberId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCoverageNumberId() {
        return coverageNumberId;
    }

    /**
     * Sets the value of the coverageNumberId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCoverageNumberId(String value) {
        this.coverageNumberId = value;
    }

    /**
     * Gets the value of the parentCoverageNumberId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParentCoverageNumberId() {
        return parentCoverageNumberId;
    }

    /**
     * Sets the value of the parentCoverageNumberId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParentCoverageNumberId(String value) {
        this.parentCoverageNumberId = value;
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
     * Gets the value of the currentGrossTermAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCurrentGrossTermAmount() {
        return currentGrossTermAmount;
    }

    /**
     * Sets the value of the currentGrossTermAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCurrentGrossTermAmount(String value) {
        this.currentGrossTermAmount = value;
    }

    /**
     * Gets the value of the medicalMalpracticeCoverageCode property.
     * 
     * @return
     *     possible object is
     *     {@link MedicalMalpracticeCoverageCodeType }
     *     
     */
    public MedicalMalpracticeCoverageCodeType getMedicalMalpracticeCoverageCode() {
        return medicalMalpracticeCoverageCode;
    }

    /**
     * Sets the value of the medicalMalpracticeCoverageCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link MedicalMalpracticeCoverageCodeType }
     *     
     */
    public void setMedicalMalpracticeCoverageCode(MedicalMalpracticeCoverageCodeType value) {
        this.medicalMalpracticeCoverageCode = value;
    }

    /**
     * Gets the value of the referredInsured property.
     * 
     * @return
     *     possible object is
     *     {@link ReferredInsuredType }
     *     
     */
    public ReferredInsuredType getReferredInsured() {
        return referredInsured;
    }

    /**
     * Sets the value of the referredInsured property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferredInsuredType }
     *     
     */
    public void setReferredInsured(ReferredInsuredType value) {
        this.referredInsured = value;
    }

    /**
     * Gets the value of the medicalMalpracticeCoverageVersion property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the medicalMalpracticeCoverageVersion property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMedicalMalpracticeCoverageVersion().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MedicalMalpracticeCoverageVersionType }
     * 
     * 
     */
    public List<MedicalMalpracticeCoverageVersionType> getMedicalMalpracticeCoverageVersion() {
        if (medicalMalpracticeCoverageVersion == null) {
            medicalMalpracticeCoverageVersion = new ArrayList<MedicalMalpracticeCoverageVersionType>();
        }
        return this.medicalMalpracticeCoverageVersion;
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
