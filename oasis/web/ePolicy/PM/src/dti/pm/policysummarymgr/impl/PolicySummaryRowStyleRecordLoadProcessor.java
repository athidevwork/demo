package dti.pm.policysummarymgr.impl;

import dti.oasis.app.ApplicationContext;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.busobjs.PMStatusCode;
import dti.pm.busobjs.RecordMode;
import dti.pm.policysummarymgr.PolicySummaryFields;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Row style load processor for Policy Summary
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 06, 2016
 *
 * @author wdang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/06/2016       wdang       168069 - Initial version.
 * ---------------------------------------------------
 */
public class PolicySummaryRowStyleRecordLoadProcessor implements RecordLoadProcessor {

    @Override
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecord", new Object[]{record, rowIsOnCurrentPage});

        // get status
        String riskStatus = null;
        if (record.hasField(PolicySummaryFields.RISK_STATUS)) {
            riskStatus = record.getStringValue(PolicySummaryFields.RISK_STATUS);
        }
        // get record code mode
        String recordModeCode = null;
        if (record.hasField(PolicySummaryFields.RECORD_MODE_CODE)) {
            recordModeCode = record.getStringValue(PolicySummaryFields.RECORD_MODE_CODE);
        }
        // get row style
        String defaultRowStyle = ApplicationContext.getInstance().getProperty("defaultRowStyle");
        String cancelRowStyle = ApplicationContext.getInstance().getProperty("cancelRowStyle");
        String tempRowStyle = ApplicationContext.getInstance().getProperty("tempRowStyle");

        // set value to field
        if (PMStatusCode.CANCEL.getName().equals(riskStatus)) {
            record.setFieldValue(PolicySummaryFields.POLICY_SUMMARY_LIST_GRID_ROW_STYLE, cancelRowStyle);
        }
        else if (RecordMode.ENDQUOTE.getName().equals(recordModeCode)
            || RecordMode.TEMP.getName().equals(recordModeCode)) {
            record.setFieldValue(PolicySummaryFields.POLICY_SUMMARY_LIST_GRID_ROW_STYLE, tempRowStyle);
        }
        else {
            record.setFieldValue(PolicySummaryFields.POLICY_SUMMARY_LIST_GRID_ROW_STYLE, defaultRowStyle);
        }

        l.exiting(getClass().getName(), "postProcessRecord");
        return true;
    }


    @Override
    public void postProcessRecordSet(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecordSet", new Object[]{recordSet});
        // If no record is fetched, add the row style field to the recordset field collection
        if (recordSet.getSize() == 0) {
            ArrayList rowStyleFields = new ArrayList();
            rowStyleFields.add(PolicySummaryFields.POLICY_SUMMARY_LIST_GRID_ROW_STYLE);
            recordSet.addFieldNameCollection(rowStyleFields);
        }

        l.exiting(getClass().getName(), "postProcessRecordSet");
    }
}
