package dti.pm.transactionmgr.cancelprocessmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.transactionmgr.cancelprocessmgr.CancelProcessManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for view cancellation detail
 * <p/>
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 30, 2010
 *
 * @author Bhong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/05/2011       ryzhao      124622 - For pages with multiple grids:
 *                              1) Pass gridId as the third parameter to the setDataBean() method
 *                                 for all but the first grid.
 * ---------------------------------------------------
 */
public class ViewTransactionSnapshotAction extends PMBaseAction {
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
    public ActionForward unspecified(ActionMapping mapping,
                                     ActionForm form,
                                     HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        return loadAllTransactionSnapshot(mapping, form, request, response);
    }

    /**
     * Load all transaction snapshot
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllTransactionSnapshot(ActionMapping mapping,
                                                    ActionForm form,
                                                    HttpServletRequest request,
                                                    HttpServletResponse response) throws Exception {

        String forwardString = "loadTransactionSnapshot";
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllTransactionSnapshot", new Object[]{mapping, form, request, response});
        }

        try {
            // Secures page
            securePage(request, form);

            // Load policy Header
            PolicyHeader policyHeader = getPolicyHeader(request);

            // Get recordset
            RecordSet rs = getCancelProcessManager().loadAllTransactionSnapshot(policyHeader.toRecord());

            // Set data bean
            setDataBean(request, rs);

            // Load grid header
            loadGridHeader(request, getAnchorColumnNames()[0], getGridHeaderLayerIds()[0]);

            // Load LOVs
            loadListOfValues(request, form);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(
                AppException.UNEXPECTED_ERROR, "Failed to load transaction snapshot.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllTransactionSnapshot", af);
        }

        return af;
    }

    /**
     * Load all term snapshot
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllTermSnapshot(ActionMapping mapping,
                                             ActionForm form,
                                             HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {

        String forwardString = "loadTermSnapshot";
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllTermSnapshot", new Object[]{mapping, form, request, response});
        }

        try {
            // Secures page
            securePage(request, form);

            // Get recordset
            RecordSet rs = getCancelProcessManager().loadAllTermSnapshot(getInputRecord(request));

            // Set data bean
            setDataBean(request, rs, TERM_SNAPSHOT_GRID_ID);

            // Load grid header
            loadGridHeader(request, getAnchorColumnNames()[1], getGridHeaderLayerIds()[1]);

            // Load LOVs
            loadListOfValues(request, form);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(
                AppException.UNEXPECTED_ERROR, "Failed to load term snapshot.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllTermSnapshot", af);
        }

        return af;
    }

    /**
     * Load all policy component snapshot
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllPolicyComponentSnapshot(ActionMapping mapping,
                                                        ActionForm form,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) throws Exception {

        String forwardString = "loadPolicyComponentSnapshot";
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllPolicyComponentSnapshot", new Object[]{mapping, form, request, response});
        }

        try {
            // Secures page
            securePage(request, form);

            // Get recordset
            RecordSet rs = getCancelProcessManager().loadAllPolicyComponentSnapshot(getInputRecord(request));

            // Set data bean
            setDataBean(request, rs, POLICY_COMPONENT_SNAPSHOT_GRID_ID);

            // Load grid header
            loadGridHeader(request, getAnchorColumnNames()[2], getGridHeaderLayerIds()[2]);

            // Load LOVs
            loadListOfValues(request, form);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(
                AppException.UNEXPECTED_ERROR, "Failed to load policy component snapshot.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllPolicyComponentSnapshot", af);
        }

        return af;
    }

    /**
     * Load all risk snapshot
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllRiskSnapshot(ActionMapping mapping,
                                             ActionForm form,
                                             HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {

        String forwardString = "loadRiskSnapshot";
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRiskSnapshot", new Object[]{mapping, form, request, response});
        }

        try {
            // Secures page
            securePage(request, form);

            // Get recordset
            RecordSet rs = getCancelProcessManager().loadAllRiskSnapshot(getInputRecord(request));

            // Set data bean
            setDataBean(request, rs, RISK_SNAPSHOT_GRID_ID);

            // Load grid header
            loadGridHeader(request, getAnchorColumnNames()[3], getGridHeaderLayerIds()[3]);

            // Load LOVs
            loadListOfValues(request, form);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(
                AppException.UNEXPECTED_ERROR, "Failed to load risk snapshot.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRiskSnapshot", af);
        }

        return af;
    }

    /**
     * Load all coverage snapshot
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllCoverageSnapshot(ActionMapping mapping,
                                                 ActionForm form,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) throws Exception {

        String forwardString = "loadCoverageSnapshot";
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllCoverageSnapshot", new Object[]{mapping, form, request, response});
        }

        try {
            // Secures page
            securePage(request, form);

            // Get recordset
            RecordSet rs = getCancelProcessManager().loadAllCoverageSnapshot(getInputRecord(request));

            // Set data bean
            setDataBean(request, rs, COVERAGE_SNAPSHOT_GRID_ID);

            // Load grid header
            loadGridHeader(request, getAnchorColumnNames()[4], getGridHeaderLayerIds()[4]);

            // Load LOVs
            loadListOfValues(request, form);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(
                AppException.UNEXPECTED_ERROR, "Failed to load coverage snapshot.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllCoverageSnapshot", af);
        }

        return af;
    }

    /**
     * Load all coverage component snapshot
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllCoverageComponentSnapshot(ActionMapping mapping,
                                                          ActionForm form,
                                                          HttpServletRequest request,
                                                          HttpServletResponse response) throws Exception {

        String forwardString = "loadCoverageComponentSnapshot";
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllCoverageComponentSnapshot", new Object[]{mapping, form, request, response});
        }

        try {
            // Secures page
            securePage(request, form);

            // Get recordset
            RecordSet rs = getCancelProcessManager().loadAllCoverageComponentSnapshot(getInputRecord(request));

            // Set data bean
            setDataBean(request, rs, COVERAGE_COMPONENT_SNAPSHOT_GRID_ID);

            // Load grid header
            loadGridHeader(request, getAnchorColumnNames()[5], getGridHeaderLayerIds()[5]);

            // Load LOVs
            loadListOfValues(request, form);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(
                AppException.UNEXPECTED_ERROR, "Failed to load coverage component snapshot.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllCoverageComponentSnapshot", af);
        }

        return af;
    }

    /**
     * verifyConfig
     */
    public void verifyConfig() {
        if (getCancelProcessManager() == null) {
            throw new ConfigurationException("The required property 'cancelProcessManager' is missing.");
        }
        if (getAnchorColumnNames() == null) {
            throw new ConfigurationException("The required property 'anchorColumns' is missing.");
        }
        if (getGridHeaderLayerIds() == null) {
            throw new ConfigurationException("The required property 'gridHeaderLayerIds' is missing.");
        }
    }

    /**
     * Get CancelProcessManager
     *
     * @return CancelProcessManager
     */
    public CancelProcessManager getCancelProcessManager() {
        return m_cancelProcessManager;
    }

    /**
     * Set CancelProcessManager
     *
     * @param cancelProcessManager
     */
    public void setCancelProcessManager(CancelProcessManager cancelProcessManager) {
        m_cancelProcessManager = cancelProcessManager;
    }

    /**
     * Get anchor column names
     *
     * @return String[]
     */
    public String[] getAnchorColumnNames() {
        return m_anchorColumnNames;
    }

    /**
     * Set anchor column name
     *
     * @param anchorColumnNames
     */
    public void setAnchorColumnNames(String[] anchorColumnNames) {
        m_anchorColumnNames = anchorColumnNames;
    }

    /**
     * Get grid header layer ids
     *
     * @return String[]
     */
    public String[] getGridHeaderLayerIds() {
        return m_gridHeaderLayerIds;
    }

    /**
     * Set grid header layer ids
     *
     * @param gridHeaderLayerIds
     */
    public void setGridHeaderLayerIds(String[] gridHeaderLayerIds) {
        m_gridHeaderLayerIds = gridHeaderLayerIds;
    }

    private CancelProcessManager m_cancelProcessManager;
    private String[] m_anchorColumnNames;
    private String[] m_gridHeaderLayerIds;
    protected static final String TERM_SNAPSHOT_GRID_ID = "termSnapshotGrid";
    protected static final String POLICY_COMPONENT_SNAPSHOT_GRID_ID = "policyComponentSnapshotGrid";
    protected static final String RISK_SNAPSHOT_GRID_ID = "riskSnapshotGrid";
    protected static final String COVERAGE_SNAPSHOT_GRID_ID = "coverageSnapshotGrid";
    protected static final String COVERAGE_COMPONENT_SNAPSHOT_GRID_ID = "coverageComponentSnapshotGrid";
}
