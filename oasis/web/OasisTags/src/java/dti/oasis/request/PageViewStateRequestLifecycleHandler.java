package dti.oasis.request;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.http.RequestIds;
import dti.oasis.session.PageViewStateAdmin;
import dti.oasis.session.UserSessionManager;
import dti.oasis.session.pageviewstate.PageViewStateManager;
import dti.oasis.util.LogUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 18, 2011
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
public class PageViewStateRequestLifecycleHandler implements RequestLifecycleListener {
  @Override
  public void initialize() {
      l.entering(getClass().getName(), "initialize");
    try {
        RequestStorageManager rsm = RequestStorageManager.getInstance();
        if (!rsm.has(RequestStorageIds.EXECUTING_IN_BACKGROUND_THREAD) ||
                !YesNoFlag.getInstance((String) rsm.get(RequestStorageIds.EXECUTING_IN_BACKGROUND_THREAD)).booleanValue()) {
            HttpServletRequest request = (HttpServletRequest) rsm.get(RequestStorageIds.HTTP_SEVLET_REQUEST);
            PageViewStateManager.getInstance().setupPageViewState(request);
            //By default, terminate the page view state to clear the memory at the end of each request life cycle.
            //If the resulting page includes header or header popup jsp, this value will be changed to FALSE,
            //so that the page view state is available for subsequent requests. For such requests, the page view state is
            //cleared as part of browser page unload event or session time out - which ever occurs first.
            rsm.set(PageViewStateManager.TERMINATE_PAGE_VIEW_STATE, Boolean.TRUE);
        }
        else {
            l.logp(Level.FINE, getClass().getName(), "initialize", "Skipping initialize logic since executing in a background thread.");
        }
    } catch (Exception e) {
        l.logp(Level.SEVERE, getClass().getName(), "terminate", "Fail to terminate in PageViewStateRequestLifecycleHandler", e);
    }
    l.exiting(getClass().getName(), "initialize");
  }

  @Override
  public void terminate() {
      l.entering(getClass().getName(), "terminate");
    try {
        RequestStorageManager rsm = RequestStorageManager.getInstance();
        if (!rsm.has(RequestStorageIds.EXECUTING_IN_BACKGROUND_THREAD) ||
                !YesNoFlag.getInstance((String) rsm.get(RequestStorageIds.EXECUTING_IN_BACKGROUND_THREAD)).booleanValue()) {
            HttpServletRequest request = (HttpServletRequest) rsm.get(RequestStorageIds.HTTP_SEVLET_REQUEST);
            boolean isAjaxRequest = false;
            if (request.getParameter("__isAjaxRequest") != null) {
                isAjaxRequest = Boolean.valueOf(request.getParameter("__isAjaxRequest").toString());
            }
            if (request.getParameter("_isAJAX") != null) {   /* By Code Lookup Requests */
                isAjaxRequest = Boolean.valueOf(YesNoFlag.getInstance(request.getParameter("_isAJAX").toString()).booleanValue());
            }
            if ( !isAjaxRequest && Boolean.valueOf((Boolean) rsm.get(PageViewStateManager.TERMINATE_PAGE_VIEW_STATE))) {
              if (rsm.has(RequestIds.CACHE_ID_FOR_PAGE_VIEW)) {
                  String pageViewId = (String) rsm.get(RequestIds.CACHE_ID_FOR_PAGE_VIEW);
                  PageViewStateAdmin pageViewStateAdmin = (PageViewStateAdmin) UserSessionManager.getInstance().getUserSession(request);
                  pageViewStateAdmin.clearPageViewData(pageViewId);
                  l.logp(Level.FINE, getClass().getName(), "terminate", "Page view state content has been cleared for page view id:" + pageViewId);
              }
            }
        }
        else {
            l.logp(Level.FINE, getClass().getName(), "terminate", "Skipping terminate logic since executing in a background thread.");
        }
    } catch (Exception e) {
        l.logp(Level.SEVERE, getClass().getName(), "terminate", "Fail to terminate in PageViewStateRequestLifecycleHandler", e);
    }
    l.exiting(getClass().getName(), "terminate");
  }

  @Override
  public boolean failure(Throwable e, boolean fixed) {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }
    private final Logger l = LogUtils.getLogger(getClass());
}
