package dti.pm.riskmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for view insured information
 * <p/>
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * Date:   June 29, 2012
 *
 * @author tcheng
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 02/21/2013       adeng       139879 - Get policyHeader and riskHeader to display risk name on panel title.
 * ---------------------------------------------------
 */

public class ViewInsuredInfoAction extends PMBaseAction {


    /**
     * This method is triggered automatically when where is no process parameter
     * sent in along the requested url.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadAllInsuredInfo(mapping, form, request, response);
    }

    /**
     * Method to load all insured information.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllInsuredInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                               HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllInsuredInfo", new Object[]{mapping, form, request, response});
        }
        String forwardString = "loadResult";
        try {
            // Secures access to the page, loads the oasis fields without loading LOVs,
            // and map the input parameters into the fields.
            securePage(request, form);
            // Get inputrecord.
            Record inputRecord = getInputRecord(request);
            // Get the policy header from the request, and load the risk header
            PolicyHeader policyHeader = getPolicyHeader(request,true);
            // Get all insured information.
            RecordSet rs = getRiskManager().loadAllInsuredInfo(inputRecord);
            setDataBean(request, rs);

            publishOutputRecord(request, rs.getSummaryRecord());

            loadGridHeader(request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load all insured information page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllInsuredInfo", af);
        return af;
    }

    /**
     * verifyConfig
     */
    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
    }
}