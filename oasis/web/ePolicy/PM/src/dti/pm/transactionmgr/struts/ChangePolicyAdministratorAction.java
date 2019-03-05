package dti.pm.transactionmgr.struts;

import dti.oasis.request.RequestStorageManager;
import dti.oasis.session.UserSessionManager;
import dti.pm.busobjs.PolicyHeaderReloadCode;
import dti.pm.core.request.RequestStorageIds;
import dti.pm.core.session.UserSessionIds;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

import dti.oasis.util.LogUtils;
import dti.oasis.recordset.Record;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.core.struts.PMBaseAction;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 7, 2007
 *
 * @author zlzhu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * Nov 7, 2007          zlzhu     Created
 * 10/26/2018       ryzhao     196166 - Modified changePolicyAdministrator() to reload policy header after new
 *                                      policyholder is saved successfully.
 * ---------------------------------------------------
 */

public class ChangePolicyAdministratorAction extends PMBaseAction {
    /**
     * This method is triggered automatically when there is no process parameter sent in along the requested url.
     * <p/>
     *
     * @param mapping  mapping
     * @param form     form bean
     * @param request  request
     * @param response response
     * @return ActionForward where to forward
     * @throws Exception if there's some error
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return capturePolicyAdministrator(mapping, form, request, response);
    }

    /**
     * Load current term effective date and current term expiration date
     *
     * @param mapping  mapping
     * @param form     form bean
     * @param request  request
     * @param response response
     * @return ActionForward where to forward
     * @throws Exception if there's some error
     */
    public ActionForward capturePolicyAdministrator(ActionMapping mapping,
                                                    ActionForm form,
                                                    HttpServletRequest request,
                                                    HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "capturePolicyAdministrator", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";

        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            PolicyHeader policyHeader = getPolicyHeader(request);
            Record record = (Record) request.getAttribute(SAVED_INPUT_RECORD);
            //retrieve save input
            if (record == null) {
                record = getTransactionManager().getInitialValuesForChangePolicyAdministrator(policyHeader);
            }
            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, record);
            loadListOfValues(request, form);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR,
                "Failed to load the Change Policy Admin page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "capturePolicyAdministrator", af);
        return af;
    }

    /**
     * Change the term dates.
     *
     * @param mapping  mapping
     * @param form     form bean
     * @param request  request
     * @param response response
     * @return ActionForward where to forward
     * @throws Exception if there's some error
     */
    public ActionForward changePolicyAdministrator(ActionMapping mapping,
                                                   ActionForm form,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "savePolicyAdministrator", new Object[]{mapping, form, request, response});
        Record inputRecord = null;
        String forwardString = "saveResult";
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page, load the Oasis Fields without loading the LOVs,
                // and map the input parameters to the Fields
                securePage(request, form);

                // get policyHeader
                PolicyHeader policyHeader = getPolicyHeader(request);

                // get values from the form
                inputRecord = getInputRecord(request);

                // use TransactionManager to save the dates.
                getTransactionManager().savePolicyAdministrator(policyHeader, inputRecord);
                // Save the input records into request,so user can see the newly selected admin
                request.setAttribute(SAVED_INPUT_RECORD, inputRecord);

                //Update the Cache in session and request
                policyHeader.setReloadCode(PolicyHeaderReloadCode.CURRENT_TERM);
                RequestStorageManager.getInstance().set(RequestStorageIds.POLICY_HEADER, policyHeader);
                UserSessionManager.getInstance().getUserSession().set(UserSessionIds.POLICY_HEADER, policyHeader);

                // Reload policy header due to policy data is changed.
                reloadPolicyHeader(request);

            }
        }
        catch (ValidationException ve) {
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to save the batchRenewalProcess page.", e, request, mapping);
            request.setAttribute("isRenewalSucceed", "false");
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "savePolicyAdministrator", af);
        return af;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getTransactionManager() == null) {
            throw new ConfigurationException("The required property 'transactionManager' is missing.");
        }
        if (getUserSessionManager() == null)
            throw new ConfigurationException("The required property 'userSessionManager' is missing.");
    }

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    public UserSessionManager getUserSessionManager() {
        return m_userSessionManager;
    }

    public void setUserSessionManager(UserSessionManager userSessionManager) {
        m_userSessionManager = userSessionManager;
    }

    protected static final String SAVED_INPUT_RECORD = "savedInputRecord";
    private TransactionManager m_transactionManager;
    private UserSessionManager m_userSessionManager;
}
