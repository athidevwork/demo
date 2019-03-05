package dti.pm.riskmgr.addtlexposuremgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

/**
 * <p>(C) 2017 Delphi Technology, inc. (dti)</p>
 * Date:   May 23, 2017
 *
 * @author eyin
 */
/*
 *
 * Revision Date Revised By Description
 * ---------------------------------------------------
 * 09/21/2017    eyin       169483, Initial version.
 * 11/21/2017    lzhang     189820  Add getRiskAddtlBaseId method.
 * 12/27/2017    eyin       190491  1) Added isOoseChangeDateAllowed.
 * ---------------------------------------------------
 */
public interface RiskAddtlExposureDAO {
    
    /**
     * Returns a RecordSet loaded with list of available Additional Exposure for the provided risk.
     *
     * @param inputRecord   input records that contains key information
     * @param loadProcessor record load processor
     * @return              RecordSet a RecordSet loaded with list of available Addtional Exposure.
     */
    RecordSet loadAllRiskAddtlExposure(Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * Returns a RecordSet loaded with list of Primary Practice for the provided risk.
     *
     * @param inputRecord   input records that contains key information
     * @return              RecordSet a RecordSet loaded with list of Primary Practice
     */
    RecordSet loadPrimaryPractice(Record inputRecord);

    /**
     * Returns a RecordSet loaded with list of available Additional Exposure for the provided policy term.
     * @param inputRecord
     * @return
     */
    RecordSet loadAllRiskAddtlExposure(Record inputRecord);

    /**
     * Insert data in Additional Exposure page
     *
     * @param inputRecords a record set with data to be inserted.
     */
    int insertAllRiskAddtlExposure(RecordSet inputRecords);
    
    /**
     * Update data in Additional Exposure page
     *
     * @param inputRecords a record set with data to be updated.
     * 
     */
    int updateAllRiskAddtlExposure(RecordSet inputRecords);
    
    /**
     * Delete data in Additional Exposure page
     *
     * @param inputRecords a record set with data to be deleted.
     */
    int deleteAllRiskAddtlExposure(RecordSet inputRecords);

    /**
     * Validate Risk Additional Practice Duplication
     * @param inputRecord
     * @return
     */
    Record validateRiskAddtlPracticeDuplicate(Record inputRecord);

    /**
     * Get risk additional base record fk
     * @param inputRecord
     * @return
     */
    public String getRiskAddtlBaseId(Record inputRecord);

    /**
     * Check if changing exposure expiring date in OOSE is allowed
     *
     * @param inputRecord
     * @return
     */
    public String isOoseChangeDateAllowed(Record inputRecord);
}
