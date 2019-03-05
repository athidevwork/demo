package dti.pm.quotemgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * An interface that provides DAO operation for quote.
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
 * ---------------------------------------------------
 */
public interface QuoteDAO {

    /**
     * Load relative policy/quote information versions specified policy/quote pk.
     * @param inputRecord
     * @return
     */
    public RecordSet loadQuoteVersions(Record inputRecord);

    /**
     * Copy quote from policy/quote.
     * @param inputRecord
     */
    public void performCopy(Record inputRecord);

    /**
     * Transfer changes.
     * @param inputRecord
     */
    public void performTransfer(Record inputRecord);

    /**
     * Apply quote.
     * @param inputRecord
     */
    public void performApply(Record inputRecord);

    /**
     * Apply quote.
     * @param inputRecord
     */
    public void performMerge(Record inputRecord);
}
