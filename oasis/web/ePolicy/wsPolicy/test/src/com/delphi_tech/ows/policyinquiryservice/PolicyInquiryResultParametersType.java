
package com.delphi_tech.ows.policyinquiryservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PolicyInquiryResultParametersType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PolicyInquiryResultParametersType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Filter" type="{http://www.delphi-tech.com/ows/PolicyInquiryService}FilterType" minOccurs="0"/>
 *         &lt;element name="ViewName" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ResolveCodes" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PolicyInquiryResultParametersType", propOrder = {
    "filter",
    "viewName",
    "resolveCodes"
})
public class PolicyInquiryResultParametersType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "Filter")
    protected FilterType filter;
    @XmlElement(name = "ViewName")
    protected List<String> viewName;
    @XmlElement(name = "ResolveCodes")
    protected String resolveCodes;

    /**
     * Gets the value of the filter property.
     * 
     * @return
     *     possible object is
     *     {@link FilterType }
     *     
     */
    public FilterType getFilter() {
        return filter;
    }

    /**
     * Sets the value of the filter property.
     * 
     * @param value
     *     allowed object is
     *     {@link FilterType }
     *     
     */
    public void setFilter(FilterType value) {
        this.filter = value;
    }

    /**
     * Gets the value of the viewName property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the viewName property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getViewName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getViewName() {
        if (viewName == null) {
            viewName = new ArrayList<String>();
        }
        return this.viewName;
    }

    /**
     * Gets the value of the resolveCodes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResolveCodes() {
        return resolveCodes;
    }

    /**
     * Sets the value of the resolveCodes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResolveCodes(String value) {
        this.resolveCodes = value;
    }

}
