package dti.pm.transactionmgr.reissueprocessmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.workflowmgr.WorkflowAgent;
import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.transactionmgr.reissueprocessmgr.ReissueProcessManager;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;
import dti.pm.entitlementmgr.EntitlementFields;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 22, 2007
 *
 * @author gjlong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/15/2008       fcb         getInitialValuesForReissuePolicy: call to validatePolicyPicture added.
 * ---------------------------------------------------
 */
public class ReissuePolicyAction extends PMBaseAction {
    /**
     * the default method is executed when no process parameter is specified
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return getInitialValuesForReissuePolicy(mapping, form, request, response);
    }

    /**
     * This method is called when there the process parameter "getInitialValuesForReissuePolicy"
     * sent in along the requested url. It is used to load the initial values
     * for Reissue Policy page.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward getInitialValuesForReissuePolicy(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                          HttpServletResponse response) throws Exception {

        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForReissuePolicy", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        ReissueProcessManager reissueProcessManager = getReissueProcessManager();
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            PolicyHeader policyHeader = getPolicyHeader(request);

            // get input values
            Record inputRecord = getInputRecord(request);

            // validate policy picture
            getTransactionManager().validatePolicyPicture(policyHeader, inputRecord);
            
            // load initialRecord conditionally
            Record outputRecord = reissueProcessManager.getInitialValuesForReissuePolicy(policyHeader, inputRecord);

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, outputRecord);
            // Load the list of values
            loadListOfValues(request, form);

            request.setAttribute(RequestIds.IS_NEW_VALUE, "Y");
        }
        catch (ValidationException v) {
            // Set everything to read-only, if there's validation error
            Record outputRecord = new Record();
            EntitlementFields.setReadOnly(outputRecord, true);
            publishOutputRecord(request, outputRecord);
            loadListOfValues(request, form);
            // Handle the validation exception
            handleValidationException(v, request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the Reissue Policy page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "getInitialValuesForReissuePolicy", af);
        return af;
    }

    /**
     * This method is called when there the process parameter "reissuePolicy"
     * sent in along the requested url. and it is triggered by a ajax call.
     * it validates the input data, and throws a validation exception if failed.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward reissuePolicy(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                       HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveReissuePolicy", new Object[]{mapping, form, request, response});
        String forwardString = null;
        Record inputRecord = null;
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page for non-premiem txn. (so transactionModeEntitlements.xml is not applied)
                // load the Oasis Fields without loading the LOVs,
                // and map the input parameters to the Fields
                securePage(request, form);

                // get policyHeader
                PolicyHeader policyHeader = getPolicyHeader(request);

                // get values from the form
                inputRecord = getInputRecord(request);

                // add LovLabel fields into inputRecord, this UC needs policyTypeCode label to display message
                getLovLabelsForInitialValues(request, inputRecord);

                // add regionalOfficeVisibility just like how createPolicyAction does. suggested by jmp
                addRegionalOfficeVisibility(request, inputRecord);

                // get ReissueProcessManager, and save it.. (it validates the values before the actual save.)
                getReissueProcessManager().reissuePolicy(policyHeader, inputRecord);

                // write a empty Ajax Reponse, so handleAjaxMessages does not fail
                writeEmptyAjaxXMLResponse(response);
                //view related policies
                String time = "PRE";
                boolean checkRelatedPolicies = false;
                try {
                    checkRelatedPolicies = getTransactionManager().checkRelatedPolicy(policyHeader, time);
                }
                catch (Exception e) {
                    l.logp(Level.SEVERE, getClass().getName(), "performReissue",
                        "Failed to determine workflow for view related policy.", e);
                    checkRelatedPolicies = true;
                }
                if (checkRelatedPolicies) {
                    WorkflowAgent wa = WorkflowAgentImpl.getInstance();
                    wa.initializeWorkflow(PolicyHeaderFields.getPolicyNo(inputRecord),
                        RELATED_POLICY_WORKFLOW_PROCESS,
                        RELATED_POLICY_INITIAL_STATE);
                }
            }
        }
        catch (ValidationException ve) {
            handleValidationExceptionForAjax(ve, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to save reissue policy page", e, response);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveReissuePolicy", af);
        return af;
    }

    /**
     * This method is called when there the process parameter "getDefaultExpirationDateForReissuePolicy"
     * sent in along the requested url.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward getExpirationDateForReissuePolicy(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                           HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getDefaultExpirationDateForReissuePolicy", new Object[]{mapping, form, request, response});
        try {
            // Secure access to the page for non-premiem txn,
            // without loading the Oasis Fields
            securePage(request, form, false);

            Record inputRecord = getInputRecord(request);
            // get policyHeader
            PolicyHeader policyHeader = getPolicyHeader(request);
            // process field change
            Record record = getReissueProcessManager().getExpirationDateForReissuePolicy(policyHeader, inputRecord);
            // return term expiration date

            writeAjaxXmlResponse(response, record, true);
        }
        catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get default expiration date for reissue policy page.", e, response);
        }

        // done
        ActionForward af = null;
        l.exiting(getClass().getName(), "getDefaultExpirationDateForReissuePolicy", af);
        return af;
    }

    public ReissueProcessManager getReissueProcessManager() {
        return m_reissueProcessManager;
    }

    public void setReissueProcessManager(ReissueProcessManager reissueProcessManager) {
        m_reissueProcessManager = reissueProcessManager;
    }

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getReissueProcessManager() == null) {
            throw new ConfigurationException("The required property 'reissuepolicymanager' is missing.");
        }
    }

    private ReissueProcessManager m_reissueProcessManager;
    private TransactionManager m_transactionManager;
    private static final String RELATED_POLICY_WORKFLOW_PROCESS = "RelatedPolicyWorkflow";
    private static final String RELATED_POLICY_INITIAL_STATE = "invokeViewRelPolicy";
}
