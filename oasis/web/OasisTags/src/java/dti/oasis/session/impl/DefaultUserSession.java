package dti.oasis.session.impl;

import dti.oasis.util.OasisUser;
import dti.oasis.util.LogUtils;
import dti.oasis.app.AppException;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;


/**
 * The DefaultUserSession implements the UserSession by storing all values locally.
 * This UserSession is not replicated to other servers in a clustered environment.
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

public class DefaultUserSession extends BaseUserSession {

    /**
     * Construct the DefaultUserSession with the required OasisUser.
     * The OasisUser.getUserId() is used as the unique Session id.
     * @param oasisUser
     */
    public DefaultUserSession(OasisUser oasisUser) {
        super();
        setRequiredAttributes(oasisUser.getUserId(), oasisUser);

        Logger l = LogUtils.enterLog(getClass(), "DefaultUserSession");
        l.exiting(getClass().getName(), "DefaultUserSession");
    }

    /**
     * Returns true if there is a value associated with the given key in the UserSession.
     * Otherwise, false.
     */
    public boolean has(String key) {
        updateLastAccessedTime();
        return m_sessionAttributes.containsKey(key);
    }

    /**
     * Get the value mapped to the given key.
     *
     * @throws dti.oasis.app.AppException if there is no value associated with the given key.
     */
    public Object get(String key) {
        Logger l = LogUtils.enterLog(getClass(), "get", new Object[]{key});
        updateLastAccessedTime();

        Object value = m_sessionAttributes.get(key);
        if (value == null) {
            String message = null;
            if (OASIS_USER.equals(key)) {
                message = "The UserSession does not contain a value for the key'" + OASIS_USER + "'.";
            } else {
                message = "The UserSession for userId'"+getUserId()+"' does not contain a value for the key'"+key+"'.";
            }
            AppException e = new AppException(message);
            l.throwing(getClass().getName(), "get", e);
        }

        l.exiting(getClass().getName(), "get", value);
        return value;
    }

    /**
     * Return all key names contained in this UserSession
     * @return iterator of key names.
     */
    public Iterator getKeyNames() {
        return m_sessionAttributes.keySet().iterator();
    }

    /**
     * Stores a value in the UserSession associated with the given key.
     */
    public void set(String key, Object value) {
        Logger l = LogUtils.enterLog(getClass(), "set", new Object[]{key, value});
        updateLastAccessedTime();

        m_sessionAttributes.put(key, value);

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
        m_sessionAttributes.remove(key);

        l.exiting(getClass().getName(), "remove");
    }

    @Override
    public String toString() {
        return super.toString()+" -- "+"DefaultUserSession{" +
            "m_sessionAttributes=" + m_sessionAttributes +
            '}';
    }

    private Map m_sessionAttributes = new HashMap();
}