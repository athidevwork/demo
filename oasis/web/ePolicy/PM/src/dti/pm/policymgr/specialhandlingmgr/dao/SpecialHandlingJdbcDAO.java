package dti.pm.policymgr.specialhandlingmgr.dao;

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
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 16, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/12/2007       JMP         Remove invalid mapping of column note to notes
 * ---------------------------------------------------
 */

public class SpecialHandlingJdbcDAO extends BaseDAO implements SpecialHandlingDAO {

    /**
     * Retrieves all special handling information.
     *
     * @param record              input record
     * @param recordLoadProcessor an instance of the load processor to set page entitlements
     * @return recordSet
     */
    public RecordSet loadAllSpecialHandlings(Record record, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllSpecialHandlings", new Object[]{record, recordLoadProcessor});
        }

        RecordSet rs;
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Special_Handling", mapping);
            rs = spDao.execute(record, recordLoadProcessor);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllSpecialHandlings", rs);
            }
            return rs;

        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load special handling information", e);
            l.throwing(getClass().getName(), "loadAllSpecialHandlings", ae);
            throw ae;
        }
    }

    /**
     * Save all special handlings' information.
     *
     * @param inputRecords intput record
     * @return the number of row updateds
     */
    public int saveAllSpecialHandlings(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllSpecialHandlings");

        int updateCount = 0;

        /* Create DataRecordMappig for this stored procedure */
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("polSpecHandId", "polSpecialHandlingId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("specHandCode", "specialHandlingCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "effectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExpDate", "effectiveToDate"));

        // Insert the records in batch mode with 'Pm_Save_Screens.Save_Pol_Spec_Handling'
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Save_Screens.Save_Pol_Spec_Handling", mapping);
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save special handlings.", e);
            l.throwing(getClass().getName(), "saveAllSpecialHandlings", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllSpecialHandlings", new Integer(updateCount));
        }
        return updateCount;
    }

    /**
     * Find if configuration is there in pm_attribute to make it editable.
     *
     * @param inputRecord              input record
     * @return String a String indicating whether editable based on configuration.
     */
    public String getEditableConfiguration(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());

        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getEditableConfiguration", new Object[]{inputRecord});
        }

        // get the return value
        String returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy.Is_Special_Handling_Editable");
        try {
            returnValue = spDao.execute(inputRecord).getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get special handling configuration ", e);
            l.throwing(getClass().getName(), "getEditableConfiguration", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getEditableConfiguration", returnValue);
        }
        return returnValue;
    }
}