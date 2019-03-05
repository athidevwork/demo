package dti.pm.transactionmgr.reissueprocessmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.busobjs.YesNoFlag;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 22, 2007
 *
 * @author gjlong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface ReissueProcessDAO {

    /**    method to return the transaction code for the reissue transction to be created
     *
     * @param inputRecord  a record that is used to determine the transactionCode
     * @return transaction code
     */
     public String getTransactionCodeForReissueRenewalTransaction(Record inputRecord);

    /**
     *
     * @param inputRecord  a record that is used to determine if a Coi message should be generated
     * @return   YesNoFlag
     */
     public YesNoFlag isCoiCarriedForward(Record inputRecord);

    /**
     *
     * @param inputRecord a record that is used to reissue policy
     */
     public void reissuePolicy(Record inputRecord);

    /**
     *
     * @param inputRecord a record that is used to determine if a policy
     *         has at least one of its term(s) booked
     * @return  true/false indicating if policy has its term(s) booked
     */
     public boolean isPolicyTermBooked(Record inputRecord);

    /**
     *
     * @param inputRecord a record that is used to determine if the dates are
     *          overlapping with a policy's exsting terms
     * @return   true/false indicating if the dates are overlapping
     */
     public boolean areTermDatesOverlapping(Record inputRecord);
}
