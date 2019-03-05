package dti.pm.policymgr.renewalflagmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.error.ValidationException;
import dti.oasis.http.RequestIds;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.IOasisAction;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.oasis.util.LogUtils;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.renewalflagmgr.RenewalFlagFields;
import dti.pm.policymgr.renewalflagmgr.RenewalFlagManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.LabelValueBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   2/16/2016
 *
 * @author tzeng
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 02/16/2016       tzeng       Initial version.
 * ---------------------------------------------------
 */

public class MaintainRenewalFlagAction extends PMBaseAction{
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
        return loadAllRenewalFlag(mapping, form, request, response);
    }

    /**
     * Method to load list of renewal flags by request.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllRenewalFlag(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                 HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllRenewalFlag",
            new Object[]{mapping, form, request, response});

        String forwardString = "loadResult";

        try {
            // Secures access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            // Gets policy header
            PolicyHeader policyHeader = getPolicyHeader(request);

            // Gets grid record set
            RecordSet rs = (RecordSet)request.getAttribute(RequestIds.GRID_RECORD_SET);
            if (rs== null) {
                rs = getRenewalFlagManager().loadAllRenewalFlag(policyHeader);
            }

            // Make the Summary Record available for output
            Record outputRecord = rs.getSummaryRecord();

            // Sets data Bean
            setDataBean(request, rs);

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, outputRecord);

            // Load LOV
            loadListOfValues(request, form);

            // Load grid header bean
            loadGridHeader(request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load renewal flag page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllRenewalFlag", af);
        return af;
    }

    /**
     * Save all renewal flags.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveAllRenewalFlag(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveAllRenewalFlag", new Object[]{mapping, form, request, response});

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
                getRenewalFlagManager().saveAllRenewalFlag(policyHeader, inputRecords);
            }

        }
        catch (Exception e) {
            forwardString =
                handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to save all renewal flags.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveAllRenewalFlag", af);
        return af;
    }

    /**
     * Get initial values for adding new renewal flag.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getInitialValuesForRenewalFlag(ActionMapping mapping,
                                                        ActionForm form,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForUnderlyingPolicy",
            new Object[]{mapping, form, request, response});

        try {
            //Secure page and load all Oasis fields value include lov label fields -149171.
            securePage(request, form);

            //Get policy header
            PolicyHeader policyHeader = getPolicyHeader(request);

            // Get the initial values
            Record initialValuesRec = getRenewalFlagManager().getInitialValuesForAddRenewalFlag(policyHeader);

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
        l.exiting(getClass().getName(), "getInitialValuesForRenewalFlag", af);
        return af;
    }

    public RenewalFlagManager getRenewalFlagManager() {
        return m_renewalFlagManager;
    }

    public void setRenewalFlagManager(RenewalFlagManager renewalFlagManager) {
        m_renewalFlagManager = renewalFlagManager;
    }

    private RenewalFlagManager m_renewalFlagManager;
}
