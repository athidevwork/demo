package dti.pm.transactionmgr.renewalprocessmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.busobjs.YesNoFlag;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 16, 2007
 *
 * @author yhchen
 */
/*
 * An interface that provides DAO operation for renewal process.
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/15/2016       tzeng       177134 - Modified performAutoRenewal() return value to record.
 * ---------------------------------------------------
 */

public interface RenewalProcessDAO {
    /**
     * save renewal information.
     *
     * @param inputRecord intput record
     * @return the return record of execute result
     */
    Record renewPolicy(Record inputRecord);

    /**
     * load Pending Renewal Transaction
     *
     * @param inputRecord
     * @return the return record of PRT
     */
    Record getPendingRenewalTransaction(Record inputRecord);

    /**
     * load policy type configured parameter
     *
     * @param inputRecord
     * @return is policy type configured
     */
    YesNoFlag isPolicyTypeConfigured(Record inputRecord);

    /**
     * Validate auto renewal
     * @param inputRecord
     * @return Record
     */
    Record validateAutoRenewal(Record inputRecord);

    /**
     * Perform auto renewal
     * @param inputRecord
     * @return Record
     */
    public Record performAutoRenewal(Record inputRecord);
}
