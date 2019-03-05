package dti.oasis.app.impl;

import dti.oasis.util.LogUtils;
import weblogic.application.ApplicationLifecycleEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is registered with WebLogic as a ApplicationLifecycleListener
 * to trigger the ApplicationLifecycleAdvisor to notify listeners of application initialization and termination.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 25, 2006
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
public class WebLogicApplicationLifecycleListener extends weblogic.application.ApplicationLifecycleListener {

    /**
     * Default Constructor, to be invoked only by WebLogic.
     */
    public WebLogicApplicationLifecycleListener() {
        Logger l = LogUtils.enterLog(getClass(), "WebLogicApplicationLifecycleListener");

        l.logp(Level.FINE, getClass().getName(), "WebLogicApplicationLifecycleListener", "Loading from OasisTags in: " + this.getClass().getClassLoader());
        // Construct this class with a new instance of the DefaultApplicationLifecycleAdvisor.
        m_advisor = ApplicationLifecycleAdvisorImpl.getInstance();

        l.exiting(getClass().getName(), "WebLogicApplicationLifecycleListener");
    }

    /**
     * Handle the preStart method to trigger the Initialization Application Lifecycle Event in the ApplicationLifecycleAdvisor.
     */
    public void preStart(ApplicationLifecycleEvent applicationLifecycleEvent) {
        Logger l = LogUtils.enterLog(getClass(), "preStart");

        l.logp(Level.INFO, getClass().getName(), "preStart", "Loading ApplicationName = " + applicationLifecycleEvent.getApplicationContext().getApplicationName());
        m_advisor.initialize(applicationLifecycleEvent.getApplicationContext().getApplicationName());

        l.exiting(getClass().getName(), "preStart");
    }

    /**
     * Handle the postStop method to trigger the Termination Application Lifecycle Event in the ApplicationLifecycleAdvisor.
     */
    public void postStop(ApplicationLifecycleEvent applicationLifecycleEvent) {
        Logger l = LogUtils.enterLog(getClass(), "postStop");

        l.logp(Level.INFO, getClass().getName(), "postStop", "Terminating ApplicationName = " + applicationLifecycleEvent.getApplicationContext().getApplicationName());
        m_advisor.terminate();

        l.exiting(getClass().getName(), "postStop");
    }

    /**
     * Ignore this event.
     */
    public void postStart(ApplicationLifecycleEvent applicationLifecycleEvent) {
        Logger l = LogUtils.enterLog(getClass(), "postStart");
        // Do nothing
        l.logp(Level.FINE, getClass().getName(), "postStart", "Ignore this WebLogic Application Lifecycle Event.");
        l.exiting(getClass().getName(), "postStart");
    }

    /**
     * Ignore this event.
     */
    public void preStop(ApplicationLifecycleEvent applicationLifecycleEvent) {
        Logger l = LogUtils.enterLog(getClass(), "preStop");
        // Do nothing
        l.logp(Level.FINE, getClass().getName(), "preStop", "Ignore this WebLogic Application Lifecycle Event");
        l.exiting(getClass().getName(), "preStop");
    }

    private ApplicationLifecycleAdvisorImpl m_advisor;
}