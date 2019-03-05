package dti.pm.transactionmgr.struts;

import dti.cs.policynotificationmgr.NotificationTransactionTimeEnum;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.OasisUser;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.core.http.RequestIds;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.busobjs.PolicyHeaderReloadCode;
import dti.oasis.util.LogUtils;
import dti.oasis.recordset.Record;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.workflowmgr.WorkflowAgent;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 8, 2007
 *
 * @author Sxm
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/14/2008       fcb         initializeRating added.
 * 06/15/2012       tcheng      Issue 133870 -  Added initializeOutput and modified initializeRating.
 * 04/06/2013       fcb         142697 - removed clearing the Workflow after SKIP_OUTPUT as a new Workflow Step
 *                                       was introduced at the end (View Premium).
 * 02/22/2017       tzeng       168385 - Modified rateTransaction() to set requested transaction time.
 * ---------------------------------------------------
 */

public class RateTransactionAction extends PMBaseAction {
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
        request.setAttribute(RequestIds.PROCESS,"rateTransaction");
        return rateTransaction(mapping, form, request, response);
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
    public ActionForward rateTransaction(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "rateTransaction", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";

        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            // Get PolicyHeader
            PolicyHeader policyHeader = getPolicyHeader(request);
            Record inputRecord = policyHeader.toRecord();

            // Set notification transaction time to oasis user
            OasisUser oasisUser = UserSessionManager.getInstance().getUserSession().getOasisUser();
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            boolean hasWorkFlowB = wa.hasWorkflow(policyHeader.getPolicyNo());
            if (hasWorkFlowB) {
                String processId = wa.getWorkflowProcessId(policyHeader.getPolicyNo());
                String transactionTimeCode = NotificationTransactionTimeEnum.getTransactionTimeCodeByValue(processId);
                oasisUser.setRequestedTransactionTime(transactionTimeCode);
            }

            // Rate transaction
            String result = getTransactionManager().performTransactionRating(inputRecord);

            // Check if workflow exists, otherwise throw application exception
            if (hasWorkFlowB) {

                // Set the transition flag so that workflow knows about the rating status
                wa.setWorkflowTransitionParameter(policyHeader.getPolicyNo(), result);

                // Get the next state
                forwardString = wa.getNextState(policyHeader.getPolicyNo());
                //here we should refresh policy header
                policyHeader.setReloadCode(PolicyHeaderReloadCode.CURRENT_TERM);
                setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
            }
            else {
                throw new AppException(AppException.UNEXPECTED_ERROR,
                    "Failed to determine workflow for Rate Transaction.");
            }
        }
        catch (Exception e) {
            forwardString =  handleErrorPopup(AppException.UNEXPECTED_ERROR,
                "Failed to rate transaction.", e, request, mapping);
        }

        // done
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "rateTransaction", af);
        return af;
    }

   /**
    * This method is called to initialize rating within the workflow.
    * <p/>
    *
    * @param mapping
    * @param form
    * @param request
    * @param response
    * @return ActionForward
    * @throws Exception
    */
    public ActionForward initializeRating(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                    HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "initializeRating", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";

        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            // Get PolicyHeader
            PolicyHeader policyHeader = getPolicyHeader(request);
            Record inputRecord = policyHeader.toRecord();

            // Check if workflow exists, otherwise throw application exception
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            if (wa.hasWorkflow(policyHeader.getPolicyNo())) {

                if(wa.hasWorkflowAttribute(policyHeader.getPolicyNo(), "processRating")) {
                    YesNoFlag procRating = (YesNoFlag)wa.getWorkflowAttribute(PolicyHeaderFields.getPolicyNo(inputRecord), "processRating");

                    if(!procRating.booleanValue()) {
                        // They must happen in this order, do not refactor if/else
                        wa.setWorkflowTransitionParameter(policyHeader.getPolicyNo(), "SKIP_RATING");
                        forwardString = wa.getNextState(policyHeader.getPolicyNo());
                    }
                    else {
                        forwardString = wa.getNextState(policyHeader.getPolicyNo());
                    }
                }
                else {
                    forwardString = wa.getNextState(policyHeader.getPolicyNo());
                }

                policyHeader.setReloadCode(PolicyHeaderReloadCode.CURRENT_TERM);
                setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
            }
            else {
                throw new AppException(AppException.UNEXPECTED_ERROR,
                    "Failed to determine workflow for initialize rating.");
            }
        }
        catch (Exception e) {
            forwardString =  handleErrorPopup(AppException.UNEXPECTED_ERROR,
                "Failed to initialize rating.", e, request, mapping);
        }

        // done
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "initializeRating", af);
        return af;
    }
    /**
    * This method is called to initialize outputs within the workflow.
    * <p/>
    *
    * @param mapping
    * @param form
    * @param request
    * @param response
    * @return ActionForward
    * @throws Exception
    */
    public ActionForward initializeOutput(ActionMapping mapping,
                                          ActionForm form,
                                          HttpServletRequest request,
                                          HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "initializeOutput", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";

        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            // Get PolicyHeader
            PolicyHeader policyHeader = getPolicyHeader(request);
            Record inputRecord = policyHeader.toRecord();

            // Check if workflow exists, otherwise throw application exception
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            if (wa.hasWorkflow(policyHeader.getPolicyNo())) {
                // Only processOutput is Y, process outputs; otherwise skip outputs.
                if(wa.hasWorkflowAttribute(policyHeader.getPolicyNo(), "processOutput")) {
                    YesNoFlag outB = (YesNoFlag)wa.getWorkflowAttribute(PolicyHeaderFields.getPolicyNo(inputRecord), "processOutput");
                    // Only processOutput is Y, process outputs
                    if(outB.booleanValue()) {
                        forwardString = wa.getNextState(policyHeader.getPolicyNo());
                    } else {
                        wa.setWorkflowTransitionParameter(policyHeader.getPolicyNo(), "SKIP_OUTPUT");
                        forwardString = wa.getNextState(policyHeader.getPolicyNo());
                    }
                } else {
                    wa.setWorkflowTransitionParameter(policyHeader.getPolicyNo(), "SKIP_OUTPUT");
                    forwardString = wa.getNextState(policyHeader.getPolicyNo());
                }

                policyHeader.setReloadCode(PolicyHeaderReloadCode.CURRENT_TERM);
                setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
            }
            else {
                throw new AppException(AppException.UNEXPECTED_ERROR,
                    "Failed to determine workflow for initialize outputs.");
            }
        }
        catch (Exception e) {
            forwardString =  handleErrorPopup(AppException.UNEXPECTED_ERROR,
                "Failed to initialize outputs.", e, request, mapping);
        }

        // done
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "initializeOutput", af);
        return af;
    }
    /**
    * This method is to process re-rate policy
    * <p/>
    *
    * @param mapping
    * @param form
    * @param request
    * @param response
    * @return ActionForward
    * @throws Exception
    */
    public ActionForward reRatePolicy(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "reRatePolicy", new Object[]{mapping, form, request, response});
        String forwardString = "reRatePolicy";
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            // Get PolicyHeader
            PolicyHeader policyHeader = getPolicyHeader(request);
            Record inputRecord = getInputRecord(request);

            // Rate transaction
            getTransactionManager().performReRatePolicy(policyHeader,inputRecord);
            Record record = new Record();
            record.setFieldValue("policyNo",policyHeader.getPolicyNo());
            writeAjaxXmlResponse(response, record);
            
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR,
                "Failed to reRate policy.", e, response);
        }

        // done
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "reRatePolicy", af);
        return af;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getTransactionManager() == null)
            throw new ConfigurationException("The required property 'transactionmanager' is missing.");
    }

    public RateTransactionAction() {}

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    private TransactionManager m_transactionManager;
}
