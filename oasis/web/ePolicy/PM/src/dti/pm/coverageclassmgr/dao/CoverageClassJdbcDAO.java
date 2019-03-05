package dti.pm.coverageclassmgr.dao;

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
import dti.pm.policymgr.PolicyHeaderFields;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the CoverageClassDAO interface.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 8, 2007
 *
 * @author jmpotosky
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/08/2011       wqfu        121349 - Add isAddCoverageClassAllowed and getShortTermCoverageClassEffAndExpDates.
 * 04/09/2011       syang       131516 - Modified loadAllAvailableCoverageClass() to change data mapping for taxStatus.
 * 05/24/2013       xnie        142949 - Added validateCopyAllCoverageClass().
 * 04/28/2014       sxm         154227 - Remove call to PM_CopyAll_Sub_Covg
 * 09/16/2014       awu         157552 - Add validateCoverageClassDuplicate;
 * ---------------------------------------------------
 */

public class CoverageClassJdbcDAO extends BaseDAO implements CoverageClassDAO {

    /**
     * Returns a RecordSet loaded with list of available coverage classes for the provided
     * policy information.
     * <p/>
     *
     * @param inputRecord         record with policy, risk, coverage key fields.
     * @param recordLoadProcessor an instance of data load processor
     * @return RecordSet a RecordSet loaded with list of available coverage classes.
     */
    public RecordSet loadAllCoverageClass(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllCoverageClass", new Object[]{inputRecord, recordLoadProcessor});

        RecordSet rs = null;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termId", "policyTermHistoryId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("endQuoteId", "endorsementQuoteId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Sel_Sub_Coverage_Info", mapping);
        try {
            rs = spDao.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load Coverage Class information for policyNo:" + PolicyHeaderFields.getPolicyNo(inputRecord), e);
            l.throwing(getClass().getName(), "loadAllCoverageClass", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllCoverageClass", rs);
        }
        return rs;
    }

    /**
     * check if it is valid for update
     *
     * @param parameters
     * @return boolean
     */
    private boolean isValidForUpdate(RecordSet parameters) {
        return (parameters != null && parameters.getSize() > 0) ? true : false;
    }

    /**
     * Save all given input records with the Pm_Nb_End.Save_sub_Coverage stored procedure,
     * assuming they all have recordModeCode = TEMP, were added during this WIP transaction
     * (ie. have not been saved as Official yet).
     * This assumes all input records have recordModeCode = TEMP.
     * Set the rowStatus field to NEW for records that are newly added in this request.
     * Set the rowStatus field to MODIFIED for records that have already been saved in this WIP transaction,
     * and are just being updated not.
     *
     * @param inputRecords a set of Records, each with the PolicyHeader, PolicyIdentifier,
     *                     and Coverage Class Detail info matching the fields returned from the loadAllCoverage method..
     * @return the number of rows updated.
     */

    public int addAllCoverageClass(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "addAllCoverageClass", new Object[]{inputRecords});

        int updateCount = 0;

        /* If inputRecords is invalid, return directly. added by Joe 04/10/2007 */
        if (!isValidForUpdate(inputRecords)) {
            return updateCount;
        }

        // Create a DataRecordMapping for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();

        mapping.addFieldMapping(new DataRecordFieldMapping("subCoverageId", "coverageClassId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("baseCovgId", "parentCoverageBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("baseSubCovgId", "coverageClassBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("subCovgCode", "productCoverageClassCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effFromDate", "coverageClassEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effToDate", "coverageClassEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExpDate", "termEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("covgStatus", "coverageClassStatus"));
        mapping.addFieldMapping(new DataRecordFieldMapping("offRec", "officialRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("afterImageB", "afterImageRecordB"));
        mapping.addFieldMapping(new DataRecordFieldMapping("methodCode", "cancellationMethodCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("baseTermId", "termBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("annBaseRate", "annualBaseRate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("subcovgLimitCode", "coverageLimitCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("retroactiveDate", "retroDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("ratingBasis", "actualRatingBasis"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Nb_End.Save_sub_Coverage", mapping);
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save inserted/updated coverage classes.", e);
            l.throwing(getClass().getName(), "addAllCoverageClass", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addAllCoverageClass", new Integer(updateCount));
        }
        return updateCount;
    }

    /**
     * Update all given input records with the Pm_Endorse.Change_Sub_Coverage stored procedure,
     * assuming they all have field recordModeCode = Official, and were marked as updated.
     *
     * @param inputRecords a set of Records, each with the PolicyHeader, PolicyIdentifier,
     *                     and Coverage Class Detail info matching the fields returned from the loadAllCoverage method..
     * @return the number of rows updated.
     */

    public int updateAllCoverageClass(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "updateAllCoverageClass", new Object[]{inputRecords});

        //int updateCount = inputRecords.getSize(); // no need to initialize it here.
        int updateCount = 0;

        /* If inputRecords is invalid, return directly. added by Joe 04/10/2007 */
        if (!isValidForUpdate(inputRecords)) {
            return updateCount;
        }

        // Create a DataRecordMapping for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("subCoverageId", "coverageClassId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("baseSubCovgId", "coverageClassBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("baseCovgId", "parentCoverageBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("prodSubCovgCode", "productCoverageClassCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effFromDate", "transEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "coverageClassEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("annBaseRate", "annualBaseRate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("subcovgLimitCode", "coverageLimitCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("ratingBasis", "actualRatingBasis"));

        // Version the records in batch mode with 'Pm_Endorse.Change_Sub_Coverage'
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Endorse.Change_Sub_Coverage", mapping);
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to endorse coverage classes.", e);
            l.throwing(getClass().getName(), "updateAllCoverageClass", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "updateAllCoverageClass", new Integer(updateCount));
        }

        l.exiting(getClass().getName(), "updateAllCoverageClass", new Integer(updateCount));
        return updateCount;
    }

    /**
     * Delete all given input records with the Pm_Nb_Del.Del_Coverage stored procedure,
     * assuming they all have recordModeCode = TEMP, and were marked for delete.
     *
     * @param inputRecords a set of Records, each with the PolicyHeader, PolicyIdentifier,
     *                     and Coverage Class Detail info matching the fields returned from the loadAllCoverage method..
     * @return the number of rows updated.
     */

    public int deleteAllCoverageClass(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "deleteAllCoverageClass", new Object[]{inputRecords});

        //int updateCount = inputRecords.getSize(); // no need to initialize it here.
        int updateCount = 0;

        /* If inputRecords is invalid, return directly. added by Joe 04/10/2007 */
        if (!isValidForUpdate(inputRecords)) {
            return updateCount;
        }

        // Create a DataRecordMapping for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("covgId", "coverageClassId"));

        // Delete the records in batch mode with 'Pm_Nb_Del.Del_Coverage'
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Nb_Del.Del_Coverage", mapping);
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to delete coverage classes.", e);
            l.throwing(getClass().getName(), "deleteAllCoverageClass", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "deleteAllCoverageClass", new Integer(updateCount));
        }


        l.exiting(getClass().getName(), "deleteAllCoverageClass", new Integer(updateCount));
        return updateCount;
    }

    /**
     * Load all available coverage class
     *
     * @param inputRecord
     * @param recordLoadProcessor
     * @return RecordSet
     */
    public RecordSet loadAllAvailableCoverageClass(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAvailableCoverageClass", new Object[]{inputRecord, recordLoadProcessor});
        }

        RecordSet rs;
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("covgCode", "productCoverageCode"));
            mapping.addFieldMapping(new DataRecordFieldMapping("transId", "lastTransactionId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("taxStatus", "taxStatusCode"));
            mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "coverageClassEffectiveFromDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "coverageClassEffectiveToDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("coverageClassShortDesc", "coverageClassShortDescription"));
            mapping.addFieldMapping(new DataRecordFieldMapping("coverageClassLongDesc", "coverageClassLongDescription"));
            mapping.addFieldMapping(new DataRecordFieldMapping("covClassCode", "productCoverageClassCode"));

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Sel_Product_Covg_Class", mapping);
            rs = spDao.execute(inputRecord, recordLoadProcessor);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllAvailableCoverageClass", rs);
            }
            return rs;
        }
        catch (SQLException ex) {
            AppException ae = ExceptionHelper.getInstance().
                handleException("Unable to load available coverage class information", ex);
            l.throwing(getClass().getName(), "loadAllAvailableCoverageClass", ae);
            throw ae;
        }
    }

    /**
     * Get coverage class Id and base Id.
     *
     * @param inputRecord Input record containing coverageBaseRecordId and productCoverageCode infor.
     * @return Record that contains coverage class Id and base Id.
     */
    public Record getCoverageClassIdAndBaseId(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getCoverageClassIdAndBaseId", new Object[]{inputRecord});
        }

        Record record;
        // Fix issue 99910, pass the product coverage code of the coverage class being added, it is productCoverageClassCode.
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("productCoverageCode", "productCoverageClassCode"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Coverage_Class.Get_Class_PK_and_Base_FK", mapping);
        try {
            record = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().
                handleException("Unable to get coverage class ID and base ID", e);
            l.throwing(getClass().getName(), "getCoverageClassIdAndBaseId", e);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getCoverageClassIdAndBaseId", record);
        }
        return record;
    }

    /**
     * Get exposure information
     *
     * @param inputRecord
     * @return Record
     */
    public String getExposureInfo(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getExposureInfo", new Object[]{inputRecord});
        }

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));

        String sExposureInfo;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Facility_Rating.Get_Exposure_Basis_No", mapping);
        try {
            sExposureInfo = spDao.execute(inputRecord).getSummaryRecord().getStringValue(
                StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().
                handleException("Unable to get exposure information for coverage class", e);
            l.throwing(getClass().getName(), "getExposureInfo", e);
            throw ae;
        }

        return sExposureInfo;
    }

    /**
     * Check if Change option is available or not by function Pm_Valid_Oos_Coverage
     *
     * @param inputRecord
     * @return
     */
    public String isOosCoverageClassValid(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isOosCoverageClassValid", new Object[]{inputRecord});
        }

        String isValid;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("covgEff", "coverageClassEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("covgBaseId", "coverageClassBaseRecordId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Valid_Oos_Coverage", mapping);
        try {
            isValid = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "Unable to check oos coverage class is valid or not", e);
            l.throwing(getClass().getName(), "isOosCoverageClassValid", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isOosCoverageClassValid", isValid);
        }
        return isValid;
    }

    /**
     * delete all coverage class from coverage for delete risk all
     *
     * @param covgClassRs
     */
    public void deleteAllCopiedCoverageClass(RecordSet covgClassRs) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "deleteAllCopiedCoverageClass", new Object[]{covgClassRs});
        }

        // Create the input data mapping
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("fromSubCovgId", "coverageClassId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("toCoverageBaseId", "toCoverageBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effDtForEndorse", "transEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExpiration", "termExp"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Copyall_Delete.Pm_Delete_Subcoverage", mapping);
        try {
            spDao.executeBatch(covgClassRs);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to delete all coverage class from given coverage", e);
            l.throwing(getClass().getName(), "deleteAllCopiedCoverageClass", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "deleteAllCopiedCoverageClass");
        }
    }

    /**
     * Check if add coverage class is allowed
     *
     * @param inputRecord
     * @return
     */
    public String isAddCoverageClassAllowed(Record inputRecord){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isAddCoverageClassAllowed", new Object[]{inputRecord});
        }
        String isAddCoverageAllowed;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Coverage_Class.Is_Add_Covg_Class_Allowed");
        try {
            isAddCoverageAllowed = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get isAddCoverageClassAllowed.", e);
            l.throwing(getClass().getName(), "isAddCoverageClassAllowed", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isAddCoverageClassAllowed", isAddCoverageAllowed);
        }
        return isAddCoverageAllowed;
    }

    /**
     * Get short term coverage class's effective from and effective to date
     *
     * @param inputRecord Input record containing coverage level details
     * @return Record that contains coverage class effective from date and effective to date.
     */
    public Record getShortTermCoverageClassEffAndExpDates(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getShortTermCoverageClassEffAndExpDates", new Object[]{inputRecord});
        }

        // get the return value
        Record returnRecord;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Web_Coverage_Class.Get_Short_Covg_Class_Dates");
        try {
            returnRecord = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get coverage class dates", e);
            l.throwing(getClass().getName(), "getShortTermCoverageClassEffAndExpDates", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getShortTermCoverageClassEffAndExpDates", returnRecord);
        }
        return returnRecord;
    }

    /**
     * validate coverage class copy
     *
     * @param inputRecord
     * @return validate status code statusCode
     */
    public String validateCopyAllCoverageClass(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateCopyAllCoverageClass", new Object[]{inputRecord});
        }

        String statusCode = null;
        // Create the input data mapping
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("fromSubCovgId", "coverageClassId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("toCoverageBaseId", "toCoverageBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expUnit", "exposureUnit"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termEffective", "transEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExpiration", "termEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("annualRateB", "annualBaseRate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("char1B", "char1"));
        mapping.addFieldMapping(new DataRecordFieldMapping("char2B", "char2"));
        mapping.addFieldMapping(new DataRecordFieldMapping("char3B", "char3"));
        mapping.addFieldMapping(new DataRecordFieldMapping("num1B", "num1"));
        mapping.addFieldMapping(new DataRecordFieldMapping("num2B", "num2"));
        mapping.addFieldMapping(new DataRecordFieldMapping("num3B", "num3"));
        mapping.addFieldMapping(new DataRecordFieldMapping("date1B", "date1"));
        mapping.addFieldMapping(new DataRecordFieldMapping("date2B", "date2"));
        mapping.addFieldMapping(new DataRecordFieldMapping("date3B", "date3"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Val_Cpy.Val_Sub_Covg", mapping);
        try {
            statusCode = spDao.execute(inputRecord).getSummaryRecord().getStringValue("statusCode");
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to validate coverage class copy", e);
            l.throwing(getClass().getName(), "validateCopyAllCoverageClass", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateCopyAllCoverageClass", statusCode);
        }

        return statusCode;
    }

    /**
     * Validate coverage class duplicate.
     * @param inputRecord
     * @return
     */
    public Record validateCoverageClassDuplicate(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateCoverageClassDuplicate", new Object[]{inputRecord});
        }

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("parentCoverageBaseId", "parentCoverageBaseRecordId"));

        RecordSet rs = null;
        Record outputRec = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Validate_Data.New_Coverage_Class_Duplicate", mapping);
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to validate coverage class duplication.", e);
            l.throwing(getClass().getName(), "validateCoverageClassDuplicate", ae);
            throw ae;
        }
        if (rs != null) {
            outputRec = rs.getSummaryRecord();
        }


        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateCoverageClassDuplicate", outputRec);
        }
        return outputRec;
    }
    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public CoverageClassJdbcDAO() {
    }
}

