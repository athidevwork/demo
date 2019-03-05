
package com.delphi_tech.ows.party;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SuspensionReinstatementInformationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SuspensionReinstatementInformationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="LicenseSuspensionPeriod" type="{http://www.delphi-tech.com/ows/Party}LicenseSuspensionPeriodType" minOccurs="0"/>
 *         &lt;element name="SuspensionRevocationReasonCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SuspensionReinstatementInformationType", propOrder = {
    "licenseSuspensionPeriod",
    "suspensionRevocationReasonCode"
})
public class SuspensionReinstatementInformationType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "LicenseSuspensionPeriod")
    protected LicenseSuspensionPeriodType licenseSuspensionPeriod;
    @XmlElement(name = "SuspensionRevocationReasonCode")
    protected String suspensionRevocationReasonCode;

    /**
     * Gets the value of the licenseSuspensionPeriod property.
     * 
     * @return
     *     possible object is
     *     {@link LicenseSuspensionPeriodType }
     *     
     */
    public LicenseSuspensionPeriodType getLicenseSuspensionPeriod() {
        return licenseSuspensionPeriod;
    }

    /**
     * Sets the value of the licenseSuspensionPeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link LicenseSuspensionPeriodType }
     *     
     */
    public void setLicenseSuspensionPeriod(LicenseSuspensionPeriodType value) {
        this.licenseSuspensionPeriod = value;
    }

    /**
     * Gets the value of the suspensionRevocationReasonCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSuspensionRevocationReasonCode() {
        return suspensionRevocationReasonCode;
    }

    /**
     * Sets the value of the suspensionRevocationReasonCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSuspensionRevocationReasonCode(String value) {
        this.suspensionRevocationReasonCode = value;
    }

}
