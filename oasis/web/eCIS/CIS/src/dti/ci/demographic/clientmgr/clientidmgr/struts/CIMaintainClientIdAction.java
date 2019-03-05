package dti.ci.demographic.clientmgr.clientidmgr.struts;

import dti.ci.struts.action.CIBaseAction;
import dti.ci.demographic.clientmgr.clientidmgr.ClientIdManager;
import dti.ci.helpers.ICIConstants;
import dti.oasis.app.ConfigurationException;
import dti.oasis.app.AppException;
import dti.oasis.http.RequestIds;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.struts.ActionHelper;
import dti.oasis.error.ValidationException;
import dti.oasis.util.StringUtils;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>(C) 2007 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 12, 2008
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/06/2010       wfu         111776: Replaced hardcode string with resource definition
 * 09/22/2014       wkong       Issue 157106: When validate error, load grid data from request.
 * ---------------------------------------------------
 */
public class CIMaintainClientIdAction extends CIBaseAction {
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
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadAllClientIds(mapping, form, request, response);
    }

    /**
     * Method to load list of underwriters for requested policy.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllClientIds(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                          HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllClientIds",
                new Object[]{mapping, form, request, response});

        String forwardString = "loadClientIdResult";

        try {
            securePage(request, form);

            String entityPk = getInputRecord(request).getStringValue(ICIConstants.PK_PROPERTY, "");

            /* validate */
            if (!FormatUtils.isLong(entityPk)) {
                throw new AppException("ci.cicore.invalidError.EntityfkNotExists",
                        new StringBuffer().append(
                                "entity FK [").append(entityPk)
                                .append("] should be a number.")
                                .toString(),
                        new Object[]{entityPk});
            }

            request.setAttribute(ICIConstants.PK_PROPERTY, entityPk);

            RecordSet rs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            if (rs == null) {
                /* Loads all underwriter Data */
                rs = getClientIdManager().loadAllClientIds(new Long(entityPk));
            }

            /* Sets data Bean */
            setDataBean(request, rs);

            // Make the Summary Record available for output
            Record output = rs.getSummaryRecord();

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);

            /* Load grid header bean */
            loadGridHeader(request);

            /* Load LOV */
            loadListOfValues(request, form);

            saveToken(request);

            // set js messages
            addJsMessages();
            
            /* publish other fields */
            request.setAttribute("showingAll", "Y");
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR,
                    "Failed to load the loadAllClientIds page.",
                    e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllClientIds", af);
        return af;
    }

    //add js message
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("js.delete.confirmation");
        MessageManager.getInstance().addJsMessage("ci.common.error.rowSelect.delete");
        MessageManager.getInstance().addJsMessage("js.refresh.lose.changes.confirmation");
        MessageManager.getInstance().addJsMessage("ci.entity.message.row.wrongMessage");
        MessageManager.getInstance().addJsMessage("ci.maintainClientId.clientIdType.required");
        MessageManager.getInstance().addJsMessage("ci.maintainClientId.effectiveFromDate.required");
        MessageManager.getInstance().addJsMessage("ci.maintainClientId.invalidEffectiveFromDate.error");
        MessageManager.getInstance().addJsMessage("ci.maintainClientId.externalId.required");
        MessageManager.getInstance().addJsMessage("ci.maintainClientId.invalidEffectiveToDate.error");
        MessageManager.getInstance().addJsMessage("ci.maintainClientId.EndDateBeforeStartDate.error");
    }



    /**
     * Save all underwriters
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveAllClientIds(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                          HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "saveAllClientIds", new Object[]{mapping, form, request, response});

        String forwardString = "saveClientIdResult";
        RecordSet inputRecords = null;
        try {
            String entityPk = "";
            if (!StringUtils.isBlank(request.getParameter(ICIConstants.PK_PROPERTY))) {
                entityPk = request.getParameter(ICIConstants.PK_PROPERTY);
            }

            /* validate */
            if (!FormatUtils.isLong(entityPk)) {
                throw new IllegalArgumentException(new StringBuffer().append(
                        "entity FK [").append(entityPk)
                        .append("] should be a number.")
                        .toString());
            }

            request.setAttribute(ICIConstants.PK_PROPERTY, entityPk);

            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                /* Secure page */
                ActionHelper.securePage(request, getClass().getName());
                
                /* Generate input records */
                inputRecords = getInputRecordSet(request);

                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "saveAllClientIds",
                            "Saving the ClientIds inputRecords: " + inputRecords);
                }

                /* Call the business component to implement the validate/save logic */
                getClientIdManager().saveAllClientIds(new Long(entityPk), inputRecords);
            }
        } catch (ValidationException v) {
            // Save the recordset into the request
            request.setAttribute(RequestIds.GRID_RECORD_SET, inputRecords);

            // Handle the validation exception
            handleValidationException(v, request);
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the Underwriter page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "save", af);
        return af;
    }

    /* Configuration constructor and accessor methods */
    public void verifyConfig() {
        if (getClientIdManager() == null)
            throw new ConfigurationException("The required property 'clientIdManager' is missing.");
    }

    public CIMaintainClientIdAction() {
        super();
    }

    public ClientIdManager getClientIdManager() {
        return clientIdManager;
    }

    public void setClientIdManager(ClientIdManager clientIdManager) {
        this.clientIdManager = clientIdManager;
    }

    private ClientIdManager clientIdManager;
}
