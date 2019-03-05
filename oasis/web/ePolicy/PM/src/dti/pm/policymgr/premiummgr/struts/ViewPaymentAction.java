package dti.pm.policymgr.premiummgr.struts;

import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.premiummgr.PremiumManager;
import dti.pm.policymgr.PolicyHeader;
import dti.oasis.app.ConfigurationException;
import dti.oasis.app.AppException;
import dti.oasis.util.LogUtils;
import dti.oasis.recordset.RecordSet;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Action class for view payment
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 19, 2008
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public class ViewPaymentAction extends PMBaseAction {

     /**
     * This method is triggered automatically when where is no process parameter
     * sent in along the requested url.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        return loadAllPayment(mapping, form, request, response);
    }

    /**
     * Load all payment information of the request policy
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllPayment(ActionMapping mapping, ActionForm form,HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllPayment", new Object[]{mapping, form, request, response});
        }
        String forwardString = "loadPaymentResult";
        try {
            // Secures access to the page, loads the oasis fields and map the input parameters into the fields.
            securePage(request, form);
            // Gets Policy Header                            
            PolicyHeader policyHeader = getPolicyHeader(request);
            // Gets RecordSet
            RecordSet rs = getPremiumManager().loadAllPayment(policyHeader);
            // Sets data bean
            setDataBean(request, rs);
            // Load grid header bean
            loadGridHeader(request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the loadAllPayment page.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllPayment", af);
        return af;
    }

    //verify spring config
    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getPremiumManager() == null)
            throw new ConfigurationException("The required property 'premiumManager' is missing.");
    }

    public PremiumManager getPremiumManager() {
        return m_PremiumManager;
    }

    public void setPremiumManager(PremiumManager premiumManager) {
        m_PremiumManager = premiumManager;
    }

    private PremiumManager m_PremiumManager;
}
