package dti.ci.educationmgr.struts;

import dti.ci.educationmgr.EducationFields;
import dti.ci.educationmgr.EducationManager;

import dti.ci.helpers.CILinkGenerator;
import dti.ci.struts.action.CIBaseAction;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.ActionHelper;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.oasis.util.*;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Action Class for Education
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   May 17, 2006
 *
 * @author gjli
 */

/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/17/2007       Fred        Added call to CILinkGenerator.generateLink()
 * 04/3/2007        Jerry       Entities based on the value entered in the system parameter 'CI_SCHOOL_CLASS'.
 * 07/19/2007       FWCH        Replaced ' with &#039; in institution name
 * 07/21/2008       Guang       Removed with &#039; logic, no longer needed. created hidden fields in
 *                                  ciEducationseLinsNamePopup.jsp to take care special chars
 * 10/06/2010       wfu         111776: Replaced hardcode string with resource definition
 * 12/01/2014        ylu        158252: remove reference of CIW_EDU_COUNTRY_DD, it should be driven by WorkWB
 * 07/14/2016       dpang       176370: set initial values when adding education.
 * ---------------------------------------------------
*/
public class MaintainEducationAction extends CIBaseAction {
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
        return loadEducationList(mapping, form, request, response);
    }

    /**
     * Get education list for entity.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadEducationList(ActionMapping mapping, ActionForm form,
                                            HttpServletRequest request, HttpServletResponse response) throws Exception {

        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadEducationList", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadEducationList";
        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);
            OasisFields fields = ActionHelper.getFields(request);

            /* validate */
            String entityId = request.getParameter(PK_PROPERTY);
            if (!FormatUtils.isLong(entityId)) {
                throw new IllegalArgumentException(new StringBuffer().append(
                        "entity FK [").append(entityId)
                        .append("] should be a number.")
                        .toString());
            }

            String entityType = request.getParameter(ENTITY_TYPE_PROPERTY);  
            /* set menu beans Search & Select an entity type of  'organization'.*/
            if (entityType.charAt(0) == ENTITY_TYPE_ORG_CHAR) {
                checkCisFolderMenu(request);
            }

            RecordSet rs = (RecordSet) request.getAttribute("gridRecordSet");
            if (rs == null) {
                // load Education Grid List
                inputRecord.setFieldValue("entityId",entityId);
                rs = getEducationManager().loadEducationList(inputRecord);
            }
            setCisHeaderFields(request);
            setDataBean(request, rs);
            loadGridHeader(request);

            // Get entity Birth and Death
            Record entityInfoRecord = getEducationManager().getEntityInfo(inputRecord);
            request.setAttribute(EducationFields.DATE_OF_BIRTH, EducationFields.getDateOfBirth(entityInfoRecord));
            request.setAttribute(EducationFields.DATE_OF_DEATH, EducationFields.getDateOfDeath(entityInfoRecord));

            setEntityCommonInfoToRequest(request, inputRecord);
            request.setAttribute(ENTITY_FK_PROPERTY, entityId);
            loadListOfValues(request,form);

            /* Gets links for Paging */
            new CILinkGenerator().generateLink(request, entityId, this.getClass().getName());
            saveToken(request);
            addJsMessages();            
        }
        catch (Exception e) {
            forwardString = handleError(
                    AppException.UNEXPECTED_ERROR, "Failed to load Education page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadEducationList", af);
        }
        return af;
    }

    //add js message
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("ci.common.error.rowSelect.delete");
        MessageManager.getInstance().addJsMessage("js.delete.confirmation");
        MessageManager.getInstance().addJsMessage("ci.common.error.newRecords.delete");
        MessageManager.getInstance().addJsMessage("ci.common.error.existRecords.delete");
        MessageManager.getInstance().addJsMessage("ci.entity.message.institution.required");
        MessageManager.getInstance().addJsMessage("ci.entity.message.year.invalid");
        MessageManager.getInstance().addJsMessage("ci.entity.message.year.outOfRange");
        MessageManager.getInstance().addJsMessage("ci.entity.message.year.later");
        MessageManager.getInstance().addJsMessage("ci.entity.message.endDate.afterStartDate");
        MessageManager.getInstance().addJsMessage("ci.entity.message.startDate.entered");
        MessageManager.getInstance().addJsMessage("ci.entity.message.startDate.earlier");
        MessageManager.getInstance().addJsMessage("ci.entity.message.startDate.notLater");
        MessageManager.getInstance().addJsMessage("ci.entity.message.endDate.notLater");

        MessageManager.getInstance().addJsMessage("ci.entity.message.stateCode.required");
        MessageManager.getInstance().addJsMessage("ci.entity.message.endDate.notLater");
        MessageManager.getInstance().addJsMessage("ci.entity.message.endDate.notLater");
        MessageManager.getInstance().addJsMessage("js.refresh.lose.changes.confirmation");
        MessageManager.getInstance().addJsMessage("cs.changes.lost.confirm");
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
    public ActionForward saveEducationData(ActionMapping mapping, ActionForm form,
                                           HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveEducationDataInfo", new Object[]{mapping, form, request, response});
        }

        String forwardString = "saveEducationData";
        RecordSet inputRs = null;

        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                securePage(request, form);

                inputRs = getInputRecordSet(request);
                inputRs.setSummaryRecord(getInputRecord(request));

                getEducationManager().saveEducationData(inputRs);
            }
        } catch (ValidationException v) {
            // Save the recordset into the request
            request.setAttribute("gridRecordSet", inputRs);

            // Handle the validation exception.
            handleValidationException(v, request);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to Save the Education page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveEducationInfo", af);
        return af;

    }

    public ActionForward getInitialValuesForAddEducation(ActionMapping mapping, ActionForm form,
                                                        HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForAddEducation");
        securePage(request, form);
        Record inputRecord = getInputRecord(request);
        inputRecord.setFieldValue("className", this.getClass().getName());
        Record initialValues = getEducationManager().getInitialValuesForAddEducation(inputRecord);
        writeAjaxResponse(response, initialValues, true);
        l.exiting(getClass().getName(), "getInitialValuesForAddEducation", initialValues);
        return null;
    }

    public void verifyConfig() {
        if (getEducationManager() == null) {
            throw new ConfigurationException("The required property 'educationManager' is missing.");
        }
    }

    public EducationManager getEducationManager() {
        return m_educationManager;
    }

    public void setEducationManager(EducationManager educationManager) {
        this.m_educationManager = educationManager;
    }

    private EducationManager m_educationManager;
}
