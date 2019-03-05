package dti.ci.riskmgr.windowperiedhistorymgr.struts;

import dti.ci.riskmgr.RiskManager;
import dti.ci.struts.action.CIBaseAction;
import dti.oasis.app.AppException;
import dti.oasis.recordset.Record;
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
 * The action class for the Window Period History page.
 *
 * <p>(C) 2014 Delphi Technology, inc. (dti)</p>
 * Date:   2/25/14
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class MaintainWindowPeriodHistoryAction extends CIBaseAction {

    /**
     * This method is triggered automatically when there is no process parameter
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
        return loadAllWindowPeriodHistory(mapping, form, request, response);
    }

    /**
     * Load all RM Window Period information for an entity.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllWindowPeriodHistory(ActionMapping mapping, ActionForm form,
                                                    HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllWindowPeriodHistory", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadAllWindowPeriodHistoryResult";
        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);
            RecordSet rs = getRiskManager().getWindowPeriodHistory(inputRecord);

            setDataBean(request, rs);
            loadGridHeader(request);

            publishOutputRecord(request, inputRecord);

            loadListOfValues(request, form);
            saveToken(request);
        } catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR,
                    "Failed to load Window Period History page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllWindowPeriodHistory", af);
        return af;
    }

    public RiskManager getRiskManager() {
        return m_riskManager;
    }

    public void setRiskManager(RiskManager riskManager) {
        m_riskManager = riskManager;
    }

    private RiskManager m_riskManager;
}
