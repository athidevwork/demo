
package com.delphi_tech.ows.policy;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LimitType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LimitType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="LimitTypeCode" type="{http://www.delphi-tech.com/ows/Policy}LimitTypeCodeType" minOccurs="0"/>
 *         &lt;element name="SharedLimitIndicator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IncidentLimitTypeCode" type="{http://www.delphi-tech.com/ows/Policy}IncidentLimitTypeCodeType" minOccurs="0"/>
 *         &lt;element name="AdjustedIncidentLimitTypeCode" type="{http://www.delphi-tech.com/ows/Policy}AdjustedIncidentLimitTypeCodeType" minOccurs="0"/>
 *         &lt;element name="AgregateLimitTypeCode" type="{http://www.delphi-tech.com/ows/Policy}AgregateLimitTypeCodeType" minOccurs="0"/>
 *         &lt;element name="AdjustedAgregateLimitTypeCode" type="{http://www.delphi-tech.com/ows/Policy}AdjustedAgregateLimitTypeCodeType" minOccurs="0"/>
 *         &lt;element name="SubLimitIndicator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ErosionTypeCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ManualIncidentLimitTypeCode" type="{http://www.delphi-tech.com/ows/Policy}ManualIncidentLimitTypeCodeType" minOccurs="0"/>
 *         &lt;element name="ManualAggregateLimitTypeCode" type="{http://www.delphi-tech.com/ows/Policy}ManualAggregateLimitTypeCodeType" minOccurs="0"/>
 *         &lt;element name="DailyLimitAmount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LimitType", propOrder = {
    "limitTypeCode",
    "sharedLimitIndicator",
    "incidentLimitTypeCode",
    "adjustedIncidentLimitTypeCode",
    "agregateLimitTypeCode",
    "adjustedAgregateLimitTypeCode",
    "subLimitIndicator",
    "erosionTypeCode",
    "manualIncidentLimitTypeCode",
    "manualAggregateLimitTypeCode",
    "dailyLimitAmount"
})
public class LimitType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "LimitTypeCode")
    protected LimitTypeCodeType limitTypeCode;
    @XmlElement(name = "SharedLimitIndicator")
    protected String sharedLimitIndicator;
    @XmlElement(name = "IncidentLimitTypeCode")
    protected IncidentLimitTypeCodeType incidentLimitTypeCode;
    @XmlElement(name = "AdjustedIncidentLimitTypeCode")
    protected AdjustedIncidentLimitTypeCodeType adjustedIncidentLimitTypeCode;
    @XmlElement(name = "AgregateLimitTypeCode")
    protected AgregateLimitTypeCodeType agregateLimitTypeCode;
    @XmlElement(name = "AdjustedAgregateLimitTypeCode")
    protected AdjustedAgregateLimitTypeCodeType adjustedAgregateLimitTypeCode;
    @XmlElement(name = "SubLimitIndicator")
    protected String subLimitIndicator;
    @XmlElement(name = "ErosionTypeCode")
    protected String erosionTypeCode;
    @XmlElement(name = "ManualIncidentLimitTypeCode")
    protected ManualIncidentLimitTypeCodeType manualIncidentLimitTypeCode;
    @XmlElement(name = "ManualAggregateLimitTypeCode")
    protected ManualAggregateLimitTypeCodeType manualAggregateLimitTypeCode;
    @XmlElement(name = "DailyLimitAmount")
    protected String dailyLimitAmount;

    /**
     * Gets the value of the limitTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link LimitTypeCodeType }
     *     
     */
    public LimitTypeCodeType getLimitTypeCode() {
        return limitTypeCode;
    }

    /**
     * Sets the value of the limitTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link LimitTypeCodeType }
     *     
     */
    public void setLimitTypeCode(LimitTypeCodeType value) {
        this.limitTypeCode = value;
    }

    /**
     * Gets the value of the sharedLimitIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSharedLimitIndicator() {
        return sharedLimitIndicator;
    }

    /**
     * Sets the value of the sharedLimitIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSharedLimitIndicator(String value) {
        this.sharedLimitIndicator = value;
    }

    /**
     * Gets the value of the incidentLimitTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link IncidentLimitTypeCodeType }
     *     
     */
    public IncidentLimitTypeCodeType getIncidentLimitTypeCode() {
        return incidentLimitTypeCode;
    }

    /**
     * Sets the value of the incidentLimitTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link IncidentLimitTypeCodeType }
     *     
     */
    public void setIncidentLimitTypeCode(IncidentLimitTypeCodeType value) {
        this.incidentLimitTypeCode = value;
    }

    /**
     * Gets the value of the adjustedIncidentLimitTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link AdjustedIncidentLimitTypeCodeType }
     *     
     */
    public AdjustedIncidentLimitTypeCodeType getAdjustedIncidentLimitTypeCode() {
        return adjustedIncidentLimitTypeCode;
    }

    /**
     * Sets the value of the adjustedIncidentLimitTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdjustedIncidentLimitTypeCodeType }
     *     
     */
    public void setAdjustedIncidentLimitTypeCode(AdjustedIncidentLimitTypeCodeType value) {
        this.adjustedIncidentLimitTypeCode = value;
    }

    /**
     * Gets the value of the agregateLimitTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link AgregateLimitTypeCodeType }
     *     
     */
    public AgregateLimitTypeCodeType getAgregateLimitTypeCode() {
        return agregateLimitTypeCode;
    }

    /**
     * Sets the value of the agregateLimitTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link AgregateLimitTypeCodeType }
     *     
     */
    public void setAgregateLimitTypeCode(AgregateLimitTypeCodeType value) {
        this.agregateLimitTypeCode = value;
    }

    /**
     * Gets the value of the adjustedAgregateLimitTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link AdjustedAgregateLimitTypeCodeType }
     *     
     */
    public AdjustedAgregateLimitTypeCodeType getAdjustedAgregateLimitTypeCode() {
        return adjustedAgregateLimitTypeCode;
    }

    /**
     * Sets the value of the adjustedAgregateLimitTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdjustedAgregateLimitTypeCodeType }
     *     
     */
    public void setAdjustedAgregateLimitTypeCode(AdjustedAgregateLimitTypeCodeType value) {
        this.adjustedAgregateLimitTypeCode = value;
    }

    /**
     * Gets the value of the subLimitIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubLimitIndicator() {
        return subLimitIndicator;
    }

    /**
     * Sets the value of the subLimitIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubLimitIndicator(String value) {
        this.subLimitIndicator = value;
    }

    /**
     * Gets the value of the erosionTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErosionTypeCode() {
        return erosionTypeCode;
    }

    /**
     * Sets the value of the erosionTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErosionTypeCode(String value) {
        this.erosionTypeCode = value;
    }

    /**
     * Gets the value of the manualIncidentLimitTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link ManualIncidentLimitTypeCodeType }
     *     
     */
    public ManualIncidentLimitTypeCodeType getManualIncidentLimitTypeCode() {
        return manualIncidentLimitTypeCode;
    }

    /**
     * Sets the value of the manualIncidentLimitTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link ManualIncidentLimitTypeCodeType }
     *     
     */
    public void setManualIncidentLimitTypeCode(ManualIncidentLimitTypeCodeType value) {
        this.manualIncidentLimitTypeCode = value;
    }

    /**
     * Gets the value of the manualAggregateLimitTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link ManualAggregateLimitTypeCodeType }
     *     
     */
    public ManualAggregateLimitTypeCodeType getManualAggregateLimitTypeCode() {
        return manualAggregateLimitTypeCode;
    }

    /**
     * Sets the value of the manualAggregateLimitTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link ManualAggregateLimitTypeCodeType }
     *     
     */
    public void setManualAggregateLimitTypeCode(ManualAggregateLimitTypeCodeType value) {
        this.manualAggregateLimitTypeCode = value;
    }

    /**
     * Gets the value of the dailyLimitAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDailyLimitAmount() {
        return dailyLimitAmount;
    }

    /**
     * Sets the value of the dailyLimitAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDailyLimitAmount(String value) {
        this.dailyLimitAmount = value;
    }

}
