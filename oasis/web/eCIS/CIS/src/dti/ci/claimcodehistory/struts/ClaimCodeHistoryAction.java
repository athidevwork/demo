package dti.ci.claimcodehistory.struts;

import dti.ci.struts.action.CIBaseAction;
import dti.ci.helpers.ICIConstants;
import dti.ci.claimcodehistory.ClaimCodeHistoryManager;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.util.LogUtils;
import dti.oasis.util.FormatUtils;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.http.RequestIds;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 5, 2009
 *
 * @author msnadar
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/06/2010       wfu         111776: Replaced hardcode string with resource definition;
 * 02/01/2012       kshen       Issue 108498.
 * ---------------------------------------------------
 */
public class  ClaimCodeHistoryAction extends CIBaseAction {
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
        return load(mapping, form, request, response);
    }

    /**
     * Method to load CM Code History
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "load", new Object[]{mapping, form, request, response});
        }

        String forwardString = "load";
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

            String entityName = getInputRecord(request).getStringValue(ENTITY_NAME_PROPERTY);

            Record inputRecord = new Record();
            inputRecord.setFieldValue("srcRecId",getInputRecord(request).getStringValue("srcRecId", ""));
            inputRecord.setFieldValue("isCodeType",getInputRecord(request).getStringValue("isCodeType", ""));
            request.setAttribute("isCodeType", getInputRecord(request).getStringValue("isCodeType", ""));
            Record output = new Record();
            RecordSet claimCodeHistoryRecordSet = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            claimCodeHistoryRecordSet = getClaimCodeHistoryManager().getClaimCodeHistory(inputRecord);
            output.setFields(claimCodeHistoryRecordSet.getSummaryRecord(),false);
            publishOutputRecord(request, output);

            RequestStorageManager.getInstance().set(CURRENT_GRID_ID, CLAIM_CODE_HISTORY_GRID);
            setDataBean(request, claimCodeHistoryRecordSet);
            loadGridHeader(request);

            loadListOfValues(request, form);
            // Save token
            saveToken(request);

            // set js messages
            addJsMessages();

        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR,
                "Failed to display Claim Code History Page.",
                e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "load", af);
        return af;
    }

//add js message
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("js.delete.confirmation");
        MessageManager.getInstance().addJsMessage("ci.common.error.rowSelect.delete");
        MessageManager.getInstance().addJsMessage("js.refresh.lose.changes.confirmation");

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


    public void verifyConfig() {
        if (getClaimCodeHistoryManager() == null) {
            throw new ConfigurationException("The required property 'riskManager' is missing.");
        }
        if (getClaimCodeHistoryGridAnchorColumnName() == null) {
            throw new ConfigurationException("The required property 'claimCodeHistoryGridAnchorColumnName' is missing.");
        }
    }

//    public ClaimCodeHistoryAction() {
//    }

    public ClaimCodeHistoryManager getClaimCodeHistoryManager() {
        return claimCodeHistoryManager;
    }

    public void setClaimCodeHistoryManager(ClaimCodeHistoryManager claimCodeHistoryManager) {
        this.claimCodeHistoryManager = claimCodeHistoryManager;
    }

    private ClaimCodeHistoryManager claimCodeHistoryManager;

    public String getClaimCodeHistoryGridAnchorColumnName() {
        return claimCodeHistoryGridAnchorColumnName;
    }

    public void setClaimCodeHistoryGridAnchorColumnName(String claimCodeHistoryGridAnchorColumnName) {
        this.claimCodeHistoryGridAnchorColumnName = claimCodeHistoryGridAnchorColumnName;
    }

    private String claimCodeHistoryGridAnchorColumnName;
    private static final String CURRENT_GRID_ID = "currentGridId";
    private static final String CLAIM_CODE_HISTORY_GRID = "claimCodeHistoryGrid";
    // Layer Id
    private static final String CLAIM_CODE_HISTORY_GRID_GH = "claimCodeHistoryGrid_GH";
    private static final String ACTIVE_STATUS = "ACTIVE";
    private static final String INACTIVE_STATUS = "INACTIVE";
    private static final String CODE_TYPE = "EXPWITNESS_STATUS";
}
