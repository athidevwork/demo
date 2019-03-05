package dti.pm.policymgr.dividendmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

/**
 * An interface that provides DAO operation for process dividend.
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 30, 2011
 *
 * @author wfu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/13/2012       wfu         128705 - Added related methods to handle new dividend process.
 * 12/26/2013       awu         148187 - Added loadAllTransferRisk, transferDividend, loadDividendAudit.
 * ---------------------------------------------------
 */

public interface DividendDAO {

    /**
     * Returns a RecordSet loaded with list of dividend rule
     *
     * @param inputRecord
     * @param loadProcessor an instance of data load processor
     * @return RecordSet a RecordSet loaded with list of available dividend rule.
     */
    public RecordSet loadAllDividendRule(Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * Save dividend info
     *
     * @param inputRecords dividend info
     * @return
     */
    public void saveAllDividendRule(RecordSet inputRecords);

    /**
     * Calculate dividend
     *
     * @param inputRecord dividend info
     * @return record with return code and return message
     */
    public Record calculateDividend(Record inputRecord);

    /**
     * Load all the prior dividend
     *
     * @param inputRecord search criteria info
     * @return RecordSet
     */
    public RecordSet loadAllPriorDividend(Record inputRecord);

    /**
     * Load all the calculated dividend
     *
     * @param inputRecord
     * @param load an instance of data load processor
     * @return RecordSet
     */
    public RecordSet loadAllCalculatedDividend(Record inputRecord, RecordLoadProcessor load);

    /**
     * Load all the dividend report summary
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllDividendReportSummary(Record inputRecord);

    /**
     * Load all the dividend report detail
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllDividendReportDetail(Record inputRecord);

    /**
     * Post dividends for selected rows
     *
     * @param inputRecord
     * @return record with return code and return message
     */
    public Record postDividend(Record inputRecord);

    /**
     * Returns a RecordSet loaded with list of dividend declaration
     *
     * @param inputRecord
     * @param loadProcessor an instance of data load processor
     * @return RecordSet a RecordSet loaded with list of available dividend declaration.
     */
    public RecordSet loadAllDividendDeclare(Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * Save dividend declaration info
     *
     * @param inputRecords dividend declaration info
     * @return
     */
    public void saveAllDividendDeclare(RecordSet inputRecords);

    /**
     * Returns a RecordSet loaded with list of dividends
     *
     * @param inputRecord
     * @return RecordSet a RecordSet loaded with list of available dividends.
     */
    public RecordSet loadAllDividendForPreview(Record inputRecord);

    /**
     * Process dividends for selected rows
     *
     * @param inputRecord
     * @return
     */
    public void performProcessDividend(Record inputRecord);

    /**
     * Load all of the processed dividends
     *
     * @param inputRecord
     * @param loadProcessor an instance of data load processor
     * @return RecordSet a RecordSet loaded with list of processed dividends.
     */
    public RecordSet loadAllProcessedDividend(Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * Post dividends for selected rows
     *
     * @param inputRecord
     * @return record with return code and return message
     */
    public Record performPostDividend(Record inputRecord);

    /**
     * Load out all the available risks which can do dividend transfer.
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllTransferRisk(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * Process dividend transfer.
     *
     * @param inputRecord
     * @return
     */
    public Record transferDividend(Record inputRecord);

    /**
     * Load out all the dividend audit data.
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllDividendAudit(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

}
