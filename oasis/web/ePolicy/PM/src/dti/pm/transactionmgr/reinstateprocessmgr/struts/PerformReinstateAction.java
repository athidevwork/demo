package dti.pm.transactionmgr.reinstateprocessmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.workflowmgr.WorkflowAgent;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.transactionmgr.reinstateprocessmgr.ReinstateProcessManager;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for  Maintain Reinstate Process.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 20, 2007
 *
 * @author Jerry
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/06/2010       syang       106538 - Modified performReinstate, if reinstate level is RISK RELATION, the workflow "SaveAsOfficialDetail"
 *                              has been started in ReinstateProcessManager, the RelatedPolicyWorkflow shouldn't be started again.
 * ---------------------------------------------------
 */
public class PerformReinstateAction extends PMBaseAction {
    /**
     * This method is triggered automatically when there is no process parameter
     * sent in along the requested url.
     * <p/>
     *
     * @param mapping  for ActionMapping
     * @param form     ActionForm
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        return validateStatusAndTerm(mapping, form, request, response);
    }

    /**
     * capture Reinstate Process
     * validate term
     *
     * @param mapping  ActionMapping
     * @param form     ActionForm
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @throws Exception ErrorMessage
     */
    public ActionForward validateStatusAndTerm(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "validateStatusAndTerm",
            new Object[]{mapping, form, request, response});

        try {
            securePage(request, form, false);
            //get policy header from request
            PolicyHeader policyHeader = getPolicyHeader(request);
            Record inputRecord = getInputRecord(request);
            //call reinstateProcessManager to validate term
            getReinstateProcessManager().validateStatusAndTerm(policyHeader, inputRecord);
            writeEmptyAjaxXMLResponse(response);
        }

        catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to load term in reinstate.", e, response);
        }
        ActionForward af = null;
        l.exiting(getClass().getName(), "validateStatusAndTerm", af);
        return af;

    }

    /**
     * validate Reinstate Process
     *
     * @param mapping  ActionMapping
     * @param form     ActionForm
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @throws Exception ErrorMessage
     */
    public ActionForward validateReinstateProcess(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "validateReinstateProcess",
            new Object[]{mapping, form, request, response});

        try {
            securePage(request, form, false);
            //get policy header from request
            PolicyHeader policyHeader = getPolicyHeader(request);
            Record inputRecord = getInputRecord(request);
            getReinstateProcessManager().validateProcessReinstate(policyHeader, inputRecord);
            writeEmptyAjaxXMLResponse(response);

        }

        catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to validate Reinstate.", e, response);
        }
        ActionForward af = null;
        l.exiting(getClass().getName(), "validateReinstateProcess", af);
        return af;

    }

    /**
     * perform Reinstate
     *
     * @param mapping  ActionMapping
     * @param form     ActionForm
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @throws Exception ErrorMessage
     */
    public ActionForward performReinstate(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "performReinstate",
            new Object[]{mapping, form, request, response});

        try {
            //If the request has valid save token,then process
            if (hasValidSaveToken(request)) {
                // Secures access to the page
                securePage(request, form, false);
                //get policy header from request
                PolicyHeader policyHeader = getPolicyHeader(request);
                Record inputRecord = getInputRecord(request);
                
                //call getReinstateProcessManager to get performReinstate
                Record outputRecord = getReinstateProcessManager().performReinstate(policyHeader, inputRecord);
                writeAjaxXmlResponse(response, outputRecord);

                //view related policies
                String time = "PRE";
                boolean checkRelatedPolicies = false;
                try {
                    checkRelatedPolicies = getTransactionManager().checkRelatedPolicy(policyHeader, time);
                }
                catch (Exception e) {
                    l.logp(Level.SEVERE, getClass().getName(), "performReinstate",
                        "Failed to determine workflow for view related policy.", e);
                    checkRelatedPolicies = true;
                }
                String reinstateLevel = inputRecord.getStringValue("reinstateLevel");
                if (checkRelatedPolicies && !reinstateLevel.equals("RISK RELATION")) {
                    WorkflowAgent wa = WorkflowAgentImpl.getInstance();
                    wa.initializeWorkflow(PolicyHeaderFields.getPolicyNo(inputRecord),
                        RELATED_POLICY_WORKFLOW_PROCESS,
                        RELATED_POLICY_INITIAL_STATE);
                }
            }
        }
        catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            handleErrorForAjax("pm.maintainReinstate.error.save", null, e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "performReinstate", af);
        return af;


    }

    public void verifyConfig() {
        if (getReinstateProcessManager() == null)
            throw new ConfigurationException("The required property 'performReinstate' is missing.");
    }

    public ReinstateProcessManager getReinstateProcessManager() {
        return m_ReinstateProcessManager;
    }

    public void setReinstateProcessManager(ReinstateProcessManager reinstateProcessManager) {
        m_ReinstateProcessManager = reinstateProcessManager;
    }

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    private ReinstateProcessManager m_ReinstateProcessManager;
    private TransactionManager m_transactionManager;
    private static final String RELATED_POLICY_WORKFLOW_PROCESS = "RelatedPolicyWorkflow";
    private static final String RELATED_POLICY_INITIAL_STATE = "invokeViewRelPolicy";

}

