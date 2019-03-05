package dti.pm.renewalquestionnairemgr.struts;

import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.RecordSet;
import dti.oasis.session.UserSession;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.LogUtils;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.renewalquestionnairemgr.RenewalQuestionnaireFields;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Action class for Generate Renewal Questionnaire Information.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 14, 2007
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
public class GenerateRenewalQustionnaireInformationAction extends PMBaseAction {
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
        return loadAllRenewalQuestionnaireInformation(mapping, form, request, response);
    }

    /**
     * Load the generate renewal questionnaire information page.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllRenewalQuestionnaireInformation(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllRenewalQuestionnaireInformation",
            new Object[]{mapping, form, request, response});
        String forwardString = "loadAllRenewalQuestionnaireInformation";
        RecordSet inputRecords = null;
        try {
            // Secures access to the page, loads the oasis fields and map the input parameters into the fields.
            securePage(request, form);
            // Get UserSession.
            UserSession userSession = UserSessionManager.getInstance().getUserSession();
            if (userSession.has(RenewalQuestionnaireFields.GENERATE_QUESTIONNAIRE_INFORMATION)) {
                inputRecords = (RecordSet) userSession.get(RenewalQuestionnaireFields.GENERATE_QUESTIONNAIRE_INFORMATION);
                userSession.remove(RenewalQuestionnaireFields.GENERATE_QUESTIONNAIRE_INFORMATION);
            }
            // If underwriter refreshes this page,system initializes the RecordSet empty.
            if (inputRecords == null) {
                inputRecords = new RecordSet();
                List fieldNameList = new ArrayList();
                fieldNameList.add(RenewalQuestionnaireFields.RENEW_FORM_ID);
                inputRecords.addFieldNameCollection(fieldNameList);
            }
            // Sets data bean
            setDataBean(request, inputRecords);
            // Load grid header bean
            loadGridHeader(request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup("pm.generateRenewalQuestionnaire.system.error",
                "Failed to load the Generate Renewal Questionnaire Information page.", e, request, mapping);
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
