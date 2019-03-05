package dti.ci.entityglancemgr.struts;


import dti.ci.core.struts.MaintainEntityFolderBaseAction;
import dti.ci.entityglancemgr.EntityGlanceFields;
import dti.ci.entityglancemgr.EntityGlanceManager;
import dti.cs.securitymgr.AccessControlFilterManager;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.util.LogUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * User: Michael
 * Date: September 08, 2011
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/23/2018       ylu         Issue 193481
 * ---------------------------------------------------
 */
public class MaintainEntityGlanceAction extends MaintainEntityFolderBaseAction {
    private final Logger l = LogUtils.getLogger(getClass());

    /**
     * Unspecified
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

        return loadAllAvailableEntityGlance(mapping, form, request, response);
    }

    /**
     * Load all entityGlance
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllAvailableEntityGlance(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                 HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAvailableEntityGlance", new Object[]{mapping,form,request,response});
        }

        String forwardString = "loadResult";


        try {
            securePage(request, form);
            String pk = request.getParameter(EntityGlanceFields.PK_PROPERTY);
            Record inputRecord = getInputRecord(request);
            inputRecord.setFieldValue("entityId", pk);
            Record output = getEntityGlanceManager().loadEntityDemographic(inputRecord);

            // Publish the output record for use by the Oasis Tags and JSP.
            publishOutputRecord(request, output);
            String clientId = output.getStringValue(EntityGlanceFields.CLIENT_ID);
            //setEntityCommonInfoToRequest
            request.setAttribute("displayClientId", clientId);
            // Load LOV
            loadListOfValues(request, form);
            RecordSet rs = null;

            // Set currentGridId to relationshipGrid before load component grid header
            RequestStorageManager.getInstance().set(MaintainEntityGlanceAction.CURRENT_GRID_ID, MaintainEntityGlanceAction.GLANCE_RELATIONSHIP_GRID_ID);
            loadGridHeader(request, null, MaintainEntityGlanceAction.GLANCE_RELATIONSHIP_GRID_ID, MaintainEntityGlanceAction.GLANCE_RELATIONSHIP_GRID_LAYER_ID);
            rs = getEntityGlanceManager().loadRelationships(inputRecord);
            setDataBean(request, rs, MaintainEntityGlanceAction.GLANCE_RELATIONSHIP_GRID_ID);
            //outputRecord = rs.getSummaryRecord();
            //publishOutputRecord(request, outputRecord);

            // Set currentGridId to relationshipGrid before load component grid header
            RequestStorageManager.getInstance().set(MaintainEntityGlanceAction.CURRENT_GRID_ID, MaintainEntityGlanceAction.GLANCE_CLAIMS_GRID_ID);
            loadGridHeader(request, null, MaintainEntityGlanceAction.GLANCE_CLAIMS_GRID_ID, MaintainEntityGlanceAction.GLANCE_CLAIMS_GRID_LAYER_ID);
            rs = getEntityGlanceManager().loadClaims(inputRecord);
            //154954,Claim Restrict Filter
            rs = getAccessControlFilterManager().filterRecordSetViaAccessControl(request, rs, "", "claimNo");
            setDataBean(request, rs, MaintainEntityGlanceAction.GLANCE_CLAIMS_GRID_ID);

            // Set currentGridId to relationshipGrid before load component grid header
            RequestStorageManager.getInstance().set(MaintainEntityGlanceAction.CURRENT_GRID_ID, MaintainEntityGlanceAction.GLANCE_PARTICIPANT_GRID_ID);
            loadGridHeader(request, null, MaintainEntityGlanceAction.GLANCE_PARTICIPANT_GRID_ID, MaintainEntityGlanceAction.GLANCE_PARTICIPANT_GRID_LAYER_ID);
            rs = getEntityGlanceManager().loadParticipants(inputRecord);
            setDataBean(request, rs, MaintainEntityGlanceAction.GLANCE_PARTICIPANT_GRID_ID);

            // Set currentGridId to policyGrid before load component grid header
            RequestStorageManager.getInstance().set(MaintainEntityGlanceAction.CURRENT_GRID_ID, MaintainEntityGlanceAction.GLANCE_POLICY_GRID_ID);
            loadGridHeader(request, null, MaintainEntityGlanceAction.GLANCE_POLICY_GRID_ID, MaintainEntityGlanceAction.GLANCE_POLICY_GRID_LAYER_ID);
            rs = getEntityGlanceManager().loadPolicyQuote(inputRecord);
            setDataBean(request, rs, MaintainEntityGlanceAction.GLANCE_POLICY_GRID_ID);

            // Set currentGridId to transactionGrid before load component grid header
            RequestStorageManager.getInstance().set(MaintainEntityGlanceAction.CURRENT_GRID_ID, MaintainEntityGlanceAction.GLANCE_TRANSACTION_GRID_ID);
            loadGridHeader(request, null, MaintainEntityGlanceAction.GLANCE_TRANSACTION_GRID_ID, MaintainEntityGlanceAction.GLANCE_TRANSACTION_GRID_LAYER_ID);
            rs = getEntityGlanceManager().loadTransactions(inputRecord);
            setDataBean(request, rs, MaintainEntityGlanceAction.GLANCE_TRANSACTION_GRID_ID);

            // Set currentGridId to transactionFormGrid before load component grid header
            RequestStorageManager.getInstance().set(MaintainEntityGlanceAction.CURRENT_GRID_ID, MaintainEntityGlanceAction.GLANCE_TRANSACTION_FORM_GRID_ID);
            loadGridHeader(request, null, MaintainEntityGlanceAction.GLANCE_TRANSACTION_FORM_GRID_ID, MaintainEntityGlanceAction.GLANCE_TRANSACTION_FORM_GRID_LAYER_ID);
            rs = getEntityGlanceManager().loadTransactionForms(inputRecord);
            setDataBean(request, rs, MaintainEntityGlanceAction.GLANCE_TRANSACTION_FORM_GRID_ID);

            // Set currentGridId to financialGrid before load component grid header
            RequestStorageManager.getInstance().set(MaintainEntityGlanceAction.CURRENT_GRID_ID, MaintainEntityGlanceAction.GLANCE_FINANCIAL_GRID_ID);
            loadGridHeader(request, null, MaintainEntityGlanceAction.GLANCE_FINANCIAL_GRID_ID, MaintainEntityGlanceAction.GLANCE_FINANCIAL_GRID_LAYER_ID);
            rs = getEntityGlanceManager().loadFinances(inputRecord);
            setDataBean(request, rs, MaintainEntityGlanceAction.GLANCE_FINANCIAL_GRID_ID);

            // Set currentGridId to financialFormGrid before load component grid header
            RequestStorageManager.getInstance().set(MaintainEntityGlanceAction.CURRENT_GRID_ID, MaintainEntityGlanceAction.GLANCE_FINANCIAL_FORM_GRID_ID);
            loadGridHeader(request, null, MaintainEntityGlanceAction.GLANCE_FINANCIAL_FORM_GRID_ID, MaintainEntityGlanceAction.GLANCE_FINANCIAL_FORM_GRID_LAYER_ID);
            rs = getEntityGlanceManager().loadFinanceForms(inputRecord);
            setDataBean(request, rs, MaintainEntityGlanceAction.GLANCE_FINANCIAL_FORM_GRID_ID);
            //-----------
            addJsMessages();
        }
        catch (Exception e) {
            l.throwing(getClass().getName(), "loadAllAvailableEntityGlance", e);
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the EntityGlance page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAvailableEntityGlance", af);
        }
        return af;
    }


    //add js message

    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("ci.entity.glance.form.title");
        MessageManager.getInstance().addJsMessage("ci.entity.search.label.glance");
        MessageManager.getInstance().addJsMessage("js.lose.changes.confirmation");
        MessageManager.getInstance().addJsMessage("js.refresh.lose.changes.confirmation");
        MessageManager.getInstance().addJsMessage("ci.entity.message.source.noAvailable");
        MessageManager.getInstance().addJsMessage("ci.claim.restrict.message.noAuthority.claim");
        MessageManager.getInstance().addJsMessage("ci.claim.restrict.message.noAuthority.case");
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
        if (RequestStorageManager.getInstance().has(MaintainEntityGlanceAction.CURRENT_GRID_ID)) {
            String currentGridId = (String) RequestStorageManager.getInstance().get(MaintainEntityGlanceAction.CURRENT_GRID_ID);
            if (currentGridId.equals(MaintainEntityGlanceAction.GLANCE_RELATIONSHIP_GRID_ID)) {
                anchorName = getRelationshipAnchorColumnName();
            } else if (currentGridId.equals(MaintainEntityGlanceAction.GLANCE_CLAIMS_GRID_ID)) {
                anchorName = getClaimAnchorColumnName();
            } else if (currentGridId.equals(MaintainEntityGlanceAction.GLANCE_PARTICIPANT_GRID_ID)) {
                anchorName = getParticipantAnchorColumnName();
            }else if (currentGridId.equals(MaintainEntityGlanceAction.GLANCE_POLICY_GRID_ID)) {
                anchorName = getPolicyAnchorColumnName();
            }else if (currentGridId.equals(MaintainEntityGlanceAction.GLANCE_TRANSACTION_GRID_ID)) {
                anchorName = getTransactionAnchorColumnName();
            }else if (currentGridId.equals(MaintainEntityGlanceAction.GLANCE_TRANSACTION_FORM_GRID_ID)) {
                anchorName = getTransactionFormAnchorColumnName();
            }else if (currentGridId.equals(MaintainEntityGlanceAction.GLANCE_FINANCIAL_GRID_ID)) {
                anchorName = getFinancialAnchorColumnName();
            }else if (currentGridId.equals(MaintainEntityGlanceAction.GLANCE_FINANCIAL_FORM_GRID_ID)) {
                anchorName = getFinancialFormAnchorColumnName();
            }else {
                anchorName = super.getAnchorColumnName();
            }
        } else {
            anchorName = super.getAnchorColumnName();
        }
        l.exiting(getClass().getName(), "getAnchorColumnName", anchorName);
        return anchorName;
    }
    /* Configuration constructor and accessor methods */

    public void verifyConfig() {
        if (getEntityGlanceManager() == null)
            throw new ConfigurationException("The required property 'getEntityGlanceManager' is missing.");
    }

    public EntityGlanceManager getEntityGlanceManager() {
        return entityGlanceManager;
    }

    public void setEntityGlanceManager(EntityGlanceManager entityGlanceManager) {
        this.entityGlanceManager = entityGlanceManager;
    }
    public String getRelationshipAnchorColumnName() {
        return relationshipAnchorColumnName;
    }

    public void setRelationshipAnchorColumnName(String in_relationshipAnchorColumnName) {
        relationshipAnchorColumnName = in_relationshipAnchorColumnName;
    }
    public String getClaimAnchorColumnName() {
        return claimAnchorColumnName;
    }

    public void setClaimAnchorColumnName(String in_claimAnchorColumnName) {
        claimAnchorColumnName = in_claimAnchorColumnName;
    }
    public String getParticipantAnchorColumnName() {
        return participantAnchorColumnName;
    }

    public void setParticipantAnchorColumnName(String in_participantAnchorColumnName) {
        participantAnchorColumnName = in_participantAnchorColumnName;
    }
    public String getPolicyAnchorColumnName() {
        return policyAnchorColumnName;
    }

    public void setPolicyAnchorColumnName(String in_policyAnchorColumnName) {
        policyAnchorColumnName = in_policyAnchorColumnName;
    }
    public String getTransactionAnchorColumnName() {
        return transactionAnchorColumnName;
    }

    public void setTransactionAnchorColumnName(String in_transactionAnchorColumnName) {
        transactionAnchorColumnName = in_transactionAnchorColumnName;
    }
    public String getTransactionFormAnchorColumnName() {
        return transactionFormAnchorColumnName;
    }

    public void setTransactionFormAnchorColumnName(String in_transactionFormAnchorColumnName) {
        transactionFormAnchorColumnName = in_transactionFormAnchorColumnName;
    }
    public String getFinancialAnchorColumnName() {
        return financialAnchorColumnName;
    }

    public void setFinancialAnchorColumnName(String in_financialAnchorColumnName) {
        financialAnchorColumnName = in_financialAnchorColumnName;
    }
    public String getFinancialFormAnchorColumnName() {
        return financialFormAnchorColumnName;
    }

    public void setFinancialFormAnchorColumnName(String in_financialFormAnchorColumnName) {
        financialFormAnchorColumnName = in_financialFormAnchorColumnName;
    }
    public AccessControlFilterManager getAccessControlFilterManager() {
        return accessControlFilterManager;
    }

    public void setAccessControlFilterManager(AccessControlFilterManager accessControlFilterManager) {
        this.accessControlFilterManager = accessControlFilterManager;
    }
    private AccessControlFilterManager accessControlFilterManager;
    private EntityGlanceManager entityGlanceManager;
    private String relationshipAnchorColumnName;
    private String claimAnchorColumnName;
    private String participantAnchorColumnName;
    private String policyAnchorColumnName;
    private String transactionAnchorColumnName;
    private String transactionFormAnchorColumnName;
    private String financialAnchorColumnName;
    private String financialFormAnchorColumnName;
    protected static final String CURRENT_GRID_ID = "currentGridId";
    protected static final String GLANCE_RELATIONSHIP_GRID_ID = "relationshipGrid";
    protected static final String GLANCE_CLAIMS_GRID_ID = "claimGrid";
    protected static final String GLANCE_PARTICIPANT_GRID_ID = "participantGrid";
    protected static final String GLANCE_POLICY_GRID_ID = "policyGrid";
    protected static final String GLANCE_TRANSACTION_GRID_ID = "transactionGrid";
    protected static final String GLANCE_TRANSACTION_FORM_GRID_ID = "transactionFormGrid";
    protected static final String GLANCE_FINANCIAL_GRID_ID = "financialGrid";
    protected static final String GLANCE_FINANCIAL_FORM_GRID_ID = "financialFormGrid";

    protected static final String GLANCE_RELATIONSHIP_GRID_LAYER_ID = "Entity_Glance_Relationship_Layer";
    protected static final String GLANCE_CLAIMS_GRID_LAYER_ID = "Entity_Glance_Claim_Layer";
    protected static final String GLANCE_PARTICIPANT_GRID_LAYER_ID = "Entity_Glance_Participant_Layer";
    protected static final String GLANCE_POLICY_GRID_LAYER_ID = "Entity_Glance_Policy_Layer";
    protected static final String GLANCE_TRANSACTION_GRID_LAYER_ID = "Entity_Glance_Transaction_Layer";
    protected static final String GLANCE_TRANSACTION_FORM_GRID_LAYER_ID = "Entity_Glance_Transaction_From_Layer";
    protected static final String GLANCE_FINANCIAL_GRID_LAYER_ID = "Entity_Glance_Financial_Layer";
    protected static final String GLANCE_FINANCIAL_FORM_GRID_LAYER_ID = "Entity_Glance_Financial_From_Layer";
}
