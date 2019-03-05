package dti.ci.credentialrequestmgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * The business component of Credential Request Fee Adjustment.
 * <p/>
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:  03/04/2016
 *
 * @author jdingle
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface CredentialFeeAdjustManager {

    /**
     * Load Service Charges for the account.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllServiceCharges(Record inputRecord);
    /**
     * Process Reversal for selected Service Charges.
     *
     * @param inputRecordSet
     * @return int
     */
    public int saveAllServiceCharges(RecordSet inputRecordSet);
}
