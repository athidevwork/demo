
package com.delphi_tech.ows.policyinquiryservice;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for InsuredType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InsuredType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="InsuredNumberId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InsuredType", propOrder = {
    "insuredNumberId"
})
public class InsuredType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "InsuredNumberId")
    protected String insuredNumberId;

    /**
     * Gets the value of the insuredNumberId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInsuredNumberId() {
        return insuredNumberId;
    }

    /**
     * Sets the value of the insuredNumberId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInsuredNumberId(String value) {
        this.insuredNumberId = value;
    }

}
