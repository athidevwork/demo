package dti.pm.policysummarymgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

/**
 * An interface that provides DAO operation for Policy Summary.
 * <p/>
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 06, 2016
 *
 * @author wdang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/06/2016       wdang       168069 - Initial version.
 * ---------------------------------------------------
 */
public interface PolicySummaryDAO {
    /**
     * Load policy summary
     *
     * @param inputRecord          an input record that contains all member variables for the PolicyHeader
     * @param recordLoadProcessor an instance of data load processor
     * @return RecordSet
     */
    public RecordSet loadPolicySummary(Record inputRecord, RecordLoadProcessor recordLoadProcessor);
}
