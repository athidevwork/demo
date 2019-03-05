package dti.ci.credentialrequestmgr.struts;

import dti.ci.credentialrequestmgr.CredentialRequestManager;
import dti.ci.struts.action.CIBaseAction;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.security.Base64Coder;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action Class Credential Request page.
 * <p/>
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:  03/04/2016
 *
 * @author jdingle
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/15/2018       dpang        195835: Grid replacement
 * ---------------------------------------------------
*/
public class MaintainCredentialRequestAction extends CIBaseAction {
    /**
     * Unspecified
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
        return init(mapping, form, request, response);
    }

    /**
     * Initialize the Credential Request page.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward init(ActionMapping mapping, ActionForm form,
                              HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "init", new Object[]{mapping, form, request, response});
        }


        String forwardString = "initPage";
        try {
            // Secures page
            securePage(request, form);

            // Clear field values.
            Record inputRecord = getInputRecord(request);

            String entityFK = inputRecord.getStringValue(PK_PROPERTY, "");
            /* validate */
            if (!FormatUtils.isLong(entityFK)) {
                throw new IllegalArgumentException(new StringBuffer().append(
                        "entity FK [").append(entityFK)
                        .append("] should be a number.")
                        .toString());
            }

            String entityType = inputRecord.getStringValue(ENTITY_TYPE_PROPERTY, "");
            /* set menu beans Search & Select an entity type of  'organization'.*/
            if (entityType.charAt(0) == ENTITY_TYPE_ORG_CHAR) {
                checkCisFolderMenu(request);
            }

            String ciCredReqId = inputRecord.getStringValue("ciCredReqId", "-1");

            inputRecord.setFieldValue("entityId", entityFK);
            inputRecord.setFieldValue("ciCredReqId", ciCredReqId);

            // Load Detail Data.
            RecordSet rsDet = null;
            if (RequestStorageManager.getInstance().has("requestDetailRS")) {
                rsDet = (RecordSet) RequestStorageManager.getInstance().get("requestDetailRS");
            }
            if (rsDet==null) {
                rsDet = getCredentialRequestManager().loadDetail(inputRecord);
            }

            // Load Account Data.
            RecordSet rs = getCredentialRequestManager().loadAllAccount(inputRecord);

            setDataBean(request, rsDet, DETAIL_BEAN_NAME);
            setDataBean(request, rs, ACCOUNT_BEAN_NAME);
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, DETAIL_GRID_ID);
            loadGridHeader(request, null, DETAIL_BEAN_NAME, DETAIL_GRID_HEADER_LAYER);
            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, ACCOUNT_GRID_ID);
            loadGridHeader(request, null, ACCOUNT_BEAN_NAME, ACCOUNT_GRID_HEADER_LAYER);

            request.setAttribute(PK_PROPERTY, entityFK);
            request.setAttribute(ENTITY_TYPE_PROPERTY, entityType);

            publishOutputRecord(request, inputRecord);

            // Load LOVs
            loadListOfValues(request, form);

            addJsMessages();

            saveToken(request);
        } catch (Exception e) {
            forwardString = handleErrorPopup(
                    AppException.UNEXPECTED_ERROR, "Failed to initialize the Credential Request page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "init", af);
        }
        return af;
    }

    /**
     * Load Entity Detail.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadEntity(ActionMapping mapping, ActionForm form,
                                    HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadEntity", new Object[]{mapping, form, request, response});
        }

        try {
            // Clear field values.
            Record inputRecord = getInputRecord(request);

            // Load Account Data.
            Record rec = getCredentialRequestManager().loadEntity(inputRecord);

            writeAjaxResponse(response, rec, true);

        } catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR,
                    "Unable to load entity.", e, response);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadEntity", "exit");
        }
        return null;
    }

    /**
     * Save Credential Request.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveRequest(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveRequest", new Object[]{mapping, form, request, response});
        }

        String forwardString = "saveResult";
        Record inputRecord = null;
        RecordSet requestDetailRecords = null;
        Record rec = null;

        try {
            // Secures page
            securePage(request, form);

            if (hasValidSaveToken(request)) {
                inputRecord = getInputRecord(request);
                String entityFK = inputRecord.getStringValue(PK_PROPERTY, "");
                String entityType = inputRecord.getStringValue(ENTITY_TYPE_PROPERTY, "");
                rec = getCredentialRequestManager().saveRequest(inputRecord);
                String reqId = rec.getStringValue("ciCredReqId");
                RequestStorageManager.getInstance().set(CURRENT_GRID_ID, DETAIL_GRID_ID);
                requestDetailRecords = getInputRecordSet(request, "detailListGrid");
                requestDetailRecords.setFieldValueOnAll("ciCredReqId", reqId);
                inputRecord.setFieldValue("ciCredReqId", reqId);
                getCredentialRequestManager().saveAllRequestDetail(requestDetailRecords);
                getCredentialRequestManager().saveProcessRequest(inputRecord);
                request.setAttribute("inputRecord", inputRecord);
                request.setAttribute(PK_PROPERTY, entityFK);
                request.setAttribute(ENTITY_TYPE_PROPERTY, entityType);
                request.setAttribute("ciCredReqId", reqId);
                // set variable so we can submit to Cincom on reload.
                request.setAttribute("submitRequest", "Y");
                // Clear out cached detail data, it will reload saved data from DB.
                RequestStorageManager.getInstance().set("requestDetailRS", null);
            }
        } catch (ValidationException ve) {
            // Save the input records into request.
            request.setAttribute("inputRecord", inputRecord);
            // Handle the validation exception
            handleValidationException(ve, request);
        } catch (Exception e) {
            forwardString = handleError(
                    AppException.UNEXPECTED_ERROR, "Failed to Save Credential Request.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveRequest", af);
        }
        return af;
    }

    /**
     * Create new service account in FM.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveAccount(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAccount", new Object[]{mapping, form, request, response});
        }

        String forwardString = "genAccount";
        Record inputRecord = null;
        RecordSet requestDetailRecords = null;
        Record rec = null;

        try {
            // Secures page
            securePage(request, form);

            if (hasValidSaveToken(request)) {
                inputRecord = getInputRecord(request);
                String entityFK = inputRecord.getStringValue(PK_PROPERTY, "");
                Record recIn = new Record();
                recIn.setFieldValue("entityId", entityFK);
                rec = getCredentialRequestManager().saveAccount(recIn);
                requestDetailRecords = getInputRecordSet(request, "detailListGrid");
                // save detail records to request so that it can reload if data not saved
                RequestStorageManager.getInstance().set("requestDetailRS", requestDetailRecords);
                request.setAttribute("inputRecord", inputRecord);
            }
        } catch (ValidationException ve) {
            // Save the input records into request.
            request.setAttribute("inputRecord", inputRecord);
            // Handle the validation exception
            handleValidationException(ve, request);
        } catch (Exception e) {
            forwardString = handleError(
                    AppException.UNEXPECTED_ERROR, "Failed to generate new account.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAccount", af);
        }
        return af;
    }

    /**
     * Send Request To Cincom.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward submitRequest(ActionMapping mapping, ActionForm form,
                                      HttpServletRequest request, HttpServletResponse response) throws Exception {

        OutputStream out = null;
        ByteArrayInputStream bis = null; //<-- to test display of a PDF without connecting to Cincom, comment out.
        ByteArrayOutputStream bos = null;

        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "submitRequest", new Object[]{mapping, form, request, response});
        }

        try {
            // ----to test display of a PDF without connecting to Cincom, comment out this section ---
            // Clear field values.
            Record inputRecord = getInputRecord(request);

            // Load Account Data.
            Record rec = getCredentialRequestManager().submitRequest(inputRecord);

            // write PDF stream or error message
            if ("Y".equalsIgnoreCase(rec.getStringValue("successFlag"))) {
                response.setContentType("application/pdf");
                bis = new ByteArrayInputStream(Base64Coder.decode(rec.getStringValue("resultString")));
            } else {
                response.setContentType("text/html");
                bis = new ByteArrayInputStream(rec.getStringValue("resultString").getBytes());
            }
            // ----to test display of a PDF without connecting to Cincom, comment out this section ---

            // ----to test display of a PDF without connecting to Cincom, uncomment this section ---
//            FileInputStream fis;
//            fis = new FileInputStream("H:\\AC Form.pdf");
//            BufferedInputStream bis;
//            bis = new BufferedInputStream(fis);
            // ----to test display of a PDF without connecting to Cincom, uncomment this section ---

            bos = new ByteArrayOutputStream();
            out = response.getOutputStream();
            byte[] buff = new byte[2048];
            int bytesRead;
            int total = 0;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
                total += bytesRead;
            }
            response.setContentLength(total);

            bos.writeTo(out);
            out.flush();

        } catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR,
                    "Unable to submit request.", e, response);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "submitRequest", "exit");
        }
        return null;
    }

    @Override
    public String getAnchorColumnName() {
        String anchorName;
        String currentGridId = getCurrentGridId();
        if (StringUtils.isBlank(currentGridId)) {
            anchorName = super.getAnchorColumnName();
        } else if (currentGridId.startsWith(DETAIL_BEAN_NAME)) {
            anchorName = getDetailAnchorColumnName();
        } else if (currentGridId.startsWith(ACCOUNT_BEAN_NAME)) {
            anchorName = getAccountAnchorColumnName();
        } else {
            anchorName = super.getAnchorColumnName();
        }
        return anchorName;
    }
    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("js.selectRowBeforeNextAction");
        MessageManager.getInstance().addJsMessage("cs.records.delete.confirm");
        MessageManager.getInstance().addJsMessage("ci.credentialRequest.account.missing");
        MessageManager.getInstance().addJsMessage("ci.credentialRequest.account.onHold");
    }

    public void verifyConfig() {
        if (getCredentialRequestManager() == null) {
            throw new ConfigurationException("The required property 'credentialRequestManager' is missing.");
        }
        if (getWorkbenchConfiguration() == null) {
            throw new ConfigurationException("The required property 'workbenchConfiguration' is missing.");
        }
        if (getAccountAnchorColumnName() == null) {
            throw new ConfigurationException("The required property 'accountAnchorColumnName' is missing.");
        }
        if (getDetailAnchorColumnName() == null) {
            throw new ConfigurationException("The required property 'detailAnchorColumnName' is missing.");
        }
    }

    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return m_workbenchConfiguration;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration) {
        m_workbenchConfiguration = workbenchConfiguration;
    }

    public CredentialRequestManager getCredentialRequestManager() {
        return this.m_credentialRequestManager;
    }

    public void setCredentialRequestManager(CredentialRequestManager credentialRequestManager) {
        this.m_credentialRequestManager = credentialRequestManager;
    }

    private CredentialRequestManager m_credentialRequestManager;

    public String getDetailAnchorColumnName() {
        return m_detailAnchorColumnName;
    }

    public void setDetailAnchorColumnName(String detailAnchorColumnName) {
        this.m_detailAnchorColumnName = detailAnchorColumnName;
    }

    public String getAccountAnchorColumnName() {
        return m_accountAnchorColumnName;
    }

    public void setAccountAnchorColumnName(String accountAnchorColumnName) {
        this.m_accountAnchorColumnName = accountAnchorColumnName;
    }

    private String m_detailAnchorColumnName;
    private String m_accountAnchorColumnName;
    private WorkbenchConfiguration m_workbenchConfiguration;

    private static final String CURRENT_GRID_ID = "currentGridId";

    protected static final String DETAIL_GRID_ID = "detailListGrid";
    private static final String DETAIL_BEAN_NAME = "detailList";
    private static final String DETAIL_GRID_HEADER_LAYER = "CI_CREDREQ_DETAIL_GH";

    protected static final String ACCOUNT_GRID_ID = "accountListGrid";
    private static final String ACCOUNT_BEAN_NAME = "accountList";
    private static final String ACCOUNT_GRID_HEADER_LAYER = "CI_CREDREQ_ACCOUNT_GH";

}
