package dti.ci.agentmgr.dao;

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
 * 03/04/2010       kshen       Added method loadAllSubProducer.
 * 07/19/2016       iwang       Issue 177546 - Added load, add and update methods for Agents, Agent Overrides.
 * ---------------------------------------------------
 */
public interface AgentDAO {

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
     * @param inputRecord         a record containing entity information
     * @param recordLoadProcessor a processor to be added to the recordSet before returned
     * @return recordSet resultset containing agent pay commission information
     */
    public RecordSet loadAllAgentPayCommission(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * Method to load agent contract list
     *
     * @param inputRecord         a record containing entity information
     * @param recordLoadProcessor a processor to be added to the recordSet before returned
     * @return recordSet resultset containing agent contract information
     */
    public RecordSet loadAllAgentContract(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * Method to load agent contract commission list
     *
     * @param inputRecord         a record containing entity information
     * @param recordLoadProcessor a processor to be added to the recordSet before returned
     * @return recordSet resultset containing agent contract commission information
     */
    public RecordSet loadAllAgentContractCommission(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * Method to load agent staff list
     *
     * @param inputRecord        a record containing entity information
     * @return recordSet resultSet containing agent staff information
     */
    public RecordSet loadAllAgentStaff(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * Method to load agent staff override list
     *
     * @param inputRecord      a record containing entity information
     * @param recordLoadProcessor a processor to be added to the recordSet before returned
     * @return recordSet resultSet containing agent staff override information
     */
    public RecordSet loadAllAgentStaffOverride(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * Method to load agent available contract list
     *
     * @param inputRecord a record containing entity information
     * @return recordSet resultset containing agent contract commission information
     */
    public RecordSet loadAllAvailableAgentContract(Record inputRecord);

    /**
     * method to save agent information
     *
     * @param inputRecord
     * @return
     */
    public void saveAllAgent(Record inputRecord);


    /**
     * method to add new agent pay commissions
     *
     * @param inputRecords
     * @return
     */
    public int addAllAgentPayCommission(RecordSet inputRecords);

    /**
     * method to add new agent staff
     *
     * @param inputRecords
     * @return
     */
    public int addAllAgentStaff(RecordSet inputRecords);

    /**
     * method to add new agent staff overrides
     *
     * @param inputRecords
     * @return
     */
    public int addAllAgentStaffOverride(RecordSet inputRecords);

    /**
     * method to update existing agent pay commissions
     *
     * @param inputRecords
     * @return
     */
    public int updateAllAgentPayCommission(RecordSet inputRecords);

    /**
     * method to update existing agent staffs
     *
     * @param inputRecords
     * @return
     */
    public int updateAllAgentStaff(RecordSet inputRecords);

    /**
     * method to update existing agent overrides
     *
     * @param inputRecords
     * @return
     */
    public int updateAllAgentStaffOverride(RecordSet inputRecords);

    /**
     * method to add new agent Contract
     *
     * @param inputRecords
     * @return
     */
    public int addAllAgentContract(RecordSet inputRecords);

    /**
     * method to update existing agent Contract
     *
     * @param inputRecords
     * @return
     */
    public int updateAllAgentContract(RecordSet inputRecords);

    /**
     * method to add new agent contract commissions
     *
     * @param inputRecords
     * @return
     */
    public int addAllAgentContractCommission(RecordSet inputRecords);

    /**
     * method to update existing agent contract commissions
     *
     * @param inputRecords
     * @return
     */
    public int updateAllAgentContractCommission(RecordSet inputRecords);

    /**
     * Load all sub producers of an producer.
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllSubProducer(Record inputRecord);

    /**
     * To load all agent output options
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllAgentOutputOption(Record inputRecord);

    /**
     * To save all agent output options
     * @param outputRs
     */
    public void saveAllAgentOutputOption(RecordSet outputRs);
}
