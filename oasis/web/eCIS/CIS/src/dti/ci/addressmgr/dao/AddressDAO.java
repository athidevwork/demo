package dti.ci.addressmgr.dao;

import dti.oasis.data.DataRecordMapping;
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
 * 08/11/2011       kshen       Added method updateAddressDetailForBulkModify.
 * 03/05/2018       dzhang      Issue 109177: Add methods for vendor address refactor
 * ---------------------------------------------------
 */
public interface AddressDAO {
    /**
     * Load all Address Search Add List.
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadAddressSearchAddList(Record inputRecord);

    /**
     * save all Address Search Add List
     *
     * @param inputRecords
     */
    public void updateAddressSearchAddList(RecordSet inputRecords);

    /**
     * Load the Vendor Address Type Info.
     *
     * @return
     */
    public RecordSet loadVendorAddressTypeInfo();

    /**
     * for a given address pk, return the address data
     *
     * @param inputRecord
     * @param loadProcessor
     * @return
     */
    public Record loadAddressDetailInfo(Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * Get county code for the given USA address city/state/zip
     *
     * @param inputRecord
     * @return
     */
    public Record loadCountyCode(Record inputRecord);

    /**
     * Bulk modify address.
     * @param rs
     * @return
     */
    public void updateAddressDetailForBulkModify(RecordSet rs);

    /**
     * for a given address pk, return the address data for expire
     *
     * @param inputRecord
     * @return
     */
    public Record loadAddressForExpire(Record inputRecord);

    /**
     * Expire a non-primary address
     *
     * @return
     */
    public void expireNonPrimaryAddress(Record inputRecord);

    /**
     * Expire a non-primary address for web service
     *
     * @param inputRecord
     * @throws Exception
     */
    public void expireNonPrimaryAddressForWS(Record inputRecord);

    /**
     * Load entity vendor address information
     * @param inputRecord
     * @return
     */
    public Record loadVendorAddress(Record inputRecord);


    /**
     * save vendor address
     * @param inputRecord
     * @return
     */
    public Record saveVendorAddress(Record inputRecord);

    /**
     * save Address Detail Info
     *
     * @param inputRecord
     */
    public Record updateAddressDetailInfo(Record inputRecord, DataRecordMapping dataRecordMapping);

    /**
     * save Address Detail Info for web service
     *
     * @param inputRecord
     */
    public Record updateAddressDetailInfoForWS(Record inputRecord);

    /**
     * Load the entity relation list info.
     *
     * @param inputRecord
     * @param loadProcessor
     * @return
     */
    public RecordSet loadEntityRelation(Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * Copy Address for selected entity
     *
     * @param inputRecords
     */
    public void performAddressCopy(RecordSet inputRecords);

    /**
     * moved here from old AddressRoleChgDAO
     *
     * @param inputRecord
     * @param loadProcessor
     * @return
     */
    public RecordSet loadChangeAddressRoles(Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * moved here from old AddressRoleChgDAO
     *
     * @param inputRecord
     */
    public void performTransferAddressRoles(Record inputRecord);

    /**
     * moved here from old AddressRoleChgDAO
     *
     * @param inputRecords
     */
    public void updateAddressRoles(RecordSet inputRecords);

    /**
     * moved here from AddressListDAO
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadEffectAddressList(Record inputRecord);
}
