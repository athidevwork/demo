package dti.ci.mergehistory.struts;

import dti.ci.mergehistory.EntityMergeHistoryManager;
import dti.ci.struts.action.CIBaseAction;
import dti.cs.securitymgr.AccessControlFilterManager;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.http.RequestIds;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.ActionHelper;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Action Class for Merge History
 *
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:  10/09/2015
 *
 * @author
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/09/2015       ylu         Issue 164517
 * 06/28/2018       ylu         Issue 194117: update for CSRF security.
 * ---------------------------------------------------
*/

public class CIEntityMergeHistoryAction extends CIBaseAction{

    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadMergeHistory(mapping, form, request, response);
    }

    /**
     * Load the entity merge history record
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadMergeHistory(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                               HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadMergeHistory",
                new Object[]{mapping, form, request, response});

        String forwardString = "loadHistory";

        try {
            securePage(request, form);
            Record inputRecord = new Record();
            RecordSet recordSet = getEntityMergeHistoryManager().loadMergeHistory(inputRecord);
            recordSet = getAccessControlFilterManager().filterRecordSetViaAccessControl(request, recordSet, "", "entityIdFrom");
            recordSet = getAccessControlFilterManager().filterRecordSetViaAccessControl(request, recordSet, "", "entityIdTo");
            setDataBean(request, recordSet);
            loadGridHeader(request);
            saveToken(request);
            //Set display message
            if (recordSet == null || recordSet.getSize() == 0) {
                MessageManager.getInstance().
                        addInfoMessage("ci.maintainClientDup.unmerge.noEntryFound");
            } else {
                MessageManager.getInstance().
                        addInfoMessage("ci.maintainClientDup.unmerge.selectEntryFromList");
            }
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load Entity Merge History page.", e, request, mapping);
        } finally {
        l.exiting(this.getClass().getName(), "loadMergeHistory");
        }
        loadJsMessage();
        return mapping.findForward(forwardString);
    }

    /**
     * Un-merge the history records
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward unMergeProcess(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                   HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "unMergeProcess",
                new Object[]{mapping, form, request, response});

        String forwardString = "success";
        try {
            if (hasValidSaveToken(request)) {
                securePage(request, form);
                Record inputRecord  = getInputRecord(request);
                String rsltMsg = getEntityMergeHistoryManager().unMergeProcess(inputRecord);
                if (StringUtils.isBlank(rsltMsg)) {
                    MessageManager.getInstance().addInfoMessage("ci.maintainClientDup.unmerge.success");
                } else {
                    MessageManager.getInstance().addErrorMessage("ci.maintainClientDup.save.error", new Object[]{rsltMsg});
                }
            }
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to un-merge the Entity.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "unMergeProcess", af);
        return af;
    }

    /**
     * Add Js messages
     */
    private void loadJsMessage() {
        MessageManager.getInstance().addJsMessage("ci.maintainClientDup.unmerge.warning");
        MessageManager.getInstance().addJsMessage("ci.maintainClientDup.unmerge.success");
        MessageManager.getInstance().addJsMessage("ci.maintainClientDup.save.error");
        MessageManager.getInstance().addJsMessage("ci.common.error.onlyOneRow.noSelect");
    }

    @Override
    public void verifyConfig() {
        if (getEntityMergeHistoryManager() == null) {
            throw new ConfigurationException("The required property 'EntityMergeHistoryManager' is missing.");
        }
    }

    public AccessControlFilterManager getAccessControlFilterManager() {
        return accessControlFilterManager;
    }

    public void setAccessControlFilterManager(AccessControlFilterManager accessControlFilterManager) {
        this.accessControlFilterManager = accessControlFilterManager;
    }
    private AccessControlFilterManager accessControlFilterManager;

    public EntityMergeHistoryManager getEntityMergeHistoryManager() {
        return m_entityMergeHistoryManager;
    }

    public void setEntityMergeHistoryManager(EntityMergeHistoryManager entityMergeHistoryManager) {
        this.m_entityMergeHistoryManager = entityMergeHistoryManager;
    }

    private EntityMergeHistoryManager m_entityMergeHistoryManager;
}
