
package com.delphi_tech.ows.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MessageStatusType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MessageStatusType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MessageStatusCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ExtendedStatus" type="{http://www.delphi-tech.com/ows/Common}ExtendedStatusType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MessageStatusType", propOrder = {
    "messageStatusCode",
    "extendedStatus"
})
public class MessageStatusType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "MessageStatusCode", required = true)
    protected String messageStatusCode;
    @XmlElement(name = "ExtendedStatus")
    protected List<ExtendedStatusType> extendedStatus;

    /**
     * Gets the value of the messageStatusCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessageStatusCode() {
        return messageStatusCode;
    }

    /**
     * Sets the value of the messageStatusCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessageStatusCode(String value) {
        this.messageStatusCode = value;
    }

    /**
     * Gets the value of the extendedStatus property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the extendedStatus property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExtendedStatus().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExtendedStatusType }
     * 
     * 
     */
    public List<ExtendedStatusType> getExtendedStatus() {
        if (extendedStatus == null) {
            extendedStatus = new ArrayList<ExtendedStatusType>();
        }
        return this.extendedStatus;
    }

}
