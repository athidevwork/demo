package dti.ci.credentialrequestmgr.rest.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

public class Requestor implements Serializable {
    @XmlElement(name = "PartyNumberId")
    @JsonProperty("partyNumberId")
    public long requestorId;

    @XmlElement(name = "AddressNumberId")
    @JsonProperty("addressNumberId")
    public long addressId;

    @XmlElement(name = "BillingAcctNumberId")
    @JsonProperty("billingAcctNumberId")
    public long billingAcctId;

    @XmlElement(name = "Name")
    @JsonProperty("name")
    public String name;

    @XmlElement(name = "Email")
    @JsonProperty("email")
    public String email;

    @XmlElement(name = "AttentionOf")
    @JsonProperty("attentionOf")
    public String attnOf;

    @XmlElement(name = "Notes")
    @JsonProperty("notes")
    public String notes;

    public Requestor() { }

    public long getRequestorId() { return requestorId; }

    public void setRequestorId(long requestorId) {
        this.requestorId = requestorId;
    }

    public long getAddressId() {
        return addressId;
    }

    public void setAddressId(long addressId) {
        this.addressId = addressId;
    }

    public long getBillingAcctId() {
        return billingAcctId;
    }

    public void setBillingAcctId(long billingAcctId) {
        this.billingAcctId = billingAcctId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAttnOf() {
        return attnOf;
    }

    public void setAttnOf(String attnOf) {
        this.attnOf = attnOf;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "{" +
                "requestorId='" + requestorId + '\'' +
                ", addressId='" + addressId + '\'' +
                ", billingAcctId='" + billingAcctId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", attnOf='" + attnOf + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}
