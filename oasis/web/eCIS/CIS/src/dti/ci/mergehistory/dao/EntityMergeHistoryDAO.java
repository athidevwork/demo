package dti.ci.mergehistory.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * DAO for Merge History
 *
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   10/09/2015
 *
 * @author
 */

/*
 * Revision Date      Revised By       Description
 * ---------------------------------------------------
 * 10/09/2015         ylu              Issue 164517
 * ---------------------------------------------------
*/

public interface EntityMergeHistoryDAO {
    /**
     * load entity merge history
     * @param inputRecord
     * @return
     */
    RecordSet loadMergeHistory(Record inputRecord);

    /**
     * un-merge process
     * @param inputRecord
     * @return
     */
    String unMergeProcess(Record inputRecord);
}
