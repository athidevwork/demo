
package com.delphi_tech.ows.policy;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FullTimeEquivalencyInformationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FullTimeEquivalencyInformationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FullTimeEquivalency" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FullTimeHours" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PartTimeHours" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PerDiemHours" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FullTimeEquivalencyInformationType", propOrder = {
    "fullTimeEquivalency",
    "fullTimeHours",
    "partTimeHours",
    "perDiemHours"
})
public class FullTimeEquivalencyInformationType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "FullTimeEquivalency")
    protected String fullTimeEquivalency;
    @XmlElement(name = "FullTimeHours")
    protected String fullTimeHours;
    @XmlElement(name = "PartTimeHours")
    protected String partTimeHours;
    @XmlElement(name = "PerDiemHours")
    protected String perDiemHours;

    /**
     * Gets the value of the fullTimeEquivalency property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFullTimeEquivalency() {
        return fullTimeEquivalency;
    }

    /**
     * Sets the value of the fullTimeEquivalency property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFullTimeEquivalency(String value) {
        this.fullTimeEquivalency = value;
    }

    /**
     * Gets the value of the fullTimeHours property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFullTimeHours() {
        return fullTimeHours;
    }

    /**
     * Sets the value of the fullTimeHours property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFullTimeHours(String value) {
        this.fullTimeHours = value;
    }

    /**
     * Gets the value of the partTimeHours property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPartTimeHours() {
        return partTimeHours;
    }

    /**
     * Sets the value of the partTimeHours property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPartTimeHours(String value) {
        this.partTimeHours = value;
    }

    /**
     * Gets the value of the perDiemHours property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPerDiemHours() {
        return perDiemHours;
    }

    /**
     * Sets the value of the perDiemHours property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPerDiemHours(String value) {
        this.perDiemHours = value;
    }

}
