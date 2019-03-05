package dti.pm.transactionmgr.batchrenewalprocessmgr.impl;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.busobjs.YesNoFlag;
import dti.pm.transactionmgr.batchrenewalprocessmgr.BatchRenewalFields;

import java.util.Iterator;
import java.util.logging.Logger;
import java.util.ArrayList;

/**
 * This class extends the default record load processor to enforce entitlements for Batch Renewal Detail web page. This class works in
 * conjunction with pageEntitlements.xml configuration.
 * <p/>
 * <p>Any field that requires entitlement enforcement is determined by an indicator field that gets appended to the list
 * of fields in a record. This record is published as xml data island, which then gets intercepted by pageEntitlements
 * oasis tag to auto-generate javascript that enforces the entitlements.</p>
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 5, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/04/2007       fcb         postProcessRecordSet: logic added for 0 size recordSet.
 * 03/12/2013       adeng       Modified postProcessRecordSet() to set a indicator for all policies excluded or not into
 *                              the summary record of the recordSet when the selected event code is PRERENEWAL.
 * 08/27/2014       kxiang      a.Modified postProcessRecord, removed ExcludeB logical codes.
 *                              b.Modified postProcessRecordSet,changed compared ExcludeB value in if condition.
 * ---------------------------------------------------
 */
public class BatchRenewalDetailEntitlementRecordLoadProcessor implements RecordLoadProcessor {

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

        // Exclude
        if (getProcessCode()!=null && getProcessCode().equals(BatchRenewalFields.ProcessCodeValues.PRERENEWAL)) {
            record.setFieldValue("isExcludeAvailable", YesNoFlag.Y);
        }
        else {
            record.setFieldValue("isExcludeAvailable", YesNoFlag.N);
        }

        l.exiting(getClass().getName(), "postProcessRecord");
        return true;
    }

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        if (recordSet.getSize() == 0) {
            ArrayList pageEntitlementFields = new ArrayList();
            pageEntitlementFields.add("isExcludeAvailable");
            recordSet.addFieldNameCollection(pageEntitlementFields);
        }
        if (getProcessCode() != null && getProcessCode().equals(BatchRenewalFields.ProcessCodeValues.PRERENEWAL)) {
            YesNoFlag isAllExcluded = YesNoFlag.Y;
            Iterator recIter = recordSet.getRecords();
            while (recIter.hasNext()) {
                Record record = (Record) recIter.next();
                if (!BatchRenewalFields.getExcludeB(record).equals("Y")) {
                    isAllExcluded = YesNoFlag.N;
                    break;
                }
            }
            recordSet.getSummaryRecord().setFieldValue(BatchRenewalFields.IS_ALL_EXCLUDED, isAllExcluded);
        }
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
    }

    public BatchRenewalDetailEntitlementRecordLoadProcessor() {
    }

    public BatchRenewalDetailEntitlementRecordLoadProcessor(String processCode) {
        setProcessCode(processCode);
    }


    public String getProcessCode() {
        return processCode;
    }

    public void setProcessCode(String processCode) {
        this.processCode = processCode;
    }

    private String processCode;
}
