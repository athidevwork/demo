package dti.pm.policymgr.quickquotemgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * DAO for quick quote.
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
public interface QuickQuoteDAO {
    /**
     * Get import file path
     *
     * @param inputRecord
     * @return String
     */
    public String getImportFilePath(Record inputRecord);

    /**
     * Import file to quote
     *
     * @param inputRecord
     * @return Record
     */
    public Record importQuote(Record inputRecord);

    /**
     * get load event header
     *
     * @param inputRecord
     * @return Record
     */
    public Record getLoadEventHeader(Record inputRecord);

    /**
     * Load all import result
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllImportResult(Record inputRecord);

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
     * @param inputRecord
     * @return Record
     */
    public Record populateCis(Record inputRecord);
}
