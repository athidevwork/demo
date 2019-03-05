package dti.ci.entityclassmgr.impl;

import dti.ci.entityclassmgr.EntityClassFields;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.AddSelectIndLoadProcessor;
import dti.oasis.util.LogUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   3/28/2018
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class EntityClassListRecordLoadProcessor implements RecordLoadProcessor {
    private final Logger l = LogUtils.getLogger(getClass());
    private RecordLoadProcessor m_addSelectIndLoadProcessor = AddSelectIndLoadProcessor.getInstance();

    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     * @return true if this Record should be added to the RecordSet;
     * false if this Record should be excluded from the RecordSet.
     */
    @Override
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "postProcessRecord", new Object[]{record});
        }

        m_addSelectIndLoadProcessor.postProcessRecord(record, rowIsOnCurrentPage);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "postProcessRecord", true);
        }
        return true;
    }

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    @Override
    public void postProcessRecordSet(RecordSet recordSet) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "postProcessRecordSet", new Object[]{recordSet});
        }

        // Add select ind
        m_addSelectIndLoadProcessor.postProcessRecordSet(recordSet);

        // Add edit column.
        recordSet.setFieldValueOnAll(EntityClassFields.EDIT, EntityClassFields.EDIT_LABEL);

        l.exiting(getClass().getName(), "postProcessRecordSet");
    }
}
