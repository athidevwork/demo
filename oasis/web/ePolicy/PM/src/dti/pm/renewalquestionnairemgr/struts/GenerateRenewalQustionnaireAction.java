package dti.pm.renewalquestionnairemgr.struts;

import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.session.UserSessionManager;
import dti.oasis.struts.IOasisAction;
import dti.oasis.tags.OasisFields;
import dti.oasis.util.LogUtils;
import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.renewalquestionnairemgr.RenewalQuestionnaireFields;
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
 * Action class for Generate Renewal Questionnaire.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 14, 2007
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/19/2015       eyin        167171 - Modified loadAllRenewalQuestionnaire(), Add logic to process when inputRecord is null.
 * ---------------------------------------------------
 */

public class GenerateRenewalQustionnaireAction extends PMBaseAction {
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
        return loadAllRenewalQuestionnaire(mapping, form, request, response);
    }

    /**
     * 1)Initialize generate renewal questionnaire page.
     * 2)If the parameter searchQuestionnaire exists, this method uses to search questionnaires.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllRenewalQuestionnaire(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllRenewalQuestionnaire", new Object[]{mapping, form, request, response});
        String forwardString = "loadRenewalQuestion";
        Record inputRecord = null;
        try {
            // Secures access to the page, loads the oasis fields and map the input parameters into the fields.
            securePage(request, form);
            // Get all request parameters into a record
            inputRecord = getInputRecord(request);
            // Get the RecordSet
            RecordSet rs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            if (rs == null) {
                rs = getRenewalQuestionnaireManager().loadAllRenewalQuestionnaire(inputRecord);
                // If RecordSet's size is larger than maxRow,system displays a information.
                if (rs.getSize() > 0) {
                    int maxSize = rs.getRecord(0).getIntegerValue("maxRows").intValue();
                    if (maxSize != -1 && rs.getSize() >= maxSize) {
                        MessageManager.getInstance().addInfoMessage("pm.generateRenewalQuestionnaire.maxQuestionnaire",
                            new String[]{String.valueOf(maxSize)});
                    }
                }
            }
            Record sumRecord = rs.getSummaryRecord();
            if (YesNoFlag.getInstance(RenewalQuestionnaireFields.getSearchQuestionnaire(inputRecord)).booleanValue()) {
                // In the case of search successfully, if system determines no questionnaire then add an error message.
                if (rs.getSize() <= 0) {
                    MessageManager.getInstance().addErrorMessage("pm.generateRenewalQuestionnaire.noQuestionnaire");
                }
            }
            else if(!YesNoFlag.getInstance(RenewalQuestionnaireFields.getSearchException(inputRecord)).booleanValue()){
                // When the page is loaded at the first time , the page fields should be initialized.
                String[] fieldList = new String[]{
                    RenewalQuestionnaireFields.SECOND_MAILING_DATE,
                    RenewalQuestionnaireFields.THIRD_MAILING_DATE,
                    RenewalQuestionnaireFields.DEADLINE_DATE};
                OasisFields fieldsMap = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);
                initializeEntitlementFieldFromOasisField(sumRecord, fieldList, fieldsMap);
                // If the mailing date are visible,they should be initialized.
                Record returnRecord = getRenewalQuestionnaireManager().getInitialValuesForRenewalQuestionnaire(sumRecord);
                sumRecord.setFields(returnRecord);
            }
            // publish page field
            publishOutputRecord(request, sumRecord);
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
            // When system throws a ValidationException,the parameter should be set false.
            forwardString = "searchRenewalQuestion";
            // In practise, inputRecord could not be null here, set default just in case.
            inputRecord = inputRecord == null ? new Record() : inputRecord;
            // Set search exception.
            RenewalQuestionnaireFields.setSearchException(inputRecord,YesNoFlag.Y);
            if(YesNoFlag.getInstance(RenewalQuestionnaireFields.getSearchQuestionnaire(inputRecord)).booleanValue()){
               RenewalQuestionnaireFields.setSearchQuestionnaire(inputRecord,YesNoFlag.N);
            }
            // Set a new inputRecords to request.
            RecordSet inputRecords = new RecordSet();
            inputRecords.setSummaryRecord(inputRecord);
            List fieldNameList = new ArrayList();
            fieldNameList.add(RenewalQuestionnaireFields.RENEW_FORM_ID);
            inputRecords.addFieldNameCollection(fieldNameList);
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);
            // Handle the validation exception
            handleValidationException(v, request);
        }
        catch (Exception e) {
            forwardString = handleError("pm.generateRenewalQuestionnaire.system.error",
                "Failed to load the Generate Renewal Questionnaire page.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllRenewalQuestionnaire", null);
        return af;
    }

    /**
     * Generate the selected questionnaires.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward generateRenewalQuestionnaire(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "generateRenewalQuestionnaire", new Object[]{mapping, form, request, response});
        RecordSet inputRecords;
        try {
            // Secures access to the page.
            securePage(request, form, false);
            // Get the selected questionnaire(s) from the recordset
            inputRecords = getInputRecordSet(request);
            // Get the recordset which stores the generated return message.
            RecordSet rs = getRenewalQuestionnaireManager().generateRenewalQuestionnaire(inputRecords);
            // Add the recordset to usersession.
            UserSessionManager.getInstance().getUserSession().set(RenewalQuestionnaireFields.GENERATE_QUESTIONNAIRE_INFORMATION, rs);
            Record record = new Record();
            record.setFieldValue(RenewalQuestionnaireFields.GENERATE_QUESTIONNAIRE, YesNoFlag.Y);
            writeAjaxXmlResponse(response, record, true);
        }
        catch (ValidationException v) {
            // Handle the validation exception
            handleValidationExceptionForAjax(v, response);
        }
        catch (Exception e) {
            handleErrorForAjax("pm.generateRenewalQuestionnaire.system.error", "generate renewal questionnaire failed", e, response);
        }
        l.exiting(getClass().getName(), "generateRenewalQuestionnaire");
        return null;
    }

    /**
     * When underwriter changes the End Search Date, system reset the Deadline Date.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getDefaultDeadlineDate(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getDefaultDeadlineDate", new Object[]{mapping, form, request, response});
        Record inputRecord;
        try {
            // Secures access to the page, loads the oasis fields and map the input parameters into the fields.
            securePage(request, form, false);
            // Get input record
            inputRecord = getInputRecord(request);
            // If the deadlineDate is visible, then set it to the Deadline Date.
            String deadlineDate = "";
            if (YesNoFlag.getInstance(RenewalQuestionnaireFields.getDeadlineDateAvailable(inputRecord)).booleanValue()) {
                deadlineDate = getRenewalQuestionnaireManager().getRenewalQuestionDefaultDeadlineDate(inputRecord);
            }
            Record record = new Record();
            RenewalQuestionnaireFields.setDeadlineDate(record, deadlineDate);
            writeAjaxXmlResponse(response, record, true);
        }
        catch (Exception e) {
            handleErrorForAjax("pm.generateRenewalQuestionnaire.system.error", "Reset deadline date failed", e, response);
        }
        l.exiting(getClass().getName(), "getDefaultDeadlineDate");
        return null;
    }

    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.generateRenewalQuestionnaire.abandonGenerateQuestionnaire");
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
