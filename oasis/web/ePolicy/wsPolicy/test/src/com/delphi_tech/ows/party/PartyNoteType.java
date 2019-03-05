
package com.delphi_tech.ows.party;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PartyNoteType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PartyNoteType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PartyNoteNumberId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PartyNoteType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PartyNoteDateTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PartyNoteStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Note" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="key" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PartyNoteType", propOrder = {
    "partyNoteNumberId",
    "partyNoteType",
    "partyNoteDateTime",
    "partyNoteStatus",
    "note"
})
public class PartyNoteType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "PartyNoteNumberId")
    protected String partyNoteNumberId;
    @XmlElement(name = "PartyNoteType")
    protected String partyNoteType;
    @XmlElement(name = "PartyNoteDateTime")
    protected String partyNoteDateTime;
    @XmlElement(name = "PartyNoteStatus")
    protected String partyNoteStatus;
    @XmlElement(name = "Note")
    protected String note;
    @XmlAttribute(name = "key")
    protected String key;

    /**
     * Gets the value of the partyNoteNumberId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPartyNoteNumberId() {
        return partyNoteNumberId;
    }

    /**
     * Sets the value of the partyNoteNumberId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPartyNoteNumberId(String value) {
        this.partyNoteNumberId = value;
    }

    /**
     * Gets the value of the partyNoteType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPartyNoteType() {
        return partyNoteType;
    }

    /**
     * Sets the value of the partyNoteType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPartyNoteType(String value) {
        this.partyNoteType = value;
    }

    /**
     * Gets the value of the partyNoteDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPartyNoteDateTime() {
        return partyNoteDateTime;
    }

    /**
     * Sets the value of the partyNoteDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPartyNoteDateTime(String value) {
        this.partyNoteDateTime = value;
    }

    /**
     * Gets the value of the partyNoteStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPartyNoteStatus() {
        return partyNoteStatus;
    }

    /**
     * Sets the value of the partyNoteStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPartyNoteStatus(String value) {
        this.partyNoteStatus = value;
    }

    /**
     * Gets the value of the note property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNote() {
        return note;
    }

    /**
     * Sets the value of the note property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNote(String value) {
        this.note = value;
    }

    /**
     * Gets the value of the key property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the value of the key property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKey(String value) {
        this.key = value;
    }

}
