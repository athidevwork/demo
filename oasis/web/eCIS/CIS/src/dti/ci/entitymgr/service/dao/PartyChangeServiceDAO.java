package dti.ci.entitymgr.service.dao;

import dti.oasis.recordset.Record;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   5/20/2016
 *
 * @author dpang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface PartyChangeServiceDAO {

    /**
     * Set hub origin to db session variable
     * @param record
     */
    void setHubOrigin(Record record);
}
