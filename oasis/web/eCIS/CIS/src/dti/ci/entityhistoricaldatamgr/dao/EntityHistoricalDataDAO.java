package dti.ci.entityhistoricaldatamgr.dao;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;

/**
 * An interface that provides DAO operation for Discount Points Hist.
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
public interface EntityHistoricalDataDAO {
    /**
     * Get all Discount Points Hist from DB
     *
     * @param record
     * @param recordLoadProcessor
     * @return
     */
    RecordSet loadAllAvailableEntityHistoricalDatas(Record record, RecordLoadProcessor recordLoadProcessor);


   
}
