package dti.oasis.request;

import dti.oasis.session.UserSession;

import java.util.Map;
import java.util.Random;
import java.util.Date;
import java.io.Serializable;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 29, 2008
 *
 * @author fcbibire
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class RequestSession {

    public Map getRequestStorageMap() {
        return m_requestStorageMap;
    }

    public void setRequestStorageMap(Map requestStorageMap) {
        m_requestStorageMap = requestStorageMap;
    }

    public UserSession getUserSession() {
        return m_userSession;
    }

    public void setUserSession(UserSession userSession) {
        m_userSession = userSession;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(
            "requestStorageMap{"+m_requestStorageMap+"}"+
            ",userSession{"+m_userSession+"}");
        return sb.toString();
    }

    Map m_requestStorageMap;
    UserSession m_userSession;
    //private static final long serialVersionUID = (new Random((new Date()).getTime())).nextLong();
}
