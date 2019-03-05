package dti.oasis.session.impl;

import dti.oasis.app.AppException;
import dti.oasis.util.OasisUser;
import dti.oasis.util.LogUtils;
import javax.servlet.http.HttpSession;
import java.util.logging.Logger;
import java.util.Iterator;

import org.apache.commons.collections.iterators.EnumerationIterator;

/**
 * The HttpUserSession implements the UserSession by storing all values in a HttpSession.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 25, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 02/28/2008       fcb         getKeyNames() added.
 * ---------------------------------------------------
 */
public class HttpUserSession extends BaseUserSession {

    /**
     * Construct a HttpUserSession with the HttpSession and OasisUser.
     */
    public HttpUserSession(HttpSession httpSession, OasisUser oasisUser) {
        super();
        Logger l = LogUtils.enterLog(getClass(), "HttpUserSessionImpl", new Object[]{httpSession, oasisUser});

        m_httpSession = httpSession;
        setRequiredAttributes(httpSession.getId(), oasisUser);

        l.exiting(getClass().getName(), "HttpUserSessionImpl");
    }

    /**
     * Returns true if there is a value associated with the given key in the UserSession.
     * Otherwise, false.
     */
    public boolean has(String key) {
        updateLastAccessedTime();
        return m_httpSession.getAttribute(key) != null;
    }

    /**
     * Get the value mapped to the given key.
     *
     * @throws dti.oasis.app.AppException if there is no value associated with the given key.
     */
    public Object get(String key) {
        Logger l = LogUtils.enterLog(getClass(), "get", new Object[]{key});
        updateLastAccessedTime();

        Object value = m_httpSession.getAttribute(key);
        if (value == null) {
            String message = null;
            if (OASIS_USER.equals(key)) {
                message = "The UserSession does not contain a value for the key'" + OASIS_USER + "'.";
            } else {
                message = "The UserSession for userId'" + getUserId() + "' does not contain a value for the key'" + key + "'.";
            }
            AppException e = new AppException(message);
            l.throwing(getClass().getName(), "get", e);
        }

        l.exiting(getClass().getName(), "get", value);
        return value;
    }


    /**
     * Return all key names contained in this UserSession
     *
     * @return iterator of key names.
     */
    public Iterator getKeyNames() {
        return new EnumerationIterator(m_httpSession.getAttributeNames());
    }

    /**
     * Stores a value in the UserSession associated with the given key.
     */
    public void set(String key, Object value) {
        Logger l = LogUtils.enterLog(getClass(), "set", new Object[]{key, value});
        updateLastAccessedTime();

        m_httpSession.setAttribute(key, value);

        l.exiting(getClass().getName(), "set");
    }

    /**
     * Removes the value mapped to the given key.
     *
     * @throws dti.oasis.app.AppException if there is no value associated with the given key.
     */
    public void remove(String key) {
        Logger l = LogUtils.enterLog(getClass(), "remove", new Object[]{key});
        updateLastAccessedTime();

        get(key);   // Verify the attribute exists.
        m_httpSession.removeAttribute(key);

        l.exiting(getClass().getName(), "remove");
    }

    public HttpSession getHttpSession() {
        return m_httpSession;
    }

    protected void setHttpSession(HttpSession httpSession) {
        m_httpSession = httpSession;
    }

    @Override
    public String toString() {
        return super.toString()+" -- "+"HttpUserSession{" +
            "m_httpSession=" + m_httpSession.getId() + ": " + m_httpSession +
            '}';
    }

    private HttpSession m_httpSession;
}