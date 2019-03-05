package dti.oasis.error.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import dti.oasis.util.LogUtils;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   2/5/2019
 *
 * @author athi
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
@XmlRootElement(name = "ExceptionResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExceptionResponse implements Serializable {
    @XmlElement(name = "Timestamp")
    @JsonProperty
    private Date timestamp;
    @XmlElement(name = "Method")
    @JsonProperty
    private String method;
    @XmlElement(name = "Request")
    @JsonProperty
    private String request;
    @XmlElement(name = "Details")
    @JsonProperty
    private String details;
    @XmlElement(name = "Message")
    @JsonProperty
    private List<String> message = new ArrayList<String>();

    public ExceptionResponse() { }

    public ExceptionResponse(Date timestamp, String method, String request, String message, String details) {
        super();
        if (this.timestamp == null) {
            this.timestamp = timestamp;
            this.method = method;
            this.request = request;
            this.details = details;
        }
        addMessage(message);
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getMethod() { return method; }

    public void setMethod(String method) { this.method = method; }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public List<String> getMessage() {
        return message;
    }

    public void setMessage(List<String> message) {
        this.message = message;
    }

    public void addMessage(String message) { getMessage().add(message); }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "{" +
                "timestamp=" + timestamp +
                ", method='" + method + '\'' +
                ", request='" + request + '\'' +
                ", message='" + message + '\'' +
                ", details='" + details + '\'' +
                '}';
    }

    private final Logger l = LogUtils.getLogger(getClass());
}
