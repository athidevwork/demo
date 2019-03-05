package dti.pm.policymgr.underlyingpolicymgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.recordset.Record;

import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;

import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.underlyingpolicymgr.UnderlyingPolicyFields;
import dti.pm.policymgr.underlyingpolicymgr.UnderlyingPolicyManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Action class for maintain Underlying Policy
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 3, 2008
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
  * 07/04/2013       Awu         1. getInitialValuesForUnderlyingPolicy, Modified to set the loading fields flag to false in securePage for Issue141758.
 *                               2. The fields should be loaded in getInitialValuesForAddAgent, or else,
 *                                 problem like issue145732 will be happened. So rollback before changes to load the fields.
 *  12/18/2015       tzeng      165794 - Remove useless import class and modified loadAllUnderlyingPolicy to standard
 *                                       format code.
 * ---------------------------------------------------
 */
public class MaintainUnderlyingPolicyAction extends PMBaseAction {
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
        return loadAllUnderlyingPolicy(mapping, form, request, response);
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
    public ActionForward loadAllUnderlyingPolicy(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                 HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllUnderlyingPolicy",
            new Object[]{mapping, form, request, response});

        String forwardString = "loadResult";

        try {
            // Secures access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);
            // Gets policy header
            PolicyHeader policyHeader = getPolicyHeader(request);
            // try to get the validated recordset from request
            // if not found, call DAO method to load all underlying policy information from database
            RecordSet underPolicyRs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            if (underPolicyRs == null) {
                underPolicyRs = getUnderlyingPolicyManager().loadAllUnderlyingPolicy(policyHeader);
            }
            Record outputRecord = underPolicyRs.getSummaryRecord();
            // Sets data Bean
            setDataBean(request, underPolicyRs);
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
        l.exiting(getClass().getName(), "loadAllUnderlyingPolicy", af);
        return af;
    }

    /**
     * Save all underlying policy
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveAllUnderlyingPolicy(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveAllUnderlyingPolicy", new Object[]{mapping, form, request, response});

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
                getUnderlyingPolicyManager().saveAllUnderlyingPolicy(policyHeader, inputRecords);
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
                handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to save all Underlying Policy.",
                    e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveAllUnderlyingPolicy", af);
        return af;
    }

    /**
     * get initial values for adding new underlying policy
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getInitialValuesForUnderlyingPolicy(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForUnderlyingPolicy",
            new Object[]{mapping, form, request, response});
        try {
            securePage(request, form);
            //Get policy header
            PolicyHeader policyHeader = getPolicyHeader(request);
            // Get all request parameters into a record
            Record inputRecord = getInputRecord(request);
            // Get the initial values
            Record initialValuesRec =
                getUnderlyingPolicyManager().getInitialValuesForUnderlyingPolicy(policyHeader, inputRecord);

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
                "Failed to get underlying policy initial values for new record.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForUnderlyingPolicy", af);
        return af;
    }

    /**
     * get retro date for reset by Ajax call
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getRetroDateForReset(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getRetroDateForReset",
            new Object[]{mapping, form, request, response});
        try {
            securePage(request, form, false);
            //Get policy header
            PolicyHeader policyHeader = getPolicyHeader(request);
            // Get all request parameters into a record
            Record inputRecord = getInputRecord(request);
            // Get the initial values
            String retroDate =
                getUnderlyingPolicyManager().getRetroDateForReset(policyHeader, inputRecord);
            Record retroRec = new Record();
            UnderlyingPolicyFields.setRetroactiveDate(retroRec, retroDate);
            writeAjaxXmlResponse(response, retroRec, true);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR,
                "Failed to get retro date for reset by Ajax call.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getRetroDateForReset", af);
        return af;
    }

    // Configuration constructor and accessor methods
    public void verifyConfig() {
        if (getUnderlyingPolicyManager() == null)
            throw new ConfigurationException("The required property 'underlyingPolicyManager' is missing.");
    }

    public UnderlyingPolicyManager getUnderlyingPolicyManager() {
        return m_underlyingPolicyManager;
    }

    public void setUnderlyingPolicyManager(UnderlyingPolicyManager underlyingPolicyManager) {
        m_underlyingPolicyManager = underlyingPolicyManager;
    }

    private UnderlyingPolicyManager m_underlyingPolicyManager;
}
