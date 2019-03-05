package dti.pm.agentmgr.impl;

import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import java.util.Date;
import java.util.logging.Logger;

/**
 * This class extends the default record load processor.
 * <p/>
 * <p>Any field that requires entitlement enforcement is determined by an indicator field that gets appended to the list
 * of fields in a record. This record is published as xml data island, which then gets intercepted by pageEntitlements
 * oasis tag to auto-generate javascript that enforces the entitlements.
 * This is also used to do special formatting of the fields before displaying them. </p>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb,1 2013
 *
 * @author skommi
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class AgentHistoryRecordLoadProcessor extends DefaultRecordLoadProcessor {

    /**
     * Process the given record after it's been loaded to format based on the basis fields and apply accounting date filter.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecord", new Object[]{record, String.valueOf(rowIsOnCurrentPage)});

        String fRenewalRate = getFormattedRate(record, "renewalCommRate", "renewalCommBasis");
        String fNewBusRate = getFormattedRate(record, "newbusCommRate", "newbusCommBasis");
        String fERERate = getFormattedRate(record, "ereCommRate", "ereCommBasis");
        record.setFieldValue("renewalCommRate", fRenewalRate);
        record.setFieldValue("newbusCommRate", fNewBusRate);
        record.setFieldValue("ereCommRate", fERERate);

        Date today = new Date();
        if (record.getDateValue("accountingFromDate").after(today) ||
            record.getDateValue("accountingToDate").after(today)) {
            return false;
        }
        l.exiting(getClass().getName(), "postProcessRecord");
        return true;
    }

    private String getFormattedRate(Record record, String rate, String basis) {
        String formattedRate = "";
        String basisType = "";
        if (record.hasStringValue(basis)) {
            basisType = record.getStringValue(basis);
        }
        if (basisType.equalsIgnoreCase("PERCENT")) {
            if (record.hasStringValue(rate)) {
                double rateDouble = record.getDoubleValue(rate) / 100.0;
                formattedRate = FormatUtils.formatPercentage(Double.toString(rateDouble), 2);
            }
        }
        else if (basisType.equalsIgnoreCase("FLAT")) {
            int pos = rate.indexOf("Rate");
            String flatString = basis.substring(0, pos) + "FlatAmount";
            if (record.hasStringValue(flatString)) {
                Double dFlatAmt = record.getDoubleValue(flatString);
                formattedRate = FormatUtils.formatCurrency(dFlatAmt, 2);
            }
        }
        else if (basisType.equalsIgnoreCase("SCHED")) {
            int pos1 = rate.indexOf("Rate");
            String schedString = basis.substring(0, pos1) + "ScheduleDesc";
            if (record.hasStringValue(schedString)) {
                formattedRate = record.getStringValue(schedString);
            }
        }
        return formattedRate;
    }

    public AgentHistoryRecordLoadProcessor() {
    }

}
