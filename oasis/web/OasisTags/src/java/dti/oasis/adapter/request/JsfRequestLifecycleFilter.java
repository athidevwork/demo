package dti.oasis.adapter.request;

import dti.oasis.app.ApplicationContext;
import dti.oasis.request.RequestLifecycleAdvisor;
import dti.oasis.util.LogUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * Date:   3/6/12
 *
 * @author mgitelman
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class JsfRequestLifecycleFilter implements Filter {
    public void init(FilterConfig config) throws ServletException {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws ServletException, IOException {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "doFilter", new Object[]{servletRequest, servletResponse, chain});
        }

        HttpServletRequest request = (HttpServletRequest) servletRequest;
//        System.out.println(new java.sql.Timestamp(System.currentTimeMillis()) + " -- "
//                + this.getClass().getName() + " -- REQUEST000: " + request.getRequestURI()+" -- CONTEXT: "+request.getContextPath());

        if(request.getRequestURI().contains(".dti")){
//            System.out.println(new java.sql.Timestamp(System.currentTimeMillis()) + " -- "
//                    + this.getClass().getName() + " -- REQUEST: " + request.getRequestURI()+" -- CONTEXT: "+request.getContextPath());
        }
        // Initialize the Request Lifecycle
        getRequestLifecycleAdvisor().initialize((HttpServletRequest) servletRequest);
        try {
            chain.doFilter(servletRequest, servletResponse);
        } finally {
//            System.out.println(new java.sql.Timestamp(System.currentTimeMillis()) + " -- "
//                    + this.getClass().getName() + " -- REQUEST: " + request.getRequestURI()+" -- TESTING FINALLY");

            // Terminate the Request Lifecycle
            getRequestLifecycleAdvisor().terminate();
        }


        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "doFilter");
        }

    }

    public void destroy() {
//        System.out.println("JsfRequestLifecycleFilter.destroy");
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
}
