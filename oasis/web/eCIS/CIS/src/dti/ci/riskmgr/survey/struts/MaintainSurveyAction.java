package dti.ci.riskmgr.survey.struts;

import dti.ci.core.struts.MaintainEntityFolderBaseAction;
import dti.ci.riskmgr.survey.SurveyManager;
import dti.oasis.app.ConfigurationException;
import dti.oasis.app.AppException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.util.LogUtils;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.error.ValidationException;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * The Action class for Risk Management Survey Tracking.
 * <p>(C) 2009 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 10, 2009
 *
 * @author gjlong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/02/2009       Leo         Issue 101300
 * 10/06/2010       wfu         111776: Replaced hardcode string with resource definition
 * 06/29/2018       ylu         Issue 194117: additional update.
 * ---------------------------------------------------
 */

public class MaintainSurveyAction extends MaintainEntityFolderBaseAction {

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
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadAllSurvey(mapping, form, request, response);
    }

    /** method to handle the request with process parmeter of loadAllSurvey
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllSurvey(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l =LogUtils.enterLog(getClass(),"loadAllSurvey", request);
        String forwardString = "loadAll";
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            securePage(request, form);
            // get input
            Record inputRecord = getInputRecord(request);

            inputRecord.setFieldValue("entityId", inputRecord.getStringValue("pk"));
            // load survey data set the dataBean to request
            RecordSet rs = (RecordSet) request.getAttribute(SURVEY_RECORD_SET);
            if (rs == null) { // get rs only if have to
              rs =  getSurveyManager().loadAllSurvey(inputRecord);
            }
            publishOutputRecord(request, rs.getSummaryRecord());

            setDataBean(request, rs);

            // Load survey grid header
            loadGridHeader(request);

            // load list of values
            loadListOfValues(request, form);

            //add js message
            addJsMessages();
            // Save token
            saveToken(request);
        }
        catch (Exception e) {
            l.warning(getClass().getName()+".loadAllSurvey"+e.getMessage());
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the Survey information.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(),"loadAllSurvey");
        return af;
    }

     /** method to handle the request with process parmeter of saveAllSurvey
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveAllSurvey(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
         Logger l = LogUtils.enterLog(getClass(), "saveAllSurvey", request);
         RecordSet rs = null;
         try {
             if (hasValidSaveToken(request)) {
                 rs = getInputRecordSet(request);
                 int rows = getSurveyManager().saveAllSurvey(rs);
             }
         }
         catch (ValidationException ve) {
             // Save the input records into request, so loadAllSurvey can optinally skipp the DAO calls
             request.setAttribute(SURVEY_RECORD_SET, rs);
             // Handle the validation exception
             handleValidationException(ve, request);
         }
         catch (Exception e) {
             l.warning(getClass().getName()+".loadAllSurvey"+e.getMessage());
             handleError(AppException.UNEXPECTED_ERROR, "Failed to save the Survey information.", e, request, mapping);

         }
         l.exiting(getClass().getName(), "saveAllSurvey");

         return loadAllSurvey(mapping, form, request, response);
     }

    /** method to handle the AJAX request to obtain the default values when adding a new row
      *
      * @param mapping
      * @param form
      * @param request
      * @param response
      * @return
      * @throws Exception
      */
     public ActionForward getInitialValuesForNewSurvey(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
           Logger l =LogUtils.enterLog(getClass(),"getInitialValuesForNewSurvey", request);
          try {
           // Secure access to the page, load the Oasis Fields without loading the LOVs,
            securePage(request, form);

            // get input
            Record inputRecord = getInputRecord(request);
            Record initialRecord = getSurveyManager().getInitialValuesForNewSurvey(inputRecord);

             // get LOV labels for initial values
            publishOutputRecord(request, initialRecord);
            loadListOfValues(request, form);
            getLovLabelsForInitialValues(request, initialRecord);

            // prepare return values
            writeAjaxXmlResponse(response, initialRecord, true);
        }
        catch (Exception e) {
            l.warning(getClass().getName()+".loadAllSurvey"+e.getMessage());
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get initial Survey data.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getInitialValuesForNewSurvey", af);
        return af;

     }


    public void verifyConfig() {
        if (getSurveyManager() == null) {
            throw new ConfigurationException("The required property 'SurveyManager' is missing.");
        }
      }

    public SurveyManager getSurveyManager(){
        return surveyManager;
    }

    public void setSurveyManager(SurveyManager  surveymgr){
        this.surveyManager = surveymgr;
    }

    private SurveyManager surveyManager;
    private static String SURVEY_RECORD_SET = "surveyRecordSet";

    //add js message
    private void addJsMessages() {
        // add js messages for csCommon.js
        MessageManager.getInstance().addJsMessage("cs.changes.lost.confirm");
        MessageManager.getInstance().addJsMessage("cs.save.process.notCompleted");
        MessageManager.getInstance().addJsMessage("cs.term.select.error.noSelect");
        MessageManager.getInstance().addJsMessage("cs.entity.miniPopup.error.noEntityId");
        MessageManager.getInstance().addJsMessage("cs.function.error.notExist");
        MessageManager.getInstance().addJsMessage("cs.records.delete.confirm");
        MessageManager.getInstance().addJsMessage("cs.entity.information.error.notRecorded");
        MessageManager.getInstance().addJsMessage("cs.rowSelected.error.exception");
        MessageManager.getInstance().addJsMessage("cs.run.error.grid.value");
    }
}
