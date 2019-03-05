package dti.pm.policymgr.analyticsmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

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
public interface PredictiveAnalyticsDAO {

    /**
     * Retrieve request.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllRequest(Record inputRecord);

    /**
     * Retrieve request.
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
     * @return Record
     */
    public Record processOpa(Record inputRecord);

    /**
     * Get model type for current policy.
     *
     * @param inputRecord
     * @return String
     */
    public String getModelType(Record inputRecord);

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
}
