package dti.ci.service.rest.model;

import javax.xml.bind.annotation.XmlElement;

public class ExtendedStatus {
    @XmlElement(name = "StatusCode")
    public String statusCode;
    @XmlElement(name = "StatusType")
    public String type;
    @XmlElement(name = "StatusDescription")
    public String desc;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "{" +
                "statusCode='" + statusCode + '\'' +
                ", type='" + type + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
