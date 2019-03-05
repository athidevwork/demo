package dti.pm.policymgr.quickpaymgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.pm.policymgr.PolicyHeader;

/**
 * this class is an interface for quick pay manager
 * <p/>
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * Date:   July 22, 2010
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

public interface QuickPayManager {

    /**
     * To load all quick pay transaction data.
     * <p/>
     *
     * @param inputRecord input record with the passed request values.
     * @return a RecordSet loaded with list of available quick pay transaction data.
     */
    public RecordSet loadAllQuickPayTransaction(Record inputRecord);

    /**
     * To load all quick pay transaction history data.
     * <p/>
     *
     * @param inputRecord   input record with the passed request values.
     * @return a RecordSet loaded with list of available quick pay transaction history data.
     */
    public RecordSet loadAllTransactionHistory(Record inputRecord);

    /**
     * To load all quick pay risks/coverages for transaction detail page.
     * <p/>
     *
     * @param inputRecord   input record with the passed request values.
     * @return a RecordSet loaded with list of available quick pay risks/coverages data.
     */
    public RecordSet loadAllRiskCoverageForTransactionDetail(Record inputRecord);

    /**
     * To load all quick pay risks/coverages for quick pay detail.
     * <p/>
     *
     * @param inputRecord   input record with the passed request values.
     * @return a RecordSet loaded with list of available quick pay risks/coverages data.
     */
    public RecordSet loadAllRiskCoverageForQuickPayDetail(Record inputRecord);

    /**
     * Get the initial values for manage quick pay search criteria
     * <p/>
     *
     * @param policyHeader policyHeader.
     * @return the result include the initial values.
     */
    public Record getInitialValuesForSearchCriteria(PolicyHeader policyHeader);

    /**
     * To load quick pay summary data.
     * <p/>
     *
     * @param inputRecord input record with the passed request values.
     * @return a Record loaded with quick pay summary data.
     */
    public Record loadQuickPaySummary(Record inputRecord);


    /**
     * To save all quick pay transaction detail data.
     * <p/>
     *
     * @param inputRecords input record with the passed request values.
     * @return the number of rows updated.
     */
    public String saveAllQuickPayTransactionDetail(RecordSet inputRecords);

    /**
     * To remove quick pay discount data.
     * <p/>
     *
     * @param inputRecords input record with the passed request values.
     */
    public void removeQuickPayDiscount(RecordSet inputRecords);

    /**
     * To give quick pay discount.
     * <p/>
     *
     * @param inputRecords input record with the passed request values.
     */
    public void addQuickPayDiscount(RecordSet inputRecords);

    /**
     * To save all quick pay discount data.
     * <p/>
     *
     * @param inputRecords input record with the passed request values.
     */
    public void completeQuickPayTransaction(RecordSet inputRecords);

    /**
     * Get last quick pay transaction log id.
     * <p/>
     *
     * @param inputRecord input record.
     * @return String  last quick pay transaction log id.
     */
    public String getLastQuickPayTransactionLogId(Record inputRecord);

    /**
     * To check if the quick pay discount can be given or not.
     *
     * @param inputRecord a record loaded with query conditions
     * @return YesNoFlag to indicate quick pay discount can be given or not.
     */
    boolean isAddQuickPayAllowed(Record inputRecord);

    /**
     * To delete the WIP data
     * <p/>
     *
     * @param inputRecord input record.
     */
    public void deleteQuickPayWip(Record inputRecord);

    /**
     * To load all Original Transaction data.
     * <p/>
     *
     * @param inputRecord input record with the passed request values.
     * @return a RecordSet loaded with list of original transaction data.
     */
    public Record loadOriginalTransaction(Record inputRecord);

    /**
     * To load all quick pay risks/coverages data.
     * <p/>
     *
     * @param inputRecord input record with the passed request values.
     * @return a RecordSet loaded with list of available quick pay risks/coverages data.
     */
    public RecordSet loadAllRiskCoverageForOriginalTransaction(Record inputRecord);

    /**
     * To save all quick pay transaction detail data.
     * <p/>
     *
     * @param inputRecords input record with the passed request values.
     */
    public void saveAllRiskCoverageForOriginalTransaction(RecordSet inputRecords);

    /**
     * To check if the coverage payor is a hospital.
     *
     * @param inputRecord input record.
     * @return true or false.
     */
    public boolean isHospitalCoveragePayor(Record inputRecord);

    /**
     * To load quick pay transaction summary data.
     * <p/>
     *
     * @param inputRecord input record with the passed request values.
     * @return a Record loaded with quick pay transaction summary data.
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
