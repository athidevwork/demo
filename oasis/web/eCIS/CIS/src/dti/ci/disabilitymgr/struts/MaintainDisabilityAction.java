package dti.ci.disabilitymgr.struts;

import dti.ci.disabilitymgr.DisabilityFields;
import dti.ci.disabilitymgr.DisabilityManager;
import dti.ci.helpers.CILinkGenerator;
import dti.ci.struts.action.CIBaseAction;
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
 * Action Class for Disability
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   May 12, 2006
 *
 * @author bhong
 */

/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/17/2007       Fred        Added call to CILinkGenerator.generateLink()
 * 10/05/2007       kshen       Check CIS menu for org.
 * 10/06/2010       wfu         111776: Replaced hardcode string with resource definition
 * 01/20/2011       Michael Li  Issue:116335
 * ---------------------------------------------------
*/
public class MaintainDisabilityAction extends CIBaseAction {
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
        return loadDisabilityList(mapping, form, request, response);
    }

    /**
     * Get disability list for entity.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadDisabilityList(ActionMapping mapping, ActionForm form,
                                            HttpServletRequest request, HttpServletResponse response) throws Exception {

        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadDisabilityList", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadDisabilityList";
        try {
            // Secures page
            securePage(request, form);

            Record inputRecord = getInputRecord(request);

            String entityId =request.getParameter(PK_PROPERTY);

            request.setAttribute(ENTITY_FK_PROPERTY, entityId);
            setEntityCommonInfoToRequest(request, inputRecord);

            if (!FormatUtils.isLong(entityId)) {
                throw new IllegalArgumentException(new StringBuffer().append(
                        "entity FK [").append(entityId)
                        .append("] should be a number.")
                        .toString());
            }

            String entityType = inputRecord.getStringValue(ENTITY_TYPE_PROPERTY);
            if (entityType.charAt(0) == ENTITY_TYPE_ORG_CHAR) {
                checkCisFolderMenu(request);
            }

            RecordSet rs = (RecordSet) request.getAttribute("gridRecordSet");
            if (rs==null) {
                // pass through the parameters into db package to load the Disability list
                inputRecord.setFieldValue("entityId",entityId);
                inputRecord.setFieldValue("categoryCode",DisabilityFields.getFltCategory(inputRecord));
                inputRecord.setFieldValue("effectiveFromDate",DisabilityFields.getEffectiveFromDate(inputRecord));
                inputRecord.setFieldValue("effectiveToDate",DisabilityFields.getEffectiveToDate(inputRecord));
                rs = getDisabilityManager().loadDisabilityList(inputRecord);
            }

            setCisHeaderFields(request);
            setDataBean(request, rs);
            loadGridHeader(request);

            // Make the Summary Record available for output
            Record output = rs.getSummaryRecord();
            // Publish the input record
            publishOutputRecord(request, output);

            loadListOfValues(request, form);

            /* Gets links for Paging */
            new CILinkGenerator().generateLink(request, entityId, this.getClass().getName());
            saveToken(request);
            addJsMessages();
        }
        catch (Exception e) {
            forwardString = handleError(
                    AppException.UNEXPECTED_ERROR, "Failed to load Disability page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadDisabilityList", af);
        }
        return af;
    }

    /**
     * save Disability Data.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward saveDisabilityData(ActionMapping mapping, ActionForm form,
                                           HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveDisabilityData", new Object[]{mapping, form, request, response});
        }

        String forwardString = "saveDisabilityData";
        RecordSet inputRs = null;

        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                securePage(request, form);

                inputRs = getInputRecordSet(request);
                inputRs.setSummaryRecord(getInputRecord(request));

                getDisabilityManager().saveDisabilityData(inputRs); 
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
        l.exiting(getClass().getName(), "saveDisabilityData", af);
        return af;

    }

    //add js message
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("ci.common.error.rowSelect.delete");
        MessageManager.getInstance().addJsMessage("js.delete.confirmation");
        MessageManager.getInstance().addJsMessage("ci.common.error.newRecords.delete");
        MessageManager.getInstance().addJsMessage("ci.common.error.existRecords.delete");
        MessageManager.getInstance().addJsMessage("ci.common.error.value.number");
        MessageManager.getInstance().addJsMessage("ci.common.error.element.required");
        MessageManager.getInstance().addJsMessage("ci.entity.message.date.enter");
        MessageManager.getInstance().addJsMessage("ci.common.error.element.before");
        MessageManager.getInstance().addJsMessage("js.refresh.lose.changes.confirmation");
        MessageManager.getInstance().addJsMessage("cs.entity.message.filterCriteria.StartDateAfterEndDate.error");

    }

    public void verifyConfig() {
        if (getDisabilityManager() == null) {
            throw new ConfigurationException("The required property 'disabilityManager' is missing.");
        }
    }

    public DisabilityManager getDisabilityManager() {
        return m_disabilityManager;
    }

    public void setDisabilityManager(DisabilityManager m_disabilityManager) {
        this.m_disabilityManager = m_disabilityManager;
    }

    private DisabilityManager m_disabilityManager;

}
