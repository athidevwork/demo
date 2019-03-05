package dti.pm.transactionmgr.renewalprocessmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policyattributesmgr.PolicyAttributesManager;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.transactionmgr.batchrenewalprocessmgr.BatchRenewalProcessManager;
import dti.pm.transactionmgr.renewalprocessmgr.RenewalProcessManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Action class for Maintain Renewal Process.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 16, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/23/2016       tzeng       167531 - Modified performAutoRenewal to judge if need add message and renewal event id
 *                                       to session when click auto renew.
 * 08/15/2016       tzeng       177134 - Remove previous 167531 changes to RenewalProcessManagerImpl.
 * 10/18/2016       lzhang      180263 - Add checkPRTConfirmationRequired():invoke
 *                                       getRenewalProcessManager().checkPRTConfirmationRequired().
 * ---------------------------------------------------
 */

public class RenewPolicyAction extends PMBaseAction {

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
        return renewPolicy(mapping, form, request, response);
    }

    /**
     * renew policy
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward renewPolicy(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {

        Logger l = LogUtils.enterLog(getClass(), "renewPolicy", new Object[]{mapping, form, request, response});

        try {
            if (hasValidSaveToken(request)) {
                // Secures access to the page
                securePage(request, form, false);

                PolicyHeader policyHeader = getPolicyHeader(request);

                // Secures access to the page
                securePage(request, form, false);

                // Map request to record for input
                Record inputRecord = getInputRecord(request);

                //renewalProcessManager handle renewal process
                getRenewalProcessManager().renewPolicy(policyHeader, inputRecord);

                writeEmptyAjaxXMLResponse(response);
            }
        }
        catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to renew policy.", e, response);
        }
        l.exiting(getClass().getName(), "renewPolicy", null);
        return null;
    }


    /**
     * if there is PRT,set needConfirmPRT = 'Y' and write it to ajax response ,
     * otherwise set needConfirmPRT = 'n' and write it to ajax response
     * if there RENEXPDT is configured for current policy type,also write parameter renexpdtConfiged
     * to ajax response
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getRenewalParms(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getRenewalParms",
            new Object[]{mapping, form, request, response});
        try {
            // Secures access to the page
            securePage(request, form, false);

            //get policy header from request
            PolicyHeader policyHeader = getPolicyHeader(request);

            //check PRT confirmation required
            getRenewalProcessManager().checkPRTConfirmationRequired(policyHeader);

            //check renexpdt is configed
            YesNoFlag renexpdtConfiged = getRenewalProcessManager().isRenewalTermExpirationRequired(policyHeader);

            //set output record,to ajax response
            Record paraRecord = new Record();
            paraRecord.setFieldValue("renexpdtConfiged", renexpdtConfiged);

            writeAjaxXmlResponse(response, paraRecord);
        }
        catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to load Pending Renewal Transaction.", e, response);
        }
        ActionForward af = null;
        l.exiting(getClass().getName(), "getRenewalParms", af);
        return af;
    }

    /**
     * load page to show term expiration page
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward captureRenewalTermExpiration(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "captureRenewalTermExpiration",
            new Object[]{mapping, form, request, response});

        String forwardString = "loadRenewTermExpiration";

        try {
            // Secures access to the page, load the Oasis Fields without loading the LOVs,
            //   and map the input parameters to the Fields
            securePage(request, form);

            PolicyHeader policyHeader = getPolicyHeader(request);

            // Publish the output record for use by the Oasis Tags and JSP
            Record outputRecord = getRenewalProcessManager().getInitialValuesForRenewalTermExpiration(policyHeader);
            publishOutputRecord(request, outputRecord);

            loadListOfValues(request, form);

            // Add Js messages
            addJsMessages();

        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load Renew Term Expiration page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "captureRenewalTermExpiration", af);
        return af;
    }

    /**
     * Valdiate auto renewal
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward validateAutoRenewal(ActionMapping mapping,
                                             ActionForm form,
                                             HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAutoRenewal", new Object[]{mapping, form, request, response});
        }

        try {
            securePage(request, form, false);
            PolicyHeader policyHeader = getPolicyHeader(request);

            // Validate auto renewal
            getRenewalProcessManager().validateAutoRenewal(policyHeader);
            writeEmptyAjaxXMLResponse(response);
        }
        catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to validate auto renewal.", e, response);
        }

        ActionForward af = null;

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateAutoRenewal", af);
        }
        return af;
    }

    /**
     * Perform auto renewal
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward performAutoRenewal(ActionMapping mapping,
                                            ActionForm form,
                                            HttpServletRequest request,
                                            HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performAutoRenewal", new Object[]{mapping, form, request, response});
        }

        try {
            if (hasValidSaveToken(request)) {
                securePage(request, form, false);
                PolicyHeader policyHeader = getPolicyHeader(request);

                // Map request to record for input
                Record inputRecord = getInputRecord(request);

                // Validate auto renewal
                getRenewalProcessManager().performAutoRenewal(policyHeader, inputRecord);
                writeEmptyAjaxXMLResponse(response);
            }
        }
        catch (Exception e) {
            handleErrorForAjax("pm.autoRenewal.fail.error", "Failed to perform auto renew.", e, response);
        }

        ActionForward af = null;

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performAutoRenewal", af);
        }
        return af;
    }

    /**
     * checkPRTConfirmationRequired
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward checkPRTConfirmationRequired(ActionMapping mapping,
                                             ActionForm form,
                                             HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "checkPRTConfirmationRequired", new Object[]{mapping, form, request, response});
        }

        try {
            securePage(request, form, false);
            PolicyHeader policyHeader = getPolicyHeader(request);

            //check PRT confirmation required
            getRenewalProcessManager().checkPRTConfirmationRequired(policyHeader);

            writeEmptyAjaxXMLResponse(response);
        }
        catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to validate auto renewal.", e, response);
        }

        ActionForward af = null;

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "checkPRTConfirmationRequired", af);
        }
        return af;
    }

    /**
     * Verify config
     */
    public void verifyConfig() {
        if (getRenewalProcessManager() == null)
            throw new ConfigurationException("The required property 'renewalProcessManager' is missing.");
    }

    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.maintainRenewal.expPriorToEff.error");
    }

    public RenewalProcessManager getRenewalProcessManager() {
        return m_renewalProcessManager;
    }

    public void setRenewalProcessManager(RenewalProcessManager renewalProcessManager) {
        m_renewalProcessManager = renewalProcessManager;
    }

    private RenewalProcessManager m_renewalProcessManager;

}
