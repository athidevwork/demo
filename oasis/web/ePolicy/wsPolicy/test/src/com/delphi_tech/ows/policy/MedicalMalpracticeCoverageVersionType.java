
package com.delphi_tech.ows.policy;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MedicalMalpracticeCoverageVersionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MedicalMalpracticeCoverageVersionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MedicalMalpracticeCoverageVersionId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PrimaryIndicator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MedicalMalpracticeCoverageStatusCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EffectivePeriod" type="{http://www.delphi-tech.com/ows/Policy}EffectivePeriodType" minOccurs="0"/>
 *         &lt;element name="Limit" type="{http://www.delphi-tech.com/ows/Policy}LimitType" minOccurs="0"/>
 *         &lt;element name="ClaimMadeLiabilityPolicyInformation" type="{http://www.delphi-tech.com/ows/Policy}ClaimMadeLiabilityPolicyInformationType" minOccurs="0"/>
 *         &lt;element name="PayorCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CancellationMethodCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AnnualBaseRate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DefaultAmountOfInsurance" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AdditionalAmountOfInsurance" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LossOfIncomeDays" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ExposureUnit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BuildingRate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ForecastIndicator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DirectPrimaryIndicator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AdditionalSymbolCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CoverageConversionInformation" type="{http://www.delphi-tech.com/ows/Policy}CoverageConversionInformationType" minOccurs="0"/>
 *         &lt;element name="Pcf" type="{http://www.delphi-tech.com/ows/Policy}PcfType" minOccurs="0"/>
 *         &lt;element name="Deductible" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ManualDeductibleSIRCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ManualDeductibleSIRIncidentAmount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ManualDeductibleSIRAggregateAmount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DeductibleSIRIndemnityTypeCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AdditionalInformation" type="{http://www.delphi-tech.com/ows/Policy}AdditionalInformationType" minOccurs="0"/>
 *         &lt;element name="MedicalMalpracticeCoverageVersionDetail" type="{http://www.delphi-tech.com/ows/Policy}MedicalMalpracticeCoverageVersionDetailType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MedicalMalpracticeCoverageVersionType", propOrder = {
    "medicalMalpracticeCoverageVersionId",
    "primaryIndicator",
    "medicalMalpracticeCoverageStatusCode",
    "effectivePeriod",
    "limit",
    "claimMadeLiabilityPolicyInformation",
    "payorCode",
    "cancellationMethodCode",
    "annualBaseRate",
    "defaultAmountOfInsurance",
    "additionalAmountOfInsurance",
    "lossOfIncomeDays",
    "exposureUnit",
    "buildingRate",
    "forecastIndicator",
    "directPrimaryIndicator",
    "additionalSymbolCode",
    "coverageConversionInformation",
    "pcf",
    "deductible",
    "manualDeductibleSIRCode",
    "manualDeductibleSIRIncidentAmount",
    "manualDeductibleSIRAggregateAmount",
    "deductibleSIRIndemnityTypeCode",
    "additionalInformation",
    "medicalMalpracticeCoverageVersionDetail"
})
public class MedicalMalpracticeCoverageVersionType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "MedicalMalpracticeCoverageVersionId")
    protected String medicalMalpracticeCoverageVersionId;
    @XmlElement(name = "PrimaryIndicator")
    protected String primaryIndicator;
    @XmlElement(name = "MedicalMalpracticeCoverageStatusCode")
    protected String medicalMalpracticeCoverageStatusCode;
    @XmlElement(name = "EffectivePeriod")
    protected EffectivePeriodType effectivePeriod;
    @XmlElement(name = "Limit")
    protected LimitType limit;
    @XmlElement(name = "ClaimMadeLiabilityPolicyInformation")
    protected ClaimMadeLiabilityPolicyInformationType claimMadeLiabilityPolicyInformation;
    @XmlElement(name = "PayorCode")
    protected String payorCode;
    @XmlElement(name = "CancellationMethodCode")
    protected String cancellationMethodCode;
    @XmlElement(name = "AnnualBaseRate")
    protected String annualBaseRate;
    @XmlElement(name = "DefaultAmountOfInsurance")
    protected String defaultAmountOfInsurance;
    @XmlElement(name = "AdditionalAmountOfInsurance")
    protected String additionalAmountOfInsurance;
    @XmlElement(name = "LossOfIncomeDays")
    protected String lossOfIncomeDays;
    @XmlElement(name = "ExposureUnit")
    protected String exposureUnit;
    @XmlElement(name = "BuildingRate")
    protected String buildingRate;
    @XmlElement(name = "ForecastIndicator")
    protected String forecastIndicator;
    @XmlElement(name = "DirectPrimaryIndicator")
    protected String directPrimaryIndicator;
    @XmlElement(name = "AdditionalSymbolCode")
    protected String additionalSymbolCode;
    @XmlElement(name = "CoverageConversionInformation")
    protected CoverageConversionInformationType coverageConversionInformation;
    @XmlElement(name = "Pcf")
    protected PcfType pcf;
    @XmlElement(name = "Deductible")
    protected String deductible;
    @XmlElement(name = "ManualDeductibleSIRCode")
    protected String manualDeductibleSIRCode;
    @XmlElement(name = "ManualDeductibleSIRIncidentAmount")
    protected String manualDeductibleSIRIncidentAmount;
    @XmlElement(name = "ManualDeductibleSIRAggregateAmount")
    protected String manualDeductibleSIRAggregateAmount;
    @XmlElement(name = "DeductibleSIRIndemnityTypeCode")
    protected String deductibleSIRIndemnityTypeCode;
    @XmlElement(name = "AdditionalInformation")
    protected AdditionalInformationType additionalInformation;
    @XmlElement(name = "MedicalMalpracticeCoverageVersionDetail")
    protected MedicalMalpracticeCoverageVersionDetailType medicalMalpracticeCoverageVersionDetail;

    /**
     * Gets the value of the medicalMalpracticeCoverageVersionId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMedicalMalpracticeCoverageVersionId() {
        return medicalMalpracticeCoverageVersionId;
    }

    /**
     * Sets the value of the medicalMalpracticeCoverageVersionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMedicalMalpracticeCoverageVersionId(String value) {
        this.medicalMalpracticeCoverageVersionId = value;
    }

    /**
     * Gets the value of the primaryIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrimaryIndicator() {
        return primaryIndicator;
    }

    /**
     * Sets the value of the primaryIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrimaryIndicator(String value) {
        this.primaryIndicator = value;
    }

    /**
     * Gets the value of the medicalMalpracticeCoverageStatusCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMedicalMalpracticeCoverageStatusCode() {
        return medicalMalpracticeCoverageStatusCode;
    }

    /**
     * Sets the value of the medicalMalpracticeCoverageStatusCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMedicalMalpracticeCoverageStatusCode(String value) {
        this.medicalMalpracticeCoverageStatusCode = value;
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
     * Gets the value of the limit property.
     * 
     * @return
     *     possible object is
     *     {@link LimitType }
     *     
     */
    public LimitType getLimit() {
        return limit;
    }

    /**
     * Sets the value of the limit property.
     * 
     * @param value
     *     allowed object is
     *     {@link LimitType }
     *     
     */
    public void setLimit(LimitType value) {
        this.limit = value;
    }

    /**
     * Gets the value of the claimMadeLiabilityPolicyInformation property.
     * 
     * @return
     *     possible object is
     *     {@link ClaimMadeLiabilityPolicyInformationType }
     *     
     */
    public ClaimMadeLiabilityPolicyInformationType getClaimMadeLiabilityPolicyInformation() {
        return claimMadeLiabilityPolicyInformation;
    }

    /**
     * Sets the value of the claimMadeLiabilityPolicyInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClaimMadeLiabilityPolicyInformationType }
     *     
     */
    public void setClaimMadeLiabilityPolicyInformation(ClaimMadeLiabilityPolicyInformationType value) {
        this.claimMadeLiabilityPolicyInformation = value;
    }

    /**
     * Gets the value of the payorCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPayorCode() {
        return payorCode;
    }

    /**
     * Sets the value of the payorCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPayorCode(String value) {
        this.payorCode = value;
    }

    /**
     * Gets the value of the cancellationMethodCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCancellationMethodCode() {
        return cancellationMethodCode;
    }

    /**
     * Sets the value of the cancellationMethodCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCancellationMethodCode(String value) {
        this.cancellationMethodCode = value;
    }

    /**
     * Gets the value of the annualBaseRate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAnnualBaseRate() {
        return annualBaseRate;
    }

    /**
     * Sets the value of the annualBaseRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAnnualBaseRate(String value) {
        this.annualBaseRate = value;
    }

    /**
     * Gets the value of the defaultAmountOfInsurance property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultAmountOfInsurance() {
        return defaultAmountOfInsurance;
    }

    /**
     * Sets the value of the defaultAmountOfInsurance property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultAmountOfInsurance(String value) {
        this.defaultAmountOfInsurance = value;
    }

    /**
     * Gets the value of the additionalAmountOfInsurance property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalAmountOfInsurance() {
        return additionalAmountOfInsurance;
    }

    /**
     * Sets the value of the additionalAmountOfInsurance property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalAmountOfInsurance(String value) {
        this.additionalAmountOfInsurance = value;
    }

    /**
     * Gets the value of the lossOfIncomeDays property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLossOfIncomeDays() {
        return lossOfIncomeDays;
    }

    /**
     * Sets the value of the lossOfIncomeDays property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLossOfIncomeDays(String value) {
        this.lossOfIncomeDays = value;
    }

    /**
     * Gets the value of the exposureUnit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExposureUnit() {
        return exposureUnit;
    }

    /**
     * Sets the value of the exposureUnit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExposureUnit(String value) {
        this.exposureUnit = value;
    }

    /**
     * Gets the value of the buildingRate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBuildingRate() {
        return buildingRate;
    }

    /**
     * Sets the value of the buildingRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBuildingRate(String value) {
        this.buildingRate = value;
    }

    /**
     * Gets the value of the forecastIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getForecastIndicator() {
        return forecastIndicator;
    }

    /**
     * Sets the value of the forecastIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setForecastIndicator(String value) {
        this.forecastIndicator = value;
    }

    /**
     * Gets the value of the directPrimaryIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDirectPrimaryIndicator() {
        return directPrimaryIndicator;
    }

    /**
     * Sets the value of the directPrimaryIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDirectPrimaryIndicator(String value) {
        this.directPrimaryIndicator = value;
    }

    /**
     * Gets the value of the additionalSymbolCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalSymbolCode() {
        return additionalSymbolCode;
    }

    /**
     * Sets the value of the additionalSymbolCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalSymbolCode(String value) {
        this.additionalSymbolCode = value;
    }

    /**
     * Gets the value of the coverageConversionInformation property.
     * 
     * @return
     *     possible object is
     *     {@link CoverageConversionInformationType }
     *     
     */
    public CoverageConversionInformationType getCoverageConversionInformation() {
        return coverageConversionInformation;
    }

    /**
     * Sets the value of the coverageConversionInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link CoverageConversionInformationType }
     *     
     */
    public void setCoverageConversionInformation(CoverageConversionInformationType value) {
        this.coverageConversionInformation = value;
    }

    /**
     * Gets the value of the pcf property.
     * 
     * @return
     *     possible object is
     *     {@link PcfType }
     *     
     */
    public PcfType getPcf() {
        return pcf;
    }

    /**
     * Sets the value of the pcf property.
     * 
     * @param value
     *     allowed object is
     *     {@link PcfType }
     *     
     */
    public void setPcf(PcfType value) {
        this.pcf = value;
    }

    /**
     * Gets the value of the deductible property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeductible() {
        return deductible;
    }

    /**
     * Sets the value of the deductible property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeductible(String value) {
        this.deductible = value;
    }

    /**
     * Gets the value of the manualDeductibleSIRCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getManualDeductibleSIRCode() {
        return manualDeductibleSIRCode;
    }

    /**
     * Sets the value of the manualDeductibleSIRCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setManualDeductibleSIRCode(String value) {
        this.manualDeductibleSIRCode = value;
    }

    /**
     * Gets the value of the manualDeductibleSIRIncidentAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getManualDeductibleSIRIncidentAmount() {
        return manualDeductibleSIRIncidentAmount;
    }

    /**
     * Sets the value of the manualDeductibleSIRIncidentAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setManualDeductibleSIRIncidentAmount(String value) {
        this.manualDeductibleSIRIncidentAmount = value;
    }

    /**
     * Gets the value of the manualDeductibleSIRAggregateAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getManualDeductibleSIRAggregateAmount() {
        return manualDeductibleSIRAggregateAmount;
    }

    /**
     * Sets the value of the manualDeductibleSIRAggregateAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setManualDeductibleSIRAggregateAmount(String value) {
        this.manualDeductibleSIRAggregateAmount = value;
    }

    /**
     * Gets the value of the deductibleSIRIndemnityTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeductibleSIRIndemnityTypeCode() {
        return deductibleSIRIndemnityTypeCode;
    }

    /**
     * Sets the value of the deductibleSIRIndemnityTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeductibleSIRIndemnityTypeCode(String value) {
        this.deductibleSIRIndemnityTypeCode = value;
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
     * Gets the value of the medicalMalpracticeCoverageVersionDetail property.
     * 
     * @return
     *     possible object is
     *     {@link MedicalMalpracticeCoverageVersionDetailType }
     *     
     */
    public MedicalMalpracticeCoverageVersionDetailType getMedicalMalpracticeCoverageVersionDetail() {
        return medicalMalpracticeCoverageVersionDetail;
    }

    /**
     * Sets the value of the medicalMalpracticeCoverageVersionDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link MedicalMalpracticeCoverageVersionDetailType }
     *     
     */
    public void setMedicalMalpracticeCoverageVersionDetail(MedicalMalpracticeCoverageVersionDetailType value) {
        this.medicalMalpracticeCoverageVersionDetail = value;
    }

}
