package dti.pm.riskmgr.coimgr.dao;

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
import dti.pm.riskmgr.coimgr.CoiFields;
import dti.pm.policymgr.PolicyHeader;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Iterator;
import java.sql.SQLException;

/**
 * This class implements the CoiDAO interface. This is consumed by any business logic objects
 * that requires information about one or more COI Holders.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 5, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/25/2008       fcb         getNoteByNoteCode added.
 * 01/08/2013       fcb         137981 - changes related to Pm_Dates modifications.
 * 05/22/2013       adeng       144925 - Modified loadAllCoiHolder() to pass in record mode code
 *                              & endorsement Quote Id.
 * 06/06/2013       adeng       Added one more parameter Record inputRecord for method copyAllCoi(),
 *                              and use this object instead to create a new object.
 * 10/28/2016       lzhang      180689 - Modifed saveAllCoiHolder: pass term effective date to backend
 * 09/12/2017       wrong       187839 - Added function generateCoiForWS().
 * 06/26/2018       dpang       109175 - Modified field id for refactoring 'Entity Role List' page in CIS.
 * ---------------------------------------------------
 */
public class CoiJdbcDAO extends BaseDAO implements CoiDAO {
    /**
     * To calculate dates for load COI holder.
     * <p/>
     *
     * @param inputRecord
     * @return
     */
    public Record calculateDateForCoi(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "calculateDateForCoi", new Object[]{inputRecord});

        Record record;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("allowDateChangeB", "dateChangeAllowedB"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Coi.Calculate_Dates", mapping);
        try {
            record = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "Unable to calculate dates for COI Holder", e);
            l.throwing(getClass().getName(), "calculateDateForCoi", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "calculateDateForCoi", record);
        return record;
    }

    /**
     * Returns a RecordSet loaded with list of available COI Holders for the provided
     * risk information.
     * <p/>
     *
     * @param inputRecord         record with risk fields.
     * @param recordLoadProcessor an instance of data load processor
     * @return RecordSet a RecordSet loaded with list of available COI Holders.
     */
    public RecordSet loadAllCoiHolder(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllCoiHolder", new Object[]{inputRecord, recordLoadProcessor});

        RecordSet rs;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("entityId", "riskEntityId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("riskId", "riskBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("endQuoteId", "endorsementQuoteId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("recordMode", "recordModeCode"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_COI_HOLDER", mapping);
        try {
            rs = spDao.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "Unable to load COI Holder information", e);
            l.throwing(getClass().getName(), "loadAllCoiHolder", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "loadAllCoiHolder", new Object[]{rs});
        return rs;
    }

    /**
     * Save all COI Holder informations.
     * <p/>
     *
     * @param inputRecords intput records
     * @return the number of row updateds
     */
    public int saveAllCoiHolder(RecordSet inputRecords, PolicyHeader policyHeader) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllCoiHolder", new Object[]{inputRecords});

        int updateCount;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("entRoleId", "entityRoleId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExpDate", policyHeader.getTermEffectiveToDate()));
        mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "effectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("entityId", "riskEntityId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("riskId", "riskBaseRecordId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Save_Screens.Save_Coi", mapping);
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save COI Holder.", e);
            l.throwing(getClass().getName(), "saveAllCoiHolder", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "saveAllCoiHolder", new Integer(updateCount));
        return updateCount;
    }

    /**
     * If not slot risk type, caculate effective from date and effective to date by stored procedure "Pm_Sel_Date_Range".
     * <p/>
     *
     * @param inputRecord record with riskBaseId and type
     * @return a Record loaded with the effective from date and effective to date.
     */
    public Record loadDateRangeForCoiHolder(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadDateRangeForCoiHolder", new Object[]{inputRecord});

        Record record;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("baseId", "riskBaseRecordId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Sel_Date_Range", mapping);
        try {
            record = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to load date range for COI Holder.", e);
            l.throwing(getClass().getName(), "loadDateRangeForCoiHolder", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "loadDateRangeForCoiHolder", new Object[]{record});
        return record;
    }

    /**
     * Load the original contiguous period risk start date by stored procedure "Pm_Dates.Nb_Risk_Startdt".
     * <p/>
     *
     * @param inputRecord record with riskBaseId and the selected risk effecitve date
     * @return a Record loaded with risk effective from date.
     */
    public Record loadOrigContiguousRiskEffFromDate(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadOrigContiguousRiskEffFromDate", new Object[]{inputRecord});

        Record record;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("riskBaseId", "riskBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("findDate", "riskEffectiveFromDate"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Dates.Nb_Risk_Startdt", mapping);
        try {
            record = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "Failed to load original contiguous risk effecitve from date.", e);
            l.throwing(getClass().getName(), "loadOrigContiguousRiskEffFromDate", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "loadOrigContiguousRiskEffFromDate", new Object[]{record});
        return record;
    }

    /**
     * Load the cutoff date for COI Claims
     *
     * @return a Record loaded with cutoff date.
     */
    public Record loadCutoffDateForCoiClaim() {
        Logger l = LogUtils.enterLog(getClass(), "loadCutoffDateForCoiClaim");

        Record record;

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Coi.Get_Claim_Cutoff_Date");
        try {
            record = spDao.execute(new Record()).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "Failed to load the cutoff date for the COI Claims.", e);
            l.throwing(getClass().getName(), "loadCutoffDateForCoiClaim", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "loadCutoffDateForCoiClaim");
        return record;
    }

    /**
     * Generate all COI.
     *
     * @param inputRecord input coi data.
     */
    public void generateAllCoi(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "generateAllCoi", new Object[]{inputRecord});

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("termBaseId", "termBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("coiPks", "selectToGenerateCoiIds"));
        mapping.addFieldMapping(new DataRecordFieldMapping("covType", "coverageType"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cutoffDate", "coiCutoffDate"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Coi.Generate_Coi", mapping);
        try {
            spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to generate COI.", e);
            l.throwing(getClass().getName(), "generateAllCoi", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "generateAllCoi");
    }

    /**
     * To get actual term expiration date.
     *
     * @param inputRecord record with external id
     * @return actual term expiration date
     */
    public String getActualExpDate(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getActualExpDate", new Object[]{inputRecord});
        }

        String returnValue;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("extId", CoiFields.EXTERNAL_ID));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Coi.Get_Actual_Exp_Date", mapping);
        try {
            Record record = spDao.execute(inputRecord).getSummaryRecord();
            returnValue = record.getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get actual term expiration date.", e);
            l.throwing(getClass().getName(), "getActualExpDate", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getActualExpDate");
        }
        return returnValue;
    }

    /**
     * To process all Client COI.
     *
     * @param inputRecord intput records
     * @return record with return code and return message
     */
    public Record processAllCoi(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "processAllCoi", new Object[]{inputRecord});

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("hospitalId", "entityId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transEff", "coiAsOfDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("covType", "coverageType"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cutoffDate", "coiCutoffDate"));

        Record retRec;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Coi.Process_Client_Coi", mapping);
        try {
            retRec = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to generate COI.", e);
            l.throwing(getClass().getName(), "processAllCoi", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "processAllCoi", retRec);
        return retRec;
    }

    /**
     * copy all coi data to target risk
     *
     * @param inputRecords
     */
    public void copyAllCoi(RecordSet inputRecords, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "copyAllCoi", new Object[]{inputRecords});
        }

        // Create the input data mapping
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effFromForEndorse", "transEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExpDate", "termExp"));
        mapping.addFieldMapping(new DataRecordFieldMapping("toRiskBaseId", "toRiskBaseRecordId"));


        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Copy_All.Copyall_Coi", mapping);
        try {
            Iterator coiIter = inputRecords.getRecords();
            int coiNum = 0;
            Record coiRec = null;
            StringBuffer coiPkBuff = new StringBuffer();
            while (coiIter.hasNext()) {
                coiRec = (Record) coiIter.next();
                String coiPk = coiRec.getStringValue("coiHolderId");
                if (coiPkBuff.length() != 0)
                    coiPkBuff.append(",");
                coiPkBuff.append(coiPk);
                coiNum++;
            }
            inputRecord.setFieldValue("coiPks", coiPkBuff.toString());
            inputRecord.setFieldValue("numCois",String.valueOf(coiNum));
            spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to copy all coi", e);
            l.throwing(getClass().getName(), "copyAllCoi", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "copyAllCoi");
        }
    }

    /**
     * Method to get the notes based on the note code.
     * @param inputRecord
     * @return
     */
    public Record getNoteByNoteCode(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        Record record = null;
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getNoteByNoteCode", new Object[]{inputRecord});
        }

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transId", "transactionLogId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Risk.Web_Replace_Coi_Note_Tags", mapping);
        try {
            record = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get notes based on note code.", e);
            l.throwing(getClass().getName(), "getNoteByNoteCode", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getNoteByNoteCode", record);
        }
        return record;
    }

    /**
     * Method that generate Coi information base on input record.
     * <p/>
     * @param  inputRecord
     * @return record with return message information.
     */
    public Record generateCoiForWS(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "generateCoiForWS", new Object[]{inputRecord});
        }

        Record outputRecord;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("policyTermId", "policyTermNumberId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("coiPks", "CertificateHolderNumberId"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Web_Coi.Generate_Coi_For_WS", mapping);
        try {
            outputRecord = spDao.executeUpdate(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to execute PM_Web_Coi.Generate_Coi_For_WS.", e);
            l.throwing(getClass().getName(), "generateCoiForWS", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "generateCoiForWS", outputRecord);
        }
        return outputRecord;
    }

}
