package dti.ci.entityadditionalmgr.struts;


import dti.ci.entityadditionalmgr.EntityAdditionalFields;
import dti.ci.entityadditionalmgr.EntityAdditionalManager;
import dti.ci.helpers.CILinkGenerator;
import dti.ci.struts.action.CIBaseAction;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.http.RequestIds;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.IOasisAction;
import dti.oasis.util.LogUtils;
import dti.oasis.util.PageBean;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * User: Michael
 * Date: February 08, 2011
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/06/2012       Parker      130270. set CIS notes visiable for this business.
 *
 * ---------------------------------------------------
 */
public class MaintainEntityAdditionalAction extends CIBaseAction {

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

        return loadAllAvailableEntityAdditionals(mapping, form, request, response);
    }

    /**
     * Load all entityAdditional
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllAvailableEntityAdditionals(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                 HttpServletResponse response) throws Exception {

        Logger l = LogUtils.enterLog(getClass(), "loadAllAvailableEntityAdditionals", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";


        try {
            String pk = request.getParameter(EntityAdditionalFields.PK_PROPERTY);
            String entityName = request.getParameter(EntityAdditionalFields.ENTITY_NAME_PROPERTY);
            String entityType = request.getParameter(EntityAdditionalFields.ENTITY_TYPE_PROPERTY);
            request.setAttribute(EntityAdditionalFields.PK_PROPERTY, pk);
            request.setAttribute(EntityAdditionalFields.ENTITY_NAME_PROPERTY, entityName);
            request.setAttribute(EntityAdditionalFields.ENTITY_TYPE_PROPERTY, entityType);
             if (entityType.charAt(0) == ENTITY_TYPE_ORG_CHAR) {
                checkCisFolderMenu(request);
            }
            securePage(request, form);
            Record inputRecord = getInputRecord(request);
            inputRecord.setFieldValue("entityId", pk);

            RecordSet reviewRs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            if (reviewRs == null) {
                reviewRs = getEntityAdditionalManager().loadAllAvailableEntityAdditionals(inputRecord);
            }
            Record output = null;
            if (reviewRs != null && reviewRs.getSize() >= 1) {
                output = reviewRs.getFirstRecord();
                request.setAttribute(EntityAdditionalFields.SQL_OPERATION_PROPERTY, EntityAdditionalFields.UPDATE_CODE);
            } else {

                output = new Record();
                request.setAttribute(EntityAdditionalFields.SQL_OPERATION_PROPERTY, EntityAdditionalFields.INSERT_CODE);
            }
            // Make the Summary Record available for output.

            output.setFieldValue("entityId", pk);
            // Publish the output record for use by the Oasis Tags and JSP.
            publishOutputRecord(request, output);

            // Load LOV
            loadListOfValues(request, form);

            addJsMessages();
            setCisHeaderFields(request); 
            
            saveToken(request);
            
            this.highLightCurrentMenuItem((PageBean) request.getAttribute(IOasisAction.KEY_PAGEBEAN));
            new CILinkGenerator().generateLink(request, pk, this.getClass().getName());

        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the EntityAdditional page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllAvailableEntityAdditionals", af);
        return af;
    }

    /**
     * Save all entityAdditional.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveAllEntityAdditionals(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllEntityAdditionals", new Object[]{mapping, form, request, response});
        }

        String forwardString = "saveResult";
        RecordSet inputRecords = new RecordSet();
        Record inputRecord = null;
        String sqlOperation;
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure access to the page without loading the Oasis Fields.
                securePage(request, form);
                // Map litigation review textXML to RecordSet for input.
                inputRecord = getInputRecord(request);
                this.dealPercentage(inputRecord);
                sqlOperation = (String) inputRecord.getFieldValue(EntityAdditionalFields.SQL_OPERATION_PROPERTY);
                if (sqlOperation.indexOf(EntityAdditionalFields.UPDATE_CODE) > -1) {
                    inputRecord.setUpdateIndicator(UpdateIndicator.UPDATED);
                } else {
                    inputRecord.setUpdateIndicator(UpdateIndicator.INSERTED);
                }
                inputRecords.addRecord(inputRecord);
                // Save all the entityAdditionalAddress.
                getEntityAdditionalManager().saveAllEntityAdditionals(inputRecords);

            }
        }
        catch (ValidationException v) {
            // Save the input records into request.
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);
            // Handle the validation exception.
            handleValidationException(v, request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to save EntityAdditional review.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveAllEntityAdditionals", af);
        return af;
    }

    //add js message

    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("ci.common.error.certifiedDate.after");
        MessageManager.getInstance().addJsMessage("ci.entity.message.payment.before");
    }

    private void dealPercentage(Record inputRecord) {
        String pctWcPractice = (String) inputRecord.getFieldValue(EntityAdditionalFields.PCTWCPRACTICE);
        String newpctWcPractice;
        if (pctWcPractice != null && pctWcPractice.indexOf("%") > -1) {
            newpctWcPractice = pctWcPractice.substring(0, pctWcPractice.length() - 1);
            inputRecord.setFieldValue("pctWcPractice", String.valueOf(Float.parseFloat(newpctWcPractice) / 100));
        }
    }
    /* Configuration constructor and accessor methods */

    public void verifyConfig() {
        if (getEntityAdditionalManager() == null)
            throw new ConfigurationException("The required property 'getEntityAdditionalManager' is missing.");
    }

    public EntityAdditionalManager getEntityAdditionalManager() {
        return entityAdditionalManager;
    }

    public void setEntityAdditionalManager(EntityAdditionalManager entityAdditionalManager) {
        this.entityAdditionalManager = entityAdditionalManager;
    }

    private EntityAdditionalManager entityAdditionalManager;


}
