package dti.pm.coveragemgr.minitailmgr.dao;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;

import java.util.Date;

/**
 * It's interface for mini tail DAO
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 20, 2007
 *
 * @author zlzhu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * Jul 20, 2007          zlzhu     Created
 * ---------------------------------------------------
 */

public interface MinitailDAO {
    /**
     * load the risk coverage data
     *
     * @param inputRecord it should include the following field:
     * @param recordLoadProcessor the load processor
     * @return the result which met the condition
     */
    public RecordSet loadAllMinitailRiskCoverage(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * load the risk mini tail
     *
     * @param inputRecord input record
     * @param recordLoadProcessor load processor
     * @return the result which met the condition
     */
    public RecordSet loadAllMinitail(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * save/update the mini tail data
     *
     * @param miniTails mini tail record set
     * @return number of the updated rows
     */
    public int saveAllMinitail(RecordSet miniTails);

    /**
     * get the start date of current risk
     *
     * @param inputRecord input record
     * @return the start date of current risk
     */
    public Date getRiskStartDate(Record inputRecord);

    /**
     * get the mini tail effective date
     *
     * @param inputRecord input record
     * @return the mini tail effective date
     */
    public Date getMiniRiskEffectiveDate(Record inputRecord);

    /**
     * Load All free mini tail
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllFreeMiniTail(Record inputRecord);

    /**
     * Check if free mini tail exist
     *
     * @param inputRecord
     * @return int
     */
    public int checkFreeMiniTail(Record inputRecord);
}
