package dti.pm.riskmgr.ibnrriskmgr.impl;

import dti.oasis.app.ApplicationContext;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.RecordMode;
import dti.pm.riskmgr.ibnrriskmgr.InactiveRiskFields;

import java.util.ArrayList;

/**
 * Row style load processor for risk
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 24, 2011
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
public class InactiveRiskRowStyleRecordLoadProcessor extends DefaultRecordLoadProcessor {
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
        String rowStyleFieldName = InactiveRiskFields.SECOND_GRID_ROW_STYLE;
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
        // If no record is fetched, add the row style field to the recordset field collection
        if (recordSet.getSize() == 0) {
            ArrayList rowStyleFields = new ArrayList();
            rowStyleFields.add(InactiveRiskFields.SECOND_GRID_ROW_STYLE);
            recordSet.addFieldNameCollection(rowStyleFields);
        }
    }

    /**
     * Set initial entitlment values for row style
     *
     * @param inputRecord
     */
    public static void setInitialEntitlementValuesForRowStyle(Record inputRecord) {

        InactiveRiskRowStyleRecordLoadProcessor lp = new InactiveRiskRowStyleRecordLoadProcessor();
        // Set page entitlement values
        lp.postProcessRecord(inputRecord, true);
    }

}