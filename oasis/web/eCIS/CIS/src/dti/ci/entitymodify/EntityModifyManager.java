package dti.ci.entitymodify;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

public interface EntityModifyManager {

    /**
     * load name history
     * @param record
     * @return
     */
    public RecordSet loadNameHistory(Record record);

    /**
     * load tax data history
     * @param record
     * @return
     */
    public RecordSet loadTaxHistory(Record record);

    /**
     * load loss data histroy
     * @param record
     * @return
     */
    public RecordSet loadLossHistory(Record record);

    /**
     * load DBA history
     * @param record
     * @return
     */
    public RecordSet loadDbaHistory(Record record);

    /**
     * load electric data
     * @param record
     * @return
     */
    public RecordSet loadEtdHistory(Record record);

    /**
     * save entity data
     * @param record
     */
    public void saveEntityData(Record record) throws Exception ;

    /**
     * change Entity Type
     * @param record
     * @return
     */
    public String changeEntityType(Record record);

    /**
     * validate Policy number entered in Reference Number field
     * @param record
     * @return
     */
    public String validateReferenceNumberAsStr(Record record);

    /**
     * check if the client has active pol
     * @param record
     * @return
     */
    public String getClientDiscardPolCheck(Record record);

    /**
     * get the flag to show or hide ExpWitness tab menu
     * @param record
     * @return
     */
    public boolean getExpWitTabVisibilityflag(Record record);

    /**
     * get enttiy's type for given entity
     * @param record
     * @return
     */
    public String getEntityType(Record record);

    /**
     * get if there is notes existed for the entity
     * @param entityPk
     * @return
     */
    public boolean getIfEntityHasNoteExistsB(String entityPk);
}
