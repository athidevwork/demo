package dti.ci.entityclassmgr.struts;

import dti.ci.core.CIFields;
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
 * Entity Class Modify Action Class.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author Gerald C. Carney
 * Date:   Apr 1, 2004
 * <p/>
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/31/2018       ylu         Issue 195835: per code review:
 *                              1). remove unused SQL_OPERATION_PROPERTY, ENTITY_CLASS_ID, IS_NEW_VAL_PROPERTY
 *                              2). add entityClassId web field in webWB
 * ---------------------------------------------------
 */

public class MaintainModifyEntityClassAction extends BaseMaintainEntityClassAction {
    private final Logger l = LogUtils.getLogger(getClass());

    @Override
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadEntityClass(mapping, form, request, response);
    }

    public ActionForward loadEntityClass(ActionMapping mapping, ActionForm form,
                                         HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadEntityClass", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadEntityClassResult";

        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);

            processGetEntityType(request);

            Record entityClassInfo = (Record) request.getAttribute(MODIFIED_ENTITY_CLASS_RECORD);
            if (entityClassInfo == null) {
                // Load entity class info.
                entityClassInfo = getEntityClassManager().loadEntityClass(inputRecord);

                entityClassInfo.setFieldValue(CIFields.ENTITY_TYPE, inputRecord.getStringValueDefaultEmpty(CIFields.ENTITY_TYPE));

                // Process entity class info for modify.
                getEntityClassManager().processEntityClassInfoForModify(entityClassInfo);
            }

            entityClassInfo.setFields(inputRecord, false);

            publishOutputRecord(request, entityClassInfo);

            loadListOfValues(request, form);

            processSetCommonPropertiesToRequest(request, mapping);

        } catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load Modify Entity Class.", e, request, mapping);
            l.throwing(getClass().getName(), "loadEntityClass", e);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadEntityClass", af);
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
        Record inputRecord = null;

        try {
            if (isTokenValid(request, true)) {
                securePage(request, form, false);

                processGetEntityType(request);

                inputRecord = getInputRecord(request);

                getEntityClassManager().modifyEntityClass(inputRecord);
            }
        } catch (ValidationException ve) {
            handleValidationException(ve, request);
            request.setAttribute(MODIFIED_ENTITY_CLASS_RECORD, inputRecord);
            forwardString = "saveEntityClassFailed";

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

    private final static String MODIFIED_ENTITY_CLASS_RECORD = "modifiedEntityClassRecord";
}
