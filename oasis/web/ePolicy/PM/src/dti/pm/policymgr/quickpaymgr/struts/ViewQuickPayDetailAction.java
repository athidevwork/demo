package dti.pm.policymgr.quickpaymgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.quickpaymgr.QuickPayManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * This is an action class for view quick pay details.
 * <p/>
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * Date:   July 21, 2010
 *
 * @author Dzhang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/19/2010       dzhang      Update per Bill's comments.
 * 09/05/2011       ryzhao      124622 - For pages with multiple grids:
 *                              1) Pass gridId as the third parameter to the setDataBean() method
 *                                 for all but the first grid.
 * ---------------------------------------------------
 */

public class ViewQuickPayDetailAction extends PMBaseAction {
    /**
     * do this process when no process is specified
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadAllQuickPayDetail(mapping, form, request, response);
    }

    /**
     * Method to load quick pay data.
     * <p/>
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward loadAllQuickPayDetail(ActionMapping mapping,
                                               ActionForm form,
                                               HttpServletRequest request,
                                               HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllQuickPayDetail", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {

            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            Record inputRecord = getInputRecord(request);

            // Get data for transaction summary layer
            Record output = getQuickPayManager().loadTransactionSummary(inputRecord);
            if (output == null) {
                output = new Record();
                // if no data found, add error message.
                MessageManager.getInstance().addErrorMessage("pm.viewQuickPayDetails.transactionSummaryLayer.noDataFound.error");
            }

            // Get quick pay transaction list
            RecordSet rs = getQuickPayManager().loadAllQuickPayTransaction(inputRecord);
            // if no data found, add error message.
            if (rs.getSize() == 0) {
                MessageManager.getInstance().addErrorMessage("pm.viewQuickPayDetails.quickPayTransactionList.noDataFound.error");
            }

            // Set all data beans to request
            setDataBean(request, rs);

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);

            // Load grid header
            loadGridHeader(request, getAnchorColumnNames()[0], getGridHeaderLayerIds()[0]);

        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the view quick pay details page.",
                e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllQuickPayDetail", af);
        return af;
    }


    /**
     * Method to load list of risks/coverages list data.
     * <p/>
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward loadAllRiskCoverage(ActionMapping mapping,
                                             ActionForm form,
                                             HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllRiskCoverage", new Object[]{mapping, form, request, response});
        String forwardString = "loadRiskCoverageResult";
        Record output;
        try {

            securePage(request, form);

            Record inputRecord = getInputRecord(request);
            // Load the risks/coverages
            RecordSet rs = getQuickPayManager().loadAllRiskCoverageForQuickPayDetail(inputRecord);

            // Set all risks/coverages data into request
            setDataBean(request, rs, RISK_COVERAGE_GRID_ID);
            // Make the Summary Record available for output
            output = rs.getSummaryRecord();
            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);

            loadGridHeader(request, getAnchorColumnNames()[1], getGridHeaderLayerIds()[1]);

        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load View Quick Pay risks/coverages data.",
                e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllRiskCoverage", af);
        return af;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getQuickPayManager() == null)
            throw new ConfigurationException("The required property 'quickPayManager' is missing.");
        if (getAnchorColumnNames() == null)
            throw new ConfigurationException("The required property 'anchorColumnNames' is missing.");
    }

    public ViewQuickPayDetailAction() {
    }

    public QuickPayManager getQuickPayManager() {
        return m_quickPayManager;
    }

    public void setQuickPayManager(QuickPayManager quickPayManager) {
        m_quickPayManager = quickPayManager;
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
     * @param anchorColumnNames anchorColumnNames
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
     * @param gridHeaderLayerIds gridHeaderLayerIds
     */
    public void setGridHeaderLayerIds(String[] gridHeaderLayerIds) {
        m_gridHeaderLayerIds = gridHeaderLayerIds;
    }

    private String[] m_anchorColumnNames;
    private String[] m_gridHeaderLayerIds;
    private QuickPayManager m_quickPayManager;
    protected static final String RISK_COVERAGE_GRID_ID = "thirdGrid";
}
