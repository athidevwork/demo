package dti.pm.policymgr.distributionmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

/**
 * An interface that provides DAO operation for process distribution.
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 11, 2011
 *
 * @author wfu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/06/2013       xnie        142674 - Added processCatchUp() to catch up dividend.
 * ---------------------------------------------------
 */

public interface DistributionDAO {

    /**
     * Returns a RecordSet loaded with list of distributions
     *
     * @param inputRecord
     * @param load an instance of data load processor
     * @return RecordSet a RecordSet loaded with list of available distributions.
     */
    public RecordSet loadAllDistribution(Record inputRecord, RecordLoadProcessor load);

    /**
     * Save distribution info
     *
     * @param inputRecords distribution info
     * @return
     */
    public void saveAllDistribution(RecordSet inputRecords);

    /**
     * Process distribution for selected row
     *
     * @param inputRecord
     * @return
     */
    public void processDistribution(Record inputRecord);

    /**
     * Catch up dividend
     *
     * @param inputRecord
     * @return
     */
    public void processCatchUp(Record inputRecord);
}
