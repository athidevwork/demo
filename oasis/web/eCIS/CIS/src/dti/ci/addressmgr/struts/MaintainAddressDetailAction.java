package dti.ci.addressmgr.struts;

import dti.ci.addressmgr.AddressFields;
import dti.ci.addressmgr.AddressManager;
import dti.ci.addressmgr.addresslistmgr.AddressListManager;
import dti.ci.struts.action.CIBaseAction;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.http.RequestIds;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
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
 * Date: Dec 16, 2010
 */
/*
 *
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/13/2011    Blake      Modified for issue 120677
 * 08/11/2011    kshen      Modified for issue 99502.
 * 08/22/2011    kshen      Modified for issue 123906.
 * 12/26/2011    parker     address type duplicate check for issue 128197
 * 03/20/2013    kshen      Issue 141836
 * 06/21/2013    kshen      Issue 144776
 * 03/26/2014               Issue 151940: firstly look sysparm, instead of get countycode from record,
 * 09/07/2018    jdingle    Issue 194899: Add back update of sourceRecordFK.
 * 10/31/2018    dzhang     Issue 195835: Add message for js using.
 * ---------------------------------------------------
 */
public class MaintainAddressDetailAction extends CIBaseAction {

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
        return loadAddressDetail(mapping, form, request, response);
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
    public ActionForward loadAddressDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAddressDetail", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadResult";

        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);
            String sourceRecordId = inputRecord.getStringValueDefaultEmpty(AddressFields.SOURCE_RECORD_ID);
            String sourceRecordFK = inputRecord.getStringValueDefaultEmpty(AddressFields.SOURCE_RECORD_F_K);
            if (sourceRecordId.isEmpty()) {
                AddressFields.setSourceRecordId(inputRecord, sourceRecordFK);
                sourceRecordId = sourceRecordFK;
            }
            inputRecord.setFieldValue("actionClassName", getClass().getName());
            Record outRecord = getAddressManager().loadAddressDetailInfo(inputRecord);

            // check whether entity is locked with any policy, this field is used in OBR
            inputRecord.setFieldValue("pk", inputRecord.getStringValueDefaultEmpty("sourceRecordId"));
            Record entityLockRecord = getAddressListManager().getEntityLockFlag(inputRecord);
            outRecord.setFieldValue("entityLockedFlag", entityLockRecord.getStringValueDefaultEmpty(AddressFields.ENTITY_LOCK_FLAG));
            outRecord.setFieldValue(AddressFields.SOURCE_RECORD_F_K,sourceRecordId);

            publishOutputRecord(request, outRecord);

            loadListOfValues(request, form);

            addJsMessages();

            saveToken(request);
        } catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load Address detail.", e, request, mapping);
        }

        ActionForward actionForward = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAddressDetail", actionForward);
        }
        return actionForward;
    }

    /**
     * save address detail
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveAddressDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAddressDetail", new Object[]{mapping, form, request, response});
        }

        String forwardString = "saveResult";
        Record inputRecord = null;

        try {
            if (isTokenValid(request, true)) {
                securePage(request, form);

                inputRecord = getInputRecord(request);
                getAddressManager().saveAddressDetail(inputRecord);

                request.setAttribute("saveSucceed","Y");
            }
        } catch (ValidationException ve) {
            request.setAttribute(RequestIds.DATA_BEAN, inputRecord);
        } catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to save Address Detail.", e, request, mapping);
        }

        ActionForward actionForward = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAddressDetail", actionForward);
        }
        return actionForward;
    }

    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("ci.entity.addressDetail.confirm.primaryAddressChange");
        MessageManager.getInstance().addJsMessage("ci.entity.addressDetail.error.primaryAddressFromDate");
        MessageManager.getInstance().addJsMessage("ci.entity.addressDetail.error.primaryAddressToDate");
        MessageManager.getInstance().addJsMessage("ci.entity.addressDetail.error.transferRoleLater");
        MessageManager.getInstance().addJsMessage("ci.entity.message.address.onlyOneFuturePrimaryAddressAllowed");
    }

    @Override
    public void verifyConfig() {
        if (getAddressManager() == null) {
            throw new ConfigurationException("The required property 'addressManager' is missing.");
        }
        if (getAddressListManager() == null) {
            throw new ConfigurationException("The required property 'addressListManager' is missing.");
        }
    }

    public AddressListManager getAddressListManager() {
        return m_addressListManager;
    }

    public void setAddressListManager(AddressListManager addressListManager) {
        this.m_addressListManager = addressListManager;
    }

    public AddressManager getAddressManager() {
        return m_addressManager;
    }

    public void setAddressManager(AddressManager addressManager) {
        this.m_addressManager = addressManager;
    }

    private AddressListManager m_addressListManager;
    private AddressManager m_addressManager;

}
