package dti.pm.riskmgr.nationalprogrammgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.riskmgr.nationalprogrammgr.NationalProgramFields;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class extends the default record load processor to enforce entitlements for Maintain National Program web page.
 * This class works in conjunction with pageEntitlements.xml configuration.
 * <p/>
 * <p>Any field that requires entitlement enforcement is determined by an indicator field that gets appended to the list
 * of fields in a record. This record is published as xml data island, which then gets intercepted by pageEntitlements
 * oasis tag to auto-generate java script that enforces the entitlements.</p>
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   May 25, 2011
 *
 * @author Dzhang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/07/2017       lzhang      182769 - Invisible ADD and SAVE button when transaction date
 *                                       is not located in coverage period
 * ---------------------------------------------------
 */
public class NationalProgramEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {
    /**
     * Process the given record after it's been loaded to enforce page entitlements.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecord",
            new Object[]{record, String.valueOf(rowIsOnCurrentPage)});

        String transDateStr = getPolicyHeader().getLastTransactionInfo().getTransEffectiveFromDate();
        String contigRiskEffectiveDateStr = getPolicyHeader().getRiskHeader().getContigRiskEffectiveDate();
        String contigRiskExpireDateStr = getPolicyHeader().getRiskHeader().getContigRiskExpireDate();
        if (isScreenInEditableMode(transDateStr, contigRiskEffectiveDateStr, contigRiskExpireDateStr)) {
            record.setFieldValue(IS_DELETE_AVAILABLE, isDeleteAvailable(record));
            record.setFieldValue(IS_NATIONAL_PROGRAM_EDITABLE, isNationalProgramEditable(record));
            record.setFieldValue(IS_RECORD_EDITABLE, isRecordEditable(record));
            String closingTransLogId = NationalProgramFields.getClosingTransLogId(record);

            if ((!StringUtils.isBlank(closingTransLogId)) && closingTransLogId.equals(getPolicyHeader().getLastTransactionId())) {
                record.setDisplayIndicator("N");
            }
        }
        else {
            ScreenModeCode modeCode = getPolicyHeader().getScreenModeCode();
            if (modeCode.isCancelWIP() || modeCode.isResinstateWIP() ||
                (modeCode.isManualEntry() && getPolicyHeader().getLastTransactionInfo().getTransactionCode().isReissue())) {
                String closingTransLogId = NationalProgramFields.getClosingTransLogId(record);
                // Not display the closed record.
                if ((!StringUtils.isBlank(closingTransLogId)) && closingTransLogId.equals(getPolicyHeader().getLastTransactionId())) {
                    record.setDisplayIndicator("N");
                }
            }
            record.setFieldValue(IS_DELETE_AVAILABLE, YesNoFlag.N);
            record.setFieldValue(IS_NATIONAL_PROGRAM_EDITABLE, YesNoFlag.N);
            record.setFieldValue(IS_RECORD_EDITABLE, YesNoFlag.N);
        }

        l.exiting(getClass().getName(), "postProcessRecord");
        return true;
    }

    /**
     * Process the RecordSet after all records have been loaded and processed.
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecordSet", new Object[]{recordSet});

        // If no record is fetched, add the page entitlement fields to the recordset field collection
        if (recordSet.getSize() == 0) {
            ArrayList pageEntitlementFields = new ArrayList();
            pageEntitlementFields.add(IS_DELETE_AVAILABLE);
            pageEntitlementFields.add(IS_SAVE_AVAILABLE);
            pageEntitlementFields.add(IS_ADD_AVAILABLE);
            pageEntitlementFields.add(IS_NATIONAL_PROGRAM_EDITABLE);
            pageEntitlementFields.add(IS_RECORD_EDITABLE);
            recordSet.addFieldNameCollection(pageEntitlementFields);
        }
        Record summaryRecord = recordSet.getSummaryRecord();
        String transDateStr = getPolicyHeader().getLastTransactionInfo().getTransEffectiveFromDate();
        String contigRiskEffectiveDateStr = getPolicyHeader().getRiskHeader().getContigRiskEffectiveDate();
        String contigRiskExpireDateStr = getPolicyHeader().getRiskHeader().getContigRiskExpireDate();
        boolean editable = isScreenInEditableMode(transDateStr, contigRiskEffectiveDateStr, contigRiskExpireDateStr);
        if (editable) {
            summaryRecord.setFieldValue(IS_SAVE_AVAILABLE, YesNoFlag.Y);
            summaryRecord.setFieldValue(IS_ADD_AVAILABLE, YesNoFlag.Y);
        }
        else {
            summaryRecord.setFieldValue(IS_SAVE_AVAILABLE, YesNoFlag.N);
            summaryRecord.setFieldValue(IS_ADD_AVAILABLE, YesNoFlag.N);
        }
        //EntitlementFields.setReadOnly(recordSet.getSummaryRecord(), !editable);
        l.exiting(getClass().getName(), "postProcessRecordSet");
    }

    /**
     * check if Delete option available for current selected row.
     *
     * @param record the current record
     * @return YesNoFlag
     */
    private YesNoFlag isDeleteAvailable(Record record) {
        YesNoFlag isAvailable = YesNoFlag.N;

        if (PMCommonFields.hasRecordModeCode(record)) {
            RecordMode recordModeCode = PMCommonFields.getRecordModeCode(record);
            if (recordModeCode.isTemp()) {
                isAvailable = YesNoFlag.Y;
            }
        }

        return isAvailable;
    }

    /**
     * check if Program field editable for current selected row.
     *
     * @param record the current record
     * @return YesNoFlag
     */
    private YesNoFlag isNationalProgramEditable(Record record) {
        YesNoFlag isEditable = YesNoFlag.N;

        if (PMCommonFields.hasRecordModeCode(record)) {
            RecordMode recordModeCode = PMCommonFields.getRecordModeCode(record);
            if (recordModeCode.isTemp() && StringUtils.isBlank(NationalProgramFields.getOfficialRecordId(record))) {
                isEditable = YesNoFlag.Y;
            }
        }

        return isEditable;
    }

    /**
     * check if record editable for current selected row.
     *
     * @param record the current record
     * @return YesNoFlag
     */
    private YesNoFlag isRecordEditable(Record record) {
        YesNoFlag isEditable = YesNoFlag.N;

        if (PMCommonFields.hasRecordModeCode(record)) {
            RecordMode recordModeCode = PMCommonFields.getRecordModeCode(record);
            if (recordModeCode.isTemp()) {
                isEditable = YesNoFlag.Y;
            }
            else if (recordModeCode.isOfficial()) {
                Date effDate = record.getDateValue(NationalProgramFields.EFFECTIVE_FROM_DATE);
                Date expDate = record.getDateValue(NationalProgramFields.EFFECTIVE_TO_DATE);
                Date transEffDate = DateUtils.parseDate(getPolicyHeader().getLastTransactionInfo().getTransEffectiveFromDate());
                if ((transEffDate.equals(effDate) || transEffDate.after(effDate)) && transEffDate.before(expDate)) {
                    isEditable = YesNoFlag.Y;
                }
            }
        }

        return isEditable;
    }

    /**
     * check if the screen in editable mode.
     *
     * @return true/false
     */
    private boolean isScreenInEditableMode(String transEffFromDate, String effFromDate, String effToDate) {
        boolean isEditable = false;
        ScreenModeCode screenModeCode = getPolicyHeader().getScreenModeCode();
        // The screen is editable during WIP of these transactions:New Business, Endorsement, Out of Sequence Endorsement,
        // Renewal, and Reissue.
        if (screenModeCode.isWIP() || screenModeCode.isOosWIP() || screenModeCode.isRenewWIP() || screenModeCode.isManualEntry()){
            if (!DateUtils.isTargetDateNotInDatesPeriod(transEffFromDate, effFromDate, effToDate)){
                isEditable = true;
            }else{
                isEditable = false;
            }
        }

        return isEditable;
    }

    /**
     * Set initial entitlement Values for Inactive Risk
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param record       record that contains the national program information
     */
    public static void setInitialEntitlementValuesForNationalProgram(PolicyHeader policyHeader, Record record) {
        NationalProgramEntitlementRecordLoadProcessor entitlementRLP = new NationalProgramEntitlementRecordLoadProcessor(
            policyHeader);
        entitlementRLP.postProcessRecord(record, true);
    }

    public NationalProgramEntitlementRecordLoadProcessor() {
    }

    public NationalProgramEntitlementRecordLoadProcessor(PolicyHeader policyHeader) {
        setPolicyHeader(policyHeader);
    }

    public PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    public void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }

    private PolicyHeader m_policyHeader;
    public static final String IS_SAVE_AVAILABLE = "isSaveAvailable";
    public static final String IS_ADD_AVAILABLE = "isAddAvailable";
    public static final String IS_DELETE_AVAILABLE = "isDeleteAvailable";
    public static final String IS_NATIONAL_PROGRAM_EDITABLE = "isNationalProgramEditable";
    public static final String IS_RECORD_EDITABLE = "isRecordEditable";
}