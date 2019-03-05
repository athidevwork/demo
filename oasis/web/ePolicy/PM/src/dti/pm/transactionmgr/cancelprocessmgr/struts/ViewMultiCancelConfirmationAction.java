package dti.pm.transactionmgr.cancelprocessmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.LogUtils;
import dti.oasis.workflowmgr.WorkflowAgent;
import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.transactionmgr.cancelprocessmgr.CancelProcessFields;
import dti.pm.transactionmgr.cancelprocessmgr.CancelProcessManager;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.logging.Logger;

/**
 * Action class handle view multi cancel confirmation page
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 18, 2011
 *
 * @author syang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/10/2012       ryzhao      133360 - If it is opened from view cancel information page, we should get records from
 *                                       request bu not from user session. The records in user session don't have the
 *                                       correct select_ind flag that user has made.
 * ---------------------------------------------------
 */
public class ViewMultiCancelConfirmationAction extends PMBaseAction {

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
        return loadAllMultiCancelConfirmation(mapping, form, request, response);
    }

    /**
     * Load the multi cancellation confirmation page.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllMultiCancelConfirmation(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllMultiCancelConfirmation", new Object[]{mapping, form, request, response});

        String forwardString = "loadResult";
        try {
            // Secure access to the page, load the Oasis Fields
            securePage(request, form);
            // Get policy header
            PolicyHeader policyHeader = getPolicyHeader(request);
            // Get input record.
            Record inputRecord = getInputRecord(request);
            // Get input recordSet from user session.
            RecordSet inputRecords = (RecordSet) UserSessionManager.getInstance().getUserSession().get(CancelProcessFields.CONFIRMATION_RECORDS);
            // System should filter the selected records if it is opened from view cancel information page.
            if(inputRecord.hasStringValue(CancelProcessFields.PROCESS_CODE) &&
                CancelProcessFields.getProcessCode(inputRecord).equals(CancelProcessFields.CancelProcessCodeValues.CONTINUE)){
                inputRecords = getInputRecordSet(request);
                inputRecords = inputRecords.getSubSet(new RecordFilter(RequestIds.SELECT_IND, "-1"));
            }

            // Process input recordSet and return a list of two record sets, the first one is for confirmation grid
            // and the second one is for transaction detail grid. 
            List rsList = getCancelProcessManager().loadAllMultiCancelConfirmation(policyHeader, inputRecords, inputRecord);
            UserSessionManager.getInstance().getUserSession().set(CancelProcessFields.CONFIRM_TRANSACTION_LIST, rsList);
            // Get the confirmation recordSet.
            RecordSet confirmRs = (RecordSet) rsList.get(0);
            // Get the transaction detail recordSet.
            RecordSet transDetailRs = (RecordSet) rsList.get(1);
            // Set loaded data into request.
            setDataBean(request, confirmRs, CONFIRMATION_GRID_ID);
            setDataBean(request, transDetailRs, TRANSACTION_DETAIL_GRID_ID);
            // Load confirmation grid header
            loadGridHeader(request, null, CONFIRMATION_GRID_ID, CONFIRMATION_GRID_LAYER_ID);
            // Set currentGridId to transDetailListGrid before load transaction detail gird header
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, TRANSACTION_DETAIL_GRID_ID);
            // Load transaction detail grid header
            loadGridHeader(request, null, TRANSACTION_DETAIL_GRID_ID, TRANSACTION_DETAIL_GRID_LAYER_ID);

            // Publish the input record and summary record of confirmation record set.
            Record output = new Record();
            output.setFields(confirmRs.getSummaryRecord());
            output.setFields(inputRecord);
            publishOutputRecord(request, output);
            // Load the list of values after loading the data.
            loadListOfValues(request, form);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the multi cancel confirmation.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllMultiCancelConfirmation", af);
        return af;
    }

    /**
     * Process the selected risk/coverage/coverage class/component.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward processMultiCancelConfirmation(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "processMultiCancelConfirmation", new Object[]{mapping, form, request, response});

        try {
            // Secures access to the page.
            securePage(request, form, false);
            // Get policy header.
            PolicyHeader policyHeader = getPolicyHeader(request);
            // Get input record.
            Record inputRecord = getInputRecord(request);
            // If there are more than one cancellation transaction, system starts workflow to process multi cancellation transaction.
            // Set isInitTransB to Y, it is used to determine whether it is the first time to call processMultiCancelConfirmation.
            CancelProcessFields.setIsInitTransB(inputRecord, YesNoFlag.Y.toString());
            // Process multi cancellation transactions.
            getCancelProcessManager().processMultiCancelConfirmation(policyHeader, inputRecord);
            // Remove previous confirmation recordSet from user session and set the parsed cancellation transactions list to user session.
            UserSessionManager.getInstance().getUserSession().remove(CancelProcessFields.CONFIRMATION_RECORDS);
            // Write empty response.
            writeEmptyAjaxXMLResponse(response);
        }
        catch (ValidationException v) {
            // Handle the validation exception
            handleValidationExceptionForAjax(v, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Process multi cancel confirmation failed", e, response);
        }
        l.exiting(getClass().getName(), "processMultiCancelConfirmation");
        return null;
    }

    /**
     * Process next transaction for multi cancellation.
     * Get all the cancel transactions and remove the processing one from session since it will n't be used.
     * If no any cancel transaction found, system considers all the transactions have been cancelled successfully
     * and exists the current workflow.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward processNextCancelTransaction(
        ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "processNextCancelTransaction", new Object[]{mapping, form, request, response});
        String forwardString;
        PolicyHeader policyHeader = null;
        try {
            // Secures access to the page.
            securePage(request, form, false);
            // Get policy header.
            policyHeader = getPolicyHeader(request);
            // Process multi cancellation transactions.
            getCancelProcessManager().processMultiCancelConfirmation(policyHeader, getInputRecord(request));
            // Check workflow.
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            if (wa.hasWorkflow(policyHeader.getPolicyNo())) {
                // Get the next state.
                forwardString = wa.getNextState(policyHeader.getPolicyNo());
                setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
            }
            else {
                throw new AppException(AppException.UNEXPECTED_ERROR,
                    "Failed to determine workflow for save multi cancellation transaction Official.");
            }
        }
        catch (ValidationException e) {
            forwardString = null;
            // Check workflow.
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            if (wa.hasWorkflow(policyHeader.getPolicyNo())) {
                wa.clearWorkflow(policyHeader.getPolicyNo());
            }
            // Handle the validation exception
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR,
                "Failed to process next cancellation transaction.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "processNextCancelTransaction", af);
        return af;
    }

    /**
     * Overrides method in BaseAction. To get the anchor column name by current grid Id.
     * This is effective only for the multiple grids.
     *
     * @return anchor column name for the current grid
     */
    public String getAnchorColumnName() {
        Logger l = LogUtils.enterLog(getClass(), "getAnchorColumnName");
        String anchorName;
        if (RequestStorageManager.getInstance().has(CURRENT_GRID_ID)) {
            String currentGridId = (String) RequestStorageManager.getInstance().get(CURRENT_GRID_ID);
            if (currentGridId.equals(TRANSACTION_DETAIL_GRID_ID)) {
                anchorName = getTransDetailAnchorColumnName();
            }
            else {
                anchorName = super.getAnchorColumnName();
            }
        }
        else {
            anchorName = super.getAnchorColumnName();
        }

        l.exiting(getClass().getName(), "getAnchorColumnName", anchorName);
        return anchorName;
    }

    /**
     * Configuration constructor and access methods.
     */
    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getTransDetailAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'transDetailAnchorColumnName' is missing.");
        if (getCancelProcessManager() == null)
            throw new ConfigurationException("The required property 'cancelProcessManager' is missing.");
    }

    public String getTransDetailAnchorColumnName() {
        return m_transDetailAnchorColumnName;
    }

    public void setTransDetailAnchorColumnName(String transDetailAnchorColumnName) {
        m_transDetailAnchorColumnName = transDetailAnchorColumnName;
    }

    public CancelProcessManager getCancelProcessManager() {
        return m_cancelProcessManager;
    }

    public void setCancelProcessManager(CancelProcessManager cancelProcessManager) {
        m_cancelProcessManager = cancelProcessManager;
    }

    private CancelProcessManager m_cancelProcessManager;
    private String m_transDetailAnchorColumnName;
    protected static final String CURRENT_GRID_ID = "currentGridId";
    protected static final String CONFIRMATION_GRID_ID = "confirmationListGrid";
    protected static final String TRANSACTION_DETAIL_GRID_ID = "transDetailListGrid";
    protected static final String CONFIRMATION_GRID_LAYER_ID = "PM_MULTI_CANCEL_CONFIRM_GH";
    protected static final String TRANSACTION_DETAIL_GRID_LAYER_ID = "PM_MULTI_CANCEL_CONFIRM_TRANS_GH";
}
