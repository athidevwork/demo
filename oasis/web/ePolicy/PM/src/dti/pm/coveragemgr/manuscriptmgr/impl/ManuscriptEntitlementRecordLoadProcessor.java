package dti.pm.coveragemgr.manuscriptmgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.util.LogUtils;
import dti.oasis.util.DateUtils;
import dti.oasis.util.StringUtils;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.PolicyStatus;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.entitlementmgr.EntitlementFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.coveragemgr.manuscriptmgr.ManuscriptFields;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import dti.oasis.util.SysParmProvider;
/**
 * This class extends the default record load processor to enforce entitlements for Manuscript web page. This class works in
 * conjunction with pageEntitlements.xml configuration.
 * <p/>
 * <p>Any field that requires entitlement enforcement is determined by an indicator field that gets appended to the list
 * of fields in a record. This record is published as xml data island, which then gets intercepted by pageEntitlements
 * oasis tag to auto-generate javascript that enforces the entitlements.</p>
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 17, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/27/2007       fcb         Re-wrote logic for Delete action item and row edit indicator.
 * 02/09/2010       gxc         Issue 117088 Modified references to manuscriptEffectiveFromDate and manuscriptEffectiveToDate
 *                              and replaced with effectiveFromDate and effectiveToDate
 * 04/19/2011       syang       119513 - Hide the expired official record.
 * 02/10/2012       wfu         125055 - Added logic for Upload RTF and Extract RTF buttons. 
 * 06/11/2012       xnie        134250 - Modified postProcessRecord() to correct manuscript form editable or not logic.
 * 06/27/2012       tcheng      134650 - Modified postProcessRecord() to filter image as "N" for show uploadFile button
 * 07/06/2012       sxm         134889 - Set screen to readonly in prior term during renewal WIP
 * 01/20/2014       adeng       150450 - Added logic to use page entitlement to control renew indicator, it should be
 *                                       disabled and set to N if manuscript effective to date is prior to the term
 *                                       expiration date.
 * 07/04/2014       Jyang       154814 - Roll back 150450's change of adding logic to use page entitlement for renew indicator.
 * 09/17/2015       Elvin       Issue 160360: show Data Entry and hide Upload RTF button for Eloquence forms
 * 06/08/2016       cesar       - changed Integer.parseInt to Long.parseLong in postProcessRecord()
 * 07/14/2016       mlm         170307 - Integration of Ghostdraft.
 * 09/07/2016       mlm         179382 - Fix the Data Entry button visibility.
 * 03/13/2017       eyin        180675 - Added condition 'pmUIStyle.equals("T")', initialized the field 'isGridDetailAvailable' for UI change.
 * ---------------------------------------------------
 */
public class ManuscriptEntitlementRecordLoadProcessor implements RecordLoadProcessor {
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

        RecordMode recordMode = PMCommonFields.getRecordModeCode(record);
        record.setEditIndicator(YesNoFlag.N);
        record.setFieldValue("isAdditionalTextVisible", YesNoFlag.N);
        record.setFieldValue("isDeleteAvailable", YesNoFlag.N);
        record.setFieldValue("isUploadAvailable", YesNoFlag.N);
        record.setFieldValue("isExtractAvailable", YesNoFlag.N);
        record.setFieldValue("isDataEntryAvailable", YesNoFlag.N);
        if (!(getScreenModeCode().isCancelWIP() || getScreenModeCode().isViewPolicy() ||
            getScreenModeCode().isViewEndquote()|| getScreenModeCode().isResinstateWIP())){
 
            if (!recordMode.isOfficial()){
                record.setFieldValue("isDeleteAvailable", YesNoFlag.Y);
            }

            if (!recordMode.isOfficial() && record.hasStringValue(ManuscriptFields.OFFICIAL_RECORD_ID)) {
                if (ManuscriptFields.getAfterImageRecordB(record).booleanValue()) {
                    record.setEditIndicator(YesNoFlag.Y);
                }
            }
            else {
                String manEffDateStr = ManuscriptFields.getEffectiveFromDate(record);
                Date manEffDate = DateUtils.parseDate(manEffDateStr);
                String manExpDateStr = ManuscriptFields.getEffectiveToDate(record);
                Date manExpDate = DateUtils.parseDate(manExpDateStr);
                String trEffDateStr = getPolicyHeader().getLastTransactionInfo().getTransEffectiveFromDate();
                Date trEffDate = DateUtils.parseDate(trEffDateStr);

                if ((trEffDate.equals(manEffDate) || trEffDate.after(manEffDate)) &&
                    (trEffDate.equals(manEffDate) || trEffDate.before(manExpDate))) {
                    record.setEditIndicator(YesNoFlag.Y);
                }
            }

            //Upload button can be visible if manuscript record is
            //  1. new added.
            //  2. can be modified.
            if (((!recordMode.isOfficial()
                   && record.hasStringValue(ManuscriptFields.OFFICIAL_RECORD_ID)
                   && ManuscriptFields.getAfterImageRecordB(record).booleanValue())
                  || record.getEditIndicatorBooleanValue())
                && record.hasStringValue(ManuscriptFields.MANUSCRIPT_ENDORSEMENT_ID)
                && Long.parseLong(ManuscriptFields.getManuscriptEndorsementId(record)) > 0 ) {

                String docGenPrdName = record.getStringValue("docGenPrdName");
                // show Data Entry button and hide Upload RTF button for any 3rd party hosted forms
                if (!StringUtils.isBlank(docGenPrdName)) {
                    record.setFieldValue("isDataEntryAvailable", YesNoFlag.Y);
                } else {
                    record.setFieldValue("isUploadAvailable", YesNoFlag.Y);
                }

            }
        }

        // Handle Additional Text field
        if (record.hasStringValue(ManuscriptFields.ADDL_TEXT) && ManuscriptFields.getAddlText(record).booleanValue()) {
            record.setFieldValue("isAdditionalTextVisible", YesNoFlag.Y);
        }

        record.setFieldValue("isManuscriptEffectiveFromDateVisible", YesNoFlag.N);

        // Detail option
        if (record.hasStringValue(ManuscriptFields.HAS_DETAIL) && ManuscriptFields.getHasDetail(record).booleanValue()
            && (!ManuscriptFields.hasManuscriptStatus(record) || 
            ManuscriptFields.hasManuscriptStatus(record) && !ManuscriptFields.getManuscriptStatus(record).isPending())) {
            String pmUIStyle = SysParmProvider.getInstance().getSysParm("PM_UI_STYLE", "T");
            if(pmUIStyle.equals("T")){
                record.setFieldValue("isGridDetailAvailable", YesNoFlag.Y);
                record.setFieldValue("isDetailAvailable", YesNoFlag.N);
            }else{
                record.setFieldValue("isGridDetailAvailable", YesNoFlag.N);
                record.setFieldValue("isDetailAvailable", YesNoFlag.Y);
            }
        }
        else {
            record.setFieldValue("isDetailAvailable", YesNoFlag.N);
            record.setFieldValue("isGridDetailAvailable", YesNoFlag.N);
        }

        // Copy the value of "effectiveFromDate" to "manuscriptEffectiveToDate" to display the
        // computed value for manuscript effective to date field.
        // Put this piece of codes here to avoid creating a new loadProcessor class.
        ManuscriptFields.setManuscriptEffectiveToDate(record, ManuscriptFields.getEffectiveToDate(record));

        // During OOSWIP or RENEWWIP, the buttons should be invisible if the current term is not the initial term.
        if ((getScreenModeCode().isOosWIP() || getScreenModeCode().isRenewWIP()) && !getPolicyHeader().isInitTermB()) {
            record.setFieldValue("isDeleteAvailable", YesNoFlag.N);
            record.setFieldValue("isDetailAvailable", YesNoFlag.N);
            record.setFieldValue("isGridDetailAvailable", YesNoFlag.N);
            record.setFieldValue("isUploadAvailable", YesNoFlag.N);
            record.setFieldValue("isDataEntryAvailable", YesNoFlag.N);
        }

        // 105611, the following buttons should be hidden if it is opened from view cancellation detail page.
        if(getInputRecord().hasStringValue("snapshotB") && "Y".equalsIgnoreCase(getInputRecord().getStringValue("snapshotB"))){
            record.setFieldValue("isDeleteAvailable", YesNoFlag.N);
            record.setFieldValue("isDetailAvailable", YesNoFlag.N);
            record.setFieldValue("isGridDetailAvailable", YesNoFlag.N);
            record.setFieldValue("isUploadAvailable", YesNoFlag.N);
            record.setFieldValue("isDataEntryAvailable", YesNoFlag.N);
        }

        //Extract button can be visible if manuscript has attached RTF file.
        if (record.hasStringValue(ManuscriptFields.RTF_FILE_B) && ManuscriptFields.getRtfFileB(record).booleanValue()) {
            record.setFieldValue("isExtractAvailable", YesNoFlag.Y);
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

        // If no record is fetched, add the page entitlement fields to the recordset field collection
        if (recordSet.getSize() == 0) {
            ArrayList pageEntitlementFields = new ArrayList();
            pageEntitlementFields.add("isDeleteAvailable");
            pageEntitlementFields.add("isDetailAvailable");
            pageEntitlementFields.add("isGridDetailAvailable");
            pageEntitlementFields.add("isUploadAvailable");
            pageEntitlementFields.add("isExtractAvailable");
            pageEntitlementFields.add("isAdditionalTextVisible");
            pageEntitlementFields.add("isManuscriptEffectiveFromDateVisible");
            pageEntitlementFields.add("isDataEntryAvailable");
            recordSet.addFieldNameCollection(pageEntitlementFields);
        }

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
                EntitlementFields.setReadOnly(summaryRecord, YesNoFlag.Y);
                summaryRecord.setFieldValue("isAddAvailable", YesNoFlag.N);
            }
        }

        // Transaction Effective Date cannot be prior to Coverage Effective Date
        String tranEffDateStr = getPolicyHeader().getLastTransactionInfo().getTransEffectiveFromDate();
        Date tranEffDate = DateUtils.parseDate(tranEffDateStr);
        String covgEffDateStr = getPolicyHeader().getCoverageHeader().getCoverageEffectiveFromDate();
        Date covgEffDate = DateUtils.parseDate(covgEffDateStr);
        if (tranEffDate.before(covgEffDate)) {
            summaryRecord.setFieldValue("isAddAvailable", YesNoFlag.N);
        }

        // Filtering:
        // If a record with record mode code = 'TEMP' exists with a non-zero official record id,
        // if a record whose manuscript endorsement primary key equals the official record id is found,
        //  that record is not displayed.
        RecordSet tempRecords = recordSet.getSubSet(new RecordFilter("recordModeCode", "TEMP"));
        RecordSet offRecords = recordSet.getSubSet(new RecordFilter("recordModeCode", "OFFICIAL"));
        Iterator tempIt = tempRecords.getRecords();
        // Loop through records to set official record display_ind to "N", which hides rows in grid
        while (tempIt.hasNext()) {
            Record tempRec = (Record) tempIt.next();
            String sOfficialRecordId = tempRec.getStringValue("officialRecordId");
            Iterator offIt = offRecords.getRecords();
            while (offIt.hasNext()) {
                Record offRecord = (Record) offIt.next();
                String SManuscriptEndorsementId = offRecord.getStringValue("manuscriptEndorsementId");
                if (SManuscriptEndorsementId.equals(sOfficialRecordId)) {
                    offRecord.setDisplayIndicator("N");
                }
            }
        }
        // Hide the expired official record.
        Iterator offIt = offRecords.getRecords();
        String transactionLogId = getPolicyHeader().getLastTransactionInfo().getTransactionLogId();
        while (offIt.hasNext()) {
            Record offRecord = (Record) offIt.next();
            if (offRecord.hasStringValue("closingTransLogId") && transactionLogId.equals(offRecord.getStringValue("closingTransLogId"))) {
                offRecord.setDisplayIndicator("N");
            }
        }

        // During OOSWIP or RENEWWIP, the buttons should be invisible if the current term is not the initial term.
        if (getScreenModeCode().isViewPolicy() ||
            (getScreenModeCode().isOosWIP() || getScreenModeCode().isRenewWIP()) && !getPolicyHeader().isInitTermB()) {
            summaryRecord.setFieldValue("isAddAvailable", YesNoFlag.N);
            summaryRecord.setFieldValue("isSaveAvailable", YesNoFlag.N);
        }

        // 105611, the following buttons should be hidden if it is opened from view cancellation detail page.
        if(getInputRecord().hasStringValue("snapshotB") && "Y".equalsIgnoreCase(getInputRecord().getStringValue("snapshotB"))){
            summaryRecord.setFieldValue("isAddAvailable", YesNoFlag.N);
            summaryRecord.setFieldValue("isSaveAvailable", YesNoFlag.N);
        }

        String contigCovgEffectiveDateStr = getPolicyHeader().getCoverageHeader().getContigCoverageEffectiveDate();
        String contigCovgExpireDateStr = getPolicyHeader().getCoverageHeader().getContigCoverageExpireDate();
        ScreenModeCode screenModeCode = getPolicyHeader().getScreenModeCode();
        if ((screenModeCode.isManualEntry() || screenModeCode.isWIP() || screenModeCode.isRenewWIP() || screenModeCode.isOosWIP())
            && DateUtils.isTargetDateNotInDatesPeriod(tranEffDateStr, contigCovgEffectiveDateStr, contigCovgExpireDateStr)){
            summaryRecord.setFieldValue("isAddAvailable", YesNoFlag.N);
            summaryRecord.setFieldValue("isSaveAvailable", YesNoFlag.N);
        }

        // Set readOnly attribute to summary record
        EntitlementFields.setReadOnly(summaryRecord, isRecordSetReadOnly());

        l.exiting(getClass().getName(), "postProcessRecordSet");
    }

    /**
     * Set initial engitlement Values for Manuscript
     *
     * @param policyHeader
     * @param record
     */
    public static void setInitialEntitlementValuesForManuscript(
        PolicyHeader policyHeader, Record record) {
        ManuscriptEntitlementRecordLoadProcessor entitlementRLP =
            new ManuscriptEntitlementRecordLoadProcessor(policyHeader, record);
        // Set page entitlement values
        entitlementRLP.postProcessRecord(record, true);
    }

    public ManuscriptEntitlementRecordLoadProcessor(PolicyHeader policyHeader, Record inputRecord) {
        setPolicyHeader(policyHeader);
        setScreenModeCode(policyHeader.getScreenModeCode());
        setInputRecord(inputRecord);
    }

    /**
     * Check if current recordSet is readOnly
     *
     * @return boolean
     */
    private boolean isRecordSetReadOnly() {
        boolean isReadOnly = false;
        ScreenModeCode screenModeCode = getPolicyHeader().getScreenModeCode();

        // During OOSWIP or RENEWWIP, this page should be read only if the current time is not the initial term.
        if (screenModeCode.isViewPolicy() ||
            (screenModeCode.isOosWIP() || screenModeCode.isRenewWIP()) && !getPolicyHeader().isInitTermB()) {
            isReadOnly = true;
        }

        // 105611, the page should be readonly if it is opened from view cancellation detail page.
        if(getInputRecord().hasStringValue("snapshotB") && "Y".equalsIgnoreCase(getInputRecord().getStringValue("snapshotB"))){
            isReadOnly = true;
        }

        return isReadOnly;
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

    public Record getInputRecord() {
        return  m_inputRecord;
    }

    public void setInputRecord(Record inputRecord) {
        m_inputRecord = inputRecord;
    }

    private PolicyHeader m_policyHeader;
    private ScreenModeCode m_screenModeCode;
    private Record m_inputRecord;
}
