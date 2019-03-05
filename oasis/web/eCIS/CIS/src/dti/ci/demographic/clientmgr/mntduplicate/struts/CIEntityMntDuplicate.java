package dti.ci.demographic.clientmgr.mntduplicate.struts;

import dti.ci.demographic.clientmgr.mntduplicate.bo.EntityMntDuplicateManager;
import dti.ci.struts.action.CIBaseAction;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.http.RequestIds;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Action class for Maintain Entity Duplicate
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 19, 2008
 *
 * @author ldong
 */
/* 
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 
 *
 * ---------------------------------------------------
*/

public class CIEntityMntDuplicate extends CIBaseAction {

    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute(RequestIds.PROCESS, "iniEntityMntDuplicate");
        return iniEntityMntDuplicate(mapping, form, request, response);
    }

    /**
     * Method to initialize Entity Maintain Duplicate page
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward iniEntityMntDuplicate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                               HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "iniEntityMntDuplicate",
                new Object[]{mapping, form, request, response});

        String forwardString = "iniEntityMntDuplicate";
        try {
            securePage(request, form);
            String entityPk = request.getParameter("pk");
            String entityName = request.getParameter("entityName");
            if (StringUtils.isBlank(entityName)) {
                entityName = (String) request.getAttribute("entityName");    
            }
            /* validate */
            if (!FormatUtils.isLong(entityPk)) {
                throw new IllegalArgumentException(new StringBuffer().append(
                        "entity PK [").append(entityPk)
                        .append("] should be a number.")
                        .toString());
            }

            request.setAttribute("pk", entityPk);
            Record output = new Record();
            output.setFieldValue("originalEntity", entityName);
            output.setFieldValue("duplicateEntity", "");

            publishOutputRecord(request, output);

            /* Load LOV */
            loadListOfValues(request, form);

            addJsMessages();

            saveToken(request);

        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the Entity Duplicate page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "iniEntityMntDuplicate", af);
        return af;
    }

    /**
     * Method to save Entity Maintain Duplicate page
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward saveMntEntityDuplicate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveMntEntityDuplicate",
                new Object[]{mapping, form, request, response});

        String forwardString = "saveEntityMntDuplicate";
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                securePage(request, form);
                String entityOrgPk = request.getParameter("pk");
                String originalEntity = request.getParameter("originalEntity");
                request.setAttribute("entityName", originalEntity);
                /* validate */
                if (!FormatUtils.isLong(entityOrgPk)) {
                    throw new IllegalArgumentException(new StringBuffer().append(
                            "entity PK [").append(entityOrgPk)
                            .append("] should be a number.")
                            .toString());
                }
                String entityDupPk = request.getParameter("duplicateEntityPk");

                /* validate */
                if (!FormatUtils.isLong(entityDupPk)) {
                    throw new IllegalArgumentException(new StringBuffer().append(
                            "entity PK [").append(entityDupPk)
                            .append("] should be a number.")
                            .toString());
                }
                Record inputRecords = getInputRecord(request);
                String rsltMsg = getEntityMntDuplicateManager().saveEntityMntDuplicate(inputRecords);
                if (StringUtils.isBlank(rsltMsg)) {
                    MessageManager.getInstance().addInfoMessage("ci.maintainClientDup.save.success");
                } else {
                    MessageManager.getInstance().addErrorMessage("ci.maintainClientDup.save.error", new Object[]{rsltMsg});
                }
                saveToken(request);
            }

        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to save the Entity Duplicate page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveMntEntityDuplicate", af);
        return af;
    }


    /**
     * Add Js messages
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("ci.maintainClientDup.save.warning");
        MessageManager.getInstance().addJsMessage("ci.maintainClientDup.save.nodup");
        MessageManager.getInstance().addJsMessage("ci.maintainClientDup.save.noorg");
        MessageManager.getInstance().addJsMessage("ci.maintainClientDup.save.samepks");
    }

    public CIEntityMntDuplicate() {
    }

    /* Configuration constructor and accessor methods */
    public void verifyConfig() {
        if (getEntityMntDuplicateManager() == null)
            throw new ConfigurationException("The required property 'entityMntDuplicateManager' is missing.");
    }

    public EntityMntDuplicateManager getEntityMntDuplicateManager() {
        return entityMntDuplicateManager;
    }

    public void setEntityMntDuplicateManager(EntityMntDuplicateManager entityMntDupManager) {
        entityMntDuplicateManager = entityMntDupManager;
    }

    private EntityMntDuplicateManager entityMntDuplicateManager;
}