
package com.delphi_tech.ows.account;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PrincipalBillingAccountInformationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PrincipalBillingAccountInformationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="BillingAccountId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="BillingAccountNumberId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BillingAccountDetail" type="{http://www.delphi-tech.com/ows/Account}BillingAccountDetailType"/>
 *         &lt;element name="BillingAccountBalance" type="{http://www.delphi-tech.com/ows/Account}BillingAccountBalanceType"/>
 *         &lt;element name="AccountHolder" type="{http://www.delphi-tech.com/ows/Account}AccountHolderType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="LinkedPolicy" type="{http://www.delphi-tech.com/ows/Account}LinkedPolicyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="BillingActivityInformation" type="{http://www.delphi-tech.com/ows/Account}BillingActivityInformationType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="TransactionDetail" type="{http://www.delphi-tech.com/ows/Account}TransactionDetailType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PrincipalBillingAccountInformationType", propOrder = {
    "billingAccountId",
    "billingAccountNumberId",
    "billingAccountDetail",
    "billingAccountBalance",
    "accountHolder",
    "linkedPolicy",
    "billingActivityInformation",
    "transactionDetail"
})
public class PrincipalBillingAccountInformationType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "BillingAccountId", required = true)
    protected String billingAccountId;
    @XmlElement(name = "BillingAccountNumberId")
    protected String billingAccountNumberId;
    @XmlElement(name = "BillingAccountDetail", required = true)
    protected BillingAccountDetailType billingAccountDetail;
    @XmlElement(name = "BillingAccountBalance", required = true)
    protected BillingAccountBalanceType billingAccountBalance;
    @XmlElement(name = "AccountHolder")
    protected List<AccountHolderType> accountHolder;
    @XmlElement(name = "LinkedPolicy")
    protected List<LinkedPolicyType> linkedPolicy;
    @XmlElement(name = "BillingActivityInformation")
    protected List<BillingActivityInformationType> billingActivityInformation;
    @XmlElement(name = "TransactionDetail")
    protected List<TransactionDetailType> transactionDetail;

    /**
     * Gets the value of the billingAccountId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillingAccountId() {
        return billingAccountId;
    }

    /**
     * Sets the value of the billingAccountId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillingAccountId(String value) {
        this.billingAccountId = value;
    }

    /**
     * Gets the value of the billingAccountNumberId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillingAccountNumberId() {
        return billingAccountNumberId;
    }

    /**
     * Sets the value of the billingAccountNumberId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillingAccountNumberId(String value) {
        this.billingAccountNumberId = value;
    }

    /**
     * Gets the value of the billingAccountDetail property.
     * 
     * @return
     *     possible object is
     *     {@link BillingAccountDetailType }
     *     
     */
    public BillingAccountDetailType getBillingAccountDetail() {
        return billingAccountDetail;
    }

    /**
     * Sets the value of the billingAccountDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link BillingAccountDetailType }
     *     
     */
    public void setBillingAccountDetail(BillingAccountDetailType value) {
        this.billingAccountDetail = value;
    }

    /**
     * Gets the value of the billingAccountBalance property.
     * 
     * @return
     *     possible object is
     *     {@link BillingAccountBalanceType }
     *     
     */
    public BillingAccountBalanceType getBillingAccountBalance() {
        return billingAccountBalance;
    }

    /**
     * Sets the value of the billingAccountBalance property.
     * 
     * @param value
     *     allowed object is
     *     {@link BillingAccountBalanceType }
     *     
     */
    public void setBillingAccountBalance(BillingAccountBalanceType value) {
        this.billingAccountBalance = value;
    }

    /**
     * Gets the value of the accountHolder property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the accountHolder property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAccountHolder().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AccountHolderType }
     * 
     * 
     */
    public List<AccountHolderType> getAccountHolder() {
        if (accountHolder == null) {
            accountHolder = new ArrayList<AccountHolderType>();
        }
        return this.accountHolder;
    }

    /**
     * Gets the value of the linkedPolicy property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the linkedPolicy property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLinkedPolicy().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LinkedPolicyType }
     * 
     * 
     */
    public List<LinkedPolicyType> getLinkedPolicy() {
        if (linkedPolicy == null) {
            linkedPolicy = new ArrayList<LinkedPolicyType>();
        }
        return this.linkedPolicy;
    }

    /**
     * Gets the value of the billingActivityInformation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the billingActivityInformation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBillingActivityInformation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BillingActivityInformationType }
     * 
     * 
     */
    public List<BillingActivityInformationType> getBillingActivityInformation() {
        if (billingActivityInformation == null) {
            billingActivityInformation = new ArrayList<BillingActivityInformationType>();
        }
        return this.billingActivityInformation;
    }

    /**
     * Gets the value of the transactionDetail property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the transactionDetail property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransactionDetail().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TransactionDetailType }
     * 
     * 
     */
    public List<TransactionDetailType> getTransactionDetail() {
        if (transactionDetail == null) {
            transactionDetail = new ArrayList<TransactionDetailType>();
        }
        return this.transactionDetail;
    }

}
