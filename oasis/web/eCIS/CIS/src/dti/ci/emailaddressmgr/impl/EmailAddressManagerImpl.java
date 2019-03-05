package dti.ci.emailaddressmgr.impl;

import dti.ci.emailaddressmgr.EmailAddressManager;
import dti.ci.emailaddressmgr.dao.EmailAddressDAO;
import dti.oasis.app.ConfigurationException;
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
 * 04/01/2010       kshen       getAllClientEmailAddress
 * ---------------------------------------------------
 */

public class EmailAddressManagerImpl extends EmailAddressManager {
    public String getClientEmailAddress(Long clientId) {                  
        return getEmailAddressDAO().getClientEmailAddress(clientId);
    }

    /**
     * Get all email addresses of an client
     *
     * @param record
     * @return
     */
    public Record getAllClientEmailAddress(Record record) {
        return getEmailAddressDAO().getAllClientEmailAddress(record);
    }


    public EmailAddressManagerImpl() {
    }

    public void verifyConfig() {
        if (getEmailAddressDAO() == null)
            throw new ConfigurationException("The required property 'emailAddressDAO' is missing.");
    }

    public EmailAddressDAO getEmailAddressDAO() {
        return emailAddressDAO;
    }

    public void setEmailAddressDAO(EmailAddressDAO emailAddressDAO) {
        this.emailAddressDAO = emailAddressDAO;
    }

    private EmailAddressDAO emailAddressDAO;
}
