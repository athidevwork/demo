package dti.ci.priorcarriermgr.impl;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.AddSelectIndLoadProcessor;
import dti.oasis.util.SysParmProvider;

/**
 * The load processor class for Prior Carrier.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   3/25/14
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class PriorCarrierLoadProcessor implements RecordLoadProcessor {
    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     * @return true if this Record should be added to the RecordSet;
     *         false if this Record should be excluded from the RecordSet.
     */
    @Override
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        AddSelectIndLoadProcessor.getInstance().postProcessRecord(record, rowIsOnCurrentPage);
        String CI_PRIOR_CARRIER_ADT = SysParmProvider.getInstance().getSysParm("CI_PRIOR_CARRIER_ADT", "N");
        if (CI_PRIOR_CARRIER_ADT.equals("Y")) {
            record.setFieldValue("char5", record.getStringValue("char5", "N"));
        }
        return true;
    }

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    @Override
    public void postProcessRecordSet(RecordSet recordSet) {
        AddSelectIndLoadProcessor.getInstance().postProcessRecordSet(recordSet);
    }
}
