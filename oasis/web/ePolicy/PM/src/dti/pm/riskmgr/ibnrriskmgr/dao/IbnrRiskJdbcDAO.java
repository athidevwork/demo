package dti.pm.riskmgr.ibnrriskmgr.dao;

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
 * This class implements the IbnrRiskDAO interface. This is consumed by any business logic objects
 * that requires information about one or more IBNR risks.
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 07, 2011
 *
 * @author Dzhang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 
 * ---------------------------------------------------
 */
public class IbnrRiskJdbcDAO extends BaseDAO implements IbnrRiskDAO {

    /**
     * Returns a RecordSet loaded with list of available risk types for the provided
     * policy information.
     *
     * @param inputRecord Record contains input values
     * @return RecordSet a RecordSet loaded with list of available risk types.
     */
    public RecordSet loadAllIbnrRiskType(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllIbnrRiskType", new Object[]{inputRecord});
        }

        // Create the input data mapping
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transEffDate", "transEffectiveFromDate"));
        // Execute query
        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Ibnr_Risk.Sel_Ibnr_Risks", mapping);
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load selectable Associated Risk list", e);
            l.throwing(getClass().getName(), "loadAllIbnrRiskType", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllIbnrRiskType", rs);
        }
        return rs;
    }

    /**
     * Change associated risk
     *
     * @param inputRecord Record contains input values
     */
    public void processChangeAssociatedRisk(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "processChangeAssociatedRisk", new Object[]{inputRecord});

        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Ibnr_Risk.Change_Associated_Risk");
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to process change associated.", e);
            l.throwing(getClass().getName(), "processChangeAssociatedRisk", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "processChangeAssociatedRisk", new Object[]{rs});
    }

    /**
     * load all the risks exist in a given policy that have IBNR inactive risks associated to them
     *
     * @param inputRecord input records that contains key information
     * @return  a RecordSet loaded with list of available associated risk data.
     */
    public RecordSet loadAllAssociatedRisk(Record inputRecord, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAssociatedRisk", new Object[]{inputRecord});
        }

        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("Pm_Web_Ibnr_Risk.Sel_All_Associated_Risk");
        RecordSet outRecordSet = null;
        try {
            outRecordSet = sp.execute(inputRecord, loadProcessor);
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Error when executing loadAllAssociatedRisk", se);
            l.throwing(getClass().getName(), "loadAllAssociatedRisk", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAssociatedRisk", outRecordSet);
        }
        return outRecordSet;
    }

    /**
     * load all the risks exist in a given policy that have IBNR inactive risks associated to them
     *
     * @param inputRecord input records that contains key information
     * @return  a RecordSet loaded with list of available inactive risk data.
     */
    public RecordSet loadAllIbnrInactiveRisk(Record inputRecord, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllIbnrInactiveRisk", new Object[]{inputRecord});
        }

        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("Pm_Web_Ibnr_Risk.Sel_All_Ibnr_Inactive_Risk");
        RecordSet outRecordSet = null;
        try {
            outRecordSet = sp.execute(inputRecord, loadProcessor);
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Error when executing loadAllIbnrInactiveRisk", se);
            l.throwing(getClass().getName(), "loadAllIbnrInactiveRisk", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllIbnrInactiveRisk", outRecordSet);
        }
        return outRecordSet;
    }

    /**
     * load all the risks exist in a given policy that have IBNR inactive risks associated to them
     *
     * @param inputRecord input records that contains key information
     * @return a RecordSet loaded with list of available associated risk for inactive risk data.
     */
    public RecordSet loadAllAssociatedRiskForIbnrInactiveRisk(Record inputRecord, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAssociatedRiskForIbnrInactiveRisk", new Object[]{inputRecord});
        }

        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("Pm_Web_Ibnr_Risk.Sel_Asso_Risk_For_Ina_Risk");
        RecordSet outRecordSet = null;
        try {
            outRecordSet = sp.execute(inputRecord,loadProcessor);
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Error when executing loadAllAssociatedRiskForIbnrInactiveRisk", se);
            l.throwing(getClass().getName(), "loadAllAssociatedRiskForIbnrInactiveRisk", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAssociatedRiskForIbnrInactiveRisk", outRecordSet);
        }
        return outRecordSet;
    }

    /**
     * Save all data in IBNR Inactive Risk page
     *
     * @param inputRecords a record set with data to be saved
     */
    public void saveAllInactiveRisk(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllInactiveRisk", new Object[]{inputRecords});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Ibnr_Risk.Save_Inactive_Risk");
        try {
            spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save ibnr inactive risk.", e);
            l.throwing(getClass().getName(), "saveAllInactiveRisk", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllInactiveRisk");
        }
    }

    /**
     * Cancel IBNR active risk
     *
     * @param inputRecord Record contains input values
     */
    public void processCancelActiveIbnrRisk(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "processCancelActiveIbnrRisk", new Object[]{inputRecord});

        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Ibnr_Risk.Cancel_Active_Ibnr_Risk");
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to process cancel active IBNR active risk.", e);
            l.throwing(getClass().getName(), "processCancelActiveIbnrRisk", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "processCancelActiveIbnrRisk", new Object[]{rs});
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public IbnrRiskJdbcDAO() {
    }
}