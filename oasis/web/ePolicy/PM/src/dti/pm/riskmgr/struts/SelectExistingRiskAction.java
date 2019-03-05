package dti.pm.riskmgr.struts;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.core.struts.AddSelectIndLoadProcessor;
import dti.oasis.util.LogUtils;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;

import java.util.logging.Logger;

/**
 * this class is a class for select existing risk
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 9, 2007
 *
 * @author zlzhu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * Oct 9, 2007          zlzhu     Created
 * ---------------------------------------------------
 */

public class SelectExistingRiskAction extends PMBaseAction {
    /**
     * This method is triggered automatically when there is no process parameter sent in along the requested url.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadAllExistingRisk(mapping, form, request, response);
    }

    /**
    * This method is called when there the process parameter "loadAllExistingRisk"
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
    public ActionForward loadAllExistingRisk(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllExistingRisk", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);
            AddSelectIndLoadProcessor processer = AddSelectIndLoadProcessor.getInstance();
            Record rec = getInputRecord(request);
            RecordSet rs = getRiskManager().loadAllExistingRisk(getPolicyHeader(request,false),rec,processer);
            setDataBean(request, rs);
            // load grid header
            loadGridHeader(request);
            //add js messages to messagemanager for the current request
            addJsMessages();
        }
        catch (Exception e) {
            forwardString =  handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load existing risks.", e, request, mapping);
        }

        // done
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllExistingRisk", af);
        return af;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getRiskManager() == null)
            throw new ConfigurationException("The required property 'riskManager' is missing.");
    }

    public SelectExistingRiskAction() {}
    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.existingRisk.missing.error");
    }
}
