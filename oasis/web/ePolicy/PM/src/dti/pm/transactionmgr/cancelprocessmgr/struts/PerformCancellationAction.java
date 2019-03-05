package dti.pm.transactionmgr.cancelprocessmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.messagemgr.MessageManager;
import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.entitlementmgr.EntitlementFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.transactionmgr.cancelprocessmgr.CancelProcessFields;
import dti.pm.transactionmgr.cancelprocessmgr.CancelProcessManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Action class for Maintain Cancel Process.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 4, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/15/2008       fcb         captureCancellationDetail: call to validatePolicyPicture added.
 * 09/01/2010       fcb         111109 - validatePrePerformCancellation added.
 * 01/10/2011       ryzhao      113558 - Set loadRiskHeader value true in validatePrePerformCancellation method.
 * 01/14/2011       ryzhao      113558 - Revert the changes.
 * 01/12/20101      syang       105832 - Retrieve active risk for discipline decline list.
 * 10/19/2011       wfu         125007 - Added logic to catch policy locking or policy picture changing
 *                                       validation exception for correct error message displaying.
 * 07/13/2016       eyin        176476 - Modified performCancellation(), set field futureCancellationExistB as yes if
 *                                       future cancellation exist.
 * ---------------------------------------------------
 */

public class PerformCancellationAction extends PMBaseAction {

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
        return captureCancellationDetail(mapping, form, request, response);
    }


    /**
     * load page to show cancellation page
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward captureCancellationDetail(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "captureCancellationDetail",
            new Object[]{mapping, form, request, response});

        String forwardString = "showCancellationPage";

        try {
            // Secures access to the page, load the Oasis Fields without loading the LOVs,
            //   and map the input parameters to the Fields
            securePage(request, form);

            PolicyHeader policyHeader = getPolicyHeader(request);
            Record inputRecord = getInputRecord(request);

            getTransactionManager().validatePolicyPicture(policyHeader, inputRecord);

            // get initial values for cancellation popup window
            Record outputRecord = getCancelProcessManager().getInitialValuesForCancellation(policyHeader, inputRecord);

            //set data bean
            RecordSet rs = getCancelProcessManager().loadAllActiveRiskForCancellation(policyHeader, inputRecord);

            setDataBean(request, rs);

            loadGridHeader(request);
            
            publishOutputRecord(request, outputRecord);

            loadListOfValues(request, form);
            addJsMessages();
        } catch (ValidationException v) {
            // Set everything to read-only, if there's validation error
            RecordSet rs = new RecordSet();
            Record output = rs.getSummaryRecord();
            setDataBean(request, rs);
            EntitlementFields.setReadOnly(output, true);
            publishOutputRecord(request, output);
            loadListOfValues(request, form);
            // Handle the validation exception
            handleValidationException(v, request);
        } catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to capture Cancellation Data page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "captureCancellationDetail", af);
        return af;
    }

    /**
     * Validations prior to perform cancellation.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward validatePrePerformCancellation(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                            HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "validatePrePerformCancellation", new Object[]{mapping, form, request, response});

        try {
            securePage(request, form, false);

            PolicyHeader policyHeader = getPolicyHeader(request);
            Record inputRecord = getInputRecord(request);

            getCancelProcessManager().validatePrePerformCancellation(policyHeader, inputRecord);

            writeAjaxXmlResponse(response, inputRecord);
        }
        catch (ValidationException ve) {
            // Handle the validation exception
             handleValidationExceptionForAjax(ve, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to pre validate for cancellation.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "validatePrePerformCancellation", af);
        return af;
    }

    /**
     * perform cancellation
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performCancellation(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "performCancellation",
            new Object[]{mapping, form, request, response});
        String forwardString = null;

        try {
            //If the request has valid save token,then process
            if (hasValidSaveToken(request)) {
                // Secures access to the page
                securePage(request, form, false);
                //get policy header from request
                PolicyHeader policyHeader = getPolicyHeader(request);
                Record inputRecord = getInputRecord(request);
                // Get ddl list for policy/risk cancellation.
                RecordSet inputRecords;
                String cancelLevel = CancelProcessFields.getCancellationLevel(inputRecord);
                String markAsDdl = inputRecord.getStringValue(CancelProcessFields.MARK_AS_DDL, "N");
                if (YesNoFlag.getInstance(markAsDdl).booleanValue() && ("POLICY".equalsIgnoreCase(cancelLevel) || "RISK".equalsIgnoreCase(cancelLevel))) {
                    inputRecords = getInputRecordSet(request);
                }
                else {
                    inputRecords = new RecordSet();
                }
                //call cancelProcessManager to process cancellation
                Record outputRecord = getCancelProcessManager().performCancellation(policyHeader, inputRecord, inputRecords);
                writeAjaxXmlResponse(response, outputRecord);
                
            }
        }
        catch (ValidationException e) {
            if(RequestStorageManager.getInstance().has(CancelProcessFields.FUTURECANCELLATIONEXISTB)&&
                RequestStorageManager.getInstance().get(CancelProcessFields.FUTURECANCELLATIONEXISTB).equals(YesNoFlag.Y)){
                Record output = new Record();
                CancelProcessFields.setFutureCancellationExistB(output, YesNoFlag.Y);

                writeAjaxXmlResponse(response, output);
            }
            else{
                handleValidationExceptionForAjax(e, response);
            }
        }
        catch (Exception e) {
            handleErrorForAjax("pm.maintainCancellation.cancellationProcessFailed.error", "Failed to cancel the item.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "performCancellation", af);
        return af;
    }

    /**
     * validate perform cancellation
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward validateCancellationDetail(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "validateCancellationDetail",
            new Object[]{mapping, form, request, response});
        String forwardString = null;

        try {
            // Secures access to the page
            securePage(request, form, false);
            //get policy header from request
            PolicyHeader policyHeader = getPolicyHeader(request);
            Record inputRecord = getInputRecord(request);

            //call cancelProcessManager to validate process cancellation
            getCancelProcessManager().validateCancellationDetail(policyHeader, inputRecord);
            writeEmptyAjaxXMLResponse(response);
        }
        catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            handleErrorForAjax("pm.maintainCancellation.cancellationProcessFailed.error", "Failed to cancel the item.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "validateCancellationDetail", af);
        return af;
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.amalgamation.success.info");
    }

    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getCancelProcessManager() == null)
            throw new ConfigurationException("The required property 'cancelProcessManager' is missing.");
    }

    public CancelProcessManager getCancelProcessManager() {
        return m_cancelProcessManager;
    }

    public void setCancelProcessManager(CancelProcessManager cancelProcessManager) {
        m_cancelProcessManager = cancelProcessManager;
    }

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    private CancelProcessManager m_cancelProcessManager;
    private TransactionManager m_transactionManager;
    
}
