package dti.pm.riskmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.DisplayIndicator;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Field;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.struts.IOasisAction;
import dti.oasis.tags.OasisFormField;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.WebLayer;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;

/**
 * Action class for Maintain Risk.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 22, 2006
 *
 * @author mlmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/09/2007       sxm         Added logic to set the currentpolicyTermHistoryId, riskId, coverageId
 *                              and coverageClassId in user session
 * 11/20/2007       fcb         addJsMessages: added pm.oose.modified.record.exist.error2
 * 09/17/2009       fcb         98370 - logic to default the dddw moved to RiskManagerImpl
 * 03/01/2010       fcb         104191 - added pm.addRisk.unsaved.changes.error.
 * 04/26/2010       bhong       106922 - Add RowStyleLoadProcessor for addRiskBatch when load existing risks
 *                              to populate row style values
 * 05/04/2010       bhong       106924 - Added RowStyleLoadProcessor for saveAllRisk when reloading risks from database
 * 06/11/2010       dzhang      101253 - Added setAlwaysEnabledFieldIds("procedureCodes") in loadAllRisk() to make field
 *                              Procedure Codes always enable.
 * 06/28/2010       dzhang      101253 - Move setAlwaysEnabledFieldIds("procedureCodes") position.
 * 08/04/2010       syang       103793 - Add js message for Maintain Surcharge Points page in risk level.
 * 09/16/2010       dzhang      103813 - Add js message for Undo Term.
 * 01/31/2011       wfu         116334 - Added js message for view claims summary.
 * 04/29/2011       syang       120297 - Modified addRiskBatch() to set current select risk to the first new added risk.
 * 03/09/2012       jshen       131499 - Set href value for Name column when get initial values for newly added risk.
 * 05/04/2012       xnie        132993 - Set href value for Name column when get initial values for oose risk which is
 *                                       similar with adding risk.  
 * 07/06/2012       tcheng      133964 - Added js message for view insured information.
 * 07/24/2012       awu         129250 - Added autoSaveAllRisk().
 * 10/18/2013       fcb         145725 - call to loadAllRisk modified to not process entitlements when saving risk info.
 * 12/02/2013       jyang       149171 - Roll back 141758's change to load LOV label fields' value in
 *                                       getInitialValuesForXXX method.
 * 12/19/2013       jyang       148585 - Add logic to make sure the coverageId and riskId are in right group which are
 *                                       used to get isCoverageAvailable value.
 * 12/30/2013       adeng       150041 - Modified saveAllRisk() to load risk again with page entitlements when a
 *                                       ValidationException is raised during the process of saveAllRisk.
 * 02/14/2014       jyang       150391 - Modified getRiskAddlInfo() to check currency data type for risk addlInfo record.
 * 03/20/2014       adeng       149313 - Modified saveAllRisk() to add logic to keep the records have same fields.
 * 03/28/2014       awu         153399 - Modified getRiskAddlInfo to set coverage header to null if it is loaded failed.
 * 04/22/2014       adeng       154037 - Modified addRiskBatch() to set the correct selected riskId into session.
 * 07/25/2014       awu         152034 - 1). Added updatePolicyHeader to set coverageHeader and coverageClassId.
 *                                       2). Modified loadAllRisk to call updatePolicyHeader, and remove the logic of user session.
 *                                       3). Roll back the changes of issue148585 in getRiskAddlInfo.
 * 07/29/2014       xnie        155378 - Modified saveAllRisk() to call resetDisplayIndicatorForDeletedRs when save error.
 * 08/25/2014       awu         152034 - Modified updatePolicyHeader to highlight the active record if the current record is closed.
 * 09/15/2014       wdang       157555 - Modified updatePolicyHeader to check if riskId is a numeric before use it.
 * 09/18/2014       awu         157729 - Modified saveAllRisk to use ArrayList instead of HashSet.
 * 10/06/2014       awu         157694 - Rolled back the changes from 157729, both issues were fixed in Framework level.
 * 11/06/2014       awu         157552 - Modified updatePolicyHeader to empty coverage header if coverage id is not available at this term.
 * 12/16/2014       awu         159187 - Modified autoSaveAllRisk to catch ValidationException.
 * 05/07/2016       wdang       157211 - 1) Modified getRiskAddlInfo to put isInsuredTrackingAvailable.
 *                                       2) Added another isEffectiveToDateEditable method.
 * 10/13/2015       tzeng       164679 - 1) Modified loadAllRisk, added PROCESS_SAVE_WIP_B and
 *                                          RISK_RELATION_MESSAGE_TYPE to display auto risk relation result message
 *                                          after rating or save official or save WIP at the first time.
 *                                       2) Modified autoSaveAllRisk to return auto risk relation result to display
 *                                          message.
 * 01/21/2016       wdang       166924 - Set isAlternativeRatingMethodEditable indicator.
 * 01/28/2016       wdang       169024 - 1) Reverted changes of 164679.
 *                                       2) Modified loadAllRisk to invoke addSaveMessages().
 * 07/22/2016       bwang       178033 - Modified updatePolicyHeader(),changed integer type variables to long type
 *                                       which are from PK/FK fields in DB.
 * 08/15/2016       eyin        177410 - Modified addJsMessages(), added validateTempCovgExist() and
 *                                       performAutoDeleteTempCovgs(), to delete temp coverages automatically after
 *                                       issue state was changed.
 * 06/06/2017       xnie        185709 - Added getRiskSumB() to get what tab system needs to show user. Risk Information
 *                                       tab page or Risk Summary Information tab page.
 * 07/17/2017       wrong       168374 - Added new methods loadIsFundStateValue(), getDefaultValueForPcfCounty()
 *                                       and getDefaultValueForPcfRiskClass() to process ajax request.
 * 07/26/2017       lzhang      182246 - Delete Js message
 * 06/13/2018       wrong       192557 - Modified performAutoDeleteTempCovgs to call hasValidSaveToken() to
 *                                       be used for CSRFInterceptor.
 * 09/14/2018       cesar       195306 - added saveToken(request) to loadAllRisk() to implement CSRF requirement.
 * ---------------------------------------------------
 */
public class MaintainRiskAction extends PMBaseAction {

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
        request.setAttribute(RequestIds.PROCESS, "loadAllRisk");
        return loadAllRisk(mapping, form, request, response);
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
    public ActionForward loadAllRisk(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllRisk", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            //First,load policy header without risk header
            PolicyHeader policyHeader = getPolicyHeader(request);

            // Load the Risks
            RecordSet rs = (RecordSet) request.getAttribute(dti.oasis.http.RequestIds.GRID_RECORD_SET);
            if (rs == null) {
                RecordLoadProcessor rowStyleLp = new RiskRowStyleRecordLoadprocessor();
                rs = getRiskManager().loadAllRisk(policyHeader, rowStyleLp);
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

            saveToken(request);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the risk page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllRisk", af);
        return af;
    }

    /**
     * Save all updated risk records.
     */
    public ActionForward saveAllRisk(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
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
                //load risk before modified
                RecordLoadProcessor rowStyleLp = new RiskRowStyleRecordLoadprocessor();
                rs = getRiskManager().loadAllRisk(policyHeader, rowStyleLp, false);
                //merge modified risk records and old risk records
                rs.merge(inputRecords, "riskId");
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "saveAllRisk", "Saving the risk inputRecords: " + inputRecords);
                }
                try {
                    // Save the changes
                    getRiskManager().processSaveAllRisk(getPolicyHeader(request), rs);
                }
                catch (ValidationException ve) {
                    // We need to reload the risk information with entitlements as when the validation
                    // error occurs the system will not reload the page as in the regular case.
                    rs = getRiskManager().loadAllRisk(policyHeader, rowStyleLp);
                    //merge modified risk records and old risk records
                    rs.merge(inputRecords, "riskId");

                    // Reset display indicator to 'Y' for official record which is closed by deleted temp record.
                    resetDisplayIndicatorForDeletedRs(rs, "riskId");

                    // Create a set, add all field names to the set.
                    Set allFieldNames = new HashSet();
                    Iterator iter = rs.getRecords();
                    while (iter.hasNext()) {
                        Record record = (Record) iter.next();
                        allFieldNames.addAll(record.getFieldNameList());
                    }
                    // Keep the records have same fields.
                    Iterator recordIter = rs.getRecords();
                    while (recordIter.hasNext()) {
                        Record record = (Record) recordIter.next();
                        Iterator fieldNamesIter = allFieldNames.iterator();
                        while (fieldNamesIter.hasNext()) {
                            String fieldName = (String) fieldNamesIter.next();
                            if (!record.hasField(fieldName)) {
                                record.setField(fieldName, new Field());
                            }
                        }
                    }

                    // Save the input records into request
                    request.setAttribute(RequestIds.GRID_RECORD_SET, rs);

                    // Handle the validation exception
                    handleValidationException(ve, request);
                }
            }
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to save the risk page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "save", af);
        return af;
    }

    /**
     * Save all updated risk records.
     */
    public ActionForward autoSaveAllRisk(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "autoSaveAllRisk", new Object[]{mapping, form, request, response});

        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);

                PolicyHeader policyHeader = getPolicyHeader(request);
                // Map textXML to RecordSet for input
                RecordSet inputRecords = getInputRecordSet(request);
                //load risk before modified
                RecordLoadProcessor rowStyleLp = new RiskRowStyleRecordLoadprocessor();
                RecordSet rs = getRiskManager().loadAllRisk(policyHeader, rowStyleLp);
                //merge modified risk records and old risk records
                rs.merge(inputRecords, "riskId");
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "saveAllRisk", "Saving the risk inputRecords: " + inputRecords);
                }
                // Save the changes
                getRiskManager().processAutoSaveAllRisk(getPolicyHeader(request), rs);

                writeEmptyAjaxXMLResponse(response);
            }
        }
        catch (ValidationException ve) {
            writeEmptyAjaxXMLResponse(response);
        }
        catch (Exception e) {
            handleErrorForAutoSaveAjax(AppException.UNEXPECTED_ERROR, "Failed to auto save the risk page.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "autoSaveAllRisk", af);
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

            OasisFields fields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);
            OasisFormField riskNameField = fields.getField(RiskFields.RISK_NAME_GH);
            RiskFields.setRiskNameHref(record, riskNameField.getHref());

            // get LOV labels for initial values
            publishOutputRecord(request, record);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, record);

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

            // get initial values
            Record record = getRiskManager().getInitialValuesForOoseRisk(policyHeader, inputRecord);

            OasisFields fields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);
            OasisFormField riskNameField = fields.getField(RiskFields.RISK_NAME_GH);
            RiskFields.setRiskNameHref(record, riskNameField.getHref());

            // get LOV labels for initial values
            publishOutputRecord(request, record);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, record);

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

            // get initial values
            Record record = getRiskManager().getInitialValuesForSlotOccupant(policyHeader, inputRecord);

            // get LOV labels for initial values
            publishOutputRecord(request, record);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, record);

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
     * Get entity owner id for location.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getEntityOwnerId(ActionMapping mapping,
                                          ActionForm form,
                                          HttpServletRequest request,
                                          HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getEntityOwnerId",
            new Object[]{mapping, form, request, response});
        try {
            // Secure access to the page.
            securePage(request, form, false);
            // Get input record.
            Record inputRecord = getInputRecord(request);
            // Get entity owner id.
            Record record = getRiskManager().getEntityOwnerId(inputRecord);
            writeAjaxXmlResponse(response, record);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get entity owner id for location.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getEntityOwnerId", af);
        return af;
    }

    /**
     * Validate If Any Temp Coverage exists under the Risk.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward validateTempCovgExist(ActionMapping mapping,
                                               ActionForm form,
                                               HttpServletRequest request,
                                               HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "validateTempCovgExist",
            new Object[]{mapping, form, request, response});
        try {
            // Secure access to the page.
            securePage(request, form, false);
            // Get input record.
            Record inputRecord = getInputRecord(request);

            Record record = getRiskManager().validateTempCovgExist(inputRecord);

            writeAjaxXmlResponse(response, record);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to validate", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "validateTempCovgExist", af);
        return af;
    }

    /**
     * Delete temp coverages automatically when issue state is changed.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performAutoDeleteTempCovgs(ActionMapping mapping,
                                                    ActionForm form,
                                                    HttpServletRequest request,
                                                    HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "performAutoDeleteTempCovgs",
            new Object[]{mapping, form, request, response});
        try {
            if (hasValidSaveToken(request)) {
                // Secure access to the page
                securePage(request, form, false);

                // Get input record.
                Record inputRecord = getInputRecord(request);

                getRiskManager().performAutoDeleteTempCovgs(inputRecord);

                writeEmptyAjaxXMLResponse(response);
            }
        }
        catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to delete temp coverage.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "performAutoDeleteTempCovgs", af);
        return af;
    }

    /**
     * An ajax call to get risk addl information based on each risk selected
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
            securePage(request, form);

            PolicyHeader policyHeader = getPolicyHeader(request);
            Record inputRecord = getInputRecord(request);
            
            inputRecord.setFields(policyHeader.toRecord(), false);

            // get data change allow flag
            PolicyHeaderFields.setPolicyTypeCode(inputRecord, policyHeader.getPolicyTypeCode());
            YesNoFlag isDateChangeAllowed = YesNoFlag.getInstance(getRiskManager().isDateChangeAllowed(inputRecord));  

            // get effective to date change rule
            YesNoFlag isEffectiveToDateEditable = getRiskManager().isEffectiveToDateEditable(policyHeader, inputRecord, isDateChangeAllowed.booleanValue());

            // get determination of the coverage class option for this risk
            boolean coverageClassAvailable = getPolicyManager().isCoverageClassAvailable(policyHeader, inputRecord, false);

            // get additional info fields
            Record addlInfoRecord = getRiskManager().loadRiskAddlInfo(inputRecord);
            OasisFields fields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);
            Iterator iter = addlInfoRecord.getFieldNames();
            while (iter.hasNext()) {
                String fldName = iter.next().toString();
                if (fields.hasField(fldName)) {
                    String type = fields.getField(fldName).getDatatype();
                    if (type.equals(OasisFields.TYPE_CURRENCY) || type.equals(OasisFields.TYPE_CURRENCY_FORMATTED)) {
                        addlInfoRecord.setFieldValue(fldName, FormatUtils.formatCurrency(addlInfoRecord.getStringValue(fldName)));
                    }
                }
            }

            // pull all record information together
            addlInfoRecord.setFieldValue("isInsuredTrackingAvailable", isDateChangeAllowed.booleanValue() ? YesNoFlag.N : YesNoFlag.Y);
            addlInfoRecord.setFieldValue("isRiskEffectiveToDateEditable", isEffectiveToDateEditable);
            addlInfoRecord.setFieldValue("isCoverageClassAvailable", YesNoFlag.getInstance(coverageClassAvailable));
            addlInfoRecord.setFieldValue("isAlternativeRatingMethodEditable", getRiskManager().isAlternativeRatingMethodEditable(policyHeader, inputRecord));

            // prepare return values
            writeAjaxXmlResponse(response, addlInfoRecord, true);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR,
                "Failed to check risk effective to date is editable or not.", e, response);
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
            RecordSet rs = getRiskManager().loadAllRisk(policyHeader, rowStyleLp);

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
     * Validate reinstate ibnr risk
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward valReinstateIbnrRisk(ActionMapping mapping,
                                              ActionForm form,
                                              HttpServletRequest request,
                                              HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "valReinstateIbnrRisk",
            new Object[]{mapping, form, request, response});
        
        try {
            // Secure access to the page.
            securePage(request, form, false);
            // Get input record.
            Record inputRecord = getInputRecord(request);
            // Get entity owner id.
            Record record = getRiskManager().valReinstateIbnrRisk(inputRecord);

            writeAjaxXmlResponse(response, record, true);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get entity owner id for location.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "valReinstateIbnrRisk", af);
        return af;
    }

    /**
     * Return isFundState field value.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadIsFundStateValue(ActionMapping mapping,
                                               ActionForm form,
                                               HttpServletRequest request,
                                               HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadIsFundStateValue",
            new Object[]{mapping, form, request, response});
        try {
            // Secure access to the page.
            securePage(request, form, false);
            // Get input record.
            Record inputRecord = getInputRecord(request);

            Record record = getRiskManager().loadIsFundStateValue(inputRecord);

            writeAjaxXmlResponse(response, record);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to load isFundSate field value", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "loadIsFundStateValue", af);
        return af;
    }

    /**
     * Return default pcf risk county field value.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getDefaultValueForPcfCounty(ActionMapping mapping,
                                              ActionForm form,
                                              HttpServletRequest request,
                                              HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getDefaultValueForPcfCounty",
            new Object[]{mapping, form, request, response});
        try {
            // Secure access to the page.
            securePage(request, form, false);
            // Get input record.
            Record inputRecord = getInputRecord(request);

            Record record = getRiskManager().getDefaultValueForPcfCounty(inputRecord);

            writeAjaxXmlResponse(response, record);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get default pcf county value", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getDefaultValueForPcfCounty", af);
        return af;
    }

    /**
     * Return default pcf risk class field value.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getDefaultValueForPcfRiskClass(ActionMapping mapping,
                                                     ActionForm form,
                                                     HttpServletRequest request,
                                                     HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getDefaultValueForPcfRiskClass",
            new Object[]{mapping, form, request, response});
        try {
            // Secure access to the page.
            securePage(request, form, false);
            // Get input record.
            Record inputRecord = getInputRecord(request);

            Record record = getRiskManager().getDefaultValueForPcfRiskClass(inputRecord);

            writeAjaxXmlResponse(response, record);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR,
                               "Failed to get default pcf risk class value", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getDefaultValueForPcfRiskClass", af);
        return af;
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
        // For Coi Holder, Affiliation, Schedule and Insured Tracking
        MessageManager.getInstance().addJsMessage("pm.maintainRisk.unsaved.changes.insuredTracking.error");
        MessageManager.getInstance().addJsMessage("pm.addRisk.unsaved.changes.error");
        // For Undo Term
        MessageManager.getInstance().addJsMessage("pm.undoTerm.confirm.continue");
        // For View Claim Summary
        MessageManager.getInstance().addJsMessage("pm.viewClaimsSummary.risk.select.error");
        // For IBNR risk
        MessageManager.getInstance().addJsMessage("pm.maintainRisk.isReinstateIbnrRiskValid.info");
        // For auto delete temp coverage confirmation after issue state was changed
        MessageManager.getInstance().addJsMessage("pm.maintainRisk.auto.delete.coverage.confirmation");
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

            try {
                //Load the coverage header if coverageId is existing in the request.
                String coverageId = request.getParameter(RequestIds.COVERAGE_ID);
                if (coverageId != null && StringUtils.isNumeric(coverageId) && Long.parseLong(coverageId) > 0) {
                    policyHeader = getCoverageManager().loadCoverageHeader(policyHeader, coverageId);
                }
            }
            catch (AppException ae) {
                policyHeader.setCoverageHeader(null);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "updatePolicyHeader", policyHeader);
        }
    }

    /**
     * Get what tab system needs to show to user.Risk Information tab page or Risk Summary Information tab page.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getRiskSumB(ActionMapping mapping,
                                     ActionForm form,
                                     HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getRiskSumB", new Object[]{mapping, form, request, response});
        try {
            // Secure access to the page without loading the Oasis Fields
            securePage(request, form, false);

            PolicyHeader policyHeader = getPolicyHeader(request);
            // get risk tab page.
            String riskSumB;
            boolean isRiskSumAvailableB = policyHeader.getRiskSumAvailableB();
            if (isRiskSumAvailableB) {
                riskSumB = "Y";
            }
            else {
                riskSumB = "N";
            }
            Record record = new Record();
            RiskFields.setRiskSumB(record, riskSumB);
            writeAjaxXmlResponse(response, record, true);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get risk tab page.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getRiskSumB", af);
        return af;
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

    public MaintainRiskAction() {
    }

}
