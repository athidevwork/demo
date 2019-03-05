package dti.pm.agentmgr.dao;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

/**
 * An interface that provides DAO operation for Agents.
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
 * 10/07/2008       yhyang      Issue#86934 Move CIS Agent to eCIS. 
 * 03/13/2013       awu         141924 - Added isLicensedAgent.
 * ---------------------------------------------------
 */
public interface AgentDAO {

    /**
     * Method to load agent  and its related data for a policy
     *
     * @param inputRecord         a record containing policy information
     * @param recordLoadProcessor a processor to be added to the recordSet before returned
     * @return recordSet resultsset of agent and commission information
     */
    public RecordSet loadAllAgent(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * Method to load agent summary
     *
     * @param inputRecord input record that contains policy id
     * @return agent summary
     */
    public RecordSet loadAllAgentSummary(Record inputRecord);

    /**
     * method to load the initial license information for a given agent
     * that indicated by inputRecord's field: agentLicenseId
     *
     * @param inputRecord a record contains one of the valid licenseClassCode(
     *                    PRODUCER, SUB_PROD, COUNT_SIGN, AUTH_REP)
     *                    state code, policyTypeCode, policyEffDate, policyId, more important, a agentLicId
     * @return record
     */
    public Record getInitalValuesForAgent(Record inputRecord);


    /**
     * Method to get the assignment information for a CommRateScheudleId
     *
     * @param inputRecord
     * @return
     */
    public RecordSet getAssignmentForRateSchedule(Record inputRecord);

    /**
     * method is used to determine if the commPayCode is available for insert a new agent for policy
     *
     * @param inputRecord: a record contains policyType, issueState, ans issueCompayEntityId
     * @return
     */
    public boolean isCommPayCodeAvailable(Record inputRecord);

    /**
     * An agent has been saved on the policy?: call fmn_check_pth_commrate_exists to find out
     *
     * @param inputRecord a record containing at least policyId
     * @return true/false indicate if a agent exists for a given policy
     */
    public boolean agentExistsForPolicy(Record inputRecord);

    /**
     * method to return true / false to indicate if it should hide the Add option
     * This method is used for complete transactionsCode only
     *
     * @param inputRecord
     * @return true/false to indicate if we should hide the Add option
     */
    public boolean hideAddOptionForCompleteTransaction(Record inputRecord);

    /**
     * method to return true / false to indicate if it should hide the Add option
     * This method is used for NonComplete transactionsCode only
     *
     * @param inputRecord
     * @return true/false to indicate if we should hide the Add option
     */
    public boolean hideAddOptionForNonCompleteTransaction(Record inputRecord);

    /**
     * method to add new agents for a policy by
     * calling Fmn_Chg_Pol_Pth_Agent_Info.Agent_Reassign
     *
     * @param inputRecords
     * @return
     */
    public int addAllAgentByReassign(RecordSet inputRecords);

    /**
     * method to add new agents for a policy by
     * calling Fmn_Chg_Pol_Pth_Agent_Info.Adjust_Policy_Agent
     *
     * @param inputRecords
     * @return
     */
    public int addAllAgentByAdjust(RecordSet inputRecords);

    /**
     * method to update existing agents for a policy by
     * calling Fmn_Chg_Pol_Pth_Agent_Info.update_policy_agent
     *
     * @param inputRecords
     * @return
     */
    public int updateAllAgent(RecordSet inputRecords);

    /**
     * checks if a valid subproducer has been selected.
     *
     * @param inputRecord
     * @return
     */
    public YesNoFlag isValidSubproducerOnSave(Record inputRecord);

    /**
     * check if the producer exists or not.
     * @param inputRecord
     * @return
     */
    public boolean isLicensedAgent(Record inputRecord);
}
