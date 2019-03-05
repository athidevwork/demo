package dti.pm.coveragemgr.underlyingmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.AddSelectIndLoadProcessor;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.coveragemgr.underlyingmgr.UnderlyingCoverageFields;
import dti.pm.coveragemgr.underlyingmgr.UnderlyingCoverageManager;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.underlyingpolicymgr.UnderlyingPolicyManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Action class for Select Underlying Relation
 * <p>(C) 2018 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 31, 2018
 *
 * @author wrong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *  08/31/2018       wrong      188391 - Initial version.
 * ---------------------------------------------------
 */
public class SelectUnderlyingRelationAction extends PMBaseAction {
    /**
     * This method is triggered automatically when there is no process parameter
     * sent in along the requested url.
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
        return loadAvailableRelatedCoverage(mapping, form, request, response);
    }

    /**
     * Method to load list of available related coverage for adding new underlying policy.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAvailableRelatedCoverage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                      HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAvailableRelatedCoverage",
            new Object[]{mapping, form, request, response});

        String forwardString = "loadResult";

        try {
            // Secures access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);
            // Gets policy header
            PolicyHeader policyHeader = getPolicyHeader(request);
            //get input record
            Record inputRecord = getInputRecord(request);
            request.setAttribute("hasAvailableRelatedCoverages", "Y");
            RecordSet currentCoverageRs = getUnderlyingCoverageManager().getCurrentCoverage(policyHeader);
            RecordSet availableCoverageRs = new RecordSet();
            // Gets active related coverage information
            if (!StringUtils.isBlank(inputRecord.getStringValue(UnderlyingCoverageFields.POLICY_UNDER_POL_NO))) {
                availableCoverageRs = getUnderlyingCoverageManager().loadAvailableRelatedCoverage(policyHeader, inputRecord);
            }
            //no data found, add error message
            if(availableCoverageRs.getSize() == 0){
                request.setAttribute("hasAvailableRelatedCoverages", "N");
            }
            Record outputRecord = availableCoverageRs.getSummaryRecord();
            // Sets data Bean
            setDataBean(request, currentCoverageRs);
            setDataBean(request, availableCoverageRs, REL_COVG_GRID_ID);
            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, outputRecord);
            // Load LOV
            loadListOfValues(request, form);
            // Add Js messages
            addJsMessages();
            // Load grid header bean
            loadGridHeader(request);
            // Set currentGridId to selectCurrentCovgListGrid before load current coverage gird header
            setCurrentGridId(REL_COVG_GRID_ID);
            // Load current coverage grid header
            loadGridHeader(request, null, REL_COVG_GRID_ID, RELATED_GRID_LAYER_ID);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the available related coverage page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAvailableRelatedCoverage", af);
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
            if (currentGridId.equals(REL_COVG_GRID_ID)) {
                anchorName = getRelCoverageAnchorColumnName();
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
     * Add Js messages
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.maintainUnderlyingRelation.noSelection.error");
    }

    // Configuration constructor and accessor methods
    public void verifyConfig() {
        if (getUnderlyingCoverageManager() == null)
            throw new ConfigurationException("The required property 'underlyingCoverageManager' is missing.");
        if (getRelCoverageAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'currCoverageAnchorColumnName' is missing.");
    }

    public UnderlyingCoverageManager getUnderlyingCoverageManager() {
        return m_underlyingCoverageManager;
    }

    public void setUnderlyingCoverageManager(UnderlyingCoverageManager underlyingCoverageManager) {
        m_underlyingCoverageManager = underlyingCoverageManager;
    }

    public String getRelCoverageAnchorColumnName() {
        return m_relCoverageAnchorColumnName;
    }

    public void setRelCoverageAnchorColumnName(String relCoverageAnchorColumnName) {
        m_relCoverageAnchorColumnName = relCoverageAnchorColumnName;
    }

    protected static final String REL_COVG_GRID_ID = "selectRelatedCovgListGrid";
    protected static final String CURRENT_GRID_ID = "currentGridId";
    protected static final String RELATED_GRID_LAYER_ID = "PM_SEL_REL_COVG_GH";

    private String m_relCoverageAnchorColumnName;
    private UnderlyingCoverageManager m_underlyingCoverageManager;
}
