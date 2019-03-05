package dti.pm.coveragemgr.manuscriptmgr.struts;

import dti.pm.core.struts.PMBaseAction;
import dti.pm.core.struts.AddSelectIndLoadProcessor;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.coveragemgr.manuscriptmgr.ManuscriptManager;
import dti.oasis.util.LogUtils;
import dti.oasis.recordset.RecordLoadProcessor;
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
 * Action class for manuscript selection.
 * <p/>
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 20, 2007
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
public class SelectManuscriptAction extends PMBaseAction {
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
        return loadAllAvailableManuscript(mapping, form, request, response);
    }

    /**
     * Method to load list of available coverage.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllAvailableManuscript(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                  HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllAvailableManuscript",
            new Object[]{mapping, form, request, response});

        String forwardString = "success";

        try {
            // Secures access to the page, load the Oasis Fields without loading the LOVs,
            //   and map the input parameters to the Fields
            securePage(request, form);

            // Gets policy header
            PolicyHeader policyHeader = getPolicyHeader(request);

            // Loads available coverages for selection
            RecordLoadProcessor loadProcessor = AddSelectIndLoadProcessor.getInstance();
            RecordSet rs = getManuscriptManager().loadAllAvailableManuscript(policyHeader, loadProcessor);

            // Sets data Bean
            setDataBean(request, rs);

            // Load grid header bean
            loadGridHeader(request);

            // Add Js messages
            addJsMessages();
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load select manuscript page.",
                e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllAvailableManuscript", af);
        return af;
    }

    /**
     * Add Js messages
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.maintainManu.addManu.noSelection.error");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getManuscriptManager() == null)
            throw new ConfigurationException("The required property 'manuscriptManager' is missing.");
    }

    public ManuscriptManager getManuscriptManager() {
        return m_manuscriptManager;
    }

    public void setManuscriptManager(ManuscriptManager manuscriptManager) {
        m_manuscriptManager = manuscriptManager;
    }

    public SelectManuscriptAction(){}

    private ManuscriptManager m_manuscriptManager;
}
