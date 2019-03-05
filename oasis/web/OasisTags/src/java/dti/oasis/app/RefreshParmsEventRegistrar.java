package dti.oasis.app;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import dti.oasis.util.LogUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Register all beans that implement the RefreshParmsEventListener during the 'before initialization' event.
 * <p/>
 * <p>(C) 2013 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 13, 2013
 *
 * @author fcb
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class RefreshParmsEventRegistrar implements BeanPostProcessor {
    /**
     * Ignore the post initialization event.
     */
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    /**
     * Register all beans that implement the RefreshParmsEventListener
     */
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Logger l = LogUtils.enterLog(getClass(), "postProcessAfterInitialization", new Object[]{beanName});

        if (bean instanceof RefreshParmsEventListener) {
            l.logp(Level.FINE, getClass().getName(), "postProcessAfterInitialization", "Registering the bean '" + beanName +
                "' as a RefreshParmsEventListener.");
            m_refreshListeners.add((RefreshParmsEventListener)bean);
        }

        l.exiting(getClass().getName(), "postProcessAfterInitialization");

        return bean;
    }

    public void triggerRefreshParmsEvent(HttpServletRequest request) {
        Logger l = LogUtils.enterLog(getClass(), "triggerRefreshParmsEvent");

        Iterator it = m_refreshListeners.iterator();
        while (it.hasNext()) {
            ((RefreshParmsEventListener)it.next()).refreshParms(request);
        }

        l.exiting(RefreshParmsEventRegistrar.class.getName(), "triggerRefreshParmsEvent");
    }

    public RefreshParmsEventRegistrar getInstance() {
        Logger l = LogUtils.enterLog(RefreshParmsEventRegistrar.class, "getInstance");

        if (c_instance == null) {
            throw new ConfigurationException("A concrete implementation of RefreshParmsEventRegistrar has not been configured.");
        }

        l.exiting(RefreshParmsEventRegistrar.class.getName(), "getInstance", c_instance);

        return c_instance;
    }

    /**
     * Constructor. Store a reference to the concrete implementation of this class.
     */
    protected RefreshParmsEventRegistrar() {
        Logger l = LogUtils.enterLog(getClass(), "RefreshParmsEventRegistrar");

        c_instance = this;

        l.exiting(getClass().getName(), "RefreshParmsEventRegistrar");
    }

    private RefreshParmsEventRegistrar c_instance;
    private ArrayList<RefreshParmsEventListener> m_refreshListeners = new ArrayList<RefreshParmsEventListener>();

}
