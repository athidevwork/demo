package dti.pm.policymgr.creditstackmgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * Interface to handle Implementation of CreditStackingManager.
 * <p/>
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
public interface CreditStackingManager {

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
     * @return RecordSet
     */
    public RecordSet loadAllAppliedInformation(Record inputRecord);
}
