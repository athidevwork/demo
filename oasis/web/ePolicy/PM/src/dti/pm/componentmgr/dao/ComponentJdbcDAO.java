package dti.pm.componentmgr.dao;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.util.LogUtils;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;
import dti.pm.core.dao.BaseDAO;
import dti.pm.componentmgr.ComponentFields;
import dti.pm.busobjs.ComponentOwner;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.sql.SQLException;

/**
 * This class implements the ComponentDAO interface.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 18, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/17/2007       fcb         loadAllAvailableComponent: mapping adde dfor coverageId
 * 01/15/2009       yhyang      #89108 Add five methods for Process RM component: loadAllProcessingEvent,
 *                              loadAllProcessingDetail,saveAllProcessingEvent,processEvent,processRmDiscount.
 * 01/20/2009       yhyang      #89109 Add four methods for Porcess Org/Corp Component:loadAllCorpOrgDiscountMember,
 *                              processCorpOrgDiscount,loadAllProcessEventHistory,loadAllProcessDetailHistory.
 * 08/06/2010       syang       110662 - Modified updateAllComponents() to map transEffectiveFromDate to effFromDate.
 * 05/18/2011       dzhang      #117246 Added isAddComponentAllowed and getShortTermCompEffAndExpDates.
 * 06/10/2011       wqfu        103799 - Added loadAllPendPriorActComp.
 * 09/16/2011       ryzhao      122840 - Added isComponentTempRecordExist.
 * 04/26/2012       hxu         132111 - Removed getShortTermCompEffAndExpDates.
 * 04/27/2012       jshen       132111 - roll back the change
 * 01/08/2013       fcb         137981 - changes related to Pm_Dates modifications.
 * 08/29/2013       adeng       146449 - Modified updateAllComponents() to mapping effFromDate to
 *                                       componentEffectiveFromDate, in order to address records whose owner is prior act.
 * 10/14/2013       xnie        146082 - Added isOoseChangeDateAllowed() to check if component expiring date can be
 *                                       changed in out of sequence transaction.
 * 01/24/2013       jyang       150639 - Add getCoverageExpirationDate method.
 * 04/28/2014       sxm         154227 - Remove call to PM_CopyAll_Component
 * 09/16/2014       awu         157552 - Add validateComponentDuplicate.
 * 11/18/2014       fcb         157975 - Added getNddSkipValidateB.
 * 06/28/2017       tzeng       186273 - Modified loadAllAvailableComponent to map main coverage id.
 * 08/28/2018       ryzhao      188891 - Added loadExpHistoryInfo() and loadClaimInfo() for new experience discount
 *                                       history page.
 * ---------------------------------------------------
 */
public class ComponentJdbcDAO extends BaseDAO implements ComponentDAO {
    /**
     * Retrieves all coverage components
     *
     * @param record              input record
     * @param recordLoadProcessor an instance of the load processor to set page entitlements
     * @return
     */
    public RecordSet loadAllComponents(Record record, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllComponents", new Object[]{record, recordLoadProcessor});
        }

        RecordSet rs;
        try {

            DataRecordMapping mapping = new DataRecordMapping();

            ComponentOwner owner = ComponentOwner.getInstance(ComponentFields.getComponentOwner(record));
            if (owner.isPriorActOwner()) {
                mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "coverageRetroDate"));
                mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "coverageEffectiveDate"));
                mapping.addFieldMapping(new DataRecordFieldMapping("endQuoteId", "endorsementQuoteId"));

                record.setFieldValue("typeCode", "PRIORACTS");
                record.setFieldValue("componentOwner", "COVERAGE");
            }
            else {
                mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
                mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));
                mapping.addFieldMapping(new DataRecordFieldMapping("typeCode", "componentTypeCode"));
                mapping.addFieldMapping(new DataRecordFieldMapping("endQuoteId", "endorsementQuoteId"));
            }


            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Sel_Dis_Ded_Sur_Info", mapping);
            rs = spDao.execute(record, recordLoadProcessor);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllComponents", rs);
            }
            return rs;

        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load Components information", e);
            l.throwing(getClass().getName(), "loadAllComponents", ae);
            throw ae;
        }
    }

    /**
     * Save all input component records with the Pm_Nb_End.Save_Covg_Component stored procedure.
     * Set the rowStatus field to NEW for records that are newly added in this request.
     * Set the rowStatus field to MODIFIED for records that have already been saved in this WIP transaction,
     * and are just being updated.
     *
     * @param inputRecords a set of Records, each with the PolicyHeader, PolicyIdentifier,
     *                     and Component Detail info matching the fields returned from the loadAllComponents method.
     * @return the number of rows updated.
     */
    public int addAllComponents(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "addAllComponents", new Object[]{inputRecords});

        int updateCount;

        // Create a DataRecordMapping for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("polCovCompId", "policyCovComponentId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("prodCovCompId", "productCovComponentId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("covCompBaseId", "polCovCompBaseRecId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("coverageBaseId", "coverageBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effFromDate", "componentEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effToDate", "componentEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cycleDate", "componentCycleDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("offRec", "officialRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveTo"));
        mapping.addFieldMapping(new DataRecordFieldMapping("renewB", "renewalB"));
        mapping.addFieldMapping(new DataRecordFieldMapping("afterImageB", "afterImageRecordB"));
        mapping.addFieldMapping(new DataRecordFieldMapping("prorateB", "toProrateB"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Nb_End.Save_Covg_Component", mapping);
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save inserted/updated components.", e);
            l.throwing(getClass().getName(), "addAllComponents", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addAllComponents", new Integer(updateCount));
        }

        return updateCount;
    }

    /**
     * Update all given input records with the Pm_Endorse.Change_Covg_Component stored procedure.
     *
     * @param inputRecords a set of Records, each with the PolicyHeader, PolicyIdentifier,
     *                     and Component Detail info matching the fields returned from the loadAllComponents method.
     * @return the number of rows updated.
     */
    public int updateAllComponents(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "updateAllComponents", new Object[]{inputRecords});

        int updateCount;

        // Create a DataRecordMapping for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("polCovCompId", "policyCovComponentId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("prodCovCompId", "productCovComponentId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("covCompBaseId", "polCovCompBaseRecId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("coverageBaseId", "coverageBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effFromDate", "componentEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effToDate", "componentEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cycleDate", "componentCycleDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("renewB", "renewalB"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Endorse.Change_Covg_Component", mapping);
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to endorse components.", e);
            l.throwing(getClass().getName(), "updateAllComponents", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "updateAllComponents", new Integer(updateCount));
        }

        return updateCount;
    }

    /**
     * Delete all given input records with the Pm_Nb_Del.Del_Covg_Component stored procedure.
     *
     * @param inputRecords a set of Records, each with the PolicyHeader, PolicyIdentifier,
     *                     and Component Detail info matching the fields returned from the loadAllComponents method.
     * @return the number of rows updated.
     */
    public int deleteAllComponents(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "deleteAllComponents", new Object[]{inputRecords});

        int updateCount;
        // Create a DataRecordMapping for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("CovgCompId", "policyCovComponentId"));

        // Delete the records in batch mode with 'Pm_Nb_Del.Del_Covg_Component'
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Nb_Del.Del_Covg_Component", mapping);
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to delete coverage components.", e);
            l.throwing(getClass().getName(), "deleteAllComponents", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "deleteAllComponents", new Integer(updateCount));
        }

        return updateCount;
    }

    /**
     * Get the Cancel WIP rule
     *
     * @param record
     * @return
     */
    public Record getCancelWipRule(Record record) {
        Logger l = LogUtils.enterLog(getClass(), "getCancelWipRule", new Object[]{record});

        Record rec;
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("polType", "policyTypeCode"));
            mapping.addFieldMapping(new DataRecordFieldMapping("issCompId", "issueCompanyEntityId"));

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Get_CanWip_Rule", mapping);
            RecordSet rs = spDao.execute(record);

            rec = rs.getSummaryRecord();

        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to get Cancel WIP Rule", e);
            l.throwing(getClass().getName(), "getCancelWipRule", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "getCancelWipRule", rec);
        return rec;
    }

    /**
     * Get the component cycle years
     *
     * @param record
     * @return
     */
    public int getCycleYearsForComponent(Record record) {
        Logger l = LogUtils.enterLog(getClass(), "getCycleYearsForComponent", new Object[]{record});

        int cycleYears;
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("prodCompId", "productCovComponentId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "componentEffectiveFromDate"));

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Get_Comp_Cycle_Years", mapping);
            RecordSet rs = spDao.execute(record);

            cycleYears = rs.getSummaryRecord().getIntegerValue(StoredProcedureDAO.RETURN_VALUE_FIELD).intValue();

        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to get Component Cycle Years", e);
            l.throwing(getClass().getName(), "getCycleYearsForComponent", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "getCycleYearsForComponent", new Integer(cycleYears));
        return cycleYears;
    }

    /**
     * Get the component num days
     *
     * @param record
     * @return
     */
    public int getNumDaysForComponent(Record record) {
        Logger l = LogUtils.enterLog(getClass(), "getNumDaysForComponent", new Object[]{record});

        int numDays;
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("from", "componentCycleDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("to", "componentEffectiveFromDate"));

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Utility.Num_Days_String", mapping);
            RecordSet rs = spDao.execute(record);

            numDays = rs.getSummaryRecord().getIntegerValue(StoredProcedureDAO.RETURN_VALUE_FIELD).intValue();

        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to get Component Num Days", e);
            l.throwing(getClass().getName(), "getNumDaysForComponent", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "getNumDaysForComponent", new Integer(numDays));
        return numDays;
    }

    /**
     * To load all dependent components
     *
     * @param record
     * @param recordLoadProcessor
     * @return
     */
    public RecordSet loadAllAvailableComponent(Record record, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAvailableComponent", new Object[]{record, recordLoadProcessor});
        }

        RecordSet rs;
        try {

            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("prodCovgCode", "productCoverageCode"));
            mapping.addFieldMapping(new DataRecordFieldMapping("covgEffDate", "coverageEffectiveFromDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("transactionId", "transactionLogId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("parentCovCompCode", "componentParent"));
            mapping.addFieldMapping(new DataRecordFieldMapping("coverageId", ComponentFields.COVERAGE_BASE_RECORD_ID));
            mapping.addFieldMapping(new DataRecordFieldMapping("mainCoverageId", ComponentFields.MAIN_COVERAGE_BASE_RECORD_ID));

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Sel_Covg_Component", mapping);
            rs = spDao.execute(record, recordLoadProcessor);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllAvailableComponent", rs);
            }
            return rs;

        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load dependent Components information", e);
            l.throwing(getClass().getName(), "loadAllAvailableComponent", ae);
            throw ae;
        }
    }

    /**
     * Get the earliest contiguous coverage effective date with the function Pm_Dates.Nb_Covg_Startdt(coverage_fk, check_dt)
     *
     * @param record
     * @return
     */
    public Record getCoverageContiguousEffectiveDate(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getCoverageContiguousEffectiveDate", new Object[]{record});
        }

        Record rec;
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("covgBaseId", "coverageBaseRecordId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("findDate", "checkDt"));

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Dates.Nb_Covg_Startdt", mapping);
            RecordSet rs = spDao.execute(record);

            rec = rs.getSummaryRecord();

        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to get the earliest contiguous effective date", e);
            l.throwing(getClass().getName(), "getCoverageContiguousEffectiveDate", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "getCoverageContiguousEffectiveDate", rec);
        return rec;
    }

    /**
     * Get component PK and base record FK
     *
     * @param record
     * @return
     */
    public Record getComponentIdAndBaseId(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getComponentIdAndBaseId", new Object[]{record});
        }

        Record rec;
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("covgBase", "coverageBaseRecordId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("prodComp", "productCovComponentId"));

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Component.Get_Component_PK_and_Base_FK",
                mapping);
            RecordSet rs = spDao.execute(record);

            rec = rs.getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to get Component PK and base record Fk", e);
            l.throwing(getClass().getName(), "getComponentIdAndBaseId", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getComponentIdAndBaseId", rec);
        }
        return rec;
    }

    /**
     * Load Cyecle detail
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllCycleDetail(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllCycleDetail", new Object[]{inputRecord});
        }

        RecordSet rs;
        try {

            // Create data mapping
            DataRecordMapping mapping = new DataRecordMapping();
            
            mapping.addFieldMapping(new DataRecordFieldMapping("riskBaseId", "riskBaseRecordId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("prodCovCompId", "productCovComponentId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("discEffectFrom", "componentEffectiveFromDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("discEffectTo", "componentEffectiveToDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("discCycleDate", "componentCycleDate"));

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Get_Ndd_Info", mapping);
            rs = spDao.execute(inputRecord);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllCycleDetail", rs);
            }
            return rs;

        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load cycle detail information", e);
            l.throwing(getClass().getName(), "loadAllCycleDetail", ae);
            throw ae;
        }
    }

    /**
     * Load Surcharge Points
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllSurchargePoint(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllSurchargePoint", new Object[]{inputRecord});
        }

        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Component.Sel_Surcharge_Points");
            rs = spDao.execute(inputRecord);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllSurchargePoint", rs);
            }
            return rs;

        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load Surcharge Point information", e);
            l.throwing(getClass().getName(), "loadAllSurchargePoint", ae);
            throw ae;
        }
    }

    /**
     * Save all surcharge points data.
     *
     * @param inputRecords intput record
     * @return the number of row updateds
     */
    public int saveAllSurchargePoint(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllSurchargePoint");

        int updateCount = 0;

        // Create DataRecordMappig for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("compPointOverrideId", "pmComponentPointOverrideId"));

        // update the records in batch mode
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Component.Save_Surcharge_Points", mapping);
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save surcharge points.", e);
            l.throwing(getClass().getName(), "saveAllSurchargePoint", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllSurchargePoint", new Integer(updateCount));
        }
        return updateCount;
    }

    /**
     * validate component copy
     *
     * @param inputRecord
     * @return validate status code statusCode
     */
    public String validateCopyAllComponent(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateCopyAllComponent", new Object[]{inputRecord});
        }

        String statusCode = null;
        // Create the input data mapping
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("toRiskBaseId", "toRiskBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("fromComponentId", "policyCovComponentId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("toCovgBaseId", "toCoverageBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "transEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "termEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("compVal", "componentValue"));
        mapping.addFieldMapping(new DataRecordFieldMapping("incValB", "incidentValueB"));
        mapping.addFieldMapping(new DataRecordFieldMapping("aggValB", "aggregateValueB"));
        mapping.addFieldMapping(new DataRecordFieldMapping("char1B", "compChar1B"));
        mapping.addFieldMapping(new DataRecordFieldMapping("char2B", "compChar2B"));
        mapping.addFieldMapping(new DataRecordFieldMapping("char3B", "compChar3B"));
        mapping.addFieldMapping(new DataRecordFieldMapping("num1B", "compNum1B"));
        mapping.addFieldMapping(new DataRecordFieldMapping("num2B", "compNum2B"));
        mapping.addFieldMapping(new DataRecordFieldMapping("num3B", "compNum3B"));
        mapping.addFieldMapping(new DataRecordFieldMapping("date1B", "compDate1B"));
        mapping.addFieldMapping(new DataRecordFieldMapping("date2B", "compDate2B"));
        mapping.addFieldMapping(new DataRecordFieldMapping("date3B", "compDate3B"));
        
        //set constant fields
        inputRecord.setFieldValue("policyCycle", "POLICY");
        inputRecord.setFieldValue("compValB", "Y");

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Val_Cpy.Val_Comp", mapping);
        try {
            statusCode = spDao.execute(inputRecord).getSummaryRecord().getStringValue("statusCode");
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to validate component copy", e);
            l.throwing(getClass().getName(), "validateCopyAllComponent", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateCopyAllComponent", statusCode);
        }

        return statusCode;
    }

    /**
     * delete all component from coverage for delete risk all
     *
     * @param compRs
     */
    public void deleteAllCopiedComponent(RecordSet compRs) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "deleteAllCopiedComponent", new Object[]{compRs});
        }

        // Create the input data mapping
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("fromComponentId", "policyCovComponentId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("toCoverageBaseId", "toCoverageBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termEffective", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExpiration", "termExp"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effDtForEndorse", "transEffectiveFromDate"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Copyall_Delete.Pm_Delete_Component", mapping);
        try {
            spDao.executeBatch(compRs);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to delete all component from given coverage", e);
            l.throwing(getClass().getName(), "deleteAllCopiedComponent", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "deleteAllCopiedComponent");
        }
    }

    /**
     * Load all processing event.
     *
     * @param inputRecord
     * @param entitlementRLP
     * @return RecordSet
     */
    public RecordSet loadAllProcessingEvent(Record inputRecord, RecordLoadProcessor entitlementRLP) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllProcessingEvent", inputRecord);
        }
        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Process_Rm_Disc.Sel_All_Processing_Event");
        try {
            rs = spDao.execute(inputRecord, entitlementRLP);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load processing event.", e);
            l.throwing(getClass().getName(), "loadAllProcessingEvent", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllProcessingEvent", rs);
        }
        return rs;
    }

    /**
     * Load all processing detail.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllProcessingDetail(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllProcessingDetail", inputRecord);
        }
        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Process_Rm_Disc.Sel_All_Processing_Detail");
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load processing detail.", e);
            l.throwing(getClass().getName(), "loadAllProcessingDetail", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllProcessingDetail", rs);
        }
        return rs;
    }

    /**
     * Save all processing event.
     *
     * @param inputRecords
     * @return the number of row updated
     */
    public int saveAllProcessingEvent(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllProcessingEvent", inputRecords);
        int updateCount = 0;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Process_Rm_Disc.Save_All_Processing_Event");
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save processing event.", e);
            l.throwing(getClass().getName(), "saveAllProcessingEvent", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllProcessingEvent", new Integer(updateCount));
        }
        return updateCount;
    }

    /**
     * Set RMT Classification indicator.
     *
     * @param inputRecord
     */
    public void setRMTIndicator(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "setRMTIndicator", inputRecord);
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Process_Rm_Disc.Set_RMT_Indicator");
        try {
            RecordSet rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to set RMT indicator.", e);
            l.throwing(getClass().getName(), "setRMTIndicator", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setRMTIndicator");
        }
    }

    /**
     * Process RM Discount
     *
     * @param inputRecord
     * @return Record
     */
    public Record processRmDiscount(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "processRmDiscount", inputRecord);
        Record record;
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("rmProcMstrId", "pmRmProcessMstrId"));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Process_Rm_Disc.Process_Rm_Discount", mapping);
            RecordSet rs = spDao.execute(inputRecord);
            record = rs.getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to process Rm discount", e);
            l.throwing(getClass().getName(), "processRmDiscount", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "processRmDiscount", record);
        return record;
    }

    /**
     * Load all Corp/Org discount member.
     *
     * @param inputRecord
     * @param entitlementRLP
     * @return RecordSet
     */
    public RecordSet loadAllCorpOrgDiscountMember(Record inputRecord, RecordLoadProcessor entitlementRLP) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllCorpOrgDiscountMember", inputRecord);
        }
        RecordSet rs;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("corpPhysInd", "discountType"));
        mapping.addFieldMapping(new DataRecordFieldMapping("corpEntId", "parentOrganizationEntityId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transEff", "transactionEffectiveDate"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_SEL_CORPORG_DISC_REL", mapping);
        try {
            rs = spDao.execute(inputRecord, entitlementRLP);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load Corp/Org discount memeber.", e);
            l.throwing(getClass().getName(), "loadAllCorpOrgDiscountMember", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllCorpOrgDiscountMember", rs);
        }
        return rs;
    }

    /**
     * Process Corp/Org discount
     *
     * @param inputRecord
     * @return Record
     */
    public Record processCorpOrgDiscount(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processCorpOrgDiscount", inputRecord);
        }
        Record record;
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("discType", "discountType"));
            mapping.addFieldMapping(new DataRecordFieldMapping("corpEntityId", "parentOrganizationEntityId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("transEff", "transactionEffectiveDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("nbrOfRisks", "numberOfMember"));
            mapping.addFieldMapping(new DataRecordFieldMapping("riskFkString", "riskIdString"));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Component.Process_Web_CorpOrg_Discount", mapping);
            RecordSet rs = spDao.execute(inputRecord);
            record = rs.getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to process Corp/Org discount", e);
            l.throwing(getClass().getName(), "processCorpOrgDiscount", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "processCorpOrgDiscount", record);
        }
        return record;
    }

    /**
     * Load all processing event history
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllProcessEventHistory(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllProcessEventHistory", inputRecord);
        }
        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Component.Sel_Process_Event_History");
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load processing event history.", e);
            l.throwing(getClass().getName(), "loadAllProcessEventHistory", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllProcessEventHistory", rs);
        }
        return rs;
    }

    /**
     * Load all processing detail history
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllProcessDetailHistory(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllProcessDetailHistory", inputRecord);
        }
        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Component.Sel_Process_Detail_History");
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load processing detail history.", e);
            l.throwing(getClass().getName(), "loadAllProcessDetailHistory", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllProcessDetailHistory", rs);
        }
        return rs;
    }

    /**
     * Apply the component
     *
     * @param inputRecord
     * @return Record
     */
    public Record applyMassComponet(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "applyMassComponet", inputRecord);
        Record outputRecord;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transId", "transactionLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termId", "termBaseRecordId"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_WEB_QUICK_QUOTE.Apply_Components_Action", mapping);
        try {
            RecordSet rs = spDao.execute(inputRecord);
            outputRecord = rs.getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to apply component.", e);
            l.throwing(getClass().getName(), "applyMassComponet", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "applyMassComponet", outputRecord);
        }
        return outputRecord;
    }

    /**
     * Check if it is a problem policy
     *
     * @param inputRecord
     * @return String
     */
    public String isProblemPolicy(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isProblemPolicy", new Object[]{inputRecord});
        }

        String returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_REINST_ADD_COMP");
        try {
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            returnValue = outputRecordSet.getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to check if it is problem policy.", e);
            l.throwing(getClass().getName(), "isProblemPolicy", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isProblemPolicy", returnValue);
        }
        return returnValue;
    }

    /**
     * Check if add component allowed
     *
     * @param inputRecord
     * @return
     */
   public String isAddComponentAllowed(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isAddComponentAllowed", new Object[]{inputRecord});
        }
        String isAddComponentAllowed;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Component.Is_Add_Component_Allowed");
        try {
            isAddComponentAllowed = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get isAddComponentAllowed.", e);
            l.throwing(getClass().getName(), "isAddComponentAllowed", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isAddComponentAllowed", isAddComponentAllowed);
        }
        return isAddComponentAllowed;
    }

    /**
     * Get short term component's effective from and effective to date
     *
     * @param inputRecord Input record containing risk and coverage level details
     * @return Record that contains component effective from date and effective to date.
     */
    public Record getShortTermCompEffAndExpDates(Record inputRecord){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getShortTermCompEffAndExpDates", new Object[]{inputRecord});
        }

        // get the return value
        Record returnRecord;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Component.Get_Short_Term_Comp_Dates");
        try {
            returnRecord = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get component dates", e);
            l.throwing(getClass().getName(), "getShortTermCompEffAndExpDates", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getShortTermCompEffAndExpDates", returnRecord);
        }
        return returnRecord;
    }

    /**
     * Retrieves all pending prior act coverage components
     *
     * @param record              input record
     * @param recordLoadProcessor an instance of the load processor to set page entitlements
     * @return
     */
    public RecordSet loadAllPendPriorActComp(Record record, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllPendPriorActComp", new Object[]{record, recordLoadProcessor});
        }

        RecordSet rs;
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "coverageRetroDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "coverageEffectiveDate"));

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Nose.Sel_Pend_Nose_Comp", mapping);
            rs = spDao.execute(record, recordLoadProcessor);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllPendPriorActComp", rs);
            }
            return rs;
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load pending prior act Components information", e);
            l.throwing(getClass().getName(), "loadAllPendPriorActComp", ae);
            throw ae;
        }
    }

    /**
     * Check if the official component record has a temp record exists for the specific transaction.
     *
     * @param inputRecord include component base record id and transaction id
     * @return true if component temp record exists
     *         false if component temp record does not exist
     */
    public boolean isComponentTempRecordExist(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isComponentTempRecordExist", new Object[]{inputRecord});
        }

        String isComponentTempRecordExist;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("covCompBaseId", "polCovCompBaseRecId"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Component_Temp_Record_Exist", mapping);
        try {
            isComponentTempRecordExist = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get isComponentTempRecordExist.", e);
            l.throwing(getClass().getName(), "isComponentTempRecordExist", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isComponentTempRecordExist", isComponentTempRecordExist);
        }
        return YesNoFlag.getInstance(isComponentTempRecordExist).booleanValue();
    }

    /**
     * Check if changing component expiring date in OOSE is allowed
     *
     * @param inputRecord
     * @return
     */
    public String isOoseChangeDateAllowed(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isOoseChangeDateAllowed", new Object[]{inputRecord});
        }
        String isOoseChangeDateAllowed;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("baseId", ComponentFields.POL_COV_COMP_BASE_REC_ID));
        mapping.addFieldMapping(new DataRecordFieldMapping("changedDate", ComponentFields.EFFECTIVE_TO_DATE));
        mapping.addFieldMapping(new DataRecordFieldMapping("ooseExpDate", ComponentFields.TERM_EFFECTIVE_TO_DATE));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Component.Is_OOSE_Change_Date_Allowed", mapping);
        try {
            isOoseChangeDateAllowed = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get isOoseChangeDateAllowed.", e);
            l.throwing(getClass().getName(), "isOoseChangeDateAllowed", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isOoseChangeDateAllowed", isOoseChangeDateAllowed);
        }
        return isOoseChangeDateAllowed;
    }

    /**
     * Load effective to date with PM_Dates.NB_Covg_ExpDt stored procedure.
     * <p/>
     *
     * @param inputRecord a Record with information to load the effective to date.
     * @return Coverage effective to date.
     */
    public String getCoverageExpirationDate(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getCoverageExpirationDate", new Object[]{inputRecord});
        }

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("covgBaseId", "coverageBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("findDate", "coverageEffectiveFromDate"));

        String expirationDate;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Dates.NB_Covg_ExpDt", mapping);
        try {
            expirationDate = spDao.execute(inputRecord).getSummaryRecord().getStringValue(
                StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get coverage expiration date.", e);
            l.throwing(getClass().getName(), "getCoverageExpirationDate", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getCoverageExpirationDate", expirationDate);
        }
        return expirationDate;
    }

    /**
     * Validate component duplicate.
     * @param inputRecord
     * @return
     */
    public Record validateComponentDuplicate(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateComponentDuplicate", new Object[]{inputRecord});
        }

        RecordSet rs = null;
        Record outputRec = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Validate_Data.New_Component_Duplicate");
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable validate component duplicate.", e);
            l.throwing(getClass().getName(), "validateComponentDuplicate", ae);
            throw ae;
        }
        if (rs != null) {
            outputRec = rs.getSummaryRecord();
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateComponentDuplicate", outputRec);
        }
        return outputRec;
    }

    /**
     * Check if the NDD expiration date is configured for the component.
     *
     * @param inputRecord include component base record id and transaction id
     * @return true if configured
     *         false if not configured
     */
    public boolean getNddSkipValidateB(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getNddSkipValidateB", new Object[]{inputRecord});
        }

        boolean isValidate = true;

        DataRecordMapping mapping = new DataRecordMapping();

        mapping.addFieldMapping(new DataRecordFieldMapping("productcoveragecode", "compproductcoveragecode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("coveragecomponentcode", "code"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termeffectivefrom"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Validate_Data.Is_Skip_Newdoctor_Validate", mapping);
        try {
            isValidate = YesNoFlag.getInstance(
                spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD)).booleanValue();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get NDD validate exp date flag.", e);
            l.throwing(getClass().getName(), "getNddSkipValidateB", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getNddSkipValidateB", isValidate);
        }

        return isValidate;
    }

    /**
     * Load experience discount history information.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadExpHistoryInfo(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadExpHistoryInfo", inputRecord);
        }
        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Custom.Load_Exp_History_Info");
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load experience discount history information.", e);
            l.throwing(getClass().getName(), "loadExpHistoryInfo", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadExpHistoryInfo", rs);
        }
        return rs;
    }

    /**
     * Load claim information for a specific period of the risk.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadClaimInfo(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadClaimInfo", inputRecord);
        }
        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Custom.Load_Claim_Info");
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load claim information.", e);
            l.throwing(getClass().getName(), "loadClaimInfo", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadClaimInfo", rs);
        }
        return rs;
    }

}

