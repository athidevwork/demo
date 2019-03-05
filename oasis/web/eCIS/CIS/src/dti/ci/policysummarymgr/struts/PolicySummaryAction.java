package dti.ci.policysummarymgr.struts;

import dti.ci.core.CIFields;
import dti.ci.core.struts.MaintainEntityFolderBaseAction;
import dti.ci.policysummarymgr.PolicySummaryManager;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

import dti.oasis.util.SysParmProvider;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 19, 2007
 * This class is for view policy summary,it simply do redirect
 *
 * @author zlzhu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * Dec 19, 2007     zlzhu       Created
 * 10/06/2010       wfu         111776: Replaced hardcode string with resource definition
 * 03/06/2012       Parker      130270. set CIS notes visiable for this business.
 * 12/20/2013       hxk         139442
 *                              1)  Add logic for addl info layer
 *                              2)  Refactor code
 * 06/28/2018       ylu         Issue 194117: update for CSRF security.
 * ---------------------------------------------------
 */

public class PolicySummaryAction extends MaintainEntityFolderBaseAction {
    private final Logger l = LogUtils.getLogger(getClass());

    @Override
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadAllPolicy(mapping, form, request, response);
    }

    /**
     * Load all entity class.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     */
    public ActionForward loadAllPolicy(ActionMapping mapping, ActionForm form,
                                       HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllPolicy", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadAllPolicyResult";

        try {
            // Secures page
            securePage(request, form);

            Record inputRecord = getInputRecord(request);

            String entityId = inputRecord.getStringValue(CIFields.PK);

            // if the sysparm says to get addl info, do it ...
            if (YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm("CI_POLTAB_ADDLINFO", "N")).booleanValue()) {
                inputRecord.setFieldValue(CIFields.ENTITY_ID, entityId);

                Record addlInfoRec = getPolicySummaryManager().loadAddlInfo(inputRecord);

                if (addlInfoRec == null) {
                    MessageManager.getInstance().addInfoMessage("ci.licensemgr.noRecords.found.error");
                }
                publishOutputRecord(request, addlInfoRec);
            }

            loadListOfValues(request, form);

            addJsMessages();

            saveToken(request);

        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR,
                    "Failed to load the policy summary page.",
                    e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "execute", af);
        }
        return af;
    }

    public void verifyConfig() {
        if (getPolicySummaryManager() == null)
            throw new ConfigurationException("The required property 'policySummaryManager' is missing.");
    }

    //add js message
    private void addJsMessages() {
        // No messages right now...
    }

    public PolicySummaryManager getPolicySummaryManager() {
        return m_policySummaryManager;
    }

    public void setPolicySummaryManager(PolicySummaryManager policySummaryManager) {
        this.m_policySummaryManager = policySummaryManager;
    }

    private PolicySummaryManager m_policySummaryManager;
}
