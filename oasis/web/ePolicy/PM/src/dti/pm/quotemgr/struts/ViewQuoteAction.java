package dti.pm.quotemgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.quotemgr.QuoteFields;
import dti.pm.quotemgr.QuoteManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class to view Quote page
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:   April 27, 2016
 *
 * @author wdang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/26/2016       wdang       167534 - Initial version.
 * ---------------------------------------------------
 */
public class ViewQuoteAction extends PMBaseAction {

    /**
     * This method is triggered automatically when where is no process parameter
     * sent in along the requested url.
     *
     * @param mapping       mapping
     * @param form          form
     * @param request       request
     * @param response      response
     * @return              action forward
     * @throws Exception
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadQuotes(mapping, form, request, response);
    }

    /**
     * Method to load all quotes info for requested policy.
     *
     * @param mapping       mapping
     * @param form          form
     * @param request       request
     * @param response      response
     * @return              action forward
     * @throws Exception
     */
    public ActionForward loadQuotes(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                    HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadQuotes", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadResult";
        try {

            // Secures access to the page, loads the oasis fields without
            // loading LOVs, and map the input parameters into the fields.
            securePage(request, form);

            // Gets Policy Header
            PolicyHeader policyHeader = getPolicyHeader(request);

            // Gets quote information.
            Record inputRecord = getInputRecord(request);

            // Build select mode
            Record record = policyHeader.toRecord();
            record.setFields(inputRecord);
            record.setFieldValue(QuoteFields.SELECT_MODE, QuoteFields.joinSelectMode(
                QuoteFields.EXCL_SELF, QuoteFields.TERM_SENS, QuoteFields.DISP_PREM, QuoteFields.EXCL_NB));

            // Load quote information
            RecordSet rs = getQuoteManager().loadQuoteVersions(record);

            // Publish page field
            publishOutputRecord(request, inputRecord);

            // Sets data bean
            setDataBean(request, rs);

            // Loads list of values
            loadListOfValues(request, form);

            // Load grid header bean
            loadGridHeader(request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the loadQuotes page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadQuotes", af);
        return af;
    }

    public QuoteManager getQuoteManager() {
        return m_quoteManager;
    }

    public void setQuoteManager(QuoteManager quoteManager) {
        m_quoteManager = quoteManager;
    }

    private QuoteManager m_quoteManager;
}
