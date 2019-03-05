package dti.ci.rolemgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

import java.util.List;

/**
 * <p>(C) 2018 Delphi Technology, inc. (dti)</p>
 * Date:   03/29/2018
 *
 * @author Herb Koenig
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface RoleManager {

    /**
     * Get Role List for an entity.
     * @param inputRecord
     * @return Recordset
     */
    public RecordSet loadRoleList(Record inputRecord);


}
