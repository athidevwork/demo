package dti.pm.policymgr.struts;

import dti.ci.addressmgr.AddressFields;
import dti.cs.recordexistsmgr.RecordExistsManager;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.busobjs.YesNoFlag;
import dti.pm.busobjs.PolicyHeaderReloadCode;
import dti.pm.core.http.RequestIds;
import dti.pm.core.request.RequestStorageIds;
import dti.pm.core.session.UserSessionIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.entitymgr.EntityFields;
import dti.pm.policymgr.PolicyFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.busobjs.ComponentOwner;
import dti.pm.componentmgr.ComponentManager;
import dti.pm.componentmgr.impl.ComponentRowStyleRecordLoadprocessor;
import dti.pm.policymgr.impl.PolicySaveProcessor;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.parser.Entity;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for Maintain Policy.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 14, 2006
 *
 * @author jmpotosky
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/09/2007       sxm         Added logic to set the currentpolicyTermHistoryId, riskId, coverageId 
 *                              and coverageClassId in user session
 * 12/29/2007       Joe         Add method isPolicyNotesExist() to check policy notes
 * 04/23/2008       fcb         addJsMessages: message added.
 * 01/14/2009       yhyang      #89259: Redirect the page when user changes the policy no.
 * 06/11/2010       bhong       108653 - Added message for cancel valdiation
 * 07/13/2010       syang       103797 - Added js message.
 * 09/14/2010       dzhang      103813 - Added js message.
 * 01/31/2011       wfu         116334 - Added js message for view claims summary.
 * 04/06/2011       fcb         119324: pass indicator to not load default values.
 * 11/21/2011       syang       125711 - Modified loadPolicyDetail() to save default values if it is a newly created policy.
 * 11/25/2011       syang       127661 - Add js message.
 * 12/15/2011       fcb         128024 - loadPolicyDetail(): logic had been previously added to save the default values
 *                                       when the page is loaded for the first time.
 *                                       Because this is done on load after the data was retrieved from the DB, and not
 *                                       when the user saves from the Web Page, the record set has fields with NULL
 *                                       values instead of empty strings as it would have when coming from the browser.
 *                                       Therefore for this specific case we set them to empty strings as if it was
 *                                       coming from the browser. Subsequently, we re-load the data.
 * 07/24/2012       awu         129250 - Added autoSavePolicy().
 * 09/03/2012       adeng       135972 - Modified loadPolicyDetail() to check has risk or not, and set the status to input record.
 * 01/22/2013       adeng       141183 - Modified loadPolicyDetail() to get note field's setting of visible in WebWB and
 *                                       set it into input record.
 * 01/25/2013       adeng       141183 - Roll backed last change.
 * 12/06/2013       Parker      148036 - Refactor maintainRecordExists code to make one call per subsystem to the database.
 * 07/25/2014       awu         152034 - 1). Added updatePolicyHeader to load riskHeader/coverageHeader, set coverageClassId.
 *                                       2). Modified loadPolicyDetail to call setPolicyHeader.
 * 08/20/2014       awu         152034 - 1). Modified updatePolicyHeader to load the coverage header only when risk header is not null.
 *                                       2). Coverage Class logic in updatePolicyHeader is no needed because it will be
 *                                           handled in MaintainCoverageClassAction.
 * 12/03/2014       kxiang      158932 - Modified savePolicy() to save component first then save policy.
 * 12/16/2014       awu         159187 - Modified autoSavePolicy to catch ValidationException.
 * 10/13/2015       tzeng       164679 - Modified loadPolicyDetail and added RISK_RELATION_MESSAGE_TYPE to display auto
 *                                       risk relation result message after add new risk then navigate to policy tab.
 * 01/28/2016       wdang       169024 - 1) Reverted changes of 164679.
 *                                       2) Modified loadPolicyDetail to invoke addSaveMessages().
 * 03/25/2016       eyin        170323 - Modified method loadPolicyDetail to get the value of new added buttons and
 *                                       put them in session.
 * 07/22/2016       bwang       178033 - Modified updatePolicyHeader(),changed integer type variables to long type which are from PK/FK fields in DB.
 * 06/17/2016       ssheng      164927 - filter PM entity
 * 07/26/2017       lzhang      182246 - Delete Js message
 * 12/04/2017       lzhang      190020 - Modified remove updatePolicyHeader() to PMBaseAction.
 * 06/13/2018       wrong       192557 - Modified deleteOosPolicyDetail() to call hasValidSaveToken() to be used for
 *                                       CSRFInterceptor.
 * 07/04/2018       xnie        187070 - Modified savePolicy() and autoSavePolicy() to reload policy header.
 * 09/14/2018       cesar       195306 - added saveToken(request) to loadPolicyDetail() to implement CSRF requirement.
 * ---------------------------------------------------
 */

public class MaintainPolicyAction extends PMBaseAction {
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
        return loadPolicyDetail(mapping, form, request, response);
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
    public ActionForward loadPolicyDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadPolicyDetail", new Object[]{mapping, form, request, response});

        String forwardString = "loadResult";
        Record output = null;

        try {
            //First,load policy header without risk header
            PolicyHeader policyHeader = getPolicyHeader(request);

            //Update risk header, coverage header, coverage class id to policy header.
            boolean hasRisk = updatePolicyHeader(request, policyHeader);

            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            Record inputRecord = getInputRecord(request);

            if(inputRecord.hasField(RequestIds.ORG_SORT_COLUMN)){
                Record orgRecord = new Record();
                orgRecord.setFieldValue(RequestIds.ORG_SORT_COLUMN, inputRecord.getFieldValue(RequestIds.ORG_SORT_COLUMN, null));
                orgRecord.setFieldValue(RequestIds.ORG_SORT_TYPE, inputRecord.getFieldValue(RequestIds.ORG_SORT_TYPE, null));
                orgRecord.setFieldValue(RequestIds.ORG_SORT_ORDER, inputRecord.getFieldValue(RequestIds.ORG_SORT_ORDER, null));
                orgRecord.setFieldValue(RequestIds.ORG_ROW_ID, inputRecord.getFieldValue(RequestIds.ORG_ROW_ID, null));
                UserSessionManager.getInstance().getUserSession(request).set(RequestIds.BACK_TO_LIST_ORG_INFO, orgRecord);
            }

            PolicyFields.setHasRisk(inputRecord, YesNoFlag.getInstance(hasRisk));
            // Check for validation record data
            output = (Record) request.getAttribute(RequestIds.GRID_RECORD_SET);
            boolean isNewPolicyCreated = YesNoFlag.getInstance((String)request.getParameter(RequestIds.IS_NEW_POLICY_CREATED)).booleanValue();
            if (output == null) {
                // Load the Policy Data
                output = getPolicyManager().loadPolicyDetail(policyHeader, inputRecord);
                // If it is a newly created policy, system tries to save default values defined in WebWB firstly.
                if (isNewPolicyCreated) {
                    Record defaultValues = getPolicyManager().getWorkbenchDefaultValues();
                    output.setFields(defaultValues);
                    output.setNullFieldsToEmpty();
                    ((PolicySaveProcessor) getPolicyManager()).savePolicy(policyHeader, output);
                    // Reload the policy data.
                    output = getPolicyManager().loadPolicyDetail(policyHeader, getInputRecord(request));
                }
            }
            RecordSet rs = new RecordSet();
            // Prepare component owner
            ComponentOwner owner = ComponentOwner.POLICY;
            // Load the components
            RecordSet compRs = (RecordSet) request.getAttribute(COMP_GRID_RECORD_SET);
            if (compRs == null) {
                ComponentRowStyleRecordLoadprocessor compRowStyleLp = new ComponentRowStyleRecordLoadprocessor();
                compRs = getComponentManager().loadAllComponent(policyHeader, getInputRecord(request), owner, rs, compRowStyleLp);
            }

            // Set loaded Component data into request
            setDataBean(request, compRs);
            output.setFields(compRs.getSummaryRecord(), false);
            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);

            if (isNewPolicyCreated) {
                request.setAttribute(RequestIds.IS_NEW_POLICY_CREATED, "Y");
            }

            // Load the list of values after loading the data
            loadListOfValues(request, form);

            // Load grid header bean
            loadGridHeader(request);
            request.setAttribute("recordExistResult",getRecordExistsManager().retrieveRecordExistsIndicator("PM_POLICY","PMS",policyHeader.toRecord()));
            // Add js messages to messagemanager for the current request
            addJsMessages();

            // Add messages for save purpose
            addSaveMessages(policyHeader, request);

            saveToken(request);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the policy page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadPolicyDetail", af);
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
    public ActionForward verifyPolicyNo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "verifyPolicyNo", new Object[]{mapping, form, request, response});

        String forwardString = null;

        try {
            String modifiedPolicyNo = request.getParameter("modifiedPolicyNo");
            String policyId = request.getParameter("policyId");

            try {
                // Validation policy number change
                getPolicyManager().validateModifiedPolicyNo(modifiedPolicyNo, policyId);

                writeEmptyAjaxXMLResponse(response);
            }
            catch (ValidationException e) {
                handleValidationExceptionForAjax(e, response);
            }
        }
        catch (Exception e) {
            l.exiting(getClass().getName(), "verifyPolicyNo", e);
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to verify policy no edit.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "verifyPolicyNo", af);
        return af;
    }

    /**
     * Save updated policy record.
     */
    public ActionForward savePolicy(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "savePolicy", new Object[]{mapping, form, request, response});

        String forwardString = "saveResult";
        ActionForward af;
        Record inputRecord = null;
        RecordSet componentInputRecords = null;
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {

                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);

                // Map request to record for input
                inputRecord = getInputRecord(request);
                // Map component textXML to RecordSet for input
                componentInputRecords = getInputRecordSet(request);
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "savePolicy", "Saving the policy inputRecord: " + inputRecord);
                }

                // Save the changes
                PolicyHeader policyHeader = getPolicyHeader(request);
                // Prepare component owner
                ComponentOwner owner = ComponentOwner.POLICY;
                // Save the component changes
                RecordSet ownRecordSet = new RecordSet();
                getComponentManager().saveAllComponent(policyHeader, componentInputRecords, owner, ownRecordSet);
                // Save policy changes
                getPolicyManager().processSavePolicy(policyHeader, inputRecord);
                // In case the policyNo changed by user input, redirect the page and pass the changed policyNo
                String policyNo = request.getParameter(RequestIds.POLICY_NO);
                String changedPolicyNo = inputRecord.getStringValue("policyNoEdit");
                if (!StringUtils.isBlank(changedPolicyNo) && !changedPolicyNo.equals(policyNo)) {
                    forwardString = "saveResultRedirect";
                    setForwardParameter(request,"policyNo",changedPolicyNo);
                }

                //Update the Cache in session and request
                policyHeader.setReloadCode(PolicyHeaderReloadCode.CURRENT_TERM);
                RequestStorageManager.getInstance().set(RequestStorageIds.POLICY_HEADER, policyHeader);
                UserSessionManager.getInstance().getUserSession().set(UserSessionIds.POLICY_HEADER, policyHeader);

                // Reload policy header due to policy data is changed.
                reloadPolicyHeader(request);
            }
        }
        catch (ValidationException ve) {
            // Save the input records into request
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecord);

            // Handle the validation exception
            handleValidationException(ve, request);

            // Save the input records into request
            request.setAttribute(COMP_GRID_RECORD_SET, componentInputRecords);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the policy page.", e, request, mapping);
        }

        af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "savePolicy", af);
        return af;
    }

    /**
     * Auto save updated policy record.
     */
    public ActionForward autoSavePolicy(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "autoSavePolicy", new Object[]{mapping, form, request, response});

        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);

                // Map request to record for input
                Record inputRecord = getInputRecord(request);
                // Map component textXML to RecordSet for input
                RecordSet componentInputRecords = getInputRecordSet(request);
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "autoSavePolicy", "Saving the policy inputRecord: " + inputRecord);
                }

                // Save the changes
                PolicyHeader policyHeader = getPolicyHeader(request);
                getPolicyManager().processAutoSavePolicyWIP(policyHeader, inputRecord);
                // Prepare component owner
                ComponentOwner owner = ComponentOwner.POLICY;
                // Save the component changes
                RecordSet ownRecordSet = new RecordSet();
                getComponentManager().saveAllComponent(policyHeader, componentInputRecords, owner, ownRecordSet);

                writeEmptyAjaxXMLResponse(response);

                //Update the Cache in session and request
                policyHeader.setReloadCode(PolicyHeaderReloadCode.CURRENT_TERM);
                RequestStorageManager.getInstance().set(RequestStorageIds.POLICY_HEADER, policyHeader);
                UserSessionManager.getInstance().getUserSession().set(UserSessionIds.POLICY_HEADER, policyHeader);

                // Reload policy header due to policy data is changed.
                reloadPolicyHeader(request);
            }
        }
        catch (ValidationException ve) {
            writeEmptyAjaxXMLResponse(response);
        }
        catch (Exception e) {
            handleErrorForAutoSaveAjax(AppException.UNEXPECTED_ERROR, "Failed to auto save the policy page.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "autoSavePolicy", af);
        return af;
    }

    /**
     * Method to delete the OOS policy detail term information.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward deleteOosPolicyDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "deleteOosPolicyDetail", new Object[]{mapping, form, request, response});

        String forwardString = "saveResult";

        try {
            if (hasValidSaveToken(request)) {
                // Get the policy header from the request
                PolicyHeader policyHeader = getPolicyHeader(request);

                // Process the deletion
                getPolicyManager().deleteOosPolicyDetail(policyHeader, getInputRecord(request));
            }
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to delete oose detail for the policy page.", e, request, mapping);
        }

        // Forward back to the load
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "deleteOosPolicyDetail", af);
        return af;
    }

    /**
     * Method to check if the policy notes exist.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward isPolicyNotesExist(ActionMapping mapping, ActionForm form,
                                            HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "isPolicyNotesExist", new Object[]{mapping, form, request, response});

        try {
            // Secure page without load fields
            securePage(request, form, false);

            // Map request to record for input
            Record inputRecord = getInputRecord(request);
            inputRecord.setFieldValue("id", PolicyHeaderFields.getPolicyId(inputRecord));
            inputRecord.setFieldValue("table", "NOTE");

            // Get the additional info data
            YesNoFlag isEditable = getPolicyManager().isRecordExist(inputRecord);
            Record output = new Record();
            output.setFieldValue("isPolicyNotesExist", isEditable);

            // Send back xml data
            writeAjaxXmlResponse(response, output);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR,
                "Failed to get policy notes information.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "isPolicyNotesExist", af);
        return af;
    }

        public ActionForward isPolicySpecialHandlingExist(ActionMapping mapping, ActionForm form,
                                                HttpServletRequest request, HttpServletResponse response) throws Exception {
            Logger l = LogUtils.enterLog(getClass(), "isPolicySpecialHandlingExist", new Object[]{mapping, form, request, response});
            try {
                // Secure page without load fields
                securePage(request, form, false);

                // Map request to record for input
                Record inputRecord = getInputRecord(request);
                inputRecord.setFieldValue("id", PolicyHeaderFields.getPolicyId(inputRecord));
                inputRecord.setFieldValue("table", "SPECIAL_HANDLING");

                // Get the additional info data
                YesNoFlag isEditable = getPolicyManager().isRecordExist(inputRecord);
                Record output = new Record();
                output.setFieldValue("isPolicySpecialHandlingExist", isEditable);
                // Send back xml data
                writeAjaxXmlResponse(response, output);
            }
            catch (Exception e) {
                handleErrorForAjax(AppException.UNEXPECTED_ERROR,
                    "Failed to get special handling information.", e, response);
            }

            ActionForward af = null;
            l.exiting(getClass().getName(), "isPolicySpecialHandlingExist", af);
            return af;
        }

    /**
     * this method handles copy policy to quote
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward copyPolicyToQuote(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "copyPolicyToQuote", new Object[]{mapping, form, request, response});
        try {
            if (hasValidSaveToken(request)) {
                // Secures access to the page
                securePage(request, form, false);

                PolicyHeader policyHeader = getPolicyHeader(request);                

                // Map request to record for input
                Record inputRecord = getInputRecord(request);

                //quoteManger handle copy policy to quote process
                Record outputRecord = getPolicyManager().copyPolicyToQuote(policyHeader, inputRecord);

                writeAjaxXmlResponse(response, outputRecord);
            }
        }
        catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            handleErrorForAjax("pm.maintainPolicy.copyPolicyToQuote.error", "Failed to copy policy to quote.", e, response);
        }
        l.exiting(getClass().getName(), "copyPolicyToQuote", null);
        return null;
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.maintainRenewal.confirm.applyPRT");
        MessageManager.getInstance().addJsMessage("pm.maintainReinstate.confirm.continue");
        MessageManager.getInstance().addJsMessage("pm.maintainPolicy.copyPolicyToQuote.error");
        MessageManager.getInstance().addJsMessage("pm.deleteEndQuote.confirmed.warning");
        MessageManager.getInstance().addJsMessage("pm.applyEndQuote.confirmed.warning");
        MessageManager.getInstance().addJsMessage("pm.maintainQuote.copyPolicyToQuote.convert.confirmation");
        // Component messages
        MessageManager.getInstance().addJsMessage("pm.maintainComponent.effectiveToDate.rule1.error");
        MessageManager.getInstance().addJsMessage("pm.addCoverageClass.missingCoverage.error");
        MessageManager.getInstance().addJsMessage("pm.addComponent.noCoverage.error");
        MessageManager.getInstance().addJsMessage("pm.maintainReinstate.confirm.continue");
        MessageManager.getInstance().addJsMessage("pm.addComponent.duplicated.error");
        MessageManager.getInstance().addJsMessage("pm.deleteEndQuote.confirmed.warning");
        MessageManager.getInstance().addJsMessage("pm.applyEndQuote.confirmed.warning");
        // auto renewal
        MessageManager.getInstance().addJsMessage("pm.autoRenewal.confirmation.info");
        MessageManager.getInstance().addJsMessage("pm.oose.modified.record.exist.error");
        MessageManager.getInstance().addJsMessage("pm.oose.modified.record.exist.error2");
        // cancel
        MessageManager.getInstance().addJsMessage("pm.maintainCancellation.cancellationNotPermitted.error");
        //undo term
        MessageManager.getInstance().addJsMessage("pm.undoTerm.confirm.continue");
        // For View Claim Summary
        MessageManager.getInstance().addJsMessage("pm.viewClaimsSummary.risk.select.error");
        MessageManager.getInstance().addJsMessage("pm.maintainPolicy.clickOk.changesSave.confirm");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    /**
     * Copy Address
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward isPolicyEntity(ActionMapping mapping,
                                     ActionForm form,
                                     HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isPolicyEntity", new Object[]{mapping, form, request, response});
        }

        try {
            securePage(request, form);
            Record inputRecord = getInputRecord(request);
            String outputRecord = getPolicyManager().isPolicyEntity(inputRecord);
            PrintWriter wri = response.getWriter();
            response.setContentType("text/html");
            wri.write(outputRecord.toString());
            wri.write(inputRecord.getStringValue(EntityFields.ENTITY_ID));
            wri.flush();
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to validate policy entity.", e, response);
        }
        ActionForward af = null;
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isPolicyEntity", af);
        }
        return af;
    }

    public MaintainPolicyAction() {
    }

     /**
     * Verify RenewalQuestionnaireManager and anchorColumnName in spring config
     */
    public void verifyConfig() {
        if (getComponentManager() == null)
            throw new ConfigurationException("The required property 'componentManager' is missing.");
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
    }

    public ComponentManager getComponentManager() {
        return m_componentManager;
    }

    public void setComponentManager(ComponentManager componentManager) {
        m_componentManager = componentManager;
    }

    private ComponentManager m_componentManager;
    protected static final String COMP_GRID_RECORD_SET = "compGridRecordSet";

    public RecordExistsManager getRecordExistsManager() {
        return m_recordExistsManager;
    }

    public void setRecordExistsManager(RecordExistsManager recordExistsManager) {
        this.m_recordExistsManager = recordExistsManager;
    }

    private RecordExistsManager m_recordExistsManager;
}
