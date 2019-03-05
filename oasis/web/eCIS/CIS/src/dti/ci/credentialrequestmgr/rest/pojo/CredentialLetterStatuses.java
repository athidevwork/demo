package dti.ci.credentialrequestmgr.rest.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "CredentialLetterStatuses")
@XmlAccessorType(XmlAccessType.FIELD)
public class CredentialLetterStatuses implements Serializable {
    @XmlElement(name = "CredentialLetterStatus")
    @JsonProperty("credentialLetterStatus")
    public List<CredentialLetterStatus> statusList = new ArrayList<CredentialLetterStatus>();

    public CredentialLetterStatuses() {
    }

    public List<CredentialLetterStatus> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<CredentialLetterStatus> statusList) {
        this.statusList = statusList;
    }

    public void addStatus(CredentialLetterStatus status) {
        getStatusList().add(status);
    }

    @Override
    public String toString() {
        return "{" +
                "status=" + statusList +
                '}';
    }
}
