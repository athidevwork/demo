package dti.pm.policymgr.lockmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.busobjs.PolicyViewMode;
import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for maintain policy lock.
 * <p/>
 * <p/>
 * <p>(C) 2007 Delphi Technology, inc. (dti)</p>
 * Date:   August 24, 2007
 *
 * @author Bhong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/17/2011       fcb         124144 - refreshPolicyLock: added logic to prevent
 *                              raising an error when viewMode is OFFICIAL.
 * 12/31/2014       wdang       158738 - refreshPolicyLock: added a return parameter "termSpecified"
 *                              if the term id is not included in request.
 * 12/15/2015      jyang2       167179 - refreshPolicyLock: removed the throw exception code, since the front page will
 *                              refresh if refresh lock failed, no need to throw this exception. And in addition, the
 *                              exception sentence will block setting value for the "termSpecified" parameter.
 * ---------------------------------------------------
 */
public class MaintainLockAction extends PMBaseAction {
    /**
     * This method is triggered automatically when there is no process parameter
     * sent in along the requested url.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        return refreshPolicyLock(mapping, form, request, response);
    }

    /**
     * Method to refresh policy lock.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward refreshPolicyLock(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "refreshPolicyLock",
            new Object[]{mapping, form, request, response});

        try {
            // Refresh policy lock
            PolicyHeader policyHeader = getPolicyHeader(request);
            Record record = new Record();

            // Set termSpecified base on the request.
            if (StringUtils.isBlank(request.getParameter(RequestIds.POLICY_TERM_HISTORY_ID))) {
                record.setFieldValue("termSpecified", YesNoFlag.N);
            }
            else {
                record.setFieldValue("termSpecified", YesNoFlag.Y);
            }

            PolicyViewMode viewMode = policyHeader.getPolicyIdentifier().getPolicyViewMode();
            if (viewMode.isOfficial()) {
                record.setFieldValue("refreshPolicyLock", "N");
            }
            else {
                // Check if it ownes the lock
                if (policyHeader.getPolicyIdentifier().ownLock()) {
                    record.setFieldValue("refreshPolicyLock", "Y");
                }
            }

            writeAjaxXmlResponse(response, record);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to refresh Policy Lock.", e, response);
        }
        ActionForward af = null;
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "refreshPolicyLock", af);
        }
        return af;
    }

    public ActionForward unlockPolicy(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "unlockPolicy",new Object[]{mapping, form, request, response});
        }

        try {
            // unlock previous held lock
            getLockManager().unLockPreviouslyHeldLock();

            writeEmptyAjaxXMLResponse(response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to unlock policy.", e, response);
        }

        ActionForward af = null;
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "unlockPolicy", af);
        }
        return af;
    }
}
