
package com.delphi_tech.ows.policy;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BuildingInformationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BuildingInformationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="BuildingClassCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BuildingValue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BuildingTypeCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BuildingUseTypeCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FrameTypeCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ProtectionClassCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SprinklerIndicator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ConstructionTypeCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RoofTypeCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FloorTypeCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ProtectionTypeCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FireServiceTypeCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="HydrantTypeCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SecurityTypeCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LocationCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LocationDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="addressReference" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BuildingInformationType", propOrder = {
    "buildingClassCode",
    "buildingValue",
    "buildingTypeCode",
    "buildingUseTypeCode",
    "frameTypeCode",
    "protectionClassCode",
    "sprinklerIndicator",
    "constructionTypeCode",
    "roofTypeCode",
    "floorTypeCode",
    "protectionTypeCode",
    "fireServiceTypeCode",
    "hydrantTypeCode",
    "securityTypeCode",
    "locationCode",
    "locationDescription"
})
public class BuildingInformationType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "BuildingClassCode")
    protected String buildingClassCode;
    @XmlElement(name = "BuildingValue")
    protected String buildingValue;
    @XmlElement(name = "BuildingTypeCode")
    protected String buildingTypeCode;
    @XmlElement(name = "BuildingUseTypeCode")
    protected String buildingUseTypeCode;
    @XmlElement(name = "FrameTypeCode")
    protected String frameTypeCode;
    @XmlElement(name = "ProtectionClassCode")
    protected String protectionClassCode;
    @XmlElement(name = "SprinklerIndicator")
    protected String sprinklerIndicator;
    @XmlElement(name = "ConstructionTypeCode")
    protected String constructionTypeCode;
    @XmlElement(name = "RoofTypeCode")
    protected String roofTypeCode;
    @XmlElement(name = "FloorTypeCode")
    protected String floorTypeCode;
    @XmlElement(name = "ProtectionTypeCode")
    protected String protectionTypeCode;
    @XmlElement(name = "FireServiceTypeCode")
    protected String fireServiceTypeCode;
    @XmlElement(name = "HydrantTypeCode")
    protected String hydrantTypeCode;
    @XmlElement(name = "SecurityTypeCode")
    protected String securityTypeCode;
    @XmlElement(name = "LocationCode")
    protected String locationCode;
    @XmlElement(name = "LocationDescription")
    protected String locationDescription;
    @XmlAttribute(name = "addressReference")
    protected String addressReference;

    /**
     * Gets the value of the buildingClassCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBuildingClassCode() {
        return buildingClassCode;
    }

    /**
     * Sets the value of the buildingClassCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBuildingClassCode(String value) {
        this.buildingClassCode = value;
    }

    /**
     * Gets the value of the buildingValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBuildingValue() {
        return buildingValue;
    }

    /**
     * Sets the value of the buildingValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBuildingValue(String value) {
        this.buildingValue = value;
    }

    /**
     * Gets the value of the buildingTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBuildingTypeCode() {
        return buildingTypeCode;
    }

    /**
     * Sets the value of the buildingTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBuildingTypeCode(String value) {
        this.buildingTypeCode = value;
    }

    /**
     * Gets the value of the buildingUseTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBuildingUseTypeCode() {
        return buildingUseTypeCode;
    }

    /**
     * Sets the value of the buildingUseTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBuildingUseTypeCode(String value) {
        this.buildingUseTypeCode = value;
    }

    /**
     * Gets the value of the frameTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFrameTypeCode() {
        return frameTypeCode;
    }

    /**
     * Sets the value of the frameTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFrameTypeCode(String value) {
        this.frameTypeCode = value;
    }

    /**
     * Gets the value of the protectionClassCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProtectionClassCode() {
        return protectionClassCode;
    }

    /**
     * Sets the value of the protectionClassCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProtectionClassCode(String value) {
        this.protectionClassCode = value;
    }

    /**
     * Gets the value of the sprinklerIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSprinklerIndicator() {
        return sprinklerIndicator;
    }

    /**
     * Sets the value of the sprinklerIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSprinklerIndicator(String value) {
        this.sprinklerIndicator = value;
    }

    /**
     * Gets the value of the constructionTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConstructionTypeCode() {
        return constructionTypeCode;
    }

    /**
     * Sets the value of the constructionTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConstructionTypeCode(String value) {
        this.constructionTypeCode = value;
    }

    /**
     * Gets the value of the roofTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRoofTypeCode() {
        return roofTypeCode;
    }

    /**
     * Sets the value of the roofTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRoofTypeCode(String value) {
        this.roofTypeCode = value;
    }

    /**
     * Gets the value of the floorTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFloorTypeCode() {
        return floorTypeCode;
    }

    /**
     * Sets the value of the floorTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFloorTypeCode(String value) {
        this.floorTypeCode = value;
    }

    /**
     * Gets the value of the protectionTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProtectionTypeCode() {
        return protectionTypeCode;
    }

    /**
     * Sets the value of the protectionTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProtectionTypeCode(String value) {
        this.protectionTypeCode = value;
    }

    /**
     * Gets the value of the fireServiceTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFireServiceTypeCode() {
        return fireServiceTypeCode;
    }

    /**
     * Sets the value of the fireServiceTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFireServiceTypeCode(String value) {
        this.fireServiceTypeCode = value;
    }

    /**
     * Gets the value of the hydrantTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHydrantTypeCode() {
        return hydrantTypeCode;
    }

    /**
     * Sets the value of the hydrantTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHydrantTypeCode(String value) {
        this.hydrantTypeCode = value;
    }

    /**
     * Gets the value of the securityTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSecurityTypeCode() {
        return securityTypeCode;
    }

    /**
     * Sets the value of the securityTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSecurityTypeCode(String value) {
        this.securityTypeCode = value;
    }

    /**
     * Gets the value of the locationCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocationCode() {
        return locationCode;
    }

    /**
     * Sets the value of the locationCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocationCode(String value) {
        this.locationCode = value;
    }

    /**
     * Gets the value of the locationDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocationDescription() {
        return locationDescription;
    }

    /**
     * Sets the value of the locationDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocationDescription(String value) {
        this.locationDescription = value;
    }

    /**
     * Gets the value of the addressReference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddressReference() {
        return addressReference;
    }

    /**
     * Sets the value of the addressReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddressReference(String value) {
        this.addressReference = value;
    }

}
