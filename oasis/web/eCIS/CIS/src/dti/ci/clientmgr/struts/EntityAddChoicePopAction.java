package dti.ci.clientmgr.struts;

import dti.ci.helpers.ICIConstants;
import dti.ci.struts.action.CIBaseAction;
import dti.oasis.app.AppException;
import dti.oasis.util.LogUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * STRUTS Action class for chosing how to add an entity. This page is
 * a popup page opened by the add button on entity select
 * search page.
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 8, 2005
 *
 * @author hxy
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
*/
public class EntityAddChoicePopAction extends CIBaseAction {

    private final Logger l = LogUtils.getLogger(getClass());

    /**
     * Handle unspecified action
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
        return initPage(mapping, form, request, response);
    }

    /**
     * Initialize page
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     */
    public ActionForward initPage(ActionMapping mapping, ActionForm form,
                                  HttpServletRequest request, HttpServletResponse response) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "initPage", new Object[]{mapping, form, request, response});
        }
        String actionForward = ICIConstants.SUCCESS;
        try {
            securePage(request, form);
            loadListOfValues(request, form);
        } catch (Exception e) {
            actionForward = handleError(AppException.UNEXPECTED_ERROR, "Failed to initialize Entity Add Choice page.", e, request, mapping);
            l.throwing(this.getClass().getName(), "initPage", e);
        }

        ActionForward af = mapping.findForward(actionForward);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "initPage", af);
        }
        return af;
    }
}
