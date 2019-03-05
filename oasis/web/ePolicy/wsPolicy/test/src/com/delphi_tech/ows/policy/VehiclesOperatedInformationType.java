
package com.delphi_tech.ows.policy;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for VehiclesOperatedInformationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VehiclesOperatedInformationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FleetIndicator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="VehicleManufacturerCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="VehicleManufacturerSubclassCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="VehicleModelCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="VehicleYear" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="VehicleOriginalCost" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="VehicleVin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VehiclesOperatedInformationType", propOrder = {
    "fleetIndicator",
    "vehicleManufacturerCode",
    "vehicleManufacturerSubclassCode",
    "vehicleModelCode",
    "vehicleYear",
    "vehicleOriginalCost",
    "vehicleVin"
})
public class VehiclesOperatedInformationType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "FleetIndicator")
    protected String fleetIndicator;
    @XmlElement(name = "VehicleManufacturerCode")
    protected String vehicleManufacturerCode;
    @XmlElement(name = "VehicleManufacturerSubclassCode")
    protected String vehicleManufacturerSubclassCode;
    @XmlElement(name = "VehicleModelCode")
    protected String vehicleModelCode;
    @XmlElement(name = "VehicleYear")
    protected String vehicleYear;
    @XmlElement(name = "VehicleOriginalCost")
    protected String vehicleOriginalCost;
    @XmlElement(name = "VehicleVin")
    protected String vehicleVin;

    /**
     * Gets the value of the fleetIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFleetIndicator() {
        return fleetIndicator;
    }

    /**
     * Sets the value of the fleetIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFleetIndicator(String value) {
        this.fleetIndicator = value;
    }

    /**
     * Gets the value of the vehicleManufacturerCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVehicleManufacturerCode() {
        return vehicleManufacturerCode;
    }

    /**
     * Sets the value of the vehicleManufacturerCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVehicleManufacturerCode(String value) {
        this.vehicleManufacturerCode = value;
    }

    /**
     * Gets the value of the vehicleManufacturerSubclassCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVehicleManufacturerSubclassCode() {
        return vehicleManufacturerSubclassCode;
    }

    /**
     * Sets the value of the vehicleManufacturerSubclassCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVehicleManufacturerSubclassCode(String value) {
        this.vehicleManufacturerSubclassCode = value;
    }

    /**
     * Gets the value of the vehicleModelCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVehicleModelCode() {
        return vehicleModelCode;
    }

    /**
     * Sets the value of the vehicleModelCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVehicleModelCode(String value) {
        this.vehicleModelCode = value;
    }

    /**
     * Gets the value of the vehicleYear property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVehicleYear() {
        return vehicleYear;
    }

    /**
     * Sets the value of the vehicleYear property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVehicleYear(String value) {
        this.vehicleYear = value;
    }

    /**
     * Gets the value of the vehicleOriginalCost property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVehicleOriginalCost() {
        return vehicleOriginalCost;
    }

    /**
     * Sets the value of the vehicleOriginalCost property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVehicleOriginalCost(String value) {
        this.vehicleOriginalCost = value;
    }

    /**
     * Gets the value of the vehicleVin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVehicleVin() {
        return vehicleVin;
    }

    /**
     * Sets the value of the vehicleVin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVehicleVin(String value) {
        this.vehicleVin = value;
    }

}
