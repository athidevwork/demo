package dti.pm.coveragemgr.manuscriptmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.oasis.struts.IOasisAction;
import dti.oasis.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.coveragemgr.manuscriptmgr.ManuscriptManager;
import dti.pm.coveragemgr.manuscriptmgr.ManuscriptFields;
import dti.pm.policymgr.PolicyHeader;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;
import java.util.List;

/**
 * Action class for maintain Manuscript detail.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 24, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/09/2011       Jerry       Issue 123574 - Upper case the field Id for record filter when finding the field format
 * 10/09/2014       wdang       Issue 156038 - Replaced getPolicyHeader(request, true) with getPolicyHeader(request) in 
 *                                             getInitialValuesForAddManuscriptDetail() and saveAllManuscriptDetail().
 * ---------------------------------------------------
 */
public class MaintainManuscriptDetailAction extends PMBaseAction {
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
        return loadAllManuscriptDetail(mapping, form, request, response);
    }

    /**
     * Method to load list of Manuscript Detail data.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllManuscriptDetail(ActionMapping mapping,
                                                 ActionForm form,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllManuscriptDetail", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {

            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            PolicyHeader policyHeader = getPolicyHeader(request);
            Record record = getInputRecord(request);

            // load manuscript detail formatting information
            RecordSet detailFormattingRs = getManuscriptManager().loadManuscriptEndorsementDtl(record);

            // format the detail fields
            formatDetailFields(request, detailFormattingRs);

            // Load the Manuscript
            RecordSet rs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            if (rs == null) {
                rs = getManuscriptManager().loadAllManuscriptDetail(policyHeader, record,
                    DefaultRecordLoadProcessor.DEFAULT_INSTANCE);
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

            request.setAttribute(ManuscriptFields.FORM_CODE, ManuscriptFields.getFormCode(record));
            request.setAttribute(ManuscriptFields.MANUSCRIPT_ENDORSEMENT_ID, ManuscriptFields.getManuscriptEndorsementId(record));
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the Manuscript Detail page.",
                e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllManuscriptDetail", af);
        return af;
    }

    /**
     * Get Inital Values for newly added manuscript detail
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward getInitialValuesForAddManuscriptDetail(ActionMapping mapping,
                                                                ActionForm form,
                                                                HttpServletRequest request,
                                                                HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForAddManuscriptDetail",
            new Object[]{mapping, form, request, response});

        try {
            securePage(request, form, false);
            PolicyHeader policyHeader = getPolicyHeader(request);

            // Get inital values
            Record record = getManuscriptManager().getInitialValuesForAddManuscriptDetail(
                policyHeader, getInputRecord(request));

            // Get LOV labels for initial values
            publishOutputRecord(request, record);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, record);

            // Send back xml data
            writeAjaxXmlResponse(response, record);

        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial values for manuscript Detail.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForAddManuscriptDetail", af);
        return af;
    }

    /**
     * Save all manuscript detail records.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward saveAllManuscriptDetail(ActionMapping mapping,
                                                 ActionForm form,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveAllManuscriptDetail", new Object[]{mapping, form, request, response});

        String forwardString = "saveResult";
        RecordSet inputRecords = null;

        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields
                securePage(request, form, false);

                // Get policy header
                PolicyHeader policyHeader = getPolicyHeader(request);

                // Map textXML to RecordSet for input
                inputRecords = getInputRecordSet(request);

                // Save the changes
                getManuscriptManager().saveAllManuscriptDetail(policyHeader, inputRecords);
            }
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR,
                "Failed to save Manuscript Detail.", e, request, mapping);
        }

        // Return the forward
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveAllManuscriptDetail", af);
        return af;
    }

    /**
     * Format manuscript detail fields (label, visibility and data type of layer fields and page fields).
     *
     * @param request
     * @param detailFormattingRs
     */
    private void formatDetailFields(HttpServletRequest request, RecordSet detailFormattingRs) {
        OasisFields oasisFields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);

        // layer fields
        List layerFields = oasisFields.getLayerFields("PM_MANU_DETAIL_GH");
        int size = layerFields.size();
        for (int i = 0; i < size; i++) {
            OasisFormField field = (OasisFormField) layerFields.get(i);
            String fieldId = field.getFieldId();
            fieldId = fieldId.substring(0, fieldId.indexOf("_GH"));
            fieldId = fieldId.substring(0, 5) + "_" + fieldId.substring(5);
            RecordFilter fieldFilter = new RecordFilter(ManuscriptFields.COL_NAME, fieldId.toUpperCase());
            RecordSet filteredFieldRs = detailFormattingRs.getSubSet(fieldFilter);
            if (filteredFieldRs.getSize() == 1) {
                Record formattingRecord = filteredFieldRs.getFirstRecord();
                field.setLabel(ManuscriptFields.getColLabel(formattingRecord));
            }
            else {
                field.setIsVisible(false);
            }
        }

        // page fields
        List pageFields = oasisFields.getPageFields();
        size = pageFields.size();
        for (int i = 0; i < size; i++) {
            OasisFormField field = (OasisFormField) pageFields.get(i);
            String fieldId = field.getFieldId();
            fieldId = fieldId.substring(0, 5) + "_" + fieldId.substring(5);
            RecordFilter fieldFilter = new RecordFilter(ManuscriptFields.COL_NAME, fieldId.toUpperCase());
            RecordSet filteredFieldRs = detailFormattingRs.getSubSet(fieldFilter);
            if (filteredFieldRs.getSize() == 1) {
                Record formattingRecord = filteredFieldRs.getFirstRecord();
                // set label
                field.setLabel(ManuscriptFields.getColLabel(formattingRecord));
                // set data type
                String dataType = getDataType(formattingRecord);
                if (dataType != null) {
                    field.setDatatype(dataType);
                }
            }
            else {
                field.setIsVisible(false);
            }
        }
    }

    /**
     * Get data type of the formatted record
     * 
     * @param formattingRecord
     * @return
     */
    private String getDataType(Record formattingRecord) {
        String dataType = null;
        String fdataType = ManuscriptFields.getDataType(formattingRecord);
        if (fdataType.equalsIgnoreCase("number")) {
            dataType = OasisFields.TYPE_NUMBER;
        }
        else if (fdataType.equalsIgnoreCase("varchar2")) {
            dataType = OasisFields.TYPE_TEXT;
        }
        else if (fdataType.equalsIgnoreCase("date")) {
            dataType = OasisFields.TYPE_DATE;
        }
        return dataType;
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

    public MaintainManuscriptDetailAction() {
    }

    private ManuscriptManager m_manuscriptManager;
}
