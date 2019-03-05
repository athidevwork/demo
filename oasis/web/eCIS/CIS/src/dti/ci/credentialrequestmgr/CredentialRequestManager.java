package dti.ci.credentialrequestmgr;

import dti.ci.credentialrequestmgr.rest.pojo.CredentialLetterRequest;
import dti.ci.credentialrequestmgr.rest.pojo.CredentialLetterStatus;
import dti.ci.credentialrequestmgr.rest.pojo.CredentialLetterStatuses;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * The business component of Credential Request.
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
public interface CredentialRequestManager {
    /**
     * Load Entity Detail.
     *
     * @param inputRecord
     * @return Record
     */
    public Record loadEntity(Record inputRecord);
    /**
     * Load Detail.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadDetail(Record inputRecord);
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
     * Send Credential Request To FM.
     * @param inputRecord
     * @return Record
     */
    public Record saveProcessRequest(Record inputRecord);

    /**
     * Send Request To Cincom.
     *
     * @param inputRecord
     * @return String
     */
    public Record submitRequest(Record inputRecord);
    }
