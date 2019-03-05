package dti.pm.policymgr.mailingmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.mailingmgr.MailingEventFields;
import dti.pm.policymgr.mailingmgr.PolicyMailingManager;
import dti.pm.policymgr.mailingmgr.impl.MailingEventEntitlementRecordLoadProcessor;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for Maintain Policy Mailing.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 12, 2007
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/02/2008       sxm         Issue 86930 - modified mailing generation error handling
 * 10/06/2008       sxm         Issue 86930 - get detail generation error along when generate mailing
 * 06/23/2010       wtian       Issue 107312 - remove incorrect added in "getInitialValuesForMailingEvent" method.
 * 07/29/2010       syang       Issue 110028 - Modified loadAllMailingRecipient() to set currentGridId as mailingRecipientListGrid,
 *                                             so as to set the anchorColumnName as the first column. 
 * 09/05/2011       ryzhao      124622 - For pages with multiple grids:
 *                              1) Pass gridId as the third parameter to the setDataBean() method
 *                                 for all but the first grid.
 *                              2) Pass gridId/layerId as the third/fourth parameter to the loadGridHeader() method
 *                                 for all but the first grid.
 * 12/17/2012       awu         137609 - 1. Modified createPolicyMailingForPolicy to remove the logic of loading policy
 *                                          mailing page.
 * 06/03/2013       tcheng      145238 - Modified generateMailingEvent to remove dataBean and set attribute with mailing
 *                                       event recordSet.
 * 11/19/2015       eyin        167171 - Modified loadAllMailingRecipient(), Add logic to process when recordSet is null.
 * ---------------------------------------------------
 */

public class MaintainPolicyMailingAction extends PMBaseAction {
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
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadAllPolicyMailing(mapping, form, request, response);
    }

    /**
     * Method to load list of available mailing event and mailing attribute.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllPolicyMailing(ActionMapping mapping, ActionForm form,
                                              HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllPolicyMailing", new Object[]{mapping, form, request, response});
        String forwardString = "loadResultForMainMenu";
        try {
            securePage(request, form);
            Record inputRecord = getInputRecord(request);
            Record record = generateInputRecord(inputRecord);
            RecordSet rs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            if (rs == null) {
                rs = getPolicyMailingManager().loadAllMailingEvent(record);
            }
            // Set loaded mailing event data into request
            setDataBean(request, rs);
            // Load the group detail
            RecordSet mailingAttrbuteRs = (RecordSet) request.getAttribute(MAILING_ATTRIBUTE_RECORD_SET);
            if (mailingAttrbuteRs == null) {
                mailingAttrbuteRs = getPolicyMailingManager().loadAllMailingAttribute(inputRecord);
            }
            // Set loaded mailing attribute data into request
            setDataBean(request, mailingAttrbuteRs, MAILING_ATTRIBUTE_GRID_ID);
            // Make the Summary Record available for output
            Record output = rs.getSummaryRecord();
            output.setFields(mailingAttrbuteRs.getSummaryRecord(), false);
            output.setFields(inputRecord, true);
            if (output.hasFieldValue("toBeSelectedMailingEvent")) {
                request.setAttribute("toBeSelectedMailingEvent", output.getStringValue("toBeSelectedMailingEvent"));
            }
            if ((output.hasStringValue("pageType"))&&(("popup").equals(output.getStringValue("pageType")))) {
                request.setAttribute("pageType", "popup");
                forwardString = "loadResultForPopUp";
            }
            else{
                request.setAttribute("pageType","noPopup");
            }
            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);
            loadGridHeader(request);
            // Set currentGridId to mailingAttributeListGrid before load mailing attribute gird header
            setCurrentGridId(MAILING_ATTRIBUTE_GRID_ID);
            // Load the list of values after loading the data
            loadListOfValues(request, form);
            // Load shared detail grid header
            loadGridHeader(request, null, MAILING_ATTRIBUTE_GRID_ID, MAILING_ATTRIBUTE_GRID_LAYER_ID);
            // Populate messages for javascirpt
            addJsMessages();


        }
        catch (Exception e) {
            if (request.getParameter("pageType") != null && request.getParameter("pageType").equals("popup")) {
                forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the policy mailing page.", e, request, mapping);
            }
            else {
                forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the policy mailing page.", e, request, mapping);
            }
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllPolicyMailing", af);
        return af;
    }

    /**
     * Method to clear mailing event and mailing attribute.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward clearAllPolicyMailing(ActionMapping mapping, ActionForm form,
                                               HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "clearAllPolicyMailing", new Object[]{mapping, form, request, response});
        String forwardString = "loadResultForMainMenu";
        try {
            securePage(request, form);
            Record inputRecord = getInputRecord(request);
            inputRecord.setFieldValue("policyNum", "");
            inputRecord.setFieldValue("startSearchDate", getMonthFirstDate(new Date()));
            inputRecord.setFieldValue("endSearchDate", getMonthLastDate(new Date()));
            inputRecord.setFieldValue("queryId", "");
            Record record = generateInputRecord(inputRecord);
            //set policyno to "0" so make the rs empty.
            record.setFieldValue("policyNum", "0");
            // RecordSet rs = getEmptyRecordSet();
            RecordSet rs = getPolicyMailingManager().loadAllMailingEvent(record);
            // Set loaded mailing event data into request
            setDataBean(request, rs);
            // Load the group detail
            // RecordSet mailingAttrbuteRs =getEmptyRecordSet();
            RecordSet mailingAttrbuteRs = getPolicyMailingManager().loadAllMailingAttribute(record);
            // Set loaded mailing attribute data into request
            setDataBean(request, mailingAttrbuteRs, MAILING_ATTRIBUTE_GRID_ID);
              if ((inputRecord.hasStringValue("pageType"))&&(("popup").equals(inputRecord.getStringValue("pageType")))) {
                request.setAttribute("pageType", "popup");
                forwardString = "loadResultForPopUp";
            }
            else{
                request.setAttribute("pageType","noPopup");
            }
            publishOutputRecord(request, inputRecord);
            loadGridHeader(request);
            // Set currentGridId to mailingAttributeListGrid before load mailing attribute gird header
            setCurrentGridId(MAILING_ATTRIBUTE_GRID_ID);
            // Load the list of values after loading the data
            loadListOfValues(request, form);
            // Load shared detail grid header
            loadGridHeader(request, null, MAILING_ATTRIBUTE_GRID_ID, MAILING_ATTRIBUTE_GRID_LAYER_ID);
            // Populate messages for javascirpt
            addJsMessages();
        }
        catch (Exception e) {
            if (request.getParameter("pageType") != null && request.getParameter("pageType").equals("popup")) {
                forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to clear the policy mailing page.", e, request, mapping);
            }
            else {
                forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to clear the policy mailing page.", e, request, mapping);
            }
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "clearAllPolicyMailing", af);
        return af;
    }

    /**
     * load all mailing recipient(for bottom grid)
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward loadAllMailingRecipient(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllMailingRecipient", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadMailingRecipient";
        try {
            securePage(request, form);
            Record inputRecord = getInputRecord(request);
            RecordSet rs = getPolicyMailingManager().loadAllMailingRecipient(inputRecord);
            // Set loaded change detail data into request
            setDataBean(request, rs, MAILING_RECIPIENT_GRID_ID);

            //initialize header title for result list
            String resultHeader = "pm.maintainPolicyMailing.RecipientList.header";
            String resultHeaderMsg = MessageManager.getInstance().formatMessage(resultHeader);
            if (rs != null && rs.getSize()>0 &&
                rs.getFirstRecord().hasField("maxRows")){
                int intTotalRowsReturned = rs.getSize();
                int intMaxRowsConfigured = Integer.parseInt(rs.getFirstRecord().getStringValue("maxRows"));
                if(intTotalRowsReturned >= intMaxRowsConfigured){
                    resultHeader = "pm.maintainPolicyMailing.RecipientList.abortSort.header";
                    resultHeaderMsg = MessageManager.getInstance().formatMessage(resultHeader,
                        new String[]{String.valueOf(intMaxRowsConfigured)});
                }
            }            
            request.setAttribute(RESULT_LIST_HEADER, resultHeaderMsg);

            // Make the Summary Record available for output
            Record output = null;
            if (rs != null) {
                output = rs.getSummaryRecord();
            }
            else {
                output = new Record();
            }
            output.setFields(inputRecord);
            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);
            setCurrentGridId(MAILING_RECIPIENT_GRID_ID);
            loadGridHeader(request, null, MAILING_RECIPIENT_GRID_ID, MAILING_RECIPIENT_GRID_LAYER_ID);
            loadListOfValues(request, form);
            addJsMessages();
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the mailing recipient.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllMailingRecipient", af);
        }
        return af;
    }

    /**
     * Save policy mailing record(maing event and mailing recipient).
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward createPolicyMailingForPolicy(ActionMapping mapping, ActionForm form,
                                                      HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "createPolicyMailingForPolicy", new Object[]{mapping, form, request, response});
        try {

            securePage(request, form, false);
            Record inputRecord = getInputRecord(request);
            String policyMailingId = getPolicyMailingManager().createPolicyMailingFromPolicy(inputRecord);
            setCurrentGridId(MAILING_EVENT_GRID_ID);

            Record output = new Record();
            MailingEventFields.setPolicyMailingId(output, policyMailingId);

            // Send back xml data
            writeAjaxXmlResponse(response, output);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR,
                "Failed to create policy mailing from policy.", e, response);
        }
        // Return the forward
        ActionForward af = null;
        l.exiting(getClass().getName(), "createPolicyMailingForPolicy", af);
        return af;
    }

    /**
     * Save all updated policy mailing records.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward saveAllPolicyMailing(ActionMapping mapping, ActionForm form,
                                              HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveAllPolicyMailing", new Object[]{mapping, form, request, response});

        RecordSet mailingEventRecords = null;
        RecordSet mailingAttributeRecords = null;
        RecordSet mailingRecipientRecords = null;
        String forwardString = "saveResult";
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);
                // First save shared group records
                // Map mailing event textXML to RecordSet for input
                mailingEventRecords = getInputRecordSet(request, MAILING_EVENT_GRID_ID);
                setCurrentGridId(MAILING_ATTRIBUTE_GRID_ID);
                // Map mailingAttribute textXML to RecordSet for input
                mailingAttributeRecords = getInputRecordSet(request, MAILING_ATTRIBUTE_GRID_ID);
                //Map mailingAttribute textXML to recordSet for input
                setCurrentGridId(MAILING_RECIPIENT_GRID_ID);
                mailingRecipientRecords = getInputRecordSet(request, MAILING_RECIPIENT_GRID_ID);
                // Save all changes
                getPolicyMailingManager().saveAllPolicyMailing(mailingEventRecords, mailingAttributeRecords, mailingRecipientRecords);
                // Set back to mailingEventListGrid
                setCurrentGridId(MAILING_EVENT_GRID_ID);
                // request.setAttribute("toBeSelectedMailingEvent", mailingEventRecords.getRecord(0).getStringValue("policyMailingId"));
            }
        }
        catch (ValidationException ve) {
            // Save the input records into request
            setCurrentGridId(MAILING_EVENT_GRID_ID);
            request.setAttribute(RequestIds.GRID_RECORD_SET, mailingEventRecords);
            request.setAttribute(MAILING_ATTRIBUTE_RECORD_SET, mailingAttributeRecords);
            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to save the limit Sharing page.", e, request, mapping);
        }

        // Return the forward
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveAllPolicyMailing", af);
        return af;
    }


    /**
     * Get Inital Values for new added mailing event
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward getInitialValuesForMailingEvent(ActionMapping mapping,
                                                         ActionForm form,
                                                         HttpServletRequest request,
                                                         HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForMailingEvent",
            new Object[]{mapping, form, request, response});

        try {
            securePage(request, form);
            // Get inital values
            Record record = getPolicyMailingManager().getInitialValuesForMailingEvent();
            new MailingEventEntitlementRecordLoadProcessor().setInitialEntitlementValuesForMailingEvent(record);
            // Get LOV labels for initial values
            publishOutputRecord(request, record);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, record);
            // Send back xml data
            writeAjaxXmlResponse(response, record);

        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values for mailing event.", e, response);
        }
        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForMailingEvent", af);
        return af;

    }

    /**
     * Get Inital Values for new added mailing attribute
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward getInitialValuesForMailingAttribute(ActionMapping mapping,
                                                             ActionForm form,
                                                             HttpServletRequest request,
                                                             HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForMailingAttribute",
            new Object[]{mapping, form, request, response});

        try {
            securePage(request, form);
            // Get inital values
            Record inputRecord = getInputRecord(request);
            Record record = getPolicyMailingManager().getInitialValuesForMailingAttribute(inputRecord);
            // Get LOV labels for initial values
            publishOutputRecord(request, record);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, record);
            setCurrentGridId(MAILING_ATTRIBUTE_GRID_ID);
            // Send back xml data
            writeAjaxXmlResponse(response, record);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values for mailing attribute.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForMailingAttribute", af);
        return af;
    }


    /**
     * Get Inital Values for new added mailing recipient
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward getInitialValuesForMailingRecipient(ActionMapping mapping,
                                                             ActionForm form,
                                                             HttpServletRequest request,
                                                             HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForMailingRecipient",
            new Object[]{mapping, form, request, response});

        try {
            securePage(request, form);
            // Get inital values
            Record inputRecord = getInputRecord(request);
            Record record = getPolicyMailingManager().getInitialValuesForMailingRecipient(inputRecord);

            // Get LOV labels for initial values
            publishOutputRecord(request, record);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, record);
            setCurrentGridId(MAILING_RECIPIENT_GRID_ID);
            // Send back xml data
            writeAjaxXmlResponse(response, record);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values for mailing recipient.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForMailingRecipient", af);
        return af;
    }

    /**
     * This method is used to validate the policyNo for mailing recipient is valid or not
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward validateMailingRecipient(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                  HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "validateMailingRecipient",
            new Object[]{mapping, form, request, response});

        try {
            Record inputRecord = getInputRecord(request);
            Record record = getPolicyMailingManager().validateMailingRecipient(inputRecord);
            if ("Y".equals(record.getStringValue("errorFlag"))) {
                MessageManager.getInstance().addErrorMessage("pm.policyMailing.validateRecipient.error", new Object[]{inputRecord.getStringValue("policyNo")});
                throw new ValidationException();
            }
            writeAjaxXmlResponse(response, record);
        }
        catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to validate mailing recipient", e, response);
        }
        ActionForward af = null;
        l.exiting(getClass().getName(), "validateMaiingRecipient", af);
        return af;
    }

    /**
     * This method is used to get resend days by selected productMailingResendId   (for ajax call)
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getResendDaysBySelectedResend(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                       HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getResendDaysBySelectedResend",
            new Object[]{mapping, form, request, response});

        try {
            Record inputRecord = getInputRecord(request);
            Record record = getPolicyMailingManager().getResendDaysBySelectedResend(inputRecord);
            writeAjaxXmlResponse(response, record);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get resend days", e, response);
        }
        ActionForward af = null;
        l.exiting(getClass().getName(), "getResendDaysBySelectedResend", af);
        return af;
    }

    public ActionForward invokeProcessMsg(ActionMapping mapping, ActionForm form,
                                          HttpServletRequest request, HttpServletResponse response) throws Exception {

        Logger l = LogUtils.enterLog(getClass(), "invokeProcessMsg", new Object[]{mapping, form, request, response});
        securePage(request, form);
        String fromButton = request.getParameter("fromButton");
        if (fromButton.equals("reprint")) {
            request.setAttribute("process", "reprintMailingEvent");
            MessageManager.getInstance().addInfoMessage("pm.maintainPolicyMailing.reprintProcess.info");
        }
        else if (fromButton.equals("generate")) {
            request.setAttribute("process", "generateMailingEvent");
            MessageManager.getInstance().addInfoMessage("pm.maintainPolicyMailing.generateProcess.info");
        }
        else if (fromButton.equals("afterCheckPastMailing")) {
            request.setAttribute("process", "generateMailingEventAfterCheckPast");
            request.setAttribute("selectedMailingDtls", request.getParameter("selectedMailingDtls"));
            MessageManager.getInstance().addInfoMessage("pm.maintainPolicyMailing.generateProcess.info");
        }
        String forwardString = "processMsg";
        String policyMailingId = request.getParameter("policyMailingId");
        request.setAttribute("policyMailingId", policyMailingId);
        // Return the forward
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "invokeProcessMsg", af);
        return af;
    }

    /**
     * generate mailing event
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward generateMailingEvent(ActionMapping mapping, ActionForm form,
                                              HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "generateMailingEvent", new Object[]{mapping, form, request, response});
        String forwardString = "generateResult";

        try {
            securePage(request, form);
            Record inputRecord = getInputRecord(request);
            int count = getPolicyMailingManager().checkPastMailing(inputRecord);
            if (count > 0) {
                forwardString = "loadPastMailingPolicy";
                request.setAttribute("policyMailingId", inputRecord.getStringValue("policyMailingId"));
            }
            else {
                RecordSet rs = getPolicyMailingManager().generateMailingEvent(inputRecord);
                request.setAttribute(MAILING_ERROR_RECORD_SET, rs);
                request.setAttribute("policyMailingId", inputRecord.getStringValue("policyMailingId"));
            }
        }

        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to generate mailing event.", e, request, mapping);
        }
        // Return the forward
        ActionForward af = mapping.findForward(forwardString);

        l.exiting(getClass().getName(), "generateMailingEvent", af);
        return af;
    }

    /**
     * generate mailing event after check past
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward generateMailingEventAfterCheckPast(ActionMapping mapping, ActionForm form,
                                                            HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "generateMailingEventAfterCheckPast", new Object[]{mapping, form, request, response});
        String forwardString = "generateResult";

        try {
            securePage(request, form);
            Record inputRecord = getInputRecord(request);
            //delete exluded policy
            String selectedMailingDtls = inputRecord.getStringValue("selectedMailingDtls");
            if (!StringUtils.isBlank(selectedMailingDtls)) {
                getPolicyMailingManager().deleteExludedPolicies(selectedMailingDtls);
            }
            RecordSet rs = getPolicyMailingManager().generateMailingEvent(inputRecord);
            setDataBean(request, rs);
            request.setAttribute("policyMailingId", inputRecord.getStringValue("policyMailingId"));
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to generate mailing event.", e, request, mapping);
        }
        // Return the forward
        ActionForward af = mapping.findForward(forwardString);

        l.exiting(getClass().getName(), "generateMailingEventAfterCheckPast", af);
        return af;
    }

    /**
     * reprint mailing event
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward reprintMailingEvent(ActionMapping mapping, ActionForm form,
                                             HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "reprintMailingEvent", new Object[]{mapping, form, request, response});
        String forwardString = "reprintResult";

        try {
            securePage(request, form);
            Record inputRecord = getInputRecord(request);

            int errorCount = getPolicyMailingManager().reprintMailingEvent(inputRecord);
            if (errorCount != 0) {
                MessageManager.getInstance().addErrorMessage("pm.maintainPolicyMailing.reprintFail.error");
            }
            else {
                MessageManager.getInstance().addInfoMessage("pm.maintainPolicyMailing.reprintSuccess.info");
            }
        }

        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to reprint mailing event.", e, request, mapping);
        }
        // Return the forward
        ActionForward af = mapping.findForward(forwardString);

        l.exiting(getClass().getName(), "reprintMailingEvent", af);
        return af;
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
        if (hasCurrentGridId()) {
            String currentGridId = getCurrentGridId();
            if (currentGridId.equals(MAILING_ATTRIBUTE_GRID_ID)) {
                anchorName = getMailingAttributeAnchorColumnName();
            }
            else if (currentGridId.equals(MAILING_RECIPIENT_GRID_ID)) {
                anchorName = getMailingRecipientAnchorColumnName();
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
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.maintainPolicyMailing.recipientNotFound.error");
        MessageManager.getInstance().addJsMessage("pm.maintainPolicyMailing.undeletedMailingAttribute.error");
        MessageManager.getInstance().addJsMessage("pm.maintainPolicyMailing.undeletedMailingRecipient.error");
        MessageManager.getInstance().addJsMessage("pm.maintainPolicyMailing.unsavedData.error");
        MessageManager.getInstance().addJsMessage("pm.maintainPolicyMailing.postGenNotAvailable.error");
        MessageManager.getInstance().addJsMessage("pm.maintainPolicyMailing.deleteAttributeAfterGenerated.error");
        MessageManager.getInstance().addJsMessage("pm.maintainPolicyMailing.fieldRequried.error");
        MessageManager.getInstance().addJsMessage("pm.maintainPolicyMailing.InvalidPolicyNo.error");
        MessageManager.getInstance().addJsMessage("pm.maintainPolicyMailing.unCompeletedData.error");
        MessageManager.getInstance().addJsMessage("pm.generatePolicyMailing.noPolicy.error");
        MessageManager.getInstance().addJsMessage("pm.maitainolicyMailing.unsavedData.error");
    }
    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getPolicyMailingManager() == null)
            throw new ConfigurationException("The required property 'limitSharingManager' is missing.");
        if (getMailingAttributeAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'mailingAttributeAnchorColumnName' is missing.");
        if (getMailingRecipientAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'mailingRecipientAnchorColumnName' is missing.");
        if (super.getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
    }

    public String getMailingAttributeAnchorColumnName() {
        return mailingAttributeAnchorColumnName;
    }

    public void setMailingAttributeAnchorColumnName(String mailingAttributeAnchorColumnName) {
        this.mailingAttributeAnchorColumnName = mailingAttributeAnchorColumnName;
    }

    public String getMailingRecipientAnchorColumnName() {
        return mailingRecipientAnchorColumnName;
    }

    public void setMailingRecipientAnchorColumnName(String mailingRecipientAnchorColumnName) {
        this.mailingRecipientAnchorColumnName = mailingRecipientAnchorColumnName;
    }

    public PolicyMailingManager getPolicyMailingManager() {
        return policyMailingManager;
    }

    public void setPolicyMailingManager(PolicyMailingManager policyMailingManager) {
        this.policyMailingManager = policyMailingManager;
    }


    public MaintainPolicyMailingAction() {
    }

    //get input record for search
    private Record generateInputRecord(Record inputRecord) {
        Record record = new Record();
        if (inputRecord.hasStringValue("fromPage")) {
            if (inputRecord.getFieldValue("fromPage").equals("processPolicyMailing")) {
                if (inputRecord.hasStringValue("startSearchDate")) {
                    record.setFieldValue("startSearchDate", inputRecord.getFieldValue("startSearchDate"));
                }
                else {
                    record.setFieldValue("startSearchDate", "01/01/1900");
                }
                if (inputRecord.hasStringValue("endSearchDate")) {
                    record.setFieldValue("endSearchDate", inputRecord.getFieldValue("endSearchDate"));
                }
                else {
                    record.setFieldValue("endSearchDate", "01/01/3000");
                }
            }
        }
        else {
            String currentMonthFirstDate = DateUtils.formatDate(getMonthFirstDate(new Date()));
            String currentMonthLastDate = DateUtils.formatDate(getMonthLastDate(new Date()));
            record.setFieldValue("startSearchDate", currentMonthFirstDate);
            record.setFieldValue("endSearchDate", currentMonthLastDate);
            inputRecord.setFieldValue("startSearchDate", currentMonthFirstDate);
            inputRecord.setFieldValue("endSearchDate", currentMonthLastDate);
        }
        if (inputRecord.hasStringValue("queryId")) {
            record.setFieldValue("productMailingId", inputRecord.getFieldValue("queryId"));
        }
        else {
            record.setFieldValue("productMailingId", "");
        }
        if (inputRecord.hasStringValue("policyNum")) {
            record.setFieldValue("policyNum", inputRecord.getFieldValue("policyNum"));
        }
        else {
            record.setFieldValue("policyNum", "");
        }
        return record;


    }

    //get month fist date
    private Date getMonthFirstDate(Date date) {
        int year = DateUtils.getYear(date);
        int month = DateUtils.getMonth(date);
        return DateUtils.makeDate(year, month, 1);
    }

    // get month last date
    private Date getMonthLastDate(Date date) {
        int year = DateUtils.getYear(date);
        int month = DateUtils.getMonth(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int maxDate = cal.getActualMaximum(Calendar.DATE);
        return DateUtils.makeDate(year, month, maxDate);
    }


    protected static final String MAILING_EVENT_GRID_ID = "mailingEventListGrid";
    protected static final String MAILING_ATTRIBUTE_GRID_ID = "mailingAttributeListGrid";
    protected static final String MAILING_RECIPIENT_GRID_ID = "mailingRecipientListGrid";
    protected static final String MAILING_ATTRIBUTE_GRID_LAYER_ID = "PM_MAILING_ATTRIBUTE_GH";
    protected static final String MAILING_RECIPIENT_GRID_LAYER_ID = "PM_MAILING_RECIPIENT_GH";
    protected static final String MAILING_ATTRIBUTE_RECORD_SET = "mailingAttributeGridRecordSet";
    protected static final String MAILING_ERROR_RECORD_SET = "mailingErrorRecordSet";

    private PolicyMailingManager policyMailingManager;
    private String mailingAttributeAnchorColumnName;
    private String mailingRecipientAnchorColumnName;
    protected static final String RESULT_LIST_HEADER = "resultHeader";
}
