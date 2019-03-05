package dti.ci.phonemgr.struts;

import dti.ci.entityadditionalmgr.EntityAdditionalFields;
import dti.ci.helpers.ICIConstants;
import dti.ci.helpers.ICIPhoneNumberConstants;
import dti.ci.helpers.CILinkGenerator;
import dti.ci.phonemgr.PhoneListManager;
import dti.ci.struts.action.CIBaseAction;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.util.*;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.messagemgr.MessageManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Phone Number List Action Class.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author Gerald C. Carney
 *         Date:   Mar 22, 2004
 *         <p/>
 *         Revision Date    Revised By  Description
 *         ------------------------------------------------------------------
 *         04/01/2005       HXY         Extends CIBaseAction.
 *         04/08/2005       HXY         Used OasisFields to set up grid header.
 *         01/17/2007       Fred        Added call to CILinkGenerator.generateLink()
 *         7/2/2010         Blake       Add All source function for issue 103463
 *         10/06/2010       wfu         111776: Replaced hardcode string with resource definition
 *         12/01/2011       kshen       Isuse 126632.
 *         02/14/2012       parker      for Isuse 128809.default value for "source" field.
 *         03/06/2012       Parker      130270. set CIS notes visiable for this business.
 *         08/28/2014       ylu         156586: handle with "All source" and "Select" option
 *         ------------------------------------------------------------------
 */

public class MaintainPhoneListAction extends CIBaseAction {
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
    public ActionForward unspecified(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        return loadPhoneList(mapping, form, request, response);
    }

    /**
     * Execute method for action class.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     */
    public ActionForward loadPhoneList(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadPhoneList", new Object[]{mapping, form, request, response});
        }
        String forwardString = "loadpage";
        Record inputRecord = getInputRecord(request);

        setEntityCommonInfoToRequest(request, inputRecord);

        String entityId =inputRecord.getStringValue(EntityAdditionalFields.PK_PROPERTY);
        if (!FormatUtils.isLong(entityId)) {
            throw new IllegalArgumentException(new StringBuffer().append(
                    "entity FK [").append(entityId)
                    .append("] should be a number.")
                    .toString());
        }
        inputRecord.setFieldValue("entityId",entityId);
        try {
            // Secures page
            securePage(request, form);
            String entityType = inputRecord.getStringValue(ICIConstants.ENTITY_TYPE_PROPERTY);
            if (entityType.charAt(0) == ENTITY_TYPE_ORG_CHAR) {
                checkCisFolderMenu(request);
            }

            ArrayList srcRecList = getPhoneListManager().createSourceRecordLOV(inputRecord);
            request.setAttribute(ICIPhoneNumberConstants.PHONE_NUM_SRC_REC_FK_ID + "LOV", srcRecList);
            
            String srcRecFK = request.getParameter(ICIPhoneNumberConstants.PHONE_NUM_SRC_REC_FK_ID);

            if (SysParmProvider.getInstance().getSysParm(ICIPhoneNumberConstants.CI_SHOW_ALL_PHONENUM, "N").equals(ICIPhoneNumberConstants.SHOW_ALL_SOURCES)) {
                if (StringUtils.isBlank(srcRecFK)) {
                    srcRecFK = ICIPhoneNumberConstants.SELECT_ALL_SOURCES_VALUE;
                }
            } else {
                if (ICIPhoneNumberConstants.SELECT_ALL_SOURCES_VALUE.equals(srcRecFK)) {
                    // when srcRecFk getting "-2", it means page initial open from menu,
                    // transform -2 to -1 for display "Select" option, driven by CI_SHOW_ALL_PHONENUM=N
                    srcRecFK = ICIPhoneNumberConstants.SELECT_SELECT_VALUE;
                }
            }

            RecordSet rs = new RecordSet();
            if (srcRecFK == null) {
                inputRecord.setFieldValue(ICIPhoneNumberConstants.ENTITY_ID,-9999);
                inputRecord.setFieldValue(ICIPhoneNumberConstants.SOURCE_RECORD_ID,-9999);
            }else{
                inputRecord.setFieldValue(ICIPhoneNumberConstants.ENTITY_ID,entityId);
                inputRecord.setFieldValue(ICIPhoneNumberConstants.SOURCE_RECORD_ID,getPhoneListManager().transformSourceFK(srcRecFK));
                forwardString = "loadgrid";
            }
            rs = getPhoneListManager().getPhoneNumberList(inputRecord);

            new CILinkGenerator().generateLink(request, entityId, this.getClass().getName());
            request.setAttribute(ICIPhoneNumberConstants.CUR_PHONE_NUM_SRC_REC_FK_PROPERTY, srcRecFK);
//            request.setAttribute(CIPhoneNumberListHelper.MSG_PROPERTY,message);

            setCisHeaderFields(request);
            setDataBean(request, rs);
            loadGridHeader(request);

            // Make the Summary Record available for output
            Record output = rs.getSummaryRecord();
            // Publish the input record
            publishOutputRecord(request, output);

            loadListOfValues(request, form);

            saveToken(request);
            addJsMessages();

        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR,
                "Failed to load the phoneList page.",
                e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadPhoneList", af);
        }
        return af;
    }
    
    /**
     * save PhoneList Data.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward savePhoneList(ActionMapping mapping, ActionForm form,
                                            HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "savePhoneList",
                new Object[]{mapping, form, request, response});
        }
        String forwardString = "savePhoneList";

        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                securePage(request, form);
                getPhoneListManager().saveAllPhoneNumber(getInputRecordSet(request));
            }
        }catch (ValidationException v) {
            // Handle the validation exception.
            handleValidationException(v, request);
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to Save the PhoneList page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "savePhoneList", af);
        return af;
    }

    //add js message
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("ci.phoneNumberList.select.warning");
        MessageManager.getInstance().addJsMessage("js.delete.confirmation");
        MessageManager.getInstance().addJsMessage("ci.common.error.rowSelect.delete");

        MessageManager.getInstance().addJsMessage("ci.entity.message.areaCode.invalid");
        MessageManager.getInstance().addJsMessage("ci.entity.message.source.noSelect");
        MessageManager.getInstance().addJsMessage("ci.entity.message.phoneNumber.noDeleted");
        MessageManager.getInstance().addJsMessage("ci.entity.message.phoneNumber.noChanged");
        MessageManager.getInstance().addJsMessage("ci.entity.message.phoneNumber.noAdded");
        MessageManager.getInstance().addJsMessage("ci.entity.message.source.onePrimary");
        MessageManager.getInstance().addJsMessage("ci.entity.message.record.sourceRequired");
        MessageManager.getInstance().addJsMessage("ci.entity.message.record.clientRequired");
        MessageManager.getInstance().addJsMessage("ci.entity.message.record.typeRequired");
        MessageManager.getInstance().addJsMessage("ci.entity.message.record.areaCodeRequired");
        MessageManager.getInstance().addJsMessage("ci.entity.message.areaCode.number");
        MessageManager.getInstance().addJsMessage("ci.entity.message.record.phoneNumberRequired");

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

    /**
     *  method to handle the ajax request to get the init values from workbench for this page
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getIntialValuesForAddingPhoneNumber(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response) throws Exception {

        Logger l = LogUtils.enterLog(getClass(),"getIntialValuesForAddingPhoneNumber", new Object[]{mapping, form, request, response});
        securePage(request, form);
        Record input = getInputRecord(request);
        input.setFieldValue("className",getClass().getName());
        Record initValues = getPhoneListManager().getIntialValuesForAddingPhoneNumber(input);
        writeAjaxXmlResponse(response, initValues);
        l.exiting(getClass().getName(),"getIntialValuesForAddingPhoneNumber");
        return null;
    }


    public void verifyConfig() {
        if (getPhoneListManager() == null) {
            throw new ConfigurationException("The required property 'phoneListManager' is missing.");
        }
    }
    
    public PhoneListManager getPhoneListManager() {
        return m_phoneListManager;
    }

    public void setPhoneListManager(PhoneListManager phoneListManager) {
        this.m_phoneListManager = phoneListManager;
    }

    private PhoneListManager m_phoneListManager;
}
