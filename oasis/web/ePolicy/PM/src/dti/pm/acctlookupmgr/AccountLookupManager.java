package dti.pm.acctlookupmgr;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;

/**
 * An interface to handle CRUD operation on Account Lookup.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   March 14, 2007
 *
 * @author sxm
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface AccountLookupManager {

    /**
     * Method that returns a list of billing accounts.
     * <p/>
     *
     * @param inputRecord Record contains input values
     * @return RecordSet containing the billing accounts based on the input criteria
     */
    public RecordSet loadAllAccount(Record inputRecord);
}
