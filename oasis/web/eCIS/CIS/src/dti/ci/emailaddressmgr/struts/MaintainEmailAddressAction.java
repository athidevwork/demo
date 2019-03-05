package dti.ci.emailaddressmgr.struts;

import dti.ci.struts.action.CIBaseAction;
import dti.ci.emailaddressmgr.EmailAddressManager;
import dti.ci.emailaddressmgr.EmailAddressFields;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.app.AppException;
import dti.oasis.recordset.Record;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * The action class for get email address for an entity.
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 29, 2010
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/16/2010       kshen       Use method getAllClientEmailAddressAsStr to get email addresses.
 * ---------------------------------------------------
 */

public class MaintainEmailAddressAction extends CIBaseAction {
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
        return loadAllClientEmailAddressForAjax(mapping, form, request, response);
    }

    public ActionForward loadAllClientEmailAddressForAjax(ActionMapping mapping, ActionForm form,
                                                          HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllClientEmailAddress", new Object[]{mapping, form, request, response});
        }

        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            securePage(request, form);
            // get input
            Record inputRecord = getInputRecord(request);

            String emailAddress = EmailAddressManager.getInstance().getAllClientEmailAddressAsStr(
                Long.valueOf(EmailAddressFields.getEntityId(inputRecord)));

            writeAjaxResponse(response, emailAddress);

        } catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get Email Address.", e, response);
        }

        return null;
    }
}
