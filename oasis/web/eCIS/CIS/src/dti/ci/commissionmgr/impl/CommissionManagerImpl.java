package dti.ci.commissionmgr.impl;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.app.ConfigurationException;
import dti.oasis.util.StringUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.error.ValidationException;
import dti.ci.commissionmgr.CommissionManager;
import dti.ci.commissionmgr.dao.CommissionDAO;

import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 23, 2007
 *
 * @author gjlong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/07/2008       yhyang      Issue#86934 Move CIS Agent to eCIS.
 * ---------------------------------------------------
 */
public class CommissionManagerImpl implements CommissionManager {

    private static String LOOKUP_COMMISSION_ACTION_CLASS_NAME = "dti.ci.commissionmgr.struts.LookupCommissionAction";

    /**
     * method to load all commission rate Bracket for a given commRateSchedId
     *
     * @param inputRecord a record containing a commRateSchedId field
     * @return recordset
     */
    public RecordSet loadAllCommissionBracket(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllCommissionBracket", new Object[]{inputRecord});
        RecordSet rs = null;
        // follow the pattern to get the default values configured from the web bench,
        // althought this is not used currently for this uc
        Record defaultValuesRecord = getWorkbenchConfiguration().getDefaultValues(LOOKUP_COMMISSION_ACTION_CLASS_NAME);
        inputRecord.setFields(defaultValuesRecord);

        rs = getCommissionDAO().loadAllCommissionBracket(inputRecord);
        l.exiting(getClass().toString(), "loadAllCommissionBracket");
        return rs;
    }

    public void verifyConfig() {
        if (getCommissionDAO() == null)
            throw new ConfigurationException("The required property 'commissionDAO' is missing.");

        if (getWorkbenchConfiguration() == null)
            throw new ConfigurationException("The required property 'workbenchConfiguration' is missing.");
    }

    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return m_workbenchConfiguration;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration) {
        m_workbenchConfiguration = workbenchConfiguration;
    }

    public CommissionDAO getCommissionDAO() {
        return m_commissionDAO;
    }

    public void setCommissionDAO(CommissionDAO commissionDAO) {
        m_commissionDAO = commissionDAO;
    }

    private WorkbenchConfiguration m_workbenchConfiguration;
    private CommissionDAO m_commissionDAO;
}
