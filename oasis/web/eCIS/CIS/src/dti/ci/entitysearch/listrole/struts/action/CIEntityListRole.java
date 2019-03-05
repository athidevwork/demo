package dti.ci.entitysearch.listrole.struts.action;

import dti.ci.entitysearch.EntitySearchFields;
import dti.ci.entitysearch.listrole.bo.EntityListRoleManager;
import dti.ci.claimsmgr.impl.ClaimsManagerImpl;
import dti.ci.struts.action.CIBaseAction;
import dti.cs.securitymgr.ClaimSecurityManager;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.http.RequestIds;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.navigationmgr.NavigationManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.ActionHelper;
import dti.oasis.struts.IOasisAction;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.PageBean;
import dti.oasis.util.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for Entity List Role page
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 18, 2008
 *
 * @author ldong
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/02/2009       Leo         Issue 95512
 * 10/16/2009       Jacky       Add 'Jurisdiction' logic for issue #97673
 * 10/29/2009       Fred        Re-factor code based on review
 * 02/18/2011       kshen       Issue 114422. 1) Added method getGotoSourceUrl.
 *                              2) Added js messages for this issue.
 * 02/12/2014       hxk         Issue 151104
 *                              1)  Add ci.entity.message.source.noAvailable to js messages.
 * 08/18/2016       ylu         Issue 178205: handle with velocity data
 * 01/12/2017       Elvin       Issue 182136: Velocity Integration
 * ---------------------------------------------------
*/

public class CIEntityListRole extends CIBaseAction {

    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute(RequestIds.PROCESS, "loadEntityListRole");
        return loadEntityListRole(mapping, form, request, response);
    }

    /**
     * Method to load list of Entity Roles
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadEntityListRole(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                 HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadEntityListRole",
                new Object[]{mapping, form, request, response});

        String forwardString = "loadEntityListRole";
        RecordSet rs = null;

        try {
            securePage(request, form);
            String entityPk = request.getParameter("pk");
            // accountNo holder filter
            String entityPKFieldName = request.getParameter(EntitySearchFields.ENT_PK_FLD_NAME_PROPERTY);
            /* validate */
            if (!FormatUtils.isLong(entityPk)) {
                throw new IllegalArgumentException(new StringBuffer().append(
                        "entity PK [").append(entityPk)
                        .append("] should be a number.")
                        .toString());
            }

            rs = (null == entityPKFieldName ? getEntityListRoleManager().loadEntityListRoleByEntity(entityPk)
                                                            : getEntityListRoleManager().loadEntityListRoleByEntity(entityPk, entityPKFieldName) );

            // load entity role of velocity policy
            RecordSet velocityRecords = getEntityListRoleManager().getVelocityPolicyData(entityPk, rs.getFieldNameList());
            if (velocityRecords != null && velocityRecords.getSize() > 0) {
                rs.addRecords(velocityRecords);
            }

            // issue 97673---------------------------------------------------------------------------------
            // entity search list filter for issue 97673
            if (getClaimSecurityManager().isFilterConfigured()) {
                for (int i = 0; i < rs.getSize(); i++) {
                    Record rd = rs.getRecord(i);
                    String claimNo = rd.getStringValue("externalId"); // claim no
                    rd.setFieldValue("sourceTableName", "CLAIM");
                    rd.setFieldValue("sourceNo", claimNo);
                }
                rs = getClaimSecurityManager().filterRecordSetViaJurisdiction(rs);
            }

            if(rs.getSize() == 0) {
                asynBrowser(response);

                return null;
            }
            else {
                request.setAttribute("entityName", rs.getRecord(0).getFieldValue("entityName"));
            }

            /* Sets data Bean */
            setDataBean(request, rs);

            /* Load LOV */
            loadListOfValues(request, form);

            /* Load grid header bean */
            loadGridHeader(request);

            String eventName = request.getParameter("eventName");
            if (StringUtils.isBlank(eventName) || (!eventName.equals("handleOnFindAccount()") && !eventName.equals("handleOnFindClient()"))) {
                String userId = ActionHelper.getCurrentUserId(request);
                NavigationManager.getInstance().removeActionItem(request, userId, (PageBean) request.getAttribute(IOasisAction.KEY_PAGEBEAN), "CI_LISTROLE_PAGE_AIG", "CI_LISTROLE_PAGE_SELECT");
            }
            String fromDocProcess = request.getParameter("fromDocProcess");
            request.setAttribute("fromDocProcess", fromDocProcess);
            loadJsMessage();
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the load Entity List Role page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadEntityListRole", af);
        return af;
    }

    /**
     * Get Goto Type Info
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward getGotoSourceUrl(ActionMapping mapping,
                                         ActionForm form,
                                         HttpServletRequest request,
                                         HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getGotoSourceUrl", new Object[]{mapping, form, request, response});
        }
        try {
            securePage(request, form);
            Record inputRecord = getInputRecord(request);
            // Get the values
            String gotoSourceUrl = getEntityListRoleManager().getGotoSourceUrl(inputRecord);
            Record recResult = new Record();
            recResult.setFieldValue("gotoSourceUrl", gotoSourceUrl);
            recResult.setFields(inputRecord);
            writeAjaxXmlResponse(response, recResult);
        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get goto source url.", e, response);
        }
        ActionForward af = null;
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getGotoSourceUrl", af);
        }
        return af;
    }


    public CIEntityListRole() {
    }

    /* Configuration constructor and accessor methods */
    public void verifyConfig() {
        if (getEntityListRoleManager() == null)
            throw new ConfigurationException("The required property 'entityListRoleManager' is missing.");
    }

    public EntityListRoleManager getEntityListRoleManager() {
        return entityListRoleManager;
    }

    public void setEntityListRoleManager(EntityListRoleManager entityListRoleMng) {
        this.entityListRoleManager = entityListRoleMng;
    }

    /**
     * Write asynchronized information back to browser.
     *
     * @param response         HttpServletResponse
     */
    private void asynBrowser(HttpServletResponse response) throws Exception {
        StringBuffer buff = new StringBuffer();
        PrintWriter wri = response.getWriter();
        // since information is simple, do not have to build a xml data structure
        response.setContentType("text/html");
        // response.setContentType("text/xml;charset=utf-8");
        buff.append("no role");
        wri.write(buff.toString());
        wri.flush();

    }

    private void loadJsMessage(){
        MessageManager.getInstance().addJsMessage("js.select.row");

        // add js messages for CICommon.js
        MessageManager.getInstance().addJsMessage("js.lose.changes.confirmation");
        MessageManager.getInstance().addJsMessage("js.refresh.lose.changes.confirmation");
        MessageManager.getInstance().addJsMessage("ci.entity.message.entityType.unknown");
        MessageManager.getInstance().addJsMessage("ci.entity.message.module.unknown");
        MessageManager.getInstance().addJsMessage("ci.common.error.format.ssn");
        MessageManager.getInstance().addJsMessage("ci.common.error.format.email");

        MessageManager.getInstance().addJsMessage("ci.common.error.foreignAddress.invalid");
        MessageManager.getInstance().addJsMessage("ci.common.error.foreignAddress.required");
        MessageManager.getInstance().addJsMessage("ci.common.error.classDescription.after");

        MessageManager.getInstance().addJsMessage("ci.entity.message.value.verified");
        MessageManager.getInstance().addJsMessage("ci.entity.message.verified.beforeMaking");
        MessageManager.getInstance().addJsMessage("ci.entityRoleList.noGotoSourceUrlConfigured");
        MessageManager.getInstance().addJsMessage("ci.entityRoleList.improperlyGotoSourceUrl");
        MessageManager.getInstance().addJsMessage("ci.entity.message.source.noAvailable");
    }

    public ClaimSecurityManager getClaimSecurityManager() {
        return claimSecurityManager;
    }

    public void setClaimSecurityManager(ClaimSecurityManager claimSecurityManager) {
        this.claimSecurityManager = claimSecurityManager;
    }

    private EntityListRoleManager entityListRoleManager;
    private ClaimSecurityManager claimSecurityManager;
}