package dti.pm.tailmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.RecordLoadProcessor;

/**
 * An interface that provides DAO operation for tails.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 21, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/01/2010       syang       Issue 113780 - Added getCurrentRate() to retrieve current rate.
 * ---------------------------------------------------
 */

public interface TailDAO {

    /**
     * load all tales
     * @param inputRecord
     * @param recordLoadProcessor an instance of data load processor
     * @return  recordset include all tail parents
     */
    RecordSet loadAllTail(Record inputRecord,RecordLoadProcessor recordLoadProcessor);

    /**
     * load tail detail
     * @param inputRecord
     * @return recordSet include infos about tail
     */
    RecordSet loadAllTailDetail(Record inputRecord,RecordLoadProcessor recordLoadProcessor);


    /**
     * add manual tail coverage
     * @param inputRecord
     * @return the added record count
     */
    int addManualTail(Record inputRecord);

    /**
     * update all tail
     * @param inputRecords
     * @return the updated record count
     */
    int updateAllTail(RecordSet inputRecords);

    /**
     * call Pm_Validate_Tail.data to check the tail data
     * @param inputRecord
     * @return  string value to indicate if the tail data is valid
     */
    String getTailDataValidateResult(Record inputRecord);

    /**
     * call PM_WEB_TAIL.GET_POLICY_TERM_HISTORY_FK to get policyTermHistoryId of the tail record
     * @param inputRecord
     * @return  string value of policyTermHistoryId
     */
    String getTailHistoryId(Record inputRecord);

    /**
     * get the current ammount and amountNo info for the billing account related to the tail coverage
     * @param inputRecord
     * @return record include ammount and accountNo infos
     */
    Record getTailCredit(Record inputRecord);

    /**
     * validate tail coverage
     * @param inputRecord
     * @return rcord include status, message, validateResult VALID/INVALID
     */
    Record getTailProcessValidateResult(Record inputRecord);

    /**
     * get Tail Transaction Effective Date
     * @param inputRecord
     * @return tail transaction effective date
     */
    String getTailTransactionEffectiveDate(Record inputRecord);

    /**
     * process tail changes
     * @param inputRecord
     * @return rcord
     */
    Record processTail(Record inputRecord);


    /**
     * validate tail delta
     * @param inputRecord
     * @return record include error message and validate result
     */
    Record getTailDeltaValidateResult(Record inputRecord);

    /**
     * save tail charge
     * @param inputRecord
     * @return record excecute result
     */
    Record saveTailCharge(Record inputRecord);

    /**
     * load all available manual tails for adding new tail coverage
     * @param inputRecord
     * @param recordLoadProcessor
     * @return recordset of available tails for adding
     */
    RecordSet loadAllManualTail(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * get tail parent coverage's effective date
     * @param inputRecord
     * @return parent effective date
     */
    String getParentEffecitveDate(Record inputRecord);

    /**
     * get exist tail coverage count
     * @param inputRecord
     * @return count of exist tail coverages
     */
    int getTailCount(Record inputRecord);

    /**
     * Get current rate
     * @param inputRecord
     * @return current rate
     */
    float getCurrentRate(Record inputRecord);

}
