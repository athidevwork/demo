package dti.ci.entitymodify.struts;

import dti.ci.core.EntityInfo;
import dti.ci.entitymodify.EntityModifyManager;
import dti.ci.helpers.ICIConstants;
import dti.ci.struts.action.CIBaseAction;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dti.oasis.util.StringUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.recordset.Record;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 13, 2009 12:06:26 PM
 *
 * @author gjlong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/30/2016      dpang       181349 - reset navigationList when goes back to entity list.
 * 04/13/2018      ylu         109088: Entity Modify refactor
 * ---------------------------------------------------
 */
public class MaintainEntityAction extends CIBaseAction {

    private final Logger l = LogUtils.getLogger(getClass());

    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) {
        l.entering(getClass().getName(),"unspecified", request);
        Record inputRecord = getInputRecord(request);
        String entityType = request.getParameter("entityType");
        if (StringUtils.isBlank(entityType)) {
            // get the entityType for a given entityPk
            entityType = getEntityModifyManager().getEntityType(inputRecord);
        }
        l.exiting(getClass().getName(),"unspecified",entityType);
        return mapping.findForward(entityType);
    }

    /**
     * Reset navigationList to session when user goes back to entity list page.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward setBackToEntityListSession(ActionMapping mapping, ActionForm form,
                                                    HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setBackToEntityListSession", new Object[]{mapping, form, request, response});
        }

        try {
            Record inputRecord = getInputRecord(request);
            String navigationList = inputRecord.getStringValue("navigationList", "");
            request.getSession(false).setAttribute(ICIConstants.ENTITY_SELECT_RESULTS, navigationList);
            Record result = new Record();
            result.setFieldValue("success", "Y");
            writeAjaxXmlResponse(response, result);
        } catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR,
                    "Unable to set back to entity list session", e, response);
        }

        l.exiting(getClass().getName(), "setBackToEntityListSession");
        return null;
    }

    /**
     * Update EntityInfo.noteExistB information after entity header notes gets updated
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward updateNoteExistInSession(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "updateNoteExistInSession", new Object[]{mapping, form, request, response});
        }

        try {
            Record inputRecord = getInputRecord(request);

            if (request.getSession().getAttribute("EntityInfo") != null) {
                EntityInfo entityInfo = (EntityInfo) request.getSession().getAttribute("EntityInfo");
                entityInfo.setNoteExistB(inputRecord.getStringValue("noteExistB"));
            }

            writeAjaxResponse(response, "Y");
        } catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to update noteExistB in EntityInfo in session.", e, response);
        }

        l.exiting(getClass().getName(), "updateNoteExistInSession");
        return null;
    }

    public void verifyConfig() {
        if (getEntityModifyManager() == null) {
            throw new ConfigurationException("The required property 'entityModifyManager' is missing.");
        }
    }

    public EntityModifyManager getEntityModifyManager() {
        return m_entityModifyManager;
    }

    public void setEntityModifyManager(EntityModifyManager entityModifyManager) {
        this.m_entityModifyManager = entityModifyManager;
    }

    private EntityModifyManager m_entityModifyManager;

}
