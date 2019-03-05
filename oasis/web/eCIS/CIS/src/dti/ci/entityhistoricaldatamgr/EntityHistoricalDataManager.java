package dti.ci.entityhistoricaldatamgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * Interface to handle Implementation of Discount Points Hist Manager.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * User: Michael
 * Date: August 08, 2010
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface EntityHistoricalDataManager {

    /**
     * Get the Discount Points Hist recordset
     *
     * @param inputRecord
     * @return
     */
    RecordSet loadAllAvailableEntityHistoricalDatas(Record inputRecord);

   
}
