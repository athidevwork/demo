package dti.ci.credentialrequestmgr.resource.service.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;

import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   2/12/2019
 *
 * @author athi
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
     * Get Cache defaults for originating system.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet getCacheDefaults(Record inputRecord);

    /**
     * Saves Credential Request Submit request.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet saveCredentialRequest(Record inputRecord);

    /**
     * Validates requestor to be in policy or a policy holder.
     *
     * @param requestorId
     * @return
     */
    public String validateEntity(long requestorId);

    /**
     * Gets request status.
     *
     * @param requestId
     * @return
     */
    public RecordSet getRequestStatus(String requestId);

    /**
     * Gets Requestor status.
     *
     * @param requestorId
     * @return
     */
    public RecordSet getRequestorStatus(String requestorId);
}
