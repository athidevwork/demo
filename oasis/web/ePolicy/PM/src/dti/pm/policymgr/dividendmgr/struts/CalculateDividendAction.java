package dti.pm.policymgr.dividendmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.dividendmgr.DividendFields;
import dti.pm.policymgr.dividendmgr.DividendManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Action class for calculate dividend.
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 30, 2011
 *
 * @author wfu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
  *
 * ---------------------------------------------------
 */

public class CalculateDividendAction extends PMBaseAction {
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
    public ActionForward unspecified(ActionMapping mapping,
                                     ActionForm form,
                                     HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        return display(mapping, form, request, response);
    }

    /**
     * Method to display calculate dividend page.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward display(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "display", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {
            securePage(request, form);

            // Set accounting date as SysDate default value
            Record record = new Record();
            DividendFields.setAccountingDate(record, DateUtils.formatDate(new Date()));
            publishOutputRecord(request, record);
            // Set loaded distribution data into request
            setDataBean(request, new RecordSet());
            // Loads list of values
            loadListOfValues(request, form);

            addJsMessages();
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the calculate dividend page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "display", af);
        return af;
    }

    /**
     * Calculate the dividend.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward calculateDividend(ActionMapping mapping,
                                           ActionForm form,
                                           HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "calculateDividend", new Object[]{mapping, form, request, response});

        Record inputRecord = null;
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);

                // Map textXML to RecordSet for input
                inputRecord = getInputRecord(request);
                // Save the changes
                getDividendManager().calculateDividend(inputRecord);

                writeEmptyAjaxXMLResponse(response);
            }
        } catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to calculate the dividend.", e, response);
        }

        // Return the forward
        ActionForward af = null;
        l.exiting(getClass().getName(), "calculateDividend", af);
        return af;
    }

    /**
     * Process the validation of input dividend.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward validateCalculateDividend(ActionMapping mapping,
                                                   ActionForm form,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "validateCalculateDividend", new Object[]{mapping, form, request, response});
        String forwardString = "saveResult";

        Record inputRecord = null;
        try {
            // Secure access to the page without loading the Oasis Fields
            securePage(request, form, false);

            inputRecord = getInputRecord(request);

            // Validate the dividend
            getDividendManager().validateCalculateDividend(inputRecord);

            writeEmptyAjaxXMLResponse(response);
        } catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to validate the dividend for calculate.", e, response);
        }

        // Return the forward
        ActionForward af = null;
        l.exiting(getClass().getName(), "validateCalculateDividend", af);
        return af;
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        // Add message for confirming to calculate dividend
        MessageManager.getInstance().addJsMessage("pm.dividend.calculate.process.confirm");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    /**
     * Verify dividendManager in spring config
     */
    public void verifyConfig() {
        if (getDividendManager() == null)
            throw new ConfigurationException("The required property 'dividendManager' is missing.");
    }

    public DividendManager getDividendManager() {
        return m_dividendManager;
    }

    public void setDividendManager(DividendManager dividendManager) {
        this.m_dividendManager = dividendManager;
    }

    private DividendManager m_dividendManager;

}