package dti.pm.coveragemgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Action class for Maintain Excess Coverage.
 * <p/>
 * <p>(C) 2009 Delphi Technology, inc. (dti)</p>
 * Date:   April 2, 2009
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/13/2010       wfu         111776: Replaced hardcode string with resource definition
 * 12/02/2013       jyang       149171 - Roll back 141758's change to load LOV label fields' value in
 *                                       getInitialValuesForXXX method.
 * 06/13/2018       wrong       192557 - Modified saveAllCarrier() to call hasValidSaveToken() to be used for CSRFInterceptor.
 * ---------------------------------------------------
 */

public class MaintainExcessCoverageAction extends PMBaseAction {
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
        return loadAllCarrier(mapping, form, request, response);
    }

    /**
     * Method to load all carrier.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllCarrier(ActionMapping mapping, ActionForm form,
                                        HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllCarrier", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);
            // Get policyHeader
            PolicyHeader policyHeader = getPolicyHeader(request);
            // Get the inputRecord
            Record inputRecord = getInputRecord(request);
            inputRecord.setFields(policyHeader.toRecord());
            // Load the prior carrier
            RecordSet rs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            if (rs == null) {
                rs = getCoverageManager().loadAllPriorCarrier(inputRecord);
            }
            // Get the current carrier
            Record record = getCoverageManager().loadAllCurrentCarrier(inputRecord);
            // Set the current carrier to summaryRecord
            rs.getSummaryRecord().setFields(record);

            setDataBean(request, rs);
            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, rs.getSummaryRecord());
            // Load coverage grid header
            loadGridHeader(request);

            // Populate messages for javascirpt
            addJsMessages();

            // Load the list of values after loading the data
            loadListOfValues(request, form);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the excess coverage page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllCarrier", af);
        return af;
    }

    /**
     * Save all updated carrier.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward saveAllCarrier(ActionMapping mapping, ActionForm form,
                                        HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveAllCarrier", new Object[]{mapping, form, request, response});

        RecordSet inputRecords = null;
        String forwardString = "saveResult";
        try {
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);
                // Get the RecordSet for input
                inputRecords = getInputRecordSet(request);
                // Save the carrier
                getCoverageManager().saveAllPriorCarrier(inputRecords);
            }
        }
        catch (ValidationException ve) {
            // Save the input records into request
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);
            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to save the carrier.", e, request, mapping);
        }

        // Return the forward
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveAllCarrier", af);
        return af;
    }

    /**
     * Get Inital Values for new added prior carrier
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward getInitialValuesForPriorCarrier(ActionMapping mapping,
                                                         ActionForm form,
                                                         HttpServletRequest request,
                                                         HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForPriorCarrier",
            new Object[]{mapping, form, request, response});
        try {
			//Secure page and load all Oasis fields value include lov label fields -149171.
            securePage(request, form);
            // Get the inputRecord
            Record inputRecord = getInputRecord(request);
            // Get inital values
            Record record = getCoverageManager().getInitialValuesForPriorCarrier(inputRecord);
            // Send back xml data
            writeAjaxXmlResponse(response, record);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values for adding prior carrier.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForPriorCarrier", af);
        return af;

    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getCoverageManager() == null)
            throw new ConfigurationException("The required property 'coverageManager' is missing.");
        if (super.getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        // For handle with on blur error
        MessageManager.getInstance().addJsMessage("pm.maintainExcessCoverage.handleOnBlur.error");
    }

}
