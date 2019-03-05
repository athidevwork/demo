package dti.oasis.restful;

import dti.oasis.jpa.NewEntityKeyMap;
import dti.oasis.messagemgr.Message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * <p/>
 * The standard response for restful service
 * <p/>
 * <p>(C) 2015 Delphi Technology, inc. (dti)</p>
 * Date: 03/10/2015
 *
 * @author jxgu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * ---------------------------------------------------
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceResponse {

    public static  final String STATUS_SUCCESS = "SUCCESS";
    public static  final String STATUS_FAILED = "FAILED";

    private String status = STATUS_SUCCESS;

    private Object body;

    private List<String> infoMessageList = new ArrayList<String>();

    private List<String> errorMessageList = new ArrayList<String>();

    private List<String> warningMessageList = new ArrayList<String>();

    private List<Message> messageList = new ArrayList<Message>();

    private List<NewEntityKeyMap> newEntityKeyMapList = new ArrayList<NewEntityKeyMap>();

    public ServiceResponse() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public List<String> getInfoMessageList() {
        return infoMessageList;
    }

    public void setInfoMessageList(List<String> infoMessageList) {
        this.infoMessageList = infoMessageList;
    }

    public List<String> getErrorMessageList() {
        return errorMessageList;
    }

    public void setErrorMessageList(List<String> errorMessageList) {
        this.errorMessageList = errorMessageList;
    }

    public List<String> getWarningMessageList() {
        return warningMessageList;
    }

    public void setWarningMessageList(List<String> warningMessageList) {
        this.warningMessageList = warningMessageList;
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }

    public List<NewEntityKeyMap> getNewEntityKeyMapList() {
        return newEntityKeyMapList;
    }

    public void setNewEntityKeyMapList(List<NewEntityKeyMap> newEntityKeyMapList) {
        this.newEntityKeyMapList = newEntityKeyMapList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServiceResponse that = (ServiceResponse) o;

        if (errorMessageList != null ? !errorMessageList.equals(that.errorMessageList) : that.errorMessageList != null)
            return false;
        if (infoMessageList != null ? !infoMessageList.equals(that.infoMessageList) : that.infoMessageList != null)
            return false;
        if (body != null ? !body.equals(that.body) : that.body != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (warningMessageList != null ? !warningMessageList.equals(that.warningMessageList) : that.warningMessageList != null)
            return false;
        if (messageList != null ? !messageList.equals(that.messageList) : that.messageList != null)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = status != null ? status.hashCode() : 0;
        result = 31 * result + (body != null ? body.hashCode() : 0);
        result = 31 * result + (infoMessageList != null ? infoMessageList.hashCode() : 0);
        result = 31 * result + (errorMessageList != null ? errorMessageList.hashCode() : 0);
        result = 31 * result + (warningMessageList != null ? warningMessageList.hashCode() : 0);
        result = 31 * result + (messageList != null ? messageList.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ServiceResponse{" +
                "status='" + status + '\'' +
                ", body=" + body +
                ", infoMessageList=" + infoMessageList +
                ", errorMessageList=" + errorMessageList +
                ", warningMessageList=" + warningMessageList +
                ", messageList=" + messageList +
                '}';
    }
}
