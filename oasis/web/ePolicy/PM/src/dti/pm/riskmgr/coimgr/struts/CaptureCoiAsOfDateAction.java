package dti.pm.riskmgr.coimgr.struts;

import dti.pm.riskmgr.coimgr.CoiFields;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.riskmgr.coimgr.CoiManager;
import dti.oasis.util.LogUtils;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.Record;
import dti.oasis.error.ValidationException;

import java.util.logging.Logger;

/**
 * Action class for display/validate As of Date for Generate COI.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 6, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/30/2012       adeng        Issue 135342 -  Modified display() to add Default as of date .
 * ---------------------------------------------------
 */
public class CaptureCoiAsOfDateAction extends PMBaseAction {
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
            //set effective date of plicy as default as of date.

            Record output = new Record();
            CoiFields.setAsOfDate(output, getPolicyHeader(request).getTermEffectiveFromDate());

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the COI As of Date page.",
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
            getCoiManager().validateAsOfDateForGenerateCoi(policyHeader, inputRecord);

            // Send back xml data
            writeEmptyAjaxXMLResponse(response);
        }
        catch (ValidationException ve) {
            handleValidationExceptionForAjax(ve, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to validate COI as of date.", e, response);
        }

        // Return the forward
        l.exiting(getClass().getName(), "validateAsOfDate", null);
        return null;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getCoiManager() == null)
            throw new ConfigurationException("The required property 'coiManager' is missing.");
    }

    public CoiManager getCoiManager() {
        return m_coiManager;
    }

    public void setCoiManager(CoiManager coiManager) {
        m_coiManager = coiManager;
    }

    public CaptureCoiAsOfDateAction() {
    }
    
    private CoiManager m_coiManager;
}
