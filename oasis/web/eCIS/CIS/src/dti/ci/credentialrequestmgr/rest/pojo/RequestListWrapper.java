package dti.ci.credentialrequestmgr.rest.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "Requests")
public class RequestListWrapper {
    @XmlElement(name = "Request")
    @JsonProperty("request")
    public List<Request> requests;

    public RequestListWrapper() {}

    public List<Request> getRequests() {
        return requests;
    }

    public void setRequests(List<Request> requests) {
        this.requests = requests;
    }

    public void addRequest(Request request) {
        getRequests().add(request);
    }
}
