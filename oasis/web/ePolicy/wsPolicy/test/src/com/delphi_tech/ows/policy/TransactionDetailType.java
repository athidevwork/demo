
package com.delphi_tech.ows.policy;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TransactionDetailType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TransactionDetailType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TransactionNumberId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TransactionEffectiveDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TransactionCode" type="{http://www.delphi-tech.com/ows/Policy}TransactionCodeType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransactionDetailType", propOrder = {
    "transactionNumberId",
    "transactionEffectiveDate",
    "transactionCode"
})
public class TransactionDetailType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "TransactionNumberId")
    protected String transactionNumberId;
    @XmlElement(name = "TransactionEffectiveDate")
    protected String transactionEffectiveDate;
    @XmlElement(name = "TransactionCode")
    protected TransactionCodeType transactionCode;

    /**
     * Gets the value of the transactionNumberId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionNumberId() {
        return transactionNumberId;
    }

    /**
     * Sets the value of the transactionNumberId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionNumberId(String value) {
        this.transactionNumberId = value;
    }

    /**
     * Gets the value of the transactionEffectiveDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionEffectiveDate() {
        return transactionEffectiveDate;
    }

    /**
     * Sets the value of the transactionEffectiveDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionEffectiveDate(String value) {
        this.transactionEffectiveDate = value;
    }

    /**
     * Gets the value of the transactionCode property.
     * 
     * @return
     *     possible object is
     *     {@link TransactionCodeType }
     *     
     */
    public TransactionCodeType getTransactionCode() {
        return transactionCode;
    }

    /**
     * Sets the value of the transactionCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionCodeType }
     *     
     */
    public void setTransactionCode(TransactionCodeType value) {
        this.transactionCode = value;
    }

}
