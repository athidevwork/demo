package dti.pm.transactionmgr.cancelprocessmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.busobjs.YesNoFlag;

/**
 * An interface that provides DAO operation for cancel process.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 4, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/31/2008       yhyang      #87658 Change the isTailExistForTerm to getTailTerm.
 * 08/26/2010       fcb         111109 - validateRenewalWipCancellation added.
 * 01/11/2011       ryzhao      113558 - isNewCarrierEnabled added.
 * 01/15/20101      syang       105832 - Added saveAllDisciplineDeclineEntity() to save discipline decline entity.
 * 03/21/2012       xnie        130643 - Renamed purgePolicy to cancelPolicy.
 * 08/29/2011       ryzhao      133360 - Change the return type of isAllRiskOwnersSelected from YesNoFlag to Record
 *                                       which includes two return fields.
 * 05/09/2014       awu         152675 - Added validateCancelCoverage.
 * 07/04/2016       eyin        176476 - Changed validateCancellation, validateCancelRisk, validateCancelCoverage and
 *                                       validateCancelCoverageClass, to return type of output parameter from Record to
 *                                       RecordSet, which includes future cancellation records.
 * ---------------------------------------------------
 */

public interface CancelProcessDAO {
    /**
     * to check if the entity is solo owner
     *
     * @param inputRecord
     * @return boolean value to indicate if the entity is solo owner
     */
    boolean isSoloOwner(Record inputRecord);

    /**
     * to check if the tail need to be created
     *
     * @param inputRecord
     * @return boolean value to indicate if the tail need to be created
     */
    boolean isToCreateTail(Record inputRecord);

    /**
     * to check if the cancellation is valid
     *
     * @param inputRecord
     * @return recordSet include the result, error message and future cancellation records
     */
    RecordSet validateCancellation(Record inputRecord);

    /**
     * to check if the renewal wip cancellation is valid
     *
     * @param inputRecord
     * @return record include the result and error message
     */
    Record validateRenewalWipCancellation(Record inputRecord);

    /**
     * to process the cancellation process
     *
     * @param inputRecord
     * @return the excecution result
     */
    Record performCancellation(Record inputRecord);

    /**
     * to process the cancellation process on slot
     *
     * @param inputRecord
     * @return the excecution result
     */
    Record performSlotCancellation(Record inputRecord);

    /**
     * to process the cancellation process on risk relation
     *
     * @param inputRecord
     * @return
     */
    Record performRiskRelationCancellation(Record inputRecord);

    /**
     * load all cancelable items for multi cancel
     *
     * @param inputRecord
     * @param lp
     * @return
     */
    RecordSet loadAllCancelableItem(Record inputRecord, RecordLoadProcessor lp);

    /**
     * load all cancelable coi holders for multi cancel
     *
     * @param inputRecord
     * @param lp
     * @return
     */
    RecordSet loadAllCancelableCoi(Record inputRecord, RecordLoadProcessor lp);

    /**
     * perfom risk cancellation for multi cancel
     *
     * @param inputRecord
     * @return
     */
    Record cancelRisk(Record inputRecord);

    /**
     * perfom coverage cancellation for multi cancel
     *
     * @param inputRecord
     * @return
     */
    Record cancelCoverage(Record inputRecord);

    /**
     * perfom component cancellation for multi cancel
     *
     * @param inputRecords
     * @return
     */
    void cancelAllComponent(RecordSet inputRecords);

    /**
     * perfom sub coverage cancellation for multi cancel
     *
     * @param inputRecord
     * @return
     */
    Record cancelCoverageClass(Record inputRecord);

    /**
     * perfom VL Employee cancellation
     *
     * @param inputRecord
     * @return
     */
    Record cancelVLEmployee(Record inputRecord);

    /**
     * get future primary risk count for risk cancel validation
     *
     * @param inputRecord
     * @return
     */
    int getFuturePrimaryRiskCount(Record inputRecord);

    /**
     * perfrom coi holder cancellation for certain risk
     * @param inputRecords
     */
    void cancelAllCoiHolder(RecordSet inputRecords);

    /**
     * to get the result to indicate if all owners of the entity risk are selected
     *
     * @param inputRecord
     * @return
     */
    Record isAllRiskOwnersSelected(Record inputRecord);

    /**
     * validate risk cancellation for multi cancel
     *
     * @param inputRecord
     * @return recordSet include the result, error message and future cancellation records
     */
    RecordSet validateCancelRisk(Record inputRecord);

    /**
     * get the count of sub coverages which can be cancelled
     * @param inputRecord
     * @return
     */
    int getCancelableCoverageClassCount(Record inputRecord);

    /**
     * validate coverage class cancellation for multi cancel
     * @param inputRecord
     * @return recordSet include the result, error message and future cancellation records
     */
    RecordSet validateCancelCoverageClass(Record inputRecord);

    /**
     * validate coverage cancellation for multi cancel
     * @param inputRecord
     * @return recordSet include the result, error message and future cancellation records
     */
    RecordSet validateCancelCoverage(Record inputRecord);

    /**
     * return the tail term base ID after multi cancellation
     * @return inputRecord
     */
    String getTailTerm(Record inputRecord);

    /**
     * resolve tail for multi cancel
     * @param inputRecord
     */
    void resolveTail(Record inputRecord);

    /**
     * Validate amalgamation data
     *
     * @param inputRecord
     * @return Record
     */
    public Record validateAmalgamation(Record inputRecord);

    /**
     * process amalgamation
     *
     * @param inputRecord
     * @return Record
     */
    public Record processAmalgamation(Record inputRecord);

    /**
     * Cancel policy
     *
     * @param inputRecord
     */
    public void cancelPolicy(Record inputRecord);

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
     * to get the result to indicate if new carrier field is available in the cancel page.
     *
     * @param inputRecord
     * @return YesNoFlag
     */
    public YesNoFlag isNewCarrierEnabled(Record inputRecord);

    /**
     * Save all discipline decline entity.
     *
     * @param inputRecords
     * @return
     */
    public int saveAllDisciplineDeclineEntity(RecordSet inputRecords);
}
