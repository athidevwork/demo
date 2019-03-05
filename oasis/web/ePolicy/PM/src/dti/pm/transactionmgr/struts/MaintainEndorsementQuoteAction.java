package dti.pm.transactionmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.oasis.workflowmgr.WorkflowAgent;
import dti.pm.busobjs.SysParmIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 20, 2008
 * Action class for delete/apply/copy endorsement quote
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/10/2010       bhong       110269 - added apply endorsement quote for MLMIC "coverage billing setup".
 * 06/13/2018       wrong       192557 - Modified deleteEndQuote() and copyEndQuote() to call hasValidSaveToken() to
 *                                       be used for CSRFInterceptor.
 * ---------------------------------------------------
 */
public class MaintainEndorsementQuoteAction extends PMBaseAction {
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
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return deleteEndQuote(mapping, form, request, response);
    }

    /**
     * Method for AJAX call to delete EndQuote
     * <p/>
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward deleteEndQuote(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "deleteEndQuote", new Object[]{mapping, form, request, response});
        try {
            if (hasValidSaveToken(request)) {
                // Secure access to the page, load the Oasis Fields without loading the LOVs,
                // and map the input parameters to the Fields
                securePage(request, form);

                PolicyHeader policyHeader = getPolicyHeader(request);

                getTransactionManager().performDeleteEndQuoteTransaction(policyHeader);

                writeEmptyAjaxXMLResponse(response);
            }
        }
        catch (Exception e) {
            // it could be a db error.
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to delete EndQuote transaction.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "deleteEndQuote", af);
        return af;
    }

    /**
     * Method for AJAX call to apply EndQuote
     * <p/>
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward applyEndQuote(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "applyEndQuote", new Object[]{mapping, form, request, response});
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            PolicyHeader policyHeader = getPolicyHeader(request);

            SysParmProvider sysParm = SysParmProvider.getInstance();
            boolean isCovgBillingSetup = YesNoFlag.getInstance(sysParm.getSysParm(
                SysParmIds.PM_SET_BILLING_COVG)).booleanValue();
            // If coverage billing is enabled, initialize workflow to load coverage billing,
            // otherwise execute original base logic.
            if (isCovgBillingSetup && policyHeader.getPolicyCycleCode().isPolicy()) {
                WorkflowAgent wa = WorkflowAgentImpl.getInstance();
                wa.initializeWorkflow(policyHeader.getPolicyNo(),
                    APPLY_ENDORSEMENT_QUOTE_WORKFLOW_ID,
                    APPLY_ENDORSEMENT_QUOTE_INITIAL_STATE);
                // Set policy view mode and endorsement quote id
                // Policy view mode
                String policyViewMode = policyHeader.getPolicyIdentifier().getPolicyViewMode().getName();
                if (!StringUtils.isBlank(policyViewMode)) {
                    wa.setWorkflowAttribute(policyHeader.getPolicyNo(), "policyViewMode", policyViewMode);
                }
                // Endorsement quote id
                String endQuoteId = policyHeader.getLastTransactionInfo().getEndorsementQuoteId();
                if (!StringUtils.isBlank(endQuoteId)) {
                    wa.setWorkflowAttribute(policyHeader.getPolicyNo(), "endQuoteId", endQuoteId);
                }
            }
            else {
                getTransactionManager().applyEndorsementQuote(policyHeader);
            }

            writeEmptyAjaxXMLResponse(response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to apply endorsement quote.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "applyEndQuote", af);
        return af;
    }

    /**
     * Method for AJAX call to copy EndQuote
     * <p/>
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward copyEndQuote(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "copyEndQuote", new Object[]{mapping, form, request, response});
        try {
            if (hasValidSaveToken(request)) {
                // Secure access to the page, load the Oasis Fields without loading the LOVs,
                // and map the input parameters to the Fields
                securePage(request, form);

                PolicyHeader policyHeader = getPolicyHeader(request);
                // add all request parameters into a record
                Record inputRecord = getInputRecord(request);
                // attempt to save. throws validationException if data does not pass the validation test

                getTransactionManager().copyEndorsementQuote(policyHeader, inputRecord);

                writeEmptyAjaxXMLResponse(response);
            }
        }

        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to copy endorsement quote.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "copyEndQuote", af);
        return af;
    }

    /**
     * This method unlocks policy when workflow exists
     * <p/>
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward exitWorkflowAndUnlockPolicy(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                     HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "exitWorkflowAndUnlockPolicy", new Object[]{mapping, form, request, response});
        String forwardString = "exitWorkflow";

        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            // Get PolicyHeader
            PolicyHeader policyHeader = getPolicyHeader(request);
            getLockManager().unLockPolicy(policyHeader, YesNoFlag.N, "Unlock from Exit Workflow And Unlock Policy");
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to unlock policy.", e, request, mapping);
        }

        // done
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "exitWorkflowAndUnlockPolicy", af);
        return af;
    }

    /**
     * Process apply endorsement quote
     * <p/>
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward processApplyEndorsementQuote(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                      HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "processApplyEndorsementQuote", new Object[]{mapping, form, request, response});
        String forwardString;

        try {
            // Secure page
            securePage(request, form);
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            // Get PolicyHeader
            PolicyHeader policyHeader = getPolicyHeader(request);
            try {
                getTransactionManager().processApplyEndorsementQuote(policyHeader);
            }
            catch (AppException ae) {
                // An AppException indicates a failure in apply endorsement quote. Do not raise this error,
                // rather get the AppException message key, strip out the info needed,  and set it into workflow for the correct transition
                String messageKey = ae.getMessageKey();
                int lastPeriod = messageKey.lastIndexOf(".") + 1;
                String transitionParameter = messageKey.substring(lastPeriod);
                wa.setWorkflowTransitionParameter(policyHeader.getPolicyNo(), transitionParameter);
            }

            // Check if workflow exists, otherwise throw application exception
            if (wa.hasWorkflow(policyHeader.getPolicyNo())) {
                // Get the next state
                forwardString = wa.getNextState(policyHeader.getPolicyNo());
                setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
            }
            else {
                throw new AppException(AppException.UNEXPECTED_ERROR,
                    "Failed to determine workflow for apply endorsement quote.");
            }
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to process apply endorsement quote.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "processApplyEndorsementQuote", af);
        return af;
    }

    /**
     * Perform lock policy
     * <p/>
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward lockPolicy(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                    HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "lockPolicy", new Object[]{mapping, form, request, response});
        String forwardString;

        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            // Get PolicyHeader
            PolicyHeader policyHeader = getPolicyHeader(request);
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            try {
                getTransactionManager().lockPolicy(policyHeader);
            }
            catch (ValidationException ve) {
                wa.setWorkflowTransitionParameter(policyHeader.getPolicyNo(), "LOCK_ERROR");
            }
            // Check if workflow exists, otherwise throw application exception
            if (wa.hasWorkflow(policyHeader.getPolicyNo())) {
                // Get the next state
                forwardString = wa.getNextState(policyHeader.getPolicyNo());
                setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
            }
            else {
                throw new AppException(AppException.UNEXPECTED_ERROR,
                    "Failed to lock policy.");
            }
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to lock policy.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "lockPolicy", af);
        return af;
    }

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    public void verifyConfig() {
        if (getTransactionManager() == null)
            throw new ConfigurationException("The required property 'transactionmanager' is missing.");
    }

    private TransactionManager m_transactionManager;
    private static final String APPLY_ENDORSEMENT_QUOTE_WORKFLOW_ID = "ApplyEndorsementQuoteWorkflow";
    private static final String APPLY_ENDORSEMENT_QUOTE_INITIAL_STATE = "invokeLockPolicyAndApplyEndorsementQuote";
}
