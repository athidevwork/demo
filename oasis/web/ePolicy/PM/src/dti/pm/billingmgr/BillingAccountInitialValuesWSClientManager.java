package dti.pm.billingmgr;

import dti.oasis.recordset.Record;
import dti.pm.policymgr.PolicyHeader;

import java.net.MalformedURLException;

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
public interface BillingAccountInitialValuesWSClientManager {

    /**
     * Call webservice to load the initial data and set them to output record.
     * @param policyHeader
     * @param inputRecord
     * @return
     * @throws MalformedURLException
     */
    public Record getInitialValuesForBilling(PolicyHeader policyHeader, Record inputRecord);
}
