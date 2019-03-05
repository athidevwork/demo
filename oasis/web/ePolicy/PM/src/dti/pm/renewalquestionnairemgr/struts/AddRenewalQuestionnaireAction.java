package dti.pm.renewalquestionnairemgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.renewalquestionnairemgr.QuestionnaireMailingEventFields;
import dti.pm.renewalquestionnairemgr.RenewalQuestionnaireManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Action class for Add Renewal Questionnaire.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   June 13, 2008
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/13/2018       wrong       192557 - Modified addRenewalQuestionnare() to call hasValidSaveToken() to be used for
 *                                       CSRFInterceptor.
 * ---------------------------------------------------
 */

public class AddRenewalQuestionnaireAction extends PMBaseAction {
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
        return getInitialValuesForAddRenewalQuestionnare(mapping, form, request, response);
    }

    /**
     * Get initial values for add a renewal questionnaire.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward getInitialValuesForAddRenewalQuestionnare(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitValueForAddRenewalQuestionnare", new Object[]{mapping, form, request, response});
        String forwardString = "addRenewalQuestion";
        Record inputRecord;
        try {
            // Secures access to the page, loads the oasis fields and map the input parameters into the fields.
            securePage(request, form);
            inputRecord = getInputRecord(request);
            request.setAttribute(QuestionnaireMailingEventFields.POL_REN_FRM_MASTER_ID, QuestionnaireMailingEventFields.getPolRenfrmMasterId(inputRecord));
            // Loads list of values
            loadListOfValues(request, form);
            // Add Js messages
            addJsMessages();
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR,
                "Failed to load the Add Renewal Questionnaire page.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "getInitValueForAddRenewalQuestionnare", null);
        return af;
    }

    /**
     * Add a renewal questionnaire.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward addRenewalQuestionnare(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "addRenewalQuestionnare", new Object[]{mapping, form, request, response});
        Record inputRecord;
        try {
            if (hasValidSaveToken(request)) {
                // Secures access to the page.
                securePage(request, form, false);
                // Get the selected questionnaire(s) from the recordset
                inputRecord = getInputRecord(request);
                // Get the recordset which stores the generated return message.
                getRenewalQuestionnaireManager().addRenewalQuestionnaire(inputRecord);
                Record record = new Record();
                QuestionnaireMailingEventFields.setAddQuestionnaire(record, "Y");
                writeAjaxXmlResponse(response, record, true);
            }
        }
        catch (ValidationException v) {
            // Handle the validation exception
            handleValidationExceptionForAjax(v, response);
        }
        catch (Exception e) {
            handleErrorForAjax("pm.renewalQuestionnaireMailingEvent.addRenewal.fail", "Add renewal questionnaire failed", e, response);
        }
        l.exiting(getClass().getName(), "addRenewalQuestionnare");
        return null;
    }

    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.renewalQuestionnaireMailingEvent.addRenewal.fail");
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
