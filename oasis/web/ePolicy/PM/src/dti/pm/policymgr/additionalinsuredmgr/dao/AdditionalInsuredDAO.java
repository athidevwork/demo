package dti.pm.policymgr.additionalinsuredmgr.dao;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;

/**
 * An interface that provides DAO operation for Additional Insured information.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 15, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/17/2010       syang       Issue 111445 - Added getAddInsCoverageData() to retrieve coverage data.
 * 02/27/2013       xnie        Issue 138026 - Added generateAllAddIns() to generate all of Additional Insureds.
 * ---------------------------------------------------
 */
public interface AdditionalInsuredDAO {

    /**
     * load all additioanl insured    
     * @param inputRecord
     * @param recordLoadProcessor
     * @return recordset of additional insured
     */
    RecordSet loadAllAdditionalInsured(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * save all additional insured data
     * @param inputRecords
     * @return processed records count
     */
    int saveAllAdditionalInsured(RecordSet inputRecords);

    /**
     * get Additionsl Insured Policy Type Count
     * @param inputRecord
     * @return  the count of Additionsl Insured Policy Type
     */
     int getAdditionslInsuredPolicyTypeCount(Record inputRecord);

    /**
     * To get coverage data
     *
     * @param inputRecord
     * @return Record
     */
    public Record getAddInsCoverageData(Record inputRecord);

    /**
     * Generate all Additional Insured.
     *
     * @param inputRecord input Additional Insured data.
     */
    void generateAllAddIns(Record inputRecord);
}


