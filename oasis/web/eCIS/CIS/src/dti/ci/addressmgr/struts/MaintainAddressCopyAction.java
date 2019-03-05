package dti.ci.addressmgr.struts;

import dti.ci.addressmgr.AddressManager;
import dti.ci.struts.action.CIBaseAction;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
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
 * Action Class for Address Copy
 * <p/>
 * <p>(C) 2007 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 06, 2007
 *
 * @author bhong
 */

/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * 10/06/2010       wfu         111776: Replaced hardcode string with resource definition
 * ---------------------------------------------------
*/
public class MaintainAddressCopyAction extends CIBaseAction {

    private final Logger l = LogUtils.getLogger(getClass());

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
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadAddressCopy(mapping, form, request, response);
    }

    /**
     * Load data for Address Copy Page.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAddressCopy(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAddressCopy", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadResult";

        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);
            RecordSet outRs = getAddressManager().loadEntityRelation(inputRecord);
            setDataBean(request, outRs);

            loadListOfValues(request, form);

            loadGridHeader(request);

            addJsMessages();

            request.setAttribute("entityId", inputRecord.getStringValue("entityId"));
            request.setAttribute("addressId", inputRecord.getStringValue("addressId"));

            saveToken(request);
        } catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load Address Copy information.", e, request, mapping);
        }

        ActionForward actionForward = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAddressCopy", actionForward);
        }
        return actionForward;
    }

    /**
     * Copy Address
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward copyAddress(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "copyAddress", new Object[]{mapping, form, request, response});
        }

        try {
            securePage(request, form);

            RecordSet inputRecords = getInputRecordSet(request);
            getAddressManager().performAddressCopy(inputRecords);

            writeAjaxResponse(response, "Y");
        } catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to copy Address.", e, response);
        }

        l.exiting(getClass().getName(), "copyAddress");
        return null;
    }

    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("ci.common.error.exit.noSelect");
        MessageManager.getInstance().addJsMessage("ci.entity.message.address.copied");
    }

    @Override
    public void verifyConfig() {
        if (getAddressManager() == null) {
            throw new ConfigurationException("The required property 'addressManager' is missing.");
        }
    }

    public AddressManager getAddressManager() {
        return m_addressManager;
    }

    public void setAddressManager(AddressManager addressManager) {
        this.m_addressManager = addressManager;
    }

    private AddressManager m_addressManager;

}