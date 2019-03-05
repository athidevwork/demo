
package com.delphi_tech.ows.policy;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MalpracticeLiabilityExposureInformationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MalpracticeLiabilityExposureInformationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ExposureUnit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ExposureBasisCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DoctorCount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SquareFootage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="VapCount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BedCount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ExtendedBedCount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SkillBedCount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CensusCount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OutpatientVisitCount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DeliveryCount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ImpatientSurgeryCount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OutpatientSurgeryCount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EmergencyRoomVisitCount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MalpracticeLiabilityExposureInformationType", propOrder = {
    "exposureUnit",
    "exposureBasisCode",
    "doctorCount",
    "squareFootage",
    "vapCount",
    "bedCount",
    "extendedBedCount",
    "skillBedCount",
    "censusCount",
    "outpatientVisitCount",
    "deliveryCount",
    "impatientSurgeryCount",
    "outpatientSurgeryCount",
    "emergencyRoomVisitCount"
})
public class MalpracticeLiabilityExposureInformationType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "ExposureUnit")
    protected String exposureUnit;
    @XmlElement(name = "ExposureBasisCode")
    protected String exposureBasisCode;
    @XmlElement(name = "DoctorCount")
    protected String doctorCount;
    @XmlElement(name = "SquareFootage")
    protected String squareFootage;
    @XmlElement(name = "VapCount")
    protected String vapCount;
    @XmlElement(name = "BedCount")
    protected String bedCount;
    @XmlElement(name = "ExtendedBedCount")
    protected String extendedBedCount;
    @XmlElement(name = "SkillBedCount")
    protected String skillBedCount;
    @XmlElement(name = "CensusCount")
    protected String censusCount;
    @XmlElement(name = "OutpatientVisitCount")
    protected String outpatientVisitCount;
    @XmlElement(name = "DeliveryCount")
    protected String deliveryCount;
    @XmlElement(name = "ImpatientSurgeryCount")
    protected String impatientSurgeryCount;
    @XmlElement(name = "OutpatientSurgeryCount")
    protected String outpatientSurgeryCount;
    @XmlElement(name = "EmergencyRoomVisitCount")
    protected String emergencyRoomVisitCount;

    /**
     * Gets the value of the exposureUnit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExposureUnit() {
        return exposureUnit;
    }

    /**
     * Sets the value of the exposureUnit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExposureUnit(String value) {
        this.exposureUnit = value;
    }

    /**
     * Gets the value of the exposureBasisCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExposureBasisCode() {
        return exposureBasisCode;
    }

    /**
     * Sets the value of the exposureBasisCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExposureBasisCode(String value) {
        this.exposureBasisCode = value;
    }

    /**
     * Gets the value of the doctorCount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDoctorCount() {
        return doctorCount;
    }

    /**
     * Sets the value of the doctorCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDoctorCount(String value) {
        this.doctorCount = value;
    }

    /**
     * Gets the value of the squareFootage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSquareFootage() {
        return squareFootage;
    }

    /**
     * Sets the value of the squareFootage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSquareFootage(String value) {
        this.squareFootage = value;
    }

    /**
     * Gets the value of the vapCount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVapCount() {
        return vapCount;
    }

    /**
     * Sets the value of the vapCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVapCount(String value) {
        this.vapCount = value;
    }

    /**
     * Gets the value of the bedCount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBedCount() {
        return bedCount;
    }

    /**
     * Sets the value of the bedCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBedCount(String value) {
        this.bedCount = value;
    }

    /**
     * Gets the value of the extendedBedCount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtendedBedCount() {
        return extendedBedCount;
    }

    /**
     * Sets the value of the extendedBedCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtendedBedCount(String value) {
        this.extendedBedCount = value;
    }

    /**
     * Gets the value of the skillBedCount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSkillBedCount() {
        return skillBedCount;
    }

    /**
     * Sets the value of the skillBedCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSkillBedCount(String value) {
        this.skillBedCount = value;
    }

    /**
     * Gets the value of the censusCount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCensusCount() {
        return censusCount;
    }

    /**
     * Sets the value of the censusCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCensusCount(String value) {
        this.censusCount = value;
    }

    /**
     * Gets the value of the outpatientVisitCount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOutpatientVisitCount() {
        return outpatientVisitCount;
    }

    /**
     * Sets the value of the outpatientVisitCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOutpatientVisitCount(String value) {
        this.outpatientVisitCount = value;
    }

    /**
     * Gets the value of the deliveryCount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeliveryCount() {
        return deliveryCount;
    }

    /**
     * Sets the value of the deliveryCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeliveryCount(String value) {
        this.deliveryCount = value;
    }

    /**
     * Gets the value of the impatientSurgeryCount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImpatientSurgeryCount() {
        return impatientSurgeryCount;
    }

    /**
     * Sets the value of the impatientSurgeryCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImpatientSurgeryCount(String value) {
        this.impatientSurgeryCount = value;
    }

    /**
     * Gets the value of the outpatientSurgeryCount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOutpatientSurgeryCount() {
        return outpatientSurgeryCount;
    }

    /**
     * Sets the value of the outpatientSurgeryCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOutpatientSurgeryCount(String value) {
        this.outpatientSurgeryCount = value;
    }

    /**
     * Gets the value of the emergencyRoomVisitCount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmergencyRoomVisitCount() {
        return emergencyRoomVisitCount;
    }

    /**
     * Sets the value of the emergencyRoomVisitCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmergencyRoomVisitCount(String value) {
        this.emergencyRoomVisitCount = value;
    }

}
