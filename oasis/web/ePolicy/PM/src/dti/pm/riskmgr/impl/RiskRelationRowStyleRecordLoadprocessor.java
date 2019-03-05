package dti.pm.riskmgr.impl;

import dti.oasis.app.ApplicationContext;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.util.StringUtils;
import dti.pm.riskmgr.RiskRelationFields;

/**
 * Row style load processor for risk relation
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   8/28/2015
 *
 * @author tzeng
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/28/2015       Tzeng       set the newly saved risk relation records in blue color.
 * ---------------------------------------------------
 */

public class RiskRelationRowStyleRecordLoadprocessor extends DefaultRecordLoadProcessor {

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
        String recordModeCode = record.getStringValue(RiskRelationFields.RECORD_MODE_CODE);
        String officialRecordId = record.getStringValue(RiskRelationFields.OFFICIAL_RECORD_ID);
        String rowStyleFieldName = "maintainRiskRelationListGridRowStyle";

        if (StringUtils.isBlank(officialRecordId) && "TEMP".equals(recordModeCode)) {
            record.setFieldValue(rowStyleFieldName, tempRowStyle);
        }
        return true;
    }

}
