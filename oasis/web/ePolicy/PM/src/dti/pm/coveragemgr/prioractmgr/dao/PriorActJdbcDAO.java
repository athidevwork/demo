package dti.pm.coveragemgr.prioractmgr.dao;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.util.LogUtils;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.busobjs.YesNoFlag;
import dti.pm.core.dao.BaseDAO;
import dti.pm.coveragemgr.CoverageFields;
import dti.pm.dao.DataFieldNames;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.sql.SQLException;

/**
  * This class implements the PriorActDAO interface.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 29, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/10/2011       wqfu        103799 - Added copyPriorActsStats,deleteAllPendPriorActs,
 *                                       loadAllPendPriorActRisk,loadAllPendPriorActCovg.
 * 01/08/2013       fcb         137981 - changes related to Pm_Dates modifications.
 * 04/26/2014       adeng       154011 - Correctly mapped "tranLogId" to "curTransactionId".
 * 12/22/2014       fcb         157919 - getCoveragePriorExpirationDate: fixed mapping.
 * 01/29/2015       xnie        160614 - 1) Renamed getCoveragePriorExpirationDate to getPriorActsRetroDate.
 *                                       2) Modified getProductCoverageCode to correct throw table name string.
 * 08/26/2016       wdang       167534 - Added isEditableForRenewalQuote.
 * 03/20/2017       lzhang      183097 - Modified getCoverageStartDate:
 *                                       invoke Pm_Dates.NB_SAMECOVG_Date
 * ---------------------------------------------------
 */
public class PriorActJdbcDAO extends BaseDAO implements PriorActDAO{
    /**
     * Load all prior act risk
     * @param inputRecord
     * @param recordLoadProcessor
     * @return RecordSet
     */
    public RecordSet loadAllPriorActRisk(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllPriorActRisk", new Object[]{inputRecord});
        }
        RecordSet rs;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("coverageBaseId", "coverageBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("covgVersionEff", "coverageEffectiveDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("retroDate", "coverageRetroDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));
        
        // Setup the stored procedure
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Sel_Nose_Stat",mapping);
        try {
            rs = spDao.execute(inputRecord,recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load prior act risks", e);
            l.throwing(getClass().getName(), "loadAllPriorActRisk", ae);
            throw ae;
        }

        return rs;
    }

    /**
     * Load all prior act coverage
     * @param inputRecord
     * @param recordLoadProcessor
     * @return RecordSet
     */
    public RecordSet loadAllPriorActCoverage(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllPriorActCoverage", new Object[]{inputRecord});
        }
        RecordSet rs;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("coverageBaseId", "coverageBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("covgVersionEff", "coverageEffectiveDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("retroDate", "coverageRetroDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));

        // Setup the stored procedure
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Sel_Nose_Covg",mapping);
        try {
            rs = spDao.execute(inputRecord,recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load prior act coverages", e);
            l.throwing(getClass().getName(), "loadAllPriorActCoverage", ae);
            throw ae;
        }

        return rs;
    }

    /**
     * save all temp prior act risk
     *
     * @param inputRecords
     * @return processed records count
     */
    public int saveAllTempPriorActRisk(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllTempPriorActRisk", new Object[]{inputRecords});
        }

        int processCount = 0;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("baseRiskId", "riskBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("countyCode", "riskCountyCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("altSpecialty", "specialty"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effFromDate", "riskEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effToDate", "riskEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("tranLogId", "curTransactionId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("practiceState", "practiceStateCode"));
        
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Nose.Save_Nose_Risk",mapping);
        try {
            processCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to save all temp prior act risks ", e);
            l.throwing(getClass().getName(), "saveAllTempPriorActRisk", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllTempPriorActRisk", String.valueOf(processCount));
        }

        return processCount;

    }


    /**
     * save all official prior act risk
     *
     * @param inputRecords
     * @return processed records count
     */
    public int saveAllOfficialPriorActRisk(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllOfficialPriorActRisk", new Object[]{inputRecords});
        }

        int processCount = 0;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("baseRiskId", "riskBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("countyCode", "riskCountyCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("altSpecialty", "specialty"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effFromDate", "riskEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effToDate", "riskEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("tranLogId", "curTransactionId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("practiceState", "practiceStateCode"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Nose.Endorse_Nose_Risk",mapping);
        try {
            processCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to save all official prior act risks ", e);
            l.throwing(getClass().getName(), "saveAllOfficialPriorActRisk", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllOfficialPriorActRisk", String.valueOf(processCount));
        }

        return processCount;
    }

    /**
     * save all temp prior act coverage
     *
     * @param inputRecords
     * @return processed records count
     */
    public int saveAllTempPriorActCoverage(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllTempPriorActCoverage", new Object[]{inputRecords});
        }

        int processCount = 0;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("baseCovgId", "coverageBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("baseRiskId", "riskBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("prodCovgCode", "productCoverageCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("covgLimitCode", "coverageLimitCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effFromDate", "coverageEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effToDate", "coverageEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("covgVersionEff", "coverageEffectiveDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("retroDate", "coverageRetroDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("tranLogId", "curTransactionId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("practiceState", "practiceStateCode"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Nose.Save_Nose_Covg",mapping);
        try {
            processCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to save all temp prior act coverages ", e);
            l.throwing(getClass().getName(), "saveAllTempPriorActCoverage", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllTempPriorActCoverage", String.valueOf(processCount));
        }

        return processCount;
    }

    /**
     * save all official prior act coverage
     *
     * @param inputRecords
     * @return processed records count
     */
    public int saveAllOfficialPriorActCoverage(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllOfficialPriorActCoverage", new Object[]{inputRecords});
        }

        int processCount = 0;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("baseCovgId", "coverageBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("baseRiskId", "riskBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("prodCovgCode", "productCoverageCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("covgLimitCode", "coverageLimitCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effFromDate", "coverageEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effToDate", "coverageEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("covgVersionEff", "coverageEffectiveDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("retroDate", "coverageRetroDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("tranLogId", "curTransactionId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("practiceState", "practiceStateCode"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Nose.Endorse_Nose_Covg",mapping);
        try {
            processCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to save all official prior act coverages ", e);
            l.throwing(getClass().getName(), "saveAllOfficialPriorActCoverage", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllOfficialPriorActCoverage", String.valueOf(processCount));
        }

        return processCount;
    }

    
    /**
     * delete all prior act risk
     *
     * @param inputRecords
     * @return
     */
    public int deleteAllPriorActRisk(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "deleteAllPriorActRisk", new Object[]{inputRecords});
        }

        int processCount = 0;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("tranLogId", "curTransactionId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Nose.Delete_Nose_Risk",mapping);
        try {
            processCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to delete all official prior act risk ", e);
            l.throwing(getClass().getName(), "deleteAllPriorActRisk", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "deleteAllPriorActRisk", String.valueOf(processCount));
        }

        return processCount;
    }

    /**
     * delete all prior act coverage
     *
     * @param inputRecords
     * @return
     */
    public int deleteAllPriorActCoverage(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "deleteAllPriorActCoverage", new Object[]{inputRecords});
        }

        int processCount = 0;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("tranLogId", "curTransactionId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Nose.Delete_Nose_Covg",mapping);
        try {
            processCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to delete all official prior act coverage ", e);
            l.throwing(getClass().getName(), "deleteAllPriorActCoverage", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "deleteAllPriorActCoverage", String.valueOf(processCount));
        }

        return processCount;
    }

    /**
     * return a boolean value to indicate if the prior acts has break
     *
     * @param inputRecord
     * @return boolean result
     */
    public boolean isPriorActsBreak(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isPriorActsBreak", new Object[]{inputRecord});
        }
        boolean isBreak;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("baseCovgId", "coverageBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("baseRiskId", "riskBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("tranLogId", "curTransactionId"));

        // Setup the stored procedure
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Nose.Valid_Breaks",mapping);
        try {
            isBreak = !YesNoFlag.getInstance(
                spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD))
                .booleanValue();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get isPriorActsBreak", e);
            l.throwing(getClass().getName(), "isPriorActsBreak", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isPriorActsBreak", String.valueOf(isBreak));
        }
        return isBreak;
    }


    /**
     * get risk start date
     *
     * @param inputRecord
     * @return
     */
    public String getRiskStartDate(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRiskStartDate", new Object[]{inputRecord});
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
            l.throwing(getClass().getName(), "getRiskStartDate", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRiskStartDate", riskStartDate);
        }
        return riskStartDate;
    }

    /**
     * get coverage start date
     *
     * @param inputRecord
     * @return
     */
    public String getCoverageStartDate(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getCoverageStartDate", new Object[]{inputRecord});
        }
        String covgStartDate;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("riskBaseId", "riskBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("coverageBaseId", "coverageBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "coverageEffectiveFromDate"));

        // Setup the stored procedure
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Dates.NB_SAMECOVG_Date",mapping);
        try {
            covgStartDate =
                spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);

        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get coverage start date", e);
            l.throwing(getClass().getName(), "getCoverageStartDate", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getCoverageStartDate", covgStartDate);
        }
        return covgStartDate;
    }


    /**
     * get prior acts coverage retroactive date
     *
     * @param inputRecord
     * @return
     */
    public String getPriorActsRetroDate(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPriorActsRetroDate", new Object[]{inputRecord});
        }
        String covgRetroDate;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("covgBaseId", CoverageFields.COVERAGE_BASE_RECORD_ID));
        mapping.addFieldMapping(new DataRecordFieldMapping("covgRetroDate", CoverageFields.RETRO_DATE));
        mapping.addFieldMapping(new DataRecordFieldMapping("covgEffDate", CoverageFields.COVERAGE_EFFECTIVE_FROM_DATE));

        // Setup the stored procedure
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Nose.Get_Covg_Retro_Date",mapping);
        try {
            covgRetroDate =
                spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);

        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable get prior acts coverage retroactive date", e);
            l.throwing(getClass().getName(), "getPriorActsRetroDate", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPriorActsRetroDate", covgRetroDate);
        }
        return covgRetroDate;
    }


    /**
     * get product coverage code
     *
     * @param inputRecord
     * @return product coverage code
     */
    public String getProductCoverageCode(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getProductCoverageCode", new Object[]{inputRecord });
        }
        String prdtCovgCode;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("covgCode", "commProductCoverageCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("stateCode", "practiceStateCode"));

        // Setup the stored procedure
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Get_Prod_Cvg_Code",mapping);
        try {
            prdtCovgCode =
                spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);

        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable get product coverage code", e);
            l.throwing(getClass().getName(), "getProductCoverageCode", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getProductCoverageCode", prdtCovgCode);
        }

        return prdtCovgCode;

    }


    /**
     * get active carrier count
     *
     * @param inputRecord
     * @return
     */
    public int getActiveCarrierCount(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getActiveCarrierCount", new Object[]{inputRecord });
        }
        int activeCarrierCount;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transId", "transactionLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("covgBaseId", "coverageBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("riskBaseId", "riskBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("riskId", "seledRiskId"));
        // Setup the stored procedure
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Nose.Get_Active_Carrier_Count",mapping);
        try {
            activeCarrierCount =
                spDao.execute(inputRecord).getSummaryRecord().getIntegerValue(StoredProcedureDAO.RETURN_VALUE_FIELD).intValue();

        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable get active carrier count", e);
            l.throwing(getClass().getName(), "getActiveCarrierCount", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getActiveCarrierCount", String.valueOf(activeCarrierCount));
        }

        return activeCarrierCount;
    }


    /**
     * get prior coverage count for delete risk page entitlement
     *
     * @param inputRecord
     * @return prior coverage count
     */
    public int getPriorActCoverageCount(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPriorActCoverageCount", new Object[]{inputRecord });
        }
        int covgCount;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transId", "transactionLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("covgBaseId", "coverageBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("riskBaseId", "riskBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("covgRetroDate", "coverageRetroDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("covgEffDate", "coverageEffectiveDate"));
        // Setup the stored procedure
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Nose.Get_Nose_Covg_Count",mapping);
        try {
            covgCount =
                spDao.execute(inputRecord).getSummaryRecord().getIntegerValue(StoredProcedureDAO.RETURN_VALUE_FIELD).intValue();

        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable get prior coverage count", e);
            l.throwing(getClass().getName(), "getPriorActCoverageCount", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPriorActCoverageCount", String.valueOf(covgCount));
        }

        return covgCount;
    }

    /**
     * get prior risk coverage count for validate prior acts data
     *
     * @param inputRecord
     * @return prior coverage count
     */
    public int getPriorActRiskCoverageCount(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPriorActRiskCoverageCount", new Object[]{inputRecord });
        }
        int covgCount;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transId", "transactionLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("covgBaseId", "coverageBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("riskBaseId", "riskBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("covgRetroDate", "coverageRetroDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("covgEffDate", "coverageEffectiveDate"));
        // Setup the stored procedure
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Nose.Get_Nose_Risk_Covg_Count",mapping);
        try {
            covgCount =
                spDao.execute(inputRecord).getSummaryRecord().getIntegerValue(StoredProcedureDAO.RETURN_VALUE_FIELD).intValue();

        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable get prior risk coverage count", e);
            l.throwing(getClass().getName(), "getPriorActRiskCoverageCount", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPriorActRiskCoverageCount", String.valueOf(covgCount));
        }

        return covgCount;
    }

    /**
     * get the minimal retro data of selected risk
     *
     * @param inputRecord
     * @return the minimal retro data of selected risk
     */
    public String getMinRetroDate(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getMinRetroDate", new Object[]{inputRecord });
        }
        String minRetroDate;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("riskBaseId", "riskBaseRecordId"));
        // Setup the stored procedure
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Nose.Get_Min_Retro_Date",mapping);
        try {
            minRetroDate =
                spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);

        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable get get Minimal Retro Date", e);
            l.throwing(getClass().getName(), "getMinRetroDate", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getMinRetroDate",minRetroDate);
        }

        return minRetroDate;
    }

    /**
     * Call stored procedure to copy prior acts stats
     *
     * @param inputRecord
     * @return
     */
    public void copyPriorActsStats(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "copyPriorActsStats", inputRecord);
        }

        // Setup the stored procedure
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Nose.Merge_Nose_Stat");
        try {
            spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to copy prior acts stats", e);
            l.throwing(getClass().getName(), "copyPriorActsStats", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "copyPriorActsStats");
        }
    }

    /**
     * Delete pending prior acts
     * @param inputRecord
     * @return
     */
    public void deleteAllPendPriorActs(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "deleteAllPendPriorActs", inputRecord);
        }

        // Setup the stored procedure
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Nose.Del_Pend_Nose_Stat");
        try {
            spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to delete pending prior acts", e);
            l.throwing(getClass().getName(), "deleteAllPendPriorActs", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "deleteAllPendPriorActs");
        }
    }

    /**
     * Load all pending prior act risk
     * @param inputRecord
     * @param recordLoadProcessor
     * @return RecordSet
     */
    public RecordSet loadAllPendPriorActRisk(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllPendPriorActRisk", new Object[]{inputRecord});
        }
        RecordSet rs;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("coverageBaseId", "coverageBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("covgVersionEff", "coverageEffectiveDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("retroDate", "coverageRetroDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));

        // Setup the stored procedure
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Nose.Sel_Pend_Nose_Risk",mapping);
        try {
            rs = spDao.execute(inputRecord,recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load pending prior act risks", e);
            l.throwing(getClass().getName(), "loadAllPendPriorActRisk", ae);
            throw ae;
        }

        return rs;
    }

    /**
     * Load all pending prior act coverage
     * @param inputRecord
     * @param recordLoadProcessor
     * @return RecordSet
     */
    public RecordSet loadAllPendPriorActCovg(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllPendPriorActCovg", new Object[]{inputRecord});
        }
        RecordSet rs;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("coverageBaseId", "coverageBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("covgVersionEff", "coverageEffectiveDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("retroDate", "coverageRetroDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));

        // Setup the stored procedure
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Nose.Sel_Pend_Nose_Covg",mapping);
        try {
            rs = spDao.execute(inputRecord,recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load pending prior act coverages", e);
            l.throwing(getClass().getName(), "loadAllPendPriorActCovg", ae);
            throw ae;
        }

        return rs;
    }

    @Override
    public boolean isEditableForRenewalQuote(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isEditableForRenewalQuote", new Object[]{inputRecord});
        }

        boolean returnValue = false;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Renewal_Quote.Is_Prior_Acts_Editable");
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            returnValue = outputRecordSet.getSummaryRecord().getBooleanValue(spDao.RETURN_VALUE_FIELD).booleanValue();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get Prior Acts Editable.", e);
            l.throwing(getClass().getName(), "isEditableForRenewalQuote", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isEditableForRenewalQuote", returnValue);
        }
        return returnValue;
    }
}
