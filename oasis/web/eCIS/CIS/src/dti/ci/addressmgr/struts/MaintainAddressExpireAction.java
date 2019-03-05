package dti.ci.addressmgr.struts;

import dti.ci.addressmgr.AddressManager;
import dti.ci.struts.action.CIBaseAction;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

import dti.oasis.util.LogUtils;

/**
 * <p>Expire form action class</p>
 * This action is only for expiring a Non-primary addresses.
 * If expiring a non-primary address, system will always force
 * users to creating a new address
 *
 * <p>(C) 2007 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 1, 2007
 *
 * @author kshen
 */

/**
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/06/2010       wfu         111776: Replaced hardcode string with resource definition
 * 02/17/2012       Leo         130057: Refactor
 * 10/17/2012       jdingle     Issue 179272.
 * ---------------------------------------------------
 */
public class MaintainAddressExpireAction extends CIBaseAction {

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
        return loadAddressForExpire(mapping, form, request, response);
    }

    /**
     * Load data for Address Detail page.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAddressForExpire(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAddressForExpire", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadAddressExpire";

        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);
            inputRecord.setFieldValue("actionClassName", getClass().getName());
            Record outRecord = getAddressManager().loadAddressForExpire(inputRecord);
            publishOutputRecord(request, outRecord);

            loadListOfValues(request, form);

            addJsMessages();

            saveToken(request);
        } catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load Address Expire page.", e, request, mapping);
        }

        ActionForward actionForward = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAddressForExpire", actionForward);
        }
        return actionForward;
    }
    
    /**
     * Expire Address.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward expireAddress(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "expireAddress", new Object[]{mapping, form, request, response});
        }

        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);
            getAddressManager().expireNonPrimaryAddress(inputRecord);

            writeAjaxResponse(response, "Y");
        } catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to expire Address.", e, response);
        }

        l.exiting(getClass().getName(), "expireAddress");
        return null;
    }

    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("ci.entity.message.address.createNew");
        MessageManager.getInstance().addJsMessage("ci.entity.message.address.cannotBeExpired");        
        MessageManager.getInstance().addJsMessage("ci.entity.message.address.expiredSuccessfully");
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
