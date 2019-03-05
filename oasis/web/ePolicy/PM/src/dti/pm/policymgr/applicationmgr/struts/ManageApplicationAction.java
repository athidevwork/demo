package dti.pm.policymgr.applicationmgr.struts;

import com.trinisys.tdes.swat.client.AuthTokenBuilder;
import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordLoadProcessorChainManager;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.ActionHelper;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.pm.core.struts.AddSelectIndLoadProcessor;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.applicationmgr.ApplicationFields;
import dti.pm.policymgr.applicationmgr.ApplicationManager;
import dti.pm.policymgr.applicationmgr.impl.ApplicationGroupRecordLoadProcessor;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for manage application.
 * <p/>
 * <p>(C) 2012 Delphi Technology, inc. (dti)</p>
 * Date:   May 07, 2012
 *
 * @author Bhong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/17/2013       adeng       141786 - Modified loadAllApplication() to analysis exception to check if System
 *                              Parameter is defined or not, if not ,displays validation error.
 * 04/10/2017       tzeng       166929 - Remove getUtcExpirationDate() to ApplicationManager.
 * ---------------------------------------------------
 */
public class ManageApplicationAction extends PMBaseAction {
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
        // Initially load page without grid
        request.setAttribute("loadGridData", YesNoFlag.N);
        return loadAllApplication(mapping, form, request, response);
    }

    /**
     * Clear search criteria
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
    public ActionForward clear(ActionMapping mapping, ActionForm form,
                               HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Initially load page without grid
        request.setAttribute("loadGridData", YesNoFlag.N);
        return loadAllApplication(mapping, form, request, response);
    }

    /**
     * Load All application based on search criteria
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllApplication(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                            HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllApplication", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadResult";
        try {
            // Secures access to the page, load the Oasis Fields without loading the LOVs,
            //   and map the input parameters to the Fields
            securePage(request, form);
            boolean loadGridData = true;
            if (request.getAttribute("loadGridData") != null) {
                loadGridData = ((YesNoFlag) request.getAttribute("loadGridData")).booleanValue();
            }

            if (loadGridData) {
                // Retrieve application list
                // Loads available coverages for selection
                RecordLoadProcessor loadProcessor = AddSelectIndLoadProcessor.getInstance();
                ApplicationGroupRecordLoadProcessor groupProcessor = new ApplicationGroupRecordLoadProcessor();
                RecordLoadProcessor processor = RecordLoadProcessorChainManager.
                    getRecordLoadProcessor(loadProcessor, groupProcessor);
                RecordSet rs = getApplicationManager().loadAllApplication(getInputRecord(request), processor);
                // Sets data Bean
                setDataBean(request, rs);

                // Publish the output record for use by the Oasis Tags and JSP
                publishOutputRecord(request, rs.getSummaryRecord());
            }
            else {
                setEmptyDataBean(request);
            }

            // Load LOVs
            loadListOfValues(request, form);
            // Load grid header bean
            loadGridHeader(request);
            // Populate messages for javascirpt
            addJsMessages();
        }
        catch (Exception e) {
            int pos = e.getCause().getMessage().indexOf("PM_APP_EXT");
            if (pos > -1) {
                MessageManager.getInstance().addErrorMessage("pm.applicationManagement.validate.error.noExtDefined");
                ValidationException ve = new ValidationException("The associated system parameter are not defined.");
                clear(mapping, form, request, response);
                handleValidationException(ve, request);
            }
            else {
                forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load applications.", e, request, mapping);
            }
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllApplication", af);
        }
        return af;
    }

    /**
     * Save all applications
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveAllApplication(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllApplication", new Object[]{mapping, form, request, response});
        }

        String forwardString = "saveResult";
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure page
                securePage(request, form, false);
                getApplicationManager().saveAllApplication(getInputRecord(request), getInputRecordSet(request));
            }
        }
        catch (Exception e) {
            handleError(AppException.UNEXPECTED_ERROR, "Fail to save application.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllApplication", af);
        }
        return af;
    }

    /**
     * Method to view PDF file
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward viewDocument(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "viewDocument", new Object[]{mapping, form, request, response});
        }

        ActionForward af = null;
        Record inputRecord = null;
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            securePage(request, form);
            // get input
            inputRecord = getInputRecord(request);

            ServletOutputStream out = response.getOutputStream();
            String decodedFileFullPath = ApplicationFields.getDecodedFileFullPath(inputRecord);
            BufferedInputStream bufferedInput = null;
            byte[] buffer = new byte[1024];

            try {
                // Set content type by suffix of file name,
                // need to add more content type for other file suffix in the future.
                String contentType = "";
                String suffix = decodedFileFullPath.substring(decodedFileFullPath.lastIndexOf(".") + 1);
                response.setContentType("application/pdf");
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
                throw new AppException(ex.getMessage());
            }
            catch (IOException ex) {
                l.warning("file " + decodedFileFullPath + " can not be read. IO Exception occurred:" + ex.getMessage());
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
            handleError(AppException.UNEXPECTED_ERROR, "Fail to open pdf file.", e, request, mapping);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "viewDocument", af);
        }
        return af;
    }

    /**
     * Get url for external eApp application
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getApplicationUrl(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getApplicationUrl", new Object[]{mapping, form, request, response});
        }

        try {
            securePage(request, form);
            AuthTokenBuilder builder = new AuthTokenBuilder();
            builder.setUsername(ActionHelper.getCurrentUserId(request));

            // Set the expiration
            builder.setExpiration(getApplicationManager().getUtcExpirationDate(30));

            // Add the workItemId parameter
            Properties props = new Properties();
            String workItemId = request.getParameter(ApplicationFields.WORK_ITEM_ID);
            props.setProperty("workItemId", workItemId);
            builder.setOtherProperties(props);

            // Add checksum
            String eAppBaseUrl = SysParmProvider.getInstance().getSysParm(ApplicationFields.EAPP_BASE_URL_OVERRIDE);
            if(StringUtils.isBlank(eAppBaseUrl))
                eAppBaseUrl = ApplicationContext.getInstance().getProperty(ApplicationFields.EAPP_BASE_URL);
            eAppBaseUrl += "view/reviewer/viewForm";
            eAppBaseUrl += "?workItemId=" + workItemId;
            String authKey = SysParmProvider.getInstance().getSysParm(ApplicationFields.EAPP_TOKEN_AUTH_KEY_OVERRIDE);
            if(StringUtils.isBlank(authKey))
                authKey = ApplicationContext.getInstance().getProperty(ApplicationFields.EAPP_TOKEN_AUTH_KEY);
            String authIv = SysParmProvider.getInstance().getSysParm(ApplicationFields.EAPP_TOKEN_AUTH_INIT_VECTOR_OVERRIDE);
            if(StringUtils.isBlank(authIv))
                authIv = ApplicationContext.getInstance().getProperty(ApplicationFields.EAPP_TOKEN_AUTH_INIT_VECTOR);
            String protectedSwatUrl = builder.makeProtectedSwatUrl(eAppBaseUrl, authKey, authIv);

            if (l.isLoggable(Level.FINER)) {
                l.logp(Level.FINER, getClass().getName(), "getApplicationUrl", "eAppBaseUrl: "+eAppBaseUrl+" authKey: "+authKey+" authIv: "+authIv);
            }

            Record result = new Record();
            ApplicationFields.setUrl(result, protectedSwatUrl);
            writeAjaxXmlResponse(response, result);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get application URL.", e, response);
        }
        ActionForward af = null;
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getApplicationUrl", af);
        }
        return af;
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.applicationManagement.noSelection.error");
        MessageManager.getInstance().addJsMessage("pm.applicationManagement.unsavedData.error");
        MessageManager.getInstance().addJsMessage("pm.applicationManagement.reassign.warning");
    }

    /**
     * verifyConfig
     */
    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getApplicationManager() == null)
            throw new ConfigurationException("The required property 'applicationManager' is missing.");
    }

    public ApplicationManager getApplicationManager() {
        return m_applicationManager;
    }

    public void setApplicationManager(ApplicationManager applicationManager) {
        m_applicationManager = applicationManager;
    }

    private ApplicationManager m_applicationManager;
}
