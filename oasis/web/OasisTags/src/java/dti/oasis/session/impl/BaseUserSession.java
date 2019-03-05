package dti.oasis.session.impl;

import dti.oasis.http.RequestIds;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.session.PageViewStateAdmin;
import dti.oasis.util.LogUtils;
import dti.oasis.util.OasisUser;
import dti.oasis.session.UserSession;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides common reusable implementation for UserSession implementations.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 27, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 02/28/2008       fcb         clone() added.
 * 09/20/2012       fcb         Issue 136956: getUserId() - removed usage of Logger. This method is called from
 *                              Logger.processMessage, and this would cause an infinite recursion.
 * ---------------------------------------------------
 */
public abstract class BaseUserSession implements UserSession, PageViewStateAdmin {

    /**
     * Default constructor
     */
    protected BaseUserSession() {
    }

    /**
     * Set the required sessionId and OasisUser.
     * @param sessionId
     * @param oasisUser
     */
    protected void setRequiredAttributes(String sessionId, OasisUser oasisUser) {
        Logger l = LogUtils.enterLog(getClass(), "setRequiredAttributes", new Object[]{sessionId, oasisUser});

        setSessionId(sessionId);
        setOasisUser(oasisUser);

        l.exiting(getClass().getName(), "setRequiredAttributes");
    }

    /**
     * Get the unique identifier of this UserSession object.
     */
    public String getSessionId() {
        Logger l = LogUtils.enterLog(getClass(), "getSessionId");
        updateLastAccessedTime();
        l.exiting(getClass().getName(), "getSessionId");

        return m_sessionId;
    }

    protected void setSessionId(String sessionId) {
        Logger l = LogUtils.enterLog(getClass(), "setSessionId", new Object[]{sessionId});

        m_sessionId = sessionId;

        l.exiting(getClass().getName(), "setSessionId");
    }

    /**
     * Get the OasisUser describing the User associated with this UserSession.
     */
    public OasisUser getOasisUser() {
        updateLastAccessedTime();
        return (OasisUser) get(OASIS_USER);
    }

    /**
     * Implement this method to store the OasisUser associated with this UserSession.
     */
    protected void setOasisUser(OasisUser oasisUser) {
        Logger l = LogUtils.enterLog(getClass(), "setOasisUser", new Object[]{oasisUser});

        updateLastAccessedTime();
        set(OASIS_USER, oasisUser);

        l.exiting(getClass().getName(), "setOasisUser");
    }

    /**
     * A convenience method to get the userId from the contained OasisUser.
     * DO NOT USE Logger inside this method, this method is called from Logger.processMessage, and this
     * would create an infinite recursion.
     */
    public String getUserId() {
        updateLastAccessedTime();

        String userId = getOasisUser().getUserId();

        return userId;
    }

    /**
     * A convenience method to get the user name from the contained OasisUser.
     */
    public String getUserName() {
        Logger l = LogUtils.enterLog(getClass(), "getUserName");
        updateLastAccessedTime();

        String userName = getOasisUser().getUserName();

        l.exiting(getClass().getName(), "getUserName", userName);
        return userName;
    }

    /**
     * Get the last time this UserSession was accessed
     *
     * @return a long representing the last time this UserSession was accessed, expressed in milliseconds since 1/1/1970 GMT.
     */
    public long getLastAccessedTime() {
        Logger l = LogUtils.enterLog(getClass(), "getLastAccessedTime");

        l.exiting(getClass().getName(), "getLastAccessedTime", new Long(m_lastAccessedTime));
        return m_lastAccessedTime;
    }

    /**
     * Update the last accessed time.
     */
    public void updateLastAccessedTime() {
        m_lastAccessedTime = System.currentTimeMillis();
    }

    /**
     * Get the time this UserSession was created.
     *
     * @return a long representing the time this UserSession was created, expressed in milliseconds since 1/1/1970 GMT.
     */
    public long getCreationTime() {
        updateLastAccessedTime();
        return m_creationTime;
    }

    /**
     * Returns the unique identifier of this UserSession Object.
     */
    public int hashCode() {
        updateLastAccessedTime();
        return getSessionId().hashCode();
    }

  /**
   * Returns a new unique id for page document.
   */
    public String getNewPageViewId() {
        String pageViewId = java.util.UUID.randomUUID().toString();
        Map pageViewData = getPageViewData(pageViewId);
        return pageViewId;
    }

  /**
   * Get cached data for the cached page document id, if exists.
   * Otherwise a new page view state instance is created, cached and returned.
   */
    public Map getPageViewData() {
        String pageViewId = "";
        if (RequestStorageManager.getInstance().has(RequestIds.CACHE_ID_FOR_PAGE_VIEW)) {
            pageViewId = (String) RequestStorageManager.getInstance().get(RequestIds.CACHE_ID_FOR_PAGE_VIEW);
        } else {
            pageViewId = getNewPageViewId();
        }

      return getPageViewData(pageViewId);
    }

  /**
   * Get cached data for the page document.
   * @return a map representing cached data for the page document.
   */
    public Map getPageViewData(String pageViewId) {
        Logger l = LogUtils.enterLog(getClass(), "getPageViewData");

        Map pageViewData;
        if (m_pageViewSet.containsKey(pageViewId)) {
            if (l.isLoggable(Level.FINE))
                l.logp(Level.FINE, getClass().getName(), "getPageViewData", "Page View State Id [" + pageViewId + "] retrieved from map for session [" + m_sessionId + "].");
            pageViewData = (HashMap) m_pageViewSet.get(pageViewId);
        } else {
            if (l.isLoggable(Level.FINE))
                l.logp(Level.FINE, getClass().getName(), "getPageViewData", "New map has been created for page view state id [" + pageViewId + "] and stored for future retrieval for session [" + m_sessionId + "].");
            pageViewData = new HashMap();
            m_pageViewSet.put(pageViewId, pageViewData);
        }

        l.exiting(getClass().getName(), "getPageViewData", pageViewData);

        return pageViewData;
    }

   /**
    * Removes cached data for the page document.
    */
    public void clearPageViewData(String pageViewId) {
      Logger l = LogUtils.enterLog(getClass(), "clearPageViewData", new Object[]{pageViewId});
      m_pageViewSet.remove(pageViewId);
        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "clearPageViewData", "Cleared the cache for page view id:" + pageViewId);
            l.logp(Level.FINE, getClass().getName(), "clearPageViewData", "Number of page view state available:" + m_pageViewSet.size());
        }
      l.exiting(getClass().getName(), "clearPageViewData");
    }

  /**
     * Creates and returns a shallow copy of this object.
     *
     * @return a clone of this instance.
     * @see Cloneable
     */
    protected Object clone() throws CloneNotSupportedException {
        Logger l = LogUtils.enterLog(getClass(), "clone");

        UserSession clone = new DefaultUserSession(getOasisUser());
        ((BaseUserSession)clone).setSessionId(this.getSessionId());
        ((BaseUserSession)clone).setRequiredAttributes(this.getSessionId(),this.getOasisUser());
        ((BaseUserSession)clone).setOasisUser(this.getOasisUser());

        Iterator it = this.getKeyNames();
        while(it.hasNext()) {
            String key = (String)it.next();
            clone.set(key,this.get(key));
        }
        
        l.exiting(getClass().getName(), "clone");
        return clone;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss.SSSZ");
        String currentUser = "Session expired.User is empty";
        try{
            currentUser = getOasisUser().toString();
        }  catch (IllegalStateException e){
            //if the session expired in cleanupScheduledTasks().A IllegalStateException exception would be throw.
            //No need to deal with. Just return a empty user message.
        }
        return "BaseUserSession{" +
            "m_creationTime=" + sdf.format(new Date(m_creationTime)) +
            ", m_sessionId='" + m_sessionId + '\'' +
            ", m_lastAccessedTime=" + sdf.format(new Date(m_lastAccessedTime)) +
            ", m_pageViewSet=" + m_pageViewSet +
            ", OasisUser="+currentUser+
            '}';
    }

    private String m_sessionId;
    private long m_creationTime = System.currentTimeMillis();
    private long m_lastAccessedTime = System.currentTimeMillis();
    private Map m_pageViewSet = Collections.synchronizedMap(new HashMap());
}
