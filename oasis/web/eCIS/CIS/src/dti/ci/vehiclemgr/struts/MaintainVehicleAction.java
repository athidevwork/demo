package dti.ci.vehiclemgr.struts;

import dti.ci.core.struts.MaintainEntityFolderBaseAction;
import dti.ci.vehiclemgr.VehicleManager;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
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
 * Action Class for Vehicle
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 26, 2006
 *
 * @author gjli
 */

/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/17/2007       Fred        Added call to CILinkGenerator.generateLink()
 * 10/06/2010       wfu         111776: Replaced hardcode string with resource definition
 * 10/16/2018       Elvin       Issue 195835: grid replacement, extends MaintainEntityFolderBaseAction
 * ---------------------------------------------------
*/
public class MaintainVehicleAction extends MaintainEntityFolderBaseAction {

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
        return loadAllVehicle(mapping, form, request, response);
    }

    /**
     * Process loading vehicle.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllVehicle(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllVehicle", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadAllVehicleResult";

        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);
            String entityId = getEntityIdForMaintainEntityAction(request);
            inputRecord.setFieldValue("entityId", entityId);

            RecordSet rs = (RecordSet) request.getAttribute("gridRecordSet");
            if (rs == null) {
                rs = getVehicleManager().loadAllVehicle(inputRecord);
            }
            setDataBean(request, rs);
            loadGridHeader(request);

            Record outputRecord = new Record();
            outputRecord.setFields(inputRecord);
            outputRecord.setFields(rs.getSummaryRecord());

            publishOutputRecord(request, outputRecord);

            loadListOfValues(request, form);

            addJsMessages();

            saveToken(request);
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load vehicle page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllVehicle", af);
        }
        return af;
    }

    /**
     * Process saving vehicle.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveAllVehicle(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllVehicle", new Object[]{mapping, form, request, response});
        }

        String forwardString = "saveAllVehicleResult";
        RecordSet inputRs = null;

        try {
            if (hasValidSaveToken(request)) {
                securePage(request, form);

                inputRs = getInputRecordSet(request);
                inputRs.setSummaryRecord(getInputRecord(request));

                getVehicleManager().saveAllVehicle(inputRs);
            }
        } catch (ValidationException v) {
            request.setAttribute("gridRecordSet", inputRs);
            handleValidationException(v, request);
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to save all vehicle.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllVehicle", af);
        }
        return af;
    }

    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("ci.common.error.rowSelect.delete");
        MessageManager.getInstance().addJsMessage("js.delete.confirmation");
        MessageManager.getInstance().addJsMessage("ci.common.error.newRecords.delete");
        MessageManager.getInstance().addJsMessage("ci.common.error.existRecords.delete");
        MessageManager.getInstance().addJsMessage("js.refresh.lose.changes.confirmation");
        MessageManager.getInstance().addJsMessage("ci.entity.message.vehicleDescription.required");
        MessageManager.getInstance().addJsMessage("ci.entity.message.vehicleYear.between");
        MessageManager.getInstance().addJsMessage("ci.entity.message.netWeight.lessGross");
        MessageManager.getInstance().addJsMessage("ci.entity.message.acquisitionDate.invalid");

        MessageManager.getInstance().addJsMessage("ci.entity.message.price.number");
        MessageManager.getInstance().addJsMessage("ci.common.error.weight.number");
        MessageManager.getInstance().addJsMessage("ci.common.error.value.between");
    }

    public void verifyConfig() {
        if (getVehicleManager() == null) {
            throw new ConfigurationException("The required property 'vehicleManager' is missing.");
        }
    }

    public VehicleManager getVehicleManager() {
        return m_vehicleManager;
    }

    public void setVehicleManager(VehicleManager vehicleManager) {
        m_vehicleManager = vehicleManager;
    }

    private VehicleManager m_vehicleManager;
}
