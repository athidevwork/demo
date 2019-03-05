package dti.pm.riskmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.struts.IOasisAction;
import dti.oasis.tags.OasisFields;
import dti.oasis.util.StringUtils;
import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.riskmgr.RiskFields;
import dti.pm.riskmgr.impl.RiskRowStyleRecordLoadprocessor;
import dti.pm.transactionmgr.TransactionFields;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for Maintain Risk Detail.
 * <p/>
 * <p>(C) 2013 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 17, 2014
 *
 * @author xnie
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/17/2014       xnie        148083 - Initial version.
 * 03/18/2014       xnie        152969 - Modified loadAllRiskSummaryOrDetail() to set newRiskB attribute to request.
 * 07/23/2014       xnie        156208 - Added getRiskAddlInfo().
 * 10/13/2015       tzeng       164679 - Modified loadAllRiskSummaryOrDetail() and added RISK_RELATION_MESSAGE_TYPE to
 *                                       display risk relation message after click save button.
 * 01/21/2016       tzeng       166924 - Modified getRiskAddlInfo() to set isAlternativeRatingMethodEditable indicator.
 * 01/28/2016       wdang       169024 - 1) Reverted changes of 164679.
 *                                       2) Modified loadAllRiskSummaryOrDetail to invoke addSaveMessages().
 * ---------------------------------------------------
 */
public class MaintainRiskDetailAction extends PMBaseAction {

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
        request.setAttribute(RequestIds.PROCESS, "loadAllRiskSummaryOrDetail");
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

            PolicyHeader policyHeader = getPolicyHeader(request);

            // get input
            Record inputRecord = getInputRecord(request);

            if (!inputRecord.getBooleanValue(RiskFields.SAVE_CLOSE_B, false).booleanValue() &&
                inputRecord.getBooleanValue(RiskFields.SAVED_B, false).booleanValue()) {
                // Add messages for save purpose
                addSaveMessages(policyHeader, request);
            }

            // Load the Risks
            RecordLoadProcessor rowStyleLp = new RiskRowStyleRecordLoadprocessor();
            Record riskRecord;
            if (UserSessionManager.getInstance().getUserSession().has(RiskFields.CACHED_RISK_RECORD)
                && !inputRecord.hasField(RiskFields.RISK_DETAIL_ID)) {
                riskRecord = (Record) UserSessionManager.getInstance().getUserSession().get(RiskFields.CACHED_RISK_RECORD);
                RiskFields.setRiskId(riskRecord, RiskFields.getRiskDetailId(riskRecord));
                request.setAttribute(UpdateIndicator.FIELD_NAME, UpdateIndicator.INSERTED);
                request.setAttribute(RiskFields.EDIT_IND, "Y");
            }
            else {
                TransactionFields.setTransactionLogId(inputRecord, policyHeader.getLastTransactionId());
                PolicyFields.setTermEffectiveFromDate(inputRecord, policyHeader.getTermEffectiveFromDate());
                PolicyFields.setTermEffectiveToDate(inputRecord, policyHeader.getTermEffectiveToDate());
                // Get updated risk detail id based on gaven risk detail id/transaction/term eff/exp date.
                String riskDetailId = getRiskManager().getRiskDetailId((inputRecord));
                riskRecord = getRiskManager().loadAllRiskSummaryOrDetail(policyHeader, rowStyleLp, riskDetailId).getFirstRecord();
                request.setAttribute(UpdateIndicator.FIELD_NAME, UpdateIndicator.NOT_CHANGED);
                request.setAttribute(RiskFields.EDIT_IND, riskRecord.getEditIndicator());
            }

            request.setAttribute(RiskFields.ORIG_RISK_EFFECTIVE_TO_DATE, RiskFields.getOrigRiskEffectiveToDate(riskRecord));
            if (riskRecord.hasField(RiskFields.ORIG_ROLLING_IBNR_B)) {
                if (StringUtils.isBlank(RiskFields.getOrigRollingIbnrB(riskRecord))) {
                    request.setAttribute(RiskFields.ORIG_ROLLING_IBNR_B, "");
                }
                else {
                    request.setAttribute(RiskFields.ORIG_ROLLING_IBNR_B, RiskFields.getOrigRollingIbnrB(riskRecord));
                }
            }
            if (!inputRecord.hasField(RiskFields.SAVE_CLOSE_B)) {
                request.setAttribute(RiskFields.SAVE_CLOSE_B, "");
            }
            else {
                request.setAttribute(RiskFields.SAVE_CLOSE_B, RiskFields.getSaveCloseB(inputRecord));
            }
            if (!inputRecord.hasField(RiskFields.SAVED_B)) {
                request.setAttribute(RiskFields.SAVED_B, "");
            }
            else {
                request.setAttribute(RiskFields.SAVED_B, RiskFields.getSavedB(inputRecord));
            }
            if (!inputRecord.hasField(RiskFields.NEW_RISK_B)) {
                request.setAttribute(RiskFields.NEW_RISK_B, "");
            }
            else {
                request.setAttribute(RiskFields.NEW_RISK_B, RiskFields.getNewRiskB(inputRecord));
            }
            if (!inputRecord.hasField(RiskFields.SLOT_OCCUPANT_B)) {
                request.setAttribute(RiskFields.SLOT_OCCUPANT_B, "");
            }
            else {
                request.setAttribute(RiskFields.SLOT_OCCUPANT_B, RiskFields.getSlotOccupantB(inputRecord));
            }

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, riskRecord);

            // Load the list of values after loading the data
            loadListOfValues(request, form);

            //Set fields 'Procedure Codes' in risk tab to always enabled.
            setAlwaysEnabledFieldIds("procedureCodes");

            // Set current policyTermHistoryId, riskId and coverageId in the usersession
            getPolicyManager().setCurrentIdsInSession(policyHeader.getTermBaseRecordId(),
                request.getParameter(RequestIds.RISK_ID), request.getParameter(RequestIds.COVERAGE_ID),
                request.getParameter(RequestIds.COVERAGE_CLASS_ID), UserSessionManager.getInstance().getUserSession());
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the risk detail page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllRiskSummaryOrDetail", af);
        return af;
    }

    /**
     * Save all updated risk records.
     */
    public ActionForward saveRiskDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "save", new Object[]{mapping, form, request, response});
        String forwardString = "saveResult";
        Record inputRecord = null;
        RecordSet rs = null;
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page with loading the Oasis Fields
                securePage(request, form, false);

                // get values from the form
                inputRecord = getInputRecord(request);
                inputRecord.remove("org.apache.struts.taglib.html.TOKEN");
                RiskFields.setRiskId(inputRecord, RiskFields.getRiskDetailId(inputRecord));
                inputRecord.setUpdateIndicator(RiskFields.getUpdateInd(inputRecord));

                RecordSet inputRecords = new RecordSet();
                inputRecords.addRecord(inputRecord);
                inputRecords.getSummaryRecord().setFieldValue("newSaveOption", inputRecord.getStringValue("newSaveOption"));
                RiskFields.setRiskDetailB(inputRecords.getSummaryRecord(), YesNoFlag.getInstance("Y"));

                PolicyHeader policyHeader = getPolicyHeader(request);
                RecordLoadProcessor rowStyleLp = new RiskRowStyleRecordLoadprocessor();
                rs = getRiskManager().loadAllRisk(policyHeader, rowStyleLp, false);
                //merge modified risk records and old risk records
                rs.merge(inputRecords, "riskId");
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "saveRiskDetail", "Saving the risk detail: " + inputRecord);
                }
                // Save the changes
                getRiskManager().processSaveAllRisk(getPolicyHeader(request), rs);

                UserSessionManager.getInstance().getUserSession().remove(RiskFields.CACHED_RISK_RECORD);
            }
        }
        catch (ValidationException ve) {
            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to save the risk detail page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "save", af);
        return af;
    }

    /**
     * An ajax call to get risk addl information based on passed in risk
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

            PolicyHeader policyHeader = getPolicyHeader(request, true);
            Record inputRecord = getInputRecord(request);
            inputRecord.setFields(policyHeader.toRecord(), false);

            // get effective to date change rule
            YesNoFlag isEffectiveToDateEditable = getRiskManager().isEffectiveToDateEditable(policyHeader, inputRecord);

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
            addlInfoRecord.setFieldValue("isRiskEffectiveToDateEditable", isEffectiveToDateEditable);
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

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getRiskManager() == null)
            throw new ConfigurationException("The required property 'riskManager' is missing.");
    }

    public MaintainRiskDetailAction() {
    }
}
