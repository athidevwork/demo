package dti.pm.policymgr.limitsharingmgr.dao;

import dti.oasis.app.AppException;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.dao.BaseDAO;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the LimitSharingDAO interface.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 10, 2007
 *
 * @author rlli
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/17/2010       fcb         97217  - validateSharedLimitGroup added.
 * 01/12/2011       dzhang     113568 - Added loadAllSharedLimit().
 * ---------------------------------------------------
 */


public class LimitSharingJdbcDAO extends BaseDAO implements LimitSharingDAO {


    /**
     * load all shared group
     *
     * @param inputRecord   (policy_id, current endorsement quote id, term_effective_from_date,
     *                      term_effective_to_date,record mode, transactionLogId, current term base record id)
     * @param loadProcessor
     * @return shared group record set
     */
    public RecordSet loadAllSharedGroup(Record inputRecord, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllSharedGroup", new Object[]{inputRecord});
        RecordSet rs = null;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("endQuoteId", "endorsementQuoteId"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Policy_Shared_Group.Sel_Shared_Group_Info", mapping);
        try {
            rs = spDao.execute(inputRecord, loadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load shared group information ", e);
            l.throwing(getClass().getName(), "loadAllSharedGroup", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllSharedGroup", rs);
        }
        return rs;
    }

    /**
     * load all shared group detail
     *
     * @param inputRecord   (policy_id, current endorsement quote id, term_effective_from_date,
     *                      term_effective_to_date,record mode, transactionLogId)
     * @param loadProcessor
     * @return shared group detail record set
     */
    public RecordSet loadAllSharedGroupDetail(Record inputRecord, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllSharedGroupDetail", new Object[]{inputRecord});
        RecordSet rs = null;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("endQuoteId", "endorsementQuoteId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Policy_Shared_Group.Sel_Shared_Group_Detail_Info", mapping);
        try {
            rs = spDao.execute(inputRecord, loadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load shared group detail information", e);
            l.throwing(getClass().getName(), "loadAllSharedGroupDetail", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllSharedGroupDetail", rs);
        }
        return rs;
    }

    /**
     * load all separate limit by policy_id, current endorsement quote id, term_effective_from_date,
     * term_effective_to_date,record mode, transactionLogId, current term base record id.
     *
     * @param inputRecord
     * @return shared group detail record set
     */
    public RecordSet loadAllSeparateLimit(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllSeparateLimit", new Object[]{inputRecord});
        RecordSet rs = null;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("endQuoteId", "endorsementQuoteId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Policy_Shared_Group.Sel_Separate_Limit_Info", mapping);
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load separate limit information", e);
            l.throwing(getClass().getName(), "loadAllSeparateLimit", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllSeparateLimit", rs);
        }
        return rs;
    }

    /**
     * load available shared detail list
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllAvailableSharedDetail(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAvailableSharedDetail", new Object[]{inputRecord, recordLoadProcessor});
        }
        RecordSet rs;
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Policy_Shared_Group.Add_Risk_Covg", mapping);
            rs = spDao.execute(inputRecord, recordLoadProcessor);
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllAvailableSharedDetail", rs);
            }
            return rs;

        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load available shared detail information", e);
            l.throwing(getClass().getName(), "loadAllAvailableSharedDetail", ae);
            throw ae;
        }
    }

    /**
     * delete all shared detail
     *
     * @param inputRecords
     * @return updateCount
     */
    public int deleteAllSharedDetail(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "deleteAllSharedDetail", new Object[]{inputRecords});

        int updateCount = 0;
        // Create a DataRecordMapping for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("shareGrpDetId", "policyShareGroupDtlId"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Policy_Shared_Group.Delete_Shared_Group_Dtl", mapping);
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to delete shared group detail.", e);
            l.throwing(getClass().getName(), "deleteAllSharedDetail", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "deleteAllSharedDetail", new Integer(updateCount));
        return updateCount;
    }

    /**
     * save all shared detail
     *
     * @param inputRecords
     * @return updateCount
     */
    public int saveAllSharedDetail(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllSharedDetail", new Object[]{inputRecords});

        int updateCount = 0;

        // Create a DataRecordMapping for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("polGrpDtlId", "policyShareGroupDtlId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("polGrpMasterId", "shareDtlGroupMasterId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transactionLogId", "shareDtlTransLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("sourceId", "shareDtlSourceRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("sourceTable", "shareDtlSourceTableName"));
        mapping.addFieldMapping(new DataRecordFieldMapping("ownerB", "shareDtlOwnerB"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effDt", "shareDtlEffFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expDt", "shareDtlEffToDate"));

        mapping.addFieldMapping(new DataRecordFieldMapping("grpExp", "groupExpirationDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termExpirationDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("renewB", "shareDtlRenewalB"));
        // Version the records in batch mode with 'Pm_Endorse.Change_Coverage'
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Policy_Shared_Group.Save_Shared_Group_Dtl", mapping);
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save shared detail.", e);
            l.throwing(getClass().getName(), "saveAllSharedDetail", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "saveAllSharedDetail", new Integer(updateCount));
        return updateCount;
    }

    /**
     * delete all shared group
     *
     * @param inputRecords
     * @return updateCount
     */
    public int deleteAllSharedGroup(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "deleteAllSharedGroup", new Object[]{inputRecords});

        int updateCount = 0;
        // Create a DataRecordMapping for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("shareGrpId", "policySharedGroupMasterId"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Policy_Shared_Group.Delete_Shared_Group", mapping);
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to delete shared group.", e);
            l.throwing(getClass().getName(), "deleteAllSharedGroup", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "deleteAllSharedGroup", new Integer(updateCount));
        return updateCount;
    }

    /**
     * save all shared group
     *
     * @param inputRecords
     * @return updateCount
     */
    public int saveAllSharedGroup(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllSharedGroup", new Object[]{inputRecords});

        int updateCount = 0;
        // Create a DataRecordMapping for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("polGrpId", "policySharedGroupMasterId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("polGrpNo", "shareGroupNo"));
        mapping.addFieldMapping(new DataRecordFieldMapping("productGrpId", "productSharedGroupMasterId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transactionLogId", "shareGroupTransLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effDt", "shareGroupEffFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expDt", "shareGroupEffToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("groupDesc", "shareGroupDesc"));
        mapping.addFieldMapping(new DataRecordFieldMapping("sharedDeductB", "shareDeductB"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termExpirationDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("renewB", "renewalB"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Policy_Shared_Group.Save_Shared_Group", mapping);
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save shared detail.", e);
            l.throwing(getClass().getName(), "saveAllSharedDetail", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "saveAllSharedDetail", new Integer(updateCount));
        return updateCount;

    }

    /**
     * Get validation info for Deductive
     *
     * @param inputRecord
     * @return transactionId
     */
    public String validateShareGroupDeduct(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validateShareGroupDeduct", new Object[]{inputRecord});

        String returnValue;

        try {
            // Execute the stored procedure
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Policy_Shared_Group.Web_Validate_Deduct_Share");
            Record output = spDao.executeUpdate(inputRecord);
            returnValue = output.getStringValue("retMsg");

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Pm_Policy_Shared_Group.Web_Validate_Deduct_Share", e);
            l.throwing(getClass().getName(), "validateShareGroupDeduct", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "validateShareGroupDeduct", returnValue);
        return returnValue;
    }

    /**
     * Get validation info for SIR
     *
     * @param inputRecord
     * @return transactionId
     */
    public String validateShareGroupSir(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validateShareGroupSir", new Object[]{inputRecord});
        String returnValue;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Policy_Shared_Group.Web_Val_Share_SIR");
            Record output = spDao.executeUpdate(inputRecord);
            returnValue = output.getStringValue("retMsg");
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Pm_Policy_Shared_Group.Web_Val_Share_SIR", e);
            l.throwing(getClass().getName(), "validateShareGroupSir", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "validateShareGroupSir", returnValue);
        return returnValue;
    }

    /**
     * validate sir is visible or not.
     *
     * @param inputRecord (policyId,currentTransactionEffFromDate)
     * @return String (Y or N)
     */
    public String validateSirVisibility(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validateSirVisibility", new Object[]{inputRecord});
        String returnValue;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Policy_Shared_Group.Validate_Sir_Visibility");
            returnValue = spDao.execute(inputRecord).getSummaryRecord().getStringValue(
                StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Pm_Policy_Shared_Group.Validate_Sir_Visibility", e);
            l.throwing(getClass().getName(), "validateSirVisibility", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "validateSirVisibility", returnValue);
        return returnValue;
    }

    /**
     * Generic validation for Shared Limit.
     *
     * @param inputRecord (sharedGroupId,sourceId,policyId,transactionId)
     * @return String (Y or N)
     */
    public String validateSharedLimitGroup(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validateSharedLimitGroup", new Object[]{inputRecord});
        String returnValue;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Policy_Shared_Group.Val_Shared_Limit_Group");
            Record output = spDao.executeUpdate(inputRecord);
            returnValue = output.getStringValue("retMsg");
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Pm_Policy_Shared_Group.Val_Shared_Limit_Group", e);
            l.throwing(getClass().getName(), "validateSharedLimitGroup", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "validateSharedLimitGroup", returnValue);
        return returnValue;
    }

    /**
     * load available shared limit detail list
     *
     * @param inputRecord
     * @return recordSet
     */
    public RecordSet loadAllSharedLimit(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllSharedLimit", new Object[]{inputRecord});
        }
        RecordSet rs;
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Sel_Shared_Limit_Info", mapping);
            rs = spDao.execute(inputRecord);
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllSharedLimit", rs);
            }
            return rs;

        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load available shared limit detail information", e);
            l.throwing(getClass().getName(), "loadAllSharedLimit", ae);
            throw ae;
        }
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public LimitSharingJdbcDAO() {
    }
}
