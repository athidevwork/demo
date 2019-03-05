
package com.delphi_tech.ows.policy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AdditionalInformationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AdditionalInformationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AdditionalDateTime" type="{http://www.delphi-tech.com/ows/Policy}AdditionalDateTimeType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="AdditionalNumber" type="{http://www.delphi-tech.com/ows/Policy}AdditionalNumberType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="AdditionalData" type="{http://www.delphi-tech.com/ows/Policy}AdditionalDataType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AdditionalInformationType", propOrder = {
    "additionalDateTime",
    "additionalNumber",
    "additionalData"
})
public class AdditionalInformationType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "AdditionalDateTime")
    protected List<AdditionalDateTimeType> additionalDateTime;
    @XmlElement(name = "AdditionalNumber")
    protected List<AdditionalNumberType> additionalNumber;
    @XmlElement(name = "AdditionalData")
    protected List<AdditionalDataType> additionalData;

    /**
     * Gets the value of the additionalDateTime property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the additionalDateTime property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAdditionalDateTime().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AdditionalDateTimeType }
     * 
     * 
     */
    public List<AdditionalDateTimeType> getAdditionalDateTime() {
        if (additionalDateTime == null) {
            additionalDateTime = new ArrayList<AdditionalDateTimeType>();
        }
        return this.additionalDateTime;
    }

    /**
     * Gets the value of the additionalNumber property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the additionalNumber property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAdditionalNumber().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AdditionalNumberType }
     * 
     * 
     */
    public List<AdditionalNumberType> getAdditionalNumber() {
        if (additionalNumber == null) {
            additionalNumber = new ArrayList<AdditionalNumberType>();
        }
        return this.additionalNumber;
    }

    /**
     * Gets the value of the additionalData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the additionalData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAdditionalData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AdditionalDataType }
     * 
     * 
     */
    public List<AdditionalDataType> getAdditionalData() {
        if (additionalData == null) {
            additionalData = new ArrayList<AdditionalDataType>();
        }
        return this.additionalData;
    }

}
