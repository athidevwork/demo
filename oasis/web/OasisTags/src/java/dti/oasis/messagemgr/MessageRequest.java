package dti.oasis.messagemgr;

import java.io.Serializable;
import java.util.List;

/**
 * <p>(C) 2014 Delphi Technology, inc. (dti)</p>
 * Date:   5/21/2015
 *
 * @author jxgu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class MessageRequest implements Serializable {

    public MessageRequest() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MessageRequest that = (MessageRequest) o;

        if (key != null ? !key.equals(that.key) : that.key != null) return false;
        if (parameters != null ? !parameters.equals(that.parameters) : that.parameters != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MessageRequest{" +
                "key='" + key + '\'' +
                ", parameters=" + parameters +
                '}';
    }

    private String key;

    private List<String> parameters;

}
