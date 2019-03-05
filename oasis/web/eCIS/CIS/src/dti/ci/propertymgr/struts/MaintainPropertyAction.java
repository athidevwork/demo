package dti.ci.propertymgr.struts;

import dti.ci.helpers.CILinkGenerator;
import dti.ci.propertymgr.PropertyManager;
import dti.ci.struts.action.CIBaseAction;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.messagemgr.MessageManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Action Class for Property
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 28, 2006
 *
 * @author bhong
 */

/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/17/2007       Fred        Added call to CILinkGenerator.generateLink()
 * 05/13/2009       kshen       Added code to handle db error.
 * 10/06/2010       wfu         111776: Replaced hardcode string with resource definition
 * 03/06/2012       Parker      130270. set CIS notes visiable for this business.
 * 12/18/2015       ylu         166999: store passed entity Id in request for page reload
 * ---------------------------------------------------
*/
public class MaintainPropertyAction extends CIBaseAction {
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
        return loadAllProperty(mapping, form, request, response);
    }

    public ActionForward loadAllProperty(ActionMapping mapping, ActionForm form,
                                         HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllProperty", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadAllPropertyResult";

        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);

            String entityFK = inputRecord.getStringValue(PK_PROPERTY, "");
            /* validate */
            if (!FormatUtils.isLong(entityFK)) {
                throw new IllegalArgumentException(new StringBuffer().append(
                        "entity FK [").append(entityFK)
                        .append("] should be a number.")
                        .toString());
            }

            String entityType = inputRecord.getStringValue(ENTITY_TYPE_PROPERTY, "");
            /* set menu beans Search & Select an entity type of  'organization'.*/
            if (entityType.charAt(0) == ENTITY_TYPE_ORG_CHAR) {
                checkCisFolderMenu(request);
            }

            // Load vehicle.
            RecordSet rs = (RecordSet) request.getAttribute("gridRecordSet");
            if (rs == null) {
                rs = getPropertyManager().loadAllProperty(inputRecord);
            }
            setDataBean(request, rs);
            loadGridHeader(request);

            setEntityCommonInfoToRequest(request, inputRecord);

            Record outputRecord = new Record();
            outputRecord.setFields(inputRecord);
            outputRecord.setFields(rs.getSummaryRecord());

            publishOutputRecord(request, outputRecord);
            /* get LOV */
            loadListOfValues(request, form);

            /* Gets links for Paging */
            new CILinkGenerator().generateLink(request, entityFK, this.getClass().getName());

            // set js messages
            addJsMessages();
            setCisHeaderFields(request);

            saveToken(request);

        } catch (Exception e) {
            forwardString = handleError(
                    AppException.UNEXPECTED_ERROR, "Failed to load property page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllProperty", af);
        }
        return af;
    }

    public ActionForward saveAllProperty(ActionMapping mapping, ActionForm form,
                                         HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllProperty", new Object[]{mapping, form, request, response});
        }

        String forwardString = "saveAllPropertyResult";
        RecordSet inputRs = null;

        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                securePage(request, form);

                inputRs = getInputRecordSet(request);
                inputRs.setSummaryRecord(getInputRecord(request));

                getPropertyManager().saveAllProperty(inputRs);
            }
        } catch (ValidationException v) {
            // Save the recordset into the request
            request.setAttribute("gridRecordSet", inputRs);

            // Handle the validation exception
            handleValidationException(v, request);
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to save all property.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllProperty", af);
        }
        return af;
    }

    public ActionForward lookupProperty(ActionMapping mapping, ActionForm form,
                                         HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllProperty", new Object[]{mapping, form, request, });
        }

        String forwardString = "lookupPropertyResult";

        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);

            String entityFK = inputRecord.getStringValue(PK_PROPERTY, "");
            /* validate */
            if (!FormatUtils.isLong(entityFK)) {
                throw new IllegalArgumentException(new StringBuffer().append(
                        "entity FK [").append(entityFK)
                        .append("] should be a number.")
                        .toString());
            } else {
                request.setAttribute("entityFK", entityFK);
            }

            // Load vehicle.
            RecordSet rs = getPropertyManager().loadAllProperty(inputRecord);

            setDataBean(request, rs);
            loadGridHeader(request);

            Record outputRecord = new Record();
            outputRecord.setFields(inputRecord);
            outputRecord.setFields(rs.getSummaryRecord());

            publishOutputRecord(request, outputRecord);
            /* get LOV */
            loadListOfValues(request, form);

            // set js messages
            addJsMessages();

            saveToken(request);
        } catch (Exception e) {
            forwardString = handleErrorPopup(
                    AppException.UNEXPECTED_ERROR, "Failed to load property lookup page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "lookupProperty", af);
        }
        return af;
    }


    //add js message
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("ci.common.error.rowSelect.delete");
        MessageManager.getInstance().addJsMessage("js.delete.confirmation");
        MessageManager.getInstance().addJsMessage("ci.common.error.newRecords.delete");
        MessageManager.getInstance().addJsMessage("ci.common.error.existRecords.delete");
        MessageManager.getInstance().addJsMessage("js.refresh.lose.changes.confirmation");
        MessageManager.getInstance().addJsMessage("ci.common.error.propertyValue.number");
        MessageManager.getInstance().addJsMessage("ci.common.error.value.number");
        MessageManager.getInstance().addJsMessage("ci.common.error.value.between");
        MessageManager.getInstance().addJsMessage("ci.common.error.value.percent");
        MessageManager.getInstance().addJsMessage("ci.common.error.element.less");
        MessageManager.getInstance().addJsMessage("ci.common.error.element.addUp");
        MessageManager.getInstance().addJsMessage("ci.common.error.element.beforeToday");
        MessageManager.getInstance().addJsMessage("ci.common.error.element.prior");
        MessageManager.getInstance().addJsMessage("ci.entity.message.notesError.notAvailable");
        MessageManager.getInstance().addJsMessage("ci.common.error.onlyOneRow.noSelect");
    }

    public void verifyConfig() {
        if (getPropertyManager() == null) {
            throw new ConfigurationException("The required property 'propertyManager' is missing.");
        }
    }

    public PropertyManager getPropertyManager() {
        return m_propertyManager;
    }

    public void setPropertyManager(PropertyManager propertyManager) {
        m_propertyManager = propertyManager;
    }

    private PropertyManager m_propertyManager;
}
