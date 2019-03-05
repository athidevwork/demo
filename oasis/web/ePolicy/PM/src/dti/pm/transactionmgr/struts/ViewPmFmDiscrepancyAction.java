package dti.pm.transactionmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.util.LogUtils;
import dti.oasis.workflowmgr.WorkflowAgent;
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
 * Action class for view PM FM Discrepancy
 * <p/>
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 10, 2010
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
public class ViewPmFmDiscrepancyAction extends PMBaseAction {
    /**
     * This method is triggered automatically when where is no process parameter
     * sent in along the requested url.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadAllPmFmDiscrepancy(mapping, form, request, response);
    }

    /**
     * This method is called when there the process parameter "loadAllPmFmDiscrepancy"
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
    public ActionForward loadAllPmFmDiscrepancy(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllPmFmDiscrepancy", new Object[]{mapping, form, request, response});
        String forwardString = "loadDiscrepancy";

        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            // Get PolicyHeader
            PolicyHeader policyHeader = getPolicyHeader(request);
            Record inputRecord = policyHeader.toRecord();

            // Get the summary discrepancy information
            Record summaryRecord = getTransactionManager().loadDiscrepancySummaryInfo(inputRecord);

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, summaryRecord);

            // Set the summary information as a Message for display purposes
            MessageManager.getInstance().addInfoMessage("pm.pmFmDiscrepancy.interfaceStatus.header",
                new String[]{summaryRecord.getStringValue("pmFmInterfaceMsg")});

            // Get the discrepancy compare information
            RecordSet compareRs = getTransactionManager().loadDiscrepancyCompareInfo(inputRecord);
            // Set loaded compare data into request
            setDataBean(request, compareRs);
            // Load discrepancy compare grid header
            loadGridHeader(request);

            // Set currentGridId to discrepancyTransListGrid
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, DISCREPANCY_TRANS_GRID_ID);
            // Get the discrepancy transaction information
            RecordSet transactionRs = getTransactionManager().loadDiscrepancyTransCompareInfo(inputRecord);
            // Set loaded transaction data into request
            setDataBean(request, transactionRs, DISCREPANCY_TRANS_GRID_ID);
            // Load discrepancy transactions grid header
            loadGridHeader(request, null, DISCREPANCY_TRANS_GRID_ID, DISCREPANCY_TRANS_LAYER_ID);

            // Set currentGridId to discrepancyTransNotProcessedListGrid
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, DISCREPANCY_INTFC_GRID_ID);
            // Get the discrepancy transaction to be processed information
            RecordSet transactionIntfcRs = getTransactionManager().loadDiscrepancyIntfcInfo(inputRecord);
            // Set loaded transaction data into request
            setDataBean(request, transactionIntfcRs, DISCREPANCY_INTFC_GRID_ID);
            // Load discrepancy transactions not processed grid header
            loadGridHeader(request, null, DISCREPANCY_INTFC_GRID_ID, DISCREPANCY_INTFC_LAYER_ID);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to perform pm fm discrepancy view functionality.", e, request, mapping);
        }

        // done
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllPmFmDiscrepancy", af);
        return af;
    }

    /**
     * This method is called when there the process parameter "closePmFmDiscrepancy"
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
    public ActionForward closePmFmDiscrepancy(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                              HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "closePmFmDiscrepancy", new Object[]{mapping, form, request, response});
        String forwardString = "closePage";

        try {
            // Get PolicyHeader
            PolicyHeader policyHeader = getPolicyHeader(request);

            // Check if workflow exists, otherwise do nothing
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            if (wa.hasWorkflow(policyHeader.getPolicyNo())) {

                // Get the next state
                forwardString = wa.getNextState(policyHeader.getPolicyNo());
                setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
            }
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to perform pm fm discrepancy close.", e, request, mapping);
        }

        // done
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "closePmFmDiscrepancy", af);
        return af;
    }

    /**
     * Overrides method in BaseAction. To get the anchor column name by current grid Id.
     * This is effective only for the multiple grids.
     *
     * @return anchor column name for the current grid
     */
    public String getAnchorColumnName() {
        if (RequestStorageManager.getInstance().has(CURRENT_GRID_ID)) {
            String currentGridId = (String) RequestStorageManager.getInstance().get(CURRENT_GRID_ID);
            if (currentGridId.equals(DISCREPANCY_TRANS_GRID_ID)) {
                return getTransAnchorColumnName();
            }
            else if (currentGridId.equals(DISCREPANCY_INTFC_GRID_ID)) {
                return getIntfcAnchorColumnName();
            }
            else {
                return super.getAnchorColumnName();
            }
        }
        else {
            return super.getAnchorColumnName();
        }
    }

    /**
     * Verify Configuration
     */
    public void verifyConfig() {
        if (getTransactionManager() == null)
            throw new ConfigurationException("The required property 'transactionmanager' is missing.");
    }

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    public String getTransAnchorColumnName() {
        return m_transAnchorColumnName;
    }

    public void setTransAnchorColumnName(String transAnchorColumnName) {
        m_transAnchorColumnName = transAnchorColumnName;
    }

    public String getIntfcAnchorColumnName() {
        return m_intfcAnchorColumnName;
    }

    public void setIntfcAnchorColumnName(String intfcAnchorColumnName) {
        m_intfcAnchorColumnName = intfcAnchorColumnName;
    }

    private TransactionManager m_transactionManager;
    private String m_transAnchorColumnName;
    private String m_intfcAnchorColumnName;
    protected static final String CURRENT_GRID_ID = "currentGridId";
    protected static final String DISCREPANCY_TRANS_GRID_ID = "discrepancyTransListGrid";
    protected static final String DISCREPANCY_TRANS_LAYER_ID = "DISCREPANCY_TRANS_GH";
    protected static final String DISCREPANCY_INTFC_GRID_ID = "discrepancyInftcListGrid";
    protected static final String DISCREPANCY_INTFC_LAYER_ID = "DISCREPANCY_INTFC_GH";
}
