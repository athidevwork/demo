package dti.ci.disabilitymgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * Created by IntelliJ IDEA.
 * User: yllu
 * Date: Feb 28, 2012
 * Time: 5:25:12 PM
 * To change this template use File | Settings | File Templates.
 */
public interface DisabilityDAO {
    /**
     * Get the Disability list of an entity.
     * @param inputRecord
     * @return
     */
    public RecordSet getDisabilityList(Record inputRecord);

    /**
    * Method to save disability information
    *
    * @param inputRecords
    * @return int
    */
    public int saveDisabilityData(RecordSet inputRecords);
}
