package dti.pm.transactionmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.workflowmgr.WorkflowAgent;
import dti.oasis.busobjs.YesNoFlag;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.policymgr.lockmgr.LockManager;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;
import dti.pm.entitlementmgr.EntitlementFields;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 26, 2007
 *
 * @author gjlong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/15/2008       fcb         display: handleError replaced by handleErrorPopup
 * 05/01/2011       fcb         convertCoverageTransaction() added.
 * 04/21/2016       eyin        171030 - Modified createEndorsementTransaction(), unlockWIPReinitialize() is called to
 *                                       unlock policy once ValidationException is caught.
 * ---------------------------------------------------
 */
public class   CreateEndorsementTransactionAction extends PMBaseAction {
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
        return display(mapping, form, request, response);
    }

    /**
     * Method to load the initial values for display.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward display(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "display", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";

        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            PolicyHeader policyHeader = getPolicyHeader(request);

            // add all request parameters into a record
            Record inputRecord = getInputRecord(request);
            
            Record outputRecord = getTransactionManager().getInitialValuesForCreateEndorsementTransaction(policyHeader, inputRecord);

            // publish the output record for use by the Oasis Tags and JSP


            publishOutputRecord(request, outputRecord);
            // Load the list of values after loading the data
            loadListOfValues(request, form);
            //for capture transaction details
            if (inputRecord.hasStringValue("isForCaptureTransactionDetail")) {
                request.setAttribute("isForCaptureTransactionDetail", inputRecord.getStringValue("isForCaptureTransactionDetail"));
            }

        }
        catch (ValidationException ve) {
            // Set everything to read-only, if there's validation error
            Record outputRecord = new Record();
            outputRecord.setFieldValue("endorsementCode", "");
            EntitlementFields.setReadOnly(outputRecord, true);
            publishOutputRecord(request, outputRecord);
            loadListOfValues(request, form);
            // Handle the validation exception
            handleValidationException(ve, request);

        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to get the default initial transaction details information.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "display", af);
        return af;
    }


    /**
     * Method is invoked by ajax to createEndorsementTransaction, It validates first,
     * and if validation fails, forward to the display
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
    public ActionForward createEndorsementTransaction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "createEndorsementTransaction", new Object[]{mapping, form, request, response});
        String forwardString = null;
        PolicyHeader policyHeader = null;
        try {
            if (hasValidSaveToken(request)) {
                // Secure access to the page, load the Oasis Fields without loading the LOVs,
                // and map the input parameters to the Fields
                securePage(request, form);

                // if (hasValidSaveToken(request)) {
                policyHeader = getPolicyHeader(request);

                // add all request parameters into a record
                Record inputRecord = getInputRecord(request);

                // attempt to save. throws validationException if data does not pass the validation test
                getTransactionManager().createTransaction(policyHeader, inputRecord, inputRecord.getStringValue("effectiveFromDate"), TransactionFields.getTransactionCode(inputRecord));

                // write a empty Ajax Reponse, so handleAjaxMessages does not fail
                writeEmptyAjaxXMLResponse(response);
                //view related policies
                String time = "PRE";
                boolean checkRelatedPolicies = false;
                try {
                    checkRelatedPolicies = getTransactionManager().checkRelatedPolicy(policyHeader, time);
                }
                catch (Exception e) {
                    l.logp(Level.SEVERE, getClass().getName(), "createEndorsement",
                        "Failed to determine workflow for view related policy.", e);
                    checkRelatedPolicies = true;
                }
                if (checkRelatedPolicies) {
                    WorkflowAgent wa = WorkflowAgentImpl.getInstance();
                    wa.initializeWorkflow(PolicyHeaderFields.getPolicyNo(inputRecord),
                        RELATED_POLICY_WORKFLOW_PROCESS,
                        RELATED_POLICY_INITIAL_STATE);
                }
            }
        }
        catch (ValidationException ve) {
            // System should unlock the policy if it is locked by itself.
            boolean ownLock = policyHeader.getPolicyIdentifier().ownLock();
            if (ownLock) {
                getLockManager().unlockWIPReinitialize(policyHeader, YesNoFlag.N, "Unlock from ValidationException during Create Endorsement Transaction");
            }
            handleValidationExceptionForAjax(ve, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to create endorsement transaction .", e, response);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "createEndorsementTransaction", af);
        return af;
    }

    /**
     * Method to convert coverages.
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
    public ActionForward convertCoverageTransaction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "convertCoverageTransaction", new Object[]{mapping, form, request, response});
        String forwardString = null;
        PolicyHeader policyHeader = null;
        try {
            if (hasValidSaveToken(request)) {
                // Secure access to the page, load the Oasis Fields without loading the LOVs,
                // and map the input parameters to the Fields
                securePage(request, form, false);

                policyHeader = getPolicyHeader(request);

                // add all request parameters into a record
                Record inputRecord = getInputRecord(request);

                getTransactionManager().convertCoverageTransaction(policyHeader, inputRecord);

                // write a empty Ajax Reponse, so handleAjaxMessages does not fail
                writeEmptyAjaxXMLResponse(response);
            }
        }
        catch (ValidationException ve) {
            // System should unlock the policy if it is locked by itself.
            boolean ownLock = policyHeader.getPolicyIdentifier().ownLock();
            if (ownLock) {
                getLockManager().unLockPolicy(policyHeader, YesNoFlag.N, "Unlock from ValidationException during Convert Coverage Transaction");
            }
            handleValidationExceptionForAjax(ve, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to convert coverage.", e, response);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "convertCoverageTransaction", af);
        return af;
    }

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

    public LockManager getLockManager() {
        return m_lockManager;
    }

    public void setLockManager(LockManager lockManager) {
        m_lockManager = lockManager;
    }

    private TransactionManager m_transactionManager;
    private LockManager m_lockManager;
    private static final String RELATED_POLICY_WORKFLOW_PROCESS = "RelatedPolicyWorkflow";
    private static final String RELATED_POLICY_INITIAL_STATE = "invokeViewRelPolicy";
}
