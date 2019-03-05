package dti.pm.policymgr.analyticsmgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.pm.policymgr.PolicyHeader;

/**
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   May 06, 2011
 *
 * @author syang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/14/2011       kshen       Added methods for Opa Error page.
 * ---------------------------------------------------
 */
public interface PredictiveAnalyticsManager {

    /**
     * Retrieve request.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllRequest(Record inputRecord);

    /**
     * Retrieve result.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllResult(Record inputRecord);

    /**
     * Retrieve reason.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllReason(Record inputRecord);

    /**
     * Process opa.
     *
     * @param inputRecord
     */
    public void processOpa(Record inputRecord);

    /**
     * Get initial values for opa.
     *
     * @param policyHeader
     * @return
     */
    public Record getInitialValueForOpa(PolicyHeader policyHeader);

    /**
     * Get initial values for opa error page.
     * @return
     */
    public Record getInitialValueForOpaError();

    /**
     * Load all Scoring Errors Records.
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllScoringError(Record inputRecord);

    /**
     * Load scoring error details.
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllScoringErrorDetail(Record inputRecord);

    /**
     * Search Scoring Error Records.
     * @param inputRecord
     * @return
     */
    public RecordSet searchScoringError(Record inputRecord);
}
