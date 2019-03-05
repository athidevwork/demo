package dti.pm.coveragemgr.vlcoveragemgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.DateUtils;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.PMStatusCode;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.busobjs.TransactionCode;
import dti.pm.coveragemgr.vlcoveragemgr.VLCoverageFields;
import dti.pm.entitlementmgr.EntitlementFields;
import dti.pm.policymgr.PolicyHeader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * page entitlement recordload processor for VL coverage info records loading
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 14, 2008
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class VLCoverageEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {

    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        RecordMode recordMode = PMCommonFields.getRecordModeCode(record);
        PMStatusCode vlCoverageStatus = VLCoverageFields.getVlCoverageStatus(getInputRecord());
        Date transEff = DateUtils.parseDate(getPolicyHeader().getLastTransactionInfo().getTransEffectiveFromDate());
        PMStatusCode employeeStatus = VLCoverageFields.getStatus(record);
        TransactionCode tranCode = getPolicyHeader().getLastTransactionInfo().getTransactionCode();
        ScreenModeCode vlScreenModeCode = VLCoverageFields.getVLScreenModeCode(getInputRecord());
        Date effDate = DateUtils.parseDate(VLCoverageFields.getEffectiveFromDate(record));
        Date expDate = DateUtils.parseDate(VLCoverageFields.getEffectiveToDate(record));
        boolean offIdGreaterZero = record.hasStringValue(VLCoverageFields.OFFICIAL_RECORD_ID)
            && Long.parseLong(VLCoverageFields.getOfficialRecordId(record)) > 0;
        boolean isAfterImage = VLCoverageFields.getAfterImageRecordB(record).booleanValue();
        boolean isCompanyInsured = VLCoverageFields.getCompanyInsuredB(record).booleanValue();
        boolean isRateFormEnabled = false;
        //set rate form enabled indicater
        if (vlScreenModeCode.isManualEntry() ||
            (!(vlScreenModeCode.isViewPolicy() || vlScreenModeCode.isViewEndquote() || vlScreenModeCode.isResinstateWIP() || vlScreenModeCode.isCancelWIP())
                && (vlCoverageStatus.isActive()) || vlCoverageStatus.isPending())
                && ((offIdGreaterZero && !recordMode.isOfficial() && isAfterImage)
                || (!offIdGreaterZero && employeeStatus.isPending())
                || ((!offIdGreaterZero || recordMode.isOfficial()) && employeeStatus.isActive()
                && !vlScreenModeCode.isOosWIP() && (!transEff.before(effDate)) && !transEff.after(expDate)))) {
            isRateFormEnabled = true;
        }

        //page entitlements
        EntitlementFields.setIsRowEligibleForDelete(record, false);
        if (!(vlScreenModeCode.isViewPolicy() || vlScreenModeCode.isViewEndquote()
            || vlScreenModeCode.isResinstateWIP() || vlScreenModeCode.isCancelWIP()) &&
            recordMode.isTemp()) {
            EntitlementFields.setIsRowEligibleForDelete(record, true);
        }
        record.setFieldValue("isCancelAvailable", YesNoFlag.N);
        if (vlScreenModeCode.isViewPolicy() && vlCoverageStatus.isActive() && employeeStatus.isActive()
            && !getPolicyHeader().isWipB()) {
            record.setFieldValue("isCancelAvailable", YesNoFlag.Y);
        }
        record.setFieldValue("isEndDateEditable", YesNoFlag.N);
        if (tranCode.isOosEndorsement() && isRateFormEnabled) {
            record.setFieldValue("isEndDateEditable", YesNoFlag.Y);
        }
        record.setFieldValue("isEffDateEditable", YesNoFlag.N);
        if (vlScreenModeCode.isOosWIP()
            && (!record.hasStringValue(VLCoverageFields.OFFICIAL_RECORD_ID)
            || Long.parseLong(VLCoverageFields.getOfficialRecordId(record)) <= 0)
            && isRateFormEnabled) {
            record.setFieldValue("isEffDateEditable", YesNoFlag.Y);
        }

        //rate form field
        record.setFieldValue("isRateFieldEnabled", YesNoFlag.N);
        if (isRateFormEnabled && !isCompanyInsured) {
            record.setFieldValue("isRateFieldEnabled", YesNoFlag.Y);
        }

        return true;
    }

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        ScreenModeCode vlScreenModeCode = VLCoverageFields.getVLScreenModeCode(getInputRecord());
        PMStatusCode vlCoverageStatus = VLCoverageFields.getVlCoverageStatus(getInputRecord());
        Record summeryRecord = recordSet.getSummaryRecord();
        //page entitlements
        summeryRecord.setFieldValue("isAddAvailable", YesNoFlag.N);
        if (vlScreenModeCode.isManualEntry()) {
            summeryRecord.setFieldValue("isAddAvailable", YesNoFlag.Y);
        }
        else if (!(vlScreenModeCode.isViewPolicy() || vlScreenModeCode.isViewEndquote()
            || vlScreenModeCode.isResinstateWIP() || vlScreenModeCode.isCancelWIP()) &&
            (vlCoverageStatus.isActive() || vlCoverageStatus.isPending())) {
            summeryRecord.setFieldValue("isAddAvailable", YesNoFlag.Y);
        }
        // add field names when the size of the recordset is zero
        if (recordSet.getSize() == 0) {
            List nameList = new ArrayList();
            nameList.add("isCancelAvailable");
            nameList.add("isEndDateEditable");
            nameList.add("isEffDateEditable");
            nameList.add("isRateFieldEnabled");
            nameList.add(EntitlementFields.IS_ROW_ELIGIBLE_FOR_DELETE);
            recordSet.addFieldNameCollection(nameList);
        }
    }

    public VLCoverageEntitlementRecordLoadProcessor(PolicyHeader policyHeader, Record inputRecord) {
        setInputRecord(inputRecord);
        setPolicyHeader(policyHeader);
    }

    private Record getInputRecord() {
        return m_inputRecord;
    }

    private void setInputRecord(Record inputRecord) {
        m_inputRecord = inputRecord;
    }

    private PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    private void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }

    private PolicyHeader m_policyHeader;
    private Record m_inputRecord;

}
