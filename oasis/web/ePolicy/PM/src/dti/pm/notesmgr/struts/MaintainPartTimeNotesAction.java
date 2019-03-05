package dti.pm.notesmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.notesmgr.NotesManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for Maintain Part time notes.
 * <p/>
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 24, 2008
 *
 * @author Bhong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/30/2014       jyang       159787 - Modified getInitialValuesForNotes(), corrected the debug and log message.
 * ---------------------------------------------------
 */

public class MaintainPartTimeNotesAction extends PMBaseAction {
    /**
     * This method is triggered automatically when there is no process parameter
     * sent in along the requested url.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute("unspecified", "Y");
        return loadAllPartTimeNotes(mapping, form, request, response);
    }

    /**
     * Method to load all part time notes.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllPartTimeNotes(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                              HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllPartTimeNotes", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadResult";
        try {
            // Secure page
            securePage(request, form);
            // Load lovs
            loadListOfValues(request, form);

            // If the process is unspecified, this page loads without grid.
            boolean isUnspecified = YesNoFlag.getInstance((String) request.getAttribute("unspecified")).booleanValue();
            boolean isClearScreen = YesNoFlag.getInstance(request.getParameter("clearScreen")).booleanValue();
            if (!isUnspecified & !isClearScreen) {
                Record inputRecord = getInputRecord(request);
                // Load the COI Holders
                RecordSet rs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
                if (rs == null) {
                    // Validate search criteria
                    getNotesManager().validateSearchCriteria(inputRecord);
                    // Retrieve all part time notes
                    rs = getNotesManager().loadAllPartTimeNotes(inputRecord);
                }
                if (rs.getSize() == 0) {
                    MessageManager.getInstance().addErrorMessage("pm.partTimeNotes.noDataFound");
                }
                // Sets data Bean
                setDataBean(request, rs);
                // Load grid header bean
                loadGridHeader(request);
                request.setAttribute("isAddAvailable", "Y");
                request.setAttribute("isSaveAvailable", "Y");
            }
            else {
                // This is only to prevent Jsp page from crashing if the databean is null
                setDataBean(request, new RecordSet());
                request.setAttribute("isAddAvailable", "N");
                request.setAttribute("isSaveAvailable", "N");
            }
            // Set js messages
            addJsMessages();
        }
        catch (ValidationException ve) {
            // This is only to prevent Jsp page from crashing if the databean is null
            setDataBean(request, new RecordSet());
            request.setAttribute("isAddAvailable", "N");
            request.setAttribute("isSaveAvailable", "N");
            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR,
                "Failed to load the part time notes page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllPartTimeNotes", af);
        }
        return af;
    }

    /**
     * Validate policy no
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionFoward
     * @throws Exception
     */
    public ActionForward validatePolicyNo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "validatePolicyNo",
            new Object[]{mapping, form, request, response});
        try {
            securePage(request, form);
            // Get all request parameters into a record
            Record inputRecord = getInputRecord(request);
            // Get the initial values
            Record result = getNotesManager().validatePolicyNo(inputRecord);
            writeAjaxXmlResponse(response, result);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to validate policy no.", e, response);
        }
        ActionForward af = null;
        l.exiting(getClass().getName(), "validatePolicyNo", af);
        return af;
    }


    /**
     * Get initial values for notes
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionFoward
     * @throws Exception
     */
    public ActionForward getInitialValuesForNotes(ActionMapping mapping,
                                                  ActionForm form,
                                                  HttpServletRequest request,
                                                  HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForNotes",
                new Object[]{mapping, form, request, response});
        }

        try {
            securePage(request, form);
            // Get all request parameters into a record
            Record inputRecord = getInputRecord(request);
            // Get the initial values
            Record initialValuesRec = getNotesManager().getInitialValues(inputRecord);
            writeAjaxXmlResponse(response, initialValuesRec);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get part time notes initial values for new record.", e, response);
        }
        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForNotes", af);
        return af;
    }

    /**
     * Save all part time notes
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward saveAllPartTimeNotes(ActionMapping mapping,
                                              ActionForm form,
                                              HttpServletRequest request,
                                              HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllPartTimeNotes",
                new Object[]{mapping, form, request, response});
        }
        String forwardString = "saveResult";
        RecordSet inputRecords = null;

        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure page
                securePage(request, form, false);
                // Generate input records
                inputRecords = getInputRecordSet(request);
                // Call the business component to implement the validate/save logic
                getNotesManager().saveAllPartTimeNotes(inputRecords);
            }
        }
        catch (ValidationException v) {
            // Save the recordset into the request
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);
            // Handle the validation exception
            handleValidationException(v, request);
        }

        catch (Exception e) {
            handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to save part time information.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllPartTimeNotes", af);
        }
        return af;
    }


    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.partTimeNotes.clearConfirm");
    }

    /**
     * verifyConfig
     */
    public void verifyConfig() {
        if (getAnchorColumnName() == null) {
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        }
        if (getNotesManager() == null) {
            throw new ConfigurationException("The required property 'notesManager' is missing.");
        }
    }

    public NotesManager getNotesManager() {
        return m_notesManager;
    }

    public void setNotesManager(NotesManager notesManager) {
        m_notesManager = notesManager;
    }

    private NotesManager m_notesManager;
}
