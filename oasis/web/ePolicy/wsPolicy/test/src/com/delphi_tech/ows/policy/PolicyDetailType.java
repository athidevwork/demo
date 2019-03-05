
package com.delphi_tech.ows.policy;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PolicyDetailType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PolicyDetailType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PolicyTypeCode" type="{http://www.delphi-tech.com/ows/Policy}PolicyTypeCodeType" minOccurs="0"/>
 *         &lt;element name="PolicyPhaseCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BinderEndDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ClaimMadeLiabilityPolicyInformation" type="{http://www.delphi-tech.com/ows/Policy}ClaimMadeLiabilityPolicyInformationType" minOccurs="0"/>
 *         &lt;element name="ShortTermIndicator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PolicyFormCode" type="{http://www.delphi-tech.com/ows/Policy}PolicyFormCodeType" minOccurs="0"/>
 *         &lt;element name="PolicyStatusCode" type="{http://www.delphi-tech.com/ows/Policy}PolicyStatusCodeType" minOccurs="0"/>
 *         &lt;element name="PolicyCycleCode" type="{http://www.delphi-tech.com/ows/Policy}PolicyCycleCodeType" minOccurs="0"/>
 *         &lt;element name="QuoteCycleCode" type="{http://www.delphi-tech.com/ows/Policy}QuoteCycleCodeType" minOccurs="0"/>
 *         &lt;element name="PolicyLayerCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PriorPolicy" type="{http://www.delphi-tech.com/ows/Policy}PriorPolicyType" minOccurs="0"/>
 *         &lt;element name="GuaranteeDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DeclinationDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IbnrDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IssueCompany" type="{http://www.delphi-tech.com/ows/Policy}IssueCompanyType" minOccurs="0"/>
 *         &lt;element name="OrganizationType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BinderIndicator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CollateralIndicator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="InsuredByCompanyIndicator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ProgramCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CategoryCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="HospitalTier" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ClaimsMadeYear" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PeerGroupCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FirstPotentialCancelDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SecondPotentialCancelDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PlAggregatCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="GlAggregateCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AdditionalInformation" type="{http://www.delphi-tech.com/ows/Policy}AdditionalInformationType" minOccurs="0"/>
 *         &lt;element name="AdditionalRatingInformation" type="{http://www.delphi-tech.com/ows/Policy}AdditionalRatingInformationType" minOccurs="0"/>
 *         &lt;element name="ExposureInformation" type="{http://www.delphi-tech.com/ows/Policy}ExposureInformationType" minOccurs="0"/>
 *         &lt;element name="PolicyVersionDetail" type="{http://www.delphi-tech.com/ows/Policy}PolicyVersionDetailType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PolicyDetailType", propOrder = {
    "policyTypeCode",
    "policyPhaseCode",
    "binderEndDate",
    "claimMadeLiabilityPolicyInformation",
    "shortTermIndicator",
    "policyFormCode",
    "policyStatusCode",
    "policyCycleCode",
    "quoteCycleCode",
    "policyLayerCode",
    "priorPolicy",
    "guaranteeDate",
    "declinationDate",
    "ibnrDate",
    "issueCompany",
    "organizationType",
    "binderIndicator",
    "collateralIndicator",
    "insuredByCompanyIndicator",
    "programCode",
    "categoryCode",
    "hospitalTier",
    "claimsMadeYear",
    "peerGroupCode",
    "firstPotentialCancelDate",
    "secondPotentialCancelDate",
    "plAggregatCode",
    "glAggregateCode",
    "additionalInformation",
    "additionalRatingInformation",
    "exposureInformation",
    "policyVersionDetail"
})
public class PolicyDetailType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "PolicyTypeCode")
    protected PolicyTypeCodeType policyTypeCode;
    @XmlElement(name = "PolicyPhaseCode")
    protected String policyPhaseCode;
    @XmlElement(name = "BinderEndDate")
    protected String binderEndDate;
    @XmlElement(name = "ClaimMadeLiabilityPolicyInformation")
    protected ClaimMadeLiabilityPolicyInformationType claimMadeLiabilityPolicyInformation;
    @XmlElement(name = "ShortTermIndicator")
    protected String shortTermIndicator;
    @XmlElement(name = "PolicyFormCode")
    protected PolicyFormCodeType policyFormCode;
    @XmlElement(name = "PolicyStatusCode")
    protected PolicyStatusCodeType policyStatusCode;
    @XmlElement(name = "PolicyCycleCode")
    protected PolicyCycleCodeType policyCycleCode;
    @XmlElement(name = "QuoteCycleCode")
    protected QuoteCycleCodeType quoteCycleCode;
    @XmlElement(name = "PolicyLayerCode")
    protected String policyLayerCode;
    @XmlElement(name = "PriorPolicy")
    protected PriorPolicyType priorPolicy;
    @XmlElement(name = "GuaranteeDate")
    protected String guaranteeDate;
    @XmlElement(name = "DeclinationDate")
    protected String declinationDate;
    @XmlElement(name = "IbnrDate")
    protected String ibnrDate;
    @XmlElement(name = "IssueCompany")
    protected IssueCompanyType issueCompany;
    @XmlElement(name = "OrganizationType")
    protected String organizationType;
    @XmlElement(name = "BinderIndicator")
    protected String binderIndicator;
    @XmlElement(name = "CollateralIndicator")
    protected String collateralIndicator;
    @XmlElement(name = "InsuredByCompanyIndicator")
    protected String insuredByCompanyIndicator;
    @XmlElement(name = "ProgramCode")
    protected String programCode;
    @XmlElement(name = "CategoryCode")
    protected String categoryCode;
    @XmlElement(name = "HospitalTier")
    protected String hospitalTier;
    @XmlElement(name = "ClaimsMadeYear")
    protected String claimsMadeYear;
    @XmlElement(name = "PeerGroupCode")
    protected String peerGroupCode;
    @XmlElement(name = "FirstPotentialCancelDate")
    protected String firstPotentialCancelDate;
    @XmlElement(name = "SecondPotentialCancelDate")
    protected String secondPotentialCancelDate;
    @XmlElement(name = "PlAggregatCode")
    protected String plAggregatCode;
    @XmlElement(name = "GlAggregateCode")
    protected String glAggregateCode;
    @XmlElement(name = "AdditionalInformation")
    protected AdditionalInformationType additionalInformation;
    @XmlElement(name = "AdditionalRatingInformation")
    protected AdditionalRatingInformationType additionalRatingInformation;
    @XmlElement(name = "ExposureInformation")
    protected ExposureInformationType exposureInformation;
    @XmlElement(name = "PolicyVersionDetail")
    protected PolicyVersionDetailType policyVersionDetail;

    /**
     * Gets the value of the policyTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link PolicyTypeCodeType }
     *     
     */
    public PolicyTypeCodeType getPolicyTypeCode() {
        return policyTypeCode;
    }

    /**
     * Sets the value of the policyTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link PolicyTypeCodeType }
     *     
     */
    public void setPolicyTypeCode(PolicyTypeCodeType value) {
        this.policyTypeCode = value;
    }

    /**
     * Gets the value of the policyPhaseCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPolicyPhaseCode() {
        return policyPhaseCode;
    }

    /**
     * Sets the value of the policyPhaseCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPolicyPhaseCode(String value) {
        this.policyPhaseCode = value;
    }

    /**
     * Gets the value of the binderEndDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBinderEndDate() {
        return binderEndDate;
    }

    /**
     * Sets the value of the binderEndDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBinderEndDate(String value) {
        this.binderEndDate = value;
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
     * Gets the value of the shortTermIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShortTermIndicator() {
        return shortTermIndicator;
    }

    /**
     * Sets the value of the shortTermIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShortTermIndicator(String value) {
        this.shortTermIndicator = value;
    }

    /**
     * Gets the value of the policyFormCode property.
     * 
     * @return
     *     possible object is
     *     {@link PolicyFormCodeType }
     *     
     */
    public PolicyFormCodeType getPolicyFormCode() {
        return policyFormCode;
    }

    /**
     * Sets the value of the policyFormCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link PolicyFormCodeType }
     *     
     */
    public void setPolicyFormCode(PolicyFormCodeType value) {
        this.policyFormCode = value;
    }

    /**
     * Gets the value of the policyStatusCode property.
     * 
     * @return
     *     possible object is
     *     {@link PolicyStatusCodeType }
     *     
     */
    public PolicyStatusCodeType getPolicyStatusCode() {
        return policyStatusCode;
    }

    /**
     * Sets the value of the policyStatusCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link PolicyStatusCodeType }
     *     
     */
    public void setPolicyStatusCode(PolicyStatusCodeType value) {
        this.policyStatusCode = value;
    }

    /**
     * Gets the value of the policyCycleCode property.
     * 
     * @return
     *     possible object is
     *     {@link PolicyCycleCodeType }
     *     
     */
    public PolicyCycleCodeType getPolicyCycleCode() {
        return policyCycleCode;
    }

    /**
     * Sets the value of the policyCycleCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link PolicyCycleCodeType }
     *     
     */
    public void setPolicyCycleCode(PolicyCycleCodeType value) {
        this.policyCycleCode = value;
    }

    /**
     * Gets the value of the quoteCycleCode property.
     * 
     * @return
     *     possible object is
     *     {@link QuoteCycleCodeType }
     *     
     */
    public QuoteCycleCodeType getQuoteCycleCode() {
        return quoteCycleCode;
    }

    /**
     * Sets the value of the quoteCycleCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link QuoteCycleCodeType }
     *     
     */
    public void setQuoteCycleCode(QuoteCycleCodeType value) {
        this.quoteCycleCode = value;
    }

    /**
     * Gets the value of the policyLayerCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPolicyLayerCode() {
        return policyLayerCode;
    }

    /**
     * Sets the value of the policyLayerCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPolicyLayerCode(String value) {
        this.policyLayerCode = value;
    }

    /**
     * Gets the value of the priorPolicy property.
     * 
     * @return
     *     possible object is
     *     {@link PriorPolicyType }
     *     
     */
    public PriorPolicyType getPriorPolicy() {
        return priorPolicy;
    }

    /**
     * Sets the value of the priorPolicy property.
     * 
     * @param value
     *     allowed object is
     *     {@link PriorPolicyType }
     *     
     */
    public void setPriorPolicy(PriorPolicyType value) {
        this.priorPolicy = value;
    }

    /**
     * Gets the value of the guaranteeDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGuaranteeDate() {
        return guaranteeDate;
    }

    /**
     * Sets the value of the guaranteeDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGuaranteeDate(String value) {
        this.guaranteeDate = value;
    }

    /**
     * Gets the value of the declinationDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeclinationDate() {
        return declinationDate;
    }

    /**
     * Sets the value of the declinationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeclinationDate(String value) {
        this.declinationDate = value;
    }

    /**
     * Gets the value of the ibnrDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIbnrDate() {
        return ibnrDate;
    }

    /**
     * Sets the value of the ibnrDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIbnrDate(String value) {
        this.ibnrDate = value;
    }

    /**
     * Gets the value of the issueCompany property.
     * 
     * @return
     *     possible object is
     *     {@link IssueCompanyType }
     *     
     */
    public IssueCompanyType getIssueCompany() {
        return issueCompany;
    }

    /**
     * Sets the value of the issueCompany property.
     * 
     * @param value
     *     allowed object is
     *     {@link IssueCompanyType }
     *     
     */
    public void setIssueCompany(IssueCompanyType value) {
        this.issueCompany = value;
    }

    /**
     * Gets the value of the organizationType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrganizationType() {
        return organizationType;
    }

    /**
     * Sets the value of the organizationType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrganizationType(String value) {
        this.organizationType = value;
    }

    /**
     * Gets the value of the binderIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBinderIndicator() {
        return binderIndicator;
    }

    /**
     * Sets the value of the binderIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBinderIndicator(String value) {
        this.binderIndicator = value;
    }

    /**
     * Gets the value of the collateralIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCollateralIndicator() {
        return collateralIndicator;
    }

    /**
     * Sets the value of the collateralIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCollateralIndicator(String value) {
        this.collateralIndicator = value;
    }

    /**
     * Gets the value of the insuredByCompanyIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInsuredByCompanyIndicator() {
        return insuredByCompanyIndicator;
    }

    /**
     * Sets the value of the insuredByCompanyIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInsuredByCompanyIndicator(String value) {
        this.insuredByCompanyIndicator = value;
    }

    /**
     * Gets the value of the programCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProgramCode() {
        return programCode;
    }

    /**
     * Sets the value of the programCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProgramCode(String value) {
        this.programCode = value;
    }

    /**
     * Gets the value of the categoryCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCategoryCode() {
        return categoryCode;
    }

    /**
     * Sets the value of the categoryCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCategoryCode(String value) {
        this.categoryCode = value;
    }

    /**
     * Gets the value of the hospitalTier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHospitalTier() {
        return hospitalTier;
    }

    /**
     * Sets the value of the hospitalTier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHospitalTier(String value) {
        this.hospitalTier = value;
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
     * Gets the value of the peerGroupCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPeerGroupCode() {
        return peerGroupCode;
    }

    /**
     * Sets the value of the peerGroupCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPeerGroupCode(String value) {
        this.peerGroupCode = value;
    }

    /**
     * Gets the value of the firstPotentialCancelDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirstPotentialCancelDate() {
        return firstPotentialCancelDate;
    }

    /**
     * Sets the value of the firstPotentialCancelDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirstPotentialCancelDate(String value) {
        this.firstPotentialCancelDate = value;
    }

    /**
     * Gets the value of the secondPotentialCancelDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSecondPotentialCancelDate() {
        return secondPotentialCancelDate;
    }

    /**
     * Sets the value of the secondPotentialCancelDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSecondPotentialCancelDate(String value) {
        this.secondPotentialCancelDate = value;
    }

    /**
     * Gets the value of the plAggregatCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPlAggregatCode() {
        return plAggregatCode;
    }

    /**
     * Sets the value of the plAggregatCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPlAggregatCode(String value) {
        this.plAggregatCode = value;
    }

    /**
     * Gets the value of the glAggregateCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGlAggregateCode() {
        return glAggregateCode;
    }

    /**
     * Sets the value of the glAggregateCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGlAggregateCode(String value) {
        this.glAggregateCode = value;
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
     * Gets the value of the additionalRatingInformation property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalRatingInformationType }
     *     
     */
    public AdditionalRatingInformationType getAdditionalRatingInformation() {
        return additionalRatingInformation;
    }

    /**
     * Sets the value of the additionalRatingInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalRatingInformationType }
     *     
     */
    public void setAdditionalRatingInformation(AdditionalRatingInformationType value) {
        this.additionalRatingInformation = value;
    }

    /**
     * Gets the value of the exposureInformation property.
     * 
     * @return
     *     possible object is
     *     {@link ExposureInformationType }
     *     
     */
    public ExposureInformationType getExposureInformation() {
        return exposureInformation;
    }

    /**
     * Sets the value of the exposureInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExposureInformationType }
     *     
     */
    public void setExposureInformation(ExposureInformationType value) {
        this.exposureInformation = value;
    }

    /**
     * Gets the value of the policyVersionDetail property.
     * 
     * @return
     *     possible object is
     *     {@link PolicyVersionDetailType }
     *     
     */
    public PolicyVersionDetailType getPolicyVersionDetail() {
        return policyVersionDetail;
    }

    /**
     * Sets the value of the policyVersionDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link PolicyVersionDetailType }
     *     
     */
    public void setPolicyVersionDetail(PolicyVersionDetailType value) {
        this.policyVersionDetail = value;
    }

}
