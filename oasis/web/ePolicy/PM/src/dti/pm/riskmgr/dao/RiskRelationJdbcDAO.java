package dti.pm.riskmgr.dao;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.util.LogUtils;
import dti.oasis.util.SysParmProvider;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.busobjs.YesNoFlag;
import dti.pm.core.dao.BaseDAO;
import dti.pm.busobjs.SysParmIds;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.transactionmgr.TransactionFields;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.sql.SQLException;

/**
 * This class implements the RiskRelationDAO interface. This is consumed by any business logic objects
 * that requires information about Risk Relationship.
 * <p/>
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 1, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/22/2008       fcb         80759: getMultiRiskRelation and loadAvailablePolicyForCompanyInsuredRisk added
 * 02/11/2014       adeng       150945 - Modified loadAllRiskRelation() to map issueCompanyEntityId to issCompId.
 * 05/08/2017       xnie        180317 Added isRiskRelValAvailable().
 * ---------------------------------------------------
 */
public class RiskRelationJdbcDAO extends BaseDAO implements RiskRelationDAO {
    /**
     * To load all risk relation data.
     *
     * @param inputRecord   record with enough information to load risk relation.
     * @param loadProcessor an instance of data load processor
     * @return RecordSet a RecordSet loaded with list of available risk relations.
     */
    public RecordSet loadAllRiskRelation(Record inputRecord, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllRiskRelation", new Object[]{inputRecord});

        RecordSet rs;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("riskChildId", "riskBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("riskType", "currentRiskTypeCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "termEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("endQuoteId", "endorsementQuoteId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transTypeCode", "transactionTypeCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("issCompId", "issueCompanyEntityId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Web_Risk_Relation.Sel_Risk_Relation_info", mapping);
        try {
            rs = spDao.execute(inputRecord, loadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "Unable to load Risk Relation information", e);
            l.throwing(getClass().getName(), "loadAllRiskRelation", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "loadAllRiskRelation", rs);
        return rs;
    }

    /**
     * To save all newly added or updated Risk Relation data.
     *
     * @param inputRecords a set of Risk Relation Records for saving.
     * @return the number of rows updated.
     */
    public int saveAllRiskRelation(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllRiskRelation", new Object[]{inputRecords});

        int updateCount;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("childRiskId", "riskBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("parentRiskId", "riskParentId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("riskRelId", "riskRelationId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("relTypeCode", "riskRelationTypeCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "riskRelEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "riskRelEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("tranLogId", "transactionLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("statusCode", "riskRelationStatus"));
        mapping.addFieldMapping(new DataRecordFieldMapping("overrideRiskId", "overrideRiskBaseId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Risk_Rel.Save_Risk_Rel", mapping);
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save Risk Relation.", e);
            l.throwing(getClass().getName(), "saveAllRiskRelation", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "saveAllRiskRelation", new Integer(updateCount));
        return updateCount;
    }

    /**
     * To update all Risk Relation data when the officially saved data changed.
     *
     * @param inputRecords a set of Risk Relation Records for updating.
     * @return the number of rows updated.
     */
    public int updateAllRiskRelation(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "updateAllRiskRelation", new Object[]{inputRecords});

        int updateCount;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("riskRelId", "riskRelationId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("startDate", "transEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("relTypeCode", "riskRelationTypeCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("overrideRiskId", "overrideRiskBaseId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Risk_Rel.Change_Risk_Rel", mapping);
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to update changed Risk Relation.", e);
            l.throwing(getClass().getName(), "updateAllRiskRelation", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "updateAllRiskRelation", new Integer(updateCount));
        return updateCount;
    }

    /**
     * To delete all given input Risk Relation records.
     *
     * @param inputRecords a set of Risk Relation Records for deleting.
     * @return the number of rows deleted.
     */
    public int deleteAllRiskRelation(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "deleteAllRiskRelation", new Object[]{inputRecords});

        int updateCount;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("riskRelId", "riskRelationId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Risk_Rel.Delete_Risk_Rel", mapping);
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to delete risk relations.", e);
            l.throwing(getClass().getName(), "deleteAllRiskRelation", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "deleteAllRiskRelation", new Integer(updateCount));
        }

        return updateCount;
    }

    /**
     * To save all NI risk information.
     *
     * @param inputRecords a set of Risk Records for saving.
     * @return the number of rows saved.
     */
    public int saveAllNIRisk(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllNIRisk", new Object[]{inputRecords});
        }

        int processCount;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("id", "riskId"));
        // call Pm_Save_Data.Main procedure to update all NI Risk data
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Save_Data.Main", mapping);
        try {
            processCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save NI risks.", e);
            l.throwing(getClass().getName(), "saveAllNIRisk", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllNIRisk", new Integer(processCount));
        }
        return processCount;
    }

    /**
     * To save all coverage information.
     *
     * @param inputRecords a set of Coverage Records for saving.
     * @return the number of rows saved.
     */
    public int saveAllNICoverage(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllNICoverage", new Object[]{inputRecords});
        }

        int count;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("riskId", "riskParentId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("covgId", "coverageId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("covgBaseId", "coverageBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "transEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "newTermEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termId", "termBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("riskType", "niRiskTypeCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("covgLimitCode", "niCoverageLimitCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("retroDate", "niRetroDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("currentCarrier", "niCurrentCarrierId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("status", "riskRelationStatus"));

        // call Pm_Process_Prof_Entity.Process_NI_Coverage procedure to update all NI Coverage data
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Process_Prof_Entity.Process_NI_Coverage", mapping);
        try {
            count = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save NI coverages.", e);
            l.throwing(getClass().getName(), "saveAllNICoverage", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllNICoverage", new Integer(count));
        }
        return count;
    }

    /**
     * To get Non Insured Premium Count.
     *
     * @param inputRecord a Record with information of transaction effective from date, risk type and risk class.
     * @return the count of Non Insured Premium.
     */
    public int getNIPremiumCount(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getNIPremiumCount", new Object[]{inputRecord});

        int returnValue;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transEffDate", "transEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("riskClass", "riskClassCode"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Risk_Rel.Get_Nin_Premium_Count", mapping);
        try {
            Record record = spDao.execute(inputRecord).getSummaryRecord();
            returnValue = record.getIntegerValue(StoredProcedureDAO.RETURN_VALUE_FIELD).intValue();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get Non Insured Premium count.", e);
            l.throwing(getClass().getName(), "getNIPremiumCount", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "getNIPremiumCount", String.valueOf(returnValue));
        return returnValue;
    }

    /**
     * To get non-insured's default coverage code.
     *
     * @param inputRecord a Record with needed information.
     * @return a String value of coverage code.
     */
    public String getNICoverage(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getNICoverage", new Object[]{inputRecord});

        String returnValue;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("riskType", "niRiskTypeCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("stateCode", "practiceStateCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("retroDate", "niRetroDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "transEffectiveFromDate"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Process_Prof_Entity.Get_Ni_Covg", mapping);
        try {
            Record record = spDao.execute(inputRecord).getSummaryRecord();
            returnValue = record.getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get Ni Fte count.", e);
            l.throwing(getClass().getName(), "getNICoverage", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "getNICoverage", String.valueOf(returnValue));
        return returnValue;
    }

    /**
     * To get Non-Insured FTE count.
     *
     * @param inputRecord a Record with information of policy type.
     * @return the count of Non Insured FTE.
     */
    public int getNIFteCount(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getNIFteCount", new Object[]{inputRecord});

        int returnValue;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("policyType", "policyTypeCode"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Risk_Rel.Get_Ni_Fte_Count", mapping);
        try {
            Record record = spDao.execute(inputRecord).getSummaryRecord();
            returnValue = record.getIntegerValue(StoredProcedureDAO.RETURN_VALUE_FIELD).intValue();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get Ni Fte count.", e);
            l.throwing(getClass().getName(), "getNIFteCount", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "getNIFteCount", String.valueOf(returnValue));
        return returnValue;
    }

    /**
     * To get information of if company is insured.
     *
     * @param inputRecord a Record with needed information.
     * @return a String value "Y", "N" or "X".
     */
    public String getCompanyInsured(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getCompanyInsured", new Object[]{inputRecord});

        String returnValue;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("riskType", "currentRiskTypeCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("practiceState", "practiceStateCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Find_Comp_Insured", mapping);
        try {
            Record record = spDao.execute(inputRecord).getSummaryRecord();
            returnValue = record.getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get company insured token.", e);
            l.throwing(getClass().getName(), "getCompanyInsured", ae);
            throw ae;
        }

        if (returnValue.equals("X")) {
            returnValue = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_USE_COMP_INSURED, "Y");
        }
        l.exiting(getClass().getName(), "getCompanyInsured", returnValue);
        return returnValue;
    }

    /**
     * To load all available risks from other policies for adding company insured risk relation.
     *
     * @param inputRecord a Record with information to load the results.
     * @param loadProcessor an instance of data load processor
     * @return a set of Records with loaded available Policy data.
     */
    public RecordSet loadAllAvailableRiskForCompanyInsuredRiskRelation(Record inputRecord,
                                                                       RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAvailableRiskForCompanyInsuredRiskRelation", new Object[]{inputRecord});
        }

        RecordSet rs;
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "termEffectiveFromDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "termEffectiveToDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("policyCycle", "policyCycleCode"));
            mapping.addFieldMapping(new DataRecordFieldMapping("transEff", "transEffectiveFromDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("parentRiskType", "currentRiskTypeCode"));

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Sel_Active_Policy_Risk", mapping);
            rs = spDao.execute(inputRecord, loadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().
                handleException("Unable to load available company insured risk information", e);
            l.throwing(getClass().getName(), "loadAllAvailableRiskForCompanyInsuredRiskRelation", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAvailableRiskForCompanyInsuredRiskRelation", rs);
        }
        return rs;
    }

    /**
     * To get initial values for adding company insured risk relation.
     *
     * @param inputRecord a Record with the needed information.
     * @return a Record with the loaded initial values.
     */
    public Record getInitialValuesForAddCompINRiskRelation(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForAddCompINRiskRelation", new Object[]{inputRecord});

        try {
            Record output;

            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("polId", "policyId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("riskBaseId", "riskBaseRecordId"));

            // Execute the stored procedure for additional info fields
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Sel_Last_VLRisk_Info", mapping);
            output = spDao.executeUpdate(inputRecord);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "getInitialValuesForAddCompINRiskRelation", output);
            }
            return output;

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "Unable to get initial values for add company insured risk relation", e);
            l.throwing(getClass().getName(), "getInitialValuesForAddCompINRiskRelation", ae);
            throw ae;
        }
    }

    /**
     * To load all available risks for adding policy insured risk relation.
     *
     * @param inputRecord a Record with all needed information to load data.
     * @param loadProcessor an instance of data load processor
     * @return a set of Records with all available risks.
     */
    public RecordSet loadAllAvailableRiskForPolicyInsuredRiskRelation(Record inputRecord,
                                                                      RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAvailableRiskForPolicyInsuredRiskRelation", new Object[]{inputRecord});
        }

        RecordSet rs;
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("riskBaseId", "riskBaseRecordId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("transEff", "transEffectiveFromDate"));

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Risk_Rel.Get_Available_Risks", mapping);
            rs = spDao.execute(inputRecord, loadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().
                handleException("Unable to load available policy insured risk information", e);
            l.throwing(getClass().getName(), "loadAllAvailableRiskForPolicyInsuredRiskRelation", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAvailableRiskForPolicyInsuredRiskRelation", rs);
        }
        return rs;
    }

    /**
     * To get Add NI Coverage value
     *
     * @param inputRecord a Record with all needed information to do the query
     * @return Y/N value
     */
    public String getAddNICoverageB(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getAddNICoverageB", new Object[]{inputRecord});

        String returnValue;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("entityPoltype", "policyTypeCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("entityRiskType", "currentRiskTypeCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("riskRelType", "riskRelationTypeCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Process_Prof_Entity.Add_Nonins_Coverage", mapping);
        try {
            Record record = spDao.execute(inputRecord).getSummaryRecord();
            returnValue = record.getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get value of addNiCoverageB.", e);
            l.throwing(getClass().getName(), "getAddNICoverageB", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "getAddNICoverageB", String.valueOf(returnValue));
        return returnValue;
    }

    /**
     * To get Multi Risk Relation Indicator
     *
     * @param inputRecord a Record with all needed information to do the query
     * @return Y/N value
     */
    public YesNoFlag getMultiRiskRelation(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getMultiRiskRelation", new Object[]{inputRecord});
        YesNoFlag isMultiRiskRelation;

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Risk_Rel.Get_Multi_Risk_Rel");
        try {
            Record record = spDao.execute(inputRecord).getSummaryRecord();
            isMultiRiskRelation = YesNoFlag.getInstance(record.getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD));
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get value multiRiskRelation", e);
            l.throwing(getClass().getName(), "getMultiRiskRelation", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "getMultiRiskRelation", isMultiRiskRelation);
        return isMultiRiskRelation;
    }

    /**
     * To get available policy for Company Insured Risk
     *
     * @param inputRecord a Record with all needed information to do the query
     * @return string policy id
     */
    public String loadAvailablePolicyForCompanyInsuredRisk(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadAvailablePolicyForCompanyInsuredRisk", new Object[]{inputRecord});
        String policyId = "0";
        RecordSet rs;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("entityId", "riskEntityId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "termEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("policyCycle", "policyCycleCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transEff", "transEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("parentRiskType", "riskTypeCode"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Sel_Active_Policy_Risk", mapping);
        try {
            Record paramRecord = policyHeader.toRecord();
            paramRecord.setFieldValue("riskEntityId", inputRecord.getFieldValue("riskEntityId"));
            paramRecord.setFieldValue("riskTypeCode", policyHeader.getRiskHeader().getRiskTypeCode());

            rs = spDao.execute(paramRecord);

            if( rs.getSize()>0) {
                policyId = rs.getFirstRecord().getFieldValue("policyId").toString();
            }
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get value multiRiskRelation policy no.", e);
            l.throwing(getClass().getName(), "loadAvailablePolicyForCompanyInsuredRisk", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "loadAvailablePolicyForCompanyInsuredRisk", policyId);
        return policyId;
    }

    /**
     * Check if system needs to do risk relation attribute required validation.
     *
     * @param inputRecord a Record with all needed information to do the query
     * @return Y/N value
     */
    public YesNoFlag isRiskRelValAvailable(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "isRiskRelValAvailable", new Object[]{inputRecord});
        YesNoFlag isRiskRelValAvailable;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping(TransactionFields.TRANSACTION_EFF, TransactionFields.TRANSACTION_EFFECTIVE_FROM_DATE));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Risk_Rel.Is_Risk_Rel_Val_Available", mapping);
        try {
            Record record = spDao.execute(inputRecord).getSummaryRecord();
            isRiskRelValAvailable = YesNoFlag.getInstance(record.getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD));
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get value isRiskRelValAvailable", e);
            l.throwing(getClass().getName(), "isRiskRelValAvailable", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "isRiskRelValAvailable", isRiskRelValAvailable);
        return isRiskRelValAvailable;
    }
}
