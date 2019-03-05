package dti.pm.renewalquestionnairemgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.struts.IOasisAction;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.oasis.util.LogUtils;
import dti.oasis.util.PageBean;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.pm.busobjs.SysParmIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.renewalquestionnairemgr.QuestionnaireResponseFields;
import dti.pm.renewalquestionnairemgr.RenewalQuestionnaireManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Action class for Renewal Questionnaire Response.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   July 08, 2008
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/03/2011       ryzhao      117394 - Modified loadAllQuestionnaireResponse() to hide the entire response section
 *                                       if sysparm PM_WEB_URL is NULL or undefined.
 * 05/06/2011       ryzhao      117394 - Updated per Joe's comments.
 * 06/13/2018       wrong       192557 - Modified saveResponseDate() and saveResponses() to call hasValidSaveToken()
 *                                       to be used for CSRFInterceptor.
 * ---------------------------------------------------
 */

public class RenewalQuestionnaireResponseAction extends PMBaseAction {
    /**
     * This method is triggered automatically when there is no process parameter sent in along the requested url.
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
        return loadAllQuestionnaireResponse(mapping, form, request, response);
    }

    /**
     * Load renewal questionnaire response page.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllQuestionnaireResponse(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllQuestionnaireResponse", new Object[]{mapping, form, request, response});
        String forwardString = "loadQuestionnaireResponse";
        Record inputRecord = null;
        Record outputRecord = new Record();
        try {
            // Secures access to the page, loads the oasis fields and map the input parameters into the fields.
            securePage(request, form);
            // Get all request parameters.
            inputRecord = getInputRecord(request);
            // Check this page is opened from which place.
            if (!QuestionnaireResponseFields.getSaveResponse(inputRecord).booleanValue()) {
                if (inputRecord.hasStringValue(QuestionnaireResponseFields.POLICY_NO_CRITERIA) &&
                    inputRecord.hasStringValue(QuestionnaireResponseFields.POLICY_RENEW_FORM_ID)) {
                    if (QuestionnaireResponseFields.getSearchDate(inputRecord).booleanValue()) {
                        QuestionnaireResponseFields.setComeFromMailingEvent(inputRecord, YesNoFlag.N);
                    }
                    else {
                        QuestionnaireResponseFields.setComeFromMailingEvent(inputRecord, YesNoFlag.Y);
                    }
                }
                else {
                    outputRecord.setFieldValue("loadResponse", YesNoFlag.Y);
                }
            }
            /** If the page is opened from mailing event, change the page title and set the search fields are not required.
             *  Actually, all fields are required if the form is visible.In some custom environment risk is not required,
             *  so these fields can't be validated via Java code and if the form is invisible, these fields are not required.
             */
            if (QuestionnaireResponseFields.getComeFromMailingEvent(inputRecord).booleanValue()) {
                forwardString = "loadQuestionnaireResponsePopup";
//                PageBean pageBean = (PageBean) request.getAttribute(IOasisAction.KEY_PAGEBEAN);
//                String title = pageBean.getTitle() + MessageManager.getInstance().formatMessage("pm.renewalQuestionnaireResponse.policyNo");
//                pageBean.setTitle(title + QuestionnaireResponseFields.getPolicyNoCriteria(inputRecord));
                OasisFields fieldsMap = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);
                OasisFormField policyNoField = fieldsMap.getField(QuestionnaireResponseFields.POLICY_NO_CRITERIA);
                OasisFormField riskIdField = fieldsMap.getField(QuestionnaireResponseFields.RISK_ID);
                OasisFormField questionnaireField = fieldsMap.getField(QuestionnaireResponseFields.POLICY_RENEW_FORM_ID);
                if (policyNoField != null) {
                    policyNoField.setIsRequired(false);
                }
                if (riskIdField != null) {
                    policyNoField.setIsRequired(false);
                }
                if (questionnaireField != null) {
                    policyNoField.setIsRequired(false);
                }
            }
            // If sysparm PM_WEB_URL is NULL or undefined, hide the entire response section
            String webUrl = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_WEB_URL);
            if (StringUtils.isBlank(webUrl)) {
                request.setAttribute(QuestionnaireResponseFields.SHOW_RESPONSE_SECTION, YesNoFlag.N);
                QuestionnaireResponseFields.setShowResponseSection(inputRecord, YesNoFlag.N);
            }
            if (!outputRecord.hasStringValue("loadResponse")) {
                // Get output Record.
                if (!QuestionnaireResponseFields.getSearchDateError(inputRecord).booleanValue()) {
                    outputRecord = getRenewalQuestionnaireManager().getInitialValuesForQuestionnaireResponse(inputRecord);
                    // If the record is not a new one and comes from mailing event, add the hidden parameters into request.
                    if (!QuestionnaireResponseFields.getIsNewDate(outputRecord).booleanValue() &&
                        QuestionnaireResponseFields.getComeFromMailingEvent(inputRecord).booleanValue()) {
                        request.setAttribute(QuestionnaireResponseFields.POLICY_NO_CRITERIA, QuestionnaireResponseFields.getPolicyNoCriteria(outputRecord));
                        request.setAttribute(QuestionnaireResponseFields.RISK_ID, QuestionnaireResponseFields.getRiskId(outputRecord));
                        request.setAttribute(QuestionnaireResponseFields.POLICY_RENEW_FORM_ID, QuestionnaireResponseFields.getPolicyRenewalFormId(outputRecord));
                    }
                }
                // Set the three parameters to request.
                QuestionnaireResponseFields.setComeFromMailingEvent(outputRecord, QuestionnaireResponseFields.getComeFromMailingEvent(inputRecord));
                QuestionnaireResponseFields.setSearchDate(outputRecord, QuestionnaireResponseFields.getSearchDate(inputRecord));
                QuestionnaireResponseFields.setReopenResponse(outputRecord, QuestionnaireResponseFields.getReopenResponse(inputRecord));
            }
            else {
                request.setAttribute("loadResponse", YesNoFlag.Y);
            }
            // publish page field
            publishOutputRecord(request, outputRecord);
            // Loads list of values
            loadListOfValues(request, form);
            // Add Js messages
            addJsMessages();
        }
        catch (ValidationException v) {
            QuestionnaireResponseFields.setSearchDateError(inputRecord, YesNoFlag.Y);
            forwardString = "searchResponseDate";
            // Handle the validation exception
            handleValidationException(v, request);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR,
                "Failed to load the Renewal Questionnaire Response page.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllQuestionnaireResponse", null);
        return af;
    }

    /**
     * Save the dates.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward saveResponseDate(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveResponseDate", new Object[]{mapping, form, request, response});
        Record inputRecord;
        try {
            if (hasValidSaveToken(request)) {
                // Secures access to the page.
                securePage(request, form, false);
                // Get the changed questionnaire(s) from the recordset
                inputRecord = getInputRecord(request);
                // Save the dates.
                Record record = getRenewalQuestionnaireManager().saveResponseDate(inputRecord);
                writeAjaxXmlResponse(response, record, true);
            }
        }
        catch (ValidationException v) {
            // Handle the validation exception
            handleValidationExceptionForAjax(v, response);
        }
        catch (Exception e) {
            handleErrorForAjax("pm.renewalQuestionnaireResponse.saveData.error", "Save response date failed", e, response);
        }
        l.exiting(getClass().getName(), "saveResponseDate");
        return null;
    }

    /**
     * Save the responses.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward saveResponses(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveResponses", new Object[]{mapping, form, request, response});
        String forwardString = "saveResponse";
        Record inputRecord = null;
        try {
            if (hasValidSaveToken(request)) {
                // Secures access to the page, loads the oasis fields and map the input parameters into the fields.
                securePage(request, form);
                // Get all request parameters into a record.
                inputRecord = getInputRecord(request);
                getRenewalQuestionnaireManager().saveResponses(inputRecord);
                getRenewalQuestionnaireManager().saveAppStatus(inputRecord);
                QuestionnaireResponseFields.setSaveResponse(inputRecord, YesNoFlag.Y);
                MessageManager.getInstance().addInfoMessage("pm.renewalQuestionnaireResponse.saveResposne.success");
                // publish page field
                publishOutputRecord(request, inputRecord);
                // Loads list of values
                loadListOfValues(request, form);
                // Add Js messages
                addJsMessages();
            }
        }
        catch (ValidationException v) {
            QuestionnaireResponseFields.setSearchDateError(inputRecord, YesNoFlag.Y);
            // Handle the validation exception
            handleValidationException(v, request);
        }
        catch (Exception e) {
            forwardString = handleError("pm.renewalQuestionnaireResponse.saveData.error", "Save responses date failed", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveResponses", null);
        return af;
    }

    /**
     * Get the return value of data change of the current page.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward getDataChanged(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getDataChanged", new Object[]{mapping, form, request, response});
        Record inputRecord;
        try {
            // Secures access to the page.
            securePage(request, form, false);
            // Get the changed questionnaire(s) from the recordset
            inputRecord = getInputRecord(request);
            // Save date.
            YesNoFlag returnFlag = getRenewalQuestionnaireManager().isDataChanged(inputRecord);
            Record record = new Record();
            QuestionnaireResponseFields.setDataChanged(record, returnFlag);
            writeAjaxXmlResponse(response, record, true);
        }
        catch (ValidationException v) {
            // Handle the validation exception
            handleValidationExceptionForAjax(v, response);
        }
        catch (Exception e) {
            handleErrorForAjax("pm.renewalQuestionnaireResponse.saveData.error", "Get data changed failed", e, response);
        }
        l.exiting(getClass().getName(), "getDataChanged");
        return null;
    }

    /**
     * Reopen the renewal questionnaire response.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward reopenRenewQuestionnaireResponse(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "reopenRenewQuestionnaireResponse", new Object[]{mapping, form, request, response});
        String forwardString = "reopenResponse";
        Record inputRecord;
        try {
            // Secures access to the page, loads the oasis fields and map the input parameters into the fields.
            securePage(request, form);
            // Get all request parameters into a record.
            inputRecord = getInputRecord(request);
            // This parameter will be used when save app status.
            QuestionnaireResponseFields.setReopenResponse(inputRecord, YesNoFlag.Y);
            // Set app status.
            getRenewalQuestionnaireManager().saveAppStatus(inputRecord);
            // This parameter should be set N after reopening.
            QuestionnaireResponseFields.setReopenResponse(inputRecord, YesNoFlag.N);
            // publish page field.
            publishOutputRecord(request, inputRecord);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR,
                "Failed to reopen the Renewal Questionnaire Response page.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "reopenRenewQuestionnaireResponse", null);
        return af;
    }

    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.renewalQuestionnaireResponse.saveDate.success");
        MessageManager.getInstance().addJsMessage("pm.renewalQuestionnaireMailingEvent.save.changed");
    }

    /**
     * Verify RenewalQuestionnaireManager and anchorColumnName in spring config
     */
    public void verifyConfig() {
        if (getRenewalQuestionnaireManager() == null)
            throw new ConfigurationException("The required property 'renewalQuestionnaireManager' is missing.");
    }

    public RenewalQuestionnaireManager getRenewalQuestionnaireManager() {
        return m_renewalQuestionnaireManager;
    }

    public void setRenewalQuestionnaireManager(RenewalQuestionnaireManager renewalProcessManager) {
        m_renewalQuestionnaireManager = renewalProcessManager;
    }

    private RenewalQuestionnaireManager m_renewalQuestionnaireManager;
}
