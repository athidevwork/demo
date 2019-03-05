package dti.ci.entitysearch.listrole.bo;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

import java.util.List;

/**
 * An interface to handle Entity List Role page information.
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 18, 2008
 *
 * @author ldong
 */
/* 
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/16/2009       Jacky       Add 'Jurisdiction' logic for issue #97673 
 * 02/18/2011       kshen       Added method getGotoSourceUrl.
 * 08/18/2016       ylu         Issue 178205: handle with velocity data
 * 01/12/2017       Elvin       Issue 182136: Velocity Integration
 * ---------------------------------------------------
*/

public interface EntityListRoleManager {
    /**
     * Retrieves all Entity Roles information
     *
     * @param entityPk
     * @return RecordSet
     */
    RecordSet loadEntityListRoleByEntity(String entityPk);

    /**
     * Retrieves Entity Roles information by filter
     *
     * @param entityPk
     * @param filter
     * @return RecordSet
     */
    RecordSet loadEntityListRoleByEntity(String entityPk, String filter);

    /**
     * Get goto source url for role type code.
     * @param inputRecord
     * @return
     */
    public String getGotoSourceUrl(Record inputRecord);

    /**
     * Get Velocity data
     * @param entityPk
     * @return
     */
    public RecordSet getVelocityPolicyData(String entityPk, List<String> fieldNameList);
}
