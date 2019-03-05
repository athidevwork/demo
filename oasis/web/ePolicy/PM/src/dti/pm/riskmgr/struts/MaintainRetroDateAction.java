package dti.pm.riskmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.LogUtils;
import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.coverageclassmgr.CoverageClassManager;
import dti.pm.coveragemgr.CoverageFields;
import dti.pm.policymgr.PolicyHeader;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Action class handle risk copy all process
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   July 11, 2008
 *
 * @author lzhang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/11/2016       lzhang      177681 initial version:
 *                              loadAllNewCopiedCMCoverage and saveAllRetroDate method.
 * ---------------------------------------------------
 */
public class MaintainRetroDateAction extends PMBaseAction {
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
        request.setAttribute(RequestIds.PROCESS, "loadAllNewCopiedCMCoverage");
        return loadAllNewCopiedCMCoverage(mapping, form, request, response);
    }

    /**
     * Method to load all new copied CM coverages
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllNewCopiedCMCoverage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllNewCopiedCMCoverage", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadResult";
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            // Gets policy header
            PolicyHeader policyHeader = getPolicyHeader(request, true);
            Record inputRecord = getInputRecord(request);
            RecordSet rs = (RecordSet) request.getAttribute(GRID_RECORD_SET);
            if (inputRecord.hasStringValue(CoverageFields.TO_COVG_BASE_RECORD_IDS)){
                UserSessionManager.getInstance().getUserSession(request).set(CoverageFields.TO_COVG_BASE_RECORD_IDS, CoverageFields.getToCovgBaseRecordIds(inputRecord));
            }else {
                CoverageFields.setToCovgBaseRecordIds(inputRecord, (String)UserSessionManager.getInstance().getUserSession(request).get(CoverageFields.TO_COVG_BASE_RECORD_IDS));
            }

            if (rs == null) {
                // Loads all new copied Claim Made retroactive Date Data
                rs = getCoverageManager().loadAllNewCopiedCMCoverage(inputRecord);
            }

            // Sets data Bean
            setDataBean(request, rs);
            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, rs.getSummaryRecord());

            // Load LOVs
            loadListOfValues(request, form);
            // Load grid header bean
            loadGridHeader(request);

        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the retro date page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllNewCopiedCMCoverage", af);
        }

        return af;
    }

    /**
     * Save all underwriters
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveAllRetroDate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "save", new Object[]{mapping, form, request, response});

        String forwardString = "saveResult";
        RecordSet inputRecords = null;

        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure page
                securePage(request, form, false);

                // Generate input records
                inputRecords = getInputRecordSet(request);

                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "saveAllRetroDate",
                        "Saving the Retro Date: " + inputRecords);
                }

                // Call the business component to implement the validate/save logic
                int updateCount = getCoverageManager().saveAllRetroDate(inputRecords);
            }

        }
        catch (ValidationException v) {
            // Save the recordset into the request
            request.setAttribute(GRID_RECORD_SET, inputRecords);

            // Handle the validation exception
            handleValidationException(v, request);
        }

        catch (Exception e) {
            handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to save the retro date.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "save", af);
        return af;
    }
    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
    }

    private static final String GRID_RECORD_SET = "gridRecordSet";
}
