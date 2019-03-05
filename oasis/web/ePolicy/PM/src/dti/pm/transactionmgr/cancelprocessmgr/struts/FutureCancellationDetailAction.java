package dti.pm.transactionmgr.cancelprocessmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.LogUtils;
import dti.oasis.recordset.RecordSet;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.transactionmgr.cancelprocessmgr.CancelProcessFields;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Action class for Purge Policy.
 * <p>(C) 2009 Delphi Technology, inc. (dti)</p>
 * Date:   July 10, 2016
 *
 * @author eyin
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/10/2016       eyin        176476 - Added this action for Future Cancellation Details Popup.
 * ---------------------------------------------------
 */

public class FutureCancellationDetailAction extends PMBaseAction {

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
        return captureFutureCancellationDetail(mapping, form, request, response);
    }

    /**
     * perform cancellation
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward captureFutureCancellationDetail(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "captureFutureCancellationDetail",
            new Object[]{mapping, form, request, response});

        String forwardString = "showFutureCancellationDetailsPage";

        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);

            RecordSet rs = new RecordSet();

            Object obj = UserSessionManager.getInstance().getUserSession().get("futureCancellationDetailsGridDataBean");
            if (obj instanceof RecordSet) {
                rs = (RecordSet) obj;
            }

            if(inputRecord.hasStringValue(CancelProcessFields.STATUS) &&
                (CancelProcessFields.getStatus(inputRecord).equalsIgnoreCase(CancelProcessFields.StatusCodeValues.INVALID) ||
                    CancelProcessFields.getStatus(inputRecord).equalsIgnoreCase(CancelProcessFields.StatusCodeValues.WARNING))){
                request.setAttribute(CancelProcessFields.STATUS, CancelProcessFields.getStatus(inputRecord));
            }

            UserSessionManager.getInstance().getUserSession().remove("futureCancellationDetailsGridDataBean");
            setDataBean(request, rs);

            loadGridHeader(request);

            MessageManager.getInstance().addErrorMessage("pm.maintainCancellation.processCancellation.error",
                new String[]{"Future Cancellation Exists."});
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to open Future Cancellation Details page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "captureFutureCancellationDetail", af);
        return af;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
    }
}
