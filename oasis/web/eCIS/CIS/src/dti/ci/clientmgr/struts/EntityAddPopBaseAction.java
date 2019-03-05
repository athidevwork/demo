package dti.ci.clientmgr.struts;

import dti.ci.clientmgr.EntityAddFields;
import dti.ci.helpers.ICIConstants;
import dti.oasis.app.AppException;
import dti.oasis.error.ExpectedException;
import dti.oasis.error.ValidationException;
import dti.oasis.util.LogUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/27/2018
 *
 * @author dpang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/28/2018       dpang       issue 192743 - eCS-eCIS Refactoring: Add Person/ Add Organization
 * ---------------------------------------------------
 */
public class EntityAddPopBaseAction extends EntityAddBaseAction {

    private final Logger l = LogUtils.getLogger(getClass());

    /**
     * Initialize popup Add Person/ Add Organization page
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    @Override
    public ActionForward initPage(ActionMapping mapping,
                                  ActionForm form,
                                  HttpServletRequest request,
                                  HttpServletResponse response) throws Exception {
        String methodName = INIT_PAGE;
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{mapping, form, request, response});
        }

        String actionForward = ICIConstants.SUCCESS;
        try {
            commonLoadPage(mapping, request, form, true);
        } catch (Exception e) {
            actionForward = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to initialize page.", e, request, mapping);
            l.throwing(this.getClass().getName(), methodName, e);
        }

        ActionForward af = mapping.findForward(actionForward);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, af);
        }
        return af;
    }

    /**
     * Initialize popup Add Person/ Add Organization page
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    @Override
    public ActionForward loadPageAfterSave(ActionMapping mapping,
                                           ActionForm form,
                                           HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        String methodName = "loadPageAfterSave";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{mapping, form, request, response});
        }

        String actionForward = ICIConstants.SUCCESS;
        try {
            commonLoadPage(mapping, request, form, false);
        } catch (Exception e) {
            actionForward = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load page after saving entity.", e, request, mapping);
            l.throwing(this.getClass().getName(), methodName, e);
        }

        ActionForward af = mapping.findForward(actionForward);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, af);
        }
        return af;
    }

    /**
     * Process select.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward select(ActionMapping mapping, ActionForm form,
                                HttpServletRequest request, HttpServletResponse response) throws Exception {
        String methodName = "select";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{mapping, form, request, response});
        }
        String actionForward = ICIConstants.SUCCESS;
        try {
            actionForward = commonSave(request, form, mapping, true, false);
            request.setAttribute(EntityAddFields.PROCESS_SAVE_AFTER, EntityAddFields.PROCESS_SAVE_AFTER_SELECT);
        } catch (ValidationException ve) {
            actionForward = INIT_PAGE;
            l.throwing(this.getClass().getName(), methodName, ve);
        } catch (ExpectedException ee) {
            actionForward = INIT_PAGE;
            l.throwing(getClass().getName(), methodName, ee);
        } catch (Exception e) {
            actionForward = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to save entity.", e, request, mapping);
            l.throwing(this.getClass().getName(), methodName, e);
        }

        ActionForward af = mapping.findForward(actionForward);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, af);
        }
        return af;
    }

    /**
     * Process goToClient.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward goToClient(ActionMapping mapping, ActionForm form,
                                    HttpServletRequest request, HttpServletResponse response) throws Exception {

        String methodName = "goToClient";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{mapping, form, request, response});
        }

        String actionForward = ICIConstants.SUCCESS;
        try {
            actionForward = commonSave(request, form, mapping, true, false);
            request.setAttribute(EntityAddFields.PROCESS_SAVE_AFTER, EntityAddFields.PROCESS_SAVE_AFTER_GOTOCLIENT);
        } catch (ValidationException ve) {
            actionForward = INIT_PAGE;
            l.throwing(this.getClass().getName(), methodName, ve);
        } catch (ExpectedException ee) {
            actionForward = INIT_PAGE;
            l.throwing(getClass().getName(), methodName, ee);
        } catch (Exception e) {
            actionForward = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to save entity.", e, request, mapping);
            l.throwing(this.getClass().getName(), methodName, e);
        }

        ActionForward af = mapping.findForward(actionForward);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, af);
        }
        return af;
    }
}
