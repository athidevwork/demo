package dti.pm.transactionmgr.cancelprocessmgr.dao;

import dti.pm.core.dao.BaseDAO;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.util.LogUtils;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.busobjs.YesNoFlag;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.sql.SQLException;

/**
 * This class implements the CancelProcessDAO interface. This is consumed by any business logic objects
 * that handles cancel operation.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 4, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/31/2008       yhyang      #87658 Change the isTailExistForTerm to getTailTerm.
 * 02/18/2009       yhyang      #84592 Change performCancellation and processAmalgamation for amalgamation.
 * 09/01/2010       fcb         111109 validateRenewalWipCancellation added.
 * 01/11/2011       ryzhao      113558 Add isNewCarrierEnabled method.
 *                                     Map "carrier" to "parms" for Pm_Process_Transaction.web_process_cancellation procedure.
 * 01/14/2011       ryzhao      113558 Map "carrier" to "parms" moved.
 * 01/15/20101      syang       105832 - Added saveAllDisciplineDeclineEntity() to save discipline decline entity.
 * 02/28/2012       xnie        130244 - Modified cancelAllComponent to pass coverageBaseRecordId to Pm_Save_Data.Main.
 * 03/02/2012       xnie        130244 - Roll backed prior version.
 * 03/21/2012       xnie        130643 - Renamed purgePolicy to cancelPolicy.
 * 08/29/2011       ryzhao      133360 - Change the return type of isAllRiskOwnersSelected from YesNoFlag to Record
 *                                       which includes two return fields.
 * 06/21/2013       adeng       117011 - Added field mapping transactionComment2 to newTransactionComment2.
 * 06/27/2013       jshen       146155 - Modified cancelAllComponent method to pass effectiveFromDate into procedure.
 * 05/14/2014       jyang       153212 - Modified processAmalgamation method use cancellationDate for amalgamationDate.
 * 05/09/2014       awu         152675 - Added validateCancelCoverage.
 * 07/04/2016       eyin        176476 - Changed validateCancellation, validateCancelRisk, validateCancelCoverage and
 *                                       validateCancelCoverageClass, to return type of output parameter from Record to
 *                                       RecordSet, which includes future cancellation records.
 * ---------------------------------------------------
 */

public class CancelProcessJdbcDAO extends BaseDAO implements CancelProcessDAO {
    /**
     * to check if the entity is solo owner
     *
     * @param inputRecord
     * @return boolean value to indicate if the entity is solo owner
     */
    public boolean isSoloOwner(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "isSoloOwner");
        boolean isSoloOwner = false;
        Record outputRecord;
        /* Create DataRecordMappig for this stored procedure */
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("tranEffDate", "cancellationDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("riskBaseId", "baseId"));

        // call PM_IS_SOLO_OWNER function to check if the entity is solo owner
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_IS_SOLO_OWNER", mapping);
        try {
            outputRecord = spDao.execute(inputRecord).getSummaryRecord();
            if (outputRecord.getIntegerValue(StoredProcedureDAO.RETURN_VALUE_FIELD).intValue() == 1) {
                isSoloOwner = true;
            }
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to check is Solo Owner.", e);
            l.throwing(getClass().getName(), "isSoloOwner", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isSoloOwner", Boolean.valueOf(isSoloOwner));
        }
        return isSoloOwner;
    }

    /**
     * to check if the tail need to be created
     *
     * @param inputRecord
     * @return boolean value to indicate if the tail need to be created
     */
    public boolean isToCreateTail(Record inputRecord) {

        Logger l = LogUtils.enterLog(getClass(), "isToCreateTail");
        boolean isToCreateTail = false;
        Record outputRecord;
        /* Create DataRecordMappig for this stored procedure */
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("cancelDate", "cancellationDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cancelLevel", "cancellationLevel"));

        // call Pm_Tail.Pm_Create_Tail_B function to check if need to create tail
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Tail.Pm_Create_Tail_B", mapping);
        try {
            outputRecord = spDao.execute(inputRecord).getSummaryRecord();
            if (outputRecord.getBooleanValue(StoredProcedureDAO.RETURN_VALUE_FIELD).booleanValue()) {
                isToCreateTail = true;
            }
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to check weather is to create tail.", e);
            l.throwing(getClass().getName(), "isToCreateTail", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isToCreateTail", Boolean.valueOf(isToCreateTail));
        }
        return isToCreateTail;
    }

    /**
     * to check if the cancellation is valid
     *
     * @param inputRecord
     * @return record include the result and error message
     */
    public RecordSet validateCancellation(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validateCancellation");

        RecordSet outputRecords;
        /* Create DataRecordMappig for this stored procedure */
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("termId", "termBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("level", "cancellationLevel"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cancelDt", "cancellationDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cancelType", "cancellationType"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cancelReason", "cancellationReason"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cancelMethod", "cancellationMethod"));

        // call PM_IS_SOLO_OWNER function to check if the entity is solo owner
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Process_Transaction.Pm_Validate_Cxl_Level", mapping);
        try {
            outputRecords = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to validate cancellation.", e);
            l.throwing(getClass().getName(), "validateCancellation", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateCancellation", outputRecords);
        }
        return outputRecords;
    }

    /**
     * to check if the renewal wip cancellation is valid
     *
     * @param inputRecord
     * @return record include the result and error message
     */
    public Record validateRenewalWipCancellation(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validateRenewalWipCancellation");

        Record outputRecord;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("level", "cancellationLevel"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Process_Transaction.Pm_Validate_RenWip_Cxl", mapping);
        try {
            outputRecord = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to validate renewal wip cancellation.", e);
            l.throwing(getClass().getName(), "validateRenewalWipCancellation", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateRenewalWipCancellation", outputRecord);
        }
        return outputRecord;
    }

    /**
     * to process the cancellation process
     *
     * @param inputRecord
     * @return the excecution result
     */
    public Record performCancellation(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "performCancellation");

        Record outputRecord;
        /* Create DataRecordMappig for this stored procedure */
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("termId", "termBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cancelDt", "cancellationDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("acctDt", "accountingDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("comments", "cancellationComments"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transactionComment2", "newTransactionComment2"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cancelType", "cancellationType"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cancelReason", "cancellationReason"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cancelMethod", "cancellationMethod"));
        mapping.addFieldMapping(new DataRecordFieldMapping("level", "cancellationLevel"));
        mapping.addFieldMapping(new DataRecordFieldMapping("destPolicyNo", "amalgamationTo"));
        mapping.addFieldMapping(new DataRecordFieldMapping("claimsAccessInd", "claimsAccessIndicator"));
        //set fields values
        inputRecord.setFieldValue("batchModeYn", "Y");
        inputRecord.setFieldValue("validateB", "Y");
        inputRecord.setFieldValue("saveOfficialB", "N");
        inputRecord.setFieldValue("numAgeRisk", "0");
        // call PM_IS_SOLO_OWNER function to check if the entity is solo owner
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Process_Transaction.web_process_cancellation", mapping);
        try {
            outputRecord = spDao.execute(inputRecord).getSummaryRecord();

        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to perform cancellation.", e);
            l.throwing(getClass().getName(), "performCancellation", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performCancellation", outputRecord);
        }
        return outputRecord;
    }

    /**
     * to process the cancellation process on slot
     *
     * @param inputRecord
     * @return the excecution result
     */
    public Record performSlotCancellation(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performSlotCancellation", new Object[]{inputRecord});
        }
        Record outputRecord;
        /* Create DataRecordMappig for this stored procedure */
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("riskId", "baseId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cancelDate", "cancellationDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cancelReason", "cancellationReason"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));

        // call PM_IS_SOLO_OWNER function to check if the entity is solo owner
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Cancel_Slot_Occupant", mapping);
        try {
            outputRecord = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to perform cancellation on slot.", e);
            l.throwing(getClass().getName(), "performSlotCancellation", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performSlotCancellation", outputRecord);
        }
        return outputRecord;

    }

    /**
     * to process the cancellation process on risk relation
     *
     * @param inputRecord
     * @return
     */
    public Record performRiskRelationCancellation(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performRiskRelationCancellation", new Object[]{inputRecord});
        }

        Record outputRecord;
        /* Create DataRecordMappig for this stored procedure */
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("riskRelId", "baseId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cancelDate", "cancellationDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("chiTermEff", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("chiTermExp", "termEffectiveToDate"));

        // call PM_IS_SOLO_OWNER function to check if the entity is solo owner
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Risk_Rel.Cancel_Risk_Rel", mapping);
        try {
            outputRecord = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to perform cancellation on risk relation.", e);
            l.throwing(getClass().getName(), "performRiskRelationCancellation", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performRiskRelationCancellation", outputRecord);
        }
        return outputRecord;
    }


    /**
     * load all cancelable items for multi cancel
     *
     * @param inputRecord
     * @param lp
     * @return
     */
    public RecordSet loadAllCancelableItem(Record inputRecord, RecordLoadProcessor lp) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllCancelableItem", new Object[]{inputRecord});
        }

        RecordSet rs = null;
        /* Create DataRecordMappig for this stored procedure */
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("termEffDate", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExpDate", "termEffectiveToDate"));

        // call Pm_Web_Cancel.Sel_Cancel_Item function to load all cancelable items for multi cancel
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Cancel.Sel_Cancel_Item", mapping);
        try {
            rs = spDao.execute(inputRecord, lp);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to load cancelable items.", e);
            l.throwing(getClass().getName(), "loadAllCancelableItem", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllCancelableItem", rs);
        }

        return rs;

    }


    /**
     * load all cancelable coi for multi cancel
     *
     * @param inputRecord
     * @param lp
     * @return
     */
    public RecordSet loadAllCancelableCoi(Record inputRecord, RecordLoadProcessor lp) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllCancelableCoi", new Object[]{inputRecord});
        }

        RecordSet rs = null;
        /* Create DataRecordMappig for this stored procedure */
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("termEffDate", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExpDate", "termEffectiveToDate"));

        // call Pm_Web_Cancel.Sel_Coi_Candidate to load all cancelable COI Holders for multi cancel
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Cancel.Sel_Coi_Candidate", mapping);
        try {
            rs = spDao.execute(inputRecord, lp);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to load cancelable cois.", e);
            l.throwing(getClass().getName(), "loadAllCancelableCoi", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllCancelableCoi", rs);
        }

        return rs;
    }

    /**
     * perfom risk cancellation for multi cancel
     *
     * @param inputRecord
     * @return
     */
    public Record cancelRisk(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "cancelRisk", new Object[]{inputRecord});
        }

        Record outputRecord;
        /* Create DataRecordMappig for this stored procedure */
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cancelType", "cancellationType"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cancelReason", "cancellationReason"));
        mapping.addFieldMapping(new DataRecordFieldMapping("methodCode", "cancellationMethod"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cancelDate", "cancellationDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termBaseId", "termBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("riskBaseId", "riskBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termEffFrom", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termEffTo", "termEffectiveToDate"));

        //set initial values
        inputRecord.setFieldValue("numAgeOvrdRisks", "0");
        inputRecord.setFieldValue("ageOvrdRisks", "0");

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Cancel.Cancel_Risk", mapping);
        try {
            outputRecord = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "Failed to perform risk cancellation for multi cancel.", e);
            l.throwing(getClass().getName(), "cancelRisk", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "cancelRisk", outputRecord);
        }
        return outputRecord;
    }


    /**
     * perfom coverage cancellation for multi cancel
     *
     * @param inputRecord
     * @return
     */
    public Record cancelCoverage(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "cancelCoverage", new Object[]{inputRecord});
        }
        Record outputRecord;
        /* Create DataRecordMappig for this stored procedure */
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cancelType", "cancellationType"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cancelReason", "cancellationReason"));
        mapping.addFieldMapping(new DataRecordFieldMapping("methodCode", "cancellationMethod"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cancelDate", "cancellationDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termBaseId", "termBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("covBaseId", "coverageBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("riskBaseId", "riskBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termEffFrom", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termEffTo", "termEffectiveToDate"));

        //set initial values
        inputRecord.setFieldValue("numAgeOvrdRisks", "0");
        inputRecord.setFieldValue("ageOvrdRisks", "0");

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Cancel.Cancel_Coverage", mapping);
        try {
            outputRecord = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "Failed to perform coverage cancellation for multi cancel.", e);
            l.throwing(getClass().getName(), "cancelCoverage", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "cancelCoverage");
        }
        return outputRecord;
    }


    /**
     * perfom component cancellation for multi cancel
     *
     * @param inputRecords
     * @return
     */
    public void cancelAllComponent(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "cancelAllComponent", new Object[]{inputRecords});
        }

        /* Create DataRecordMappig for this stored procedure */
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("id", "policyId"));

        //set parms
        Iterator recIter = inputRecords.getRecords();
        while (recIter.hasNext()) {
            Record rec = (Record) recIter.next();
            rec.setFieldValue("type", "COMPONENT");
            StringBuffer parmsBuffer = new StringBuffer();
            parmsBuffer.append("POL_COV_COMP_BASE_REC_FK^").append(rec.getStringValue("polCovCompBaseRecId"))
                .append("^EFFECTIVE_FROM_DATE^").append(rec.getStringValue("componentEffectiveFromDate"))
                .append("^EFFECTIVE_TO_DATE^").append(rec.getStringValue("cancellationDate"))
                .append("^TRANSACTION_LOG_FK^").append(rec.getStringValue("transactionLogId")).append("^");
            rec.setFieldValue("parms", parmsBuffer.toString());
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Save_Data.Main", mapping);
        try {
            spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to perform components cancellation for multi cancel.", e);
            l.throwing(getClass().getName(), "cancelAllComponent", ae);
            throw ae;
        }


        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "cancelAllComponent");
        }

    }

    /**
     * perfom sub coverage cancellation for multi cancel
     *
     * @param inputRecord
     * @return
     */
    public Record cancelCoverageClass(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "cancelCoverageClass", new Object[]{inputRecord});
        }
        Record outputRecord;
        /* Create DataRecordMappig for this stored procedure */
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cancelType", "cancellationType"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cancelReason", "cancellationReason"));
        mapping.addFieldMapping(new DataRecordFieldMapping("scovgBaseId", "subcoverageBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("methodCode", "cancellationMethod"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cancelDate", "cancellationDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termBaseId", "termBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("riskBaseId", "riskBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termEffFrom", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termEffTo", "termEffectiveToDate"));

        //set initial values
        inputRecord.setFieldValue("covgBaseId", "0");
        inputRecord.setFieldValue("numAgeOvrdRisks", "0");
        inputRecord.setFieldValue("ageOvrdRisks", "0");

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Cancel.Cancel_Sub_Coverage", mapping);
        try {
            outputRecord = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "Failed to perform sub coverage cancellation for multi cancel.", e);
            l.throwing(getClass().getName(), "cancelCoverageClass", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "cancelCoverageClass");
        }

        return outputRecord;
    }

    /**
     * perfom VL Employee cancellation
     *
     * @param inputRecord
     * @return
     */
    public Record cancelVLEmployee(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "cancelVLEmployee", new Object[]{inputRecord});
        }
        Record outputRecord;
        /* Create DataRecordMappig for this stored procedure */
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("covgRelEntityId", "baseId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cancelDate", "cancellationDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cancelReason", "cancellationReason"));
        
        inputRecord.setFieldValue("cancellationReason", "CRENTITY");

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Cancel_Vlrisk", mapping);
        try {
            outputRecord = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "Failed to perform VL Employee cancellation.", e);
            l.throwing(getClass().getName(), "cancelVLEmployee", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "cancelVLEmployee", outputRecord);
        }

        return outputRecord;
    }


    /**
     * perfrom coi holder cancellation
     *
     * @param inputRecords
     */
    public void cancelAllCoiHolder(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "cancelAllCoiHolder", new Object[]{inputRecords});
        }

        // Create the input data mapping
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("riskBaseId", "riskBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cancelDate", "cancellationDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("acctDate", "accountingDate"));
        Map riskCoiMap = new HashMap();

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Cancel.Cancel_Coi_Holder", mapping);
        try {
            Iterator coiIter = inputRecords.getRecords();
            Record coiRec = null;
            StringBuffer coiPkBuff;
            while (coiIter.hasNext()) {
                coiRec = (Record) coiIter.next();
                String riskBaseId = coiRec.getStringValue("riskBaseRecordId");
                if (riskCoiMap.containsKey(riskBaseId)) {
                    coiPkBuff = (StringBuffer) riskCoiMap.get(riskBaseId);
                }
                else {
                    coiPkBuff = new StringBuffer();
                    riskCoiMap.put(riskBaseId, coiPkBuff);
                }

                String coiPk = coiRec.getStringValue("entityId");
                if (coiPkBuff.length() != 0)
                    coiPkBuff.append(",");
                coiPkBuff.append(coiPk);

            }

            Iterator it = riskCoiMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                String riskBaseId = (String) entry.getKey();
                String coiPks = ((StringBuffer) entry.getValue()).toString();
                Record inputRecord = new Record();
                inputRecord.setFields(coiRec);
                inputRecord.setFieldValue("riskBaseRecordId", riskBaseId);
                inputRecord.setFieldValue("coiEntityFks", coiPks);
                inputRecord.setFieldValue("coiCount", String.valueOf(coiPks.split(",").length));
                spDao.execute(inputRecord);
            }
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to multi cancel coi holders", e);
            l.throwing(getClass().getName(), "cancelAllCoiHolder", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "cancelAllCoiHolder");
        }
    }

    /**
     * get future primary risk count for risk cancel validation
     *
     * @param inputRecord
     * @return
     */
    public int getFuturePrimaryRiskCount(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getFuturePrimaryRiskCount", new Object[]{inputRecord});
        }

        int result = 0;
        /* Create DataRecordMappig for this stored procedure */
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("riskBaseId", "riskBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effective", "cancellationDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExpiration", "termEffectiveToDate"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(
            "Pm_Web_Cancel.Get_Future_Primary_Risk_Count", mapping);
        try {
            result = spDao.execute(inputRecord).getSummaryRecord().
                getIntegerValue(StoredProcedureDAO.RETURN_VALUE_FIELD).intValue();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "Failed to get Future Primary Risk Count for risk validation.", e);
            l.throwing(getClass().getName(), "getFuturePrimaryRiskCount", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getFuturePrimaryRiskCount", String.valueOf(result));
        }

        return result;
    }


    /**
     * to get the result if all owners of the entity risk are selected
     *
     * @param inputRecord
     * @return
     */
    public Record isAllRiskOwnersSelected(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isAllRiskOwnersSelected", new Object[]{inputRecord});
        }

        Record record = null;
        /* Create DataRecordMappig for this stored procedure */
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("riskBase", "riskBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transEffective", "cancellationDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExpiration", "termEffectiveToDate"));

        //set initial values
        inputRecord.setFieldValue("inpString", "CANCEL_OWNER");

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Cancel.Val_Ent_Risk_Rel", mapping);
        try {
            record = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().
                handleException("Failed get the result if all owners of the entity risk are selected.", e);
            l.throwing(getClass().getName(), "isAllRiskOwnersSelected", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isAllRiskOwnersSelected", record);
        }

        return record;
    }


    /**
     * validate risk cancellation for multi cancel
     *
     * @param inputRecord
     * @return
     */
    public RecordSet validateCancelRisk(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateCancelRisk", new Object[]{inputRecord});
        }
        RecordSet outputRecords;
        /* Create DataRecordMappig for this stored procedure */
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("riskBaseId", "riskBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termBaseId", "termBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cancelDate", "cancellationDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("type", "cancellationType"));
        mapping.addFieldMapping(new DataRecordFieldMapping("reason", "cancellationReason"));
        mapping.addFieldMapping(new DataRecordFieldMapping("methodCode", "cancellationMethod"));


        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Validate_Cancel.Val_Risk", mapping);
        try {
            outputRecords = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to risk cancellation for multi cancel.", e);
            l.throwing(getClass().getName(), "validateCancelRisk", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateCancelRisk", outputRecords);
        }

        return outputRecords;
    }


    /**
     * get the count of sub coverages which can be cancelled
     *
     * @param inputRecord
     * @return
     */
    public int getCancelableCoverageClassCount(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getCancelableCoverageClassCount", new Object[]{inputRecord});
        }

        int result = 0;
        /* Create DataRecordMappig for this stored procedure */
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("parentBaseRecId", "coverageBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effective", "cancellationDate"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(
            "Pm_Web_Cancel.Get_Cancelable_Sub_Covg_Count", mapping);
        try {
            result = spDao.execute(inputRecord).getSummaryRecord().
                getIntegerValue(StoredProcedureDAO.RETURN_VALUE_FIELD).intValue();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "Failed to get the count of sub coverages which can be cancelled.", e);
            l.throwing(getClass().getName(), "getCancelableCoverageClassCount", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getCancelableCoverageClassCount", String.valueOf(result));
        }

        return result;
    }


    /**
     * validate coverage class cancellation for multi cancel
     *
     * @param inputRecord
     * @return
     */
    public RecordSet validateCancelCoverageClass(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateCancelCoverageClass", new Object[]{inputRecord});
        }
        RecordSet outputRecords;
        /* Create DataRecordMappig for this stored procedure */
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("covBaseId", "coverageBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("subcovBaseId", "subcoverageBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termBaseId", "termBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cancelDate", "cancellationDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("type", "cancellationType"));
        mapping.addFieldMapping(new DataRecordFieldMapping("reason", "cancellationReason"));
        mapping.addFieldMapping(new DataRecordFieldMapping("methodCode", "cancellationMethod"));


        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Validate_Cancel.Val_Coverage_Class", mapping);
        try {
            outputRecords = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "Failed to validate coverage cancellation for multi cancel.", e);
            l.throwing(getClass().getName(), "validateCancelCoverageClass", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateCancelCoverageClass", outputRecords);
        }

        return outputRecords;
    }

    /**
     * validate coverage cancellation for multi cancel
     *
     * @param inputRecord
     * @return
     */
    public RecordSet validateCancelCoverage(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateCancelCoverage", new Object[]{inputRecord});
        }

        RecordSet outputRecords;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Validate_Cancel.Val_Coverage");
        try {
            outputRecords = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "Failed to validate coverage cancellation for multi cancel.", e);
            l.throwing(getClass().getName(), "validateCancelCoverage", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateCancelCoverage", outputRecords);
        }

        return outputRecords;
    }

    /**
     * resolve tail for multi cancel
     *
     * @param inputRecord
     */
    public void resolveTail(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "resolveTail", new Object[]{inputRecord});
        }

        /* Create DataRecordMappig for this stored procedure */
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transId", "transactionLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Policy_Group.Resolve_Tail", mapping);
        try {
            spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "Failed to resolve tail.", e);
            l.throwing(getClass().getName(), "resolveTail", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "resolveTail");
        }
    }


    /**
     * return the tail term base ID after multi cancellation
     *
     * @return inputRecord
     */
    public String getTailTerm(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getTailTerm", new Object[]{inputRecord});
        }

        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Cancel.Sel_Tail_Term");
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get the tail term base ID.", e);
            l.throwing(getClass().getName(), "getTailTerm", ae);
            throw ae;
        }

        String termBaseId = rs.getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getTailTerm", termBaseId);
        }

        return termBaseId;
    }

    /**
     * Validate amalgamation data
     *
     * @param inputRecord
     * @return Record
     */
    public Record validateAmalgamation(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAmalgamation", new Object[]{inputRecord});
        }

        Record result;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("cancelDate", "cancellationDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("destPolicyNo", "amalgamationTo"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("pm_validate_cancel.val_amalgamation", mapping);
        try {
            result = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "Failed to validate amalgamation.", e);
            l.throwing(getClass().getName(), "validateAmalgamation", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateAmalgamation", result);
        }
        return result;
    }

    /**
     * process amalgamation
     *
     * @param inputRecord
     * @return Record
     */
    public Record processAmalgamation(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processAmalgamation", new Object[]{inputRecord});
        }

        Record result;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("srcPolicyId", "policyId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("destPolicyNo", "amalgamationTo"));
        mapping.addFieldMapping(new DataRecordFieldMapping("claimsAccessInd", "claimsAccessIndicator"));
        mapping.addFieldMapping(new DataRecordFieldMapping("level", "cancellationLevel"));
        mapping.addFieldMapping(new DataRecordFieldMapping("amalgamationDate", "cancellationDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("riskIdString", "riskBaseRecordIds"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Amalgamation.Process_Amalgamation", mapping);
        try {
            result = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "Failed to process amalgamation.", e);
            l.throwing(getClass().getName(), "processAmalgamation", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "processAmalgamation", result);
        }
        return result;
    }

    /**
     * Cancel policy
     *
     * @param inputRecord
     */
    public void cancelPolicy(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "cancelPolicy", new Object[]{inputRecord});
        }

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cancelDate", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termBaseId", "termBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termEffFrom", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termEffTo", "termEffectiveToDate"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Cancel.Cancel_Policy", mapping);
        try {
            spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to cancel policy.", e);
            l.throwing(getClass().getName(), "cancelPolicy", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "cancelPolicy");
        }
    }

    /**
     * Load all transaction snapshot
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllTransactionSnapshot(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllTransactionSnapshot", new Object[]{inputRecord});
        }

        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Trans_Snapshot.Sel_Trans_Snapshot");
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load all transaction snapshot", e);
            l.throwing(getClass().getName(), "loadAllTransactionSnapshot", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllTransactionSnapshot");
        }
        return rs;
    }

    /**
     * Load all term snapshot
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllTermSnapshot(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllTermSnapshot", new Object[]{inputRecord});
        }

        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Trans_Snapshot.Sel_Term_Info");
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load all term snapshot", e);
            l.throwing(getClass().getName(), "loadAllTermSnapshot", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllTermSnapshot");
        }
        return rs;
    }

    /**
     * Load all policy component snapshot
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllPolicyComponentSnapshot(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllPolicyComponentSnapshot", new Object[]{inputRecord});
        }

        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Trans_Snapshot.Sel_Policy_Components");
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load all policy component snapshot", e);
            l.throwing(getClass().getName(), "loadAllPolicyComponentSnapshot", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllPolicyComponentSnapshot");
        }
        return rs;
    }

    /**
     * Load all risk snapshot
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllRiskSnapshot(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRiskSnapshot", new Object[]{inputRecord});
        }

        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Trans_Snapshot.Sel_Risk_Info");
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load all risk snapshot", e);
            l.throwing(getClass().getName(), "loadAllRiskSnapshot", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRiskSnapshot");
        }
        return rs;
    }

    /**
     * Load all coverage snapshot
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllCoverageSnapshot(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllCoverageSnapshot", new Object[]{inputRecord});
        }

        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Trans_Snapshot.Sel_Covg_Info");
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load all coverage snapshot", e);
            l.throwing(getClass().getName(), "loadAllCoverageSnapshot", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllCoverageSnapshot");
        }
        return rs;
    }

    /**
     * Load all coverage component
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllCoverageComponentSnapshot(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllCoverageComponentSnapshot", new Object[]{inputRecord});
        }

        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Trans_Snapshot.Sel_Covg_Components");
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load all coverage component snapshot", e);
            l.throwing(getClass().getName(), "loadAllCoverageComponentSnapshot", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllCoverageComponentSnapshot");
        }
        return rs;
    }

    /**
     * to get the result to indicate if new carrier field is available in the cancel page.
     *
     * @param inputRecord
     * @return YesNoFlag
     */
    public YesNoFlag isNewCarrierEnabled(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isNewCarrierEnabled", new Object[]{inputRecord});
        }

        String result = null;

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Cancel.New_Carrier_Enabled");
        try {
            result = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().
                handleException("Failed get the result if new carrier is available.", e);
            l.throwing(getClass().getName(), "isNewCarrierEnabled", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isNewCarrierEnabled", result);
        }

        return YesNoFlag.getInstance(result);
    }

    /**
     * Save all discipline decline entity.
     *
     * @param inputRecords
     * @return
     */
    public int saveAllDisciplineDeclineEntity(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllDisciplineDeclineEntity", new Object[]{inputRecords});
        }
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("effectiveFromDate", "cancellationDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("ddlMethod", "cancellationMethod"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CS_WEB_DISCIPLINE_DECLINE.Save_Canceled_Entity", mapping);
        int updateCount = 0;
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Error when executing saveAllDisciplineDeclineEntity", se);
            l.throwing(getClass().getName(), "saveAllDisciplineDeclineEntity", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllDisciplineDeclineEntity", updateCount);
        }
        return updateCount;
    }
}
