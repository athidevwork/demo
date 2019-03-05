package dti.oasis.obr;

import dti.oasis.app.ApplicationContext;
import dti.oasis.error.ValidationException;
import dti.oasis.http.RequestIds;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.request.RequestStorageIds;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.util.LogUtils;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.struts.action.Action;
import org.springframework.aop.support.AopUtils;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.util.PatternMatchUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides the AOP model to intercept the request's method invocation and perform OBR logic
 * for save events
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 26, 2011
 *
 * @author jxgu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class SaveEventInterceptor extends org.springframework.transaction.interceptor.TransactionInterceptor{



    /**
     * This invokes the method for the provided request's method invocation information.

     * @param methodInvocation, information about the method that needs to be invoked.
     * @return object
     * @throws Throwable
     */
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		// Work out the target class: may be <code>null</code>.
		// The TransactionAttributeSource should be passed the target class
		// as well as the method, which may be from an interface.
		Class<?> targetClass = (methodInvocation.getThis() != null ? AopUtils.getTargetClass(methodInvocation.getThis()) : null);

		// If the transaction attribute is null, the method is non-transactional.
		final TransactionAttribute txAttr =
				getTransactionAttributeSource().getTransactionAttribute(methodInvocation.getMethod(), targetClass);
        Object objResult = null;
        Map pageViewCachedData =  m_requestHelper.getPageViewCacheMap();
        try {
            if (txAttr == null || isExcludeBean(methodInvocation.getThis()) || isExcludeMethod(methodInvocation.getMethod().getName())) {
                objResult = methodInvocation.proceed();
            } else {
                RequestStorageManager requestStorageManager = RequestStorageManager.getInstance();
                HttpServletRequest request = (HttpServletRequest) requestStorageManager.get(RequestStorageIds.HTTP_SEVLET_REQUEST);
                boolean isProcessExcludedForObr = (Boolean) requestStorageManager.get(RequestStorageIds.IS_PROCESS_EXCLUDED_FOR_OBR);
                if (!isProcessExcludedForObr && request.getAttribute(RequestHelper.IS_EXECUTE_BEFORE_SAVE_DONE) == null) {
                    Action action = (Action) requestStorageManager.get(RequestStorageIds.STRUTS_ACTION_CLASS);
                    MessageManager messageManager = MessageManager.getInstance();
                    int errorMessageCountBefore = messageManager.getErrorMessageCount();
                    m_requestHelper.executeBeforeSave(request, action);
                    int errorMessageCountAfter = messageManager.getErrorMessageCount();
                    if (errorMessageCountAfter > errorMessageCountBefore) {
                        // throw validation exception
                        throw new ValidationException("ValidationException from OBR OnSave");
                    }
                }
                objResult = methodInvocation.proceed();
            }
        } catch (ValidationException ve) {

            // The page view state has been already marked for cleanup.
            // Take a reference of the page view state data and place it in the request storage manager, so that the
            // garbage collector will not remove the data from the memory until this reference is removed.
            RequestStorageManager.getInstance().set(RequestIds.CACHE_ID_FOR_PAGE_VIEW_DATA, pageViewCachedData);

            throw ve;
        } catch (Exception e) {
            throw e;
        }
        return objResult ;
    }


    /**
     * check if the object is in exclude bean list
     * @param object
     * @return
     */
    protected boolean isExcludeBean(Object object) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isExcludeBean", new Object[]{object});
        }
        boolean isExcludedBean = false;
        if (this.m_excludeBeanNames != null) {
            for (String excludeBeanName : this.m_excludeBeanNames) {
                Object beanObject = ApplicationContext.getInstance().getBean(excludeBeanName);
                try {
                    Method getTargetSourceMethod = beanObject.getClass().getMethod("getTargetSource");
                    Object targetSource = getTargetSourceMethod.invoke(beanObject);
                    Method getTargetMethod = targetSource.getClass().getMethod("getTarget");
                    Object target = getTargetMethod.invoke(targetSource);
                    if (object == target) {
                        isExcludedBean = true;
                        break;
                    }
                } catch (Exception e) {
                    l.logp(Level.WARNING, getClass().getName(), "isExcludeBeanName", "Fail to get target object in bean " + excludeBeanName);
                }
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isExcludeBean", isExcludedBean);
        }
        return isExcludedBean;
    }

    /**
     * check if method is in exclude method list
     * @param methodName
     * @return
     */
    protected boolean isExcludeMethod(String methodName) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isExcludeMethod", new Object[]{methodName});
        }
        boolean isExcludeMethod = false;
        if (m_excludeMethodNames != null) {
            for (String excludeName : m_excludeMethodNames) {
                if (PatternMatchUtils.simpleMatch(excludeName, methodName)) {
                    isExcludeMethod = true;
                    break;
                }
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isExcludeMethod", isExcludeMethod);
        }
        return isExcludeMethod;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public SaveEventInterceptor() {
        super();
    }

    public List<String> getExcludeMethodNames() {
        return m_excludeMethodNames;
    }

    public void setExcludeMethodNames(List<String> excludeMethodNames) {
        m_excludeMethodNames = excludeMethodNames;
    }

    public List<String> getExcludeBeanNames() {
        return m_excludeBeanNames;
    }

    public void setExcludeBeanNames(List<String> excludeBeanNames) {
        m_excludeBeanNames = excludeBeanNames;
    }

    private List<String> m_excludeMethodNames = new ArrayList<String>();
    private List<String> m_excludeBeanNames = new ArrayList<String>();
    private RequestHelper m_requestHelper = new RequestHelper();
    private final Logger l = LogUtils.getLogger(getClass());
}