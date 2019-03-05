package dti.pm.transactionmgr.coirenewalmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.transactionmgr.coirenewalmgr.CoiRenewalManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is an action for COI Renewal
 * <p/>
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 17, 2010
 *
 * @author Dzhang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/05/2010       dzhang      Rename Action name & method name.
 * ---------------------------------------------------
 */

public class CreateCoiRenewalAction extends PMBaseAction {

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
        return captureCoiRenewal(mapping, form, request, response);
    }

    /**
     * load all the COI renewal data
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward captureCoiRenewal(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "captureCoiRenewal", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadResult";
        try {
            securePage(request, form);
            Record inputRecord = (Record) request.getAttribute(SAVED_INPUT_RECORD);
            //retrieve save input
            if (inputRecord == null) {
                inputRecord = new Record();
            }

            publishOutputRecord(request, inputRecord);
            loadListOfValues(request, form);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the COI Renewal page.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "captureCoiRenewal", af);
        }
        return af;
    }

    /**
     * save COI renewal data
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward createCoiRenewal(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                          HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "createCOIRenewal", new Object[]{mapping, form, request, response});
        }

        String forwardString = "saveResult";
        Record inputRecord = getInputRecord(request);
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form);
                getCoiRenewalManager().createCoiRenewal(inputRecord);
            }
        }
        catch (ValidationException ve) {
            // Save the input records into request
            request.setAttribute(SAVED_INPUT_RECORD, inputRecord);
            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            forwardString = handleError("pm.createCoiRenewal.system.error", "Failed to save the COI Renewal data.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);

        MessageManager.getInstance().addInfoMessage("pm.createCoiRenewal.success.info");

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "createCOIRenewal", af);
        }
        return af;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    /**
     * verify config
     */
    public void verifyConfig() {
        if (getCoiRenewalManager() == null)
            throw new ConfigurationException("The required property 'CoiRenewalManager' is missing.");
    }

    /**
     * get CoiRenewalManager
     *
     * @return CoiRenewalManager
     */
    public CoiRenewalManager getCoiRenewalManager() {
        return m_coiRenewalManager;
    }

    /**
     * set CoiRenewalManager
     *
     * @param coiRenewalManager COIRenewalProcess manager
     */
    public void setCoiRenewalManager(CoiRenewalManager coiRenewalManager) {
        m_coiRenewalManager = coiRenewalManager;
    }

    protected static final String SAVED_INPUT_RECORD = "savedInputRecord";
    private CoiRenewalManager m_coiRenewalManager;
}

