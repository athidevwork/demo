package dti.pm.coveragemgr.dao;

import dti.oasis.app.AppException;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordBeanMapper;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.dao.BaseDAO;
import dti.pm.coveragemgr.CoverageFields;
import dti.pm.coveragemgr.CoverageHeader;
import dti.pm.dao.DataFieldNames;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.riskmgr.RiskCopyFields;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Iterator;

/**
 * This class implements the CoverageDAO interface.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 10, 2007
 *
 * @author jmpotosky
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/12/2007       sxm         Rename productCoverageCodeDescription to productCoverageDesc in loadAllAvailableCoverage()
 * 04/07/2008       fcb         getCoverageLimitShared added.
 * 01/27/2011       dzhang      116359 - Add isAddCoverageAllowed().
 * 04/26/2011       ryzhao      116863 - Update loadCoverageHeader() to set inputRecord with policy header fields.
 *                                       System will use term eff date and term exp date to load primary risk.
 * 05/18/2011       dzhang      117246 - Added getShortTermCoverageEffAndExpDates().
 * 03/12/2012       dzhang      126811 - Modified loadDependentCoverage(): change the input parameter to practiceStateCode
 *                                       instead of issueStateCode.
 * 01/08/2013       fcb         137981 - changes related to Pm_Dates modifications.
 * 06/17/2013       adeng       143400 - 1)Add mapping for viewMode & endorsementQuoteId to retrieve record exist information.
 *                                       2)Correct wrong mapping for id to coverageBaseRecordId,it should map sourceId.
 * 08/07/2013       awu         146878 - Added getCurrentCoverageStatus.
 * 09/06/2013       adeng       147468 - Modified getCurrentCoverageStatus() to map endorsementQuoteId to endqId.
 * 10/11/2013       adeng       148929 - Modified addAllCoverage() & updateAllCoverage() to add mapping for subLimitB.
 * 01/24/2014       jyang       150639 - Move getCoverageExpirationDate() to ComponentDao.
 * 04/28/2014       sxm         154227 - Remove call to PM_CopyAll_Coverage
 * 10/12/2014       jyang       157749 - Added getRiskContiguousEffectiveDate().
 * 09/16/2014       awu         157552 - Add validateCoverageDuplicate.
 * 12/26/2014       xnie        156995 - Added loadAllCoverageByIds() to load all coverages based on gaven ID list.
 * 07/11/2016       lzhang      177681 - Add loadAllNewCopiedCMCoverage and saveAllRetroDate method.
 * 07/26/2016       lzhang      169751 - Modified addAllCoverage and updateAllCoverage: add mapping for
 *                                       IBNRCovgEffectiveFromDate and IBNRCovgEffectiveToDate
 * ---------------------------------------------------
 */

public class CoverageJdbcDAO extends BaseDAO implements CoverageDAO {
    /**
     * Returns a RecordSet loaded with list of available risks for the provided
     * policy information.
     * <p/>
     *
     * @param policyHeader an instance of the PolicyHeader object with the current policy/term details loaded
     * @param inputRecord  record containting input parameters including an option risk id.
     * @return CoverageHeader an instance of the CoverageHeader object loaded
     */
    public CoverageHeader loadCoverageHeader(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadCoverageHeader", new Object[]{policyHeader, inputRecord});
        }

        CoverageHeader coverageHeader = null;
        RecordSet rs;

        // Setup the stored procedure
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy_Header.Get_Covg_Header");
        try {
            inputRecord.setFields(policyHeader.toRecord(), false);
            rs = spDao.execute(inputRecord);

            if (rs.getSize() != 1) {
                throw new AppException("Unable to get coverage header for coverageId: " + inputRecord.getStringValue("coverageId"));
            }

            //Pull the first record to get ref cursor information
            Record outputRecord = rs.getFirstRecord();

            // Map the output to the CoverageHeader
            RecordBeanMapper mapper = new RecordBeanMapper();
            coverageHeader = new CoverageHeader();
            mapper.map(outputRecord, coverageHeader);

        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load coverage header for policyNo:" + PolicyHeaderFields.getPolicyNo(inputRecord), e);
            l.throwing(getClass().getName(), "loadCoverageHeader", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadCoverageHeader", coverageHeader);
        }

        return coverageHeader;
    }

    /**
     * Returns a RecordSet loaded with list of available coverages for the provided
     * policy/risk information.
     * <p/>
     *
     * @param inputRecord         record with policy/risk key fields.
     * @param recordLoadProcessor an instance of data load processor
     * @return RecordSet a RecordSet loaded with list of available coverages.
     */
    public RecordSet loadAllCoverage(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllCoverage", new Object[]{recordLoadProcessor});

        RecordSet rs = null;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping(DataFieldNames.TERM_EFF, "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping(DataFieldNames.TERM_EXP, "termEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("endQuoteId", "endorsementQuoteId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Sel_Coverage_Info", mapping);
        try {
            rs = spDao.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load coverage information for policyNo:" + PolicyHeaderFields.getPolicyNo(inputRecord), e);
            l.throwing(getClass().getName(), "loadAllCoverage", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllCoverage", rs);
        }
        return rs;
    }

    /**
     * Load all coverage summary by policy id
     *
     * @param inputRecord input record that contains policy id
     * @return coverage summary
     */
    public RecordSet loadAllCoverageSummary(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllCoverageSummary", new Object[]{inputRecord});
        }
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("riskBaseId", "riskBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("polId", "policyId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("polTermId", "termBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "effectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "effectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("lastTranId", "lastTransactionId"));

        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("Pm_Sel_Coverage_Current_Info", mapping);
        RecordSet outRecordSet = null;
        try {
            outRecordSet = sp.execute(inputRecord);
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Error when executing loadAllCoverageSummary", se);
            l.throwing(getClass().getName(), "loadAllCoverageSummary", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllCoverageSummary", outRecordSet);
        }
        return outRecordSet;
    }

    /**
     * Returns a Record with the additional info 1,2,3 fields for the particular coverage and term dates.
     * <p/>
     *
     * @param inputRecord record with coverageId and term dates.
     * @return Record a Record with the Addl Info fields if configured via system parameters.
     */
    public Record loadCoverageAddlInfo(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadCoverageAddlInfo", new Object[]{inputRecord});

        try {
            Record output = new Record();

            // Set some initial values
            inputRecord.setFieldValue("inputLevel", "COVERAGE");

            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("level", "inputLevel"));
            mapping.addFieldMapping(new DataRecordFieldMapping("value", "coverageId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("fromDate", "termEffectiveFromDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("toDate", "termEffectiveToDate"));

            // Execute the stored procedure for additional info fields
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy.Sel_Addl_Info", mapping);
            output = spDao.executeUpdate(inputRecord);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadCoverageAddlInfo", output);
            }
            return output;

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to select coverage additional information for: " + inputRecord.getStringValue("coverageId"), e);
            l.throwing(getClass().getName(), "loadCoverageAddlInfo", ae);
            throw ae;
        }
    }

    /**
     * Save all given input records with the Pm_Nb_End.Save_Coverage stored procedure,
     * assuming they all have recordModeCode = TEMP, were added during this WIP transaction
     * (ie. have not been saved as Official yet).
     * This assumes all input records have recordModeCode = TEMP.
     * Set the rowStatus field to NEW for records that are newly added in this request.
     * Set the rowStatus field to MODIFIED for records that have already been saved in this WIP transaction,
     * and are just being updated not.
     *
     * @param inputRecords a set of Records, each with the PolicyHeader, PolicyIdentifier,
     *                     and Coverage Detail info matching the fields returned from the loadAllCoverage method..
     * @return the number of rows updated.
     */
    public int addAllCoverage(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "addAllCoverage", new Object[]{inputRecords});

        int updateCount = 0;

        // Create a DataRecordMapping for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("baseCovgId", "coverageBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("baseRiskId", "riskBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("baseTermId", "termBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("prodCovgCode", "productCoverageCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("primaryCvgB", "primaryCoverageB"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effFromDate", "coverageEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effToDate", "coverageEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExpDate", "termEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("covgStatus", "coverageStatus"));
        mapping.addFieldMapping(new DataRecordFieldMapping("covgLimitCode", "coverageLimitCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("sharedLimB", "sharedLimitsB"));
        mapping.addFieldMapping(new DataRecordFieldMapping("offRec", "officialRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("afterImageB", "afterImageRecordB"));
        mapping.addFieldMapping(new DataRecordFieldMapping("aggrLimit", "extendedAggregateLimit"));
        mapping.addFieldMapping(new DataRecordFieldMapping("methodCode", "cancellationMethodCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("annBaseRate", "annualBaseRate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("paRetroDate", "priorActsRetroDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("pcfCounty", "pcfCountyCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("pcfDate", "pcfParticipationDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("deductId", "deductibleComponentId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cpSharedLimB", "covgPartSharedLimitB"));
        mapping.addFieldMapping(new DataRecordFieldMapping("defaultAmtOfInsurance", "defaultAmountOfInsurance"));
        mapping.addFieldMapping(new DataRecordFieldMapping("addtlAmtOfInsurance", "addtlAmountOfInsurance"));
        mapping.addFieldMapping(new DataRecordFieldMapping("sublimitB", "subLimitB"));
        mapping.addFieldMapping(new DataRecordFieldMapping("ibnrCovgEffFromDate", "IBNRCovgEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("ibnrCovgEffToDate", "IBNRCovgEffectiveToDate"));

        // Insert the records in batch mode with 'Pm_Nb_End.Save_Coverage'
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Nb_End.Save_Coverage", mapping);
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save inserted/updated coverages.", e);
            l.throwing(getClass().getName(), "addAllCoverage", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addAllCoverage", new Integer(updateCount));
        }
        return updateCount;
    }

    /**
     * Update all given input records with the Pm_Endorse.Change_Coverage stored procedure,
     * assuming they all have field recordModeCode = Official, and were marked as updated.
     *
     * @param inputRecords a set of Records, each with the PolicyHeader, PolicyIdentifier,
     *                     and Coverage Detail info matching the fields returned from the loadAllCoverage method..
     * @return the number of rows updated.
     */
    public int updateAllCoverage(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "updateAllCoverage", new Object[]{inputRecords});

        int updateCount = inputRecords.getSize();

        // Create a DataRecordMapping for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("baseCovgId", "coverageBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("baseRiskId", "riskBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("prodCovgCode", "productCoverageCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("primaryCvgB", "primaryCoverageB"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effFromDate", "transEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping(DataFieldNames.EXP_DATE, "coverageEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("covgLimitCode", "coverageLimitCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("sharedLimB", "sharedLimitsB"));
        mapping.addFieldMapping(new DataRecordFieldMapping("aggrLimit", "extendedAggregateLimit"));
        mapping.addFieldMapping(new DataRecordFieldMapping("annBaseRate", "annualBaseRate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("paRetroDate", "priorActsRetroDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("pcfCounty", "pcfCountyCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("pcfDate", "pcfParticipationDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("deductId", "deductibleComponentId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cpSharedLimB", "covgPartSharedLimitB"));
        mapping.addFieldMapping(new DataRecordFieldMapping("defaultAmtOfInsurance", "defaultAmountOfInsurance"));
        mapping.addFieldMapping(new DataRecordFieldMapping("addtlAmtOfInsurance", "addtlAmountOfInsurance"));
        mapping.addFieldMapping(new DataRecordFieldMapping("sublimitB", "subLimitB"));
        mapping.addFieldMapping(new DataRecordFieldMapping("ibnrCovgEffFromDate", "IBNRCovgEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("ibnrCovgEffToDate", "IBNRCovgEffectiveToDate"));

        // Version the records in batch mode with 'Pm_Endorse.Change_Coverage'
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Endorse.Change_Coverage", mapping);
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to endorse coverages.", e);
            l.throwing(getClass().getName(), "updateAllCoverage", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "updateAllCoverage", new Integer(updateCount));
        }

        l.exiting(getClass().getName(), "updateAllCoverage", new Integer(updateCount));
        return updateCount;
    }

    /**
     * Delete all given input records with the Pm_Nb_Del.Del_Coverage stored procedure,
     * assuming they all have recordModeCode = TEMP, and were marked for delete.
     *
     * @param inputRecords a set of Records, each with the PolicyHeader, PolicyIdentifier,
     *                     and Coverage Detail info matching the fields returned from the loadAllCoverage method..
     * @return the number of rows updated.
     */
    public int deleteAllCoverage(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "deleteAllCoverage", new Object[]{inputRecords});

        int updateCount = inputRecords.getSize();

        // Create a DataRecordMapping for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("covgId", "coverageId"));

        // Delete the records in batch mode with 'Pm_Nb_Del.Del_Coverage'
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Nb_Del.Del_Coverage", mapping);
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to delete coverages.", e);
            l.throwing(getClass().getName(), "deleteAllCoverage", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "deleteAllCoverage", new Integer(updateCount));
        }


        l.exiting(getClass().getName(), "deleteAllCoverage", new Integer(updateCount));
        return updateCount;
    }

    /**
     * Load all available Coverage
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllAvailableCoverage(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAvailableCoverage", new Object[]{inputRecord, recordLoadProcessor});
        }

        RecordSet rs;
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("stateCode", "practiceStateCode"));
            mapping.addFieldMapping(new DataRecordFieldMapping("transactionId", "lastTransactionId"));
            mapping.addFieldMapping(new DataRecordFieldMapping(DataFieldNames.TERM_EFF, "termEffectiveFromDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping(DataFieldNames.TERM_EXP, "termEffectiveToDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("productCoverageCodeDesc", "productCoverageDesc"));
            mapping.addFieldMapping(new DataRecordFieldMapping("coverageSegmentCodeDesc", "coverageSegmentCodeDescription"));

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Sel_Product_Covg", mapping);
            rs = spDao.execute(inputRecord, recordLoadProcessor);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllAvailableCoverage", rs);
            }

            return rs;

        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load available coverage information", e);
            l.throwing(getClass().getName(), "loadAllAvailableCoverage", ae);
            throw ae;
        }
    }

    /**
     * Load all dependent Coverage
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadDependentCoverage(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadDependentCoverage", new Object[]{inputRecord});
        }

        RecordSet rs;
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("stateCode", "practiceStateCode"));
            mapping.addFieldMapping(new DataRecordFieldMapping("transactionId", "lastTransactionId"));
            mapping.addFieldMapping(new DataRecordFieldMapping(DataFieldNames.TERM_EFF, "termEffectiveFromDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping(DataFieldNames.TERM_EXP, "termEffectiveToDate"));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Sel_Product_Covg", mapping);
            rs = spDao.execute(inputRecord);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadDependentCoverage", rs);
            }
            return rs;

        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load available coverage information", e);
            l.throwing(getClass().getName(), "loadDependentCoverage", ae);
            throw ae;
        }
    }

    /**
     * Get coverage Id and base ID.
     *
     * @param inputRecord Input record containing risk and coverage level details
     * @return Record that contains coverage Id and base ID.
     */
    public Record getCoverageIdAndBaseId(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getCoverageIdAndBaseId", new Object[]{inputRecord});
        }

        // get the return value
        Record returnRecord;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Web_Coverage.Get_Coverage_PK_And_Base_FK");
        try {
            returnRecord = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get coverage ID and base ID", e);
            l.throwing(getClass().getName(), "getCoverageIdAndBaseId", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getCoverageIdAndBaseId", returnRecord);
        }
        return returnRecord;
    }

    /**
     * Validate OOSWIP Retroactive Date
     *
     * @param policyHeader
     * @param inputRecord
     * @return Record
     */
    public Record validationRetroactiveDateForOoswip(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validationRetroactiveDateForOoswip", new Object[]{inputRecord});
        }

        inputRecord.setFieldValue(DataFieldNames.TRANS_EFF, policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("covgId", "coverageBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping(DataFieldNames.TERM_EFF, "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping(DataFieldNames.TERM_EXP, "termEffectiveToDate"));

        Record rc;
        /* Validate retroactive date and get the error message */
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Validate_Retro_Change", mapping);
        try {
            rc = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "Failed to validate OOSWIP Retroactive Date.", e);
            l.throwing(getClass().getName(), "validationRetroactiveDateForOoswip", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "validationRetroactiveDateForOoswip", rc);

        return rc;
    }

    /**
     * Check if prior acts exist
     *
     * @param inputRecord
     * @return String
     */
    public String isPriorActsExist(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isPriorActsExist", new Object[]{inputRecord});
        }

        // Set constant field values
        inputRecord.setFieldValue("table", "PRIOR_ACTS");

        // Create field mapping
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping(DataFieldNames.TERM_EFF, "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("sourceId", "coverageBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("viewMode", "policyViewMode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("endQuoteId", "endorsementQuoteId"));

        String result;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Record_Exist", mapping);
        try {
            result = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "Failed to get prior acts exist information.", e);
            l.throwing(getClass().getName(), "isPriorActsExist", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "isPriorActsExist", result);

        return result;
    }

    /**
     * Get retroactive date
     *
     * @param inputRecord
     * @return String
     */
    public String getRetroactiveDate(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRetroactiveDate", new Object[]{inputRecord});
        }

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("riskId", "riskBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("covgEffFrom", "coverageEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("productCoverage", "productCoverageCode"));

        String sdate;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Get_Retro_Date", mapping);
        try {
            sdate = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "Failed to get Retroactive Date", e);
            l.throwing(getClass().getName(), "getRetroactiveDate", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "getRetroactiveDate", sdate);

        return sdate;
    }

    /**
     * Get the ratingModuleCode for the copied record when oose coverage
     *
     * @param inputRecord
     * @return
     */
    public String getRatingModuleCode(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRatingModuleCode", new Object[]{inputRecord});
        }

        String ratingModuleCode;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transId", "transactionLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effFrom", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effTo", "termEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("prodCovg", "productCoverageCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("alg", "ratingModuleCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("newCovgB", "newCovgB"));
        mapping.addFieldMapping(new DataRecordFieldMapping("covgId", "coverageBaseRecordId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("rt_get.get_rate_alg_remap", mapping);
        try {
            ratingModuleCode = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get ratingModuleCode", e);
            l.throwing(getClass().getName(), "getRatingModuleCode", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRatingModuleCode", ratingModuleCode);
        }
        return ratingModuleCode;
    }

    /**
     * Check if Change option is available or not by function Pm_Valid_Oos_Coverage
     *
     * @param inputRecord
     * @return
     */
    public String isOosCoverageValid(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isOosCoverageValid", new Object[]{inputRecord});
        }

        String isValid;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("covgEff", "coverageEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("covgBaseId", "coverageBaseRecordId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Valid_Oos_Coverage", mapping);
        try {
            isValid = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "Unable to check oos coverage is valid or not", e);
            l.throwing(getClass().getName(), "isOosCoverageValid", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isOosCoverageValid", isValid);
        }
        return isValid;
    }

    /**
     * get validate practice state result
     *
     * @param inputRecord
     * @return validate result
     */
    public String getValidatePracticeStateResult(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getValidatePracticeStateResult", new Object[]{inputRecord});
        }

        String result;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("covgId", "coverageId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("covgBaseId", "coverageBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("riskBaseId", "riskBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));
        inputRecord.setFieldValue("typeCode", "PRIORACTS");
        inputRecord.setFieldValue("componentOwner", "coverage");

        // Setup the stored procedure
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Validate_Required.Practice_State", mapping);
        try {
            result = spDao.execute(inputRecord).getSummaryRecord().getStringValue("return");
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get Validate Practice State Result", e);
            l.throwing(getClass().getName(), "getValidatePracticeStateResult", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getValidatePracticeStateResult", result);
        }

        return result;
    }

    /**
     * check similar coverage
     *
     * @param inputRecord
     * @return
     */
    public String getCheckSimilarCoverageResult(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getCheckSimilarCoverageResult", new Object[]{inputRecord});
        }

        String result;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("covgBaseId", "coverageBaseRecordId"));

        // Setup the stored procedure
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Check_Similar_Covg", mapping);
        try {
            result = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to check similar coverage", e);
            l.throwing(getClass().getName(), "getCheckSimilarCoverageResult", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getCheckSimilarCoverageResult", result);
        }

        return result;
    }

    /**
     * get coverage base record id
     *
     * @param inputRecord
     * @return coverage base record id
     */
    public String getCoverageBaseId(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getCoverageBaseId", new Object[]{inputRecord});
        }

        // Create the input data mapping
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("riskBase", "toRiskBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("prodCovg", "productCoverageCode"));

        // get the return value
        String returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Base_Fk.Get_Covg_Base_Fk", mapping);
        try {
            returnValue = spDao.execute(inputRecord).getSummaryRecord().getStringValue("baseId");
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get coverage base id ", e);
            l.throwing(getClass().getName(), "getCoverageBaseId", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getCoverageBaseId", returnValue);
        }
        return returnValue;
    }

    /**
     * validate coverage copy
     *
     * @param inputRecord
     * @return validate status code statusCode
     */
    public String validateCopyAllCoverage(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateCopyAllCoverage", new Object[]{inputRecord});
        }

        String statusCode = null;
        // Create the input data mapping
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("fromCovgId", "coverageId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("toRiskBaseId", "toRiskBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "transEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("annRateB", "annualRateB"));
        mapping.addFieldMapping(new DataRecordFieldMapping("sharedIndB", "sharedLimitB"));
        mapping.addFieldMapping(new DataRecordFieldMapping("char1B", "covgChar1B"));
        mapping.addFieldMapping(new DataRecordFieldMapping("char2B", "covgChar2B"));
        mapping.addFieldMapping(new DataRecordFieldMapping("char3B", "covgChar3B"));
        mapping.addFieldMapping(new DataRecordFieldMapping("num1B", "covgNum1B"));
        mapping.addFieldMapping(new DataRecordFieldMapping("num2B", "covgNum2B"));
        mapping.addFieldMapping(new DataRecordFieldMapping("num3B", "covgNum3B"));
        mapping.addFieldMapping(new DataRecordFieldMapping("date1B", "covgDate1B"));
        mapping.addFieldMapping(new DataRecordFieldMapping("date2B", "covgDate2B"));
        mapping.addFieldMapping(new DataRecordFieldMapping("date3B", "covgDate3B"));

        //set constant fields
        inputRecord.setFieldValue("policyCycle", "POLICY");

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Val_Cpy.Val_Covg", mapping);
        try {
            statusCode = spDao.execute(inputRecord).getSummaryRecord().getStringValue("statusCode");
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to validate coverage copy", e);
            l.throwing(getClass().getName(), "validateCopyAllCoverage", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateCopyAllCoverage", statusCode);
        }

        return statusCode;
    }

    /**
     * delete all coverage from risk for delete risk all
     *
     * @param coverageRs
     */
    public void deleteAllCopiedCoverage(RecordSet coverageRs) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "deleteAllCopiedCoverage", new Object[]{coverageRs});
        }

        // Create the input data mapping
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("fromCoverageId", "coverageId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("toRiskBaseId", "toRiskBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExpDate", "termExp"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effFromForEndorse", "transEffectiveFromDate"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Copyall_Delete.Pm_Delete_Coverage", mapping);
        try {

            Iterator covgIter = coverageRs.getRecords();
            while (covgIter.hasNext()) {
                Record covgRec = (Record) covgIter.next();
                Record outputRecord = spDao.execute(covgRec).getSummaryRecord();
                RiskCopyFields.setToCoverageBaseRecordId(covgRec, outputRecord.getStringValue("delCovgBaseId"));
            }
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to delete all coverage from given risk", e);
            l.throwing(getClass().getName(), "deleteAllCopiedCoverage", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "deleteAllCopiedCoverage");
        }
    }

    /**
     * get coverage shared limit indicator
     * @param inputRecord
     * @return Y/N indicator
     */
    public String getCoverageLimitShared(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getCoverageLimitShared", new Object[]{inputRecord});
        }

        String returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Policy_Shared_Group.Is_Covg_Limit_Shared");
        try {
            returnValue = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get coverage limit shared indicator ", e);
            l.throwing(getClass().getName(), "getCoverageLimitShared", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getCoverageLimitShared");
        }

        return returnValue;
    }

    /**
     * Load all the prior carrier
     *
     * @param inputRecord
     * @param recordLoadProcessor
     * @return RecordSet
     */
    public RecordSet loadAllPriorCarrier(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllPriorCarrier", inputRecord);
        }
        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Coverage.Sel_Prior_Carrier");
        try {
            rs = spDao.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load all prior carrier.", e);
            l.throwing(getClass().getName(), "loadAllPriorCarrier", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllPriorCarrier", rs);
        }
        return rs;
    }

    /**
     * Load the current carrier
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllCurrentCarrier(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllCurrentCarrier", inputRecord);
        }
        RecordSet rs;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Sel_Excess_Covg", mapping);
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load current carrier.", e);
            l.throwing(getClass().getName(), "loadAllCurrentCarrier", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllCurrentCarrier", rs);
        }
        return rs;
    }

    /**
     * Save all parior carrier
     *
     * @param inputRecords
     * @return the number of rows updated
     */
    public int saveAllPriorCarrier(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllPriorCarrier", inputRecords);
        int updateCount;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Coverage.Save_Prior_Carrier");
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save all prior carrier.", e);
            l.throwing(getClass().getName(), "saveAllPriorCarrier", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllPriorCarrier", new Integer(updateCount));
        }
        return updateCount;
    }

    /**
     * Insert a current carrier
     *
     * @param inputRecord
     */
    public void insertCurrentCarrier(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "insertCurrentCarrier", inputRecord);
        int updateCount = 0;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("newId", "hospitalMiscInfoId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("entityId", "carrierEntityId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("brokerId", "brokerEntityId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("retroDate", "excessRetroDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "effectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "effectiveToDate"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Save_Screens.Insert_Excess", mapping);
        try {
            spDao.executeUpdate(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to insert current carrier.", e);
            l.throwing(getClass().getName(), "insertCurrentCarrier", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "insertCurrentCarrier", new Integer(updateCount));
        }
    }

    /**
     * Update the current carrier
     *
     * @param inputRecord
     */
    public void updateCurrentCarrier(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "updateCurrentCarrier", inputRecord);
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("oldId", "hospitalMiscInfoId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("entityId", "carrierEntityId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("brokerId", "brokerEntityId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("retroDate", "excessRetroDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "effectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "effectiveToDate"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Save_Screens.Update_Excess", mapping);
        try {
            spDao.executeUpdate(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to update current carrier.", e);
            l.throwing(getClass().getName(), "updateCurrentCarrier", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "updateCurrentCarrier");
        }
    }

    /**
     * Get product coverage type
     *
     * @param inputRecord
     * @return String
     */
    public String getProductCoverageType(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getProductCoverageType", new Object[]{inputRecord});
        }

        String returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_GET_PRDCOVG_TYPE");
        try {
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            returnValue = outputRecordSet.getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get product coverage code.", e);
            l.throwing(getClass().getName(), "getProductCoverageType", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getProductCoverageType", returnValue);
        }
        return returnValue;
    }

    /**
     * Return if manual excess button enable
     *
     * @param inputRecord
     * @return String
     */
    public String isManualExcessButtonEnable(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isManualExcessButtonEnable", new Object[]{inputRecord,});
        }

        String returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_M_XS_PREM.Man_XS_Button");
        try {
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            returnValue = outputRecordSet.getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get manual excess button information.", e);
            l.throwing(getClass().getName(), "isManualExcessButtonEnable", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isManualExcessButtonEnable", returnValue);
        }
        return returnValue;
    }

    /**
     * Check if add coverage allowed
     *
     * @param inputRecord
     * @return
     */
    public String isAddCoverageAllowed(Record inputRecord){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isAddCoverageAllowed", new Object[]{inputRecord});
        }
        String isAddCoverageAllowed;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Coverage.Is_Add_Coverage_Allowed");
        try {
            isAddCoverageAllowed = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get isAddCoverageAllowed.", e);
            l.throwing(getClass().getName(), "isAddCoverageAllowed", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isAddCoverageAllowed", isAddCoverageAllowed);
        }
        return isAddCoverageAllowed;
    }

    /**
     * Get short term coverage's effective from and effective to date
     *
     * @param inputRecord Input record containing risk and coverage level details
     * @return Record that contains coverage effective from date and effective to date.
     */
    public Record getShortTermCoverageEffAndExpDates(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getShortTermCoverageEffAndExpDates", new Object[]{inputRecord});
        }

        // get the return value
        Record returnRecord;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Web_Coverage.Get_Short_Term_Covg_Dates");
        try {
            returnRecord = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get coverage dates", e);
            l.throwing(getClass().getName(), "getShortTermCoverageEffAndExpDates", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getShortTermCoverageEffAndExpDates", returnRecord);
        }
        return returnRecord;
    }

    /**
     * Get the coverage chain status
     *
     * @param inputRecord
     * @return
     */
    public Record getCurrentCoverageStatus(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getCurrentCoverageStatus", new Object[]{inputRecord});
        }

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("endqId", "endorsementQuoteId"));
        // get the return value
        Record returnRecord;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("pm_chain.covg_status",mapping);
        try {
            returnRecord = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get coverage chain status", e);
            l.throwing(getClass().getName(), "getCurrentCoverageStatus", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getCurrentCoverageStatus", returnRecord);
        }
        return returnRecord;
    }

    /**
     * get risk start date
     *
     * @param inputRecord
     * @return
     */
    public String getRiskContiguousEffectiveDate(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRiskContiguousEffectiveDate", new Object[]{inputRecord});
        }
        String riskStartDate;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("riskBaseId", "riskBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("findDate", "riskEffectiveFromDate"));

        // Setup the stored procedure
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Dates.NB_Risk_StartDt",mapping);
        try {
            riskStartDate =
                spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);

        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get risk start date", e);
            l.throwing(getClass().getName(), "getRiskContiguousEffectiveDate", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRiskContiguousEffectiveDate", riskStartDate);
        }
        return riskStartDate;
    }

    /**
     * Validate for coverage duplicate.
     *
     * @param inputRecord
     * @return
     */
    public Record validateCoverageDuplicate(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateCoverageDuplicate", new Object[]{inputRecord});
        }

        RecordSet rs;
        Record returnRecord = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Validate_Data.New_Coverage_Duplicate");
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to validate coverage duplicate", e);
            l.throwing(getClass().getName(), "validateCoverageDuplicate", ae);
            throw ae;
        }
        if (rs != null) {
            returnRecord = rs.getSummaryRecord();
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateCoverageDuplicate", returnRecord);
        }
        return returnRecord;
    }

    /**
     * Get all coverages based on given ID list.
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllCoverageByIds(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllCoverageByIds", new Object[]{inputRecord});
        RecordSet rs = null;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("pks", "ids"));
        mapping.addFieldMapping(new DataRecordFieldMapping("fieldsList", "coverageFieldsList"));
        mapping.addFieldMapping(new DataRecordFieldMapping("dbFieldsList", "coverageDbFieldsList"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Misc.Sel_Data_By_Id_List", mapping);
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load coverages by gaven ID list:"
                + CoverageFields.getCoverageIds(inputRecord), e);
            l.throwing(getClass().getName(), "loadAllCoverageByIds", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllCoverageByIds", rs);
        }
        return rs;
    }

    /**
     * Returns a RecordSet loaded with list of new copied CM coverages
     * coverage information.
     * <p/>
     *
     * @param inputRecord         record with new copied CM coverage IDs.
     * @return RecordSet a RecordSet loaded with list of new copied CM coverages.
     */
    public RecordSet loadAllNewCopiedCMCoverage(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllNewCopiedCMCoverage", new Object[]{inputRecord});

        RecordSet rs = null;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping(RiskCopyFields.COVERAGE_PKS, CoverageFields.TO_COVG_BASE_RECORD_IDS));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Process_Copyall.Pm_Sel_New_Copied_CM_Coverage", mapping);
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load new copied CM coverage information", e);
            l.throwing(getClass().getName(), "loadAllNewCopiedCMCoverage", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllNewCopiedCMCoverage", rs);
        }
        return rs;
    }

    /**
     * Save all retro date of new copied CM coverages
     *
     * @param inputRecords
     */
    public int saveAllRetroDate(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllRetroDate", new Object[]{inputRecords});
        }

        int updateCount;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Process_Copyall.Pm_Update_Retro_Date");
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to update retro date information.", e);
            l.throwing(getClass().getName(), "saveAllRetroDate", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllRetroDate");
        }
        return updateCount;
    }
    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public CoverageJdbcDAO() {
    }
}
