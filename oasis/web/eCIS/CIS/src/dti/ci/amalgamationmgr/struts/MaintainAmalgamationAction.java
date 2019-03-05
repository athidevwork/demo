package dti.ci.amalgamationmgr.struts;

import dti.ci.amalgamationmgr.AmalgamationManager;
import dti.ci.struts.action.CIBaseAction;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.http.RequestIds;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Action class for maintain amalgamation
 * <p/>
 * <p>(C) 2009 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 12, 2009
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/06/2010       wfu         111776: Replaced hardcode string with resource definition
 * ---------------------------------------------------
 */
public class MaintainAmalgamationAction extends CIBaseAction {

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
        return loadAllAmalgamation(mapping, form, request, response);
    }

    /**
     * This method is called to display amalgamation page
     * <p/>
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllAmalgamation(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllAmalgamation", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            securePage(request, form);
            // get inputRecord
            Record inputRecord = getInputRecord(request);
            // load all amalgamation and set the dataBean to request
            RecordSet rs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            if (rs == null) {
                rs = getAmalgamationManager().loadAllAmalgamation(inputRecord);
            }
            setDataBean(request, rs);
            // Load amalgamation grid header
            loadGridHeader(request);
            // load list of values
            loadListOfValues(request, form);

            // set js messages
            addJsMessages();

            // Save token
            saveToken(request);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load amalgamation information.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllAmalgamation", af);
        return af;
    }

    //add js message
    private void addJsMessages() {
        // add js messages for csCommon.js
        MessageManager.getInstance().addJsMessage("cs.changes.lost.confirm");
        MessageManager.getInstance().addJsMessage("cs.save.process.notCompleted");
        MessageManager.getInstance().addJsMessage("cs.term.select.error.noSelect");
        MessageManager.getInstance().addJsMessage("cs.entity.miniPopup.error.noEntityId");
        MessageManager.getInstance().addJsMessage("cs.function.error.notExist");
        MessageManager.getInstance().addJsMessage("cs.records.delete.confirm");
        MessageManager.getInstance().addJsMessage("cs.entity.information.error.notRecorded");
        MessageManager.getInstance().addJsMessage("cs.rowSelected.error.exception");
        MessageManager.getInstance().addJsMessage("cs.run.error.grid.value");
    }

    /**
     * Save all amalgamation.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveAllAmalgamation(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveAllAmalgamation", new Object[]{mapping, form, request, response});
        String forwardString = "saveResult";
        RecordSet inputRecords = null;
        try {
            // Secure access to the page without loading the Oasis Fields
            securePage(request, form);
            //If the request has valid save token, then system saves the data else forward to load page.
            if (isTokenValid(request, true)) {
                inputRecords = getInputRecordSet(request);
                // Save the changes
                getAmalgamationManager().saveAllAmalgamation(inputRecords);
            }
        }
        catch (ValidationException ve) {
            // Set inputRecords to request.
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);
            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to save amalgamation.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveAllAmalgamation", af);
        return af;
    }

    /**
     * Get initial values for adding amalgamation.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getInitialValuesForAmalgamation(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                         HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForAmalgamation", new Object[]{mapping, form, request, response});
        try {
            // Secure page.
            securePage(request, form);
            // Get the initial values.
            Record record = getAmalgamationManager().getInitialValuesForAmalgamation();
            writeAjaxXmlResponse(response, record);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values for adding amalgamation .", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForAmalgamation", af);
        return af;
    }

    /**
     * verifyConfig
     */
    public void verifyConfig() {
        if (getAnchorColumnName() == null) {
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        }
        if (getAmalgamationManager() == null) {
            throw new ConfigurationException("The required property 'amalgamationManager' is missing.");
        }
    }

    public AmalgamationManager getAmalgamationManager() {
        return m_amalgamationManager;
    }

    public void setAmalgamationManager(AmalgamationManager amalgamationManager) {
        m_amalgamationManager = amalgamationManager;
    }

    private AmalgamationManager m_amalgamationManager;

}
