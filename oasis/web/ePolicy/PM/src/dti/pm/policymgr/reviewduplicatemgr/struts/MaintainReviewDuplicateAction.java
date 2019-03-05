package dti.pm.policymgr.reviewduplicatemgr.struts;

import dti.cs.renewalcandidatemgr.RenewalCandidateFields;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.http.RequestIds;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.util.LogUtils;
import dti.oasis.workflowmgr.WorkflowAgent;
import dti.pm.busobjs.TransactionStatus;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.reviewduplicatemgr.ReviewDuplicateFields;
import dti.pm.policymgr.reviewduplicatemgr.ReviewDuplicateManager;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for Renewal Candidate.
 * <p/>
 *
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:   June 28, 2016
 *
 * @author ssheng
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/28/2016        ssheng      164927 - Created this action class for Quote Import enhancement.
 * 10/17/2016        ssheng      180207 -
 * 06/13/2018        wrong       192557 - Modified saveReviewDuplicate() to call hasValidSaveToken() to be used for
 *                                        CSRFInterceptor.
 * 07/13/2018        wrong       194455 - Modified display() to process no Roster risk case to avoid page error when
 *                                        loading page.
 * ---------------------------------------------------
 */
public class MaintainReviewDuplicateAction extends PMBaseAction {
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
        return display(mapping, form, request, response);
    }

    /**
     * load the file review duplicates page
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     */
    public ActionForward display(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "display", new Object[]{form, request, response});
        }

        String forwardString = "loadResult";

        try {
            // Secure page
            securePage(request, form);

            // Gets policy header
            PolicyHeader policyHeader = getPolicyHeader(request);

            // Load the roster risk recordSet from request.
            RecordSet rosterRiskRs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            // Load the cis duplicate recordSet from request.
            RecordSet cisDupRs = (RecordSet) request.getAttribute(CIS_DUP_RECORD_SET);

            if (rosterRiskRs == null) {
                rosterRiskRs = getReviewDuplicateManager().loadAllRosterRisk(policyHeader);
            }

            if (cisDupRs == null) {
                if (rosterRiskRs.getSize() <= 0) {
                    MessageManager.getInstance().addInfoMessage("pm.reviewDuplicate.rosterRisk.noDataFound");
                    Record r = new Record();
                    cisDupRs = getReviewDuplicateManager().loadAllCISDuplicate(r);
                }
                else {
                    for (Record r : rosterRiskRs.getRecordList()) {
                        if (cisDupRs == null) {
                            cisDupRs = getReviewDuplicateManager().loadAllCISDuplicate(r);
                        }
                        else {
                            cisDupRs.addRecords(getReviewDuplicateManager().loadAllCISDuplicate(r));
                        }
                    }
                }

            }

            for(int i = 0; cisDupRs != null && i < cisDupRs.getSize(); i++ ) {
                cisDupRs.getRecord(i).setFieldValue("rownum", i + 1);
            }

            if (cisDupRs == null || cisDupRs.getSubSet(new RecordFilter(ReviewDuplicateFields.CIS_SAVED_B, YesNoFlag.N)).getSize() == 0) {
                MessageManager.getInstance().addInfoMessage("pm.reviewDuplicate.processResult.info");
                request.setAttribute(ReviewDuplicateFields.NO_DUPLICATE, "Y");
            }else{
                request.setAttribute(ReviewDuplicateFields.NO_DUPLICATE, "N");
            }

            // Set loaded data into request.
            setDataBean(request, rosterRiskRs);
            setDataBean(request, cisDupRs, CIS_DUP_GRID_ID);

            // Publish the output record for use by the Oasis Tags and JSP.
            publishOutputRecord(request, rosterRiskRs.getSummaryRecord());

            // Set currentGridId to every gridID on page before load gird header
            // then load grid header for each grid.
            loadGridHeader(request);

            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, CIS_DUP_GRID_ID);
            loadGridHeader(request, null, CIS_DUP_GRID_ID, CIS_DUP_LAYER_ID);

            // Load the list of values after loading the data.
            loadListOfValues(request, form);

            addJsMessages();
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load review duplicates page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "display", af);
        }
        return af;
    }

    /**
     * Save all updated Roster Risk.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward saveReviewDuplicate(ActionMapping mapping, ActionForm form,
                                              HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveReviewDuplicate", new Object[]{mapping, form, request, response});
        }

        String forwardString = "saveResult";
        RecordSet rosterRiskInputRecords = null;
        RecordSet cisDupInputRecords = null;

        // Pull the policy header from the request
        PolicyHeader policyHeader = getPolicyHeader(request);
        try {
            if (hasValidSaveToken(request)) {
                //If the request has valid save token, then proceed with save; if not forward to load page.
                // Secure access to the page without loading the Oasis Fields.
                securePage(request, form, false);
                // Map roster risk textXML to RecordSet for input.
                rosterRiskInputRecords = getInputRecordSet(request, ROSTER_RISK_GRID_ID);
                // set currentGridId to cisDuplicateListGrid before get input recordSet for team member grid.
                RequestStorageManager.getInstance().set(CURRENT_GRID_ID, CIS_DUP_GRID_ID);
                // Map cis duplicate textXML to RecordSet for input.
                cisDupInputRecords = getInputRecordSet(request, CIS_DUP_GRID_ID);
                // Save all the roster risk and cis duplicate changes.
                getReviewDuplicateManager().saveReviewDuplicate(policyHeader, rosterRiskInputRecords, cisDupInputRecords);
                // Set back to rosterRiskListGrid.
                RequestStorageManager.getInstance().set(CURRENT_GRID_ID, ROSTER_RISK_GRID_ID);

                // Go to the next step based on the validation result
                // Check if workflow exists, otherwise throw application exception
                WorkflowAgent wa = WorkflowAgentImpl.getInstance();
                if (wa.hasWorkflow(policyHeader.getPolicyNo())) {
                    // Validate transaction
                    String result = getReviewDuplicateManager().validateReviewDuplicate(policyHeader);

                    // Set the transition flag so that workflow knows about the validation status
                    wa.setWorkflowTransitionParameter(policyHeader.getPolicyNo(), result);

                    // Get the next state
                    forwardString = wa.getNextState(policyHeader.getPolicyNo());
                    setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
                }
            }
        }
        catch (ValidationException v) {
            // Go to the next step based on the validation result
            // Check if workflow exists, otherwise throw application exception
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            if (wa.hasWorkflow(policyHeader.getPolicyNo())) {
                // Validate transaction
                String result = getReviewDuplicateManager().validateReviewDuplicate(policyHeader);

                // Set the transition flag so that workflow knows about the validation status
                wa.setWorkflowTransitionParameter(policyHeader.getPolicyNo(), result);

                // Get the next state
                forwardString = wa.getNextState(policyHeader.getPolicyNo());
                setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
            }

            // Save the input records into request.
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, ROSTER_RISK_GRID_ID);
            request.setAttribute(RequestIds.GRID_RECORD_SET, rosterRiskInputRecords);
            request.setAttribute(ROSTER_RISK_RECORD_SET, cisDupInputRecords);
            // Handle the validation exception.
            handleValidationException(v, request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to save saveReviewDuplicate.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveReviewDuplicate", af);
        }
        return af;
    }

    /**
     * Save all review duplicate to CIS.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward saveAllToCIS(ActionMapping mapping, ActionForm form,
                                             HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllToCIS", new Object[]{mapping, form, request, response});
        }

        String forwardString = "saveResult";
        PolicyHeader policyHeader;
        RecordSet rosterRiskInputRecords = null;
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            // Secure access to the page without loading the Oasis Fields.
            securePage(request, form, false);
            // Pull the policy header from the request
            policyHeader = getPolicyHeader(request);
            // Map Roster Risk textXML to RecordSet for input.
            rosterRiskInputRecords = getInputRecordSet(request, ROSTER_RISK_GRID_ID);
            // Save all the Roster Risk.
            getReviewDuplicateManager().saveAllToCIS(policyHeader, rosterRiskInputRecords);

            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            if (wa.hasWorkflow(policyHeader.getPolicyNo())) {
                // Check if workflow continue
                // Set the transition flag
                wa.setWorkflowTransitionParameter(policyHeader.getPolicyNo(), getReviewDuplicateManager().validateReviewDuplicate(policyHeader));

                // Get the next state
                forwardString = wa.getNextState(policyHeader.getPolicyNo());
                setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
            }
        }
        catch (ValidationException v) {
            // Handle the validation exception.
            handleValidationException(v, request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to save saveAllToCIS.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllToCIS", af);
        }
        return af;
    }

    /**
     * This method is called when there the process parameter "loadAllValidationError"
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
    public ActionForward validateReviewDuplicate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "validateReviewDuplicate", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";

        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            // Get PolicyHeader
            PolicyHeader policyHeader = getPolicyHeader(request);

            // Validate transaction
            String result = getReviewDuplicateManager().validateReviewDuplicate(policyHeader);
            if(policyHeader.getPolicyCycleCode().isQuote()) {
                result = ReviewDuplicateFields.QUOTE;
            }

            l.info("Validate Review Duplicate Result=="+result);

            // Go to the next step based on the validation result
            // Check if workflow exists, otherwise throw application exception
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            if (wa.hasWorkflow(policyHeader.getPolicyNo())) {

                // Set the transition flag so that workflow knows about the validation status
                wa.setWorkflowTransitionParameter(policyHeader.getPolicyNo(), result);

                // Get the next state
                forwardString = wa.getNextState(policyHeader.getPolicyNo());
                setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
            }
            else {
                throw new AppException(AppException.UNEXPECTED_ERROR,
                    "Failed to determine workflow for Validate Review Duplicate.");
            }
        }
        catch (Exception e) {
            forwardString =  handleErrorPopup(AppException.UNEXPECTED_ERROR,
                "Failed to validate review duplicate.", e, request, mapping);
        }

        // done
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "validateReviewDuplicate", af);
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

            wa.setWorkflowTransitionParameter(policyHeader.getPolicyNo(), "CLOSE");

            // Get the next state
            forwardString = wa.getNextState(policyHeader.getPolicyNo());
            setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());

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
            if (currentGridId.equals(CIS_DUP_GRID_ID)) {
                anchorName = getCisAnchorColumnName();
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
     * get the anchor column for the bottom grid
     * <p/>
     *
     * @return anchor column
     */
    public String getCisAnchorColumnName() {
        return m_cisAnchorColumnName;
    }

    /**
     * set the anchor column for the bottom grid
     * <p/>
     *
     * @param cisAnchorColumnName anchor column name
     */
    public void setCisAnchorColumnName (String cisAnchorColumnName) {
        m_cisAnchorColumnName = cisAnchorColumnName;
    }

    /**
     * add js messages
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.reviewDuplicate.confirmation.info");
    }

    /**
     * Verify config
     */
    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getCisAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'cisAnchorColumnName' is missing.");
        if (getReviewDuplicateManager() == null)
            throw new ConfigurationException("The required property 'reviewDuplicateManager' is missing.");
    }

    public ReviewDuplicateManager getReviewDuplicateManager() {
        return m_reviewDuplicateManager;
    }

    public void setReviewDuplicateManager(ReviewDuplicateManager reviewDuplicateManager) {
        m_reviewDuplicateManager = reviewDuplicateManager;
    }

    private ReviewDuplicateManager m_reviewDuplicateManager;
    protected static final String CURRENT_GRID_ID = "currentGridId";
    protected static final String ROSTER_RISK_GRID_ID = "rosterRiskListGrid";
    protected static final String CIS_DUP_GRID_ID = "cisDuplicateListGrid";
    protected static final String ROSTER_RISK_LAYER_ID = "PM_ROSTER_RISK_LIST_GH";
    protected static final String CIS_DUP_LAYER_ID = "PM_CIS_DUPLICATE_LIST_GH";
    protected static final String ROSTER_RISK_RECORD_SET = "cisDuplicateRecordSet";
    protected static final String CIS_DUP_RECORD_SET = "cisDuplicateRecordSet";

    private String m_cisAnchorColumnName;
}
