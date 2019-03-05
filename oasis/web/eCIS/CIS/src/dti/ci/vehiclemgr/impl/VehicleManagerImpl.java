package dti.ci.vehiclemgr.impl;

import dti.ci.core.CIFields;
import dti.ci.vehiclemgr.VehicleFields;
import dti.ci.vehiclemgr.VehicleManager;
import dti.ci.vehiclemgr.dao.VehicleDAO;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.OasisRecordSetHelper;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.struts.AddSelectIndLoadProcessor;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Business Object for Vehicle
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:  Jun 26, 2006
 *
 * @author gjli
 */

/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/06/2015       ylu         159740: remove description required hard check, it can be done by workbench
 * 11/17/2015       Elvin       Issue 167139: remove validateVehicles, all the logic are moved into page rules
 * ---------------------------------------------------
*/
public class VehicleManagerImpl implements VehicleManager {
    /**
     * Load all vehicles of an entity.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public RecordSet loadAllVehicle(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllVehicle", new Object[]{inputRecord});
        }

        Record record = new Record();
        CIFields.setEntityId(record, CIFields.getPk(inputRecord));

        RecordSet rs = getVehicleDAO().loadAllVehicle(record, AddSelectIndLoadProcessor.getInstance());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllVehicle", rs);
        }

        return rs;
    }

    /**
     * Save all vehicles of an entity.
     *
     * @param inputRecordSet
     * @return
     */
    @Override
    public int saveAllVehicle(RecordSet inputRecordSet) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllVehicle", new Object[]{inputRecordSet});
        }

        RecordSet changedRecords = OasisRecordSetHelper.setRowStatusOnModifiedRecords(inputRecordSet);
        int count = getVehicleDAO().saveAllVehicle(changedRecords);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllVehicle", Integer.valueOf(count));
        }

        return count;
    }

    public void verifyConfig() {
        if (getVehicleDAO() == null) {
            throw new ConfigurationException("The required property 'vehicleDAO' is missing.");
        }
    }

    public VehicleDAO getVehicleDAO() {
        return m_vehicleDAO;
    }

    public void setVehicleDAO(VehicleDAO vehicleDAO) {
        m_vehicleDAO = vehicleDAO;
    }

    private VehicleDAO m_vehicleDAO;
}
