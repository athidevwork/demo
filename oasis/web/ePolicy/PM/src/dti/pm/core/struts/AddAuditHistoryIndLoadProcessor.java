package dti.pm.core.struts;

import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.Field;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.app.ApplicationContext;
import dti.pm.core.http.RequestIds;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * This class adds "AuditHistory" field to record set.
 * The purpose of this class is to display select checkbox for option selection page.
 *
 * <p/>
 * <p>(C) 2007 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 9, 2007
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class AddAuditHistoryIndLoadProcessor implements RecordLoadProcessor {

    /**
     * Add Select Ind field
     *
     * @param record
     * @param rowIsOnCurrentPage
     * @return boolean
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "postProcessRecord",
                new Object[]{record, String.valueOf(rowIsOnCurrentPage)});
        }
        record.setFieldValue("auditHistory", "View");
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "postProcessRecord");
        }
        return true;
    }  

    public void postProcessRecordSet(RecordSet recordSet) {
    }
}
