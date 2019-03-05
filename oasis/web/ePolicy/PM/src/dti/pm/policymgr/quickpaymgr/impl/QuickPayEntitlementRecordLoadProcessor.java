package dti.pm.policymgr.quickpaymgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * This class extends the default record load processor to enforce entitlements for risk web page. This class works in
 * conjunction with pageEntitlements.xml configuration.
 * <p>
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 30, 2010
 *
 * @author dzhang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/19/2010       dzhang      Update per Bill's comments.
 * 09/09/2010       dzhang      #103800 - Added logic when no data in transaction history gird, hidden all action items.
 * ---------------------------------------------------
 */

public class QuickPayEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {

    /**
     * Process the given record after it's been loaded to enforce page entitlements.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "postProcessRecord", new Object[]{record, String.valueOf(rowIsOnCurrentPage)});
        }

        YesNoFlag isRemoveQPAvailable = YesNoFlag.N;
        YesNoFlag isGiveQPPercentAvailable = YesNoFlag.N;
        YesNoFlag isGiveQPAmountAvailable = YesNoFlag.N;
        YesNoFlag isQPDetailAvailable = YesNoFlag.Y;

        if (!StringUtils.isBlank(record.getStringValue("qpAmount")) &&
            !StringUtils.isBlank(record.getStringValue("qpEligAmount"))) {
            float qpAmount = record.getFloatValue("qpAmount");
            float qpEligAmount = record.getFloatValue("qpEligAmount");

            if (!hasWipData() && (qpAmount == 0) && (qpEligAmount != 0)) {
                isGiveQPPercentAvailable = YesNoFlag.Y;
                isGiveQPAmountAvailable =  YesNoFlag.Y;
            }

            if (!hasWipData() && (qpAmount != 0)) {
                isRemoveQPAvailable = YesNoFlag.Y;
            }

            if (hasWipData() && (qpAmount == 0) && (qpEligAmount == 0)) {
                isQPDetailAvailable = YesNoFlag.N;
            }
        }

        record.setFieldValue("isRemoveQPAvailable", isRemoveQPAvailable);
        record.setFieldValue("isGiveQPPercentAvailable", isGiveQPPercentAvailable);
        record.setFieldValue("isGiveQPAmountAvailable", isGiveQPAmountAvailable);
        record.setFieldValue("isQPDetailAvailable", isQPDetailAvailable);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "postProcessRecord");
        }
        return true;
    }

    /**
     * Process the RecordSet for enforcing page entitlements after all records have been loaded and processed.
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "postProcessRecordSet", new Object[]{recordSet});
        }
        // If no record is fetched, add the page entitlement fields to the recordset field collection
        if (recordSet.getSize() == 0) {
            recordSet.getSummaryRecord().setFieldValue("isTransDetailAvailable", YesNoFlag.N);
            recordSet.getSummaryRecord().setFieldValue("isSaveAvailable", YesNoFlag.N);
            ArrayList pageEntitlementFields = new ArrayList();
            pageEntitlementFields.add("isGiveQPAmountAvailable");
            pageEntitlementFields.add("isGiveQPPercentAvailable");
            pageEntitlementFields.add("isRemoveQPAvailable");
            pageEntitlementFields.add("isQPDetailAvailable");
            recordSet.addFieldNameCollection(pageEntitlementFields);
        }
        else {
            recordSet.getSummaryRecord().setFieldValue("isTransDetailAvailable", YesNoFlag.Y);
            recordSet.getSummaryRecord().setFieldValue("isSaveAvailable", YesNoFlag.Y);   
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "postProcessRecordSet");
        }
    }

    public QuickPayEntitlementRecordLoadProcessor(Record inputRecord, String wipQpTransId) {
        setInputRecord(inputRecord);
        setWipQpTransId(wipQpTransId);
    }

    private boolean hasWipData() {
        boolean hasWipData = false;
        if (!StringUtils.isBlank(getWipQpTransId())) {
            long longWipQpTransId = Long.parseLong(getWipQpTransId());
            if (longWipQpTransId > 0) {
                hasWipData = true;
            }
        }

        return hasWipData;
    }

    public void setInputRecord(Record inputRecord) {
        this.m_inputRecord = inputRecord;
    }

    public String getWipQpTransId() {
        return m_wipQpTransId;
    }

    public void setWipQpTransId(String wipQpTransId) {
        this.m_wipQpTransId = wipQpTransId;
    }

    public Record getInputRecord() {
        return m_inputRecord;
    }

    private String m_wipQpTransId;
    private Record m_inputRecord;
}
