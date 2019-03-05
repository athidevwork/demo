package dti.pm.riskmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.DisplayIndicator;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.session.UserSessionManager;
import dti.oasis.struts.IOasisAction;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.WebLayer;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.riskmgr.RiskFields;
import dti.pm.riskmgr.impl.RiskRowStyleRecordLoadprocessor;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for Maintain Risk.
 * <p/>
 * <p>(C) 2013 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 25, 2013
 *
 * @author xnie
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/25/2013       xnie        148083 - Initial version.
 * 3/18/2014        xnie        152969 - Modified addJsMessages() to show error message when user wants to go into risk
 *                                       detail page and new risk tab page has data changes.
 * 04/22/2014       adeng       154037 - Modified addRiskBatch() to set the correct selected riskId into session.
 * 07/25/2014       awu         152034 - 1). Added updatePolicyHeader to set coverageHeader and coverageClassId.
 *                                       2). Modified loadAllRiskSummaryOrDetail to call updatePolicyHeader, and remove the logic of user session.
 * 08/25/2014       awu         152034 - Modified updatePolicyHeader to highlight the active record if the current record is closed.
 * 09/15/2014       wdang       157555 - Modified updatePolicyHeader to check if riskId is a numeric before use it.
 * 12/19/2014       wdang       159454 - Modified addJsMessages() to show error messages when user clicks button 'occupant'
 *                                       in some case.
 * 05/07/2016       wdang       157211 - Modified getRiskAddlInfo to put isInsuredTrackingAvailable.
 * 10/13/2015       tzeng       164679 - Modified loadAllRiskSummaryOrDetail and added RISK_RELATION_MESSAGE_TYPE to
 *                                       display risk relation result message after save and close risk detail page.
 * 01/28/2016       wdang       169024 - 1) Reverted changes of 164679.
 *                                       2) Modified loadAllRiskSummaryOrDetail to invoke addSaveMessages().
 * 07/22/2016       bwang       178033 - Modified updatePolicyHeader(),Changed integer type variables to long type
 *                                       which are from PK/FK fields in DB.
 * 08/15/2016       eyin        177410 - Modified addJsMessages(), add confirmation prompt msg used when state was changed.
 * 07/26/2017       lzhang      182246 - Delete Js message
 * ---------------------------------------------------
 */
public class ViewRiskSummaryAction extends PMBaseAction {

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
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute(RequestIds.PROCESS, "loadAllRiskSummary");
        return loadAllRiskSummaryOrDetail(mapping, form, request, response);
    }

    /**
     * Method to load list of available risk for requested policy.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllRiskSummaryOrDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllRiskSummaryOrDetail", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {

            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            //First,load policy header without risk header
            PolicyHeader policyHeader = getPolicyHeader(request);

            // Load the Risk Summary
            RecordSet rs = (RecordSet) request.getAttribute(dti.oasis.http.RequestIds.GRID_RECORD_SET);
            if (rs == null) {
                RecordLoadProcessor rowStyleLp = new RiskRowStyleRecordLoadprocessor();
                rs = getRiskManager().loadAllRiskSummaryOrDetail(policyHeader, rowStyleLp, null);
            }
            setDataBean(request, rs);

            // Make the Summary Record available for output
            Record output = rs.getSummaryRecord();

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);

            // Load the list of values after loading the data
            loadListOfValues(request, form);

            loadGridHeader(request);

            //Set fields 'Procedure Codes' in risk tab to always enabled.
            setAlwaysEnabledFieldIds("procedureCodes");

            //add js messages to messagemanager for the current request
            addJsMessages();

            //add messages for save purpose
            addSaveMessages(policyHeader, request);

            //update risk header, coverage header, coverage class id to policy header.
            updatePolicyHeader(request, policyHeader, rs);

            request.setAttribute(RiskFields.IS_REINSTATE_IBNR_RISK_VALID, RiskFields.getIsReinstateIbnrRiskValid(output));
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the risk summary page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllRiskSummaryOrDetail", af);
        return af;
    }

    /**
     * Save all updated risk summary records.
     */
    public ActionForward saveAllRiskSummary(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "save", new Object[]{mapping, form, request, response});
        String forwardString = "saveResult";
        RecordSet inputRecords = null;
        RecordSet rs = null;
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);
                PolicyHeader policyHeader = getPolicyHeader(request);
                // Map textXML to RecordSet for input
                inputRecords = getInputRecordSet(request);
                //load risk summary before modified
                RecordLoadProcessor rowStyleLp = new RiskRowStyleRecordLoadprocessor();
                rs = getRiskManager().loadAllRiskSummaryOrDetail(policyHeader, rowStyleLp, null, false);
                //merge modified risk summary records and old risk summary records
                rs.merge(inputRecords, "riskId");
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "saveAllRiskSummary", "Saving the risk summary inputRecords: " + inputRecords);
                }
                // Save the changes
                getRiskManager().processSaveAllRisk(getPolicyHeader(request), rs);
            }

        }
        catch (ValidationException ve) {
            // Save the input records into request
            request.setAttribute(RequestIds.GRID_RECORD_SET, rs);

            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to save the risk summary page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "save", af);
        return af;
    }

    /**
     * Save all updated risk summary records.
     */
    public ActionForward autoSaveAllRiskSummary(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "autoSaveAllRiskSummary", new Object[]{mapping, form, request, response});

        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);

                PolicyHeader policyHeader = getPolicyHeader(request);
                // Map textXML to RecordSet for input
                RecordSet inputRecords = getInputRecordSet(request);
                //load risk summary before modified
                RecordLoadProcessor rowStyleLp = new RiskRowStyleRecordLoadprocessor();
                RecordSet rs = getRiskManager().loadAllRiskSummaryOrDetail(policyHeader, rowStyleLp, null);
                //merge modified risk summary records and old risk summary records
                rs.merge(inputRecords, "riskId");
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "autoSaveAllRiskSummary", "Saving the risk summary inputRecords: " + inputRecords);
                }
                // Save the changes
                getRiskManager().processAutoSaveAllRisk(getPolicyHeader(request), rs);

                writeEmptyAjaxXMLResponse(response);
            }
        }
        catch (Exception e) {
            handleErrorForAutoSaveAjax(AppException.UNEXPECTED_ERROR, "Failed to auto save the risk summary page.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "autoSaveAllRiskSummary", af);
        return af;
    }

    /**
     * Get Add Code for a selected risk type.
     */
    public ActionForward getAddCodeForRisk(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getAddCodeForRisk", new Object[]{mapping, form, request, response});
        try {
            // Secure access to the page without loading the Oasis Fields
            securePage(request, form, false);

            // get Add Code
            String addCode = getRiskManager().getAddCodeForRisk(getInputRecord(request));
            Record record = new Record();
            record.setFieldValue("addCode", addCode);
            writeAjaxXmlResponse(response, record, true);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get add code for risk.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getAddCodeForRisk", af);
        return af;
    }

    /**
     * validate for adding risk.
     */
    public ActionForward validateForAddRisk(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "validateForAddRisk",
            new Object[]{mapping, form, request, response});
        try {
            // Secure access to the page without loading the Oasis Fields
            securePage(request, form, false);

            // validate new risk data
            try {
                getRiskManager().validateForAddRisk(getPolicyHeader(request), getInputRecord(request));

//                writeAjaxXmlResponse(response, new Record(), true);
                // Send back xml data
                writeEmptyAjaxXMLResponse(response);
            }
            catch (ValidationException e) {
                handleValidationExceptionForAjax(e, response);
            }
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to validate.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "validateForAddRisk", af);
        return af;
    }

    /**
     * Check if Change option is available
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward validateForOoseRisk(ActionMapping mapping,
                                             ActionForm form,
                                             HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "validateForOoseRisk",
            new Object[]{mapping, form, request, response});
        try {
            // Secure access to the page
            securePage(request, form, false);

            PolicyHeader policyHeader = getPolicyHeader(request);
            Record inputRecord = getInputRecord(request);

            // do validation
            getRiskManager().validateForOoseRisk(policyHeader, inputRecord);

            // Send back xml data
            writeEmptyAjaxXMLResponse(response);
        }
        catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to validate OOSE risk.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "validateForOoseRisk", af);
        return af;
    }

    /**
     * Get initial values for OOSE risk
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getInitialValuesForOoseRisk(ActionMapping mapping,
                                                     ActionForm form,
                                                     HttpServletRequest request,
                                                     HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForOoseRisk",
            new Object[]{mapping, form, request, response});
        try {
            // Secure access to the page
            securePage(request, form);

            PolicyHeader policyHeader = getPolicyHeader(request, true);
            Record inputRecord = getInputRecord(request);

            // get original values
            RecordLoadProcessor rowStyleLp = new RiskRowStyleRecordLoadprocessor();
            Record origRecord = getRiskManager().loadAllRiskSummaryOrDetail(policyHeader, rowStyleLp, RiskFields.getRiskId(inputRecord)).getFirstRecord();

            // get initial values
            Record record = getRiskManager().getInitialValuesForOoseRisk(policyHeader, inputRecord);

            // merge the record
            record.setFields(origRecord, false);

            // get LOV labels for initial values
            publishOutputRecord(request, record);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, record);
            UserSessionManager.getInstance().getUserSession().set(RiskFields.CACHED_RISK_RECORD, record);

            // prepare return values
            writeAjaxXmlResponse(response, record, true);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values for OOSE risk.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForOoseRisk", af);
        return af;
    }

    /**
     * get initial values for adding risk.
     */
    public ActionForward getInitialValuesForAddRisk(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForAddRisk",
            new Object[]{mapping, form, request, response});
        try {
            // Secure access to the page
            securePage(request, form);

            // get initial values
            Record record = getRiskManager().getInitialValuesForAddRisk(getPolicyHeader(request), getInputRecord(request));

            // get LOV labels for initial values
            publishOutputRecord(request, record);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, record);
            UserSessionManager.getInstance().getUserSession().set(RiskFields.CACHED_RISK_RECORD, record);

            // prepare return values
            writeAjaxXmlResponse(response, record, true);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForAddRisk", af);
        return af;
    }

    /**
     * Get initial values for slot occupant
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getInitialValuesForSlotOccupant(ActionMapping mapping,
                                                         ActionForm form,
                                                         HttpServletRequest request,
                                                         HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForSlotOccupant",
            new Object[]{mapping, form, request, response});
        try {
            //Secure page and load all Oasis fields value include lov label fields -149171.
            securePage(request, form);

            PolicyHeader policyHeader = getPolicyHeader(request, true);
            Record inputRecord = getInputRecord(request);

            // get original values
            RecordLoadProcessor rowStyleLp = new RiskRowStyleRecordLoadprocessor();
            Record origRecord = getRiskManager().loadAllRiskSummaryOrDetail(policyHeader, rowStyleLp, RiskFields.getRiskId(inputRecord)).getFirstRecord();

            // get initial values
            Record record = getRiskManager().getInitialValuesForSlotOccupant(policyHeader, inputRecord);

            // merge the record
            record.setFields(origRecord, false);

            // get LOV labels for initial values
            publishOutputRecord(request, record);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, record);
            UserSessionManager.getInstance().getUserSession().set(RiskFields.CACHED_RISK_RECORD, record);

            // prepare return values
            writeAjaxXmlResponse(response, record, true);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values for slot occupant.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForSlotOccupant", af);
        return af;
    }

    /**
     * An ajax call to get risk summary addl information based on each risk summary selected
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getRiskAddlInfo(ActionMapping mapping,
                                         ActionForm form,
                                         HttpServletRequest request,
                                         HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getRiskAddlInfo",
            new Object[]{mapping, form, request, response});
        try {
            // Secure access to the page
            securePage(request, form, false);

            PolicyHeader policyHeader = getPolicyHeader(request);
            Record inputRecord = getInputRecord(request);
            inputRecord.setFields(policyHeader.toRecord(), false);

            // get data change allow flag
            PolicyHeaderFields.setPolicyTypeCode(inputRecord, policyHeader.getPolicyTypeCode());
            YesNoFlag isDateChangeAllowed = YesNoFlag.getInstance(getRiskManager().isDateChangeAllowed(inputRecord));

            // get determination of the coverage class option for this risk
            boolean coverageClassAvailable = getPolicyManager().isCoverageClassAvailable(policyHeader, inputRecord, false);

            Record addlInfoRecord = new Record();

            // pull all record information together
            addlInfoRecord.setFieldValue("isInsuredTrackingAvailable", isDateChangeAllowed.booleanValue() ? YesNoFlag.N : YesNoFlag.Y);
            addlInfoRecord.setFieldValue("isCoverageClassAvailable", YesNoFlag.getInstance(coverageClassAvailable));

            //set FTE available info
            if (getRiskManager().getFteFacilityCount(inputRecord) > 0) {
                addlInfoRecord.setFieldValue("isEmpPhysAvailable", YesNoFlag.Y);
            }
            else {
                addlInfoRecord.setFieldValue("isEmpPhysAvailable", YesNoFlag.N);
            }

            // prepare return values
            writeAjaxXmlResponse(response, addlInfoRecord, true);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get risk additional information.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getRiskAddlInfo", af);
        return af;
    }

    /**
     * Method to add risks in batch.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward addRiskBatch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "addRiskBatch", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {

            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            securePage(request, form);

            PolicyHeader policyHeader = getPolicyHeader(request);
            // Map textXML to RecordSet for input
            RecordSet inputRecords = getInputRecordSet(request);

            //load existing risks in database
            RecordLoadProcessor rowStyleLp = new RiskRowStyleRecordLoadprocessor();
            RecordSet rs = getRiskManager().loadAllRiskSummaryOrDetail(policyHeader, rowStyleLp, null);

            //merge modified risk records and old risk records
            rs.merge(inputRecords, "riskId");

            Record inputRecord = getInputRecord(request);
            RecordSet newRecords = getRiskManager().getAllMergedRisk(policyHeader, inputRecord, rs);

            // Set data bean
            setDataBean(request, newRecords);

            // Make the Summary Record available for output
            Record output = newRecords.getSummaryRecord();

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);

            // Load the list of values after loading the data
            loadListOfValues(request, form);

            loadGridHeader(request);

            //add js messages to messagemanager for the current request
            addJsMessages();

            // Issue 106158. Disable default sorting by clear up default value for "gridSortOrder" field in layer.
            OasisFields fieldsMap = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);
            List layerList = fieldsMap.getLayers();
            if (layerList != null && layerList.size() > 0) {
                WebLayer riskLayer = (WebLayer) layerList.get(0);
                riskLayer.setGridSortOrder("");
            }
            // Set current select risk to the first new added risk
            if (rs.getSize() > 0) {
                RecordSet insertedRecords = rs.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.INSERTED));
                if (insertedRecords.getSize() > 0) {
                    //Set the current risk id to policy header, system will highlight it on Risk page.
                    policyHeader.setCurrentSelectedId(RequestIds.RISK_ID, rs.getSummaryRecord().getStringValue("selectedRiskId"));
                }
            }
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to add risks page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "addRiskBatch", af);
        return af;
    }

    /**
     *  Load coverageHeader and set them to policyHeader.
     * @param request
     * @param policyHeader
     * @return
     */
    private void updatePolicyHeader(HttpServletRequest request, PolicyHeader policyHeader, RecordSet rs) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "updatePolicyHeader", new Object[]{policyHeader});
        }

        String riskId = request.getParameter(RequestIds.RISK_ID);

        //If the risk Id in URL is existing at the loaded recordSet, and its record is not closed, then highlight it.
        RecordSet riskRecSet = null;
        Record riskRec = null;
        if (StringUtils.isNumeric(riskId)) {
            riskRecSet = rs.getSubSet(new RecordFilter(RiskFields.RISK_ID, riskId));
        }
        if (riskRecSet != null && riskRecSet.getSize() > 0) {
            riskRec = riskRecSet.getRecord(0);

            //Highlight the first active record if the original official record is split to more than 2 versions.
            if (DisplayIndicator.INVISIBLE.equals(riskRec.getDisplayIndicator())) {
                RecordSet officialRS = rs.getSubSet(new RecordFilter(RiskFields.OFFICIAL_RECORD_ID, riskId));
                if (officialRS.getSize() > 0) {
                    riskId = RiskFields.getRiskId(officialRS.getRecord(0));
                }
            }
            getRiskManager().loadRiskHeader(policyHeader, riskId);
        }

        if (policyHeader.hasRiskHeader()) {
            //Set current risk id to policy header, system will highlight it on Risk page.
            policyHeader.setCurrentSelectedId(RequestIds.RISK_ID, policyHeader.getRiskHeader().getRiskId());

            //Load the coverage header if coverageId is existing in the request.
            String coverageId = request.getParameter(RequestIds.COVERAGE_ID);
            if (coverageId != null && StringUtils.isNumeric(coverageId) && Long.parseLong(coverageId) > 0) {
                policyHeader = getCoverageManager().loadCoverageHeader(policyHeader, coverageId);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "updatePolicyHeader", policyHeader);
        }
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.addRisk.riskExists.error");
        MessageManager.getInstance().addJsMessage("pm.addRisk.noRiskSelected.error");
        MessageManager.getInstance().addJsMessage("pm.maintainRisk.countyCode.warning");
        MessageManager.getInstance().addJsMessage("pm.oose.modified.record.exist.error");
        MessageManager.getInstance().addJsMessage("pm.addSlotOccupant.slotOccupied.error");
        MessageManager.getInstance().addJsMessage("pm.addSlotOccupant.ooswipCheck.error");
        MessageManager.getInstance().addJsMessage("pm.maintainReinstate.confirm.continue");
        MessageManager.getInstance().addJsMessage("pm.maintainRiskRelation.noRisk.error");
        MessageManager.getInstance().addJsMessage("pm.oose.modified.record.exist.error2");
        MessageManager.getInstance().addJsMessage("pm.maintainRiskCopy.saveRiskFirst.error");
        MessageManager.getInstance().addJsMessage("pm.maintainRiskCopy.deleteAll.deleteTarget.confirm");
        MessageManager.getInstance().addJsMessage("pm.deleteEndQuote.confirmed.warning");
        MessageManager.getInstance().addJsMessage("pm.applyEndQuote.confirmed.warning");
        MessageManager.getInstance().addJsMessage("pm.maintainQuote.copyPolicyToQuote.convert.confirmation");
        // auto renewal
        MessageManager.getInstance().addJsMessage("pm.autoRenewal.confirmation.info");
        // For Undo Term
        MessageManager.getInstance().addJsMessage("pm.undoTerm.confirm.continue");
        // For View Claim Summary
        MessageManager.getInstance().addJsMessage("pm.viewClaimsSummary.risk.select.error");
        // For IBNR risk
        MessageManager.getInstance().addJsMessage("pm.maintainRisk.isReinstateIbnrRiskValid.info");
        // For auto delete temp coverage confirmation after issue state was changed
        MessageManager.getInstance().addJsMessage("pm.maintainRisk.auto.delete.coverage.confirmation");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getRiskManager() == null)
            throw new ConfigurationException("The required property 'riskManager' is missing.");
    }

    public ViewRiskSummaryAction() {
    }
}
