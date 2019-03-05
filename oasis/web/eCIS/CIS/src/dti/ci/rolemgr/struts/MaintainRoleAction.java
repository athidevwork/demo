package dti.ci.rolemgr.struts;

import dti.ci.core.struts.MaintainEntityFolderBaseAction;
import dti.ci.rolemgr.RoleManager;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * [Description here]
 * <p/>
 * <p>(C) 2018 Delphi Technology, inc. (dti)</p>
 *
 * @author Herb Koenig
 *         Date:   Apr 12, 2004
 *         <p/>
 *         Revision Date    Revised By  Description
 *         ------------------------------------------------------------------
 *         05/30/2018       ylu         Issue 109175: refactor update
 *         06/30/2018       ylu         Issue 194117: update for CSRF securiy.
 *         ------------------------------------------------------------------
 */

public class MaintainRoleAction extends MaintainEntityFolderBaseAction {

    private final Logger l = LogUtils.getLogger(getClass());

    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadRoleList(mapping, form, request, response);
    }

    /**
     * Get role list for entity.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadRoleList(ActionMapping mapping, ActionForm form,
                                          HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadRoleList", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadRoleList";
        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);

            RecordSet rs = (RecordSet) request.getAttribute("gridRecordSet");
            if (rs == null) {
                // load role list grid list
                rs = getRoleManager().loadRoleList(inputRecord);
            }

            setDataBean(request, rs);

            publishOutputRecord(request, inputRecord);

            loadGridHeader(request);

            loadListOfValues(request,form);

            addJsMessages();

            saveToken(request);
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load Entity Role page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadRoleList", af);
        }
        return af;
    }

    //add js message
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("ci.common.error.certifiedDate.after");
        MessageManager.getInstance().addJsMessage("ci.entity.message.coiHolder.select");
        MessageManager.getInstance().addJsMessage("ci.entity.message.selection.invalid");
        MessageManager.getInstance().addJsMessage("ci.entity.message.coiHolder.oneSelect");
        MessageManager.getInstance().addJsMessage("ci.entity.message.source.noAvailable");
        MessageManager.getInstance().addJsMessage("ci.entity.message.coiHolder.Pendingselect");
        MessageManager.getInstance().addJsMessage("ci.claim.restrict.message.noAuthority.claim");
        MessageManager.getInstance().addJsMessage("ci.claim.restrict.message.noAuthority.case");
    }

    public void verifyConfig() {
        if (getRoleManager() == null) {
            throw new ConfigurationException("The required property 'roleManager' is missing.");
        }
    }

    public RoleManager getRoleManager() {
        return m_roleManager;
    }

    public void setRoleManager(RoleManager roleManager) {
        this.m_roleManager = roleManager;
    }

    private RoleManager m_roleManager;

}
