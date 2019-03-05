package dti.ci.emailaddressmgr;

import dti.oasis.app.ApplicationContext;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.ci.emailaddressmgr.impl.EmailAddressManagerImpl;

import java.util.logging.Logger;
import java.util.logging.Level;

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
 * 04/01/2010       kshen       Added method getAllClientEmailAddress.
 * 04/16/2010       kshen       Added method getAllClientEmailAddressAsStr
 * ---------------------------------------------------
 */

public abstract class EmailAddressManager {
    /**
     * The bean name of a RequestStorageManager extension if this default is not used.
     */
    public static final String BEAN_NAME = "emailAddressManager";

        /**
     * Return an instance of the RequestStorageManager.
     */
    public synchronized static EmailAddressManager getInstance() {
        if (c_instance == null) {
            if (ApplicationContext.getInstance().hasBean(BEAN_NAME)) {
                c_instance = (EmailAddressManager) ApplicationContext.getInstance().getBean(BEAN_NAME);
            }
            else{
                c_instance = new EmailAddressManagerImpl();
            }
        }
        return c_instance;
    }


    /**
     * Get e-mail address by client id.
     * If the client has no e-mail address, return null;
     * If the client has more than one e-mail address, the primary one returned.
     * @param clientId
     * @return
     */
    public abstract String getClientEmailAddress (Long clientId);

    /**
     * Get all email addresses of an client
     * @param record
     * @return
     */
    public abstract Record getAllClientEmailAddress (Record record);

    public String getAllClientEmailAddressAsStr(Long clientId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAllClientEmailAddressAsStr", new Object[]{clientId});
        }

        Record record = new Record();
        EmailAddressFields.setEntityId(record, clientId.toString());

        Record emailAddressRecord = getAllClientEmailAddress(record);
        String emailAddress = "";
        String emailAddress1 = EmailAddressFields.getEmailAddress1(emailAddressRecord);
        String emailAddress2 = EmailAddressFields.getEmailAddress2(emailAddressRecord);
        String emailAddress3 = EmailAddressFields.getEmailAddress3(emailAddressRecord);

        if (!StringUtils.isBlank(emailAddress1)) {
            if (!StringUtils.isBlank(emailAddress)) {
                emailAddress = emailAddress + ";";
            }
            emailAddress = emailAddress + emailAddress1;
        }
        if (!StringUtils.isBlank(emailAddress2)) {
            if (!StringUtils.isBlank(emailAddress)) {
                emailAddress = emailAddress + ";";
            }
            emailAddress = emailAddress + emailAddress2;
        }
        if (!StringUtils.isBlank(emailAddress3)) {
            if (!StringUtils.isBlank(emailAddress)) {
                emailAddress = emailAddress + ";";
            }
            emailAddress = emailAddress + emailAddress3;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAllClientEmailAddressAsStr", emailAddress);
        }

        return emailAddress;
    }


    private static EmailAddressManager c_instance;
}
