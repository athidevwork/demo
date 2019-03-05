package dti.pm.transactionmgr.struts;

import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.transactionmgr.TransactionManager;
import dti.oasis.util.LogUtils;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.Record;
import dti.oasis.error.ValidationException;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Action class for change term dates.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 29, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class ChangeTermDatesAction extends PMBaseAction {
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
        return loadAllTermDates(mapping, form, request, response);
    }

    /**
     * Load current term effective date and current term expiration date
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllTermDates(ActionMapping mapping,
                                          ActionForm form,
                                          HttpServletRequest request,
                                          HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllTermDates", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";

        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            PolicyHeader policyHeader = getPolicyHeader(request);

            Record outputRecord = getTransactionManager().getInitialValueForChangeTermDates(policyHeader);

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, outputRecord);
        }
        catch (Exception e) {
            forwardString =  handleErrorPopup(AppException.UNEXPECTED_ERROR,
                "Failed to load the Change Term Dates page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllTermDates", af);
        return af;
    }

    /**
     * Change the term dates.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveChangedTermDates(ActionMapping mapping,
                                         ActionForm form,
                                         HttpServletRequest request,
                                         HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveChangedTermDates", new Object[]{mapping, form, request, response});

        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page, load the Oasis Fields without loading the LOVs,
                // and map the input parameters to the Fields
                securePage(request, form);

                // get policyHeader
                PolicyHeader policyHeader = getPolicyHeader(request);

                // get values from the form
                Record inputRecord = getInputRecord(request);

                // use TransactionManager to save the dates.
                getTransactionManager().saveTermDates(policyHeader, inputRecord);

                writeEmptyAjaxXMLResponse(response);
            }
        }
        catch (ValidationException ve) {
            // Handle the validation exception
            handleValidationExceptionForAjax(ve, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to save the term dates.", e, response);
        }

        l.exiting(getClass().getName(), "saveChangedTermDates", null);
        return null;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getTransactionManager() == null) {
            throw new ConfigurationException("The required property 'transactionManager' is missing.");
        }
    }

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    private TransactionManager m_transactionManager;
}
