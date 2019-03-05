
package com.delphi_tech.ows.policy;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for ReferredMedicalMalpracticeCoverageType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReferredMedicalMalpracticeCoverageType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="medicalMalpracticeCoverageReference" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReferredMedicalMalpracticeCoverageType", propOrder = {
    "value"
})
public class ReferredMedicalMalpracticeCoverageType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlValue
    protected String value;
    @XmlAttribute(name = "medicalMalpracticeCoverageReference")
    protected String medicalMalpracticeCoverageReference;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the medicalMalpracticeCoverageReference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMedicalMalpracticeCoverageReference() {
        return medicalMalpracticeCoverageReference;
    }

    /**
     * Sets the value of the medicalMalpracticeCoverageReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMedicalMalpracticeCoverageReference(String value) {
        this.medicalMalpracticeCoverageReference = value;
    }

}
