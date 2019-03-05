package dti.ci.entityquickaddmgr.struts;

import dti.ci.clientmgr.EntityAddInfo;
import dti.ci.entitymgr.EntityFields;
import dti.ci.entityquickaddmgr.EntityQuickAddManager;
import dti.ci.entitysearch.EntitySearchFields;
import dti.ci.helpers.ICIConstants;
import dti.ci.helpers.ICIEntityConstants;
import dti.ci.struts.action.CIBaseAction;
import dti.cs.ziplookupmgr.ZipLookupFields;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/30/2018
 *
 * @author dpang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/11/2018       ylu         Issue 192743: corresponding change for refactor QuickAddPerson/Org page.
 * 09/26/2018       kshen       195835. CIS grid replacement.
 * 11/16/2018       Elvin       Issue 195835: grid replacement
 * ---------------------------------------------------
 */
public class EntityQuickAddBaseAction extends CIBaseAction implements ICIConstants, ICIEntityConstants {

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
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return init(mapping, form, request, response);
    }

    /**
     * Initialize the page.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward init(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "init", new Object[]{mapping, form, request, response});
        }

        String forwardString = "initPage";
        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);

            publishOutputRecord(request, inputRecord);

            loadListOfValues(request, form);

            addJsMessages();

            saveToken(request);
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to initialize the Entity Quick Add Organization page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "init", af);
        }
        return af;
    }

    /**
     * Save All Entity Info.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveAllEntity(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllEntity", new Object[]{mapping, form, request, response});
        }

        String forwardString = "saveResult";
        Record inputRecord = null;
        String okToSkipEntityDups = "N";
        String okToSkipTaxIDDups = "N";
        try {
            securePage(request, form);

            if (hasValidSaveToken(request)) {
                inputRecord = getInputRecord(request);
                EntityAddInfo addInfo = getEntityQuickAddManager().saveAllEntity(inputRecord);

                if (addInfo.isEntityAdded()) {
                    String saveAndClose = inputRecord.getStringValue("saveAndClose");
                    request.setAttribute("saveAndClose", saveAndClose);
                    if ("Y".equalsIgnoreCase(saveAndClose)) {
                        request.setAttribute(PK_PROPERTY, addInfo.getEntityPK());
                        request.setAttribute(EntityFields.ENTITY_TYPE, inputRecord.getStringValue(ENTITY_TYPE_ID));
                        request.setAttribute(PROCESS_PROPERTY, MODIFY_PROCESS_DESC);
                        return mapping.findForward(ICIConstants.MODIFY_PROCESS_DESC);
                    }
                    request.setAttribute("newPk", addInfo.getEntityPK());
                    request.setAttribute("CI_ENTY_CONTINUE_ADD", "Y");
                } else {
                    okToSkipEntityDups = "Y";
                    if (addInfo.isUserCanDupTaxID()) {
                        okToSkipTaxIDDups = "Y";
                    }
                    // end set up for not save due to duplicate
                }
                request.setAttribute("duplicatedEntityExists", addInfo.getMergedDupsInfo().size() > 0);
                request.setAttribute(ICIEntityConstants.OK_TO_SKIP_ENTITY_DUPS_PROPERTY, okToSkipEntityDups);
                request.setAttribute(ICIEntityConstants.OK_TO_SKIP_TAX_ID_DUPS_PROPERTY, okToSkipTaxIDDups);
            }
        } catch (ValidationException ve) {
            // Save the input records into request.
            request.setAttribute("inputRecord", inputRecord);
            // Handle the validation exception
            handleValidationException(ve, request);
        } catch (AppException ae) {
            l.throwing(getClass().getName(), "saveAllEntity", ae);
            if (!MessageManager.getInstance().hasErrorMessages())
                MessageManager.getInstance().addErrorMessage("ci.generic.error",
                        new Object[]{StringUtils.formatDBErrorForHtml(ae.getCause().getMessage())});
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to Save Entity.", e, request, mapping);
        }

        //Remove entity search criteria that may be stored in session. If save entity and go to entity modify page, then return to entity list,
        //the entity list may display inconsistently if exists previous search criteria in session.
        request.getSession(false).removeAttribute(genCriteriaIDForSession(request, EntitySearchFields.SEARCH_CRITERIA_FOR_SESSION_PREFIX));

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllEntity", af);
        }
        return af;
    }

    protected void addJsMessages() {
        MessageManager.getInstance().addJsMessage("cs.changes.lost.confirm");
        MessageManager.getInstance().addJsMessage("ci.entity.message.bothValues.required");
        MessageManager.getInstance().addJsMessage("ci.entity.message.dateValue.after");
        MessageManager.getInstance().addJsMessage("ci.entity.message.dateValue.beforeToday");
        MessageManager.getInstance().addJsMessage("ci.entity.message.dateValue.afterToday");
        MessageManager.getInstance().addJsMessage("ci.entity.class.invalidNetworkDiscount");
        MessageManager.getInstance().addJsMessage("ci.entity.message.zipCode.invalid");
        MessageManager.getInstance().addJsMessage("ci.entity.message.postalCode.invalid");
        MessageManager.getInstance().addJsMessage("ci.common.error.classCode.required");
        MessageManager.getInstance().addJsMessage("ci.entity.duplicates.form.title");
        MessageManager.getInstance().addJsMessage("ci.common.error.licenseDate.before");
        MessageManager.getInstance().addJsMessage("ci.common.error.value.number");
        MessageManager.getInstance().addJsMessage("ci.detail.denominator.date.after");
    }

    @Override
    public void verifyConfig() {
        if (getWorkbenchConfiguration() == null) {
            throw new ConfigurationException("The required property 'workbenchConfiguration' is missing.");
        }
    }

    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return m_workbenchConfiguration;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration) {
        m_workbenchConfiguration = workbenchConfiguration;
    }

    public EntityQuickAddManager getEntityQuickAddManager() {
        return m_entityQuickAddManager;
    }

    public void setEntityQuickAddManager(EntityQuickAddManager entityQuickAddManager) {
        this.m_entityQuickAddManager = entityQuickAddManager;
    }

    private EntityQuickAddManager m_entityQuickAddManager;
    private WorkbenchConfiguration m_workbenchConfiguration;
}
