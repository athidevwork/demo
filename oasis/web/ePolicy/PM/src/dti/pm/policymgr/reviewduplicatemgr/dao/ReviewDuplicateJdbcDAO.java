package dti.pm.policymgr.reviewduplicatemgr.dao;

import dti.oasis.app.AppException;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.dao.BaseDAO;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.reviewduplicatemgr.ReviewDuplicateFields;
import dti.pm.policymgr.taxmgr.TaxFields;
import dti.pm.transactionmgr.TransactionFields;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Action class for Renewal Candidate.
 * <p/>
 *
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:   June 28, 2016
 *
 * @author ssheng
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/28/2016        ssheng      164927 - Created this action class for Quote Import enhancement.
 * 03/13/2018        tzeng       189424 - Added validateRosterRisk to get all invalid types per one roster risk.
 * ---------------------------------------------------
 */
public class ReviewDuplicateJdbcDAO extends BaseDAO implements ReviewDuplicateDAO {
     /**
     * Load all roster risks
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllRosterRisk(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRosterRisk", new Object[]{inputRecord});
        }

        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Quick_Quote.PM_Sel_Load_Roster_Risk");
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load roster risks", e);
            l.throwing(getClass().getName(), "loadAllRosterRisk", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRosterRisk", rs);
        }

        return rs;
    }

    /**
     * Load all CIS Match entity
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllCISDuplicate(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllCISDuplicate", new Object[]{inputRecord});
        }

        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Quick_Quote.Load_CIS_Duplicates");
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load CIS entity", e);
            l.throwing(getClass().getName(), "loadAllCISDuplicate", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllCISDuplicate", rs);
        }

        return rs;
    }

    /**
     * Add new entity to CIS
     *
     * @param inputRecords
     * @return
     */
    public void savePopulateToCIS(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "savePopulateToCIS", inputRecords);
        }

        DataRecordMapping mapping = new DataRecordMapping();
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Quick_Quote.Pm_Populate_CIS", mapping);
        try {
            spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to save Populate To CIS.", e);
            l.throwing(getClass().getName(), "savePopulateToCIS", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "savePopulateToCIS");
        }
    }

    /**
     * Use CIS entity
     *
     * @param inputRecords
     * @return
     */
    public void saveUseCISRecord(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveUseCISRecord", inputRecords);
        }

        DataRecordMapping mapping = new DataRecordMapping();
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Quick_Quote.Use_Cis_Entity", mapping);
        try {
            spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to save Use CIS Record.", e);
            l.throwing(getClass().getName(), "saveUseCISRecord", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveUseCISRecord");
        }
    }

    /**
     * Get the maximum expiration date of manual tax by specific term.
     *
     * @param inputRecord input Record
     * @return validation message
     */
    public String validateRisk(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validateRisk", new Object[]{inputRecord});

        String returnMessage;
        try {
            DataRecordMapping mapping = new DataRecordMapping();

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Quick_Quote.Validate_risk", mapping);
            Record output = spDao.executeUpdate(inputRecord);
            returnMessage = output.getStringValue("retMsg");

            l.exiting(getClass().getName(), "validateRisk", returnMessage);
            return returnMessage;
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to validate risk.", e);
            l.throwing(getClass().getName(), "validateRisk", ae);
            throw ae;
        }
    }

    /**
     * Validate no process review duplicate
     *
     * @param inputRecord
     * @return
     */
    public String validateReviewDuplicate(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validateReviewDuplicate", new Object[]{inputRecord});

        String returnValue =null;
        try {
            DataRecordMapping mapping = new DataRecordMapping();

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Quick_Quote.Validate_Review_Dup_Existed", mapping);
            returnValue = spDao.executeUpdate(inputRecord).getStringValue("retMsg");

            l.exiting(getClass().getName(), "validateReviewDuplicate", returnValue);
            return returnValue;
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to validate review duplicate.", e);
            l.throwing(getClass().getName(), "validateReviewDuplicate", ae);
            throw ae;
        }
    }

    /**
     * Get all invalid types of CIS information.
     *
     * @param inputRecord input Record
     * @return invalidateTypes
     */
    public String validateCISInfo(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validateCISInfo", new Object[]{inputRecord});

        String invalidateTypes;
        try {
            DataRecordMapping mapping = new DataRecordMapping();

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Quick_Quote.Validate_CIS_Info", mapping);
            Record output = spDao.executeUpdate(inputRecord);
            invalidateTypes = output.getStringValue("invalidateTypes");

            l.exiting(getClass().getName(), "validateCISInfo", invalidateTypes);
            return invalidateTypes;
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to validate CIS info.", e);
            l.throwing(getClass().getName(), "validateCISInfo", ae);
            throw ae;
        }
    }
}
