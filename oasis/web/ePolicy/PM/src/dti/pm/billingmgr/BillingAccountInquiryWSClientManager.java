package dti.pm.billingmgr;

import dti.oasis.recordset.Record;
import dti.pm.policymgr.PolicyHeader;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 20, 2014
 *
 * @author awu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * ---------------------------------------------------
 */

public interface BillingAccountInquiryWSClientManager {

    /**
     * Call webService to load the billing data.
     * @param policyHeader
     * @param inputRecord
     * @return
     */
    public boolean isBillingExists(PolicyHeader policyHeader, Record inputRecord);
}
