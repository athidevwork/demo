package dti.pm.transactionmgr.struts;

import dti.cs.policynotificationmgr.NotificationTransactionTimeEnum;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.OasisUser;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;
import dti.oasis.util.LogUtils;
import dti.oasis.recordset.Record;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.workflowmgr.WorkflowAgent;
import dti.oasis.messagemgr.MessageManager;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 6, 2007
 *
 * @author gjlong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/25/2010       syang       108041 - Modified delete() to stop delete wip while workflow is processing.
 * 08/26/2016       wdang       167534 - Modified delete() to call processAutoPendingRenewal.
 * 02/22/2017       tzeng       168385 - Modified delete() to set requested transaction time.
 * 06/13/2018       wrong       192557 - Modified delete() to call hasValidSaveToken() to be used for CSRFInterceptor.
 * ---------------------------------------------------
 */
public class DeleteWIPTransactionAction extends PMBaseAction {
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
        return delete(mapping, form, request, response);
    }

    /**
     * Method for AJAX call to delete,
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
    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "delete", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        String processErrorMessagekey = "pm.transactionmgr.deleteWIPTransaction.error.processError";
        try {
            if (hasValidSaveToken(request)) {
                // Secure access to the page, load the Oasis Fields without loading the LOVs,
                // and map the input parameters to the Fields
                securePage(request, form, false);

                PolicyHeader policyHeader = getPolicyHeader(request);
                String policyNo = policyHeader.getPolicyNo();
                WorkflowAgent wa = WorkflowAgentImpl.getInstance();
                if (wa.hasWorkflow(policyNo)) {
                    MessageManager.getInstance().addErrorMessage("pm.deleteWIPTransaction.workflowExist.error");
                    throw new ValidationException("Can't delete WIP transaction while workflow is in progress.");
                }
                // add all request parameters into a record
                Record requestRecord = getInputRecord(request);

                getQuoteManager().processAutoPendingRenewal(policyHeader, requestRecord);

                // Set notification transaction time to oasis user
                OasisUser oasisUser = UserSessionManager.getInstance().getUserSession().getOasisUser();
                oasisUser.setRequestedTransactionTime(NotificationTransactionTimeEnum.DELETE_WIP.getCode());

                Record outputRecord = getTransactionManager().deleteWipTransaction(policyHeader, requestRecord);

                writeEmptyAjaxXMLResponse(response);
            }
        }
        catch (ValidationException ve) {
            // the messages are stored by BO.
            handleValidationExceptionForAjax(ve, response);
        }
        catch (Exception e) {
            // it could be a db error.
            handleErrorForAjax(processErrorMessagekey, "Failed to delete WIP transaction.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "delete", af);
        return af;
    }


    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    public void verifyConfig() {
        if (getTransactionManager() == null)
            throw new ConfigurationException("The required property 'transactionmanager' is missing.");
    }

    private TransactionManager m_transactionManager;
}

