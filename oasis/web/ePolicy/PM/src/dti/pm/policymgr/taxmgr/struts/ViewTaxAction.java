package dti.pm.policymgr.taxmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.workflowmgr.WorkflowAgent;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.taxmgr.TaxManager;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 21, 2007
 *
 * @author fcbibire
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/19/2007       fcb         1. Changed the commenting style to 2 slashes.
 *                              2. Call to publishOutputRecord removed.
 * ---------------------------------------------------
 */
public class ViewTaxAction extends PMBaseAction{

    /**
     * This method is triggered automatically when where is no process parameter
     * sent in along the requested url.
     *
     * @param mapping       mapping
     * @param form          form
     * @param request       request
     * @param response      response
     * @return              action forward
     * @throws Exception
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadAllTax(mapping, form, request, response);
    }

    /**
     * Method to load all tax info for requested policy.
     *
     * @param mapping       mapping
     * @param form          form
     * @param request       request
     * @param response      response
     * @return              action forward
     * @throws Exception
     */
    public ActionForward loadAllTax(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllTax", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadTaxResult";
        try {

            // Secures access to the page, loads the oasis fields without
            // loading LOVs, and map the input parameters into the fields.
            securePage(request, form);

            // Gets Policy Header
            PolicyHeader policyHeader = getPolicyHeader(request);

            // Gets tax information.
            Record inputRecord = getInputRecord(request);
            
            // Load tax information
            RecordSet rs = getTaxManager().loadAllTax(policyHeader, inputRecord);

            Record record = new Record();

            // Set page UI attributes
            if ((rs.getSize()) <= 0) {
                MessageManager.getInstance().addErrorMessage("pm.viewTaxInfo.taxList.noDataFound");
            } else {
                record.setFieldValue("transactionLogId", rs.getFirstRecord().getStringValue(TransactionFields.TRANSACTION_LOG_ID));
            }

            // Publish page field
            publishOutputRecord(request, record);

            // Sets data bean
            setDataBean(request, rs);

            // Loads list of values
            loadListOfValues(request, form);
            
            // Load grid header bean
            loadGridHeader(request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the loadAllTax page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllTax", af);
        return af;
    }

    /**
     * This method is called when the process parameter "closePage"
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

    /**
     * Method to verify Configuration
     */
    public void verifyConfig() {
        if (getTaxManager() == null)
            throw new ConfigurationException("The required property 'taxManager' is missing.");
    }

    public TaxManager getTaxManager() {
        return m_TaxManager;
    }

    public void setTaxManager(TaxManager taxManager) {
        m_TaxManager = taxManager;
    }

    private TaxManager m_TaxManager;
    
}