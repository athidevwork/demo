package dti.pm.policymgr.additionalinsuredmgr.struts;

import dti.pm.policymgr.additionalinsuredmgr.AdditionalInsuredFields;
import dti.pm.policymgr.additionalinsuredmgr.AdditionalInsuredManager;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.oasis.util.LogUtils;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.Record;
import dti.oasis.error.ValidationException;

import java.util.logging.Logger;

/**
 * Action class for display/validate As of Date for Generate Additional Insured.
 * <p/>
 * <p>(C) 2013 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 27, 2013
 *
 * @author xnie
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 02/27/2013       xnie        138026 -  Initial version.
 * ---------------------------------------------------
 */
public class CaptureAddInsAsOfDateAction extends PMBaseAction {
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
     * Method to load As of Date.
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

            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);
            //set effective date of policy as default as of date.

            Record output = new Record();
            AdditionalInsuredFields.setAsOfDate(output, getPolicyHeader(request).getTermEffectiveFromDate());

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the Additional Insured As of Date page.",
                e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "display", af);
        return af;
    }

    /**
     * To validate As of Date.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward validateAsOfDate(ActionMapping mapping,
                                          ActionForm form,
                                          HttpServletRequest request,
                                          HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "validateAsOfDate", new Object[]{mapping, form, request, response});

        try {
            // If the request has valid save token, then proceed with save; if not forward to load page.
            // Secure access to the page without loading the Oasis Fields
            securePage(request, form, false);

            // Get policy header
            PolicyHeader policyHeader = getPolicyHeader(request);

            // Map As of Date field to RecordSet for input
            Record inputRecord = getInputRecord(request);

            // Save the changes
            getAdditionalInsuredManager().validateAsOfDateForGenerateAddIns(policyHeader, inputRecord);

            // Send back xml data
            writeEmptyAjaxXMLResponse(response);
        }
        catch (ValidationException ve) {
            handleValidationExceptionForAjax(ve, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to validate Additional Insured as of date.", e, response);
        }

        // Return the forward
        l.exiting(getClass().getName(), "validateAsOfDate", null);
        return null;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getAdditionalInsuredManager() == null)
            throw new ConfigurationException("The required property 'additionalInsuredManager' is missing.");
    }

    public AdditionalInsuredManager getAdditionalInsuredManager() {
        return m_additionalInsuredManager;
    }

    public void setAdditionalInsuredManager(AdditionalInsuredManager additionalInsuredManager) {
        m_additionalInsuredManager = additionalInsuredManager;
    }

    public CaptureAddInsAsOfDateAction() {
    }
    
    private AdditionalInsuredManager m_additionalInsuredManager;
}
