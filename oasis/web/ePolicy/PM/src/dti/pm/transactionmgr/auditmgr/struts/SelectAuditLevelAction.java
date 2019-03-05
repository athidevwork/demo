package dti.pm.transactionmgr.auditmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.util.LogUtils;
import dti.pm.core.struts.PMBaseAction;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for view audit
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 24, 2007
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public class SelectAuditLevelAction extends PMBaseAction {


    /**
     * This method is triggered automatically when where is no process parameter
     * sent in along the requested url.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        return selectAuditLevel(mapping, form, request, response);
    }


    /**
     * Method to load all audit info for requested policy.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward selectAuditLevel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                          HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "selectAuditLevel", new Object[]{mapping, form, request, response});
        }
        String forwardString = "selectAuditLevel";
        try {
            // Secures access to the page, loads the oasis fields without loading LOVs,
            // and map the input parameters into the fields.
            securePage(request, form);
            loadListOfValues(request, form);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the select audilt level page.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "selectAuditLevel", af);
        return af;
    }

}
