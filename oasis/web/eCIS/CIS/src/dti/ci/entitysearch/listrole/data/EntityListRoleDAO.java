package dti.ci.entitysearch.listrole.data;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * An interface that provides DAO operation for Entity List Role.
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 18, 2008
 *
 * @author ldong
 */
/* 
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 02/18/2011       kshen       Added method getGotoSourceUrl.
 *
 * ---------------------------------------------------
*/

public interface EntityListRoleDAO {
    /**
     * Retrieves all Entity Role information
     *
     * @param record Record
     * @return RecordSet
     */
    RecordSet loadEntityListRoleByEntity(Record record);

    /**
     * Get goto source url for role type code.
     * @param inputRecord
     * @return
     */
    public String getGotoSourceUrl(Record inputRecord);
}
