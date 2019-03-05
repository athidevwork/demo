package dti.pm.transactionmgr.auditmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.userpreference.UserPreferenceManager;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.transactionmgr.auditmgr.AuditManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for view audit
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 24, 2007
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public class ViewAuditAction extends PMBaseAction {


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
        return loadAllAudit(mapping, form, request, response);
    }

    /**
     * Method to load all audit info for requested policy.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllAudit(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAudit", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadAuditResult";

        try {
            // Secures access to the page, loads the oasis fields without loading LOVs,
            // and map the input parameters into the fields.
            securePage(request, form);
            //audit level is got from pre audil level selection page
            String auditLevel = request.getParameter("auditLevel");
            if ((StringUtils.isBlank(auditLevel))) {
                auditLevel = getUserPreferenceManager().getUserPreference("AUDIT_LOG_DISPLAY","ASK");
            }
            String fromPage=request.getParameter("fromPage");
            if ((auditLevel.equals("ASK"))&&(!fromPage.equals("transaction-transaction"))) {
                request.setAttribute("contextId", request.getParameter("contextId"));
                request.setAttribute("fromPage", request.getParameter("fromPage"));
                forwardString = "selectAuditLevel";
            }
            else {
                PolicyHeader policyHeader = getPolicyHeader(request);
                publishOutputRecord(request, policyHeader.toRecord());
                Record inputRecord = getInputRecord(request);
                inputRecord.setFieldValue("auditLevel",auditLevel);

                RecordSet rs = getAuditManager().loadAllAudit(policyHeader,inputRecord);
                //Set page UI attributes
                Record record = rs.getSummaryRecord();
                // publish page field
                publishOutputRecord(request, record);
                // Sets data bean
                setDataBean(request, rs);
                // Loads list of values
                loadListOfValues(request, form);
                // Load grid header bean
                loadGridHeader(request);
            }
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the loadAllAudit page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllAudit", af);
        return af;
    }

    public AuditManager getAuditManager() {
        return m_AuditManager;
    }

    public void setAuditManager(AuditManager auditManager) {
        m_AuditManager = auditManager;
    }

    public UserPreferenceManager getUserPreferenceManager() {
        return m_userPreferenceManager;
    }

    public void setUserPreferenceManager(UserPreferenceManager userPreferenceManager) {
        m_userPreferenceManager = userPreferenceManager;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getAuditManager() == null)
            throw new ConfigurationException("The required property 'auditManager' is missing.");
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if(getUserPreferenceManager()==null)
            throw new ConfigurationException("The required property 'userPreferenceManager' is missing.");
    }

    private AuditManager m_AuditManager;

    private UserPreferenceManager m_userPreferenceManager;

}
