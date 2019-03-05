package dti.ci.addressmgr.addresslistmgr.dao;

import dti.ci.core.CIFields;
import dti.ci.core.dao.BaseDAO;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.SysParmProvider;

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
 *         07/25/2011       kshen       Added method changePrimaryAddress.
 *         08/09/2011       kshen       Changed for issue 123063.
 *         12/26/2014       Elvin       Issue 157520: add isValidStateAndCounty
 *         05/30/2016       dpang       Issue 149588: retrieve hub data if needed.
 *         09/21/2016       ylu         Issue 179400: update primary address
 *         -------------------------------------------------------------------
 */

public class AddressListJdbcDAO extends BaseDAO implements AddressListDAO {

    private final Logger l = LogUtils.getLogger(getClass());

    @Override
    public RecordSet loadAddressList(Record inputRecord, RecordLoadProcessor loadProcessor) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAddressList", new Object[]{inputRecord, loadProcessor});
        }

        RecordSet rs = null;
        boolean isHubEnabled = "Y".equalsIgnoreCase(SysParmProvider.getInstance().getSysParm("CI_ENABLE_HUB", "N"));
        StoredProcedureDAO spDao = null;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping(isHubEnabled? "entityId" : CIFields.ID, CIFields.PK));
        spDao = StoredProcedureDAO.getInstance(isHubEnabled ? "Ci_Web_Address_Base_H.sel_address_list" : "CI_Web_Address.Sel_Address_List", mapping);

        try {
            rs = spDao.execute(inputRecord, loadProcessor);
        } catch (SQLException e) {
            handleSQLException(e, "ci.generic.error", getClass().getName(), "loadAddressList");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAddressList", rs);
        }
        return rs;
    }

    @Override
    public Record getEntityLockFlag(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getEntityLockFlag", new Object[]{inputRecord});
        }

        Record outRecord = null;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping(CIFields.ID, CIFields.PK));
        StoredProcedureDAO spDAO = StoredProcedureDAO.getInstance("CI_Web_Address.Get_Entity_Lock_Flag", mapping);

        try {
            outRecord = spDAO.execute(inputRecord).getSummaryRecord();
        } catch (SQLException e) {
            handleSQLException(e, "ci.generic.error", getClass().getName(), "getEntityLockFlag");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getEntityLockFlag", outRecord);
        }
        return outRecord;
    }

    @Override
    public RecordSet loadPrimaryAddress(Record inputRecord, RecordLoadProcessor loadProcessor) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadPrimaryAddress", new Object[]{inputRecord, loadProcessor});
        }

        RecordSet rs = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CI_Web_Address.Sel_Primary_Address");

        try {
            rs = spDao.execute(inputRecord, loadProcessor);
        } catch (SQLException e) {
            handleSQLException(e, "ci.generic.error", getClass().getName(), "loadPrimaryAddress");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadPrimaryAddress", rs);
        }
        return rs;
    }

    @Override
    public void changePrimaryAddress(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "changePrimaryAddress", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CI_Web_Address.Common_Address_Update");

        try {
            spDao.executeUpdate(inputRecord);
        } catch (SQLException e) {
            handleSQLException(e, "ci.generic.error", getClass().getName(), "changePrimaryAddress");
        }

        l.exiting(getClass().getName(), "changePrimaryAddress");
    }

    @Override
    public int saveAllAddress(RecordSet inputRecords) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllAddress", new Object[]{inputRecords});
        }

        int updateCount = 0;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CI_Web_Address.Save_Address");

        try {
            updateCount = spDao.executeBatch(inputRecords);
        } catch (SQLException e) {
            handleSQLException(e, "ci.generic.error", getClass().getName(), "saveAllAddress");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllAddress", updateCount);
        }
        return updateCount;
    }

    @Override
    public Record savePrimaryAddress(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "savePrimaryAddress", new Object[]{inputRecord});
        }

        RecordSet outRecord = new RecordSet();
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CI_Web_Address.Update_Address_Detail_Info");

        try {
            outRecord = spDao.execute(inputRecord);
        } catch (SQLException e) {
            handleSQLException(e, "ci.generic.error", getClass().getName(), "savePrimaryAddress");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "savePrimaryAddress", outRecord);
        }
        return outRecord.getSummaryRecord();
    }

    @Override
    public boolean isValidStateAndCounty(String stateCode, String countyCode) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isValidStateAndCounty", new Object[]{stateCode, countyCode});
        }

        boolean successFlag = false;
        Record inputRecord = new Record();
        inputRecord.setFieldValue("stateCode", stateCode);
        inputRecord.setFieldValue("countyCode", countyCode);
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CI_Web_Address.Is_Valid_State_County");

        try {
            RecordSet rs = spDao.execute(inputRecord);
            successFlag = YesNoFlag.getInstance(rs.getSummaryRecord().getStringValue("returnValue")).booleanValue();
        } catch (SQLException e) {
            handleSQLException(e, "ci.generic.error", getClass().getName(), "isValidStateAndCounty");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isValidStateAndCounty", successFlag);
        }
        return successFlag;
    }
}
