package dti.pm.policymgr.underwritermgr.dao;

import dti.oasis.app.AppException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.dao.BaseDAO;
import dti.pm.policymgr.PolicyFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JDBC dao for underwriter
 * <p/>
 * <p>(C) 2006 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 19, 2006
 *
 * @author Bhong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/20/2013       awu         141924 - Added isUnderwriterEntity
 * 06/05/2013        awu        138241 - Add loadAllUnderwriterTeamMember, getUnderwriterTeam
 * 09/07/2015       awu         164026 - Added loadAllUnderwriters without any page entitlement processor.
 * 04/24/2018       xnie        192517 - Added validateDuplicateUnderwriters.
 * ---------------------------------------------------
 */
public class UnderwriterJdbcDAO extends BaseDAO implements UnderwriterDAO {

    /**
     * Returns a RecordSet loaded with list of available risks for the provided
     * policy information.
     * <p/>
     *
     * @param inputRecord record with policy key fields.
     * @param recordLoadProcessor an instance of data load processor
     * @return RecordSet a RecordSet loaded with list of available risks.
     */

    /**
     * Get list of underwriters
     *
     * @param record              input records
     * @param recordLoadProcessor an instance of the load processor to set page entitlements
     * @return RecordSet
     */
    public RecordSet loadAllUnderwriters(Record record, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllUnderwriters", new Object[]{record, recordLoadProcessor});
        }

        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Sel_Underwriter");
            rs = spDao.execute(record, recordLoadProcessor);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllUnderwriters", rs);
            }
            return rs;

        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load underwriter information", e);
            l.throwing(getClass().getName(), "loadAllUnderwriters", ae);
            throw ae;
        }
    }

    /**
     * Save all given input records with the Pm_Save_Screens.Save_Underwriter stored procedure,
     * inserting information.
     *
     * @param inputRecords a set of Records, each with the PolicyHeader, PolicyIdentifier,
     *                     and Underwriter Detail info matching the fields returned from the loadTermUnderwriters method..
     * @return the number of rows inserted.
     */
    public int addAllUnderwriters(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "addAllUnderwriters", new Object[]{inputRecords});

        int updateCount = 0;

        // Create a DataRecordMapping for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("effectiveDate", "effectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expirationDate", "effectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExpDate", "termEffectiveToDate"));

        // Insert the records in batch mode with 'Pm_Save_Screens.Save_Undwriter'
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Save_Screens.Save_Undwriter", mapping);
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save inserted underwriters.", e);
            l.throwing(getClass().getName(), "addAllUnderwriters", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addAllUnderwriters", new Integer(updateCount));
        }
        return updateCount;
    }

    /**
     * Save all given input records with the Pm_Save_Screens.Save_Underwriter stored procedure,
     * updating the existing information.
     *
     * @param inputRecords a set of Records, each with the PolicyHeader, PolicyIdentifier,
     *                     and Underwriter Detail info matching the fields returned from the loadTermUnderwriters method..
     * @return the number of rows updated.
     */
    public int updateAllUnderwriters(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "updateAllUnderwriters", new Object[]{inputRecords});

        int updateCount = 0;

        // Create a DataRecordMapping for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("effectiveDate", "effectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expirationDate", "effectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExpDate", "termEffectiveToDate"));

        // Insert the records in batch mode with 'Pm_Save_Screens.Save_Undwriter'
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Save_Screens.Save_Undwriter", mapping);
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save updated underwriters.", e);
            l.throwing(getClass().getName(), "updateAllUnderwriters", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "updateAllUnderwriters", new Integer(updateCount));
        }
        return updateCount;
    }

    public Record loadAdditionalPolicyInfo(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAdditionalPolicyInfo", new Object[]{policyHeader});
        }

        RecordSet rs;
        try {
            Record input = new Record();
            input.setFieldValue(PolicyHeaderFields.POLICY_ID, policyHeader.getPolicyId());

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Sel_Underwriter_Pol_Info");
            rs = spDao.execute(input);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAdditionalPolicyInfo", rs);
            }
            return rs.getFirstRecord();

        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load additional information for policyNo:"
                    + policyHeader.getPolicyNo(), e);
            l.throwing(getClass().getName(), "loadAdditionalPolicyInfo", ae);
            throw ae;
        }
    }

    /**
     * Saves additional policy information
     *
     * @param inputRecords
     */
    public void saveAdditionalPolicyInfo(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAdditionalPolicyInfo", new Object[]{inputRecords});
        }

        // Setup the input record
        Record input = inputRecords.getSummaryRecord();

        // Update the record with Pm_Web_Underwriter.Save_Addl_Policy_Info
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Underwriter.Save_Addl_Policy_Info");
        try {
            Record output = spDao.executeUpdate(input);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to update policy additional information.", e);
            l.throwing(getClass().getName(), "saveAdditionalPolicyInfo", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAdditionalPolicyInfo");
        }
    }

    /**
     * Retrieve all policy info by from underwriter and other search criteria
     *
     * @param inputRecord
     * @return policy list
     */
    public RecordSet loadAllPolicyByUnderwriter(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllPolicyByUnderwriter", new Object[]{inputRecord});
        }
        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy.Sel_Pol_Info_For_Underwriter");
            rs = spDao.execute(inputRecord, recordLoadProcessor);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllPolicyByUnderwriter", rs);
            }
            return rs;
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load policy information by underwriter", e);
            l.throwing(getClass().getName(), "loadAllPolicyByUnderwriter", ae);
            throw ae;
        }
    }

    /**
     * perform transfer Underwriter
     *
     * @param inputRecord
     * @return
     */
    public Record performTransferUnderwriter(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performTransferUnderwriter", new Object[]{inputRecord});
        }
        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Save_Screens.Web_Transfer_Underwriter");
            rs = spDao.execute(inputRecord);
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "performTransferUnderwriter", rs.getSummaryRecord());
            }
            return rs.getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to perform transfer underwriter", e);
            l.throwing(getClass().getName(), "performTransferUnderwriter", ae);
            throw ae;
        }
    }

    public YesNoFlag isUnderwriterEntity(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isUnderwriterEntity", new Object[]{inputRecord});
        }
        YesNoFlag returnValue = YesNoFlag.N;
        RecordSet rs;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "termEffectiveToDate"));
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Underwriter.Is_Valid_Underwriter", mapping);
            rs = spDao.execute(inputRecord);
            String returnFlag = rs.getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD);
            returnValue = YesNoFlag.getInstance(returnFlag);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load underwriter", e);
            l.throwing(getClass().getName(), "isUnderwriterEntity", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isUnderwriterEntity", returnValue);
        }
        return returnValue;
    }

    /**
     * load the team members of the underwriter.
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllUnderwriterTeamMember(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllUnderwriterTeamMember", new Object[]{inputRecord});
        }
        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Underwriter.Sel_Underwriter_Team_Member");
            rs = spDao.execute(inputRecord);
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllUnderwriterTeamMember", rs.getSummaryRecord());
            }
            return rs;
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load the members of underwriter", e);
            l.throwing(getClass().getName(), "loadAllUnderwriterTeamMember", ae);
            throw ae;
        }
    }

    /**
     * get the underwriter's team.
     * @param record
     * @return
     */
    public Record getUnderwriterTeam(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getUnderwriterTeam", new Object[]{record});
        }
        RecordSet outRecSet;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Get_Regional_Team");
        try {
            outRecSet = spDao.execute(record);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to load underwriter's team.", e);
            l.throwing(getClass().getName(), "getUnderwriterTeam", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getUnderwriterTeam");
        }

        return outRecSet.getSummaryRecord();
    }

    /**
     * Load all the underwriters for web service.
     *
     * @param record              input records
     * @return RecordSet
     */
    public RecordSet loadAllUnderwriters(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllUnderwriters", new Object[]{record});
        }

        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Sel_Underwriter");
            rs = spDao.execute(record);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllUnderwriters", rs);
            }
            return rs;

        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load underwriter information", e);
            l.throwing(getClass().getName(), "loadAllUnderwriters", ae);
            throw ae;
        }
    }

    /**
     * Validate duplicate underwriters.
     * @param record
     * @return
     */
    public String validateDuplicateUnderwriters(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateDuplicateUnderwriters", new Object[]{record});
        }

        // Get the return value.
        String returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Underwriter.Val_Duplicate_Underwriters");
        try {
            returnValue = spDao.execute(record).getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to validate duplicate underwriters.", e);
            l.throwing(getClass().getName(), "validateDuplicateUnderwriters", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateDuplicateUnderwriters");
        }
        return returnValue;
    }

    public UnderwriterJdbcDAO() {
    }
}
