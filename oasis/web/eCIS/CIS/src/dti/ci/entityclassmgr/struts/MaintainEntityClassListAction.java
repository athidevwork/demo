package dti.ci.entityclassmgr.struts;

import dti.ci.core.struts.MaintainEntityFolderBaseAction;
import dti.ci.entityclassmgr.EntityClassFields;
import dti.ci.entityclassmgr.EntityClassManager;
import dti.ci.helpers.ICIConstants;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ExpectedException;
import dti.oasis.error.UnexpectedDBException;
import dti.oasis.error.ValidationException;
import dti.oasis.http.RequestIds;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.ActionHelper;
import dti.oasis.tags.OasisFields;
import dti.oasis.util.LogUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Entity Class List Action Class.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author Gerald C. Carney
 * Date:   Mar 30, 2004
 * <p/>
 * Revision Date    Revised By  Description
 * -------------------------------------------------------------------
 * 04/04/2005       HXY         Extends CIBaseAction.
 * 04/11/2005       HXY         Used OasisFields to set up grid header.
 * 12/17/2006       Larry       Added the class code by the class code
 * 01/17/2007       Fred        Added call to CILinkGenerator.generateLink()
 * 01/20/2011       Michael Li  Issue:116335
 * 08/23/2013       kshen       Issue 142975.
 * 12/08/2017       ylu         Issue 190017
 * 06/29/2018       ylu         Issue 194117: update for CSRF.
 * 09/17/2018       ylu         Issue 195835: remove excess messageKey as commonDeleteRow is used.
 * -------------------------------------------------------------------
 */

public class MaintainEntityClassListAction extends MaintainEntityFolderBaseAction {
    private final Logger l = LogUtils.getLogger(getClass());

    @Override
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadAllEntityClass(mapping, form, request, response);
    }

    /**
     * Load all entity class.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllEntityClass(ActionMapping mapping, ActionForm form,
                                            HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllEntityClass", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadAllEntityClassListResult";

        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);

            RecordSet rs = (RecordSet) request.getAttribute(RequestIds.DATA_BEAN);
            if (rs == null) {
                rs = getEntityClassManager().loadAllEntityClass(inputRecord);
            }

            request.setAttribute(ICIConstants.LIST_DISPLAYED_PROPERTY, YesNoFlag.getInstance(rs.getSize() > 0).getName());

            handleFieldsForOrg(request);

            setDataBean(request, rs);

            publishOutputRecord(request, inputRecord);

            loadGridHeader(request);

            loadListOfValues(request, form);

            addJsMessages();

            saveToken(request);
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load Entity Search page.", e, request, mapping);
            l.throwing(getClass().getName(), "loadAllEntityClass", e);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllEntityClass", af);
        }
        return af;
    }

    public ActionForward saveAllEntityClass(ActionMapping mapping, ActionForm form,
                                            HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "deleteEntityClasses", new Object[]{mapping, form, request, response});
        }

        String forwardString = "saveAllEntityClassListResult";
        RecordSet rs = null;

        try {
            if (isTokenValid(request, true)) {
                securePage(request, form, false);

                rs = getInputRecordSet(request);

                getEntityClassManager().deleteEntityClasses(rs);
            }
        } catch (ExpectedException ve) {
            request.setAttribute(RequestIds.DATA_BEAN, rs);
            handleExpectedException(ve, request);

        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Unable to save entity class.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "deleteEntityClasses", af);
        }
        return af;
    }

    protected void handleFieldsForOrg(HttpServletRequest request) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "handleFieldsForOrg", new Object[]{request});
        }

        Record inputRecord = getInputRecord(request);

        String entityType = inputRecord.getStringValue(ENTITY_TYPE_PROPERTY);

        if (entityType.charAt(0) != ENTITY_TYPE_ORG_CHAR) {
            OasisFields fields = ActionHelper.getFields(request);
            if (fields.hasField(EntityClassFields.NETWORK_DISCOUNT_COLUMN_NAME)) {
                fields.getField(EntityClassFields.NETWORK_DISCOUNT_COLUMN_NAME).setIsVisible(false);
            }
        }

        l.exiting(getClass().getName(), "handleFieldsForOrg");
    }

    //add js message
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("ci.entity.message.entityNotes.notAvailable");
        MessageManager.getInstance().addJsMessage("ci.entity.message.classification.delete");
        MessageManager.getInstance().addJsMessage("cs.records.delete.confirm");
    }

    public void verifyConfig() {
        super.verifyConfig();

        if (getEntityClassManager() == null)
            throw new ConfigurationException("The required property 'entityClassManager' is missing.");
    }

    public EntityClassManager getEntityClassManager() {
        return m_entityClassManager;
    }

    public void setEntityClassManager(EntityClassManager entityClassManager) {
        m_entityClassManager = entityClassManager;
    }

    private EntityClassManager m_entityClassManager;
}
