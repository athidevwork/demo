package dti.ci.demographic.clientmgr.mntduplicate.data;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;

/**
 * An interface that provides DAO operation for Maintain Entity Duplicate.
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 19, 2008
 *
 * @author ldong
 */
/* 
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 
 * 11/26/2015       dpang       Issue 164029. Add saveEntityMntDuplicateWs for web service PartyChangeService
 * ---------------------------------------------------
*/

public interface EntityMntDuplicateDAO {
    /**
     * Merge Duplicate Entity
     *
     * @param inputRecords input records
     * @return a string representing the error Message if any, or null
     */
    String saveEntityMntDuplicate(Record inputRecords);

    /**
     * Merge Duplicate Entity for web service PartyChangeService.
     *
     * @param inputRecords input records
     * @return a string representing the error Message if any, or null
     */
    String saveEntityMntDuplicateWs(Record inputRecords);
}
