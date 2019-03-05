
package com.delphi_tech.ows.party;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CertificationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CertificationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CertificationNumberId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CertificationCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CertifiedIndicator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CertificationPeriod" type="{http://www.delphi-tech.com/ows/Party}CertificationPeriodType" minOccurs="0"/>
 *         &lt;element name="CertificationBoard" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Specialty" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ControllingStateOrProvinceCode" type="{http://www.delphi-tech.com/ows/Party}ControllingStateOrProvinceCodeType" minOccurs="0"/>
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
@XmlType(name = "CertificationType", propOrder = {
    "certificationNumberId",
    "certificationCode",
    "certifiedIndicator",
    "certificationPeriod",
    "certificationBoard",
    "specialty",
    "controllingStateOrProvinceCode"
})
public class CertificationType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "CertificationNumberId")
    protected String certificationNumberId;
    @XmlElement(name = "CertificationCode")
    protected String certificationCode;
    @XmlElement(name = "CertifiedIndicator")
    protected String certifiedIndicator;
    @XmlElement(name = "CertificationPeriod")
    protected CertificationPeriodType certificationPeriod;
    @XmlElement(name = "CertificationBoard")
    protected String certificationBoard;
    @XmlElement(name = "Specialty")
    protected String specialty;
    @XmlElement(name = "ControllingStateOrProvinceCode")
    protected ControllingStateOrProvinceCodeType controllingStateOrProvinceCode;
    @XmlAttribute(name = "key")
    protected String key;

    /**
     * Gets the value of the certificationNumberId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCertificationNumberId() {
        return certificationNumberId;
    }

    /**
     * Sets the value of the certificationNumberId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCertificationNumberId(String value) {
        this.certificationNumberId = value;
    }

    /**
     * Gets the value of the certificationCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCertificationCode() {
        return certificationCode;
    }

    /**
     * Sets the value of the certificationCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCertificationCode(String value) {
        this.certificationCode = value;
    }

    /**
     * Gets the value of the certifiedIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCertifiedIndicator() {
        return certifiedIndicator;
    }

    /**
     * Sets the value of the certifiedIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCertifiedIndicator(String value) {
        this.certifiedIndicator = value;
    }

    /**
     * Gets the value of the certificationPeriod property.
     * 
     * @return
     *     possible object is
     *     {@link CertificationPeriodType }
     *     
     */
    public CertificationPeriodType getCertificationPeriod() {
        return certificationPeriod;
    }

    /**
     * Sets the value of the certificationPeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link CertificationPeriodType }
     *     
     */
    public void setCertificationPeriod(CertificationPeriodType value) {
        this.certificationPeriod = value;
    }

    /**
     * Gets the value of the certificationBoard property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCertificationBoard() {
        return certificationBoard;
    }

    /**
     * Sets the value of the certificationBoard property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCertificationBoard(String value) {
        this.certificationBoard = value;
    }

    /**
     * Gets the value of the specialty property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpecialty() {
        return specialty;
    }

    /**
     * Sets the value of the specialty property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpecialty(String value) {
        this.specialty = value;
    }

    /**
     * Gets the value of the controllingStateOrProvinceCode property.
     * 
     * @return
     *     possible object is
     *     {@link ControllingStateOrProvinceCodeType }
     *     
     */
    public ControllingStateOrProvinceCodeType getControllingStateOrProvinceCode() {
        return controllingStateOrProvinceCode;
    }

    /**
     * Sets the value of the controllingStateOrProvinceCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link ControllingStateOrProvinceCodeType }
     *     
     */
    public void setControllingStateOrProvinceCode(ControllingStateOrProvinceCodeType value) {
        this.controllingStateOrProvinceCode = value;
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
