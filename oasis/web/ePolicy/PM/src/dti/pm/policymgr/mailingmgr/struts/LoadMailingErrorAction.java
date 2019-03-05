package dti.pm.policymgr.mailingmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.util.LogUtils;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.mailingmgr.PolicyMailingManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for select mailing type
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 2, 2008
 *
 * @author sxm
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/03/2013       tcheng      Issue 145238 - Modified loadAllMailingGenerationError to set dataBean with mailing event recordSet.
 * ---------------------------------------------------
 */

public class LoadMailingErrorAction extends PMBaseAction {


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
        return loadAllMailingGenerationError(mapping, form, request, response);
    }


    /**
     * Method to load all mailing generation errors.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllMailingGenerationError(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllMailingGenerationError", new Object[]{mapping, form, request, response});
        }

        String forwardString = "mailingError";
        try {
            securePage(request, form);
            loadListOfValues(request, form);
            RecordSet rs = (RecordSet)request.getAttribute(MAILING_ERROR_RECORD_SET);
            if (rs != null) {
                setDataBean(request, rs);
            }
            else {
                setEmptyDataBean(request);
            }

            // load grid header only since grid content is already loaded at this point
            loadGridHeader(request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the maililng generation errors.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllMailingGenerationError", af);
        return af;
    }


    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getPolicyMailingManager() == null)
            throw new ConfigurationException("The required property 'policyMailingManager' is missing.");
    }

    public LoadMailingErrorAction() {
    }

    public PolicyMailingManager getPolicyMailingManager() {
        return policyMailingManager;
    }

    public void setPolicyMailingManager(PolicyMailingManager policyMailingManager) {
        this.policyMailingManager = policyMailingManager;
    }

    private PolicyMailingManager policyMailingManager;
    protected static final String MAILING_ERROR_RECORD_SET = "mailingErrorRecordSet";
}

