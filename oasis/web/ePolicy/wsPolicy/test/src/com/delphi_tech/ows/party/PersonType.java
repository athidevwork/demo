
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
 * <p>Java class for PersonType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PersonType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PersonNumberId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ClientId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ExternalReferenceId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PersonName" type="{http://www.delphi-tech.com/ows/Party}PersonNameType" minOccurs="0"/>
 *         &lt;element name="NationalProviderId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="GenderCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SocialSecurityNumberId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BirthDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BusinessEmail" type="{http://www.delphi-tech.com/ows/Party}BusinessEmailType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="BasicPhoneNumber" type="{http://www.delphi-tech.com/ows/Party}BasicPhoneNumberType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="BasicAddress" type="{http://www.delphi-tech.com/ows/Party}BasicAddressType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="EducationInformation" type="{http://www.delphi-tech.com/ows/Party}EducationInformationType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ProfessionalLicense" type="{http://www.delphi-tech.com/ows/Party}ProfessionalLicenseType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Certification" type="{http://www.delphi-tech.com/ows/Party}CertificationType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Contact" type="{http://www.delphi-tech.com/ows/Party}ContactType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="PartyNote" type="{http://www.delphi-tech.com/ows/Party}PartyNoteType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Relationship" type="{http://www.delphi-tech.com/ows/Party}RelationshipType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="PartyClassification" type="{http://www.delphi-tech.com/ows/Party}PartyClassificationType" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "PersonType", propOrder = {
    "personNumberId",
    "clientId",
    "externalReferenceId",
    "personName",
    "nationalProviderId",
    "genderCode",
    "socialSecurityNumberId",
    "birthDate",
    "businessEmail",
    "basicPhoneNumber",
    "basicAddress",
    "educationInformation",
    "professionalLicense",
    "certification",
    "contact",
    "partyNote",
    "relationship",
    "partyClassification"
})
public class PersonType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "PersonNumberId")
    protected String personNumberId;
    @XmlElement(name = "ClientId")
    protected String clientId;
    @XmlElement(name = "ExternalReferenceId")
    protected String externalReferenceId;
    @XmlElement(name = "PersonName")
    protected PersonNameType personName;
    @XmlElement(name = "NationalProviderId")
    protected String nationalProviderId;
    @XmlElement(name = "GenderCode")
    protected String genderCode;
    @XmlElement(name = "SocialSecurityNumberId")
    protected String socialSecurityNumberId;
    @XmlElement(name = "BirthDate")
    protected String birthDate;
    @XmlElement(name = "BusinessEmail")
    protected List<BusinessEmailType> businessEmail;
    @XmlElement(name = "BasicPhoneNumber")
    protected List<BasicPhoneNumberType> basicPhoneNumber;
    @XmlElement(name = "BasicAddress")
    protected List<BasicAddressType> basicAddress;
    @XmlElement(name = "EducationInformation")
    protected List<EducationInformationType> educationInformation;
    @XmlElement(name = "ProfessionalLicense")
    protected List<ProfessionalLicenseType> professionalLicense;
    @XmlElement(name = "Certification")
    protected List<CertificationType> certification;
    @XmlElement(name = "Contact")
    protected List<ContactType> contact;
    @XmlElement(name = "PartyNote")
    protected List<PartyNoteType> partyNote;
    @XmlElement(name = "Relationship")
    protected List<RelationshipType> relationship;
    @XmlElement(name = "PartyClassification")
    protected List<PartyClassificationType> partyClassification;
    @XmlAttribute(name = "key")
    protected String key;

    /**
     * Gets the value of the personNumberId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPersonNumberId() {
        return personNumberId;
    }

    /**
     * Sets the value of the personNumberId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPersonNumberId(String value) {
        this.personNumberId = value;
    }

    /**
     * Gets the value of the clientId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Sets the value of the clientId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClientId(String value) {
        this.clientId = value;
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
     * Gets the value of the nationalProviderId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNationalProviderId() {
        return nationalProviderId;
    }

    /**
     * Sets the value of the nationalProviderId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNationalProviderId(String value) {
        this.nationalProviderId = value;
    }

    /**
     * Gets the value of the genderCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGenderCode() {
        return genderCode;
    }

    /**
     * Sets the value of the genderCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGenderCode(String value) {
        this.genderCode = value;
    }

    /**
     * Gets the value of the socialSecurityNumberId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSocialSecurityNumberId() {
        return socialSecurityNumberId;
    }

    /**
     * Sets the value of the socialSecurityNumberId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSocialSecurityNumberId(String value) {
        this.socialSecurityNumberId = value;
    }

    /**
     * Gets the value of the birthDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBirthDate() {
        return birthDate;
    }

    /**
     * Sets the value of the birthDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBirthDate(String value) {
        this.birthDate = value;
    }

    /**
     * Gets the value of the businessEmail property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the businessEmail property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBusinessEmail().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BusinessEmailType }
     * 
     * 
     */
    public List<BusinessEmailType> getBusinessEmail() {
        if (businessEmail == null) {
            businessEmail = new ArrayList<BusinessEmailType>();
        }
        return this.businessEmail;
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
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the basicAddress property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBasicAddress().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BasicAddressType }
     * 
     * 
     */
    public List<BasicAddressType> getBasicAddress() {
        if (basicAddress == null) {
            basicAddress = new ArrayList<BasicAddressType>();
        }
        return this.basicAddress;
    }

    /**
     * Gets the value of the educationInformation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the educationInformation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEducationInformation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EducationInformationType }
     * 
     * 
     */
    public List<EducationInformationType> getEducationInformation() {
        if (educationInformation == null) {
            educationInformation = new ArrayList<EducationInformationType>();
        }
        return this.educationInformation;
    }

    /**
     * Gets the value of the professionalLicense property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the professionalLicense property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProfessionalLicense().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProfessionalLicenseType }
     * 
     * 
     */
    public List<ProfessionalLicenseType> getProfessionalLicense() {
        if (professionalLicense == null) {
            professionalLicense = new ArrayList<ProfessionalLicenseType>();
        }
        return this.professionalLicense;
    }

    /**
     * Gets the value of the certification property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the certification property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCertification().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CertificationType }
     * 
     * 
     */
    public List<CertificationType> getCertification() {
        if (certification == null) {
            certification = new ArrayList<CertificationType>();
        }
        return this.certification;
    }

    /**
     * Gets the value of the contact property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the contact property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContact().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ContactType }
     * 
     * 
     */
    public List<ContactType> getContact() {
        if (contact == null) {
            contact = new ArrayList<ContactType>();
        }
        return this.contact;
    }

    /**
     * Gets the value of the partyNote property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the partyNote property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPartyNote().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PartyNoteType }
     * 
     * 
     */
    public List<PartyNoteType> getPartyNote() {
        if (partyNote == null) {
            partyNote = new ArrayList<PartyNoteType>();
        }
        return this.partyNote;
    }

    /**
     * Gets the value of the relationship property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the relationship property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRelationship().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RelationshipType }
     * 
     * 
     */
    public List<RelationshipType> getRelationship() {
        if (relationship == null) {
            relationship = new ArrayList<RelationshipType>();
        }
        return this.relationship;
    }

    /**
     * Gets the value of the partyClassification property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the partyClassification property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPartyClassification().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PartyClassificationType }
     * 
     * 
     */
    public List<PartyClassificationType> getPartyClassification() {
        if (partyClassification == null) {
            partyClassification = new ArrayList<PartyClassificationType>();
        }
        return this.partyClassification;
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
