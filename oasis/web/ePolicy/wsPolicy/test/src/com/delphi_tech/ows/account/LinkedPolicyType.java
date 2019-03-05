
package com.delphi_tech.ows.account;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LinkedPolicyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LinkedPolicyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PolicyId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PolicyOriginCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="InsuredId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CoverageId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PaymentOption" type="{http://www.delphi-tech.com/ows/Account}PaymentOptionType" maxOccurs="unbounded"/>
 *         &lt;element name="PolicyTerm" type="{http://www.delphi-tech.com/ows/Account}PolicyTermType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LinkedPolicyType", propOrder = {
    "policyId",
    "policyOriginCode",
    "insuredId",
    "coverageId",
    "paymentOption",
    "policyTerm"
})
public class LinkedPolicyType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "PolicyId", required = true)
    protected String policyId;
    @XmlElement(name = "PolicyOriginCode")
    protected String policyOriginCode;
    @XmlElement(name = "InsuredId")
    protected String insuredId;
    @XmlElement(name = "CoverageId")
    protected String coverageId;
    @XmlElement(name = "PaymentOption", required = true)
    protected List<PaymentOptionType> paymentOption;
    @XmlElement(name = "PolicyTerm")
    protected List<PolicyTermType> policyTerm;

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
     * Gets the value of the policyOriginCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPolicyOriginCode() {
        return policyOriginCode;
    }

    /**
     * Sets the value of the policyOriginCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPolicyOriginCode(String value) {
        this.policyOriginCode = value;
    }

    /**
     * Gets the value of the insuredId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInsuredId() {
        return insuredId;
    }

    /**
     * Sets the value of the insuredId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInsuredId(String value) {
        this.insuredId = value;
    }

    /**
     * Gets the value of the coverageId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCoverageId() {
        return coverageId;
    }

    /**
     * Sets the value of the coverageId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCoverageId(String value) {
        this.coverageId = value;
    }

    /**
     * Gets the value of the paymentOption property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the paymentOption property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPaymentOption().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PaymentOptionType }
     * 
     * 
     */
    public List<PaymentOptionType> getPaymentOption() {
        if (paymentOption == null) {
            paymentOption = new ArrayList<PaymentOptionType>();
        }
        return this.paymentOption;
    }

    /**
     * Gets the value of the policyTerm property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the policyTerm property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPolicyTerm().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PolicyTermType }
     * 
     * 
     */
    public List<PolicyTermType> getPolicyTerm() {
        if (policyTerm == null) {
            policyTerm = new ArrayList<PolicyTermType>();
        }
        return this.policyTerm;
    }

}
