
package com.delphi_tech.ows.policyinquiryservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PolicyInquiryRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PolicyInquiryRequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MessageId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CorrelationId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="UserId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PolicyInquiryRequestParameters" type="{http://www.delphi-tech.com/ows/PolicyInquiryService}PolicyInquiryRequestParametersType" maxOccurs="unbounded"/>
 *         &lt;element name="PolicyInquiryResultParameters" type="{http://www.delphi-tech.com/ows/PolicyInquiryService}PolicyInquiryResultParametersType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PolicyInquiryRequestType", propOrder = {
    "messageId",
    "correlationId",
    "userId",
    "policyInquiryRequestParameters",
    "policyInquiryResultParameters"
})
public class PolicyInquiryRequestType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "MessageId")
    protected String messageId;
    @XmlElement(name = "CorrelationId")
    protected String correlationId;
    @XmlElement(name = "UserId", required = true)
    protected String userId;
    @XmlElement(name = "PolicyInquiryRequestParameters", required = true)
    protected List<PolicyInquiryRequestParametersType> policyInquiryRequestParameters;
    @XmlElement(name = "PolicyInquiryResultParameters")
    protected PolicyInquiryResultParametersType policyInquiryResultParameters;

    /**
     * Gets the value of the messageId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Sets the value of the messageId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessageId(String value) {
        this.messageId = value;
    }

    /**
     * Gets the value of the correlationId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCorrelationId() {
        return correlationId;
    }

    /**
     * Sets the value of the correlationId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCorrelationId(String value) {
        this.correlationId = value;
    }

    /**
     * Gets the value of the userId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the value of the userId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserId(String value) {
        this.userId = value;
    }

    /**
     * Gets the value of the policyInquiryRequestParameters property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the policyInquiryRequestParameters property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPolicyInquiryRequestParameters().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PolicyInquiryRequestParametersType }
     * 
     * 
     */
    public List<PolicyInquiryRequestParametersType> getPolicyInquiryRequestParameters() {
        if (policyInquiryRequestParameters == null) {
            policyInquiryRequestParameters = new ArrayList<PolicyInquiryRequestParametersType>();
        }
        return this.policyInquiryRequestParameters;
    }

    /**
     * Gets the value of the policyInquiryResultParameters property.
     * 
     * @return
     *     possible object is
     *     {@link PolicyInquiryResultParametersType }
     *     
     */
    public PolicyInquiryResultParametersType getPolicyInquiryResultParameters() {
        return policyInquiryResultParameters;
    }

    /**
     * Sets the value of the policyInquiryResultParameters property.
     * 
     * @param value
     *     allowed object is
     *     {@link PolicyInquiryResultParametersType }
     *     
     */
    public void setPolicyInquiryResultParameters(PolicyInquiryResultParametersType value) {
        this.policyInquiryResultParameters = value;
    }

}
