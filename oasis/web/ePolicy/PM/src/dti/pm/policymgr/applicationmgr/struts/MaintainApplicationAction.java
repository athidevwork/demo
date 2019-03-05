package dti.pm.policymgr.applicationmgr.struts;

import dti.oasis.struts.ActionHelper;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.applicationmgr.ApplicationManager;
import dti.oasis.util.LogUtils;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.pm.policymgr.service.EApplicationInquiryFields;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 17, 2009
 *
 * @author gchitta
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/28/2017       tzeng       166929 - Added initiateApp.
 * ---------------------------------------------------
 */
public class MaintainApplicationAction extends PMBaseAction {
    private final Logger l = LogUtils.getLogger(getClass());
    /**
     * This method is triggered automatically when there is no process parameter sent in along the requested url.
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
        return loadCurrentApplication(mapping, form, request, response);
    }

    /**
     * Load display application list.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadCurrentApplication(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {

        Logger l = LogUtils.enterLog(getClass(), "loadCurrentApplication", new Object[]{mapping, form, request, response});
        String forwardString = "loadCurrentApplication";
        Record inputRecord;
        RecordSet rs;
        try {
            // Secures access to the page, loads the oasis fields and map the input parameters into the fields.
            securePage(request, form);
            // Get all request parameters.
            inputRecord = getInputRecord(request);
            if (!inputRecord.hasField("termDesc")) {
                inputRecord.setFieldValue("termDesc", getPolicyHeader(request).getTermBaseRecordId());
            }
            rs = getApplicationManager().loadApplicationList(inputRecord);

            // Loads list of values
            loadListOfValues(request, form);

            // Add Js messages
            //addJsMessages();

            // Load grid header bean
            loadGridHeader(request);

            // Sets data Bean
            setDataBean(request, rs);
        }

        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR,
                "Failed to load the Application List page.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadCurrentApplication", null);
        return af;
    }

    /**
     * Initate Application
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward initiateApp(ActionMapping mapping,
                                     ActionForm form,
                                     HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "initiateApp", new Object[]{mapping, form, request, response});
        }

        try {
            //Secure page
            securePage(request, form);

            //Get policy header
            PolicyHeader policyHeader = getPolicyHeader(request);

            //Get request userName
            String userName = ActionHelper.getCurrentUserId(request);

            //Get input record from policy header
            Record inputRecord = policyHeader.toRecord();

            //Set application type code.
            String applicationTypeCode = getApplicationManager().getApplicationTypeCode(inputRecord);
            EApplicationInquiryFields.setTypeCode(inputRecord, applicationTypeCode);

            //Initiate Application
            getApplicationManager().initiateAppForUI(inputRecord, userName);

            writeEmptyAjaxXMLResponse(response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to initiate application.", e, response);
        }

        ActionForward af = null;

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "initiateApp", af);
        }
        return af;
    }

    /**
     * Verify ApplicationManager in spring config
     */
    public void verifyConfig() {
        if (getApplicationManager() == null)
            throw new ConfigurationException("The required property 'applicationManager' is missing.");
    }

    public ApplicationManager getApplicationManager() {
        return m_applicationManager;
    }

    public void setApplicationManager(ApplicationManager ApplicationManager) {
        m_applicationManager = ApplicationManager;
    }

    private ApplicationManager m_applicationManager;



}
