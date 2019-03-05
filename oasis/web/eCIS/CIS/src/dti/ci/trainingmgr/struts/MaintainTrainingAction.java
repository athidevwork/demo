package dti.ci.trainingmgr.struts;

import dti.ci.core.struts.MaintainEntityFolderBaseAction;
import dti.ci.trainingmgr.TrainingManager;
import dti.ci.trainingmgr.TrainingFields;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.*;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Action Class CIS training page.
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 12, 2006
 *
 * @author Hong Yuan
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/17/2007       Fred        Added call to CILinkGenerator.generateLink()
 * 10/06/2010       wfu         111776: Replaced hardcode string with resource definition
 * 06/01/2011       kshen       121377: Added token check when saving training records.
 * 03/06/2012       Parker      130270. set CIS notes visible for this business.
 * 10/16/2018       Elvin       Issue 195835: grid replacement, extends MaintainEntityFolderBaseAction
 * ---------------------------------------------------
*/

public class MaintainTrainingAction extends MaintainEntityFolderBaseAction {

    private final Logger l = LogUtils.getLogger(getClass());

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
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadTrainingList(mapping, form, request, response);
    }

    /**
     * Get training list for entity.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadTrainingList(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadTrainingList", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadTrainingList";

        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);
            String entityId = getEntityIdForMaintainEntityAction(request);
            inputRecord.setFieldValue("entityId", entityId);

            RecordSet rs = (RecordSet) request.getAttribute("gridRecordSet");
            if (rs == null) {
                rs = getTrainingManager().loadTrainingList(inputRecord);
            }
            setDataBean(request, rs);
            loadGridHeader(request);

            // Get entity Birth
            request.setAttribute(TrainingFields.DATE_OF_BIRTH, rs.getSummaryRecord().getStringValue(TrainingFields.DATE_OF_BIRTH));

            loadListOfValues(request,form);

            addJsMessages();

            saveToken(request);
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load Training page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadTrainingList", af);
        }
        return af;
    }

    /**
     * save Edication Data.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward saveTrainingData(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveTrainingData", new Object[]{mapping, form, request, response});
        }

        String forwardString = "saveTrainingData";
        RecordSet inputRs = null;

        try {
            if (hasValidSaveToken(request)) {
                securePage(request, form);

                inputRs = getInputRecordSet(request);
                inputRs.setSummaryRecord(getInputRecord(request));

                getTrainingManager().saveTrainingData(inputRs);
            }
        } catch (ValidationException v) {
            request.setAttribute("gridRecordSet", inputRs);
            handleValidationException(v, request);
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to Save the Training page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveTrainingData", af);
        }
        return af;
    }

    public ActionForward getInitialValuesForAddTraining(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForAddTraining", new Object[]{mapping, form, request, response});
        }

        try {
            securePage(request, form);
            Record inputRecord = getInputRecord(request);
            inputRecord.setFieldValue("className", this.getClass().getName());
            Record initialValues = getTrainingManager().getInitialValuesForAddTraining(inputRecord);
            writeAjaxResponse(response, initialValues, true);
        } catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Unable to get initial values.", e, response);
        }

        l.exiting(getClass().getName(), "getInitialValuesForAddTraining");
        return null;
    }

    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("ci.common.error.rowSelect.delete");
        MessageManager.getInstance().addJsMessage("js.delete.confirmation");
        MessageManager.getInstance().addJsMessage("ci.common.error.existRecords.delete");
        MessageManager.getInstance().addJsMessage("ci.common.error.date.valid");
        MessageManager.getInstance().addJsMessage("ci.common.error.birthInception.after");
        MessageManager.getInstance().addJsMessage("ci.common.error.certifiedDate.after");
    }

    public void verifyConfig() {
        if (getTrainingManager() == null) {
            throw new ConfigurationException("The required property 'trainingManager' is missing.");
        }
    }

    public TrainingManager getTrainingManager() {
        return m_trainingManager;
    }

    public void setTrainingManager(TrainingManager trainingManager) {
        this.m_trainingManager = trainingManager;
    }

    private TrainingManager m_trainingManager;
}
