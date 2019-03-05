package dti.pm.policymgr.premiummgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.premiummgr.PremiumManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for view member contribution
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   July 13, 2007
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

public class ViewMemberContributionAction extends PMBaseAction {


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
        return loadAllMemberContribution(mapping, form, request, response);
    }

    /**
     * Method to load all member contribution for requested risk.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllMemberContribution(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                   HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllMemberContribution", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadMemberContributionResult";
        try {

            // Secures access to the page, loads the oasis fields without loading LOVs,
            // and map the input parameters into the fields.
            securePage(request, form);
            Record inputRecord = getInputRecord(request);
            RecordSet rs = getPremiumManager().loadAllMemberContribution(inputRecord);
            String riskName = inputRecord.getStringValue("riskDesc");

            //add error msg
            if (rs.getSize() <= 0) {
                MessageManager.getInstance().addErrorMessage("pm.viewMemberContribution.memContList.noDataFound", new Object[]{riskName});
            }
            Record summaryRecord = rs.getSummaryRecord();
            summaryRecord.setFieldValue("riskName", riskName);
            // publish page field
            publishOutputRecord(request, summaryRecord);
            // Sets data bean
            setDataBean(request, rs);
            // Load grid header bean
            loadGridHeader(request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the loadAllMemberContribution page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllMemberContribution", af);
        return af;
    }

    public PremiumManager getPremiumManager() {
        return m_PremiumManager;
    }

    public void setPremiumManager(PremiumManager premiumManager) {
        m_PremiumManager = premiumManager;
    }

    private PremiumManager m_PremiumManager;

    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getPremiumManager() == null)
            throw new ConfigurationException("The required property 'premiumManager' is missing.");
    }
}
