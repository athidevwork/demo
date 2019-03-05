package dti.pm.transactionmgr.cancelprocessmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.LogUtils;
import dti.oasis.messagemgr.MessageManager;
import dti.pm.busobjs.PolicyViewMode;
import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.entitlementmgr.EntitlementFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.transactionmgr.cancelprocessmgr.CancelProcessFields;
import dti.pm.transactionmgr.cancelprocessmgr.CancelProcessManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Action class for Maintain Multi Cancel Process.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 19, 2008
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/26/2011       fcb         1) performMultiCancellation(): added logic to reload the dataset when a ValidationException occurs.
 * 08/18/2011       syang       121201 - Added viewMultiCancelConfirmation() to validate multi cancellation transactions.
 * 10/19/2011       wfu         125007 - Added logic to catch policy locking or policy picture changing
 *                                       validation exception for correct error message displaying.
 * 09/10/2012       ryzhao      133360 - When there is only WARNING message, we still need to display the cancel info page.
 * 12/02/2013       jyang       149171 - Roll back 141758's change to load LOV label fields' value in
 *                                       getInitialValuesForXXX method.
 * 08/21/2014       jyang       156829 - Updated performMultiCancellation(), removed useless code because now the
 *                                       performMultiCancellation only work for multi-cancel COI holder, no amalgamation
 *                                       will be done through this method.
 * 11/19/2015       eyin        167171 - Modified performMultiCancellation(), Add logic to process when inputRecord is null.
 * ---------------------------------------------------
 */
public class PerformMultiCancellationAction extends PMBaseAction {
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
        return loadAllCancelableItem(mapping, form, request, response);
    }

    /**
     * load page to show multi cancellation page
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllCancelableItem(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllCancelableItem",
            new Object[]{mapping, form, request, response});

        String forwardString = "loadResult";
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            //First,load policy header without risk header
            PolicyHeader policyHeader = getPolicyHeader(request);
            //get inputRecord from request parameters
            Record inputRecord = getInputRecord(request);

            getTransactionManager().validatePolicyPicture(policyHeader, inputRecord);

            // Attempt to get the gridRecordSet out of the request.  This will be populated
            // on a validation error to provide data to reload the page.
            RecordSet rs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            // Load the schedules
            if (rs == null) {
                rs = getCancelProcessManager().loadAllCancelableItem(policyHeader, inputRecord);
            }
            setDataBean(request, rs);

            // Make the Summary Record available for output
            Record output = rs.getSummaryRecord();

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);

            // Load the list of values after loading the data
            loadListOfValues(request, form);

            //load grid header
            loadGridHeader(request);

            addJsMessages();

        } catch (ValidationException v) {
            // Set everything to read-only, if there's validation error
            RecordSet rs = new RecordSet();
            Record output = rs.getSummaryRecord();
            setDataBean(request, rs);
            EntitlementFields.setReadOnly(output, true);
            output.setFieldValue(CancelProcessFields.IS_PROCESS_AVAILABLE, YesNoFlag.N);
            publishOutputRecord(request, output);
            loadListOfValues(request, form);
            // Handle the validation exception
            handleValidationException(v, request);
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the multi cancel page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllCancelableItem", af);
        return af;
    }


    /**
     * perform multi cancellation
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performMultiCancellation(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "performMultiCancellation",
            new Object[]{mapping, form, request, response});
        //set default fowrwadString to "closePage"
        String forwardString = "closePage";

        RecordSet inputRecords = null;
        Record inputRecord = null;
        PolicyHeader policyHeader = null;
        try {
            //If the request has valid save token,then process
            if (hasValidSaveToken(request)) {
                // Secures access to the page
                securePage(request, form, false);
                //get policy header from request
                policyHeader = getPolicyHeader(request);
                // Generate input records
                inputRecords = getInputRecordSet(request);
                //get inputRecord
                inputRecord = getInputRecord(request);
                //call cancelProcessManager to process multi cancellation
                Record outputRecord = getCancelProcessManager().
                    performMultiCancellation(policyHeader, inputRecords, inputRecord);
                //get multi cancel status from the outputRecord
                String status = CancelProcessFields.getStatus(outputRecord);                

                //if validation error exists
                if (status.equals("INVALID")) {
                    // Save the input records into request 
                    request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);
                    forwardString = "viewMultiCancelInfo";
                }
                //if process multi cancel failed
                else if (status.equals("FAILED")) {
                    forwardString =
                        handleErrorPopup(AppException.UNEXPECTED_ERROR,
                            "Failed to process multi cancellation.", null, request, mapping);                    
                }
                //if process multi cancel successfully
                else {
                    // define if refresh the parent page directly
                    request.setAttribute("refreshPage", outputRecord.getFieldValue("refreshPage"));
                    request.setAttribute(RequestIds.POLICY_VIEW_MODE, PolicyViewMode.WIP.getName());
                }
            }
            addJsMessages();
        }
        catch (ValidationException ve) {
            // In practise, inputRecord could not be null here, set default just in case.
            inputRecord = inputRecord == null ? new Record() : inputRecord;
            // Reload the whole data set set and save it into the request. This is because not all the data set
            // might have been sent to be processed.
            RecordSet rs = getCancelProcessManager().loadAllCancelableItem(policyHeader, inputRecord);
            setDataBean(request, rs);
            // Handle the validation exception
            handleValidationException(ve, request);
            //if the request come from view multi cancel info page, redirect back to that page
            if (CancelProcessFields.getProcessCode(inputRecord).equals(PROCESS_CODE_CONTINUE)) {
                request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);
                forwardString = "viewMultiCancelInfo";
            }
            else {
                forwardString = "loadAllCancelItem";
            }
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to perform multi cancellation.",
                e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "performMultiCancellation", af);
        return af;
    }


    /**
     * Get initial values for muli cancel page
     * When cancel level is changed on the page, this method is called to return page entitlement indicators
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getInitialValueForMultiCancel(ActionMapping mapping,
                                                       ActionForm form,
                                                       HttpServletRequest request,
                                                       HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValueForMultiCancel",
            new Object[]{mapping, form, request, response});

        try {
			//Secure page and load all Oasis fields value include lov label fields -149171.
            securePage(request, form);
            PolicyHeader policyHeader = getPolicyHeader(request, true);

            Record inputRecord = getInputRecord(request);

            // Get inital values
            Record record = getCancelProcessManager().getInitialValueForMultiCancel(policyHeader, inputRecord);

            // Send back xml data which contains the page entitlement indicators
            writeAjaxXmlResponse(response, record, true);

        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValueForMultiCancel", af);
        return af;
    }


    /**
     * rate policy for multi cancel
     * this method is called in Ajax mode, when process multi cancel finishes
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward ratePolicyForMultiCancel(ActionMapping mapping,
                                                  ActionForm form,
                                                  HttpServletRequest request,
                                                  HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "ratePolicyForMultiCancel",
            new Object[]{mapping, form, request, response});

        try {
            securePage(request, form, false);
            PolicyHeader policyHeader = getPolicyHeader(request, true);

            // Get inital values
            Record record = getCancelProcessManager().ratePolicyForMultiCancel(policyHeader);

            // Send back xml data which contains rete results
            writeAjaxXmlResponse(response, record, true);

        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "failed rating Policy for Multi Cancel.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "ratePolicyForMultiCancel", af);
        return af;
    }

    /**
     * View the selected risk/coverage/coverage class/component confirmation.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward viewMultiCancelConfirmation(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "viewMultiCancelConfirmation", new Object[]{mapping, form, request, response});

        try {
            // Secures access to the page.
            securePage(request, form, false);
            // Get policy header
            PolicyHeader policyHeader = getPolicyHeader(request);
            // Get input record.
            Record inputRecord = getInputRecord(request);
            // Get the selected risk/coverage/coverage class/component from the record set.
            RecordSet inputRecords = getInputRecordSet(request);
            // Validate all cancellation transactions.
            Record validateRecord = getCancelProcessManager().validateMultiCancelConfirmation(policyHeader, inputRecords, inputRecord);
            // Add the recordSet to user session, it will be removed while starting to save as official in confirmation page.
            UserSessionManager.getInstance().getUserSession().set(CancelProcessFields.CONFIRMATION_RECORDS, inputRecords);
            // Check validation result. System forwards to view cancel information page if it is invalid.
            String valStatus = CancelProcessFields.getStatus(validateRecord);
            if (CancelProcessFields.StatusCodeValues.INVALID.equals(valStatus) || CancelProcessFields.StatusCodeValues.WARNING.equals(valStatus)) {
                // If validation error exists, system overrides confirmationRecords by processRecords(contain some error message).
                // it will be used in both view cancel information and confirmation page. 
                RecordSet rs = (RecordSet) RequestStorageManager.getInstance().get(CancelProcessFields.PROCESS_RECORDS);
                UserSessionManager.getInstance().getUserSession().set(CancelProcessFields.CONFIRMATION_RECORDS, rs);
            }
            writeAjaxXmlResponse(response, validateRecord);
        }
        catch (ValidationException ve) {
            handleValidationException(ve, request);
            writeEmptyAjaxXMLResponse(response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "View multi cancel confirmation failed", e, response);
        }
        
        l.exiting(getClass().getName(), "viewMultiCancelConfirmation");
        return null;
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.amalgamation.success.info");
        MessageManager.getInstance().addJsMessage("pm.matainMultiCancel.noSeledRisk.error");
        MessageManager.getInstance().addJsMessage("pm.matainMultiCancel.noSeledCovg.error");
        MessageManager.getInstance().addJsMessage("pm.matainMultiCancel.noSeledSubCovg.error");
        MessageManager.getInstance().addJsMessage("pm.matainMultiCancel.noSeledComponent.error");
        MessageManager.getInstance().addJsMessage("pm.matainMultiCancel.noSeledCoi.error");
    }

    public void verifyConfig() {
        if (getCancelProcessManager() == null)
            throw new ConfigurationException("The required property 'cancelProcessManager' is missing.");
    }

    public CancelProcessManager getCancelProcessManager() {
        return m_cancelProcessManager;
    }

    public void setCancelProcessManager(CancelProcessManager cancelProcessManager) {
        m_cancelProcessManager = cancelProcessManager;
    }

    private CancelProcessManager m_cancelProcessManager;

    private static final String PROCESS_CODE_CONTINUE = "CONTINUE";

}
