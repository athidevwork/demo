package dti.pm.riskmgr.impl;

import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyFields;
import dti.pm.policymgr.PolicyHeaderFields;

import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 16, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class SelCompInsRiskRelEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {
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
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecordSet", new Object[]{recordSet});

        RecordSet newRecords = new RecordSet();
        String policyId = getPolicyHeader().getPolicyId();
        int rsSize = recordSet.getSize();
        for (int i = 0; i < rsSize; i++) {
            Record r = recordSet.getRecord(i);
            if(!policyId.equals(PolicyHeaderFields.getPolicyId(r))){
                newRecords.addRecord(r);
            }
        }
        Record summaryRec = recordSet.getSummaryRecord();
        List fieldNames = (List) ((ArrayList) recordSet.getFieldNameList()).clone();
        recordSet.clear();
        recordSet.addRecords(newRecords);
        recordSet.addFieldNameCollection(fieldNames);
        recordSet.setSummaryRecord(summaryRec);

        l.exiting(getClass().getName(), "postProcessRecordSet");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
    }

    public SelCompInsRiskRelEntitlementRecordLoadProcessor(PolicyHeader policyHeader) {
        setPolicyHeader(policyHeader);
    }

    public PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    public void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }

    private PolicyHeader m_policyHeader;
}
