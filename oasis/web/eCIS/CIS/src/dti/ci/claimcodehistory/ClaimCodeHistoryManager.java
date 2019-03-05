package dti.ci.claimcodehistory;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 5, 2009
 *
 * @author msnadar
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface ClaimCodeHistoryManager {
    /**
     * Get CM Code History.
     * @param inputRecord source pk and code type
     * @return the CM Code History
     */
    public RecordSet getClaimCodeHistory(Record inputRecord);

}
