package dti.pm.policymgr.dividendmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.util.LogUtils;
import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.dividendmgr.DividendFields;
import dti.pm.policymgr.dividendmgr.DividendManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Action class for Dividend Report.
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 30, 2011
 *
 * @author wfu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
  *
 * ---------------------------------------------------
 */

public class DividendReportAction extends PMBaseAction {
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
    public ActionForward unspecified(ActionMapping mapping,
                                     ActionForm form,
                                     HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        return loadAllDividendReport(mapping, form, request, response);
    }

    /**
     * Method to load list of available dividend report.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllDividendReport(ActionMapping mapping,
                                               ActionForm form,
                                               HttpServletRequest request,
                                               HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllDividendReport", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);

            // Load the report summary
            RecordSet rs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            // Load the report detail
            RecordSet detailRs = (RecordSet) request.getAttribute(DETAIL_GRID_RECORD_SET);

            if (rs == null) {
                rs = getDividendManager().loadAllDividendReportSummary(inputRecord);
            }
            if (detailRs == null) {
                if (rs.getSize() == 0) {
                    //If no summary, set eventId as -1 to return empty recordSet
                    DividendFields.setDividendEventId(inputRecord, "-1");
                } else {
                    //Set eventId as null to load all records for filtering
                    DividendFields.setDividendEventId(inputRecord, null);
                }
                detailRs = getDividendManager().loadAllDividendReportDetail(inputRecord);
            }
            // Set loaded report summary data into request
            setDataBean(request, rs);

            // Set loaded report detail data into request
            setDataBean(request, detailRs, DIVIDEND_REPORT_SUMMARY_GRID_ID);

            // Load the list of values after loading the data
            loadListOfValues(request, form);
            loadGridHeader(request);

            // Set currentGridId to componentListGrid before load component gird header
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, DIVIDEND_REPORT_SUMMARY_GRID_ID);

            // Load the list of values after loading the data
            loadListOfValues(request, form);

            // Load component grid header
            loadGridHeader(request, null, DIVIDEND_REPORT_SUMMARY_GRID_ID, DETAIL_GRID_LAYER_ID);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the dividend report.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllDividendReport", af);
        return af;
    }

    /**
     * Overrides method in BaseAction. To get the anchor column name by current grid Id.
     * This is effective only for the multiple grids.
     *
     * @return anchor column name for the current grid
     */
    public String getAnchorColumnName() {
        Logger l = LogUtils.enterLog(getClass(), "getAnchorColumnName");
        String anchorName;
        if (RequestStorageManager.getInstance().has(CURRENT_GRID_ID)) {
            String currentGridId = (String) RequestStorageManager.getInstance().get(CURRENT_GRID_ID);
            if (currentGridId.equals(DIVIDEND_REPORT_SUMMARY_GRID_ID)) {
                anchorName = getDetailAnchorColumnName();
            }
            else {
                anchorName = super.getAnchorColumnName();
            }
        }
        else {
            anchorName = super.getAnchorColumnName();
        }
        l.exiting(getClass().getName(), "getAnchorColumnName", anchorName);
        return anchorName;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    /**
     * Verify dividendManager, anchorColumnName and detailAnchorColumnName in spring config
     */
    public void verifyConfig() {
        if (getDividendManager() == null)
            throw new ConfigurationException("The required property 'dividendManager' is missing.");
        if (super.getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getDetailAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'detailAnchorColumnName' is missing.");
    }

    public DividendManager getDividendManager() {
        return m_dividendManager;
    }

    public void setDividendManager(DividendManager dividendManager) {
        this.m_dividendManager = dividendManager;
    }

    public String getDetailAnchorColumnName() {
        return m_detailAnchorColumnName;
    }

    public void setDetailAnchorColumnName(String detailAnchorColumnName) {
        m_detailAnchorColumnName = detailAnchorColumnName;
    }

    protected static final String CURRENT_GRID_ID = "currentGridId";
    protected static final String DIVIDEND_REPORT_SUMMARY_GRID_ID = "dividendReportDetailListGrid";
    protected static final String DETAIL_GRID_LAYER_ID = "PM_DIVIDEND_REP_DETAIL_GH";
    protected static final String DETAIL_GRID_RECORD_SET = "detailGridRecordSet";

    private DividendManager m_dividendManager;
    private String m_detailAnchorColumnName;

}