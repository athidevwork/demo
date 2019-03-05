package dti.pm.coveragemgr.manuscriptmgr.impl;

import dti.oasis.app.ConfigurationException;
import dti.oasis.app.AppException;
import dti.oasis.busobjs.DisplayIndicator;
import dti.oasis.recordset.DisplayIndicatorRecordFilter;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordLoadProcessorChainManager;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.DateUtils;
import dti.oasis.util.SysParmProvider;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.error.ValidationException;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.busobjs.SysParmIds;
import dti.pm.busobjs.PMRecordSetHelper;
import dti.pm.busobjs.PMStatusCode;
import dti.pm.componentmgr.ComponentManager;
import dti.pm.core.data.FilterOfficialRowForEndquoteRecordLoadProcessor;
import dti.pm.coveragemgr.CoverageFields;
import dti.pm.coveragemgr.manuscriptmgr.ManuscriptFields;
import dti.pm.coveragemgr.manuscriptmgr.ManuscriptManager;
import dti.pm.coveragemgr.manuscriptmgr.dao.ManuscriptDAO;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.Term;
import dti.pm.validationmgr.impl.AddOrigFieldsRecordLoadProcessor;
import dti.pm.validationmgr.impl.ContinuityRecordSetValidator;
import dti.pm.transactionmgr.TransactionFields;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Date;
import java.util.Iterator;
import java.util.GregorianCalendar;
import java.util.Calendar;

/**
 * This class provides the implementation details of ManuscriptManager Interface.
 * <p/>
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
 * 09/26/2007       fcb         75486 - getInitialValuesForAddManuscript: added logic to set the
 *                                      manuscript expiration date to the coverage exp date when
 *                                      the coverage expires before term effective_to_date
 * 09/23/2010       syang       Issue 110819 - Added FilterOfficialRowForEndquoteRecordLoadProcessor to loadAllManuscript().
 * 10/4/2010        gxc         Issue 110250 - Added setFieldsForExpireManuscript to set values in other editable fields to
 *                              null when expiring a record.  Also added validation to not allow changing other fields when expiring
 * 11/17/2010       wfu         Issue 115877 - Fixed defect when to set values in other editable fields before validation.
 * 02/09/2010       gxc         Issue 117088 Modified references to manuscriptEffectiveFromDate and manuscriptEffectiveToDate
 *                              and replaced with effectiveFromDate and effectiveToDate
 * 05/27/2011       syang       119771 - Modified validateAllManuscript() to validate End Date for not newly added row.
 * 08/03/2011       lmjiang     Issue 123573 Remove expire end date check when saving a manuscript
 * 08/30/2011       ryzhao      124458 - Modified validateAllManuscript to use FormatUtils.formatDateForDisplay()
 *                                       to format date when adding error messages.
 * 11/01/2011       lmjiang     Issue 126315 - move delete button show/hide rules to row level.
 * 02/10/2012       wfu         125055 - Added saveAttachment and loadAttachment.
 * 05/16/2012       jshen       132118 - 1) Added inputRecord parameter to method saveAllManuscript().
 *                                       2) Use coverage base effective to date to validate if manuscript's exp date is
 *                                          after that date when the corresponding risk is date change allowed risk type.
 * 06/25/2012       tcheng      134650 - Modified saveAttachment logic for support version in uploading file.
 * 07/06/2012       sxm         134889 - Disable Save option in prior term during renewal WIP
 * 07/20/2012       tcheng      135128 - Modified saveAttachment logic for support field type is CLOB
 * 08/15/2012       xnie        136023 - 1) Modified validateAllManuscript() to use coverage continuous effective to date
 *                                       for date compare, but not base coverage effective to date.
 *                                       2) Roll backed 132118 fix.
 * 01/08/2013       fcb         137981 - changes related to Pm_Dates modifications.
 * 07/06/2012       adeng       138685 - Modified validateAllManuscript() to use a DisplayIndicatorRecordFilter to filter
 *                                       out invisible records from the record set which is going to be validated.
 * 09/30/2013       xnie        140103 - Modified validateAllManuscript() to prevent changing value for other editable
 *                                       fields when expire a manuscript.
 * 01/24/2014       jyang       150639 - Move CoverageManager.getCoverageExpirationDate method to ComponentManager.
 * 02/11/2014       adeng       150657 - Modified validateAllManuscript() to correct the condition which added for issue
 *                                       140103.
 * 02/27/2014       Parker      149313 - Problem 3,The duplicated error message display issue.
 * 02/28/2014       adeng       149313 - Modified validateAllManuscript() to set the correct row number into validation
 *                                       error message, and to correct more messages for duplicated error.
 * 07/04/2014       Jyang       154814 - Modified getInitialValuesForAddManuscript(), add default value for Renew field
 *                                       based on manuscript's expDate and policy term's effective to date.
 * 06/17/2015       Enoch       157447 - Modified getInitialValuesForAddManuscript() to remove setting renewal indicator
 *                                       default value logic.
 * 07/07/2014       Tzeng       162487 - Expand Addl Text area for Manuscript Endorsements to allow 3000 characters to be
 *                                       entered.
 * 08/20/2015       ssheng      165340 - Use Common Function PolicyHeader.getRecordMode to replace calcuate recordModeCode
 *                                       via screenMode.
 * 09/15/2015       ssheng      165966 - Prevent save both renewal indicator and other values except expiration date
 * 08/31/2016       tzeng       179057 - 1) Added validateManuscriptOverlap to validate duplicate manuscripts in back end.
 *                                       2) Changed saveAllManuscript to call validateManuscriptOverlap.
 * 09/09/2016       xnie        178813 - 1) Added validateSameOffVersionExists() to check if any manuscript endorsement
 *                                          version which is from same official record and in same time period exists.
 *                                       2) Modified validateAllManuscript() to call validateSameOffVersionExists().
 * 02/07/2017       sjin        183025 - 1) Modified validateAllManuscript() to ignore the flat records while validating
 *                                          overlapping records.
 * 03/13/2017       wli         183962 - Revert changes by 183025
 * 07/31/2017       lzhang      182769 - Invisible ADD and SAVE button
 *                                       when transaction date is not located in risk period.
 * 10/08/2018       tyang       195966 - Modify setFieldsForExpireManuscript() to set renew indicator to NULL when its'
 *                                       value is not changed or it' value is changed but EffToDate is also beed changed.
 * ---------------------------------------------------
 */
public class ManuscriptManagerImpl implements ManuscriptManager {
    /**
     * Returns a RecordSet loaded with list of Manuscript data.
     * <p/>
     *
     * @param policyHeader  policy header that contains all key policy information.
     * @param loadProcessor an instance of data load processor
     * @param inputRecord   input record
     * @return a RecordSet loaded with list of Manuscript.
     */
    public RecordSet loadAllManuscript(PolicyHeader policyHeader, RecordLoadProcessor loadProcessor, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllManuscript", new Object[]{policyHeader, loadProcessor});
        }

        Record input = policyHeader.toRecord();
        // 105611, override the transactionLogId in policyHeader.
        input.setFields(inputRecord);
        // Set record mode code
        RecordMode recordMode = policyHeader.getRecordMode();
        ScreenModeCode screenModeCode = policyHeader.getScreenModeCode();
        PMCommonFields.setRecordModeCode(input, recordMode);

        // Setup the load processors
        RecordLoadProcessor entitlementLoadProcessor =
            new ManuscriptEntitlementRecordLoadProcessor(policyHeader, inputRecord);
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(
            loadProcessor, entitlementLoadProcessor);
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(
            loadProcessor, origFieldLoadProcessor);
        // Issue 110819, filter official record for end quote.
        FilterOfficialRowForEndquoteRecordLoadProcessor endquoteLoadProcessor =
            new FilterOfficialRowForEndquoteRecordLoadProcessor(policyHeader, "manuscriptEndorsementId");
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(loadProcessor, endquoteLoadProcessor);
        // Load Manuscript data
        RecordSet rs = getManuscriptDAO().loadAllManuscript(input, loadProcessor);

        // Show error message if there's no record in VIEW_POLICY or VIEW_ENDQUOTE mode
        if (rs.getSize() <= 0 && (screenModeCode.isViewPolicy() || screenModeCode.isViewEndquote())) {
            MessageManager.getInstance().addErrorMessage("pm.maintainManu.nodata.error");
        }

        // Show/hide save option
        // For OOSWIP or RENEWWIP, the Save option should be invisible if the term is not the initial term.
        String transDateStr = policyHeader.getLastTransactionInfo().getTransEffectiveFromDate();
        String contigCovgEffectiveDateStr = policyHeader.getCoverageHeader().getContigCoverageEffectiveDate();
        String contigCovgExpireDateStr = policyHeader.getCoverageHeader().getContigCoverageExpireDate();
        if (screenModeCode.isViewPolicy() || screenModeCode.isViewEndquote() ||
           (screenModeCode.isOosWIP() || screenModeCode.isRenewWIP()) && !policyHeader.isInitTermB()) {
            rs.getSummaryRecord().setFieldValue("isSaveAvailable", YesNoFlag.N);
        }
        else if ((screenModeCode.isManualEntry() || screenModeCode.isWIP()
            || screenModeCode.isRenewWIP() || screenModeCode.isOosWIP())
            && DateUtils.isTargetDateNotInDatesPeriod(transDateStr, contigCovgEffectiveDateStr, contigCovgExpireDateStr)){
            rs.getSummaryRecord().setFieldValue("isSaveAvailable", YesNoFlag.N);
        }
        else {
            rs.getSummaryRecord().setFieldValue("isSaveAvailable", YesNoFlag.Y);
        }

        // 105611, the following buttons should be hidden if it is opened from view cancellation detail page.
        if (inputRecord.hasStringValue("snapshotB") && "Y".equalsIgnoreCase(inputRecord.getStringValue("snapshotB"))) {
            rs.getSummaryRecord().setFieldValue("isSaveAvailable", YesNoFlag.N);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllManuscript", rs);
        }
        return rs;
    }

    /**
     * Returns a RecordSet loaded with list of Manuscript detail data.
     * <p/>
     *
     * @param policyHeader  policy header that contains all key policy information.
     * @param inputRecord   a Record with conditions for loading Manuscript detail data.
     * @param loadProcessor an instance of data load processor
     * @return a RecordSet loaded with list of Manuscript detail.
     */
    public RecordSet loadAllManuscriptDetail(PolicyHeader policyHeader, Record inputRecord, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllManuscriptDetail", new Object[]{policyHeader, inputRecord});
        }

        RecordLoadProcessor entitlementLoadProcessor = new ManuscriptDetailEntitlementRecordLoadProcessor(
            policyHeader, PMCommonFields.getRecordModeCode(inputRecord),
            ManuscriptFields.getOfficialRecordId(inputRecord),
            ManuscriptFields.getAfterImageRecordB(inputRecord),
            ManuscriptFields.getEffectiveFromDate(inputRecord),
            ManuscriptFields.getEffectiveToDate(inputRecord));
        loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(
            loadProcessor, entitlementLoadProcessor);
        RecordSet rs = getManuscriptDAO().loadAllManuscriptDetail(inputRecord, loadProcessor);
        Record summaryRecord = rs.getSummaryRecord();
        ManuscriptFields.setFormCode(summaryRecord, ManuscriptFields.getFormCode(inputRecord));
        PMCommonFields.setRecordModeCode(summaryRecord, PMCommonFields.getRecordModeCode(inputRecord));
        ManuscriptFields.setAfterImageRecordB(summaryRecord, ManuscriptFields.getAfterImageRecordB(inputRecord));
        ManuscriptFields.setOfficialRecordId(summaryRecord, ManuscriptFields.getOfficialRecordId(inputRecord));
        ManuscriptFields.setManuscriptEffectiveFromDate(summaryRecord, ManuscriptFields.getManuscriptEffectiveFromDate(inputRecord));
        ManuscriptFields.setManuscriptEffectiveToDate(summaryRecord, ManuscriptFields.getManuscriptEffectiveToDate(inputRecord));

        // Show/hide save option
        ScreenModeCode screenModeCode = policyHeader.getScreenModeCode();
        String transDateStr = policyHeader.getLastTransactionInfo().getTransEffectiveFromDate();
        String contigCovgEffectiveDateStr = policyHeader.getCoverageHeader().getContigCoverageEffectiveDate();
        String contigCovgExpireDateStr = policyHeader.getCoverageHeader().getContigCoverageExpireDate();
        if (screenModeCode.isViewPolicy() || screenModeCode.isViewEndquote()) {
            summaryRecord.setFieldValue("isSaveAvailable", YesNoFlag.N);
        }
        else if ((screenModeCode.isManualEntry() || screenModeCode.isWIP()
            || screenModeCode.isRenewWIP() || screenModeCode.isOosWIP())
            && DateUtils.isTargetDateNotInDatesPeriod(transDateStr, contigCovgEffectiveDateStr, contigCovgExpireDateStr)){
            rs.getSummaryRecord().setFieldValue("isSaveAvailable", YesNoFlag.N);
        }
        else {
            summaryRecord.setFieldValue("isSaveAvailable", YesNoFlag.Y);
        }

        //Show/Hide Delete option
        if(rs.getSize() > 0){
            rs.setFieldValueOnAll("isDeleteAvailable", inputRecord.getStringValue("isDeleteAvailable"));
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllManuscriptDetail", rs);
        }
        return rs;
    }

    /**
     * Load label, width and visibility for Manuscript Detail data.
     * <p/>
     *
     * @param record input record
     * @return a RecordSet with loaded list of information to control Manuscript Detail data.
     */
    public RecordSet loadManuscriptEndorsementDtl(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadManuscriptEndorsementDtl", new Object[]{record});
        }

        RecordSet rs = getManuscriptDAO().loadManuscriptEndorsementDtl(record);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadManuscriptEndorsementDtl", rs);
        }
        return rs;
    }

    /**
     * Save/Update/Delete all inserted/updated/deleted Manuscript data.
     * <p/>
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecords a set of Records, each with the added/updated Manuscript info.
     * @return the number of rows updated.
     */
    public int saveAllManuscript(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllManuscript", new Object[]{policyHeader, inputRecords});

        int updateCount = 0;

        // Validate the manuscipt data first
        validateAllManuscript(policyHeader, inputRecords);

        RecordSet changedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.DELETED, UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));

        // Add the PolicyHeader info to each Manuscript Record
        changedRecords.setFieldsOnAll(policyHeader.toRecord(), false);
        // issue 95005: The changed record's transaction fk must be set to current in progress transaction from policy header
        changedRecords.setFieldValueOnAll("transactionLogId", policyHeader.getLastTransactionId());

        RecordSet wipRecords = changedRecords.getSubSet(new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.TEMP));
        RecordSet offRecords = changedRecords.getSubSet(new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.OFFICIAL));

        // Delete the WIP records marked for delete in batch mode
        RecordSet deleteRecords = wipRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.DELETED));
        updateCount += getManuscriptDAO().deleteAllManuscript(deleteRecords);

        // Update the OFFICIAL records marked for update in batch mode
        offRecords.setFieldValueOnAll("EffectiveFromDate", policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        RecordSet updateRecords = offRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));
        setFieldsForExpireManuscript(updateRecords);
        updateCount += getManuscriptDAO().updateAllManuscript(updateRecords);

        // Get all inserted and updated records from WIP records
        RecordSet modifiedRecords = PMRecordSetHelper.setRowStatusOnModifiedRecords(wipRecords);
        updateCount += getManuscriptDAO().saveAllManuscript(modifiedRecords);

        if (!YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm(SysParmIds.PM_ALLOW_DUP_MANUSPT)).booleanValue()) {
            //validate all terms manuscript overlap data in back end.
            validateManuscriptOverlap(policyHeader);
        }

        l.exiting(getClass().getName(), "saveAllManuscript", new Integer(updateCount));
        return updateCount;
    }

    /**
     * All terms overlap validation in back end.
     * @param policyHeader
     * @throws ValidationException
     */
    public void validateManuscriptOverlap(PolicyHeader policyHeader) throws ValidationException {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateManuscriptOverlap", new Object[] { policyHeader });
        }
        Record record = new Record();
        CoverageFields.setCoverageBaseRecordId(record, policyHeader.getCoverageHeader().getCoverageBaseRecordId());
        if (getManuscriptDAO().hasManuscriptOverlap(record)) {
            MessageManager.getInstance().addErrorMessage("pm.maintainManu.allTermOverlapDates.error");
        }

        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid Data.");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateManuscriptOverlap");
        }
    }


    /**
     * Save/Update/Delete all inserted/updated/deleted Manuscript Detail data.
     * <p/>
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecords a set of Records, each with the added/updated Manuscript Detail info.
     * @return the number of rows updated.
     */
    public int saveAllManuscriptDetail(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllManuscriptDetail", new Object[]{policyHeader, inputRecords});

        int updateCount = 0;

        RecordSet changedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.DELETED, UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));

        // Add the PolicyHeader info to each Manuscript Record
        changedRecords.setFieldsOnAll(policyHeader.toRecord(), false);

        // Delete the WIP records marked for delete in batch mode
        RecordSet deleteRecords = changedRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.DELETED));
        updateCount += getManuscriptDAO().deleteAllManuscriptDetail(deleteRecords);

        // Get all inserted and updated records from WIP records
        RecordSet modifiedRecords = PMRecordSetHelper.setRowStatusOnModifiedRecords(changedRecords);
        updateCount += getManuscriptDAO().saveAllManuscriptDetail(modifiedRecords);

        l.exiting(getClass().getName(), "saveAllManuscriptDetail", new Integer(updateCount));
        return updateCount;
    }

    /**
     * Load all available Manuscript data for selection.
     * <p/>
     *
     * @param policyHeader  policy header that contains all key policy information.
     * @param loadProcessor an instance of data load processor.
     * @return a RecordSet loaded with list of available Manuscript.
     */
    public RecordSet loadAllAvailableManuscript(PolicyHeader policyHeader, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAvailableManuscript", new Object[]{policyHeader});
        }

        Record input = policyHeader.toRecord();

        // Get available Manuscript record set
        RecordSet rs = getManuscriptDAO().loadAllAvailableManuscript(input, loadProcessor);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAvailableManuscript", rs);
        }
        return rs;
    }

    /**
     * Get the default values for newly added Manuscript(s).
     * <p/>
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a Record with conditions for getting default Manuscript data.
     * @return a Record with Manuscript default data.
     */
    public Record getInitialValuesForAddManuscript(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForAddManuscript", new Object[]{policyHeader, inputRecord});
        }

        Record returnRecord = new Record();

        // Firstly, merge default value from web work bench configuration
        Record configuredDefaultValues = getWorkbenchConfiguration().getDefaultValues(MAINTAIN_MANUSCRIPT_ACTION_CLASS_NAME);
        if (configuredDefaultValues != null) {
            returnRecord.setFields(configuredDefaultValues);
        }

        // Secondly, Merge all default values for selected manuscript
        CoverageFields.setCoverageBaseRecordId(returnRecord, policyHeader.getCoverageHeader().getCoverageBaseRecordId());
        String transEffDate = policyHeader.getLastTransactionInfo().getTransEffectiveFromDate();
        ManuscriptFields.setEffectiveFromDate(returnRecord, transEffDate);
        ManuscriptFields.setManuscriptEffectiveFromDate(returnRecord, transEffDate);
        TransactionFields.setTransactionCode(returnRecord, policyHeader.getLastTransactionInfo().getTransactionCode());
        if (policyHeader.isQuoteEndorsementExists()) {
            TransactionFields.setEndorsementQuoteId(returnRecord, policyHeader.getLastTransactionInfo().getEndorsementQuoteId());
        }

        String formCode = ManuscriptFields.getFormCode(inputRecord);
        RecordSet availableManuscripts = loadAllAvailableManuscript(policyHeader, DefaultRecordLoadProcessor.DEFAULT_INSTANCE);
        RecordSet selectedManuscriptRs = availableManuscripts.getSubSet(new RecordFilter(ManuscriptFields.FORM_CODE, formCode));
        if (selectedManuscriptRs.getSize() == 1) {
            Record selectedManuscript = selectedManuscriptRs.getRecord(0);
            returnRecord.setFields(selectedManuscript);
            ManuscriptFields.setManuscriptPremium(returnRecord, ManuscriptFields.getDefaultPremiumAmt(selectedManuscript));

            // set manuscript effective to date
            Date effectiveFromDate = DateUtils.parseDate(transEffDate);
            String duration = ManuscriptFields.getDuration(selectedManuscript);
            String durationType = ManuscriptFields.getDurationType(selectedManuscript);
            if (!StringUtils.isBlank(duration) && !StringUtils.isBlank(durationType)) {
                GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTime(effectiveFromDate);

                if (durationType.equalsIgnoreCase(ManuscriptFields.DurationTypeValues.YEARS)) {
                    calendar.add(Calendar.YEAR, Integer.valueOf(duration).intValue());
                    ManuscriptFields.setManuscriptEffectiveToDate(returnRecord, DateUtils.formatDate(calendar.getTime()));
                }
                else if (durationType.equalsIgnoreCase(ManuscriptFields.DurationTypeValues.MONTHS)) {
                    calendar.add(Calendar.MONTH, Integer.valueOf(duration).intValue());
                    ManuscriptFields.setManuscriptEffectiveToDate(returnRecord, DateUtils.formatDate(calendar.getTime()));
                }
                else if (durationType.equalsIgnoreCase(ManuscriptFields.DurationTypeValues.WEEKS)) {
                    calendar.add(Calendar.DATE, Integer.valueOf(duration).intValue() * 7);
                    ManuscriptFields.setManuscriptEffectiveToDate(returnRecord, DateUtils.formatDate(calendar.getTime()));
                }
                else if (durationType.equalsIgnoreCase(ManuscriptFields.DurationTypeValues.DAYS)) {
                    calendar.add(Calendar.DATE, Integer.valueOf(duration).intValue());
                    ManuscriptFields.setManuscriptEffectiveToDate(returnRecord, DateUtils.formatDate(calendar.getTime()));
                }

                // set effectiveToDate
                Date manuscriptEffectiveToDate = DateUtils.parseDate(ManuscriptFields.getManuscriptEffectiveToDate(returnRecord));
                String latestTermExpDateStr = getLatestTermExpDate(policyHeader);
                Date latestTermExpDate = DateUtils.parseDate(latestTermExpDateStr);
                if (manuscriptEffectiveToDate.before(latestTermExpDate)) {
                    ManuscriptFields.setEffectiveToDate(returnRecord, ManuscriptFields.getManuscriptEffectiveToDate(returnRecord));
                }
                else {
                    ManuscriptFields.setEffectiveToDate(returnRecord, latestTermExpDateStr);
                }

                String sCovExpirationDateStr = getComponentManager().getCoverageExpirationDate(policyHeader.toRecord());
                if (!StringUtils.isBlank(sCovExpirationDateStr)) {
                    Date sCovExpirationDate = DateUtils.parseDate(sCovExpirationDateStr);
                    Date currEffToDate = DateUtils.parseDate(ManuscriptFields.getEffectiveToDate(returnRecord));
                    if (sCovExpirationDate.before(currEffToDate)) {
                        ManuscriptFields.setEffectiveToDate(returnRecord, sCovExpirationDateStr);
                        ManuscriptFields.setManuscriptEffectiveToDate(returnRecord, sCovExpirationDateStr);
                    }
                }
            }
            else {
                YesNoFlag dateChangeAllowedB = policyHeader.getRiskHeader().getDateChangeAllowedB();
                if (dateChangeAllowedB.booleanValue()) {
                    String sCovExpirationDateStr = getComponentManager().getCoverageExpirationDate(policyHeader.toRecord());
                    Date sCovExpirationDate = null;
                    if (!StringUtils.isBlank(sCovExpirationDateStr)) {
                        sCovExpirationDate = DateUtils.parseDate(sCovExpirationDateStr);
                        ManuscriptFields.setEffectiveToDate(returnRecord, sCovExpirationDateStr);
                        ManuscriptFields.setManuscriptEffectiveToDate(returnRecord, sCovExpirationDateStr);
                    }
                    String sTermExpirationDateStr = policyHeader.getTermEffectiveToDate();
                    Date sTermExpirationDate = DateUtils.parseDate(sTermExpirationDateStr);
                    if (!StringUtils.isBlank(sCovExpirationDateStr) && sCovExpirationDate.after(sTermExpirationDate)) {
                        ManuscriptFields.setManuscriptEffectiveToDate(returnRecord, sTermExpirationDateStr);
                        ManuscriptFields.setEffectiveToDate(returnRecord, sTermExpirationDateStr);
                    }
                    String covgEffDateStr = policyHeader.getCoverageHeader().getCoverageEffectiveFromDate();
                    Date covgEffDate = DateUtils.parseDate(covgEffDateStr);
                    if (covgEffDate.after(effectiveFromDate)) {
                        ManuscriptFields.setEffectiveFromDate(returnRecord, covgEffDateStr);
                        ManuscriptFields.setManuscriptEffectiveFromDate(returnRecord, covgEffDateStr);
                    }
                }
                else {
                    ScreenModeCode screenModeCode = policyHeader.getScreenModeCode();
                    if (screenModeCode.isOosWIP()) {
                        ManuscriptFields.setManuscriptEffectiveToDate(returnRecord, policyHeader.getTermEffectiveToDate());
                        ManuscriptFields.setEffectiveToDate(returnRecord, policyHeader.getTermEffectiveToDate());
                    }
                    else {
                        ManuscriptFields.setManuscriptEffectiveToDate(returnRecord, policyHeader.getPolicyExpirationDate());
                        ManuscriptFields.setEffectiveToDate(returnRecord, policyHeader.getPolicyExpirationDate());
                    }
                }
            }
            //Add default value for renewal based on the manuscript's expDate and policy's term effective to date.
            if (returnRecord.hasStringValue(ManuscriptFields.EFFECTIVE_TO_DATE) && DateUtils.parseDate(ManuscriptFields.
                getEffectiveToDate(returnRecord)).before(DateUtils.parseDate(policyHeader.getTermEffectiveToDate()))) {
                ManuscriptFields.setRenewalB(returnRecord, YesNoFlag.N);
            }
        }
        else {
            throw new AppException(AppException.UNEXPECTED_ERROR, "the selected available manuscript for <" +
                formCode + "> is not equals to 1; it returned " + selectedManuscriptRs.getSize() + " records.");
        }

        PMCommonFields.setRecordModeCode(returnRecord, RecordMode.TEMP);
        ManuscriptFields.setManuscriptStatus(returnRecord, PMStatusCode.PENDING);

        // Set the initial Manuscript Entitlement values
        ManuscriptEntitlementRecordLoadProcessor.setInitialEntitlementValuesForManuscript(policyHeader, returnRecord);

        // Set original value
        origFieldLoadProcessor.postProcessRecord(returnRecord, true);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForAddManuscript", returnRecord);
        }
        return returnRecord;
    }

    /**
     * Get the default values for newly added Manuscript Detail.
     * <p/>
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a Record with conditions for getting default Manuscript Detail data.
     * @return a Record with Manuscript Detail default data.
     */
    public Record getInitialValuesForAddManuscriptDetail(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForAddManuscriptDetail", new Object[]{policyHeader, inputRecord});
        }

        Record returnRecord = new Record();
        ManuscriptFields.setManuscriptEndorsementId(returnRecord, ManuscriptFields.getManuscriptEndorsementId(inputRecord));
        returnRecord.setFieldValue("isDeleteAvailable", YesNoFlag.Y);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForAddManuscriptDetail", returnRecord);
        }
        return returnRecord;
    }

    /**
     * Validate all manuscript.
     *
     * @param policyHeader
     * @param inputRecords
     */
    protected void validateAllManuscript(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAllManuscript", new Object[]{inputRecords});
        }

        // Set the displayRecordNumber to all visible records.
        inputRecords = PMRecordSetHelper.setDisplayRecordNumberOnRecords(inputRecords);

        //get validate recordset(inserted and updated) from input records
        RecordSet changedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));

        Iterator it = changedRecords.getRecords();

        while (it.hasNext()) {
            Record r = (Record) it.next();
            String rowNum = String.valueOf(r.getFieldValue(PMRecordSetHelper.DISPLAY_RECORD_NUMBER));
            String rowId = ManuscriptFields.getManuscriptEndorsementId(r);

            String formCode = "";
            if (r.hasStringValue(ManuscriptFields.FORM_CODE)) {
                formCode = r.getStringValue(ManuscriptFields.FORM_CODE);
            }

            String origFormCode = "";
            if (r.hasStringValue(ManuscriptFields.ORIG_FORM_CODE)) {
                origFormCode = r.getStringValue(ManuscriptFields.ORIG_FORM_CODE);
            }

            String fileName = "";
            if (r.hasStringValue(ManuscriptFields.FILE_NAME)) {
                fileName = r.getStringValue(ManuscriptFields.FILE_NAME);
            }

            String origFileName = "";
            if (r.hasStringValue(ManuscriptFields.ORIG_FILE_NAME)) {
                origFileName = r.getStringValue(ManuscriptFields.ORIG_FILE_NAME);
            }

            String additionalText = "";
            if (r.hasStringValue(ManuscriptFields.ADDITIONAL_TEXT)) {
                additionalText = r.getStringValue(ManuscriptFields.ADDITIONAL_TEXT);
            }

            String origAdditionalText = "";
            if (r.hasStringValue(ManuscriptFields.ORIG_ADDITIONAL_TEXT)) {
                origAdditionalText = r.getStringValue(ManuscriptFields.ORIG_ADDITIONAL_TEXT);
            }

            String manuscriptPremium = "";
            if (r.hasStringValue(ManuscriptFields.MANUSCRIPT_PREMIUM)) {
                manuscriptPremium = r.getStringValue(ManuscriptFields.MANUSCRIPT_PREMIUM);
            }

            String origManuscriptPremium = "";
            if (r.hasStringValue(ManuscriptFields.ORIG_MANUSCRIPT_PREMIUM)) {
                origManuscriptPremium = r.getStringValue(ManuscriptFields.ORIG_MANUSCRIPT_PREMIUM);
            }

            YesNoFlag renewB = ManuscriptFields.getRenewalB(r);
            YesNoFlag origRenewB = ManuscriptFields.getOrigRenewalB(r);

            //System prevent to change other manuscript attributes when expiring the manuscript
            boolean official = PMCommonFields.getRecordModeCode(r).isOfficial();
            boolean changedFromOfficialToTemp = false;
            String manuscriptOfficialRecordId = "";
            if (r.hasStringValue(ManuscriptFields.OFFICIAL_RECORD_ID)) {
                manuscriptOfficialRecordId = ManuscriptFields.getOfficialRecordId(r);
                if (Long.parseLong(manuscriptOfficialRecordId) > 0 && !PMCommonFields.getRecordModeCode(r).isOfficial()) {
                    changedFromOfficialToTemp = true;

                }
            }
            // only those two status should validate :  1, official. 2, change status from official to temp.
            if (official || changedFromOfficialToTemp) {
                String compareFormCode = origFormCode;
                String compareFileName = origFileName;
                String compareManuscriptPremium = origManuscriptPremium;
                String compareAdditionalText = origAdditionalText;
                String compareEffectiveFromDate = ManuscriptFields.getOrigEffectiveFromDate(r);
                String compareEffectiveToDate = ManuscriptFields.getOrigEffectiveToDate(r);
                YesNoFlag compareRenewB = origRenewB;
                if (changedFromOfficialToTemp) {
                    Iterator offIt = inputRecords.getRecords();
                    while (offIt.hasNext()) {
                        Record offR = (Record) offIt.next();
                        if (ManuscriptFields.getManuscriptEndorsementId(offR).equals(manuscriptOfficialRecordId)){
                            if (offR.hasStringValue(ManuscriptFields.FORM_CODE)) {
                                compareFormCode = ManuscriptFields.getFormCode(offR);
                            }
                            if (offR.hasStringValue(ManuscriptFields.FILE_NAME)) {
                                compareFileName = ManuscriptFields.getFileName(offR);
                            }
                            if (offR.hasStringValue(ManuscriptFields.MANUSCRIPT_PREMIUM)) {
                                compareManuscriptPremium = ManuscriptFields.getManuscriptPremium(offR);
                            }
                            if (offR.hasStringValue(ManuscriptFields.ADDITIONAL_TEXT)) {
                                compareAdditionalText = ManuscriptFields.getAdditionalText(offR);
                            }
                            if (offR.hasStringValue(ManuscriptFields.EFFECTIVE_FROM_DATE)) {
                                compareEffectiveFromDate = ManuscriptFields.getEffectiveFromDate(offR);
                            }
                            if (offR.hasStringValue(ManuscriptFields.EFFECTIVE_TO_DATE)) {
                                compareEffectiveToDate = ManuscriptFields.getEffectiveToDate(offR);
                            }
                            if (offR.hasStringValue(ManuscriptFields.RENEWAL_B)) {
                                compareRenewB = ManuscriptFields.getRenewalB(offR);
                            }
                            break;
                        }
                    }
                }

                Date compareEffectiveToDt =  DateUtils.parseDate(compareEffectiveToDate);
                Date compareEffectiveFromDt =  DateUtils.parseDate(compareEffectiveFromDate);
                Date effectiveFromDt = DateUtils.parseDate(ManuscriptFields.getEffectiveFromDate(r));
                Date effectiveToDt = DateUtils.parseDate(ManuscriptFields.getEffectiveToDate(r));
                if ((!effectiveToDt.equals(compareEffectiveToDt)
                    || (effectiveToDt.equals(compareEffectiveToDt)
                    && (effectiveFromDt.equals(compareEffectiveFromDt)
                    && effectiveFromDt.before(DateUtils.parseDate(policyHeader.getTermEffectiveFromDate()))
                    && changedFromOfficialToTemp))
                    || !renewB.booleanValue() == compareRenewB.booleanValue()) &&
                    (!formCode.equals(compareFormCode) || !fileName.equals(compareFileName) || !manuscriptPremium.equals(compareManuscriptPremium) || !additionalText.equals(compareAdditionalText))) {
                     MessageManager.getInstance().addErrorMessage("pm.maintainManu.expireManu.invalid.error", new String[]{rowNum}, "", rowId);
                }
            }
            // Validate the length of the Additional Text.
            if (r.hasStringValue(ManuscriptFields.ADDITIONAL_TEXT) && !StringUtils.isBlank(r.getStringValue(ManuscriptFields.ADDITIONAL_TEXT))) {
                int length = r.getStringValue(ManuscriptFields.ADDITIONAL_TEXT).length();
                if (length > 3000) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainManu.additionalText.tooLong", new String[]{rowNum}, "", rowId);
                }
            }
            // Validation #1: Invalid Renewal Flag
            String duration = ManuscriptFields.getDuration(r);
            String durationType = ManuscriptFields.getDurationType(r);
            if (renewB.booleanValue()) {
                if (!StringUtils.isBlank(duration) && !StringUtils.isBlank(durationType)) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainManu.renewal.invalid.error",
                        new String[]{rowNum}, "", rowId);
                    // reset to original value
                    ManuscriptFields.setRenewalB(r, ManuscriptFields.getOrigRenewalB(r));
                }
            }

            // Validation #2: Expiration Date Outside Policy Date
            String manuExpDateStr = ManuscriptFields.getEffectiveToDate(r);
            Date manuExpDate = DateUtils.parseDate(manuExpDateStr);
            String tranEffDateStr = policyHeader.getLastTransactionInfo().getTransEffectiveFromDate();
            Date tranEffDate = DateUtils.parseDate(tranEffDateStr);
            if (StringUtils.isBlank(duration) && StringUtils.isBlank(durationType)) {
                String latestTermExpDateStr = getLatestTermExpDate(policyHeader);
                Date latestTermExpDate = DateUtils.parseDate(latestTermExpDateStr);
                if (manuExpDate.before(tranEffDate) || manuExpDate.after(latestTermExpDate)) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainManu.expDate.outside.policyDate.error",
                        new String[]{rowNum,
                            FormatUtils.formatDateForDisplay(tranEffDateStr),
                            FormatUtils.formatDateForDisplay(latestTermExpDateStr)},"",rowId);
                    // reset to original value
                    ManuscriptFields.setEffectiveToDate(r, ManuscriptFields.getOrigEffectiveToDate(r));
                }
            }

            // Validate #3: Expiration Date After Coverage Expiration Date
            YesNoFlag dateChangeAllowedB = policyHeader.getRiskHeader().getDateChangeAllowedB();
            if (dateChangeAllowedB.booleanValue()) {
                String covgExpDateStr = getComponentManager().getCoverageExpirationDate(policyHeader.toRecord());
                if (!StringUtils.isBlank(covgExpDateStr)) {
                    Date covgExpDate = DateUtils.parseDate(covgExpDateStr);
                    if (manuExpDate.after(covgExpDate)) {
                        MessageManager.getInstance().addErrorMessage("pm.maintainManu.expDate.after.covgExpDate.error",
                            new String[]{rowNum}, "", rowId);
                        // reset the original value
                        ManuscriptFields.setEffectiveToDate(r, ManuscriptFields.getOrigEffectiveToDate(r));
                    }
                }
            }

            // Validation #4: Expiration Date Does Not Match Transaction Effective Date (For not newly added row)
            String valManendDates = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_VAL_MANEND_DATES);
            if (YesNoFlag.getInstance(valManendDates).booleanValue() && !manuExpDate.equals(tranEffDate) &&
                (r.hasStringValue(PMCommonFields.RECORD_MODE_CODE) && !PMCommonFields.getRecordModeCode(r).isTemp())) {
                MessageManager.getInstance().addErrorMessage("pm.maintainManu.expDate.notMatch.tranEffDate.error",
                    new String[]{rowNum}, "", rowId);
                // reset the original value
                ManuscriptFields.setEffectiveToDate(r, ManuscriptFields.getOrigEffectiveToDate(r));
            }

            // Validation #5: Premium Bearing Manuscript Extending Past the Current Term
            if (r.hasStringValue(ManuscriptFields.MANUSCRIPT_PREMIUM)) {
                String premium = ManuscriptFields.getManuscriptPremium(r);
                if (Double.parseDouble(premium) > 0) {
                    String termExpDateStr = policyHeader.getTermEffectiveToDate();
                    Date termExpDate = DateUtils.parseDate(termExpDateStr);
                    if (manuExpDate.after(termExpDate)) {
                        MessageManager.getInstance().addErrorMessage("pm.maintainManu.expDate.premium.extend.termExpDate.error",
                            new String[]{rowNum}, "", rowId);
                    }
                }
            }

            // Validation #6: Check if any manuscript endorsement version which is from same official record and in same
            // time period exists when current record's official record fk is null and effective to date is changed.
            if (!StringUtils.isBlank(ManuscriptFields.getOfficialRecordId(r)) &&
                !StringUtils.isSame(ManuscriptFields.getEffectiveToDate(r), ManuscriptFields.getOrigEffectiveToDate(r))) {
                if (validateSameOffVersionExists(r).equals("Y")) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainManu.sameOffVersionExists.error");
                }
            }
        }

        // Validation #7: Overlapping Manuscript Endorsement
        String allowDupManuspt = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_ALLOW_DUP_MANUSPT);
        if (!YesNoFlag.getInstance(allowDupManuspt).booleanValue()) {
            String[] keyFieldNames = new String[]{ManuscriptFields.FORM_CODE};
            ContinuityRecordSetValidator continuityValidator = new ContinuityRecordSetValidator(
                ManuscriptFields.EFFECTIVE_FROM_DATE, ManuscriptFields.EFFECTIVE_TO_DATE,
                ManuscriptFields.MANUSCRIPT_ENDORSEMENT_ID,
                "pm.maintainManu.duplicate.error", keyFieldNames, keyFieldNames);
            RecordSet wipRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
                new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED, UpdateIndicator.NOT_CHANGED}))
                .getSubSet(new DisplayIndicatorRecordFilter(new String[]{DisplayIndicator.VISIBLE}));
            continuityValidator.validate(wipRecords);
        }

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid Manuscript data.");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateAllManuscript");
        }
    }

    /**
     * Check if any manuscript endorsement version which is from same official record and in same time period exists.
     *
     * @param inputRecord
     */
    protected String validateSameOffVersionExists(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateSameOffVersionExists", new Object[]{inputRecord});
        }
        String sameOffVersionExists = getManuscriptDAO().validateSameOffVersionExists(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateSameOffVersionExists");
        }
        return sameOffVersionExists;
    }

    /**
     * Set fields to null if the manuscript is being expired.
     *
     * @param records
     */
    protected void setFieldsForExpireManuscript(RecordSet records) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setFieldsForExpireManuscript", new Object[]{records});
        }
        Iterator it = records.getRecords();
        while (it.hasNext()) {
            Record record = (Record) it.next();
            if (OFFICIAL.equals(record.getStringValue("RECORDMODECODE")) &&
                !record.getStringValue(ManuscriptFields.EFFECTIVE_TO_DATE).equals(ManuscriptFields.ORIG_EFFECTIVE_TO_DATE)) {

                String formCode = "";
                if (record.hasStringValue(ManuscriptFields.FORM_CODE)) {
                    formCode = record.getStringValue(ManuscriptFields.FORM_CODE);
                }

                String origFormCode = "";
                if (record.hasStringValue(ManuscriptFields.ORIG_FORM_CODE)) {
                    origFormCode = record.getStringValue(ManuscriptFields.ORIG_FORM_CODE);
                }

                String fileName = "";
                if (record.hasStringValue(ManuscriptFields.FILE_NAME)) {
                    fileName = record.getStringValue(ManuscriptFields.FILE_NAME);
                }

                String origFileName = "";
                if (record.hasStringValue(ManuscriptFields.ORIG_FILE_NAME)) {
                    origFileName = record.getStringValue(ManuscriptFields.ORIG_FILE_NAME);
                }

                String additionalText = "";
                if (record.hasStringValue(ManuscriptFields.ADDITIONAL_TEXT)) {
                    additionalText = record.getStringValue(ManuscriptFields.ADDITIONAL_TEXT);
                }

                String origAdditionalText = "";
                if (record.hasStringValue(ManuscriptFields.ORIG_ADDITIONAL_TEXT)) {
                    origAdditionalText = record.getStringValue(ManuscriptFields.ORIG_ADDITIONAL_TEXT);
                }

                String manuscriptPremium = "";
                if (record.hasStringValue(ManuscriptFields.MANUSCRIPT_PREMIUM)) {
                    manuscriptPremium = record.getStringValue(ManuscriptFields.MANUSCRIPT_PREMIUM);
                }

                String origManuscriptPremium = "";
                if (record.hasStringValue(ManuscriptFields.ORIG_MANUSCRIPT_PREMIUM)) {
                    origManuscriptPremium = record.getStringValue(ManuscriptFields.ORIG_MANUSCRIPT_PREMIUM);
                }

                String manuscriptRenew = "";
                if (record.hasStringValue(ManuscriptFields.RENEWAL_B)) {
                    manuscriptRenew = record.getStringValue(ManuscriptFields.RENEWAL_B);
                }

                String origRenew = "";
                if (record.hasStringValue(ManuscriptFields.ORIG_RENEWAL_B)) {
                    origRenew = record.getStringValue(ManuscriptFields.ORIG_RENEWAL_B);
                }

                String manuscriptEffToDate = "";
                if (record.hasStringValue(ManuscriptFields.EFFECTIVE_TO_DATE)) {
                    manuscriptEffToDate = record.getStringValue(ManuscriptFields.EFFECTIVE_TO_DATE);
                }

                String origEffToDate = "";
                if (record.hasStringValue(ManuscriptFields.ORIG_EFFECTIVE_TO_DATE)) {
                    origEffToDate = record.getStringValue(ManuscriptFields.ORIG_EFFECTIVE_TO_DATE);
                }

                if (formCode.equals(origFormCode) && fileName.equals(origFileName) && additionalText.equals(origAdditionalText) &&
                    manuscriptPremium.equals(origManuscriptPremium) && (manuscriptRenew.equals(origRenew) ||
                    !manuscriptRenew.equals(origRenew) && !manuscriptEffToDate.equals(origEffToDate))) {
                    record.setFieldValue(ManuscriptFields.FORM_CODE, null);
                    record.setFieldValue(ManuscriptFields.FILE_NAME, null);
                    record.setFieldValue(ManuscriptFields.ADDITIONAL_TEXT, null);
                    record.setFieldValue(ManuscriptFields.MANUSCRIPT_PREMIUM, null);
                    record.setFieldValue(ManuscriptFields.RENEWAL_B, null);
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setFieldsForExpireManuscript");
        }
    }

    /**
     * Get the latest term expiration date.
     *
     * @param policyHeader
     * @return
     */
    private String getLatestTermExpDate(PolicyHeader policyHeader) {
        String latestTermExpDateStr = "";
        Iterator iter = policyHeader.getPolicyTerms();
        if (iter.hasNext()) {
            Term lastTerm = (Term) iter.next();
            latestTermExpDateStr = lastTerm.getEffectiveToDate();
        }
        return latestTermExpDateStr;
    }

    /**
     * Save the attached RTF file to db table.
     * <p/>
     *
     * @param inputRecord  a Record with selected manuscript pk and file path.
     * @return
     */
    public void saveAttachment(PolicyHeader policyHeader,Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAttachment", inputRecord);
        }
        Reader reader = null;
        try {
            String filePath = ManuscriptFields.getImportFilePath(inputRecord);
            File file = new File(filePath);
            reader = new BufferedReader(new FileReader(file));
            ManuscriptFields.setFileContent(inputRecord, reader);
            RecordMode recordModeCode = null;
            if(inputRecord.hasStringValue(ManuscriptFields.RECORD_MODE_CODE)) {
                recordModeCode = RecordMode.getInstance(ManuscriptFields.getRecordModeCode(inputRecord));
                if(recordModeCode.isOfficial()){
                    TransactionFields.setTransactionLogId(inputRecord, policyHeader.getLastTransactionId());
                    ManuscriptFields.setEffectiveFromDate(inputRecord, policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
                    getManuscriptDAO().changeAttachment(inputRecord);
                } else {
                    //Call DAO method to save the file
                    getManuscriptDAO().saveAttachment(inputRecord);
                }
            }

            reader.close();
            reader = null;

            //Delete the temp file stored in the Weblogic server
            new File(filePath).delete();
        } catch (Exception e) {
            throw new AppException("pm.maintainManu.upload.error", "Failed to save the file.");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                    reader = null;
                } catch (Exception e) {
                    l.warning("File character stream closed fail.");
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAttachment");
        }
    }

    /**
     * Load the attached RTF file to download.
     * <p/>
     *
     * @param inputRecord  a Record with selected manuscript pk.
     * @return a Record with file input stream.
     */
    public Record loadAttachment(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAttachment", inputRecord);
        }

        //Call DAO method to load the file
        Record record = getManuscriptDAO().loadAttachment(inputRecord);
        if (record == null) {
            throw new AppException("pm.maintainManu.extract.error", "Failed to load the file.");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAttachment");
        }

        return record;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getManuscriptDAO() == null)
            throw new ConfigurationException("The required property 'manuscriptDAO' is missing.");
        if (getComponentManager() == null)
            throw new ConfigurationException("The required property 'componentManager' is missing.");
    }

    public ManuscriptManagerImpl() {
    }

    public ManuscriptDAO getManuscriptDAO() {
        return m_manuscriptDAO;
    }

    public void setManuscriptDAO(ManuscriptDAO manuscriptDAO) {
        m_manuscriptDAO = manuscriptDAO;
    }

    public ComponentManager getComponentManager() {
        return m_componentManager;
    }

    public void setComponentManager(ComponentManager componentManager) {
        m_componentManager = componentManager;
    }

    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return m_workbenchConfiguration;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration) {
        m_workbenchConfiguration = workbenchConfiguration;
    }

    protected static final String NULL = "NULL";
    protected static final String OFFICIAL = "OFFICIAL";

    private ManuscriptDAO m_manuscriptDAO;
    private ComponentManager m_componentManager;
    private WorkbenchConfiguration m_workbenchConfiguration;

    private static AddOrigFieldsRecordLoadProcessor origFieldLoadProcessor = new AddOrigFieldsRecordLoadProcessor(
        new String[]{ManuscriptFields.EFFECTIVE_FROM_DATE, ManuscriptFields.EFFECTIVE_TO_DATE,
            ManuscriptFields.RENEWAL_B, ManuscriptFields.FORM_CODE, ManuscriptFields.FILE_NAME,
            ManuscriptFields.ADDITIONAL_TEXT, ManuscriptFields.MANUSCRIPT_PREMIUM});
}
