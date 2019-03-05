package dti.pm.policymgr.applicationmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.applicationmgr.ApplicationManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for select all available app reviewer.
 * <p/>
 * <p>(C) 2012 Delphi Technology, inc. (dti)</p>
 * Date:   July 3, 2012
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class SelectAppReviewerAction extends PMBaseAction {
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
        return loadAllAvailableAppReviewer(mapping, form, request, response);
    }

    /**
     * Load all available app reviewers
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllAvailableAppReviewer(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                     HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAvailableAppReviewer", new Object[]{mapping, form, request, response});
        }

        String forwardString = "success";
        try {
            // Secures access to the page, load the Oasis Fields without loading the LOVs,
            //   and map the input parameters to the Fields
            securePage(request, form);
            // Load available app reviewers
            RecordSet rs = getApplicationManager().loadAllAvailableAppReviewer();
            // Set data bean
            setDataBean(request, rs);
            // Load LOVs
            loadListOfValues(request, form);
            // Load grid header bean
            loadGridHeader(request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load available app reviewers.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAvailableAppReviewer", af);
        }
        return af;
    }

    /**
     * verifyConfig
     */
    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getApplicationManager() == null)
            throw new ConfigurationException("The required property 'applicationManager' is missing.");
    }


    public ApplicationManager getApplicationManager() {
        return m_applicationManager;
    }

    public void setApplicationManager(ApplicationManager applicationManager) {
        m_applicationManager = applicationManager;
    }

    private ApplicationManager m_applicationManager;
}
