package dti.pm.riskmgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.security.Authenticator;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.SysParmProvider;
import dti.pm.busobjs.PolicyViewMode;
import dti.pm.policymgr.PolicyHeader;

/**
 * This class extends the default record load processor to enforce entitlements for risk web page. This class works in
 * conjunction with pageEntitlements.xml configuration.
 * <p/>
 * <p>Any field that requires entitlement enforcement is determined by an indicator field that gets appended to the list
 * of fields in a record. This record is published as xml data island, which then gets intercepted by pageEntitlements
 * oasis tag to auto-generate javascript that enforces the entitlements.</p>
 * <p/>
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 03, 2010
 *
 * @author syang
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/26/2013       fcb         145725 - Added logic to check the profile in UserCacheManager via OasisUser.
 * ---------------------------------------------------
 */
public class RiskSurchargePointsEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {

    /**
     * Process the RecordSet for enforcing page entitlements after all records have been loaded and processed.
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        // If there is a WIP transaction and the policy view mode is not OFFICIAL, then the Save option is available.
        YesNoFlag isSaveAvailable = YesNoFlag.N;
        YesNoFlag isOverrideTierAvailable = YesNoFlag.N;
        String sysParameter = SysParmProvider.getInstance().getSysParm("PM_CUST_SURCG_POINTS", "N");
        if (YesNoFlag.getInstance(sysParameter).booleanValue()) {
            PolicyViewMode viewMode = getPolicyHeader().getPolicyIdentifier().getPolicyViewMode();
            if (recordSet.getSize() > 0 && getPolicyHeader().isWipB() && !viewMode.isOfficial()) {
                isSaveAvailable = YesNoFlag.Y;
                // The user should has the profile "TR_EXCLUDE" to edit Override Tier and Exclude fields.
                if ( UserSessionManager.getInstance().getUserSession().getOasisUser().hasProfile("TR_EXCLUDE")) {
                    isOverrideTierAvailable = YesNoFlag.Y;
                }
            }
        }
        recordSet.getSummaryRecord().setFieldValue("isSaveAvailable", isSaveAvailable);
        recordSet.getSummaryRecord().setFieldValue("isOverrideTierAvailable", isOverrideTierAvailable);
    }

    public RiskSurchargePointsEntitlementRecordLoadProcessor(PolicyHeader policyHeader) {
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