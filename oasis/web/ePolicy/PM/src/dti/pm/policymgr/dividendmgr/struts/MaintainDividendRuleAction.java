package dti.pm.policymgr.dividendmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
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
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 10, 2011
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

public class MaintainDividendRuleAction extends PMBaseAction {
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
        return loadAllDividendRule(mapping, form, request, response);
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
    public ActionForward loadAllDividendRule(ActionMapping mapping,
                                             ActionForm form,
                                             HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllDividendRule", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);

            RecordSet rs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            if (rs == null) {
                rs = getDividendManager().loadAllDividendRule(inputRecord);
            }
            // Set loaded distribution data into request
            setDataBean(request, rs);
            // Get LOV labels for initial values
            publishOutputRecord(request, inputRecord);
            
            // Loads list of values
            loadListOfValues(request, form);

            loadGridHeader(request);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the dividend rules page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllDividendRule", af);
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
    public ActionForward saveAllDividendRule(ActionMapping mapping,
                                             ActionForm form,
                                             HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveAllDividendRule", new Object[]{mapping, form, request, response});
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
                getDividendManager().saveAllDividendRule(inputRecords);
            }
        } catch (ValidationException ve) {
            // Save the input records into request
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);
            // Handle the validation exception
            handleValidationException(ve, request);
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to save the dividend rules.", e, request, mapping);
        }

        // Return the forward
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveAllDividendRule", af);
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
    public ActionForward getInitialValuesForAddDividendRule(ActionMapping mapping,
                                                            ActionForm form,
                                                            HttpServletRequest request,
                                                            HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForAddDividendRule",
            new Object[]{mapping, form, request, response});

        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);
            Record record = getDividendManager().getInitialValuesForAddDividendRule(inputRecord);
            // Get LOV labels for initial values
            publishOutputRecord(request, record);

            // Send back xml data
            writeAjaxXmlResponse(response, record);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values for add distribution.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForAddDividendRule", af);
        return af;
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