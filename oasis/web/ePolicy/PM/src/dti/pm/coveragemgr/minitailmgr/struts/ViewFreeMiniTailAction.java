package dti.pm.coveragemgr.minitailmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.workflowmgr.WorkflowAgent;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.coveragemgr.minitailmgr.MinitailManager;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for view free mini tail.
 * <p/>
 * <p>(C) 2009 Delphi Technology, inc. (dti)</p>
 * Date:   July 29, 2009
 *
 * @author Bhong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public class ViewFreeMiniTailAction extends PMBaseAction {

    /**
     * Load all free mini tail
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllFreeMiniTail(ActionMapping mapping,
                                             ActionForm form,
                                             HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllFreeMiniTail", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadResult";
        try {
            // Secures access to the page, load the Oasis Fields without loading the LOVs,
            //   and map the input parameters to the Fields
            securePage(request, form);

            // Gets policy header
            PolicyHeader policyHeader = getPolicyHeader(request);
            RecordSet rs = getMinitailManager().loadAllFreeMiniTail(policyHeader);
            // Sets data Bean
            setDataBean(request, rs);
            // Load LOVs
            loadListOfValues(request, form);
            // Load grid header bean
            loadGridHeader(request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(
                AppException.UNEXPECTED_ERROR, "Failed to load free mini tail page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllFreeMiniTail", af);
        }

        return af;
    }

    /**
     * This method is called when there the process parameter "closePage"
     * sent in along the requested url.
     * <p/>
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return ActionForward the forward
     * @throws Exception if there are some errors
     */
    public ActionForward closePage(ActionMapping mapping,
                                   ActionForm form,
                                   HttpServletRequest request,
                                   HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "closePage", new Object[]{mapping, form, request, response});
        String forwardString = "closePage";
        try {
            // Get PolicyHeader
            PolicyHeader policyHeader = getPolicyHeader(request);
            // Check if workflow exists, otherwise just forward to the original
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            if (wa.hasWorkflow(policyHeader.getPolicyNo())) {
                // Get the next state
                forwardString = wa.getNextState(policyHeader.getPolicyNo());
                setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
            }
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to determine validation closure method.", e, request, mapping);
        }

        // Done
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "closePage", af);
        return af;
    }

    /**
     * verify config
     */
    public void verifyConfig() {
        if (getMinitailManager() == null)
            throw new ConfigurationException("The required property 'minitailManager' is missing.");
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
    }

    public MinitailManager getMinitailManager() {
        return m_minitailManager;
    }

    public void setMinitailManager(MinitailManager minitailManager) {
        m_minitailManager = minitailManager;
    }

    private MinitailManager m_minitailManager;
}
