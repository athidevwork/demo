package dti.ci.vehiclemgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

/**
 * The DAO of vehicle.
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
public interface VehicleDAO {
    /**
     * Load all vehicles of an entity.
     * @param inputRecord
     * @param loadProcessor
     * @return
     */
    public RecordSet loadAllVehicle(Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * Save all vehicles of an entity.
     * @param inputRecordSet
     * @return
     */
    public int saveAllVehicle(RecordSet inputRecordSet);
}
