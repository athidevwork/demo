
package com.delphi_tech.ows.policy;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for InsuredVersionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InsuredVersionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="InsuredVersionNumberId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PrimaryIndicator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EffectivePeriod" type="{http://www.delphi-tech.com/ows/Policy}EffectivePeriodType" minOccurs="0"/>
 *         &lt;element name="PracticeStateOrProvinceCode" type="{http://www.delphi-tech.com/ows/Policy}PracticeStateOrProvinceCodeType" minOccurs="0"/>
 *         &lt;element name="PracticeCountyCode" type="{http://www.delphi-tech.com/ows/Policy}PracticeCountyCodeType" minOccurs="0"/>
 *         &lt;element name="InsuredStatusCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="InsuredTypeCode" type="{http://www.delphi-tech.com/ows/Policy}InsuredTypeCodeType" minOccurs="0"/>
 *         &lt;element name="InsuredClassCode" type="{http://www.delphi-tech.com/ows/Policy}InsuredClassCodeType" minOccurs="0"/>
 *         &lt;element name="InsuredSubClassCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="InsuredAlternateSpecialtyCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="InsuredClaimsDeductibleNumberId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="InsuredAlternateMethodCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="InsuredRevenueBandAmount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="InsuredRatingTier" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TeachingIndicator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="InsuredProcedureCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="InsuredMatureIndicator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="InsuredMoonlightingIndicator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ClaimsMadeYear" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IbnrIndicator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IbnrStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ScorecardEligibilityIndicator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="InsuredCityCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AdditionalNotes" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AddressNumberId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FullTimeEquivalencyInformation" type="{http://www.delphi-tech.com/ows/Policy}FullTimeEquivalencyInformationType" minOccurs="0"/>
 *         &lt;element name="MalpracticeLiabilityExposureInformation" type="{http://www.delphi-tech.com/ows/Policy}MalpracticeLiabilityExposureInformationType" minOccurs="0"/>
 *         &lt;element name="BuildingInformation" type="{http://www.delphi-tech.com/ows/Policy}BuildingInformationType" minOccurs="0"/>
 *         &lt;element name="VehiclesOperatedInformation" type="{http://www.delphi-tech.com/ows/Policy}VehiclesOperatedInformationType" minOccurs="0"/>
 *         &lt;element name="AdditionalInformation" type="{http://www.delphi-tech.com/ows/Policy}AdditionalInformationType" minOccurs="0"/>
 *         &lt;element name="InsuredVersionDetail" type="{http://www.delphi-tech.com/ows/Policy}InsuredVersionDetailType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InsuredVersionType", propOrder = {
    "insuredVersionNumberId",
    "primaryIndicator",
    "effectivePeriod",
    "practiceStateOrProvinceCode",
    "practiceCountyCode",
    "insuredStatusCode",
    "insuredTypeCode",
    "insuredClassCode",
    "insuredSubClassCode",
    "insuredAlternateSpecialtyCode",
    "insuredClaimsDeductibleNumberId",
    "insuredAlternateMethodCode",
    "insuredRevenueBandAmount",
    "insuredRatingTier",
    "teachingIndicator",
    "insuredProcedureCode",
    "insuredMatureIndicator",
    "insuredMoonlightingIndicator",
    "claimsMadeYear",
    "ibnrIndicator",
    "ibnrStatus",
    "scorecardEligibilityIndicator",
    "insuredCityCode",
    "additionalNotes",
    "addressNumberId",
    "fullTimeEquivalencyInformation",
    "malpracticeLiabilityExposureInformation",
    "buildingInformation",
    "vehiclesOperatedInformation",
    "additionalInformation",
    "insuredVersionDetail"
})
public class InsuredVersionType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "InsuredVersionNumberId")
    protected String insuredVersionNumberId;
    @XmlElement(name = "PrimaryIndicator")
    protected String primaryIndicator;
    @XmlElement(name = "EffectivePeriod")
    protected EffectivePeriodType effectivePeriod;
    @XmlElement(name = "PracticeStateOrProvinceCode")
    protected PracticeStateOrProvinceCodeType practiceStateOrProvinceCode;
    @XmlElement(name = "PracticeCountyCode")
    protected PracticeCountyCodeType practiceCountyCode;
    @XmlElement(name = "InsuredStatusCode")
    protected String insuredStatusCode;
    @XmlElement(name = "InsuredTypeCode")
    protected InsuredTypeCodeType insuredTypeCode;
    @XmlElement(name = "InsuredClassCode")
    protected InsuredClassCodeType insuredClassCode;
    @XmlElement(name = "InsuredSubClassCode")
    protected String insuredSubClassCode;
    @XmlElement(name = "InsuredAlternateSpecialtyCode")
    protected String insuredAlternateSpecialtyCode;
    @XmlElement(name = "InsuredClaimsDeductibleNumberId")
    protected String insuredClaimsDeductibleNumberId;
    @XmlElement(name = "InsuredAlternateMethodCode")
    protected String insuredAlternateMethodCode;
    @XmlElement(name = "InsuredRevenueBandAmount")
    protected String insuredRevenueBandAmount;
    @XmlElement(name = "InsuredRatingTier")
    protected String insuredRatingTier;
    @XmlElement(name = "TeachingIndicator")
    protected String teachingIndicator;
    @XmlElement(name = "InsuredProcedureCode")
    protected String insuredProcedureCode;
    @XmlElement(name = "InsuredMatureIndicator")
    protected String insuredMatureIndicator;
    @XmlElement(name = "InsuredMoonlightingIndicator")
    protected String insuredMoonlightingIndicator;
    @XmlElement(name = "ClaimsMadeYear")
    protected String claimsMadeYear;
    @XmlElement(name = "IbnrIndicator")
    protected String ibnrIndicator;
    @XmlElement(name = "IbnrStatus")
    protected String ibnrStatus;
    @XmlElement(name = "ScorecardEligibilityIndicator")
    protected String scorecardEligibilityIndicator;
    @XmlElement(name = "InsuredCityCode")
    protected String insuredCityCode;
    @XmlElement(name = "AdditionalNotes")
    protected String additionalNotes;
    @XmlElement(name = "AddressNumberId")
    protected String addressNumberId;
    @XmlElement(name = "FullTimeEquivalencyInformation")
    protected FullTimeEquivalencyInformationType fullTimeEquivalencyInformation;
    @XmlElement(name = "MalpracticeLiabilityExposureInformation")
    protected MalpracticeLiabilityExposureInformationType malpracticeLiabilityExposureInformation;
    @XmlElement(name = "BuildingInformation")
    protected BuildingInformationType buildingInformation;
    @XmlElement(name = "VehiclesOperatedInformation")
    protected VehiclesOperatedInformationType vehiclesOperatedInformation;
    @XmlElement(name = "AdditionalInformation")
    protected AdditionalInformationType additionalInformation;
    @XmlElement(name = "InsuredVersionDetail")
    protected InsuredVersionDetailType insuredVersionDetail;

    /**
     * Gets the value of the insuredVersionNumberId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInsuredVersionNumberId() {
        return insuredVersionNumberId;
    }

    /**
     * Sets the value of the insuredVersionNumberId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInsuredVersionNumberId(String value) {
        this.insuredVersionNumberId = value;
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
     * Gets the value of the practiceStateOrProvinceCode property.
     * 
     * @return
     *     possible object is
     *     {@link PracticeStateOrProvinceCodeType }
     *     
     */
    public PracticeStateOrProvinceCodeType getPracticeStateOrProvinceCode() {
        return practiceStateOrProvinceCode;
    }

    /**
     * Sets the value of the practiceStateOrProvinceCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link PracticeStateOrProvinceCodeType }
     *     
     */
    public void setPracticeStateOrProvinceCode(PracticeStateOrProvinceCodeType value) {
        this.practiceStateOrProvinceCode = value;
    }

    /**
     * Gets the value of the practiceCountyCode property.
     * 
     * @return
     *     possible object is
     *     {@link PracticeCountyCodeType }
     *     
     */
    public PracticeCountyCodeType getPracticeCountyCode() {
        return practiceCountyCode;
    }

    /**
     * Sets the value of the practiceCountyCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link PracticeCountyCodeType }
     *     
     */
    public void setPracticeCountyCode(PracticeCountyCodeType value) {
        this.practiceCountyCode = value;
    }

    /**
     * Gets the value of the insuredStatusCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInsuredStatusCode() {
        return insuredStatusCode;
    }

    /**
     * Sets the value of the insuredStatusCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInsuredStatusCode(String value) {
        this.insuredStatusCode = value;
    }

    /**
     * Gets the value of the insuredTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link InsuredTypeCodeType }
     *     
     */
    public InsuredTypeCodeType getInsuredTypeCode() {
        return insuredTypeCode;
    }

    /**
     * Sets the value of the insuredTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link InsuredTypeCodeType }
     *     
     */
    public void setInsuredTypeCode(InsuredTypeCodeType value) {
        this.insuredTypeCode = value;
    }

    /**
     * Gets the value of the insuredClassCode property.
     * 
     * @return
     *     possible object is
     *     {@link InsuredClassCodeType }
     *     
     */
    public InsuredClassCodeType getInsuredClassCode() {
        return insuredClassCode;
    }

    /**
     * Sets the value of the insuredClassCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link InsuredClassCodeType }
     *     
     */
    public void setInsuredClassCode(InsuredClassCodeType value) {
        this.insuredClassCode = value;
    }

    /**
     * Gets the value of the insuredSubClassCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInsuredSubClassCode() {
        return insuredSubClassCode;
    }

    /**
     * Sets the value of the insuredSubClassCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInsuredSubClassCode(String value) {
        this.insuredSubClassCode = value;
    }

    /**
     * Gets the value of the insuredAlternateSpecialtyCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInsuredAlternateSpecialtyCode() {
        return insuredAlternateSpecialtyCode;
    }

    /**
     * Sets the value of the insuredAlternateSpecialtyCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInsuredAlternateSpecialtyCode(String value) {
        this.insuredAlternateSpecialtyCode = value;
    }

    /**
     * Gets the value of the insuredClaimsDeductibleNumberId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInsuredClaimsDeductibleNumberId() {
        return insuredClaimsDeductibleNumberId;
    }

    /**
     * Sets the value of the insuredClaimsDeductibleNumberId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInsuredClaimsDeductibleNumberId(String value) {
        this.insuredClaimsDeductibleNumberId = value;
    }

    /**
     * Gets the value of the insuredAlternateMethodCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInsuredAlternateMethodCode() {
        return insuredAlternateMethodCode;
    }

    /**
     * Sets the value of the insuredAlternateMethodCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInsuredAlternateMethodCode(String value) {
        this.insuredAlternateMethodCode = value;
    }

    /**
     * Gets the value of the insuredRevenueBandAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInsuredRevenueBandAmount() {
        return insuredRevenueBandAmount;
    }

    /**
     * Sets the value of the insuredRevenueBandAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInsuredRevenueBandAmount(String value) {
        this.insuredRevenueBandAmount = value;
    }

    /**
     * Gets the value of the insuredRatingTier property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getInsuredRatingTier() {
        return insuredRatingTier;
    }

    /**
     * Sets the value of the insuredRatingTier property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setInsuredRatingTier(String value) {
        this.insuredRatingTier = value;
    }

    /**
     * Gets the value of the teachingIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTeachingIndicator() {
        return teachingIndicator;
    }

    /**
     * Sets the value of the teachingIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTeachingIndicator(String value) {
        this.teachingIndicator = value;
    }

    /**
     * Gets the value of the insuredProcedureCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInsuredProcedureCode() {
        return insuredProcedureCode;
    }

    /**
     * Sets the value of the insuredProcedureCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInsuredProcedureCode(String value) {
        this.insuredProcedureCode = value;
    }

    /**
     * Gets the value of the insuredMatureIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInsuredMatureIndicator() {
        return insuredMatureIndicator;
    }

    /**
     * Sets the value of the insuredMatureIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInsuredMatureIndicator(String value) {
        this.insuredMatureIndicator = value;
    }

    /**
     * Gets the value of the insuredMoonlightingIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInsuredMoonlightingIndicator() {
        return insuredMoonlightingIndicator;
    }

    /**
     * Sets the value of the insuredMoonlightingIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInsuredMoonlightingIndicator(String value) {
        this.insuredMoonlightingIndicator = value;
    }

    /**
     * Gets the value of the claimsMadeYear property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClaimsMadeYear() {
        return claimsMadeYear;
    }

    /**
     * Sets the value of the claimsMadeYear property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClaimsMadeYear(String value) {
        this.claimsMadeYear = value;
    }

    /**
     * Gets the value of the ibnrIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIbnrIndicator() {
        return ibnrIndicator;
    }

    /**
     * Sets the value of the ibnrIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIbnrIndicator(String value) {
        this.ibnrIndicator = value;
    }

    /**
     * Gets the value of the ibnrStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIbnrStatus() {
        return ibnrStatus;
    }

    /**
     * Sets the value of the ibnrStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIbnrStatus(String value) {
        this.ibnrStatus = value;
    }

    /**
     * Gets the value of the scorecardEligibilityIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getScorecardEligibilityIndicator() {
        return scorecardEligibilityIndicator;
    }

    /**
     * Sets the value of the scorecardEligibilityIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScorecardEligibilityIndicator(String value) {
        this.scorecardEligibilityIndicator = value;
    }

    /**
     * Gets the value of the insuredCityCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInsuredCityCode() {
        return insuredCityCode;
    }

    /**
     * Sets the value of the insuredCityCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInsuredCityCode(String value) {
        this.insuredCityCode = value;
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
     * Gets the value of the addressNumberId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddressNumberId() {
        return addressNumberId;
    }

    /**
     * Sets the value of the addressNumberId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddressNumberId(String value) {
        this.addressNumberId = value;
    }

    /**
     * Gets the value of the fullTimeEquivalencyInformation property.
     * 
     * @return
     *     possible object is
     *     {@link FullTimeEquivalencyInformationType }
     *     
     */
    public FullTimeEquivalencyInformationType getFullTimeEquivalencyInformation() {
        return fullTimeEquivalencyInformation;
    }

    /**
     * Sets the value of the fullTimeEquivalencyInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link FullTimeEquivalencyInformationType }
     *     
     */
    public void setFullTimeEquivalencyInformation(FullTimeEquivalencyInformationType value) {
        this.fullTimeEquivalencyInformation = value;
    }

    /**
     * Gets the value of the malpracticeLiabilityExposureInformation property.
     * 
     * @return
     *     possible object is
     *     {@link MalpracticeLiabilityExposureInformationType }
     *     
     */
    public MalpracticeLiabilityExposureInformationType getMalpracticeLiabilityExposureInformation() {
        return malpracticeLiabilityExposureInformation;
    }

    /**
     * Sets the value of the malpracticeLiabilityExposureInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link MalpracticeLiabilityExposureInformationType }
     *     
     */
    public void setMalpracticeLiabilityExposureInformation(MalpracticeLiabilityExposureInformationType value) {
        this.malpracticeLiabilityExposureInformation = value;
    }

    /**
     * Gets the value of the buildingInformation property.
     * 
     * @return
     *     possible object is
     *     {@link BuildingInformationType }
     *     
     */
    public BuildingInformationType getBuildingInformation() {
        return buildingInformation;
    }

    /**
     * Sets the value of the buildingInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link BuildingInformationType }
     *     
     */
    public void setBuildingInformation(BuildingInformationType value) {
        this.buildingInformation = value;
    }

    /**
     * Gets the value of the vehiclesOperatedInformation property.
     * 
     * @return
     *     possible object is
     *     {@link VehiclesOperatedInformationType }
     *     
     */
    public VehiclesOperatedInformationType getVehiclesOperatedInformation() {
        return vehiclesOperatedInformation;
    }

    /**
     * Sets the value of the vehiclesOperatedInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link VehiclesOperatedInformationType }
     *     
     */
    public void setVehiclesOperatedInformation(VehiclesOperatedInformationType value) {
        this.vehiclesOperatedInformation = value;
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
     * Gets the value of the insuredVersionDetail property.
     * 
     * @return
     *     possible object is
     *     {@link InsuredVersionDetailType }
     *     
     */
    public InsuredVersionDetailType getInsuredVersionDetail() {
        return insuredVersionDetail;
    }

    /**
     * Sets the value of the insuredVersionDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link InsuredVersionDetailType }
     *     
     */
    public void setInsuredVersionDetail(InsuredVersionDetailType value) {
        this.insuredVersionDetail = value;
    }

}
