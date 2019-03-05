package dti.pm.riskmgr.ibnrriskmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageCategory;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.messagemgr.MessageManagerAdmin;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.workflowmgr.WorkflowAgent;
import dti.pm.core.struts.PMBaseAction;
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
 * Action class to notify/processResponse default IBNR risk for workflow
 * 
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 7, 2011
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class IbnrNotifyAction extends PMBaseAction {

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
        return notify(mapping, form, request, response);
    }

    /**
     * This method is called when the process parameter "notify"
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
    public ActionForward notify(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "notify", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";

        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            PolicyHeader policyHeader = getPolicyHeader(request);
            String policyNo = policyHeader.getPolicyNo();
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            if (wa.hasWorkflow(policyNo)) {
                String transactionLogId = policyHeader.getLastTransactionInfo().getTransactionLogId();
                String transEffDate = policyHeader.getLastTransactionInfo().getTransEffectiveFromDate();
                String issueCompanyId = policyHeader.getIssueCompanyEntityId();

                // Try to get associated risks to see if there is only one risk
                Record rec = new Record();
                InactiveRiskFields.setOrgAssociatedRiskId(rec, "-1");
                TransactionFields.setTransactionLogId(rec, transactionLogId);
                TransactionFields.setTransactionEffectiveFromDate(rec, transEffDate);
                PolicyFields.setIssueCompanyId(rec, issueCompanyId);
                PolicyFields.setPolicyId(rec, policyHeader.getPolicyId());
                RecordSet rs = getIbnrRiskManager().loadAllIbnrRiskType(rec);
                request.setAttribute(InactiveRiskFields.ASSOCIOATED_RISK_COUNT, String.valueOf(rs.getSize()));
                if (rs.getSize() == 1) {
                    request.setAttribute(InactiveRiskFields.TO_ASSOCIATED_RISK_ID, RiskFields.getRiskBaseRecordId(rs.getRecord(0)));
                }
                else {
                    request.setAttribute(InactiveRiskFields.TO_ASSOCIATED_RISK_ID, "");
                }
            }

            ((MessageManagerAdmin) MessageManager.getInstance()).defineMessageForRequest(
                MessageCategory.getInstance("CONFIRMATION_PROMPT"),
                "ibnrNotify", "Do you wish to default IBNR inactive details for the cancelled risk?",
                "N");
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR,
                "Failed to process product notify.", e, request, mapping);
        }

        // done
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "notify", af);
        return af;
    }

    /**
     * This method is called when there the process parameter "processResponse"
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
    public ActionForward processResponse(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                         HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "processResponse", new Object[]{mapping, form, request, response});
        String forwardString = "viewAssociatedRisk";

        try {
            // Get PolicyHeader
            PolicyHeader policyHeader = getPolicyHeader(request);
            String policyNo = policyHeader.getPolicyNo();

            // Check if workflow exists, otherwise throw application exception
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            if (wa.hasWorkflow(policyNo)) {
                Record inputRecord = getInputRecord(request);
                boolean ibnrNotifyConfirmed = InactiveRiskFields.getIbnrNotifyConfirmed(inputRecord).booleanValue();
                if (!ibnrNotifyConfirmed) {
                    wa.setWorkflowTransitionParameter(policyNo, "INVALID");
                }
                else {
                    // If there is only one associated risk
                    if (inputRecord.hasStringValue(InactiveRiskFields.ASSOCIOATED_RISK_COUNT)
                        && InactiveRiskFields.getAssociatedRiskCount(inputRecord).equals("1")) {
                        // perform cancel IBNR active risk directly and forward to invokeViewIbnrInactiveRisk step
                        getIbnrRiskManager().performCancellation(policyHeader, inputRecord);
                        wa.setWorkflowTransitionParameter(policyNo, "SINGLEASSO");
                    }
                    else if (inputRecord.hasStringValue(InactiveRiskFields.ASSOCIOATED_RISK_COUNT)
                        && InactiveRiskFields.getAssociatedRiskCount(inputRecord).equals("0")) { // should not enter this part
                        wa.setWorkflowTransitionParameter(policyNo, "INVALID");
                    }

                    // Set attributes
                    String transactionLogId = policyHeader.getLastTransactionInfo().getTransactionLogId();
                    String transEffDate = policyHeader.getLastTransactionInfo().getTransEffectiveFromDate();
                    String issueCompanyId = policyHeader.getIssueCompanyEntityId();
                    wa.setWorkflowAttribute(policyNo, TransactionFields.TRANSACTION_LOG_ID, transactionLogId);
                    wa.setWorkflowAttribute(policyNo, PremiumAccountingFields.TRANS_EFF_DATE, transEffDate);
                    wa.setWorkflowAttribute(policyNo, PolicyFields.ISSUE_COMPANY_ID, issueCompanyId);
                    wa.setWorkflowAttribute(policyNo, PolicyFields.POLICY_ID, policyHeader.getPolicyId());
                }

                // Get the next state
                forwardString = wa.getNextState(policyNo);
                setForwardParametersForWorkflow(request, forwardString, policyNo, wa.getWorkflowInstanceIdName());
            }
            else {
                throw new AppException(AppException.UNEXPECTED_ERROR,
                    "Failed to determine workflow for IBNR Notify.");
            }
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to process response.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "processResponse", af);
        return af;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getIbnrRiskManager() == null)
            throw new ConfigurationException("The required property 'ibnrRiskManager' is missing.");
    }

    public IbnrRiskManager getIbnrRiskManager() {
        return m_ibnrRiskManager;
    }

    public void setIbnrRiskManager(IbnrRiskManager ibnrRiskManager) {
        m_ibnrRiskManager = ibnrRiskManager;
    }

    public IbnrNotifyAction() {
    }

    private IbnrRiskManager m_ibnrRiskManager;
}
