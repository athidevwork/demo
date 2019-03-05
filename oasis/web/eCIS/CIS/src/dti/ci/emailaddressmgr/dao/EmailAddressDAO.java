package dti.ci.emailaddressmgr.dao;

import dti.oasis.recordset.Record;

/**
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 1, 2008
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/01/2010       kshen       Added method getAllClientEmailAddress to get all email address of a client.
 * ---------------------------------------------------
 */

public interface EmailAddressDAO {
    /**
     * Get e-mail address by client id.
     * If the client has no e-mail address, return null;
     * If the client has more than one e-mail address, the primary one returned.
     * @param clientId
     * @return
     */
    String getClientEmailAddress (Long clientId);

    /**
     * Get all email addresses of an client
     * @param record
     * @return
     */
    public Record getAllClientEmailAddress (Record record);
}
