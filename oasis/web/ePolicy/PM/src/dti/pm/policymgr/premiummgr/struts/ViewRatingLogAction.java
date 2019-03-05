package dti.pm.policymgr.premiummgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.filter.CharacterEncodingFilter;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.tags.OasisGrid;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.premiummgr.PremiumAccountingFields;
import dti.pm.policymgr.premiummgr.PremiumManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 28, 2007
 * action class for view rating log
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/08/2010       syang       Issue 111672 - Publish the inputRecord for filter criteria.
 * 06/15/2011       syang       111676 - Added exportExcelCSV() to export excel CSV.
 * 06/06/2013       tcheng      145427 - Modified exportExcelCSV() to convert special character to text format for exporting excel.
 * 11/19/2015       eyin        167171 - Modified exportExcelCSV(), Add logic to remove spaces when dispType is not null.
 * ---------------------------------------------------
 */
public class ViewRatingLogAction extends PMBaseAction {

    /**
     * This method is triggered automatically when where is no process parameter
     * sent in along the requested url.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadAllRatingLog(mapping, form, request, response);
    }

    /**
     * Method to load all special handlings for requested policy.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllRatingLog(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                          HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRatingLog", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadRatingLogResult";
        try {

            // Secures access to the page, loads the oasis fields without loading LOVs,
            // and map the input parameters into the fields.
            securePage(request, form);
            // Gets Policy Header
            PolicyHeader policyHeader = getPolicyHeader(request);

            Record inputRecord = getInputRecord(request);

            RecordSet rs = getPremiumManager().loadAllRatingLog(policyHeader, inputRecord);

            if (rs.getSize() <= 0) {
                MessageManager.getInstance().addErrorMessage("pm.viewRatingLog.nodata.error");
            }
            Record record = rs.getSummaryRecord();
            record.setFields(inputRecord, false);
            publishOutputRecord(request, record);
            // Sets data bean
            setDataBean(request, rs);
            // Loads list of values
            loadListOfValues(request, form);
            //addJsMessage
            addJsMessages();
            // Load grid header bean
            loadGridHeader(request);


        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the loadAllRatingLog page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllRatingLog", af);
        return af;
    }

    /**
     * Export excel CSV.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward exportExcelCSV(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "exportExcelCSV", new Object[]{mapping, form, request, response});
        }

        String forwardString = null;
        try {
            // Secures access to the page, loads the oasis fields without loading LOVs,
            // and map the input parameters into the fields.
            securePage(request, form);
            // Gets Policy Header
            PolicyHeader policyHeader = getPolicyHeader(request);
            Record inputRecord = getInputRecord(request);
            // Get all rating log
            PremiumAccountingFields.setExportB(inputRecord, YesNoFlag.Y.toString());
            RecordSet rs = getPremiumManager().loadAllRatingLog(policyHeader, inputRecord);
            String textForFile = StringUtils.htmlToText(getExcelCSVData(request, rs, "PM_VIEW_RATING_LOG_GH"));
            String encoding = ApplicationContext.getInstance().getProperty(CharacterEncodingFilter.CHARACTER_ENCODING_DEFAULT);
            response.setHeader("Content-Type", "text/html; charset=" + encoding);
            response.setContentType("application/x-excel");
            String dispType = request.getParameter("dispositionType");
            if (StringUtils.isBlank(dispType)) {
                dispType = OasisGrid.ATTACH_DISP_TYPE;
            }
            else {
                dispType = org.apache.commons.lang3.StringUtils.deleteWhitespace(dispType);
            }
            response.setHeader("Content-Disposition", dispType + "; filename=grid.csv");
            ServletOutputStream out = response.getOutputStream();
            out.print(textForFile);
            out.flush();
            out.close();
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to export excel CSV.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "exportExcelCSV", af);
        return af;
    }

    /**
     * Export excel XLS or XLSX.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward exportExcelXLS(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "exportExcelXLS", new Object[]{mapping, form, request, response});
        }

        String forwardString = null;
        try {
            // Secures access to the page, loads the oasis fields without loading LOVs,
            // and map the input parameters into the fields.
            securePage(request, form);
            // Gets Policy Header
            PolicyHeader policyHeader = getPolicyHeader(request);
            Record inputRecord = getInputRecord(request);
            // Get all rating log
            PremiumAccountingFields.setExportB(inputRecord, YesNoFlag.Y.toString());
            RecordSet rs = getPremiumManager().loadAllRatingLog(policyHeader, inputRecord);
            String textForFile = StringUtils.htmlToText(getExcelCSVData(request, rs, "PM_VIEW_RATING_LOG_GH"));
            String exportType = request.getParameter("exportType");
            String fileExt = ".xlsx";
            if (exportType.equalsIgnoreCase("XLS"))
                fileExt = ".xls";

            String gridId = request.getParameter("gridId");
            String fileName = "";
            if (StringUtils.isBlank(gridId)) {
                fileName = "grid" + fileExt;
            }
            else {
                fileName = gridId + fileExt;
            }
            processExcelExport(request, response, textForFile, fileName);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to export excel XLS or XLSX.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "exportExcelXLS", af);
        return af;
    }

    public PremiumManager getPremiumManager() {
        return m_PremiumManager;
    }

    public void setPremiumManager(PremiumManager premiumManager) {
        m_PremiumManager = premiumManager;
    }

    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.viewRatingLog.coverageFilter.error");
    }

    private PremiumManager m_PremiumManager;
}
