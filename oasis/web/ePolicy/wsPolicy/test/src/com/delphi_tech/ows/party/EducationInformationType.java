
package com.delphi_tech.ows.party;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EducationInformationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EducationInformationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="EducationInformationNumberId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EducationTypeCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EducationDegree" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="GraduationYear" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EducationalInstitution" type="{http://www.delphi-tech.com/ows/Party}EducationalInstitutionType" minOccurs="0"/>
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
@XmlType(name = "EducationInformationType", propOrder = {
    "educationInformationNumberId",
    "educationTypeCode",
    "educationDegree",
    "graduationYear",
    "educationalInstitution"
})
public class EducationInformationType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "EducationInformationNumberId")
    protected String educationInformationNumberId;
    @XmlElement(name = "EducationTypeCode")
    protected String educationTypeCode;
    @XmlElement(name = "EducationDegree")
    protected String educationDegree;
    @XmlElement(name = "GraduationYear")
    protected String graduationYear;
    @XmlElement(name = "EducationalInstitution")
    protected EducationalInstitutionType educationalInstitution;
    @XmlAttribute(name = "key")
    protected String key;

    /**
     * Gets the value of the educationInformationNumberId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEducationInformationNumberId() {
        return educationInformationNumberId;
    }

    /**
     * Sets the value of the educationInformationNumberId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEducationInformationNumberId(String value) {
        this.educationInformationNumberId = value;
    }

    /**
     * Gets the value of the educationTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEducationTypeCode() {
        return educationTypeCode;
    }

    /**
     * Sets the value of the educationTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEducationTypeCode(String value) {
        this.educationTypeCode = value;
    }

    /**
     * Gets the value of the educationDegree property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEducationDegree() {
        return educationDegree;
    }

    /**
     * Sets the value of the educationDegree property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEducationDegree(String value) {
        this.educationDegree = value;
    }

    /**
     * Gets the value of the graduationYear property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGraduationYear() {
        return graduationYear;
    }

    /**
     * Sets the value of the graduationYear property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGraduationYear(String value) {
        this.graduationYear = value;
    }

    /**
     * Gets the value of the educationalInstitution property.
     * 
     * @return
     *     possible object is
     *     {@link EducationalInstitutionType }
     *     
     */
    public EducationalInstitutionType getEducationalInstitution() {
        return educationalInstitution;
    }

    /**
     * Sets the value of the educationalInstitution property.
     * 
     * @param value
     *     allowed object is
     *     {@link EducationalInstitutionType }
     *     
     */
    public void setEducationalInstitution(EducationalInstitutionType value) {
        this.educationalInstitution = value;
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
