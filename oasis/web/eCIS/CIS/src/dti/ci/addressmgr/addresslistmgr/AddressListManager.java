package dti.ci.addressmgr.addresslistmgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * Helper class for Address List.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author Gerald C. Carney
 *         Date:   Jan 23, 2004
 *         <p/>
 *         Revision Date    Revised By  Description
 *         ----------------------------------------------------------------
 *         04/01/2005       HXY         Removed singleton implementation.
 *         04/19/2005       HXY         Created one instance DAO.
 *         02/05/2007       kshen
 *         11/27/2008       Leo         For issue 88568.
 *         07/25/2011       kshen       Added method changePrimaryAddress.
 *         ----------------------------------------------------------------
 */

public interface AddressListManager {

    /**
     * Load all entity address list
     * @param inputRecord
     * @return
     */
    public RecordSet loadAddressList(Record inputRecord);

    /**
     * Get entity lock flag according to Policy
     *
     * @param inputRecord
     * @return Record
     */
    public Record getEntityLockFlag(Record inputRecord);

    /**
     * Get the number of address roles.
     * @param inputRecord p_address_pk
     * @return
     */
    public Record getNumOfAddrRole(Record inputRecord);

    /**
     * Get the number of primary address roles info.
     * @param inputRecord p_entity_pk
     * @return
     */
    public Record getNumOfPrimaryAddrRoleInfo(Record inputRecord);

    /**
     * Change non-primary address to primary
     *
     * @param inputRecord A record includes entity pk and new primary address pk.
     * @return
     */
    public void changePrimaryAddress(Record inputRecord);

    /**
     * Save the changed Address to DB
     *
     * @param inputRecords

     * @return
     */
    int saveAllAddress(RecordSet inputRecords);
}
