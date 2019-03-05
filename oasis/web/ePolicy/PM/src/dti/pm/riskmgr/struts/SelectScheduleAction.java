package dti.pm.riskmgr.struts;

import dti.pm.core.struts.PMBaseAction;
import dti.pm.core.struts.AddSelectIndLoadProcessor;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.schedulemgr.ScheduleManager;
import dti.oasis.util.LogUtils;
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
 * Select Schedule page
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   March 19, 2009
 *
 * @author mnadar
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class SelectScheduleAction extends PMBaseAction {
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
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadAllSchedule(mapping, form, request, response);
    }

    /**
     * Load all schedule holder
     * This method is called when there the process parameter "loadAllSchedule"
     * <p/>
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllSchedule(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                          HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllSchedule", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);
            //get policyHeader and inputRecord
            PolicyHeader policyHeader = getPolicyHeader(request, true);
            RecordSet scheduleRs = null;
            AddSelectIndLoadProcessor processer = AddSelectIndLoadProcessor.getInstance();
            scheduleRs = getScheduleManager().loadAllSchedules(policyHeader,processer, getInputRecord(request));

            setDataBean(request, scheduleRs);
            // load grid header
            loadGridHeader(request);
            //load list of values
            loadListOfValues(request, form);
            //add js messages to messagemanager for the current request
            addJsMessages();
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load schedule.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllSchedule", af);
        return af;
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.generateCoi.noselection.error");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getScheduleManager() == null)
            throw new ConfigurationException("The required property 'scheduleManager' is missing.");
    }


    public ScheduleManager getScheduleManager() {
        return m_scheduleManager;
    }

    public void setScheduleManager(ScheduleManager scheduleManager) {
        m_scheduleManager = scheduleManager;
    }

    private ScheduleManager m_scheduleManager;
    protected static final String SCHEDULE_GRID_ID = "scheduleListGrid";

}
