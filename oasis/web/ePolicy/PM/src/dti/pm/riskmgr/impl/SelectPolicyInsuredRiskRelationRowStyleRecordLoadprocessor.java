package dti.pm.riskmgr.impl;

import dti.oasis.app.ApplicationContext;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.util.StringUtils;
import dti.pm.riskmgr.RiskFields;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   8/28/2015
 *
 * @author tzeng
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/28/2015       tzeng       set the newly available related risk records in blue color.
 * ---------------------------------------------------
 */
public class SelectPolicyInsuredRiskRelationRowStyleRecordLoadprocessor extends DefaultRecordLoadProcessor {

    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     * @return
     */
    @Override
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        String tempRowStyle = ApplicationContext.getInstance().getProperty("tempRowStyle");
        String recordModeCode = record.getStringValue(RiskFields.RECORD_MODE_CODE);
        String officialRecordId = record.getStringValue(RiskFields.OFFICIAL_RECORD_ID);
        String rowStyleFieldName = "selectPolInsRiskGridRowStyle";

        if (StringUtils.isBlank(officialRecordId) && "TEMP".equals(recordModeCode)) {
            record.setFieldValue(rowStyleFieldName, tempRowStyle);
        }
        return true;
    }
}
