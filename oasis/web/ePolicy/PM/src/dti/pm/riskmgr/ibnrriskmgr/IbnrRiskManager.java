package dti.pm.riskmgr.ibnrriskmgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.pm.policymgr.PolicyHeader;

/**
 * Interface to handle Implementation of IBNR Risk Manager.
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 07, 2011
 *
 * @author Dzhang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 
 * ---------------------------------------------------
 */
public interface IbnrRiskManager {

    /**
     * Returns a RecordSet loaded with list of available IBNR risk types for the provided
     * policy information.
     *
     * @param inputRecord Record contains input values
     * @return RecordSet a RecordSet loaded with list of available associated risk types.
     */
    RecordSet loadAllIbnrRiskType(Record inputRecord);

    /**
     * Change associated risk
     *
     * @param inputRecord Record contains input values
     */
    void processChangeAssociatedRisk(Record inputRecord);

    /**
     * load all the risks exist in a given policy that have IBNR inactive risks associated to them
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  input records that contains key information
     * @return a RecordSet loaded with list of available associated risk data.
     */
    public RecordSet loadAllAssociatedRisk(PolicyHeader policyHeader, Record inputRecord);

    /**
     * load all the risks exist in a given policy that have IBNR inactive risks associated to them
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  input records that contains key information
     * @return a RecordSet loaded with list of available IBNR Inactive risk data.
     */
    public RecordSet loadAllIbnrInactiveRisk(PolicyHeader policyHeader, Record inputRecord);

    /**
     * load all the risks exist in a given policy that have IBNR inactive risks associated to them
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  input records that contains key information
     * @return a RecordSet loaded with list of available associated risk for inactive risk data.
     */
    public RecordSet loadAllAssociatedRiskForInactiveRisk(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Get initial values for associated risk
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a record loaded with user entered data
     * @return a Record loaded with initial values
     */
    Record getInitialValuesForAddAssociatedRisk(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Get initial values for IBNR Inactive risk
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a record loaded with user entered data
     * @return a Record loaded with initial values
     */
    Record getInitialValuesForAddInactiveRisk(PolicyHeader policyHeader, Record inputRecord, String anchorColumnName);

    /**
     * Save all data in IBNR Inactive Risk page
     *
     * @param policyHeader    policy header that contains all key policy information.
     * @param inputRecordSets a record set with data to be saved
     */
    public void saveAllInactiveRisk(PolicyHeader policyHeader, RecordSet[] inputRecordSets);

    /**
     * To cancel active IBNR risk
     *
     * @param policyHeader
     * @param inputRecord
     */
    void performCancellation(PolicyHeader policyHeader, Record inputRecord);
}