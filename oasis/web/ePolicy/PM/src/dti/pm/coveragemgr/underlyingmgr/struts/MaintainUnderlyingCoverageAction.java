package dti.pm.coveragemgr.underlyingmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.recordset.Record;

import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;

import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.coveragemgr.underlyingmgr.UnderlyingCoverageManager;
import dti.pm.policymgr.PolicyHeader;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;


/**
 * Action class for maintain Underlying Coverage
 * <p>(C) 2018 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 24, 2018
 *
 * @author wrong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *  08/24/2018       wrong      188391 - Initial version.
 * ---------------------------------------------------
 */
public class MaintainUnderlyingCoverageAction extends PMBaseAction {
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
        return loadAllUnderlyingCoverage(mapping, form, request, response);
    }

    /**
     * Method to load list of underlying policy for requested policy.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllUnderlyingCoverage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                   HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllUnderlyingCoverage",
            new Object[]{mapping, form, request, response});

        String forwardString = "loadResult";

        try {
            // Secures access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);
            // Gets policy header
            PolicyHeader policyHeader = getPolicyHeader(request);

            Record inputRecord = getInputRecord(request);

            // try to get the validated recordset from request
            // if not found, call DAO method to load all underlying policy information from database
            RecordSet underCoverageRs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            if (underCoverageRs == null) {
                underCoverageRs = getUnderlyingCoverageManager().loadAllUnderlyingCoverage(policyHeader, inputRecord);
            }
            Record outputRecord = underCoverageRs.getSummaryRecord();
            // Sets data Bean
            setDataBean(request, underCoverageRs);
            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, outputRecord);
            // Load LOV
            loadListOfValues(request, form);
            // Load grid header bean
            loadGridHeader(request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the Underlying Policy page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllUnderlyingCoverage", af);
        return af;
    }

    /**
     * Save all underlying coverage
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveAllUnderlyingCoverage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveAllUnderlyingCoverage", new Object[]{mapping, form, request, response});

        String forwardString = "saveResult";
        PolicyHeader policyHeader;
        RecordSet inputRecords = null;

        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure page, not load all fields
                securePage(request, form, false);
                // Pull the policy header from the request
                policyHeader = getPolicyHeader(request);
                // Get input records
                inputRecords = getInputRecordSet(request);
                // Call the business component to save all underlying policies
                getUnderlyingCoverageManager().saveAllUnderlyingCoverage(policyHeader, inputRecords);
            }

        }
        catch (ValidationException v) {
            // Save the recordset into the request
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);
            // Handle the validation exception
            handleValidationException(v, request);
        }
        catch (Exception e) {
            forwardString =
                handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to save all Underlying Coverage.",
                    e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveAllUnderlyingCoverage", af);
        return af;
    }

    /**
     * get initial values for adding new underlying coverage
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getInitialValuesForUnderlyingCoverage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForUnderlyingCoverage",
            new Object[]{mapping, form, request, response});
        try {
            securePage(request, form);
            //Get policy header
            PolicyHeader policyHeader = getPolicyHeader(request);
            // Get all request parameters into a record
            Record inputRecord = getInputRecord(request);
            // Get the initial values
            Record initialValuesRec =
                getUnderlyingCoverageManager().getInitialValuesForUnderlyingCoverage(policyHeader, inputRecord);

            // get LOV labels for initial values
            publishOutputRecord(request, initialValuesRec);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, initialValuesRec);

            writeAjaxResponse(response, initialValuesRec, true);
        }
        catch (ValidationException ve) {
            handleValidationExceptionForAjax(ve, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR,
                "Failed to get underlying coverage initial values for new record.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForUnderlyingCoverage", af);
        return af;
    }


    // Configuration constructor and accessor methods
    public void verifyConfig() {
        if (getUnderlyingCoverageManager() == null)
            throw new ConfigurationException("The required property 'underlyingCoverageManager' is missing.");
    }

    public UnderlyingCoverageManager getUnderlyingCoverageManager() {
        return m_underlyingCoverageManager;
    }

    public void setUnderlyingCoverageManager(UnderlyingCoverageManager underlyingCoverageManager) {
        m_underlyingCoverageManager = underlyingCoverageManager;
    }

    private UnderlyingCoverageManager m_underlyingCoverageManager;
}
