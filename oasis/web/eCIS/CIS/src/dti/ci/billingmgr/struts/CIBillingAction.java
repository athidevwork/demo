package dti.ci.billingmgr.struts;

import dti.ci.billingmgr.CIBillingManager;
import dti.ci.core.struts.MaintainEntityFolderBaseAction;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.util.LogUtils;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Action Class for CIS Billing Tab
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 27, 2009
 *
 * @author yjmiao
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/04/2010       Joe         Resolve the agent name incorrect problem if the name include special character "&"
 * 10/06/2010       wfu         111776: Replaced hardcode string with resource definition
 * 03/06/2012       Parker      130270. set CIS notes visiable for this business.
 * ---------------------------------------------------
 */

public class CIBillingAction extends MaintainEntityFolderBaseAction {
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
        return loadAllAccount(mapping, form, request, response);
    }

    public ActionForward loadAllAccount(ActionMapping mapping, ActionForm form,
                                        HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllAccount", new Object[]{mapping, form, request, response});
        String forwardString = "success";

        try {

            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            Record inputRecord = getInputRecord(request);

            //load billing data
            RecordSet acctRs = getCiBillingManager().loadAllAccount(inputRecord);

            // Set data bean and load grid header bean
            setDataBean(request, acctRs);

            loadGridHeader(request);

            // set js messages
            addJsMessages();
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load all account.",
                    e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllAccount", af);
        return af;
    }

    //add js message
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("ci.entity.message.imageRight.determine");
        MessageManager.getInstance().addJsMessage("ci.entity.message.formLetters.open");
    }

    public void verifyConfig() {
        if (getCiBillingManager() == null)
            throw new ConfigurationException("The required property 'ciBillingManager' is missing.");
    }

    public CIBillingManager getCiBillingManager() {
        return m_ciBillingManager;
    }

    public void setCiBillingManager(CIBillingManager ciBillingManager) {
        this.m_ciBillingManager = ciBillingManager;
    }

    private CIBillingManager m_ciBillingManager;
}
