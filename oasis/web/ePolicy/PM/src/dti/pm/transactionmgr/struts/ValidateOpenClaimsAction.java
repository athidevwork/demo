package dti.pm.transactionmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.ConfirmationFields;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.workflowmgr.WorkflowAgent;
import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.transactionmgr.ValidateOpenClaimsMessageIds;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 12, 2007
 *
 * @author jmpotosky
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public class ValidateOpenClaimsAction extends PMBaseAction {
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
        request.setAttribute(RequestIds.PROCESS, "validateOpenClaims");
        return validateOpenClaims(mapping, form, request, response);
    }

    /**
     * This method is called when there the process parameter "validateOpenClaims"
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
    public ActionForward validateOpenClaims(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                            HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "validateOpenClaims", new Object[]{mapping, form, request, response});

        String forwardString;
        boolean userInteractionRequired = false;

        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            // Get input
            Record inputRecord = getInputRecord(request);

            // Get PolicyHeader
            PolicyHeader policyHeader = getPolicyHeader(request);
            inputRecord.setFields(policyHeader.toRecord(), false);

            // Validate open claims
            getTransactionManager().validateOpenClaims(inputRecord);

            // Check if workflow exists, otherwise do nothing
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            if (wa.hasWorkflow(policyHeader.getPolicyNo())) {
                String flag = YesNoFlag.getInstance(ConfirmationFields.isConfirmed(ValidateOpenClaimsMessageIds.CONFIRM_OPEN_CLAIMS, inputRecord)).getName();
                // Set the flag so that workflow knows about the validation status
                wa.setWorkflowTransitionParameter(policyHeader.getPolicyNo(), flag);
                // Get the next state
                forwardString = wa.getNextState(policyHeader.getPolicyNo());
                setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
            }
            else {
                throw new AppException(AppException.UNEXPECTED_ERROR,
                    "Failed to determine workflow for Validate Open Claims.");
            }
        }
        catch (ValidationException v) {
            // Set forward string to handle the confirmation prompt
            forwardString = "loadResult";
            handleValidationException(v, request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR,
                "Failed to validate open claims.", e, request, mapping);
        }

        // done
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "validateOpenClaims", af);
        return af;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getTransactionManager() == null)
            throw new ConfigurationException("The required property 'transactionmanager' is missing.");
    }

    public ValidateOpenClaimsAction() {
    }

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    private TransactionManager m_transactionManager;
}

