package dti.pm.policymgr.userviewmgr.dao;

import dti.oasis.app.AppException;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.dao.BaseDAO;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Jdbc dao for user view
 * 
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   July 27, 2007
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/21/2007       zlzhu       Modified field id to match actual parameter in the stored procedure
 * ---------------------------------------------------
 */

public class UserViewJdbcDAO extends BaseDAO implements UserViewDAO {


    /**
     * load all user view information
     *
     * @param record (pmUserViewId)
     * @return recordSet
     */
    public RecordSet loadUserView(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadUserView", new Object[]{record});
        }

        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy.Sel_User_View");
        try {
            rs = spDao.execute(record);
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadUserView", rs);
            }
            return rs;

        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load user view information", e);
            l.throwing(getClass().getName(), "loadUserView", ae);
            throw ae;
        }
    }

    /**
     * save  user view information
     *
     * @param record
     * @return Record
     */
    public Record saveUserView(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveUserView", new Object[]{record});
        }
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("longDescription", "userViewLongDescription"));
       // mapping.addFieldMapping(new DataRecordFieldMapping("polHolderName", "policyHolderName"));
      // mapping.addFieldMapping(new DataRecordFieldMapping("policyTermRecordModeCode", "termStatusCode"));
        //mapping.addFieldMapping(new DataRecordFieldMapping("issueCompanyEntityId", "issueCompanyEntityId"));
       // mapping.addFieldMapping(new DataRecordFieldMapping("policyCycleCode", "policyCycle"));
       // mapping.addFieldMapping(new DataRecordFieldMapping("transEff", "transEffectiveExistDate"));
       // mapping.addFieldMapping(new DataRecordFieldMapping("transactionCode", "transactionCode"));
       // mapping.addFieldMapping(new DataRecordFieldMapping("policyRelStatTypeCode", "policyStatus"));
       // mapping.addFieldMapping(new DataRecordFieldMapping("transEffFrom", "transEffectiveFromDate"));
        //mapping.addFieldMapping(new DataRecordFieldMapping("transEffTo", "transEffectiveToDate"));
//        mapping.addFieldMapping(new DataRecordFieldMapping("acctDtFrom", "transAccountingFromDate"));
//        mapping.addFieldMapping(new DataRecordFieldMapping("acctDtTo", "transAccountingToDate"));
//        mapping.addFieldMapping(new DataRecordFieldMapping("termEffStart", "termEffectiveFromDate"));
//        mapping.addFieldMapping(new DataRecordFieldMapping("termEffEnd", "termEffectiveToDate"));
//        mapping.addFieldMapping(new DataRecordFieldMapping("termExpStart", "termExpirationFromDate"));
//        mapping.addFieldMapping(new DataRecordFieldMapping("termExpEnd", "termExpirationToDate"));
//        mapping.addFieldMapping(new DataRecordFieldMapping("riskName", "riskEntityName"));
//        mapping.addFieldMapping(new DataRecordFieldMapping("riskEntityId", "riskEntityId"));
//        mapping.addFieldMapping(new DataRecordFieldMapping("riskEffFrom", "riskEffectiveFromDate"));
//        mapping.addFieldMapping(new DataRecordFieldMapping("riskEffTo", "riskEffectiveToDate"));
//        mapping.addFieldMapping(new DataRecordFieldMapping("covgDescription", "productCoverageDesc"));
//        mapping.addFieldMapping(new DataRecordFieldMapping("covgLimit", "coverageLimitCode"));
//        mapping.addFieldMapping(new DataRecordFieldMapping("covgEffFrom", "coverageEffectiveFromDate"));
//        mapping.addFieldMapping(new DataRecordFieldMapping("covgEffTo", "coverageEffectiveToDate"));
//        mapping.addFieldMapping(new DataRecordFieldMapping("compDescription", "coverageCompDesc"));
        mapping.addFieldMapping(new DataRecordFieldMapping("compEffFrom", "componentEffectiveFromDate"));
//        mapping.addFieldMapping(new DataRecordFieldMapping("compEffTo", "componentEffectiveToDate"));
//        mapping.addFieldMapping(new DataRecordFieldMapping("addlSql", "additionalSql"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy.Save_User_View", mapping);
        try {

            Record outputRecord = spDao.executeUpdate(record);
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "SaveUserView");
            }
            return outputRecord;

        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to save user view information", e);
            l.throwing(getClass().getName(), "saveUserView", ae);
            throw ae;
        }
    }

    /**
     * delete user view information from DB
     *
     * @param record (pmUserViewId)
     * @return
     */
    public void deleteUserView(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "deleteUserView", new Object[]{record});
        }

        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy.Delete_User_View");
        try {

            spDao.execute(record);
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "deleteUserView");
            }
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to delete user view information", e);
            l.throwing(getClass().getName(), "deleteUserView", ae);
            throw ae;
        }
    }

    /**
     * validate addtional sql
     * 
     * @param inputRecord
     * @return
     */
    public String validateAdditionalSql(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAdditionalSql", new Object[]{inputRecord});
        }
        String returnValue;


        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy.Validate_Sql");
        try {
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            returnValue = outputRecordSet.getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "validateAdditionalSql");
            }
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to validate additional sql", e);
            l.throwing(getClass().getName(), "validateAdditionalSql", ae);
            throw ae;
        }
        return returnValue;
    }
}
