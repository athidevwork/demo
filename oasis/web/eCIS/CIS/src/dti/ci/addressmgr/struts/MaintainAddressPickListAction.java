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
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * User: cyzhao
 * Date: Feb 18, 2011
 */
/*
 *
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class MaintainAddressPickListAction extends CIBaseAction {

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
        return loadAddressPickList(mapping, form, request, response);
    }

    /**
     * Load data for Address List Page.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAddressPickList(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAddressPickList", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadResult";

        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);
            RecordSet outRs = getAddressManager().loadEffectAddressList(inputRecord);

            setDataBean(request, outRs);

            loadListOfValues(request, form);

            loadGridHeader(request);

            addJsMessages();

            saveToken(request);
        } catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load Address Pick List information.", e, request, mapping);
        }

        ActionForward actionForward = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAddressPickList", actionForward);
        }
        return actionForward;
    }


    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("ci.address.addressListMgr.msg.warning.addressNotFound");
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
