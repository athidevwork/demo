package dti.pm.policymgr.userviewmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * An interface that provides DAO operation for user view.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   July 27, 2007
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public interface UserViewDAO {

    /**
     * load user view info.
     *
     * @param record
     * @return
     */
    RecordSet loadUserView(Record record);

    /**
     * save user view info
     *
     * @param inputRecord
     * @return
     */
    Record saveUserView(Record inputRecord);

    /**
     * delete user view from database
     *
     * @param inputRecord
     * @return
     */
    void deleteUserView(Record inputRecord);

    /**
     * validate additional sql
     * 
     * @param inputRecord
     * @return
     */
    String validateAdditionalSql(Record inputRecord);
}
