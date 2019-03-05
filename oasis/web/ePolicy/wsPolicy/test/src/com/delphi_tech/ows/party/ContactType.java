
package com.delphi_tech.ows.party;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ContactType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ContactType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ContactNumberId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PersonName" type="{http://www.delphi-tech.com/ows/Party}PersonNameType" minOccurs="0"/>
 *         &lt;element name="BusinessEmail" type="{http://www.delphi-tech.com/ows/Party}BusinessEmailType" minOccurs="0"/>
 *         &lt;element name="BasicPhoneNumber" type="{http://www.delphi-tech.com/ows/Party}BasicPhoneNumberType" minOccurs="0"/>
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
@XmlType(name = "ContactType", propOrder = {
    "contactNumberId",
    "personName",
    "businessEmail",
    "basicPhoneNumber"
})
public class ContactType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "ContactNumberId")
    protected String contactNumberId;
    @XmlElement(name = "PersonName")
    protected PersonNameType personName;
    @XmlElement(name = "BusinessEmail")
    protected BusinessEmailType businessEmail;
    @XmlElement(name = "BasicPhoneNumber")
    protected BasicPhoneNumberType basicPhoneNumber;
    @XmlAttribute(name = "key")
    protected String key;

    /**
     * Gets the value of the contactNumberId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContactNumberId() {
        return contactNumberId;
    }

    /**
     * Sets the value of the contactNumberId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContactNumberId(String value) {
        this.contactNumberId = value;
    }

    /**
     * Gets the value of the personName property.
     * 
     * @return
     *     possible object is
     *     {@link PersonNameType }
     *     
     */
    public PersonNameType getPersonName() {
        return personName;
    }

    /**
     * Sets the value of the personName property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonNameType }
     *     
     */
    public void setPersonName(PersonNameType value) {
        this.personName = value;
    }

    /**
     * Gets the value of the businessEmail property.
     * 
     * @return
     *     possible object is
     *     {@link BusinessEmailType }
     *     
     */
    public BusinessEmailType getBusinessEmail() {
        return businessEmail;
    }

    /**
     * Sets the value of the businessEmail property.
     * 
     * @param value
     *     allowed object is
     *     {@link BusinessEmailType }
     *     
     */
    public void setBusinessEmail(BusinessEmailType value) {
        this.businessEmail = value;
    }

    /**
     * Gets the value of the basicPhoneNumber property.
     * 
     * @return
     *     possible object is
     *     {@link BasicPhoneNumberType }
     *     
     */
    public BasicPhoneNumberType getBasicPhoneNumber() {
        return basicPhoneNumber;
    }

    /**
     * Sets the value of the basicPhoneNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link BasicPhoneNumberType }
     *     
     */
    public void setBasicPhoneNumber(BasicPhoneNumberType value) {
        this.basicPhoneNumber = value;
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
