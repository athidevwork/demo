package dti.pm.policymgr.dividendmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.util.LogUtils;
import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.dividendmgr.DividendFields;
import dti.pm.policymgr.dividendmgr.DividendManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.logging.Logger;

/**
 * Action class for Process Dividend.
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
 * 09/05/2011       ryzhao      124622 - For pages with multiple grids:
 *                              1) Pass gridId as the third parameter to the setDataBean() method
 *                                 for all but the first grid.
 *                              2) Pass gridId/layerId as the third/fourth parameter to the loadGridHeader() method
 *                                 for all but the first grid.
 * ---------------------------------------------------
 */

public class ProcessDividendAction extends PMBaseAction {
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
        return loadAllPriorDividend(mapping, form, request, response);
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
    public ActionForward loadAllPriorDividend(ActionMapping mapping,
                                              ActionForm form,
                                              HttpServletRequest request,
                                              HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllPriorDividend", new Object[]{mapping, form, request, response});
        String forwardString = "loadPriorResult";
        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);

            RecordSet rs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            if (rs == null) {
                rs = getDividendManager().loadAllPriorDividend(inputRecord);
            }

            //Set flag when page is first loaded
            if (!inputRecord.hasField(DividendFields.POLICY_TYPE)) {
                request.setAttribute(DividendFields.IS_FIRST_LOADED, YesNoFlag.Y);
            }

            // Set loaded distribution data into request
            setDataBean(request, rs);
            publishOutputRecord(request, inputRecord);
            // Loads list of values
            loadListOfValues(request, form);
            loadGridHeader(request);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the prior dividend page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllPriorDividend", af);
        return af;
    }

    /**
     * Method to load list of available calculated dividends.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllCalculatedDividend(ActionMapping mapping,
                                                   ActionForm form,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllCalculatedDividend", new Object[]{mapping, form, request, response});
        String forwardString = "loadCalculatedResult";
        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);

            RecordSet rs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            if (rs == null) {
                rs = getDividendManager().loadAllCalculatedDividend(inputRecord);
            }

            // Set loaded distribution data into request
            setDataBean(request, rs, CALCULATED_DIVIDEND_GRID_ID);
            publishOutputRecord(request, rs.getSummaryRecord());
            // Loads list of values
            loadListOfValues(request, form);

            // Set currentGridId to componentListGrid before load component gird header
            setCurrentGridId(CALCULATED_DIVIDEND_GRID_ID);
            loadGridHeader(request, null, CALCULATED_DIVIDEND_GRID_ID, CALCULATED_DIVIDEND_GRID_LAYER_ID);

            addJsMessages();
        }
        catch (Exception e) {
            forwardString = handleErrorIFrame(AppException.UNEXPECTED_ERROR, "Failed to load the calculated dividends.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllCalculatedDividend", af);
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
    public ActionForward postDividend(ActionMapping mapping,
                                      ActionForm form,
                                      HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "postDividend", new Object[]{mapping, form, request, response});

        Record inputRecord = null;
        try {
            // Secure access to the page without loading the Oasis Fields
            securePage(request, form, false);

            // Map textXML to RecordSet for input
            inputRecord = getInputRecord(request);
            // Process the distribution
            getDividendManager().postDividend(inputRecord);

            writeEmptyAjaxXMLResponse(response);
        } catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to post the dividends.", e, response);
        }

        // Return the forward
        ActionForward af = null;
        l.exiting(getClass().getName(), "postDividend", af);
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

    /**
     * Overrides method in BaseAction. To get the anchor column name by current grid Id.
     * This is effective only for the multiple grids.
     *
     * @return anchor column name for the current grid
     */
    public String getAnchorColumnName() {
        Logger l = LogUtils.enterLog(getClass(), "getAnchorColumnName");
        String anchorName;
        if (hasCurrentGridId()) {
            String currentGridId = getCurrentGridId();
            if (currentGridId.equals(CALCULATED_DIVIDEND_GRID_ID)) {
                anchorName = getDetailAnchorColumnName();
            }
            else {
                anchorName = super.getAnchorColumnName();
            }
        }
        else {
            anchorName = super.getAnchorColumnName();
        }
        l.exiting(getClass().getName(), "getAnchorColumnName", anchorName);
        return anchorName;
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
        if (getDetailAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'detailAnchorColumnName' is missing.");
    }

    public DividendManager getDividendManager() {
        return m_dividendManager;
    }

    public void setDividendManager(DividendManager dividendManager) {
        this.m_dividendManager = dividendManager;
    }

    public String getDetailAnchorColumnName() {
        return m_detailAnchorColumnName;
    }

    public void setDetailAnchorColumnName(String detailAnchorColumnName) {
        m_detailAnchorColumnName = detailAnchorColumnName;
    }

    protected static final String CALCULATED_DIVIDEND_GRID_ID = "calculatedDividendListGrid";
    protected static final String CALCULATED_DIVIDEND_GRID_LAYER_ID = "PM_DIVIDEND_PRO_DETAIL_GH";
    private DividendManager m_dividendManager;
    private String m_detailAnchorColumnName;
}
