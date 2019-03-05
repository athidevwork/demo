
package com.delphi_tech.ows.account;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PaymentOptionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PaymentOptionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PaymentPlanId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="EffectivePeriod" type="{http://www.delphi-tech.com/ows/Account}EffectivePeriodType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentOptionType", propOrder = {
    "paymentPlanId",
    "effectivePeriod"
})
public class PaymentOptionType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "PaymentPlanId", required = true)
    protected String paymentPlanId;
    @XmlElement(name = "EffectivePeriod", required = true)
    protected EffectivePeriodType effectivePeriod;

    /**
     * Gets the value of the paymentPlanId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentPlanId() {
        return paymentPlanId;
    }

    /**
     * Sets the value of the paymentPlanId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentPlanId(String value) {
        this.paymentPlanId = value;
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

}
