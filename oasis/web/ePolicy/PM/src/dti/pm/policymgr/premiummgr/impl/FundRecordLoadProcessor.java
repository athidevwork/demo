package dti.pm.policymgr.premiummgr.impl;

import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.pm.policymgr.premiummgr.PremiumFields;
import dti.pm.transactionmgr.TransactionFields;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
 * This class extends the default record load processor .
 * <p/>
 * <p>Any field that requires entitlement enforcement is determined by an indicator field that gets appended to the list
 * of fields in a record. This record is published as xml data island, which then gets intercepted by pageEntitlements
 * oasis tag to auto-generate javascript that enforces the entitlements.</p>
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   August 16, 2007
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/01/2011       ryzhao      118806 - Modified postProcessRecordSet() to set summary records.
 *                                       Added private methods addCoverageTotalRecord(), addRiskTotalRecord(),
 *                                       addTransTotalRecord() to support postProcessRecordSet(). 
 * 08/04/2011       ryzhao      118806 - Merge the three new added private methods together.
 * 07/19/2013       adeng       146439 - Modified addTotalRecord() to set deltaB of coverage total and risk total as
 *                                       the source record's deltaB.
 * ---------------------------------------------------
 */
public class FundRecordLoadProcessor implements RecordLoadProcessor {

    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     * @return true if this Record should be added to the RecordSet;
     *         false if this Record should be excluded from the RecordSet.
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        if ("pmfm".equalsIgnoreCase(record.getStringValue("componentCode"))) {
            record.setFieldValue("componentCode", "");
        }

        return true;
    }

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        int size = recordSet.getSize();
        if (size == 1) {
            Record rec = recordSet.getRecord(0);
            // Add Coverage Total Record
            addTotalRecord(COVERAGE_TOTAL_CODE, recordSet, rec, COVERAGE_TOTAL_TITLE, 2);
            // Add Risk Total Record
            addTotalRecord(RISK_TOTAL_CODE, recordSet, rec, RISK_TOTAL_TITLE, 3);
            // Add Transaction Total Record
            addTotalRecord(TRANSACTION_TOTAL_CODE, recordSet, rec, TRANSACTION_TOTAL_TITLE, 4);
        }
        else if (size > 1) {
            RecordSet returnRs = new RecordSet();
            Iterator rsIter = recordSet.getRecords();

            // Add first record in recordSet
            Record preRec = (Record) rsIter.next();
            returnRs.addRecord(preRec);
            String preRecRiskId = PremiumFields.getRiskId(preRec);
            String preRecCovgId = PremiumFields.getCoverageId(preRec);
            String preRecParentCovgId = PremiumFields.getParentCovgId(preRec);

            int recordId = 2;
            // Loop from the second record in recordSet
            while (rsIter.hasNext()) {
                Record currRec = (Record) rsIter.next();
                String currRecRiskId = PremiumFields.getRiskId(currRec);
                String currRecCovgId = PremiumFields.getCoverageId(currRec);
                String currRecParentCovgId = PremiumFields.getParentCovgId(currRec);
                // Add Total record before adding current record if necessary
                if (!currRecCovgId.equals(preRecCovgId)) {
                    if (preRecCovgId.equals(preRecParentCovgId)) {
                        // Add Coverage Total Record
                        addTotalRecord(COVERAGE_TOTAL_CODE, returnRs, preRec, COVERAGE_TOTAL_TITLE, recordId++);
                    }
                    else {
                        // Add Sub Coverage Total Record
                        addTotalRecord(SUB_COVERAGE_TOTAL_CODE, returnRs, preRec, SUB_COVERAGE_TOTAL_TITLE, recordId++);
                    }
                }
                if (!currRecRiskId.equals(preRecRiskId)) {
                    // Add Risk Total Record
                    addTotalRecord(RISK_TOTAL_CODE, returnRs, preRec, RISK_TOTAL_TITLE, recordId++);
                }
                // Add current record
                PremiumFields.setRecordId(currRec, String.valueOf(recordId++));
                returnRs.addRecord(currRec);
                // Set current record to pre record for next loop
                preRec = currRec;
                preRecRiskId = currRecRiskId;
                preRecCovgId = currRecCovgId;
                preRecParentCovgId = currRecParentCovgId;
            }

            // Add total records after the last coverage record
            addTotalRecord(COVERAGE_TOTAL_CODE, returnRs, preRec, COVERAGE_TOTAL_TITLE, recordId++);
            addTotalRecord(RISK_TOTAL_CODE, returnRs, preRec, RISK_TOTAL_TITLE, recordId++);
            addTotalRecord(TRANSACTION_TOTAL_CODE, returnRs, preRec, TRANSACTION_TOTAL_TITLE, recordId++);

            Record sumRec = recordSet.getSummaryRecord();
            List fieldNames = (List) ((ArrayList) recordSet.getFieldNameList()).clone();
            recordSet.clear();
            recordSet.addRecords(returnRs);
            recordSet.addFieldNameCollection(fieldNames);
            recordSet.setSummaryRecord(sumRec);
        }
    }

    /**
     * Add a coverage/risk/transaction total record to the destination record set
     *
     * @param destRecSet Destination record set
     * @param srcRec     Record include the total amount
     */
    private void addTotalRecord(String totalCode, RecordSet destRecSet, Record srcRec, String title, int recordId) {
        Record totalRec = new Record();
        Iterator fnIter = srcRec.getFieldNames();
        while (fnIter.hasNext()) {
            totalRec.setFieldValue((String) fnIter.next(), "");
        }
        PremiumFields.setComponentCode(totalRec, title);
        PremiumFields.setRecordId(totalRec, String.valueOf(recordId));
        TransactionFields.setTransactionLogId(totalRec, TransactionFields.getTransactionLogId(srcRec));
        if(totalCode.equals(COVERAGE_TOTAL_CODE) || totalCode.equals(SUB_COVERAGE_TOTAL_CODE)){
            PremiumFields.setRiskId(totalRec, PremiumFields.getRiskId(srcRec));
            PremiumFields.setWrittenPremium(totalRec, PremiumFields.getCoverageAmountTotal(srcRec));
            PremiumFields.setDeltaAmount(totalRec, PremiumFields.getCoverageDeltaTotal(srcRec));
            PremiumFields.setDeltaB(totalRec, PremiumFields.getDeltaB(srcRec));
        }
        else if(totalCode.equals(RISK_TOTAL_CODE)){
            PremiumFields.setRiskId(totalRec, PremiumFields.getRiskId(srcRec));
            PremiumFields.setWrittenPremium(totalRec, PremiumFields.getRiskAmountTotal(srcRec));
            PremiumFields.setDeltaAmount(totalRec, PremiumFields.getRiskDeltaTotal(srcRec));
            PremiumFields.setDeltaB(totalRec, PremiumFields.getDeltaB(srcRec));
        }
        else if(totalCode.equals(TRANSACTION_TOTAL_CODE)){
            PremiumFields.setWrittenPremium(totalRec, PremiumFields.getTransAmountTotal(srcRec));
            PremiumFields.setDeltaAmount(totalRec, PremiumFields.getTransDeltaTotal(srcRec));
        }
        destRecSet.addRecord(totalRec);
    }

    private static final String COVERAGE_TOTAL_CODE = "COVERAGE";
    private static final String COVERAGE_TOTAL_TITLE = "Coverage Total";
    private static final String SUB_COVERAGE_TOTAL_CODE = "SUB_COVERAGE";
    private static final String SUB_COVERAGE_TOTAL_TITLE = "Sub Coverage Total";
    private static final String RISK_TOTAL_CODE = "RISK";
    private static final String RISK_TOTAL_TITLE = "Risk Total";
    private static final String TRANSACTION_TOTAL_CODE = "TRANSACTION";
    private static final String TRANSACTION_TOTAL_TITLE = "Transaction Total";

}
