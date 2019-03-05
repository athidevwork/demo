package dti.oasis.session.impl;

import dti.oasis.app.ApplicationContext;
import dti.oasis.session.PageViewStateAdmin;
import dti.oasis.session.UserSessionManager;
import dti.oasis.session.UserSessionManagerAdmin;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 31, 2011
 *
 * @author mmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class HttpUserSessionListener implements HttpSessionListener {

    /**
     * The bean name of a HttpUserSessionListener extension if this default is not used.
     */
    public static final String BEAN_NAME = "HttpUserSessionListener";

    /**
     * Return an instance of the HttpUserSessionListener.
     */
    public synchronized static HttpUserSessionListener getInstance() {
        Logger l = LogUtils.enterLog(HttpUserSessionListener.class, "getInstance");
        if (c_instance == null) {
            if (ApplicationContext.getInstance().hasBean(BEAN_NAME)) {
                c_instance = (HttpUserSessionListener) ApplicationContext.getInstance().getBean(BEAN_NAME);
            }
        }
        l.exiting(HttpUserSessionListener.class.getName(), "getInstance", c_instance);
        return c_instance;
    }

    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        Logger l = LogUtils.enterLog(getClass(), "sessionCreated");

        HttpSession session = httpSessionEvent.getSession();
        String userSessionId = session.getId() + ":" + session.getServletContext().getContextPath();
        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "sessionCreated", "New session [" + session.getId() + "] has been created.");
        }

        UserSessionManagerAdmin admin = getUserSessionManagerAdmin();
        if (l.isLoggable(Level.FINER))
            admin.displayAllUserSessionsFromRequestStorageManager();
        admin.setHttpSessionMaxInactiveInterval(session);

        if (l.isLoggable(Level.FINER)) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss.SSSZ");
            l.logp(Level.FINER,getClass().getName(),"sessionCreated","NEW SESSION ["+session.getId()+"] CREATION TIME: "+sdf.format(new Date(session.getCreationTime()))+
                " LAST ACCESS TIME: "+sdf.format(new Date(session.getLastAccessedTime()))+" Key:["+userSessionId+"]");
        }

        l.exiting(getClass().getName(), "sessionCreated");
    }

    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        Logger l = LogUtils.enterLog(getClass(), "sessionDestroyed");

        HttpSession session = httpSessionEvent.getSession();
        String userSessionId = session.getId() + ":" + session.getServletContext().getContextPath();

        UserSessionManagerAdmin admin = getUserSessionManagerAdmin();
        if (l.isLoggable(Level.FINER))
            admin.displayAllUserSessionsFromRequestStorageManager();
        admin.cleanupUserSession(session);

        if (l.isLoggable(Level.FINER)) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss.SSSZ");
            l.logp(Level.FINER,getClass().getName(),"sessionDestroyed","OLD SESSION ["+session.getId()+"] CREATION TIME: "+sdf.format(new Date(session.getCreationTime()))+
                " LAST ACCESS TIME: "+sdf.format(new Date(session.getLastAccessedTime()))+" Key:["+userSessionId+"]");
        }

        session.invalidate();

        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "sessionDestroyed", "Session [" + session.getId() + "] has been destroyed.");
        }

        l.exiting(getClass().getName(), "sessionDestroyed");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public HttpUserSessionListener() {
    }

    public UserSessionManagerAdmin getUserSessionManagerAdmin() {
        if (m_userSessionManagerAdmin == null) {
            m_userSessionManagerAdmin = (UserSessionManagerAdmin) UserSessionManager.getInstance();
        }
        return m_userSessionManagerAdmin;
    }

    public void setUserSessionManagerAdmin(UserSessionManagerAdmin userSessionManagerAdmin) {
        m_userSessionManagerAdmin = userSessionManagerAdmin;
    }

    private UserSessionManagerAdmin m_userSessionManagerAdmin;
    private static HttpUserSessionListener c_instance;
}
