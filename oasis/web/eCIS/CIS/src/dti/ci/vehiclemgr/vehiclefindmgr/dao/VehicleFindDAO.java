package dti.ci.vehiclemgr.vehiclefindmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

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
public interface VehicleFindDAO {
    /**
     * Load Entity Vehicle List.
     *
     * @param inputRecord
     * @param loadProcessor
     * @return
     */
    public RecordSet loadEntityVehicleList(Record inputRecord, RecordLoadProcessor loadProcessor);
}
