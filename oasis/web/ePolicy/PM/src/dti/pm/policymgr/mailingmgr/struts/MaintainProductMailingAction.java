package dti.pm.policymgr.mailingmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.mailingmgr.ProductMailingManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for Maintain Product Mailing.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 25, 2013
 *
 * @author AWU
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * ---------------------------------------------------
 */
public class MaintainProductMailingAction extends PMBaseAction {
    /**
     * This method is triggered automatically when there is no process parameter sent in along the requested url.
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
        return loadAllProductMailing(mapping, form, request, response);
    }

    /**
     * This method used to load the list of the product mailing data.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllProductMailing(ActionMapping mapping, ActionForm form,
                                               HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllProductMailing", new Object[]{mapping, form, request, response});
        String forwardString = "loadProductMailing";

        try {
            securePage(request, form);
            Record inputRecord = getInputRecord(request);
            RecordSet rs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            if (rs == null) {
                rs = getProductMailingManager().loadProductMailing(inputRecord);
            }

            // Set loaded data into request
            setDataBean(request, rs);

            Record output = rs.getSummaryRecord();

            // Publish the output record for use by the Oasis Tags and JSP.
            publishOutputRecord(request, output);

            // Set currentGridId to every gridID on page before load gird header
            // then load grid header for each grid.
            loadGridHeader(request);

            // Load the list of values after loading the data.
            loadListOfValues(request, form);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load product mailing page.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllProductMailing", af);
        return af;
    }

    /**
     * Save all the product mailing data.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward saveProductMailing(ActionMapping mapping, ActionForm form,
                                            HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveProductMailing", new Object[]{mapping, form, request, response});
        }

        String forwardString = "saveProductMailing";
        RecordSet inputRecords = null;
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields.
                securePage(request, form, false);
                inputRecords = getInputRecordSet(request);
                getProductMailingManager().saveProductMailing(inputRecords);
            }
        }
        catch (ValidationException v) {
            // Save the recordset into the request
            request.setAttribute("gridRecordSet", inputRecords);

            // Handle the validation exception
            handleValidationException(v, request);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to save all product mailing.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveProductMailing", af);
        return af;
    }

    /**
     * Get initial values for adding product mailing.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward getInitialValuesForProductMailing(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                           HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForProductMailing", new Object[]{mapping, form, request, response});
        try {
            // Secure page.
            securePage(request, form);
            Record defaultRecord = getInputRecord(request, true);
            Record record = getProductMailingManager().getInitialValuesForProductMailing();
            record.setFields(defaultRecord);
            writeAjaxXmlResponse(response, record);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values for add product mailing .", e, response);
        }
        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForProductMailing", af);
        return af;
    }

    /**
     * Verify ProductMailingManager in spring config.
     */
    public void verifyConfig() {
        if (getProductMailingManager() == null) {
            throw new ConfigurationException("The required property 'productMailingManager' is missing.");
        }
    }

    public ProductMailingManager getProductMailingManager() {
        return this.productMailingManager;
    }

    public void setProductMailingManager(ProductMailingManager productMailingManager) {
        this.productMailingManager = productMailingManager;
    }

    private ProductMailingManager productMailingManager;
}
