package dti.pm.renewalquestionnairemgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
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
 * Action class for Renewal Mailing Event Questionnaire.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   June 13, 2008
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public class RenewalMailingEventQuestionnaireAction extends PMBaseAction {
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
        return loadAllMailingEventQuestionnaire(mapping, form, request, response);
    }

    /**
     * Load the questionnaire for the selected mailing event.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllMailingEventQuestionnaire(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllMailingEventQuestionnaire", new Object[]{mapping, form, request, response});
        String forwardString = "loadAllQuestionnaire";
        Record inputRecord;
        try {
            // Secures access to the page, loads the oasis fields and map the input parameters into the fields.
            securePage(request, form);
            // Get all request parameters into a record
            inputRecord = getInputRecord(request);
            // Get the RecordSet
            RecordSet rs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            if (rs == null) {
                rs = getRenewalQuestionnaireManager().loadAllQuestionnaireForMailingEvent(inputRecord);
                if (rs.getSize() == 0) {
                    rs = new RecordSet();
                    List fieldNameList = new ArrayList();
                    fieldNameList.add("rownum");
                    fieldNameList.add("isReceivedBAvailable");
                    fieldNameList.add("isCapturedBAvailable");
                    fieldNameList.add("isResendBAvailable");
                    rs.addFieldNameCollection(fieldNameList);
                }
                else if (rs.getSize() > 0) {
                    // If RecordSet's size is more than maxRow,system displays a information.
                    int maxSize = rs.getRecord(0).getIntegerValue("maxRows").intValue();
                    if (maxSize != -1 && rs.getSize() >= maxSize) {
                        MessageManager.getInstance().addInfoMessage("pm.generateRenewalQuestionnaire.maxQuestionnaire",
                            new String[]{String.valueOf(maxSize)});
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
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR,
                "Failed to load the questionnaire for mailing event.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllMailingEventQuestionnaire", null);
        return af;
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
