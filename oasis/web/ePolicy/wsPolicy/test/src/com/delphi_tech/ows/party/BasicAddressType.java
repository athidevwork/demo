
package com.delphi_tech.ows.party;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BasicAddressType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BasicAddressType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PrimaryIndicator" type="{http://www.delphi-tech.com/ows/Party}PrimaryIndicatorType" minOccurs="0"/>
 *         &lt;element name="FuturePrimaryIndicator" type="{http://www.delphi-tech.com/ows/Party}PrimaryIndicatorType" minOccurs="0"/>
 *         &lt;element name="AddressTypeCode" type="{http://www.delphi-tech.com/ows/Party}AddressTypeCodeType" minOccurs="0"/>
 *         &lt;element name="AddressPeriod" type="{http://www.delphi-tech.com/ows/Party}AddressPeriodType" minOccurs="0"/>
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
@XmlType(name = "BasicAddressType", propOrder = {
    "primaryIndicator",
    "futurePrimaryIndicator",
    "addressTypeCode",
    "addressPeriod"
})
public class BasicAddressType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "PrimaryIndicator")
    protected PrimaryIndicatorType primaryIndicator;
    @XmlElement(name = "FuturePrimaryIndicator")
    protected PrimaryIndicatorType futurePrimaryIndicator;
    @XmlElement(name = "AddressTypeCode")
    protected AddressTypeCodeType addressTypeCode;
    @XmlElement(name = "AddressPeriod")
    protected AddressPeriodType addressPeriod;
    @XmlAttribute(name = "addressReference")
    protected String addressReference;
    @XmlAttribute(name = "key")
    protected String key;

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
     * Gets the value of the futurePrimaryIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link PrimaryIndicatorType }
     *     
     */
    public PrimaryIndicatorType getFuturePrimaryIndicator() {
        return futurePrimaryIndicator;
    }

    /**
     * Sets the value of the futurePrimaryIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link PrimaryIndicatorType }
     *     
     */
    public void setFuturePrimaryIndicator(PrimaryIndicatorType value) {
        this.futurePrimaryIndicator = value;
    }

    /**
     * Gets the value of the addressTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link AddressTypeCodeType }
     *     
     */
    public AddressTypeCodeType getAddressTypeCode() {
        return addressTypeCode;
    }

    /**
     * Sets the value of the addressTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddressTypeCodeType }
     *     
     */
    public void setAddressTypeCode(AddressTypeCodeType value) {
        this.addressTypeCode = value;
    }

    /**
     * Gets the value of the addressPeriod property.
     * 
     * @return
     *     possible object is
     *     {@link AddressPeriodType }
     *     
     */
    public AddressPeriodType getAddressPeriod() {
        return addressPeriod;
    }

    /**
     * Sets the value of the addressPeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddressPeriodType }
     *     
     */
    public void setAddressPeriod(AddressPeriodType value) {
        this.addressPeriod = value;
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
