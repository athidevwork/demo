package dti.pm.policymgr.underlyingpolicymgr.struts;

import dti.pm.core.struts.PMBaseAction;
import dti.pm.coveragemgr.underlyingmgr.UnderlyingCoverageManager;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.underlyingpolicymgr.UnderlyingPolicyManager;
import dti.oasis.util.LogUtils;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
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
 * Action class for select active policy for adding new underlying policy
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 3, 2008
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/15/2018       wrong     188391 - Modified loadAllActivePolicy() to support for Underlying coverage.
 * ---------------------------------------------------
 */
public class SelectActivePolicyAction extends PMBaseAction {
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
        return loadAllActivePolicy(mapping, form, request, response);
    }

    /**
     * Method to load list of active policy for adding new underlying policy.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllActivePolicy(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                 HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllActivePolicy",
            new Object[]{mapping, form, request, response});

        String forwardString = "loadResult";

        try {
            // Secures access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields 
            securePage(request, form);
            // Gets policy header
            PolicyHeader policyHeader = getPolicyHeader(request);
            //get input record
            Record inputRecord = getInputRecord(request);
            RecordSet activePolicyRs;
            // Gets active policy information
            if (request.getParameter("isCoverageLevel") != null) {
                activePolicyRs = getUnderlyingCoverageManager().loadAllActivePolicy(policyHeader, inputRecord);
            } else {
                activePolicyRs = getUnderlyingPolicyManager().loadAllActivePolicy(policyHeader, inputRecord);
            }
            //no data found, add error message
            if(activePolicyRs.getSize() == 0){
                MessageManager.getInstance().addErrorMessage("pm.maintainUnderlyingPolicy.noActivePolicy");
            }
            Record outputRecord = activePolicyRs.getSummaryRecord();
            // Sets data Bean
            setDataBean(request, activePolicyRs);
            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, outputRecord);
            // Load LOV
            loadListOfValues(request, form);
            // Load grid header bean
            loadGridHeader(request);
            // Set all fields readonly
            setFieldsToReadOnly(request, null, true, true);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the active policy page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllActivePolicy", af);
        return af;
    }

    // Configuration constructor and accessor methods
    public void verifyConfig() {
        if (getUnderlyingPolicyManager() == null)
            throw new ConfigurationException("The required property 'underlyingPolicyManager' is missing.");
    }

    public UnderlyingPolicyManager getUnderlyingPolicyManager() {
        return m_underlyingPolicyManager;
    }

    public void setUnderlyingPolicyManager(UnderlyingPolicyManager underlyingPolicyManager) {
        m_underlyingPolicyManager = underlyingPolicyManager;
    }

    public UnderlyingCoverageManager getUnderlyingCoverageManager() {
        return m_underlyingCoverageManager;
    }

    public void setUnderlyingCoverageManager(UnderlyingCoverageManager underlyingCoverageManager) {
        m_underlyingCoverageManager = underlyingCoverageManager;
    }

    private UnderlyingPolicyManager m_underlyingPolicyManager;
    private UnderlyingCoverageManager m_underlyingCoverageManager;

}
