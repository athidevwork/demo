
package com.delphi_tech.ows.policy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.delphi_tech.ows.account.PrincipalBillingAccountInformationType;


/**
 * <p>Java class for MedicalMalpracticePolicyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MedicalMalpracticePolicyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="originalPolicyId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="originalPolicyCycleCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PolicyId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PolicyNumberId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PolicyTermNumberId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CurrentTermAmount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PrintName" type="{http://www.delphi-tech.com/ows/Policy}PrintNameType" minOccurs="0"/>
 *         &lt;element name="ContractPeriod" type="{http://www.delphi-tech.com/ows/Policy}ContractPeriodType" minOccurs="0"/>
 *         &lt;element name="TransactionDetail" type="{http://www.delphi-tech.com/ows/Policy}TransactionDetailType" minOccurs="0"/>
 *         &lt;element name="RenewalDetail" type="{http://www.delphi-tech.com/ows/Policy}RenewalDetailType" minOccurs="0"/>
 *         &lt;element name="PolicyHolder" type="{http://www.delphi-tech.com/ows/Policy}PolicyHolderType" minOccurs="0"/>
 *         &lt;element name="Insurer" type="{http://www.delphi-tech.com/ows/Policy}InsurerType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Producer" type="{http://www.delphi-tech.com/ows/Policy}ProducerType" minOccurs="0"/>
 *         &lt;element name="Underwriter" type="{http://www.delphi-tech.com/ows/Policy}UnderwriterType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="PrincipalBillingAccountInformation" type="{http://www.delphi-tech.com/ows/Account}PrincipalBillingAccountInformationType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="PolicyDetail" type="{http://www.delphi-tech.com/ows/Policy}PolicyDetailType" minOccurs="0"/>
 *         &lt;element name="Insured" type="{http://www.delphi-tech.com/ows/Policy}InsuredType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="MedicalMalpracticeLineOfBusiness" type="{http://www.delphi-tech.com/ows/Policy}MedicalMalpracticeLineOfBusinessType" minOccurs="0"/>
 *         &lt;element name="CreditSurchargeDeductible" type="{http://www.delphi-tech.com/ows/Policy}CreditSurchargeDeductibleType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MedicalMalpracticePolicyType", propOrder = {
    "originalPolicyId",
    "originalPolicyCycleCode",
    "policyId",
    "policyNumberId",
    "policyTermNumberId",
    "currentTermAmount",
    "printName",
    "contractPeriod",
    "transactionDetail",
    "renewalDetail",
    "policyHolder",
    "insurer",
    "producer",
    "underwriter",
    "principalBillingAccountInformation",
    "policyDetail",
    "insured",
    "medicalMalpracticeLineOfBusiness",
    "creditSurchargeDeductible"
})
public class MedicalMalpracticePolicyType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    protected String originalPolicyId;
    protected String originalPolicyCycleCode;
    @XmlElement(name = "PolicyId")
    protected String policyId;
    @XmlElement(name = "PolicyNumberId")
    protected String policyNumberId;
    @XmlElement(name = "PolicyTermNumberId")
    protected String policyTermNumberId;
    @XmlElement(name = "CurrentTermAmount")
    protected String currentTermAmount;
    @XmlElement(name = "PrintName")
    protected PrintNameType printName;
    @XmlElement(name = "ContractPeriod")
    protected ContractPeriodType contractPeriod;
    @XmlElement(name = "TransactionDetail")
    protected TransactionDetailType transactionDetail;
    @XmlElement(name = "RenewalDetail")
    protected RenewalDetailType renewalDetail;
    @XmlElement(name = "PolicyHolder")
    protected PolicyHolderType policyHolder;
    @XmlElement(name = "Insurer")
    protected List<InsurerType> insurer;
    @XmlElement(name = "Producer")
    protected ProducerType producer;
    @XmlElement(name = "Underwriter")
    protected List<UnderwriterType> underwriter;
    @XmlElement(name = "PrincipalBillingAccountInformation")
    protected List<PrincipalBillingAccountInformationType> principalBillingAccountInformation;
    @XmlElement(name = "PolicyDetail")
    protected PolicyDetailType policyDetail;
    @XmlElement(name = "Insured")
    protected List<InsuredType> insured;
    @XmlElement(name = "MedicalMalpracticeLineOfBusiness")
    protected MedicalMalpracticeLineOfBusinessType medicalMalpracticeLineOfBusiness;
    @XmlElement(name = "CreditSurchargeDeductible")
    protected List<CreditSurchargeDeductibleType> creditSurchargeDeductible;

    /**
     * Gets the value of the originalPolicyId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOriginalPolicyId() {
        return originalPolicyId;
    }

    /**
     * Sets the value of the originalPolicyId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOriginalPolicyId(String value) {
        this.originalPolicyId = value;
    }

    /**
     * Gets the value of the originalPolicyCycleCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOriginalPolicyCycleCode() {
        return originalPolicyCycleCode;
    }

    /**
     * Sets the value of the originalPolicyCycleCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOriginalPolicyCycleCode(String value) {
        this.originalPolicyCycleCode = value;
    }

    /**
     * Gets the value of the policyId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPolicyId() {
        return policyId;
    }

    /**
     * Sets the value of the policyId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPolicyId(String value) {
        this.policyId = value;
    }

    /**
     * Gets the value of the policyNumberId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPolicyNumberId() {
        return policyNumberId;
    }

    /**
     * Sets the value of the policyNumberId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPolicyNumberId(String value) {
        this.policyNumberId = value;
    }

    /**
     * Gets the value of the policyTermNumberId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPolicyTermNumberId() {
        return policyTermNumberId;
    }

    /**
     * Sets the value of the policyTermNumberId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPolicyTermNumberId(String value) {
        this.policyTermNumberId = value;
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
     * Gets the value of the printName property.
     * 
     * @return
     *     possible object is
     *     {@link PrintNameType }
     *     
     */
    public PrintNameType getPrintName() {
        return printName;
    }

    /**
     * Sets the value of the printName property.
     * 
     * @param value
     *     allowed object is
     *     {@link PrintNameType }
     *     
     */
    public void setPrintName(PrintNameType value) {
        this.printName = value;
    }

    /**
     * Gets the value of the contractPeriod property.
     * 
     * @return
     *     possible object is
     *     {@link ContractPeriodType }
     *     
     */
    public ContractPeriodType getContractPeriod() {
        return contractPeriod;
    }

    /**
     * Sets the value of the contractPeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContractPeriodType }
     *     
     */
    public void setContractPeriod(ContractPeriodType value) {
        this.contractPeriod = value;
    }

    /**
     * Gets the value of the transactionDetail property.
     * 
     * @return
     *     possible object is
     *     {@link TransactionDetailType }
     *     
     */
    public TransactionDetailType getTransactionDetail() {
        return transactionDetail;
    }

    /**
     * Sets the value of the transactionDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionDetailType }
     *     
     */
    public void setTransactionDetail(TransactionDetailType value) {
        this.transactionDetail = value;
    }

    /**
     * Gets the value of the renewalDetail property.
     * 
     * @return
     *     possible object is
     *     {@link RenewalDetailType }
     *     
     */
    public RenewalDetailType getRenewalDetail() {
        return renewalDetail;
    }

    /**
     * Sets the value of the renewalDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link RenewalDetailType }
     *     
     */
    public void setRenewalDetail(RenewalDetailType value) {
        this.renewalDetail = value;
    }

    /**
     * Gets the value of the policyHolder property.
     * 
     * @return
     *     possible object is
     *     {@link PolicyHolderType }
     *     
     */
    public PolicyHolderType getPolicyHolder() {
        return policyHolder;
    }

    /**
     * Sets the value of the policyHolder property.
     * 
     * @param value
     *     allowed object is
     *     {@link PolicyHolderType }
     *     
     */
    public void setPolicyHolder(PolicyHolderType value) {
        this.policyHolder = value;
    }

    /**
     * Gets the value of the insurer property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the insurer property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInsurer().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link InsurerType }
     * 
     * 
     */
    public List<InsurerType> getInsurer() {
        if (insurer == null) {
            insurer = new ArrayList<InsurerType>();
        }
        return this.insurer;
    }

    /**
     * Gets the value of the producer property.
     * 
     * @return
     *     possible object is
     *     {@link ProducerType }
     *     
     */
    public ProducerType getProducer() {
        return producer;
    }

    /**
     * Sets the value of the producer property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProducerType }
     *     
     */
    public void setProducer(ProducerType value) {
        this.producer = value;
    }

    /**
     * Gets the value of the underwriter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the underwriter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUnderwriter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UnderwriterType }
     * 
     * 
     */
    public List<UnderwriterType> getUnderwriter() {
        if (underwriter == null) {
            underwriter = new ArrayList<UnderwriterType>();
        }
        return this.underwriter;
    }

    /**
     * Gets the value of the principalBillingAccountInformation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the principalBillingAccountInformation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPrincipalBillingAccountInformation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PrincipalBillingAccountInformationType }
     * 
     * 
     */
    public List<PrincipalBillingAccountInformationType> getPrincipalBillingAccountInformation() {
        if (principalBillingAccountInformation == null) {
            principalBillingAccountInformation = new ArrayList<PrincipalBillingAccountInformationType>();
        }
        return this.principalBillingAccountInformation;
    }

    /**
     * Gets the value of the policyDetail property.
     * 
     * @return
     *     possible object is
     *     {@link PolicyDetailType }
     *     
     */
    public PolicyDetailType getPolicyDetail() {
        return policyDetail;
    }

    /**
     * Sets the value of the policyDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link PolicyDetailType }
     *     
     */
    public void setPolicyDetail(PolicyDetailType value) {
        this.policyDetail = value;
    }

    /**
     * Gets the value of the insured property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the insured property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInsured().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link InsuredType }
     * 
     * 
     */
    public List<InsuredType> getInsured() {
        if (insured == null) {
            insured = new ArrayList<InsuredType>();
        }
        return this.insured;
    }

    /**
     * Gets the value of the medicalMalpracticeLineOfBusiness property.
     * 
     * @return
     *     possible object is
     *     {@link MedicalMalpracticeLineOfBusinessType }
     *     
     */
    public MedicalMalpracticeLineOfBusinessType getMedicalMalpracticeLineOfBusiness() {
        return medicalMalpracticeLineOfBusiness;
    }

    /**
     * Sets the value of the medicalMalpracticeLineOfBusiness property.
     * 
     * @param value
     *     allowed object is
     *     {@link MedicalMalpracticeLineOfBusinessType }
     *     
     */
    public void setMedicalMalpracticeLineOfBusiness(MedicalMalpracticeLineOfBusinessType value) {
        this.medicalMalpracticeLineOfBusiness = value;
    }

    /**
     * Gets the value of the creditSurchargeDeductible property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the creditSurchargeDeductible property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCreditSurchargeDeductible().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CreditSurchargeDeductibleType }
     * 
     * 
     */
    public List<CreditSurchargeDeductibleType> getCreditSurchargeDeductible() {
        if (creditSurchargeDeductible == null) {
            creditSurchargeDeductible = new ArrayList<CreditSurchargeDeductibleType>();
        }
        return this.creditSurchargeDeductible;
    }

}
