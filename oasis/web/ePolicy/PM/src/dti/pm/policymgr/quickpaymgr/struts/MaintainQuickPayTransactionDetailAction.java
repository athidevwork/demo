package dti.pm.policymgr.quickpaymgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.error.ValidationException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.quickpaymgr.QuickPayManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * This is an action class for manage quick pay transaction detail.
 * <p/>
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 27, 2010
 *
 * @author dzhang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/19/2010       dzhang      Update per Bill's comments.
 * 09/09/2010       dzhangd     #103800 - Modified loadAllQuickPayTransactionDetail().
 * ---------------------------------------------------
 */

public class MaintainQuickPayTransactionDetailAction extends PMBaseAction {

    /**
     * do this process when no process is specified
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadAllQuickPayTransactionDetail(mapping, form, request, response);
    }

    /**
     * Method to load quick pay data.
     * <p/>
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward loadAllQuickPayTransactionDetail(ActionMapping mapping,
                                                          ActionForm form,
                                                          HttpServletRequest request,
                                                          HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllQuickPayTransactionDetail", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {

            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);
            Record inputRecord = getInputRecord(request);

            // Get data for transaction summary
            Record output = getQuickPayManager().loadTransactionSummary(inputRecord);
            if (output == null)  {
                output = new Record();
                // if no data found, add error message.
                MessageManager.getInstance().addErrorMessage("pm.quickPayTransactionDetail.summaryList.noDataFound.error");
            }

            // Get recorset and set databean
            RecordSet rs = (RecordSet) request.getAttribute("gridRecordSet");
            if (rs == null) {
                // Get data bean for the grid - risks/coverages
                Record r = new Record();
                r.setFields(inputRecord);
                if("ADD_QPDISCOUNT".equals(inputRecord.getStringValue("openMode"))) {
                  r.setFieldValue("lastQpTransLogId", request.getAttribute("lastQpTransLogId") == null? "0": request.getAttribute("lastQpTransLogId"));
                }               
                rs = getQuickPayManager().loadAllRiskCoverageForTransactionDetail(r);
                if (rs.getSize() > 0) {
                    output.setFields(rs.getSummaryRecord());
                }
                else {
                    // if no data found, add warning message.
                    MessageManager.getInstance().addErrorMessage("pm.quickPayTransactionDetail.riskCoverageList.noDataFound.error");
                }
            }

            // Set all data beans to request
            setDataBean(request, rs);

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);

            // Load grid header
            loadGridHeader(request);

            request.setAttribute("lastQpTransLogId", request.getAttribute("lastQpTransLogId"));  
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the Quick Pay Transaction Detail page.",
                e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllQuickPayTransactionDetail", af);
        return af;
    }

    /**
     * Method to save all quick pay transaction detail records.
     * <p/>
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */

    public ActionForward saveAllQuickPayTransactionDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveAllQuickPayTransactionDetail", new Object[]{mapping, form, request, response});
        String forwardString = "saveResult";
        RecordSet inputRecords = getInputRecordSet(request);
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);

                // Map textXML to RecordSet for input
                inputRecords = getInputRecordSet(request);

                // Set back the new quick pay transaction log id to inputRecord, the risks/coverages grid depend on this field.
                String lastQpTransLogId = getQuickPayManager().saveAllQuickPayTransactionDetail(inputRecords);
                request.setAttribute("lastQpTransLogId", lastQpTransLogId == null ? "0" : lastQpTransLogId);
            }

        }
        catch (ValidationException ve) {
            // Save the input records into request
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);

            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            forwardString = handleError("pm.quickPayTransactionDetail.save.error", "Failed to save the quick pay transaction detail page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveAllQuickPayTransactionDetail", af);
        return af;
    }


    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getQuickPayManager() == null)
            throw new ConfigurationException("The required property 'quickPayManager' is missing.");
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
    }

    public MaintainQuickPayTransactionDetailAction() {
    }

    public QuickPayManager getQuickPayManager() {
        return m_quickPayManager;
    }

    public void setQuickPayManager(QuickPayManager quickPayManager) {
        m_quickPayManager = quickPayManager;
    }

    private QuickPayManager m_quickPayManager;
}
