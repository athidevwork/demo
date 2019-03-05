package dti.ci.disabilitymgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * Created by IntelliJ IDEA.
 * User: yllu
 * Date: Feb 28, 2012
 * Time: 4:10:42 PM
 * To change this template use File | Settings | File Templates.
 */
public interface DisabilityManager {

    /**
     * Get Disability  List for an entity.
     * @param inputRecord
     * @return  RecordSet
     */
    public RecordSet loadDisabilityList(Record inputRecord);

    /**
     * Save disability Data for an entity
     * @param inputRecords
     * @return int
     */
    public int saveDisabilityData(RecordSet inputRecords);

}
