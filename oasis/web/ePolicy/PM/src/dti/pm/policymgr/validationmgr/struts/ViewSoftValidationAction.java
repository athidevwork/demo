package dti.pm.policymgr.validationmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.http.RequestIds;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.validationmgr.SoftValidationManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   11/11/2016
 *
 * @author tzeng
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/07/2016       tzeng       166929 - Initial version.
 * ---------------------------------------------------
 */

public class ViewSoftValidationAction extends PMBaseAction{
    private final Logger l = LogUtils.getLogger(getClass());
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
        return loadSoftValidation(mapping, form, request, response);
    }

    /**
     * Method to load all premium info for requested policy.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadSoftValidation(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadSoftValidation", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadResult";

        try {
            // Secures access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            // Get Policy Header
            PolicyHeader policyHeader = getPolicyHeader(request);

            // Get input record
            Record inputRecord = getInputRecord(request);

            // Load the soft validation list
            RecordSet rs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            if (rs == null) {
                rs = getSoftValidationManager().loadSoftValidation(policyHeader, inputRecord);
            }

            // Make the Summary Record available for output
            Record outputRecord = rs.getSummaryRecord();

            // publish page field
            publishOutputRecord(request, outputRecord);

            // Sets data bean
            setDataBean(request, rs);

            // Loads list of values
            loadListOfValues(request, form);

            // Load grid header bean
            loadGridHeader(request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load soft validation page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadSoftValidation", af);
        }
        return af;
    }

    /**
     * Check whether soft validation exists based on current transaction.
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward hasSoftValidationExists(ActionMapping mapping,
                                                 ActionForm form,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "hasSoftValidationExists", new Object[]{mapping, form, request, response});
        }

        try {
            //Secure page
            securePage(request, form);

            //Get policy header
            PolicyHeader policyHeader = getPolicyHeader(request);

            //Get the indicator for soft validation exists or not
            Record outputRecord = getSoftValidationManager().getSoftValidationB(policyHeader.toRecord());

            writeAjaxXmlResponse(response, outputRecord);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR,
                "Failed to check whether soft validation exists based on current transaction.", e, response);
        }

        ActionForward af = null;

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "hasSoftValidationExists", af);
        }
        return af;
    }

    public SoftValidationManager getSoftValidationManager() {
        return m_softValidationManager;
    }

    public void setSoftValidationManager(SoftValidationManager softValidationManager) {
        m_softValidationManager = softValidationManager;
    }

    private SoftValidationManager m_softValidationManager;
}
