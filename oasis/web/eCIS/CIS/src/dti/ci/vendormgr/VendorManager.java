package dti.ci.vendormgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * <p>(C) 2018 Delphi Technology, inc. (dti)</p>
 * Date:   4/3/2018
 *
 * @author jdingle
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface VendorManager {
    /**
     * Get Vendor data for an entity.
     * @param  inputRecord
     * @return RecordSet
     */
    public RecordSet loadVendor(Record inputRecord);

    /**
     * Get Vendor Address data for an entity.
     * @param  inputRecord
     * @return RecordSet
     */
    public RecordSet loadVendorAddress(Record inputRecord);

    /**
     * Get Vendor Payment data for an entity.
     * @param  inputRecord
     * @return RecordSet
     */
    public RecordSet loadVendorPayment(Record inputRecord);

    /**
     * Save Entity Training data.
     * @param  inputRecord
     * @return Record
     */
    public Record saveVendor(Record inputRecord);
}
