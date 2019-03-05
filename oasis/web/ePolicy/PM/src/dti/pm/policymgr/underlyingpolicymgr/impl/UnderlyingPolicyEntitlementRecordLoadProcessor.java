package dti.pm.policymgr.underlyingpolicymgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.busobjs.SysParmIds;
import dti.pm.entitlementmgr.EntitlementFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.underlyingpolicymgr.UnderlyingPolicyFields;

import java.util.List;
import java.util.ArrayList;

/**
 * This class extends the default record load processor to enforce entitlements for the underlying policy web page.
 * This class works in conjunction with pageEntitlements.xml configuration.
 * <p/>
 * <p>Any field that requires entitlement enforcement is determined by an indicator field that gets appended to the list
 * of fields in a record. This record is published as xml data island, which then gets intercepted by pageEntitlements
 * oasis tag to auto-generate javascript that enforces the entitlements.</p>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 3, 2008
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *  06/25/2012      sxm         134889 - Set screen to readonly in prior term during renewal WIP
 *  01/02/2014      jyang       148771 - Disable retroactiveDate when companyInsuredB is 'Y' or policyFormCode
 *                                       is 'OCCURRENCE'.Modified setInitialEntitlementValues process. Add
 *                                       isRetroDateAvailable indicator to page field name list.
 *  12/14/2015      tzeng       165794 - 1)Set the Copy button show when page is not read only.
 *                                       2)Set Policy No field read only when record is from official.
 *                                       3)Set page read only when transaction in cancel, endquote, official.
 *  02/04/2016      tzeng       169217 - Modified postProcessRecord to make the closing official record also should be
 *                                       displayed on the underlying page if the closing record is not in the last
 *                                       transaction view mode.
 *  08/25/2016      ssheng      178365 - Set Coverage and effective from date field read only
 *                                       when record is from official.
 * ---------------------------------------------------
 */
public class UnderlyingPolicyEntitlementRecordLoadProcessor implements RecordLoadProcessor {
    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     * @return true if this Record should be added to the RecordSet;
     *         false if this Record should be excluded from the RecordSet.
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        EntitlementFields.setIsRowEligibleForDelete(record, YesNoFlag.Y);
        // show copy button
        record.setFieldValue(IS_COPY_SHOW, YesNoFlag.Y);
        if (isRecordSetReadOnly()) {
            EntitlementFields.setIsRowEligibleForDelete(record, YesNoFlag.N);
            record.setFieldValue(IS_COPY_SHOW, YesNoFlag.N);
        } else if ((record.hasStringValue(UnderlyingPolicyFields.RECORD_MODE_CODE) &&
            !RecordMode.TEMP.getName().equals(UnderlyingPolicyFields.getRecordModeCode(record)))) {
            EntitlementFields.setIsRowEligibleForDelete(record, YesNoFlag.N);
        }
        YesNoFlag companyInsuredB = UnderlyingPolicyFields.getCompanyInsuredB(record);
        UnderlyingPolicyFields.setEffectiveFromDateReadOnlyB(record, companyInsuredB);
        YesNoFlag isRetroDateAvailable = YesNoFlag.N;
        // Check editable of the retroactive date
        if (!companyInsuredB.booleanValue()) {
            if (!record.hasStringValue(UnderlyingPolicyFields.POLICY_FORM_CODE) || UnderlyingPolicyFields.getPolicyFormCode(record).equals("CM")) {
                isRetroDateAvailable = YesNoFlag.Y;
            }
        }
        record.setFieldValue(IS_RETRO_DATE_AVAILABLE, isRetroDateAvailable);

        // set policy no editable/readonly
        if (record.hasStringValue(UnderlyingPolicyFields.RECORD_MODE_CODE) &&
            (!RecordMode.TEMP.getName().equals(UnderlyingPolicyFields.getRecordModeCode(record)) ||
                record.hasStringValue(UnderlyingPolicyFields.OFFICIAL_RECORD_ID)) &&
            YesNoFlag.Y.getName().equals(SysParmProvider.getInstance().getSysParm(SysParmIds.PM_UNDERLYING_TERM, YesNoFlag.N.getName()))) {
            // Set Delete option invisible, if screen mode is ViewPolicy
            UnderlyingPolicyFields.setPolicyNoReadOnlyB(record, YesNoFlag.Y);
            UnderlyingPolicyFields.setCovPartCoverageCodeReadOnlyB(record, YesNoFlag.Y);
            UnderlyingPolicyFields.setEffectiveFromDateReadOnlyB(record, YesNoFlag.Y);
        };

        // set the closed record invisible
        if (record.hasStringValue(UnderlyingPolicyFields.CLOSING_TRANS_LOG_ID) &&
            StringUtils.isSame(getPolicyHeader().getLastTransactionId(), record.getStringValue(UnderlyingPolicyFields.CLOSING_TRANS_LOG_ID))) {
            record.setDisplayIndicator(YesNoFlag.N);
        }
        else {
            record.setDisplayIndicator(YesNoFlag.Y);
        }


        return true;
    }

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        Record summaryRecord = recordSet.getSummaryRecord();

        // Set the RecordSet as readonly
        EntitlementFields.setReadOnly(summaryRecord, isRecordSetReadOnly());

        if(recordSet.getSize()==0){
            List fieldNameList = new ArrayList();
            fieldNameList.add(EntitlementFields.IS_ROW_ELIGIBLE_FOR_DELETE);
            fieldNameList.add(IS_RETRO_DATE_AVAILABLE);
            fieldNameList.add(IS_COPY_SHOW);
            recordSet.addFieldNameCollection(fieldNameList);
        }
    }

    /**
     * Check if current recordSet is readOnly
     *
     * @return boolean
     */
    private boolean isRecordSetReadOnly() {
        boolean isReadOnly = true;
        ScreenModeCode screenMode = getPolicyHeader().getScreenModeCode();
        if (screenMode.isManualEntry() ||
            screenMode.isRenewWIP() ||
            screenMode.isResinstateWIP()||
            screenMode.isOosWIP()||
            screenMode.isWIP()) {
            isReadOnly = false;
        }
        return isReadOnly;
    }

    /**
     * Set initialEntitlementValues for UnderLyingPolicy
     *
     * @param policyHeader
     * @param record
     */
    public static void setInitialEntitlementValuesForUnderlyingPolicy(PolicyHeader policyHeader,
                                                                      Record record) {
        UnderlyingPolicyEntitlementRecordLoadProcessor entitlementRLP = new UnderlyingPolicyEntitlementRecordLoadProcessor(policyHeader);
        //set page entitlement values
        entitlementRLP.postProcessRecord(record, true);
    }

    public UnderlyingPolicyEntitlementRecordLoadProcessor(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }

    public PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    public void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }

    private PolicyHeader m_policyHeader;
    private static final String IS_RETRO_DATE_AVAILABLE = "isRetroDateAvailable";
    private static final String IS_COPY_SHOW = "isCopyShow";
}
