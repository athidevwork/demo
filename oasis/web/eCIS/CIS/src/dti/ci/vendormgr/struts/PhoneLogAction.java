package dti.ci.vendormgr.struts;

import dti.ci.helpers.ICIPhoneLogConstants;
import dti.ci.struts.action.CIBaseAction;
import dti.ci.vendormgr.PhoneLogManager;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.http.RequestIds;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   8/12/14
 *
 * @author wkong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/08/2018       Elvin       Issue 195627: enable default values setting when adding phone log
 * ---------------------------------------------------
 */
public class PhoneLogAction extends CIBaseAction implements ICIPhoneLogConstants{

    private final Logger l = LogUtils.getLogger(getClass());

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
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return load(mapping, form, request, response);
    }

    /**
     * Method to load phone log page.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "load", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadResult";

        try {
            // Secures page
            securePage(request, form);
            Record inputRecord = getInputRecord(request);

            String entityFK = inputRecord.getStringValue(ENTITY_FK_PROPERTY);
            String vendorPK = inputRecord.getStringValue(VENDOR_PK_PROPERTY);

            /* validate */
            if (!FormatUtils.isLong(entityFK)) {
                throw new IllegalArgumentException(new StringBuffer().append(
                        "entity FK [").append(entityFK)
                        .append("] should be a number.")
                        .toString());
            }

            request.setAttribute("pk",entityFK);
            if (!FormatUtils.isLong(vendorPK)) {
                throw new IllegalArgumentException(new StringBuffer().append(
                        "vendor PK [").append(vendorPK)
                        .append("] should be a number.")
                        .toString());
            }

            // load phone log list and set the dataBean to request
            RecordSet phoneLogList = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            if (phoneLogList == null) {
                phoneLogList = getPhoneLogManager().getPhoneLog(inputRecord);
            }
            setDataBean(request, phoneLogList);

            Record summaryRecord = phoneLogList.getSummaryRecord();
            Record outputRecord = new Record();
            outputRecord.setFields(inputRecord);
            outputRecord.setFields(summaryRecord, true);

            request.setAttribute(ENTITY_FK_PROPERTY, entityFK);
            request.setAttribute(VENDOR_PK_PROPERTY, vendorPK);

            publishOutputRecord(request, outputRecord);

            loadGridHeader(request);

            loadListOfValues(request, form);

            addJsMessages();

            saveToken(request);
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR,
                    "Failed to load phone log page.",
                    e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "load", af);
        return af;
    }


    /**
     * Save Phone Log.
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "save", new Object[]{mapping, form, request, response});
        }

        String forwardString = "saveResult";
        RecordSet inputRecordSet = null;
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (isTokenValid(request, true)) {
                // Secure page
                securePage(request, form, false);

                // get input notes record
                Record inputRecord = getInputRecord(request);
                // get input roster record set
                inputRecordSet = getInputRecordSet(request);

                inputRecordSet.setFieldValueOnAll(ENTITY_FK_PROPERTY, inputRecord.getStringValue(ENTITY_FK_PROPERTY));
                inputRecordSet.setFieldValueOnAll(VENDOR_PK_PROPERTY, inputRecord.getStringValue(VENDOR_PK_PROPERTY));

                // Call the business component to implement the save logic
                getPhoneLogManager().savePhoneLog(inputRecordSet);
            }
            saveToken(request);
        } catch (ValidationException ve) {
            // Save the input records into request.
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecordSet);
            // Handle the validation exception.
            handleValidationException(ve, request);
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to save phone log.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "save", af);
        }
        return af;
    }

    public ActionForward getInitialValuesForAddPhoneLog(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForAddPhoneLog", new Object[]{mapping, form, request, response});
        }

        try {
            securePage(request, form);
            Record inputRecord = getInputRecord(request);
            inputRecord.setFieldValue("actionClassName", this.getClass().getName());
            Record initialValues = getPhoneLogManager().getFieldDefaultValues(inputRecord);
            writeAjaxResponse(response, initialValues, true);
        } catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Unable to get initial values for Phone Log page.", e, response);
        }

        l.exiting(getClass().getName(), "getInitialValuesForAddPhoneLog");
        return null;
    }

    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("ci.common.error.rowSelect.delete");
        MessageManager.getInstance().addJsMessage("js.delete.confirmation");
        MessageManager.getInstance().addJsMessage("ci.entity.message.value.noEmpty");
        MessageManager.getInstance().addJsMessage("ci.common.error.date.valid");
        MessageManager.getInstance().addJsMessage("ci.entity.message.notesError.notAvailable");
        MessageManager.getInstance().addJsMessage("ci.common.error.element.greater");
        MessageManager.getInstance().addJsMessage("ci.vendor.phoneLog.invalidPhoneNumber");
    }

    public void verifyConfig() {
        if (getPhoneLogManager() == null) {
            throw new ConfigurationException("The required property 'phoneLogManager' is missing.");
        }
    }

    public PhoneLogManager getPhoneLogManager() {
        return m_phoneLogManager;
    }

    public void setPhoneLogManager(PhoneLogManager phoneLogManager) {
        this.m_phoneLogManager = phoneLogManager;
    }

    private PhoneLogManager m_phoneLogManager;
}
