package dti.pm.policymgr.tailquotemgr.dao;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;

/**
 * An interface that provides DAO operation for Tail Quote information.
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
public interface TailQuoteDAO {
    /**
     * method to load all tail quote transactions
     * @param inputRecord
     * @param recordLoadProcessor
     * @return the recordset of tail quote transactions
     */
    RecordSet loadAllTailQuoteTransaction(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * method to load all quote tail
     * @param inputRecord
     * @param recordLoadProcessor 
     * @return the recordset of tail quote transactions
     */
    RecordSet loadAllTailQuote(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * save all tail quote transaction data
     * @param inputRecords
     * @return processed records count
     */
    int saveAllTailQuoteTransaction(RecordSet inputRecords);

    /**
     * save all quote tails
     * @param inputRecords
     * @return processed records count
     */
    int saveAllTailQuote(RecordSet inputRecords);

}
