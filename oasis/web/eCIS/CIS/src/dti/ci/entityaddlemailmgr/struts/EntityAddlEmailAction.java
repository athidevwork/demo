package dti.ci.entityaddlemailmgr.struts;

import dti.ci.entityaddlemailmgr.EntityAddlEmailFields;
import dti.ci.entityaddlemailmgr.EntityAddlEmailManager;
import dti.ci.helpers.ICIConstants;
import dti.ci.struts.action.CIBaseAction;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.http.RequestIds;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.FormatUtils;
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
 * Date:   4/11/13
 *
 * @author bzhu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/24/2013       bzhu        Issue 145614
 * ---------------------------------------------------
 */
public class EntityAddlEmailAction extends CIBaseAction {

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
    public ActionForward unspecified(ActionMapping mapping,
                                     ActionForm form,
                                     HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        return init(mapping, form, request, response);
    }

    /**
     * Initialize form exclusion page
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward init(ActionMapping mapping,
                              ActionForm form,
                              HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "init", new Object[]{mapping, form, request, response});
        }

        String forwardString = "initPage";
        RecordSet rs = null;
        try {
            // Secure the page and get the fields.
            securePage(request, form);

            String entityPk = EntityAddlEmailFields.getPK(getInputRecord(request));

            /* validate */
            if (!FormatUtils.isLong(entityPk)) {
                throw new AppException("ci.cicore.invalidError.EntityfkNotExists",
                        new StringBuffer().append(
                                "entity FK [").append(entityPk)
                                .append("] should be a number.")
                                .toString(),
                        new Object[]{entityPk});
            }

            request.setAttribute(ICIConstants.PK_PROPERTY, entityPk);

            Record input = new Record();
            input.setFieldValue(EntityAddlEmailFields.ENTITY_ID, entityPk);

            // Load entity additional email
            rs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            if (rs == null) {
                rs = getEntityAddlEmailManager().loadEntityAddlEmailList(input);
            }

            rs.setFieldValueOnAll(EntityAddlEmailFields.IS_DELETE_AVAILABLE, YesNoFlag.Y);

            setDataBean(request, rs);

            loadGridHeader(request);

            loadListOfValues(request, form);

            addJsMessages();

            saveToken(request);

        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR,
                    "Error when initializing the [CIS] electronic distribution page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "init", af);
        }

        return af;
    }


    /**
     * save the entity additional email
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward saveEntityAddlEmail(ActionMapping mapping,
                                             ActionForm form,
                                             HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveEntityAddlEmail", new Object[]{mapping, form, request, response});
        }

        String forwardString = "save";
        RecordSet inputRecords = null;

        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure the page and get the fields.
                securePage(request, form);

                inputRecords = getInputRecordSet(request);
                //Set retrieved electronic distribution recordset
                getEntityAddlEmailManager().updateEntityAddlEmailList(inputRecords);
            }

        } catch (ValidationException v) {
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);
            l.warning(v.getMessage());
            handleValidationException(v, request);
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR,
                    "Error when saving the entity additional email page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveEntityAddlEmail", af);
        }

        return af;
    }

    //add js message
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("cs.records.delete.confirm");
        MessageManager.getInstance().addJsMessage("ci.common.error.changes.lost");
    }

    public void verifyConfig() {
        if (getEntityAddlEmailManager() == null)
            throw new ConfigurationException("The required property 'EntityAddlEmailManager' is missing.");
    }

    public EntityAddlEmailManager getEntityAddlEmailManager() {
        return m_entityAdditionalManager;
    }

    public void setEntityAddlEmailManager(EntityAddlEmailManager entityAdditionalManager) {
        this.m_entityAdditionalManager = entityAdditionalManager;
    }

    private EntityAddlEmailManager m_entityAdditionalManager;
}
