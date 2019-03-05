
package com.delphi_tech.ows.policy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CreditSurchargeDeductibleType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CreditSurchargeDeductibleType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CreditSurchargeDeductibleNumberId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ReferredMedicalMalpracticeCoverage" type="{http://www.delphi-tech.com/ows/Policy}ReferredMedicalMalpracticeCoverageType" minOccurs="0"/>
 *         &lt;element name="CreditSurchargeDeductibleCodeNumberId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CurrentTermAmount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CreditSurchargeDeductibleCode" type="{http://www.delphi-tech.com/ows/Policy}CreditSurchargeDeductibleCodeType" minOccurs="0"/>
 *         &lt;element name="CreditSurchargeDeductibleTypeCode" type="{http://www.delphi-tech.com/ows/Policy}CreditSurchargeDeductibleTypeCodeType" minOccurs="0"/>
 *         &lt;element name="CreditSurchargeDeductibleVersion" type="{http://www.delphi-tech.com/ows/Policy}CreditSurchargeDeductibleVersionType" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "CreditSurchargeDeductibleType", propOrder = {
    "creditSurchargeDeductibleNumberId",
    "referredMedicalMalpracticeCoverage",
    "creditSurchargeDeductibleCodeNumberId",
    "currentTermAmount",
    "creditSurchargeDeductibleCode",
    "creditSurchargeDeductibleTypeCode",
    "creditSurchargeDeductibleVersion"
})
public class CreditSurchargeDeductibleType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "CreditSurchargeDeductibleNumberId")
    protected String creditSurchargeDeductibleNumberId;
    @XmlElement(name = "ReferredMedicalMalpracticeCoverage")
    protected ReferredMedicalMalpracticeCoverageType referredMedicalMalpracticeCoverage;
    @XmlElement(name = "CreditSurchargeDeductibleCodeNumberId")
    protected String creditSurchargeDeductibleCodeNumberId;
    @XmlElement(name = "CurrentTermAmount")
    protected String currentTermAmount;
    @XmlElement(name = "CreditSurchargeDeductibleCode")
    protected CreditSurchargeDeductibleCodeType creditSurchargeDeductibleCode;
    @XmlElement(name = "CreditSurchargeDeductibleTypeCode")
    protected CreditSurchargeDeductibleTypeCodeType creditSurchargeDeductibleTypeCode;
    @XmlElement(name = "CreditSurchargeDeductibleVersion")
    protected List<CreditSurchargeDeductibleVersionType> creditSurchargeDeductibleVersion;
    @XmlAttribute(name = "key")
    protected String key;

    /**
     * Gets the value of the creditSurchargeDeductibleNumberId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreditSurchargeDeductibleNumberId() {
        return creditSurchargeDeductibleNumberId;
    }

    /**
     * Sets the value of the creditSurchargeDeductibleNumberId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreditSurchargeDeductibleNumberId(String value) {
        this.creditSurchargeDeductibleNumberId = value;
    }

    /**
     * Gets the value of the referredMedicalMalpracticeCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link ReferredMedicalMalpracticeCoverageType }
     *     
     */
    public ReferredMedicalMalpracticeCoverageType getReferredMedicalMalpracticeCoverage() {
        return referredMedicalMalpracticeCoverage;
    }

    /**
     * Sets the value of the referredMedicalMalpracticeCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferredMedicalMalpracticeCoverageType }
     *     
     */
    public void setReferredMedicalMalpracticeCoverage(ReferredMedicalMalpracticeCoverageType value) {
        this.referredMedicalMalpracticeCoverage = value;
    }

    /**
     * Gets the value of the creditSurchargeDeductibleCodeNumberId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreditSurchargeDeductibleCodeNumberId() {
        return creditSurchargeDeductibleCodeNumberId;
    }

    /**
     * Sets the value of the creditSurchargeDeductibleCodeNumberId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreditSurchargeDeductibleCodeNumberId(String value) {
        this.creditSurchargeDeductibleCodeNumberId = value;
    }

    /**
     * Gets the value of the currentTermAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCurrentTermAmount() {
        return currentTermAmount;
    }

    /**
     * Sets the value of the currentTermAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCurrentTermAmount(String value) {
        this.currentTermAmount = value;
    }

    /**
     * Gets the value of the creditSurchargeDeductibleCode property.
     * 
     * @return
     *     possible object is
     *     {@link CreditSurchargeDeductibleCodeType }
     *     
     */
    public CreditSurchargeDeductibleCodeType getCreditSurchargeDeductibleCode() {
        return creditSurchargeDeductibleCode;
    }

    /**
     * Sets the value of the creditSurchargeDeductibleCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CreditSurchargeDeductibleCodeType }
     *     
     */
    public void setCreditSurchargeDeductibleCode(CreditSurchargeDeductibleCodeType value) {
        this.creditSurchargeDeductibleCode = value;
    }

    /**
     * Gets the value of the creditSurchargeDeductibleTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link CreditSurchargeDeductibleTypeCodeType }
     *     
     */
    public CreditSurchargeDeductibleTypeCodeType getCreditSurchargeDeductibleTypeCode() {
        return creditSurchargeDeductibleTypeCode;
    }

    /**
     * Sets the value of the creditSurchargeDeductibleTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CreditSurchargeDeductibleTypeCodeType }
     *     
     */
    public void setCreditSurchargeDeductibleTypeCode(CreditSurchargeDeductibleTypeCodeType value) {
        this.creditSurchargeDeductibleTypeCode = value;
    }

    /**
     * Gets the value of the creditSurchargeDeductibleVersion property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the creditSurchargeDeductibleVersion property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCreditSurchargeDeductibleVersion().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CreditSurchargeDeductibleVersionType }
     * 
     * 
     */
    public List<CreditSurchargeDeductibleVersionType> getCreditSurchargeDeductibleVersion() {
        if (creditSurchargeDeductibleVersion == null) {
            creditSurchargeDeductibleVersion = new ArrayList<CreditSurchargeDeductibleVersionType>();
        }
        return this.creditSurchargeDeductibleVersion;
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
