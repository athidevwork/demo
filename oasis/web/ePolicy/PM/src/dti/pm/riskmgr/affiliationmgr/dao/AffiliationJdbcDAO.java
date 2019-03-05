package dti.pm.riskmgr.affiliationmgr.dao;

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
import dti.pm.policymgr.PolicyHeader;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the AffiliationDAO interface. This is consumed by any business logic objects
 * that requires information about one or more Affiliaions.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 21, 2008
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 5/8/2008         Simon.Li    For UC process affiliation
 * 10/31/2017       lzhang      188425 Add validatePractPercForOOSENonInitTerms method
 * ---------------------------------------------------
 */
public class AffiliationJdbcDAO extends BaseDAO implements AffiliationDAO {


    /**
     * To calculate dates for load Affiliation.
     * <p/>
     *
     * @param inputRecord
     * @return
     */
    public Record calculateDateForAffiliation(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "calculateDateForAffiliation", new Object[]{inputRecord});
        Record record;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Affiliation.Calculate_Dates");
        try {
            record = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "Unable to calculate dates for Affiliation", e);
            l.throwing(getClass().getName(), "calculateDateForAffiliation", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "calculateDateForAffiliation", record);
        return record;
    }

    /**
     * validate affiliation copy
     *
     * @param inputRecord
     * @return validate status code statusCode
     */
    public String validateCopyAllAffiliation(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateCopyAllAffiliation", new Object[]{inputRecord});
        }

        String statusCode = null;
        // Create the input data mapping
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "transEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "termExp"));


        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Copy_All.Val_Affiliation", mapping);
        try {
            statusCode = spDao.execute(inputRecord).getSummaryRecord().getStringValue("statusCode");
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to validate affiliation", e);
            l.throwing(getClass().getName(), "validateCopyAllAffiliation", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateCopyAllAffiliation", statusCode);
        }

        return statusCode;
    }

    /**
     * copy all affliation data to target risk
     *
     * @param inputRecord
     */
    public void copyAllAffiliation(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "copyAllAffiliation", new Object[]{inputRecord});
        }

        // Create the input data mapping
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effFromForEndorse", "transEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("toRiskBaseId", "toRiskBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExpDate", "termExp"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Copy_All.Copyall_Affiliation", mapping);
        try {
            spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to copy all affiliation", e);
            l.throwing(getClass().getName(), "copyAllAffiliation", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "copyAllAffiliation");
        }
    }

    /**
     * load all affiliations
     *
     * @param inputRecord
     * @param processor
     * @return affliations recordset
     */
    public RecordSet loadAllAffiliation(Record inputRecord, RecordLoadProcessor processor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAffiliation", new Object[]{inputRecord});
        }
        // Create the input data mapping
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("riskEntId", "riskEntityId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effectiveFromDate", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effectiveToDate", "termEffectiveToDate"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Sel_Affiliations", mapping);
        RecordSet rs = null;
        try {
            rs = spDao.execute(inputRecord, processor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load affiliation data", e);
            l.throwing(getClass().getName(), "loadAllAffiliation", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAffiliation", rs);
        }
        return rs;
    }

    /**
     * Save all affiliation informations.
     * <p/>
     *
     * @param inputRecords intput records
     * @return the number of row updateds
     */
    public int saveAllAffiliation(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllAffiliation", new Object[]{inputRecords});

        int updateCount;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("entRelId", "entityRelationId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("entRelType", "relationTypeCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("affRelType", "addlRelationTypeCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("orgId", "entityParentId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("riskId", "entityChildId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effectiveFromDate", "effDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExpDate", "riskEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("payVapB", "vapB"));
        mapping.addFieldMapping(new DataRecordFieldMapping("primaryAffiliation", "primaryAffiliationB"));
        mapping.addFieldMapping(new DataRecordFieldMapping("excessB", "payExcessB"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Save_Screens.Save_Affi", mapping);
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save Affiliation.", e);
            l.throwing(getClass().getName(), "saveAllAffiliation", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "saveAllAffiliation", new Integer(updateCount));
        return updateCount;
    }

    /**
     * validate percent of practice for same time period cannot total over 100%.
     *
     * @param inputRecord
     * @return invalid terms
     */
    public String validatePractPercForOOSENonInitTerms(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validatePractPercForOOSENonInitTerms", new Object[]{inputRecord});
        }

        String failedTerms = null;
        // Create the input data mapping
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("effectiveFromDate", "termEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effectiveToDate", "maxAffExpDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termBaseRecordIds", "policyTermBaseRecordIds"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Affiliation.Val_Practice_Percent_For_OOSE", mapping);
        try {
            failedTerms = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to validate Practice Percent For Non Initail Terms", e);
            l.throwing(getClass().getName(), "validatePractPercForOOSENonInitTerms", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validatePractPercForOOSENonInitTerms", failedTerms);
        }

        return failedTerms;
    }
}
