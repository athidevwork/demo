package dti.pm.policymgr.dividendmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
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
 * Action class for View and Post Dividend.
 * <p/>
 * <p>(C) 2012 Delphi Technology, inc. (dti)</p>
 * Date:   March 13, 2012
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

public class MaintainDividendAction extends PMBaseAction {
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
        return loadAllProcessedDividend(mapping, form, request, response);
    }

    /**
     * Method to load list of available prior dividends.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllProcessedDividend(ActionMapping mapping,
                                                  ActionForm form,
                                                  HttpServletRequest request,
                                                  HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllProcessedDividend", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);

            RecordSet rs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            if (rs == null) {
                rs = getDividendManager().loadAllProcessedDividend(inputRecord);
            }

            // Set loaded distribution data into request
            setDataBean(request, rs);
            inputRecord.setFields(rs.getSummaryRecord(), true);
            publishOutputRecord(request, inputRecord);
            // Loads list of values
            loadListOfValues(request, form);
            loadGridHeader(request);
            addJsMessages();
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the processed dividend.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllProcessedDividend", af);
        return af;
    }

    /**
     * Post the selected dividends.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward performPostDividend(ActionMapping mapping,
                                      ActionForm form,
                                      HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "performPostDividend", new Object[]{mapping, form, request, response});

        Record inputRecord = null;
        try {
            // Secure access to the page without loading the Oasis Fields
            securePage(request, form, false);

            // Map textXML to RecordSet for input
            inputRecord = getInputRecord(request);
            // Process the distribution
            getDividendManager().performPostDividend(inputRecord);

            writeEmptyAjaxXMLResponse(response);
        } catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to post the dividends.", e, response);
        }

        // Return the forward
        ActionForward af = null;
        l.exiting(getClass().getName(), "performPostDividend", af);
        return af;
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        // Add message for confirming to post dividend
        MessageManager.getInstance().addJsMessage("pm.dividend.process.post.confirm");
        MessageManager.getInstance().addJsMessage("pm.dividend.process.post.noRecord.select");
        MessageManager.getInstance().addJsMessage("pm.dividend.process.post.success.info");
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
