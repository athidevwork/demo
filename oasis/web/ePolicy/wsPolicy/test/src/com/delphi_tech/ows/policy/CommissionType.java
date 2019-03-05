
package com.delphi_tech.ows.policy;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CommissionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CommissionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CommissionRatePercent" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CommissionAmount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LimitChargeAmount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CommissionBasisCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RateScheduleCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CommissionPayCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CommissionTypeCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CommissionType", propOrder = {
    "commissionRatePercent",
    "commissionAmount",
    "limitChargeAmount",
    "commissionBasisCode",
    "rateScheduleCode",
    "commissionPayCode",
    "commissionTypeCode"
})
public class CommissionType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "CommissionRatePercent")
    protected String commissionRatePercent;
    @XmlElement(name = "CommissionAmount")
    protected String commissionAmount;
    @XmlElement(name = "LimitChargeAmount")
    protected String limitChargeAmount;
    @XmlElement(name = "CommissionBasisCode")
    protected String commissionBasisCode;
    @XmlElement(name = "RateScheduleCode")
    protected String rateScheduleCode;
    @XmlElement(name = "CommissionPayCode")
    protected String commissionPayCode;
    @XmlElement(name = "CommissionTypeCode")
    protected String commissionTypeCode;

    /**
     * Gets the value of the commissionRatePercent property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCommissionRatePercent() {
        return commissionRatePercent;
    }

    /**
     * Sets the value of the commissionRatePercent property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCommissionRatePercent(String value) {
        this.commissionRatePercent = value;
    }

    /**
     * Gets the value of the commissionAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCommissionAmount() {
        return commissionAmount;
    }

    /**
     * Sets the value of the commissionAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCommissionAmount(String value) {
        this.commissionAmount = value;
    }

    /**
     * Gets the value of the limitChargeAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLimitChargeAmount() {
        return limitChargeAmount;
    }

    /**
     * Sets the value of the limitChargeAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLimitChargeAmount(String value) {
        this.limitChargeAmount = value;
    }

    /**
     * Gets the value of the commissionBasisCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCommissionBasisCode() {
        return commissionBasisCode;
    }

    /**
     * Sets the value of the commissionBasisCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCommissionBasisCode(String value) {
        this.commissionBasisCode = value;
    }

    /**
     * Gets the value of the rateScheduleCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRateScheduleCode() {
        return rateScheduleCode;
    }

    /**
     * Sets the value of the rateScheduleCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRateScheduleCode(String value) {
        this.rateScheduleCode = value;
    }

    /**
     * Gets the value of the commissionPayCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCommissionPayCode() {
        return commissionPayCode;
    }

    /**
     * Sets the value of the commissionPayCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCommissionPayCode(String value) {
        this.commissionPayCode = value;
    }

    /**
     * Gets the value of the commissionTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCommissionTypeCode() {
        return commissionTypeCode;
    }

    /**
     * Sets the value of the commissionTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCommissionTypeCode(String value) {
        this.commissionTypeCode = value;
    }

}
