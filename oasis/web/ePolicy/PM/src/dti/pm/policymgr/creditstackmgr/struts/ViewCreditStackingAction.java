package dti.pm.policymgr.creditstackmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.creditstackmgr.CreditStackingFields;
import dti.pm.policymgr.creditstackmgr.CreditStackingManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Action class for ViewCreditStacking Action.
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   May 26, 2011
 *
 * @author syang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class ViewCreditStackingAction extends PMBaseAction {

    /**
     * This method is triggered automatically when there is no process parameter sent in along the requested url.
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
        return loadAllCreditStacking(mapping, form, request, response);
    }

    /**
     * Load all credit stacking data.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllCreditStacking(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {

        Logger l = LogUtils.enterLog(getClass(), "loadAllCreditStacking", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {
            // Secures access to the page, loads the oasis fields and map the input parameters into the fields.
            securePage(request, form);
            // Get input record.
            Record inputRecord = getInputRecord(request);

            // Get record set and set data bean
            RecordSet[] recordSets = (RecordSet[]) request.getAttribute(GRID_RECORD_SETS);
            if (recordSets == null) {
                recordSets = new RecordSet[2];
                if (inputRecord.hasStringValue(CreditStackingFields.SEARCH_B) && YesNoFlag.getInstance(CreditStackingFields.getSearchB(inputRecord)).booleanValue()) {
                    // Get record set for first grid - header
                    RecordSet headerRs = getCreditStackingManager().loadAllHeaderInformation(inputRecord);
                    recordSets[0] = headerRs;

                    // Get record set for second grid - applied
                    RecordSet appliedRs = getCreditStackingManager().loadAllAppliedInformation(inputRecord);
                    recordSets[1] = appliedRs;
                    if (headerRs.getSize() <= 0 && appliedRs.getSize() <= 0) {
                        MessageManager.getInstance().addErrorMessage("pm.creditStacking.noDataFound");
                    }
                }
                else {
                    recordSets = getInitialRecordSets();
                }
            }
            // Set all data beans to request
            setAllDataBean(request, recordSets);
            // Public output record
            publishOutputRecord(request, inputRecord);
            // Load all grid headers for all grids, the grid header bean name is: firstGridHeaderBean, secondGridHeaderBean
            loadAllGridHeader(request, getAnchorColumnNames());
            // Load LOVs
            loadListOfValues(request, form);
        }
        catch (ValidationException ve) {
            forwardString = "loadPage";
            // If the search criteria is empty, system should add the anchor column names.
            RecordSet[] recordSets = getInitialRecordSets();
            // Set into request.
            request.setAttribute(GRID_RECORD_SETS, recordSets);
            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the View Credit Stacking page.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllCreditStacking", null);
        return af;
    }

    /**
     * Get initial RecordSets.
     *
     * @return RecordSet[]
     */
    protected RecordSet[] getInitialRecordSets() {
        Logger l = LogUtils.enterLog(getClass(), "getInitialRecordSets");

        RecordSet[] recordSets = new RecordSet[2];
        RecordSet headerRs = new RecordSet();
        List fieldNames = new ArrayList();
        fieldNames.add(CreditStackingFields.SEL_ORDER);
        headerRs.addFieldNameCollection(fieldNames);
        recordSets[0] = headerRs;

        RecordSet appliedRs = new RecordSet();
        List appliedFieldNames = new ArrayList();
        appliedFieldNames.add(CreditStackingFields.WIN_BUCKET);
        appliedRs.addFieldNameCollection(appliedFieldNames);
        recordSets[1] = appliedRs;

        l.exiting(getClass().getName(), "getInitialRecordSets", recordSets);
        return recordSets;
    }

    /**
     * Verify CreditStackingManager in spring config.
     */
    public void verifyConfig() {
        if (getCreditStackingManager() == null)
            throw new ConfigurationException("The required property 'creditStackingManager' is missing.");
        if (getAnchorColumnNames() == null) {
            throw new ConfigurationException("The required property 'anchorColumnNames' is missing.");
        }
    }

    public CreditStackingManager getCreditStackingManager() {
        return m_creditStackingManager;
    }

    public void setCreditStackingManager(CreditStackingManager creditStackingManager) {
        m_creditStackingManager = creditStackingManager;
    }

    public String[] getAnchorColumnNames() {
        return m_anchorColumnNames;
    }

    public void setAnchorColumnNames(String[] anchorColumnNames) {
        m_anchorColumnNames = anchorColumnNames;
    }

    private CreditStackingManager m_creditStackingManager;
    private String[] m_anchorColumnNames;
    private static final String GRID_RECORD_SETS = "gridRecordSets";

}
