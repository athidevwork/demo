package dti.ci.vendormgr.struts;

import dti.ci.core.struts.MaintainEntityFolderBaseAction;
import dti.ci.helpers.ICIVendorConstants;
import dti.ci.vendormgr.VendorManager;
import dti.oasis.app.AppException;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.util.*;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action Class for Vendor.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author Gerald C. Carney
 *         Date:   Apr 15, 2004
 *         <p/>
 *         Revision Date    Revised By  Description
 *         ---------------------------------------------------------------
 *         04/01/2005       HXY         Extends CIBaseAction.
 *         04/07/2005       HXY         Used OasisFields to set up grid header.
 *         04/13/2005       HXY         Added fields to dataMap for grid
 *         size control.
 *         05/02/2005       HXY         Added readOnly vendor address info.
 *         01/17/2007       Fred        Added call to CILinkGenerator.generateLink()
 *         07/06/2007       FWCH        Added method setFieldVisibility() to set fields'(state and country)
 *                                      visibility via isUsaAddress value
 *         04/28/2009       Fred        Removed loading payment info grid code
 *         10/06/2010       wfu         111776: Replaced hardcode string with resource definition
 *         05/13/2011       kshen       Added js message ci.entity.vendor.bankInfoRequiredForEFT.message.
 *         03/06/2012       Parker      130270. set CIS notes visiable for this business.
 *         08/06/2014       wkong       156093: Load dataOfBirth.
 *         01/16/2018       dzhang      190534: If a field display type is not lov display type, the lov sql will
 *                                              be removed when initiate field. avoid to access the lov value since
 *                                              it is null.
 *         10/16/2018       Elvin       Issue 195835: grid replacement, extends MaintainEntityFolderBaseAction
 *         ---------------------------------------------------------------
 */

public class MaintainVendorAction extends MaintainEntityFolderBaseAction implements ICIVendorConstants {

    private final Logger l = LogUtils.getLogger(getClass());

    /**
     * Unspecified
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadVendor(mapping, form, request, response);
    }

    /**
     * Get training list for entity.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadVendor(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadVendor", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadResult";

        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);
            String entityId = getEntityIdForMaintainEntityAction(request);
            inputRecord.setFieldValue("entityId", entityId);

            Record vendorRecord = (Record) request.getAttribute("vendorRecord");
            if (vendorRecord == null) {
                RecordSet rs = getVendorManager().loadVendor(inputRecord);

                if (rs != null && rs.getSize() > 0) {
                    vendorRecord = rs.getFirstRecord();
                } else {
                    vendorRecord = new Record();
                }
            }

            RecordSet vendorAddressRs = getVendorManager().loadVendorAddress(inputRecord);
            if (vendorAddressRs != null && vendorAddressRs.getSize() > 0) {
                vendorRecord.setFields(vendorAddressRs.getFirstRecord());
            }

            // the countComputed can only be 0 or 1 per procedure, no idea why we need to do such validation here but we just keep it
            String countComputed = vendorRecord.getStringValueDefaultEmpty(VND_COUNT_ID);
            if (!StringUtils.isBlank(countComputed) && FormatUtils.isLong(countComputed) && Long.parseLong(countComputed) > 1) {
                MessageManager.getInstance().addWarningMessage("ci.entity.vendor.warning.multipleVendorFound",
                        new String[]{ countComputed, getEntityInfoBean(request, entityId, false).getEntityName() });
            }

            // we only have payment total layer displayed, the payment info layer is not used for now, just keep it
            RecordSet vendorPaymentRs = getVendorManager().loadVendorPayment(inputRecord);
            loadGridHeader(request, null, PAYMENT_TOTALS_GRID_ID, PAYMENT_TOTALS_LAYER);
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, PAYMENT_TOTALS_GRID_ID);
            setDataBean(request, vendorPaymentRs, PAYMENT_TOTALS_GRID_ID);

            publishOutputRecord(request, vendorRecord);

            loadListOfValues(request, form);

            addJsMessages();

            saveToken(request);
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load vendor page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadVendor", af);
        }
        return af;
    }

    /**
     * save Edication Data.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward saveVendor(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveVendor", new Object[]{mapping, form, request, response});
        }

        String forwardString = "saveResult";
        Record inputRec = null;

        try {
            if (hasValidSaveToken(request)) {
                securePage(request, form);
                inputRec = getInputRecord(request);
                getVendorManager().saveVendor(inputRec);
            }
        } catch (ValidationException v) {
            request.setAttribute("vendorRecord", inputRec);
            handleValidationException(v, request);
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to save vendor information.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveVendor", af);
        }
        return af;
    }

    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("ci.common.error.vendor.hasExisted");
        MessageManager.getInstance().addJsMessage("ci.common.error.certifiedDate.after");
        MessageManager.getInstance().addJsMessage("ci.entity.message.payment.before");
        MessageManager.getInstance().addJsMessage("ci.entity.vendor.bankInfoRequiredForEFT.message");
    }

    public VendorManager getVendorManager() {
        return m_vendorManager;
    }

    public void setVendorManager(VendorManager vendorManager) {
        this.m_vendorManager = vendorManager;
    }

    private VendorManager m_vendorManager;
    protected static final String CURRENT_GRID_ID = "currentGridId";
    protected static final String PAYMENT_TOTALS_GRID_ID = "paymentTotalsGrid";
    protected static final String PAYMENT_TOTALS_LAYER = "Vendor_Payment_Total_Grid_Header_Layer";
}
