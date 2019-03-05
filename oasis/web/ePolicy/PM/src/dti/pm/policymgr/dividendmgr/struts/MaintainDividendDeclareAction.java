package dti.pm.policymgr.dividendmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.dividendmgr.DividendManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Action class for Maintain Dividend Declaration.
 * <p/>
 * <p>(C) 2012 Delphi Technology, inc. (dti)</p>
 * Date:   March 07, 2012
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

public class MaintainDividendDeclareAction extends PMBaseAction {
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
        return loadAllDividendDeclare(mapping, form, request, response);
    }

    /**
     * Method to load list of available dividend rules.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllDividendDeclare(ActionMapping mapping,
                                                ActionForm form,
                                                HttpServletRequest request,
                                                HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllDividendDeclare", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);

            RecordSet rs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            if (rs == null) {
                rs = getDividendManager().loadAllDividendDeclare(inputRecord);
            }
            // Set loaded distribution data into request
            setDataBean(request, rs);
            inputRecord.setFields(rs.getSummaryRecord(), true);
            // Get LOV labels for initial values
            publishOutputRecord(request, inputRecord);
            
            // Loads list of values
            loadListOfValues(request, form);

            loadGridHeader(request);
            addJsMessages();
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the dividend declaration page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllDividendDeclare", af);
        return af;
    }

    /**
     * Save all dividend rules.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward saveAllDividendDeclare(ActionMapping mapping,
                                                ActionForm form,
                                                HttpServletRequest request,
                                                HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveAllDividendDeclare", new Object[]{mapping, form, request, response});
        String forwardString = "saveResult";

        RecordSet inputRecords = null;
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);

                // Map textXML to RecordSet for input
                inputRecords = getInputRecordSet(request);
                // Save the changes
                getDividendManager().saveAllDividendDeclare(inputRecords);
            }
        } catch (ValidationException ve) {
            // Save the input records into request
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);
            // Handle the validation exception
            handleValidationException(ve, request);
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to save the dividend declaration.", e, request, mapping);
        }

        // Return the forward
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveAllDividendDeclare", af);
        return af;
    }

    /**
     * Get initial values for new added distribution
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward getInitialValuesForAddDividendDeclare(ActionMapping mapping,
                                                               ActionForm form,
                                                               HttpServletRequest request,
                                                               HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForAddDividendDeclare",
            new Object[]{mapping, form, request, response});

        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);
            Record record = getDividendManager().getInitialValuesForAddDividendDeclare(inputRecord);
            // Get LOV labels for initial values
            publishOutputRecord(request, record);

            // Send back xml data
            writeAjaxXmlResponse(response, record);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values for adding dividend declaration.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForAddDividendDeclare", af);
        return af;
    }

    /**
     * Process the selected dividend declarations.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward performProcessDividend(ActionMapping mapping,
                                         ActionForm form,
                                         HttpServletRequest request,
                                         HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "processDividend", new Object[]{mapping, form, request, response});

        Record inputRecord = null;
        try {
            // Secure access to the page without loading the Oasis Fields
            securePage(request, form, false);

            // Map textXML to RecordSet for input
            inputRecord = getInputRecord(request);
            // Process the distribution
            getDividendManager().performProcessDividend(inputRecord);

            writeEmptyAjaxXMLResponse(response);
        } catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to process the dividend declaration.", e, response);
        }

        // Return the forward
        ActionForward af = null;
        l.exiting(getClass().getName(), "processDividend", af);
        return af;
    }

    /**
     * add js messages to message manager for the current request
     */
    private void addJsMessages() {
        // Add message for confirming to calculate dividend
        MessageManager.getInstance().addJsMessage("pm.dividend.process.post.noRecord.select");
        MessageManager.getInstance().addJsMessage("pm.dividend.maintain.process.success.info");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    /**
     * Verify dividendManager and anchorColumnName in spring config
     */
    public void verifyConfig() {
        if (getDividendManager() == null)
            throw new ConfigurationException("The required property 'dividendManager' is missing.");
        if (super.getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
    }

    public DividendManager getDividendManager() {
        return m_dividendManager;
    }

    public void setDividendManager(DividendManager dividendManager) {
        this.m_dividendManager = dividendManager;
    }

    private DividendManager m_dividendManager;

}