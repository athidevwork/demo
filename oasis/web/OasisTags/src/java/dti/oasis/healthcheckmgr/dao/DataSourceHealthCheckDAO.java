package dti.oasis.healthcheckmgr.dao;

import dti.oasis.recordset.Record;

/**
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 16, 2010
 *
 * @author fcb
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface DataSourceHealthCheckDAO {

    /**
     * Method to add check the database connectivity
     * <p/>
     *
     * @param inputRecord that represents the input data.
     * @return String that contains the return value
     */
    public String checkDatabaseConnectivity(Record inputRecord);

}
