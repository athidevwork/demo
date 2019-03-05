package dti.pm.policymgr.underwritermgr.impl;

import dti.cs.data.dbutility.DBUtilityManager;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.DisplayIndicator;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.converter.ConverterFactory;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.DisplayIndicatorRecordFilter;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordComparator;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.util.DateUtils;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.StringUtils;
import dti.pm.busobjs.TransactionCode;
import dti.pm.busobjs.TransactionStatus;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.core.http.RequestIds;
import dti.pm.core.struts.AddSelectIndLoadProcessor;
import dti.pm.entitlementmgr.EntitlementFields;
import dti.pm.policymgr.PolicyFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.policymgr.underwritermgr.UnderwriterManager;
import dti.pm.policymgr.underwritermgr.UnderwritingFields;
import dti.pm.policymgr.underwritermgr.dao.UnderwriterDAO;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.transactionmgr.transaction.Transaction;
import dti.pm.validationmgr.impl.ContinuityRecordSetValidator;

import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides the implementation details for the underwriter manager.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 5, 2007
 *
 * @author jmpotosky
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/12/2007       JMP         Add setting of WebWB defaults
 * 06/29/2010       dzhang      DB prcedrue changed, hence need to amend loadAllUnderwriters & loadUnderwritersByTerm
 *                              and add method  getScreenModeCodeForLoadUnderwriter.
 * 08/30/2011       ryzhao      124458 - Modified validateAllUnderwriter to use FormatUtils.formatDateForDisplay()
 *                                       to format date when adding error messages.
 * 03/20/2013       awu         added isUnderwriterEntityExists
 * 06/05/2013       awu         138241 - 1. Add addUnderwriterTeam, addTeamMembers, getUnderwriterTeam, setReturnRecord,
 *                                       2. Modified saveAllUnderwriters to get the current record.
 *                                       3. Modified performTransferUnderwriter to add uwTypeCode and transferTeamB
 *                                       4. Modified loadAllPolicyByUnderwriter to add uwTypeCode
 * 07/29/2013       awu         147031 - Modified addUnderwriterTeam to set the Type field to enable.
 * 07/30/2013       awu         147025 - Modified validateAllUnderwriter to validate the required of uwTypeCode,entityId,
 *                                       effectiveFromDate, effectiveToDate
 * 08/13/2013       awu         147172 - Modified addUnderwriterTeam to set start date, end date, renew flag for every role.
 * 08/22/2013       awu         147236 - 1. Modified addTeamMembers to expire the original records by effective date of the
 *                                       original UW.
 *                                       2. Modified validateAllUnderwriter to validate the records which are displaying.
 * 08/26/2013       awu         145390 - Modified validateAllUnderwriter to filter out the flat expired record.
 * 11/11/2013       awu         149894 - Modified addTeamMembers to set the unchanged UW record to NOT_CHANGED.
 * 03/24/2014       awu         152334 - 1. Added sortAllUnderwritings.
 *                                       2. Modified addUnderwriterTeam to let Underwriter display at the first of its
 *                                          team members.
 * 12/08/2014       jyang       158577 - 1. Modified validateAllUnderwriter, copy inputRecords to continuityRecords for
 *                                          for continuity validation to make the inputRecords display indicator unchanged.
 *                                       2. Modified addUnderwriterTeam to populate 'isRenewalBAvailable' value based on
 *                                          underwriter's effective to date.
 * 03/13/2015       wdang       161495 - Fixed the problem introduced by 158577-1. Moved the flat expired condition lines
 *                                       out of the update/insert indicator block in validateAllUnderwriter().
 * 08/21/2015       ssheng      165340 - Use Common Function PolicyHeader.getRecordMode to replace calcuate recordModeCode
 *                                       via screenMode.
 * 09/07/2015       awu         164026 - Add loadUnderwritersByTermForWS.
 * 10/09/2015       wdang       166301 - Modified loadAllPolicyByUnderwriter to make the field name "territoryCode"
 *                                       consistent with oracle procedure parameter.
 * 03/13/2017       wli         183962 - 1. Modified saveAllUnderwriters to undo sort the inputRecords
 *                                       2. Modified validateAllUnderwriter to undo filter flat records
 * 04/24/2018       xnie        192517 - Modified saveAllUnderwriters to call validateDuplicateUnderwriters.
 * 09/03/2018       tyang       194775 - Modified sortAllUnderwritings to include underwriter type condition when decide
 *                                       if underwriter is added to sort record set.
 * ---------------------------------------------------
 */
public class UnderwriterManagerImpl implements UnderwriterManager {

    /**
     * Load all underwriters
     *
     * @param policyHeader
     * @return REcordSet
     */
    public RecordSet loadAllUnderwriters(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllUnderwriters", new Object[]{policyHeader});
        }

        Record inputRecord = new Record();
        inputRecord.setFieldValue(PolicyHeaderFields.POLICY_ID, policyHeader.getPolicyId());

        //Temporary set to term effictive from date & term effictive to date.
        inputRecord.setFieldValue("effDate", policyHeader.getTermEffectiveFromDate());
        inputRecord.setFieldValue("expDate", policyHeader.getTermEffectiveToDate());
        inputRecord.setFieldValue("recordMode", getScreenModeCodeForLoadUnderwriter(policyHeader));
        inputRecord.setFieldValue("showAllB","Y");

        // Setup the entitlements load processor
        RecordLoadProcessor entitlementRLP = new UnderwriterEntitlementRecordLoadProcessor(policyHeader);
        // Gets underwriters record set
        RecordSet rs = getUnderwriterDAO().loadAllUnderwriters(inputRecord, entitlementRLP);
        // If it shows all terms' data, all fields should be readOnly
        EntitlementFields.setReadOnly(rs.getSummaryRecord(), true);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllUnderwriters", rs);
        }
        return rs;
    }

    /**
     * Load all term underwriters
     *
     * @param policyHeader
     * @return RecordSet
     */
    public RecordSet loadUnderwritersByTerm(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadUnderwritersByTerm", new Object[]{policyHeader});
        }

        Record inputRecord = new Record();
        inputRecord.setFields(policyHeader.toRecord(), false);
        inputRecord.setFieldValue(PolicyHeaderFields.POLICY_ID, policyHeader.getPolicyId());
        inputRecord.setFieldValue("effDate", policyHeader.getTermEffectiveFromDate());
        inputRecord.setFieldValue("expDate", policyHeader.getTermEffectiveToDate());
        inputRecord.setFieldValue("recordMode", getScreenModeCodeForLoadUnderwriter(policyHeader));
        inputRecord.setFieldValue("showAllB","N");

        /* Setup the entitlements load processor */
        RecordLoadProcessor entitlementRLP = new UnderwriterEntitlementRecordLoadProcessor(policyHeader);

        /* Gets underwriters record set */
        RecordSet rs = getUnderwriterDAO().loadAllUnderwriters(inputRecord, entitlementRLP);

        /* Sort underwriting record set */
        RecordSet sortedRs = sortAllUnderwritings(rs);
        rs.getRecordList().clear();
        rs.addRecords(sortedRs);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadUnderwritersByTerm", rs);
        }
        return rs;
    }

    /**
     * Save all input records with UPDATE_IND set to 'Y' - updated, 'I' - inserted, or 'D' - deleted.
     *
     * @param policyHeader the summary policy information.
     * @param inputRecords a set of Records, each with the updated underwriter info
     * @return the number of rows updated.
     */
    public int saveAllUnderwriters(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllUnderwriters", new Object[]{inputRecords});

        int updateCount = 0;
        Transaction trans;

        inputRecords.setFieldsOnAll(policyHeader.toRecord(), false);

        // Determine if anything has changed
        RecordSet insertedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.INSERTED));
        RecordSet updatedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));

        String addlPolicyInfoChanged = inputRecords.getSummaryRecord().getStringValue("addlPolicyInfoChangedB");

        // If a change has occurred to either underwriter data or addl policy info - validate, create a trans and save
        if ((insertedRecords.getSize() + updatedRecords.getSize()) > 0 || addlPolicyInfoChanged.equalsIgnoreCase(String.valueOf(YesNoFlag.Y))) {

            // Validate the input underwriters prior to saving them.
            validateAllUnderwriter(policyHeader, inputRecords);

            // Get the summary record out and use it for createTransaction
            Record inputRecord = inputRecords.getSummaryRecord();

            // Create the transaction first
            trans = getTransactionManager().createTransaction(policyHeader, inputRecord, policyHeader.getTermEffectiveFromDate(), TransactionCode.ENDPOLADD, false);

            // Save additional policy information if changed
            if (YesNoFlag.getInstance(addlPolicyInfoChanged).booleanValue()) {

                // Before saving set the legacy policy # to all uppercase
                String legacyPolicyNo = inputRecords.getSummaryRecord().getStringValue("legacyPolicyNo");
                inputRecords.getSummaryRecord().setFieldValue("legacyPolicyNo", legacyPolicyNo.toUpperCase());

                // Call the DAO to process the save
                getUnderwriterDAO().saveAdditionalPolicyInfo(inputRecords);
            }
            // Set default values for insert, update
            inputRecords.setFieldValueOnAll("transactionLogId", trans.getTransactionLogId());

            // Add the inserted WIP records in batch mode
            if (insertedRecords.getSize() > 0) {
                insertedRecords.setFieldValueOnAll("rowStatus", "NEW");
                updateCount += getUnderwriterDAO().addAllUnderwriters(insertedRecords);
            }

            // Update the OFFICIAL records marked for update in batch mode
            if (updatedRecords.getSize() > 0) {
                updatedRecords.setFieldValueOnAll("rowStatus", "MODIFIED");
                updateCount += getUnderwriterDAO().updateAllUnderwriters(updatedRecords);
            }

            try {
                Record rec = new Record();
                PolicyFields.setPolicyId(rec, policyHeader.getPolicyId());
                String duplicateB = getUnderwriterDAO().validateDuplicateUnderwriters(rec);

                if (duplicateB.equals("Y")) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainUnderwriter.duplicateUnderwriter.error",
                        new String[]{duplicateB});
                    throw new ValidationException("Duplicate Underwriters.");
                }

                // Complete the transaction
                getTransactionManager().UpdateTransactionStatusNoLock(trans, TransactionStatus.COMPLETE);
            }
            catch (ValidationException ve) {
                //if there is validation exception throw it
                throw ve;
            }
            catch (Exception ex) {
                // If save failed, roll back all changes and delete wip if transaction is created by the page.
                getTransactionManager().deleteWipTransaction(policyHeader, inputRecord);

                // Throw the exception
                throw ExceptionHelper.getInstance().handleException("Failed to save underwriter.", ex);
            }
        }

        l.exiting(getClass().getName(), "saveAllUnderwriters", new Integer(updateCount));
        return updateCount;
    }


    protected void validateAllUnderwriter(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAllUnderwriter", new Object[]{policyHeader, inputRecords});
        }

        Iterator iter = inputRecords.getRecords();

        Date earliestStartDate = DateUtils.parseDate("01/01/3000");
        Date latestEndDate = DateUtils.parseDate("01/01/1900");

        while (iter.hasNext()) {
            Record rec = (Record) iter.next();
            String rowNum = String.valueOf(rec.getRecordNumber() + 1);
            String rowId = rec.getStringValue("entityRoleId");
            // If modified perform first set of validations
            if (rec.isUpdateIndicatorInserted() || rec.isUpdateIndicatorUpdated()) {
                // Validation for required fields.
                if (!rec.hasStringValue(UnderwritingFields.TYPE)) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainUnderwriter.required.error",
                        new String[]{rowNum, "Type"}, UnderwritingFields.TYPE, rowId);
                    throw new ValidationException("Invalid type.");
                }
                if (!rec.hasStringValue(UnderwritingFields.ENTITY_ID)) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainUnderwriter.required.error",
                        new String[]{rowNum, "Name"}, UnderwritingFields.ENTITY_ID, rowId);
                    throw new ValidationException("Invalid entity.");
                }
                if (!rec.hasStringValue(UnderwritingFields.EFFECTIVE_FROM_DATE)) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainUnderwriter.required.error",
                        new String[]{rowNum, "Start Date"}, UnderwritingFields.EFFECTIVE_FROM_DATE, rowId);
                    throw new ValidationException("Invalid Start Date.");
                }
                if (!rec.hasStringValue(UnderwritingFields.EFFECTIVE_TO_DATE)) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainUnderwriter.required.error",
                        new String[]{rowNum, "End Date"}, UnderwritingFields.EFFECTIVE_TO_DATE, rowId);
                    throw new ValidationException("Invalid End Date.");
                }
                // Validation #1:  End Date must be greater than or equal to Start Date
                if (rec.getDateValue("effectiveToDate").before(rec.getDateValue("effectiveFromDate"))) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainUnderwriter.invalidEffectiveToDate.error",
                        new String[]{rowNum}, "effectiveToDate", rowId);
                    throw new ValidationException("Invalid effective to date.");
                }

                // Validation #2:  Underwriter dates must be within term dates
                if (rec.getDateValue("effectiveFromDate").before(DateUtils.parseDate(policyHeader.getTermEffectiveFromDate())) ||
                    rec.getDateValue("effectiveToDate").after(DateUtils.parseDate(policyHeader.getTermEffectiveToDate()))) {
                    String fieldId = "effectiveToDate";
                    if (rec.getDateValue("effectiveFromDate").before(DateUtils.parseDate(policyHeader.getTermEffectiveFromDate())))
                        fieldId = "effectiveFromDate";
                    MessageManager.getInstance().addErrorMessage("pm.maintainUnderwriter.outsideTermDates.error",
                        new Object[]{rowNum,
                            FormatUtils.formatDateForDisplay(policyHeader.getTermEffectiveFromDate()),
                            FormatUtils.formatDateForDisplay(policyHeader.getTermEffectiveToDate())},
                        fieldId, rowId);
                    throw new ValidationException("Outside term dates.");
                }
            }

            // Validation #3:  Setup - Ensure earliest UW record is effective on term effective date
            if (rec.getDateValue("effectiveFromDate").before(earliestStartDate)) {
                earliestStartDate = rec.getDateValue("effectiveFromDate");
            }

            // Validation #4:  Setup - UW period must cover start and end dates
            if (rec.getDateValue("effectiveToDate").after(latestEndDate)) {
                latestEndDate = rec.getDateValue("effectiveToDate");
            }

            // stop validating the remaining records if we found problem(s) already
            if (MessageManager.getInstance().hasErrorMessages())
                break;
        }

        // Validation #3:  Validate - Ensure earliest UW record is effective on term effective date
        if (!earliestStartDate.equals(DateUtils.parseDate(policyHeader.getTermEffectiveFromDate()))) {
            MessageManager.getInstance().addErrorMessage("pm.maintainUnderwriter.invalidPeriodStartDate.error",
                new Object[]{FormatUtils.formatDateForDisplay(policyHeader.getTermEffectiveFromDate())});
        }

        // Validation #4:  Validate - UW period must cover start and end dates
        if (!latestEndDate.equals(DateUtils.parseDate(policyHeader.getTermEffectiveToDate()))) {
            MessageManager.getInstance().addErrorMessage("pm.maintainUnderwriter.invalidPeriodEndDate.error");
        }

        // Validation #5:  Validate continuity
        ContinuityRecordSetValidator continuityValidator = new ContinuityRecordSetValidator(
            "effectiveFromDate", "effectiveToDate", "entityRoleId",
            "pm.maintainUnderwriter.invalidContinuity.error", new String[]{UnderwritingFields.TYPE},
            new String[]{UnderwritingFields.TYPE}, true);

        continuityValidator.validate(inputRecords.getSubSet(new DisplayIndicatorRecordFilter(DisplayIndicator.VISIBLE)));

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid underwriter data.");
        }

        l.exiting(getClass().getName(), "validateAllUnderwriter");
    }

    public Record loadAdditionalPolicyInfo(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAdditionalPolicyInfo", new Object[]{policyHeader});
        }

        /* Gets additional policy information record set */
        Record record = getUnderwriterDAO().loadAdditionalPolicyInfo(policyHeader);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAdditionalPolicyInfo", record);
        }
        return record;
    }

    /**
     * Initial values defaults for a new underwriter record
     *
     * @param inputRecord contains policy term id level information
     * @return Record
     */
    public Record getInitialValues(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValues", new Object[]{inputRecord,});
        }

        //get default record from workbench
        Record output = getWorkbenchConfiguration().getDefaultValues(MAINTAIN_UNDERWRITER_ACTION_CLASS_NAME);

        // Get the initial entitlement values
        output.setFields(UnderwriterEntitlementRecordLoadProcessor.getInitialEntitlementValuesForUnderwriter());

        // Default term effective and expiration dates based on current policy term
        output.setFieldValue("effectiveFromDate", inputRecord.getStringValue(PolicyHeaderFields.TERM_EFFECTIVE_FROM_DATE));
        output.setFieldValue("effectiveToDate", inputRecord.getStringValue(PolicyHeaderFields.TERM_EFFECTIVE_TO_DATE));

        l.exiting(getClass().getName(), "getInitialValues");
        return output;
    }


    /**
     * Retrieve all policy info by from underwriter and other search criteria
     *
     * @param inputRecord
     * @return policy list
     */
    public RecordSet loadAllPolicyByUnderwriter(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllPolicyByUnderwriter", new Object[]{inputRecord});
        }
        Record record = new Record();
        record.setFieldValue("effDate", inputRecord.getFieldValue("effDate"));
        record.setFieldValue("underwriterId", inputRecord.getFieldValue("fromEntityId"));
        record.setFieldValue("issueCompanyEntityId", inputRecord.getFieldValue("issueCompanyEntityId"));
        record.setFieldValue("issueStateCode", inputRecord.getFieldValue("issueState"));
        record.setFieldValue("policyTypeCode", inputRecord.getFieldValue("policyTypeCode"));
        record.setFieldValue("countyCode", inputRecord.getFieldValue("countyCode"));
        record.setFieldValue("territoryCode", inputRecord.getFieldValue("territory"));
        record.setFieldValue("agentId", inputRecord.getFieldValue("agent"));
        record.setFieldValue("uwTypeCode", inputRecord.getFieldValue("uwTypeCode"));
        RecordLoadProcessor selectIndProcessor = AddSelectIndLoadProcessor.getInstance();
        RecordSet rs = getUnderwriterDAO().loadAllPolicyByUnderwriter(record, selectIndProcessor);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllPolicyByUnderwriter", rs);
        }
        return rs;
    }

    /**
     * perform transfer underwriter
     *
     * @param inputRecord
     * @return
     */
    public Record performTransferUnderwriter(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performTransferUnderwriter", new Object[]{inputRecord});
        }

        String transferTeamB = UnderwritingFields.getTransferTeam(inputRecord);

        Record record = new Record();
        record.setFieldValue("policyId", inputRecord.getFieldValue("selectedPolicyIds"));
        record.setFieldValue("policyNo", inputRecord.getFieldValue("selectedPolicyNos"));
        record.setFieldValue("noOfPols", inputRecord.getFieldValue("numOfSelectedPolicies"));
        record.setFieldValue("dateOfChange", inputRecord.getFieldValue("effDate"));
        record.setFieldValue("fromEntityId", inputRecord.getFieldValue("fromEntityId"));
        record.setFieldValue("toEntityId", inputRecord.getFieldValue("toEntityId"));
        UnderwritingFields.setTransferTeam(record, transferTeamB);
        UnderwritingFields.setType(record, UnderwritingFields.getType(inputRecord));
        record.setFieldValue("acctDate", DateUtils.formatDate(new Date()));
        Record resultRecord = getUnderwriterDAO().performTransferUnderwriter(record);

        //set to user session
        if (resultRecord != null && !"VALID".equals(resultRecord.getFieldValue("return"))) {
            resultRecord.setFieldValue("policyNos", inputRecord.getFieldValue("selectedPolicyNos"));
            UserSessionManager.getInstance().getUserSession().set("errMsgForTransUW", resultRecord);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performTransferUnderwriter", resultRecord);
        }
        return resultRecord;
    }

    /**
     * add underwriter and its team members
     * @param inputRecord
     * @param inputRecordSet
     */
    public void addUnderwriterTeam(Record inputRecord, RecordSet inputRecordSet, PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addUnderwriterTeam", new Object[]{inputRecord});
        }

        Record outRec = null;
        RecordSet tempMemberSet = new RecordSet();

        String underwriterParams = UnderwritingFields.getUnderwriterParams(inputRecord);
        String[] underwriterArray = underwriterParams.split("@");

        Record underwriterRec = new Record();
        UnderwritingFields.setEntityId(underwriterRec, underwriterArray[0].trim());
        UnderwritingFields.setEffectiveFromDate(underwriterRec, underwriterArray[2].trim());
        UnderwritingFields.setEffectiveToDate(underwriterRec, underwriterArray[3].trim());
        UnderwritingFields.setRenewalB(underwriterRec, YesNoFlag.getInstance(underwriterArray[4].trim()));
        String addTeamB = underwriterArray[1].trim();
        boolean isUnderwriter = false;
        // if addTeamB is Y, load the team members out and populate the underwriter and it's members to page.
        if (YesNoFlag.getInstance(addTeamB).booleanValue()) {
            RecordSet memberSet = getUnderwriterDAO().loadAllUnderwriterTeamMember(underwriterRec);
            int memberSize = 0;
            if (memberSet != null) {
                memberSize = memberSet.getSize();
            }
            for (int i = 0; i < memberSize; i++) {
                Record tempRec = memberSet.getRecord(i);
                Record initialRec;
                if (!UnderwritingFields.getEntityId(tempRec).equals(UnderwritingFields.getEntityId(underwriterRec))) {
                    tempRec.setFields(policyHeader.toRecord());
                    initialRec = getInitialValues(tempRec);
                    UnderwritingFields.setEntityId(initialRec, UnderwritingFields.getEntityId(tempRec));
                }
                else {
                    initialRec = tempRec;
                    initialRec.setFieldValue("isTypeEditable", YesNoFlag.Y);
                    isUnderwriter = true;
                }
                UnderwritingFields.setEffectiveFromDate(initialRec, underwriterArray[2].trim());
                UnderwritingFields.setEffectiveToDate(initialRec, underwriterArray[3].trim());
                UnderwritingFields.setRenewalB(initialRec, YesNoFlag.getInstance(underwriterArray[4].trim()));
                UnderwritingFields.setType(initialRec, UnderwritingFields.getType(tempRec));
                //Set isRenewalBAvailable indicator value based on underwriter's expDate
                Date effectiveToDate = initialRec.getDateValue(UnderwritingFields.EFFECTIVE_TO_DATE);
                YesNoFlag isAvailable = YesNoFlag.Y;
                Date termExpirationDate = DateUtils.parseDate(policyHeader.getTermEffectiveToDate());
                if (effectiveToDate.before(termExpirationDate)) {
                    isAvailable = YesNoFlag.N;
                }
                initialRec.setFieldValue("isRenewalBAvailable", isAvailable);
                initialRec.setFieldValue("isRowEligibleForDelete", YesNoFlag.Y);
                initialRec.setFieldValue("isEffectiveFromDateEditable", YesNoFlag.Y);
                outRec = new Record();
                setReturnRecord(initialRec, outRec, inputRecordSet);
                long index = getDbUtilityManager().getNextSequenceNo();
                outRec.setFieldValue("entityRoleId", index);

                if (isUnderwriter) {
                    inputRecordSet.addRecord(outRec);
                    UserSessionManager.getInstance().getUserSession().set(RequestIds.ENTITY_ROLE_ID, index);
                    isUnderwriter = false;
                }
                else {
                    tempMemberSet.addRecord(outRec);
                }
            }
            inputRecordSet.addRecords(tempMemberSet);
        }
        //if addTeamB is N, then just populate the underwriter to page.
        else {
            outRec = new Record();
            setReturnRecord(underwriterRec, outRec, inputRecordSet);
            UnderwritingFields.setEntityRoleId(outRec, String.valueOf(getDbUtilityManager().getNextSequenceNo()));
            UnderwritingFields.setType(outRec, UnderwritingFields.UnderwritingCodeValues.UNDERWRITER);
            outRec.setFieldValue("isRowEligibleForDelete", YesNoFlag.Y);
            outRec.setFieldValue("isEffectiveFromDateEditable", YesNoFlag.Y);

            Date effectiveToDate = underwriterRec.getDateValue(UnderwritingFields.EFFECTIVE_TO_DATE);
            YesNoFlag isAvailable = YesNoFlag.Y;
            Date termExpirationDate = DateUtils.parseDate(policyHeader.getTermEffectiveToDate());
            if (effectiveToDate.before(termExpirationDate)) {
                isAvailable = YesNoFlag.N;
            }
            outRec.setFieldValue("isRenewalBAvailable", isAvailable);

            outRec.setFieldValue("isTypeEditable", YesNoFlag.Y);
            UnderwritingFields.setUnderwritingTeam(outRec, underwriterArray[5].trim());
            inputRecordSet.addRecord(outRec);
            UserSessionManager.getInstance().getUserSession().set(RequestIds.ENTITY_ROLE_ID,
                UnderwritingFields.getEntityRoleId(outRec));
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addUnderwriterTeam", null);
        }
    }

    /**
     * add team members and expire existed roles.
     * @param inputRecord
     * @param inputRecordSet
     * @param policyHeader
     * @return
     */
    public RecordSet addTeamMembers(Record inputRecord, RecordSet inputRecordSet, PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addTeamMembers", inputRecord);
        }
        Record outRec;
        RecordSet outputRecSet = new RecordSet();
        String existedUWEffDateStr = UnderwritingFields.getEffectiveFromDate(inputRecord);
        Date existedUWEffDate = DateUtils.parseDate(existedUWEffDateStr);
        String existedUWToDateStr = UnderwritingFields.getEffectiveToDate(inputRecord);
        RecordSet existedRecordSet = loadUnderwritersByTerm(policyHeader);
        int size = existedRecordSet.getSize();
        for (int i = 0; i < size; i++) {
            Record tempRec = existedRecordSet.getRecord(i);
            Date fromDate = DateUtils.parseDate(UnderwritingFields.getEffectiveFromDate(tempRec));
            Date toDate = DateUtils.parseDate(UnderwritingFields.getEffectiveToDate(tempRec));
            outRec = new Record();
            if ((existedUWEffDate.after(fromDate) || existedUWEffDate.equals(fromDate)) && existedUWEffDate.before(toDate)) {
                UnderwritingFields.setEffectiveToDate(tempRec, UnderwritingFields.getEffectiveFromDate(tempRec));
                UnderwritingFields.setRenewalB(tempRec, YesNoFlag.N);
                setReturnRecord(tempRec, outRec, inputRecordSet);
                outRec.setUpdateIndicator(UpdateIndicator.UPDATED);
                outRec.setDisplayIndicator(YesNoFlag.N);
                outputRecSet.addRecord(outRec);
            }
            else {
                setReturnRecord(tempRec, outRec, inputRecordSet);
                outRec.setUpdateIndicator(UpdateIndicator.NOT_CHANGED);
                outputRecSet.addRecord(outRec);
            }
        }

        UnderwritingFields.setEffectiveFromDate(inputRecord, policyHeader.getTermEffectiveFromDate());
        UnderwritingFields.setEffectiveToDate(inputRecord, policyHeader.getTermEffectiveToDate());
        UnderwritingFields.setType(inputRecord, UnderwritingFields.UnderwritingCodeValues.UNDERWRITER);
        RecordSet memberSet = getUnderwriterDAO().loadAllUnderwriterTeamMember(inputRecord);

        size = memberSet.getSize();
        //if the underwriter doesn't in any team.
        if (size == 0) {
            memberSet.addRecord(inputRecord);
            size = 1;
        }
        Record tempRec;
        for (int i = 0; i < size; i++) {
            tempRec = memberSet.getRecord(i);
            tempRec.setFields(policyHeader.toRecord());
            Record initialRec = getInitialValues(tempRec);
            UnderwritingFields.setType(initialRec, UnderwritingFields.getType(tempRec));
            UnderwritingFields.setEntityId(initialRec, UnderwritingFields.getEntityId(tempRec));
            UnderwritingFields.setUnderwritingTeam(initialRec, UnderwritingFields.getUnderwritingTeam(tempRec));
            initialRec.setFieldValue("isRowEligibleForDelete", "Y");
            UnderwritingFields.setEffectiveFromDate(initialRec, existedUWEffDateStr);
            UnderwritingFields.setEffectiveToDate(initialRec, existedUWToDateStr);
            outRec = new Record();
            setReturnRecord(initialRec, outRec, inputRecordSet);
            outRec.setUpdateIndicator(UpdateIndicator.INSERTED);
            UnderwritingFields.setEntityRoleId(outRec, String.valueOf(getDbUtilityManager().getNextSequenceNo()));
            outputRecSet.addRecord(outRec);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addTeamMembers", null);
        }
        return outputRecSet;
    }

    /**
     * get the underwriter's team.
     * @param inputRecord
     * @return
     */
    public Record getUnderwriterTeam(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getUnderwriterTeam", inputRecord);
        }

        Record memberInput = new Record();
        UnderwritingFields.setEntityId(memberInput, UnderwritingFields.getEntityId(inputRecord));
        UnderwritingFields.setEffectiveFromDate(memberInput, inputRecord.getStringValue("termEff"));
        UnderwritingFields.setEffectiveToDate(memberInput, inputRecord.getStringValue("termExp"));

        YesNoFlag changeTeamB = YesNoFlag.Y;

        RecordSet memberSet = getUnderwriterDAO().loadAllUnderwriterTeamMember(memberInput);
        int size = memberSet.getSize();
        if (size <= 1) {
            changeTeamB = YesNoFlag.N;
        }
        else {
            for (int i = 0; i < size; i++) {
                Record temp = memberSet.getRecord(i);
                String type = UnderwritingFields.getType(temp);
                if (!UnderwritingFields.UnderwritingCodeValues.UNDERWRITER.equals(type)) {
                    changeTeamB = YesNoFlag.Y;
                }
            }
        }
        Record outputRec = new Record();
        outputRec.setFieldValue("changeTeamB", changeTeamB);
        if (changeTeamB.booleanValue()) {
            Record record = getUnderwriterDAO().getUnderwriterTeam(inputRecord);
            String teamCode = record.getStringValue("returnValue");

            UnderwritingFields.setEntityId(outputRec, UnderwritingFields.getEntityId(inputRecord));
            UnderwritingFields.setUnderwritingTeam(outputRec, teamCode);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getUnderwriterTeam", null);
        }
        return outputRec;
    }


    /**
     * Load all term underwriters
     *
     * @param policyHeader
     * @return RecordSet
     */
    public RecordSet loadUnderwritersByTermForWS(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadUnderwritersByTermForWS", new Object[]{policyHeader});
        }

        Record inputRecord = new Record();
        inputRecord.setFields(policyHeader.toRecord(), false);
        inputRecord.setFieldValue(PolicyHeaderFields.POLICY_ID, policyHeader.getPolicyId());
        inputRecord.setFieldValue("effDate", policyHeader.getTermEffectiveFromDate());
        inputRecord.setFieldValue("expDate", policyHeader.getTermEffectiveToDate());
        inputRecord.setFieldValue("recordMode", "WIP");
        inputRecord.setFieldValue("showAllB","N");

        /* Gets underwriters record set */
        RecordSet rs = getUnderwriterDAO().loadAllUnderwriters(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadUnderwritersByTermForWS", rs);
        }
        return rs;
    }

    /**
     * Set up returned record for display using
     *
     * @param inputRecord
     * @param returnRecord
     * @param inputRecords
     * @return record
     */
    private void setReturnRecord(Record inputRecord, Record returnRecord, RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setReturnRecord", inputRecord);
        }

        String columnName = null;
        Iterator iter = inputRecords.getFieldNames();
        while (iter.hasNext()) {
            columnName = (String) iter.next();
            // Set empty value for all other columns that are not initialized
            if (inputRecord.hasField(columnName)) {
                returnRecord.setFieldValue(columnName, inputRecord.getStringValue(columnName));
            }
            else {
                returnRecord.setFieldValue(columnName, null);
            }
        }
        // Set indicators
        returnRecord.setDisplayIndicator(YesNoFlag.Y);
        returnRecord.setEditIndicator(YesNoFlag.Y);
        returnRecord.setUpdateIndicator(UpdateIndicator.INSERTED);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setReturnRecord", null);
        }

    }
    /**
     * Get screen mode code for load underwriter
     *
     * @param policyHeader policyHeader
     * @return Record
     */
    private String getScreenModeCodeForLoadUnderwriter(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getScreenModeCodeForLoadUnderwriter", new Object[]{policyHeader,});
        }

        String result = policyHeader.getRecordMode().getName();
        l.exiting(getClass().getName(), "getScreenModeCodeForLoadUnderwriter");
        return result;
    }

    /**
     * To check the entity id is a valid underwriter or not
     * @param inputRecord
     * @param policyHeader
     * @return
     */
    public boolean isUnderwriterEntityExists(Record inputRecord, PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isUnderwriterEntityExists", new Object[]{policyHeader, inputRecord});
        }
        inputRecord.setFields(policyHeader.toRecord(), false);
        YesNoFlag returnValue = getUnderwriterDAO().isUnderwriterEntity(inputRecord);
        l.exiting(getClass().getName(), "isUnderwriterEntityExists", returnValue);
        return returnValue.booleanValue();
    }

    /**
     *  Every member should be displayed under its underwriter. If a member doesn't have its team underwriter,
     *  then display it on the top.
     * @param rs
     * @return
     */
    public RecordSet sortAllUnderwritings(RecordSet rs) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "sortAllUnderwritings", new Object[]{rs});
        }

        RecordSet sortedRs = new RecordSet();
        RecordSet newRs = new RecordSet();

        //Filter out the Underwriter sub recordSet. Loop every Underwriter to load its team members.
        RecordSet undRs = rs.getSubSet(new RecordFilter(UnderwritingFields.TYPE, UnderwritingFields.UnderwritingCodeValues.UNDERWRITER));
        int undSize = undRs.getSize();
        for (int i = 0; i < undSize; i++) {
            Record undRec = undRs.getRecord(i);
            Date undEffFromDate = DateUtils.parseDate(UnderwritingFields.getEffectiveFromDate(undRec));
            Date undEffToDate = DateUtils.parseDate(UnderwritingFields.getEffectiveToDate(undRec));
            newRs.addRecord(undRec);
            RecordSet memberSet = getUnderwriterDAO().loadAllUnderwriterTeamMember(undRec);
            int memberSize = memberSet.getSize();
            for (int j = 0; j < memberSize; j++) {
                Record memberRec = memberSet.getRecord(j);
                if (UnderwritingFields.UnderwritingCodeValues.UNDERWRITER.equals(UnderwritingFields.getType(memberRec))) {
                    continue;
                }
                int allSize = rs.getSize();
                for (int k = 0; k < allSize; k++) {
                    Record tempRec = rs.getRecord(k);
                    Date membEffFromDate = DateUtils.parseDate(UnderwritingFields.getEffectiveFromDate(tempRec));
                    Date membEffToDate = DateUtils.parseDate(UnderwritingFields.getEffectiveToDate(tempRec));
                    if (UnderwritingFields.getEntityId(tempRec).equals(UnderwritingFields.getEntityId(memberRec))
                        && membEffFromDate.before(undEffToDate)
                        && membEffToDate.after(undEffFromDate)
                        && !newRs.getRecordList().contains(tempRec)) {
                        newRs.addRecord(tempRec);
                    }
                }
            }
        }

        //To add those non-Underwriter roles.
        int allSize = rs.getSize();
        int newSize = newRs.getSize();
        for (int i = 0; i < allSize; i++) {
            Record tempRec = rs.getRecord(i);
            boolean isAdded = false;
            for (int j = 0; j < newSize; j++) {
                Record tempNewRec = newRs.getRecord(j);
                if (UnderwritingFields.getEntityId(tempNewRec).equals(UnderwritingFields.getEntityId(tempRec)) &&
                    UnderwritingFields.getType(tempNewRec).equals(UnderwritingFields.getType(tempRec))) {
                    isAdded = true;
                }
            }
            if (!isAdded) {
                sortedRs.addRecord(tempRec);
            }
        }

        sortedRs.addRecords(newRs);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "sortAllUnderwritings", sortedRs);
        }
        return sortedRs;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getTransactionManager() == null)
            throw new ConfigurationException("The required property 'transactionManager' is missing.");
        if (getUnderwriterDAO() == null) {
            throw new ConfigurationException("The required property 'UnderwriterDAO' is missing.");
        }
        if (getWorkbenchConfiguration() == null)
            throw new ConfigurationException("The required property 'workbenchConfiguration' is missing.");
    }

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    public UnderwriterDAO getUnderwriterDAO() {
        return m_underwriterDAO;
    }

    public void setUnderwriterDAO(UnderwriterDAO underwriterDAO) {
        m_underwriterDAO = underwriterDAO;
    }

    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return m_workbenchConfiguration;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration) {
        m_workbenchConfiguration = workbenchConfiguration;
    }

    public DBUtilityManager getDbUtilityManager() {
        return m_dbUtilityManager;
    }

    public void setDbUtilityManager(DBUtilityManager dbUtilityManager) {
        m_dbUtilityManager = dbUtilityManager;
    }

    private UnderwriterDAO m_underwriterDAO;
    private TransactionManager m_transactionManager;
    private WorkbenchConfiguration m_workbenchConfiguration;
    private DBUtilityManager m_dbUtilityManager;

    protected static final String MAINTAIN_UNDERWRITER_ACTION_CLASS_NAME = "dti.pm.policymgr.underwritermgr.struts.MaintainUnderwriterAction";
}
