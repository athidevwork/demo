package dti.oasis.request.service;

import dti.oasis.accesstrailmgr.AccessTrailRequestIds;
import dti.oasis.app.ApplicationContext;
import dti.oasis.request.RequestLifecycleAdvisor;
import dti.oasis.request.RequestStorageIds;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.util.LogUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 17, 2010
 *
 * @author fcb
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/20/2012       fcb         129528 - Added initialization for OBR process.
 * 08/29/2014       parker      Issue 138227. Enhancement to add the ows logs.
 * ---------------------------------------------------
 */
public class
        WebServiceRequestLifecycleFilter implements Filter {

    public void init(FilterConfig config) throws ServletException {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "doFilter", new Object[]{req, resp, chain});
        }
        HttpServletRequest request = (HttpServletRequest) req;
        request.setAttribute(AccessTrailRequestIds.OWS_TRAIL_ACCESS_DATE, new Date());
        Map parameters = req.getParameterMap();
        if( parameters.containsKey("WSDL")) {
            chain.doFilter(req, resp);
        }
        else {
            try {
                // Initialize the Request Lifecycle
                getRequestLifecycleAdvisor().initialize((HttpServletRequest) req);
                RequestStorageManager rsm = RequestStorageManager.getInstance();
                rsm.set(RequestStorageIds.IS_PROCESS_EXCLUDED_FOR_OBR, true);
                chain.doFilter(req, resp);
            }
            // Skipping the catch block because all web services MUST handle exceptions
            finally {
                // Terminate the Request Lifecycle
                getRequestLifecycleAdvisor().terminate();
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "doFilter");
        }

   }

    public void destroy() {
    }

    protected ApplicationContext getApplicationContext() {
        if (m_applicationContext == null) {
            m_applicationContext = ApplicationContext.getInstance();
        }
        return m_applicationContext;
    }

    protected RequestLifecycleAdvisor getRequestLifecycleAdvisor() {
        if (m_requestLifecycleAdvisor == null) {
            m_requestLifecycleAdvisor = (RequestLifecycleAdvisor) getApplicationContext().getBean(RequestLifecycleAdvisor.BEAN_NAME);
        }
        return m_requestLifecycleAdvisor;
    }

    private ApplicationContext m_applicationContext;
    private RequestLifecycleAdvisor m_requestLifecycleAdvisor;

    private final Logger l = LogUtils.getLogger(getClass());
}
