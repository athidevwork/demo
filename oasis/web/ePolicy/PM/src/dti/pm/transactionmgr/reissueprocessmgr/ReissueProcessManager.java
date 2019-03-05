package dti.pm.transactionmgr.reissueprocessmgr;

import dti.oasis.recordset.Record;
import dti.pm.policymgr.PolicyHeader;

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
public interface ReissueProcessManager {
    /**
     *
     * @param policyHeader the summary information for the policy being reissued
     * @param inputRecord  an input record from request prior to get the initial values
     * @return  record containing initial values for reissuing policy
     */
    public Record getInitialValuesForReissuePolicy(PolicyHeader policyHeader, Record inputRecord);

    /**
     *
     * @param policyHeader the summary information for the policy being reissued
     * @param inputRecord  an input record being used for reissuing policy
     *  It throws a validation exception when the inputRecord does not pass the validation
     */
    public void reissuePolicy(PolicyHeader policyHeader, Record inputRecord);

     /**
     * @param policyHeader the summary information for the policy being reissued
     * @param inputRecord a record that is used to get the term expiration date by OASIS
     * @return  record that is used by AJAX to set field values
     */
   public Record getExpirationDateForReissuePolicy(PolicyHeader policyHeader, Record inputRecord);

}
