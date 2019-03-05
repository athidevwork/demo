
package com.delphi_tech.ows.policyinquiryservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.delphi_tech.ows.common.MessageStatusType;
import com.delphi_tech.ows.party.AddressType;
import com.delphi_tech.ows.party.OrganizationType;
import com.delphi_tech.ows.party.PersonType;
import com.delphi_tech.ows.party.PropertyType;
import com.delphi_tech.ows.policy.MedicalMalpracticePolicyType;


/**
 * <p>Java class for PolicyInquiryResultType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PolicyInquiryResultType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MessageId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CorrelationId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element ref="{http://www.delphi-tech.com/ows/Party}Address" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.delphi-tech.com/ows/Party}Person" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.delphi-tech.com/ows/Party}Organization" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.delphi-tech.com/ows/Party}Property" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.delphi-tech.com/ows/Policy}MedicalMalpracticePolicy" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.delphi-tech.com/ows/Common}MessageStatus"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PolicyInquiryResultType", propOrder = {
    "messageId",
    "correlationId",
    "address",
    "person",
    "organization",
    "property",
    "medicalMalpracticePolicy",
    "messageStatus"
})
public class PolicyInquiryResultType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "MessageId")
    protected String messageId;
    @XmlElement(name = "CorrelationId")
    protected String correlationId;
    @XmlElement(name = "Address", namespace = "http://www.delphi-tech.com/ows/Party")
    protected List<AddressType> address;
    @XmlElement(name = "Person", namespace = "http://www.delphi-tech.com/ows/Party")
    protected List<PersonType> person;
    @XmlElement(name = "Organization", namespace = "http://www.delphi-tech.com/ows/Party")
    protected List<OrganizationType> organization;
    @XmlElement(name = "Property", namespace = "http://www.delphi-tech.com/ows/Party")
    protected List<PropertyType> property;
    @XmlElement(name = "MedicalMalpracticePolicy", namespace = "http://www.delphi-tech.com/ows/Policy")
    protected List<MedicalMalpracticePolicyType> medicalMalpracticePolicy;
    @XmlElement(name = "MessageStatus", namespace = "http://www.delphi-tech.com/ows/Common", required = true)
    protected MessageStatusType messageStatus;

    /**
     * Gets the value of the messageId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Sets the value of the messageId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessageId(String value) {
        this.messageId = value;
    }

    /**
     * Gets the value of the correlationId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCorrelationId() {
        return correlationId;
    }

    /**
     * Sets the value of the correlationId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCorrelationId(String value) {
        this.correlationId = value;
    }

    /**
     * Gets the value of the address property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the address property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAddress().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AddressType }
     * 
     * 
     */
    public List<AddressType> getAddress() {
        if (address == null) {
            address = new ArrayList<AddressType>();
        }
        return this.address;
    }

    /**
     * Gets the value of the person property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the person property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPerson().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PersonType }
     * 
     * 
     */
    public List<PersonType> getPerson() {
        if (person == null) {
            person = new ArrayList<PersonType>();
        }
        return this.person;
    }

    /**
     * Gets the value of the organization property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the organization property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOrganization().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OrganizationType }
     * 
     * 
     */
    public List<OrganizationType> getOrganization() {
        if (organization == null) {
            organization = new ArrayList<OrganizationType>();
        }
        return this.organization;
    }

    /**
     * Gets the value of the property property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the property property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProperty().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PropertyType }
     * 
     * 
     */
    public List<PropertyType> getProperty() {
        if (property == null) {
            property = new ArrayList<PropertyType>();
        }
        return this.property;
    }

    /**
     * Gets the value of the medicalMalpracticePolicy property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the medicalMalpracticePolicy property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMedicalMalpracticePolicy().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MedicalMalpracticePolicyType }
     * 
     * 
     */
    public List<MedicalMalpracticePolicyType> getMedicalMalpracticePolicy() {
        if (medicalMalpracticePolicy == null) {
            medicalMalpracticePolicy = new ArrayList<MedicalMalpracticePolicyType>();
        }
        return this.medicalMalpracticePolicy;
    }

    /**
     * Gets the value of the messageStatus property.
     * 
     * @return
     *     possible object is
     *     {@link MessageStatusType }
     *     
     */
    public MessageStatusType getMessageStatus() {
        return messageStatus;
    }

    /**
     * Sets the value of the messageStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageStatusType }
     *     
     */
    public void setMessageStatus(MessageStatusType value) {
        this.messageStatus = value;
    }

}
