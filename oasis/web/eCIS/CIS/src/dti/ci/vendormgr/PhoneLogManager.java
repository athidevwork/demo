package dti.ci.vendormgr;

import dti.oasis.recordset.Record;
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
 * 11/08/2018       Elvin       Issue 195627: enable default values setting when adding phone log
 * ---------------------------------------------------
 */
public interface PhoneLogManager {
    /**
     * Get the phone log list for an entity.
     * @param inputRecord the information of an entity.
     * @return The phone log list of the entity.
     */
    public RecordSet getPhoneLog(Record inputRecord);

    /**
     * Save phone Log.
     * @param recordSet the detail info.
     */
    public void savePhoneLog(RecordSet recordSet);

    public Record getFieldDefaultValues(Record inputRecord);
}
