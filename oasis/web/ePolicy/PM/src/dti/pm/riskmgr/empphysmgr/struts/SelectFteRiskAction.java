package dti.pm.riskmgr.empphysmgr.struts;

import dti.pm.core.struts.PMBaseAction;
import dti.pm.core.struts.AddSelectIndLoadProcessor;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.riskmgr.empphysmgr.EmployedPhysicianManager;
import dti.oasis.util.LogUtils;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 30, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class SelectFteRiskAction extends PMBaseAction {

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
        return loadAllFteRisk(mapping, form, request, response);
    }


    /**
     * load page to show Fte Risks for selection
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllFteRisk(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllFteRisk", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            //get parameters from request
            Record inputRecord = getInputRecord(request);

            // Get the policy header from the request
            PolicyHeader policyHeader = getPolicyHeader(request);

            // Load the FTE risks
            RecordLoadProcessor selectIndProcessor = AddSelectIndLoadProcessor.getInstance();
            RecordSet fteRiskRs = getEmployedPhysicianManager().loadAllFteRisk(policyHeader, inputRecord, selectIndProcessor);

            setDataBean(request, fteRiskRs);

            if (fteRiskRs.getSize() == 0) {
                MessageManager.getInstance().addErrorMessage("pm.selectFteRisk.noFteRisk.error");
            }

            //publish page entitlement info
            publishOutputRecord(request, fteRiskRs.getSummaryRecord());

            // Load tail grid header
            loadGridHeader(request);

            // add js messages to messagemanager for the current request
            addJsMessages();

        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load FTE Risks for selection.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllFteRisk", af);
        return af;
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.selectFteRisk.noSelectedRisk.error");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getEmployedPhysicianManager() == null)
            throw new ConfigurationException("The required property 'employedPhysicianManager' is missing.");
    }


    public EmployedPhysicianManager getEmployedPhysicianManager() {
        return m_employedPhysicianManager;
    }

    public void setEmployedPhysicianManager(EmployedPhysicianManager employedPhysicianManager) {
        m_employedPhysicianManager = employedPhysicianManager;
    }

    private EmployedPhysicianManager m_employedPhysicianManager;

}
