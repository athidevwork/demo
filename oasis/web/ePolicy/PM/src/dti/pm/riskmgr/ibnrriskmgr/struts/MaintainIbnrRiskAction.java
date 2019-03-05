package dti.pm.riskmgr.ibnrriskmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.workflowmgr.WorkflowAgent;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.riskmgr.ibnrriskmgr.IbnrRiskManager;
import dti.pm.riskmgr.ibnrriskmgr.InactiveRiskFields;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class to maintain IBNR risk
 *
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 07, 2011
 *
 * @author Dzhang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/27/2010       dzhang      126639 - Add js message for PT % validation.
 * 12/02/2013       jyang       149171 - Roll back 141758's change to load LOV label fields' value in
 *                                       getInitialValuesForXXX method.
 * ---------------------------------------------------
 */

public class MaintainIbnrRiskAction extends PMBaseAction {
    /**
     * This method is triggered automatically when there is no process parameter sent in along the requested url.
     * <p/>
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadAllIbnrRisk(mapping, form, request, response);
    }

    /**
     * This method is called when there the process parameter "loadAllIbnrRisk"
     * sent in along the requested url.
     * <p/>
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward loadAllIbnrRisk(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                         HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllIbnrRisk", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            // get input
            Record inputRecord = getInputRecord(request);

            // get policy header
            PolicyHeader policyHeader = getPolicyHeader(request);

            Record outputRecord = policyHeader.toRecord();
            // Get recorset and set databean
            RecordSet[] recordSets = (RecordSet[]) request.getAttribute(GRID_RECORD_SETS);
            if (recordSets == null) {
                recordSets = new RecordSet[3];
                ArrayList rsList = new ArrayList();
                // Get record set for first grid - Associated Risk list
                RecordSet rs = getIbnrRiskManager().loadAllAssociatedRisk(policyHeader, inputRecord);
                rsList.add(rs);
                outputRecord.setFields(rs.getSummaryRecord(), false);
                // Get record set for second grid - IBNR Inactive Risk Detail List
                rs = getIbnrRiskManager().loadAllIbnrInactiveRisk(policyHeader, inputRecord);
                rsList.add(rs);
                outputRecord.setFields(rs.getSummaryRecord(), false);
                // Get record set for third grid - Associated Risk's For IBNR Inactive Risk List
                rs = getIbnrRiskManager().loadAllAssociatedRiskForInactiveRisk(policyHeader, inputRecord);
                rsList.add(rs);
                outputRecord.setFields(rs.getSummaryRecord(), false);
                rsList.toArray(recordSets);
            }

            // Set all data beans to request
            setAllDataBean(request, recordSets);

            // Load all grid headers for all three grids, the grid header bean name is:
            // firstGridHeaderBean, secondGridHeaderBean, thirdGridHeaderBean
            loadAllGridHeader(request, getAnchorColumnNames());
            publishOutputRecord(request, outputRecord);

            // load list of values
            loadListOfValues(request, form);

            //add js messages to messagemanager for the current request
            addJsMessages();

            request.setAttribute(InactiveRiskFields.IS_IN_WORKFLOW,
                WorkflowAgentImpl.getInstance().hasWorkflow(policyHeader.getPolicyNo()) ? YesNoFlag.Y : YesNoFlag.N);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load IBNR risk list.", e, request, mapping);
        }

        // done
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllIbnrRisk", af);
        return af;
    }

    /**
     * Get initial values for Associated risk
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward getInitialValuesForAddAssociatedRisk(ActionMapping mapping,
                                                              ActionForm form,
                                                              HttpServletRequest request,
                                                              HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForAddAssociatedRisk",
            new Object[]{mapping, form, request, response});

        try {
            //Secure page and load all Oasis fields value include lov label fields -149171.
            securePage(request, form);

            PolicyHeader policyHeader = getPolicyHeader(request);
            Record inputRecord = getInputRecord(request);

            // get initial values
            Record record = getIbnrRiskManager().getInitialValuesForAddAssociatedRisk(policyHeader, inputRecord);

            // get LOV labels for initial values
            publishOutputRecord(request, record);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, record);

            // prepare return values
            writeAjaxXmlResponse(response, record, true);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values for associated risk.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForAddAssociatedRisk", af);
        return af;
    }


    /**
     * Get initial values for IBNR Inactive risk
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward getInitialValuesForAddInactiveRisk(ActionMapping mapping,
                                                            ActionForm form,
                                                            HttpServletRequest request,
                                                            HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForAddInactiveRisk",
            new Object[]{mapping, form, request, response});
        
        try {
            //Secure page and load all Oasis fields value include lov label fields -149171.
            securePage(request, form);

            PolicyHeader policyHeader = getPolicyHeader(request);
            Record inputRecord = getInputRecord(request);

            // get initial values
            Record record = getIbnrRiskManager().getInitialValuesForAddInactiveRisk(policyHeader, inputRecord, getAnchorColumnNames()[1]);

            // get LOV labels for initial values
            publishOutputRecord(request, record);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, record);

            // prepare return values
            writeAjaxXmlResponse(response, record);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values for IBNR Inactive risk.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForAddInactiveRisk", af);
        return af;
    }

    /**
     * Save all information in IBNR Inactive Risk page
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward saveAllInactiveRisk(ActionMapping mapping,
                                             ActionForm form,
                                             HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllInactiveRisk", new Object[]{mapping, form, request, response});
        }

        String forwardString = "saveResult";
        RecordSet[] inputRecordSets = null;
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (isTokenValid(request, true)) {
                // Secure page
                securePage(request, form, false);

                //get policy header
                PolicyHeader policyHeader = getPolicyHeader(request);

                // get input record sets for all grids
                inputRecordSets = getAllInputRecordSet(request, getAnchorColumnNames());
                // Call the business component to implement the validate/save logic
                getIbnrRiskManager().saveAllInactiveRisk(policyHeader, inputRecordSets);
            }
        }
        catch (ValidationException v) {
            // Save the recordset into the request
            request.setAttribute(GRID_RECORD_SETS, inputRecordSets);
            // Handle the validation exception
            handleValidationException(v, request);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to save IBNR Inactive Risk.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllInactiveRisk", af);
        }
        return af;
    }

    /**
     * Method to process change associated risk
     * <p/>
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward processChangeAssociatedRisk(ActionMapping mapping, ActionForm form,
                                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "processChangeAssociatedRisk", new Object[]{mapping, form, request, response});

        try {
            // Secure page without load fields
            securePage(request, form, false);

            // Map request to record for input
            Record inputRecord = getInputRecord(request);
            getIbnrRiskManager().processChangeAssociatedRisk(inputRecord);

            // Send back xml data
            writeEmptyAjaxXMLResponse(response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to process change associated risk.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "processChangeAssociatedRisk", af);
        return af;
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.maintainInactive.associatedRiskNameRequired.error");
        MessageManager.getInstance().addJsMessage("pm.maintainInactive.inactiveRiskHasClaimAttached.error");
        MessageManager.getInstance().addJsMessage("pm.maintainInactive.addAssociatedRisk.riskExists.error");
        MessageManager.getInstance().addJsMessage("pm.maintainInactive.unsavedData.error");
        MessageManager.getInstance().addJsMessage("pm.maintainInactive.invalidPtPercent");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getAnchorColumnNames() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getIbnrRiskManager() == null)
            throw new ConfigurationException("The required property 'affiliationManager' is missing.");
    }

    public IbnrRiskManager getIbnrRiskManager() {
        return m_ibnrRiskManager;
    }

    public void setIbnrRiskManager(IbnrRiskManager affiliationManager) {
        m_ibnrRiskManager = affiliationManager;
    }

    public MaintainIbnrRiskAction() {
    }

    /**
     * Get anchor column names
     *
     * @return String[]
     */
    public String[] getAnchorColumnNames() {
        return m_anchorColumnNames;
    }

    /**
     * Set anchor column name
     *
     * @param anchorColumnNames anchorColumnNames
     */
    public void setAnchorColumnNames(String[] anchorColumnNames) {
        m_anchorColumnNames = anchorColumnNames;
    }

    private String[] m_anchorColumnNames;
    private IbnrRiskManager m_ibnrRiskManager;
    protected static final String GRID_RECORD_SETS = "gridRecordSets";
}