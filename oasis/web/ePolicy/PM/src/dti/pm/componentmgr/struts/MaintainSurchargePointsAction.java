package dti.pm.componentmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.componentmgr.ComponentManager;
import dti.pm.policymgr.PolicyHeader;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Action class for Surcharge Points.
 * <p/>
 * <p/>
 * <p>(C) 2007 Delphi Technology, inc. (dti)</p>
 * Date:   May 31, 2007
 *
 * @author Bhong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class MaintainSurchargePointsAction extends PMBaseAction {
    /**
     * This method is triggered automatically when there is no process parameter
     * sent in along the requested url.
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
        return loadAllSurchargePoint(mapping, form, request, response);
    }

    /**
     * Method to load cycle detail.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllSurchargePoint(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                               HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllSurchargePoint",
            new Object[]{mapping, form, request, response});

        String forwardString = "loadResult";

        try {

            // Secures access to the page, load the Oasis Fields without loading the LOVs,
            securePage(request, form);

            // Gets policy header
            PolicyHeader policyHeader = getPolicyHeader(request);

            // Gets all surcharge points
            RecordSet rs = (RecordSet) request.getAttribute(dti.oasis.http.RequestIds.GRID_RECORD_SET);
            if (rs == null) {
                rs = getcomponentManager().loadAllSurchargePoint(policyHeader, getInputRecord(request));
            }

            // Sets data Bean
            setDataBean(request, rs);

            // Load grid header bean
            loadGridHeader(request);

            // Publish out parameter
            publishOutputRecord(request, rs.getSummaryRecord());
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR,
                "Failed to load Surcharge Points page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllSurchargePoint", af);
        return af;
    }

    /**
     * Method to save surcharge points.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveAllSurchargePoint(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                               HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveAllSurchargePoint", new Object[]{mapping, form, request, response});
        ActionForward af;
        String forwardString = "saveResult";
        RecordSet inputRecords = null;
        try {
            // If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure page
                securePage(request, form, false);

                // Parse the Grid XML as a RecordSet, and the request parameters as the Summary Record
                inputRecords = getInputRecordSet(request);

                // Call the business component to implement the validate/save logic
                getcomponentManager().saveAllSurchargePoint(inputRecords);
            }
        }
        catch (ValidationException ve) {
            // Save the input records into request
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);

            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the Surcharge points page.", e, request, mapping);
        }

        af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveAllSurchargePoint", af);
        return af;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getcomponentManager() == null)
            throw new ConfigurationException("The required property 'componentManager' is missing.");
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
    }

    public MaintainSurchargePointsAction() {
    }

    public ComponentManager getcomponentManager() {
        return m_componentManager;
    }

    public void setcomponentManager(ComponentManager componentManager) {
        m_componentManager = componentManager;
    }

    private ComponentManager m_componentManager;
}