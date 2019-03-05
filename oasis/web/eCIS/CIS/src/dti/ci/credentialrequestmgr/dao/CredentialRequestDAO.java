package dti.ci.credentialrequestmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * The DAO component of Credential Request.
 * <p/>
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:  03/04/2016
 *
 * @author jdingle
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface CredentialRequestDAO {
    /**
     * load detail
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadDetail(Record inputRecord);
    /**
     * load entity detail
     *
     * @param inputRecord
     * @return Record
     */
    public Record loadEntity(Record inputRecord);
    /**
     * Load Service Charge Accounts for Entity.
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllAccount(Record inputRecord);
    /**
     * Save Credential Request.
     * @param inputRecord
     * @return Record
     */
    public Record saveRequest(Record inputRecord);

    /**
     * Save Credential Request Detail.
     * @param inputRecordSet
     * @return RecordSet
     */
    public int saveAllRequestDetail(RecordSet inputRecordSet);

    /**
     * Ask FM to create a new Service Charge Account for Entity.
     * @param inputRecord
     * @return
     */
    public Record saveAccount(Record inputRecord);

    /**
     * Save Credential Request.
     * @param inputRecord
     * @return Record
     */
    public Record saveProcessRequest(Record inputRecord);

    /**
     * Generate the report XML.
     * @param inputRecord
     * @return String
     */
    public String exportData(Record inputRecord);
}
