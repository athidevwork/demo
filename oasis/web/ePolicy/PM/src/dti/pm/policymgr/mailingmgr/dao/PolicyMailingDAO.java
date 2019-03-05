package dti.pm.policymgr.mailingmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

/**
 * An interface that provides DAO operation for policy mailing.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 17, 2007
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/02/2008       sxm         Issue 86930 - add loadAllMailingGenerationError
 * 10/06/2008       sxm         Issue 86930 - merge generateMailingEvent and loadAllMailingGenerationError
 *                              since the generation errors are stored in global temp table
 * ---------------------------------------------------
 */

public interface PolicyMailingDAO {

    /**
     * load all mailing event
     *
     * @param inputRecord
     * @return RecordSet share groups
     */
    RecordSet loadAllMailingEvent(Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * load all mailing attribute
     *
     * @param inputRecord
     * @param loadProcessor
     * @return RecordSet mailing attribute details
     */
    RecordSet loadAllMailingAttribute(Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * load all mailing recipient
     *
     * @param inputRecord
     * @param loadProcessor
     * @return RecordSet mailing recipients
     */
    RecordSet loadAllMailingRecipient(Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * load all past mailing
     *
     * @param inputRecord
     * @param loadProcessor
     * @return RecordSet past mailing policies
     */
    RecordSet loadAllPastMailing(Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * delete all mailing event
     *
     * @param inputRecords
     * @return updateCount
     */
    int deleteAllMailingEvent(RecordSet inputRecords);

    /**
     * delete all mailing attribute
     *
     * @param inputRecords
     * @return updateCount
     */
    int deleteAllMailingAttribute(RecordSet inputRecords);


    /**
     * delete all mailing recipient
     *
     * @param inputRecords
     * @return updateCount
     */
    int deleteAllMailingRecipient(RecordSet inputRecords);

    /**
     * save all mailing attribute
     *
     * @param inputRecords
     * @return updateCount
     */
    int saveAllMailingAttribute(RecordSet inputRecords);

    /**
     * save all mailing recipient
     *
     * @param inputRecords
     * @return updateCount
     */
    public int saveAllMailingRecipient(RecordSet inputRecords);


    /**
     * save all mailing event
     *
     * @param inputRecords
     * @return updateCount
     */
    int saveAllMailingEvent(RecordSet inputRecords);

    /**
     * validate recipient
     *
     * @param inputRecord(policyNo)
     * @return recordSet(only1or0record)
     */
    public RecordSet validateMailingRecipient(Record inputRecord);

    /**
     * get resend days by selected resend
     *
     * @param inputRecord(policyNo)
     * @return String
     */
    public String getResendDaysBySelectedResend(Record inputRecord);

    /**
     * check past mailing exist or not
     *
     * @param inputRecord
     * @return count(ifcountvalue>0,exist)
     */
    public int checkPastMailing(Record inputRecord);

    /**
     * generate mailing event by policy mailng id
     *
     * @param inputRecord (policyMailingId)
     * @return RecordSet   a record set loaded with list of mailing generation errors
     */
    public RecordSet generateMailingEvent(Record inputRecord);

   /**
     * reprint mailing event by policy mailng id
     *
     * @param inputRecord
     * @return record(count and retMsg)
     */
    public Record reprintMailingEvent(Record inputRecord);

}
