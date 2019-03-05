package dti.pm.transactionmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.util.SysParmProvider;
import dti.oasis.workflowmgr.WorkflowAgent;
import dti.pm.busobjs.TransactionTypeCode;
import dti.pm.core.http.RequestIds;
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
 * Date:   Jun 8, 2007
 *
 * @author Sxm
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/29/2013       tcheng      141447 - Modified validatePremium to add a workflow of view premium
 * 03/06/2013       fcb         142697 - Removed the previous changes.
 * 12/06/2013       fcb         148037 - Changes for performance tuning.
 * ---------------------------------------------------
 */

public class ValidatePremiumAction extends PMBaseAction {
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
        request.setAttribute(RequestIds.PROCESS, "validatePremium");
        return validatePremium(mapping, form, request, response);
    }

    /**
     * This method is called when there the process parameter "loadAllValidationError"
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
    public ActionForward validatePremium(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                         HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "validatePremium", new Object[]{mapping, form, request, response});
        String forwardString;
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            SysParmProvider sysParm = SysParmProvider.getInstance();
            YesNoFlag returnValue = YesNoFlag.getInstance(sysParm.getSysParm("PM_RISK_PREM_LT_ZERO", "N"));
            int result = 0;

            // Get PolicyHeader
            PolicyHeader policyHeader = getPolicyHeader(request);

            if (!returnValue.booleanValue()) {
                // Validate premium
                result = getTransactionManager().performPremiumValidation(policyHeader.toRecord());
            }

            // Go to the next step based on the validation result
            if (result > 0) {
                forwardString = "viewValidationError";
            }
            else {
                // Check if workflow exists, otherwise throw application exception
                WorkflowAgent wa = WorkflowAgentImpl.getInstance();
                if (wa.hasWorkflow(policyHeader.getPolicyNo())) {
                    // Set the flag so that workflow knows about the validation status
                    wa.setWorkflowTransitionParameter(policyHeader.getPolicyNo(), "VALID");
                    // Get the next state
                    forwardString = wa.getNextState(policyHeader.getPolicyNo());
                    // next workflow is save as endquote
                    if (forwardString.equals("invokeProcessSaveEndorsementQuoteMsg")) {
                        String quoteType = "endorsement";
                        TransactionTypeCode transactionTypeCode = policyHeader.getLastTransactionInfo().getTransactionTypeCode();
                        if (transactionTypeCode.isRenewal()) {
                            quoteType = "renewal";
                        }
                        MessageManager.getInstance().addInfoMessage("pm.workflowmgr.save.official.invokeProcessSaveEndQuoteMsg.info", new Object[]{quoteType});
                    }
                    setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
                }
                else {
                    throw new AppException(AppException.UNEXPECTED_ERROR,
                        "Failed to determine workflow for Validate Premium.");
                }
            }
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR,
                "Failed to validate premium.", e, request, mapping);
        }

        // done
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "validatePremium", af);
        return af;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getTransactionManager() == null)
            throw new ConfigurationException("The required property 'transactionmanager' is missing.");
    }

    public ValidatePremiumAction() {
    }

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    private TransactionManager m_transactionManager;
}
