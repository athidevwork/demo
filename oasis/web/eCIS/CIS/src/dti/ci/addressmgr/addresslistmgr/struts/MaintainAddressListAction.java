package dti.ci.addressmgr.addresslistmgr.struts;

import dti.ci.addressmgr.AddressFields;
import dti.ci.addressmgr.addresslistmgr.AddressListManager;
import dti.ci.core.struts.MaintainEntityFolderBaseAction;
import dti.oasis.app.AppException;
import dti.oasis.error.ValidationException;
import dti.oasis.http.RequestIds;
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
 * <p>Address List Action Class.</p>
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author Gerald C. Carney
 *         Date:   Jan 23, 2004
 *         <p/>
 *         Revision Date    Revised By  Description
 *         ---------------------------------------------------------------------
 *         04/01/2005       HXY         Extends CIBaseAction.
 *         04/08/2005       HXY         Used OasisFields to set up grid header.
 *         09/21/2006       ligj        Issue #62554
 *         01/17/2007       Fred        Added calling CILinkGenerator.generateLink()
 *         02/05/2007       kshen       Modified retrieving address list method with county desc
 *         for displaying them on address list page. (Issue 61440)
 *         10/16/2007       kshen       Removed codes about setting message into request. - using
 *         Application Resources in jsp instead.
 *         11/27/2008       Leo         Issue 88568.
 *         10/06/2010       wfu         111776: Replaced hardcode string with resource definition
 *         03/21/2011       Leo         105792: Mail Option
 *         07/25/2011       kshen       Added method changePrimaryAddress.
 *         08/09/2011       Michael Li  for issue101250
 *         08/09/2011       kshen       Changed for issue 123063.
 *         11/09/2011       parker      Changed for issue 124494 alert null msg when no record selected.
 *         03/19/2012       Parker      Change for issue 130837,pop up message to inform user select one record
 *         04/18/2013       Elvin       Issue 141148: add js message for primaryAddressB check
 *         05/02/2013       kshen       Issue 141148.
 *         09/16/2016       ylu         Issue 179400.
 *         06/26/2018       ylu         Issue 194117: update for CSRF security.
 *         <p/>
 *         ---------------------------------------------------------------------
 */

public class MaintainAddressListAction extends MaintainEntityFolderBaseAction {

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
        return loadAddressList(mapping, form, request, response);
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
    public ActionForward loadAddressList(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAddressList", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadResult";
        RecordSet outRs = null;

        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);
            outRs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            if (outRs == null) {
                outRs = getAddressListManager().loadAddressList(inputRecord);
            }

            setDataBean(request, outRs);

            loadListOfValues(request, form);

            loadGridHeader(request);

            // Add For issue 88568
            Record entityLockedRecord = getAddressListManager().getEntityLockFlag(inputRecord);
            request.setAttribute(AddressFields.ENTITY_LOCK_FLAG, entityLockedRecord.getStringValueDefaultEmpty(AddressFields.ENTITY_LOCK_FLAG));

            addJsMessages();
            saveToken(request);
        } catch (ValidationException e) {
            request.setAttribute(RequestIds.GRID_RECORD_SET, outRs);
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load Address List information.", e, request, mapping);
        }

        ActionForward actionForward = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAddressList", actionForward);
        }
        return actionForward;
    }

    /**
     * Get the number of address roles Info
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward getNumOfAddrRoleInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getNumOfAddrRoleInfo", new Object[]{mapping, form, request, response});
        }

        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);
            Record recResult = getAddressListManager().getNumOfAddrRole(inputRecord);
            recResult.setFields(inputRecord);
            writeAjaxXmlResponse(response, recResult);
        } catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get the number of address roles information.", e, response);
        }

        l.exiting(getClass().getName(), "getNumOfAddrRoleInfo");
        return null;
    }

    /**
     * Get the number of primary address roles Info
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward getNumOfPrimaryAddrRoleInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getNumOfPrimaryAddrRoleInfo", new Object[]{mapping, form, request, response});
        }

        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);
            Record recResult = getAddressListManager().getNumOfPrimaryAddrRoleInfo(inputRecord);
            writeAjaxXmlResponse(response, recResult);
        } catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get the number of primary address roles information.", e, response);
        }

        l.exiting(getClass().getName(), "getNumOfPrimaryAddrRoleInfo");
        return null;
    }

    /**
     * Get the number of primary address roles Info
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward changePrimaryAddress(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "changePrimaryAddress", new Object[]{mapping, form, request, response});
        }

        String forwardString = "saveResult";
        RecordSet inputRecords = null;

        try {
            if (isTokenValid(request)) {
                securePage(request, form);

                Record inputRecord = getInputRecord(request);
                inputRecords = getInputRecordSet(request);
                getAddressListManager().changePrimaryAddress(inputRecord);
            }
        } catch (ValidationException v) {
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Unable to change primary address.", e, request, mapping);
        }

        ActionForward actionForward = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "changePrimaryAddress", actionForward);
        }
        return actionForward;
    }

    /**
     * Save all Address.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveAllAddress(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllAddress", new Object[]{mapping, form, request, response});
        }

        String forwardString = "saveResult";
        RecordSet inputRecords = null;

        try {
            if (isTokenValid(request)) {
                securePage(request, form);

                inputRecords = getInputRecordSet(request);
                getAddressListManager().saveAllAddress(inputRecords);
            }
        } catch (ValidationException v) {
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to save Address.", e, request, mapping);
        }

        ActionForward actionForward = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllAddress", actionForward);
        }
        return actionForward;
    }

    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("ci.entity.addressesList.error.lockedAddress");
        MessageManager.getInstance().addJsMessage("ci.entity.addressesList.error.modifyNotAllowed");
        MessageManager.getInstance().addJsMessage("ci.entity.addressesList.confirm.changeToPrimaryAddress");
        MessageManager.getInstance().addJsMessage("ci.entity.addressesList.confirm.primaryAddressToDate");
        MessageManager.getInstance().addJsMessage("ci.entity.addressDetail.error.transferRoleLater");
        MessageManager.getInstance().addJsMessage("ci.entity.addressesList.error.expiredAddressReadOnly");
        MessageManager.getInstance().addJsMessage("ci.entity.addressesList.error.primaryAddressNoBulkModify");
        MessageManager.getInstance().addJsMessage("ci.entity.addressesList.error.addressUsedInMailOption");
        MessageManager.getInstance().addJsMessage("ci.entity.addressesList.confirm.beforeSaveMailOption");
        MessageManager.getInstance().addJsMessage("ci.entity.addressesList.error.percentNotEntered");
        MessageManager.getInstance().addJsMessage("ci.entity.addressesList.error.percentInvalid");
    }

    public AddressListManager getAddressListManager() {
        return m_addressListManager;
    }

    public void setAddressListManager(AddressListManager m_addressListManager) {
        this.m_addressListManager = m_addressListManager;
    }

    private AddressListManager m_addressListManager;
}

