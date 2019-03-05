package dti.ci.credentialrequestmgr.impl;

import dti.ci.credentialrequestmgr.CredentialFeeAdjustManager;
import dti.ci.credentialrequestmgr.dao.CredentialFeeAdjustDAO;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Business Object to handle Credential Request Fee Adjustment.
 * <p/>
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:  03/04/2016
 *
 * @author jdingle
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
*/
public class CredentialFeeAdjustImpl implements CredentialFeeAdjustManager {

    /**
     * Load Service Charges for the account.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllServiceCharges(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllServiceCharges");
        }

        RecordSet rs = getCredentialFeeAdjustDAO().loadAllServiceCharges(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllServiceCharges", rs);
        }

        return rs;
    }

    /**
     * Process Reversal for selected Service Charges.
     *
     * @param inputRecordSet
     * @return int
     */
    public int saveAllServiceCharges(RecordSet inputRecordSet) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllServiceCharges");
        }

        int count = getCredentialFeeAdjustDAO().saveAllServiceCharges(inputRecordSet);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllServiceCharges", count);
        }



        return count;
    }

    /**
     * verifyConfig
     */
    public void verifyConfig() {
        if (getCredentialFeeAdjustDAO() == null)
            throw new ConfigurationException("The required property 'credentialFeeAdjustDAO' is missing.");
    }

    public CredentialFeeAdjustDAO getCredentialFeeAdjustDAO() {
        return credentialFeeAdjustDAO;
    }

    public void setCredentialFeeAdjustDAO(CredentialFeeAdjustDAO m_credentialFeeAdjustDAO) {
        this.credentialFeeAdjustDAO = m_credentialFeeAdjustDAO;
    }

    private CredentialFeeAdjustDAO credentialFeeAdjustDAO;
}
