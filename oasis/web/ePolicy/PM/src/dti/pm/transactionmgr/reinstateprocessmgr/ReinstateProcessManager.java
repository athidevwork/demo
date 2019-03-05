package dti.pm.transactionmgr.reinstateprocessmgr;

import dti.pm.policymgr.PolicyHeader;
import dti.oasis.recordset.Record;
/**
 * Interface to handle Implementation of  Reinstate Process.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 18, 2007
 *
 * @author Jerry
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface ReinstateProcessManager {

    /**
     * validate SoloOwnerReinstate,CustomReinstate,ActiveReinstate and Policy Prompt Eligible
     * <p/>
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a set of Records, each with the updated Reinstate info.
     */
    public void validateProcessReinstate(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Save all Schedule' information
     *
     * @param policyHeader policy header Record inputRecord
     * @param inputRecord  a set of Records, each with the updated Reinstate info.
     * @return record containing reinstate results
     */
    public Record performReinstate(PolicyHeader policyHeader, Record inputRecord);

    /**
     * validate Term
     *
     * @param policyHeader policy header Record inputRecord
     * @param inputRecord  a set of Records, each with the updated Reinstate info.
     */
    public void validateStatusAndTerm(PolicyHeader policyHeader, Record inputRecord);

    /**
     * is Policy Eligible For Prompt
     *
     * @param policyHeader policy header Record inputRecord
     * @param inputRecord  a set of Records, each with the updated Reinstate info.
     * @return String
     */
    public String isPolicyEligibleForPrompt(PolicyHeader policyHeader, Record inputRecord);
}
