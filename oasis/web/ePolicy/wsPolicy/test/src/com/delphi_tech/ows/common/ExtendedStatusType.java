
package com.delphi_tech.ows.common;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ExtendedStatusType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExtendedStatusType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ExtendedStatusCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CustomStatusCode" type="{http://www.delphi-tech.com/ows/Common}CustomStatusCodeType" minOccurs="0"/>
 *         &lt;element name="ExtendedStatusDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExtendedStatusType", propOrder = {
    "extendedStatusCode",
    "customStatusCode",
    "extendedStatusDescription"
})
public class ExtendedStatusType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "ExtendedStatusCode")
    protected String extendedStatusCode;
    @XmlElement(name = "CustomStatusCode")
    protected CustomStatusCodeType customStatusCode;
    @XmlElement(name = "ExtendedStatusDescription")
    protected String extendedStatusDescription;

    /**
     * Gets the value of the extendedStatusCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtendedStatusCode() {
        return extendedStatusCode;
    }

    /**
     * Sets the value of the extendedStatusCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtendedStatusCode(String value) {
        this.extendedStatusCode = value;
    }

    /**
     * Gets the value of the customStatusCode property.
     * 
     * @return
     *     possible object is
     *     {@link CustomStatusCodeType }
     *     
     */
    public CustomStatusCodeType getCustomStatusCode() {
        return customStatusCode;
    }

    /**
     * Sets the value of the customStatusCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomStatusCodeType }
     *     
     */
    public void setCustomStatusCode(CustomStatusCodeType value) {
        this.customStatusCode = value;
    }

    /**
     * Gets the value of the extendedStatusDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtendedStatusDescription() {
        return extendedStatusDescription;
    }

    /**
     * Sets the value of the extendedStatusDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtendedStatusDescription(String value) {
        this.extendedStatusDescription = value;
    }

}
