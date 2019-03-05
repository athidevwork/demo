package dti.ci.agentmgr;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;

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
 * 03/04/2010       kshen       Added method loadAllSubProducer.
 * 07/19/2016       iwang       Issue 177546 - 1) Added loadAllAgentStaff, loadAllAgentStaffOverride.
 *                                             2) Modified saveAllAgent.
 * ---------------------------------------------------
 */
public interface AgentManager {

    /**
     * Method to load agent
     *
     * @param inputRecord a record containing entity information
     * @return recordSet resultset containing agent information
     */
    public Record loadAllAgent(Record inputRecord);

    /**
     * Method to load agent pay commission list
     *
     * @param inputRecord a record containing entity information
     * @return recordSet resultset containing agent pay commission information
     */
    public RecordSet loadAllAgentPayCommission(Record inputRecord);

    /**
     * Method to load agent contract list
     *
     * @param inputRecord a record containing entity information
     * @return recordSet resultset containing agent contract information
     */
    public RecordSet loadAllAgentContract(Record inputRecord);

    /**
     * Method to load agent contract commission list
     *
     * @param inputRecord a record containing entity information
     * @return recordSet resultset containing agent contract commission information
     */
    public RecordSet loadAllAgentContractCommission(Record inputRecord);

    /**
     * Method to load agent staff list
     *
     * @param inputRecord        a record containing entity information
     * @return recordSet resultSet containing agent staff information
     */
    public RecordSet loadAllAgentStaff(Record inputRecord);

    /**
     * Method to load agent staff override list
     *
     * @param inputRecord      a record containing entity information
     * @return recordSet resultSet containing agent staff override information
     */
    public RecordSet loadAllAgentStaffOverride(Record inputRecord);

    /**
     * Method to load agent available contract list
     *
     * @param inputRecord a record containing entity information
     * @return recordSet resultset containing agent contract commission information
     */
    public RecordSet loadAllAvailableAgentContract(Record inputRecord);

    /**
     * method to get the initial value when adding agent
     *
     * @param inputRecord
     * @return record
     */
    public Record getInitialValuesForAddAgent(Record inputRecord);

    /**
     * Save all input records with UPDATE_IND set to 'Y' - updated, 'I' - inserted, or 'D' - deleted.
     * @param inputRecord           agent information
     * @param payCommissionRecords  agent pay commission list
     * @param contractRecords       agent contract list
     * @param contractCommissionRecords    agent contract commission list
     * @return the number of rows updated.
     */
    public int saveAllAgent(Record inputRecord, RecordSet payCommissionRecords,
                            RecordSet contractRecords, RecordSet contractCommissionRecords,
                            RecordSet agentRecord, RecordSet agentOverrideRecord);

    /**
     * Load all sub producers of an producer.
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllSubProducer(Record inputRecord);

    /**
     * To load all agent output options for a given agent and policy
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllAgentOutputOption(Record inputRecord);

    /**
     * To get initial values for Add Output Option
     * @param inputRecord
     * @return
     */
    public Record getInitialValuesForAddOutputOption(Record inputRecord);

    /**
     * To save all agent output options
     * @param inputRecord
     * @param outputRs
     */
    public void saveAllAgentOutputOption(Record inputRecord, RecordSet outputRs);

}
