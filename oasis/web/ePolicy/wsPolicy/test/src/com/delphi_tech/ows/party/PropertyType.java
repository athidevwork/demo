
package com.delphi_tech.ows.party;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PropertyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PropertyNumberId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PropertyName" type="{http://www.delphi-tech.com/ows/Party}PropertyNameType" minOccurs="0"/>
 *         &lt;element name="BasicPhoneNumber" type="{http://www.delphi-tech.com/ows/Party}BasicPhoneNumberType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="BasicAddress" type="{http://www.delphi-tech.com/ows/Party}BasicAddressType" minOccurs="0"/>
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
@XmlType(name = "PropertyType", propOrder = {
    "propertyNumberId",
    "propertyName",
    "basicPhoneNumber",
    "basicAddress"
})
public class PropertyType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "PropertyNumberId")
    protected String propertyNumberId;
    @XmlElement(name = "PropertyName")
    protected PropertyNameType propertyName;
    @XmlElement(name = "BasicPhoneNumber")
    protected List<BasicPhoneNumberType> basicPhoneNumber;
    @XmlElement(name = "BasicAddress")
    protected BasicAddressType basicAddress;
    @XmlAttribute(name = "key")
    protected String key;

    /**
     * Gets the value of the propertyNumberId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPropertyNumberId() {
        return propertyNumberId;
    }

    /**
     * Sets the value of the propertyNumberId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPropertyNumberId(String value) {
        this.propertyNumberId = value;
    }

    /**
     * Gets the value of the propertyName property.
     * 
     * @return
     *     possible object is
     *     {@link PropertyNameType }
     *     
     */
    public PropertyNameType getPropertyName() {
        return propertyName;
    }

    /**
     * Sets the value of the propertyName property.
     * 
     * @param value
     *     allowed object is
     *     {@link PropertyNameType }
     *     
     */
    public void setPropertyName(PropertyNameType value) {
        this.propertyName = value;
    }

    /**
     * Gets the value of the basicPhoneNumber property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the basicPhoneNumber property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBasicPhoneNumber().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BasicPhoneNumberType }
     * 
     * 
     */
    public List<BasicPhoneNumberType> getBasicPhoneNumber() {
        if (basicPhoneNumber == null) {
            basicPhoneNumber = new ArrayList<BasicPhoneNumberType>();
        }
        return this.basicPhoneNumber;
    }

    /**
     * Gets the value of the basicAddress property.
     * 
     * @return
     *     possible object is
     *     {@link BasicAddressType }
     *     
     */
    public BasicAddressType getBasicAddress() {
        return basicAddress;
    }

    /**
     * Sets the value of the basicAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link BasicAddressType }
     *     
     */
    public void setBasicAddress(BasicAddressType value) {
        this.basicAddress = value;
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
