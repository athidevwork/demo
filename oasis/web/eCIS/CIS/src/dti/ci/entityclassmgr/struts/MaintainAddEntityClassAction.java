package dti.ci.entityclassmgr.struts;

import dti.ci.entityclassmgr.EntityClassFields;
import dti.ci.helpers.ICIConstants;
import dti.oasis.app.AppException;
import dti.oasis.error.ValidationException;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Entity Class Add Action Class.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author Gerald C. Carney
 *         Date:   Apr 1, 2004
 *         <p/>
 *         Revision Date    Revised By  Description
 *         ---------------------------------------------------
 *         10/31/2018       ylu         Issue 195835: per code review:
 *                                      1).remove unused ENTITY_CLASS_ID & SQL_OPERATION_PROPERTY & IS_NEW_VAL_PROPERTY
 *                                      2).remove saveEntityClassFailed method, when save failed, forward to loadEntityClassPage instead.
 *         ---------------------------------------------------
 */

public class MaintainAddEntityClassAction extends BaseMaintainEntityClassAction {
    private final Logger l = LogUtils.getLogger(getClass());

    @Override
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        return initPage(mapping, form, request, response);
    }

    /**
     * Init page for adding entity class
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward initPage(ActionMapping mapping, ActionForm form,
                                  HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "initPage", new Object[]{mapping, form, request, response});
        }

        ActionForward af = loadEntityClassPage(mapping, form, request, response);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "initPage", af);
        }
        return af;
    }

    public ActionForward loadEntityClassPage(ActionMapping mapping, ActionForm form,
                                             HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadEntityClassPage", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadEntityClassPageResult";

        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);

            publishOutputRecord(request, inputRecord);

            loadListOfValues(request, form);

            processSetCommonPropertiesToRequest(request, mapping);

        } catch (ValidationException ve) {
            // Handle validation exception
            handleValidationException(ve, request);
        } catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load Add Entity Class page.", e, request, mapping);
            l.throwing(getClass().getName(), "loadEntityClassPage", e);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadEntityClassPage", af);
        }
        return af;
    }

    /**
     * Save entity class.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveEntityClass(ActionMapping mapping, ActionForm form,
                                         HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveEntityClass", new Object[]{mapping, form, request, response});
        }

        String forwardString = "saveEntityClassSuccess";

        try {
            if (isTokenValid(request, true)) {
                securePage(request, form, false);

                Record inputRecord = getInputRecord(request);

                getEntityClassManager().addEntityClass(inputRecord);
            }
        } catch (ValidationException ve) {
            handleValidationException(ve, request);
            forwardString = "saveEntityClassFailed";
            request.setAttribute(EntityClassFields.ADD_WITH_ERROR, "Y");
            l.throwing(getClass().getName(), "saveEntityClass", ve);

        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Unable to save entity class.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveEntityClass", af);
        }
        return af;
    }
}
