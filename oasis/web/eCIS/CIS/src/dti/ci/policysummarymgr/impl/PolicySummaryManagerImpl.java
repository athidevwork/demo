package dti.ci.policysummarymgr.impl;

import dti.ci.policysummarymgr.PolicySummaryManager;
import dti.ci.policysummarymgr.dao.PolicySummaryDAO;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import java.util.logging.Logger;
import java.util.logging.Level;


/**
 * Business Object for Policy Summary
 * <p/>
 * <p>(C) 2013 Delphi Technology, inc. (dti)</p>
 * Date:   12/20/2013
 *
 * @author hxk
 */

/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
*/
public class PolicySummaryManagerImpl implements PolicySummaryManager {
    private final Logger l = LogUtils.getLogger(getClass());
    
    /**
     * load additional info data.
     * @param record
     * @return
     */
    @Override
    public Record loadAddlInfo(Record record) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAddlInfo", new Object[]{record});
        }
        Record addlInfoRec =  getPolicySummaryDAO().loadAddlInfo(record);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAddlInfo", addlInfoRec);
        }
        return addlInfoRec;
    }

    public void verifyConfig() {
        if (getPolicySummaryDAO() == null)
            throw new ConfigurationException("The required property 'policySummaryDAO' is missing.");
    }

    public PolicySummaryDAO getPolicySummaryDAO() {
        return policySummaryDAO;
    }

    public void setPolicySummaryDAO(PolicySummaryDAO policySummaryDAO) {
        this.policySummaryDAO = policySummaryDAO;
    }

    private PolicySummaryDAO policySummaryDAO;
}
