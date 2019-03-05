package dti.pm.componentmgr.experiencemgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.componentmgr.experiencemgr.ExperienceComponentFields;
import dti.pm.componentmgr.experiencemgr.ProcessErpManager;
import dti.pm.componentmgr.experiencemgr.impl.ProcessErpManagerImpl;
import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Action class for Process ERP.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 3, 2011
 *
 * @author ryzhao
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/04/2011       ryzhao      120182 - Added JS message for comments length check.
 * 05/06/2011       ryzhao      120441 - Modified displayProcessErp() to fix the issue.
 *                                       If there is no riskId, load policy header without risk header.
 * 05/06/2011       ryzhao      120440 - Added JS message for no ERP data found.
 * 11/04/2011       ryzhao      126084 - When user has no right to access the specific page, system will throw exception
 *                                       when invoking securePage() method.
 *                                       We should set isPopUpPage first and then call securePage() to ensure we will
 *                                       forward to ErrorPagePopup.jsp page but not ErrorPage.jsp page.
 * 06/13/2018       wrong       192557 - Modified saveAllErp() and deleteErpBatch() to call hasValidSaveToken() to be
 *                                       used for CSRFInterceptor.
 * ---------------------------------------------------
 */
public class ProcessErpAction extends PMBaseAction {

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
        return displayProcessErp(mapping, form, request, response);
    }

    /**
     * Call this method to display Process ERP page.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward displayProcessErp(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "displayProcessErp", new Object[]{mapping, form, request, response});
        String forwardString = "showProcessErpPage";

        RecordSet rs = null;
        boolean isPopUpPage = false;

        try {
            Record inputRecord = getInputRecord(request);

            if (inputRecord.hasStringValue(ExperienceComponentFields.IS_POPUP_PAGE) &&
                ExperienceComponentFields.getIsPopupPage(inputRecord).booleanValue()) {
                isPopUpPage = true;
            }

            securePage(request, form);

            String riskId = request.getParameter("riskId");
            PolicyHeader policyHeader = null;
            if (StringUtils.isBlank(riskId)) {
                policyHeader = getPolicyHeader(request, false);
            }
            else {
                policyHeader = getPolicyHeader(request, true);
            }

            // get default values for search criteria
            Record iniRecord = getProcessErpManager().getDefaultValuesForSearchCriteria(policyHeader, inputRecord);

            // If accessed from policy/risk page, load erp data
            if (iniRecord.hasStringValue(ExperienceComponentFields.POLICY_ID)) {
                rs = getProcessErpManager().loadAllErp(policyHeader, iniRecord);
                request.setAttribute(RequestIds.POLICY_SEARCH_TOTAL_ROWS,
                    rs.getSummaryRecord().getFieldValue(ProcessErpManagerImpl.TOTAL_POLICY_NUMBER_FIELD_NAME));
            }
            else {
                rs = new RecordSet();
                request.setAttribute(RequestIds.POLICY_SEARCH_TOTAL_ROWS, String.valueOf(0));
            }

            setDataBean(request, rs);

            loadGridHeader(request);

            publishOutputRecord(request, iniRecord);

            loadListOfValues(request, form);

            addJsMessages();

        }
        catch (Exception e) {
            if (isPopUpPage) {
                forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to display process ERP page.", e, request, mapping);
            }
            else {
                forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to display process ERP page.", e, request, mapping);
            }
        }

        l.exiting(getClass().getName(), "displayProcessErp", forwardString);
        return mapping.findForward(forwardString);
    }

    /**
     * Call this method to load ERP data per search criteria or load all ERP data.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllErp(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllErp", new Object[]{mapping, form, request, response});
        String forwardString = "showProcessErpPage";

        RecordSet rs = null;
        boolean isPopUpPage = false;

        try {
            Record inputRecord = getInputRecord(request);

            if (inputRecord.hasStringValue(ExperienceComponentFields.POLICY_ID)) {
                isPopUpPage = true;
            }

            securePage(request, form);

            // load ERP data
            rs = getProcessErpManager().loadAllErp(getPolicyHeader(request, true), inputRecord);

            request.setAttribute(RequestIds.POLICY_SEARCH_TOTAL_ROWS,
                rs.getSummaryRecord().getFieldValue(ProcessErpManagerImpl.TOTAL_POLICY_NUMBER_FIELD_NAME));

            setDataBean(request, rs);

            loadGridHeader(request);

            publishOutputRecord(request, inputRecord);

            loadListOfValues(request, form);

            addJsMessages();

        }
        catch (Exception e) {
            if (isPopUpPage) {
                forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load ERP data.", e, request, mapping);
            }
            else {
                forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load ERP data.", e, request, mapping);
            }

        }

        l.exiting(getClass().getName(), "loadAllErp", forwardString);
        return mapping.findForward(forwardString);
    }

    /**
     * Call this method to process ERP.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward processErp(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "processErp", new Object[]{mapping, form, request, response});

        try {
            // Secures access to the page
            securePage(request, form, false);
            Record inputRecord = getInputRecord(request);

            Record outputRecord = getProcessErpManager().processErp(inputRecord);

            writeAjaxXmlResponse(response, outputRecord);
        }
        catch (ValidationException ve) {
            handleValidationExceptionForAjax(ve, response);
        }
        catch (AppException ae) {
            handleErrorForAjax(ae.getMessageKey(), "Failed to process ERP", ae, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to process ERP.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "processErp", af);
        return af;
    }

    /**
     * Call this method to save modified ERP data.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveAllErp(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveAllErp", new Object[]{mapping, form, request, response});

        try {
            if (hasValidSaveToken(request)) {
                // Secures access to the page
                securePage(request, form, false);
                RecordSet inputRecords = getInputRecordSet(request);
                getProcessErpManager().saveAllErp(inputRecords);
                writeEmptyAjaxXMLResponse(response);
            }
        }
        catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            handleErrorForAjax("pm.processErp.saveErp.error", "Failed to save ERP.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "saveAllErp", af);
        return af;
    }

    /**
     * Call this method to delete ERP batch per batch no.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward deleteErpBatch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "deleteErpBatch", new Object[]{mapping, form, request, response});

        try {
            if (hasValidSaveToken(request)) {
                // Secures access to the page
                securePage(request, form, false);
                Record inputRecord = getInputRecord(request);
                Record outputRecord = getProcessErpManager().deleteErpBatch(inputRecord);

                int rc = outputRecord.getIntegerValue("rc");

                // If rc < 0, then there are some policies that could not be deleted from the batch.
                if (rc < 0) {
                    ExperienceComponentFields.setIsDeleteErpBatchSuccess(outputRecord, YesNoFlag.N);
                    String retMsg = outputRecord.getStringValue("retMsg");
                    MessageManager.getInstance().addInfoMessage("pm.processErp.deleteBatch.result", new Object[]{retMsg});
                    // Load all error policies and store data into UserSessionManager with the key 'relatedPolicyErrorsRecordSet' for later display
                    RecordSet relatedPolicyErrors = getProcessErpManager().loadAllErrorPolicy(inputRecord);
                    UserSessionManager.getInstance().getUserSession().set(RELATED_POLICY_ERRORS_RECORDSET, relatedPolicyErrors);
                }
                // If rc >= 0, then deleting was successful.  The message returned by retMsg will be displayed to the user.
                else {
                    ExperienceComponentFields.setIsDeleteErpBatchSuccess(outputRecord, YesNoFlag.Y);
                    String retMsg = outputRecord.getStringValue("retMsg", "Delete ERP Batch Successfully.");
                    MessageManager.getInstance().addInfoMessage("pm.processErp.deleteBatch.result", new Object[]{retMsg});
                }
                writeAjaxXmlResponse(response, outputRecord);
            }
        }
        catch (ValidationException ve) {
            handleValidationExceptionForAjax(ve, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to delete ERP batch.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "deleteErpBatch", af);
        return af;
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.processErp.deleteBatch.noBatchNo.error");
        MessageManager.getInstance().addJsMessage("pm.processErp.deleteBatch.confirmation");
        MessageManager.getInstance().addJsMessage("pm.processErp.saveErp.comments.too.long.error");       
        MessageManager.getInstance().addJsMessage("pm.processErp.noErpDataFound.information");
    }

    /**
     * Verify ProcessErpManager and anchorColumnName in spring config
     */
    public void verifyConfig() {
        if (getProcessErpManager() == null)
            throw new ConfigurationException("The required property 'processErpManager' is missing.");
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
    }

    public ProcessErpManager getProcessErpManager() {
        return m_processErpManager;
    }

    public void setProcessErpManager(ProcessErpManager processErpManager) {
        m_processErpManager = processErpManager;
    }

    private ProcessErpManager m_processErpManager;
    private static final String RELATED_POLICY_ERRORS_RECORDSET = "relatedPolicyErrorsRecordSet";
}
