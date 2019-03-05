package dti.pm.policymgr.mailingmgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * Interface to handle Implementation of Policy Mailing.
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

public interface PolicyMailingManager {

    /**
     * Returns a RecordSet loaded with list of mailing event
     *
     * @param inputRecord search criteria
     * @return RecordSet a RecordSet loaded with list of available mailing event.
     */
    public RecordSet loadAllMailingEvent(Record inputRecord);

    /**
     * load all mailing attribute info
     *
     * @param inputRecord search criteria
     * @return RecordSet   a record set loaded with list of mailing attribute
     */
    public RecordSet loadAllMailingAttribute(Record inputRecord);

    /**
     * load all mailing recipient info
     *
     * @param inputRecord (policyMailingId)
     * @return RecordSet   a record set loaded with list of mailing recipient
     */
    public RecordSet loadAllMailingRecipient(Record inputRecord);

     /**
     * load all past mailing
     *
     * @param inputRecord (policyMailingId)
     * @return RecordSet   a record set loaded with list of past mailing
     */
    public RecordSet loadAllPastMailing(Record inputRecord);

    /**
     * save all policy mailing info(event, attribute, recipient)
     *
     * @param mailingEventRecords     mailing event records
     * @param mailingAttributeRecords mailing attribute records
     * @param mailingRecipientRecords mailing recipient records
     * @return the number of rows updated.
     */
    public int saveAllPolicyMailing(RecordSet mailingEventRecords, RecordSet mailingAttributeRecords, RecordSet mailingRecipientRecords);

    /**
     * create policy mailing info from selected policy
     *
     * @param inputRecord (selectedPolicyIds, productMailingId)
     * @return the number of rows updated.
     */
    public String createPolicyMailingFromPolicy(Record inputRecord);

    /**
     * get initial value of mailing event
     *
     * @return Record
     */
    public Record getInitialValuesForMailingEvent();

    /**
     * get initial value of mailing attribute
     *
     * @param inputRecord
     * @return
     */
    public Record getInitialValuesForMailingAttribute(Record inputRecord);

    /**
     * get initial value of mailing recipient
     *
     * @param inputRecord
     * @return
     */

    public Record getInitialValuesForMailingRecipient(Record inputRecord);

    /**
     * validate mailing recipient
     *
     * @param inputRecord(policyNo)
     * @return record(errorFlag,policyId,name,policyNo)
     */
    public Record validateMailingRecipient(Record inputRecord);

    /**
     * get resend days by selected resend
     *
     * @param inputRecord (productMailingResendId)
     * @return record(resendDays)
     */
    public Record getResendDaysBySelectedResend(Record inputRecord);

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
     * @return count(ifcount=0success,elsefail)
     */
    public int reprintMailingEvent(Record inputRecord);

    /**
     * delete exluded policy mailing detail
     *
     * @param policies
     * @return
     */
    public int deleteExludedPolicies(String policies);

}
