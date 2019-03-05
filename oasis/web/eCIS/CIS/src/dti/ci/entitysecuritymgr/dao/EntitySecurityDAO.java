package dti.ci.entitysecuritymgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * The db object of Security.
 *
 * <p>(C) 2013 Delphi Technology, inc. (dti)</p>
 * Date:   07/01/2013
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
public interface EntitySecurityDAO {

    /**
     * Get security
     *
     * @param inputRecord
     */
    public RecordSet getSecurity(Record inputRecord);
}
