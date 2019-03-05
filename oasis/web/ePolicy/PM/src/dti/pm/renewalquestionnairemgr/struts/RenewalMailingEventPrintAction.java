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
 * Action class for Print the Renewal Questionnaire Mailing Event.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   June 13, 2008
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/06/2012       xnie        138374 Modified performPrintRenewalQuestionnare to add a check: when performB is 'Y', system
 *                              calls manager's performPrintRenewalQuestionnare.
 * ---------------------------------------------------
 */

public class RenewalMailingEventPrintAction extends PMBaseAction {
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
        return getInitialValuesForPrintRenewalQuestionnare(mapping, form, request, response);
    }

    /**
     * Get the initial values for print job.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward getInitialValuesForPrintRenewalQuestionnare(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForPrintRenewalQuestionnare",
            new Object[]{mapping, form, request, response});
        String forwardString = "performPrint";
        Record inputRecord;
        try {
            // Secures access to the page, loads the oasis fields and map the input parameters into the fields.
            securePage(request, form);
            inputRecord = getInputRecord(request);
            request.setAttribute(QuestionnaireMailingEventFields.POL_REN_FRM_MASTER_ID,
                QuestionnaireMailingEventFields.getPolRenfrmMasterId(inputRecord));
            request.setAttribute(QuestionnaireMailingEventFields.SEND_DATE,
                QuestionnaireMailingEventFields.getSendDate(inputRecord));
            // Loads list of values
            loadListOfValues(request, form);
            // Add Js messages
            addJsMessages();
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR,
                "Failed to load the Print Options page.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "getInitialValuesForPrintRenewalQuestionnare", null);
        return af;
    }

    /**
     * Perform the print job.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward performPrintRenewalQuestionnare(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "performPrintRenewalQuestionnare", new Object[]{mapping, form, request, response});
        Record inputRecord;
        try {
            // Secures access to the page.
            securePage(request, form, false);
            // Get the selected questionnaire(s) from the recordset
            inputRecord = getInputRecord(request);
            if (QuestionnaireMailingEventFields.getPerformB(inputRecord).booleanValue()) {
                getRenewalQuestionnaireManager().performPrintRenewalQuestionnare(inputRecord);
            }
            // Get the recordset which stores the generated return message.
            Record record = new Record();
            QuestionnaireMailingEventFields.setPrintOptions(record, "Y");
            writeAjaxXmlResponse(response, record, true);
        }
        catch (ValidationException v) {
            // Handle the validation exception
            handleValidationExceptionForAjax(v, response);
        }
        catch (Exception e) {
            handleErrorForAjax("pm.renewalQuestionnaireMailingEvent.print.fail", "Print mailing event failed", e, response);
        }
        l.exiting(getClass().getName(), "performPrintRenewalQuestionnare");
        return null;
    }

    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.renewalQuestionnaireMailingEvent.print.success");
        MessageManager.getInstance().addJsMessage("pm.renewalQuestionnaireMailingEvent.print.fail");
        MessageManager.getInstance().addJsMessage("pm.renewalQuestionnaireMailingEvent.print.sendDate.empty");
        MessageManager.getInstance().addJsMessage("pm.renewalQuestionnaireMailingEvent.print.resend.nothing");
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
