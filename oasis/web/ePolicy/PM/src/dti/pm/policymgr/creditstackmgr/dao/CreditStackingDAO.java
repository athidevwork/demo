package dti.pm.policymgr.creditstackmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

/**
 * An interface that extends the ViewCreditStacking interface to provide DAO operation.
 * </p>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   May 26, 2011
 *
 * @author syang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface CreditStackingDAO {

    /**
     * Retrieve header information.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllHeaderInformation(Record inputRecord);

    /**
     * Retrieve applied information.
     *
     * @param inputRecord
     * @param recordLoadProcessor
     * @return RecordSet
     */
    public RecordSet loadAllAppliedInformation(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

}
