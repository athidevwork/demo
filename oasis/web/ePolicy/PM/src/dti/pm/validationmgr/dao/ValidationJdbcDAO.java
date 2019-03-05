package dti.pm.validationmgr.dao;

import dti.pm.core.dao.BaseDAO;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.busobjs.YesNoFlag;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.sql.SQLException;

/**
 * This class implements the ValidationDAO interface.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 21, 2007
 *
 * @author sma
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public class ValidationJdbcDAO extends BaseDAO implements ValidationDAO {
    /**
     * Validate retroactive date
     *
     * @param inputRecord
     * @param riskId
     * @param productCoverageFieldName
     * @param coverageEffectiveFromdateFieldName
     * @param retroDateFieldName
     * @return Record
     */
    public Record validateRetroactiveDate(Record inputRecord, String riskId, String productCoverageFieldName,
                                          String coverageEffectiveFromdateFieldName, String retroDateFieldName) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateRetroactiveDate", new Object[]{inputRecord,
                riskId, productCoverageFieldName, coverageEffectiveFromdateFieldName, retroDateFieldName});
        }

        Record outputRecord;
        try {
            Record record = new Record();
            record.setFieldValue("riskId", riskId);
            record.setFieldValue("productCoverage", inputRecord.getStringValue(productCoverageFieldName));
            record.setFieldValue("covgEffFrom", inputRecord.getStringValue(coverageEffectiveFromdateFieldName));
            record.setFieldValue("newRetroDate", inputRecord.getStringValue(retroDateFieldName));

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Valid_Retro_Date");
            outputRecord = spDao.execute(record).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "Failed to validate Retroactive Date.", e);
            l.throwing(getClass().getName(), "validateRetroactiveDate", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "validateRetroactiveDate", outputRecord);

        return outputRecord;
    }

    /**
     * Validate Accounting Month
     *
     * @param inputRecord Record contains input values
     * @return YesNoFlag indicates if accounting month is valid
     */
    public YesNoFlag checkAccountingMonth(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "checkAccountingMonth", new Object[]{inputRecord});
        }

        // map the values to the input record
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("acctDate", "accountingDate"));

        // get the return value
        YesNoFlag returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Oasis_Valid_Accounting_Date", mapping);
        try {
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            returnValue = YesNoFlag.getInstance(outputRecordSet.getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD));
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to execute Oasis_Valid_Accounting_Date.", e);
            l.throwing(getClass().getName(), "checkAccountingMonth", ae);
            throw ae;
        }

        // done
        l.exiting(getClass().getName(), "checkAccountingMonth", returnValue);
        return returnValue;
    }

    /**
     * Validate policy type
     *
     * @param inputRecord Record contains input values
     * @return YesNoFlag indicates if policy type is valid
     */
    public YesNoFlag checkPolicyType(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "checkPolicyType", new Object[]{inputRecord});
        }

        // map the values to the input record
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("issueCoId", "issueCompanyEntityId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("issueState", "issueStateCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("policyTypeCode", "policyTypeCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("processLoc", "regionalOffice"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "termEffectiveToDate"));

        // get the return value
        YesNoFlag returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Check_Policy_Type", mapping);
        try {
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            returnValue = YesNoFlag.getInstance(outputRecordSet.getSummaryRecord().getStringValue("status"));
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to execute PM_Check_Policy_Type.", e);
            l.throwing(getClass().getName(), "checkPolicyType", ae);
            throw ae;
        }

        // done
        l.exiting(getClass().getName(), "checkPolicyType", returnValue);
        return returnValue;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public ValidationJdbcDAO() {
    }
}
