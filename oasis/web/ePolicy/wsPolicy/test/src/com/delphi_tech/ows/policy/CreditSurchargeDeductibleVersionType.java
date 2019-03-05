
package com.delphi_tech.ows.policy;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CreditSurchargeDeductibleVersionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CreditSurchargeDeductibleVersionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CreditSurchargeDeductibleVersionId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EffectivePeriod" type="{http://www.delphi-tech.com/ows/Policy}EffectivePeriodType" minOccurs="0"/>
 *         &lt;element name="NumericValue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IncidentDeductibleNumericValue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AggregateDeductibleNumericValue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CycleDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ProrateIndicator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ClassificationCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AdditionalNotes" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AdditionalInformation" type="{http://www.delphi-tech.com/ows/Policy}AdditionalInformationType" minOccurs="0"/>
 *         &lt;element name="CreditSurchargeDeductibleVersionDetail" type="{http://www.delphi-tech.com/ows/Policy}CreditSurchargeDeductibleVersionDetailType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreditSurchargeDeductibleVersionType", propOrder = {
    "creditSurchargeDeductibleVersionId",
    "effectivePeriod",
    "numericValue",
    "incidentDeductibleNumericValue",
    "aggregateDeductibleNumericValue",
    "cycleDate",
    "prorateIndicator",
    "classificationCode",
    "additionalNotes",
    "additionalInformation",
    "creditSurchargeDeductibleVersionDetail"
})
public class CreditSurchargeDeductibleVersionType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "CreditSurchargeDeductibleVersionId")
    protected String creditSurchargeDeductibleVersionId;
    @XmlElement(name = "EffectivePeriod")
    protected EffectivePeriodType effectivePeriod;
    @XmlElement(name = "NumericValue")
    protected String numericValue;
    @XmlElement(name = "IncidentDeductibleNumericValue")
    protected String incidentDeductibleNumericValue;
    @XmlElement(name = "AggregateDeductibleNumericValue")
    protected String aggregateDeductibleNumericValue;
    @XmlElement(name = "CycleDate")
    protected String cycleDate;
    @XmlElement(name = "ProrateIndicator")
    protected String prorateIndicator;
    @XmlElement(name = "ClassificationCode")
    protected String classificationCode;
    @XmlElement(name = "AdditionalNotes")
    protected String additionalNotes;
    @XmlElement(name = "AdditionalInformation")
    protected AdditionalInformationType additionalInformation;
    @XmlElement(name = "CreditSurchargeDeductibleVersionDetail")
    protected CreditSurchargeDeductibleVersionDetailType creditSurchargeDeductibleVersionDetail;

    /**
     * Gets the value of the creditSurchargeDeductibleVersionId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreditSurchargeDeductibleVersionId() {
        return creditSurchargeDeductibleVersionId;
    }

    /**
     * Sets the value of the creditSurchargeDeductibleVersionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreditSurchargeDeductibleVersionId(String value) {
        this.creditSurchargeDeductibleVersionId = value;
    }

    /**
     * Gets the value of the effectivePeriod property.
     * 
     * @return
     *     possible object is
     *     {@link EffectivePeriodType }
     *     
     */
    public EffectivePeriodType getEffectivePeriod() {
        return effectivePeriod;
    }

    /**
     * Sets the value of the effectivePeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link EffectivePeriodType }
     *     
     */
    public void setEffectivePeriod(EffectivePeriodType value) {
        this.effectivePeriod = value;
    }

    /**
     * Gets the value of the numericValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumericValue() {
        return numericValue;
    }

    /**
     * Sets the value of the numericValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumericValue(String value) {
        this.numericValue = value;
    }

    /**
     * Gets the value of the incidentDeductibleNumericValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIncidentDeductibleNumericValue() {
        return incidentDeductibleNumericValue;
    }

    /**
     * Sets the value of the incidentDeductibleNumericValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIncidentDeductibleNumericValue(String value) {
        this.incidentDeductibleNumericValue = value;
    }

    /**
     * Gets the value of the aggregateDeductibleNumericValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAggregateDeductibleNumericValue() {
        return aggregateDeductibleNumericValue;
    }

    /**
     * Sets the value of the aggregateDeductibleNumericValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAggregateDeductibleNumericValue(String value) {
        this.aggregateDeductibleNumericValue = value;
    }

    /**
     * Gets the value of the cycleDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCycleDate() {
        return cycleDate;
    }

    /**
     * Sets the value of the cycleDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCycleDate(String value) {
        this.cycleDate = value;
    }

    /**
     * Gets the value of the prorateIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProrateIndicator() {
        return prorateIndicator;
    }

    /**
     * Sets the value of the prorateIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProrateIndicator(String value) {
        this.prorateIndicator = value;
    }

    /**
     * Gets the value of the classificationCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClassificationCode() {
        return classificationCode;
    }

    /**
     * Sets the value of the classificationCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClassificationCode(String value) {
        this.classificationCode = value;
    }

    /**
     * Gets the value of the additionalNotes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalNotes() {
        return additionalNotes;
    }

    /**
     * Sets the value of the additionalNotes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalNotes(String value) {
        this.additionalNotes = value;
    }

    /**
     * Gets the value of the additionalInformation property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalInformationType }
     *     
     */
    public AdditionalInformationType getAdditionalInformation() {
        return additionalInformation;
    }

    /**
     * Sets the value of the additionalInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalInformationType }
     *     
     */
    public void setAdditionalInformation(AdditionalInformationType value) {
        this.additionalInformation = value;
    }

    /**
     * Gets the value of the creditSurchargeDeductibleVersionDetail property.
     * 
     * @return
     *     possible object is
     *     {@link CreditSurchargeDeductibleVersionDetailType }
     *     
     */
    public CreditSurchargeDeductibleVersionDetailType getCreditSurchargeDeductibleVersionDetail() {
        return creditSurchargeDeductibleVersionDetail;
    }

    /**
     * Sets the value of the creditSurchargeDeductibleVersionDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link CreditSurchargeDeductibleVersionDetailType }
     *     
     */
    public void setCreditSurchargeDeductibleVersionDetail(CreditSurchargeDeductibleVersionDetailType value) {
        this.creditSurchargeDeductibleVersionDetail = value;
    }

}
