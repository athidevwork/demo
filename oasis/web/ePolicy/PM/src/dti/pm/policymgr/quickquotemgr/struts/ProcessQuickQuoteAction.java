package dti.pm.policymgr.quickquotemgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.quickquotemgr.QuickQuoteManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for process quick quote.
 * <p/>
 * <p>(C) 2009 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 10, 2009
 *
 * @author Bhong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/17/2011       syang       120782 - Modified viewFile() to read file byte by byte.
 * 06/28/2016       ssheng      164927 - Clean the cache.
 * 03/08/2018       wrong       191786 - Modify viewFile() to change output logic to
 *                                       deal with SYLK file case.
 * ---------------------------------------------------
 */
public class ProcessQuickQuoteAction extends PMBaseAction {
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
    public ActionForward unspecified(ActionMapping mapping,
                                     ActionForm form,
                                     HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        return display(mapping, form, request, response);
    }

    /**
     * Display the file load page
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     */
    public ActionForward display(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "display", new Object[]{form, request, response});
        }

        String forwardString = "loadResult";

        try {
            // Secure page
            securePage(request, form);

            // Load policy header
            PolicyHeader policyHeader = getPolicyHeader(request);

            // Load grid header bean
            loadGridHeader(request);

            // Try to load import result, if there's no record
            // Populate dummy field to display empty grid initially
            Record inputRecord = getInputRecord(request);
            RecordSet rs = getQuickQuoteManager().loadAllImportResult(policyHeader, inputRecord);
            setDataBean(request, rs);

            // Publish out policy_load_event_header_fk and oasis_file_pk in summary record
            publishOutputRecord(request, rs.getSummaryRecord());

            // Load lov
            loadListOfValues(request, form);
            addJsMessages();
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load quick quote page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "display", af);
        }
        return af;
    }

    /**
     * Perform import quote
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     */
    public ActionForward importQuote(ActionMapping mapping,
                                     ActionForm form,
                                     HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "importQuote", new Object[]{form, request, response});
        }

        String forwardString = "processResult";
        try {
            if (hasValidSaveToken(request)) {
                // Secure page
                securePage(request, form);
                PolicyHeader policyHeader = getPolicyHeader(request);
                policyHeader.setCacheRiskOption(new RecordSet());
                policyHeader.setCacheCoverageRiskOption(new RecordSet());
                policyHeader.setCacheCoverageOption(new RecordSet());

                // Get all request parameters into a record
                Record inputRecord = getInputRecord(request);
                Record rc = getQuickQuoteManager().importQuote(policyHeader, inputRecord);
                long returnValue = rc.getLongValue("rc").longValue();
                String message = rc.getStringValue("rmsg");
                if (returnValue < 1) {
                    MessageManager.getInstance().addErrorMessage("pm.quickQuote.processResult.info", new String[]{message});
                }
                else {
                    MessageManager.getInstance().addInfoMessage("pm.quickQuote.processResult.info", new String[]{message});
                }
            }
        }
        catch (Exception e) {
            forwardString = handleError(
                AppException.UNEXPECTED_ERROR, "Failed to import quick quote.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "importQuote", af);
        }
        return af;
    }

    /**
     * Unload date from quote
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     */
    public ActionForward undoImportQuote(ActionMapping mapping,
                                         ActionForm form,
                                         HttpServletRequest request,
                                         HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "undoImportQuote", new Object[]{form, request, response});
        }

        String forwardString = "processResult";
        try {
            if (hasValidSaveToken(request)) {
                // Secure page
                securePage(request, form);

                // Get all request parameters into a record
                Record inputRecord = getInputRecord(request);
                PolicyHeader policyHeader = getPolicyHeader(request);
                policyHeader.setCacheRiskOption(new RecordSet());
                policyHeader.setCacheCoverageRiskOption(new RecordSet());
                policyHeader.setCacheCoverageOption(new RecordSet());

                Record rc = getQuickQuoteManager().undoImportQuote(inputRecord);
                int rt = rc.getIntegerValue("rt").intValue();
                String message = rc.getStringValue("msg");
                if (rt > 0) {
                    MessageManager.getInstance().addInfoMessage("pm.quickQuote.processResult.info", new String[]{message});
                }
                else {
                    MessageManager.getInstance().addErrorMessage("pm.quickQuote.processResult.info", new String[]{message});
                }
            }

        }
        catch (Exception e) {
            forwardString = handleError(
                AppException.UNEXPECTED_ERROR, "Failed to unload quick quote.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "undoImportQuote", af);
        }
        return af;
    }

    /**
     * Unload date from quote
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     */
    public ActionForward populateCis(ActionMapping mapping,
                                     ActionForm form,
                                     HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "populateCis", new Object[]{form, request, response});
        }

        try {
            // Secure page
            securePage(request, form);
            PolicyHeader policyHeader = getPolicyHeader(request);

            // Get all request parameters into a record
            Record inputRecord = getInputRecord(request);
            Record rc = getQuickQuoteManager().populateCis(policyHeader, inputRecord);
            writeAjaxXmlResponse(response, rc);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to populate cis.", e, response);
        }

        ActionForward af = null;
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "populateCis", af);
        }
        return af;
    }

    /**
     * View CSV file
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward viewFile(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "viewFile", new Object[]{mapping, form, request, response});
        ActionForward af = null;
        try {

            securePage(request, form);

            // get file full path
            Record inputRecord = getInputRecord(request);
            String fullFilePath = getQuickQuoteManager().getImportFilePath(inputRecord);

            ServletOutputStream out = response.getOutputStream();

            BufferedInputStream bufferedInput = null;
            byte[] buffer = new byte[2];
            Boolean isFirstRead = true;

            try {
                response.setContentType("application/vnd.ms-excel");
                //Construct the BufferedInputStream object
                bufferedInput = new BufferedInputStream(new FileInputStream(fullFilePath));

                while ((bufferedInput.read(buffer)) != -1) {
                    if (isFirstRead) {
                        //if file content starts with "ID", system will change it to "Id".
                        if (new String(buffer, "UTF-8").equals("ID")) {
                            buffer = new byte[]{73, 100};
                        }
                        isFirstRead = false;
                    }
                    out.write(buffer);
                }
                out.flush();
                out.close();
            }
            catch (FileNotFoundException ex) {
                l.warning("file " + fullFilePath + " can not be found");
                throw new AppException(ex.getMessage());
            }
            catch (IOException ex) {
                l.warning("file " + fullFilePath + " can not be read. IO Exception occurred:" + ex.getMessage());
                throw new AppException(ex.getMessage());
            }
            finally {
                //Close the BufferedInputStream
                try {
                    if (bufferedInput != null)
                        bufferedInput.close();
                }
                catch (IOException ex) {
                    l.warning("file " + fullFilePath + " can not be read. IO Exception occurred:" + ex.getMessage());
                }
            }
        }
        catch (Exception e) {
            String forwardString = "viewDocument_errorpopup";
            af = mapping.findForward(forwardString);
        }

        l.exiting(getClass().getName(), "viewFile", af);
        return af;
    }

    /**
     * add js messages
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.quickQuote.hasFileHeader.confirm");
        MessageManager.getInstance().addJsMessage("pm.quickQuote.fileNotExist");
    }

    /**
     * Verify config
     */
    public void verifyConfig() {
        if (getPolicyManager() == null)
            throw new ConfigurationException("The required property 'quickQuoteManager' is missing.");
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
    }

    public QuickQuoteManager getQuickQuoteManager() {
        return m_quickQuoteManager;
    }

    public void setQuickQuoteManager(QuickQuoteManager quickQuoteManager) {
        m_quickQuoteManager = quickQuoteManager;
    }

    private QuickQuoteManager m_quickQuoteManager;
}
