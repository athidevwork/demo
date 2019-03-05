package dti.pm.policymgr.tailquotemgr;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.pm.policymgr.PolicyHeader;

/**
 * Interface to handle Tail Quote
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 23, 2008
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
public interface TailQuoteManager {
    /**
     * method to load all tail quote transactions
     * @param inputRecord
     * @param policyHeader
     * @return the recordset of tail quote transactions
     */
    RecordSet loadAllTailQuoteTransaction(PolicyHeader policyHeader, Record inputRecord);

    /**
     * method to load all tail quote data
     * @param inputRecord
     * @param policyHeader
     * @return the recordset of tail quote transactions
     */
    RecordSet loadAllTailQuote(PolicyHeader policyHeader, Record inputRecord);

    /**
     * perform process tail quote transaction
     * @param policyHeader
     * @param inputRecords
     * @param inputRecord
     * @return processed records count
     */
    int performProcessTailQuoteTransaction(PolicyHeader policyHeader,RecordSet inputRecords,Record inputRecord);
        
    /**
     * save all tail quote data
     *
     * @param policyHeader
     * @param inputRecords
     * @param inputRecord
     * @return processed records count
     */
    public int saveAllTailQuote(PolicyHeader policyHeader, RecordSet inputRecords, Record inputRecord);

    /**
     * to get initial values for a newly inserted Tail Quote Transaction record
     *
     * @param policyHeader
     * @param inputRecord
     * @return Record with initial vlaues
     */
    public Record getInitialValuesForTailQuoteTransaction(PolicyHeader policyHeader, Record inputRecord);
}
