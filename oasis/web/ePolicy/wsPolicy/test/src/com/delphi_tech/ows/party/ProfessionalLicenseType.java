
package com.delphi_tech.ows.party;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ProfessionalLicenseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProfessionalLicenseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="LicenseNumberId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LicenseNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LicensePeriod" type="{http://www.delphi-tech.com/ows/Party}LicensePeriodType" minOccurs="0"/>
 *         &lt;element name="StateOrProvinceCode" type="{http://www.delphi-tech.com/ows/Party}StateOrProvinceCodeType" minOccurs="0"/>
 *         &lt;element name="SuspensionReinstatementInformation" type="{http://www.delphi-tech.com/ows/Party}SuspensionReinstatementInformationType" minOccurs="0"/>
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
@XmlType(name = "ProfessionalLicenseType", propOrder = {
    "licenseNumberId",
    "licenseNumber",
    "licensePeriod",
    "stateOrProvinceCode",
    "suspensionReinstatementInformation"
})
public class ProfessionalLicenseType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "LicenseNumberId")
    protected String licenseNumberId;
    @XmlElement(name = "LicenseNumber")
    protected String licenseNumber;
    @XmlElement(name = "LicensePeriod")
    protected LicensePeriodType licensePeriod;
    @XmlElement(name = "StateOrProvinceCode")
    protected StateOrProvinceCodeType stateOrProvinceCode;
    @XmlElement(name = "SuspensionReinstatementInformation")
    protected SuspensionReinstatementInformationType suspensionReinstatementInformation;
    @XmlAttribute(name = "key")
    protected String key;

    /**
     * Gets the value of the licenseNumberId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLicenseNumberId() {
        return licenseNumberId;
    }

    /**
     * Sets the value of the licenseNumberId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLicenseNumberId(String value) {
        this.licenseNumberId = value;
    }

    /**
     * Gets the value of the licenseNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLicenseNumber() {
        return licenseNumber;
    }

    /**
     * Sets the value of the licenseNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLicenseNumber(String value) {
        this.licenseNumber = value;
    }

    /**
     * Gets the value of the licensePeriod property.
     * 
     * @return
     *     possible object is
     *     {@link LicensePeriodType }
     *     
     */
    public LicensePeriodType getLicensePeriod() {
        return licensePeriod;
    }

    /**
     * Sets the value of the licensePeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link LicensePeriodType }
     *     
     */
    public void setLicensePeriod(LicensePeriodType value) {
        this.licensePeriod = value;
    }

    /**
     * Gets the value of the stateOrProvinceCode property.
     * 
     * @return
     *     possible object is
     *     {@link StateOrProvinceCodeType }
     *     
     */
    public StateOrProvinceCodeType getStateOrProvinceCode() {
        return stateOrProvinceCode;
    }

    /**
     * Sets the value of the stateOrProvinceCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link StateOrProvinceCodeType }
     *     
     */
    public void setStateOrProvinceCode(StateOrProvinceCodeType value) {
        this.stateOrProvinceCode = value;
    }

    /**
     * Gets the value of the suspensionReinstatementInformation property.
     * 
     * @return
     *     possible object is
     *     {@link SuspensionReinstatementInformationType }
     *     
     */
    public SuspensionReinstatementInformationType getSuspensionReinstatementInformation() {
        return suspensionReinstatementInformation;
    }

    /**
     * Sets the value of the suspensionReinstatementInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link SuspensionReinstatementInformationType }
     *     
     */
    public void setSuspensionReinstatementInformation(SuspensionReinstatementInformationType value) {
        this.suspensionReinstatementInformation = value;
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
