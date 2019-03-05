package dti.ci.policysummarymgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;


/**
 * The business component of License information.
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   2012-02-17
 *
 * @author parker
 */

/*
 * Revision Date      Revised By       Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
*/
public interface PolicySummaryManager {
    
    /**
     * load license information.
     * @param record
     * @return
     */
    public Record loadAddlInfo(Record record);
}
    
 