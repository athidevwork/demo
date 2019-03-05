package dti.pm.policymgr.quickpaymgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

/**
 * this class is an interface for quick pay manager
 * <p/>
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * Date:   July 21, 2010
 *
 * @author Dzhang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public interface QuickPayDAO {

    /**
     * To load all quick pay transaction data.
     * <p/>
     *
     * @param inputRecord input record
     * @return quick pay transaction recordset
     */
    public RecordSet loadAllQuickPayTransaction(Record inputRecord);

    /**
     * To load all quick pay transaction history data.
     * <p/>
     *
     * @param inputRecord input record
     * @param loadProcessor record load processor
     * @return quick pay transaction history record set
     */
    public RecordSet loadAllTransactionHistory(Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * To load all quick pay risks/coverages data.
     * <p/>
     *
     * @param inputRecord input record
     * @param loadProcessor record load processor.
     * @return quick pay risks/coverages record set
     */
    public RecordSet loadAllRiskCoverage(Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * To load quick pay summary data.
     * <p/>
     *
     * @param inputRecord input record
     * @return record contains quick pay summary data.
     */
    public Record loadQuickPaySummary(Record inputRecord);

    /**
     * Remove quick pay discount.
     * <p/>
     *
     * @param inputRecord input record
     * @return quick pay summary record
     */
    public RecordSet removeQuickPayDiscount(Record inputRecord);

    /**
     * Give quick pay discount.
     * <p/>
     *
     * @param inputRecord input record
     * @return quick pay summary record
     */
    public RecordSet addQuickPayDiscount(Record inputRecord);

    /**
     * To save all quick pay information.
     *
     * @param inputRecord a set of quick pay Record for saving.
     * @return recordset.
     */
    public RecordSet saveQuickPay(Record inputRecord);

    /**
     * To complete the quick pay transaction.
     *
     * @param inputRecord a set of quick pay Record for saving.
     * @return recordset.
     */
    public RecordSet completeQuickPayTransaction(Record inputRecord);

    /**
     * To get last quick pay transaction log id
     * <p/>
     *
     * @param inputRecord input record.
     * @return quick pay transaction log id
     */
    public String getLastQuickPayTransactionLogId(Record inputRecord);

    /**
     * To check if quick pay discount can be given.
     *
     * @param inputRecord input record.
     * @return the count of Non Insured Premium.
     */
    public String isAddQuickPayAllowed(Record inputRecord);

    /**
     * To delete the WIP data
     * <p/>
     *
     * @param inputRecord input record.
     * @return RecordSet
     */
    public RecordSet deleteQuickPayWip(Record inputRecord);

    /**
     * To load all Original Transaction data.
     * <p/>
     *
     * @param inputRecord input record with the passed request values.
     * @return a Record loaded with original transaction data.
     */
    public Record loadOriginalTransaction(Record inputRecord);

    /**
     * To load all risks/coverages information for process quick pay.
     * <p/>
     *
     * @param inputRecord input record
     * @return quick pay risks/coverages recordset
     */
    public RecordSet loadAllRiskCoverageForOriginalTransaction(Record inputRecord);

    /**
     * To check if the coverage payor is a hospital.
     *
     * @param inputRecord input record.
     * @return 'Y' or 'N'.
     */
    public String isHospitalCoveragePayor(Record inputRecord);

    /**
     * To load quick pay transaction summary data.
     * <p/>
     *
     * @param inputRecord input record
     * @return record contains quick pay transaction summary data.
     */
    public Record loadTransactionSummary(Record inputRecord);

    /**
     * To get last wip quick pay transaction log id
     * <p/>
     *
     * @param inputRecord input record.
     * @return last wip quick pay transaction log id
     */
    public String getLastWipQuickPayTransactionLogId(Record inputRecord);
}
