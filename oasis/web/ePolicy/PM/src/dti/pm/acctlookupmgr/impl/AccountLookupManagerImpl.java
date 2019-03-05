package dti.pm.acctlookupmgr.impl;

import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.busobjs.YesNoFlag;
import dti.pm.acctlookupmgr.AccountLookupManager;
import dti.pm.acctlookupmgr.dao.AccountLookupDAO;

/**
 * This class provides the implementation details for Account Lookup Manager.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 14, 2007
 *
 * @author sxm
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class AccountLookupManagerImpl implements AccountLookupManager {

    /**
     * Method that returns a list of billing accounts.
     * <p/>
     *
     * @param inputRecord Record contains input values
     * @return RecordSet containing the billing accounts based on the input criteria
     */
    public RecordSet loadAllAccount(Record inputRecord) {
        RecordSet rs = getAccountLookupDAO().loadAllAccount(inputRecord);
        YesNoFlag isSelectAvailable = YesNoFlag.Y;
        if (rs.getSize() == 0) {
            MessageManager.getInstance().addErrorMessage("pm.accountlookup.noaccountfound");
            // change to N for isSelectAvailable
            isSelectAvailable = YesNoFlag.N;
        }

        // create the pageEntitlement field with value
        rs.getSummaryRecord().setFieldValue("isSelectAvailable", isSelectAvailable);
        
        return rs;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getAccountLookupDAO() == null)
            throw new ConfigurationException("The required property 'accountLookupDAO' is missing.");
    }

    public AccountLookupDAO getAccountLookupDAO() {
        return m_accountLookupDAO;
    }

    public void setAccountLookupDAO(AccountLookupDAO accountLookupDAO) {
        m_accountLookupDAO = accountLookupDAO;
    }

    private AccountLookupDAO m_accountLookupDAO;
}
