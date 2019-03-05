package dti.pm.transactionmgr.premiumadjustmentprocessmgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.transactionmgr.premiumadjustmentprocessmgr.PremiumAdjustmentFields;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class extends the default record load processor to enforce entitlements for the premium adjustment web page.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   July 15, 2008
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 *
 * ---------------------------------------------------
 */

public class PremiumAdjustmentEntitlementRecordLoadProcessor implements RecordLoadProcessor {

    /**
     * Process the given record after it's been loaded to enforce page entitlements.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        return true;
    }

    /**
     * Process the RecordSet for enforcing page entitlements after all records have been loaded and processed.
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        //If no record is fetched, add the page entitlement fields to the recordset field collection
        Iterator coverageItor = getCoverageRs().getRecords();
        int id = -3000;
        while (coverageItor.hasNext()) {
            Record coverageRecord = (Record) coverageItor.next();
            if (isAddNewRecordRequired(coverageRecord, recordSet)) {
                Record newRecord = getInitialValuesForPremiumAdjustment(getPolicyHeader(), coverageRecord, id);
                id = id - 1;
                recordSet.addRecord(newRecord);
            }
        }
    }

    /**
     * determine if new record should be added
     *
     * @param coverageRecord
     * @param premiumAdjustmentRs
     * @return
     */
    private boolean isAddNewRecordRequired(Record coverageRecord, RecordSet premiumAdjustmentRs) {
        boolean result = true;
        String coverageBaseRecordId = coverageRecord.getStringValue("coverageBaseRecordId");
        String status = coverageRecord.getStringValue("status");
        Iterator itor = premiumAdjustmentRs.getRecords();
        while (itor.hasNext()) {
            Record premiumAdjustment = (Record) itor.next();
            if (coverageBaseRecordId.equals(premiumAdjustment.getStringValue("coverageBaseRecordId"))) {
                if ((status.equals("CANCEL")) && (!StringUtils.isBlank(premiumAdjustment.getStringValue("officialRecordId")))) {
                    premiumAdjustment.setDisplayIndicator(YesNoFlag.N);
                    result = true;
                }
                else {
                    result = false;
                    break;
                }
            }

        }
        return result;
    }

    /**
     * get intial values for premium adjustment
     *
     * @param policyHeader
     * @param inputRecord
     * @param id
     * @return
     */
    private Record getInitialValuesForPremiumAdjustment(PolicyHeader policyHeader, Record inputRecord, int id) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForPremiumAdjustment", new Object[]{policyHeader, inputRecord});
        }
        //get default record from workbench
        Record output = new Record();
        PremiumAdjustmentFields.setPolicyCovComponentId(output, (id + ""));
        PremiumAdjustmentFields.setComponentTypeCode(output, "ADJUSTMENT");
        PremiumAdjustmentFields.setShortDescription(output, "Premium Debit");
        PremiumAdjustmentFields.setComponentValue(output, null);
        PremiumAdjustmentFields.setComponentSign(output, "1");
        PremiumAdjustmentFields.setOfficialRecordId(output, null);
        PremiumAdjustmentFields.setBaseRecordB(output, null);
        PremiumAdjustmentFields.setRecordModeCode(output, "TEMP");
        PremiumAdjustmentFields.setCoverageBaseRecordId(output, inputRecord.getStringValue("coverageBaseRecordId"));
        PremiumAdjustmentFields.setProductCoverageCode(output, inputRecord.getStringValue("covCode"));
        PremiumAdjustmentFields.setPolCovCompBaseRecId(output, null);
        PremiumAdjustmentFields.setTransactionLogId(output, null);
        l.exiting(getClass().getName(), "getInitialValuesForPremiumAdjustment");
        return output;
    }


    public PremiumAdjustmentEntitlementRecordLoadProcessor() {
    }

    public PremiumAdjustmentEntitlementRecordLoadProcessor(PolicyHeader policyHeader, RecordSet coverageRs) {
        this.policyHeader = policyHeader;
        this.coverageRs = coverageRs;
    }

    private PolicyHeader policyHeader;
    private RecordSet coverageRs;

    public PolicyHeader getPolicyHeader() {
        return policyHeader;
    }

    public void setPolicyHeader(PolicyHeader policyHeader) {
        this.policyHeader = policyHeader;
    }

    public RecordSet getCoverageRs() {
        return coverageRs;
    }

    public void setCoverageRs(RecordSet coverageRs) {
        this.coverageRs = coverageRs;
    }
}
