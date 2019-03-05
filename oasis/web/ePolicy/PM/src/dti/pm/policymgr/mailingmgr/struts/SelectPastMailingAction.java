package dti.pm.policymgr.mailingmgr.struts;

import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.mailingmgr.PolicyMailingManager;
import dti.pm.busobjs.SysParmIds;
import dti.oasis.util.LogUtils;
import dti.oasis.util.SysParmProvider;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Action class for select shared detail info
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 2, 2008
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
public class SelectPastMailingAction extends PMBaseAction {

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
        return loadAllPastMailing(mapping, form, request, response);
    }

    /**
     * Load all available components
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllPastMailing(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                      HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllPastMailing",
            new Object[]{mapping, form, request, response});

        String forwardString = "loadResult";

        try {
            securePage(request, form);
            Record record = getInputRecord(request);
            loadListOfValues(request, form);
            RecordSet rs = getPolicyMailingManager().loadAllPastMailing(record);
            SysParmProvider sysParm = SysParmProvider.getInstance();
            String days = sysParm.getSysParm(SysParmIds.PM_MLNG_WARN_DAYS,"25");
            MessageManager.getInstance().addInfoMessage("pm.selExcludedPolicy.header.info",new Object[]{days});
            addJsMessages();
            publishOutputRecord(request, rs.getSummaryRecord());
            loadGridHeader(request);
            setDataBean(request, rs);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load past mailing.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllPastMailing", af);
        return af;
    }

    /**
     * Add Js messages
     */
    private void addJsMessages() {
       MessageManager.getInstance().addJsMessage("pm.selExludedPolicy.excludePolicy.warning"); 
       MessageManager.getInstance().addJsMessage("pm.selExludedPolicy.noSelection.error");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getPolicyMailingManager() == null)
            throw new ConfigurationException("The required property 'policyMailingManager' is missing.");
    }

    public SelectPastMailingAction() {
    }

    public PolicyMailingManager getPolicyMailingManager() {
        return policyMailingManager;
    }

    public void setPolicyMailingManager(PolicyMailingManager policyMailingManager) {
        this.policyMailingManager = policyMailingManager;
    }

    private PolicyMailingManager policyMailingManager;
}
