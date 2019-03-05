package dti.pm.coverageclassmgr.struts;

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
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.util.BeanDtiUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.coverageclassmgr.CoverageClassFields;
import dti.pm.coverageclassmgr.CoverageClassManager;
import dti.pm.coverageclassmgr.impl.CoverageClassEntitlementRecordLoadProcessor;
import dti.pm.coverageclassmgr.impl.CoverageClassRowStyleRecordLoadprocessor;
import dti.pm.coveragemgr.CoverageFields;
import dti.pm.pmnavigationmgr.PMNavigationManager;
import dti.pm.policymgr.PolicyHeader;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.LabelValueBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for Maintain Coverage Class.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 8, 2007
 *
 * @author jmpotosky
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/09/2007       sxm         Added logic to set the currentpolicyTermHistoryId, riskId, coverageId
 *                              and coverageClassId in user session
 * 08/21/2007       sxm         Added loadNavigateSource() for policy navigation
 * 11/20/2007       fcb         addJsMessages: added pm.oose.modified.record.exist.error2 
 * 09/29/2008       sxm         Issue 86880 - append risk type to risk name, append "Selected" to current risk name,
 *                                            and set current value of selected record
 * 09/16/2010       dzhang      103813 - Added js message for Undo Term.
 * 01/31/2011       wfu         116334 - Added js message for view claims summary.
 * 07/24/2012       awu         129250 - Added autoSaveAllCoverageClass().
 * 04/26/2013       awu         141758 - Added addAllCoverageClass().
 * 12/02/2013       jyang       149171 - Roll back 141758's change to load LOV label fields' value in
 *                                       getInitialValuesForXXX method.
 * 01/01/2014       Parker      148029 - Cache risk header, coverage header and policy navigation information to policy header.
 * 07/25/2014       awu         152034 - 1). Added updatePolicyHeader to load riskHeader/coverageHeader, and set coverageClassId.
 *                                       2). Modified loadAllCoverageClass to call updatePolicyHeader.
 * 07/29/2014       xnie        155378 - Modified saveAllCoverageClass() to call resetDisplayIndicatorForDeletedRs when save error.
 * 08/25/2014       awu         152034 - 1) Modified loadAllCoverageClass to call updatePolicyHeader two times;
 *                                       2) Modified updatePolicyHeader to load the first non-primary risk if no primary risk.
 *                                       3) Modified updatePolicyHeader to highlight the active record if the current record is closed.
 * 09/15/2014       wdang       157555 - Modified updatePolicyHeader to check if coverageClassId is a numeric before use it.
 * 12/16/2014       awu         159187 - Modified autoSaveAllCoverageClass to catch ValidationException.
 * 10/15/2015       tzeng       164679 - Modified loadPolicyDetail and added RISK_RELATION_MESSAGE_TYPE to display auto
 *                                       risk relation result message after add new risk then navigate to coverage class
 *                                       tab.
 * 01/28/2016       wdang       169024 - 1) Reverted changes of 164679.
 *                                       2) Modified loadPolicyDetail to invoke addSaveMessages().
 * ---------------------------------------------------
 */

public class MaintainCoverageClassAction extends PMBaseAction {

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
        return loadAllCoverageClass(mapping, form, request, response);
    }

    /**
     * Method to load list of available coverage classes for requested policy/risk/coverage.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllCoverageClass(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllCoverageClass", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";

        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);
            Record inputRecord = getInputRecord(request);

            //First,load policy header without risk header
            PolicyHeader policyHeader = getPolicyHeader(request);
            //Load the GO TO navigation drop-down LOV.
            ArrayList lovOptions = new ArrayList();

            //Update risk header, coverage header, coverage class id to policy header.
            boolean hasRiskAndCoverage = updatePolicyHeader(request, policyHeader, lovOptions, null, PRE);

            // Load the Coverage Classes
            RecordSet rs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            //if has no risk or coverage,create an empty record
            if (!hasRiskAndCoverage) {
                rs = new RecordSet();
                CoverageClassEntitlementRecordLoadProcessor covgEntitleRLP =
                    new CoverageClassEntitlementRecordLoadProcessor(getCoverageClassManager(), policyHeader, policyHeader.getScreenModeCode(), YesNoFlag.N);
                covgEntitleRLP.postProcessRecordSet(rs);

                List fieldNames = new ArrayList();
                fieldNames.add("policyId");
                rs.addFieldNameCollection(fieldNames);
            } else {
                if (rs == null) {
                    CoverageClassRowStyleRecordLoadprocessor covgClassRowStyleLp = new CoverageClassRowStyleRecordLoadprocessor();
                    rs = getCoverageClassManager().loadAllCoverageClass(policyHeader, covgClassRowStyleLp);
                }
            }
            setDataBean(request, rs);
            // Make the Summary Record available for output
            Record output = rs.getSummaryRecord();
            // Set the navigation fields value
            Record record = getPmNavigationManager().getPolicyNavParameters(getInputRecord(request));
            output.setFields(record);

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);

            // Load the list of values after loading the data
            loadListOfValues(request, form);

            if(hasRiskAndCoverage){               
                request.setAttribute(CoverageFields.RISK_NAV_SOURCE_ID, BeanDtiUtils.createValueBean(CoverageFields.RISK_NAV_SOURCE_ID, request.getAttribute(NAVIGATION_CURRENT_VALUE)));
                request.setAttribute(CoverageFields.RISK_NAV_SOURCE_ID+"LOV", lovOptions);

                lovOptions = new ArrayList();
                inputRecord.setFieldValue(CoverageFields.POLICY_NAV_LEVEL_CODE,"COVERAGE");
                String currentValue = getPmNavigationManager().loadNavigateSourceForCoverageClass(policyHeader, getInputRecord(request), lovOptions);
                request.setAttribute(CoverageFields.COVERAGE_NAV_SOURCE_ID, BeanDtiUtils.createValueBean(CoverageFields.COVERAGE_NAV_SOURCE_ID, currentValue));
                request.setAttribute(CoverageFields.COVERAGE_NAV_SOURCE_ID+"LOV", lovOptions);
            }
            // Add Js messages
            addJsMessages();

            // Add messages for save purpose
            addSaveMessages(policyHeader, request);

            loadGridHeader(request);

            if (hasRiskAndCoverage) {
                updatePolicyHeader(request, policyHeader, null, rs, POST);
            }
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the coverage class page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllCoverageClass", af);
        return af;
    }

    /**
     * Save all updated coverage records
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward saveAllCoverageClass(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveAllCoverage", new Object[]{mapping, form, request, response});
        RecordSet inputRecords = null;
        String forwardString = "saveResult";
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);

                // Map textXML to RecordSet for input
                inputRecords = getInputRecordSet(request);

                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "saveAllCoverageClass", "Saving the coverage class inputRecords: " + inputRecords);
                }

                // Save the changes
                getCoverageClassManager().processSaveAllCoverageClass(getPolicyHeader(request, true, true), inputRecords);
            }

        }
        catch (ValidationException ve) {
            // Reset display indicator to 'Y' for official record which is closed by deleted temp record.
            resetDisplayIndicatorForDeletedRs(inputRecords, CoverageClassFields.COVERAGE_CLASS_ID);
            /* Save the input records into request */
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);

            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to save the coverage class page.", e, request, mapping);
        }

        // Forward
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveAllCoverageClass", af);
        return af;
    }

    /**
     * Save all updated coverage records
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward autoSaveAllCoverageClass(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "autoSaveAllCoverageClass", new Object[]{mapping, form, request, response});

        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);

                // Map textXML to RecordSet for input
                RecordSet inputRecords = getInputRecordSet(request);

                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "autoSaveAllCoverageClass", "Saving the coverage class inputRecords: " + inputRecords);
                }

                // Save the changes
                getCoverageClassManager().processAutoSaveAllCoverageClass(getPolicyHeader(request, true, true), inputRecords);

                writeEmptyAjaxXMLResponse(response);
            }

        }
        catch (ValidationException ve) {
            writeEmptyAjaxXMLResponse(response);
        }
        catch (Exception e) {
            handleErrorForAutoSaveAjax(AppException.UNEXPECTED_ERROR, "Failed to auto save the coverage class page.", e, response);
        }

        // Forward
        ActionForward af = null;
        l.exiting(getClass().getName(), "autoSaveAllCoverageClass", af);
        return af;
    }

    /**
     * Get initial values for Coverage class
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward getInitialValuesForCoverageClass(ActionMapping mapping,
                                                          ActionForm form,
                                                          HttpServletRequest request,
                                                          HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForCoverageClass",
            new Object[]{mapping, form, request, response});

        try {

            //Secure page and load all Oasis fields value include lov label fields -149171.
            securePage(request, form);

            // Get policy header
            PolicyHeader policyHeader = getPolicyHeader(request, true, true);

            Record record = getCoverageClassManager().getInitialValuesForCoverageClass(
                policyHeader, getInputRecord(request));

            // Get LOV labels for initial values
            publishOutputRecord(request, record);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, record);

            // Send back xml data
            writeAjaxXmlResponse(response, record);
        }

        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForCoverageClass", af);
        return af;
    }


    /**
     * Add all Coverage class
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward addAllCoverageClass(ActionMapping mapping,
                                             ActionForm form,
                                             HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "addAllCoverageClass",
            new Object[]{mapping, form, request, response});
        String forwardString = "saveResult";
        RecordSet inputRecords;
        try {
            // Secure page
            securePage(request, form, false);

            // Get policy header
            PolicyHeader policyHeader = getPolicyHeader(request, true, true);

            Record inputRecord = getInputRecord(request);

            inputRecords = getInputRecordSet(request, COVERAGE_CLASS_GRID_ID);

            getCoverageClassManager().addAllCoverageClass(policyHeader, inputRecord, inputRecords);

            RecordSet insertedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.INSERTED));
            
            String newCoverageClassId = CoverageClassFields.getCoverageClassId(insertedRecords.getRecord(0));

            setForwardParameter(request, RequestIds.COVERAGE_CLASS_ID, newCoverageClassId);
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);
        }

        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to add all coverage classes.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "addAllCoverageClass", af);
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
    public ActionForward validateForOoseCoverageClass(ActionMapping mapping,
                                                      ActionForm form,
                                                      HttpServletRequest request,
                                                      HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "validateForOoseCoverageClass",
            new Object[]{mapping, form, request, response});
        try {
            // Secure access to the page
            securePage(request, form, false);

            PolicyHeader policyHeader = getPolicyHeader(request);
            Record inputRecord = getInputRecord(request);

            // do validation
            getCoverageClassManager().validateForOoseCoverageClass(policyHeader, inputRecord);

            // Send back xml data
            writeEmptyAjaxXMLResponse(response);
        }
        catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to validate OOSE coverage class.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "validateForOoseCoverageClass", af);
        return af;
    }

    /**
     * Get initial values for OOSE coverage class
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getInitialValuesForOoseCoverageClass(ActionMapping mapping,
                                                              ActionForm form,
                                                              HttpServletRequest request,
                                                              HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForOoseCoverageClass",
            new Object[]{mapping, form, request, response});
        try {
            //Secure page and load all Oasis fields value include lov label fields -149171.
            securePage(request, form);

            PolicyHeader policyHeader = getPolicyHeader(request, true);
            Record inputRecord = getInputRecord(request);

            // get initial values
            Record record = getCoverageClassManager().getInitialValuesForOoseCoverageClass(policyHeader, inputRecord);

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
        l.exiting(getClass().getName(), "getInitialValuesForOoseCoverageClass", af);
        return af;
    }

    /**
     * Add Js messages
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.addCoverageClass.missingCoverage.error");
        MessageManager.getInstance().addJsMessage("pm.addCoverageClass.duplicateCoverageClass.error");
        MessageManager.getInstance().addJsMessage("pm.addCoverageClass.covgClassFromDate.error");
        MessageManager.getInstance().addJsMessage("pm.oose.modified.record.exist.error");
        MessageManager.getInstance().addJsMessage("pm.maintainReinstate.confirm.continue");
        MessageManager.getInstance().addJsMessage("pm.oose.modified.record.exist.error2");
        MessageManager.getInstance().addJsMessage("pm.deleteEndQuote.confirmed.warning");
        MessageManager.getInstance().addJsMessage("pm.applyEndQuote.confirmed.warning");
        MessageManager.getInstance().addJsMessage("pm.maintainQuote.copyPolicyToQuote.convert.confirmation");
        // auto renewal
        MessageManager.getInstance().addJsMessage("pm.autoRenewal.confirmation.info");
        //Undo Term
        MessageManager.getInstance().addJsMessage("pm.undoTerm.confirm.continue");
        // For View Claim Summary
        MessageManager.getInstance().addJsMessage("pm.viewClaimsSummary.risk.select.error");
    }

    /**
     * Load riskHeader, coverageHeader, and set to policyHeader.
     * @param request
     * @param policyHeader
     * @return
     */
    private boolean updatePolicyHeader(HttpServletRequest request, PolicyHeader policyHeader, ArrayList lovOptions,
                                       RecordSet rs, String actionCode) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "updatePolicyHeader", new Object[]{policyHeader});
        }

        boolean hasRisk = true;
        Record inputRecord = new Record();
        if (PRE.equals(actionCode)) {
            try {
                String riskId = request.getParameter(RequestIds.RISK_ID);
                policyHeader = getRiskManager().loadRiskHeader(policyHeader, riskId);

                String coverageId = request.getParameter(RequestIds.COVERAGE_ID);
                policyHeader = getCoverageManager().loadCoverageHeader(policyHeader, coverageId);
            }
            catch (AppException ae) {
                hasRisk = false;
                MessageManager.getInstance().addWarningMessage("pm.maintainRisk.noPrimaryRisk.warning");
            }

            //Load Risk Navigation Drop-down LOV.
            inputRecord.setFieldValue(CoverageFields.POLICY_NAV_LEVEL_CODE, "RISK");
            String currentValue = getPmNavigationManager().loadNavigateSourceForCoverageClass(policyHeader, inputRecord, lovOptions);
            request.setAttribute(NAVIGATION_CURRENT_VALUE, currentValue);
            String newRiskId = null;
            if (lovOptions.size() > 0) {
                LabelValueBean lovBean = (LabelValueBean) lovOptions.get(0);
                String navigationValue = lovBean.getValue();
                if (!StringUtils.isBlank(navigationValue)) {
                    newRiskId = navigationValue.split(":")[0];
                }
            }

            //If no primary risk existing, hasRisk will be false.
            //So try to get the first non-primary risk from the Risk navigation LOV and load its header.
            if (!hasRisk && newRiskId != null) {
                policyHeader = getRiskManager().loadRiskHeader(policyHeader, newRiskId);
                hasRisk = true;
                policyHeader = getCoverageManager().loadCoverageHeader(policyHeader, null);
            }

            //If there is no risk at all, show an error message.
            if (!hasRisk) {
                MessageManager.getInstance().addErrorMessage("pm.maintainRisk.noRisk.error");
            }
        }
        else if (POST.equals(actionCode)) {
            RecordSet coverageClassRecSet = null;
            Record coverageClassRec = null;
            String coverageClassId = request.getParameter(RequestIds.COVERAGE_CLASS_ID);
            if (rs != null && StringUtils.isNumeric(coverageClassId)) {
                coverageClassRecSet = rs.getSubSet(new RecordFilter(CoverageClassFields.COVERAGE_CLASS_ID, coverageClassId));
            }
            if (coverageClassRecSet != null && coverageClassRecSet.getSize() > 0) {
                coverageClassRec = coverageClassRecSet.getRecord(0);
                if (DisplayIndicator.INVISIBLE.equals(coverageClassRec.getDisplayIndicator())) {
                    RecordSet officialRecSet = rs.getSubSet(new RecordFilter(CoverageClassFields.OFFICIAL_RECORD_ID, coverageClassId));
                    if (officialRecSet.getSize() > 0) {
                        coverageClassId = CoverageClassFields.getCoverageClassId(officialRecSet.getRecord(0));
                    }
                }
                policyHeader.setCurrentSelectedId(RequestIds.COVERAGE_CLASS_ID, coverageClassId);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "updatePolicyHeader", hasRisk);
        }
        return hasRisk;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getCoverageClassManager() == null)
            throw new ConfigurationException("The required property 'coverageClassManager' is missing.");
        if (getPmNavigationManager() == null)
            throw new ConfigurationException("The required property 'pmNavigationManager' is missing.");
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
    }

    public MaintainCoverageClassAction() {
    }

    public CoverageClassManager getCoverageClassManager() {
        return m_coverageClassManager;
    }

    public void setCoverageClassManager(CoverageClassManager coverageClassManager) {
        m_coverageClassManager = coverageClassManager;
    }

    public PMNavigationManager getPmNavigationManager() {
        return m_pmNavigationManager;
    }

    public void setPmNavigationManager(PMNavigationManager pmNavigationManager) {
        m_pmNavigationManager = pmNavigationManager;
    }

    private CoverageClassManager m_coverageClassManager;
    private PMNavigationManager m_pmNavigationManager;

    protected static final String COVERAGE_CLASS_GRID_ID = "coverageClassListGrid";
    protected static final String PRE = "PRE";
    protected static final String POST = "POST";
    protected static final String NAVIGATION_CURRENT_VALUE = "navigationCurrentValue";
}
