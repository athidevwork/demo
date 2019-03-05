package dti.ci.credentialrequestmgr.resource.service;

import dti.ci.credentialrequestmgr.rest.pojo.CredentialLetterRequest;
import dti.ci.credentialrequestmgr.rest.pojo.CredentialLetterStatus;
import dti.ci.credentialrequestmgr.rest.pojo.CredentialLetterStatuses;
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
public interface CredentialRequestManager {
    /**
     * Saves the credential letter submit request to db.
     *
     * @param request
     * @return CredentialLetterStatus
     */
    public CredentialLetterStatus saveCredentialSubmitRequest(CredentialLetterRequest request);

    /**
     * Gets the status of the request submitted earlier.
     *
     * @param requestId
     * @return CredentialLetterStatus
     */
    public CredentialLetterStatus getRequestStatus(String requestId);

    /**
     * Gets all the pending requests submitted by a requestor.
     *
     * @param requestorId
     * @return CredentialLetterStatuses
     */
    public CredentialLetterStatuses getRequestorStatus(String requestorId);

    /**
     * Validates if the entity has a policy or in a policy.
     *
     * @param requestorId
     * @return
     */
    public String validateEntity(long requestorId);

    /**
     * Gets default values based on originating system.
     *
     * @param request
     * @return CredentialLetterDefaults
     */
    public Object getDefaultValues(CredentialLetterRequest request);

    /**
     * Starts the processing of get credential request status based on request id.
     *
     * @param requestId
     * @return CredentialLetterStatus
     */
    public CredentialLetterStatus processCredentialLetterStatus(String requestId);

    /**
     * Starts the processing of get requestor requests pending status requests.
     *
     * @param requestorId
     * @param userId
     * @return CredentialLetterStatuses
     */
    public CredentialLetterStatuses processRequestorStatus (String requestorId, String userId);

    /**
     * Starts the processing of credential letter submit request.
     *
     * @param clRequest
     * @param isValidEntities
     * @return CredentialLetterStatus
     */
    public CredentialLetterStatus performCredentialLetterSubmit(CredentialLetterRequest clRequest);
}
