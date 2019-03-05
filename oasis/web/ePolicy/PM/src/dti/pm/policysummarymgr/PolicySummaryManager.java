package dti.pm.policysummarymgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.pm.policymgr.PolicyHeader;

/**
 * Interface to handle Implementation of Policy Summary Manager.
 * <p/>
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 06, 2016
 *
 * @author wdang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/06/2016       wdang       168069 - Initial version.
 * ---------------------------------------------------
 */
public interface PolicySummaryManager {
    /**
     * Returns a RecordSet loaded with list of available insured tracking for the provided risk.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  input records that contains key information.
     * @return RecordSet   a RecordSet loaded with list of available insured tracking.
     */
    RecordSet loadPolicySummary(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Wrapper to invoke the save process of the Policy Summary.
     *
     * @param policyHeader the summary policy information corresponding to the provided policy.
     * @param inputRecord  input records that contains key information.
     */
    void savePolicySummary(PolicyHeader policyHeader, Record inputRecord);
}
