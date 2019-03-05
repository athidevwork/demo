package dti.pm.coveragemgr.minitailmgr.impl;

import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.busobjs.YesNoFlag;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.PMStatusCode;
import dti.pm.coveragemgr.CoverageFields;

import java.util.logging.Logger;
import java.util.ArrayList;

/**
 * This class is a mini tail load processor.See "load process pattern for more info"
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 27, 2007
 *
 * @author zlzhu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * Jul 27, 2007     zlzhu       Created
 * 04/29/2011       gxc         105791 - Added CONVERTED related logic.
 * ---------------------------------------------------
 */

public class RiskCoverageEntitlementRecordLoadProcessor implements RecordLoadProcessor {
    /**
     * do some process after loading data
     *
     * @param record             input record
     * @param rowIsOnCurrentPage true if this record is on the current page
     * @return true if this Record should be added to the RecordSet;
     *         false if this Record should be excluded from the RecordSet.
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecord", new Object[]{record, String.valueOf(rowIsOnCurrentPage)});
        //if process riskCoverageList
        setRecordModeCode(record);
        setEditableFlag(record);
        l.exiting(getClass().getName(), "postProcessRecord");
        return true;
    }

    /**
     * do some process on whole record set
     *
     * @param recordSet input record set
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecordSet", new Object[]{recordSet});
        // If no record is fetched, add the page entitlement fields to the recordset field collection
        if (recordSet.getSize() == 0) {
            ArrayList pageEntitlementFields = new ArrayList();
            pageEntitlementFields.add("isApplyEditable");
            pageEntitlementFields.add("isBasisEditable");
            recordSet.addFieldNameCollection(pageEntitlementFields);
        }
        l.exiting(getClass().getName(), "postProcessRecordSet");
    }

    /**
     * set editable flag(to YesNoFlag)
     *
     * @param record input record
     */
    private void setEditableFlag(Record record) {
        YesNoFlag isEditable = isEditable(record);
        record.setFieldValue("isApplyEditable", isEditable);
        record.setFieldValue("isBasisEditable", isEditable);
    }

    /**
     * set record mode field according to the business rule
     *
     * @param record input record
     */
    private void setRecordModeCode(Record record) {
        ScreenModeCode smc = m_policyHeader.getScreenModeCode();
        if (smc.isViewPolicy() || smc.isViewEndquote()) {
            record.setFieldValue("recordModeCode", RecordMode.OFFICIAL);
        }
        else {
            record.setFieldValue("recordModeCode", RecordMode.TEMP);
        }
    }

    /**
     * judge if a record is obey rule1.1 and rule1.2,if all of them are met,return Y
     *
     * @param riskCoverageRecord input risk coverage record
     * @return if all of the rules are met,return Y,else return false
     * @see #isWipInProgress
     * @see #isActiveOrPending
     */
    private YesNoFlag isEditable(Record riskCoverageRecord) {
        if (isWipInProgress() && isActiveOrPending(riskCoverageRecord))
            return YesNoFlag.Y;
        else
            return YesNoFlag.N;
    }

    /**
     * if it's obey rule2
     * Rule12:
     * the status of the coverage from the top grid is either ACTIVE or PENDING,if one of these is not met
     * all the field is readonly
     *
     * @param riskCoverageRecord input risk coverage record
     * @return if rule2 is met,return true
     */
    private boolean isActiveOrPending(Record riskCoverageRecord) {
        Logger l = LogUtils.enterLog(getClass(), "isActiveOrPending", new Object[]{riskCoverageRecord});
        boolean flag = false;
        PMStatusCode coverageStatus = CoverageFields.getCoverageStatus(riskCoverageRecord);
        l.info("coverageStatus: " + coverageStatus);
        if (coverageStatus.isActive() || coverageStatus.isPending() || coverageStatus.isConverted()) {
            flag = true;
        }
        l.exiting(getClass().getName(), "isActiveOrPending",new Object[]{Boolean.valueOf(flag)});
        return flag;
    }

    /**
     * Rule11:
     * First, system checks that a transactional WIP is inprogress (Endorsement, OOS Endorsement, Renewal, or MANUAL_ENTRY
     * - see Policy Folder UI document).
     * If one of these is not met, return false.
     *
     * @return true if obey the rule(editable)
     */
    private boolean isWipInProgress() {
        Logger l = LogUtils.enterLog(getClass(), "isWipInProgress", new Object[]{"rule11====="});
        boolean flag = false;
        ScreenModeCode smc = m_policyHeader.getScreenModeCode();
        if (smc.isWIP() || smc.isOosWIP() || smc.isRenewWIP() || smc.isManualEntry())
        {
            flag = true;
        }
        l.exiting(getClass().getName(), "isWipInProgress",new Object[]{Boolean.valueOf(flag)});
        return flag;
    }

    /**
     * @param policyHeader policy header
     */
    public RiskCoverageEntitlementRecordLoadProcessor(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }

    private static final String ACTIVE = "ACTIVE";
    private static final String PENDING = "PENDING";
    private PolicyHeader m_policyHeader;
}
