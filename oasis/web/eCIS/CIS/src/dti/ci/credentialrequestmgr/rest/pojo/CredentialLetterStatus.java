package dti.ci.credentialrequestmgr.rest.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "CredentialLetterStatus")
@XmlAccessorType(XmlAccessType.FIELD)
public class CredentialLetterStatus implements Serializable {
    @XmlElement(name = "CredentialRequestNumberId")
    @JsonProperty("credentialRequestNumberId")
    public long requestNumberId;

    @XmlElement(name = "Status")
    @JsonProperty("status")
    public String status;

    @XmlElement(name = "RequestDate")
    @JsonProperty("requestDate")
    public String requestDate;

    @XmlElement(name = "Notes")
    @JsonProperty("notes")
    public String notes;

    public CredentialLetterStatus() { }

    public long getRequestNumberId() {
        return requestNumberId;
    }

    public void setRequestNumberId(long requestNumberId) {
        this.requestNumberId = requestNumberId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
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
                "requestNumberId=" + requestNumberId +
                ", status='" + status + '\'' +
                ", requestDate='" + requestDate + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}
