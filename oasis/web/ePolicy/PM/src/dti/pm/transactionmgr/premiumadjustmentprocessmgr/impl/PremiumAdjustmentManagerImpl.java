package dti.pm.transactionmgr.premiumadjustmentprocessmgr.impl;

import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.error.ValidationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.transactionmgr.premiumadjustmentprocessmgr.PremiumAdjustmentManager;
import dti.pm.transactionmgr.premiumadjustmentprocessmgr.dao.PremiumAdjustmentDAO;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Class provides the implementation details of PremiumAdjustmentManager Interface.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   July 9, 2008
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/11/2018       tzeng       192295 - Updated saveAllPremiumAdjustment to correct the mistyping.
 * ---------------------------------------------------
 */

public class PremiumAdjustmentManagerImpl implements PremiumAdjustmentManager {

    /**
     * load all shared group info by policy info
     *
     * @param policyHeader
     * @return RecordSet a recordSet of shared group
     */

    public RecordSet loadAllCoverage(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllCoverage", new Object[]{policyHeader});
        }
        Record inputRecord = policyHeader.toRecord();
        RecordSet rs;
        rs = getPremiumAdjustmentDAO().loadAllCoverage(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllCoverage", rs);
        }
        return rs;
    }

    /**
     * load all premium adjustment info
     *
     * @param policyHeader
     * @param coverages    recordset
     * @return RecordSet a recordSet of shared detail
     */
    public RecordSet loadAllPremiumAdjustment(PolicyHeader policyHeader, RecordSet coverages) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllPremiumAdjustment", new Object[]{policyHeader});
        }
        // Build the input record
        Record inputRecord = policyHeader.toRecord();
        RecordSet rs;
        rs = getPremiumAdjustmentDAO().loadAllPremiumAdjustment(inputRecord, new PremiumAdjustmentEntitlementRecordLoadProcessor(policyHeader, coverages));
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllPremiumAdjustment", rs);
        }
        return rs;
    }


    /**
     * save all premium adjustment data
     *
     * @param policyHeader
     * @param inputRecords
     * @return updateCount
     */
    public void saveAllPremiumAdjustment(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllPremiumAdjustment", new Object[]{inputRecords});             //get recordSet need to be validate(exclude the deleted record)
        RecordSet updatedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));
        Iterator itor = updatedRecords.getRecords();
        while (itor.hasNext()) {
            Record record = (Record) itor.next();
            record.setFieldValue("transactionLogId", policyHeader.getLastTransactionId());
            record.setFieldValue("termEffectiveFromDate", policyHeader.getTermEffectiveFromDate());
            record.setFieldValue("termEffectiveToDate", policyHeader.getTermEffectiveToDate());
            String componentValue = record.getStringValue("componentValue");
            if ((!StringUtils.isBlank(componentValue)) && (!"0".equals(componentValue))) {
                String returnCode = getPremiumAdjustmentDAO().saveAllPremiumAdjustment(record);
                if (!("1".equals(returnCode))) {
                    throw new ValidationException("Save Premium adjustment failed. Please contract your administrator");
                }
            }
        }
        l.exiting(getClass().getName(), "saveAllPremiumAdjustment");
    }

//-------------------------------------------------
// Configuration constructor and accessor methods
//-------------------------------------------------

    public void verifyConfig() {
        if (getPremiumAdjustmentDAO() == null)
            throw new ConfigurationException("The required property 'premiumAdjustmentDAO' is missing.");
    }

    public PremiumAdjustmentManagerImpl() {
    }

    public PremiumAdjustmentDAO getPremiumAdjustmentDAO() {
        return m_premiumAdjustmentDAO;
    }

    public void setPremiumAdjustmentDAO(PremiumAdjustmentDAO premiumAdjustmentDAO) {
        m_premiumAdjustmentDAO = premiumAdjustmentDAO;
    }

    private PremiumAdjustmentDAO m_premiumAdjustmentDAO;


}
