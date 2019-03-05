package dti.pm.transactionmgr.cancelprocessmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.LogUtils;
import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.transactionmgr.cancelprocessmgr.CancelProcessFields;
import dti.pm.transactionmgr.cancelprocessmgr.CancelProcessManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Action class handle view multi cancel info page
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 19, 2008
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/18/2011       syang       121201 - Get recordSet from user session if it is opened before confirmation.
 * 04/26/2012       mli         130502 - Add missing JS messages
 * 09/10/2012       ryzhao      133360 - When there is only WARNING message, we still need to display the cancel info page.
 * 11/19/2015       eyin        167171 - Modified loadAllMultiCancelableInfo(), Add logic to process when recordSet is null.
 * ---------------------------------------------------
 */
public class ViewMultiCancelInfoAction extends PMBaseAction {
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
        return loadAllMultiCancelableInfo(mapping, form, request, response);
    }

    /**
     * load page to show multi cancellation info page
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllMultiCancelableInfo(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllMultiCancelableInfo",
            new Object[]{mapping, form, request, response});

        String forwardString = "loadResult";
        try {
            // Secure access to the page, load the Oasis Fields
            securePage(request, form);

            Record inputRecord = getInputRecord(request);

            // Attempt to get the gridRecordSet out of the request.
            RecordSet rs = null;
            if (RequestStorageManager.getInstance().has(CancelProcessFields.PROCESS_RECORDS)) {
                rs = (RecordSet) RequestStorageManager.getInstance().get(CancelProcessFields.PROCESS_RECORDS);
            }
            else {
                rs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            }
            // If it is opened before confirmation, system get recordSet from user session.
            if(rs == null && inputRecord.hasStringValue(CancelProcessFields.STATUS) &&
                (CancelProcessFields.getStatus(inputRecord).equalsIgnoreCase(CancelProcessFields.StatusCodeValues.INVALID) ||
                 CancelProcessFields.getStatus(inputRecord).equalsIgnoreCase(CancelProcessFields.StatusCodeValues.WARNING))){
               rs = (RecordSet) UserSessionManager.getInstance().getUserSession().get(CancelProcessFields.CONFIRMATION_RECORDS);
            }
            
            setDataBean(request, rs);

            // Make the Summary Record available for output
            Record output = null;
            if (rs != null) {
                output = rs.getSummaryRecord();
            }
            else {
                output = new Record();
            }
            output.setFields(inputRecord, false);

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);

            // Load the list of values after loading the data
            loadListOfValues(request, form);

            //load grid header
            loadGridHeader(request);

            //add js message
            addJsMessages();
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the multi cancel info.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllMultiCancelableInfo", af);
        return af;
    }
   //add js message

    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.matainMultiCancel.noSeledRisk.error");
        MessageManager.getInstance().addJsMessage("pm.matainMultiCancel.noSeledCovg.error");
        MessageManager.getInstance().addJsMessage("pm.matainMultiCancel.noSeledSubCovg.error");
        MessageManager.getInstance().addJsMessage("pm.matainMultiCancel.noSeledComponent.error");
        MessageManager.getInstance().addJsMessage("pm.matainMultiCancel.noSeledCoi.error");

    }
    public void verifyConfig() {
        if (getCancelProcessManager() == null)
            throw new ConfigurationException("The required property 'cancelProcessManager' is missing.");
    }

    public CancelProcessManager getCancelProcessManager() {
        return m_cancelProcessManager;
    }

    public void setCancelProcessManager(CancelProcessManager cancelProcessManager) {
        m_cancelProcessManager = cancelProcessManager;
    }

    private CancelProcessManager m_cancelProcessManager;

}
