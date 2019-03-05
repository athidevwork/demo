package dti.oasis.session;

import dti.oasis.util.OasisUser;
import dti.oasis.struts.IOasisAction;

import java.util.Iterator;
import java.util.Map;

/**
 * The UserSession class holds information related to a User's Session. Information is cached as the application server
 * for use in subsequent requests. Care should be take to keep the UserSession lightweight.
 * Otherwise, if  the application server is in a clustered environment, excessive time and network resources will
 * be wasted to keep a backup of the UserSession.
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
public interface UserSession extends Cloneable {

    public static final String OASIS_USER = IOasisAction.KEY_OASISUSER;

    /**
     * Get the unique identifier of this UserSession object.
     */
    public String getSessionId();

    /**
     * Get the OasisUser describing the User associated with this UserSession.
     */
    public OasisUser getOasisUser();

    /**
     * A convenience method to get the userId from the contained OasisUser.
     */
    public String getUserId();

    /**
     * A convenience method to get the user name from the contained OasisUser.
     */
    public String getUserName();

    /**
     * Returns true if there is a value associated with the given key in the UserSession.
     * Otherwise, false.
     */
    public boolean has(String key);

    /**
     * Get the value mapped to the given key.
     *
     * @throws dti.oasis.app.AppException if there is no value associated with the given key.
     */
    public Object get(String key);

    /**
     * Return all key names contained in this UserSession
     * @return iterator of key names.
     */
    public Iterator getKeyNames();

    /**
     * Stores a value in the UserSession associated with the given key.
     */
    public void set(String key, Object value);

    /**
     * Removes the value mapped to the given key.
     *
     * @throws dti.oasis.app.AppException if there is no value associated with the given key.
     */
    public void remove(String key);

    /**
     * Get the last time this UserSession was accessed
     * @return a long representing the last time this UserSession was accessed, expressed in milliseconds since 1/1/1970 GMT.
     */
    public long getLastAccessedTime();

    /**
     * Update the last accessed time.
     */
    public void updateLastAccessedTime();

    /**
     * Get the time this UserSession was created.
     * @return a long representing the time this UserSession was created, expressed in milliseconds since 1/1/1970 GMT.
     */
    public long getCreationTime();

    /**
     * Returns the unique identifier of this UserSession Object.
     */
    public int hashCode();
}
