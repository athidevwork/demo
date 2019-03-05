package dti.pm.coveragemgr.impl;

import dti.oasis.app.ApplicationContext;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.pm.coveragemgr.CoverageFields;

import java.util.ArrayList;

/**
 * Row style load processor for coverage
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * Date:   April 13, 2010
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
public class CoverageRowStyleRecordLoadprocessor extends DefaultRecordLoadProcessor {
    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        String defaultRowStyle = ApplicationContext.getInstance().getProperty("defaultRowStyle");
        String cancelRowStyle = ApplicationContext.getInstance().getProperty("cancelRowStyle");
        String requestRowStyle = ApplicationContext.getInstance().getProperty("requestRowStyle");
        String tempRowStyle = ApplicationContext.getInstance().getProperty("tempRowStyle");
        String recordModeCode = record.getStringValue("recordModeCode");
        String rowStyleFieldName = "coverageListGridRowStyle";

        if (CoverageFields.getCoverageStatus(record).isCancelled()) {
            record.setFieldValue(rowStyleFieldName, cancelRowStyle);
        }
        else if ("REQUEST".equals(recordModeCode)) {
            record.setFieldValue(rowStyleFieldName, requestRowStyle);
        }
        else if ("ENDQUOTE".equals(recordModeCode) || "TEMP".equals(recordModeCode)) {
            record.setFieldValue(rowStyleFieldName, tempRowStyle);
        }
        else {
            record.setFieldValue(rowStyleFieldName, defaultRowStyle);
        }
        return true;
    }

    /**
     * Process the RecordSet after all records have been loaded and processed.
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        // If no record is fetched, add the row style field to the recordset field collection
        if (recordSet.getSize() == 0) {
            ArrayList rowStyleFields = new ArrayList();
            rowStyleFields.add("coverageListGridRowStyle");
            recordSet.addFieldNameCollection(rowStyleFields);
        }
    }

    /**
     * Set initial entitlment values for row style
     *
     * @param inputRecord
     */
    public static void setInitialEntitlementValuesForRowStyle(Record inputRecord) {

        CoverageRowStyleRecordLoadprocessor lp = new CoverageRowStyleRecordLoadprocessor();
        // Set page entitlement values
        lp.postProcessRecord(inputRecord, true);
    }

}
