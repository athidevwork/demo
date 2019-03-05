package dti.pm.riskmgr.struts;

import dti.pm.core.struts.PMBaseAction;
import dti.pm.core.http.RequestIds;
import dti.oasis.app.ConfigurationException;
import dti.oasis.app.AppException;
import dti.oasis.util.LogUtils;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 20, 2007
 *
 * @author Sxm
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public class SelectRiskTypeAction extends PMBaseAction {
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
        request.setAttribute(RequestIds.PROCESS,"loadAllRiskType");
        return loadAllRiskType(mapping, form, request, response);
    }

    /**
    * This method is called when there the process parameter "findAllRiskType"
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
    public ActionForward loadAllRiskType(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllRiskType", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            // get input
            Record inputRecord = getInputRecord(request);
            
            // get policy header
            inputRecord.setFields(getPolicyHeader(request).toRecord(), false);

            // load grid content
            RecordSet rs = getRiskManager().loadAllRiskType(inputRecord);
            setDataBean(request, rs);
            request.setAttribute("openWhichWindow",inputRecord.getStringValue("openWhichWindow",""));
            // load grid header
            loadGridHeader(request);
        }
        catch (Exception e) {
            forwardString =  handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load risk types.", e, request, mapping);
        }

        // done
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllRiskType", af);
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

    public SelectRiskTypeAction() {}
}
