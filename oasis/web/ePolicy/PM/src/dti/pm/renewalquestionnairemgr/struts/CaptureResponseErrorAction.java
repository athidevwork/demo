package dti.pm.renewalquestionnairemgr.struts;

import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.RecordSet;
import dti.oasis.session.UserSession;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.LogUtils;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.renewalquestionnairemgr.QuestionnaireMailingEventFields;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Action class for Renewal Questionnaire Capture Response Error.
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

public class CaptureResponseErrorAction extends PMBaseAction {

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
        return loadAllResponseError(mapping, form, request, response);
    }

    /**
     * Load the Capture Response Error page.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllResponseError(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllResponseError",
            new Object[]{mapping, form, request, response});
        String forwardString = "responseError";
        RecordSet inputRecords = null;
        try {
            // Secures access to the page, loads the oasis fields and map the input parameters into the fields.
            securePage(request, form);
            // Get UserSession.
            UserSession userSession = UserSessionManager.getInstance().getUserSession();
            if (userSession.has(QuestionnaireMailingEventFields.QUESTION_ERROR_MESSAGE)) {
                inputRecords = (RecordSet) userSession.get(QuestionnaireMailingEventFields.QUESTION_ERROR_MESSAGE);
                userSession.remove(QuestionnaireMailingEventFields.QUESTION_ERROR_MESSAGE);
            }
            // If underwriter refreshes this page,system initializes the RecordSet empty.
            if (inputRecords == null) {
                inputRecords = new RecordSet();
                List fieldNameList = new ArrayList();
                fieldNameList.add("rownum");
                inputRecords.addFieldNameCollection(fieldNameList);
            }
            // Sets data bean
            setDataBean(request, inputRecords);
            // Load grid header bean
            loadGridHeader(request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup("pm.generateRenewalQuestionnaire.system.error",
                "Failed to load the Capture Response Error page.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllRenewalQuestionnaireInformation", af);
        return af;
    }

    /**
     * Verify anchorColumnName in spring config
     */
    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
    }
}
