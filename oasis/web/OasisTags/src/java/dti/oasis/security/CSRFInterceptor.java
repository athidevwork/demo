package dti.oasis.security;

import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.http.RequestIds;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.obr.RequestHelper;
import dti.oasis.request.RequestStorageIds;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.session.UserSessionIds;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.struts.Globals;
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
 * This class provides the AOP model to intercept the request's method invocation and perform transaction logic
 * for save events
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   February 2, 2018
 *
 * @author cesar valencia
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/01/2018       cesar       189605 - implementation of CSRF.
 * 05/15/2018       cesar       193003 - changed from ValidationException to AppException.
 * 05/23/2018       cesar       193446 - modified to be able to get the token from the loaded class.
 * ---------------------------------------------------
 */
public class CSRFInterceptor extends org.springframework.transaction.interceptor.TransactionInterceptor{

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
      boolean bCSRFFailed = false;
      // If the transaction attribute is null, the method is non-transactional.
      final TransactionAttribute txAttr = getTransactionAttributeSource().getTransactionAttribute(methodInvocation.getMethod(), targetClass);

      Object objResult = null;
      String msg="";

      Map pageViewCachedData =  m_requestHelper.getPageViewCacheMap();

      try {
         if (txAttr == null || isExcludeBean(methodInvocation.getThis()) || isExcludeMethod(methodInvocation.getMethod().getName())) {
            objResult = methodInvocation.proceed();
         } else {
            String sysParmEnableXSS = SysParmProvider.getInstance().getSysParm(RequestIds.CSRF_PROTECTION, "N");

            if (YesNoFlag.getInstance(sysParmEnableXSS).booleanValue()) {
               RequestStorageManager requestStorageManager = RequestStorageManager.getInstance();
               if (requestStorageManager.has(RequestStorageIds.STRUTS_ACTION_CLASS)) {
                  HttpServletRequest request = (HttpServletRequest) requestStorageManager.get(RequestStorageIds.HTTP_SEVLET_REQUEST);
                  Action action = (Action) requestStorageManager.get(RequestStorageIds.STRUTS_ACTION_CLASS);
                  String process = request.getParameter(RequestIds.PROCESS);

                  request.setAttribute(RequestStorageIds.IS_STRUTS_LOAD_ACTION, "N");

                  //get the token from the request parameter
                  String parameterToken = (request.getParameter(Globals.TOKEN_KEY) == null) ? "" : request.getParameter(Globals.TOKEN_KEY);

                  //get session token
                  String sessionClassName = (String)request.getSession().getAttribute(UserSessionIds.SUPER_CLASS_ACTION_NAME);
                  String className = action.getClass().getName() + UserSessionIds.TOKEN_SUFFIX;
                  String actionClassToken = (String) request.getSession().getAttribute(className);

                  if (StringUtils.isBlank(actionClassToken)) {
                     //at least on action class need to store the token, sometime
                     //an ajax request can invoke a different action class to perform a
                     //transactional operation. see handleOnCreatePolicy() function in createPolicy.js
                     actionClassToken = (String)request.getSession().getAttribute(sessionClassName);
                     if (actionClassToken == null) {
                        actionClassToken = "";
                     }
                     className = sessionClassName;
                  }

                  if (parameterToken.equalsIgnoreCase(actionClassToken) && !className.equalsIgnoreCase(sessionClassName)) {
                     bCSRFFailed = true;
                  } else if (!parameterToken.equalsIgnoreCase(actionClassToken) && className.equalsIgnoreCase(sessionClassName)) {
                     bCSRFFailed = true;
                  }else if (!parameterToken.equalsIgnoreCase(actionClassToken) && !className.equalsIgnoreCase(sessionClassName)) {
                     bCSRFFailed = true;
                  }

                  if (c_l.isLoggable(Level.FINER)) {
                     msg = setupMsg(className, process, parameterToken, actionClassToken);
                     c_l.logp(Level.FINER, CSRFInterceptor.class.getName(),
                        "invoke", "Unable to verify CSRF token - " + msg);
                  }

                  if(bCSRFFailed) {
                     msg = setupMsg(className, process, parameterToken, actionClassToken);
                     MessageManager.getInstance().addErrorMessage("core.security.csrf.token.verify.failed");
                     throw new AppException("core.security.csrf.token.verify.failed", "AppException: Unable to verify CSRF token: " + msg);
                  }
               }
            }

            objResult = methodInvocation.proceed();

            c_l.exiting(getClass().getName(), "invoke", targetClass.getName());

         }
      } catch (ValidationException ve) {

         // The page view state has been already marked for cleanup.
         // Take a reference of the page view state data and place it in the request storage manager, so that the
         // garbage collector will not remove the data from the memory until this reference is removed.
         RequestStorageManager.getInstance().set(RequestIds.CACHE_ID_FOR_PAGE_VIEW_DATA, pageViewCachedData);
         throw ve;
      } catch (Exception e) {
         e.printStackTrace();
         throw e;
      }
      return objResult ;
   }

   private String setupMsg(String className, String process, String parameterToken, String actionClassToken) {
      String msg=null;
      msg = "Action Class: " + className + "  process: " + process + " Page Token: " + parameterToken + " actionClassToken: " + actionClassToken;
      return msg;
   }
   /**
    * check if the object is in exclude bean list
    * @param object
    * @return
    */
   protected boolean isExcludeBean(Object object) {
      if (c_l.isLoggable(Level.FINER)) {
         c_l.entering(getClass().getName(), "isExcludeBean", new Object[]{object});
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
               c_l.logp(Level.WARNING, getClass().getName(), "isExcludeBeanName", "Fail to get target object in bean " + excludeBeanName);
            }
         }
      }
      if (c_l.isLoggable(Level.FINER)) {
         c_l.exiting(getClass().getName(), "isExcludeBean", isExcludedBean);
      }
      return isExcludedBean;
   }

   /**
    * check if method is in exclude method list
    * @param methodName
    * @return
    */
   protected boolean isExcludeMethod(String methodName) {
      if (c_l.isLoggable(Level.FINER)) {
         c_l.entering(getClass().getName(), "isExcludeMethod", new Object[]{methodName});
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
      if (c_l.isLoggable(Level.FINER)) {
         c_l.exiting(getClass().getName(), "isExcludeMethod", isExcludeMethod);
      }
      return isExcludeMethod;
   }
   //-------------------------------------------------
   // Configuration constructor and accessor methods
   //-------------------------------------------------
   public CSRFInterceptor() {
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
   private String REQUEST_HEADER_REFERER = "Referer";
   private final Logger c_l = LogUtils.getLogger(getClass());

}

