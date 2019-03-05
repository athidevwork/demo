package dti.pm.policymgr.tailquotemgr.struts;

import dti.pm.core.struts.PMBaseAction;
import dti.pm.core.http.RequestIds;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.tailquotemgr.TailQuoteManager;
import dti.oasis.util.LogUtils;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Action class for maintain Tail Quote
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 23, 2008
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/05/2011       ryzhao      124622 - For pages with multiple grids:
 *                              1) Pass gridId as the third parameter to the setDataBean() method
 *                                 for all but the first grid.
 *                              2) Pass gridId/layerId as the third/fourth parameter to the loadGridHeader() method
 *                                 for all but the first grid.
 * ---------------------------------------------------
 */
public class MaintainTailQuoteAction extends PMBaseAction {

    /**
     * This method is triggered automatically when where is no process parameter
     * sent in along the requested url.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadAllTailQuote(mapping, form, request, response);
    }

    /**
     * Method to load all tail quote transactions for the given policy
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllTailQuoteTransaction(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                     HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllTailQuoteTransaction", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadResult";
        try {

            // Secures access to the page, loads the oasis fields without loading LOVs,
            // and map the input parameters into the fields.
            securePage(request, form);

            //get input record from request
            Record inputRecord = getInputRecord(request);
            // Gets Policy Header
            PolicyHeader policyHeader = getPolicyHeader(request);

            //load tail quote transaction data
            RecordSet tailQuoteTranRs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            if (tailQuoteTranRs == null){
               tailQuoteTranRs = getTailQuoteManager().loadAllTailQuoteTransaction(policyHeader, inputRecord);
            }
            // Set loaded tail qute transaction infomation data into request
            setDataBean(request, tailQuoteTranRs);

            // Make the Summary Record available for output
            Record output = tailQuoteTranRs.getSummaryRecord();

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);

            // Load tail quote transaction grid header
            loadGridHeader(request);
            // Load the list of values after loading the data
            loadListOfValues(request, form);

            //add JS message for page
            addJsMessages();

        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the Tail Quote page.", e, request, mapping);
            e.printStackTrace();
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllTailQuoteTransaction", af);
        }
        return af;
    }

    /**
     * Method to load all tail quote for the given tail quote transaction
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllTailQuote(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                          HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllTailQuote", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadTailQuoteResult";
        try {

            // Secures access to the page, loads the oasis fields without loading LOVs,
            // and map the input parameters into the fields.
            securePage(request, form);

            //get input record from request
            Record inputRecord = getInputRecord(request);
            // Gets Policy Header
            PolicyHeader policyHeader = getPolicyHeader(request);

            //load tail quote data
            RecordSet tailQuoteRs = getTailQuoteManager().loadAllTailQuote(policyHeader, inputRecord);
            // Set loaded Component data into request
            setDataBean(request, tailQuoteRs, TAIL_QUOTE_GRID_ID);

            // Make the Summary Record available for output
            Record output = tailQuoteRs.getSummaryRecord();

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);

            // Set currentGridId to tailListGrid before load component gird header
            setCurrentGridId(TAIL_QUOTE_GRID_ID);

            // Load the list of values after loading the data
            loadListOfValues(request, form);

            // Load component grid header
            loadGridHeader(request, null, TAIL_QUOTE_GRID_ID, TAIL_QUOTE_GRID_LAYER_ID);

        }
        catch (Exception e) {
            forwardString = handleErrorIFrame(AppException.UNEXPECTED_ERROR, "Failed to load the Tail Quote page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllTailQuote", af);
        }
        return af;
    }


    /**
     * Save all tail quote data
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward saveAllTailQuote(ActionMapping mapping, ActionForm form,
                                          HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveAllTailQuote", new Object[]{mapping, form, request, response});


        String forwardString = "saveTailQuoteResult";
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);

                // First get all Quote Tail records
                // Map tail quote textXML to RecordSet for input
                setCurrentGridId(TAIL_QUOTE_GRID_ID);
                RecordSet inputRecords = getInputRecordSet(request, TAIL_QUOTE_GRID_ID);

                // get the policy header from the request
                PolicyHeader policyHeader = getPolicyHeader(request);
                //get input record from request
                Record inputRecord = getInputRecord(request);

                // save the tail quote data
                getTailQuoteManager().saveAllTailQuote(policyHeader, inputRecords, inputRecord);
            }
        }
        catch (Exception e) {
            forwardString = handleErrorIFrame(AppException.UNEXPECTED_ERROR,
                "Failed to save tail quote.", e, request, mapping);
        }

        // Return the forward
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveAllTailQuote", af);
        return af;
    }


    /**
     * perform process tail quote transaction
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward performProcessTailQuoteTransaction(ActionMapping mapping, ActionForm form,
                                                            HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "performProcessTailQuoteTransaction", new Object[]{mapping, form, request, response});

        RecordSet inputRecords = null;
        String forwardString = "saveResult";
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);

                // First get all Quote Tail records
                // Map tail quote transaction textXML to RecordSet for input
                inputRecords = getInputRecordSet(request, TAIL_QUOTE_TRANSACTION_GRID_ID);

                // Get the policy header from the request
                PolicyHeader policyHeader = getPolicyHeader(request);
                Record inputRecord = getInputRecord(request);

                // save the prior acts data
                getTailQuoteManager().performProcessTailQuoteTransaction(policyHeader, inputRecords, inputRecord);
            }
        }
        catch (ValidationException ve) {
            // Save the input records into request
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);

            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR,
                "Failed to perform Process Tail Quote Transaction.", e, request, mapping);
        }

        // Return the forward
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "performProcessTailQuoteTransaction", af);
        return af;
    }

    /**
     * for newly added tail quote transaction, provide default values
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return action forward
     * @throws Exception
     */
    public ActionForward getInitialValuesForTailQuoteTransaction(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                                 HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForTailQuoteTransaction",
            new Object[]{mapping, form, request, response});


        try {
            // Secure page
            securePage(request, form);

            // Get the policy header from the request
            PolicyHeader policyHeader = getPolicyHeader(request);

            // Get all request parameters into a record
            Record inputRecord = getInputRecord(request);

            // Get the initial values for tail quote
            Record initialValuesRec = getTailQuoteManager().getInitialValuesForTailQuoteTransaction(policyHeader, inputRecord);

            // get LOV labels for initial values
            publishOutputRecord(request, initialValuesRec);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, initialValuesRec);

            writeAjaxXmlResponse(response, initialValuesRec, true);
        }
        catch (ValidationException e) {
            handleValidationExceptionForAjax(e, response);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values for tail quote transaction.", e, response);
        }

        ActionForward af = null;
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForTailQuoteTransaction", af);
        }
        return af;
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.maintainTailQuote.saveChange.prompt");
    }

    /**
     * Overrides method in BaseAction. To get the anchor column name by current grid Id.
     * This is effective only for the multiple grids.
     *
     * @return anchor column name for the current grid
     */
    public String getAnchorColumnName() {
        Logger l = LogUtils.enterLog(getClass(), "getAnchorColumnName");
        String anchorName;
        if (hasCurrentGridId()) {
            String currentGridId = getCurrentGridId();
            if (currentGridId.equals(TAIL_QUOTE_GRID_ID)) {
                anchorName = getTailQuoteAnchorColumnName();
            }
            else {
                anchorName = super.getAnchorColumnName();
            }
        }
        else {
            anchorName = super.getAnchorColumnName();
        }
        l.exiting(getClass().getName(), "getAnchorColumnName", anchorName);
        return anchorName;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {       
        if (getTailQuoteManager() == null)
            throw new ConfigurationException("The required property 'tailQuoteManager' is missing.");
        if (getTailQuoteAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'tailQuoteAnchorColumnName' is missing.");
    }


    public TailQuoteManager getTailQuoteManager() {
        return m_tailQuoteManager;
    }

    public void setTailQuoteManager(TailQuoteManager tailQuoteManager) {
        m_tailQuoteManager = tailQuoteManager;
    }


    public String getTailQuoteAnchorColumnName() {
        return m_tailQuoteAnchorColumnName;
    }

    public void setTailQuoteAnchorColumnName(String tailQuoteAnchorColumnName) {
        m_tailQuoteAnchorColumnName = tailQuoteAnchorColumnName;
    }

    private TailQuoteManager m_tailQuoteManager;
    private String m_tailQuoteAnchorColumnName;


    protected static final String TAIL_QUOTE_TRANSACTION_GRID_ID = "transactionListGrid";
    protected static final String TAIL_QUOTE_GRID_ID = "tailListGrid";
    protected static final String TAIL_QUOTE_GRID_LAYER_ID = "PM_TAIL_QUOTE_GH";
}
