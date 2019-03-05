package dti.oasis.restful.request;

import dti.oasis.app.ApplicationContext;
import dti.oasis.request.RequestLifecycleAdvisor;
import dti.oasis.util.LogUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/30/2015
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class RestServiceRequestLifecycleFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "doFilter", new Object[]{servletRequest, servletResponse, filterChain});
        }

        getRequestLifecycleAdvisor().initialize((HttpServletRequest) servletRequest);

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            getRequestLifecycleAdvisor().terminate();
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "doFilter");
        }
    }

    @Override
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
