
package com.delphi_tech.ows.policy;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CoverageConversionInformationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CoverageConversionInformationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ClaimsMadeDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ClaimsMadeOverrideDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OccurenceDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OccurenceOverrideDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CoverageConversionInformationType", propOrder = {
    "claimsMadeDate",
    "claimsMadeOverrideDate",
    "occurenceDate",
    "occurenceOverrideDate"
})
public class CoverageConversionInformationType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "ClaimsMadeDate")
    protected String claimsMadeDate;
    @XmlElement(name = "ClaimsMadeOverrideDate")
    protected String claimsMadeOverrideDate;
    @XmlElement(name = "OccurenceDate")
    protected String occurenceDate;
    @XmlElement(name = "OccurenceOverrideDate")
    protected String occurenceOverrideDate;

    /**
     * Gets the value of the claimsMadeDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClaimsMadeDate() {
        return claimsMadeDate;
    }

    /**
     * Sets the value of the claimsMadeDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClaimsMadeDate(String value) {
        this.claimsMadeDate = value;
    }

    /**
     * Gets the value of the claimsMadeOverrideDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClaimsMadeOverrideDate() {
        return claimsMadeOverrideDate;
    }

    /**
     * Sets the value of the claimsMadeOverrideDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClaimsMadeOverrideDate(String value) {
        this.claimsMadeOverrideDate = value;
    }

    /**
     * Gets the value of the occurenceDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOccurenceDate() {
        return occurenceDate;
    }

    /**
     * Sets the value of the occurenceDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOccurenceDate(String value) {
        this.occurenceDate = value;
    }

    /**
     * Gets the value of the occurenceOverrideDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOccurenceOverrideDate() {
        return occurenceOverrideDate;
    }

    /**
     * Sets the value of the occurenceOverrideDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOccurenceOverrideDate(String value) {
        this.occurenceOverrideDate = value;
    }

}
