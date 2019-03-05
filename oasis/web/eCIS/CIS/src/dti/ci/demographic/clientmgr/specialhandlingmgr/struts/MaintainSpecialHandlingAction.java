package dti.ci.demographic.clientmgr.specialhandlingmgr.struts;

import dti.ci.demographic.clientmgr.specialhandlingmgr.SpecialHandlingManager;
import dti.ci.struts.action.CIBaseAction;
import dti.ci.helpers.ICIConstants;
import dti.ci.core.error.PersistenceException;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.http.RequestIds;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.ActionHelper;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.messagemgr.MessageManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for Maintain SpecialHandling.
 * <p/>
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 30, 2008
 *
 * @author
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/15/2010       Kenney      Issue#106087: Load initial values when adding special handling
 * 10/06/2010       wfu         111776: Replaced hardcode string with resource definition
 * 07/01/2013       hxk         Issue 141840
 *                              Add pk to request, which CIS security needs.
 * ---------------------------------------------------
 */

public class MaintainSpecialHandlingAction extends CIBaseAction {

    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute(RequestIds.PROCESS, "loadAllSpecialHandlings");
        return loadAllSpecialHandlings(mapping, form, request, response);
    }

    /**
     * Method to load list of special handlings
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllSpecialHandlings(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                 HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllSpecialHandlings",
                new Object[]{mapping, form, request, response});

        String forwardString = "loadSPHResult";
        try {
            securePage(request, form);
            String entityFK = getInputRecord(request).getStringValue(ICIConstants.PK_PROPERTY, "");
            /* validate */
            if (!FormatUtils.isLong(entityFK)) {
              Exception e = new IllegalArgumentException(new StringBuffer().append(
                        "entity FK [").append(entityFK)
                        .append("] should be a number.")
                        .toString());
                throw new AppException("ci.cicore.invalidError.EntityfkNotExists",
                        e.getMessage(),
                        new Object[]{entityFK},
                        e);
            }
            request.setAttribute("pk",entityFK);
            RecordSet rs;
            rs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            if (rs == null) {
                /* Loads all SpecialHandling Data */
                rs = getSpecialHandlingManager().loadSpecialHandlingsByEntity(Long.parseLong(entityFK));
            }
            /* Sets data Bean */
            setDataBean(request, rs);

            // Make the Summary Record available for output
            Record output = rs.getSummaryRecord();

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);

            /* Load LOV */
            loadListOfValues(request, form);

            /* Load grid header bean */
            loadGridHeader(request);

            addJsMessages();

            saveToken(request);

        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the loadAllSpecialHandlings page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllSpecialHandlings", af);
        return af;
    }

    /**
     * Save special handlings
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveAllSpecialHandlings(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "save", new Object[]{mapping, form, request, response});
        String forwardString = "saveSPHResult";
        RecordSet inputRecords = null;
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                /* Secure page */
                securePage(request, form);
                String entityFK = getInputRecord(request).getStringValue(ICIConstants.PK_PROPERTY, "");
                /* validate */
                if (!FormatUtils.isLong(entityFK)) {
                    Exception e = new IllegalArgumentException(new StringBuffer().append(
                            "entity FK [").append(entityFK)
                            .append("] should be a number.")
                            .toString());
                    throw new AppException("ci.cicore.invalidError.EntityfkNotExists",
                            e.getMessage(),
                            new Object[]{entityFK},
                            e);
                }
                /* Generate input records */
                inputRecords = getInputRecordSet(request);
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "saveAllSpecialHandlings",
                            "Saving the saveAllSpecialHandlings inputRecords: " + inputRecords);
                }

                /* Call the business component to implement the validate/save logic */
                getSpecialHandlingManager().saveAllSpecialHandlings(ActionHelper.initializeOasisUser(request).getUserId(), inputRecords);
                saveToken(request);
            }
        }
        catch (ValidationException v) {
            // Save the recordset into the request
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);

            // Handle the validation exception
            handleValidationException(v, request);
        } catch (PersistenceException pe) {
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);
            handleError(pe.getMessageKey(), "Failed to save the Special Handling", pe, request, mapping);
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to save the Special Handling", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "save", af);
        return af;
    }

    /**
     * This method is called by AJAX to get intitial values for a Special Handling
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
    public ActionForward getInitialValuesForAddSpecialHandling(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // return load(mapping, form, request, response);
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForAddSpecialHandling", new Object[]{mapping, form, request, response});
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            securePage(request, form);

            // get input
            Record inputRecord = getInputRecord(request);
            Record initialRecord = getSpecialHandlingManager().getInitialValuesForAddSpecialHandling(inputRecord);

            // get LOV labels for initial values
            publishOutputRecord(request, initialRecord);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, initialRecord);

            // prepare return values
            writeAjaxXmlResponse(response, initialRecord, true);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial Special Handling data.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForAddSpecialHandling", af);
        return af;
    }

    /**
     * add js message
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("js.select.oneOrMoreRow.delete");
        MessageManager.getInstance().addJsMessage("js.delete.confirmation");

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

    /* Configuration constructor and accessor methods */
    public void verifyConfig() {
        if (getSpecialHandlingManager() == null)
            throw new ConfigurationException("The required property 'specialHandlingManager' is missing.");
    }

    public MaintainSpecialHandlingAction() {
    }

    public SpecialHandlingManager getSpecialHandlingManager() {
        return m_specialHandlingManager;
    }

    public void setSpecialHandlingManager(SpecialHandlingManager specialHandlingManager) {
        m_specialHandlingManager = specialHandlingManager;
    }

    private SpecialHandlingManager m_specialHandlingManager;

}
