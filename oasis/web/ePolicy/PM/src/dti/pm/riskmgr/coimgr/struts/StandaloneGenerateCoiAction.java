package dti.pm.riskmgr.coimgr.struts;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dti.pm.core.struts.PMBaseAction;
import dti.pm.riskmgr.coimgr.CoiManager;
import dti.pm.riskmgr.coimgr.CoiFields;
import dti.pm.busobjs.SysParmIds;
import dti.pm.entitymgr.EntityFields;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.messagemgr.MessageManager;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.ArrayList;

/**
 * This class is for CIS Generate COI. It reused some methods in CoiManager.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 19, 2007
 *
 * @author zlzhu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/21/2007       Joe         add display()/loadAllCoiHolder()/validateAsOfDate() methods
 * 09/13/2010       wfu         111776: Replaced hardcode string with resource definition
 * ---------------------------------------------------
 */

public class StandaloneGenerateCoiAction extends PMBaseAction {
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
        return display(mapping, form, request, response);
    }

    /**
     * Method to load As of Date.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward display(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "display", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {

            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            RecordSet rs = new RecordSet();
            ArrayList fields = new ArrayList();
            fields.add("entityRoleId");
            rs.addFieldNameCollection(fields);
            setDataBean(request, rs);

            // Make the Summary Record available for output
            Record output = rs.getSummaryRecord();

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);

            // Load grid header
            loadGridHeader(request);

            // Populate messages for javascirpt
            addJsMessages();

            Record inputRecord = getInputRecord(request);
            request.setAttribute("entityId", EntityFields.getEntityId(inputRecord));
            request.setAttribute("parentGridId", inputRecord.getStringValue("parentGridId"));
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the As of Date page for generate COI.",
                e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "display", af);
        return af;
    }

    /**
     * Method to load all COI Holder which selected from CIS Entity Role List.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllCoiHolder(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                          HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllCoiHolder", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadResult";
        try {
            // Secures access to the page, loads the oasis fields without loading LOVs,
            // and map the input parameters into the fields.
            securePage(request, form);

            RecordSet rs;
            String txtXML = request.getParameter("txtXML");
            if (!StringUtils.isBlank(txtXML)) {
                rs = getInputRecordSet(request);
                // check the records by default
                rs.setFieldValueOnAll("SELECT_IND", "-1");
                Record summaryRecord = rs.getSummaryRecord();

                // derive min/max dates for each record
                getCoiManager().deriveMinAndMaxDates(rs);

                // get the minimum date from summary record to be value of As of Date
                Record sumRec = rs.getSummaryRecord();
                String asOfDate = CoiFields.getMinimumDate(sumRec);
                CoiFields.setAsOfDate(summaryRecord, asOfDate);

                // set As of Date page entitlement
                String sysPara = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_CISCOI_POPUP_DT, "Y");
                if (YesNoFlag.getInstance(sysPara).booleanValue()) {
                    summaryRecord.setFieldValue("isCoiAsOfDateEditable", YesNoFlag.Y);
                }
                else {
                    summaryRecord.setFieldValue("isCoiAsOfDateEditable", YesNoFlag.N);
                    CoiFields.setAsOfDate(summaryRecord, "");
                }
                summaryRecord.setFieldValue("pmCoiClaimsParam",
                    SysParmProvider.getInstance().getSysParm(SysParmIds.PM_COI_CLAIMS));

                // publish page field
                publishOutputRecord(request, summaryRecord);

                setDataBean(request, rs);
                request.setAttribute("pmCoiClaimsParam", summaryRecord.getStringValue("pmCoiClaimsParam"));
                request.setAttribute(CoiFields.MINIMUM_DATE, CoiFields.getMinimumDate(summaryRecord));
                request.setAttribute(CoiFields.MAXIMUM_DATE, CoiFields.getMaximumDate(summaryRecord));

                // Since this page is reloaded in js function handleOnLoad(), only add info msg in CoiManager is not enough,
                // we have to pass the saveCode (SUCCESS or FAILED) from processAllCoi() method to here.
                String saveCode = request.getParameter("saveCode");
                if (saveCode != null && saveCode.equals("SUCCESS")) {
                    MessageManager.getInstance().addInfoMessage("pm.generateCoi.save.success.info");
                }
            }
            // Loads list of values
            loadListOfValues(request, form);

            // Load grid header bean
            loadGridHeader(request);

            request.setAttribute("dataLoaded", "Y");
            Record inputRecord = getInputRecord(request);
            request.setAttribute("entityId", EntityFields.getEntityId(inputRecord));
            request.setAttribute("parentGridId", inputRecord.getStringValue("parentGridId"));
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR,
                "Failed to load the As of Date for Generate COI page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllCoiHolder", af);
        return af;
    }

    /**
     * To validate As of Date of Generate Client COI.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward validateAsOfDate(ActionMapping mapping,
                                          ActionForm form,
                                          HttpServletRequest request,
                                          HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "validateAsOfDate", new Object[]{mapping, form, request, response});

        try {
            // If the request has valid save token, then proceed with save; if not forward to load page.
            // Secure access to the page without loading the Oasis Fields
            securePage(request, form, false);
            
            // Map As of Date field to RecordSet for input
            RecordSet inputRecords = getInputRecordSet(request);

            // Save the changes
            String policyListStr = getCoiManager().validateAsOfDateForProcessCoi(inputRecords);

            Record output = new Record();
            output.setFieldValue("policyListStr", policyListStr);
            writeAjaxXmlResponse(response, output);
        }
        catch (ValidationException ve) {
            handleValidationExceptionForAjax(ve, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to validate COI as of date.", e, response);
        }

        // Return the forward
        l.exiting(getClass().getName(), "validateAsOfDate", null);
        return null;
    }

    /**
     * To process all Client COI.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward processAllCoi(ActionMapping mapping,
                                       ActionForm form,
                                       HttpServletRequest request,
                                       HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "processAllCoi", new Object[]{mapping, form, request, response});

        String forwardString = "saveResult";
        try {
            // If the request has valid save token, then proceed with generate all COI; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);

                Record inputRecord = getInputRecord(request);

                RecordSet inputRecords = getInputRecordSet(request);
                inputRecords.setFieldsOnAll(inputRecord, false);

                // Save the data
                getCoiManager().processAllCoi(inputRecords);
                if (MessageManager.getInstance().hasMessage("pm.generateCoi.save.success.info")) {
                    request.setAttribute("saveCode", "SUCCESS");
                }
                else {
                    request.setAttribute("saveCode", "FAILED");
                }
            }
        }
        catch (Exception e) {
            forwardString = handleErrorPopup("pm.generateCoi.save.failed.error",
                "Failed to process all Client COI.", e, request, mapping);
        }

        // Return the forward
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "processAllCoi", af);
        return af;
    }


    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getCoiManager() == null)
            throw new ConfigurationException("The required property 'coiManager' is missing.");
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        // For handle with on blur error
        MessageManager.getInstance().addJsMessage("pm.generateCoiAsOfDate.coiHolder.select.error");
    }

    public CoiManager getCoiManager() {
        return m_coiManager;
    }

    public void setCoiManager(CoiManager coiManager) {
        m_coiManager = coiManager;
    }

    private CoiManager m_coiManager;
}
