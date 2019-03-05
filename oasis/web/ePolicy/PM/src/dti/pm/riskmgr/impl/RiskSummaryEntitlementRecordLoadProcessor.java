package dti.pm.riskmgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.riskmgr.RiskFields;

/**
 * This class extends the default record load processor to enforce entitlements for risk summary section on CIS policy summary page.
 * This class works in conjunction with pageEntitlements.xml configuration.
 * <p/>
 * <p>Any field that requires entitlement enforcement is determined by an indicator field that gets appended to the list
 * of fields in a record. This record is published as xml data island, which then gets intercepted by pageEntitlements
 * oasis tag to auto-generate javascript that enforces the entitlements.</p>
 * <p/>
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 20, 2008
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *  07/17/2014      jyang       154149 - Updated postProcessRecordSet(),replace isCopyAddrPhoneAvailable with string constant.
 * ---------------------------------------------------
 */
public class RiskSummaryEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {
    /**
     * Process the RecordSet for enforcing page entitlements after all records have been loaded and processed.
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        if (!getPolicyHeader().isWipB() && !getPolicyHeader().getPolicyStatus().isCancelled()) {
            recordSet.getSummaryRecord().setFieldValue(RiskFields.IS_COPY_ADDR_PHONE_AVAILABLE, YesNoFlag.Y);
        }
        else {
            recordSet.getSummaryRecord().setFieldValue(RiskFields.IS_COPY_ADDR_PHONE_AVAILABLE, YesNoFlag.N);
        }
    }

    public RiskSummaryEntitlementRecordLoadProcessor() {
    }

    public RiskSummaryEntitlementRecordLoadProcessor(PolicyHeader policyHeader) {
        setPolicyHeader(policyHeader);
    }

    public PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    public void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }

    private PolicyHeader m_policyHeader;
}
