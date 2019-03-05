package dti.pm.policymgr.premiummgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.filter.CharacterEncodingFilter;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.oasis.tags.OasisGrid;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.oasis.workflowmgr.WorkflowAgent;
import dti.pm.busobjs.SysParmIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.premiummgr.PremiumFields;
import dti.pm.policymgr.premiummgr.PremiumManager;
import dti.pm.riskmgr.RiskFields;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for view premium
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   June 15, 2007
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/06/2011       dzhang      116451 - Modified loadAllPremium(): Publish the inputRecord for filter criteria.
 * 04/11/2011       wqfu        118226 - Modified loadAllPremium(): Using right system parameter.
 * 06/15/2011       syang       111676 - Added exportExcelCSV() to export excel CSV.
 * 08/01/2011       ryzhao      118806 - Do refactoring to move PremiumFields to dti.pm.policymgr.premiummgr package.
 * 08/06/2011       wfu         124367 - Modified loadAllPremium to use premium result set if existed in request.
 * 10/12/2011       fcb         125838 - Changes due to move of filtering of data from JS to DB
 * 03/06/2012       syang       131134 - Modified loadAllPremium() to default riskBaseRecordId to -1.
 * 01/29/2013       tcheng      141447 - Modified loadAllPremium() to view premium in rate workflow.
 * 01/06/2013       fcb         142697 - changes for View Premium screen in the Workflow.
 * 06/06/2013       tcheng      145427 - Modified exportExcelCSV() to convert special character to text format for exporting excel.
 * 11/19/2015       eyin        167171 - Modified exportExcelCSV(), Add logic to remove spaces when dispType is not null.
 * ---------------------------------------------------
 */

public class ViewPremiumAction extends PMBaseAction {


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
        return loadAllPremium(mapping, form, request, response);
    }

    /**
     * Method to load all premium info for requested policy.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllPremium(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllPremium", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadPremResult";
        try {
            // Gets Policy Header
            PolicyHeader policyHeader = getPolicyHeader(request);
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();

            if ( !isShowViewPremium(wa, policyHeader) ) {
                forwardString = wa.getNextState(policyHeader.getPolicyNo());
            }
            else {
                // Secures access to the page, loads the oasis fields without loading LOVs,
                // and map the input parameters into the fields.
                securePage(request, form);
                publishOutputRecord(request, policyHeader.toRecord());
                Record inputRecord = getInputRecord(request);

                // Load the premium list
                RecordSet rs = (RecordSet) request.getAttribute(dti.oasis.http.RequestIds.GRID_RECORD_SET);
                if (rs == null) {
                    rs = getPremiumManager().loadAllPremium(policyHeader, inputRecord);
                }

                //Set page UI attributes
                Record record = rs.getSummaryRecord();
                if ((rs.getSize()) <= 0) {
                    record.setFieldValue(PremiumFields.HAS_PREM_DATA_FOR_TRANSACTION, YesNoFlag.N);
                    record.setFieldValue(PremiumFields.HAS_MEMBER_PREM_CONTRIBUTION, YesNoFlag.N);
                    record.setFieldValue(PremiumFields.HAS_LAYER_DETIAL, YesNoFlag.N);
                    MessageManager.getInstance().addErrorMessage("pm.viewPremiumInfo.premiumList.noDataFound");
                }
                else {
                    record.setFieldValue(PremiumFields.HAS_PREM_DATA_FOR_TRANSACTION, YesNoFlag.Y);
                    SysParmProvider sysParm = SysParmProvider.getInstance();
                    String memContributionFlag = sysParm.getSysParm(SysParmIds.PM_ENT_PREM_CONTRIB);
                    String layerDetailFlag = sysParm.getSysParm(SysParmIds.PM_LAYER_DETAIL);
                    record.setFieldValue(PremiumFields.HAS_MEMBER_PREM_CONTRIBUTION, YesNoFlag.getInstance(memContributionFlag));
                    record.setFieldValue(PremiumFields.HAS_LAYER_DETIAL, YesNoFlag.getInstance(layerDetailFlag));
                }

                record.setFields(inputRecord, false);

                // Since PolicyHeader contains info for the term that is currently displayed in Policy Folder,
                // when retrieving data for transaction snapshot we need to replace the term base record FK in
                // it with the term selected from the transaction snapshot. In this case the termBaseRecordId
                // has been specifically passed in the input record.
                // We need to do this here because we need to retrieve dropdowns that depend on this.
                if (inputRecord.hasStringValue("termBaseRecordId")){
                    String termBaseRecordId = inputRecord.getStringValue("termBaseRecordId");
                    if (!StringUtils.isBlank(termBaseRecordId)) {
                        record.setFieldValue("termBaseRecordId", termBaseRecordId);
                    }
                }

                // The riskBaseRecordId should be defaulted to -1 if it doesn't exist in inputRecord.
                if (!inputRecord.hasStringValue(RiskFields.RISK_BASE_RECORD_ID)) {
                    RiskFields.setRiskBaseRecordId(record, "-1");
                }

                // publish page field
                publishOutputRecord(request, record);
                // Sets data bean
                setDataBean(request, rs);
                // Loads list of values
                loadListOfValues(request, form);
                // Load grid header bean
                loadGridHeader(request);
                //decide the coverage part is visible or not
                boolean visibleFlag = policyHeader.isCoveragePartConfigured();
                OasisFields fieldsMap = (OasisFields) request.getAttribute("fieldsMap");

                HashMap layerFieldsMap = fieldsMap.getLayerFieldsMap("PM_VIEW_PREM_GH");
                OasisFormField field = (OasisFormField) (layerFieldsMap.get("COVGPARTCODE_GH"));
                field.setIsVisible(visibleFlag);

                request.setAttribute(PremiumFields.IS_IN_WORKFLOW,
                    WorkflowAgentImpl.getInstance().hasWorkflow(policyHeader.getPolicyNo()) ? YesNoFlag.Y : YesNoFlag.N);
            }
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the loadAllPremium page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllPremium", af);
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
            //decide the coverage part is visible or not
            boolean visibleFlag = policyHeader.isCoveragePartConfigured();
            OasisFields fieldsMap = (OasisFields) request.getAttribute("fieldsMap");
            HashMap layerFieldsMap = fieldsMap.getLayerFieldsMap("PM_VIEW_PREM_GH");
            OasisFormField field = (OasisFormField) (layerFieldsMap.get("COVGPARTCODE_GH"));
            field.setIsVisible(visibleFlag);
            // Get all premium
            RecordSet rs = getPremiumManager().loadAllPremium(policyHeader, inputRecord);
            String textForFile = StringUtils.htmlToText(getExcelCSVData(request, rs, "PM_VIEW_PREM_GH"));
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
            //decide the coverage part is visible or not
            boolean visibleFlag = policyHeader.isCoveragePartConfigured();
            OasisFields fieldsMap = (OasisFields) request.getAttribute("fieldsMap");
            HashMap layerFieldsMap = fieldsMap.getLayerFieldsMap("PM_VIEW_PREM_GH");
            OasisFormField field = (OasisFormField) (layerFieldsMap.get("COVGPARTCODE_GH"));
            field.setIsVisible(visibleFlag);
            // Get all premium
            RecordSet rs = getPremiumManager().loadAllPremium(policyHeader, inputRecord);
            String textForFile = StringUtils.htmlToText(getExcelCSVData(request, rs, "PM_VIEW_PREM_GH"));
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

    /**
     *
     * @param wa WorkflowAgent
     * @param policyHeader PolicyHeader
     * @return true/false
     */
    private boolean isShowViewPremium(WorkflowAgent wa, PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isShowViewPremium", new Object[]{wa, policyHeader});
        }

        boolean show = true;
        String autoViewPremiumIndicator = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_AUTO_VIEW_PREMIUM, "ALL");

        if (wa.hasWorkflow(policyHeader.getPolicyNo())) {
            YesNoFlag showViewPremium = YesNoFlag.N;
            if (wa.hasWorkflowAttribute(policyHeader.getPolicyNo(), "showViewPremium"))  {
                showViewPremium = (YesNoFlag)wa.getWorkflowAttribute(policyHeader.getPolicyNo(), "showViewPremium");
            }

            boolean viewPremiumConfigured = "ALL".equals(autoViewPremiumIndicator) ||
                ",".concat(autoViewPremiumIndicator).concat(",").toLowerCase().contains(
                    ",".concat(policyHeader.getPolicyTypeCode()).concat(",").toLowerCase());

            if ( !viewPremiumConfigured || !showViewPremium.booleanValue()) {
                show = false;
            }
        }

        l.exiting(getClass().getName(), "isShowViewPremium", show);

        return show;
    }

    public PremiumManager getPremiumManager() {
        return m_PremiumManager;
    }

    public void setPremiumManager(PremiumManager premiumManager) {
        m_PremiumManager = premiumManager;
    }


    private PremiumManager m_PremiumManager;
}

