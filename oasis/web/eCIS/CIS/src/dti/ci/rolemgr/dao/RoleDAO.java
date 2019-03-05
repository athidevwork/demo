package dti.ci.rolemgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

import java.util.List;

/**
 * <p>(C) 2018 Delphi Technology, inc. (dti)</p>
 * Date:   03/28/2018
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
public interface RoleDAO {


    /**
     * Get the role list for an entity.
     *
     * @param inputRecord
     * @param recordLoadProcessor
     * @return Recordset
     */
    RecordSet getRoleList(Record inputRecord, RecordLoadProcessor recordLoadProcessor);


}
