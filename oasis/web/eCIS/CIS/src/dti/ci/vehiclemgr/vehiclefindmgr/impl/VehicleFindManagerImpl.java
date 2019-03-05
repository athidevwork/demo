package dti.ci.vehiclemgr.vehiclefindmgr.impl;

import dti.ci.vehiclemgr.VehicleFields;
import dti.ci.vehiclemgr.vehiclefindmgr.VehicleFindManager;
import dti.ci.vehiclemgr.vehiclefindmgr.dao.VehicleFindDAO;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;

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
public class VehicleFindManagerImpl implements VehicleFindManager {
    /**
     * Load Entity Vehicle List.
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadEntityVehicleList(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        String methodName = "loadEntityVehicleList";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName);
        }

        RecordLoadProcessor loadProcessor = new DefaultRecordLoadProcessor();
        RecordSet rs = getVehicleFindDAO().loadEntityVehicleList(inputRecord, loadProcessor);
        if(rs.getSize() == 0)
            MessageManager.getInstance().addInfoMessage("ci.vehicle.searchSelect.msg.warning.vehicleNotFound",new String[]{VehicleFields.getEntityDescription(inputRecord)});
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, rs);
        }
        return rs;
    }

    /**
     * Get Vehicle Find DAO
     *
     * @return
     */
    public VehicleFindDAO getVehicleFindDAO() {
        return m_vehicleFindDAO;
    }

    /**
     * Set Vehicle Find DAO
     *
     * @param m_vehicleFindDAO
     */
    public void setVehicleFindDAO(VehicleFindDAO m_vehicleFindDAO) {
        this.m_vehicleFindDAO = m_vehicleFindDAO;
    }

    private VehicleFindDAO m_vehicleFindDAO;
}
