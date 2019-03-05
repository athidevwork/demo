package dti.pm.billingmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.util.SysParmProvider;
import dti.oasis.workflowmgr.WorkflowAgent;
import dti.pm.billingmgr.BillingManager;
import dti.pm.busobjs.SysParmIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class to load Coverage Billing
 * <p/>
 * <p>(C) 2009 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 20, 2010
 *
 * @author Bhong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class LoadCoverageBillingAction extends PMBaseAction {
    /**
     * This method is triggered automatically when there is no process parameter
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
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadCoverageBilling(mapping, form, request, response);
    }

    /**
     * Load coverage billing
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadCoverageBilling(ActionMapping mapping,
                                             ActionForm form,
                                             HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadCoverageBilling", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadResult";
        try {
            // Secures access to the page, load the Oasis Fields without loading the LOVs,
            //   and map the input parameters to the Fields
            securePage(request, form);
            PolicyHeader policyHeader = getPolicyHeader(request);

            SysParmProvider sysParm = SysParmProvider.getInstance();
            boolean isCovgBillingSetup = YesNoFlag.getInstance(sysParm.getSysParm(
                SysParmIds.PM_SET_BILLING_COVG)).booleanValue();

            if (!isCovgBillingSetup || policyHeader.getPolicyCycleCode().isQuote()
                || !getBillingManager().isCoverageIdExists(policyHeader)) {
                // Go to next step if coverage billing is not setup or no coverage billing needs to add
                WorkflowAgent wa = WorkflowAgentImpl.getInstance();
                if (wa.hasWorkflow(policyHeader.getPolicyNo())) {
                    // Get the next state
                    forwardString = wa.getNextState(policyHeader.getPolicyNo());
                    setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
                }
                else {
                    throw new AppException(AppException.UNEXPECTED_ERROR,
                        "Failed to determine workflow for coverage billing.");
                }
            }
            // Initialize "isBillingSetupDone" to N to indicate the billing is not setup yet.
            request.setAttribute("isBillingSetupDone", "N");
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(
                AppException.UNEXPECTED_ERROR, "Failed to load coverage billing page.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadCoverageBilling", af);
        }

        return af;
    }

    /**
     * This method is called when there the process parameter "closePage"
     * sent in along the requested url.
     * <p/>
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return ActionForward the forward
     * @throws Exception if there are some errors
     */
    public ActionForward closePage(ActionMapping mapping,
                                   ActionForm form,
                                   HttpServletRequest request,
                                   HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "closePage", new Object[]{mapping, form, request, response});
        String forwardString = "closePage";
        try {
            // Get PolicyHeader
            PolicyHeader policyHeader = getPolicyHeader(request);
            // Check if workflow exists, otherwise just forward to the original
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            Record inputRecord = getInputRecord(request);
            boolean isBillingSetupDone = YesNoFlag.getInstance(
                inputRecord.getStringValue("isBillingSetupDone")).booleanValue();
            if (wa.hasWorkflow(policyHeader.getPolicyNo())) {
                if (!isBillingSetupDone) {
                    wa.setWorkflowTransitionParameter(policyHeader.getPolicyNo(), "INVALID");
                }
                // Get the next state
                forwardString = wa.getNextState(policyHeader.getPolicyNo());
                setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
            }
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to determine validation closure method.", e, request, mapping);
        }

        // Done
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "closePage", af);
        return af;
    }

    public BillingManager getBillingManager() {
        return m_billingManager;
    }

    public void setBillingManager(BillingManager billingManager) {
        m_billingManager = billingManager;
    }

    /**
     * Verify config
     */
    public void verifyConfig() {
        if (getBillingManager() == null) {
            throw new ConfigurationException("The required property 'billingmanager' is missing.");
        }
    }

    private BillingManager m_billingManager;
}
