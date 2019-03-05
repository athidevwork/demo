package dti.ci.claimsmgr.dao;


import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * DAO for getting data about claims.
 * <p/>
 * <p>(C) 2005 Delphi Technology, inc. (dti)</p>
 * Date: Dec 7, 2005
 *
 * @author Hong Yuan
 */
/*
* Revision Date    Revised By  Description
* --------------------------------------------------------------------
* 10/16/2009       Jacky       Add 'Jurisdiction' logic method for issue #97673
* 02/13/2015       bzhu        Issue 160886.
*                              1) Add overload function retrieveClaimNoFilterClauseSQL(HttpServletRequest request).
*                              2) revert old function retrieveClaimNoFilterClauseSQL.
* 09/10/2015       dpang       Issue 165980. Let db connection be open for later use.
* 06/26/2017       ddai        Issue 185457. Add new claim no filter.
* 04/19/2018       jld         Issue 192609: Refactor for eCIS.
* --------------------------------------------------------------------
*/

public interface ClaimsDAO {

    /**
     * Get the claim lov.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadClaimLov(Record inputRecord);

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
    public RecordSet loadClaimParticipants(Record inputRecord);

    /**
     * Get the companion claims.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadCompanion(Record inputRecord);

}
