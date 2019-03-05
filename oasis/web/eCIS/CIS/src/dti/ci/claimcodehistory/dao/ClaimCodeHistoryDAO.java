package dti.ci.claimcodehistory.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

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
public interface ClaimCodeHistoryDAO {

    /**
     * Get Claim Code History
     * @param inputRecord Source Fk and Table Name
     * @return  The Claim Code History
     */
    public RecordSet getClaimCodeHistory(Record inputRecord);
}
