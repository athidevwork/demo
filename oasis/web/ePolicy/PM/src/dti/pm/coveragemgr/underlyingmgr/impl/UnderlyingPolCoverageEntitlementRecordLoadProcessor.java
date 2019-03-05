package dti.pm.coveragemgr.underlyingmgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.policymgr.PolicyHeader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * This class extends the default record load processor to enforce entitlements for the underlying coverage web page.
 * This class works in conjunction with pageEntitlements.xml configuration.
 * <p/>
 * <p>Any field that requires entitlement enforcement is determined by an indicator field that gets appended to the list
 * of fields in a record. This record is published as xml data island, which then gets intercepted by pageEntitlements
 * oasis tag to auto-generate javascript that enforces the entitlements.</p>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 12, 2018
 *
 * @author wrong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *  10/15/2018       wrong      188391 - Initial version.
 * ---------------------------------------------------
 */
public class UnderlyingPolCoverageEntitlementRecordLoadProcessor implements RecordLoadProcessor {
    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     * @return true if this Record should be added to the RecordSet;
     *         false if this Record should be excluded from the RecordSet.
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecord",
            new Object[]{record, String.valueOf(rowIsOnCurrentPage)});

        record.setEditIndicator(YesNoFlag.N);
        record.setFieldValue("isUnderIssCompEditable", YesNoFlag.N);
        record.setFieldValue("isUnderPolicyNoEditable", YesNoFlag.N);
        record.setFieldValue("isUnderPolicyTypeCodeEditable", YesNoFlag.N);
        record.setFieldValue("isEffectiveFromDateEditable", YesNoFlag.N);
        record.setFieldValue("isEffectiveToDateEditable", YesNoFlag.N);
        record.setFieldValue("isPracticeStateCodeEditable", YesNoFlag.N);
        record.setFieldValue("isOutputBEditable", YesNoFlag.N);
        record.setFieldValue("isUnderCoverageCodeEditable", YesNoFlag.N);
        record.setFieldValue("isPolicyFormCodeEditable", YesNoFlag.N);
        record.setFieldValue("isRetroDateEditable", YesNoFlag.N);
        record.setFieldValue("isCoverageLimitCodeEditable", YesNoFlag.N);
        record.setFieldValue("isUnderRiskTypeEditable", YesNoFlag.N);
        record.setFieldValue("isRenewBEditable", YesNoFlag.N);
        record.setFieldValue("isDeleteAvailable", YesNoFlag.N);
        record.setFieldValue("isCopyAvailable", YesNoFlag.N);

        l.exiting(getClass().getName(), "postProcessRecord");
        return true;
    }

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecordSet", new Object[]{recordSet});

        // If no record is fetched, add the page entitlement fields to the recordset field collection
        if (recordSet.getSize() == 0) {
            ArrayList pageEntitlementFields = new ArrayList();
            pageEntitlementFields.add("isAddCompanyInsuredAvailable");
            pageEntitlementFields.add("isAddNonInsuredAvailable");
            pageEntitlementFields.add("isCopyAvailable");
            pageEntitlementFields.add("isDeleteAvailable");
            pageEntitlementFields.add("isUnderIssCompEditable");
            pageEntitlementFields.add("isUnderCoverageCodeEditable");
            pageEntitlementFields.add("isPracticeStateCodeEditable");
            pageEntitlementFields.add("isUnderPolicyNoEditable");
            pageEntitlementFields.add("isEffectiveFromDateEditable");
            pageEntitlementFields.add("isEffectiveToDateEditable");
            pageEntitlementFields.add("isPolicyFormCodeEditable");
            pageEntitlementFields.add("isUnderPolicyTypeCodeEditable");
            pageEntitlementFields.add("isUnderRiskTypeEditable");
            pageEntitlementFields.add("isCoverageLimitCodeEditable");
            pageEntitlementFields.add("isRetroDateEditable");
            recordSet.addFieldNameCollection(pageEntitlementFields);
        }

        Record summaryRec = recordSet.getSummaryRecord();
        summaryRec.setFieldValue("isAddCompanyInsuredAvailable", YesNoFlag.N);
        summaryRec.setFieldValue("isAddNonInsuredAvailable", YesNoFlag.N);
        summaryRec.setFieldValue("isSaveAvailable", YesNoFlag.N);

        // Filtering:
        // If a record with record mode code = 'TEMP' exists with a non-zero official record id,
        // if a record whose manuscript endorsement primary key equals the official record id is found,
        //  that record is not displayed.
        RecordSet tempRecords = recordSet.getSubSet(new RecordFilter("recordModeCode", "TEMP"));
        RecordSet offRecords = recordSet.getSubSet(new RecordFilter("recordModeCode", "OFFICIAL"));
        Iterator tempIt = tempRecords.getRecords();
        // Loop through records to set official record display_ind to "N", which hides rows in grid
        while (tempIt.hasNext()) {
            Record tempRec = (Record) tempIt.next();
            String sOfficialRecordId = tempRec.getStringValue("officialRecordId");
            Iterator offIt = offRecords.getRecords();
            while (offIt.hasNext()) {
                Record offRecord = (Record) offIt.next();
                String policyUnderlyingCovgId = offRecord.getStringValue("policyUnderlyingCovgId");
                if (policyUnderlyingCovgId.equals(sOfficialRecordId)) {
                    offRecord.setDisplayIndicator("N");
                }
            }
        }
        // Hide the expired official record.
        Iterator offIt = offRecords.getRecords();
        String transactionLogId = getPolicyHeader().getLastTransactionInfo().getTransactionLogId();
        while (offIt.hasNext()) {
            Record offRecord = (Record) offIt.next();
            if (offRecord.hasStringValue("closingTransLogId") && transactionLogId.equals(offRecord.getStringValue("closingTransLogId"))) {
                offRecord.setDisplayIndicator("N");
            }
        }

        l.exiting(getClass().getName(), "postProcessRecordSet");
    }

    public UnderlyingPolCoverageEntitlementRecordLoadProcessor(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }

    public PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    public void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }

    private PolicyHeader m_policyHeader;
}
