package dti.oasis.app.impl;

import dti.oasis.util.LogUtils;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.app.ApplicationContext;

import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 28, 2007
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class ServletContextApplicationLifecycleAdapter implements ServletContextListener {


    public ServletContextApplicationLifecycleAdapter() {
        Logger l = LogUtils.enterLog(getClass(), "ServletContextApplicationLifecycleAdapter");

        if (!isDeployedAsEar()) {
            l.logp(Level.FINE, getClass().getName(), "ServletContextApplicationLifecycleAdapter", "Loading from OasisTags in: " + this.getClass().getClassLoader());
            // Construct this class with a new instance of the DefaultApplicationLifecycleAdvisor.
            m_advisor = ApplicationLifecycleAdvisorImpl.getInstance();
        }
        else {
            l.logp(Level.FINE, getClass().getName(), "ServletContextApplicationLifecycleAdapter.ServletContextApplicationLifecycleAdapter", "Application is deployed as an ear, so skipping this implementation");
        }


        l.exiting(getClass().getName(), "ServletContextApplicationLifecycleAdapter");
    }

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        Logger l = LogUtils.enterLog(getClass(), "contextInitialized");

        if (!isDeployedAsEar()) {
            l.logp(Level.INFO, getClass().getName(), "preStart", "Loading Web ApplicationName = " + servletContextEvent.getServletContext().getServletContextName());
            m_advisor.initialize(servletContextEvent.getServletContext().getServletContextName());
        }
        else {
            l.logp(Level.FINE, getClass().getName(), "ServletContextApplicationLifecycleAdapter.contextInitialized", "Application is deployed as an ear, so skipping this implementation");
        }

        l.exiting(getClass().getName(), "contextInitialized");
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        Logger l = LogUtils.enterLog(getClass(), "contextDestroyed");

        if (!isDeployedAsEar()) {
            l.logp(Level.INFO, getClass().getName(), "contextDestroyed", "Terminating Web ApplicationName = " + servletContextEvent.getServletContext().getServletContextName());
            m_advisor.terminate();
        }
        else {
            l.logp(Level.FINE, getClass().getName(), "ServletContextApplicationLifecycleAdapter.contextDestroyed", "Application is deployed as an ear, so skipping this implementation");
        }

        l.exiting(getClass().getName(), "contextDestroyed");
    }

    private static boolean isDeployedAsEar() {
        return (ApplicationContext.isInitialized() &&
                YesNoFlag.getInstance(ApplicationContext.getInstance().
                    getProperty("deployed.as.ear", "false")).booleanValue());
    }

    private ApplicationLifecycleAdvisorImpl m_advisor;
}
