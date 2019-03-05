package dti.ci.vendormgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   8/13/14
 *
 * @author wkong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface PhoneLogDAO {

    /**
     * Get the phone log list for an entity.
     * @param inputRecord the information of an entity.
     * @return The phone log list of the entity.
     */
    public RecordSet getPhoneLog(Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * Save phone Log.
     * @param inputRecord the information of an entity.
     * @param recordSet the detail info.
     */
    public void savePhoneLog(RecordSet recordSet);
}
