package dti.pm.acctlookupmgr.struts;

import dti.pm.core.struts.PMBaseAction;
import dti.pm.core.http.RequestIds;
import dti.pm.acctlookupmgr.AccountLookupManager;
import dti.oasis.app.ConfigurationException;
import dti.oasis.app.AppException;
import dti.oasis.util.LogUtils;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.error.ValidationException;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 14, 2007
 *
 * @author Sxm
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public class LookupAccountAction extends PMBaseAction {
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
        request.setAttribute(RequestIds.PROCESS,"findAllAccount");
        return findAllAccount(mapping, form, request, response);
    }

    /**
    * This method is called when there the process parameter "findAllAccount"
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
    public ActionForward findAllAccount(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "findAllAccount", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {
            AccountLookupManager acctLookupMgr = getAccountLookupManager();

            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            // get input
            Record inputRecord = getInputRecord(request);

            try {
                // get grid content
                RecordSet rs = acctLookupMgr.loadAllAccount(inputRecord);
                setDataBean(request, rs);
                publishOutputRecord(request,rs.getSummaryRecord());
                // load grid header
                loadGridHeader(request);
            }
            catch (ValidationException e) {
                handleValidationException(e, request);
            }
        }
        catch (Exception e) {
            forwardString =  handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to find billing accounts.", e, request, mapping);
        }

        // done
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "findAllAccount", af);
        return af;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getAccountLookupManager() == null)
            throw new ConfigurationException("The required property 'accountLookupManager' is missing.");
    }

    public LookupAccountAction() {}

    public AccountLookupManager getAccountLookupManager() {
        return this.accountLookupManager;
    }

    public void setAccountLookupManager(AccountLookupManager accountLookupManager) {
        this.accountLookupManager = accountLookupManager;
    }

    // private memember variables...
    private AccountLookupManager accountLookupManager;
}
