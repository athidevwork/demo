package dti.pm.policymgr.premiummgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.http.RequestIds;
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
 * Action class for view premium accounting data
 * <p/>
 * <p>(C) 2009 Delphi Technology, inc. (dti)</p>
 * Date:   June 29, 2009
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

public class ViewPremiumAccountingAction extends PMBaseAction {


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
        return getInitialValuesForPremiumAccounting(mapping, form, request, response);
    }

    /**
     * Method to get initial values for permium accounting dates.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getInitialValuesForPremiumAccounting(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                              HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForPremiumAccounting", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadPage";
        try {
            // Secures access to the page, loads the oasis fields without loading LOVs,
            // and map the input parameters into the fields.
            securePage(request, form);
            // Get input record
            Record inputRecord = getInputRecord(request);
            // Get the initial values
            RecordSet rs = getPremiumManager().getInitialValuesForPremiumAccounting(inputRecord);
            // Sets data bean
            setDataBean(request, rs);
            // publish page field
            publishOutputRecord(request, rs.getSummaryRecord());
            // Load grid header bean
            loadGridHeader(request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the viewPremiumAccounting page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "getInitialValuesForPremiumAccounting", af);
        return af;
    }

    /**
     * Method to generate premium accounting data.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward generatePremiumAccounting(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                   HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "generatePremiumAccounting", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadResult";
        RecordSet rs = null;
        try {
            // Secures access to the page, loads the oasis fields without loading LOVs,
            // and map the input parameters into the fields.
            securePage(request, form);
            // Get input record
            Record inputRecord = getInputRecord(request);

            rs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            if (rs == null) {
                rs = getPremiumManager().generatePremiumAccounting(inputRecord);
                if (rs.getSize() <= 0) {
                    MessageManager.getInstance().addErrorMessage("pm.viewPremiumAccounting.data.empty");
                }
            }

            // publish page field
            publishOutputRecord(request, inputRecord);
            // Sets data bean
            setDataBean(request, rs);
            // Load grid header bean
            loadGridHeader(request);
        }
        catch (ValidationException v) {
            forwardString = "generateError";
            // Handle the validation exception.
            handleValidationException(v, request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to generate premium accounting data.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "generatePremiumAccounting", af);
        return af;
    }

    /**
     * verifyConfig
     */
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