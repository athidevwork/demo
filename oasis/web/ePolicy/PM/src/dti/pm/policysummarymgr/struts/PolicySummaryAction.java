package dti.pm.policysummarymgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.http.RequestIds;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policysummarymgr.PolicySummaryManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Action class to Policy Summary.
 * <p/>
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 06, 2016
 *
 * @author wdang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/06/2016       wdang       168069 - Initial version.
 * 01/28/2016       wdang       169024 - Modified loadPolicySummary to invoke addSaveMessages().
 * 12/04/2017       lzhang      190020 - Modified loadPolicySummary() to invoke updatePolicyHeader();
 * ---------------------------------------------------
 */
public class PolicySummaryAction extends PMBaseAction {
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
    @Override
    protected ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                        HttpServletRequest request, HttpServletResponse response) throws Exception {
        return this.loadPolicySummary(mapping, form, request, response);
    }

    /**
     * load Policy Summary
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadPolicySummary(ActionMapping mapping, ActionForm form,
                                            HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadPolicySummary", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";

        try {
            // Secures access to the page, loads the oasis fields without loading LOVs,
            // and map the input parameters into the fields.
            securePage(request, form);

            // get input
            Record inputRecord = getInputRecord(request);

            // Gets Policy Header
            PolicyHeader policyHeader = getPolicyHeader(request);

            //Update risk header, coverage header, coverage class id to policy header.
            boolean hasRisk = updatePolicyHeader(request, policyHeader);

            PolicyFields.setHasRisk(inputRecord, YesNoFlag.getInstance(hasRisk));

            // Gets grid record set
            RecordSet rs = (RecordSet)request.getAttribute(RequestIds.GRID_RECORD_SET) ;
            if (rs== null) {
                rs = getPolicySummaryManager().loadPolicySummary(policyHeader, inputRecord);
            }

            // Sets data bean
            setDataBean(request, rs);

            // Make the Summary Record available for output
            Record output = rs.getSummaryRecord();

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);

            // Loads list of values
            loadListOfValues(request, form);

            // Load grid header bean
            loadGridHeader(request);

            // Add js messages to messagemanager for the current request
            addJsMessages();

            // Add messages for save purpose
            addSaveMessages(policyHeader, request);

        } catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load policy summary.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadPolicySummary", af);
        return af;
    }

    /**
     * save Policy Summary
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward savePolicySummary(ActionMapping mapping, ActionForm form,
                                           HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "savePolicySummary", new Object[]{mapping, form, request, response});
        String forwardString = "saveResult";
        Record inputRecord = null;
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {

                // Secure access to the page without loading the Oasis Fields
                securePage(request, form);

                // get input
                inputRecord = getInputRecord(request);

                // Gets Policy Header
                PolicyHeader policyHeader = getPolicyHeader(request);

                // Save Policy Summary
                getPolicySummaryManager().savePolicySummary(policyHeader, inputRecord);

                // Loads list of values
                loadListOfValues(request, form);

                // Load grid header bean
                loadGridHeader(request);
            }
        }
        catch (ValidationException ve) {
            // Save the input records into request
            request.setAttribute(dti.pm.core.http.RequestIds.GRID_RECORD_SET, inputRecord);

            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load policy summary.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "savePolicySummary", af);
        return af;
    }

    /**
     * add js messages to messagemanager for the current request
     */
    public void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.maintainRenewal.confirm.applyPRT");
        MessageManager.getInstance().addJsMessage("pm.maintainReinstate.confirm.continue");
        MessageManager.getInstance().addJsMessage("pm.maintainPolicy.copyPolicyToQuote.error");
        MessageManager.getInstance().addJsMessage("pm.deleteEndQuote.confirmed.warning");
        MessageManager.getInstance().addJsMessage("pm.applyEndQuote.confirmed.warning");
        MessageManager.getInstance().addJsMessage("pm.maintainQuote.copyPolicyToQuote.convert.confirmation");
        MessageManager.getInstance().addJsMessage("pm.maintainReinstate.confirm.continue");
        MessageManager.getInstance().addJsMessage("pm.deleteEndQuote.confirmed.warning");
        MessageManager.getInstance().addJsMessage("pm.applyEndQuote.confirmed.warning");
        // auto renewal
        MessageManager.getInstance().addJsMessage("pm.autoRenewal.confirmation.info");
        // cancel
        MessageManager.getInstance().addJsMessage("pm.maintainCancellation.cancellationNotPermitted.error");
        //undo term
        MessageManager.getInstance().addJsMessage("pm.undoTerm.confirm.continue");
        // For View Claim Summary
        MessageManager.getInstance().addJsMessage("pm.viewClaimsSummary.risk.select.error");
    }

    public PolicySummaryManager getPolicySummaryManager() {
        return m_policySummaryManager;
    }

    public void setPolicySummaryManager(PolicySummaryManager policySummaryManager) {
        m_policySummaryManager = policySummaryManager;
    }

    private PolicySummaryManager m_policySummaryManager;
}
