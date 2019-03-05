package dti.ci.policymgr.struts;

import dti.ci.policymgr.CIPolicyManager;
import dti.ci.struts.action.CIBaseAction;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Displayed locked policies for specific entity in eCIS
 * <p/>
 * <p>(C) 2009 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 04, 2009
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
public class DisplayLockedPolicyAction extends CIBaseAction {
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
        return loadAllLockedPolicy(mapping, form, request, response);
    }

    /**
     * Method to load all locked policies
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllLockedPolicy(ActionMapping mapping,
                                             ActionForm form,
                                             HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllLockedPolicy", new Object[]{mapping, form, request, response});
        }

        String forwardString = "success";
        try {
            securePage(request, form);
            RecordSet rs = getPolicyManager().loadAllLockedPolicy(getInputRecord(request));

            // Set data bean and load grid header bean
            setDataBean(request, rs);
            loadGridHeader(request);
            loadListOfValues(request, form);
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load locked policies.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllLockedPolicy", af);
        }
        return af;
    }

    /**
     * verifyConfig
     */
    public void verifyConfig() {
        if (getPolicyManager() == null) {
            throw new ConfigurationException("The required property 'policyManager' is missing.");
        }
        if (getAnchorColumnName() == null) {
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        }
    }

    public CIPolicyManager getPolicyManager() {
        return m_policyManager;
    }

    public void setPolicyManager(CIPolicyManager policyManager) {
        m_policyManager = policyManager;
    }

    private CIPolicyManager m_policyManager;
}
