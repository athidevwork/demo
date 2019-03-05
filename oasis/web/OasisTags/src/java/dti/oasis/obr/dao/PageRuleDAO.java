package dti.oasis.obr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

import java.sql.Time;
import java.util.Date;

/**
 * This is an interface for DAO implementation of page rule.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 20, 2011
 *
 * @author James
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface PageRuleDAO {

    /**
     * Load all Page rules
     *
     * @param record
     * @param recordLoadProcessor
     * @return
     */
    public abstract RecordSet loadAllPageRule(Record record, RecordLoadProcessor recordLoadProcessor);

    /**
     * get last modified time
     *
     * @param pageCode
     * @return
     */
    public abstract Date getLastModifiedTime(String pageCode);

    /**
     * Load all rule imports
     *
     * @return
     */
    public abstract RecordSet loadAllRuleImport();

    /**
     * Load all rule mapping
     *
     * @return
     */
    public abstract RecordSet loadAllRuleMapping();

    /**
     * Load all page fields
     *
     * @return
     */
    public abstract RecordSet loadAllPageFields(String pageCode);

}
