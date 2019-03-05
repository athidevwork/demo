package dti.ci.entityhistoricaldatamgr.struts;
import dti.ci.entityhistoricaldatamgr.EntityHistoricalDataFields;
import dti.ci.entityhistoricaldatamgr.EntityHistoricalDataManager;
import dti.ci.helpers.CILinkGenerator;
import dti.ci.struts.action.CIBaseAction;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.http.RequestIds;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.IOasisAction;
import dti.oasis.util.LogUtils;
import dti.oasis.util.PageBean;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * User: Michael
 * Date: August 08, 2010
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/28/2018       ylu         Issue 194117: update for CSRF security.
 * 10/18/2018       ylu         Issue 195835: grid replacement.
 * ---------------------------------------------------
 */
public class MaintainEntityHistoricalDataAction extends CIBaseAction {

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

        return initPage(mapping, form, request, response);
    }
    /**
         * Load all Entity Historical Data
         *
         * @param mapping
         * @param form
         * @param request
         * @param response
         * @return
         * @throws Exception
         */
        public ActionForward initPage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                     HttpServletResponse response) throws Exception {

            Logger l = LogUtils.enterLog(getClass(), "loadAllAvailableEntityHistoricalDatas", new Object[]{mapping, form, request, response});
            String forwardString = "loadResult";


            try {
                String pk = request.getParameter(EntityHistoricalDataFields.PK_PROPERTY);
                String entityName = request.getParameter(EntityHistoricalDataFields.ENTITY_NAME_PROPERTY);
                String entityType = request.getParameter(EntityHistoricalDataFields.ENTITY_TYPE_PROPERTY);
                request.setAttribute(EntityHistoricalDataFields.PK_PROPERTY, pk);
                request.setAttribute(EntityHistoricalDataFields.ENTITY_NAME_PROPERTY, entityName);
                request.setAttribute(EntityHistoricalDataFields.ENTITY_TYPE_PROPERTY, entityType);
//             if (entityType.charAt(0) == ENTITY_TYPE_ORG_CHAR) {
//                checkCisFolderMenu(request);
//            }
                securePage(request, form);
                Record inputRecord = getInputRecord(request);
                inputRecord.setFieldValue("entityId", pk);
                String entityId = request.getParameter(EntityHistoricalDataFields.PK_PROPERTY);

                RecordSet reviewRs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
                if (reviewRs == null) {
                    reviewRs = getEntityHistoricalDataManager().loadAllAvailableEntityHistoricalDatas(inputRecord);
                    reviewRs.reduceRecords(0);
                }
                setDataBean(request, reviewRs);

                // Make the Summary Record available for output.
                Record output = reviewRs.getSummaryRecord();

                output.setFields(inputRecord);
                // Publish the output record for use by the Oasis Tags and JSP.
                publishOutputRecord(request, output);

                // Load LOV
                loadListOfValues(request, form);

                // Load grid header bean
                loadGridHeader(request);

                setCisHeaderFields(request);

                addJsMessages();
                // highlight current menu item
                this.highLightCurrentMenuItem((PageBean) request.getAttribute(IOasisAction.KEY_PAGEBEAN));
                request.setAttribute(EntityHistoricalDataFields.PK_PROPERTY, entityId);
                //setEntityCommonInfoToRequest(request, form);
                saveToken(request);
                new CILinkGenerator().generateLink(request, pk, this.getClass().getName());

            }
            catch (Exception e) {
                forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the Entity Historical Data page.", e, request, mapping);
            }

            ActionForward af = mapping.findForward(forwardString);
            l.exiting(getClass().getName(), "loadAllAvailableEntityHistoricalDatas", af);
            return af;
        }

    /**
     * Load all Entity Historical Data
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllAvailableEntityHistoricalDatas(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                 HttpServletResponse response) throws Exception {

        Logger l = LogUtils.enterLog(getClass(), "loadAllAvailableEntityHistoricalDatas", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";


        try {
            String pk = request.getParameter(EntityHistoricalDataFields.PK_PROPERTY);
            String entityName = request.getParameter(EntityHistoricalDataFields.ENTITY_NAME_PROPERTY);
            String entityType = request.getParameter(EntityHistoricalDataFields.ENTITY_TYPE_PROPERTY);
            request.setAttribute(EntityHistoricalDataFields.PK_PROPERTY, pk);
            request.setAttribute(EntityHistoricalDataFields.ENTITY_NAME_PROPERTY, entityName);
            request.setAttribute(EntityHistoricalDataFields.ENTITY_TYPE_PROPERTY, entityType);
//             if (entityType.charAt(0) == ENTITY_TYPE_ORG_CHAR) {
//                checkCisFolderMenu(request);
//            }
            securePage(request, form);
            Record inputRecord = getInputRecord(request);
            inputRecord.setFieldValue("entityId", pk);
            String entityId = request.getParameter(EntityHistoricalDataFields.PK_PROPERTY);

            RecordSet reviewRs = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);          
            if (reviewRs == null) {
                reviewRs = getEntityHistoricalDataManager().loadAllAvailableEntityHistoricalDatas(inputRecord);
            }
            setDataBean(request, reviewRs);

            // Make the Summary Record available for output.
            Record output = reviewRs.getSummaryRecord();

            output.setFields(inputRecord);
            // Publish the output record for use by the Oasis Tags and JSP.
            publishOutputRecord(request, output);

            // Load LOV
            loadListOfValues(request, form);

            // Load grid header bean
            loadGridHeader(request);

            setCisHeaderFields(request);

            addJsMessages();
            // highlight current menu item
            this.highLightCurrentMenuItem((PageBean) request.getAttribute(IOasisAction.KEY_PAGEBEAN));
            request.setAttribute(EntityHistoricalDataFields.PK_PROPERTY, entityId);
            //setEntityCommonInfoToRequest(request, form);
            saveToken(request);
            new CILinkGenerator().generateLink(request, pk, this.getClass().getName());

        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load the Entity Historical Data.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllAvailableEntityHistoricalDatas", af);
        return af;
    }
      //add js message
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("ci.entity.message.searchCriteria.enter");
    }
    /* Configuration constructor and accessor methods */
    public void verifyConfig() {
        if (getEntityHistoricalDataManager() == null)
            throw new ConfigurationException("The required property 'getEntityHistoricalDataManager' is missing.");
    }

    public EntityHistoricalDataManager getEntityHistoricalDataManager() {
        return entityHistoricalDataManager;
    }

    public void setEntityHistoricalDataManager(EntityHistoricalDataManager entityHistoricalDataManager) {
        this.entityHistoricalDataManager = entityHistoricalDataManager;
    }

    private EntityHistoricalDataManager entityHistoricalDataManager;
}