package dti.pm.policymgr.regionalmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

/**
 * An interface that provides DAO operation for maintain regional team.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 19, 2008
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/13/2014        awu        148783 - Modified loadAllRegionalTeam to add record load processor.
 * ---------------------------------------------------
 */
public interface RegionalTeamDAO {

    /**
     * Returns a RecordSet loaded with list of regional teams.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllRegionalTeam(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * Returns a RecordSet loaded with list of regional team underwriters.
     *
     * @param inputRecord
     * @param recordLoadProcessor
     * @return RecordSet
     */
    public RecordSet loadAllTeamUnderwriter(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * Save the regional team.
     * If the indicator rowStatus is 'NEW', system saves this record, 'MODIFIED' for update and 'DELETED' for delete.
     *
     * @param inputRecords
     * @return the number of rows saved.
     */
    public int saveAllRegionalTeam(RecordSet inputRecords);

    /**
     * Save regional team underwriter.
     * If the indicator rowStatus is 'NEW', system saves this record, 'MODIFIED' for update and 'DELETED' for delete.
     *
     * @param inputRecords
     * @return the number of rows saved.
     */
    public int saveAllTeamUnderwriter(RecordSet inputRecords);

    /**
     * Get underwriter Id when the administrator selects the team member name.
     *
     * @param inputRecord
     * @return
     */
    public RecordSet getUnderwriterId(Record inputRecord);

}
