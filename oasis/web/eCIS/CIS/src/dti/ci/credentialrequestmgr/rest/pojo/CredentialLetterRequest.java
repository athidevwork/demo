package dti.ci.credentialrequestmgr.rest.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

@XmlRootElement(name = "CredentialLetterRequest")
public class CredentialLetterRequest implements Serializable {
    /*@XmlElement(name = "UserId")
    @JsonProperty("userId")
    public String userId;*/

    @XmlElement(name = "OriginatingSystem")
    @JsonProperty("originatingSystem")
    public String originatingSystem;

    @XmlElement(name = "Requestor")
    @JsonProperty("requestor")
    public Requestor requestor;

    @XmlElement(name = "RequestType")
    @JsonProperty("requestType")
    public String requestType; /*pdf, email or post*/

    @XmlElement(name = "InsuredDetail")
    @JsonProperty("insuredDetail")
    public List<InsuredDetail> insuredDetail;

    @XmlElement(name = "Account")
    @JsonProperty("account")
    public List<Account> account;

    /*public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }*/

    public String getOriginatingSystem() { return originatingSystem; }

    public void setOriginatingSystem(String originatingSystem) {
        this.originatingSystem = originatingSystem;
    }

    public Requestor getRequestor() {
        return requestor;
    }

    public void setRequestor(Requestor requestor) {
        this.requestor = requestor;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public List<InsuredDetail> getInsuredDetail() {
        return insuredDetail;
    }

    public void setInsuredDetail(List<InsuredDetail> insuredDetail) {
        this.insuredDetail = insuredDetail;
    }

    public List<Account> getAccount() {
        return account;
    }

    public void setAccount(List<Account> account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "{" +
                //"userId='" + userId + '\'' +
                ", originatingSystem='" + originatingSystem + '\'' +
                ", requestor=" + requestor +
                ", requestType='" + requestType + '\'' +
                ", insuredDetail=" + insuredDetail +
                ", account=" + account +
                '}';
    }
}
