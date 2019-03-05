package dti.pm.componentmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.util.LogUtils;
import dti.pm.componentmgr.ComponentManager;
import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for process Rm component.
 * <p/>
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 15, 2008
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/13/2018       wrong       192557 - Modified saveAllProcessingEvent() to call hasValidSaveToken() to be used for
 *                                       CSRFInterceptor.
 * ---------------------------------------------------
 */
public class MaintainRmComponentAction extends PMBaseAction {
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
        return loadAllProcessingEvent(mapping, form, request, response);
    }

    /**
     * Method to load process event and detail.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllProcessingEvent(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllProcessingEvent", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {
            // Secures access to the page, loads the oasis fields without loading LOVs,
            // and map the input parameters into the fields.
            securePage(request, form);
            // Get inputRecord.
            Record inputRecord = getInputRecord(request);
            // Load the processing event recordSet from request.
            RecordSet eventRs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            // Load the processing detail from request.
            RecordSet detailRs = (RecordSet) request.getAttribute(PROCESSING_DETAIL_RECORD_SET);
            if (eventRs == null) { // Retrieve processing event recordSet from DB.
                eventRs = getComponentManager().loadAllProcessingEvent(inputRecord);
            }
            if (detailRs == null) { // Retrieves processing detail recordSet from DB.
                detailRs = getComponentManager().loadAllProcessingDetail(inputRecord);
            }
            // Set loaded data into request.
            setDataBean(request, eventRs);
            setDataBean(request, detailRs, PROCESSING_DETAIL_GRID_ID);
            // Make the Summary Record available for output.
            Record output = eventRs.getSummaryRecord();
            output.setFields(detailRs.getSummaryRecord(), false);
            output.setFields(inputRecord, false);
            // Publish the output record for use by the Oasis Tags and JSP.
            publishOutputRecord(request, output);
            // Set currentGridId to every gridID on page before load gird header then load grid header for each grid.
            loadGridHeader(request);

            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, PROCESSING_DETAIL_GRID_ID);
            loadGridHeader(request, null, PROCESSING_DETAIL_GRID_ID, PROCESSING_DETAIL_GRID_LAYER_ID);
            // Load the list of values after loading the data.
            loadListOfValues(request, form);
            // Add Js messages.
            addJsMessages();
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load process RM discount page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllProcessingEvent", af);
        return af;
    }

    /**
     * Save all processing event.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveAllProcessingEvent(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllProcessingEvent", new Object[]{mapping, form, request, response});
        }
        String forwardString = "saveResult";
        RecordSet inputRecords = null;
        try {
            if (hasValidSaveToken(request)) {
                //If the request has valid save token, then proceed with save; if not forward to load page.
                // Secure access to the page without loading the Oasis Fields.
                securePage(request, form, false);
                // Map process event textXML to RecordSet for input.
                inputRecords = getInputRecordSet(request, PROCESSING_EVENT_GRID_ID);
                // Save all the process event.
                getComponentManager().performProcessingEvent(inputRecords);
            }
        }
        catch (ValidationException v) {
            // Save the input records into request.
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, PROCESSING_EVENT_GRID_ID);
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);
            // Handle the validation exception.
            handleValidationException(v, request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup("pm.processingRmComponent.save.error", "Failed to save all processing event.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveAllProcessingEvent", af);
        return af;
    }

    /**
     * Process event.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward processEvent(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processEvent", new Object[]{mapping, form, request, response});
        }
        String forwardString = "saveResult";
        Record inputRecord;
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            // Secure access to the page without loading the Oasis Fields.
            securePage(request, form, false);
            inputRecord = getInputRecord(request);
            // Update the RMT indicator.
            if (inputRecord.hasStringValue("processRmtIndicator") &&
                YesNoFlag.getInstance(inputRecord.getStringValue("processRmtIndicator")).booleanValue()) {
                getComponentManager().setRMTIndicator(inputRecord);
            }
            // Process discount.
            getComponentManager().processRmDiscount(inputRecord);
        }
        catch (ValidationException v) {
            // Save the input records into request.
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, PROCESSING_EVENT_GRID_ID);
            // Handle the validation exception.
            handleValidationException(v, request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup("pm.processingRmComponent.process.error", "Failed to process event.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "processEvent", af);
        return af;
    }

    /**
     * Get initial values for adding processing event.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getInitialValuesForProcessingEvent(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                            HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForProcessingEvent", new Object[]{mapping, form, request, response});
        try {
            // Secure page.
            securePage(request, form);
            // Get the initial values.
            Record record = getComponentManager().getInitialValuesForProcessingEvent();
            writeAjaxXmlResponse(response, record);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values for adding processing event.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForProcessingEvent", af);
        return af;
    }

    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.processingRmComponent.unsave.data");
        MessageManager.getInstance().addJsMessage("pm.processingRmComponent.process.confirmation");
    }

    /**
     * Overrides method in BaseAction. To get the anchor column name by current grid Id.
     * This is effective only for the multiple grids.
     *
     * @return anchor column name for the current grid
     */
    public String getAnchorColumnName() {
        Logger l = LogUtils.enterLog(getClass(), "getAnchorColumnName");
        String anchorName;
        if (RequestStorageManager.getInstance().has(CURRENT_GRID_ID)) {
            String currentGridId = (String) RequestStorageManager.getInstance().get(CURRENT_GRID_ID);
            if (currentGridId.equals(PROCESSING_DETAIL_GRID_ID)) {
                anchorName = getDetailAnchorColumnName();
            }
            else {
                anchorName = super.getAnchorColumnName();
            }
        }
        else {
            anchorName = super.getAnchorColumnName();
        }

        l.exiting(getClass().getName(), "getAnchorColumnName", anchorName);
        return anchorName;
    }

    /**
     * verifyConfig
     */
    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getDetailAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'detailAnchorColumnName' is missing.");
        if (getComponentManager() == null)
            throw new ConfigurationException("The required property 'componentManager' is missing.");
    }

    public ComponentManager getComponentManager() {
        return m_componentManager;
    }

    public void setComponentManager(ComponentManager componentManager) {
        m_componentManager = componentManager;
    }

    public String getDetailAnchorColumnName() {
        return m_detailAnchorColumnName;
    }

    public void setDetailAnchorColumnName(String detailAnchorColumnName) {
        m_detailAnchorColumnName = detailAnchorColumnName;
    }

    protected static final String CURRENT_GRID_ID = "currentGridId";
    protected static final String PROCESSING_EVENT_GRID_ID = "processingEventListGrid";
    protected static final String PROCESSING_DETAIL_GRID_ID = "processingDetailListGrid";
    protected static final String PROCESSING_EVENT_GRID_LAYER_ID = "PM_PROCESS_RM_DISCOUNT_EVENT_GH";
    protected static final String PROCESSING_DETAIL_GRID_LAYER_ID = "PM_PROCESS_RM_DISCOUNT_DETAIL_GH";
    protected static final String PROCESSING_EVENT_RECORD_SET = "processingEventRecordSet";
    protected static final String PROCESSING_DETAIL_RECORD_SET = "processingDetailRecordSet";
    private ComponentManager m_componentManager;
    private String m_detailAnchorColumnName;
}
