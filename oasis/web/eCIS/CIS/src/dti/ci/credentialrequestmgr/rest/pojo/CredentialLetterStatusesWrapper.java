package dti.ci.credentialrequestmgr.rest.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "CredentialLetterStatuses")
public class CredentialLetterStatusesWrapper {
    @XmlElement(name = "CredentialLetterStatuses")
    @JsonProperty("credentialLetterStatuses")
    public List<CredentialLetterStatus> statuses;

    public CredentialLetterStatusesWrapper() {}

    public List<CredentialLetterStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<CredentialLetterStatus> statuses) {
        this.statuses = statuses;
    }

    public void addRequest(CredentialLetterStatus status) {
        getStatuses().add(status);
    }
}
