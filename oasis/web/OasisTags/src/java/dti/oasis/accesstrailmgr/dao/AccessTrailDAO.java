package dti.oasis.accesstrailmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

/**
 * This is an interface for DAO implementation of recording user activity.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 12, 2010
 *
 * @author James
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface AccessTrailDAO {

    /**
     * This method accepts the user activity information and saves it into database.
     *
     * @param inputRecord
     */
    public abstract void addAccessTrail(Record inputRecord);

    /**
     * This method accepts the user activity information and saves it into database.
     *
     * @param inputRecord
     */
    public abstract void addSessionInfo(Record inputRecord);    

    /**
     * This method get last login date/time.
     *
     * @param inputRecord
     */
    public String getPriorLoginTimestamp(Record inputRecord);

    /**
     * load latest active users
     *
     * @param record
     * @param recordLoadProcessor
     * @return
     */
    public abstract RecordSet loadAllActiveUsers(Record record, RecordLoadProcessor recordLoadProcessor);    
}
