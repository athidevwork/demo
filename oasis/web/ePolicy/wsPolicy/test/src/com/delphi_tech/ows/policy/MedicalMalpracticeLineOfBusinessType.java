
package com.delphi_tech.ows.policy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MedicalMalpracticeLineOfBusinessType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MedicalMalpracticeLineOfBusinessType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MedicalMalpracticeCoverage" type="{http://www.delphi-tech.com/ows/Policy}MedicalMalpracticeCoverageType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MedicalMalpracticeLineOfBusinessType", propOrder = {
    "medicalMalpracticeCoverage"
})
public class MedicalMalpracticeLineOfBusinessType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "MedicalMalpracticeCoverage")
    protected List<MedicalMalpracticeCoverageType> medicalMalpracticeCoverage;

    /**
     * Gets the value of the medicalMalpracticeCoverage property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the medicalMalpracticeCoverage property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMedicalMalpracticeCoverage().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MedicalMalpracticeCoverageType }
     * 
     * 
     */
    public List<MedicalMalpracticeCoverageType> getMedicalMalpracticeCoverage() {
        if (medicalMalpracticeCoverage == null) {
            medicalMalpracticeCoverage = new ArrayList<MedicalMalpracticeCoverageType>();
        }
        return this.medicalMalpracticeCoverage;
    }

}
