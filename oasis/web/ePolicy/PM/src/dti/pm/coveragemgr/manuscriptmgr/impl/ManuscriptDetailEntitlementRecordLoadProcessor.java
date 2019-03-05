package dti.pm.coveragemgr.manuscriptmgr.impl;

import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.DateUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.busobjs.YesNoFlag;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.busobjs.PolicyStatus;
import dti.pm.busobjs.RecordMode;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Date;

/**
 * This class extends the default record load processor to enforce entitlements for Manuscript Detail web page. This
 * class works in conjunction with pageEntitlements.xml configuration.
 * <p/>
 * <p>Any field that requires entitlement enforcement is determined by an indicator field that gets appended to the list
 * of fields in a record. This record is published as xml data island, which then gets intercepted by pageEntitlements
 * oasis tag to auto-generate javascript that enforces the entitlements.</p>
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 27, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/01/2011       Jerry       Issue 126315 - move delete button show/hide rules to row level.
 * 03/23/2012       xnie        Issue 131674 - Corrected add/delete detail button show/hide logic.
 * 06/08/2016       cesar       - changed Integer.parseInt to Long.parseLong in postProcessRecord()
 * 12/07/2017       lzhang      182769 - Invisible ADD and SAVE button when transaction date
 *                                       is not located in coverage period
 *                                       and not editable field when transaction date
 *                                       is not located in manuscript period
 * ---------------------------------------------------
 */
public class ManuscriptDetailEntitlementRecordLoadProcessor implements RecordLoadProcessor {

    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     * @return true if this Record should be added to the RecordSet;
     *         false if this Record should be excluded from the RecordSet.
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecord",
            new Object[]{record, String.valueOf(rowIsOnCurrentPage)});

        RecordMode recordMode = getRecordMode();
        String tranEffDateStr = getPolicyHeader().getLastTransactionInfo().getTransEffectiveFromDate();
        String manuEffDateStr = getManuscriptEffectiveFromDate();
        String manuExpDateStr = getManuscriptEffectiveToDate();
        if (getScreenModeCode().isViewPolicy() || getScreenModeCode().isViewEndquote()
            || getScreenModeCode().isCancelWIP() || getScreenModeCode().isResinstateWIP()) {
            record.setEditIndicator(YesNoFlag.N);
        }
        else if (getScreenModeCode().isManualEntry() || getScreenModeCode().isOosWIP()) {
            record.setEditIndicator(YesNoFlag.Y);
        }
        else {
            PolicyStatus policyStatus = getPolicyHeader().getPolicyStatus();
            if (policyStatus.isActive() || policyStatus.isPending() || policyStatus.isAccepted()) {
               
                long officialRecordId = 0;
                if (!StringUtils.isBlank(getOfficialRecordId())) {
                    officialRecordId = Long.parseLong(getOfficialRecordId());
                }
                if (!recordMode.isOfficial() && officialRecordId > 0) {
                    if (getAfterImageRecordB().booleanValue()) {
                        record.setEditIndicator(YesNoFlag.Y);
                    }
                    else {
                        record.setEditIndicator(YesNoFlag.N);
                    }
                }
                else {
                    Date manuEffDate = DateUtils.parseDate(manuEffDateStr);
                    Date manuExpDate = DateUtils.parseDate(manuExpDateStr);
                    Date tranEffDate = DateUtils.parseDate(tranEffDateStr);
                    if ((tranEffDate.equals(manuEffDate) || tranEffDate.after(manuEffDate))
                        && tranEffDate.before(manuExpDate)) {
                        record.setEditIndicator(YesNoFlag.Y);
                    }
                    else {
                        record.setEditIndicator(YesNoFlag.N);
                    }
                }
            }
            else {
                record.setEditIndicator(YesNoFlag.N);
            }
        }

        if (isTransDateNotInDatesPeriod(tranEffDateStr, manuEffDateStr, manuExpDateStr)){
            record.setEditIndicator(YesNoFlag.N);
        }

        l.exiting(getClass().getName(), "postProcessRecord");
        return true;
    }

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecordSet", new Object[]{recordSet});

        Record summaryRecord = recordSet.getSummaryRecord();
        if (getScreenModeCode().isViewPolicy() || getScreenModeCode().isViewEndquote()
            || getScreenModeCode().isCancelWIP() || getScreenModeCode().isResinstateWIP()) {
            summaryRecord.setFieldValue("isAddAvailable", YesNoFlag.N);
        }
        else if (getScreenModeCode().isManualEntry() || getScreenModeCode().isOosWIP()) {
            summaryRecord.setFieldValue("isAddAvailable", YesNoFlag.Y);
        }
        else {
            PolicyStatus policyStatus = getPolicyHeader().getPolicyStatus();
            if (policyStatus.isActive() || policyStatus.isPending() || policyStatus.isAccepted()) {
                summaryRecord.setFieldValue("isAddAvailable", YesNoFlag.Y);
            }
            else {
                summaryRecord.setFieldValue("isAddAvailable", YesNoFlag.N);
            }
        }

        String tranEffDateStr = getPolicyHeader().getLastTransactionInfo().getTransEffectiveFromDate();
        String contigCovgEffectiveDateStr = getPolicyHeader().getCoverageHeader().getContigCoverageEffectiveDate();
        String contigCovgExpireDateStr = getPolicyHeader().getCoverageHeader().getContigCoverageExpireDate();
        if (isTransDateNotInDatesPeriod(tranEffDateStr, contigCovgEffectiveDateStr, contigCovgExpireDateStr)){
            summaryRecord.setFieldValue("isAddAvailable", YesNoFlag.N);
        }

        // If no summaryRecord is fetched, add the page entitlement fields to the recordset field collection
        if (recordSet.getSize() == 0) {
            ArrayList pageEntitlementFields = new ArrayList();
            pageEntitlementFields.add("isAddAvailable");
            pageEntitlementFields.add("isDeleteAvailable");
            recordSet.addFieldNameCollection(pageEntitlementFields);
        }

        l.exiting(getClass().getName(), "postProcessRecordSet");
    }

    /**
     * Check whether the transaction date is located in date period
     *
     * @return YesNoFlag
     */
    public boolean isTransDateNotInDatesPeriod(String transEffFromDate, String effFromDate, String effToDate) {
        Logger l = LogUtils.getLogger(DateUtils.class);
        if (l.isLoggable(Level.FINER)) {
            l.entering(DateUtils.class.getName(), "isTransDateNotInDatesPeriod");
        }

        boolean retval = false;
        ScreenModeCode screenModeCode = getPolicyHeader().getScreenModeCode();
        if ((screenModeCode.isManualEntry() || screenModeCode.isWIP() || screenModeCode.isRenewWIP() || screenModeCode.isOosWIP())
            && DateUtils.isTargetDateNotInDatesPeriod(transEffFromDate, effFromDate, effToDate)){
            retval = true;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(DateUtils.class.getName(), "isTransDateNotInDatesPeriod", retval);
        }
        return retval;
    }

    public ManuscriptDetailEntitlementRecordLoadProcessor(PolicyHeader policyHeader, RecordMode recordMode,
                                                          String officialRecordId, YesNoFlag afterImageRecordB,
                                                          String manuscriptEffectiveFromDate,
                                                          String manuscriptEffectiveToDate) {
        setPolicyHeader(policyHeader);
        setScreenModeCode(policyHeader.getScreenModeCode());
        setRecordMode(recordMode);
        setOfficialRecordId(officialRecordId);
        setAfterImageRecordB(afterImageRecordB);
        setManuscriptEffectiveFromDate(manuscriptEffectiveFromDate);
        setManuscriptEffectiveToDate(manuscriptEffectiveToDate);
    }

    public PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    public void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }

    public ScreenModeCode getScreenModeCode() {
        return m_screenModeCode;
    }

    public void setScreenModeCode(ScreenModeCode screenModeCode) {
        m_screenModeCode = screenModeCode;
    }

    public String getOfficialRecordId() {
        return m_officialRecordId;
    }

    public void setOfficialRecordId(String officialRecordId) {
        m_officialRecordId = officialRecordId;
    }

    public YesNoFlag getAfterImageRecordB() {
        return m_afterImageRecordB;
    }

    public void setAfterImageRecordB(YesNoFlag afterImageRecordB) {
        m_afterImageRecordB = afterImageRecordB;
    }

    public RecordMode getRecordMode() {
        return m_recordMode;
    }

    public void setRecordMode(RecordMode recordMode) {
        m_recordMode = recordMode;
    }

    public String getManuscriptEffectiveFromDate() {
        return m_manuscriptEffectiveFromDate;
    }

    public void setManuscriptEffectiveFromDate(String manuscriptEffectiveFromDate) {
        m_manuscriptEffectiveFromDate = manuscriptEffectiveFromDate;
    }

    public String getManuscriptEffectiveToDate() {
        return m_manuscriptEffectiveToDate;
    }

    public void setManuscriptEffectiveToDate(String manuscriptEffectiveToDate) {
        m_manuscriptEffectiveToDate = manuscriptEffectiveToDate;
    }

    private PolicyHeader m_policyHeader;
    private ScreenModeCode m_screenModeCode;
    private RecordMode m_recordMode;
    private String m_officialRecordId;
    private YesNoFlag m_afterImageRecordB;
    private String m_manuscriptEffectiveFromDate;
    private String m_manuscriptEffectiveToDate;
}
