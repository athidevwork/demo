package dti.pm.coveragemgr.vlcoveragemgr.dao;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;

/**
 * An interface that provides DAO operation for VL Coverage information.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 7, 2008
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface VLCoverageDAO {
    /**
     * load all VL risk info
     * @param inputRecord input parameters
     * @param recordLoadProcessor record load processor
     * @return recordset of VL risk info
     */
    RecordSet loadAllVLRisk(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * save non insured VL Risk data
     * @param inputRecord input records
     */
    void saveNonInsuredVLRisk(Record inputRecord);

    /**
     * save new/modifieds record
     * @param inputRecord input record
     */
    void saveVLRisk(Record inputRecord);

    /**
     * delete all records
     * @param inputRecords input records
     * @return process count
     */
    int deleteAllVLRisk(RecordSet inputRecords);

    /**
     * Retrieves last official data for a particular entity/risk 
     * @param inputRecord input parameters
     * @return vl risk info
     */
    Record getLastVLRiskInfo(Record inputRecord);
}
