package dti.pm.tailmgr.struts;

import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.tailmgr.TailFields;
import dti.pm.tailmgr.TailManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 *  Action class for capture finance percent
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 16, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/02/2010       syang      Issue 113780 - Add js message.
 * 04/27/2012       xnie       Issue 132999 - Modified display () to add tailtermBaseRecordId to inputRecordPolicyHeader
 *                             and call getInitialValuesForTailCharge().
 * ---------------------------------------------------
 */

public class CaptureFinancePercentAction extends PMBaseAction {

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
        return display(mapping, form, request, response);
    }


    /**
     * display capture finance percent page
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward display(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "display",
            new Object[]{mapping, form, request, response});
        // Secure access to the page, load the Oasis Fields without loading the LOVs,
        securePage(request, form);

        PolicyHeader policyHeader = getPolicyHeader(request);

        // get tail term base record id value from the form
        Record inputRecord = getInputRecord(request);
        Record initialRecord = getTailManager().getInitialValuesForTailCharge(policyHeader, inputRecord);

        // publish fields with initial values
        publishOutputRecord(request, initialRecord);

        loadListOfValues(request, form);

        addJsMessages();

        ActionForward af = mapping.findForward("success");

        l.exiting(getClass().getName(), "display", af);
        return af;
    }

    /**
     * add js messages for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.maintainTail.captureFinancePercent.invalidRatePercent");
    }

    public void verifyConfig() {
        if (getTailManager() == null)
            throw new ConfigurationException("The required property 'tailManager' is missing.");
    }

    public TailManager getTailManager() {
        return m_tailManager;
    }

    public void setTailManager(TailManager tailManager) {
        m_tailManager = tailManager;
    }

    private TailManager m_tailManager;
}