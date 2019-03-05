package dti.pm.riskmgr.affiliationmgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.pm.policymgr.PolicyHeader;

/**
 * Interface to handle Implementation of Affiliaion Manager.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 21, 2008
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
public interface AffiliationManager {

    /**
     * validate affiliation copy
     *
     * @param inputRecords
     * @return validate status code statusCode
     */
    String validateCopyAllAffiliation(RecordSet inputRecords);

    /**
     * copy all affliation data to target risk
     *
     * @param inputRecords
     */
    public void copyAllAffiliation(RecordSet inputRecords);

    /**
     * load all affiliations
     *
     * @param policyHeader
     * @param inputRecord
     * @param processor
     * @return taget risks recordset
     */
    RecordSet loadAllAffiliation(PolicyHeader policyHeader, Record inputRecord, RecordLoadProcessor processor);

     /**
     * load all affiliations
     *
     * @param policyHeader
     * @param inputRecord
     * @return taget risks recordset
     */
    RecordSet loadAllAffiliation(PolicyHeader policyHeader, Record inputRecord);
    /**
     * Save all inserted/updated affiliation records.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecords a set of Records, each with the updated affiliation Detail info       *
     */
    void saveAllAffiliation(PolicyHeader policyHeader, RecordSet inputRecords);

    /**
     * Get initial values for affiliation
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a record loaded with user entered data
     * @return Record a Record loaded with initial values.
     */
    Record getInitialValuesForAffiliation(PolicyHeader policyHeader, Record inputRecord);
}
