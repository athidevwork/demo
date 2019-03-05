package dti.oasis.session.pageviewstate.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.http.RequestIds;
import dti.oasis.recordset.Record;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.session.PageViewStateAdmin;
import dti.oasis.session.UserSessionManager;
import dti.oasis.session.pageviewstate.PageViewStateManager;
import dti.oasis.session.pageviewstate.impl.PageViewStateManagerImpl;
import dti.oasis.struts.ActionHelper;
import dti.oasis.struts.BaseAction;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 26, 2011
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

public class PageViewStateAction extends BaseAction {

    /**
     * Method to perform cleanup activity for the page.
     */
    public ActionForward scheduleCleanup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "scheduleCleanup",
                    new Object[]{mapping, form, request, response});

        ActionForward af = null;
        try {

          String pageViewStateId = "";
          if (RequestStorageManager.getInstance().has(RequestIds.CACHE_ID_FOR_PAGE_VIEW)) {
              pageViewStateId = (String) RequestStorageManager.getInstance().get(RequestIds.CACHE_ID_FOR_PAGE_VIEW);
          }

          // Clear cached data for the page view
          if (!StringUtils.isBlank(pageViewStateId)) {

              l.logp(Level.FINE, getClass().getName(), "scheduleCleanup", "About to check whether cleanup activity has been already scheduled for page view state id:" + pageViewStateId);

              /*
               If the page view state id is already marked for cleanup - then avoid adding it again.
                The Before Unload JS event could be called multiple times, there by multiple cleanup request could come for the same page.
                eg. <A> tag with href will automatically fire Before Unload JS event; while the <Body> tag will fire it again.
                    This is by design for Before Unload JS event.
               */
              if (!PageViewStateManager.getInstance().isPageViewStateAlreadyScheduledForCleanup(pageViewStateId)) {
                  l.logp(Level.FINE, getClass().getName(), "scheduleCleanup", "About to schedule cleanup activity for page view state id:" + pageViewStateId + " for session id:" + UserSessionManager.getInstance().getUserSession().getSessionId());
                  PageViewStateManager.getInstance().scheduleCleanupTask((PageViewStateAdmin) UserSessionManager.getInstance().getUserSession(), pageViewStateId);
              }
          } else {
              l.logp(Level.FINE, getClass().getName(), "scheduleCleanupTask", "Page View State Id not found in request.");
          }


          Record record = new Record();
          record.setFieldValue("responseStatus", "SUCCESS");
          writeAjaxXmlResponse(response, record);

        } catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to perform cleanup activity for the page.", e, response);
        }

        af = null;
        l.exiting(getClass().getName(), "scheduleCleanup");
        return af;
    }

  //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
    }

    public PageViewStateAction() {
        super();
    }
  }