package dti.ci.addressmgr.struts;

import dti.ci.addressmgr.AddressManager;
import dti.ci.addressmgr.impl.RoleGroupRecordLoadProcessor;
import dti.ci.core.CIFields;
import dti.ci.struts.action.CIBaseAction;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordLoadProcessorChainManager;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.AddSelectIndLoadProcessor;
import dti.oasis.struts.IOasisAction;
import dti.oasis.tags.OasisFields;
import dti.oasis.util.LogUtils;
import dti.oasis.util.PageBean;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * User: cyzhao
 * Date: Feb 16, 2011
 */
/*
 *
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class MaintainAddressRoleChangeAction extends CIBaseAction {

    private final Logger l = LogUtils.getLogger(getClass());

    /**
     * This method is triggered automatically when there is no process parameter sent in along the requested url.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadAddressRoleChg(mapping, form, request, response);
    }

    /**
     * Load data for Address Role List Page.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAddressRoleChg(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAddressRoleChg", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadResult";

        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);

            loadListOfValues(request, form);

            List groupList = (List) request.getAttribute("roleGroup_GHLOV");
            RoleGroupRecordLoadProcessor groupProcessor = new RoleGroupRecordLoadProcessor(groupList);
            RecordLoadProcessor loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(AddSelectIndLoadProcessor.getInstance(), groupProcessor);
            RecordSet rsAddressRoleChg = getAddressManager().loadChangeAddressRoles(inputRecord, loadProcessor);

            setDataBean(request, rsAddressRoleChg);

            loadGridHeader(request);

            addJsMessages();

            saveToken(request);
        } catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load Address Role List.", e, request, mapping);
        }

        ActionForward actionForward = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAddressRoleChg", actionForward);
        }
        return actionForward;
    }

    /**
     * Load data for Address Role List Warning Page.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAddressRoleChgWarning(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAddressRoleChgWarning", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadWarningResult";

        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);

            loadListOfValues(request, form);

            List groupList = (List) request.getAttribute("roleGroup_GHLOV");
            RoleGroupRecordLoadProcessor groupProcessor = new RoleGroupRecordLoadProcessor(groupList);
            RecordLoadProcessor loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(AddSelectIndLoadProcessor.getInstance(), groupProcessor);
            RecordSet rsAddressRoleChg = getAddressManager().loadChangeAddressRoles(inputRecord, loadProcessor);

            setDataBean(request, rsAddressRoleChg);

            loadGridHeader(request);

            setFieldsToReadOnly((OasisFields) request.getAttribute(CIFields.KEY_FIELDS), true, null);

            PageBean bean = (PageBean) request.getAttribute(IOasisAction.KEY_PAGEBEAN);
            bean.setTitle(MessageManager.getInstance().formatMessage("ci.address.addressRoleChgMgr.title.addressRoleChgWarningPageTitle"));

            addJsMessages();

            saveToken(request);
        } catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load Address Role List.", e, request, mapping);
        }

        ActionForward actionForward = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAddressRoleChgWarning", actionForward);
        }
        return actionForward;
    }

    /**
     * Update Address Role
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward updateAddressRoles(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "updateAddressRoles", new Object[]{mapping, form, request, response});
        }

        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);
            getAddressManager().updateAddressRoles(inputRecord);

            writeAjaxResponse(response, "Y");
        } catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to transfer Address Roles.", e, response);
        }

        l.exiting(getClass().getName(), "updateAddressRoles");
        return null;
    }

    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("ci.address.addressRoleChgMgr.msg.warning.toAddressNotSelected");
        MessageManager.getInstance().addJsMessage("ci.address.addressRoleChgMgr.msg.error.failToTransfer");
        MessageManager.getInstance().addJsMessage("ci.address.addressRoleChgMgr.msg.addressRolesTransferred");
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
