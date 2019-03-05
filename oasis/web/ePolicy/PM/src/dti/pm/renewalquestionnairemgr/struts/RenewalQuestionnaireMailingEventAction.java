package dti.pm.renewalquestionnairemgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.LogUtils;
import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.renewalquestionnairemgr.QuestionnaireMailingEventFields;
import dti.pm.renewalquestionnairemgr.RenewalQuestionnaireManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Action class for Renewal Questionnaire Mailing Event.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   June 13, 2008
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/13/2018       wrong       192557 - Modified saveAllMailingQuestionnaire() to call hasValidSaveToken() to be used
 *                                       for CSRFInterceptor.
 * ---------------------------------------------------
 */

public class RenewalQuestionnaireMailingEventAction extends PMBaseAction {
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
        return loadAllMailingEvent(mapping, form, request, response);
    }

    /**
     * 1)Initialize renewal questionnaire mailing event page.
     * 2)If the parameter searchMailingEvent exists, this method uses to search mailing event.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllMailingEvent(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllMailingEvent", new Object[]{mapping, form, request, response});
        String forwardString = "loadAllMailingEvent";
        Record inputRecord = null;
        try {
            // Secures access to the page, loads the oasis fields and map the input parameters into the fields.
            securePage(request, form);
            // Get all request parameters into a record
            inputRecord = getInputRecord(request);
            // Get the RecordSet
            RecordSet rs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            if (rs == null) {
                rs = getRenewalQuestionnaireManager().loadAllMailingEvent(inputRecord);
                // In the case of search successfully, if system determines no questionnaire then add an error message.
                if (QuestionnaireMailingEventFields.getSearchMailingEvent(inputRecord).booleanValue() &&
                    !QuestionnaireMailingEventFields.getFilterMailingEvent(inputRecord).booleanValue()) {
                    if (rs.getSize() <= 0) {
                        MessageManager.getInstance().addErrorMessage("pm.generateRenewalQuestionnaire.noQuestionnaire");
                    }
                }
                // If the filter recordSet's size not less than zero, system set a indicator.
                if (QuestionnaireMailingEventFields.getFilterMailingEvent(inputRecord).booleanValue()) {
                    if (rs.getSize() > 0) {
                        request.setAttribute(QuestionnaireMailingEventFields.FILTER_SUCCESS, YesNoFlag.Y);
                    }
                }
            }
            // publish page field
            publishOutputRecord(request, rs.getSummaryRecord());
            // Sets data bean
            setDataBean(request, rs);
            // Loads list of values
            loadListOfValues(request, form);
            // Load grid header bean
            loadGridHeader(request);
            // Add Js messages
            addJsMessages();
        }
        catch (ValidationException v) {
            forwardString = "searchMailingEvent";
            // Set a new inputRecords to request.
            RecordSet inputRecords = new RecordSet();
            inputRecords.setSummaryRecord(inputRecord);
            List fieldNameList = new ArrayList();
            fieldNameList.add("rownum");
            fieldNameList.add("isMailAvailable");
            inputRecords.addFieldNameCollection(fieldNameList);
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);
            // Handle the validation exception
            handleValidationException(v, request);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR,
                "Failed to load the Renewal Questionnaire Mailing Event page.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllMailingEvent", null);
        return af;
    }

    /**
     * Save the changed comment of selected mailing and questionnaire status.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward saveAllMailingQuestionnaire(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveAllMailingQuestionnaire", new Object[]{mapping, form, request, response});
        RecordSet inputRecords;
        try {
            if (hasValidSaveToken(request)) {
                // Secures access to the page.
                securePage(request, form, false);
                // Get the changed questionnaire(s) from the recordset
                inputRecords = getInputRecordSet(request);
                // Get the recordset which stores the return message.
                RecordSet rs = getRenewalQuestionnaireManager().processSaveAllMailingQuestionnare(inputRecords);
                Record record = new Record();
                if (rs.getSize() == 0) {
                    QuestionnaireMailingEventFields.setSaveQuestionInfo(record,"Y");
                }
                else {
                    // Add the recordset to usersession.
                    QuestionnaireMailingEventFields.setSaveQuestionInfo(record,"N");
                    UserSessionManager.getInstance().getUserSession().set(QuestionnaireMailingEventFields.QUESTION_ERROR_MESSAGE, rs);
                }
                writeAjaxXmlResponse(response, record, true);
            }
        }
        catch (ValidationException v) {
            // Handle the validation exception
            handleValidationExceptionForAjax(v, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Save mailing questionnaire failed", e, response);
        }
        l.exiting(getClass().getName(), "saveAllMailingQuestionnaire");
        return null;
    }

    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.renewalQuestionnaireMailingEvent.save.changed");
        MessageManager.getInstance().addJsMessage("pm.renewalQuestionnaireMailingEvent.save.continue");
        MessageManager.getInstance().addJsMessage("pm.renewalQuestionnaireMailingEvent.save.success");
        MessageManager.getInstance().addJsMessage("pm.renewalQuestionnaireMailingEvent.addRenewal.fail");
        MessageManager.getInstance().addJsMessage("pm.renewalQuestionnaireMailingEvent.print.unsavedDate");
        MessageManager.getInstance().addJsMessage("pm.renewalQuestionnaireMailingEvent.print.success");
        MessageManager.getInstance().addJsMessage("pm.renewalQuestionnaireMailingEvent.print.fail");
        MessageManager.getInstance().addJsMessage("pm.renewalQuestionnaireMailingEvent.print.options.empty");
        MessageManager.getInstance().addJsMessage("pm.renewalQuestionnaireMailingEvent.print.sendDate.empty");
    }
    
    /**
     * Verify RenewalQuestionnaireManager and anchorColumnName in spring config
     */
    public void verifyConfig() {
        if (getRenewalQuestionnaireManager() == null)
            throw new ConfigurationException("The required property 'renewalQuestionnaireManager' is missing.");
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
    }

    public RenewalQuestionnaireManager getRenewalQuestionnaireManager() {
        return m_renewalQuestionnaireManager;
    }

    public void setRenewalQuestionnaireManager(RenewalQuestionnaireManager renewalProcessManager) {
        m_renewalQuestionnaireManager = renewalProcessManager;
    }

    private RenewalQuestionnaireManager m_renewalQuestionnaireManager;

}
