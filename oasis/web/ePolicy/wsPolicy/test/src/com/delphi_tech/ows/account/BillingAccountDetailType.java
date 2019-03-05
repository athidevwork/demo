
package com.delphi_tech.ows.account;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BillingAccountDetailType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BillingAccountDetailType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="BillingAccountType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="BillingAccountingMode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BaseMonthDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BillLeadDays" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BillFrequencyCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="NextBillDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BillOverdueRuleCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IndividualAccountIndicator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IssueCompany" type="{http://www.delphi-tech.com/ows/Account}IssueCompanyType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BillingAccountDetailType", propOrder = {
    "billingAccountType",
    "billingAccountingMode",
    "baseMonthDate",
    "billLeadDays",
    "billFrequencyCode",
    "nextBillDate",
    "billOverdueRuleCode",
    "individualAccountIndicator",
    "issueCompany"
})
public class BillingAccountDetailType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "BillingAccountType", required = true)
    protected String billingAccountType;
    @XmlElement(name = "BillingAccountingMode")
    protected String billingAccountingMode;
    @XmlElement(name = "BaseMonthDate")
    protected String baseMonthDate;
    @XmlElement(name = "BillLeadDays")
    protected String billLeadDays;
    @XmlElement(name = "BillFrequencyCode")
    protected String billFrequencyCode;
    @XmlElement(name = "NextBillDate")
    protected String nextBillDate;
    @XmlElement(name = "BillOverdueRuleCode")
    protected String billOverdueRuleCode;
    @XmlElement(name = "IndividualAccountIndicator")
    protected String individualAccountIndicator;
    @XmlElement(name = "IssueCompany")
    protected IssueCompanyType issueCompany;

    /**
     * Gets the value of the billingAccountType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillingAccountType() {
        return billingAccountType;
    }

    /**
     * Sets the value of the billingAccountType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillingAccountType(String value) {
        this.billingAccountType = value;
    }

    /**
     * Gets the value of the billingAccountingMode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillingAccountingMode() {
        return billingAccountingMode;
    }

    /**
     * Sets the value of the billingAccountingMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillingAccountingMode(String value) {
        this.billingAccountingMode = value;
    }

    /**
     * Gets the value of the baseMonthDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseMonthDate() {
        return baseMonthDate;
    }

    /**
     * Sets the value of the baseMonthDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseMonthDate(String value) {
        this.baseMonthDate = value;
    }

    /**
     * Gets the value of the billLeadDays property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillLeadDays() {
        return billLeadDays;
    }

    /**
     * Sets the value of the billLeadDays property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillLeadDays(String value) {
        this.billLeadDays = value;
    }

    /**
     * Gets the value of the billFrequencyCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillFrequencyCode() {
        return billFrequencyCode;
    }

    /**
     * Sets the value of the billFrequencyCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillFrequencyCode(String value) {
        this.billFrequencyCode = value;
    }

    /**
     * Gets the value of the nextBillDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNextBillDate() {
        return nextBillDate;
    }

    /**
     * Sets the value of the nextBillDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNextBillDate(String value) {
        this.nextBillDate = value;
    }

    /**
     * Gets the value of the billOverdueRuleCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillOverdueRuleCode() {
        return billOverdueRuleCode;
    }

    /**
     * Sets the value of the billOverdueRuleCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillOverdueRuleCode(String value) {
        this.billOverdueRuleCode = value;
    }

    /**
     * Gets the value of the individualAccountIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIndividualAccountIndicator() {
        return individualAccountIndicator;
    }

    /**
     * Sets the value of the individualAccountIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIndividualAccountIndicator(String value) {
        this.individualAccountIndicator = value;
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

}
