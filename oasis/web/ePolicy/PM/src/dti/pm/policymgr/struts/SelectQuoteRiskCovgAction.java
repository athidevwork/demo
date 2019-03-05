package dti.pm.policymgr.struts;

import dti.pm.core.struts.PMBaseAction;
import dti.pm.core.struts.AddSelectIndLoadProcessor;
import dti.pm.core.http.RequestIds;
import dti.oasis.app.ConfigurationException;
import dti.oasis.app.AppException;
import dti.oasis.util.LogUtils;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;
/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 17, 2008
 *
 * @author fcbibire
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class SelectQuoteRiskCovgAction extends PMBaseAction {
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
        request.setAttribute(RequestIds.PROCESS,"loadAllQuoteRiskCovg");
        return loadAllQuoteRiskCovg(mapping, form, request, response);
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
    public ActionForward loadAllQuoteRiskCovg(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllQuoteRiskCovg", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";

        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            // get input
            Record inputRecord = getInputRecord(request);

            // get policy header
            inputRecord.setFields(getPolicyHeader(request).toRecord(), false);

            // add load processor
            RecordLoadProcessor lp = AddSelectIndLoadProcessor.getInstance();

            // load grid content
            RecordSet rs = getPolicyManager().loadAllQuoteRiskCovg(inputRecord, lp);

            // set data bean
            setDataBean(request, rs);

            // load grid header
            loadGridHeader(request);
        }
        catch (Exception e) {
            forwardString =  handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load risk coverage.", e, request, mapping);
        }

        // done
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllQuoteRiskCovg", af);
        return af;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getPolicyManager() == null)
            throw new ConfigurationException("The required property 'policyManager' is missing.");
    }

    public SelectQuoteRiskCovgAction() {}
}

