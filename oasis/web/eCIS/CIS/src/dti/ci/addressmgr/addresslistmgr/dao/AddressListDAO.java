package dti.ci.addressmgr.addresslistmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

/**
 * <p>Data Access Object for Address.</p>
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author cyzhao
 *         Date:   Dec 07, 2010
 *         <p/>
 *         Revision Date    Revised By  Description
 *         -------------------------------------------------------------------
 *         07/25/2011       kshen       Added method changePrimaryAddress.
 *         08/09/2011       kshen       Changed for issue 123063.
 *         12/26/2014       Elvin       Issue 157520: add isValidStateAndCounty
 *         09/21/2016       ylu         Issue 179400: update primary address
 *         <p/>
 *         -------------------------------------------------------------------
 */

public interface AddressListDAO {
    /**
     * Load all entity address list
     *
     * @param inputRecord
     * @param loadProcessor
     * @return
     */
    public RecordSet loadAddressList(Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * Get entity lock flag according to Policy
     *
     * @param inputRecord
     * @return Record
     */
    public Record getEntityLockFlag(Record inputRecord);

    /**
     * Load entity primary address
     *
     * @param inputRecord
     * @param loadProcessor
     * @return
     */
    public RecordSet loadPrimaryAddress(Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * Change primary address of an entity.
     * @param inputRecord A record includes entity pk and new primary address pk.
     */
    public void changePrimaryAddress(Record inputRecord);

    /**
     * Update changes of Address to DB
     *
     * @param inputRecords
     * @return
     */
    int saveAllAddress(RecordSet inputRecords);

    /**
     * save Primary address change
     * @param inputRecord
     * @return
     */
    public Record savePrimaryAddress(Record inputRecord);
    /**
     * isValidStateAndCounty
     *
     * @param stateCode
     * @param countyCode
     * @return
     */
    public boolean isValidStateAndCounty(String stateCode, String countyCode);
}
