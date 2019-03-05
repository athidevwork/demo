package dti.ci.agentmgr.dao;

import dti.cs.data.BaseDAO;
import dti.oasis.app.AppException;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.struts.AddSelectIndLoadProcessor;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the AgentDAO interface. It provides the implementation details of all DAO
 * operations that are performed against the agent manager.
 * <p/>
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
 * 03/04/2010       kshen       Added method loadAllSubProducer.
 * 07/19/2016       iwang       Issue 177546 - Added load, add and update methods for Agents, Agent Overrides.
 * 05/31/2018       ylu         Issue 109213: refactor update - remove unnecessary long line.
 * ---------------------------------------------------
 */
public class AgentJdbcDAO extends BaseDAO implements AgentDAO {

    /**
     * load agent information
     *
     * @param inputRecord
     * @return
     */
    public Record loadAllAgent(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllAgent", new Object[]{inputRecord});

        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("CI_Web_Agent.Sel_Agent_Info");
        RecordSet outRecordSet = null;
        try {
            outRecordSet = sp.execute(inputRecord);
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get agent information", se);
            l.throwing(getClass().getName(), "loadAllAgent", ae);
            throw ae;
        }
        Record outputRecord = null;
        if (outRecordSet.getSize() > 0) {
            outputRecord = outRecordSet.getFirstRecord();
        } else {
            outputRecord = new Record();
        }
        l.exiting(getClass().toString(), "loadAllAgent", outputRecord);
        return outputRecord;
    }

    /**
     * Method to load agent pay commission list
     *
     * @param inputRecord         a record containing entity information
     * @param recordLoadProcessor a processor to be added to the recordSet before returned
     * @return recordSet resultset containing agent pay commission information
     */
    public RecordSet loadAllAgentPayCommission(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAgentPayCommission", new Object[]{inputRecord});
        }
        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("CI_Web_Agent.Sel_Agent_Pay_Commission");
        RecordSet outRecordSet = null;
        try {
            outRecordSet = sp.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Error when executing loadAllAgentPayCommission", se);
            l.throwing(getClass().getName(), "loadAllAgentPayCommission", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAgentPayCommission", outRecordSet);
        }
        return outRecordSet;
    }


    /**
     * Method to load agent contract list
     *
     * @param inputRecord         a record containing entity information
     * @param recordLoadProcessor a processor to be added to the recordSet before returned
     * @return recordSet resultset containing agent contract information
     */
    public RecordSet loadAllAgentContract(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAgentContract", new Object[]{inputRecord});
        }
        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("CI_Web_Agent.Sel_Agent_Contract");
        RecordSet outRecordSet = null;
        try {
            outRecordSet = sp.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Error when executing loadAllAgentContract", se);
            l.throwing(getClass().getName(), "loadAllAgentContract", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAgentContract", outRecordSet);
        }
        return outRecordSet;
    }

    /**
     * Method to load agent contract commission list
     *
     * @param inputRecord         a record containing entity information
     * @param recordLoadProcessor a processor to be added to the recordSet before returned
     * @return recordSet resultset containing agent contract commission information
     */
    public RecordSet loadAllAgentContractCommission(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAgentContractCommission", new Object[]{inputRecord});
        }
        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("CI_Web_Agent.Sel_Agent_Contract_Commission");
        RecordSet outRecordSet = null;
        try {
            outRecordSet = sp.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Error when executing loadAllAgentContractCommission", se);
            l.throwing(getClass().getName(), "loadAllAgentContractCommission", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAgentContractCommission", outRecordSet);
        }
        return outRecordSet;
    }

    /**
     * Method to load agent staff list
     *
     * @param inputRecord        a record containing entity information
     * @param recordLoadProcessor a processor to be added to the recordSet before returned
     * @return recordSet resultSet containing agent staff information
     */
    public RecordSet loadAllAgentStaff(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAgentStaff", new Object[]{inputRecord});
        }
        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("CI_Web_Agent.Sel_Agent_Staff");
        RecordSet outRecordSet = null;
        try {
            outRecordSet = sp.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Error when executing loadAllAgentStaff", se);
            l.throwing(getClass().getName(), "loadAllAgentStaff", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAgentStaff", outRecordSet);
        }
        return outRecordSet;
    }

    /**
     * Method to load agent staff override list
     *
     * @param inputRecord      a record containing entity information
     * @param recordLoadProcessor a processor to be added to the recordSet before returned
     * @return recordSet resultSet containing agent staff override information
     */
    public RecordSet loadAllAgentStaffOverride(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAgentStaffOverride", new Object[]{inputRecord});
        }
        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("CI_Web_Agent.Sel_Agent_Staff_Override");
        RecordSet outRecordSet = null;
        try {
            outRecordSet = sp.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Error when executing loadAllAgentStaffOverride", se);
            l.throwing(getClass().getName(), "loadAllAgentStaffOverride", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAgentStaffOverride", outRecordSet);
        }
        return outRecordSet;
    }
    
    /**
      * load available agent contract list
      * @param inputRecord
      * @return
      */
     public RecordSet loadAllAvailableAgentContract(Record inputRecord) {
         Logger l = LogUtils.getLogger(getClass());
         if (l.isLoggable(Level.FINER)) {
             l.entering(getClass().getName(), "loadAllAvailableAgentContract", new Object[]{inputRecord});
         }
         StoredProcedureDAO sp = StoredProcedureDAO.getInstance("CI_Web_Agent.Sel_Available_Agent_Contract");
         RecordSet outRecordSet = null;
         try {
             outRecordSet = sp.execute(inputRecord);
         }
         catch (SQLException se) {
             AppException ae = ExceptionHelper.getInstance().handleException("Error when executing loadAllAvailableAgentContract", se);
             l.throwing(getClass().getName(), "loadAllAvailableAgentContract", ae);
             throw ae;
         }
         if (l.isLoggable(Level.FINER)) {
             l.exiting(getClass().getName(), "loadAllAvailableAgentContract", outRecordSet);
         }
         return outRecordSet;
     }

    /**
     * save agent information
     *
     * @param inputRecord
     * @return
     */
    public void saveAllAgent(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllAgent", new Object[]{inputRecord});
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CI_Web_Agent.Save_Agent");
        try {
            spDao.execute(inputRecord);
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save agent information.", e);
            l.throwing(getClass().getName(), "saveAllAgent", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllAgent");
        }
    }

    /**
     * add new agent pay commission
     *
     * @param inputRecords
     * @return
     */
    public int addAllAgentPayCommission(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "addAllAgentPayCommission", new Object[]{inputRecords});
        int updateCount = 0;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CI_Web_Agent.Add_Agent_Pay_Commission");
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to insert agent pay commission(s).", e);
            l.throwing(getClass().getName(), "addAllAgentPayCommission", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addAllAgentPayCommission", new Integer(updateCount));
        }
        return updateCount;
    }

    /**
     * add new agent staff
     *
     * @param inputRecords
     * @return
     */
    public int addAllAgentStaff(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "addAllAgentStaff", new Object[]{inputRecords});
        int updateCount = 0;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CI_Web_Agent.Add_Agent_Staff");
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to insert agent staff(s).", e);
            l.throwing(getClass().getName(), "addAllAgentStaff", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addAllAgentStaff", new Integer(updateCount));
        }
        return updateCount;
    }

    /**
     * add new agent staff override
     *
     * @param inputRecords
     * @return
     */
    public int addAllAgentStaffOverride(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "addAllAgentStaffOverride", new Object[]{inputRecords});
        int updateCount = 0;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CI_Web_Agent.Add_Agent_Staff_Override");
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to insert agent staff(s).", e);
            l.throwing(getClass().getName(), "addAllAgentStaffOverride", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addAllAgentStaffOverride", new Integer(updateCount));
        }
        return updateCount;
    }

    /**
     * update agent pay commission
     *
     * @param inputRecords
     * @return
     */
    public int updateAllAgentPayCommission(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "updateAllAgentPayCommission", new Object[]{inputRecords});
        int updateCount = 0;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CI_Web_Agent.Update_Agent_Pay_Commission");
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to update agent pay commission(s).", e);
            l.throwing(getClass().getName(), "updateAllAgentPayCommission", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "updateAllAgentPayCommission", new Integer(updateCount));
        }
        return updateCount;
    }

    /**
     * update agent pay staffs
     *
     * @param inputRecords
     * @return
     */
    public int updateAllAgentStaff(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "updateAllAgentStaff", new Object[]{inputRecords});
        int updateCount = 0;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CI_Web_Agent.Update_Agent_Staff");
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to update agent staff(s).", e);
            l.throwing(getClass().getName(), "updateAllAgentStaff", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "updateAllAgentStaff", new Integer(updateCount));
        }
        return updateCount;
    }

    /**
     * update agent pay staff overrides
     *
     * @param inputRecords
     * @return
     */
    public int updateAllAgentStaffOverride(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "updateAllAgentStaffOverride", new Object[]{inputRecords});
        int updateCount = 0;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CI_Web_Agent.Update_Agent_Staff_Override");
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to update agent staff(s).", e);
            l.throwing(getClass().getName(), "updateAllAgentStaffOverride", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "updateAllAgentStaffOverride", new Integer(updateCount));
        }
        return updateCount;
    }

    /**
     * add new agent contract
     *
     * @param inputRecords
     * @return
     */
    public int addAllAgentContract(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "addAllAgentContract", new Object[]{inputRecords});
        int updateCount = 0;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CI_Web_Agent.Add_Agent_Contract");
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to insert agent contract(s).", e);
            l.throwing(getClass().getName(), "addAllAgentContract", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addAllAgentContract", new Integer(updateCount));
        }
        return updateCount;
    }

    /**
     * update agent contract
     *
     * @param inputRecords
     * @return
     */
    public int updateAllAgentContract(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "updateAllAgentContract", new Object[]{inputRecords});
        int updateCount = 0;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CI_Web_Agent.Update_Agent_Contract");
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to update agent contract(s).", e);
            l.throwing(getClass().getName(), "updateAllAgentContract", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "updateAllAgentContract", new Integer(updateCount));
        }
        return updateCount;
    }

    /**
     * add agent contract
     *
     * @param inputRecords
     * @return
     */
    public int addAllAgentContractCommission(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "addAllAgentContractCommission", new Object[]{inputRecords});
        int updateCount = 0;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CI_Web_Agent.Add_Agent_Contract_Commission");
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to insert agent contract commission(s).", e);
            l.throwing(getClass().getName(), "addAllAgentContractCommission", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addAllAgentContractCommission", new Integer(updateCount));
        }
        return updateCount;
    }

    /**
     * update agent contract commission
     *
     * @param inputRecords
     * @return
     */
    public int updateAllAgentContractCommission(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "updateAllAgentContractCommission", new Object[]{inputRecords});
        int updateCount = 0;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CI_Web_Agent.Update_Agent_Contract_Comm");
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to update agent contract commission(s).", e);
            l.throwing(getClass().getName(), "updateAllAgentContractCommission", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "updateAllAgentContractCommission", new Integer(updateCount));
        }
        return updateCount;
    }

    /**
     * Load all sub producers of an producer.
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllSubProducer(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllSubProducer", new Object[]{inputRecord});
        }

        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("CI_Web_Agent.Sel_Sub_Producer");

        try {
            RecordSet rs = sp.execute(inputRecord, AddSelectIndLoadProcessor.getInstance());
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllSubProducer", rs);
            }
            return rs;
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to load sub producers.", se);
            l.throwing(getClass().getName(), "loadAllSubProducer", ae);
            throw ae;
        }
    }

    /**
     * To load all agent output options
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllAgentOutputOption(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllAgentOutputOption", new Object[]{inputRecord});

        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CI_Web_Agent.Sel_Agent_Output_Option");

        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to load all agent output options.", e);
            l.throwing(getClass().getName(), "loadAllAgentOutputOption", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "loadAllAgentOutputOption", rs);
        return rs;
    }

    /**
     * To save all agent output options
     *
     * @param outputRs
     */
    public void saveAllAgentOutputOption(RecordSet outputRs) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllAgentOutputOption", new Object[]{outputRs});

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CI_Web_Agent.Save_Agent_Output_Option");
        try {
            spDao.executeBatch(outputRs);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                    "Failed to save all agent output options.", e);
            l.throwing(getClass().getName(), "saveAllAgentOutputOption", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "saveAllAgentOutputOption");
    }

}
