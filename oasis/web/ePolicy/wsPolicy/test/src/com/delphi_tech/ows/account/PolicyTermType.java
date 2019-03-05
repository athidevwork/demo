
package com.delphi_tech.ows.account;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PolicyTermType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PolicyTermType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PolicyTermNumberId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ContractPeriod" type="{http://www.delphi-tech.com/ows/Account}ContractPeriodType"/>
 *         &lt;element name="InstalmentInformation" type="{http://www.delphi-tech.com/ows/Account}InstalmentInformationType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PolicyTermType", propOrder = {
    "policyTermNumberId",
    "contractPeriod",
    "instalmentInformation"
})
public class PolicyTermType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "PolicyTermNumberId", required = true)
    protected String policyTermNumberId;
    @XmlElement(name = "ContractPeriod", required = true)
    protected ContractPeriodType contractPeriod;
    @XmlElement(name = "InstalmentInformation", required = true)
    protected List<InstalmentInformationType> instalmentInformation;

    /**
     * Gets the value of the policyTermNumberId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPolicyTermNumberId() {
        return policyTermNumberId;
    }

    /**
     * Sets the value of the policyTermNumberId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPolicyTermNumberId(String value) {
        this.policyTermNumberId = value;
    }

    /**
     * Gets the value of the contractPeriod property.
     * 
     * @return
     *     possible object is
     *     {@link ContractPeriodType }
     *     
     */
    public ContractPeriodType getContractPeriod() {
        return contractPeriod;
    }

    /**
     * Sets the value of the contractPeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContractPeriodType }
     *     
     */
    public void setContractPeriod(ContractPeriodType value) {
        this.contractPeriod = value;
    }

    /**
     * Gets the value of the instalmentInformation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the instalmentInformation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInstalmentInformation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link InstalmentInformationType }
     * 
     * 
     */
    public List<InstalmentInformationType> getInstalmentInformation() {
        if (instalmentInformation == null) {
            instalmentInformation = new ArrayList<InstalmentInformationType>();
        }
        return this.instalmentInformation;
    }

}
