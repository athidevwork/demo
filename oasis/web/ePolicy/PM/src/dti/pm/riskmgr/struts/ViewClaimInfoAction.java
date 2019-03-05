package dti.pm.riskmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.componentmgr.ComponentManager;
import dti.pm.core.struts.PMBaseAction;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * This is an action class for view experience discount history - claim information page.
 * <p/>
 * <p>(C) 2018 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 22, 2018
 *
 * @author ryzhao
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/31/2018       ryzhao      188891 - Initial version.
 * ---------------------------------------------------
 */

public class ViewClaimInfoAction extends PMBaseAction {
    /**
     * do this process when no process is specified
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadClaimInfo(mapping, form, request, response);
    }

    /**
     * Method to load claim information of the risk.
     * <p/>
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward loadClaimInfo(ActionMapping mapping,
                                       ActionForm form,
                                       HttpServletRequest request,
                                       HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadClaimInfo", new Object[]{mapping, form, request, response});
        String forwardString = "loadClaimInfo";
        try {

            securePage(request, form);

            Record inputRecord = getInputRecord(request);
            // Load the risks/coverages
            RecordSet rs = getComponentManager().loadClaimInfo(inputRecord);

            if (rs.getSize() == 0) {
                // if no data found, add error message.
                MessageManager.getInstance().addErrorMessage("pm.viewExpDiscHistory.claimInfo.noDataFound.error");
            }

            // Set all data beans to request
            setDataBean(request, rs);

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, inputRecord);

            // Load the list of values after loading the data
            loadListOfValues(request, form);

            // Load grid header
            loadGridHeader(request);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load claim data.",
                e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadClaimInfo", af);
        return af;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getComponentManager() == null)
            throw new ConfigurationException("The required property 'componentManager' is missing.");
    }

    public ViewClaimInfoAction() {
    }

    public ComponentManager getComponentManager() {
        return m_componentManager;
    }

    public void setComponentManager(ComponentManager componentManager) {
        m_componentManager = componentManager;
    }

    private ComponentManager m_componentManager;
}
