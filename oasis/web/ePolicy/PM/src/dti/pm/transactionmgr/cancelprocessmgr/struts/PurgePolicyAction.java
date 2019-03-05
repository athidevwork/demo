package dti.pm.transactionmgr.cancelprocessmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.transactionmgr.cancelprocessmgr.CancelProcessManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Action class for Purge Policy.
 * <p>(C) 2009 Delphi Technology, inc. (dti)</p>
 * Date:   July 13, 2009
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public class PurgePolicyAction extends PMBaseAction {

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
        return purgePolicy(mapping, form, request, response);
    }

    /**
     * perform cancellation
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward purgePolicy(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "purgePolicy", new Object[]{mapping, form, request, response});
        PolicyHeader policyHeader = null;
        Record inputRecord = null;
        try {
            // Secures access to the page
            securePage(request, form, false);
            //get policy header from request
            policyHeader = getPolicyHeader(request);
            //get input record from request
            inputRecord = getInputRecord(request);
            //call cancelProcessManager to purge policy
            getCancelProcessManager().purgePolicy(policyHeader, inputRecord);
            // Write response
            writeEmptyAjaxXMLResponse(response);
        }
        catch (ValidationException e) {
            // Delete WIP transaction
            getTransactionManager().deleteWipTransaction(policyHeader, inputRecord);
            // Unlock the policy if it is locked by itself
            boolean ownLock = policyHeader.getPolicyIdentifier().ownLock();
            if (ownLock) {
                getLockManager().unLockPolicy(policyHeader, YesNoFlag.N, "Unlock from ValidationException during flat purge policy.");
            }
            // Handle Validation Exception
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            // Delete WIP transaction
            getTransactionManager().deleteWipTransaction(policyHeader, inputRecord);
            // Unlock the policy if it is locked by itself
            boolean ownLock = policyHeader.getPolicyIdentifier().ownLock();
            if (ownLock) {
                getLockManager().unLockPolicy(policyHeader, YesNoFlag.N, "Unlock from Exception during flat purge policy.");
            }
            // Handle Error
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to purge the policy.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "purgePolicy", af);
        return af;
    }

    /**
     * Verify config
     */
    public void verifyConfig() {
        if (getCancelProcessManager() == null)
            throw new ConfigurationException("The required property 'cancelProcessManager' is missing.");
    }

    public CancelProcessManager getCancelProcessManager() {
        return m_cancelProcessManager;
    }

    public void setCancelProcessManager(CancelProcessManager cancelProcessManager) {
        m_cancelProcessManager = cancelProcessManager;
    }

    private CancelProcessManager m_cancelProcessManager;

}
