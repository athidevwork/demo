package dti.pm.coveragemgr.manuscriptmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.util.LogUtils;
import dti.oasis.error.ValidationException;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.core.http.RequestIds;
import dti.pm.coveragemgr.manuscriptmgr.ManuscriptFields;
import dti.pm.coveragemgr.manuscriptmgr.ManuscriptManager;
import dti.pm.policymgr.PolicyHeader;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Reader;
import java.sql.Clob;
import java.util.logging.Logger;
import dti.oasis.util.SysParmProvider;
/**
 * Action class for maintain Manuscript.
 * <p/>
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 20, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 02/10/2012       wfu         125055 - Added saveAttachment and loadAttachment.
 * 05/16/2012       jshen       132118 - Set coverageBaseEffectiveToDate into request so that it can be passed onto manuscript page.
 * 05/24/2012       jshen       132118 - Roll back previous change.
 * 06/27/2012       tcheng      134650 - 1)Modified saveAttachment for saving record set before upload RTF.
 *                                     - 2)Added a parameter policyHeader for saveAttachment amd made version for upload RTF
 * 07/20/2012       tcheng      135128 - Modified loadAttachment for support field type is CLOB.
 * 09/05/2012       xnie        136023 - Roll backed 132118 fix.
 * 03/07/2013       awu         145732 - Changed getInitialValuesForAddManuscript to load fields in securePage.
 * 09/25/2015       Elvin       Issue 160360: add js message
 * 03/13/2017       eyin        180675 - Added the third parameter 'true' when calling 'getPolicyHeader' in the method 'saveAllManuscript'.
 * 06/13/2018       wrong       192557 - Modified saveAttachment() to call hasValidSaveToken() to be used for
 *                                       CSRFInterceptor.
 * ---------------------------------------------------
 */
public class MaintainManuscriptAction extends PMBaseAction {
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
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadAllManuscript(mapping, form, request, response);
    }

    /**
     * Method to load list of Manuscript data.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllManuscript(ActionMapping mapping,
                                           ActionForm form,
                                           HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllManuscript", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {

            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            PolicyHeader policyHeader = getPolicyHeader(request, true, true);

            // Load the Manuscript
            RecordSet rs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            if (rs == null) {
                rs = getManuscriptManager().loadAllManuscript(policyHeader, DefaultRecordLoadProcessor.DEFAULT_INSTANCE, getInputRecord(request));
            }
            setDataBean(request, rs);

            // Make the Summary Record available for output
            Record output = rs.getSummaryRecord();

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);

            // Load the list of values after loading the data
            loadListOfValues(request, form);

            // Load grid header
            loadGridHeader(request);

            loadJsMessage();
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the Manuscript page.",
                e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllManuscript", af);
        return af;
    }

    /**
     * Get Inital Values for newly added manuscript
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward getInitialValuesForAddManuscript(ActionMapping mapping,
                                                          ActionForm form,
                                                          HttpServletRequest request,
                                                          HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForAddManuscript",
            new Object[]{mapping, form, request, response});

        try {
            securePage(request, form);
            PolicyHeader policyHeader = getPolicyHeader(request, true, true);

            // Get inital values
            Record record = getManuscriptManager().getInitialValuesForAddManuscript(policyHeader, getInputRecord(request));

            // Get LOV labels for initial values
            publishOutputRecord(request, record);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, record);

            // Send back xml data
            writeAjaxXmlResponse(response, record);

        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values for manuscript.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForAddManuscript", af);
        return af;
    }

    /**
     * Save all manuscript records.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward saveAllManuscript(ActionMapping mapping,
                                           ActionForm form,
                                           HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveAllManuscript", new Object[]{mapping, form, request, response});

        String forwardString = "saveResult";
        RecordSet inputRecords = null;

        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);

                // Get policy header
                PolicyHeader policyHeader;
                String pmUIStyle = SysParmProvider.getInstance().getSysParm("PM_UI_STYLE", "T");
                if(pmUIStyle.equals("T")){
                    policyHeader = getPolicyHeader(request, true, true);
                }else{
                    policyHeader = getPolicyHeader(request, true);
                }


                // Map textXML to RecordSet for input
                inputRecords = getInputRecordSet(request);

                // Save the changes
                getManuscriptManager().saveAllManuscript(policyHeader, inputRecords);
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
                "Failed to save Manuscript.", e, request, mapping);
        }

        // Return the forward
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveAllManuscript", af);
        return af;
    }

    /**
     * Save the import RTF file to db table.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward saveAttachment(ActionMapping mapping,
                                        ActionForm form,
                                        HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveAttachment", new Object[]{mapping, form, request, response});

        try {
            if (hasValidSaveToken(request)) {
                securePage(request, form, false);
                PolicyHeader policyHeader = getPolicyHeader(request);
                Record inputRecord = getInputRecord(request);
                if (inputRecord.hasStringValue(ManuscriptFields.SAVE_MANUSCRIPT_B)) {
                    RecordSet inputRecordSet = getInputRecordSet(request);
                    getManuscriptManager().saveAllManuscript(policyHeader, inputRecordSet);
                }
                // Save file
                getManuscriptManager().saveAttachment(policyHeader, inputRecord);

                // Send back xml data
                writeEmptyAjaxXMLResponse(response);
            }
        } catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to save the file.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "saveAttachment", af);
        return af;
    }

    /**
     * Extract the RTF file from db table.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAttachment(ActionMapping mapping,
                                        ActionForm form,
                                        HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAttachment", new Object[]{mapping, form, request, response});
        String forwardString = null;
        try {
            securePage(request, form, false);
            Record inputRecord = getInputRecord(request);

            // Load file
            Record record = getManuscriptManager().loadAttachment(inputRecord);

            //Output file as output stream to response for downloading
            String fileName = "Manuscript_File_" + ManuscriptFields.getManuscriptEndorsementId(inputRecord) + ".RTF";
            response.setContentType("application/x-msdownload");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            BufferedWriter bufferedWriter = new BufferedWriter(response.getWriter());

            Clob clob = (Clob)record.getFieldValue(ManuscriptFields.FILE_CONTENT);
            Reader reader = new BufferedReader(clob.getCharacterStream());
            int chunkSize = 1024;
            char[] textBuffer = new char[chunkSize];
            int i = -1;
            while((i = reader.read(textBuffer)) != -1){
              bufferedWriter.write(textBuffer, 0, i);
            }
            bufferedWriter.flush();
            bufferedWriter.close();
            reader.close();
            bufferedWriter = null;
            reader = null;
            record = null;
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the file.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAttachment", af);
        return af;
    }

    private void loadJsMessage(){
        MessageManager.getInstance().addJsMessage("pm.maintainManu.dataEntry.saveFirst");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getManuscriptManager() == null)
            throw new ConfigurationException("The required property 'manuscriptManager' is missing.");
    }

    public ManuscriptManager getManuscriptManager() {
        return m_manuscriptManager;
    }

    public void setManuscriptManager(ManuscriptManager manuscriptManager) {
        m_manuscriptManager = manuscriptManager;
    }

    public MaintainManuscriptAction() {
    }

    private ManuscriptManager m_manuscriptManager;
}
