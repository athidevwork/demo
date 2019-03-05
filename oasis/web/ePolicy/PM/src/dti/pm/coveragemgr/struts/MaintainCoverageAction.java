package dti.pm.coveragemgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.DisplayIndicator;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.BeanDtiUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.busobjs.UpdateIndicator;
import dti.pm.busobjs.ComponentOwner;
import dti.pm.componentmgr.ComponentFields;
import dti.pm.componentmgr.impl.ComponentEntitlementRecordLoadProcessor;
import dti.pm.core.data.CommonTabsEntitlementRecordLoadProcessor;
import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.componentmgr.ComponentManager;
import dti.pm.componentmgr.impl.ComponentRowStyleRecordLoadprocessor;
import dti.pm.coveragemgr.impl.CoverageEntitlementRecordLoadProcessor;
import dti.pm.pmnavigationmgr.PMNavigationManager;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.coveragemgr.impl.CoverageRowStyleRecordLoadprocessor;
import dti.pm.coveragemgr.CoverageFields;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.LabelValueBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for Maintain Coverage.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 10, 2007
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
 * 09/19/2007       fcb         AddJsMessages: pm.oose.modified.record.exist.error2 added.
 * 04/07/2008       fcb         getCoverageLimitShared added.
 * 09/29/2008       sxm         Issue 86880 - append risk type to risk name, append "Selected" to current risk name,
 *                                            and set current value of selected record
 * 06/21/2010       syang       Issue 108715 - Modified addAllDefaulComponent to set coverage id of
 *                                            newly added coverage to session.
 * 07/09/2010       syang       Issue 108715 - Modified addAllDefaulComponent to set coverage id to forward parameter,
 *                                            so as to system can get parameter from request when reload page.
 * 08/04/2010       syang       103793 - Add js message for Maintain Surcharge Points page in coverage level.
 * 09/13/2010       wfu         111776: Replaced hardcode string with resource definition
 * 09/16/2010       dzhang      103813 - Add js message for Undo Term.
 * 01/31/2011       wfu         116334 - Added js message for view claims summary.
 * 12/07/2011       wfu         127859 - Modified loadDependentCoverage, loadNavigateSource and getInitialValuesForAddComponent
 *                                       to support loading policy header without primary risk for policy component.
 * 07/24/2012       awu         129250 - Added autoSaveAllCoverage().
 * 08/10/2012       adeng       Issue 135971 - Modified loadAllCoverage to call CoverageEntitlementRecordLoadProcessor
 *                                             .postProcessRecordSet(),to make sure If no record is fetched, add the
 *                                              page entitlement fields to the recordset field collection.
 * 01/22/2013       adeng       141183 - 1)Added a method setIsNoteVisible() to get note field's setting of visible in
 *                                       WebWB and set it into input record;
 *                                       2)Modified loadAllCoverage() & getInitialValuesForAddComponent() to call above
 *                                       method.
 * 01/25/2013       adeng       141183 - Roll backed last change.
 * 04/19/2013       xnie        142770 - Modified loadAllCoverage() to load primary coverage as coverageHeader when
 *                                       coverageHeader is null (For example, policy is retrieved just now and coverage
 *                                       grid is not retrieved yet).
 * 04/24/2013       awu         141758 - 1) Changed addAllDefaultComponent to addAllCoverage to response add covg request.
 *                                       2) Add addAllComponent.
 * 04/25/2013       xnie        142770 - Roll backed prior changes.
 * 05/29/2013       jshen       141758 - Set the first inserted component's ID into session
 * 12/03/2013       jyang       149171 - Roll back issue141758's change to load lov label fields' default value in all
 *                                       getInitialValuesForXXX methods.
 * 12/19/2013       jyang       148585 - Get CoverageId from request and set it into userSession.
 * 03/13/2014       awu         152963 - Modified addJsMessages to add a new message for Part Time warning.
 * 07/25/2014       awu         152034 - 1). Added updatePolicyHeader to set riskHeader, coverageId, coverageClassId.
 *                                       2). Modified loadAllCoverage to call updatePolicyHeader.
 *                                       3). Modified loadAllCoverage to roll back the changes of issue148585.
 * 07/29/2014       xnie        155378 - Modified saveAllCoverage() to call resetDisplayIndicatorForDeletedRs when save error.
 * 08/25/2014       awu         152034 - 1). Modified loadAllCoverate to call updatePolicyHeader two times.
 *                                       2). Moved the navigation lov loading from loadAllCoverate to updatePolicyHeader.
 *                                       3). Modified updatePolicyHeader to load the coverage header for the active record
 *                                           if the current record is closed.
 * 09/15/2014       wdang       157555 - Modified updatePolicyHeader to check if coverageId is a numeric before use it.
 * 12/12/2014       fcb         158691 - Modified loadAllCoverage to add logic to run the entitlements independently
 *                                       when the coverage and the component record sets are not empty. These are the
 *                                       cases when we come from add coverage or add component pop up screens, and
 *                                       because we do not run entitlements for the record sets, some options are
 *                                       missing.
 * 12/16/2014       awu         159187 - Modified autoSaveAllCoverage to catch ValidationException.
 * 10/13/2015       tzeng       164679 - Modified loadPolicyDetail and added RISK_RELATION_MESSAGE_TYPE to display auto
 *                                       risk relation result message after add new risk then navigate to coverage tab.
 * 01/28/2016       wdang       169024 - 1) Reverted changes of 164679.
 *                                       2) Modified loadAllCoverage to invoke addSaveMessages().
 * 07/29/2016       bwang       178252 - Modified loadAllCoverage() to call Common tabs entitlement record load processor
 *                                       risk tab page visible/invisible logic when there is no any risk on policy.
 * 07/26/2017       lzhang      182246 - Delete Js message
 * 09/14/2018       cesar       195306 - added saveToken(request) to loadAllCoverage() to implement CSRF requirement.
 * ---------------------------------------------------
 */

public class MaintainCoverageAction extends PMBaseAction {
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
        return loadAllCoverage(mapping, form, request, response);
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
    public ActionForward loadAllCoverage(ActionMapping mapping, ActionForm form,
                                         HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllCoverage", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            //First,load policy header without risk header
            PolicyHeader policyHeader = getPolicyHeader(request);

            Record inputRecord = getInputRecord(request);

            //Load the GO TO navigation drop-down LOV.
            ArrayList lovOptions = new ArrayList();
            
            //update the risk header, coverage header to policy header.
            boolean hasRisk = updatePolicyHeader(request, policyHeader, lovOptions, null, PRE);

            // Load the coverages
            RecordSet rs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            // Load the components
            RecordSet compRs = (RecordSet) request.getAttribute(COMP_GRID_RECORD_SET);
            // Prepare component owner
            ComponentOwner owner = ComponentOwner.COVERAGE;
            //if has no risk,create two empty records
            if (!hasRisk) {
                List fieldNames = new ArrayList();
                fieldNames.add("policyId");
                setEmptyDataBean(request);
                rs = (RecordSet) ((HashMap) request.getAttribute(dti.oasis.http.RequestIds.RECORDSET_MAP)).get(dti.oasis.http.RequestIds.DATA_BEAN);
                CoverageEntitlementRecordLoadProcessor cerp = new CoverageEntitlementRecordLoadProcessor(getCoverageManager(), policyHeader, policyHeader.getScreenModeCode());
                cerp.postProcessRecordSet(rs);
                CommonTabsEntitlementRecordLoadProcessor commonTabsLP = new CommonTabsEntitlementRecordLoadProcessor(policyHeader);
                commonTabsLP.postProcessRecordSet(rs);
                compRs = new RecordSet();
                rs.addFieldNameCollection(fieldNames);
                compRs.addFieldNameCollection(fieldNames);
            }
            else {
                if (rs == null) {
                    RecordLoadProcessor covgRowStyleLp = new CoverageRowStyleRecordLoadprocessor();
                    rs = getCoverageManager().loadAllCoverage(policyHeader, covgRowStyleLp);
                }
                else {
                    CoverageEntitlementRecordLoadProcessor cerp = new CoverageEntitlementRecordLoadProcessor(getCoverageManager(), policyHeader, policyHeader.getScreenModeCode());
                    cerp.postProcessRecordSet(rs);
                    CommonTabsEntitlementRecordLoadProcessor commonTabsLP = new CommonTabsEntitlementRecordLoadProcessor(policyHeader);
                    commonTabsLP.postProcessRecordSet(rs);
                }

                if (compRs == null) {
                    ComponentRowStyleRecordLoadprocessor compRowStyleLp = new ComponentRowStyleRecordLoadprocessor();
                    compRs = getComponentManager().loadAllComponent(policyHeader, inputRecord, owner, rs, compRowStyleLp);
                }
                else {
                    ComponentEntitlementRecordLoadProcessor cerp = new ComponentEntitlementRecordLoadProcessor(getComponentManager(), policyHeader, inputRecord,
                        policyHeader.getScreenModeCode(), owner, rs);
                    cerp.postProcessRecordSet(compRs);
                }

                // Set loaded Coverage data into request
                setDataBean(request, rs);
            }

            // Set loaded Component data into request
            setDataBean(request, compRs, COMPONENT_GRID_ID);

            // Make the Summary Record available for output
            Record output = rs.getSummaryRecord();
            output.setFields(compRs.getSummaryRecord(), false);
            // Set the navigation fields value
            Record record = getPmNavigationManager().getPolicyNavParameters(inputRecord);
            output.setFields(record);

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);

            // Load coverage grid header
            loadGridHeader(request);

            // Set currentGridId to componentListGrid before load component gird header
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, COMPONENT_GRID_ID);

            // Load the list of values after loading the data
            loadListOfValues(request, form);

            request.setAttribute(CoverageFields.POLICY_NAV_SOURCE_ID, BeanDtiUtils.createValueBean(CoverageFields.POLICY_NAV_SOURCE_ID, request.getAttribute(NAVIGATION_CURRENT_VALUE)));
            request.setAttribute(CoverageFields.POLICY_NAV_SOURCE_ID+"LOV", lovOptions);

            // Load component grid header
            loadGridHeader(request, null, COMPONENT_GRID_ID, COMPONENT_GRID_LAYER_ID);

            // Populate messages for javascirpt
            addJsMessages();

            // Add messages for save purpose
            addSaveMessages(policyHeader, request);

            // Update the coverage header to policy header.
            if (hasRisk) {
                updatePolicyHeader(request, policyHeader, null, rs, POST);
            }

            saveToken(request);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the coverage page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllCoverage", af);
        return af;
    }

    /**
     * Method to validate a changed policy no during first term creation.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAddlInfo(ActionMapping mapping, ActionForm form,
                                      HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAddlInfo", new Object[]{mapping, form, request, response});

        try {

            // Secure page without load fields
            securePage(request, form, false);

            // Get the policy header from the request, and load the risk header and coverage header
            PolicyHeader policyHeader = getPolicyHeader(request, true, true);

            // Map request to record for input
            Record inputRecord = getInputRecord(request);

            // Get the additional info data
            Record output = getCoverageManager().loadCoverageAddlInfo(policyHeader, inputRecord);

            // Send back xml data
            writeAjaxXmlResponse(response, output);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to load address line information.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "loadAddlInfo", af);
        return af;
    }

    /**
     * Save all updated coverage records.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward saveAllCoverage(ActionMapping mapping, ActionForm form,
                                         HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveAllCoverage", new Object[]{mapping, form, request, response});

        RecordSet inputRecords = null;
        RecordSet componentInputRecords = null;
        String forwardString = "saveResult";
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);

                // First save coverage records
                // Map coverage textXML to RecordSet for input
                inputRecords = getInputRecordSet(request, COVERAGE_GRID_ID);

                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(),
                        "saveAllCoverage", "Saving the coverage inputRecords: " + inputRecords);
                }

                PolicyHeader policyHeader = getPolicyHeader(request, true);

                // set currentGridId to componentListGrid before get input recordSet for component grid
                RequestStorageManager.getInstance().set(CURRENT_GRID_ID, COMPONENT_GRID_ID);
                // Map component textXML to RecordSet for input
                componentInputRecords = getInputRecordSet(request, COMPONENT_GRID_ID);

                // Save the coverage changes
                getCoverageManager().processSaveAllCoverageAndComponent(policyHeader, inputRecords, componentInputRecords);

                // Set back to coverageListGrid
                RequestStorageManager.getInstance().set(CURRENT_GRID_ID, COVERAGE_GRID_ID);
            }
        }
        catch (ValidationException ve) {
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, COVERAGE_GRID_ID);

            // Reset display indicator to 'Y' for official record which is closed by deleted temp record.
            resetDisplayIndicatorForDeletedRs(inputRecords, CoverageFields.COVERAGE_ID);
            // Save the coverage input records into request
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);

            // Reset display indicator to 'Y' for official record which is closed by deleted temp record.
            resetDisplayIndicatorForDeletedRs(componentInputRecords, ComponentFields.POLICY_COV_COMPONENT_ID);
            // Save the component input records into request
            request.setAttribute(COMP_GRID_RECORD_SET, componentInputRecords);

            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to save the coverage page.", e, request, mapping);
        }

        // Return the forward
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveAllCoverage", af);
        return af;
    }

    /**
     * Save all updated coverage records.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward autoSaveAllCoverage(ActionMapping mapping, ActionForm form,
                                             HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "autoSaveAllCoverage", new Object[]{mapping, form, request, response});

        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);

                // First save coverage records
                // Map coverage textXML to RecordSet for input
                RecordSet inputRecords = getInputRecordSet(request, COVERAGE_GRID_ID);

                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(),
                        "saveAllCoverage", "Saving the coverage inputRecords: " + inputRecords);
                }

                PolicyHeader policyHeader = getPolicyHeader(request, true);

                // set currentGridId to componentListGrid before get input recordSet for component grid
                RequestStorageManager.getInstance().set(CURRENT_GRID_ID, COMPONENT_GRID_ID);
                // Map component textXML to RecordSet for input
                RecordSet componentInputRecords = getInputRecordSet(request, COMPONENT_GRID_ID);

                // Save the coverage changes
                getCoverageManager().processAutoSaveAllCoverageAndComponent(policyHeader, inputRecords, componentInputRecords);

                // Set back to coverageListGrid
                RequestStorageManager.getInstance().set(CURRENT_GRID_ID, COVERAGE_GRID_ID);

                writeEmptyAjaxXMLResponse(response);
            }
        }
        catch (ValidationException ve) {
            writeEmptyAjaxXMLResponse(response);
        }
        catch (Exception e) {
            handleErrorForAutoSaveAjax(AppException.UNEXPECTED_ERROR, "Failed to auto save the coverage page.", e, response);
        }

        // Return the forward
        ActionForward af = null;
        l.exiting(getClass().getName(), "autoSaveAllCoverage", af);
        return af;
    }

    /**
     * loadDependentCoverage
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadDependentCoverage(ActionMapping mapping,
                                               ActionForm form,
                                               HttpServletRequest request,
                                               HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadDependentCoverage",
            new Object[]{mapping, form, request, response});

        try {

            // Secure page without load fields
            securePage(request, form, false);

            //Load policy Header
            PolicyHeader policyHeader = getPolicyHeader(request, true);
            RecordSet rs = getCoverageManager().loadDependentCoverage(policyHeader, getInputRecord(request));

            // Send back xml data
            writeAjaxXmlResponse(response, rs);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to load dependent coverage.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "loadDependentCoverage", af);
        return af;
    }

    /**
     * Load dependent components
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadDependentComponent(ActionMapping mapping,
                                                ActionForm form,
                                                HttpServletRequest request,
                                                HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadDependentComponent",
            new Object[]{mapping, form, request, response});

        try {
            securePage(request, form);

            PolicyHeader policyHeader = getPolicyHeader(request);
            try {
                //load risk header for coverage component
                String riskId = request.getParameter("riskId");
                // If the riskId is not specified, the primary risk is loaded.
                policyHeader = getRiskManager().loadRiskHeader(policyHeader, riskId);
            } catch (AppException ae) {
                // No primary risk existed and given riskId has no record.
            }

            Record record = getInputRecord(request);
            RecordSet rs = getComponentManager().loadDependentComponent(policyHeader, record,
                DefaultRecordLoadProcessor.DEFAULT_INSTANCE);

            // Set currentGridId to componentListGrid
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, COMPONENT_GRID_ID);

            // Send back xml data
            writeAjaxXmlResponse(response, rs);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to load dependent components.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "loadDependentComponent", af);
        return af;
    }

    /**
     * Get Inital Values for new added coverage
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward getInitialValuesForCoverage(ActionMapping mapping,
                                                     ActionForm form,
                                                     HttpServletRequest request,
                                                     HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForCoverage",
            new Object[]{mapping, form, request, response});

        try {
			//Secure page and load all Oasis fields value include lov label fields -149171.
            securePage(request, form);
            PolicyHeader policyHeader = getPolicyHeader(request, true);

            // Get inital values
            Record record = getCoverageManager().getInitialValuesForCoverage(policyHeader, getInputRecord(request));

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
        l.exiting(getClass().getName(), "getInitialValuesForCoverage", af);
        return af;

    }

    /**
     * Get initial values for new added components
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getInitialValuesForAddComponent(ActionMapping mapping,
                                                         ActionForm form,
                                                         HttpServletRequest request,
                                                         HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForComponent",
            new Object[]{mapping, form, request, response});

        try {
			//Secure page and load all Oasis fields value include lov label fields -149171.
            securePage(request, form);
            PolicyHeader policyHeader = getPolicyHeader(request);
            try {
                // Load risk header for coverage component
                String riskId = request.getParameter("riskId");
                // If the riskId is not specified, the primary risk is loaded.
                policyHeader = getRiskManager().loadRiskHeader(policyHeader, riskId);
            } catch (AppException ae) {
                // No primary risk existed and given riskId has no record.
            }

            // Get inital values
            Record record = getComponentManager().getInitialValuesForAddComponent(policyHeader, getInputRecord(request));

            // Get LOV labels for initial values
            publishOutputRecord(request, record);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, record);

            // Set currentGridId to componentListGrid
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, COMPONENT_GRID_ID);

            // Send back xml data
            writeAjaxXmlResponse(response, record);

        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForComponent", af);
        return af;
    }

    /**
     * Check if prior acts existed
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward validatePriorActsExist(ActionMapping mapping,
                                                ActionForm form,
                                                HttpServletRequest request,
                                                HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "validatePriorActsExist",
            new Object[]{mapping, form, request, response});

        try {

            // Secure page without load fields
            securePage(request, form, false);

            // Load policy Header
            PolicyHeader policyHeader = getPolicyHeader(request, true);
            getCoverageManager().validatePriorActsExist(policyHeader, getInputRecord(request));

            // Send back xml data
            writeEmptyAjaxXMLResponse(response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get Prior Acts Exists.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "validatePriorActsExist", af);
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
    public ActionForward validateForOoseCoverage(ActionMapping mapping,
                                                 ActionForm form,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "validateForOoseCoverage",
            new Object[]{mapping, form, request, response});
        try {
            // Secure access to the page
            securePage(request, form, false);

            PolicyHeader policyHeader = getPolicyHeader(request, true);
            Record inputRecord = getInputRecord(request);

            // do validation
            getCoverageManager().validateForOoseCoverage(policyHeader, inputRecord);

            // Send back xml data
            writeEmptyAjaxXMLResponse(response);
        }
        catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to validate OOSE coverage.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "validateForOoseCoverage", af);
        return af;
    }

    /**
     * Get initial values for OOSE coverage
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getInitialValuesForOoseCoverage(ActionMapping mapping,
                                                         ActionForm form,
                                                         HttpServletRequest request,
                                                         HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForOoseCoverage",
            new Object[]{mapping, form, request, response});
        try {
            //Secure page and load all Oasis fields value include lov label fields -149171.
            securePage(request, form);

            PolicyHeader policyHeader = getPolicyHeader(request, true);
            Record inputRecord = getInputRecord(request);

            // get initial values
            Record record = getCoverageManager().getInitialValuesForOoseCoverage(policyHeader, inputRecord);

            // get LOV labels for initial values
            publishOutputRecord(request, record);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, record);

            // prepare return values
            writeAjaxXmlResponse(response, record, true);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values for OOSE coverage.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForOoseCoverage", af);
        return af;
    }

    /**
     * Get initial values for OOSE component
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getInitialValuesForOoseComponent(ActionMapping mapping,
                                                          ActionForm form,
                                                          HttpServletRequest request,
                                                          HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForOoseComponent",
            new Object[]{mapping, form, request, response});
        try {
            //Secure page and load all Oasis fields value include lov label fields -149171.
            securePage(request, form);

            PolicyHeader policyHeader = getPolicyHeader(request, true);
            Record inputRecord = getInputRecord(request);

            // get initial values
            Record record = getComponentManager().getInitialValuesForOoseComponent(policyHeader, inputRecord);

            // get LOV labels for initial values
            publishOutputRecord(request, record);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, record);
            // Set currentGridId to componentListGrid
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, COMPONENT_GRID_ID);
            // prepare return values
            writeAjaxXmlResponse(response, record, true);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values for OOSE component.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForOoseComponent", af);
        return af;
    }

    /**
     * Add all the coverages user selected.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward addAllCoverage(ActionMapping mapping, ActionForm form,
                                        HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "addAllCoverage", new Object[]{mapping, form, request, response});

        RecordSet inputRecords;
        RecordSet componentInputRecords;
        String forwardString = "reload";
        try {
            // Secure access to the page
            securePage(request, form, false);
            Record inputRecord = getInputRecord(request);
            // Map coverage textXML to RecordSet for input
            inputRecords = getInputRecordSet(request, COVERAGE_GRID_ID);
            // set currentGridId to componentListGrid before get input recordSet for component grid
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, COMPONENT_GRID_ID);
            // Map component textXML to RecordSet for input
            componentInputRecords = getInputRecordSet(request, COMPONENT_GRID_ID);
            PolicyHeader policyHeader = getPolicyHeader(request, true);
            // Add default components
            getCoverageManager().addAllCoverage(policyHeader, inputRecord, inputRecords, componentInputRecords);


            // Send back changed component record set which adds default components.
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);
            request.setAttribute(COMP_GRID_RECORD_SET, componentInputRecords);

            // Set back to coverageListGrid
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, COVERAGE_GRID_ID);
            // Issue 108715, set the coverage id of the newly added coverage to session.
            RecordSet insertedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.INSERTED));
            String newCoverageId = CoverageFields.getCoverageId(insertedRecords.getRecord(0));
            getPolicyManager().setCurrentIdsInSession(policyHeader.getTermBaseRecordId(),
                request.getParameter(RequestIds.RISK_ID), newCoverageId,
                request.getParameter(RequestIds.COVERAGE_CLASS_ID), UserSessionManager.getInstance().getUserSession());
            setForwardParameter(request, RequestIds.COVERAGE_ID, newCoverageId);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to add all coverages.", e, request, mapping);
        }

        // Return the forward
        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addAllCoverage", af);
        }
        return af;
    }

    /**
     * Add all selected components
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward addAllComponent(ActionMapping mapping, ActionForm form,
                                         HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "addAllComponent", new Object[]{mapping, form, request, response});

        RecordSet inputRecords;
        RecordSet componentInputRecords;
        String forwardString = "reload";
        try {
            // Secure access to the page
            securePage(request, form);
            Record inputRecord = getInputRecord(request);
            // Map coverage textXML to RecordSet for input
            inputRecords = getInputRecordSet(request, COVERAGE_GRID_ID);
            // set currentGridId to componentListGrid before get input recordSet for component grid
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, COMPONENT_GRID_ID);
            // Map component textXML to RecordSet for input
            componentInputRecords = getInputRecordSet(request, COMPONENT_GRID_ID);
            PolicyHeader policyHeader = getPolicyHeader(request, true);

            String parentCoverageId = getCoverageManager().addAllComponent(policyHeader, inputRecord, componentInputRecords);
            // Send back changed component record set which adds default components.
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);
            request.setAttribute(COMP_GRID_RECORD_SET, componentInputRecords);

            // Set back to coverageListGrid
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, COVERAGE_GRID_ID);
            RecordSet insertedRecords = componentInputRecords.getSubSet(new RecordFilter(CoverageFields.PARENT_COVERAGE_ID, parentCoverageId)).
                getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.INSERTED));
            String newComponentId = "";
            if (insertedRecords.getSize() > 0) {
                newComponentId = ComponentFields.getPolicyCovComponentId(insertedRecords.getRecord(0));
            }
            getPolicyManager().setCurrentIdsInSession(policyHeader.getTermBaseRecordId(),
                request.getParameter(RequestIds.RISK_ID), parentCoverageId, newComponentId,
                request.getParameter(RequestIds.COVERAGE_CLASS_ID), UserSessionManager.getInstance().getUserSession());
            setForwardParameter(request, RequestIds.COVERAGE_ID, parentCoverageId);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to add all component.", e, request, mapping);
        }

        // Return the forward
        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addAllComponent", af);
        }
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
            if (currentGridId.equals(COMPONENT_GRID_ID)) {
                anchorName = getComponentAnchorColumnName();
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

    /**
     * An ajax call to get the coverage limit shared flag.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getCoverageLimitShared(ActionMapping mapping,
                                                ActionForm form,
                                                HttpServletRequest request,
                                                HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getCoverageLimitShared",
            new Object[]{mapping, form, request, response});

        try {
            // Secure access to the page
            securePage(request, form, false);

            PolicyHeader policyHeader = getPolicyHeader(request);
            Record inputRecord = getInputRecord(request);

            // get indicator
            getCoverageManager().setCoverageLimitShared(policyHeader, inputRecord);

            // prepare return values
            writeAjaxXmlResponse(response, inputRecord, true);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR,
                "Failed to get coverage limit shared indicator.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getCoverageLimitShared", af);
        return af;
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        // Add and Maintain coverage messages
        MessageManager.getInstance().addJsMessage("pm.maintainCoverage.priorActsExists.warning");
        MessageManager.getInstance().addJsMessage("pm.maintainCoverage.excessPayor.warning");
        MessageManager.getInstance().addJsMessage("pm.addCoverage.coverageEffectiveDate.error");
        MessageManager.getInstance().addJsMessage("pm.addCoverage.duplicateCoverage.exist.error");
        MessageManager.getInstance().addJsMessage("pm.oose.modified.record.exist.error");
        MessageManager.getInstance().addJsMessage("pm.oose.modified.record.exist.error2");
        // Component messages
        MessageManager.getInstance().addJsMessage("pm.maintainComponent.effectiveToDate.rule1.error");
        MessageManager.getInstance().addJsMessage("pm.addCoverageClass.missingCoverage.error");
        MessageManager.getInstance().addJsMessage("pm.addComponent.noCoverage.error");
        MessageManager.getInstance().addJsMessage("pm.maintainReinstate.confirm.continue");
        MessageManager.getInstance().addJsMessage("pm.addComponent.duplicated.error");
        MessageManager.getInstance().addJsMessage("pm.deleteEndQuote.confirmed.warning");
        MessageManager.getInstance().addJsMessage("pm.applyEndQuote.confirmed.warning");
        MessageManager.getInstance().addJsMessage("pm.maintainQuote.copyPolicyToQuote.convert.confirmation");
        // auto renewal
        MessageManager.getInstance().addJsMessage("pm.autoRenewal.confirmation.info");
        // For limit shared error
        MessageManager.getInstance().addJsMessage("pm.maintainCoverage.getLimitShared.error");
        // Undo Term
        MessageManager.getInstance().addJsMessage("pm.undoTerm.confirm.continue");
        // For View Claim Summary
        MessageManager.getInstance().addJsMessage("pm.viewClaimsSummary.risk.select.error");
        // For Part Time component warning
        MessageManager.getInstance().addJsMessage("pm.addComponent.partTime.component.info");
    }

    /**
     * Load riskHeader, set riskHeader, coverageId, coverageClassId to policyHeader.
     * @param request
     * @param policyHeader
     * @return
     */
    private boolean updatePolicyHeader(HttpServletRequest request, PolicyHeader policyHeader, ArrayList lovOptions,
                                       RecordSet rs, String actionCode) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "updatePolicyHeader", new Object[]{policyHeader, actionCode});
        }

        boolean hasRisk = true;
        String newRiskId = null;

        if (PRE.equals(actionCode)) {
            try {
                //Try to load risk header
                String riskId = request.getParameter(RequestIds.RISK_ID);
                // If the riskId is not specified, the first riskId is loaded.
                policyHeader = getRiskManager().loadRiskHeader(policyHeader, riskId);
            }
            catch (AppException ae) {
                MessageManager.getInstance().addWarningMessage("pm.maintainRisk.noPrimaryRisk.warning");
                hasRisk = false;
            }

            String currentValue = getPmNavigationManager().loadNavigateSourceForCoverage(policyHeader, new Record(), lovOptions);
            request.setAttribute(NAVIGATION_CURRENT_VALUE, currentValue);
            if (lovOptions.size() > 0) {
                LabelValueBean lovBean = (LabelValueBean) lovOptions.get(0);
                newRiskId = lovBean.getValue();
            }

            if (!hasRisk) {
                if (newRiskId != null) {
                    policyHeader = getRiskManager().loadRiskHeader(policyHeader, newRiskId);
                    hasRisk = true;
                }
            }
            if (!hasRisk) {
                MessageManager.getInstance().addErrorMessage("pm.maintainRisk.noRisk.error");
            }
        }
        else if (POST.equals(actionCode)) {
            //Load the coverage header if coverageId is existing in the request.
            String coverageId = request.getParameter(RequestIds.COVERAGE_ID);
            RecordSet coverageRecSet = null;
            Record coverageRec = null;
            String currentCoverageId = null;
            if (rs != null && StringUtils.isNumeric(coverageId) ) {
                coverageRecSet = rs.getSubSet(new RecordFilter(CoverageFields.COVERAGE_ID, coverageId));
            }
            if (coverageRecSet != null && coverageRecSet.getSize() > 0) {
                coverageRec = coverageRecSet.getRecord(0);

                //A new coverage which hasn't been saved to DB yet.
                if (UpdateIndicator.INSERTED.equals(coverageRec.getUpdateIndicator())) {
                    policyHeader.setCurrentSelectedId(RequestIds.COVERAGE_ID, coverageId);
                    return true;
                }

                //If the coverage is closed, find its first active version.
                if (DisplayIndicator.INVISIBLE.equals(coverageRec.getDisplayIndicator())) {
                    RecordSet officialSet = rs.getSubSet(new RecordFilter(CoverageFields.OFFICIAL_RECORD_ID, coverageId));
                    if (officialSet.getSize() > 0) {
                        coverageId = CoverageFields.getCoverageId(officialSet.getRecord(0));
                    }
                }
                getCoverageManager().loadCoverageHeader(policyHeader, coverageId);
            }
            if (policyHeader.hasCoverageHeader()) {
                currentCoverageId = policyHeader.getCoverageHeader().getCoverageId();
            }
            policyHeader.setCurrentSelectedId(RequestIds.COVERAGE_ID, currentCoverageId);
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
        if (getCoverageManager() == null)
            throw new ConfigurationException("The required property 'coverageManager' is missing.");
        if (getComponentManager() == null)
            throw new ConfigurationException("The required property 'componentManager' is missing.");
        if (getPmNavigationManager() == null)
            throw new ConfigurationException("The required property 'pmNavigationManager' is missing.");
        if (getComponentAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'componentAnchorColumnName' is missing.");
        if (super.getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");

    }

    public ComponentManager getComponentManager() {
        return m_componentManager;
    }

    public void setComponentManager(ComponentManager componentManager) {
        m_componentManager = componentManager;
    }

    public String getComponentAnchorColumnName() {
        return m_componentAnchorColumnName;
    }

    public void setComponentAnchorColumnName(String componentAnchorColumnName) {
        m_componentAnchorColumnName = componentAnchorColumnName;
    }


    public PMNavigationManager getPmNavigationManager() {
        return m_pmNavigationManager;
    }

    public void setPmNavigationManager(PMNavigationManager pmNavigationManager) {
        m_pmNavigationManager = pmNavigationManager;
    }

    public MaintainCoverageAction() {
    }

    protected static final String DATE_CHANGE_ALLOWED = "dateChangeAllowedB";
    protected static final String CURRENT_GRID_ID = "currentGridId";
    protected static final String COVERAGE_GRID_ID = "coverageListGrid";
    protected static final String COMPONENT_GRID_ID = "componentListGrid";
    protected static final String COMPONENT_GRID_LAYER_ID = "PM_COMP_GH";
    protected static final String COMP_GRID_RECORD_SET = "compGridRecordSet";
    protected static final String PRE = "PRE";
    protected static final String POST = "POST";
    protected static final String NAVIGATION_CURRENT_VALUE = "navigationCurrentValue";

    private ComponentManager m_componentManager;
    private String m_componentAnchorColumnName;
    private PMNavigationManager m_pmNavigationManager;
}
