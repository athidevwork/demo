package dti.pm.policymgr.processacfmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.processacfmgr.ProcessAcfFields;
import dti.pm.policymgr.processacfmgr.ProcessAcfManager;
import dti.pm.policymgr.processacfmgr.impl.ProcessAcfFeeEntitlementRecordLoadProcessor;
import dti.pm.policymgr.processacfmgr.impl.ProcessAcfOverrideEntitlementRecordLoadProcessor;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for Maintain ACF.
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 30, 2011
 *
 * @author syang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/26/2013       adeng       143728 - Modified loadAllAcf to Set fields 'riskFilter' to always enabled
 * ---------------------------------------------------
 */

public class MaintainAcfAction extends PMBaseAction {


    /**
     * This method is triggered automatically when where is no process parameter sent in along the requested url.
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
        return loadAllAcf(mapping, form, request, response);
    }

    /**
     * Method to validate the transaction for premium worksheet.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllAcf(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                    HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllAcf", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {
            // Secures page
            securePage(request, form);
            // Get input record and policyHeader
            Record inputRecord = getInputRecord(request);
            PolicyHeader policyHeader = getPolicyHeader(request);
            // Get record set and set data bean
            RecordSet[] recordSets = (RecordSet[]) request.getAttribute(GRID_RECORD_SETS);
            Record outputRecord = new Record();
            if (recordSets == null) {
                recordSets = new RecordSet[4];
                ArrayList rsList = new ArrayList();
                ProcessAcfFields.setTermId(inputRecord, policyHeader.getTermBaseRecordId());
                // Get record set for first grid - product
                RecordSet productRs = getProcessAcfManager().loadAllProduct(inputRecord);
                outputRecord.setFields(productRs.getSummaryRecord());
                rsList.add(productRs);

                // Get record set for second grid - override
                RecordSet overrideRs = getProcessAcfManager().loadAllOverride(inputRecord, policyHeader);
                // "isSaveAvailable" might be overrided in load processor.
                outputRecord.setFields(overrideRs.getSummaryRecord());
                rsList.add(overrideRs);

                // Get data bean for third grid - result
                RecordSet resultRs = getProcessAcfManager().loadAllResult(inputRecord);
                outputRecord.setFields(resultRs.getSummaryRecord(), false);
                rsList.add(resultRs);

                // Get data bean for fourth grid - fee
                RecordSet feeRs = getProcessAcfManager().loadAllFee(inputRecord, policyHeader);
                outputRecord.setFields(feeRs.getSummaryRecord(), false);
                rsList.add(feeRs);
                // If product procedure retrieves no any data, system should disable all the buttons.
                if (productRs.getSize() == 0) {
                    overrideRs.setFieldValueOnAll(ProcessAcfOverrideEntitlementRecordLoadProcessor.IS_OVERRIDE_DEL_AVAILABLE, YesNoFlag.N);
                    feeRs.setFieldValueOnAll(ProcessAcfFeeEntitlementRecordLoadProcessor.IS_FEE_DEL_AVAILABLE, YesNoFlag.N);
                    outputRecord.setFieldValue(ProcessAcfFeeEntitlementRecordLoadProcessor.IS_FEE_ADD_AVAILABLE, YesNoFlag.N);
                    outputRecord.setFieldValue(ProcessAcfOverrideEntitlementRecordLoadProcessor.IS_ADD_AVAILABLE, YesNoFlag.N);
                }

                rsList.toArray(recordSets);
            }
            outputRecord.setFields(inputRecord);
            // Set all data beans to request
            setAllDataBean(request, recordSets);
            // Public output record
            publishOutputRecord(request, outputRecord);
            // Load all grid headers for all four grids, the grid header bean name is:
            // firstGridHeaderBean, secondGridHeaderBean, thirdGridHeaderBean, fourthGridHeaderBean
            loadAllGridHeader(request, getAnchorColumnNames());
            // Load LOVs
            loadListOfValues(request, form);
            //Set fields 'riskFilter' to always enabled.
            setAlwaysEnabledFieldIds("riskFilter");
            // add js messages to message manager for the current request
            addJsMessages();
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the ACF page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllAcf", af);
        return af;
    }

    /**
     * Get initial values for the override.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getInitialValuesForOverride(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                     HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForOverride", new Object[]{mapping, form, request, response});
        try {
            // Secure page.
            securePage(request, form);
            // Get input record.
            Record inputRecord = getInputRecord(request);
            // Get policy header.
            PolicyHeader policyHeader = getPolicyHeader(request);
            // Get the initial values.
            Record record = getProcessAcfManager().getInitialValuesForOverride(inputRecord, policyHeader);
            // get LOV labels for initial values
            publishOutputRecord(request, record);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, record);
            // Write xml response.
            writeAjaxXmlResponse(response, record);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values for override .", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForOverride", af);
        return af;
    }

    /**
     * Get initial values for the override.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getInitialValuesForFee(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForFee", new Object[]{mapping, form, request, response});
        try {
            // Secure page.
            securePage(request, form);
            // Get input record.
            Record inputRecord = getInputRecord(request);
            // Get policy header.
            PolicyHeader policyHeader = getPolicyHeader(request);
            // Get the initial values.
            Record record = getProcessAcfManager().getInitialValuesForFee(inputRecord, policyHeader);
            // get LOV labels for initial values
            publishOutputRecord(request, record);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, record);
            // Write xml response.
            writeAjaxXmlResponse(response, record);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values for fee .", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForFee", af);
        return af;
    }

    /**
     * Save all override and fee.
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward saveAllAcf(ActionMapping mapping,
                                    ActionForm form,
                                    HttpServletRequest request,
                                    HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllAcf", new Object[]{mapping, form, request, response});
        }

        String forwardString = "saveResult";
        RecordSet[] inputRecordSets = null;
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (isTokenValid(request, true)) {
                // Secure page
                securePage(request, form, false);
                //get policy header
                PolicyHeader policyHeader = getPolicyHeader(request);
                // get input record sets for all grids
                inputRecordSets = getAllInputRecordSet(request, getAnchorColumnNames());
                // Call the business component to implement the validate/save logic
                getProcessAcfManager().saveAllAcf(inputRecordSets, policyHeader);
            }
        }
        catch (ValidationException v) {
            // Save the record set into the request
            request.setAttribute(GRID_RECORD_SETS, inputRecordSets);
            // Handle the validation exception
            handleValidationException(v, request);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to save all acf.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllAcf", af);
        }
        return af;
    }

    /**
     * add js messages to message manager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.processAcf.add.override.noProductSelected");
    }

    /**
     * Configuration constructor and access methods
     */
    public void verifyConfig() {
        if (getProcessAcfManager() == null)
            throw new ConfigurationException("The required property 'processAcfManager' is missing.");
        if (getAnchorColumnNames() == null) {
            throw new ConfigurationException("The required property 'anchorColumnNames' is missing.");
        }
    }

    /**
     * Get ProcessAcfManager
     *
     * @return
     */
    public ProcessAcfManager getProcessAcfManager() {
        return m_processAcfManager;
    }

    /**
     * Set ProcessAcfManager.
     *
     * @param processAcfManager
     */
    public void setProcessAcfManager(ProcessAcfManager processAcfManager) {
        m_processAcfManager = processAcfManager;
    }

    /**
     * Get all anchor column names
     *
     * @return String[]
     */
    public String[] getAnchorColumnNames() {
        return m_anchorColumnNames;
    }

    /**
     * Set anchor column names
     *
     * @param anchorColumnNames
     */
    public void setAnchorColumnNames(String[] anchorColumnNames) {
        m_anchorColumnNames = anchorColumnNames;
    }

    private ProcessAcfManager m_processAcfManager;
    private String[] m_anchorColumnNames;
    private static final String GRID_RECORD_SETS = "gridRecordSets";
}