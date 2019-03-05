package dti.pm.coveragemgr.vlcoveragemgr.dao;

import dti.oasis.app.AppException;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.RecordMode;
import dti.pm.core.dao.BaseDAO;
import dti.pm.coveragemgr.vlcoveragemgr.VLCoverageFields;
import dti.pm.policymgr.PolicyFields;
import dti.pm.policymgr.PolicyHeaderFields;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the VLCoverageDAO interface.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 7, 2008
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
public class VLCoverageJdbcDAO extends BaseDAO implements VLCoverageDAO {
    /**
     * load all VL risk info
     *
     * @param inputRecord         input parameters
     * @param recordLoadProcessor record load processor
     * @return recordset of VL risk info
     */
    public RecordSet loadAllVLRisk(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllVLRisk", new Object[]{inputRecord});
        }

        RecordSet rs;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("covgBaseId", "coverageBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "termEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("endQuoteId", "endorsementQuoteId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transTypeCode", "vlScreenModeCode"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Sel_Vlrisk_INFO", mapping);
        try {
            rs = spDao.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load VL Risk info", e);
            l.throwing(getClass().getName(), "loadAllVLRisk", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllVLRisk", rs);
        }
        return rs;
    }


    /**
     * save non insured VL Risk data
     *
     * @param inputRecord input record
     */
    public void saveNonInsuredVLRisk(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveNonInsuredVLRisk", new Object[]{inputRecord});
        }

        //set constant values
        inputRecord.setFieldValue("type", "RISK");
        DataRecordMapping saveRiskMapping = new DataRecordMapping();
        saveRiskMapping.addFieldMapping(new DataRecordFieldMapping("id", "riskId"));
        StoredProcedureDAO saveRiskSpDao = StoredProcedureDAO.getInstance("Pm_Save_Data.Main", saveRiskMapping);
        try {
            //save non insured vl risk data
            inputRecord.setFieldValue("parms", getParams(inputRecord));
            saveRiskSpDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to save non insured VL Risk info", e);
            l.throwing(getClass().getName(), "saveNonInsuredVLRisk", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveNonInsuredVLRisk");
        }
    }

    /**
     * save new/modifieds record
     *
     * @param inputRecord input record
     */
    public void saveVLRisk(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveVLRisk", new Object[]{inputRecord});
        }

        //set constant values
        inputRecord.setFieldValue("type", "RISK");

        DataRecordMapping saveVlRiskMapping = new DataRecordMapping();
        saveVlRiskMapping.addFieldMapping(new DataRecordFieldMapping("covRelEntId", "covRelatedEntityId"));
        saveVlRiskMapping.addFieldMapping(new DataRecordFieldMapping("coverageBaseId", "coverageId"));
        saveVlRiskMapping.addFieldMapping(new DataRecordFieldMapping("effFromDate", "startDate"));
        saveVlRiskMapping.addFieldMapping(new DataRecordFieldMapping("expDate", "endDate"));
        saveVlRiskMapping.addFieldMapping(new DataRecordFieldMapping("termEffDate", "termEffectiveFromDate"));
        saveVlRiskMapping.addFieldMapping(new DataRecordFieldMapping("termExpDate", "termEffectiveToDate"));
        saveVlRiskMapping.addFieldMapping(new DataRecordFieldMapping("insuredB", "companyInsuredB"));
        saveVlRiskMapping.addFieldMapping(new DataRecordFieldMapping("policyNo", "vlPolicyNo"));
        saveVlRiskMapping.addFieldMapping(new DataRecordFieldMapping("transactionLogId", "lastTransactionLogId"));
        StoredProcedureDAO saveVlRiskSpDao = StoredProcedureDAO.getInstance("Pm_Nb_End.Save_Vlrisk", saveVlRiskMapping);
        try {
            saveVlRiskSpDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to save VL Risk info", e);
            l.throwing(getClass().getName(), "saveVLRisk", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveVLRisk");
        }
    }

    /**
     * delete all VL risk records
     *
     * @param inputRecords input records
     * @return process count
     */
    public int deleteAllVLRisk(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "deleteAllVLRisk", new Object[]{inputRecords});
        }

        int processCount;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("covRelEntId", "covRelatedEntityId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Nb_Del.Del_Vlrisk", mapping);
        try {
            processCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to delete VL Risk info", e);
            l.throwing(getClass().getName(), "deleteAllVLRisk", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "deleteAllVLRisk", String.valueOf(processCount));
        }
        return processCount;
    }

    /**
     * Retrieves last official data for a particular entity/risk
     *
     * @param inputRecord input parameters
     * @return vl risk info
     */
    public Record getLastVLRiskInfo(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getLastVLRiskInfo", new Object[]{inputRecord});
        }

        Record resultRec;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("polId", "externalId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Sel_Last_VLRisk_Info", mapping);
        try {
            resultRec = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get last VL Risk info", e);
            l.throwing(getClass().getName(), "getLastVLRiskInfo", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getLastVLRiskInfo", resultRec);
        }
        return resultRec;
    }

    protected String addParm(Record inputRecord, String parmName, String mapFieldName) {
        return addParm(parmName, inputRecord.getStringValue(mapFieldName));
    }

    protected String addParm(String parmName, String parmValue) {
        StringBuffer parm = new StringBuffer(parmName);
        if (StringUtils.isBlank(parmValue)) {
            parmValue = "";
        }
        parm.append("^").append(parmValue).append("^");
        return parm.toString();
    }

    protected String getParams(Record inputRecord) {
        StringBuffer pParms = new StringBuffer();
        RecordMode recordMode = PMCommonFields.getRecordModeCode(inputRecord);
        if (recordMode.isOfficial()) {
            pParms.append(addParm(inputRecord, "RISK_PK", VLCoverageFields.RISK_ID))
                .append(addParm(inputRecord, "RISK_BASE_RECORD_FK", VLCoverageFields.RISK_BASE_ID))
                .append(addParm(inputRecord, "EFFECTIVE_FROM_DATE", VLCoverageFields.EFFECTIVE_FROM_DATE))
                .append(addParm(inputRecord, "EFFECTIVE_TO_DATE", VLCoverageFields.END_DATE))
                .append(addParm("PRIMARY_RISK_B", "N"))
                .append(addParm(inputRecord, "COUNTY_CODE_USED_TO_RATE", VLCoverageFields.COUNTY_CODE_USED_TO_RATE))
                .append(addParm(inputRecord, "RISK_CLS_USED_TO_RATE", VLCoverageFields.RISK_CLS_USED_TO_RATE))
                .append(addParm(inputRecord, "RISK_SUB_CLS_USED_TO_RATE", VLCoverageFields.RISK_SUB_CLS_USED_TO_RATE))
                .append(addParm(inputRecord, "ENTITY_FK", VLCoverageFields.ENTITY_ID))
                .append(addParm(inputRecord, "POLICY_FK", PolicyHeaderFields.POLICY_ID))
                .append(addParm(inputRecord, "POLICY_NO", PolicyHeaderFields.POLICY_NO))
                .append(addParm(inputRecord, "TRANSACTION_LOG_FK", "lastTransactionLogId"))
                .append(addParm(inputRecord, "RISK_TYPE_CODE", VLCoverageFields.RISK_TYPE_CODE))
                .append(addParm(inputRecord, "RATING_BASIS", VLCoverageFields.RATING_BASIS))
                .append(addParm(inputRecord, "RISK_PROCESS_CODE", VLCoverageFields.RISK_PROCESS_CODE))
                .append(addParm(inputRecord, "PRACTICE_STATE_CODE", VLCoverageFields.PRACTICE_STATE_CODE));
        }
        else {
            pParms.append(addParm(inputRecord, "ROW_STATUS", "rowStatus"))
                .append(addParm(inputRecord, "RISK_PK", VLCoverageFields.RISK_ID))
                .append(addParm(inputRecord, "RISK_BASE_RECORD_FK", VLCoverageFields.RISK_BASE_ID))
                .append(addParm(inputRecord, "CURR_POL_REL_STATUS_CODE", VLCoverageFields.STATUS))
                .append(addParm(inputRecord, "EFFECTIVE_FROM_DATE", VLCoverageFields.EFFECTIVE_FROM_DATE))
                .append(addParm(inputRecord, "EFFECTIVE_TO_DATE", VLCoverageFields.END_DATE))
                .append(addParm("PRIMARY_RISK_B", "N"))
                .append(addParm(inputRecord, "COUNTY_CODE_USED_TO_RATE", VLCoverageFields.COUNTY_CODE_USED_TO_RATE))
                .append(addParm(inputRecord, "RISK_CLS_USED_TO_RATE", VLCoverageFields.RISK_CLS_USED_TO_RATE))
                .append(addParm(inputRecord, "RISK_SUB_CLS_USED_TO_RATE", VLCoverageFields.RISK_SUB_CLS_USED_TO_RATE))
                .append(addParm(inputRecord, "ENTITY_FK", VLCoverageFields.ENTITY_ID))
                .append(addParm(inputRecord, "POLICY_FK", PolicyHeaderFields.POLICY_ID))
                .append(addParm(inputRecord, "POLICY_NO", PolicyHeaderFields.POLICY_NO))
                .append(addParm(inputRecord, "TRANSACTION_LOG_FK", "lastTransactionLogId"))
                .append(addParm(inputRecord, "RECORD_MODE_CODE", PMCommonFields.RECORD_MODE_CODE))
                .append(addParm(inputRecord, "RISK_TYPE_CODE", VLCoverageFields.RISK_TYPE_CODE))
                .append(addParm(inputRecord, "RATING_BASIS", VLCoverageFields.RATING_BASIS))
                .append(addParm(inputRecord, "AFTER_IMAGE_RECORD_B", VLCoverageFields.AFTER_IMAGE_RECORD_B))
                .append(addParm(inputRecord, "RISK_PROCESS_CODE", VLCoverageFields.RISK_PROCESS_CODE))
                .append(addParm(inputRecord, "POLICY_CYCLE_CODE", PolicyFields.POLICY_CYCLE_CODE))
                .append(addParm(inputRecord, "PRACTICE_STATE_CODE", VLCoverageFields.PRACTICE_STATE_CODE))
                .append(addParm("COVERAGE_PART_BASE_RECORD_FK", ""));
        }

        return pParms.toString();
    }

}
