package dti.ci.addressmgr.impl;

import dti.ci.addressmgr.AddressFields;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.SysParmProvider;

/**
 * The load processor class for loading address detail info.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 10, 2011
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/07/2014       hxk         Issue 136502
 * ---------------------------------------------------
 */
public class AddressDetailLoadProcessor implements RecordLoadProcessor {
    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     * @return true if this Record should be added to the RecordSet;
     *         false if this Record should be excluded from the RecordSet.
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        if (!SysParmProvider.getInstance().getSysParm("COUNTRY_CODE_CAN", "CAN").equalsIgnoreCase(AddressFields.getCountryCode(record)))
            AddressFields.setOtherProvince(record, AddressFields.getProvince(record));
        return true;
    }

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
    }
}
