
package com.delphi_tech.ows.account;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BillingActivityInformationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BillingActivityInformationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="BillingActivityDate" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="BillingActivityId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DueDate" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ActivityDescription" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="BillingActivityAmount" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="BillingActivityOpenBalance" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BillingActivityInformationType", propOrder = {
    "billingActivityDate",
    "billingActivityId",
    "dueDate",
    "activityDescription",
    "billingActivityAmount",
    "billingActivityOpenBalance"
})
public class BillingActivityInformationType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "BillingActivityDate", required = true)
    protected String billingActivityDate;
    @XmlElement(name = "BillingActivityId", required = true)
    protected String billingActivityId;
    @XmlElement(name = "DueDate", required = true)
    protected String dueDate;
    @XmlElement(name = "ActivityDescription", required = true)
    protected String activityDescription;
    @XmlElement(name = "BillingActivityAmount", required = true)
    protected String billingActivityAmount;
    @XmlElement(name = "BillingActivityOpenBalance", required = true)
    protected String billingActivityOpenBalance;

    /**
     * Gets the value of the billingActivityDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillingActivityDate() {
        return billingActivityDate;
    }

    /**
     * Sets the value of the billingActivityDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillingActivityDate(String value) {
        this.billingActivityDate = value;
    }

    /**
     * Gets the value of the billingActivityId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillingActivityId() {
        return billingActivityId;
    }

    /**
     * Sets the value of the billingActivityId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillingActivityId(String value) {
        this.billingActivityId = value;
    }

    /**
     * Gets the value of the dueDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDueDate() {
        return dueDate;
    }

    /**
     * Sets the value of the dueDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDueDate(String value) {
        this.dueDate = value;
    }

    /**
     * Gets the value of the activityDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActivityDescription() {
        return activityDescription;
    }

    /**
     * Sets the value of the activityDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActivityDescription(String value) {
        this.activityDescription = value;
    }

    /**
     * Gets the value of the billingActivityAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillingActivityAmount() {
        return billingActivityAmount;
    }

    /**
     * Sets the value of the billingActivityAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillingActivityAmount(String value) {
        this.billingActivityAmount = value;
    }

    /**
     * Gets the value of the billingActivityOpenBalance property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillingActivityOpenBalance() {
        return billingActivityOpenBalance;
    }

    /**
     * Sets the value of the billingActivityOpenBalance property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillingActivityOpenBalance(String value) {
        this.billingActivityOpenBalance = value;
    }

}
