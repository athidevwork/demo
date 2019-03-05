package dti.pm.policymgr.regionalmgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * Load all regional team.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 19, 2008
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface RegionalTeamManager {

    /**
     * Returns a RecordSet loaded with list of regional teams.
     * <p/>
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllRegionalTeam(Record inputRecord);

    /**
     * Returns a RecordSet loaded with list of regional team members.
     * <p/>
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllTeamUnderwriter(Record inputRecord);

    /**
     * Save all input records with UPDATE_IND set to 'Y' - updated, 'I' - inserted, or 'N' - Not changed.
     *
     * @param inputRecords       A set of regional team.
     * @param memberInputRecords A set of team member.
     * @return
     */
    public int saveAllRegionalTeamAndUnderwriter(RecordSet inputRecords, RecordSet memberInputRecords);

    /**
     * Get initial values for the regional team when adds a team.
     *
     * @return
     */
    public Record getInitialValuesForRegionalTeam();

    /**
     * Get initial values for the regional team member when adds a member.
     *
     * @param inputRecord
     * @return
     */
    public Record getInitialValuesForRegionalTeamMember(Record inputRecord);

    /**
     * Get the underwriter Id when the administrator selects the team member name.
     *
     * @param inputRecord
     * @return
     */
    public Record getUnderwriterId(Record inputRecord);

}
