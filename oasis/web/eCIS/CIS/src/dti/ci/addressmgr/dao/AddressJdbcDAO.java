package dti.ci.addressmgr.dao;

import dti.ci.addressmgr.AddressFields;
import dti.ci.core.dao.BaseDAO;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.AddSelectIndLoadProcessor;
import dti.oasis.util.LogUtils;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>Data Access Object for Address.</p>
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author Gerald C. Carney
 *         Date:   Jan 22, 2004
 *         <p/>
 *         Revision Date    Revised By  Description
 *         -------------------------------------------------------------------
 *         04/01/2005       HXY         Removed singleton implementation.
 *         Changed Statement to PreparedStatement.
 *         04/14/2005       HXY         Removed commit logic back to BO.
 *         04/22/2005       HXY         return address pk when saving address.
 *         04/27/2005       HXY         Added logic for vendor address page.
 *         09/21/2006       ligj        Issue #62554
 *         02/01/2007       kshen       Issue #61440
 *         (1) Added method retrieveAddressMapForExpire,
 *         retrieveAddressMapForExpire, expireNonPrimaryAddress
 *         for expiring addresses.
 *         (2) Added mehtod buildAddressListStmtWithCountyDesc,
 *         retrieveDataResultSetWithCountyDesc for displaying county
 *         in address list page
 *         08/13/2007       FWCH         Added countryCode,province, postal code
 *         12/03/2007       FWCH         Modified  getVendorAddressSQL() and saveVendorAddressUpdate()
 *         to keep the same logic between retrieving vendor address
 *         and adding vendor address.
 *         09/22/2008       Larry       Issue 86826
 *         10/17/2008       kshen       Changed to load all columns of address data.
 *         11/27/2008       Leo         Issue 88568.
 *         06/23/2009       Fred        Check a string variable is empty or not
 *         before invoking chatAt method
 *         08/27/2009       Leo         Issue 95363
 *         12/26/2011       parker      address type duplicate check for issue 128197
 *         05/02/2013       kshen       Issue 141148.
 *         10/17/2012       jdingle     Issue 179272.
 *         03/05/2018       dzhang      Issue 109177: Add methods for vendor address refactor
 *         -------------------------------------------------------------------
 */

public class AddressJdbcDAO extends BaseDAO implements AddressDAO {

    private final Logger l = LogUtils.getLogger(getClass());

    /**
     * Load all Address Search Add List.
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadAddressSearchAddList(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAddressSearchAddList", new Object[]{inputRecord});
        }

        RecordSet rs = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CI_Web_Address.Sel_Address_Search_Add_List");

        try {
            rs = spDao.execute(inputRecord, AddSelectIndLoadProcessor.getInstance());
        } catch (SQLException e) {
            handleSQLException(e, "ci.generic.error", getClass().getName(), "loadAddressSearchAddList");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAddressSearchAddList", rs);
        }
        return rs;
    }

    /**
     * save all Address Search Add List
     *
     * @param inputRs
     */
    public void updateAddressSearchAddList(RecordSet inputRs) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "updateAddressSearchAddList", new Object[]{inputRs});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CI_Web_Address.Update_Search_Add_Address_List");

        try {
            spDao.executeBatch(inputRs);
        } catch (SQLException e) {
            handleSQLException(e, "ci.generic.error", getClass().getName(), "updateAddressSearchAddList");
        }

        l.exiting(getClass().getName(), "updateAddressSearchAddList");
    }

    /**
     * Load the Vendor Address Type Info.
     *
     * @return
     */
    public RecordSet loadVendorAddressTypeInfo() {
        l.entering(getClass().getName(), "loadVendorAddressTypeInfo");

        RecordSet rs = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CI_Web_Address.Sel_Vendor_Address_Type_Info");

        try {
            rs = spDao.execute(new Record());
        } catch (SQLException e) {
            handleSQLException(e, "ci.generic.error", getClass().getName(), "loadVendorAddressTypeInfo");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadVendorAddressTypeInfo", rs);
        }
        return rs;
    }

    /**
     * for a given address pk, return the address data
     *
     * @param inputRecord
     * @param loadProcessor
     * @return
     */
    public Record loadAddressDetailInfo(Record inputRecord, RecordLoadProcessor loadProcessor) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAddressDetailInfo", new Object[]{inputRecord, loadProcessor});
        }

        Record outRecord = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CI_Web_Address.Sel_Address_Detail_Info");

        try {
            outRecord = spDao.execute(inputRecord, loadProcessor).getFirstRecord();
        } catch (SQLException e) {
            handleSQLException(e, "ci.generic.error", getClass().getName(), "loadAddressDetailInfo");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAddressDetailInfo", outRecord);
        }
        return outRecord;
    }

    /**
     * Get county code for the given USA address city/state/zip
     *
     * @param inputRecord
     * @return
     */
    public Record loadCountyCode(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadCountyCode", new Object[]{inputRecord});
        }

        Record outRecord = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CI_Web_Address.check_county_code");

        try {
            outRecord = spDao.execute(inputRecord).getSummaryRecord();
        } catch (SQLException e) {
            handleSQLException(e, "ci.generic.error", getClass().getName(), "loadCountyCode");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadCountyCode", outRecord);
        }
        return outRecord;
    }

    /**
     * Bulk modify address.
     *
     * @param inputRs
     */
    public void updateAddressDetailForBulkModify(RecordSet inputRs) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "updateAddressDetailForBulkModify", new Object[]{inputRs});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CI_Web_Address.Bulk_Modify_Address");

        try {
            spDao.executeBatch(inputRs);
        } catch (SQLException e) {
            handleSQLException(e, "ci.generic.error", getClass().getName(), "updateAddressDetailForBulkModify");
        }

        l.exiting(getClass().getName(), "updateAddressDetailForBulkModify");
    }

    /**
     * for a given address pk, return the address data for expire
     *
     * @param inputRecord
     * @return
     */
    public Record loadAddressForExpire(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAddressForExpire", new Object[]{inputRecord});
        }

        Record outRecord = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CI_Web_Address.sel_address_for_expire");

        try {
            outRecord = spDao.execute(inputRecord).getFirstRecord();
        } catch (SQLException e) {
            handleSQLException(e, "ci.generic.error", getClass().getName(), "loadAddressForExpire");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAddressForExpire", outRecord);
        }
        return outRecord;
    }


    /**
     * Expire a non-primary address
     *
     * @return
     */
    public void expireNonPrimaryAddress(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "expireNonPrimaryAddress", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("WB_CLIENT_UTILITY.expire_address");

        try {
            spDao.execute(inputRecord);
        } catch (SQLException e) {
            handleSQLException(e, "ci.generic.error", getClass().getName(), "expireNonPrimaryAddress");
        }

        l.exiting(getClass().getName(), "expireNonPrimaryAddress");
    }

    @Override
    public void expireNonPrimaryAddressForWS(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "expireNonPrimaryAddressForWS", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CI_Web_Address.expire_address_for_ws");

        try {
            spDao.execute(inputRecord);
        } catch (SQLException e) {
            handleSQLException(e, "ci.generic.error", getClass().getName(), "expireNonPrimaryAddressForWS");
        }

        l.exiting(getClass().getName(), "expireNonPrimaryAddressForWS");
    }

    /**
     * Load entity vendor address
     * @param inputRecord
     * @return
     */
    @Override
    public Record loadVendorAddress(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadVendorAddress", new Object[]{inputRecord});
        }

        Record outRecord = null;
        StoredProcedureDAO storedProcedureDAO = StoredProcedureDAO.getInstance("CI_Web_Address.SEL_VENDOR_ADDRESS");

        try {
            RecordSet outRecordSet = storedProcedureDAO.execute(inputRecord);
            if (outRecordSet != null && outRecordSet.getSize() > 0) {
                outRecord = outRecordSet.getFirstRecord();
            }
        } catch (SQLException e) {
            handleSQLException(e, "ci.generic.error", getClass().getName(), "loadVendorAddress");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadVendorAddress", outRecord);
        }
        return outRecord;
    }

    /**
     * save vendor address
     * @param inputRecord
     * @return
     */
    @Override
    public Record saveVendorAddress(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveVendorAddress", new Object[]{inputRecord});
        }

        Record outRecord = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CI_Web_Address.save_vendor_address");

        try {
            outRecord = spDao.executeUpdate(inputRecord);
        } catch (SQLException e) {
            handleSQLException(e, "ci.generic.error", getClass().getName(), "saveVendorAddress");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveVendorAddress", outRecord);
        }
        return outRecord;
    }

    @Override
    public Record updateAddressDetailInfo(Record inputRecord, DataRecordMapping dataRecordMapping) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "updateAddressDetailInfo", new Object[]{inputRecord, dataRecordMapping});
        }

        Record outRecord = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CI_Web_Address.Update_Address_Detail_Info", dataRecordMapping);

        try {
            outRecord = spDao.executeUpdate(inputRecord);
        } catch (SQLException e) {
            handleSQLException(e, "ci.generic.error", getClass().getName(), "updateAddressDetailInfo");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "updateAddressDetailInfo", outRecord);
        }
        return outRecord;
    }

    @Override
    public Record updateAddressDetailInfoForWS(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "updateAddressDetailInfoForWS", new Object[]{inputRecord});
        }

        Record outRecord = new Record();
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CI_Web_Address.Update_Addr_Detail_For_WS");

        try {
            outRecord = spDao.executeUpdate(inputRecord);
        } catch (SQLException e) {
            handleSQLException(e, "ci.generic.error", getClass().getName(), "updateAddressDetailInfoForWS");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "updateAddressDetailInfoForWS", outRecord);
        }
        return outRecord;
    }

    @Override
    public RecordSet loadEntityRelation(Record inputRecord, RecordLoadProcessor loadProcessor) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadEntityRelation", new Object[]{inputRecord, loadProcessor});
        }

        RecordSet rs = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CI_Web_Address.Sel_Relation_List");

        try {
            rs = spDao.execute(inputRecord, loadProcessor);
        } catch (SQLException e) {
            handleSQLException(e, "ci.generic.error", getClass().getName(), "loadEntityRelation");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadEntityRelation", rs);
        }
        return rs;
    }

    @Override
    public void performAddressCopy(RecordSet inputRs) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performAddressCopy", new Object[]{inputRs});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CI_Web_Address.Copy_Address");

        try {
            spDao.executeBatch(inputRs);
        } catch (SQLException e) {
            handleSQLException(e, "ci.generic.error", getClass().getName(), "performAddressCopy");
        }

        l.exiting(getClass().getName(), "performAddressCopy");
    }

    @Override
    public RecordSet loadChangeAddressRoles(Record inputRecord, RecordLoadProcessor loadProcessor) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadChangeAddressRoles", new Object[]{inputRecord, loadProcessor});
        }

        RecordSet rs = null;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping(AddressFields.A_ADDRESS_ID, AddressFields.ADDRESS_ID));
        mapping.addFieldMapping(new DataRecordFieldMapping(AddressFields.A_ENTITY_ID, AddressFields.ENTITY_ID));
        mapping.addFieldMapping(new DataRecordFieldMapping(AddressFields.A_PRIMARY_ADDR_CHANGE, AddressFields.PRIMARY_ADDR_CHANGE));
        mapping.addFieldMapping(new DataRecordFieldMapping(AddressFields.A_USE_FOR_CHANGE, AddressFields.USE_FOR_CHANGE));
        mapping.addFieldMapping(new DataRecordFieldMapping(AddressFields.A_USE_FOR_WARNING, AddressFields.USE_FOR_WARNING));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CS_CI_SEL_CHANGE_ADDR_ROLE", mapping);

        try {
            rs = spDao.execute(inputRecord, loadProcessor);
        } catch (SQLException e) {
            handleSQLException(e, "ci.generic.error", getClass().getName(), "loadChangeAddressRoles");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadChangeAddressRoles", rs);
        }
        return rs;
    }

    @Override
    public void performTransferAddressRoles(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performTransferAddressRoles", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CI_Web_Address.Transfer_Address_Roles");

        try {
            spDao.executeUpdate(inputRecord);
        } catch (SQLException e) {
            handleSQLException(e, "ci.generic.error", getClass().getName(), "performTransferAddressRoles");
        }

        l.exiting(getClass().getName(), "performTransferAddressRoles");
    }

    @Override
    public void updateAddressRoles(RecordSet inputRecords) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "updateAddressRoles", new Object[]{inputRecords});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CI_Web_Address.Update_Address_Roles");

        try {
            spDao.executeBatch(inputRecords);
        } catch (SQLException e) {
            handleSQLException(e, "ci.generic.error", getClass().getName(), "updateAddressRoles");
        }

        l.exiting(getClass().getName(), "updateAddressRoles");
    }

    @Override
    public RecordSet loadEffectAddressList(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadEffectAddressList", new Object[]{inputRecord});
        }

        RecordSet outRs = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CI_Web_Address.Sel_Effect_Address_List");

        try {
            outRs = spDao.execute(inputRecord);
        } catch (SQLException e) {
            handleSQLException(e, "ci.generic.error", getClass().getName(), "loadEffectAddressList");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadEffectAddressList", outRs);
        }
        return outRs;
    }
}
