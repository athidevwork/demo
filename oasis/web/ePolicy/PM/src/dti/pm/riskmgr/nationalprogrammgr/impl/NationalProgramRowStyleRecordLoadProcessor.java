package dti.pm.riskmgr.nationalprogrammgr.impl;

import dti.oasis.app.ApplicationContext;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.RecordMode;
import dti.pm.riskmgr.nationalprogrammgr.NationalProgramFields;

import java.util.ArrayList;

/**
 * Row style load processor for risk
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   May 25, 2011
 *
 * @author Dzhang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class NationalProgramRowStyleRecordLoadProcessor extends DefaultRecordLoadProcessor {
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
        String rowStyleFieldName = NationalProgramFields.NATIONAL_PROGRAM_GRID_ROW_STYLE;
        RecordMode recordModeCode = PMCommonFields.getRecordModeCode(record);

        if (recordModeCode.isRequest()) {
            record.setFieldValue(rowStyleFieldName, requestRowStyle);
        }
        else if (recordModeCode.isEndquote() || recordModeCode.isTemp()) {
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
        // If no record is fetched, add the row style field to the recordSet field collection
        if (recordSet.getSize() == 0) {
            ArrayList rowStyleFields = new ArrayList();
            rowStyleFields.add(NationalProgramFields.NATIONAL_PROGRAM_GRID_ROW_STYLE);
            recordSet.addFieldNameCollection(rowStyleFields);
        }
    }

    /**
     * Set initial entitlement values for row style
     *
     * @param inputRecord  input records that contains key information
     */
    public static void setInitialEntitlementValuesForRowStyle(Record inputRecord) {

        NationalProgramRowStyleRecordLoadProcessor lp = new NationalProgramRowStyleRecordLoadProcessor();
        // Set page entitlement values
        lp.postProcessRecord(inputRecord, true);
    }

}