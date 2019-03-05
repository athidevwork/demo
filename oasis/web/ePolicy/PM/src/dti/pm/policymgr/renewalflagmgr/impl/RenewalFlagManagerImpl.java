package dti.pm.policymgr.renewalflagmgr.impl;

import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordLoadProcessorChainManager;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.util.LogUtils;
import dti.pm.busobjs.PMRecordSetHelper;
import dti.pm.busobjs.RecordMode;
import dti.pm.core.data.FilterOfficialRowForEndquoteRecordLoadProcessor;
import dti.pm.policymgr.PolicyFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.policymgr.renewalflagmgr.RenewalFlagFields;
import dti.pm.policymgr.renewalflagmgr.RenewalFlagManager;
import dti.pm.policymgr.renewalflagmgr.dao.RenewalFlagDAO;
import dti.pm.policymgr.renewalflagmgr.struts.MaintainRenewalFlagAction;
import dti.pm.policymgr.service.PolicyInquiryFields;
import dti.pm.transactionmgr.TransactionFields;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   2/16/2016
 *
 * @author tzeng
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 02/24/2016       tzeng       167532 - Initial version.
 * ---------------------------------------------------
 */

public class RenewalFlagManagerImpl implements RenewalFlagManager{

    @Override
    public RecordSet loadAllRenewalFlag(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRenewalFlag", new Object[]{policyHeader});
        }

        Record inputRecord = policyHeader.toRecord();
        RecordMode recordModeCode = policyHeader.getRecordMode();
        inputRecord.setFieldValue(RenewalFlagFields.RECORD_MODE_CODE, recordModeCode.getName());

        // record load processor
        RecordLoadProcessor lp = new RenewalFlagEntitlementRecordLoadProcessor(policyHeader);
        lp = RecordLoadProcessorChainManager.getRecordLoadProcessor(lp,
            new FilterOfficialRowForEndquoteRecordLoadProcessor(policyHeader, RenewalFlagFields.POLICY_RENEWAL_FLAG_ID));
        RecordSet rs = getRenewalFlagDAO().loadAllRenewalFlag(inputRecord, lp);

        //set auditHistory href
        rs.setFieldValueOnAll("auditHistory", "View");

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRenewalFlag", rs);
        }
        return rs;
    }

    @Override
    public Record getInitialValuesForAddRenewalFlag(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForAddRenewalFlag");
        }
        //Get default record from workbench.
        Record output = getWorkbenchConfiguration().getDefaultValues(MaintainRenewalFlagAction.class.getName());

        //Get the initial entitlement values
        output.setFields(RenewalFlagEntitlementRecordLoadProcessor.getInitialEntitlementValuesForRenewalFlag());

        PolicyFields.setPolicyId(output, policyHeader.getPolicyId());
        RenewalFlagFields.setEffectiveFromDate(output, policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        RenewalFlagFields.setEffectiveToDate(output, policyHeader.getTermEffectiveToDate());
        PolicyHeaderFields.setTermBaseRecordId(output, policyHeader.getPolicyIdentifier().getTermBaseRecordId());
        RenewalFlagFields.setRiskBaseRecordId(output, "");

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForAddRenewalFlag");
        }
        return output;
    }

    @Override
    public void saveAllRenewalFlag(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllRenewalFlag", new Object[]{inputRecords});
        }

        // input policyHeader parameter
        inputRecords.setFieldValueOnAll(TransactionFields.TRANSACTION_LOG_ID, policyHeader.getLastTransactionId(), true);

        // set changed records data
        RecordSet changedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));
        PMRecordSetHelper.setRowStatusOnModifiedRecords(changedRecords);

        RecordSet deletedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.DELETED));
        int processCount;

        // insert/update
        if(changedRecords.getSize() > 0){
            processCount = getRenewalFlagDAO().saveAllRenewalFlag(changedRecords);
            if (processCount == 0) {
                l.logp(Level.WARNING, getClass().getName(), "saveAllRenewalFlag", "Save inserted/updated failed.");
                return;
            }
        }

        // delete
        if(deletedRecords.getSize() > 0){
            processCount = getRenewalFlagDAO().deleteAllRenewalFlag(deletedRecords);
            if (processCount == 0) {
                l.logp(Level.WARNING, getClass().getName(), "deleteAllRenewalFlag", "Delete failed.");
                return;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllRenewalFlag");
        }
    }

    public RenewalFlagDAO getRenewalFlagDAO() {
        return m_renewalFlagDAO;
    }

    public void setRenewalFlagDAO(RenewalFlagDAO renewalFlagDAO) {
        this.m_renewalFlagDAO = renewalFlagDAO;
    }

    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return m_workbenchConfiguration;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration) {
        m_workbenchConfiguration = workbenchConfiguration;
    }

    private RenewalFlagDAO m_renewalFlagDAO;
    private WorkbenchConfiguration m_workbenchConfiguration;
}
