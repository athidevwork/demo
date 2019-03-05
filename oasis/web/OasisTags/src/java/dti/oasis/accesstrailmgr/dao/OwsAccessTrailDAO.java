package dti.oasis.accesstrailmgr.dao;

import dti.oasis.recordset.Record;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 22, 2014
 *
 * @author Parker Xu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/29/2014       parker      Issue 138227. Enhancement to add the ows logs.
 *
 * ---------------------------------------------------
 */
public interface OwsAccessTrailDAO {

    /**
     * This method accepts the ows information and saves it into database.
     *
     * @param inputRecord
     */
    public abstract Record addOwsAccessTrail(Record inputRecord);

    /**
     * This method update the ows information and saves it into database.
     *
     * @param inputRecord
     */
    public abstract void updateOwsAccessTrail(Record inputRecord);

    /**
     * Check The Config For RequestName
     *
     * @param inputRecord
     * @return
     */
    public abstract String checkTheConfigForRequestName(Record inputRecord);
}
