package dti.pm.policymgr.struts;

import dti.oasis.busobjs.YesNoFlag;
import dti.pm.riskmgr.RiskFields;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyManager;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.policymgr.PolicyFields;
import dti.pm.agentmgr.AgentManager;
import dti.pm.coveragemgr.CoverageManager;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.riskmgr.RiskManager;
import dti.oasis.util.LogUtils;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.request.RequestStorageManager;

import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 6, 2007
 * This class displays policy summary,risk,coverage,transaction,agent and premium info.
 *
 * @author zlzhu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * Dec 6, 2007      zlzhu       Created
 * Mar 21, 2008     James       CreatedAdd Agent Tab to ECIS.
 *                              Same functionality, look and feel
 * 09/05/2011       ryzhao      124622 - For pages with multiple grids:
 *                              1) Pass gridId as the third parameter to the setDataBean() method
 *                                 for all but the first grid.
 *                              2) Pass gridId/layerId as the third/fourth parameter to the loadGridHeader() method
 *                                 for all but the first grid.
 * 07/17/2014       jyang       154149 - Modified loadAllPolicySummary, loadAllAgentSummary, loadAllTransactionSummary,
 *                                       loadAllCoverageSummary, set default value for isCopyAddrPhoneAvailable to N
 *                                       for load policy/agent/transaction/coverage summary request.
 * ---------------------------------------------------
 */

public class ViewPolicySummaryAction extends PMBaseAction {
    /**
     * This method is triggered automatically when there is no process parameter sent in along the requested url.
     * <p/>
     *
     * @param mapping  mapping info
     * @param form     action form
     * @param request  request that contains input param
     * @param response response
     * @return ActionForward
     * @throws Exception when something error
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadAllPolicySummary(mapping, form, request, response);
    }

    /**
     * This method load all policy for one entity.
     * <p/>
     *
     * @param mapping  mapping info
     * @param form     action form
     * @param request  request that contains input param
     * @param response response
     * @return ActionForward
     * @throws Exception when something error
     */
    public ActionForward loadAllPolicySummary(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                              HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllPolicySummary", new Object[]{mapping, form, request, response});
        String forwardString = "loadAllPolicySummary";
        try {
            securePage(request, form);
            // get input
            Record inputRecord = getInputRecord(request);
            // get initial values
            RecordSet rs = getPolicyManager().loadAllPolicySummary(inputRecord);
            // Set loaded grid data into request
            setDataBean(request, rs);

            // Make the Summary Record available for output
            Record output = rs.getSummaryRecord();
            // Set isCopyAddrPhoneAvailable to N for policy summary grid.
            output.setFieldValue(RiskFields.IS_COPY_ADDR_PHONE_AVAILABLE, YesNoFlag.N);
            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);
            loadGridHeader(request, null);
            loadListOfValues(request, form);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the policy summary.", e, request, mapping);
        }

        // done
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllPolicySummary", af);
        return af;
    }

    /**
     * This method load all transaction for one entity.
     * <p/>
     *
     * @param mapping  mapping info
     * @param form     action form
     * @param request  request that contains input param
     * @param response response
     * @return ActionForward
     * @throws Exception when something error
     */
    public ActionForward loadAllTransactionSummary(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllTransactionSummary", new Object[]{mapping, form, request, response});
        String forwardString = "loadAllTransactionSummary";
        try {
            securePage(request, form);
            // get input
            Record inputRecord = getInputRecord(request);
            // get initial values
            RecordSet rs = getTransactionManager().loadAllTransactionSummary(inputRecord);
            // Set loaded grid data into request
            setDataBean(request, rs, TRANSACTION_GRID_ID);
            // Make the Summary Record available for output
            Record output = rs.getSummaryRecord();
            // Set isCopyAddrPhoneAvailable to N for transaction summary grid.
            output.setFieldValue(RiskFields.IS_COPY_ADDR_PHONE_AVAILABLE, YesNoFlag.N);

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);
            setCurrentGridId(TRANSACTION_GRID_ID);
            loadGridHeader(request, null, TRANSACTION_GRID_ID, TRANSACTION_GRID_LAYER_ID);
            loadListOfValues(request, form);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to execute loadAllTransactionSummary.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllTransactionSummary", af);
        return af;
    }

    /**
     * This method load all risk for one entity.
     * <p/>
     *
     * @param mapping  mapping info
     * @param form     action form
     * @param request  request that contains input param
     * @param response response
     * @return ActionForward
     * @throws Exception when something error
     */
    public ActionForward loadAllRiskSummary(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                            HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllRiskSummary", new Object[]{mapping, form, request, response});
        String forwardString = "loadAllRiskSummary";
        try {
            securePage(request, form);
            PolicyHeader policyHeader = getPolicyHeader(request);
            // get input
            Record inputRecord = getInputRecord(request);
            //get last transaction
            String lastTransactionId = getLastTransId(request);
            TransactionFields.setLastTransactionId(inputRecord, lastTransactionId);
            RecordSet rs = getRiskManager().loadAllRiskSummary(policyHeader, inputRecord);
            // Set loaded grid data into request
            setDataBean(request, rs, RISK_GRID_ID);
            // Make the Summary Record available for output
            Record output = rs.getSummaryRecord();

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);
            setCurrentGridId(RISK_GRID_ID);
            loadGridHeader(request, null, RISK_GRID_ID, RISK_GRID_LAYER_ID);
            loadListOfValues(request, form);

            request.setAttribute("policyId", PolicyHeaderFields.getPolicyId(inputRecord));
            request.setAttribute("policyNo", PolicyHeaderFields.getPolicyNo(inputRecord));
            request.setAttribute("termEffectiveFromDate", PolicyFields.getEffectiveFromDate(inputRecord));
            request.setAttribute("termEffectiveToDate", PolicyFields.getEffectiveToDate(inputRecord));
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to execute loadAllRiskSummary.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllRiskSummary", af);
        return af;
    }

    private String getLastTransId(HttpServletRequest request) {
        PolicyHeader policyHeader = getPolicyHeader(request);
        String lastTransactionId = policyHeader.getLastTransactionId();
        if (null == lastTransactionId) {
            lastTransactionId = "0";
        }
        return lastTransactionId;
    }

    /**
     * This method load all coverage for one entity.
     * <p/>
     *
     * @param mapping  mapping info
     * @param form     action form
     * @param request  request that contains input param
     * @param response response
     * @return ActionForward
     * @throws Exception when something error
     */
    public ActionForward loadAllCoverageSummary(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllCoverageSummary", new Object[]{mapping, form, request, response});
        String forwardString = "loadAllCoverageSummary";
        try {
            securePage(request, form);
            // get input
            Record inputRecord = getInputRecord(request);
            //In this case,becuase this method will be called absolutely after loadAllRiskSummary
            //so load policy header again is ok.
            String lastTransactionId = getLastTransId(request);
            TransactionFields.setLastTransactionId(inputRecord, lastTransactionId);
            // get initial values
            RecordSet rs = getCoverageManager().loadAllCoverageSummary(inputRecord);
            // Set loaded grid data into request
            setDataBean(request, rs, COVERAGE_GRID_ID);
            // Make the Summary Record available for output
            Record output = rs.getSummaryRecord();
            // Set isCopyAddrPhoneAvailable default value to N for coverage summary grid.
            output.setFieldValue(RiskFields.IS_COPY_ADDR_PHONE_AVAILABLE,YesNoFlag.N);

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);
            setCurrentGridId(COVERAGE_GRID_ID);
            loadGridHeader(request, null, COVERAGE_GRID_ID, COVERAGE_GRID_LAYER_ID);
            loadListOfValues(request, form);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to execute loadAllCoverageSummary.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllCoverageSummary", af);
        return af;
    }

    /**
     * This method load all agent for one entity.
     * <p/>
     *
     * @param mapping  mapping info
     * @param form     action form
     * @param request  request that contains input param
     * @param response response
     * @return ActionForward
     * @throws Exception when something error
     */
    public ActionForward loadAllAgentSummary(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllAgentSummary", new Object[]{mapping, form, request, response});
        String forwardString = "loadAllAgentSummary";
        try {
            securePage(request, form);
            // get input
            Record inputRecord = getInputRecord(request);
            // get initial values
            RecordSet rs = getAgentManager().loadAllPolicyAgentSummary(inputRecord);
            // Set loaded grid data into request
            setDataBean(request, rs, AGENT_GRID_ID);
            // Make the Summary Record available for output
            Record output = rs.getSummaryRecord();
            // Set isCopyAddrPhoneAvailable default value to N for agent summary grid.
            output.setFieldValue(RiskFields.IS_COPY_ADDR_PHONE_AVAILABLE, YesNoFlag.N);

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);
            setCurrentGridId(AGENT_GRID_ID);
            loadGridHeader(request, null, AGENT_GRID_ID, AGENT_GRID_LAYER_ID);
            loadListOfValues(request, form);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to execute loadAllAgentSummary.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllAgentSummary", af);
        return af;
    }

    /**
     * override this method to handle :
     * five grids in a page but only one has headerFileName
     *
     * @return
     */
    public boolean hasHeaderFileName() {
        if (hasCurrentGridId()) {
            String currentGridId = getCurrentGridId();
            if (currentGridId.equals(POLICY_GRID_ID)) {
                return super.hasHeaderFileName();
            }
            else if (currentGridId.equals(TRANSACTION_GRID_ID)) {
                return false;
            }
            else if (currentGridId.equals(RISK_GRID_ID)) {
                return super.hasHeaderFileName();
            }
            else if (currentGridId.equals(COVERAGE_GRID_ID)) {
                return false;
            }
            else if (currentGridId.equals(AGENT_GRID_ID)) {
                return super.hasHeaderFileName();
            }
            else {
                return super.hasHeaderFileName();
            }
        }
        else {
            return super.hasHeaderFileName();
        }
    }

    public String getHeaderFileName() {
        if (hasCurrentGridId()) {
            String currentGridId = getCurrentGridId();
            if (currentGridId.equals(TRANSACTION_GRID_ID)) {
                return null;
            }
            else if (currentGridId.equals(COVERAGE_GRID_ID)) {
                return null;
            }
            else {
                return super.getHeaderFileName();
            }
        }
        else {
            return super.getHeaderFileName();
        }
    }

    /**
     * Overrides method in BaseAction. To get the anchor column name by current grid Id.
     * This is effective only for the multiple grids.
     * <p/>
     *
     * @return anchor column name for the current grid
     */
    public String getAnchorColumnName() {
        if (hasCurrentGridId()) {
            String currentGridId = getCurrentGridId();
            if (currentGridId.equals(POLICY_GRID_ID)) {
                return super.getAnchorColumnName();
            }
            else if (currentGridId.equals(TRANSACTION_GRID_ID)) {
                return getTransactionAnchorColumnName();
            }
            else if (currentGridId.equals(RISK_GRID_ID)) {
                return getRiskAnchorColumnName();
            }
            else if (currentGridId.equals(COVERAGE_GRID_ID)) {
                return getCoverageAnchorColumnName();
            }
            else if (currentGridId.equals(AGENT_GRID_ID)) {
                return getAgentAnchorColumnName();
            }
            else {
                return super.getAnchorColumnName();
            }
        }
        else {
            return super.getAnchorColumnName();
        }
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getPolicyManager() == null)
            throw new ConfigurationException("The required property 'policyManager' is missing.");
        if (getRiskManager() == null)
            throw new ConfigurationException("The required property 'riskManager' is missing.");
        if (getTransactionManager() == null)
            throw new ConfigurationException("The required property 'transactionManager' is missing.");
        if (getCoverageManager() == null)
            throw new ConfigurationException("The required property 'coverageManager' is missing.");
        if (getAgentManager() == null)
            throw new ConfigurationException("The required property 'agentManager' is missing.");
    }


    public PolicyManager getPolicyManager() {
        return m_policyManager;
    }

    public void setPolicyManager(PolicyManager policyManager) {
        m_policyManager = policyManager;
    }

    public AgentManager getAgentManager() {
        return m_agentManager;
    }

    public void setAgentManager(AgentManager agentManager) {
        m_agentManager = agentManager;
    }

    public CoverageManager getCoverageManager() {
        return m_coverageManager;
    }

    public void setCoverageManager(CoverageManager coverageManager) {
        m_coverageManager = coverageManager;
    }

    public RiskManager getRiskManager() {
        return m_riskManager;
    }

    public void setRiskManager(RiskManager riskManager) {
        m_riskManager = riskManager;
    }

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    public String getAgentAnchorColumnName() {
        return m_agentAnchorColumnName;
    }

    public void setAgentAnchorColumnName(String agentAnchorColumnName) {
        m_agentAnchorColumnName = agentAnchorColumnName;
    }

    public String getCoverageAnchorColumnName() {
        return m_coverageAnchorColumnName;
    }

    public void setCoverageAnchorColumnName(String coverageAnchorColumnName) {
        m_coverageAnchorColumnName = coverageAnchorColumnName;
    }

    public String getPolicyAnchorColumnName() {
        return m_policyAnchorColumnName;
    }

    public void setPolicyAnchorColumnName(String policyAnchorColumnName) {
        m_policyAnchorColumnName = policyAnchorColumnName;
    }

    public String getRiskAnchorColumnName() {
        return m_riskAnchorColumnName;
    }

    public void setRiskAnchorColumnName(String riskAnchorColumnName) {
        m_riskAnchorColumnName = riskAnchorColumnName;
    }

    public String getTransactionAnchorColumnName() {
        return m_transactionAnchorColumnName;
    }

    public void setTransactionAnchorColumnName(String transactionAnchorColumnName) {
        m_transactionAnchorColumnName = transactionAnchorColumnName;
    }



    protected static final String POLICY_GRID_ID = "policySummaryGrid";
    protected static final String RISK_GRID_ID = "riskSummaryGrid";
    protected static final String RISK_GRID_LAYER_ID = "RISK_DETAIL";
    protected static final String COVERAGE_GRID_ID = "coverageSummaryGrid";
    protected static final String COVERAGE_GRID_LAYER_ID = "COVERAGE_DETAIL";
    protected static final String TRANSACTION_GRID_ID = "transactionSummaryGrid";
    protected static final String TRANSACTION_GRID_LAYER_ID = "TRANSACTION_DETAIL";
    protected static final String AGENT_GRID_ID = "agentSummaryGrid";
    protected static final String AGENT_GRID_LAYER_ID = "AGENT_DETAIL";
    private String m_policyAnchorColumnName;
    private String m_transactionAnchorColumnName;
    private String m_coverageAnchorColumnName;
    private String m_riskAnchorColumnName;
    private String m_agentAnchorColumnName;
    private PolicyManager m_policyManager;
    private RiskManager m_riskManager;
    private TransactionManager m_transactionManager;
    private CoverageManager m_coverageManager;
    private AgentManager m_agentManager;

}
