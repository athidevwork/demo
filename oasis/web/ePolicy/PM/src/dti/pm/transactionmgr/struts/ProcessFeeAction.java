package dti.pm.transactionmgr.struts;

import dti.pm.core.cachemgr.PolicyCacheManager;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.core.http.RequestIds;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;
import dti.pm.transactionmgr.TransactionManager;
import dti.oasis.util.LogUtils;
import dti.oasis.recordset.Record;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.messagemgr.ConfirmationFields;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.workflowmgr.WorkflowAgent;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 6, 2007
 *
 * @author sma
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/06/2013       fcb         148037 - changes for performance tuning.
 * 01/12/2015       awu         160142 - Modified the cache to check the Charges Fees configuration.
 * ---------------------------------------------------
 */

public class ProcessFeeAction extends PMBaseAction {
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
        request.setAttribute(RequestIds.PROCESS,"processFee");
        return processFee(mapping, form, request, response);
    }

    /**
    * This method is called when there the process parameter "processFee"
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
    public ActionForward processFee(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "processFee", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";

        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            // Get PolicyHeader
            PolicyHeader policyHeader = getPolicyHeader(request);
            Record policyRecord = policyHeader.toRecord();

            // Get input
            Record inputRecord = getInputRecord(request);

            PolicyCacheManager policyCacheManager = PolicyCacheManager.getInstance();
            if (!policyCacheManager.hasChargesNFeesConfigured()) {
                policyCacheManager.setChargesNFeeConfigured(getTransactionManager().isChargesNFeesConfigured());
            }

            if (policyCacheManager.getChargesNFeesConfigured()) {
                // Confirm fees if exist
                if (!inputRecord.hasStringValue(confirmFee+".confirmed") &&
                        getTransactionManager().isFeeDefined(policyRecord).booleanValue())
                    MessageManager.getInstance().addConfirmationPrompt(confirmFee, false);
                else {
                    // Waive fees if confirmed
                    if (ConfirmationFields.isConfirmed(confirmFee, inputRecord))
                        getTransactionManager().performFeeWaive(policyRecord);

                    WorkflowAgent wa = WorkflowAgentImpl.getInstance();
                    if (wa.hasWorkflow(policyHeader.getPolicyNo())) {
                        forwardString = wa.getNextState(policyHeader.getPolicyNo());
                        setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
                    }
                    else {
                        throw new AppException(AppException.UNEXPECTED_ERROR,
                            "Failed to determine workflow for Process Fee.");
                    }
                }
            }
            else {
                WorkflowAgent wa = WorkflowAgentImpl.getInstance();
                if (wa.hasWorkflow(policyHeader.getPolicyNo())) {
                    forwardString = wa.getNextState(policyHeader.getPolicyNo());
                    setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
                }
                else {
                    throw new AppException(AppException.UNEXPECTED_ERROR,
                        "Failed to determine workflow for Process Fee.");
                }
            }
        }
        catch (Exception e) {
            forwardString =  handleErrorPopup(AppException.UNEXPECTED_ERROR,
                "Failed to process fee.", e, request, mapping);
        }

        // done
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "processFee", af);
        return af;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getTransactionManager() == null)
            throw new ConfigurationException("The required property 'transactionmanager' is missing.");
    }

    public ProcessFeeAction() {}

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    private TransactionManager m_transactionManager;

    private static final String confirmFee = "pm.validateAndRateTransaction.confirm.fee";
}
