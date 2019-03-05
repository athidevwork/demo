package dti.ci.policymgr.impl;

import dti.ci.policymgr.CIPolicyManager;
import dti.ci.policymgr.dao.CIPolicyDAO;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This bussiness component contains all logics related with policy in eCIS.
 * <p/>
 * <p>(C) 2009 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 04, 2009
 *
 * @author Bhong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class CIPolicyManagerImpl implements CIPolicyManager {
    /**
     * Load all locked policies
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllLockedPolicy(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllLockedPolicy", new Object[]{inputRecord});
        }

        RecordSet rs = getPolicyDAO().loadAllLockedPolicy(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllLockedPolicy", rs);
        }
        return rs;
    }

    /**
     * verifyConfig
     */
    public void verifyConfig() {
        if (getPolicyDAO() == null) {
            throw new ConfigurationException("The required property 'policyDAO' is missing.");
        }
    }

    public CIPolicyDAO getPolicyDAO() {
        return m_policyDAO;
    }

    public void setPolicyDAO(CIPolicyDAO policyDAO) {
        m_policyDAO = policyDAO;
    }

    private CIPolicyDAO m_policyDAO;
}
