package dti.pm.tailmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.http.RequestIds;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.util.LogUtils;
import dti.oasis.workflowmgr.WorkflowAgent;
import dti.pm.busobjs.ComponentOwner;
import dti.pm.busobjs.PolicyViewMode;
import dti.pm.busobjs.TransactionCode;
import dti.pm.busobjs.TransactionTypeCode;
import dti.pm.busobjs.TransactionStatus;
import dti.pm.core.request.RequestStorageIds;
import dti.pm.core.struts.AddSelectIndLoadProcessor;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.componentmgr.ComponentManager;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.tailmgr.TailFields;
import dti.pm.tailmgr.TailManager;
import dti.pm.tailmgr.impl.TailProcessCode;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Action class for Maintain Tails.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 21, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/18/2010       gxc         Issue 106055
 *                              Added method saveTailCharge
 *                              Modified to set the value in captureFinancePercentage during accept process based on
 *                              system parameter value of Y.
 * 09/01/2010       syang       Issue 111417 - Modified loadAllTail() to remove the validation of related policy.
 * 10/1/2010        gxc         Issue 111905/111953 Modified saveTailCharge to display the standard error page when
 *                              there is a DB failure.
 * 12/03/2010       syang       115223 - Modified performTailProcess() to load all tails in WIP for save/cancel/decline/activate/reinstate.
 * 03/15/2011       jshen       Issue 118616 - Allow modify Gross Premium field in prior term.
 * 03/25/2011       syang       116984 - Modified performTailProcess() to load all WIP tails for Adjust Limit.
 * 09/14/2011       wfu         124652 - Modified performTailProcess to reset records after validation failed.
 * 09/21/2011       syang       125377 - Modified performTailProcess() to set policyViewMode to WIP for update.
 * 11/19/2012       tcheng      139135 - Modified validateTailDelta() to make tail transaction save successfully.
 * 01/22/2013       adeng       141183 - Modified loadAllTail() to get note field's setting of visible in WebWB and
 *                                       set it into input record.
 * 01/25/2013       adeng       141183 - Roll backed last change.
 * 04/10/2014       xnie        153450 - Modified validateTailData() to call manager's validateTailData() only when
 *                                       current policy is in WIP trans.
 * 06/13/2018       wrong       192557 - Modified saveTailCharge(), addManualTail() and validateTailData() to call
 *                                       hasValidSaveToken() to be used for CSRFInterceptor.
 * 09/25/2018       wrong       195793 - Added needToHandleExitWorkFlow and selectedIds indicators for long running case.
 * ---------------------------------------------------
 */

public class MaintainTailAction extends PMBaseAction {

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
        return loadAllTail(mapping, form, request, response);
    }


    /**
     * load page to show tail information
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllTail(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllTail", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            //get parameters from request
            Record inputRecord = getInputRecord(request);

            // Get the policy header from the request
            PolicyHeader policyHeader = getPolicyHeader(request);

            // Attempt to get the gridRecordSet out of the request.  This will be populated
            // on a validation error to provide data to reload the page.
            RecordSet tailRs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);

            request.setAttribute("captureFinancePercentage", getTailManager().isCaptureFinancePercentageRequired(policyHeader));

            if (tailRs == null) {
                RecordLoadProcessor selectIndProcessor = AddSelectIndLoadProcessor.getInstance();
                tailRs = getTailManager().loadAllTail(policyHeader, inputRecord, selectIndProcessor);
            }
            setDataBean(request, tailRs);

            if (tailRs.getSize() == 0) {
                MessageManager.getInstance().addErrorMessage("pm.maintainTail.noData.error");
            }

            // Attempt to get the gridRecordSet out of the request.  This will be populated
            // on a validation error to provide data to reload the page.
            RecordSet tailComponentRs = (RecordSet) request.getAttribute(COMP_GRID_RECORD_SET);
            // Load the tail component data
            if (tailComponentRs == null || tailComponentRs.getSize() == 0) {
                tailComponentRs = getComponentManager().loadAllComponent(policyHeader, inputRecord, ComponentOwner.TAIL, tailRs);
            }

            setDataBean(request, tailComponentRs, GRID_RECORD_SET_COMPONENT);

            Record output = policyHeader.toRecord();
            output.setFields(inputRecord, false);
            output.setFields(tailRs.getSummaryRecord(), true);
            output.setFields(tailComponentRs.getSummaryRecord(), true);
            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);

            // Load the list of values after loading the data
            loadListOfValues(request, form);

            // Load tail grid header
            loadGridHeader(request);

            // Set currentGridId to tailComponentListGrid before load component gird header
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, GRID_RECORD_SET_COMPONENT);
            // Load component grid header
            loadGridHeader(request, null, GRID_RECORD_SET_COMPONENT, COMPONENT_GRID_LAYER_ID);

            // add js messages
            addJsMessages();
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the tail page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllTail", af);
        return af;
    }


    /**
     * save tail and component information
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveAllTail(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveAllTail", new Object[]{mapping, form, request, response});
        String forwardString = "loadAllTail";
        RecordSet inputRecords = null;
        RecordSet componentInputRecords = null;
        String validateResult = "VALID";

        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form);

                PolicyHeader policyHeader = getPolicyHeader(request);
                Record inputRecord = getInputRecord(request);

                // First save coverage records
                // Map coverage textXML to RecordSet for input
                inputRecords = getInputRecordSet(request, GRID_RECORD_SET_TAIL);

                // set currentGridId to componentListGrid before get input recordSet for component grid
                RequestStorageManager.getInstance().set(CURRENT_GRID_ID, GRID_RECORD_SET_COMPONENT);
                // Map component textXML to RecordSet for input
                componentInputRecords = getInputRecordSet(request, GRID_RECORD_SET_COMPONENT);
                // Set back to coverageListGrid
                RequestStorageManager.getInstance().set(CURRENT_GRID_ID, RequestIds.GRID_RECORD_SET);

                // Save the coverage changes
                validateResult = getTailManager().processSaveAllTailAndComponent(
                    policyHeader, inputRecord, inputRecords, componentInputRecords);

                if (inputRecord.getStringValue("needToHandleExitWorkFlow").equals("Y")) {
                    request.setAttribute("needToHandleExitWorkFlow", "Y");
                    request.setAttribute("selectedIds", inputRecord.getStringValue("selectedIds"));
                }

                if (!validateResult.equals("VALID")) {
                    // Save the input records into request
                    RequestStorageManager.getInstance().set(CURRENT_GRID_ID, GRID_RECORD_SET_TAIL);
                    request.setAttribute(dti.pm.core.http.RequestIds.GRID_RECORD_SET, inputRecords);
                    request.setAttribute(COMP_GRID_RECORD_SET, componentInputRecords);
                }
            }
        }
        catch (ValidationException ve) {
            // Save the input records into request
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, GRID_RECORD_SET_TAIL);
            request.setAttribute(dti.pm.core.http.RequestIds.GRID_RECORD_SET, inputRecords);
            request.setAttribute(COMP_GRID_RECORD_SET, componentInputRecords);
            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to save or validate the tail page.", e, request, mapping);
            request.setAttribute(dti.pm.core.http.RequestIds.POLICY_VIEW_MODE, PolicyViewMode.OFFICIAL.getName());
            request.setAttribute("refreshPage", "Y");
            forwardString = "closePage";
        }

        request.setAttribute("validateResult", validateResult);
        // Return the forward
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveAllTail", af);
        return af;
    }

    /**
     * validate tail data
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward validateTailData(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "validateTailData",
            new Object[]{mapping, form, request, response});

        try {
            if (hasValidSaveToken(request)) {
                // Secures access to the page
                securePage(request, form, false);

                //get policy header from request
                PolicyHeader policyHeader = getPolicyHeader(request);

                Record inputRecord = this.getInputRecord(request);

                //set the default validate result
                String validateResult = "VALID";
                TransactionCode lastTranCode = policyHeader.getLastTransactionInfo().getTransactionCode();
                //if there is WIP and the transaction is not a Cancellation transaction, delete WIP
                if (policyHeader.getPolicyIdentifier().ownLock() && policyHeader.isWipB() && (lastTranCode.isTailAccept() || lastTranCode.isTailActivate() || lastTranCode.isTailCancel()
                    || lastTranCode.isTailDecline() || lastTranCode.isTailDeclins() || lastTranCode.isTailEndorse() || lastTranCode.isTailReinstate())) {
                    //delete WIP and unlock policy
                    getTransactionManager().deleteWipTransaction(policyHeader, inputRecord);
                }
                //if the policy is in WIP mode and last transaction is not tail transactions,
                // it will validate the tail data.
                else if (policyHeader.isWipB()) {
                    validateResult = getTailManager().validateTailData(policyHeader);
                }
                Record outputRecord = new Record();
                outputRecord.setFieldValue("validateResult", validateResult);
                writeAjaxXmlResponse(response, outputRecord);
            }
        }
        catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "validate failed", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "validateTailData", af);
        return af;
    }

    /**
     * validate tail process before process tail transaction for decline/active/reinstate/reactivate
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward validateTailProcess(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "validateTailProcess",
            new Object[]{mapping, form, request, response});
        RecordSet inputRecords = null;
        RecordSet componentInputRecords;

        String forwardString = "loadAllTail";

        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            PolicyHeader policyHeader = getPolicyHeader(request);

            Record inputRecord = getInputRecord(request);

            TailProcessCode tailProcessCode = TailProcessCode.getInstance(TailFields.getProcessCode(inputRecord));

            // Map coverage textXML to RecordSet for input
            inputRecords = getInputRecordSet(request, GRID_RECORD_SET_TAIL);

            // set currentGridId to componentListGrid before get input recordSet for component grid
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, GRID_RECORD_SET_COMPONENT);
            // Map component textXML to RecordSet for input
            componentInputRecords = getInputRecordSet(request, GRID_RECORD_SET_COMPONENT);

            //validate processed tail records
            RecordSet processedTailRs =
                getTailManager().validateTailProcess(policyHeader, inputRecord, inputRecords);
            String validateResult = processedTailRs.getSummaryRecord().getStringValue("validateResult");

            if (validateResult.equals("INVALID")) {
                // store the processedTailRS as a request attribute, and redirect to viewValidationError action
                processedTailRs.getSummaryRecord().setFields(inputRecord, false);
                request.setAttribute(RequestIds.GRID_RECORD_SET, processedTailRs);

                setForwardParameter(request, dti.pm.core.http.RequestIds.PROCESS, "unspecified");

                forwardString = "viewValidationErrors";
            }
            else {
                // Save the input records of current request back into request
                RequestStorageManager.getInstance().set(CURRENT_GRID_ID, GRID_RECORD_SET_TAIL);
                request.setAttribute(dti.pm.core.http.RequestIds.GRID_RECORD_SET, inputRecords);
                request.setAttribute(COMP_GRID_RECORD_SET, componentInputRecords);
            }

            Record transactionParmRec = getTailManager().getDefaultTransactionParms(policyHeader, inputRecord, processedTailRs);
            request.setAttribute("accountingDate", transactionParmRec.getFieldValue("accountingDate"));
            request.setAttribute("tailTransactionCode", TransactionFields.getTransactionCode(transactionParmRec));
            request.setAttribute("processCode", tailProcessCode);
            // Issue 118616: keep tailProcessCode in the hidden field processAction,
            // this is because processCode field value will be changed during processing.
            request.setAttribute("processAction", tailProcessCode);

            if (tailProcessCode.isCancel()) {
                request.setAttribute("captureCancellationDetail", "Y");
            }
            else {
                request.setAttribute("captureTransactionDetail", "Y");

                if (tailProcessCode.isAccept() &&
                    YesNoFlag.getInstance(getTailManager().isCaptureFinancePercentageRequired(policyHeader)).booleanValue()) {
                    request.setAttribute("captureFinancePercentage", "Y");
                }
            }
        }
        catch (ValidationException e) {
            // Handle the validation exception
            handleValidationException(e, request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "validate failed", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "validateTailProcess", af);
        return af;
    }


    /**
     * process tail transaction
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performTailProcess(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "performTailProcess",
            new Object[]{mapping, form, request, response});
        String forwardString = "loadAllTail";
        ActionForward af = mapping.findForward(forwardString);
        RecordSet inputRecords = null;
        TailProcessCode tailProcessCode = null;

        if (hasValidSaveToken(request)) {
            try {
                // Secure access to the page, load the Oasis Fields without loading the LOVs,
                // and map the input parameters to the Fields
                securePage(request, form);

                PolicyHeader policyHeader = getPolicyHeader(request);

                Record inputRecord = getInputRecord(request);
                // First save coverage records
                // Map coverage textXML to RecordSet for input
                inputRecords = getInputRecordSet(request, GRID_RECORD_SET_TAIL);
                tailProcessCode = TailProcessCode.getInstance(TailFields.getProcessCode(inputRecord));

                //perform tail transaction
                getTailManager().performTailProcess(policyHeader, inputRecord, inputRecords);

                // System should load all tail in WIP for save/cancel/decline/activate/reinstate.
                if (tailProcessCode.isSave() || tailProcessCode.isCancel() ||
                    tailProcessCode.isDecline() || tailProcessCode.isActivate() || tailProcessCode.isReinstate() || tailProcessCode.isAdjLimit()
                    || tailProcessCode.isUpdate()) {
                    // load all tail in WIP
                    setForwardParameter(request, dti.pm.core.http.RequestIds.POLICY_VIEW_MODE, PolicyViewMode.WIP.getName());
                    af = mapping.findForward(forwardString);
                    //the same request, need to remove the cached policyHeader
                    RequestStorageManager.getInstance().remove(RequestStorageIds.POLICY_HEADER);
                }
                // Fix 96270.
                if(tailProcessCode.isAccept()){
                    // Save the input records of current request back into request
                    RequestStorageManager.getInstance().set(CURRENT_GRID_ID, GRID_RECORD_SET_TAIL);
                    request.setAttribute(dti.pm.core.http.RequestIds.GRID_RECORD_SET, inputRecords);
                }
            }
            catch (ValidationException ve) {
                // If validation failed when updating tail, reset record set for re-selecting.
                if (tailProcessCode != null && tailProcessCode.isUpdate()) {
                    // Save the input records of current request back into request
                    RequestStorageManager.getInstance().set(CURRENT_GRID_ID, GRID_RECORD_SET_TAIL);
                    request.setAttribute(dti.pm.core.http.RequestIds.GRID_RECORD_SET, inputRecords);    
                }
                // Handle the validation exception
                handleValidationException(ve, request);
            }
            catch (Exception e) {
                forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "process tail transaction failed", e, request, mapping);
                af = mapping.findForward(forwardString);
            }
        }

        l.exiting(getClass().getName(), "performTailProcess", af);
        return af;
    }

    /**
     * This method is called when there the process parameter "validateTailDelta"
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
    public ActionForward validateTailDelta(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "validateTailDelta", new Object[]{mapping, form, request, response});

        String forwardString;

        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form, false);

            // Get PolicyHeader
            PolicyHeader policyHeader = getPolicyHeader(request);

            //validate tail delta
            String validateResult = getTailManager().validateTailDelta(policyHeader.toRecord()) ? "VALID" : "INVALID";
            // Check if workflow exists, otherwise throw application exception
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            if (wa.hasWorkflow(policyHeader.getPolicyNo())) {

                // Set the flag so that workflow knows about the validation status
                wa.setWorkflowTransitionParameter(policyHeader.getPolicyNo(), validateResult);

                // Get the next state
                forwardString = wa.getNextState(policyHeader.getPolicyNo());
                setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
            }
            else {
                throw new AppException(AppException.UNEXPECTED_ERROR,
                    "Failed to determine workflow for Validate tail delta.");
            }
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR,
                "Failed to validate tail delta.", e, request, mapping);
        }

        // done
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "validateTailDelta", af);
        return af;
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.maintainTail.noTailSelectedError");

        // Component messages
        MessageManager.getInstance().addJsMessage("pm.maintainComponent.effectiveToDate.rule1.error");
        MessageManager.getInstance().addJsMessage("pm.addComponent.noCoverage.error");
        MessageManager.getInstance().addJsMessage("pm.maintainReinstate.confirm.continue");
        MessageManager.getInstance().addJsMessage("pm.addComponent.duplicated.error");
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
            if (currentGridId.equals(GRID_RECORD_SET_COMPONENT)) {
                anchorName = getComponentAnchorColumnName();
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
     * add manual tail coverage
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward addManualTail(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "addManualTail",
            new Object[]{mapping, form, request, response});

        try {
            if (hasValidSaveToken(request)) {
                // Secures access to the page
                securePage(request, form, false);

                //get policy header from request
                PolicyHeader policyHeader = getPolicyHeader(request);

                Record inputRecord = getInputRecord(request);

                getTailManager().addManualTail(policyHeader,inputRecord);
                writeEmptyAjaxXMLResponse(response);
            }
        }
        catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            handleErrorForAjax("pm.selectTail.addManualTail.error", "add Manual Tail failed", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "addManualTail", af);
        return af;
    }
     /**
     * save tail coverage finance charge
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
     public ActionForward saveTailCharge(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveTailCharge",
            new Object[]{mapping, form, request, response});
        String forwardString = "loadAllTail";
        try {
            if (hasValidSaveToken(request)) {
                // Secures access to the page
                securePage(request, form, false);

                //get policy header from request
                PolicyHeader policyHeader = getPolicyHeader(request);

                Record inputRecord = getInputRecord(request);

                // Map coverage textXML to RecordSet for input
                RecordSet inputRecords = null;
                inputRecords = getInputRecordSet(request, GRID_RECORD_SET_TAIL);

                getTailManager().saveTailCharge(policyHeader,inputRecord,inputRecords);
            }
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Save tail finance charge failed.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveTailCharge", af);
        return af;
    }

    public void verifyConfig() {
        if (getTailManager() == null)
            throw new ConfigurationException("The required property 'tailManager' is missing.");
        if (getComponentManager() == null)
            throw new ConfigurationException("The required property 'componentManager' is missing.");
        if (getTransactionManager() == null)
            throw new ConfigurationException("The required property 'transactionManager' is missing.");
        if (getComponentAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'componentAnchorColumnName' is missing.");
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
    }

    public TailManager getTailManager() {
        return m_tailManager;
    }

    public void setTailManager(TailManager tailManager) {
        m_tailManager = tailManager;
    }

    public String getComponentAnchorColumnName() {
        return m_componentAnchorColumnName;
    }

    public void setComponentAnchorColumnName(String componentAnchorColumnName) {
        m_componentAnchorColumnName = componentAnchorColumnName;
    }


    public ComponentManager getComponentManager() {
        return m_componentManager;
    }

    public void setComponentManager(ComponentManager componentManager) {
        m_componentManager = componentManager;
    }


    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    private static final String GRID_RECORD_SET_TAIL = "coverageListGrid";
    private static final String GRID_RECORD_SET_COMPONENT = "componentListGrid";
    protected static final String CURRENT_GRID_ID = "currentGridId";
    protected static final String COMPONENT_GRID_LAYER_ID = "COMPONENT_LAYER_GH";
    protected static final String TAIL_GRID_LAYER_ID = "TAIL_LAYER_GH";
    protected static final String COMP_GRID_RECORD_SET = "compGridRecordSet";

    private TransactionManager m_transactionManager;
    private TailManager m_tailManager;
    private ComponentManager m_componentManager;
    private String m_componentAnchorColumnName;


}
