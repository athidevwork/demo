package dti.ci.addressmgr.struts;

import dti.ci.addressmgr.AddressFields;
import dti.ci.addressmgr.AddressManager;
import dti.ci.struts.action.CIBaseAction;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.http.RequestIds;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * STRUTS Action class for address search / add.
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   July 6, 2005
 *
 * @author hxy
 */

/*
 * Revision Date    Revised By  Description
 * ------------------------------------------------------------
 * 07/10/2007       FWCH        Added code to handle refreshing
 *                              county field when changing state
 * 11/20/2007       FWCH       Added method retrievePrimaryAddressForEntity()
 *                             to write primary address of an entity into response.
 * 10/06/2010       wfu         111776: Replaced hardcode string with resource definition
 * 03/31/2011       Michael    for issue 117406
 * 10/17/2012       kshen      Issue 136646.
 * 10/17/2012       jdingle    Issue 179272.
 * 09/07/2018       dzhang     Issue 194134: Back the primaryAddressForEntity method.
 * ------------------------------------------------------------
*/
public class MaintainAddressSearchAddAction extends CIBaseAction {

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
        return loadAddressSearchAdd(mapping, form, request, response);
    }

    /**
     * Load data for Address Search Add.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAddressSearchAdd(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAddressSearchAdd", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadResult";

        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);
            RecordSet rs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            if (rs == null) {
                rs = getAddressManager().loadAddressSearchAddList(inputRecord);
            }
            setDataBean(request, rs);

            // set the pass in parameters to page
            request.setAttribute(AddressFields.ENTITY_NAME, inputRecord.getFieldValue(AddressFields.ENTITY_NAME));
            request.setAttribute(AddressFields.DUMMY_SOURCE_RECORD_ID, inputRecord.getFieldValue(AddressFields.DUMMY_SOURCE_RECORD_ID));
            request.setAttribute(AddressFields.ORIG_SOURCE_RECORD_ID, inputRecord.getFieldValue(AddressFields.ORIG_SOURCE_RECORD_ID));
            request.setAttribute(AddressFields.ORIG_ADDRESS_ID, inputRecord.getFieldValue(AddressFields.ORIG_ADDRESS_ID));
            request.setAttribute(AddressFields.ORIG_ADDRESS_TYPE_CODE, inputRecord.getFieldValue(AddressFields.ORIG_ADDRESS_TYPE_CODE));
            request.setAttribute(AddressFields.ORIG_SOURCE_TABLE_NAME, inputRecord.getFieldValue(AddressFields.ORIG_SOURCE_TABLE_NAME));
            request.setAttribute(AddressFields.ALLOW_OTHER_CLIENT, inputRecord.getFieldValue(AddressFields.ALLOW_OTHER_CLIENT));
            request.setAttribute(AddressFields.READ_ONLY, inputRecord.getFieldValue(AddressFields.READ_ONLY));

            loadGridHeader(request);

            loadListOfValues(request, form);

            addJsMessages();

            saveToken(request);
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load Address Search Add page.", e, request, mapping);
        }

        ActionForward actionForward = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAddressSearchAdd", actionForward);
        }
        return actionForward;
    }

    /**
     * Save all Address Search Add List.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward saveAddressSearchAdd(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAddressSearchAdd", new Object[]{mapping, form, request, response});
        }

        String forwardString = "saveResult";
        RecordSet inputRecords = null;

        try {
            if (isTokenValid(request, true)) {
                securePage(request, form, false);

                inputRecords = getInputRecordSet(request);
                Record inputRecord = getInputRecord(request);
                inputRecords.setFieldValueOnAll(AddressFields.DUMMY_SOURCE_RECORD_ID, inputRecord.getStringValueDefaultEmpty(AddressFields.DUMMY_SOURCE_RECORD_ID));
                getAddressManager().updateAddressSearchAddList(inputRecords);
            }
        } catch (ValidationException v) {
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to save Address for search add.", e, request, mapping);
        }

        ActionForward actionForward = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAddressSearchAdd", actionForward);
        }
        return actionForward;
    }


    /**
     * Write the primary addressPK and address description to the reponse,
     * if there is no primary address for the entity then use the first valid
     * value instead.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward primaryAddressForEntity(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "primaryAddressForEntity", new Object[]{mapping, form, request, response});
        }

        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);

            String claimsOnlyAddrOnTop = AddressFields.getClaimsOnlyAddrOnTop(inputRecord);
            if (YesNoFlag.getInstance(claimsOnlyAddrOnTop).booleanValue())
                claimsOnlyAddrOnTop = "Y";
            else
                claimsOnlyAddrOnTop = "N";
            AddressFields.setClaimsOnlyAddrOnTop(inputRecord, claimsOnlyAddrOnTop);

            RecordSet rs = getAddressManager().loadAddressSearchAddList(inputRecord);
            response.setContentType("text/xml;charset=utf-8");
            PrintWriter wri = response.getWriter();
            StringBuffer buff = new StringBuffer();
            if (rs.getSize() > 0) {
                Record record = rs.getFirstRecord();
                buff.append(AddressFields.getAddressId(record));
                buff.append("^");
                buff.append(AddressFields.getAddressSingleLine(record));
            }
            wri.write(buff.toString());
            wri.flush();
            wri.close();
        } catch (Exception e) {
            l.throwing(getClass().getName(), "primaryAddressForEntity", e);
            if (!MessageManager.getInstance().hasErrorMessages())
                MessageManager.getInstance().addErrorMessage("ci.generic.error",
                        new Object[]{StringUtils.formatDBErrorForHtml(e.getCause().getMessage())});
        }

        ActionForward af = null;
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "primaryAddressForEntity", af);
        }
        return af;
    }


    /**
     * Get page fields default values when adding new row in grid
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getInitialValues(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValues", new Object[]{mapping, form, request, response});
        }

        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);
            inputRecord.setFieldValue("actionClassName", this.getClass().getName());
            Record initialValues = getAddressManager().getFieldDefaultValues(inputRecord);

            writeAjaxResponse(response, initialValues, true);
        } catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values.", e, response);
        }

        l.exiting(getClass().getName(), "getInitialValues");
        return null;
    }

    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("ci.address.searchAdd.error.futureAddressNotAllowed");
        MessageManager.getInstance().addJsMessage("ci.address.searchAdd.error.expiredAddressForbidden");
        MessageManager.getInstance().addJsMessage("ci.address.searchAdd.error.FutureAddressForbidden");
        MessageManager.getInstance().addJsMessage("ci.address.searchAdd.error.dataNotSaved");
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
