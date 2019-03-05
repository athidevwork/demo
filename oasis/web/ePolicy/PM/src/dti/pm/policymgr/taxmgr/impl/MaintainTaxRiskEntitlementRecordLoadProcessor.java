package dti.pm.policymgr.taxmgr.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.busobjs.PMStatusCode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.taxmgr.TaxFields;

/**
 * This class extends the default record load processor to enforce entitlements for maintain tax web page. This class works in
 * conjunction with pageEntitlements.xml configuration.
 * <p/>
 * <p>Any field that requires entitlement enforcement is determined by an indicator field that gets appended to the list
 * of fields in a record. This record is published to request as request attributes, which then gets intercepted by
 * pageEntitlements oasis tag to auto-generate javascript that enforces the entitlements.</p>
 * <p/>
 * <p>(C) 2014 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 21, 2014
 *
 * @author wdang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/21/2014       wdang       158112 - Initial Version
 * 12/04/2014       wdang       159491 - Replace BaseRiskStatus with RiskStatus.
 * ---------------------------------------------------
 */
public class MaintainTaxRiskEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {

    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        record.setFieldValue(TaxFields.IS_ADD_VISIBLE, isAddVisible(record));
        return true;
    }

    public void postProcessRecordSet(RecordSet recordSet) {
        if (recordSet.getSize() == 0) {
            List<String> pageEntitlementFields = new ArrayList<String>();
            pageEntitlementFields.add(TaxFields.IS_ADD_VISIBLE);
            recordSet.addFieldNameCollection(pageEntitlementFields);
        }

        ScreenModeCode screenModeCode = getPolicyHeader().getScreenModeCode();
        if (screenModeCode.isViewPolicy()
            || screenModeCode.isViewEndquote()
            || screenModeCode.isCancelWIP()
            || screenModeCode.isResinstateWIP()) {
            recordSet.setFieldValueOnAll(TaxFields.IS_ADD_VISIBLE, YesNoFlag.N);
        }
    }

    public static void getInitialValuesForAddTax(Record record){
        record.setFieldValue(TaxFields.IS_ADD_VISIBLE, YesNoFlag.Y);
    }

    /**
     * Return Add button is shown/hidden for each record
     *
     * @return
     */
    private YesNoFlag isAddVisible(Record record) {
        Logger l = LogUtils.enterLog(getClass(), "isAddVisible");
        YesNoFlag isVisible = YesNoFlag.N;

        if (PMStatusCode.ACTIVE.getName().equals(TaxFields.getRiskStatus(record))
            || PMStatusCode.PENDING.getName().equals(TaxFields.getRiskStatus(record))) {
            isVisible = YesNoFlag.Y;
        }
        else {
            isVisible = YesNoFlag.N;
        }

        l.exiting(getClass().getName(), "isAddAvailable", isVisible);
        return isVisible;
    }

    public MaintainTaxRiskEntitlementRecordLoadProcessor(PolicyHeader policyHeader){
        m_policyHeader = policyHeader;
    }

    public PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    public void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }

    private PolicyHeader m_policyHeader;
}
