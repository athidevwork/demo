package dti.ci.billingmgr;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;

/**
 * Interface to handle CIS Billing operation
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 27, 2009
 *
 * @author yjmiao
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public interface CIBillingManager {
    /**
     * To load all accounts by given entityId.
     * @param inputRecord
     * @return
     */
    RecordSet loadAllAccount(Record inputRecord);
}
