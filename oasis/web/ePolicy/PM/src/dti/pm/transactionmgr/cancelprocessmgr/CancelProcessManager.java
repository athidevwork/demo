package dti.pm.transactionmgr.cancelprocessmgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.pm.policymgr.PolicyHeader;

import java.util.List;

/**
 * Interface to handle cancel process of policy.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 4, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/06/2008       yhyang     #87658 Add isTailCreatedForPriorTerm() to check if the tail created for the prior term.
 * 09/01/2010       fcb        111109 - validatePrePerformCancellation added.
 * 01/12/20101      syang      105832 - Added load/saveAllActiveRiskForCancellation() and modified performCancellation()
 *                             to handle discipline decline list.
 * 08/18/2011       syang      121201 - Added loadAllMultiCancelConfirmation(), processMultiCancelConfirmation() and validateMultiCancelConfirmation().
 * 03/21/2012       xnie       130643 - Added flatCancelPolicy() which handles flat cancel policy logic. 
 * ---------------------------------------------------
 */

public interface CancelProcessManager {
    /**
     * process Cancellation
     *
     * @param policyHeader
     * @param inputRecord
     * @param inputRecords
     * @return record containing cancel parameter tailB
     */
    Record performCancellation(PolicyHeader policyHeader, Record inputRecord, RecordSet inputRecords);


    /**
     * set initial values for cancellation popup
     *
     * @param policyHeader the summary information for the policy being reissued
     * @param inputRecord  an input record from request prior to get the initial values
     * @return record containing initial values for cancellation
     */
    public Record getInitialValuesForCancellation(PolicyHeader policyHeader, Record inputRecord);

    /**
     * validate perfrom multi cancellation
     *
     * @param policyHeader
     * @param inputRecord
     */
    public void validatePerformCancellation(PolicyHeader policyHeader, Record inputRecord);

    /**
     * validate information pre cancellation
     *
     * @param policyHeader policy header
     */
    public void validatePrePerformCancellation(PolicyHeader policyHeader, Record inputRecord);

    /**
     * validate cancellation detail
     *
     * @param policyHeader
     * @param inputRecord  *
     */
    public void validateCancellationDetail(PolicyHeader policyHeader, Record inputRecord);

    /**
     * validate if solo owner
     *
     * @param inputRecord
     * @return boolean value indicates if it is solo owner
     */
    boolean isSoloOwner(Record inputRecord);

    /**
     * load all cancelable items for multi cancel
     *
     * @param policyHeader
     * @param inputRecord
     * @return recordset of cancelable items
     */
    public RecordSet loadAllCancelableItem(PolicyHeader policyHeader, Record inputRecord);

    /**
     * process Multi Cancellation
     *
     * @param policyHeader
     * @param inputRs
     * @param inputRecord  with policy renewal infos
     * @return record containing cancel parameter tailB
     */
    Record performMultiCancellation(PolicyHeader policyHeader, RecordSet inputRs, Record inputRecord);

    /**
     * get initial values for mulit cancellation, add some page entitlements fields.
     *
     * @param policyHeader
     * @param inputRecord
     * @return inintial values include field indicators
     */
    Record getInitialValueForMultiCancel(PolicyHeader policyHeader, Record inputRecord);

    /**
     * rate policy for multi cancel
     *
     * @param policyHeader
     */
    Record ratePolicyForMultiCancel(PolicyHeader policyHeader);

    /**
     * Check if the tail created for the prior term.
     *
     * @param policyHeader
     * @return
     */
    public boolean isTailCreatedForPriorTerm(PolicyHeader policyHeader);

    /**
     * Purge policy
     *
     * @param policyHeader
     * @param inputRecord
     */
    public void purgePolicy(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Flat cancel policy
     *
     * @param policyHeader
     * @param inputRecord
     */
    public void flatCancelPolicy(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Load all transaction snapshot
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllTransactionSnapshot(Record inputRecord);

    /**
     * Load all term snapshot
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllTermSnapshot(Record inputRecord);

    /**
     * Load all policy component snapshot
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllPolicyComponentSnapshot(Record inputRecord);

    /**
     * Load all risk snapshot
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllRiskSnapshot(Record inputRecord);

    /**
     * Load all coverage snapshot
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllCoverageSnapshot(Record inputRecord);

    /**
     * Load all coverage component
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllCoverageComponentSnapshot(Record inputRecord);

    /**
     * Load all active risks.
     *
     * @param policyHeader
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllActiveRiskForCancellation(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Save all discipline decline entity. System saves discipline decline entity only for
     * Policy and Risk level cancellation when the "markAsDdl" is "Y".
     *
     * @param inputRecord
     * @param inputRecords
     * @return
     */
    public int saveAllDisciplineDeclineEntity(Record inputRecord, RecordSet inputRecords);

    /**
     * Validate multi cancellation transactions.
     *
     * @param policyHeader policy header
     * @param inputRecords recordSet from client
     * @param inputRecord  inputRecord contain request parameters
     * @return validate result
     */
    public Record validateMultiCancelConfirmation(PolicyHeader policyHeader, RecordSet inputRecords, Record inputRecord);

    /**
     * Load all the multi cancel confirmation.
     *
     * @param policyHeader
     * @param inputRecords
     * @param inputRecord
     * @return List
     */
    public List loadAllMultiCancelConfirmation(PolicyHeader policyHeader, RecordSet inputRecords, Record inputRecord);

    /**
     * Process multi cancellation transactions.
     *
     * @param policyHeader
     * @param inputRecord
     * @return Record
     */
    public Record processMultiCancelConfirmation(PolicyHeader policyHeader, Record inputRecord);
}
