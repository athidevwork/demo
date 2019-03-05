package dti.pm.home.struts;

import dti.pm.core.struts.PMBaseAction;
import dti.pm.core.http.RequestIds;
import dti.oasis.util.LogUtils;
import dti.oasis.struts.ActionHelper;
import dti.oasis.app.AppException;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 12, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class HomeAction extends PMBaseAction {

    /**
     * This method is triggered automatically when there is no process parameter sent in along the requested url.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute(RequestIds.PROCESS, "display");
        return display(mapping, form, request, response);
    }

    /**
     * Prepare for and display the Home page.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward display(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response) throws Exception {

        Logger l = LogUtils.enterLog(getClass(), "execute",
            new Object[]{mapping, form, request, response});

        String forwardString = "success";

        try {
            ActionHelper.securePage(request, getClass().getName());
        }
        catch (Exception e) {
            forwardString =  handleError(AppException.UNEXPECTED_ERROR, "Failed to load te Home Page.", e, request, mapping);
        }

        return mapping.findForward(forwardString);
    }
}
