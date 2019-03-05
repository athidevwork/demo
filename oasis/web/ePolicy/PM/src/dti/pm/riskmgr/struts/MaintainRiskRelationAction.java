package dti.pm.riskmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.IOasisAction;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.oasis.util.LogUtils;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.coveragemgr.CoverageFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.riskmgr.RiskFields;
import dti.pm.riskmgr.RiskRelationFields;
import dti.pm.riskmgr.RiskRelationManager;
import dti.pm.riskmgr.impl.RiskRelationRowStyleRecordLoadprocessor;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Action class for Maintain Risk Relation.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 6, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/08/2008       Joe         Modified loadAllRiskRelation() to support load reverse relation data.
 * 05/22/2008       fcb         80759: logic for multiRiskRelation added.
 * 07/07/2010       dzhang      103806: logic for isCompanyInsuredStr in loadAllRiskRelation(),
 *                              and method getInitialValuesForAddNonBaseCompanyInsured() added.
 * 12/02/2013       jyang       149171 - roll back 141758's change to load LOV label fields' value in
 *                              getInitialValuesForAddRiskRelation method.
 * 05/06/2014       fcb         151632 - Added logic for refreshParentB.
 * 11/25/2014       kxiang      158853 - Set href value to nameHref in method getInitialValuesForAddRiskRelation.
 * 09/07/2015       tzeng       164679 - Modified loadAllRiskRelation() to set new records in blue.
 * 09/18/2015       lzhang      165941 - Modify loadAllRiskRelation: add screenModeCode to request.
 * 12/10/2018       cesar       197486 - Added saveToken() at the end of loadAllRiskRelation() to comply CSRF implementation.
 * ---------------------------------------------------
 */
public class MaintainRiskRelationAction extends PMBaseAction {
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
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute(RequestIds.PROCESS, "loadAllRiskRelation");
        return loadAllRiskRelation(mapping, form, request, response);
    }

    /**
     * Method to load list of available risk relation.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllRiskRelation(ActionMapping mapping, ActionForm form,
                                             HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllRiskRelation", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {

            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            PolicyHeader policyHeader = getPolicyHeader(request, true);
            Record inputRecord = getInputRecord(request);
            //set default value N
            YesNoFlag reverse = YesNoFlag.N;
            if (inputRecord.hasStringValue(RiskRelationFields.REVERSE)) {
                reverse = YesNoFlag.getInstance(RiskRelationFields.getReverse(inputRecord));
            }

            // Load the Risks
            RecordSet rs = (RecordSet) request.getAttribute(dti.oasis.http.RequestIds.GRID_RECORD_SET);
            if (rs == null) {
                RecordLoadProcessor lp = new RiskRelationRowStyleRecordLoadprocessor();
                if (reverse.booleanValue()) {
                    rs = getRiskRelationManager().loadAllReverseRiskRelation(policyHeader, inputRecord, lp);
                }
                else {
                    rs = getRiskRelationManager().loadAllRiskRelation(policyHeader, inputRecord, lp);
                }
            }
            setDataBean(request, rs);

            // Make the Summary Record available for output
            Record output = rs.getSummaryRecord();

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);

            // Load the list of values after loading the data
            loadListOfValues(request, form);

            loadGridHeader(request);

            // Add Js messages
            addJsMessages();

            String isCompanyInsuredStr = getRiskRelationManager().getCompanyInsuredValue(policyHeader, inputRecord);
            request.setAttribute("isCompanyInsuredStr", isCompanyInsuredStr);
            request.setAttribute("origRiskEffectiveFromDate", RiskFields.getOrigRiskEffectiveFromDate(output));
            request.setAttribute("isReverse", String.valueOf(reverse.booleanValue()));
            request.setAttribute("multiRiskRelation", output.getFieldValue("multiRiskRelation"));
            request.setAttribute("refreshParentB", getRiskRelationManager().isRefreshRequired(policyHeader, inputRecord));

            ScreenModeCode screenModeCode = policyHeader.getScreenModeCode();
            if (output.hasFieldValue("screenModeCode")) {
                screenModeCode = ScreenModeCode.getInstance(output.getStringValue("screenModeCode"));
            } 
            request.setAttribute("screenModeCode", screenModeCode);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the risk relation page.",
                e, request, mapping);
        }

        saveToken(request);
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllRiskRelation", af);
        return af;
    }

    /**
     * Method to check if the Annual Premium (ratingBasis) can be editable or not for the first time when clicking record.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward isRatingBasisEditable(ActionMapping mapping, ActionForm form,
                                               HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "isRatingBasisEditable", new Object[]{mapping, form, request, response});

        try {
            // Secure page without load fields
            securePage(request, form, false);

            PolicyHeader policyHeader = getPolicyHeader(request, true);

            // Map request to record for input
            Record inputRecord = getInputRecord(request);

            // Get the additional info data
            Record output = getRiskRelationManager().isRatingBasisEditable(policyHeader, inputRecord);

            // Send back xml data
            writeAjaxXmlResponse(response, output);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to check Annual Premium.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "isRatingBasisEditable", af);
        return af;
    }

    /**
     * Method to check if the Annual Premium (ratingBasis) can be editable or not when user modify the NI Specialty.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward isAnnualPremiumEditable(ActionMapping mapping, ActionForm form,
                                                 HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "isAnnualPremiumEditable", new Object[]{mapping, form, request, response});

        try {
            // Secure page without load fields
            securePage(request, form, false);

            // Map request to record for input
            Record inputRecord = getInputRecord(request);

            // Get the additional info data
            YesNoFlag isEditable = getRiskRelationManager().isNiPremiumEditable(inputRecord);
            Record output = new Record();
            output.setFieldValue("isAnnualPremiumEditable", isEditable);
            if (!isEditable.booleanValue()) {
                RiskRelationFields.setRatingBasis(output, "");
            }

            // Send back xml data
            writeAjaxXmlResponse(response, output);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR,
                "Failed to check Annual Premium when modify specialty.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "isAnnualPremiumEditable", af);
        return af;
    }

    /**
     * Method to get product coverage code when user modify NI Practice State, NI Risk Type or NI Retro Date.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward getProductCoverageCode(ActionMapping mapping, ActionForm form,
                                                HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getProductCoverageCode", new Object[]{mapping, form, request, response});

        try {
            // Secure page without load fields
            securePage(request, form, false);

            // Map request to record for input
            Record inputRecord = getInputRecord(request);

            // Get the additional info data
            String prodCovgCode = getRiskRelationManager().getNICoverage(inputRecord);
            Record output = new Record();
            CoverageFields.setProductCoverageCode(output, prodCovgCode);

            // Send back xml data
            writeAjaxXmlResponse(response, output);
        }
        catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get product coverage code.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getProductCoverageCode", af);
        return af;
    }

    /**
     * Save all Risk Relation records.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward saveAllRiskRelation(ActionMapping mapping,
                                             ActionForm form,
                                             HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveAllRiskRelation", new Object[]{mapping, form, request, response});

        String forwardString = "saveResult";
        RecordSet inputRecords = null;

        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);

                // Get policy header
                PolicyHeader policyHeader = getPolicyHeader(request, true);

                // Map textXML to RecordSet for input
                inputRecords = getInputRecordSet(request);

                Record inputRecord = getInputRecord(request);
                inputRecords.setFieldValueOnAll("origRiskEffectiveFromDate",
                    inputRecord.getStringValue("origRiskEffectiveFromDate"));
                inputRecords.setFieldValueOnAll("currentRiskType", inputRecord.getStringValue("currentRiskTypeCode"));

                // Save the changes
                getRiskRelationManager().saveAllRiskRelation(policyHeader, inputRecords);
            }
        }
        catch (ValidationException ve) {
            // Save the input records into request
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);

            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR,
                "Failed to save Risk Relation.", e, request, mapping);
        }

        // Return the forward
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveAllRiskRelation", af);
        return af;
    }

    /**
     * Get Inital Values for newly added Policy/Company/Non Insured Risk Relation
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward getInitialValuesForAddRiskRelation(ActionMapping mapping,
                                                            ActionForm form,
                                                            HttpServletRequest request,
                                                            HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForAddRiskRelation",
            new Object[]{mapping, form, request, response});

        try {
			//Secure page and load all Oasis fields value include lov label fields -149171.
            securePage(request, form);
            PolicyHeader policyHeader = getPolicyHeader(request, true);

            // Get inital values
            Record record = getRiskRelationManager().
                getInitialValuesForAddRiskRelation(policyHeader, getInputRecord(request));

            OasisFields fields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);
            OasisFormField riskNameField = fields.getField(RiskRelationFields.RISK_NAME_GH);
            RiskRelationFields.setRiskNameHref(record, riskNameField.getHref());

            // Get LOV labels for initial values
            publishOutputRecord(request, record);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, record);

            // Send back xml data
            writeAjaxXmlResponse(response, record);

        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR,
                "Failed to get initial values for policy/company/non insured risk relation.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForAddRiskRelation", af);
        return af;
    }

    /**
     * Get Inital Values for newly added Non-base Company Insured Risk Relation
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return ActionForward actonForward
     * @throws Exception if there are some errors
     */
    public ActionForward getInitialValuesForAddNonBaseCompanyInsured(ActionMapping mapping,
                                                                     ActionForm form,
                                                                     HttpServletRequest request,
                                                                     HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForAddNonBaseCompanyInsured",
            new Object[]{mapping, form, request, response});

        try {
            securePage(request, form);
            PolicyHeader policyHeader = getPolicyHeader(request, true);

            // Get inital values
            Record record = getRiskRelationManager().
                getInitialValuesForAddNonBaseCompanyInsured(policyHeader, getInputRecord(request));

            // Get LOV labels for initial values
            publishOutputRecord(request, record);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, record);

            // Send back xml data
            writeAjaxXmlResponse(response, record);

        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR,
                "Failed to get initial values for non-base company insured risk relation.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForAddNonBaseCompanyInsured", af);
        return af;
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.maintainRiskRelation.delete.changedRecord.warning");
        MessageManager.getInstance().addJsMessage("pm.reverseRiskRelation.title");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getRiskRelationManager() == null)
            throw new ConfigurationException("The required property 'riskRelationManager' is missing.");
    }

    public RiskRelationManager getRiskRelationManager() {
        return m_riskRelationManager;
    }

    public void setRiskRelationManager(RiskRelationManager riskRelationManager) {
        this.m_riskRelationManager = riskRelationManager;
    }

    private RiskRelationManager m_riskRelationManager;
}
