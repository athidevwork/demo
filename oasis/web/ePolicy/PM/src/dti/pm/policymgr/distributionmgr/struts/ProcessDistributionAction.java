package dti.pm.policymgr.distributionmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.distributionmgr.DistributionManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.logging.Logger;

/**
 * Action class for Process Distribution.
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
 * 11/15/2013       xnie        142674 1) Modified loadAllDistribution() and getInitialValuesForAddDistribution() to
 *                                        set grid lov.
 *                                     2) Modified getInitialValuesForAddDistribution() to get default values from
 *                                        workbench and then set to record.
 *                                     3) Added processCatchUp() for process catch up.
 *                                     4) Added addJsMessages().
 * ---------------------------------------------------
 */

public class ProcessDistributionAction extends PMBaseAction {
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
        return loadAllDistribution(mapping, form, request, response);
    }

    /**
     * Method to load list of available distribution.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllDistribution(ActionMapping mapping,
                                             ActionForm form,
                                             HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllDistribution", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);

            RecordSet rs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            if (rs == null) {
                rs = getDistributionManager().loadAllDistribution(inputRecord);
            }
            // Set loaded distribution data into request
            setDataBean(request, rs);

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, inputRecord);

            // Loads list of values
            loadListOfValues(request, form);

            loadGridHeader(request);

            addJsMessages();
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the distribution page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllDistribution", af);
        return af;
    }

    /**
     * Save all distributions.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward saveAllDistribution(ActionMapping mapping,
                                             ActionForm form,
                                             HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveAllDistribution", new Object[]{mapping, form, request, response});
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
                getDistributionManager().saveAllDistribution(inputRecords);
            }
        } catch (ValidationException ve) {
            // Save the input records into request
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);
            // Handle the validation exception
            handleValidationException(ve, request);
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to save the distribution.", e, request, mapping);
        }

        // Return the forward
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveAllDistribution", af);
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
    public ActionForward getInitialValuesForAddDistribution(ActionMapping mapping,
                                                            ActionForm form,
                                                            HttpServletRequest request,
                                                            HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForAddDistribution",
            new Object[]{mapping, form, request, response});

        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request, true);
            Record record = getDistributionManager().getInitialValuesForAddDistribution();
            record.setFields(inputRecord);

            // Get LOV labels for initial values
            publishOutputRecord(request, record);

            // Loads list of values
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, record);

            // Send back xml data
            writeAjaxXmlResponse(response, record);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values for add distribution.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForAddDistribution", af);
        return af;
    }

    /**
     * Process the selected distribution.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward processDistribution(ActionMapping mapping,
                                             ActionForm form,
                                             HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "processDistribution", new Object[]{mapping, form, request, response});
        String forwardString = "saveResult";

        RecordSet inputRecords = null;
        try {
            // Secure access to the page without loading the Oasis Fields
            securePage(request, form, false);

            // Map textXML to RecordSet for input
            inputRecords = getInputRecordSet(request);
            // Process the distribution
            getDistributionManager().processDistribution(inputRecords.getSummaryRecord());
        } catch (ValidationException ve) {
            // Save the input records into request
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);
            // Handle the validation exception
            handleValidationException(ve, request);
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to process the distribution.", e, request, mapping);
        }

        // Return the forward
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "processDistribution", af);
        return af;
    }

    /**
     * Catch up dividend when new rule is declared in new calendar year.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward processCatchUp(ActionMapping mapping,
                                        ActionForm form,
                                        HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "processCatchUp", new Object[]{mapping, form, request, response});
        String forwardString = "saveResult";

        try {
            // Secure access to the page without loading the Oasis Fields
            securePage(request, form, false);
            // Map textXML to RecordSet for input
            Record inputRecord = getInputRecord(request);
            // Catch up the dividend
            getDistributionManager().processCatchUp(inputRecord);
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to process catch up.", e, request, mapping);
        }

        // Return the forward
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "processCatchUp", af);
        return af;
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.process.distribution.catchUp.calendarYear.null.error");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    /**
     * Verify distributionManager and anchorColumnName in spring config
     */
    public void verifyConfig() {
        if (getDistributionManager() == null)
            throw new ConfigurationException("The required property 'distributionManager' is missing.");
        if (super.getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
    }

    public DistributionManager getDistributionManager() {
        return m_distributionManager;
    }

    public void setDistributionManager(DistributionManager distributionManager) {
        this.m_distributionManager = distributionManager;
    }

    private DistributionManager m_distributionManager;

}
