package dti.pm.billingmgr.struts;

import dti.oasis.http.Module;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.request.service.WebServiceClientHelper;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.core.http.RequestIds;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.billingmgr.BillingManager;
import dti.oasis.util.LogUtils;
import dti.oasis.recordset.Record;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 1, 2007
 *
 * @author gjlong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/29/2009       yhyang      91531: Add the method validatePolicyRelation().
 * 11/05/2014       awu         145137 - 1. Added buildFMServicePath().
 *                                       2. Modified saveBilling to set the saved records to request.
 *                                          After saving billing data, no need to call billing initial service again.
 * 06/03/2015       cv          163222 - Modified buildFMServicePath to call WebServiceClientHelper to get FM Webservice url.
 * ---------------------------------------------------
 */
public class MaintainBillingAction  extends PMBaseAction {
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
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute(RequestIds.PROCESS,"SETUPBILLING");
        return setupBilling(mapping, form, request, response);
    }

    /**
    * This method is called when there the process parameter "SETUPBILLING"
    * sent in along the requested url. It is used to load the initial values
    * for manage billingsetup page.
    * <p/>
    *
    * @param mapping
    * @param form
    * @param request
    * @param response
    * @return ActionForward
    * @throws Exception
    */
    public ActionForward setupBilling(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {

        Logger l = LogUtils.enterLog(getClass(), "setupBilling", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";

        BillingManager billingMgr = getBillingManager();
        try {
            PolicyHeader policyHeader = getPolicyHeader(request);

            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            // get default values
            Record inputRecord = getInputRecord(request);

            //get the eFM application URL
            buildFMServicePath();

            // load initialRecord conditionally
            Record outputRecord =  (Record) request.getAttribute(RequestIds.INITIAL_VALUES);
            if (outputRecord == null) {
                outputRecord = billingMgr.getInitialValuesForBilling(policyHeader, inputRecord);
            }
            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, outputRecord);
             // Load the list of values
            loadListOfValues(request, form);

            // Add Js messages
            addJsMessages();

            request.setAttribute(RequestIds.IS_NEW_VALUE,"Y");
        }
        catch (Exception e) {
            forwardString =  handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the BillingSetup page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "setupBilling", af);
        return af;
    }

    /**
    * This method is called when there the process parameter "saveBilling"
    * sent in along the requested url.  (The validation logic is called inside the 
    * business component object: BillingManagerImpl)
    * <p/>
    *
    * @param mapping
    * @param form
    * @param request
    * @param response
    * @return ActionForward
    * @throws Exception
    */
    public ActionForward saveBilling(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                              HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveBilling", new Object[]{mapping, form, request, response});
        String forwardString = "saveResult";
        Record inputRecord = null;
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if(hasValidSaveToken(request)) {
                // Secure access to the page, load the Oasis Fields without loading the LOVs,
                // and map the input parameters to the Fields
                securePage(request, form, false);

                // get policyHeader
                PolicyHeader policyHeader = getPolicyHeader(request);

                // get values from the form
                inputRecord = getInputRecord(request);

                //get the eFM application URL
                buildFMServicePath();

                // get BillingManager, and save it.. (it validates the values before the actual save.)
                Record record = getBillingManager().saveBilling(policyHeader,inputRecord);
                request.setAttribute(RequestIds.INITIAL_VALUES, record);

                // Publish the output record for use by the Oasis Tags and JSP
                publishOutputRecord(request, record);

                // Load the list of values
                loadListOfValues(request, form);

                request.setAttribute(RequestIds.IS_NEW_VALUE,"Y");
                // putMessagesIntoRequest(request,record);
            }

        }
        catch (ValidationException ve) {
            inputRecord.setFields(ve.getValidFields(),true);
            request.setAttribute(RequestIds.INITIAL_VALUES, inputRecord);

            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            forwardString =  handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to save the BillingSetup page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveBilling", af);
        return af;
    }

    /**
     * Validate relation of a policy.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward validatePolicyRelation(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                         HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "validatePolicyRelation", new Object[]{mapping, form, request, response});
        try {
            // Secure page.
            securePage(request, form, false);
            // Get values from the form
            Record inputRecord = getInputRecord(request);
            Record record = getBillingManager().getPolicyRelationValue(inputRecord);
            writeAjaxXmlResponse(response, record);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to validate policy relation.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "validatePolicyRelation", af);
        return af;
    }

    /**
     * To validate if an account already exists for an entity
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward validateAccountExistsForEntity(ActionMapping mapping,
                                                        ActionForm form,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "validateAccountExistsForEntity",
                new Object[]{mapping, form, request, response});

        try {
            securePage(request, form, false);

            Record record = getBillingManager().validateAccountExistsForEntity(getInputRecord(request));

            // Send back xml data
            writeAjaxXmlResponse(response, record);

        }
        catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR,
                    "Failed to validate if an account exists for an entity.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "validateAccountExistsForEntity", af);
        return af;
    }

    /**
     * Build the eFM application URL.
     */
    private void buildFMServicePath() {
        String fmWebServicePath = WebServiceClientHelper.getInstance().buildWebServicePath(WebServiceClientHelper.getInstance().ePOLICY_PM,
            WebServiceClientHelper.getInstance().eFM_wsFM);
        RequestStorageManager.getInstance().set("FM_SERVICE_PATH", fmWebServicePath);
    }

    /**
     * Add Js messages
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.maintainBilling.confirm.close");
    }
    
    public BillingManager getBillingManager() {
         return  m_billingManager;
     }

     public void setBillingManager(BillingManager billingManager) {
         m_billingManager = billingManager;
     }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getBillingManager() == null) {
            throw new ConfigurationException("The required property 'billingmanager' is missing.");
        }
    }

    private BillingManager m_billingManager;
}

