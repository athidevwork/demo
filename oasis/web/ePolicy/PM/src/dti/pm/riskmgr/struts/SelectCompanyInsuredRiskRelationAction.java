package dti.pm.riskmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.messagemgr.MessageManager;
import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.AddSelectIndLoadProcessor;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.riskmgr.RiskRelationManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Action class for company insured risk relation selection.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 16, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/10/2011       wqfu        103799 - Added logic to get risk header for copy prior act stats. 
 * ---------------------------------------------------
 */
public class SelectCompanyInsuredRiskRelationAction extends PMBaseAction {
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
        return loadAllAvailableRiskForCompanyInsuredRiskRelation(mapping, form, request, response);
    }

    /**
     * Method to load list of available coverage.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllAvailableRiskForCompanyInsuredRiskRelation(ActionMapping mapping,
                                                                           ActionForm form,
                                                                           HttpServletRequest request,
                                                                           HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllAvailableRiskForCompanyInsuredRiskRelation",
            new Object[]{mapping, form, request, response});

        String forwardString = "success";

        try {
            // Secures access to the page, load the Oasis Fields without loading the LOVs,
            //   and map the input parameters to the Fields
            securePage(request, form);

            // Gets policy header
            PolicyHeader policyHeader = getPolicyHeader(request);

            // If generate from copy prior acts stats, risk header is needed to get risk entity.
            Record inputRecord = getInputRecord(request);
            if (inputRecord.hasStringValue(RequestIds.IS_COPY_ACTS_STATS) &&
                YesNoFlag.getInstance(inputRecord.getStringValue(RequestIds.IS_COPY_ACTS_STATS)).booleanValue()) {
                policyHeader = getPolicyHeader(request, true);
            }

            // Loads available coverages for selection
            RecordLoadProcessor loadProcessor = AddSelectIndLoadProcessor.getInstance();
            RecordSet rs = getRiskRelationManager().loadAllAvailableRiskForCompanyInsuredRiskRelation(
                policyHeader, inputRecord, loadProcessor);

            // Sets data Bean
            setDataBean(request, rs);

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, rs.getSummaryRecord());

            // Load the list of values after loading the data
            loadListOfValues(request, form);

            // Load grid header bean
            loadGridHeader(request);

            // Add Js messages
            addJsMessages();
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load select company insured risk page.",
                e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllAvailableRiskForCompanyInsuredRiskRelation", af);
        return af;
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.maintainRiskRelation.selectCompInsRisk.noSelection.error");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getRiskRelationManager() == null)
            throw new ConfigurationException("The required property 'riskRelationManager' is missing.");
    }

    public RiskRelationManager getRiskRelationManager() {
        return m_riskRelationManager;
    }

    public void setRiskRelationManager(RiskRelationManager riskRelationManager) {
        m_riskRelationManager = riskRelationManager;
    }

    public SelectCompanyInsuredRiskRelationAction() {
    }

    private RiskRelationManager m_riskRelationManager;
}
