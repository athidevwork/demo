package dti.pm.coveragemgr.vlcoveragemgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.http.RequestIds;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.messagemgr.MessageManager;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.coveragemgr.vlcoveragemgr.VLCoverageManager;
import dti.pm.coveragemgr.vlcoveragemgr.VLCoverageFields;
import dti.pm.policymgr.PolicyHeader;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class used to maintain VL Coverages
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 7, 2008
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class MaintainVLCoverageAction extends PMBaseAction {

    /**
     * This method is triggered automatically when where is no process parameter
     * sent in along the requested url.
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
        return loadAllVLRisk(mapping, form, request, response);
    }

    /**
     * Method to load all special handlings for requested policy.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllVLRisk(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                       HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllVLRisk", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadResult";
        try {
            // Secures access to the page, loads the oasis fields without loading LOVs,
            // and map the input parameters into the fields.
            securePage(request, form);

            // get Policy Header
            PolicyHeader policyHeader = getPolicyHeader(request);
            // get input parameters
            Record inputRecord = getInputRecord(request);

            // Gets all VL coverage infos
            RecordSet rs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            if (rs == null) {
                rs = getVlCoverageManager().loadAllVLRisk(policyHeader, inputRecord);
            }
            //add no data found info
            if(rs.getSize() == 0 && !VLCoverageFields.getInUpdateMode(inputRecord).booleanValue()){
                MessageManager.getInstance().addErrorMessage("pm.maintainVLCoverage.noData.error");
            }

            // Sets data bean
            setDataBean(request, rs);

            // Make the Summary Record available for output
            Record output = rs.getSummaryRecord();
            output.setFields(inputRecord, false);
            
            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);
            // Loads list of values
            loadListOfValues(request, form);
            // Load grid header bean
            loadGridHeader(request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(
                AppException.UNEXPECTED_ERROR, "Failed to load the VL risk page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllVLRisk", af);
        return af;
    }


    public ActionForward getInitialValuesForVLRisk(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                            HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForVLRisk",
            new Object[]{mapping, form, request, response});


        try {
            // Secure page
            securePage(request, form);

            // Pull the policy header from request
            PolicyHeader policyHeader = getPolicyHeader(request);
            // Get all request parameters into a record
            Record inputRecord = getInputRecord(request);
            // Get the initial values
            Record initialValuesRec = getVlCoverageManager().getInitialValuesForVLRisk(policyHeader, inputRecord);

            // get LOV labels for initial values
            publishOutputRecord(request, initialValuesRec);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, initialValuesRec);

            writeAjaxXmlResponse(response, initialValuesRec);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values for VL Employee.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForVLRisk", af);
        return af;
    }

    /**
     * Method to save all special handlings.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveAllVLRisk(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                 HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveAllVLRisk", new Object[]{mapping, form, request, response});
        ActionForward af;
        String forwardString = "saveResult";
        RecordSet inputRecords = null;
        try {
            // If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure page
                securePage(request, form, false);

                // Parse the Grid XML as a RecordSet
                inputRecords = getInputRecordSet(request);
                // Pull the policy header from request
                PolicyHeader policyHeader = getPolicyHeader(request);
                // Get all request parameters into a record
                Record inputRecord = getInputRecord(request);
                // Call the business component to implement the validate/save logic
                getVlCoverageManager().saveAllVLRisk(policyHeader, inputRecord, inputRecords);               
            }
        }
        catch (ValidationException ve) {
            // Save the input records into request
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);
            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup("pm.maintainVLCoverage.saveData.error", "Failed to save VL risk page.",
                e, request, mapping);
        }

        af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveAllVLRisk", af);
        return af;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getVlCoverageManager() == null)
            throw new ConfigurationException("The required property 'vlCoverageManager' is missing.");
    }


    public VLCoverageManager getVlCoverageManager() {
        return m_vlCoverageManager;
    }

    public void setVlCoverageManager(VLCoverageManager vlCoverageManager) {
        m_vlCoverageManager = vlCoverageManager;
    }

    private VLCoverageManager m_vlCoverageManager;
}
