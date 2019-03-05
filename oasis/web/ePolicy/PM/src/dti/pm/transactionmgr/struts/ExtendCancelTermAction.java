package dti.pm.transactionmgr.struts;

import dti.pm.core.struts.PMBaseAction;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;
import dti.oasis.util.LogUtils;
import dti.oasis.recordset.Record;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.workflowmgr.WorkflowAgent;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 23, 2008
 *
 * @author zlzhu
 */
/*
 * This is the action for Extend Cancel.
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * Jan 23, 2008          zlzhu     Created
 * ---------------------------------------------------
 */

public class ExtendCancelTermAction extends PMBaseAction {

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
        return getInitialValuesForExtendCancelTerm(mapping, form, request, response);
    }

    /**
     * Method to load default values for extend term
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward getInitialValuesForExtendCancelTerm(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                         HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForExtendCancelTerm", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);
            PolicyHeader policyHeader = getPolicyHeader(request);
            Record inputRecord = getInputRecord(request);
            Record record = (Record) request.getAttribute(SAVED_INPUT_RECORD);
            if(record==null){
                //verify that the policy has not changed since the time the user retrieved the parent page
                record = getTransactionManager().getInitialValuesForExtendCancelTerm(policyHeader,inputRecord);
            }
            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, record);
            loadListOfValues(request, form);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to execute getInitialValuesForExtendCancelTerm.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "getInitialValuesForExtendCancelTerm", af);
        return af;
    }

    /**
     * Method to load default values for extend term
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward extendCancelTerm(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                         HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "ExtendCancelTerm", new Object[]{mapping, form, request, response});
        String forwardString = "saveResult";
        try {
            securePage(request, form, false);
            // get input
            Record inputRecord = getInputRecord(request);
            PolicyHeader policyHeader = getPolicyHeader(request);
            getTransactionManager().performExtendCancelTerm(policyHeader,inputRecord);
            // Cache input record
            request.setAttribute(SAVED_INPUT_RECORD, inputRecord);
            //let js know it need a workflow
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
        }
        catch(ValidationException ve){
            handleValidationException(ve,request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to execute performExtendCancelTerm.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "ExtendCancelTerm", af);
        return af;
    }

   public TransactionManager getTransactionManager() {
       return  m_transactionManager;
   }

   public void setTransactionManager(TransactionManager transactionManager) {
       m_transactionManager = transactionManager;
   }

   public void verifyConfig() {
       if (getTransactionManager() == null)
            throw new ConfigurationException("The required property 'transactionmanager' is missing.");
   }
   protected static final String SAVED_INPUT_RECORD = "savedInputRecord";
   private TransactionManager m_transactionManager;
}
