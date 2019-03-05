package dti.pm.agentmgr.dao;

import dti.pm.core.dao.BaseDAO;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.RecordLoadProcessor;
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
 * This class implements the AgentDAO interface. It provides the implementation details of all DAO
 * operations that are performed against the agent manager.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 26, 2007
 *
 * @author gjlong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * Mar 21, 2008     James       Issue#75265 CreatedAdd Agent Tab to eCIS.
 *                              Same functionality, look and feel
 * 04/09/2008       fcb         isValidSubproducerOnSave added.
 * Apr 17, 2008     James       Issue#75265 Modify code according to code review
 * 10/07/2008       yhyang      Issue#86934 Move CIS Agent to eCIS.
 * 03/13/2013       awu         141924 - Added isLicensedAgent.
 * ---------------------------------------------------
 */
public class AgentJdbcDAO extends BaseDAO implements AgentDAO {

    private static final String STORED_PROC_AGENT_EXISTS = "FMN_CHECK_PTH_COMMRATE_EXISTS";
    private static final String STORED_PROC_SEL_AGENT_INFO = "Fmn_Sel_Policy_Agent_Info";
    private static final String STORED_PROC_SEL_AGENT_LICENSE = "fmn_Sel_Policy_Agent_License"; //todo: chagned name
    private static final String STORED_PROC_SAVE_AGENT_REASSIGN = "Fmn_Chg_Pol_Pth_Agent_Info.Agent_Reassign";
    private static final String STORED_PROC_SAVE_AGENT_AJUST = "Fmn_Chg_Pol_Pth_Agent_Info.Adjust_Policy_Agent";
    private static final String STORED_PROC_UPDATE_POLICY_AGENT = "Fmn_Chg_Pol_Pth_Agent_Info.Update_Policy_Agent";
    private static final String STORED_PROC_COMM_PAY_CODE_AVAILABLE = "Fmn_Ins_Comm_Pay_Code";
    private static final String STORED_PROC_COMM_RATE_SCHED_ASSIGNMENT = "PM_Web_Commission.Sel_Comm_Sched_Assignment";
    private static final String STORED_PROC_HIDE_AGENT_COMPLETE = "pm_web_Agent.Hide_Add_Agent_for_complete";
    private static final String STORED_PROC_HIDE_AGENT_NON_COMPLETE = "pm_web_Agent.Hide_Add_Agent_for_Noncomplete";

    /**
     * Method to load agent  and its related data for a policy
     *
     * @param inputRecord a record containing policy information
     * @param recordLoadProcessor a processor to be added to the recordSet before returned
     * @return recordSet resultsset of agent and commission information
     */
    public RecordSet loadAllAgent(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllAgent", new Object[]{inputRecord});
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("polId", "policyId"));
        StoredProcedureDAO sp = StoredProcedureDAO.getInstance(STORED_PROC_SEL_AGENT_INFO, mapping);
        RecordSet outRecordSet = new RecordSet();
        try {
            outRecordSet = sp.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load All agents for policy", se);
            l.throwing(getClass().getName(), "loadAllAgent", ae);
            throw ae;
        }

        l.exiting(getClass().toString(), "loadAllAgent");
        return outRecordSet;
    }

    /**
     * Method to load agent summary
     *
     * @param inputRecord input record that contains policy id
     * @return agent summary
     */
    public RecordSet loadAllAgentSummary(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAgentSummary", new Object[]{inputRecord});
        }
        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("Pm_Web_Agent.Sel_Policy_Agent_Summary");
        RecordSet outRecordSet = null;
        try {
            outRecordSet = sp.execute(inputRecord);
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load agent summary", se);
            l.throwing(getClass().getName(), "loadAllAgentSummary", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAgentSummary", outRecordSet);
        }
        return outRecordSet;
    }

    /**
     * method to load the initial license information for a given agent's agentLicId
     * that indicated by inputRecord's field: agentLicenseId
     *
     * @param inputRecord a record contains one fo the valid licenseClassCode(
     *            PRODUCER, SUB_PROD, COUNT_SIGN, AUTH_REP)
     *           state code, policyTypeCode, policyEffDate, policyId, more important, a agentLicId
     * @return record
     */
    public Record getInitalValuesForAgent(Record inputRecord  ) {
        Logger l = LogUtils.enterLog(getClass(), "getInitalValuesForAgent", new Object[]{inputRecord});

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("stateCode", "issueStateCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("policyEffDate", "termEffectiveFromDate"));

        inputRecord.setFieldValue("returnAgentDataToDisplay", "Y");

        StoredProcedureDAO sp = StoredProcedureDAO.getInstance(STORED_PROC_SEL_AGENT_LICENSE, mapping);
        RecordSet outRecordSet = new RecordSet();
        try {
            outRecordSet = sp.execute(inputRecord);
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get initial vaues for Agent", se);
            l.throwing(getClass().getName(), "getInitalValuesForAgent", ae);
            throw ae;
        }

        Record outputRecord = outRecordSet.getFirstRecord();

        l.exiting(getClass().toString(), "getInitalValuesForAgent");
        return outputRecord;
    }

    /** Method to get the assignment information for a CommRateScheudleId
     *
     * @param inputRecord
     * @return
     */
   public RecordSet getAssignmentForRateSchedule(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getAssignmentForRateSchedule", new Object[]{inputRecord});

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("commRateSchedId", "commRateScheduleId"));

        StoredProcedureDAO sp = StoredProcedureDAO.getInstance(STORED_PROC_COMM_RATE_SCHED_ASSIGNMENT, mapping);
        RecordSet outRecordSet = new RecordSet();
        try {
            outRecordSet = sp.execute(inputRecord);
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get schedule assignment values for commission rate schedule", se);
            l.throwing(getClass().getName(), "getAssignmentForRateSchedule", ae);
            throw ae;
        }

        l.exiting(getClass().toString(), "getAssignmentForRateSchedule");
        return outRecordSet;
    }

    /**
     * method is used to determine if the commPayCode is available for display for policy
     * @param inputRecord: a record contains policyType, issueState, ans issueCompayEntityId
     * @return
     */
    public boolean isCommPayCodeAvailable(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "isCommPayCodeAvailableForInsert", new Object[]{inputRecord});
        boolean commPayCodeAvailable = false;
        try {
            DataRecordMapping mapping = new DataRecordMapping();

            mapping.addFieldMapping(new DataRecordFieldMapping("policyType", "policyTypeCode"));
            mapping.addFieldMapping(new DataRecordFieldMapping("issueState", "issueStateCode"));
            mapping.addFieldMapping(new DataRecordFieldMapping("issueComp", "issueCompanyEntityId"));

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(STORED_PROC_COMM_PAY_CODE_AVAILABLE, mapping);

            RecordSet outputRecordSet = spDao.execute(inputRecord);

            // Pull the stored procedure output information
            commPayCodeAvailable = outputRecordSet.getSummaryRecord().getBooleanValue(StoredProcedureDAO.RETURN_VALUE_FIELD).booleanValue();

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get value from " + STORED_PROC_COMM_PAY_CODE_AVAILABLE, e);
            l.throwing(getClass().getName(), "isCommPayCodeAvailableForInsert", ae);
            throw ae;
        }

        l.exiting(getClass().toString() + ".isCommPayCodeAvailableForInsert", Boolean.toString(commPayCodeAvailable));
        return commPayCodeAvailable;
    }
    /**
     * An agent has been saved on the policy?: call fmn_check_pth_commrate_exists to find out
     *
     * @param inputRecord a record containing at least policyId
     * @return true/false indicate if a agent exists for a given policy
     */
    public boolean agentExistsForPolicy(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "agentExistsForPolicy", new Object[]{inputRecord});
        boolean agentExists = false;
        DataRecordMapping mapping  = new DataRecordMapping();
//        mapping.addFieldMapping(new DataRecordFieldMapping("inPolicyId","policyId"));
        
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(STORED_PROC_AGENT_EXISTS,mapping);

            RecordSet outputRecordSet = spDao.execute(inputRecord);

            // Pull the stored procedure output information
            agentExists = outputRecordSet.getSummaryRecord().getBooleanValue(StoredProcedureDAO.RETURN_VALUE_FIELD).booleanValue();

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get value from " + STORED_PROC_AGENT_EXISTS, e);
            l.throwing(getClass().getName(), "agentExistsForPolicy", ae);
            throw ae;
        }
        l.exiting(getClass().toString() + ".agentExistsForPolicy", Boolean.toString(agentExists));
        return agentExists;
    }

   /** method to return true / false to indicate if it should hide the Add option 
     *   This method is used for complete transactionsCode only
     * @param inputRecord
     * @return true/false to indicate if we should hide the Add option
     */
       public boolean hideAddOptionForCompleteTransaction(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "hideAddOptionForCompleteTransaction", new Object[]{inputRecord});

        int  configurationCount = 0;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(STORED_PROC_HIDE_AGENT_COMPLETE );

            RecordSet outputRecordSet = spDao.execute(inputRecord);

            // Pull the stored procedure output information
            configurationCount = outputRecordSet.getSummaryRecord().getIntegerValue(StoredProcedureDAO.RETURN_VALUE_FIELD).intValue();

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get value from " + STORED_PROC_COMM_PAY_CODE_AVAILABLE, e);
            l.throwing(getClass().getName(), "hideAddOptionForCompleteTransaction", ae);
            throw ae;
        }

        boolean isHideConfigured = true;
        if (configurationCount == 0) {
           isHideConfigured = false;
        }
        l.exiting(getClass().toString() + ".hideAddOptionForCompleteTransaction", Boolean.toString(isHideConfigured));
        return isHideConfigured;
    }

    /** method to return true / false to indicate if it should hide the Add option
     *   This method is used for NonComplete transactionsCode only
     * @param inputRecord
     * @return true/false to indicate if we should hide the Add option
     */
       public boolean hideAddOptionForNonCompleteTransaction(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "hideAddOptionForNonCompleteTransaction", new Object[]{inputRecord});

        int  configurationCount = 0;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(STORED_PROC_HIDE_AGENT_NON_COMPLETE );
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            // Pull the stored procedure output information
            configurationCount = outputRecordSet.getSummaryRecord().getIntegerValue(StoredProcedureDAO.RETURN_VALUE_FIELD).intValue();
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get value from " + STORED_PROC_HIDE_AGENT_NON_COMPLETE, e);
            l.throwing(getClass().getName(), "hideAddOptionForNonCompleteTransaction", ae);
            throw ae;
        }

        boolean isHideConfigured = true;
        if (configurationCount == 0) {
           isHideConfigured = false;
        }
        l.exiting(getClass().toString() + ".hideAddOptionForNonCompleteTransaction", Boolean.toString(isHideConfigured));
        return isHideConfigured;
    }

    /**
     * method to add new agents for a policy by
     * calling Fmn_Chg_Pol_Pth_Agent_Info.Agent_Reassign
     *
     * @param inputRecords
     * @return
     */
    public int addAllAgentByReassign(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "addAllAgentByReassign", new Object[]{inputRecords});
        int updateCount = 0;

        DataRecordMapping mapping = new DataRecordMapping();

        mapping.addFieldMapping(new DataRecordFieldMapping("polId", "policyId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("agentLicenseId", "producerAgentLicId"));

        mapping.addFieldMapping(new DataRecordFieldMapping("newbusRate", "newbusCommRate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("newbusFlatAmount", "newbusCommFlatAmount"));
        mapping.addFieldMapping(new DataRecordFieldMapping("newbusSchedId", "newbusCommRateScheduleId"));

        mapping.addFieldMapping(new DataRecordFieldMapping("renewalRate", "renewalCommRate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("renewalFlatAmount", "renewalCommFlatAmount"));
        mapping.addFieldMapping(new DataRecordFieldMapping("renewalSchedId", "renewalCommRateScheduleId"));

        mapping.addFieldMapping(new DataRecordFieldMapping("ereRate", "ereCommRate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("ereFlatAmount", "ereCommFlatAmount"));
        mapping.addFieldMapping(new DataRecordFieldMapping("ereSchedId", "ereCommRateScheduleId"));

        mapping.addFieldMapping(new DataRecordFieldMapping("specialCond", "specialConditionCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("notes", "agentNote"));
        mapping.addFieldMapping(new DataRecordFieldMapping("subprodAgentLicenseId", "subproducerAgentLicId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("countersigAgentLicenseId", "countersignerAgentLicId"));

        mapping.addFieldMapping(new DataRecordFieldMapping("authrepAgentLicenseId", "authorizedrepAgentLicId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(STORED_PROC_SAVE_AGENT_REASSIGN, mapping);

        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to insert new agent(s).", e);
            l.throwing(getClass().getName(), "addAllAgentByReassign", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addAllAgentByReassign", new Integer(updateCount));
        }
        return updateCount;
    }



    /**
     * method to add new agents for a policy by
     * calling Fmn_Chg_Pol_Pth_Agent_Info.Adjust_Policy_Agent
     *
     * @param inputRecords
     * @return
     */
    public int addAllAgentByAdjust(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "addAllAgentByAdjust", new Object[]{inputRecords});
        int updateCount = 0;

        DataRecordMapping mapping = new DataRecordMapping();

        mapping.addFieldMapping(new DataRecordFieldMapping("polId", "policyId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("agentLicenseId", "producerAgentLicId"));

        mapping.addFieldMapping(new DataRecordFieldMapping("newbusRate", "newbusCommRate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("newbusFlatAmount", "newbusCommFlatAmount"));
        mapping.addFieldMapping(new DataRecordFieldMapping("newbusSchedId", "newbusCommRateScheduleId"));

        mapping.addFieldMapping(new DataRecordFieldMapping("renewalRate", "renewalCommRate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("renewalFlatAmount", "renewalCommFlatAmount"));
        mapping.addFieldMapping(new DataRecordFieldMapping("renewalSchedId", "renewalCommRateScheduleId"));

        mapping.addFieldMapping(new DataRecordFieldMapping("ereRate", "ereCommRate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("ereFlatAmount", "ereCommFlatAmount"));
        mapping.addFieldMapping(new DataRecordFieldMapping("ereSchedId", "ereCommRateScheduleId"));

        mapping.addFieldMapping(new DataRecordFieldMapping("specialCond", "specialConditionCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("notes", "agentNote"));

        mapping.addFieldMapping(new DataRecordFieldMapping("subprodAgentLicenseId", "subproducerAgentLicId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("countersigAgentLicenseId", "countersignerAgentLicId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("authrepAgentLicenseId", "authorizedrepAgentLicId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(STORED_PROC_SAVE_AGENT_AJUST, mapping);

        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to insert new agent(s).", e);
            l.throwing(getClass().getName(), "addAllAgentByAdjust", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addAllAgentByAdjust", new Integer(updateCount));
        }
        return updateCount;
    }

    /**
     * method to update existing agents for a policy by
     * calling Fmn_Chg_Pol_Pth_Agent_Info.update_policy_agent
     *
     * @param inputRecords
     * @return
     */
    public int updateAllAgent(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "updateAllAgent", new Object[]{inputRecords});
        int updateCount = 0;

        DataRecordMapping mapping = new DataRecordMapping();

        mapping.addFieldMapping(new DataRecordFieldMapping("specialCond", "specialConditionCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("notes", "agentNote"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(STORED_PROC_UPDATE_POLICY_AGENT, mapping);

        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to update existing agent(s).", e);
            l.throwing(getClass().getName(), "updateAllAgent", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "updateAllAgent", new Integer(updateCount));
        }
        return updateCount;
    }

    /**
     * checks if a valid subproducer has been selected.
     * @param inputRecord
     * @return
     */
    public YesNoFlag isValidSubproducerOnSave(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "isValidSubproducerOnSave", new Object[]{inputRecord});

        YesNoFlag returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Web_Agent.Valid_Subproducer");
        try {
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            returnValue = YesNoFlag.getInstance(outputRecordSet.getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD));
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to execute PM_Web_Agent.Valid_Subproducer.", e);
            l.throwing(getClass().getName(), "isValidSubproducerOnSave", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isValidSubproducerOnSave", returnValue);
        }

        return returnValue;
    }

    public boolean isLicensedAgent(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "isLicensedAgent", new Object[]{inputRecord});

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("agentLicenseId", "producerAgentLicId"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Web_Agent.Is_Licensed_Agent", mapping);
        boolean returnValue;
        try {
            RecordSet outputRS = spDao.execute(inputRecord);
            returnValue = YesNoFlag.getInstance(outputRS.getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD)).booleanValue();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to execute PM_Web_Agent.Is_Agent_Exists.", e);
            l.throwing(getClass().getName(), "isLicensedAgent", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isLicensedAgent", returnValue);
        }
        return returnValue;
    }
}
