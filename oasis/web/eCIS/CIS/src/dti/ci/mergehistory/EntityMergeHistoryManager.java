package dti.ci.mergehistory;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * The business component of Merge History
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   10/09/15
 *
 * @author
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/09/2015       ylu         Issue 164517
 * ---------------------------------------------------
 */
public interface EntityMergeHistoryManager {

    /**
     * load entity merge history
     * @param inputRecord
     * @return
     */
    RecordSet loadMergeHistory(Record inputRecord);

    /**
     * un-merge history
     * @param inputRecord
     * @return
     */
    String unMergeProcess(Record inputRecord);
}

