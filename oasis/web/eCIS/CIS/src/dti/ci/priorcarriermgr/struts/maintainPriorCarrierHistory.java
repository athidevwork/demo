package dti.ci.priorcarriermgr.struts;

import dti.ci.struts.action.CIBaseAction;
import dti.ci.priorcarriermgr.PriorCarrierManager;
import dti.ci.core.error.PersistenceException;
import dti.oasis.util.LogUtils;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.tags.WebLayer;
import dti.oasis.tags.OasisFields;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.struts.IOasisAction;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.busobjs.WorkbenchConfiguration;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: jdingle
 * Date: Apr 11, 2010
 * Time: 8:55:44 AM
 * To change this template use File | Settings | File Templates.
 */
public class maintainPriorCarrierHistory extends CIBaseAction {
    public ActionForward unspecified(ActionMapping mapping,
                                     ActionForm form,
                                     HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        return loadAllPriorCarrierHistory(mapping, form, request, response);
    }

     /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    public ActionForward loadAllPriorCarrierHistory(ActionMapping mapping,
                                               ActionForm form,
                                               HttpServletRequest request,
                                               HttpServletResponse response) {

        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllPriorCarrierHistory", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadPriorCarrierHistory";

        try {
            securePage(request, form);
            //Set Search layer to invisible
            Record input = getInputRecord(request);
            String historyPK = input.getStringValue("pk");
            RecordSet rs = getPriorCarrierManager().loadPriorCarrierHistory(input);
            if (rs.getSize() == 0) {
                MessageManager.getInstance().addInfoMessage("ci.priorcarriermgr.noRecords.found.error");
            }
            setDataBean(request, rs);
            // Make the Summary Record available for output
            Record output = rs.getSummaryRecord();
            // Publish the input record
            publishOutputRecord(request, output);

            // Load grid header bean
            loadGridHeader(request);
            loadListOfValues(request, form);

            request.setAttribute("pk",historyPK);

            saveToken(request);

            loadJsMessage();

         //   addJsMessage();
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR,
                "Failed to load the priorCarrierHistory page.",
                e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllPriorCarrierHistory", af);
        }
        return af;
   }

    private void loadJsMessage(){
        // add js messages for CICommon.js
        MessageManager.getInstance().addJsMessage("ci.entity.message.entityType.unknown");
        MessageManager.getInstance().addJsMessage("ci.entity.message.module.unknown");

        MessageManager.getInstance().addJsMessage("ci.entity.message.value.verified");
        MessageManager.getInstance().addJsMessage("ci.entity.message.verified.beforeMaking");

        // add js messages for csCommon2.js
        MessageManager.getInstance().addJsMessage("cs.entity.miniPopup.error.noEntityId");
        MessageManager.getInstance().addJsMessage("cs.function.error.notExist");
        MessageManager.getInstance().addJsMessage("cs.entity.information.error.notRecorded");
        MessageManager.getInstance().addJsMessage("cs.rowSelected.error.exception");
        MessageManager.getInstance().addJsMessage("cs.run.error.grid.value");
        MessageManager.getInstance().addJsMessage("cs.invoke.error.parameter.invalid");
        MessageManager.getInstance().addJsMessage("cs.field.error.undefined");
    }

    /**
     * save Prior Carrier History Data.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward savePriorCarrierHistory(ActionMapping mapping, ActionForm form,
                                            HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "savePriorCarrierHistory",
                new Object[]{mapping, form, request, response});
        }

        String forwardString = "savePriorCarrierHistory";
        int count = 0;
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                securePage(request, form);
                Record inputRecord = getInputRecord(request);
                String historyPK = inputRecord.getStringValue("pk");
                request.setAttribute("pk",historyPK);
                RecordSet inputRecords  = getInputRecordSet(request);
                count = getPriorCarrierManager().savePriorCarrierHistory(inputRecords);
            }
        } catch (PersistenceException pe) {
            handleError(pe.getMessageKey(), "Failed to save Prior Carrier History data", pe, request, mapping);
        }
        catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to Save the Prior Carrier History page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "savePriorCarrierHistory", af);
        return af;
    }
 
    public void verifyConfig() {
        if (getPriorCarrierManager() == null)
            throw new ConfigurationException("The required property 'getPriorCarrierManager' is missing.");
    }

    public PriorCarrierManager getPriorCarrierManager() {
        return m_priorCarrierManager;
    }

    public void setPriorCarrierManager(PriorCarrierManager priorCarrierManager) {
        m_priorCarrierManager = priorCarrierManager;
    }

    private PriorCarrierManager m_priorCarrierManager;

}
