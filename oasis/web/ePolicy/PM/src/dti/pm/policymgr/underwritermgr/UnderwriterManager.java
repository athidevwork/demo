package dti.pm.policymgr.underwritermgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.tags.OasisFields;
import dti.pm.policymgr.PolicyHeader;

/**
 * An interface to handle CRUD operation on Underwriter information.
 * <p/>
 * <p>(C) 2007 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 10, 2007
 *
 * @author Bill Hong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/20/2013       awu         added isUnderwriterEntityExists
 * 06/05/2013       awu        Add addUnderwriterTeam, addTeamMembers, getUnderwriterTeam
 * 09/07/2015       awu        Add loadUnderwritersByTermForWS.
 * ---------------------------------------------------
 */
public interface UnderwriterManager {

    /**
     * Retrieves all underwriters' information
     *
     * @param policyHeader policy header
     * @return RecordSet
     */
    RecordSet loadAllUnderwriters(PolicyHeader policyHeader);

    /**
     * Retrieves underwriters' information based on policy term
     *
     * @param policyHeader policy header
     * @return RecordSet
     */
    RecordSet loadUnderwritersByTerm(PolicyHeader policyHeader);

    /**
     * Saves all underwriters' information
     *
     * @param policyHeader policy header
     * @param inputRecords    input records
     * @return int
     */
    int saveAllUnderwriters(PolicyHeader policyHeader, RecordSet inputRecords);

    /**
     * Retrieves additional policy information
     *
     * @param policyHeader
     * @return Record
     */
    Record loadAdditionalPolicyInfo(PolicyHeader policyHeader);

    /**
     * Initial values defaults for a new underwriter record
     * @param inputRecord contains policy term id level information
     * @return Record
     */
    Record getInitialValues(Record inputRecord);

    /**
     * Retrieve all policy info by from underwriter and other search criteria
     *
     * @param inputRecord
     * @return
     */
    RecordSet loadAllPolicyByUnderwriter(Record inputRecord);

    /**
     * perform transfer underwriter
     *
     * @param inputRecord
     * @return
     */
    Record performTransferUnderwriter(Record inputRecord);

    /**
     * To check the entity id is a valid underwriter or not
     * @param inputRecord
     * @param policyHeader
     * @return
     */
    boolean isUnderwriterEntityExists(Record inputRecord, PolicyHeader policyHeader);

    /**
     * add underwriter and its team members
     * @param inputRecord
     * @param inputRecordSet
     */
    void addUnderwriterTeam(Record inputRecord, RecordSet inputRecordSet, PolicyHeader policyHeader);

    /**
     * add team members and expire existed roles.
     * @param inputRecord
     * @param inputRecordSet
     * @param policyHeader
     * @return
     */
    RecordSet addTeamMembers(Record inputRecord, RecordSet inputRecordSet, PolicyHeader policyHeader);

    /**
     * get the underwriter's team.
     * @param inputRecord
     * @return
     */
    Record getUnderwriterTeam(Record inputRecord);

    /**
     * Retrieves underwriters' information based on policy term
     * called by web service.
     * @param policyHeader
     * @return
     */
    RecordSet loadUnderwritersByTermForWS(PolicyHeader policyHeader);

}
