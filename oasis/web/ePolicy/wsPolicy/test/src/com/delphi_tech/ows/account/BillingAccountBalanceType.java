
package com.delphi_tech.ows.account;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BillingAccountBalanceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BillingAccountBalanceType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CurrentBalanceAmount" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="FutureBalanceAmount" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TotalBalanceAmount" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BillingAccountBalanceType", propOrder = {
    "currentBalanceAmount",
    "futureBalanceAmount",
    "totalBalanceAmount"
})
public class BillingAccountBalanceType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "CurrentBalanceAmount", required = true)
    protected String currentBalanceAmount;
    @XmlElement(name = "FutureBalanceAmount", required = true)
    protected String futureBalanceAmount;
    @XmlElement(name = "TotalBalanceAmount", required = true)
    protected String totalBalanceAmount;

    /**
     * Gets the value of the currentBalanceAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCurrentBalanceAmount() {
        return currentBalanceAmount;
    }

    /**
     * Sets the value of the currentBalanceAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCurrentBalanceAmount(String value) {
        this.currentBalanceAmount = value;
    }

    /**
     * Gets the value of the futureBalanceAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFutureBalanceAmount() {
        return futureBalanceAmount;
    }

    /**
     * Sets the value of the futureBalanceAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFutureBalanceAmount(String value) {
        this.futureBalanceAmount = value;
    }

    /**
     * Gets the value of the totalBalanceAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalBalanceAmount() {
        return totalBalanceAmount;
    }

    /**
     * Sets the value of the totalBalanceAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalBalanceAmount(String value) {
        this.totalBalanceAmount = value;
    }

}
