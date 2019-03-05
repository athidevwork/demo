package dti.pm.acctlookupmgr.dao;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;

/**
 * An interface that provides DAO operation for the Account Lookup Manager.
 * </p>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 14, 2007
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
public interface AccountLookupDAO {

    /**
     * Method that returns a list of billing accounts.
     * <p/>
     *
     * @param inputRecord Record contains input values
     * @return Record containing the billing accounts based on the input criteria
     */
    public RecordSet loadAllAccount(Record inputRecord);
}
