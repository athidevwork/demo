package dti.pm.policymgr.creditstackmgr.impl;

import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.policymgr.creditstackmgr.CreditStackingFields;
import dti.pm.policymgr.creditstackmgr.CreditStackingManager;
import dti.pm.policymgr.creditstackmgr.dao.CreditStackingDAO;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
  * This class provides the implementation details for CreditStackingManager.
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   May 26, 2011
 *
 * @author syang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class CreditStackingManagerImpl implements CreditStackingManager {

    /**
     * Retrieve header information.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllHeaderInformation(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllHeaderInformation", new Object[]{inputRecord});
        }
        // Validate search criteria
        validateSearchCriteria(inputRecord);
        RecordSet rs = getCreditStackingDAO().loadAllHeaderInformation(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllHeaderInformation", rs);
        }
        return rs;
    }

    /**
     * Retrieve applied information.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllAppliedInformation(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAppliedInformation", new Object[]{inputRecord});
        }
        
        RecordLoadProcessor rlp = new CreditStackingEntitlementRecordLoadProcessor();
        RecordSet rs = getCreditStackingDAO().loadAllAppliedInformation(inputRecord, rlp);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAppliedInformation", rs);
        }
        return rs;
    }

    /**
     * Validate the search criteria, the risk and coverage can't be empty.
     *
     * @param inputRecord
     */
    protected void validateSearchCriteria(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateSearchCriteria", new Object[]{inputRecord});
        }
        if (!inputRecord.hasStringValue(CreditStackingFields.RISK_ID) || !inputRecord.hasStringValue(CreditStackingFields.COVG_ID)) {
            MessageManager.getInstance().addErrorMessage("pm.creditStacking.search.empty");
            throw new ValidationException("Invalid search criteria data.");
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateSearchCriteria");
        }
    }

    public CreditStackingDAO getCreditStackingDAO() {
        return m_creditStackingDAO;
    }

    public void setCreditStackingDAO(CreditStackingDAO creditStackingDAO) {
        m_creditStackingDAO = creditStackingDAO;
    }

    private CreditStackingDAO m_creditStackingDAO;
}
