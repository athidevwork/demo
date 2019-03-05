package dti.ci.claimsmgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>(C) 2018 Delphi Technology, inc. (dti)</p>
 * Date:   4/19/2018
 *
 * @author jdingle
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/04/2018       hxk         Issue 191329
 *                              1)  Add parm to loadCompanion call.
 * ---------------------------------------------------
 */
public interface ClaimsManager {

    /**
     * Get the first claim in dropdown.
     *
     * @param inputRecord
     * @return Record
     */
    public Record loadFirstClaim(Record inputRecord);

    /**
     * Get the claim info.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadClaimInfo(Record inputRecord);

    /**
     * Get the claim participants list.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadClaimParticipants(Record inputRecord,HttpServletRequest request);

    /**
     * Get the companion claims.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadCompanion(Record inputRecord,HttpServletRequest request);
}
