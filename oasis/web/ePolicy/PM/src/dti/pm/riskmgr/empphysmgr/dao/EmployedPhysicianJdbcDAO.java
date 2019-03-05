package dti.pm.riskmgr.empphysmgr.dao;

import dti.pm.core.dao.BaseDAO;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.util.LogUtils;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.sql.SQLException;

/**
 * This Class provides the implementation details of EmployedPhysicianDAO Interface.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 16, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class EmployedPhysicianJdbcDAO extends BaseDAO implements EmployedPhysicianDAO{


    /**
     * load recordset of all Employed Physician infos
     *
     * @param inputRecord
     * @param recordLoadProcessor
     * @return recordset of all Employed Physician infos
     */
    public RecordSet loadAllEmployedPhysician(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllEmployedPhysician", new Object[]{inputRecord});
        }
        RecordSet rs = null;

        DataRecordMapping mapping = new DataRecordMapping();        
        mapping.addFieldMapping(new DataRecordFieldMapping("riskType", "riskTypeCode"));        
        mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "termEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("endQuoteId", "endorsementQuoteId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transTypeCode", "screenModeCode"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Sel_Policy_Fte_Rel_Info",mapping);
        try {
            rs = spDao.execute(inputRecord,recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load employed physician information ", e);
            l.throwing(getClass().getName(), "loadAllEmployedPhysician", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllEmployedPhysician", rs);
        }
        return rs;
    }


    /**
     * delete all Employed Physician information.
     *
     * @param inputRecords intput record
     * @return the number of row updateds
     */
    public int deleteAllEmployedPhysician(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "deleteAllEmployedPhysician", new Object[]{inputRecords});
        }
        int processCount = 0;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("policyFteRelId", "policyFteRelationId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Fte_Relation.Delete_FTE_Rel",mapping);
        try {
            processCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to delete employed physician information ", e);
            l.throwing(getClass().getName(), "deleteAllEmployedPhysician", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "deleteAllEmployedPhysician", String.valueOf(processCount));
        }
        return processCount;
    }

    /**
     * Save all Pending Employed Physician information.
     *
     * @param inputRecords intput record
     * @return the number of row updateds
     */
    public int saveAllPendingEmployedPhysician(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllPendingEmployedPhysician", new Object[]{inputRecords});
        }

        int processCount = 0;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("policyFteRelId", "policyFteRelationId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("childRiskId", "riskChildId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("parentRiskId", "riskParentId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "effectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "effectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("statusCode", "currStatusCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));
        

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Fte_Relation.Save_FTE_Rel",mapping);
        try {
            processCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to save all pending employed physician information ", e);
            l.throwing(getClass().getName(), "saveAllPendingEmployedPhysician", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllPendingEmployedPhysician", String.valueOf(processCount));
        }
        return processCount;
    }


    /**
     * Save all Active Employed Physician information.
     *
     * @param inputRecords intput record
     * @return the number of row updateds
     */
    public int saveAllActiveEmployedPhysician(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllActiveEmployedPhysician", new Object[]{inputRecords});
        }

        int processCount = 0;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("policyFteRelId", "policyFteRelationId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("startDate", "transEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("endDate", "effectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));


        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Fte_Relation.Change_FTE_Rel",mapping);
        try {
            processCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to save all active employed physician information ", e);
            l.throwing(getClass().getName(), "saveAllActiveEmployedPhysician", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllActiveEmployedPhysician", String.valueOf(processCount));
        }
        return processCount;
    }


    /**
     * load recordset of all FTE risks for selection
     *
     * @param inputRecord
     * @param selectIndProcessor
     * @return recordset of all FTE Risks
     */
    public RecordSet loadAllFteRisk(Record inputRecord,RecordLoadProcessor selectIndProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllFteRisk", new Object[]{inputRecord});
        }
        RecordSet rs = null;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transEff", "transEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("coveragePartId", "coveragePartBaseRecordId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Fte_Relation.Sel_FTE_Risk_List",mapping);
        try {
            rs = spDao.execute(inputRecord,selectIndProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load FTE Risks ", e);
            l.throwing(getClass().getName(), "loadAllFteRisk", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllFteRisk",rs );
        }
        return rs;
    }

    


}
