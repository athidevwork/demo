package dti.pm.policymgr.struts;

import dti.pm.core.struts.PMBaseAction;
import dti.oasis.util.LogUtils;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   November 16, 2012
 *
 * @author xnie
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/16/2012       xnie        138948 - Initial version.
 * ---------------------------------------------------
 */
public class MassReRateDetailAction extends PMBaseAction {
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
    public ActionForward unspecified(ActionMapping mapping,
                                     ActionForm form,
                                     HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        return loadAllReRateResultDetail(mapping, form, request, response);
    }

    /**
     * Method to load mass rerate result detail
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllReRateResultDetail(ActionMapping mapping,
                                                   ActionForm form,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllReRateResultDetail", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {

            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            // get input
            Record inputRecord = getInputRecord(request);

            // Load mass rerate result detail
            RecordSet rs = getPolicyManager().loadAllReRateResultDetail(inputRecord);
            Record outputRecord = rs.getSummaryRecord();

            // Set all data beans to request
            setDataBean(request, rs);

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, outputRecord);

            // Load grid header
            loadGridHeader(request);

            // Load the list of values after loading the data
            loadListOfValues(request, form);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load mass rerate result page.",
                e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllReRateResultDetail", af);
        return af;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
    }

    public MassReRateDetailAction(){}
}
