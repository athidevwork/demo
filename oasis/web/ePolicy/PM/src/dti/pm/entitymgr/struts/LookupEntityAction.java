package dti.pm.entitymgr.struts;

import dti.pm.core.struts.PMBaseAction;
import dti.pm.core.http.RequestIds;
import dti.pm.entitymgr.EntityFields;
import dti.pm.entitymgr.EntityManager;
import dti.oasis.util.LogUtils;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 2, 2007
 *
 * @author Sxm
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/04/2012       wfu         127802 - Add getEntityName to get entity name.
 * 09/09/2013       adeng       144663 - Add JS message.
 * ---------------------------------------------------
 */

public class LookupEntityAction extends PMBaseAction {
    /**
     * This method is triggered automatically when there is no process parameter sent in along the requested url.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute(RequestIds.PROCESS,"loadAllEntity");
        return loadAllEntity(mapping, form, request, response);
    }

    /**
    * This method is called when there the process parameter "loadAllEntity"
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
    public ActionForward loadAllEntity(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllEntity", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            // get input
            Record inputRecord = getInputRecord(request);
            inputRecord.setFieldValue("entityClassCode", inputRecord.getStringValue("entityClassCode").replaceAll(",","','"));
            // load grid content
            RecordSet rs = getEntityManager().loadAllEntity(inputRecord);
            setDataBean(request, rs);

            // load grid header
            loadGridHeader(request);

            //add js messages to messagemanager for the current request
            addJsMessages();
        }
        catch (Exception e) {
            forwardString =  handleErrorPopup(AppException.UNEXPECTED_ERROR,
                "Failed to load entities.", e, request, mapping);
        }

        // done
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllEntity", af);
        return af;
    }

    /**
    * This method is called when the process parameter is "getEntityName"
    * It's only for Ajax calling.
    * <p/>
    *
    * @param mapping
    * @param form
    * @param request
    * @param response
    * @return ActionForward
    * @throws Exception
    */
    public ActionForward getEntityName(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                       HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getEntityName", new Object[]{mapping, form, request, response});
        try {
            // get parameters from request
            Record inputRecord = getInputRecord(request);

            Record output = new Record();
            String entityId = EntityFields.getEntityId(inputRecord);
            if (inputRecord.hasStringValue(EntityFields.ENTITY_ID_FIELD_NAME)) {
                output.setFieldValue(EntityFields.getEntityIdFieldName(inputRecord), entityId);
            }
            if (inputRecord.hasStringValue(EntityFields.ENTITY_NAME_FIELD_NAME)) {
                output.setFieldValue(EntityFields.getEntityNameFieldName(inputRecord),
                        getEntityManager().getEntityName(entityId));
            }

            writeAjaxXmlResponse(response, output);

        }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to load entity name.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), "getEntityName", af);
        return af;
    }

    /**
         * This method is used to get entity role type through pm_get_pm_attr   (for ajax call)
         *
         * @param mapping
         * @param form
         * @param request
         * @param response
         * @return
         * @throws Exception
         */
        public ActionForward getEntityRoleType(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                           HttpServletResponse response) throws Exception {
            Logger l = LogUtils.enterLog(getClass(), "getEntityRoleType",
                new Object[]{mapping, form, request, response});

            try {
                Record inputRecord = getInputRecord(request);
                Record record = getEntityManager().getEntityRoleType(inputRecord);
                writeAjaxXmlResponse(response, record);
            }
            catch (Exception e) {
                handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get entity role type", e, response);
            }
            ActionForward af = null;
            l.exiting(getClass().getName(), "getEntityRoleType", af);
            return af;
        }


    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.lookupEntity.NoDataFound");
        MessageManager.getInstance().addJsMessage("pm.lookupEntity.quotation.error");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getEntityManager() == null)
            throw new ConfigurationException("The required property 'entityManager' is missing.");
    }

    public LookupEntityAction() {}

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    // private memember variables...
    private EntityManager entityManager;
}
