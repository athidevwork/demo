package dti.pm.coveragemgr.vlcoveragemgr;

import dti.pm.policymgr.PolicyHeader;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * Inteface of business component which maintains VL Coverage
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
public interface VLCoverageManager {
    /**
     * load all VL risk info
     * @param policyHeader policy header
     * @param inputRecord input paramenters
     * @return recordset contains all VL Risk info
     */
    RecordSet loadAllVLRisk(PolicyHeader policyHeader, Record inputRecord);

    /**
     * save all VL risk info
     * @param policyHeader policy header
     * @param inputRecord input paramenters
     * @param inputRecords input records
     * @return process count
     */
    int saveAllVLRisk(PolicyHeader policyHeader, Record inputRecord, RecordSet inputRecords);


    /**
     * Initial values defaults for a new VL risk record
     * @param policyHeader contains policy header information
     * @param inputRecord input parameters
     * @return Record contains initial values
     */
    Record getInitialValuesForVLRisk(PolicyHeader policyHeader, Record inputRecord);
}
