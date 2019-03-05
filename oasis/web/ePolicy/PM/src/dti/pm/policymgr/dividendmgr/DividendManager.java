package dti.pm.policymgr.dividendmgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.pm.policymgr.PolicyHeader;


/**
 * Interface to handle Implementation of Process Dividend.
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 10, 2011
 *
 * @author wfu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/13/2012       wfu         128705 - Added related methods to handle new dividend process.
 * 12/26/2013       awu         148187 - Added loadAllTransferRisk, transferDividend, loadAllDividendAudit.
 * ---------------------------------------------------
 */

public interface DividendManager {

    /**
     * Returns a RecordSet loaded with list of dividends
     *
     * @param inputRecord search criteria
     * @return RecordSet loaded dividend list.
     */
    public RecordSet loadAllDividendRule(Record inputRecord);

    /**
     * Save dividends info
     *
     * @param inputRecords dividends info
     * @return
     */
    public void saveAllDividendRule(RecordSet inputRecords);

    /**
     * Validate input dividend
     *
     * @param inputRecord dividends info
     * @return
     */
    public void validateCalculateDividend(Record inputRecord);

    /**
     * Calculate dividend record
     *
     * @param inputRecord dividend info
     * @return
     */
    public void calculateDividend(Record inputRecord);

    /**
     * Returns a RecordSet loaded with list of prior dividends
     *
     * @param inputRecord search criteria
     * @return RecordSet loaded dividend list.
     */
    public RecordSet loadAllPriorDividend(Record inputRecord);

    /**
     * Returns a RecordSet loaded with list of calculated dividends
     *
     * @param inputRecord search criteria
     * @return RecordSet loaded dividend list.
     */
    public RecordSet loadAllCalculatedDividend(Record inputRecord);

    /**
     * Returns a RecordSet loaded with list of dividends report summary
     *
     * @param inputRecord search criteria
     * @return RecordSet loaded dividend list.
     */
    public RecordSet loadAllDividendReportSummary(Record inputRecord);

    /**
     * Returns a RecordSet loaded with list of dividends report detail
     *
     * @param inputRecord search criteria
     * @return RecordSet loaded dividend list.
     */
    public RecordSet loadAllDividendReportDetail(Record inputRecord);

    /**
     * Get the initial values to add new dividend declaration
     *
     * @param inputRecord
     * @return Record
     */
    public Record getInitialValuesForAddDividendRule(Record inputRecord);

    /**
     * Post dividends for selected records
     *
     * @param inputRecord
     * @return
     */
    public void postDividend(Record inputRecord);

    /**
     * Returns a RecordSet loaded with list of dividend declaration
     *
     * @param inputRecord search criteria
     * @return RecordSet loaded dividend list.
     */
    public RecordSet loadAllDividendDeclare(Record inputRecord);

    /**
     * Save dividend declaration
     *
     * @param inputRecords dividend declarations
     * @return
     */
    public void saveAllDividendDeclare(RecordSet inputRecords);

    /**
     * Get the initial values to add new dividend declaration
     *
     * @param inputRecord
     * @return Record
     */
    public Record getInitialValuesForAddDividendDeclare(Record inputRecord);

    /**
     * Returns a RecordSet loaded with list of dividends
     *
     * @param inputRecord search criteria
     * @return RecordSet loaded dividend list.
     */
    public RecordSet loadAllDividendForPreview(Record inputRecord);

    /**
     * Process selected policy dividends
     *
     * @param inputRecord selected dividend
     * @return
     */
    public void performProcessDividend(Record inputRecord);

    /**
     * Returns a RecordSet loaded with list of processed dividends
     *
     * @param inputRecord search criteria
     * @return RecordSet loaded dividend list.
     */
    public RecordSet loadAllProcessedDividend(Record inputRecord);

    /**
     * Post selected policy dividends
     *
     * @param inputRecord selected dividend
     * @return
     */
    public void performPostDividend(Record inputRecord);

    /**
     * Load out the available risks which can do dividend transfer.
     *
     * @param policyHeader
     * @return
     */
    public RecordSet loadAllTransferRisk(PolicyHeader policyHeader);

    /**
     * process dividend transfer.
     *
     * @param policyHeader
     * @param record
     * @return
     */
    public void transferDividend(PolicyHeader policyHeader, Record record);

    /**
     * Used to load all the dividend for page dividendAudit.jsp
     *
     * @param policyHeader
     * @param record
     * @return
     */
    public RecordSet loadAllDividendAudit(PolicyHeader policyHeader, Record record);
}
