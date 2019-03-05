
package com.delphi_tech.ows.policy;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for InsuredVersionDetailType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InsuredVersionDetailType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="VersionModeCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ParentVersionNumberId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AfterImageIndicator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InsuredVersionDetailType", propOrder = {
    "versionModeCode",
    "parentVersionNumberId",
    "afterImageIndicator"
})
public class InsuredVersionDetailType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "VersionModeCode")
    protected String versionModeCode;
    @XmlElement(name = "ParentVersionNumberId")
    protected String parentVersionNumberId;
    @XmlElement(name = "AfterImageIndicator")
    protected String afterImageIndicator;

    /**
     * Gets the value of the versionModeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersionModeCode() {
        return versionModeCode;
    }

    /**
     * Sets the value of the versionModeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersionModeCode(String value) {
        this.versionModeCode = value;
    }

    /**
     * Gets the value of the parentVersionNumberId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParentVersionNumberId() {
        return parentVersionNumberId;
    }

    /**
     * Sets the value of the parentVersionNumberId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParentVersionNumberId(String value) {
        this.parentVersionNumberId = value;
    }

    /**
     * Gets the value of the afterImageIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAfterImageIndicator() {
        return afterImageIndicator;
    }

    /**
     * Sets the value of the afterImageIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAfterImageIndicator(String value) {
        this.afterImageIndicator = value;
    }

}
