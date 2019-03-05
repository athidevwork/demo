package dti.pm.policymgr.underwritermgr.dao;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.pm.policymgr.PolicyHeader;


/**
 * An interface that provides DAO operation for underwriter information.
 * <p/>
 * <p>(C) 2006 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 19, 2006
 *
 * @author Bhong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/20/2013       awu         141924 - Added isUnderwriterEntity
 * 06/05/2013        awu        138241 - Add loadAllUnderwriterTeamMember, getUnderwriterTeam
 * 09/07/2015       awu         164026 - Added loadAllUnderwriters without any page entitlement processor.
 * 04/24/2018       xnie        192517 - Added validateDuplicateUnderwriters.
 * ---------------------------------------------------
 */
public interface UnderwriterDAO {

    /**
     * Retrieves all underwriters' information
     *
     * @param record              input records
     * @param recordLoadProcessor an instance of the load processor to set page entitlements
     * @return RecordSet
     */
    RecordSet loadAllUnderwriters(Record record, RecordLoadProcessor recordLoadProcessor);

    /**
     * Save all given input records with the Pm_Save_Screens.Save_Underwriter stored procedure,
     * inserting information.
     *
     * @param inputRecords a set of Records, each with the PolicyHeader, PolicyIdentifier,
     *                     and Underwriter Detail info matching the fields returned from the loadTermUnderwriters method..
     * @return the number of rows inserted.
     */
    int addAllUnderwriters(RecordSet inputRecords);

    /**
     * Save all given input records with the Pm_Save_Screens.Save_Underwriter stored procedure,
     * updating the existing information.
     *
     * @param inputRecords a set of Records, each with the PolicyHeader, PolicyIdentifier,
     *                     and Underwriter Detail info matching the fields returned from the loadTermUnderwriters method..
     * @return the number of rows updated.
     */
    int updateAllUnderwriters(RecordSet inputRecords);

    /**
     * Retrieves additional policy information
     *
     * @param policyHeader
     * @return Record
     */
    Record loadAdditionalPolicyInfo(PolicyHeader policyHeader);

    /**
     * Saves additional policy information
     *
     * @param inputRecords
     */
    void saveAdditionalPolicyInfo(RecordSet inputRecords);


    /**
     * Retrieve all policy info by from underwriter and other search criteria
     *
     * @param inputRecord
     * @return policy list
     */
    RecordSet loadAllPolicyByUnderwriter(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * perform transfer Underwriter
     *
     * @param inputRecord
     * @return
     */
    Record performTransferUnderwriter(Record inputRecord);

    /**
     * check the entity id is validate
     * @param inputRecord
     * @return
     */
    YesNoFlag isUnderwriterEntity(Record inputRecord);

    /**
     * load the team members of the underwriter.
     * @param inputRecord
     * @return
     */
    RecordSet loadAllUnderwriterTeamMember(Record inputRecord);

    /**
     * get the underwriter's team.
     * @param record
     * @return
     */
    Record getUnderwriterTeam(Record record);

    /**
     * Load all the underwriter for web service.
     * @param record
     * @return
     */
    public RecordSet loadAllUnderwriters(Record record);

    /**
     * Validate duplicate underwriters.
     * @param record
     * @return
     */
    public String validateDuplicateUnderwriters(Record record);

}
