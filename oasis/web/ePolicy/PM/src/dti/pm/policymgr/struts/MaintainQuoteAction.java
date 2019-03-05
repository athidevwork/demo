package dti.pm.policymgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Maintain Quote
 * handles reactive quote ,copy quote
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 7, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public class MaintainQuoteAction extends PMBaseAction {
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
        return null;
    }

    /**
     * this method handles reactive quote
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward reactiveQuote(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {

        Logger l = LogUtils.enterLog(getClass(), "reactiveQuote", new Object[]{mapping, form, request, response});

        try {
            //If the request has valid save token,then process
            if (hasValidSaveToken(request)) {
                PolicyHeader policyHeader = getPolicyHeader(request);
                // Secures access to the page
                securePage(request, form, false);

                //quoteManger handle deny quote process
                getPolicyManager().reactiveQuote(policyHeader);

                writeEmptyAjaxXMLResponse(response);
            }
        }
        catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to renew policy.", e, response);
        }

        l.exiting(getClass().getName(), "reactiveQuote", null);
        return null;
    }


    /**
     * this method handles copy quote
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward copyQuote(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "copyQuote", new Object[]{mapping, form, request, response});
        try {
            if (hasValidSaveToken(request)) {
                PolicyHeader policyHeader = getPolicyHeader(request);
                // Secures access to the page
                securePage(request, form, false);

                // Map request to record for input
                Record inputRecord = getInputRecord(request);

                //quoteManger handle deny quote process
                Record outputRecord = getPolicyManager().copyQuote(policyHeader, inputRecord);

                writeAjaxXmlResponse(response, outputRecord);
            }
        }
        catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to copy policy.", e, response);
        }
        l.exiting(getClass().getName(), "copyQuote", null);
        return null;
    }


    /**
     * this method handles accept quote
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward acceptQuote(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "acceptQuote", new Object[]{mapping, form, request, response});
        try {
            if (hasValidSaveToken(request)) {
                PolicyHeader policyHeader = getPolicyHeader(request);
                // Secures access to the page
                securePage(request, form, false);

                // Map request to record for input
                Record inputRecord = getInputRecord(request);

                //quoteManger handle deny quote process
                Record outputRecord = getPolicyManager().acceptQuote(policyHeader, inputRecord);

                writeAjaxXmlResponse(response, outputRecord);
            }
        }
        catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to accept policy.", e, response);
        }
        l.exiting(getClass().getName(), "acceptQuote", null);
        return null;
    }


    public void verifyConfig() {
        if (getPolicyManager() == null)
            throw new ConfigurationException("The required property 'policyManager' is missing.");
    }

    public PolicyManager getPolicyManager() {
        return this.policyManager;
    }

    public void setPolicyManager(PolicyManager policyManager) {
        this.policyManager = policyManager;
    }

    // private memember variables...
    private PolicyManager policyManager;
}
