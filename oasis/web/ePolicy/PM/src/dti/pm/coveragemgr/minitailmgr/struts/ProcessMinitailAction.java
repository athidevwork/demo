package dti.pm.coveragemgr.minitailmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.util.LogUtils;
import dti.oasis.workflowmgr.WorkflowAgent;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.coveragemgr.minitailmgr.MinitailManager;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.riskmgr.RiskFields;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * This class is an action for process mini tail
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 20, 2007
 *
 * @author zlzhu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/20/2007       zlzhu       Created
 * 11/25/2010       bhong       114074 - passed additional parameter "inputRecord" in loadAllMinitailRiskCoverage
 *                              and loadAllMinitail methods.
 * 04/26/2012       xnie        132237 - Modified saveAllMinitail
 *                              1) Set current grid back to parent grid when save minitail validation is not passed.
 *                              2) Passed parent grid to imp for validation.
 * 04/25/2013       jshen       143625 - Modified loadAllMinitail method to populate riskBaseRecordId to the page
 * 11/05/2013       jyang2      158679 - Modified saveAllMinitail method to pass policyHeader to MinitailManager.
 *                                       saveAllMinitial.
 * ---------------------------------------------------
 */

public class ProcessMinitailAction extends PMBaseAction {

    /**
     * do this process when no process is specified
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadAllMinitail(mapping, form, request, response);
    }

    /**
     * load all the mini tail data(for bottom grid)
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward loadAllMinitail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllMinitail", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        Record output;
        try {
            securePage(request, form);
            PolicyHeader policyHeader = getPolicyHeader(request, false);
            Record inputRecord = getInputRecord(request);
            // Load the risk coverages
            RecordSet rs = getMinitailManager().loadAllMinitailRiskCoverage(inputRecord, policyHeader);
            // Set loaded risk coverage data into request
            setDataBean(request, rs);
            // Load the mini tail
            RecordSet childRs = (RecordSet) request.getAttribute(CHILD_GRID_RECORD_SET);
            if (childRs == null) {
                childRs = this.getMinitailManager().loadAllMinitail(inputRecord, policyHeader);
                if (rs.getSize() <= 0)
                    MessageManager.getInstance().addErrorMessage("pm.processMinitail.nodata.error");
            }
         
            // Set loaded mini tail data into request
            setDataBean(request, childRs, CHILD_GRID_ID);
            // Make the Summary Record available for output
            output = rs.getSummaryRecord();
            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);
            // Load coverage grid header
            loadGridHeader(request);
            // Set currentGridId to componentListGrid before load component gird header
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, CHILD_GRID_ID);
            // Load the list of values after loading the data
            loadListOfValues(request, form);
            // Load component grid header
            loadGridHeader(request, null, CHILD_GRID_ID, CHILD_GRID_LAYER_ID);
            // Populate messages for javascirpt
            addJsMessages();

            String riskBaseRecordId = inputRecord.hasStringValue(RiskFields.RISK_BASE_RECORD_ID) ? RiskFields.getRiskBaseRecordId(inputRecord) : "";
            request.setAttribute(RiskFields.RISK_BASE_RECORD_ID, riskBaseRecordId);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the minitail.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllMinitail", af);
        return af;
    }

    /**
     * save mini tail data
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward saveAllMinitail(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                         HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveAllMinitail", new Object[]{mapping, form, request, response});
        String forwardString = "saveResult";
        RecordSet inputRecords = null;
        RecordSet parentInputRecords = null;
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);
                // set currentGridId to child Grid before get input recordSet for child grid
                RequestStorageManager.getInstance().set(CURRENT_GRID_ID, CHILD_GRID_ID);
                // Map textXML to RecordSet for input
                inputRecords = getInputRecordSet(request, CHILD_GRID_ID);
                // Map parent textXML to RecordSet for input
                parentInputRecords = getInputRecordSet(request, PARENT_GRID_ID);
                // Get PolicyHeader
                PolicyHeader policyHeader = getPolicyHeader(request);
                // Save the changes
                getMinitailManager().saveAllMinitail(inputRecords, parentInputRecords, policyHeader);

                // Check if workflow exists, otherwise just forward to the original
                WorkflowAgent wa = WorkflowAgentImpl.getInstance();
                if (wa != null && wa.hasWorkflow(policyHeader.getPolicyNo())) {
                    // Get the next state
                    forwardString = wa.getWorkflowCurrentState(policyHeader.getPolicyNo());
                    setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
                }

                // Set back to parent grid
                RequestStorageManager.getInstance().set(CURRENT_GRID_ID, PARENT_GRID_ID);
            }
        }
        catch (ValidationException ve) {
            // Set back to parent grid
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, PARENT_GRID_ID);
            // Save the input records into request, so it does not
            // to get recordset again.
            request.setAttribute(CHILD_GRID_RECORD_SET, inputRecords);
            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to save the minitail page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveAllMinitail", af);
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
    public ActionForward closePage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                   HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "closePage", new Object[]{mapping, form, request, response});
        String forwardString = "closePage";
        try {
            // Get PolicyHeader
            PolicyHeader policyHeader = getPolicyHeader(request);
            // Check if workflow exists, otherwise just forward to the original
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            if (wa.hasWorkflow(policyHeader.getPolicyNo())) {
                // Check if free mini tail exists and
                // Set the transition flag
                if (getMinitailManager().isFreeMiniTailExist(policyHeader)) {
                    wa.setWorkflowTransitionParameter(policyHeader.getPolicyNo(), "FMTCREATED");
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

    /**
     * do business rule validation for mini tail(bottom grid)
     * <p/>
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @throws Exception if there are some IO problems
     */
    public void getMinitailEditable(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                    HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getMinitailEditable", new Object[]{mapping, form, request, response});
        PolicyHeader policyHeader = getPolicyHeader(request);
        Record inputRecord = getInputRecord(request);
        try {
            //editable
            Record rec = getMinitailManager().getMinitailEditable(policyHeader, inputRecord);
            writeAjaxXmlResponse(response, rec, true);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to validate mini tail.", e, response);
            l.throwing(getClass().getName(), "getMinitailEditable", e);
        }
        l.exiting(getClass().getName(), "getMinitailEditable");
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.processMinitail.nodata.error");
        MessageManager.getInstance().addJsMessage("pm.processMinitail.ratingBasis.error");
        MessageManager.getInstance().addJsMessage("pm.processMinitail.onSave.error");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    /**
     * verify config
     */
    public void verifyConfig() {
        if (getMinitailManager() == null)
            throw new ConfigurationException("The required property 'minitailManager' is missing.");
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getChildAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'childAnchorColumnName' is missing.");
        if (getPolicyManager() == null)
            throw new ConfigurationException("The required property 'policyManager' is missing.");
    }

    /**
     *
     */
    public ProcessMinitailAction() {
    }

    /**
     * get mini tail manager
     *
     * @return mini tail manager
     */
    public MinitailManager getMinitailManager() {
        return m_minitailManager;
    }

    /**
     * set mini tail manager
     * <p/>
     *
     * @param minitailManager the mini tail manager
     */
    public void setMinitailManager(MinitailManager minitailManager) {
        m_minitailManager = minitailManager;
    }

    /**
     * Overrides method in BaseAction. To get the anchor column name by current grid Id.
     * This is effective only for the multiple grids.
     * <p/>
     *
     * @return anchor column name for the current grid
     */
    public String getAnchorColumnName() {
        if (RequestStorageManager.getInstance().has(CURRENT_GRID_ID)) {
            String currentGridId = (String) RequestStorageManager.getInstance().get(CURRENT_GRID_ID);
            if (currentGridId.equals(CHILD_GRID_ID)) {
                return getChildAnchorColumnName();
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
     * get the anchor column for the bottom grid
     * <p/>
     *
     * @return anchor column
     */
    public String getChildAnchorColumnName() {
        return m_childAnchorColumnName;
    }

    /**
     * set the anchor column for the bottom grid
     * <p/>
     *
     * @param childAnchorColumnName anchor column name
     */
    public void setChildAnchorColumnName(String childAnchorColumnName) {
        m_childAnchorColumnName = childAnchorColumnName;
    }

    protected static final String DATE_CHANGE_ALLOWED = "dateChangeAllowedB";
    protected static final String CURRENT_GRID_ID = "currentGridId";
    protected static final String PARENT_GRID_ID = "riskCoverageGrid";
    protected static final String CHILD_GRID_ID = "minitailGrid";
    protected static final String CHILD_GRID_LAYER_ID = "PM_MINI_TAIL_B_GH";
    protected static final String CHILD_GRID_RECORD_SET = "childGridRecordSet";
    private String m_childAnchorColumnName;
    private MinitailManager m_minitailManager;
}