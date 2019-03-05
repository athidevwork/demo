package dti.ci.policysummarymgr.dao;

import dti.oasis.recordset.Record;

/**
 * DAO for CIS Policy Summary
 * <p/>
 * <p>(C) 2013 Delphi Technology, inc. (dti)</p>
 * Date:   12/20/2013
 *
 * @author hxk
 */

/*
 * Revision Date      Revised By       Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
*/
public interface PolicySummaryDAO {

    /**
     * load additional info data.
     * @param record
     * @return
     */
    public Record loadAddlInfo(Record inputRecord);
        
    
}
