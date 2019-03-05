package dti.ci.summarymgr.struts;

import com.delphi_tech.velocity.entityroleinquiryservice.EntityRoleInquiryResultType;
import com.delphi_tech.velocity.entityroleinquiryservice.PolicyTermType;
import com.delphi_tech.velocity.entityroleinquiryservice.PolicyType;
import dti.ci.core.struts.MaintainEntityFolderBaseAction;
import dti.ci.summarymgr.SummaryManager;
import dti.ci.entitysearch.EntitySearchFields;
import dti.cs.securitymgr.AccessControlFilterManager;
import dti.cs.velocitypolicymgr.VelocityPolicyManager;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageIds;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.SysParmProvider;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * User: cyzhao
 * Date: Jun 16, 2008
 */
/*
 * CIS Summary Web layer Control Object
 * Revision Date    Revised By  Description
 * ---------------------------------------------------------------------------
 * 01/12/2009       Fred        Load record set if it is null and ignore
 *                              the syspram value (iss101957)
 *  09/28/2011      Michael     for issue 125283
 * 03/06/2012       Parker      130270. set CIS notes visiable for this business.
 * 07/09/2015       ylu         163827: look the getPolicyListFromEntityList flag to
 *                              determine the version with the role and retro date
 * 08/26/2016       Elvin       Issue 177515: include velocity policy
 * 01/12/2017       Elvin       Issue 182136: Velocity Integration
 * 06/29/2018       ylu         Issue 194117: update for CSRF security.
 * 10/26/2018       Elvin       Issue 195835: grid replacement
 * ---------------------------------------------------------------------------
 */
public class MaintainSummaryAction extends MaintainEntityFolderBaseAction {

    private final Logger l = LogUtils.getLogger(getClass());

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
        return loadAllSummary(mapping, form, request, response);
    }

    /**
     * Load Policy/Quote Grid List, Account Grid List and Claims Grid List
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllSummary(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllSummary", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadCISummaryResult";
        // Added by Jacky 12-10-2008
        String loadOnlyPolicyResult = request.getParameter(EntitySearchFields.ENT_ID_FOR_POLICY_NO_PROPERTY);
        forwardString = null == loadOnlyPolicyResult || "".equals(loadOnlyPolicyResult) ? forwardString : "loadOnlyPolicyResult";
        
        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);
            String entityId = getEntityIdForMaintainEntityAction(request);
            inputRecord.setFieldValue("entityId", entityId);

            // If the value of "CS_SUMMARY_MINIPOL" is not "Y", we do not retrieve and display MiniPolicy Information.
            String retrieveCombinedPolicyInfoStr = SysParmProvider.getInstance().getSysParm("CS_SUMMARY_MINIPOL", "N");
            boolean retrieveCombinedPolicyInfoB = retrieveCombinedPolicyInfoStr.equals("Y");
            request.setAttribute("isPolicyCombined", retrieveCombinedPolicyInfoStr);

            String showClaim = SysParmProvider.getInstance().getSysParm("CS_SUMMARY_SHOWCLAIM", "Y");
            request.setAttribute("showClaim",showClaim);

            String termConfiguration = SysParmProvider.getInstance().getSysParm("CS_SUMPOLLIST_TRM", "LAST");
            request.setAttribute("getAllTerms", YesNoFlag.getInstance(!termConfiguration.equalsIgnoreCase("LAST")));

            // Get Policy/Quote RecordSet from request
            RecordSet rsPolandQte = (RecordSet) request.getAttribute(SUMMARY_POLQTE_GRID_RECORD_SET);
            // Get Combined Policy/Quote RecordSet from request
            RecordSet rsCombinedPolandQte = (RecordSet) request.getAttribute(SUMMARY_COMBINED_POLQTE_GRID_RECORD_SET);
            // Get Combined Risk RecordSet from request
            RecordSet rsCombinedRisk = (RecordSet) request.getAttribute(SUMMARY_COMBINED_RISK_GRID_RECORD_SET);
            // Get Account RecordSet from request
            RecordSet rsAccount = (RecordSet) request.getAttribute(SUMMARY_ACCOUNT_GRID_RECORD_SET);
            // Get Claims RecordSet from request
            RecordSet rsClaims = (RecordSet) request.getAttribute(SUMMARY_CLAIMS_GRID_RECORD_SET);

            if (rsPolandQte == null) {
                if (request.getAttribute("getPolicyListFromEntityList") != null) {
                    inputRecord.setFieldValue("getPolicyListFromEntityList", request.getAttribute("getPolicyListFromEntityList"));
                }
                // Get Policy/Quote RecordSet from BO if it isn't in request
                rsPolandQte = getSummaryManager().loadAllPolandQteByEntity(inputRecord);
            }

            if (rsCombinedPolandQte == null) {
                // Get Combined Policy/Quote RecordSet from BO if it isn't in request
                rsCombinedPolandQte = getSummaryManager().loadCombinedPolandQteByEntity(inputRecord);

                // load velocity polices
                EntityRoleInquiryResultType wsResponse = getVelocityPolicyManager().getVelocityPolicies(entityId);
                if (wsResponse != null) {
                    RecordSet velocityPolicies = parseResultToRecordSet(wsResponse, rsCombinedPolandQte.getFieldNameList());
                    if (velocityPolicies != null && velocityPolicies.getSize() > 0) {
                        rsCombinedPolandQte.addRecords(velocityPolicies);
                    }
                }
            }

            if (rsCombinedRisk == null) {
                // Get Combined Risk RecordSet from BO if it isn't in request
                rsCombinedRisk = getSummaryManager().loadCombinedRiskByEntity(inputRecord);
            }            

            if (rsAccount == null) {
                // Get Account RecordSet from BO if it isn't in request
                rsAccount = getSummaryManager().loadAllAccountsByEntity(inputRecord);
            }

            if (rsClaims == null) {
                // Get Claims RecordSet from BO if it isn't in request
                rsClaims = getSummaryManager().loadAllClaimsByEntity(inputRecord);
            }

            // 154954, Claim Restrict Filter
            rsClaims = getAccessControlFilterManager().filterRecordSetViaAccessControl(request, rsClaims, "", "claimNo");

            // Set loaded data into request
            setDataBean(request, rsPolandQte, SUMMARY_POLQTE_GRID_ID);
            setDataBean(request, rsCombinedPolandQte, SUMMARY_COMBINED_POLQTE_GRID_ID);
            setDataBean(request, rsCombinedRisk, SUMMARY_COMBINED_RISK_GRID_ID);
            setDataBean(request, rsAccount, SUMMARY_ACCOUNT_GRID_ID);
            setDataBean(request, rsClaims, SUMMARY_CLAIMS_GRID_ID);

            // Make the Summary Record and original input record available for output
            Record output = null;
            if (!retrieveCombinedPolicyInfoB) {
                output = rsPolandQte.getSummaryRecord();
            } else {
                output = rsCombinedPolandQte.getSummaryRecord();
               // output.setFields(rsCombinedPolandQte.getSummaryRecord(), false);
                output.setFields(rsCombinedRisk.getSummaryRecord(), false);
            }
            output.setFields(rsAccount.getSummaryRecord(), false);
            output.setFields(rsClaims.getSummaryRecord(), false);
            output.setFields(inputRecord, false);

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);
            
            // Set currentGridId to every gridID on page before load gird header
            // then load grid header for each grid
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, SUMMARY_POLQTE_GRID_ID);
            loadGridHeader(request, null, SUMMARY_POLQTE_GRID_ID, SUMMARY_POLQTE_GRID_LAYER_ID);

            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, SUMMARY_COMBINED_POLQTE_GRID_ID);
            loadGridHeader(request, null, SUMMARY_COMBINED_POLQTE_GRID_ID, SUMMARY_COMBINED_POLQTE_GRID_LAYER_ID);

            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, SUMMARY_COMBINED_RISK_GRID_ID);
            loadGridHeader(request, null, SUMMARY_COMBINED_RISK_GRID_ID, SUMMARY_COMBINED_RISK_GRID_LAYER_ID);

            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, SUMMARY_ACCOUNT_GRID_ID);
            loadGridHeader(request, null, SUMMARY_ACCOUNT_GRID_ID, SUMMARY_ACCOUNT_GRID_LAYER_ID);

            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, SUMMARY_CLAIMS_GRID_ID);
            loadGridHeader(request, null, SUMMARY_CLAIMS_GRID_ID, SUMMARY_CLAIMS_GRID_LAYER_ID);

            // Load the list of values after loading the data
            loadListOfValues(request, form);

            // set js messages
            addJsMessages();

            saveToken(request);
        } catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the summary information.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllSummary", af);
        }
        return af;
    }

    private RecordSet parseResultToRecordSet(EntityRoleInquiryResultType entityRoleInquiryResult, List<String> fieldNameList) {
        RecordSet recordSet = new RecordSet();
        if (entityRoleInquiryResult != null && entityRoleInquiryResult.getPolicy().size() > 0) {
            List<PolicyType> velocityPolicyList = entityRoleInquiryResult.getPolicy();
            for (int i = 0; i < velocityPolicyList.size(); i++) {
                Record record = new Record();
                for (String fieldName : fieldNameList) {
                    record.setFieldValue(fieldName, "");
                }

                PolicyType velocityPolicy = velocityPolicyList.get(i);
                record.setFieldValue("combinedpolicysourcetable", "VELOCITY_POLICY");
                record.setFieldValue("combinedPolicyId", "velocity_" + velocityPolicy.getPolicyNumberId());
                record.setFieldValue("combinedPolicyNo", "Velocity Policy# " + velocityPolicy.getPolicyId());

                if (velocityPolicy.getPolicyTerm().size() > 0) {
                    Iterator<PolicyTermType> iterator = velocityPolicy.getPolicyTerm().iterator();
                    while (iterator.hasNext()) {
                        PolicyTermType policyTerm = iterator.next();
                        if (policyTerm.getPolicyTermNumberId().equals(velocityPolicy.getPolicyTermNumberId())) {
                            record.setFieldValue("combinedPolicyExpFrom", DateUtils.parseXMLDateToOasisDate(policyTerm.getContractPeriod().getStartDate()));
                            record.setFieldValue("combinedPolicyExpTo", DateUtils.parseXMLDateToOasisDate(policyTerm.getContractPeriod().getEndDate()));
                        }
                    }
                }

                record.setFieldValue("combinedPolicyType", velocityPolicy.getPolicyType());
                record.setFieldValue("combinedPolicyStatus", velocityPolicy.getStatus());
                recordSet.addRecord(record);
            }
        }
        return recordSet;
    }

    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("ci.entity.message.source.noAvailable");
        MessageManager.getInstance().addJsMessage("ci.entity.message.filterBy.invalid");
        MessageManager.getInstance().addJsMessage("ci.claim.restrict.message.noAuthority.claim");
        MessageManager.getInstance().addJsMessage("ci.claim.restrict.message.noAuthority.case");
    }

    /**
     * Load Billing Grid List through selected Account and Policy/Quote
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllAccountBillings(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAccountBillings", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadAllAccountBillings";
        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);

            // Get Account&Policy billings RecordSet from BO
            RecordSet rs = getSummaryManager().performAllBillingsByAccountAndPolicy(inputRecord);

            // Set loaded change detail data into request
            RequestStorageManager.getInstance().set(RequestStorageIds.CURRENT_GRID_ID, SUMMARY_BILLING_GRID_ID);
            setDataBean(request, rs);

            // Make the Summary Record and original input record available for output
            Record output = rs.getSummaryRecord();
            output.setFields(inputRecord, false);

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);

            // Set currentGridId to gridID on page before load gird header
            // then load grid header
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, SUMMARY_BILLING_GRID_ID);
            loadGridHeader(request, null, SUMMARY_BILLING_GRID_ID, SUMMARY_BILLING_GRID_LAYER_ID);

            // Load the list of values after loading the data
            loadListOfValues(request, form);

            saveToken(request);
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the account billings.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAccountBillings", af);
        }
        return af;
    }

    public void verifyConfig() {
        super.verifyConfig();
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getSummaryManager() == null)
            throw new ConfigurationException("The required property 'summaryManager' is missing.");
    }

    /**
     * Overrides method in BaseAction. To get the anchor column name by current grid Id.
     * This is effective only for the multiple grids.
     *
     * @return anchor column name for the current grid
     */
    public String getAnchorColumnName() {
        l.entering(getClass().getName(), "getAnchorColumnName");

        String anchorName;
        if (RequestStorageManager.getInstance().has(CURRENT_GRID_ID)) {
            String currentGridId = (String) RequestStorageManager.getInstance().get(CURRENT_GRID_ID);
            if (currentGridId.equals(SUMMARY_POLQTE_GRID_ID)) {
                anchorName = getPolQteAnchorColumnName();
            } else if (currentGridId.equals(SUMMARY_COMBINED_POLQTE_GRID_ID)) {
                anchorName = getCombinedPolQteAnchorColumnName();
            } else if (currentGridId.equals(SUMMARY_COMBINED_RISK_GRID_ID)) {
                anchorName = getCombinedRiskAnchorColumnName();
            } else if (currentGridId.equals(SUMMARY_ACCOUNT_GRID_ID)) {
                anchorName = getAccountAnchorColumnName();
            } else if (currentGridId.equals(SUMMARY_BILLING_GRID_ID)) {
                anchorName = getBillingAnchorColumnName();
            } else if (currentGridId.equals(SUMMARY_CLAIMS_GRID_ID)) {
                anchorName = getClaimsAnchorColumnName();
            } else
                anchorName = super.getAnchorColumnName();
        } else {
            anchorName = super.getAnchorColumnName();
        }

        l.exiting(getClass().getName(), "getAnchorColumnName", anchorName);
        return anchorName;
    }

    public SummaryManager getSummaryManager() {
        return summaryManager;
    }

    public void setSummaryManager(SummaryManager summaryManager) {
        this.summaryManager = summaryManager;
    }

    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return m_workbenchConfiguration;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration) {
        m_workbenchConfiguration = workbenchConfiguration;
    }

    public String getPolQteAnchorColumnName() {
        return polQteAnchorColumnName;
    }

    public void setPolQteAnchorColumnName(String polQteAnchorColumnName) {
        this.polQteAnchorColumnName = polQteAnchorColumnName;
    }

    public String getCombinedPolQteAnchorColumnName() {
        return combinedPolQteAnchorColumnName;
    }

    public void setCombinedPolQteAnchorColumnName(String combinedPolQteAnchorColumnName) {
        this.combinedPolQteAnchorColumnName = combinedPolQteAnchorColumnName;
    }

    public String getCombinedRiskAnchorColumnName() {
        return combinedRiskAnchorColumnName;
    }

    public void setCombinedRiskAnchorColumnName(String combinedRiskAnchorColumnName) {
        this.combinedRiskAnchorColumnName = combinedRiskAnchorColumnName;
    }

    public String getAccountAnchorColumnName() {
        return accountAnchorColumnName;
    }

    public void setAccountAnchorColumnName(String accountAnchorColumnName) {
        this.accountAnchorColumnName = accountAnchorColumnName;
    }

    public String getBillingAnchorColumnName() {
        return billingAnchorColumnName;
    }

    public void setBillingAnchorColumnName(String billingAnchorColumnName) {
        this.billingAnchorColumnName = billingAnchorColumnName;
    }

    public String getClaimsAnchorColumnName() {
        return claimsAnchorColumnName;
    }

    public void setClaimsAnchorColumnName(String claimsAnchorColumnName) {
        this.claimsAnchorColumnName = claimsAnchorColumnName;
    }

    public AccessControlFilterManager getAccessControlFilterManager() {
        return accessControlFilterManager;
    }

    public void setAccessControlFilterManager(AccessControlFilterManager accessControlFilterManager) {
        this.accessControlFilterManager = accessControlFilterManager;
    }

    public VelocityPolicyManager getVelocityPolicyManager() {
        return velocityPolicyManager;
    }

    public void setVelocityPolicyManager(VelocityPolicyManager velocityPolicyManager) {
        this.velocityPolicyManager = velocityPolicyManager;
    }

    private AccessControlFilterManager accessControlFilterManager;
    private String polQteAnchorColumnName;
    private String combinedPolQteAnchorColumnName;
    private String combinedRiskAnchorColumnName;
    private String accountAnchorColumnName;
    private String billingAnchorColumnName;
    private String claimsAnchorColumnName;
    private WorkbenchConfiguration m_workbenchConfiguration;
    private SummaryManager summaryManager;
    private VelocityPolicyManager velocityPolicyManager;
    protected static final String CURRENT_GRID_ID = "currentGridId";
    protected static final String SUMMARY_POLQTE_GRID_ID = "polQteListGrid";
    protected static final String SUMMARY_COMBINED_POLQTE_GRID_ID = "combinedPolQteListGrid";
    protected static final String SUMMARY_COMBINED_RISK_GRID_ID = "combinedRiskListGrid";
    protected static final String SUMMARY_ACCOUNT_GRID_ID = "accountListGrid";
    protected static final String SUMMARY_BILLING_GRID_ID = "billingListGrid";
    protected static final String SUMMARY_CLAIMS_GRID_ID = "claimsListGrid";
    protected static final String SUMMARY_POLQTE_GRID_LAYER_ID = "CI_SUMMARY_POLQTE_GH";
    protected static final String SUMMARY_COMBINED_POLQTE_GRID_LAYER_ID = "CI_SUMMARY_COMBINED_POLQTE_GH";
    protected static final String SUMMARY_COMBINED_RISK_GRID_LAYER_ID = "CI_SUMMARY_COMBINED_RISK_GH";
    protected static final String SUMMARY_ACCOUNT_GRID_LAYER_ID = "CI_SUMMARY_ACCOUNT_GH";
    protected static final String SUMMARY_BILLING_GRID_LAYER_ID = "CI_SUMMARY_BILLING_GH";
    protected static final String SUMMARY_CLAIMS_GRID_LAYER_ID = "CI_SUMMARY_CLAIMS_GH";
    protected static final String SUMMARY_POLQTE_GRID_RECORD_SET = "polQteListGridRecordSet";
    protected static final String SUMMARY_COMBINED_POLQTE_GRID_RECORD_SET = "combinedPolQteListGridRecordSet";
    protected static final String SUMMARY_COMBINED_RISK_GRID_RECORD_SET = "combinedRiskListGridRecordSet";
    protected static final String SUMMARY_ACCOUNT_GRID_RECORD_SET = "accountListGridRecordSet";
    protected static final String SUMMARY_BILLING_GRID_RECORD_SET = "billingListGridRecordSet";
    protected static final String SUMMARY_CLAIMS_GRID_RECORD_SET = "claimsListGridRecordSet";

}
