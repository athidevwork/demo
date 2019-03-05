package dti.ci.vehiclemgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * The business component of vehicle.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 22, 2012
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface VehicleManager {
    /**
     * Load all vehicles of an entity.
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllVehicle(Record inputRecord);

    /**
     * Save all vehicles of an entity.
     * @param inputRecordSet
     * @return
     */
    public int saveAllVehicle(RecordSet inputRecordSet);
}
