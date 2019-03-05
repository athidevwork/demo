package dti.ci.entitymgr.dao;

import dti.oasis.data.DataRecordMapping;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

import java.sql.SQLException;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   April 09, 2012
 *
 * @author ldong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/24/2016       Elvin       Issue 176524: add searchEntityForWS
 * ---------------------------------------------------
 */
public interface EntityDAO {

    /**
     * Retrieves all Entity information
     *
     * @param record Record
     * @return RecordSet
     */
    public RecordSet loadEntityList(Record record);

    /**
     * save new entity
     *
     * @param inputRecord
     * @return Record
     */
    public Record AddEntity(Record inputRecord);

    /**
     * Save entity for service
     *
     * @param record
     */
    public void saveEntityForService(Record record);

    /**
     * save PartyNote
     *
     * @param inputRecord
     * @return Record
     */
    public Record savePartyNote(Record inputRecord);

    /**
     * Search entity by externalReferenceId and/or externalDataId
     *
     * @param inputRecord
     * @return
     */
    public RecordSet searchEntityForWS(Record inputRecord);

    /**
     * Load Entity Data
     * @param inputRecord
     * @return
     */
    public Record loadEntityData(Record inputRecord);

    /**
     * load entity name history
     * @param inputRecord
     * @return
     */
    public RecordSet loadNameHistory(Record inputRecord);

    /**
     * load tax history
     * @param inputRecord
     * @return
     */
    public RecordSet loadTaxHistory(Record inputRecord);

    /**
     * load loss history
     * @param inputRecord
     * @return
     */
    public RecordSet loadLossHistory(Record inputRecord);

    /**
     * load DBA history
     * @param inputRecord
     * @return
     */
    public RecordSet loadDbaHistory(Record inputRecord);

    /**
     * load electronic history
     * @param inputRecord
     * @return
     */
    public RecordSet loadEtdHistory(Record inputRecord);

    /**
     * save entity
     * @param inputRecord
     */
    public Record saveEntityData(Record inputRecord);

    /**
     * add entity
     *
     * @param inputRecord
     * @return
     */
    Record saveEntity(Record inputRecord);

    /**
     *  change Entity Type
      * @param inputRecord
     */
    public Record changeEntityType(Record inputRecord);

    /**
     * check if policy number entered in Reference number fiels is valid
     * @param record
     * @return
     */
    public boolean checkPolNo(Record record, DataRecordMapping mapping);

    /**
     * check if the entered policy number in reference number field is duplicated or not
     * @param record
     * @return
     */
    public boolean checkPolNoIsDuplicated(Record record);

    /**
     * Check whether the client has active policy associated
     * @param record
     * @return
     */
    public boolean getClientDiscardPolCheck(Record record);

    /**
     * get if this entity has Expert Witness classification
     * @param record
     * @return
     */
    public boolean getClientHasExpertWitnessClass(Record record);

    /**
     * Check if an entity has tax ID info.
     * @param inputRecord
     * @return Returns {@code true} if either "Tax ID" or "SSN" exists. Otherwise, returns {@code false};
     */
    public boolean hasTaxIdInfo(Record inputRecord);

    /**
     * Get entity type.
     * @param inputRecord
     * @return
     */
    public String getEntityType(Record inputRecord);


    /**
     * Get entity name.
     *
     * @param inputRecord
     * @return
     */
    String getEntityName(Record inputRecord);

}
