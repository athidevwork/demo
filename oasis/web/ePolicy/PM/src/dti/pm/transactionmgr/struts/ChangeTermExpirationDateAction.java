package dti.pm.transactionmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.transactionmgr.TransactionManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 26, 2007
 *
 * @author zlzhu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * Oct 26, 2007          zlzhu     Created
 * ---------------------------------------------------
 */
public class ChangeTermExpirationDateAction extends PMBaseAction {
    /**
     * This method is triggered automatically when there is no process parameter sent in along the requested url.
     * <p/>
     *
     * @param mapping  mapping
     * @param form     form bean
     * @param request  request
     * @param response response
     * @return ActionForward where to forward
     * @throws Exception if there's some error
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return captureTermExpirationDate(mapping, form, request, response);
    }

    /**
     * Load current term effective date and current term expiration date
     *
     * @param mapping  mapping
     * @param form     form bean
     * @param request  request
     * @param response response
     * @return ActionForward where to forward
     * @throws Exception if there's some error
     */
    public ActionForward captureTermExpirationDate(ActionMapping mapping,
                                                   ActionForm form,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "captureTermExpirationDate", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";

        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            PolicyHeader policyHeader = getPolicyHeader(request);
            Record record = (Record) request.getAttribute(SAVED_INPUT_RECORD);
            //retrieve save input
            if (record == null) {
                record = getTransactionManager().getInitialValuesForChangeTermExpirationDate(policyHeader);
            }
            loadListOfValues(request, form);
            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, record);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR,
                "Failed to load the Change Term Dates page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "captureTermExpirationDate", af);
        return af;
    }

    /**
     * Change the term expiration dates.
     *
     * @param mapping  mapping
     * @param form     form bean
     * @param request  request
     * @param response response
     * @return ActionForward where to forward
     * @throws Exception if there's some error
     */
    public ActionForward changeTermExpirationDate(ActionMapping mapping,
                                                  ActionForm form,
                                                  HttpServletRequest request,
                                                  HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "changeTermExpirationDate", new Object[]{mapping, form, request, response});
        Record inputRecord = null;
        String forwardString = "success";
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page, load the Oasis Fields without loading the LOVs,
                // and map the input parameters to the Fields
                securePage(request, form);

                // get policyHeader
                PolicyHeader policyHeader = getPolicyHeader(request);

                // get values from the form
                inputRecord = getInputRecord(request);

                // use TransactionManager to save the dates.
                getTransactionManager().changeTermExpirationDate(policyHeader, inputRecord);
                //if succeed,refresh the parent page
                request.setAttribute("refreshPage", "Y");
            }
        }
        catch (ValidationException ve) {
            // Save the input records into request
            request.setAttribute(SAVED_INPUT_RECORD, inputRecord);
            handleValidationException(ve, request);
            forwardString = "failure";
        }
        catch (Exception e) {
            forwardString = "failure";
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "changeTermExpirationDate", af);
        return af;
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

    protected static final String SAVED_INPUT_RECORD = "savedInputRecord";
    private TransactionManager m_transactionManager;
}