package dti.pm.riskmgr.struts;

import dti.oasis.recordset.RecordLoadProcessorChainManager;
import dti.pm.riskmgr.impl.SelectPolicyInsuredRiskRelationRowStyleRecordLoadprocessor;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

import dti.oasis.util.LogUtils;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.busobjs.YesNoFlag;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.core.struts.AddSelectIndLoadProcessor;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.riskmgr.RiskRelationManager;

/**
 * Action class for policy insured risk relation selection.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 14, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/22/2008       fcb         80759: logic for multiRiskRelation added.
 * 07/28/2010       syang       109479: Modified loadAllAvailableRiskForPolicyInsuredRiskRelation() to
 *                              load the riskHeader since system need the riskBaseRecordId to retrieve data.
 * 09/17/2015       tzeng       164679 - Modified loadAllAvailableRiskForPolicyInsuredRiskRelation() to set new records
 *                                       in blue.
 * ---------------------------------------------------
 */
public class SelectPolicyInsuredRiskRelationAction extends PMBaseAction {
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
        return loadAllAvailableRiskForPolicyInsuredRiskRelation(mapping, form, request, response);
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
    public ActionForward loadAllAvailableRiskForPolicyInsuredRiskRelation(ActionMapping mapping,
                                                                          ActionForm form,
                                                                          HttpServletRequest request,
                                                  HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllAvailableRiskForPolicyInsuredRiskRelation",
            new Object[]{mapping, form, request, response});

        String forwardString = "success";

        try {
            // Secures access to the page, load the Oasis Fields without loading the LOVs,
            //   and map the input parameters to the Fields
            securePage(request, form);

            // Gets policy header
            // Issue 109479, the riskHeader should be loaded since system need the riskBaseRecordId to retrieve data.
            PolicyHeader policyHeader = getPolicyHeader(request, true);

            // Loads available coverages for selection
            RecordLoadProcessor loadProcessor = AddSelectIndLoadProcessor.getInstance();
            RecordLoadProcessor rowStyleLp = new SelectPolicyInsuredRiskRelationRowStyleRecordLoadprocessor();
            loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(rowStyleLp, loadProcessor);

            RecordSet rs;
            YesNoFlag isMultiRiskRelation = YesNoFlag.getInstance(request.getParameter("multiRiskRelation"));
            if (isMultiRiskRelation.booleanValue()) {
                request.setAttribute("multiRiskRelation", request.getParameter("multiRiskRelation"));
                request.setAttribute("riskEntityId", request.getParameter("riskEntityId"));
                Record inputRecord = getInputRecord(request);
                rs = getRiskRelationManager().
                    loadAllAvailableMultiRiskForCompanyInsuredRiskRelation(policyHeader, inputRecord, loadProcessor);
            }
            else {
                rs = getRiskRelationManager().
                    loadAllAvailableRiskForPolicyInsuredRiskRelation(policyHeader, loadProcessor);
            }

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
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load select policy insured risk page.",
                e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllAvailableRiskForPolicyInsuredRiskRelation", af);
        return af;
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.maintainRiskRelation.selectPolInsRisk.noSelection.error");
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

    public SelectPolicyInsuredRiskRelationAction(){}

    private RiskRelationManager m_riskRelationManager;
}
