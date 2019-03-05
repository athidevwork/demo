package dti.pm.quotemgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.pm.policymgr.PolicyHeader;

import java.security.Policy;

/**
 * An interface that provides basic operation for quote manager.
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:   April 27, 2016
 *
 * @author wdang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/26/2016       wdang       167534 - Initial version.
 * 09/07/2016       wdang       179350 - Modified performCopy to add Policy Header as input parameter.
 * ---------------------------------------------------
 */
public interface QuoteManager {

    public static final String BEAN_NAME = "QuoteManager";
    /**
     * Load all quote versions
     * @param inputRecord
     * @return
     */
    public RecordSet loadQuoteVersions(Record inputRecord);

    /**
     * Copy quote from policy/quote.
     * @param policyHeader
     * @param inputRecord
     */
    public Record performCopy(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Transfer changes.
     * @param inputRecord
     */
    public void performTransfer(Record inputRecord);

    /**
     * Apply changes.
     * @param inputRecord
     */
    public void performApply(Record inputRecord);

    /**
     * merge renewal WIP by current term changes.
     * @param inputRecord
     */
    public void performMerge (Record inputRecord);

    /**
     * Process auto pending renewal after Delete WIP or Save official.
     * @param policyHeader
     * @param inputRecord
     */
    public void processAutoPendingRenewal(PolicyHeader policyHeader, Record inputRecord);
}
