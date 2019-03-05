package dti.ci.vendormgr.struts;

import dti.ci.addressmgr.AddressFields;
import dti.ci.addressmgr.AddressManager;
import dti.ci.core.struts.MaintainEntityFolderBaseAction;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.http.RequestIds;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action Class for Vendor address.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author Hong Yuan
 *         Date:   Apr 26, 2005
 *         <p/>
 *         Revision Date    Revised By  Description
 *         ---------------------------------------------------------------
 *         01/17/2007       Fred        Added call to CILinkGenerator.generateLink()
 *         07/06/2007       FWCH        Added method setFieldsProperties() to set fields'
 *                                      properties via isUsaAddress's value
 *         12/02/2008       kshen       Add system parameter "ZIP_CODE_ENABLE", "ZIP_OVERRIDE_ADDR",
 *                                      and "CS_SHOW_ZIPCD_LIST" into request.
 *         01/15/2010       kshen       Removed the codes about set fields readonly for usa/non-usa address.
 *         03/06/2012       Parker      130270. set CIS notes visiable for this business.
 *         08/25/2014       Elvin       Issue 155305: save correct province since it's not refreshed in form
 *         09/12/2014       Elvin       Issue 155376: set default values for all fields when initializing
 *         03/05/2018       dzhang      Issue 109177: vendor address refactor
 *         06/29/2018       ylu         Issue 194117: update for CSRF security.
 *         ---------------------------------------------------------------
 */

public class MaintainVendorAddressAction extends MaintainEntityFolderBaseAction {

    private final Logger l = LogUtils.getLogger(getClass());

    /**
     * The default execute when there is no process parameter
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    protected ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadVendorAddress(mapping, form, request, response);
    }

    /**
     * Load entity vendor address
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    public ActionForward loadVendorAddress(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadVendorAddress", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadResult";

        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);
            String entityId = inputRecord.getStringValueDefaultEmpty(AddressFields.PK);
            if (StringUtils.isBlank(entityId)) {
                entityId = inputRecord.getStringValueDefaultEmpty(AddressFields.ENTITY_ID);
            } else {
                inputRecord.setFieldValue(AddressFields.ENTITY_ID, entityId);
            }
            inputRecord.setFieldValue(AddressFields.ACTION_CLASS_NAME, getClass().getName());
            Record outRecord = (Record) request.getAttribute(RequestIds.DATA_BEAN);
            if (outRecord == null) {
                outRecord = getAddressManager().loadVendorAddress(inputRecord);
            }

            publishOutputRecord(request, outRecord);

            loadListOfValues(request, form);

            addJsMessages();

            saveToken(request);
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load Vendor Address information.", e, request, mapping);
        }

        ActionForward actionForward = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadVendorAddress", actionForward);
        }
        return actionForward;
    }

    /**
     * save vendor address info
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    public ActionForward saveVendorAddress(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveVendorAddress", new Object[]{mapping, form, request, response});
        }

        String forwardString = "saveResult";
        Record inputRecord = null;

        try {
            if (isTokenValid(request, true)) {
                securePage(request, form);

                inputRecord = getInputRecord(request);
                getAddressManager().saveVendorAddress(inputRecord);
            }
        } catch (ValidationException e) {
            request.setAttribute(RequestIds.DATA_BEAN, inputRecord);
        } catch (Exception ex) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to save Vendor Address information.", ex, request, mapping);
        }

        ActionForward actionForward = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveVendorAddress", actionForward);
        }
        return actionForward;
    }

    private void addJsMessages() {

    }

    /**
     * check if the address manager inject successfully.
     */
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
