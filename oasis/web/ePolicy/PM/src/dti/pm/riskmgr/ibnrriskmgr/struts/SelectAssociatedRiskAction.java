package dti.pm.riskmgr.ibnrriskmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.workflowmgr.WorkflowAgent;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.entitlementmgr.EntitlementFields;
import dti.pm.policymgr.PolicyFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.premiummgr.PremiumAccountingFields;
import dti.pm.riskmgr.RiskFields;
import dti.pm.riskmgr.ibnrriskmgr.IbnrRiskManager;
import dti.pm.riskmgr.ibnrriskmgr.InactiveRiskFields;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Action class to handle select associated risks and process cancel IBNR risk
 *
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 07, 2011
 *
 * @author Dzhang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/25/2011       dzhang      127324 - If the Select Associated Action was invoked by the IBNR main page, should not invoke workflow.
 * ---------------------------------------------------
 */

public class SelectAssociatedRiskAction extends PMBaseAction {
    /**
     * This method is triggered automatically when there is no process parameter sent in along the requested url.
     * <p/>
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadAllAssociatedRiskType(mapping, form, request, response);
    }

    /**
     * This method is called when there the process parameter "loadAllAssociatedRiskType"
     * sent in along the requested url.
     * <p/>
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward loadAllAssociatedRiskType(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                   HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllAssociatedRiskType", new Object[]{mapping, form, request, response});
        
        String forwardString = "loadResult";
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            // get input
            Record inputRecord = getInputRecord(request);

            // get policy header
            PolicyHeader policyHeader = getPolicyHeader(request);
            inputRecord.setFields(policyHeader.toRecord(), false);

            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            String policyNo = policyHeader.getPolicyNo();
            if (wa.hasWorkflow(policyNo) && !inputRecord.hasStringValue(InactiveRiskFields.NOT_INVOKER_WORK_FLOW)) {
                String transactionLogId = (String) wa.getWorkflowAttribute(policyNo, TransactionFields.TRANSACTION_LOG_ID);
                String transEffDate = (String) wa.getWorkflowAttribute(policyNo, PremiumAccountingFields.TRANS_EFF_DATE);
                String issueCompanyId = (String) wa.getWorkflowAttribute(policyNo, PolicyFields.ISSUE_COMPANY_ID);
                String riskId = (String) wa.getWorkflowAttribute(policyNo, RiskFields.RISK_ID);
                String policyId = (String) wa.getWorkflowAttribute(policyNo, PolicyFields.POLICY_ID);
                TransactionFields.setTransactionLogId(inputRecord, transactionLogId);
                TransactionFields.setTransactionEffectiveFromDate(inputRecord, transEffDate);
                PolicyFields.setIssueCompanyId(inputRecord, issueCompanyId);
                PolicyFields.setPolicyId(inputRecord, policyId);
                InactiveRiskFields.setOrgAssociatedRiskId(inputRecord, "-1");
                request.setAttribute(InactiveRiskFields.IS_IN_WORKFLOW, YesNoFlag.Y);
                request.setAttribute(RiskFields.RISK_ID, riskId);
            }
            else {
                request.setAttribute(InactiveRiskFields.IS_IN_WORKFLOW, YesNoFlag.N);
            }

            // load grid content,
            RecordSet rs = getIbnrRiskManager().loadAllIbnrRiskType(inputRecord);
            Record outputRecord = rs.getSummaryRecord();
            //if no data found, add warning message.
            if (rs.getSize() == 0) {
                EntitlementFields.setReadOnly(outputRecord, true);
                MessageManager.getInstance().addErrorMessage("pm.selectAssociatedRisk.NoDataFound");
            }

            setDataBean(request, rs);
            publishOutputRecord(request, outputRecord);
            // load grid header
            loadGridHeader(request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load associated risk list.", e, request, mapping);
        }

        // done
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllAssociatedRiskType", af);
        return af;
    }

    /**
     * Method to process cancel active IBNR risk
     * <p/>
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward processCancelIbnrRisk(ActionMapping mapping, ActionForm form,
                                               HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "processCancelIbnrRisk", new Object[]{mapping, form, request, response});

        String forwardString;
        try {
            // Secure page without load fields
            securePage(request, form, false);

            // Map request to record for input
            Record inputRecord = getInputRecord(request);
            PolicyHeader policyHeader = getPolicyHeader(request);
            String policyNo = policyHeader.getPolicyNo();
            getIbnrRiskManager().performCancellation(policyHeader, inputRecord);

            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            if (wa.hasWorkflow(policyNo)) {
                forwardString = wa.getNextState(policyNo);
                setForwardParametersForWorkflow(request, forwardString, policyNo, wa.getWorkflowInstanceIdName());
            }
            else {
                throw new AppException(AppException.UNEXPECTED_ERROR,
                    "Failed to determine workflow for Cancel IBNR risk.");
            }
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to cancel IBNR risk.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "processCancelIbnrRisk", af);
        return af;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getIbnrRiskManager() == null)
            throw new ConfigurationException("The required property 'ibnrRiskManager' is missing.");
    }

    public IbnrRiskManager getIbnrRiskManager() {
        return m_ibnrRiskManager;
    }

    public void setIbnrRiskManager(IbnrRiskManager ibnrRiskManager) {
        m_ibnrRiskManager = ibnrRiskManager;
    }

    public SelectAssociatedRiskAction() {
    }

    private IbnrRiskManager m_ibnrRiskManager;
}