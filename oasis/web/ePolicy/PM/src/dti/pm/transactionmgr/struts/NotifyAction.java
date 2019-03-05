package dti.pm.transactionmgr.struts;

import dti.pm.core.struts.PMBaseAction;
import dti.pm.core.http.RequestIds;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.validationmgr.SoftValidationManager;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.transactionmgr.NotifyFields;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;
import dti.oasis.util.LogUtils;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageCategory;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.messagemgr.MessageManagerAdmin;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.workflowmgr.WorkflowAgent;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 7, 2007
 *
 * @author Sxm
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/28/2008       fcb         82170: notifyDenyQuote added.
 * 12/06/2013       fcb         148037: changes for performance tuning.
 * 12/16/2016       tzeng       166929: Delete soft validation at the beginning of notify step.
 * ---------------------------------------------------
 */

public class NotifyAction extends PMBaseAction {
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
        request.setAttribute(RequestIds.PROCESS, "notify");
        request.setAttribute("notifyLevel", "OFFICIAL");
        return notify(mapping, form, request, response);
    }

    /**
     * This method is triggered automatically when the process parameter is notifyPreRate
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward notifyPreRate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute(RequestIds.PROCESS, "notify");
        request.setAttribute("notifyLevel", "PRERATE");
        return notify(mapping, form, request, response);
    }

    /**
     * This method is triggered automatically when the process parameter is notifyPostRate
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward notifyPostRate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute(RequestIds.PROCESS, "notify");
        request.setAttribute("notifyLevel", "POSTRATE");
        return notify(mapping, form, request, response);
    }

    /**
     * This method is triggered automatically when the process parameter is notifyDenyQuote
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward notifyDenyQuote(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute(RequestIds.PROCESS, "notify");
        request.setAttribute("notifyLevel", "DENYQUOTE");
        return notify(mapping, form, request, response);
    }

    /**
     * This method is triggered automatically when the process parameter is notifyPostRate
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward notifyOfficial(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute(RequestIds.PROCESS, "notify");
        request.setAttribute("notifyLevel", "OFFICIAL");
        return notify(mapping, form, request, response);
    }

    /**
     * This method is called when the process parameter "notify"
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
    public ActionForward notify(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "notify", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        String notifyLevel1 = (String)request.getAttribute("notifyLevel");

        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            // Get PolicyHeader
            PolicyHeader policyHeader = getPolicyHeader(request);
            Record policyRecord = policyHeader.toRecord();

            // Delete soft validation when pre-rate step
            if (request.getAttribute("notifyLevel").equals(NotifyFields.PRERATE)) {
                getSoftValidationManager().processSoftValidation(policyHeader, null);
            }

            boolean isNotifyConfigured = getTransactionManager().isNotifyConfigured(policyHeader);
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            int notifyCount = 0;
            boolean skipNotify = false;
            RecordSet rs = null;
            String notifyLevel = null;

            if (isNotifyConfigured) {
                // Get notifyLevel request attribute
                notifyLevel = (String) request.getAttribute("notifyLevel");
                policyRecord.setFieldValue("notifyLevel", notifyLevel);

                // Get product notifications
                rs = getTransactionManager().loadAllProductNotifications(policyRecord);

                //get the skipNotify workflowAttribute
                skipNotify = wa.hasWorkflowAttribute(policyHeader.getPolicyNo(),"skipNotify")?
                    ((YesNoFlag)wa.getWorkflowAttribute(policyHeader.getPolicyNo(),"skipNotify")).booleanValue():false;

                // Process product notifications
                notifyCount = rs.getSize();
            }

            if (isNotifyConfigured && notifyCount > 0 && !skipNotify) {
                for (int i = 0; i < notifyCount; i++) {
                    Record record = rs.getRecord(i);

                    if (NotifyFields.getStatus(record).equals("VALID")) {
                        // Set notifications in request
                        ((MessageManagerAdmin) MessageManager.getInstance()).defineMessageForRequest(
                            MessageCategory.getInstance(NotifyFields.getMessageCategory(record)),
                            NotifyFields.getProductNotifyId(record), NotifyFields.getMessage(record),
                            NotifyFields.getDefaultValue(record).equals("1") ? "Y" : "N");
                    }
                }

                // Set notify level back to request for response handling
                request.setAttribute("notifyLevel", notifyLevel);
            }

            // Nothing to notify, move on to the next step
            else {
                // Check if workflow exists, otherwise throw application exception
                if (wa.hasWorkflow(policyHeader.getPolicyNo())) {

                    // Get the next state
                    forwardString = wa.getNextState(policyHeader.getPolicyNo());
                    setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
                }
                else {
                    throw new AppException(AppException.UNEXPECTED_ERROR,
                        "Failed to determine workflow for Notify.");
                }
            }
        }
        catch (Exception e) {
            forwardString =  handleErrorPopup(AppException.UNEXPECTED_ERROR,
                "Failed to process product notify.", e, request, mapping);
        }

        // done
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "notify", af);
        return af;
    }

    /**
     * This method is called when there the process parameter "processResponse"
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
    public ActionForward processResponse(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                         HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "processResponse", new Object[]{mapping, form, request, response});
        String forwardString;

        try {
            // Get PolicyHeader
            PolicyHeader policyHeader = getPolicyHeader(request);

            // Get input
            Record inputRecord = getInputRecord(request);

            // Process response
            String notifyFlag = getTransactionManager().performProductNotificationResponse(policyHeader, inputRecord);

            // Check if workflow exists, otherwise throw application exception
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            if (wa.hasWorkflow(policyHeader.getPolicyNo())) {

                // Set the flag so that workflow knows about the notification response
                if (notifyFlag.equals("INVALID"))
                    wa.setWorkflowTransitionParameter(policyHeader.getPolicyNo(), notifyFlag);

                // Get the next state
                forwardString = wa.getNextState(policyHeader.getPolicyNo());
                setForwardParametersForWorkflow(request, forwardString, policyHeader.getPolicyNo(), wa.getWorkflowInstanceIdName());
            }
            else {
                throw new AppException(AppException.UNEXPECTED_ERROR,
                    "Failed to determine workflow for Notify.");
            }
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR,
                "Failed to process response.", e, request, mapping);
        }

        // Done
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "processResponse", af);
        return af;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getTransactionManager() == null)
            throw new ConfigurationException("The required property 'transactionmanager' is missing.");
    }

    public NotifyAction() {
    }

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    public SoftValidationManager getSoftValidationManager() {
        return m_softValidationManager;
    }

    public void setSoftValidationManager(SoftValidationManager softValidationManager) {
        m_softValidationManager = softValidationManager;
    }

    private TransactionManager m_transactionManager;
    private SoftValidationManager m_softValidationManager;

    private static final String NOTIFY_FIELD_PREFIX = "notifyFiled.";
}
