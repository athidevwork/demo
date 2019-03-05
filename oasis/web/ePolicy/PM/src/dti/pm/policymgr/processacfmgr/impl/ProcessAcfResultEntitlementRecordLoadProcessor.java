package dti.pm.policymgr.processacfmgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.StringUtils;
import dti.pm.entitlementmgr.EntitlementFields;
import dti.pm.policymgr.processacfmgr.ProcessAcfFields;
import dti.pm.riskmgr.RiskFields;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class extends the default record load processor to enforce entitlements for policy web page. This class works in
 * conjunction with pageEntitlements.xml configuration.
 * <p/>
 * <p>Any field that requires entitlement enforcement is determined by an indicator field that gets appended to the list
 * of fields in a record. This record is published to request as request attributes, which then gets intercepted by
 * pageEntitlements oasis tag to auto-generate java script that enforces the entitlements.</p>
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 31, 2011
 *
 * @author syang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/13/2013       adeng       144987 - Modified postProcessRecord() method to count TIV and WP once per transaction
 *                              and risk. Because enhancement 137009 is working at the risk level instead of policy
 *                              level.
 * 01/21/2014       adeng       148215 - Modified postProcessRecord() method to parse the sum of column to double type
 *                              instead of float type.
 * ---------------------------------------------------
 */
public class ProcessAcfResultEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {

    /**
     * Process the given record after it's been loaded to enforce page entitlements.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        // The totals fields are the sum of the allocation and commission amounts for each row.
        if (!StringUtils.isBlank(ProcessAcfFields.getRtAllocAmt(record))) {
            ALLOC_AMT_TOTAL += Double.parseDouble(ProcessAcfFields.getRtAllocAmt(record));
        }
        if (!StringUtils.isBlank(ProcessAcfFields.getRtCommAmt(record))) {
            COMM_AMT_TOTAL += Double.parseDouble(ProcessAcfFields.getRtCommAmt(record));
        }
        // For the Transactional TIV and Transaction Written Premium columns the totals are of the amount once per transaction.
        // As an example, if 4 rows exist, the TIV and WP will only be counted once in the total.
        String s = ProcessAcfFields.getTransactionLogId(record) + "," + RiskFields.getRiskId(record);
        if (!m_transWPMap.containsKey(s)) {
            m_transWPMap.put(s, s);
            if (!StringUtils.isBlank(ProcessAcfFields.getDeltaAmt(record))) {
                TRANS_WP_TOTAL += Double.parseDouble(ProcessAcfFields.getDeltaAmt(record));
            }

        }
        if (!m_transTIVMap.containsKey(s)) {
            m_transTIVMap.put(s, s);
            if (!StringUtils.isBlank(ProcessAcfFields.getTransactionalTiv(record))) {
                TRANS_TIV_TOTAL += Double.parseDouble(ProcessAcfFields.getTransactionalTiv(record));
            }
        }

        return true;
    }

    /**
     * Process the RecordSet for enforcing page entitlements after all records have been loaded and processed.
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        Record summaryRecord = recordSet.getSummaryRecord();
        // Set the total fields for result grid.
        ProcessAcfFields.setAllocAmtTotal(summaryRecord, String.valueOf(ALLOC_AMT_TOTAL));
        ProcessAcfFields.setCommAmtTotal(summaryRecord, String.valueOf(COMM_AMT_TOTAL));
        ProcessAcfFields.setTransTivTotal(summaryRecord, String.valueOf(TRANS_TIV_TOTAL));
        ProcessAcfFields.setTransWPTotal(summaryRecord, String.valueOf(TRANS_WP_TOTAL));
    }

    /**
     * Constructor method
     */
    public ProcessAcfResultEntitlementRecordLoadProcessor() {
        TRANS_WP_TOTAL = 0;
        TRANS_TIV_TOTAL = 0;
        COMM_AMT_TOTAL = 0;
        ALLOC_AMT_TOTAL = 0;
        m_transTIVMap.clear();
        m_transWPMap.clear();
    }

    private static double ALLOC_AMT_TOTAL = 0;
    private static double COMM_AMT_TOTAL = 0;
    private static double TRANS_TIV_TOTAL = 0;
    private static double TRANS_WP_TOTAL = 0;
    private static Map m_transTIVMap = new HashMap();
    private static Map m_transWPMap = new HashMap();
}