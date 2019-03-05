package dti.pm.agentmgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.pm.policymgr.PolicyHeader;

/**
 * Interface to handle Agent data.
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
 * Apr 17, 2008     James       Issue#75265 Modify code according to code review
 * 10/07/2008       yhyang      Issue#86934 Move CIS Agent to eCIS.
 *
 * 03/13/2013       awu         141924 - Added validateLicensedAgent.
 * 03/22/2013       skommi      Issue#111565 Added loadAllAgentHistory() method.
 * ---------------------------------------------------
 */
public interface AgentManager {

    /**
     * Method to load agent  and its related data for a policy
     *
     * @param inputRecord a record containing policy information
     * @return recordSet resultset containing agents and their commission information
     */
    public RecordSet loadAllPolicyAgent(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Method to load expired agents for a policy
     *
     * @param inputRecord a record containing policy information
     * @return recordSet resultset containing agents and their commission information
     */
    public RecordSet loadAllAgentHistory(Record inputRecord);

    /**
     * Method to load agent summary
     * @param inputRecord input record that contains policy id
     * @return agent summary
     */
    public RecordSet loadAllPolicyAgentSummary(Record inputRecord);

    /**
     * Validate all input records
     *
     * @param policyHeader the summary policy information corresponding to the provided agents.
     * @param inputRecords a set of Records, each with the updated Agent Detail info
     *  matching the fields returned from the loadAllAgent method.
     * @return record.
     */
    public void validateAllPolicyAgent(PolicyHeader policyHeader, RecordSet inputRecords);

  /**
     * Save all input records with UPDATE_IND set to 'Y' - updated, 'I' - inserted, or 'D' - deleted.
     *
     * @param policyHeader the summary policy information corresponding to the provided agents.
     * @param inputRecords a set of Records, each with the updated Agent Detail info
     *  matching the fields returned from the loadAllAgent method.
     * @return the number of rows updated.
     */
    public int saveAllPolicyAgent(PolicyHeader policyHeader, RecordSet inputRecords);
   /**
     * method to load the initial license information for a agent
     *
     * @param policyHeader: summary information about the policy
     * @param inputRecord a record contains licenseClassCode(PRODUCER, SUB_PROD, COUNT_SIGN, AUTH_REP)
     *                    state code, policyTypeCode, policyEffDate, policyId
     * @return record
     */
    public Record getInitialValuesForPolicyAgent(PolicyHeader policyHeader, Record inputRecord);
  /**
     * method to get the initial value when adding a agent
     *
     * @param inputRecord  
     *
     * @return record
     */
    public Record getInitialValuesForAddPolicyAgent(PolicyHeader policyHeader, Record inputRecord);

    /**
     * validate the producer exists or not.
     * @param inputRecord
     */
    public void validateLicensedAgent(Record inputRecord, PolicyHeader policyHeader);
}
