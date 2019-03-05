package dti.pm.riskmgr.addtlexposuremgr.impl;

import dti.oasis.app.ApplicationContext;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

import java.util.ArrayList;

/**
 * <p>(C) 2017 Delphi Technology, inc. (dti)</p>
 * Date:   May 23, 2017
 *
 * @author eyin
 */
/*
 *
 * Revision Date Revised By Description
 * ---------------------------------------------------
 * 09/21/2017    eyin       169483, Initial version.
 * ---------------------------------------------------
 */
public class RiskAddtlExposureRowStyleRecordLoadprocessor extends DefaultRecordLoadProcessor {
    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        String defaultRowStyle = ApplicationContext.getInstance().getProperty("defaultRowStyle");
        String requestRowStyle = ApplicationContext.getInstance().getProperty("requestRowStyle");
        String tempRowStyle = ApplicationContext.getInstance().getProperty("tempRowStyle");
        String recordModeCode = record.getStringValue("recordModeCode");
        String rowStyleFieldName = "riskAddtlExposureListGridRowStyle";

        if ("REQUEST".equals(recordModeCode)) {
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
            rowStyleFields.add("riskAddtlExposureListGridRowStyle");
            recordSet.addFieldNameCollection(rowStyleFields);
        }
    }

    /**
     * Set initial entitlment values for row style
     *
     * @param inputRecord
     */
    public static void setInitialEntitlementValuesForRowStyle(Record inputRecord) {

        RiskAddtlExposureRowStyleRecordLoadprocessor lp = new RiskAddtlExposureRowStyleRecordLoadprocessor();
        // Set page entitlement values
        lp.postProcessRecord(inputRecord, true);
    }

}
