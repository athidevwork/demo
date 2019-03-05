package dti.pm.transactionmgr.renewalprocessmgr;

import dti.oasis.recordset.Record;
import dti.oasis.busobjs.YesNoFlag;
import dti.pm.policymgr.PolicyHeader;

/**
 * Interface to handle renewal process of policy.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 16, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/18/2016       lzhang      180263 - Modified performAutoRenewal() input parameter
 * ---------------------------------------------------
 */

public interface RenewalProcessManager {
    /**
     * renew policy
     *
     * @param inputRecord with policy renewal infos
     */
    Record renewPolicy(PolicyHeader policyHeader, Record inputRecord);

    /**
     * load Pending Renewal Transaction
     *
     * @param policyHeader
     * @return the return record of PRT
     */
    Record loadPendingRenewalTransaction(PolicyHeader policyHeader);

    /**
     * load Pending Renewal Transaction
     *
     * @param policyHeader
     * @return is PRT confirmation required
     */
    boolean checkPRTConfirmationRequired(PolicyHeader policyHeader);

    /**
     * get initial values for  Renew Term Expiration
     * sets effective date to current term expiration date
     *
     * @param policyHeader
     * @return the return record of PRT
     */
    public Record getInitialValuesForRenewalTermExpiration(PolicyHeader policyHeader);

    /**
     * check if the renewal term expiration is required
     *
     * @param policyHeader
     * @return policy type configured parameter
     */
    public YesNoFlag isRenewalTermExpirationRequired(PolicyHeader policyHeader);

    /**
     * Validate auto renewal
     *
     * @param policyHeader
     */
    public void validateAutoRenewal(PolicyHeader policyHeader);

    /**
     * Perform auto renewal
     *
     * @param policyHeader
     */
    public void performAutoRenewal(PolicyHeader policyHeader, Record rec);
}
