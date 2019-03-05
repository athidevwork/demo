
package com.delphi_tech.ows.party;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BasicPhoneNumberType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BasicPhoneNumberType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PhoneNumberId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PhoneTypeCode" type="{http://www.delphi-tech.com/ows/Party}PhoneTypeCodeType" minOccurs="0"/>
 *         &lt;element name="PhoneNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PrimaryIndicator" type="{http://www.delphi-tech.com/ows/Party}PrimaryIndicatorType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="addressReference" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="key" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BasicPhoneNumberType", propOrder = {
    "phoneNumberId",
    "phoneTypeCode",
    "phoneNumber",
    "primaryIndicator"
})
public class BasicPhoneNumberType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "PhoneNumberId")
    protected String phoneNumberId;
    @XmlElement(name = "PhoneTypeCode")
    protected PhoneTypeCodeType phoneTypeCode;
    @XmlElement(name = "PhoneNumber")
    protected String phoneNumber;
    @XmlElement(name = "PrimaryIndicator")
    protected PrimaryIndicatorType primaryIndicator;
    @XmlAttribute(name = "addressReference")
    protected String addressReference;
    @XmlAttribute(name = "key")
    protected String key;

    /**
     * Gets the value of the phoneNumberId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhoneNumberId() {
        return phoneNumberId;
    }

    /**
     * Sets the value of the phoneNumberId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhoneNumberId(String value) {
        this.phoneNumberId = value;
    }

    /**
     * Gets the value of the phoneTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link PhoneTypeCodeType }
     *     
     */
    public PhoneTypeCodeType getPhoneTypeCode() {
        return phoneTypeCode;
    }

    /**
     * Sets the value of the phoneTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link PhoneTypeCodeType }
     *     
     */
    public void setPhoneTypeCode(PhoneTypeCodeType value) {
        this.phoneTypeCode = value;
    }

    /**
     * Gets the value of the phoneNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the value of the phoneNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhoneNumber(String value) {
        this.phoneNumber = value;
    }

    /**
     * Gets the value of the primaryIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link PrimaryIndicatorType }
     *     
     */
    public PrimaryIndicatorType getPrimaryIndicator() {
        return primaryIndicator;
    }

    /**
     * Sets the value of the primaryIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link PrimaryIndicatorType }
     *     
     */
    public void setPrimaryIndicator(PrimaryIndicatorType value) {
        this.primaryIndicator = value;
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
