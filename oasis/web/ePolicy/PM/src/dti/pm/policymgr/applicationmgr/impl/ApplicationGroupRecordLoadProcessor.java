package dti.pm.policymgr.applicationmgr.impl;

import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.pm.policymgr.applicationmgr.ApplicationFields;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Record processor to add additional fields to support expand/collapse group applications
 * <p>(C) 2012 Delphi Technology, inc. (dti)</p>
 * Date:   May 14, 2012
 *
 * @author bhong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/12/2018       kshen       190717. Changed to use FormTypeCodeValues.isGroupFormTypeCode to check if a form
 *                              type code is a group form type code.
 * ---------------------------------------------------
 */
public class ApplicationGroupRecordLoadProcessor extends DefaultRecordLoadProcessor {
    /**
     * Process the given record after it's been loaded to enforce page entitlements.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "postProcessRecord", new Object[]{record, rowIsOnCurrentPage});
        }
        boolean result = true;

        String formTypeCode = ApplicationFields.getFormTypeCode(record);
        if (ApplicationFields.FormTypeCodeValues.isGroupFormTypeCode(formTypeCode)) {
            record.setFieldValue("groupExpandCollapse", "-");
        }
        else {
            record.setFieldValue("groupExpandCollapse", "");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "postProcessRecord", Boolean.valueOf(result));
        }
        return result;
    }
}
