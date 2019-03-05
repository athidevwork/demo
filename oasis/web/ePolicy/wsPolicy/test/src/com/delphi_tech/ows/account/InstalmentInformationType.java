
package com.delphi_tech.ows.account;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for InstalmentInformationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InstalmentInformationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="InstalmentNumberNumeric" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="InstalmentBillIssueDate" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="InstalmentDueDate" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="InstalmentAmount" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="InstalmentStatus" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InstalmentInformationType", propOrder = {
    "instalmentNumberNumeric",
    "instalmentBillIssueDate",
    "instalmentDueDate",
    "instalmentAmount",
    "instalmentStatus"
})
public class InstalmentInformationType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "InstalmentNumberNumeric", required = true)
    protected String instalmentNumberNumeric;
    @XmlElement(name = "InstalmentBillIssueDate", required = true)
    protected String instalmentBillIssueDate;
    @XmlElement(name = "InstalmentDueDate", required = true)
    protected String instalmentDueDate;
    @XmlElement(name = "InstalmentAmount", required = true)
    protected String instalmentAmount;
    @XmlElement(name = "InstalmentStatus", required = true)
    protected String instalmentStatus;

    /**
     * Gets the value of the instalmentNumberNumeric property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstalmentNumberNumeric() {
        return instalmentNumberNumeric;
    }

    /**
     * Sets the value of the instalmentNumberNumeric property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstalmentNumberNumeric(String value) {
        this.instalmentNumberNumeric = value;
    }

    /**
     * Gets the value of the instalmentBillIssueDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstalmentBillIssueDate() {
        return instalmentBillIssueDate;
    }

    /**
     * Sets the value of the instalmentBillIssueDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstalmentBillIssueDate(String value) {
        this.instalmentBillIssueDate = value;
    }

    /**
     * Gets the value of the instalmentDueDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstalmentDueDate() {
        return instalmentDueDate;
    }

    /**
     * Sets the value of the instalmentDueDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstalmentDueDate(String value) {
        this.instalmentDueDate = value;
    }

    /**
     * Gets the value of the instalmentAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstalmentAmount() {
        return instalmentAmount;
    }

    /**
     * Sets the value of the instalmentAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstalmentAmount(String value) {
        this.instalmentAmount = value;
    }

    /**
     * Gets the value of the instalmentStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstalmentStatus() {
        return instalmentStatus;
    }

    /**
     * Sets the value of the instalmentStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstalmentStatus(String value) {
        this.instalmentStatus = value;
    }

}
