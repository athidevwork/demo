package dti.ci.billingmgr.impl;

import dti.ci.billingmgr.CIBillingManager;
import dti.ci.billingmgr.dao.CIBillingDAO;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.error.ValidationException;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Implementation of CIBillingManager interface 
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 27, 2009
 *
 * @author yjmiao
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public class CIBillingManagerImpl implements CIBillingManager {
    /**
     * To load all accounts by given entityId.
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllAccount(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAccount", new Object[]{inputRecord});
        }

        RecordSet rs = getCiBillingDAO().loadAllAccount(inputRecord);

        if(rs.getSize() == 0){
            MessageManager.getInstance().addWarningMessage("ci.fm.billing.noAccounts.found.error");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAccount", rs);
        }
        return rs;
    }

    public CIBillingDAO getCiBillingDAO() {
        return m_ciBillingDAO;
    }

    public void setCiBillingDAO(CIBillingDAO ciBillingDAO) {
        this.m_ciBillingDAO = ciBillingDAO;
    }

    public CIBillingManagerImpl(){
    }

    public void verifyConfig() {
        if (getCiBillingDAO() == null)
            throw new ConfigurationException("The required property 'ciBillingDAO' is missing.");
    }

    private CIBillingDAO m_ciBillingDAO;
}
