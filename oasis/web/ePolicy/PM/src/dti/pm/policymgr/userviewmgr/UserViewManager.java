package dti.pm.policymgr.userviewmgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * interface to handle operations for user view
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 26, 2007
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
public interface UserViewManager {
   /**
     * Retrieves all user view information
     *
     * @param inputRecord input Record
     * @return RecordSet
     */
    RecordSet loadUserView(Record inputRecord);

    /**
     * save all user view info
     *
     * @param inputRecord
     * @return the number of rows updated
     */
    String saveUserView(Record inputRecord);

    /**
     * delete user view
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
    void validateAdditionalSql(Record inputRecord);
}
