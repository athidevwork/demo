package dti.pm.schedulemgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.DateUtils;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.schedulemgr.ScheduleFields;
import dti.pm.entitlementmgr.EntitlementFields;
import weblogic.xml.crypto.utils.DataUtils;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * * This class extends the default record load processor to enforce entitlements for coverage web page. This class works in
 * conjunction with pageEntitlements.xml configuration.
 * <p/>
 * <p>Any field that requires entitlement enforcement is determined by an indicator field that gets appended to the list
 * of fields in a record. This record is published as xml data island, which then gets intercepted by pageEntitlements
 * oasis tag to auto-generate javascript that enforces the entitlements.</p>
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 14, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/11/2010       dzhang      112938 -  Modified postProcessRecord: In renewal WIP should hidden the record which
 *                                        closeTransactionLogId equals current transactionLogId.
 * 06/30/2011       xnie        121967 -  Modified postProcessRecord: In OOS WIP should hidden the record which
 *                                        closeTransactionLogId equals current transactionLogId.
 * 06/25/2012       sxm         134889 - Set screen to readonly in prior term during renewal WIP
 * 08/24/2015       jyang2      165575 - Modified postProcessRecord: Modified logic to hide the closed record when
 *                                       screenMode is REINSTATEWIP.
 * 11/19/2015       eyin        167171 - Modified getInitialEntitlementValuesForSchedule(), Add synchronized lock on this function.
 * 12/1/2016        sjin        181705 - Modified postProcessRecord: 1.Add REINSTATEWIP condition when show the displayed record.
 *                                       2. Add one operation when execute the  filter condition of displaying the delete button.
 * 12/07/2017       lzhang      182769 - Invisible ADD and SAVE button when transaction date
 *                                       is not located in risk/coverage period
 *                                       and not editable field when transaction date
 *                                       is not located in schedule period
 * ---------------------------------------------------
 */

public class ScheduleEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {
    /**
     * Returns a synchronized static instance of Scheudle Entitlement Record Load Processor that has the
     * implementation information.
     *
     * @param inputRecord, inuptRecord that provides basic information about selected policy
     * @return an instance of ScheudleEntitlementRecordLoadProcessor class
     */
    public synchronized static ScheduleEntitlementRecordLoadProcessor getInstance(Record inputRecord) {
        Logger l = LogUtils.enterLog(ScheduleEntitlementRecordLoadProcessor.class,
            "getInstance", new Object[]{inputRecord});

        ScheduleEntitlementRecordLoadProcessor instance;
        instance = new ScheduleEntitlementRecordLoadProcessor();
        instance.setInputRecord(inputRecord);

        l.exiting(ScheduleEntitlementRecordLoadProcessor.class.getName(), "getInstance", instance);
        return instance;
    }

    /**
     * Process the given record after it's been loaded to enforce page entitlements.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecord",
            new Object[]{record, String.valueOf(rowIsOnCurrentPage)});

        Date transEffectiveFromDate = getInputRecord().getDateValue("transEffectiveFromDate");
        ScreenModeCode screenModeCode = ScheduleFields.getScreenModeCode(getInputRecord());

        String officialRecordId = ScheduleFields.getOfficialRecordId(record);
        String scheduleId = ScheduleFields.getPolicyScheduleId(record);
        String closingTransLogId = ScheduleFields.getClosingTransLogId(record);
        String afterImageRecordB = ScheduleFields.getAfterImageRecordB(record);
        RecordMode recordModeCode = PMCommonFields.getRecordModeCode(record);
        // Fix issue 102997, if this record has been closed by the current transaction, system should not display it.
        // System retrieves the lastTransactionId from inputRecord directly since it contains all the field in PolicyHeader.
        String transactionLogId = getInputRecord().getStringValue("lastTransactionId");
        /**
         * Display Options Based on the screen mode
         * Screen Mode
         Add/Save
         Delete option
         Fields on screen

         "VIEW_POLICY", "VIEW_ENDQUOTE"
         Disabled
         Disabled
         Disabled

         "MANUAL_ENTRY", "OOSWIP"
         Enabled
         Disabled
         Enabled

         "WIP", "RENEWWIP"
         Enabled
         ** Set based on the row(explained below)
         ***

         All other modes
         Disabled
         Disabled
         Disabled


         ** If the record_mode_code of the record is 'OFFICIAL', the delete button will be disabled.
         Else, it will be enabled.

         *** If the official_record_fk of the record selected is greater than zero, AND record_mode_code = 'OFFICIAL'
         then
            if after_image_record_b is not Y then the fields should all be disabled
            if the after_image_recorb_b = Y and the screen mode is CANCELWIP, all fields should be disabled
            if the after_image_recorb_b = Y and the screen mode is not CANCELWIP, all fields should be enabled

         Else If the official_record_fk of the record selected is zero or null OR record_mode_code is not 'OFFICIAL'
         then
            if the transaction effective date does not fall within the selected record's effective and expiration date,
            then
                all the fields should be disabled
            if the transaction effective date falls within the selected record's effective and expiration date AND
               the screen mode is not CANCELWIP,
            then
                the fields should be enabled.
            If in CANCELWIP, the fields should be disabled.


         New *** ??? JMP is checking.
         If the official_record_fk of the record selected is zero or null OR record_mode_code is not 'OFFICIAL' and
            the transaction effective date does not fall within the selected record's effective and expiration date,
         then
             all the fields should be disabled
         Else If the official_record_fk of the record selected is greater than zero, AND record_mode_code = 'OFFICIAL' and
                 after_image_record_b is not Y
         then
            the fields should all be disabled
         */

        EntitlementFields.setIsRowEligibleForDelete(record, YesNoFlag.N);
        record.setEditIndicator(YesNoFlag.Y);
        if (screenModeCode.isViewPolicy() || screenModeCode.isViewEndquote()) {
            record.setEditIndicator(YesNoFlag.N);
        }
        else if (screenModeCode.isManualEntry() || screenModeCode.isOosWIP()) {
            record.setEditIndicator(YesNoFlag.Y);
            if (recordModeCode.isTemp()) {
                record.setFieldValue(IS_ROW_ELIGIBLE_FOR_DELETE, YesNoFlag.Y);
            }
        }
        else if (screenModeCode.isWIP() || screenModeCode.isRenewWIP()) {

            record.setEditIndicator(YesNoFlag.Y);
            if (recordModeCode.isTemp()) {
                record.setFieldValue(IS_ROW_ELIGIBLE_FOR_DELETE, YesNoFlag.Y);
            }
            if (recordModeCode.isOfficial()) {
                if (officialRecordId != null && Long.parseLong(officialRecordId) > 0) {
                    if (afterImageRecordB != null && !YesNoFlag.getInstance(afterImageRecordB).booleanValue()) {
                        record.setEditIndicator(YesNoFlag.N);
                    }
                    else if (afterImageRecordB != null && YesNoFlag.getInstance(afterImageRecordB).booleanValue()) {
                        if (screenModeCode.isCancelWIP()) {
                            record.setEditIndicator(YesNoFlag.N);
                        }
                        else {
                            record.setEditIndicator(YesNoFlag.Y);
                        }
                    }
                }
            }
            else if (!recordModeCode.isOfficial()
                || officialRecordId == null || Long.parseLong(officialRecordId) == 0) {
                Date effDate = DateUtils.parseDate(ScheduleFields.getEffectiveFromDate(record));
                Date expDate = DateUtils.parseDate(ScheduleFields.getEffectiveToDate(record));
                if (transEffectiveFromDate.before(effDate) || transEffectiveFromDate.after(expDate)) {
                    record.setEditIndicator(YesNoFlag.N);
                }
                else if (!screenModeCode.isCancelWIP()) {
                    record.setEditIndicator(YesNoFlag.Y);
                }
                else if (screenModeCode.isCancelWIP()) {
                    record.setEditIndicator(YesNoFlag.N);
                }
            }

        }
        else {
            record.setEditIndicator(YesNoFlag.N);
        }

        // Fix issue 102997. This record should be hiden rather than be filtered out,
        // because this official record should be redisplayed after delete the TEMP record and before Save.
        if (officialRecordId != null && Long.parseLong(officialRecordId) > 0 && recordModeCode.isOfficial()) {
            if (officialRecordId.equals(scheduleId)) {
                record.setDisplayIndicator(YesNoFlag.N);
            }
        }

        if (screenModeCode.isWIP() || screenModeCode.isViewEndquote() || screenModeCode.isCancelWIP()
            || screenModeCode.isRenewWIP() || screenModeCode.isOosWIP() || screenModeCode.isResinstateWIP()
            || screenModeCode.isManualEntry()) {
            if(closingTransLogId != null && closingTransLogId.equals(transactionLogId)){
               record.setDisplayIndicator(YesNoFlag.N);
            }
        }

        String scheEffectiveFromDate= ScheduleFields.getEffectiveFromDate(record);
        String scheEffectiveToDate= ScheduleFields.getEffectiveToDate(record);
        if (isTransDateNotInDatesPeriod(screenModeCode,
            DateUtils.formatDate(transEffectiveFromDate),
            scheEffectiveFromDate, scheEffectiveToDate)){
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

        // If no record is fetched, add the page entitlement fields to the recordset field collection
        if (recordSet.getSize() == 0) {
            recordSet.addFieldNameCollection(getInitialEntitlementValuesForSchedule().getFieldNameList());
        }
        // Set readOnly attribute to summary record
        EntitlementFields.setReadOnly(recordSet.getSummaryRecord(), isRecordSetReadOnly());
        l.exiting(getClass().getName(), "postProcessRecordSet");
    }

    /**
     * Check if current recordSet is readOnly
     *
     * @return boolean
     */
    private boolean isRecordSetReadOnly() {
        boolean isReadOnly = false;
        ScreenModeCode screenModeCode = ScheduleFields.getScreenModeCode(getInputRecord());

        // All Available fields are readonly in following screen mode:
        // 1) In "VIEW_POLICY", "VIEW_ENDQUOTE" or
        // 2) NOT in "MANUAL_ENTRY", "OOSWIP","WIP", "RENEWWIP" 
        if (screenModeCode.isViewPolicy() || screenModeCode.isViewEndquote()) {
            isReadOnly = true;
        }
        else if (!(screenModeCode.isManualEntry() || screenModeCode.isOosWIP() ||
            screenModeCode.isWIP() || screenModeCode.isRenewWIP())) {
            isReadOnly = true;
        }

        // During OOSWIP or RENEWWIP, this page should be read only if the current time is not the initial term.
        boolean initTermB = false;
        if(getInputRecord().hasStringValue("initTermB")){
             initTermB = YesNoFlag.getInstance(getInputRecord().getStringValue("initTermB")).booleanValue();
        }
        if ((screenModeCode.isOosWIP() || screenModeCode.isRenewWIP()) && !initTermB) {
            isReadOnly = true;
        }

        // 105611, system should determine whether the current request is from view cancellation detail page.
        if(getInputRecord().hasStringValue("snapshotB") && "Y".equalsIgnoreCase(getInputRecord().getStringValue("snapshotB"))){
            isReadOnly = true;
        }

        String effectiveFromDateStr= null;
        String effectiveToDateStr= null;
        String transDateStr = ScheduleFields.getTransEffectiveFromDate(getInputRecord());

        String sourceTableName = ScheduleFields.getSourceTableName(getInputRecord());
        if ("RISK".equals(sourceTableName)){
            effectiveFromDateStr = ScheduleFields.getContigRiskEffectiveDate(getInputRecord());
            effectiveToDateStr = ScheduleFields.getContigRiskExpireDate(getInputRecord());
        }else{
            effectiveFromDateStr = ScheduleFields.getContigCoverageEffectiveDate(getInputRecord());
            effectiveToDateStr = ScheduleFields.getContigCoverageExpireDate(getInputRecord());
        }

        if(isTransDateNotInDatesPeriod(screenModeCode, transDateStr, effectiveFromDateStr, effectiveToDateStr)){
            isReadOnly = true;
        }

        return isReadOnly;
    }

    /**
     * Check whether the transaction date is located in coverage period
     *
     * @return YesNoFlag
     */
    public boolean isTransDateNotInDatesPeriod(ScreenModeCode screenModeCode, String transEffFromDate, String effFromDate, String effToDate) {
        Logger l = LogUtils.getLogger(DateUtils.class);
        if (l.isLoggable(Level.FINER)) {
            l.entering(DateUtils.class.getName(), "isTransDateNotInRiskOrCovgDates");
        }

        boolean retval = false;
        if ((screenModeCode.isManualEntry() || screenModeCode.isWIP() || screenModeCode.isRenewWIP() || screenModeCode.isOosWIP())
            && DateUtils.isTargetDateNotInDatesPeriod(transEffFromDate, effFromDate, effToDate)){
            retval = true;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(DateUtils.class.getName(), "isTransDateNotInRiskOrCovgDates", retval);
        }
        return retval;
    }

    /**
     * Return a Record of initial entitlement values for a new Schedule record.
     */
    public synchronized static Record getInitialEntitlementValuesForSchedule() {
        if (c_initialEntitlementValues == null) {
            c_initialEntitlementValues = new Record();
            EntitlementFields.setIsRowEligibleForDelete(c_initialEntitlementValues, YesNoFlag.Y);
        }
        return c_initialEntitlementValues;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
    }

    public ScheduleEntitlementRecordLoadProcessor() {
    }


    public String getPageEntitlementRequestAttributePrefix() {
        return m_pageEntitlementRequestAttributePrefix;
    }

    public void setPageEntitlementRequestAttributePrefix(String pageEntitlementRequestAttributePrefix) {
        m_pageEntitlementRequestAttributePrefix = pageEntitlementRequestAttributePrefix;
    }

    private Record inputRecord;

    public Record getInputRecord() {
        return inputRecord;
    }

    public void setInputRecord(Record inputRecord) {
        this.inputRecord = inputRecord;
    }

    private String m_pageEntitlementRequestAttributePrefix;

    private static final String IS_ROW_ELIGIBLE_FOR_DELETE = "isRowEligibleForDelete";

    private static Record c_initialEntitlementValues;
}
