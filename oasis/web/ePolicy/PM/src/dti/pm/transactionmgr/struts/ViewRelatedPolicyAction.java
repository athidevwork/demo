package dti.pm.transactionmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.IOasisAction;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.oasis.util.LogUtils;
import dti.oasis.workflowmgr.WorkflowAgent;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.transactionmgr.RelatedPolicyFields;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.transactionmgr.impl.TransactionManagerImpl;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for view Related Policy
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   August 16, 2007
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/07/2011       ryzhao      103801 - Added private method setVisibilityOfGridFieldsAndBottomPortion().
 *                                       Modified loadAllRelatedPolicy() to set visibilities of grid fields and bottom portion for group/distinct mode.
 * ---------------------------------------------------
 */

public class ViewRelatedPolicyAction extends PMBaseAction {


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
        return loadAllRelatedPolicy(mapping, form, request, response);
    }

    /**
     * Method to load all related Policy info for requested policy when do some transaction.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllRelatedPolicy(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                              HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRelatedPolicy", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadRelatedPolicyResult";
        try {
            securePage(request, form);
            // Gets Policy Header
            PolicyHeader policyHeader = getPolicyHeader(request);
            String time = "PRE";
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            //get relatedPoliciesTiming from workflow
            if (wa.hasWorkflow(policyHeader.getPolicyNo())) {
                time = (String) wa.getWorkflowAttribute(policyHeader.getPolicyNo(), "relatedPoliciesTiming", "PRE");

            }
            // for save official
            if (time.equals("POST")) {
                boolean relPolCheckPass = getTransactionManager().checkRelatedPolicy(policyHeader, "POST");
                if (!relPolCheckPass) {
                    wa.setWorkflowTransitionParameter(policyHeader.getPolicyNo(), "NORELPOL");
                    forwardString = wa.getNextState(policyHeader.getPolicyNo());
                    setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
                }
                //for other transaction
                else {
                    RecordSet rs = getTransactionManager().loadAllRelatedPolicy(policyHeader, time);
                    if (rs.getSize() == 0 && wa.hasWorkflow(policyHeader.getPolicyNo())) {
                        wa.setWorkflowTransitionParameter(policyHeader.getPolicyNo(), "NORELPOL");
                        forwardString = wa.getNextState(policyHeader.getPolicyNo());
                        setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
                    }
                    else if (rs.getSize() == 0) {
                        forwardString = "closePage";
                        request.setAttribute("refreshPage", "N");
                    }
                    else {
                        // Sets data bean
                        setDataBean(request, rs);
                        // Loads list of values
                        loadListOfValues(request, form);
                        // Load grid header bean
                        loadGridHeader(request);
                        // Set visibilities of fields for group mode and distinct mode
                        // Assemble bottom portion if it is group mode or distinct mode
                        Record bottomRecord = setVisibilityOfGridFieldsAndBottomPortion(request, policyHeader, rs);
                        publishOutputRecord(request, bottomRecord);
                    }
                }
            }
            //for other transaction
            else {
                RecordSet rs = getTransactionManager().loadAllRelatedPolicy(policyHeader, time);
                if (rs.getSize() == 0 && wa.hasWorkflow(policyHeader.getPolicyNo())) {
                    wa.setWorkflowTransitionParameter(policyHeader.getPolicyNo(), "NORELPOL");
                    forwardString = wa.getNextState(policyHeader.getPolicyNo());
                    setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
                }
                else if (rs.getSize() == 0) {
                    forwardString = "closePage";
                    request.setAttribute("refreshPage", "N");
                }
                else {
                    // Sets data bean
                    setDataBean(request, rs);
                    // Loads list of values
                    loadListOfValues(request, form);
                    // Load grid header bean
                    loadGridHeader(request);
                    // Set visibilities of fields for group mode and distinct mode
                    // Assemble bottom portion if it is group mode or distinct mode
                    Record bottomRecord = setVisibilityOfGridFieldsAndBottomPortion(request, policyHeader, rs);
                    publishOutputRecord(request, bottomRecord);
                }
            }
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the loadAllRelatedPolicy page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllRelatedPolicy", af);
        return af;
    }

    /**
     * Set visibilities of grid fields for group mode and distinct mode
     * Assemble bottom portion if it is group mode or distinct mode
     *
     * @param request
     * @param policyHeader
     * @param rs           related policies record set
     * @return Record that includes the fields' value of the bottom portion
     */
    private Record setVisibilityOfGridFieldsAndBottomPortion(HttpServletRequest request, PolicyHeader policyHeader, RecordSet rs) {
        Logger l = LogUtils.enterLog(getClass(), "setVisibilityOfGridFieldsAndBottomPortion", new Object[]{request, policyHeader, rs});

        String displayMode = getTransactionManager().getRelatedPolicyDisplayMode(policyHeader.toRecord());
        OasisFields fields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);
        OasisFormField field;
        Record bottomRecord = new Record();

        if (TransactionManagerImpl.RELATED_POLICY_DISPLAY_MODE_FOR_DISTINCT.equalsIgnoreCase(displayMode)) {
            // If it is distinct display mode, hidden the fields for group
            field = (OasisFormField) fields.get("parentRefNo_GH");
            if (field != null) {
                field.setIsVisible(false);
            }
            field = (OasisFormField) fields.get("parentRiskName_GH");
            if (field != null) {
                field.setIsVisible(false);
            }
            field = (OasisFormField) fields.get("effectiveFromDate_GH");
            if (field != null) {
                field.setIsVisible(false);
            }
            field = (OasisFormField) fields.get("effectiveToDate_GH");
            if (field != null) {
                field.setIsVisible(false);
            }
            // Assemble bottom portion
            if (rs.getSize() > 0) {
                Record record = rs.getRecord(0);
                String currentPolicyNo = RelatedPolicyFields.getCurrentPolicyNo(record);
                String policyHolderName = RelatedPolicyFields.getPolicyHolderName(record);
                String effectiveFromDate = RelatedPolicyFields.getEffectiveFromDate(record);
                String effectiveToDate = RelatedPolicyFields.getEffectiveToDate(record);
                RelatedPolicyFields.setText1(bottomRecord, currentPolicyNo + " - " + policyHolderName);
                RelatedPolicyFields.setText2(bottomRecord, effectiveFromDate + " - " + effectiveToDate);
            }
        }
        else if (TransactionManagerImpl.RELATED_POLICY_DISPLAY_MODE_FOR_GROUP.equalsIgnoreCase(displayMode)) {
            // If it is group display mode, hidden the fields for distinct
            field = (OasisFormField) fields.get("parentRiskNameDistinct_GH");
            if (field != null) {
                field.setIsVisible(false);
            }
            // Assemble bottom portion
            if (rs.getSize() > 0) {
                Record record = rs.getRecord(0);
                String currentPolicyNo = RelatedPolicyFields.getCurrentPolicyNo(record);
                String policyHolderName = RelatedPolicyFields.getPolicyHolderName(record);
                String effectiveFromDate = RelatedPolicyFields.getEffectiveFromDate(record);
                String effectiveToDate = RelatedPolicyFields.getEffectiveToDate(record);
                String userId = RelatedPolicyFields.getUserId(record);
                RelatedPolicyFields.setText1(bottomRecord, currentPolicyNo + " - " + policyHolderName);
                RelatedPolicyFields.setText2(bottomRecord, effectiveFromDate + " - " + effectiveToDate + " " + userId);
            }
        }
        else {
            // It is neither group display mode nor distinct display mode, hidden all five related fields
            field = (OasisFormField) fields.get("parentRefNo_GH");
            if (field != null) {
                field.setIsVisible(false);
            }
            field = (OasisFormField) fields.get("parentRiskName_GH");
            if (field != null) {
                field.setIsVisible(false);
            }
            field = (OasisFormField) fields.get("effectiveFromDate_GH");
            if (field != null) {
                field.setIsVisible(false);
            }
            field = (OasisFormField) fields.get("effectiveToDate_GH");
            if (field != null) {
                field.setIsVisible(false);
            }
            field = (OasisFormField) fields.get("parentRiskNameDistinct_GH");
            if (field != null) {
                field.setIsVisible(false);
            }
            // Set bottom portion null value
            RelatedPolicyFields.setText1(bottomRecord, "");
            RelatedPolicyFields.setText2(bottomRecord, "");
        }

        l.exiting(getClass().getName(), "setVisibilityOfGridFieldsAndBottomPortion");
        return bottomRecord;
    }

    /**
     * This method is used to validate locked related policies in save official
     * <p/>
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward validateLockedRelatedPolicy(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                     HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "validateLockedRelatedPolicy", new Object[]{mapping, form, request, response});
        String forwardString = null;

        try {
            securePage(request, form);
            // Gets Policy Header
            PolicyHeader policyHeader = getPolicyHeader(request);
            String validateResult = getTransactionManager().validateLockedRelatedPolicy(policyHeader);
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            if (wa.hasWorkflow(policyHeader.getPolicyNo())) {
                //go to confirm page
                if (validateResult.equals("confirm")) {
                    wa.setWorkflowTransitionParameter(policyHeader.getPolicyNo(), "CONFIRM");
                }
                else if (validateResult.equals("error")) {
                    //go to error page
                    wa.setWorkflowTransitionParameter(policyHeader.getPolicyNo(), "ERROR");
                }
                forwardString = wa.getNextState(policyHeader.getPolicyNo());
                setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
            }
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "fail to validateLockedRelatedPolicy", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "validateLockedRelatedPolicy", af);
        return af;
    }

    /**
     * This method is called when there the process parameter "confirmSaveOfficial"
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
    public ActionForward confirmSaveOfficial(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "confirmSaveOfficial", new Object[]{mapping, form, request, response});
        String forwardString = "confirmSaveOfficial";
        try {
            // Get PolicyHeader
            PolicyHeader policyHeader = getPolicyHeader(request);
            // Check if workflow exists, otherwise just forward to the original
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            if (wa.hasWorkflow(policyHeader.getPolicyNo())) {
                if ("Y".equals(request.getParameter("confirmed"))) {
                    wa.setWorkflowTransitionParameter(policyHeader.getPolicyNo(), "CONFIRMED");
                }
                if ("N".equals(request.getParameter("confirmed"))) {
                    wa.setWorkflowTransitionParameter(policyHeader.getPolicyNo(), "UNCONFIRMED");
                }
                forwardString = wa.getNextState(policyHeader.getPolicyNo());
                setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
            }
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to confirmSaveOfficial ", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "confirmSaveOfficial", af);
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

    //verify spring config
    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getTransactionManager() == null)
            throw new ConfigurationException("The required property 'transactionManager' is missing.");
    }

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    private TransactionManager m_transactionManager;

}
