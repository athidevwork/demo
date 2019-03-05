package dti.oasis.spring.scope;

import dti.oasis.util.LogUtils;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PreDestroyViewMapEvent;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * Date:   5/29/12
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


public class ViewScope implements Scope, Serializable, HttpSessionBindingListener {
    private final WeakHashMap<HttpSession, Set<ViewScopeViewMapListener>> sessionToListeners = new WeakHashMap<HttpSession, Set<ViewScopeViewMapListener>>();

    @Override
    public Object get(String name, ObjectFactory objectFactory) {
        Logger l = LogUtils.enterLog(getClass(), "get", new Object[]{name, objectFactory});
        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (viewMap) {
            if (viewMap.containsKey(name)) {
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "get", "ViewMap contains key: "+name);
                }
                return viewMap.get(name);
            } else {
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.INFO, getClass().getName(), "get", "ViewMap doesn't contain key: "+name+" PUT NEW ONE...");
                }
                Object object = objectFactory.getObject();
                viewMap.put(name, object);

                return object;
            }
        }
    }

    @Override
    public Object remove(String name) {
        Logger l = LogUtils.enterLog(getClass(), "remove", new Object[]{name});
        throw new UnsupportedOperationException();
    }

    @Override
    public String getConversationId() {
        Logger l = LogUtils.enterLog(getClass(), "getConversationId");
        return null;
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
        Logger l = LogUtils.enterLog(getClass(), "registerDestructionCallback", new Object[]{name, callback});
        UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
        ViewScopeViewMapListener listener =
                new ViewScopeViewMapListener(viewRoot, name, callback, this);

        viewRoot.subscribeToViewEvent(PreDestroyViewMapEvent.class, listener);

        HttpSession httpSession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        final Set<ViewScopeViewMapListener> sessionListeners;
        synchronized (sessionToListeners) {
            if (!sessionToListeners.containsKey(httpSession)) {
                sessionToListeners.put(httpSession, new HashSet<ViewScopeViewMapListener>());
            }
            sessionListeners = sessionToListeners.get(httpSession);
        }
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (sessionListeners) {
            Set<ViewScopeViewMapListener> toRemove = new HashSet<ViewScopeViewMapListener>();
            for (ViewScopeViewMapListener viewMapListener : sessionListeners) {
                if (viewMapListener.checkRoot()) {
                    toRemove.add(viewMapListener);
                }
            }
            sessionListeners.removeAll(toRemove);
            sessionListeners.add(listener);
        }
        if (!FacesContext.getCurrentInstance().getExternalContext().getSessionMap().containsKey("sessionBindingListener")) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("sessionBindingListener", this);
        }

    }

    @Override
    public Object resolveContextualObject(String key) {
        Logger l = LogUtils.enterLog(getClass(), "resolveContextualObject", new Object[]{key});
        return null;
    }

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        Logger l = LogUtils.enterLog(getClass(), "valueBound", new Object[]{event});
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        Logger l = LogUtils.enterLog(getClass(), "valueUnbound", new Object[]{event});
        final Set<ViewScopeViewMapListener> listeners;
        synchronized (sessionToListeners) {
            if (sessionToListeners.containsKey(event.getSession())) {
                listeners = sessionToListeners.get(event.getSession());
                sessionToListeners.remove(event.getSession());
            } else {
                listeners = null;
            }
        }
        if (listeners != null) {
            for (ViewScopeViewMapListener listener : listeners) {
                listener.doCallback();
            }
        }
    }

    public void clearFromListener(ViewScopeViewMapListener listener) {
        Logger l = LogUtils.enterLog(getClass(), "clearFromListener", new Object[]{listener});
        HttpSession httpSession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        if (httpSession != null) {
            synchronized (sessionToListeners) {
                if (sessionToListeners.containsKey(httpSession)) {
                    sessionToListeners.get(httpSession).remove(listener);
                }
            }
        }
    }
}

