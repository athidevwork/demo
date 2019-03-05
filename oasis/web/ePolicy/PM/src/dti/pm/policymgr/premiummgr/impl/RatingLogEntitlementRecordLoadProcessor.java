package dti.pm.policymgr.premiummgr.impl;

import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.busobjs.YesNoFlag;
import dti.pm.policymgr.premiummgr.PremiumAccountingFields;
import dti.pm.policymgr.premiummgr.PremiumFields;

import java.util.logging.Logger;

/**
 * This class extends the default record load processor to enforce entitlements for the Rating Log web page. This
 * class works in conjunction with pageEntitlements.xml configuration.
 * <p/>
 * <p>Any field that requires entitlement enforcement is determined by an indicator field that gets appended to the list
 * of fields in a record. This record is published as xml data island, which then gets intercepted by pageEntitlements
 * oasis tag to auto-generate javascript that enforces the entitlements.</p>
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   July 11, 2007
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/01/2011       ryzhao      118806 - Do refactoring to move PremiumFields to dti.pm.policymgr.premiummgr package.
 * 12/31/2014       awu         159667 - Modified postProcessRecordSet to set 'ALL' to Coverage drop-down.
 *  ---------------------------------------------------
 */
public class RatingLogEntitlementRecordLoadProcessor implements RecordLoadProcessor {

    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     * @return true if this Record should be added to the RecordSet;
     *         false if this Record should be excluded from the RecordSet.
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        return true;
    }

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "postRatingLogProcessRecordSet", new Object[]{recordSet});
       //process page entitlement
        Record summaryRecord = recordSet.getSummaryRecord();
        if ((recordSet.getSize()) <= 0) {
                summaryRecord.setFieldValue(PremiumFields.HAS_RATING_LOG_DATA_FOR_TRANSACTION, YesNoFlag.N);
         } else{
               summaryRecord.setFieldValue(PremiumFields.HAS_RATING_LOG_DATA_FOR_TRANSACTION, YesNoFlag.Y);
         }
        // If no term or risk is selected, system should select "All" automatically.
        if (!getInputRecord().hasStringValue(PremiumAccountingFields.TERM_BASE_RECORD_ID)) {
            PremiumAccountingFields.setTermBaseRecordId(summaryRecord, "-1");
        }

        if (!getInputRecord().hasStringValue(PremiumAccountingFields.RISK_BASE_RECORD_ID)) {
            PremiumAccountingFields.setRiskBaseRecordId(summaryRecord, "-1");
        }

        if (!getInputRecord().hasStringValue(PremiumAccountingFields.COVERAGE_BASE_RECORD_ID)) {
            PremiumAccountingFields.setCoverageBaseRecordId(summaryRecord, "-1");
        }

        l.exiting(getClass().getName(), "postRatingLogProcessRecordSet");
    }

    public RatingLogEntitlementRecordLoadProcessor(Record inputRecord) {
        setInputRecord(inputRecord);
    }

    public Record getInputRecord() {
        return m_inputRecord;
    }

    public void setInputRecord(Record inputRecord) {
        m_inputRecord = inputRecord;
    }

    private Record m_inputRecord;
}
