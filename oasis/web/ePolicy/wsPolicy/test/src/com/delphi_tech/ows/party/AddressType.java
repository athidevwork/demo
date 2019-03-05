
package com.delphi_tech.ows.party;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AddressType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AddressType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AddressNumberId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ExternalReferenceId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LineOne" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LineTwo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LineThree" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CityName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CountyCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="StateOrProvinceCode" type="{http://www.delphi-tech.com/ows/Party}StateOrProvinceCodeType" minOccurs="0"/>
 *         &lt;element name="PostalCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CountryCode" type="{http://www.delphi-tech.com/ows/Party}CountryCodeType" minOccurs="0"/>
 *         &lt;element name="CountryName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AttentionName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PostOfficeAddressIndicator" type="{http://www.delphi-tech.com/ows/Party}PostOfficeAddressIndicatorType" minOccurs="0"/>
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
@XmlType(name = "AddressType", propOrder = {
    "addressNumberId",
    "externalReferenceId",
    "lineOne",
    "lineTwo",
    "lineThree",
    "cityName",
    "countyCode",
    "stateOrProvinceCode",
    "postalCode",
    "countryCode",
    "countryName",
    "attentionName",
    "postOfficeAddressIndicator"
})
public class AddressType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "AddressNumberId")
    protected String addressNumberId;
    @XmlElement(name = "ExternalReferenceId")
    protected String externalReferenceId;
    @XmlElement(name = "LineOne")
    protected String lineOne;
    @XmlElement(name = "LineTwo")
    protected String lineTwo;
    @XmlElement(name = "LineThree")
    protected String lineThree;
    @XmlElement(name = "CityName")
    protected String cityName;
    @XmlElement(name = "CountyCode")
    protected String countyCode;
    @XmlElement(name = "StateOrProvinceCode")
    protected StateOrProvinceCodeType stateOrProvinceCode;
    @XmlElement(name = "PostalCode")
    protected String postalCode;
    @XmlElement(name = "CountryCode")
    protected CountryCodeType countryCode;
    @XmlElement(name = "CountryName")
    protected String countryName;
    @XmlElement(name = "AttentionName")
    protected String attentionName;
    @XmlElement(name = "PostOfficeAddressIndicator")
    protected PostOfficeAddressIndicatorType postOfficeAddressIndicator;
    @XmlAttribute(name = "key")
    protected String key;

    /**
     * Gets the value of the addressNumberId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddressNumberId() {
        return addressNumberId;
    }

    /**
     * Sets the value of the addressNumberId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddressNumberId(String value) {
        this.addressNumberId = value;
    }

    /**
     * Gets the value of the externalReferenceId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExternalReferenceId() {
        return externalReferenceId;
    }

    /**
     * Sets the value of the externalReferenceId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExternalReferenceId(String value) {
        this.externalReferenceId = value;
    }

    /**
     * Gets the value of the lineOne property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLineOne() {
        return lineOne;
    }

    /**
     * Sets the value of the lineOne property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLineOne(String value) {
        this.lineOne = value;
    }

    /**
     * Gets the value of the lineTwo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLineTwo() {
        return lineTwo;
    }

    /**
     * Sets the value of the lineTwo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLineTwo(String value) {
        this.lineTwo = value;
    }

    /**
     * Gets the value of the lineThree property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLineThree() {
        return lineThree;
    }

    /**
     * Sets the value of the lineThree property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLineThree(String value) {
        this.lineThree = value;
    }

    /**
     * Gets the value of the cityName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCityName() {
        return cityName;
    }

    /**
     * Sets the value of the cityName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCityName(String value) {
        this.cityName = value;
    }

    /**
     * Gets the value of the countyCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountyCode() {
        return countyCode;
    }

    /**
     * Sets the value of the countyCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountyCode(String value) {
        this.countyCode = value;
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
     * Gets the value of the postalCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets the value of the postalCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostalCode(String value) {
        this.postalCode = value;
    }

    /**
     * Gets the value of the countryCode property.
     * 
     * @return
     *     possible object is
     *     {@link CountryCodeType }
     *     
     */
    public CountryCodeType getCountryCode() {
        return countryCode;
    }

    /**
     * Sets the value of the countryCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CountryCodeType }
     *     
     */
    public void setCountryCode(CountryCodeType value) {
        this.countryCode = value;
    }

    /**
     * Gets the value of the countryName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountryName() {
        return countryName;
    }

    /**
     * Sets the value of the countryName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountryName(String value) {
        this.countryName = value;
    }

    /**
     * Gets the value of the attentionName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttentionName() {
        return attentionName;
    }

    /**
     * Sets the value of the attentionName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttentionName(String value) {
        this.attentionName = value;
    }

    /**
     * Gets the value of the postOfficeAddressIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link PostOfficeAddressIndicatorType }
     *     
     */
    public PostOfficeAddressIndicatorType getPostOfficeAddressIndicator() {
        return postOfficeAddressIndicator;
    }

    /**
     * Sets the value of the postOfficeAddressIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link PostOfficeAddressIndicatorType }
     *     
     */
    public void setPostOfficeAddressIndicator(PostOfficeAddressIndicatorType value) {
        this.postOfficeAddressIndicator = value;
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
