
package com.delphi_tech.ows.account;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PolicyHolderType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PolicyHolderType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ReferredParty" type="{http://www.delphi-tech.com/ows/Account}ReferredPartyType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PolicyHolderType", propOrder = {
    "referredParty"
})
public class PolicyHolderType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "ReferredParty", required = true)
    protected ReferredPartyType referredParty;

    /**
     * Gets the value of the referredParty property.
     * 
     * @return
     *     possible object is
     *     {@link ReferredPartyType }
     *     
     */
    public ReferredPartyType getReferredParty() {
        return referredParty;
    }

    /**
     * Sets the value of the referredParty property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferredPartyType }
     *     
     */
    public void setReferredParty(ReferredPartyType value) {
        this.referredParty = value;
    }

}
