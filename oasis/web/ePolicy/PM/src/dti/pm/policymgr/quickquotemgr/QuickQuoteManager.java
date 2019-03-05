package dti.pm.policymgr.quickquotemgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.pm.policymgr.PolicyHeader;

/**
 * Business component for quick quote.
 * <p/>
 * <p>(C) 2009 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 10, 2009
 *
 * @author Bhong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface QuickQuoteManager {
    /**
     * Import quote
     *
     * @param policyHeader
     * @param inputRecord
     * @return Record
     */
    public Record importQuote(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Load all import result
     *
     * @param policyHeader
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllImportResult(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Unload quick quote
     *
     * @param inputRecord
     * @return Record
     */
    public Record undoImportQuote(Record inputRecord);

    /**
     * Populate cis
     *
     * @param policyHeader
     * @param inputRecord
     * @return Record
     */
    public Record populateCis(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Get import file path
     *
     * @param inputRecord
     * @return String
     */
    public String getImportFilePath(Record inputRecord);
}
