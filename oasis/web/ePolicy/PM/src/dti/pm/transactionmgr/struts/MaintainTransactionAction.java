package dti.pm.transactionmgr.struts;

import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyManager;
import dti.pm.transactionmgr.TransactionManager;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.app.ConfigurationException;
import dti.oasis.app.AppException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.error.ValidationException;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * This class is an action transaction
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 20, 2007
 *
 * @author zlzhu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * Aug 23, 2007          zlzhu     Created
 * 07/13/2010       syang       103797 - Added js message.
 * 09/05/2011       ryzhao      124622 - For pages with multiple grids:
 *                              1) Pass gridId as the third parameter to the setDataBean() method
 *                                 for all but the first grid.
 *                              2) Pass gridId/layerId as the third/fourth parameter to the loadGridHeader() method
 *                                 for all but the first grid.
 * 02/19/2013       jshen       141982 - Removed method loadTransactionByTerm.
 * 07/26/2017       lzhang      182246 - Delete Js message
 * ---------------------------------------------------
 */

public class MaintainTransactionAction extends PMBaseAction {

    /**
     * do this process when no process is specified
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadAllTransaction(mapping, form, request, response);
    }

    /**
     * load all the transaction data(for bottom grid)
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward loadAllTransaction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllTransaction", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadResult";
        Record output;
        try {
            securePage(request,form);

            PolicyHeader policyHeader = getPolicyHeader(request, false);
            // Load the transactionGrids
            RecordSet rs = (RecordSet) request.getAttribute(GRID_RECORD_SET);
            if (rs == null) {
                rs = getTransactionManager().loadAllTransaction(policyHeader, getInputRecord(request));
            }

            // Set loaded transactionGrid data into request
            setDataBean(request, rs);
            // Make the Summary Record available for output
            output = rs.getSummaryRecord();
            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);
            loadGridHeader(request);
            loadListOfValues(request, form);

            // Populate messages for javascirpt
            addJsMessages();
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the transaction.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllTransaction", af);
        }
        return af;
    }

    /**
     * load all the transaction data(for bottom grid)
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward loadChangeDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadChangeDetail", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadChangeResult";
        Record output;
        try {
            securePage(request,form);
            // Load the change detail
            RecordSet rs = getTransactionManager().loadAllChangeDetail(getInputRecord(request));
            // Set loaded change detail data into request
            setDataBean(request, rs, CHILD_GRID_ID);
            // Make the Summary Record available for output
            output = rs.getSummaryRecord();
            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);
            setCurrentGridId(CHILD_GRID_ID);
            loadGridHeader(request, null, CHILD_GRID_ID, CHILD_GRID_LAYER_ID);
            loadListOfValues(request, form);
        }
        catch (Exception e) {
            forwardString = handleErrorIFrame(AppException.UNEXPECTED_ERROR, "Failed to load the change detail.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadChangeDetail", af);
        }
        return af;
    }

    /**
     * load all the transaction data(for bottom grid)
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward loadTransactionForm(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadTransactionForm", new Object[]{mapping, form, request, response});
        }
        String forwardString = "loadFormResult";
        Record output;
        try {
            securePage(request,form);
            RecordSet rs = getTransactionManager().loadAllTransactionForm(getInputRecord(request));
            // Set loaded transaction forms data into request
            setDataBean(request, rs, CHILD_GRID_TRANS_FORM);
            // Make the Summary Record available for output
            output = rs.getSummaryRecord();
            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);
            setCurrentGridId(CHILD_GRID_TRANS_FORM);
            loadGridHeader(request, null, CHILD_GRID_TRANS_FORM, CHILD_GRID_LAYER_TRANS_FORM);
            loadListOfValues(request, form);
        }
        catch (Exception e) {
            forwardString = handleErrorIFrame(AppException.UNEXPECTED_ERROR, "Failed to load the transaction form.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadTransactionForm", af);
        }
        return af;
    }

    /**
     * save transaction data
     *
     * @param mapping  mapping
     * @param form     form
     * @param request  request
     * @param response response
     * @return the forward
     * @throws Exception if there are some errors
     */
    public ActionForward saveTransactionDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                               HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveTransactionDetail", new Object[]{mapping, form, request, response});
        }

        String forwardString = "saveResult";
        RecordSet inputRecords1 = null;
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                securePage(request,form);
                inputRecords1 = getInputRecordSet(request, PARENT_GRID_ID);
                // Save the changes
                getTransactionManager().saveTransactionDetail(inputRecords1);
            }
        }
        catch (ValidationException ve) {
            // Save the input records into request, so it does not
            // to get recordset again.
            request.setAttribute(GRID_RECORD_SET, inputRecords1);
            // Handle the validation exception
            handleValidationException(ve, request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to save the transaction page.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveTransactionDetail", af);
        }
        return af;
    }

    /**
     * method to view document, this method is copied from ProcessOutputAction.java
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward viewDocument(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "holdDocument", new Object[]{mapping, form, request, response});
        ActionForward af = null;
        Record inputRecord = null;
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            securePage(request, form);
            // get input
            inputRecord = getInputRecord(request);

            ServletOutputStream out = response.getOutputStream();
            String decodedFileFullPath = inputRecord.getStringValue("decodedFileFullPath");
            BufferedInputStream bufferedInput = null;
            byte[] buffer = new byte[1024];

            try {
                // Set content type by suffix of file name,
                // need to add more content type for other file suffix in the future.
                String contentType = "";
                String suffix = decodedFileFullPath.substring(decodedFileFullPath.lastIndexOf(".") + 1);
                if (!StringUtils.isBlank(suffix)) {
                    // Mapping MIME type with specific file here
                    if ("PDF".equalsIgnoreCase(suffix)) {
                        // Acrobat PDF file
                        contentType = "application/pdf";
                    }
                    else if ("DOC".equalsIgnoreCase(suffix)) {
                        // Win word document
                        contentType = "application/msword";
                    }
                }
                if (!StringUtils.isBlank(contentType)) {
                    response.setContentType(contentType);
                }
                //Construct the BufferedInputStream object
                bufferedInput = new BufferedInputStream(new FileInputStream(decodedFileFullPath));
                int i = 0;
                while ((i = bufferedInput.read(buffer)) != -1) {
                    out.write(buffer);
                }
                out.flush();
                out.close();
            }
            catch (FileNotFoundException ex) {
                l.warning("file " + decodedFileFullPath + " can not be found");
                MessageManager.getInstance().addErrorMessage("cs.outputmgr.processOutput.vewDocument.fileNotFound", new String[]{decodedFileFullPath});
                throw new AppException(ex.getMessage());
            }
            catch (IOException ex) {
                l.warning("file " + decodedFileFullPath + " can not be read. IO Exception occurred:" + ex.getMessage());
                MessageManager.getInstance().addErrorMessage("cs.outputmgr.processOutput.vewDocument.readFileError", new String[]{decodedFileFullPath});
                throw new AppException(ex.getMessage());
            }
            finally {
                //Close the BufferedInputStream
                try {
                    if (bufferedInput != null)
                        bufferedInput.close();
                }
                catch (IOException ex) {
                    l.warning("file " + decodedFileFullPath + " can not be read. IO Exception occurred:" + ex.getMessage());
                }
            }
        }
        catch (Exception e) {
            String forwardString = "viewDocument_errorpopup"; // handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to view document ", e, request, mapping);
            af = mapping.findForward(forwardString);
        }
        l.exiting(getClass().getName(), "viewDocument", af);
        return af;
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.transaction.nodata.error");
        MessageManager.getInstance().addJsMessage("pm.transaction.save.error");
        MessageManager.getInstance().addJsMessage("pm.transaction.transactionGrid.header");
        MessageManager.getInstance().addJsMessage("pm.transaction.changeDetailGrid.header");
        MessageManager.getInstance().addJsMessage("pm.transaction.form.header");
        MessageManager.getInstance().addJsMessage("pm.transaction.transactionFormGrid.header");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    /**
     * verify config
     */
    public void verifyConfig() {
        if (getPolicyManager() == null)
            throw new ConfigurationException("The required property 'policyManager' is missing.");
        if (getTransactionManager() == null)
            throw new ConfigurationException("The required property 'transactionManager' is missing.");
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getChildAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'childAnchorColumnName' is missing.");
        if (getTransFormAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'transFormAnchorColumnName' is missing.");
    }

    /**
     *
     */
    public MaintainTransactionAction() {
    }

    /**
     * get policyManager
     *
     * @return policyManager
     */
    public PolicyManager getPolicyManager() {
        return m_policyManager;
    }

    /**
     * set policyManager
     */
    public void setPolicyManager(PolicyManager policyManager) {
        m_policyManager = policyManager;
    }

    /**
     * get transactionManager
     *
     * @return transactionManager
     */
    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    /**
     * set transactionManager
     *
     * @param transactionManager transaction manager
     */
    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }
    /**
     * override this method to handle :
     * three grids in a page but only one has headerFileName
     * @return
     */
    public boolean hasHeaderFileName() {
        if (hasCurrentGridId()) {
            String currentGridId = getCurrentGridId();
            if (currentGridId.equals(PARENT_GRID_ID)) {
                return false;
            }else if (currentGridId.equals(CHILD_GRID_ID)) {
                return false;
            }else if (currentGridId.equals(CHILD_GRID_TRANS_FORM)) {
                return super.hasHeaderFileName();
            }else {
                return false;
            }
        }
        else {
            return false;
        }
    }
    /**
     * Overrides method in BaseAction. To get the anchor column name by current grid Id.
     * This is effective only for the multiple grids.
     * <p/>
     *
     * @return anchor column name for the current grid
     */
    public String getAnchorColumnName() {
        if (hasCurrentGridId()) {
            String currentGridId = getCurrentGridId();
            if (currentGridId.equals(CHILD_GRID_ID)) {
                return getChildAnchorColumnName();
            }
            if (currentGridId.equals(CHILD_GRID_TRANS_FORM)) {
                return getTransFormAnchorColumnName();
            }
            else {
                return super.getAnchorColumnName();
            }
        }
        else {
            return super.getAnchorColumnName();
        }
    }

    /**
     * get the anchor column for the bottom grid
     * <p/>
     *
     * @return anchor column
     */
    public String getChildAnchorColumnName() {
        return m_childAnchorColumnName;
    }

    /**
     * set the anchor column for the bottom grid
     * <p/>
     *
     * @param childAnchorColumnName anchor column name
     */
    public void setChildAnchorColumnName(String childAnchorColumnName) {
        m_childAnchorColumnName = childAnchorColumnName;
    }

    /**
     * get the anchor column for the bottom grid
     * <p/>
     *
     * @return anchor column
     */
    public String getTransFormAnchorColumnName() {
        return m_transFormAnchorColumnName;
    }

    /**
     * set the anchor column for the bottom grid
     * <p/>
     *
     * @param transFormAnchorColumnName anchor column name
     */
    public void setTransFormAnchorColumnName(String transFormAnchorColumnName) {
        m_transFormAnchorColumnName = transFormAnchorColumnName;
    }

    protected static final String GRID_RECORD_SET = "currentGridSet";
    protected static final String PARENT_GRID_ID = "transactionGrid";
    protected static final String CHILD_GRID_ID = "changeDetailGrid";
    protected static final String CHILD_GRID_LAYER_ID = "PM_CHANGE_DETAIL_GH";
    protected static final String CHILD_GRID_TRANS_FORM = "transactionFormGrid";
    protected static final String CHILD_GRID_LAYER_TRANS_FORM = "PM_TRANSACTION_FORMS_GH";
    private String m_childAnchorColumnName;
    private String m_transFormAnchorColumnName;
    private PolicyManager m_policyManager;
    private TransactionManager m_transactionManager;
}
