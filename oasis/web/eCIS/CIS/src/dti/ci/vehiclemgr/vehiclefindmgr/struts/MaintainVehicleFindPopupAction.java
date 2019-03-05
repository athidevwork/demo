package dti.ci.vehiclemgr.vehiclefindmgr.struts;

import dti.ci.struts.action.CIBaseAction;
import dti.ci.vehiclemgr.VehicleFields;
import dti.ci.vehiclemgr.vehiclefindmgr.VehicleFindManager;
import dti.oasis.app.AppException;
import dti.oasis.error.ValidationException;
import dti.oasis.http.RequestIds;
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
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * User: cyzhao
 * Date: Jan 12, 2011
 */
/*
 *
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class MaintainVehicleFindPopupAction extends CIBaseAction {
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
    public ActionForward unspecified(ActionMapping mapping,
                                     ActionForm form,
                                     HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        return loadEntityVehicleList(mapping, form, request, response);
    }

    /**
     * Load data for Entity Vehicle List.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadEntityVehicleList(ActionMapping mapping,
                                               ActionForm form,
                                               HttpServletRequest request,
                                               HttpServletResponse response) throws Exception {

        String forwardString = "loadResult";
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadEntityVehicleList", new Object[]{mapping, form, request, response});
        }
        RecordSet rsVehicleList = null;
        try {
            // Secures page
            securePage(request, form);
            Record inputRecord = getInputRecord(request);

            String entityId = VehicleFields.getPk(inputRecord);
            VehicleFields.setEntityId(inputRecord, entityId);

            // load batches and set the dataBean to request
            rsVehicleList = (RecordSet) request.getAttribute(RequestIds.GRID_RECORD_SET);
            if (rsVehicleList == null) {
                rsVehicleList = getVehicleFindManager().loadEntityVehicleList(inputRecord);
            }
            setDataBean(request, rsVehicleList);

            loadListOfValues(request, form);

            // Load header
            loadGridHeader(request);

            //add js message
            addJsMessages();

            request.setAttribute(VehicleFields.PK, entityId);
            request.setAttribute(VehicleFields.ENTITY_DESCRIPTION, VehicleFields.getEntityDescription(inputRecord));

            saveToken(request);
        } catch (ValidationException e) {
            request.setAttribute(RequestIds.GRID_RECORD_SET, rsVehicleList);
            handleValidationException(e, request);
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load Entity Vehicle information.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadEntityVehicleList", af);
        }

        return af;
    }

    //add js message

    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("ci.common.error.record.select");
        MessageManager.getInstance().addJsMessage("js.delete.confirmation");
        MessageManager.getInstance().addJsMessage("ci.common.error.rowSelect.delete");
        MessageManager.getInstance().addJsMessage("js.refresh.lose.changes.confirmation");
        MessageManager.getInstance().addJsMessage("ci.vehicle.searchSelect.msg.warning.vehicleNotFound");
    }

    /**
     * @return
     */
    public VehicleFindManager getVehicleFindManager() {
        return m_vehicleFindManager;
    }

    /**
     * @param m_vehicleFindManager
     */
    public void setVehicleFindManager(VehicleFindManager m_vehicleFindManager) {
        this.m_vehicleFindManager = m_vehicleFindManager;
    }

    private VehicleFindManager m_vehicleFindManager;

}
