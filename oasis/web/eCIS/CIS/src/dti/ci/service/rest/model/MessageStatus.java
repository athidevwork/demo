package dti.ci.service.rest.model;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class MessageStatus {
    @XmlElement(name = "StatusCode")
    public String statusCode;
    @XmlElement(name = "ExtendedStatus")
    public List<ExtendedStatus> extendedStatusList;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public List<ExtendedStatus> getExtendedStatusList() {
        return extendedStatusList;
    }

    public void setExtendedStatusList(List<ExtendedStatus> extendedStatusList) {
        this.extendedStatusList = extendedStatusList;
    }

    @Override
    public String toString() {
        return "{" +
                "statusCode='" + statusCode + '\'' +
                ", extendedStatus=" + extendedStatusList +
                '}';
    }
}
