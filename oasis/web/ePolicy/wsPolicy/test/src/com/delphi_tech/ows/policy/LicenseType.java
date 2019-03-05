
package com.delphi_tech.ows.policy;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LicenseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LicenseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="LicenseOrPermitNumberId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LicenseClassCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LicenseType", propOrder = {
    "licenseOrPermitNumberId",
    "licenseClassCode"
})
public class LicenseType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "LicenseOrPermitNumberId")
    protected String licenseOrPermitNumberId;
    @XmlElement(name = "LicenseClassCode")
    protected String licenseClassCode;

    /**
     * Gets the value of the licenseOrPermitNumberId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLicenseOrPermitNumberId() {
        return licenseOrPermitNumberId;
    }

    /**
     * Sets the value of the licenseOrPermitNumberId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLicenseOrPermitNumberId(String value) {
        this.licenseOrPermitNumberId = value;
    }

    /**
     * Gets the value of the licenseClassCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLicenseClassCode() {
        return licenseClassCode;
    }

    /**
     * Sets the value of the licenseClassCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLicenseClassCode(String value) {
        this.licenseClassCode = value;
    }

}
