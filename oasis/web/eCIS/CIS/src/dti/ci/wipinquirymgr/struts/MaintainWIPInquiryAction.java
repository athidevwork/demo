package dti.ci.wipinquirymgr.struts;

import dti.ci.core.struts.MaintainEntityFolderBaseAction;
import dti.ci.entitymgr.EntityFields;
import dti.ci.entitymgr.EntityManager;
import dti.ci.helpers.ICIConstants;
import dti.ci.wipinquirymgr.WIPInquiryFields;
import dti.ci.wipinquirymgr.WIPInquiryManager;
import dti.cs.securitymgr.AccessControlFilterManager;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.ActionHelper;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action Class CIS WIP Inquiry Page.
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 12, 2005
 *
 * @author Hong Yuan
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/17/2007       Fred        Added call to CILinkGenerator.generateLink()
 * 10/06/2010       wfu         111776: Replaced hardcode string with resource definition
 * 03/06/2012       Parker      130270. set CIS notes visiable for this business.
 * 02/13/2015       bzhu        Issue 160886. Add access control filter.
 * 04/17/2018       dpang       Issue 192648. Refactor WIP Inquiry.
 * ---------------------------------------------------
*/

public class MaintainWIPInquiryAction extends MaintainEntityFolderBaseAction {

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
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadWIPInquiryList(mapping, form, request, response);
    }

    /**
     * Load Wip Inquiry list
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadWIPInquiryList(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String methodName = "loadWIPInquiryList";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{mapping, form, request, response});
        }

        String actionForward = ICIConstants.SUCCESS;
        try {
            securePage(request, form);
            Record inputRecord = getInputRecord(request);

            String entityId = WIPInquiryFields.getClientEntityFK(inputRecord);
            if (StringUtils.isBlank(entityId)) {
                entityId = inputRecord.getStringValue(ICIConstants.PK_PROPERTY);
                WIPInquiryFields.setClientEntityFK(inputRecord, entityId);

                Record record = new Record();
                EntityFields.setEntityId(record, entityId);
                String clientEntityName = getEntityManager().getEntityName(record);
                WIPInquiryFields.setSearchCriteriaClientEntityName(inputRecord, clientEntityName);
            }

            RecordSet recordSet = getWipInquiryManager().loadWIPInquiry(entityId);
            recordSet = getAccessControlFilterManager().filterRecordSetViaAccessControl(request, recordSet, "", WIPInquiryFields.SOURCE_NO);

            request.setAttribute(WIPInquiryFields.RESTRICT_SOURCE_LIST, getAccessControlFilterManager().getRestrictSourceList(ActionHelper.getCurrentUserId(request)));

            publishOutputRecord(request, inputRecord);

            setDataBean(request, recordSet);

            loadListOfValues(request, form);

            loadGridHeader(request);

            addJsMessages();
        } catch (AppException ae) {
            l.throwing(this.getClass().getName(), methodName, ae);
        } catch (Exception e) {
            actionForward = handleError(AppException.UNEXPECTED_ERROR, "Failed to load WIP Inquiry List.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(actionForward);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, af);
        }
        return af;
    }

    //add js message
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("ci.common.error.date.valid");
        MessageManager.getInstance().addJsMessage("ci.entity.message.sourceRecord.invalid");
        MessageManager.getInstance().addJsMessage("ci.entity.message.auditHistory.forRecord");
        MessageManager.getInstance().addJsMessage("ci.entity.message.notesError.notAvailable");
        MessageManager.getInstance().addJsMessage("ci.common.error.pk.invalid");
        MessageManager.getInstance().addJsMessage("ci.claim.restrict.message.noAuthority.claim");
    }

    @Override
    public void verifyConfig() {
        if (getAccessControlFilterManager() == null) {
            throw new ConfigurationException("The required property 'accessControlFilterManager' is missing.");
        }

        if (getWipInquiryManager() == null) {
            throw new ConfigurationException("The required property 'wipInquiryManager' is missing.");
        }
    }

    public AccessControlFilterManager getAccessControlFilterManager() {
        return m_accessControlFilterManager;
    }

    public void setAccessControlFilterManager(AccessControlFilterManager accessControlFilterManager) {
        this.m_accessControlFilterManager = accessControlFilterManager;
    }

    public WIPInquiryManager getWipInquiryManager() {
        return m_wipInquiryManager;
    }

    public void setWipInquiryManager(WIPInquiryManager wipInquiryManager) {
        this.m_wipInquiryManager = wipInquiryManager;
    }

    public EntityManager getEntityManager() {
        return m_entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.m_entityManager = entityManager;
    }

    private AccessControlFilterManager m_accessControlFilterManager;

    private WIPInquiryManager m_wipInquiryManager;
    private EntityManager m_entityManager;
}
