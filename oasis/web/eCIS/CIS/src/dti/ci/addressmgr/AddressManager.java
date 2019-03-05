package dti.ci.addressmgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

/**
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * User: cyzhao
 * Date: Dec 13, 2010
 */
/*
 *
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/11/2011       kshen       Added method updateAddressDetailForBulkModify,
 *                              loadAddressDetailInfoForAddAddressCopy,
 *                              and loadAddressDetailInfoForBulkModifyAddress for issue 99502.
 * 03/05/2018       dzhang      Issue 109177: Add methods for vendor address refactor
 * ---------------------------------------------------
 */
public interface AddressManager {

    public Record getFieldDefaultValues(Record inputRecord);

    /**
     * Load all Address Search Add List.
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadAddressSearchAddList(Record inputRecord);

    /**
     * Save all data for Address Search Add List
     *
     * @param inputRecords
     */
    public void updateAddressSearchAddList(RecordSet inputRecords);

    /**
     * Load the Vendor Address Type Info.
     *
     * @return
     */
    public Record loadVendorAddressTypeInfo();

    /**
     * for a given address pk, return the address data
     *
     * @param inputRecord
     * @return
     */
    public Record loadAddressDetailInfo(Record inputRecord);

    /**
     * save Address Detail Info
     *
     * @param inputRecord
     */
    public Record updateAddressDetailInfo(Record inputRecord);

    /**
     * save Address Detail Info for web service
     *
     * @param inputRecord
     */
    public Record updateAddressDetailInfoForWS(Record inputRecord);

    /**
     * Bulk modify address.
     * @param inputRecord
     * @return
     */
    public void updateAddressDetailForBulkModify(Record inputRecord);

    /**
     * Load entity vendor address
     *
     * @param inputRecord
     * @return
     */
    public Record loadVendorAddress(Record inputRecord);

    /**
     * Save vendor address
     *
     * @param inputRecord
     */
    public Record saveVendorAddress(Record inputRecord);

    /**
     * save address detail information
     *
     * @param inputRecord
     */
    public void saveAddressDetail(Record inputRecord);

    /**
     * moved here from old AddressExpireManager
     *
     * @param inputRecord
     * @return
     */
    public Record loadAddressForExpire(Record inputRecord);

    /**
     * moved here from old AddressExpireManager
     *
     * @param inputRecord
     */
    public void expireNonPrimaryAddress(Record inputRecord);

    /**
     * moved here from old AddressExpireManager
     *
     * @param inputRecord
     */
    public void expireNonPrimaryAddressForWS(Record inputRecord);

    /**
     * Gets relations list for address copy, moved here from AddressCopyManager
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadEntityRelation(Record inputRecord);

    /**
     * Copy Address for selected entity, moved here from AddressCopyManager
     *
     * @param inputRecords
     */
    public void performAddressCopy(RecordSet inputRecords);

    /**
     * moved here from old AddressRoleChgManager
     *
     * @param inputRecord
     * @param loadProcessor
     * @return
     */
    public RecordSet loadChangeAddressRoles(Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * moved here from old AddressRoleChgManager
     *
     * @param inputRecord
     */
    public void performTransferAddressRoles(Record inputRecord);

    /**
     * moved here from old AddressRoleChgManager
     *
     * @param inputRecord
     */
    public void updateAddressRoles(Record inputRecord);

    /**
     * moved here from old AddressListManager
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadEffectAddressList(Record inputRecord);
}
