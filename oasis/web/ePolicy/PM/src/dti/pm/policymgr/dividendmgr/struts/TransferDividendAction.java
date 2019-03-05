package dti.pm.policymgr.dividendmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.SysParmProvider;
import dti.oasis.workflowmgr.WorkflowAgent;
import dti.pm.busobjs.SysParmIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.dividendmgr.DividendManager;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Action class for transfer dividend.
 * <p/>
 * <p>(C) 2013 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 23, 2013
 *
 * @author awu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
  *
 * ---------------------------------------------------
 */
public class TransferDividendAction extends PMBaseAction {


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
    public ActionForward unspecified(ActionMapping mapping,
                                     ActionForm form,
                                     HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        return loadAllTransferRisk(mapping, form, request, response);
    }

    /**
     * Used to load the available risks which can do dividend transfer, and display them on dividendTransfer.jsp page.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllTransferRisk(ActionMapping mapping,
                                             ActionForm form,
                                             HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllTransferRisk", new Object[]{mapping, form, request, response});
        String forwardString = "";
        try {
            securePage(request, form);

            PolicyHeader policyHeader = getPolicyHeader(request);

            String currTranCode = policyHeader.getLastTransactionInfo().getTransactionTypeCode().getName();
            RecordSet rs = null;
            //Only the transactions configured in system parameter PM_TRANSFER_DIV can transfer dividend.
            if (isTransferTransaction(currTranCode) && policyHeader.getPolicyCycleCode().isPolicy()) {
                rs = getDividendManager().loadAllTransferRisk(policyHeader);
            }

            if (rs != null && rs.getSize() > 0) {
                forwardString = "loadResult";

                // Set loaded data into request
                setDataBean(request, rs);

                Record output = rs.getSummaryRecord();

                // Publish the output record for use by the Oasis Tags and JSP.
                publishOutputRecord(request, output);

                // Load grid header for grid.
                loadGridHeader(request);

                // Load the list of values after loading the data.
                loadListOfValues(request, form);
            }
            else {
                WorkflowAgent wa = WorkflowAgentImpl.getInstance();
                if (wa.hasWorkflow(policyHeader.getPolicyNo())) {
                    // Get the next state
                    forwardString = wa.getNextState(policyHeader.getPolicyNo());
                    setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
                }
                else {
                    throw new AppException(AppException.UNEXPECTED_ERROR,
                        "Failed to determine workflow for Transfer Dividend.");
                }
            }
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the transfer dividend page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllTransferRisk", af);
        return af;
    }

    /**
     * Used to do transfer dividend, it will be called by page dividendTransfer.jsp.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward transferDividend(ActionMapping mapping,
                                          ActionForm form,
                                          HttpServletRequest request,
                                          HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "transferDividend", new Object[]{mapping, form, request, response});
        String forwardString = "";
        try {
            securePage(request, form, false);

            PolicyHeader policyHeader = getPolicyHeader(request);
            Record inputRecord = getInputRecord(request);

            getDividendManager().transferDividend(policyHeader, inputRecord);

            // Check if workflow exists, otherwise throw application exception
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            if (wa.hasWorkflow(policyHeader.getPolicyNo())) {
                // Get the next state
                forwardString = wa.getNextState(policyHeader.getPolicyNo());
                setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
            }
            else {
                throw new AppException(AppException.UNEXPECTED_ERROR,
                    "Failed to determine workflow for Transfer Dividend.");
            }
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR,
                "Failed to process transfer dividend.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "transferDividend", af);
        return af;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    /**
     * Verify dividendManager and anchorColumnName in spring config
     */
    public void verifyConfig() {
        if (getDividendManager() == null)
            throw new ConfigurationException("The required property 'dividendManager' is missing.");
        if (super.getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
    }

    /**
     * Check whether the current transaction code was configured in system parameter.
     *
     * @param transactionCode
     * @return
     */
    public boolean isTransferTransaction(String transactionCode) {
        Logger l = LogUtils.enterLog(getClass(), "isTransferTransaction", new Object[]{transactionCode});

        boolean isTransfer = false;
        String transferTranCodes = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_TRANSFER_DIVIDEND, "");
        String[] transactionCodeArr = transferTranCodes.split(",");
        for (int i = 0; i < transactionCodeArr.length; i++) {
            if (transactionCode.equals(transactionCodeArr[i])) {
                isTransfer = true;
                break;
            }
        }

        l.exiting(getClass().getName(), "isTransferTransaction", isTransfer);
        return isTransfer;
    }

    public DividendManager getDividendManager() {
        return m_dividendManager;
    }

    public void setDividendManager(DividendManager dividendManager) {
        this.m_dividendManager = dividendManager;
    }

    private DividendManager m_dividendManager;
}
