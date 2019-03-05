package dti.pm.agentmgr.impl;

import dti.pm.agentmgr.AgentManager;
import dti.pm.agentmgr.AgentFields;
import dti.pm.agentmgr.dao.AgentDAO;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.Term;
import dti.pm.busobjs.TransactionCode;
import dti.cs.data.dbutility.DBUtilityManager;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.app.ConfigurationException;
import dti.oasis.app.AppException;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.util.LogUtils;
import dti.oasis.util.SysParmProvider;
import dti.oasis.util.DateUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Date;
import java.util.Iterator;
import java.text.ParseException;

/**
 * This class provides the implementation details of AgentManager Interface.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 26, 2007
 *
 * @author gjlong
 *
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * Mar 21, 2008     James       Issue#75265 CreatedAdd Agent Tab to eCIS.
 *                              Same functionality, look and feel
 * 04/09/2008       fcb         validateAllPolicyAgent: isValidSubproducerOnSave added.
 * Apr 17, 2008     James       Issue#75265 Modify code according to code review
 * Apr 18, 2008     James       Issue#81846 Client -> Agent page: Set the Appointment
 *                              End with a valid date and left the Appointment Start
 *                              date empty. Save. No message returned and the Appointment
 *                              End date is saved. It is incorrect
 * Apr 18, 2008     James       Issue#81847 CIS -> Agent Page ->Agent Contract Commission 
 *                              part: Set the NB (RN, ERE) Comm Basis to be persent, set
 *                              the NB (RN, ERE) to be less than 0 and save. No message
 *                              returned and changes will be saved
 * Apr 21, 2008     James       Issue#81843 In CIS -> Agent page: Set Agent Start Date
 *                              to "00/00/0000" and save. No message returned  and the
 *                              change will be saved
 * Apr 28, 2008     James       Issue#81844 CIS -. Agent Page: System return the incorrect 
 *                              message when set Agent Pay Commision Start date to be
 *                              before the Agent start date
 * 10/07/2008       yhyang      Issue#86934 Move CIS Agent to eCIS.
 * 02/01/2013       skommi      Issue#111565 Added loadAllAgentHistory() method.
 * 03/13/2013       awu         141924 - Added validateLicensedAgent.
 * 09/18/2014       iwang       156529 - Modified method validateAllPolicyAgent to add null
 *                              check on the Producer Agent License Id.
 * ---------------------------------------------------
 */
public class AgentManagerImpl implements AgentManager {


    /**
     * Method to load agent  and its related data for a policy
     *
     * @param policyHeader policyHeader for the policy.
     * @param inputRecord  a record containing policy information
     * @return recordSet resultset containing agents and their commission information
     */
    public RecordSet loadAllPolicyAgent(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllPolicyAgent", new Object[]{policyHeader, inputRecord});

        RecordLoadProcessor agentRecordLoadProcessor = new AgentEntitlementRecordLoadProcessor(policyHeader);

        // make some policyHeader data available to the inputRecord prior to call DAO
        inputRecord.setFields(policyHeader.toRecord(),false);
        RecordSet rs = getAgentDAO().loadAllAgent(inputRecord, agentRecordLoadProcessor);

        l.exiting(getClass().toString(), "loadAllPolicyAgent");
        return rs;
    }

    /**
     * Method to load expired agents for a policy
     *
     * @param inputRecord a record containing policy information
     * @return recordSet resultset containing agents and their commission information
     */
    public RecordSet loadAllAgentHistory(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllAgentHistory", new Object[]{inputRecord});

        RecordLoadProcessor agentHistRecordLoadProcessor = new AgentHistoryRecordLoadProcessor();

        RecordSet rs = getAgentDAO().loadAllAgent(inputRecord, agentHistRecordLoadProcessor);

        if (rs.getSize() <= 0) {
            MessageManager.getInstance().addInfoMessage("pm.agentmgr.viewAgentHistory.noDataFound");
        }

        l.exiting(getClass().getName(), "loadAllAgentHistory", rs);

        return rs;
    }

    /**
     * Method to load agent summary
     *
     * @param inputRecord input record that contains policy id
     * @return agent summary
     */
    public RecordSet loadAllPolicyAgentSummary(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllPolicyAgentSummary", new Object[]{inputRecord});
        }
        RecordSet outRecordSet =getAgentDAO().loadAllAgentSummary(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllPolicyAgentSummary", outRecordSet);
        }
        return outRecordSet;
    }

    /**
     * method to validation all records within the recordset. althought per UC requirements
     * only one row is inserted, with potentially many updated rows
     * (to specialConditionCode and agentNote fields)
     * if it found errors, it will raise validationException.
     *
     * @param policyHeader
     * @param inputRecords
     */
    public void validateAllPolicyAgent(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "validateAllPolicyAgent", new Object[]{policyHeader, inputRecords});

        MessageManager mm = MessageManager.getInstance();
        Record summaryRecord = inputRecords.getSummaryRecord();
        String effectiveFromDate = "";
        String effectiveToDate = "";
        String renewalCommBasis = "";
        String newbusCommBasis = "";
        String ereCommBasis = "";
        String latestTermExpDate = "";
        boolean isCommPayCodeAvaiable = summaryRecord.getBooleanValue(AgentFields.IS_COMM_PAY_CODE_AVAILABLE).booleanValue();
        boolean hasInsertedRecords = false; // any records inserted from the page?
        boolean isFirstRowToInsert = true;
        int failedRecordNumber = -1;
        RecordSet insertedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.INSERTED));
        Iterator insertIterator = insertedRecords.getRecords();

        while (insertIterator.hasNext()) {
            hasInsertedRecords = true;
            Record insertedRecord = (Record) insertIterator.next();
            String rowNum = String.valueOf(insertedRecord.getRecordNumber()+1);
            String rowId = insertedRecord.getStringValue("policyAgentId");
            // Many validations use dates, and commBasis fields so get them first
            if ( insertedRecord.hasStringValue("effectiveFromDate")) {
                effectiveFromDate = insertedRecord.getStringValue("effectiveFromDate");
            }

           if ( insertedRecord.hasStringValue("effectiveToDate")) {
                effectiveToDate = insertedRecord.getStringValue("effectiveToDate");
            }

            if (insertedRecord.hasStringValue("renewalCommBasis")) {
                renewalCommBasis = insertedRecord.getStringValue("renewalCommBasis");
            }

            if (insertedRecord.hasStringValue("newbusCommBasis")) {
                newbusCommBasis = insertedRecord.getStringValue("newbusCommBasis");
            }

            if (insertedRecord.hasStringValue("ereCommBasis")) {
                ereCommBasis = insertedRecord.getStringValue("ereCommBasis");
            }

            //[55.12] modify start date field
            String startDateForInProgressTransactionKey = "pm.agentmgr.saveNewAgent.startDateForInProgressTransaction"; // = Invalid Data.  Start date should be term effective or renewal term effective date
            String startDateForNonInProgressTransactionKey = "pm.agentmgr.saveNewAgent.startDateForNonInProgressTransaction"; // = Invalid Data.  Start date should be renewal term effective date

            if (!StringUtils.isBlank(effectiveFromDate)) {
                TransactionCode transactionCode = policyHeader.getLastTransactionInfo().getTransactionCode();
                if (policyHeader.isWipB() &&
                    (transactionCode.isNewBus() ||
                        transactionCode.isConvRenew() ||
                        transactionCode.isConvReissue() ||
                        transactionCode.isReissue() ||
                        transactionCode.isManualRenewal() ||
                        transactionCode.isAutoRenewal() ||
                        transactionCode.isQuote())) {   // found it, it is a term_creation tranaction code
                    if (!effectiveFromDate.equalsIgnoreCase(policyHeader.getTermEffectiveFromDate()) &&
                        !effectiveFromDate.equalsIgnoreCase(policyHeader.getTermEffectiveToDate())) {
                        mm.addErrorMessage(startDateForInProgressTransactionKey, new String[]{rowNum},
                            "effectiveFromDate", rowId);
                    }
                }
                else {  // it is not a termCreationTransaction
                    //Get Latest Term's Effective To Date
                    Iterator iter = policyHeader.getPolicyTerms();
                    if (iter.hasNext()) {
                        Term lastTerm = (Term) iter.next();
                        latestTermExpDate = lastTerm.getEffectiveToDate();
                    }

                    if (!effectiveFromDate.equalsIgnoreCase(latestTermExpDate)) {
                        mm.addErrorMessage(startDateForNonInProgressTransactionKey, new String[]{rowNum},
                            "effectiveFromDate", rowId);
                    }
                }
            }

            //[55.16] invalid commissionRate
            String invalidCommissionRateKey = "pm.agentmgr.saveNewAgent.invalidCommissionRate"; //=Invalid Data.  Please enter a value between 0% and {0}

            SysParmProvider sysParm = SysParmProvider.getInstance();
            double maxNewbusRateDouble = Double.parseDouble(sysParm.getSysParm("NEW BUS. RATE MAX", "20"));
            double maxRenwalRateDouble = Double.parseDouble(sysParm.getSysParm("RENEWAL RATE MAX", "20"));
            double maxEreRateDouble = Double.parseDouble(sysParm.getSysParm("ERE RATE MAX", "20"));

            double newbusCommRateDouble = -1;
            if (insertedRecord.hasStringValue("newbusCommRate")) {
                String newbusCommRate = insertedRecord.getStringValue("newbusCommRate");
                newbusCommRateDouble = Double.parseDouble(newbusCommRate);
                if (newbusCommRateDouble < 0 || newbusCommRateDouble > maxNewbusRateDouble) {
                    mm.addErrorMessage(invalidCommissionRateKey, new String[]{rowNum,Double.toString(maxNewbusRateDouble) + "%"},
                            "newbusCommRate", rowId);
                }
            }

            double renewalCommRateDouble = -1;
            if (insertedRecord.hasStringValue("renewalCommRate")) {
                String renewalCommRate = insertedRecord.getStringValue("renewalCommRate");
                renewalCommRateDouble = Double.parseDouble(renewalCommRate);
                if (renewalCommRateDouble < 0 || renewalCommRateDouble > maxRenwalRateDouble) {
                    mm.addErrorMessage(invalidCommissionRateKey, new String[]{rowNum,Double.toString(maxRenwalRateDouble) + "%"},
                            "renewalCommRate", rowId);
                }
            }

            double ereCommRateDouble = -1;
            if (insertedRecord.hasStringValue("ereCommRate")) {
                String ereCommRate = insertedRecord.getStringValue("ereCommRate");
                ereCommRateDouble = Double.parseDouble(ereCommRate);
                if (ereCommRateDouble < 0 || ereCommRateDouble > maxEreRateDouble) {
                    mm.addErrorMessage(invalidCommissionRateKey, new String[]{rowNum,Double.toString(maxEreRateDouble) + "%"},
                            "ereCommRate", rowId);
                }
            }

            //[uc55.24] save validation

            //[55.24.1]: wb configuration configured as required already. skip it

            //[55.24.2]: wb configuration configured as required already. skip it.

            //[55.24.3]
            // can not use StandardEffectiveToDateREcordValidator due to the second validation it includes
            // disagree with the current uc requirements
            String startDateGreaterThanEndDateKey = "pm.agentmgr.saveNewAgent.startDateGreaterThanEndDate"; // = Invalid Data. End date can not be prior to the Start date
            if (insertedRecord.hasStringValue("effectiveFromDate") && insertedRecord.hasStringValue("effectiveToDate")) {
                try {
                    int daysDiff = DateUtils.daysDiff(effectiveFromDate, effectiveToDate);
                    if (daysDiff < 0) {
                        mm.addErrorMessage(startDateGreaterThanEndDateKey,new String[]{rowNum},
                            "effectiveToDate", rowId);
                    }
                }
                catch (ParseException pe) {
                    AppException ae = new AppException(pe.toString());
                    l.throwing(getClass().getName(), effectiveFromDate+" and "+ effectiveToDate, ae);
                    throw ae;
                }
            }

            //[55.24.4] wb configuration configured as required already. skip it.

            //[55.24.5]
            String startDateNotEqualTermDateKey = "pm.agentmgr.saveNewAgent.startDateNotEqualTermDate"; // = Invalid Data. Select an agent for the current term first
            if (!summaryRecord.hasStringValue(AGENT_EXISTS_FOR_POLICY_FIELD_NAME)) {
                boolean agentExistsForPolicy = getAgentDAO().agentExistsForPolicy(policyHeader.toRecord());
                // let us add this value to the summary . so later when saving, it does not check again
                summaryRecord.setFieldValue(AGENT_EXISTS_FOR_POLICY_FIELD_NAME, Boolean.valueOf(agentExistsForPolicy));
                if (!agentExistsForPolicy && isFirstRowToInsert) {
                    if (!effectiveFromDate.equalsIgnoreCase(policyHeader.getTermEffectiveFromDate())) {
                        mm.addErrorMessage(startDateNotEqualTermDateKey,new String[]{rowNum});
                    }                  
                }
            }
            // after the above validation, let us set isFirstRowToInsert for next possbile record's validation
            // the above validation is only performed for the first row
            isFirstRowToInsert = false;

            // [55.24.6.1] .
            String noEreCommScheduleKey = "pm.agentmgr.saveNewAgent.noEreCommSchedule"; // = Invalid Data. Please select a ERE commission schedule
            String noNewbusCommScheduleKey = "pm.agentmgr.saveNewAgent.noNewbusCommSchedule"; // = Invalid Data. Please select a .. commission schedule
            String noRenewalCommScheduleKey = "pm.agentmgr.saveNewAgent.noRenewalCommSchedule"; // = Invalid Data. Please select a .. commission schedule
            //we have to have different keys, due to MessageManager currently only store messages for unique keys
            if (COMM_BASIS_SCHEDULE.equalsIgnoreCase(newbusCommBasis)) {
                if (!insertedRecord.hasStringValue("newbusCommRateScheduleId")) {
                    mm.addErrorMessage(noNewbusCommScheduleKey,new String[]{rowNum},
                            "newbusCommRateScheduleId", rowId);
                }
            }
            if (COMM_BASIS_SCHEDULE.equalsIgnoreCase(ereCommBasis)) {
                if (!insertedRecord.hasStringValue("ereCommRateScheduleId")) {
                    mm.addErrorMessage(noEreCommScheduleKey,new String[]{rowNum},
                            "ereCommRateScheduleId", rowId);
                }
            }
            if (COMM_BASIS_SCHEDULE.equalsIgnoreCase(renewalCommBasis)) {
                if (!insertedRecord.hasStringValue("renewalCommRateScheduleId")) {
                    mm.addErrorMessage(noRenewalCommScheduleKey,new String[]{rowNum},
                            "renewalCommRateScheduleId", rowId);
                }
            }

             //[55.24.6.1.2]
            String startDateNotWithinEreScheduleDatesKey = "pm.agentmgr.saveNewAgent.startDateNotWithinEreScheduleDates"; // =Invalid Data. Please select a different Ere Schedule, This schedule is not active from the current term
            String startDateNotWithinRenewalScheduleDatesKey = "pm.agentmgr.saveNewAgent.startDateNotWithinRenewalScheduleDates"; // =Invalid Data. Please select a different .. Schedule, This schedule is not active from the current term
            String startDateNotWithinNewbusScheduleDatesKey = "pm.agentmgr.saveNewAgent.startDateNotWithinNewbusScheduleDates"; // =Invalid Data. Please select a different .. Schedule, This schedule is not active from the current term
            Record rateScheduleRecord = new Record();
            rateScheduleRecord.setFields(policyHeader.toRecord(),false);
            try {
                if ( !StringUtils.isBlank(effectiveFromDate)) {
                    if (insertedRecord.hasStringValue("ereCommRateScheduleId")) {
                        rateScheduleRecord.setFieldValue("commRateScheduleId", insertedRecord.getStringValue("ereCommRateScheduleId"));
                        Record assignmentRecord = getAssignmentForRateSchedule(rateScheduleRecord);
                        String assignmentEffectiveFromDate = assignmentRecord.getStringValue("effectiveFromDate");
                        String assignmentEffectiveToDate = assignmentRecord.getStringValue("effectiveToDate");
                        if (DateUtils.daysDiff(assignmentEffectiveFromDate, effectiveFromDate) < 0 ||
                            DateUtils.daysDiff(assignmentEffectiveToDate, effectiveFromDate) >= 0) {
                            mm.addErrorMessage(startDateNotWithinEreScheduleDatesKey,new String[]{rowNum},
                            "ereCommRateScheduleId", rowId);
                        }
                    }

                    if (insertedRecord.hasStringValue("renewalCommRateScheduleId")) {
                        rateScheduleRecord.setFieldValue("commRateScheduleId", insertedRecord.getStringValue("renewalCommRateScheduleId"));
                        Record assignmentRecord = getAssignmentForRateSchedule(rateScheduleRecord);
                        String assignmentEffectiveFromDate = assignmentRecord.getStringValue("effectiveFromDate");
                        String assignmentEffectiveToDate = assignmentRecord.getStringValue("effectiveToDate");
                        if (DateUtils.daysDiff(assignmentEffectiveFromDate, effectiveFromDate) < 0 ||
                            DateUtils.daysDiff(assignmentEffectiveToDate, effectiveFromDate) >= 0) {
                            mm.addErrorMessage(startDateNotWithinRenewalScheduleDatesKey,new String[]{rowNum},
                            "renewalCommRateScheduleId", rowId);
                        }
                    }

                    if (insertedRecord.hasStringValue("newbusCommRateScheduleId")) {
                        rateScheduleRecord.setFieldValue("commRateScheduleId", insertedRecord.getStringValue("newbusCommRateScheduleId"));
                        Record assignmentRecord = getAssignmentForRateSchedule(rateScheduleRecord);
                        String assignmentEffectiveFromDate = assignmentRecord.getStringValue("effectiveFromDate");
                        String assignmentEffectiveToDate = assignmentRecord.getStringValue("effectiveToDate");
                        if (DateUtils.daysDiff(assignmentEffectiveFromDate, effectiveFromDate) < 0 ||
                            DateUtils.daysDiff(assignmentEffectiveToDate, effectiveFromDate) >= 0) {
                            mm.addErrorMessage(startDateNotWithinNewbusScheduleDatesKey,new String[]{rowNum},
                            "newbusCommRateScheduleId", rowId);
                        }
                    }
                }
            }
            catch (ParseException pe) {
                ValidationException ve = new ValidationException(pe.getMessage());
                l.throwing(getClass().getName(), pe.getMessage(), ve);
                throw ve;
            }

            //[55.24.6.2]
            String noEreFlatAmountKey = "pm.agentmgr.saveNewAgent.noEreFlatAmount"; // = Invalid Data. Please enter a .. flat amount.
            String noRenwalFlatAmountKey = "pm.agentmgr.saveNewAgent.noRenewalFlatAmount"; // = Invalid Data. Please enter a .. flat amount.
            String noNewbusFlatAmountKey = "pm.agentmgr.saveNewAgent.noNewbusFlatAmount"; // = Invalid Data. Please enter a .. flat amount.
            if (newbusCommBasis.equalsIgnoreCase(COMM_BASIS_FLAT)) {
                if (!insertedRecord.hasStringValue("newbusCommFlatAmount")) {
                    mm.addErrorMessage(noNewbusFlatAmountKey,new String[]{rowNum},
                            "newbusCommFlatAmount", rowId);
                }
            }
            if (ereCommBasis.equalsIgnoreCase(COMM_BASIS_FLAT)) {
                if (!insertedRecord.hasStringValue("ereCommFlatAmount")) {
                    mm.addErrorMessage(noEreFlatAmountKey,new String[]{rowNum},
                            "ereCommFlatAmount", rowId);
                }
            }
            if (renewalCommBasis.equalsIgnoreCase(COMM_BASIS_FLAT)) {
                if (!insertedRecord.hasStringValue("renewalCommFlatAmount")) {
                    mm.addErrorMessage(noRenwalFlatAmountKey,new String[]{rowNum},
                            "renewalCommFlatAmount", rowId);
                }
            }

            //[55.24.6.3]
            String noErePercentAmountKey = "pm.agentmgr.saveNewAgent.noErePercentAmount"; // = Invalid Data. Please enter a .. rate.
            String noRenewalPercentAmountKey = "pm.agentmgr.saveNewAgent.noRenewalPercentAmount"; // = Invalid Data. Please enter a .. rate.
            String noNewbusPercentAmountKey = "pm.agentmgr.saveNewAgent.noNewbusPercentAmount"; // = Invalid Data. Please enter a .. rate.
            if (newbusCommBasis.equalsIgnoreCase(COMM_BASIS_PERCENT)) {
                if (!insertedRecord.hasStringValue("newbusCommRate")) {
                    mm.addErrorMessage(noNewbusPercentAmountKey,new String[]{rowNum},
                            "newbusCommRate", rowId);
                }
            }
            if (ereCommBasis.equalsIgnoreCase(COMM_BASIS_PERCENT)) {
                if (!insertedRecord.hasStringValue("ereCommRate")) {
                    mm.addErrorMessage(noErePercentAmountKey,new String[]{rowNum},
                            "ereCommRate", rowId);
                }
            }
            if (renewalCommBasis.equalsIgnoreCase(COMM_BASIS_PERCENT)) {
                if (!insertedRecord.hasStringValue("renewalCommRate")) {
                    mm.addErrorMessage(noRenewalPercentAmountKey,new String[]{rowNum},
                            "renewalCommRate", rowId);
                }
            }

            //[55.24.7], this validation should be removed from UC, per discussion with JMP.

            //[55.24.8]
            String grossPremPayCodeForPercentBasisKey = "pm.agentmgr.saveNewAgent.grossPremPayCodeForPercentBasis"; // = Invalid Data. Gross Premium Pay Code is only available for PERCENT basis
            if (isCommPayCodeAvaiable) {
                if (insertedRecord.hasStringValue("ereCommPayCode") &&
                    COMM_PAY_CODE_GROSS_PREM.equalsIgnoreCase(insertedRecord.getStringValue("ereCommPayCode"))) {
                    if (!ereCommBasis.equalsIgnoreCase(COMM_BASIS_PERCENT)) {
                        mm.addErrorMessage(grossPremPayCodeForPercentBasisKey,new String[]{rowNum},
                            "ereCommPayCode", rowId);
                    }
                }
                if (insertedRecord.hasStringValue("renewalCommPayCode") &&
                    COMM_PAY_CODE_GROSS_PREM.equalsIgnoreCase(insertedRecord.getStringValue("renewalCommPayCode"))) {
                    if (!renewalCommBasis.equalsIgnoreCase(COMM_BASIS_PERCENT)) {
                        mm.addErrorMessage(grossPremPayCodeForPercentBasisKey,new String[]{rowNum},
                            "renewalCommPayCode", rowId);
                    }
                }
                if (insertedRecord.hasStringValue("newbusCommPayCode") &&
                    COMM_PAY_CODE_GROSS_PREM.equalsIgnoreCase(insertedRecord.getStringValue("newbusCommPayCode"))) {
                    if (!newbusCommBasis.equalsIgnoreCase(COMM_BASIS_PERCENT)) {
                        mm.addErrorMessage(grossPremPayCodeForPercentBasisKey,new String[]{rowNum},
                            "newbusCommPayCode", rowId);
                    }
                }
            } else {  // commPayCode is not available, remove the values. to match PB app
                insertedRecord.setFieldValue("ereCommPayCode","");
                insertedRecord.setFieldValue("renewalCommPayCode","");
                insertedRecord.setFieldValue("newbusCommPayCode","");
            }

            if (insertedRecord.hasField("producerAgentLicId") && insertedRecord.hasField("subProducerAgentLicId")) {
                String producerLicenseId = (String)insertedRecord.getFieldValue("producerAgentLicId");
                if (StringUtils.isBlank(producerLicenseId)) {
                    mm.addErrorMessage("core.validate.error.required", new String[]{"Producer", "\\n"},
                        "producerAgentLicId", rowId);
                } else if(summaryRecord.hasField("subProducerIdCounterForSave")) {
                    String hasSubproducerErrorOnSaveKey = "pm.agentmgr.saveNewAgent.validSubproducer";
                    String subProducerAgentLicId = (String)insertedRecord.getFieldValue("subProducerAgentLicId");
                    Record record = new Record();
                    record.setFieldValue("producerAgentLicId", producerLicenseId);
                    record.setFieldValue("subProducerAgentLicId", subProducerAgentLicId);
                    record.setFieldValue("subProducerCounter", summaryRecord.getFieldValue("subProducerIdCounterForSave"));
                    boolean isValidSubproducerOnSave = (getAgentDAO().isValidSubproducerOnSave(record)).booleanValue();
                    if (!isValidSubproducerOnSave) {
                        mm.addConfirmationPrompt(hasSubproducerErrorOnSaveKey,new String[]{rowNum},true);
                    }
                }
            }

            // stop validating the remaining records if we found problem(s) already
            if (mm.hasErrorMessages())
                failedRecordNumber = insertedRecord.getRecordNumber();
                break;
        }

        // if any error Messages stored, we throw validation exception here
        if (mm.hasErrorMessages()) {
            if (hasInsertedRecords){  // disable the Add option, since we have a "fresh" record pending for save
               inputRecords.getSummaryRecord().setFieldValue(AgentFields.IS_ADD_AVAILABLE,YesNoFlag.N);
            }
            ValidationException ve = new ValidationException("record did not pass validations.."+Integer.toString(failedRecordNumber));
            l.throwing(getClass().getName(), "record did not pass validations:"+Integer.toString(failedRecordNumber), ve);
            throw ve;
        }

        l.exiting(getClass().getName(), "validateAllPolicyAgent");
    }

    /**
     * Save all input records with UPDATE_IND set to 'Y' - updated, 'I' - inserted, or 'D' - deleted.
     *
     * @param policyHeader the summary policy information corresponding to the provided agents.
     * @param inputRecords a set of Records, each with the updated Agent Detail info
     *                     matching the fields returned from the loadAllAgent method.
     * @return the number of rows updated.
     */
    public int saveAllPolicyAgent(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllPolicyAgent", new Object[]{policyHeader, inputRecords});
        
        int updateCount = 0;

        // add policyId into summaryRecord, just in case DAO needs it to
        // determine if agentExistsForPolicy before it execute in batch mode
        Record summaryRecord = inputRecords.getSummaryRecord();
        summaryRecord.setFieldValue("policyId",policyHeader.getPolicyId());

        // insert into db in batch mode:.
        RecordSet insertedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.INSERTED));
        insertedRecords.setFieldsOnAll(policyHeader.toRecord(),false);
        updateCount = addAllPolicyAgent(insertedRecords);

        // update the db in batch mode:
        RecordSet updatedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));
        updatedRecords.setFieldsOnAll(policyHeader.toRecord(),false);
        updateCount += getAgentDAO().updateAllAgent(updatedRecords);

        l.exiting(getClass().getName(), "saveAllPolicyAgent", new Integer(updateCount));
        return updateCount;
    }

   /**
     * method to add new agents for a policy by
     * calling addAllAgentByReassign and (or) addAllAgentByAdjust:
     * The first agent should be added by "Adjust"
     * all subsequent agents should be added by "Reassign"
     *
     * @param inputRecords:
     * @return  int number of agents added.
     */
   protected int addAllPolicyAgent(RecordSet inputRecords) {
       Logger l = LogUtils.enterLog(getClass(), "addAllPolicyAgent", new Object[]{inputRecords});
       int updateCount = 0;

       // set the policyAgentId to null, because eOasis framework passes the id value as -3000,-3001..etc.
       // and database has problems with this not null artificial values for insert
       inputRecords.setFieldValueOnAll("policyAgentId","");

       // set changeType to the default N/A,suspendCommissionB to N so we do not have warning from the StoredProcdureDAOHelper
       inputRecords.setFieldValueOnAll("changeType","N/A");
       inputRecords.setFieldValueOnAll("suspendCommissionB","N");
       inputRecords.setFieldValueOnAll("calcCode","NEW");

       // there are 2 ways to insert the new Agents depending on if the policy has any agents already.
       Record summaryRecord = inputRecords.getSummaryRecord();
       boolean agentExistsForPolicy = false;
       if (summaryRecord.hasStringValue(AGENT_EXISTS_FOR_POLICY_FIELD_NAME)) {
           agentExistsForPolicy = summaryRecord.getBooleanValue(AGENT_EXISTS_FOR_POLICY_FIELD_NAME).booleanValue();
       }
       else {
           agentExistsForPolicy = getAgentDAO().agentExistsForPolicy(inputRecords.getSummaryRecord());
       }

       if (agentExistsForPolicy) {
           updateCount += getAgentDAO().addAllAgentByReassign(inputRecords);
       }
       else {
           // the policy does not have any agents.
           // so we have to insert the first agent with "Adjust" approach
           // and insert all subsequent agents (if any) with "Reassign" approach.
           // we might be over-doing this logic, since only 1 record is allowed to insert at a time currently,
           // but let us keep in sync with Validation: to be able to handle mutliple insert records
           int size = inputRecords.getSize();
           if (size > 0) {
               Record firstInsertRecord = inputRecords.getRecord(0);
               updateCount += getAgentDAO().addAllAgentByAdjust(inputRecords);
               if (size > 1) {
                   RecordSet restInsertedRecords = new RecordSet();
                   for (int j = 1; j <= size; j++) {
                       restInsertedRecords.addRecord(inputRecords.getRecord(j));
                   }
                   updateCount += getAgentDAO().addAllAgentByReassign(restInsertedRecords);
               }
           }
       }
       l.exiting(getClass().toString() + "addAllPolicyAgent", Integer.toString(updateCount));
       return updateCount;
   }

    /**
     * Method to get the assignment information for a CommRateScheudleId
     *
     * @param inputRecord a record containg field: commRateScheduleId
     * @return record
     */
    public Record getAssignmentForRateSchedule(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getAssignmentForRateSchedule", new Object[]{inputRecord});

        Record outRecord = new Record();
        outRecord = getAgentDAO().getAssignmentForRateSchedule(inputRecord).getFirstRecord();
        l.exiting(getClass().toString(), "getAssignmentForRateSchedule", outRecord);
        return outRecord;
    }

    /**
     * method to get the initial value when adding a agent
     *
     * @param inputRecord a record contains licenseClassCode(PRODUCER, SUB_PROD, COUNT_SIGN, AUTH_REP)
     *                    state code, policyTypeCode, policyEffDate, policyId
     * @return record
     */
    public Record getInitialValuesForAddPolicyAgent(PolicyHeader policyHeader, Record inputRecord) {

        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForAddPolicyAgent", new Object[]{inputRecord});
        Record outRecord = new Record();

        // get the default values from the workbench configuration for this page
        Record defaultValuesRecord = getWorkbenchConfiguration().getDefaultValues(MAINTAIN_POLICY_AGENT_ACTION_CLASS_NAME);

        //start with the default record
        outRecord.setFields(defaultValuesRecord);

        //overlay it with inputRecord
        outRecord.setFields(inputRecord);

        // default the start/end date per UC
        outRecord.setFieldValue("effectiveFromDate", policyHeader.getTermEffectiveFromDate());
        outRecord.setFieldValue("effectiveToDate", "01/01/3000");

        // get the initial pageEntitlement for a agent
        outRecord.setFields(AgentEntitlementRecordLoadProcessor.getInitialEntitlementValuesForAgent(policyHeader));

        l.exiting(getClass().toString(), "getInitialValuesForAddPolicyAgent", outRecord);
        return outRecord;
    }

    /**
     * set End date on each level
     *
     * @param inputRecord
     * @param payCommissionRecords
     * @param contractRecords
     * @param contractCommissionRecords
     */
    protected void setEndDate(Record inputRecord, RecordSet payCommissionRecords,
                              RecordSet contractRecords, RecordSet contractCommissionRecords) {
        Logger l = LogUtils.enterLog(getClass(), "setEndDate",
            new Object[]{inputRecord, payCommissionRecords, contractRecords, contractCommissionRecords});

        String effectiveEndDate = null;
        if (!inputRecord.hasStringValue(AgentFields.EFFECTIVE_END_DATE)) {
            AgentFields.setEffectiveEndDate(inputRecord, DEFAULT_END_DATE);
            effectiveEndDate = DEFAULT_END_DATE;
        } else {
            effectiveEndDate = AgentFields.getEffectiveEndDateString(inputRecord);
        }

        //set end date on pay commission grid
        Iterator insertIterator = payCommissionRecords.getRecords();
        while (insertIterator.hasNext()) {
            Record record = (Record) insertIterator.next();
            if (!record.hasStringValue(AgentFields.PAY_COMMISSION_EFFECTIVE_END_DATE)) {
                AgentFields.setPayCommissionEffectiveEndDate(record, effectiveEndDate);
                if (!UpdateIndicator.INSERTED.equals(record.getUpdateIndicator())) {
                    record.setUpdateIndicator(UpdateIndicator.UPDATED);
                }
            } else {
                String payCommissionEffectiveEndDate = AgentFields.getPayCommissionEffectiveEndDateString(record);
                if (DEFAULT_END_DATE.equals(payCommissionEffectiveEndDate)) {
                    AgentFields.setPayCommissionEffectiveEndDate(record, effectiveEndDate);
                    if (!UpdateIndicator.INSERTED.equals(record.getUpdateIndicator())) {
                        record.setUpdateIndicator(UpdateIndicator.UPDATED);
                    }
                }
            }
        }
        //set end date on contract grid
        insertIterator = contractRecords.getRecords();
        while (insertIterator.hasNext()) {
            Record record = (Record) insertIterator.next();
            if (!record.hasStringValue(AgentFields.CONTRACT_EFFECTIVE_END_DATE)) {
                AgentFields.setContractEffectiveEndDate(record, effectiveEndDate);
                if (!UpdateIndicator.INSERTED.equals(record.getUpdateIndicator())) {
                    record.setUpdateIndicator(UpdateIndicator.UPDATED);
                }
            }else {
                String contractEffectiveEndDate = AgentFields.getContractEffectiveEndDateString(record);
                if (DEFAULT_END_DATE.equals(contractEffectiveEndDate)) {
                    AgentFields.setContractEffectiveEndDate(record, effectiveEndDate);
                    if (!UpdateIndicator.INSERTED.equals(record.getUpdateIndicator())) {
                        record.setUpdateIndicator(UpdateIndicator.UPDATED);
                    }
                }
            }
            if (record.hasStringValue(AgentFields.CONTRACT_APPOINTMENT_START_DATE)) {
                if (!record.hasStringValue(AgentFields.CONTRACT_APPOINTMENT_END_DATE)) {
                    AgentFields.setAppointmentEndDate(record, effectiveEndDate);
                    if (!UpdateIndicator.INSERTED.equals(record.getUpdateIndicator())) {
                        record.setUpdateIndicator(UpdateIndicator.UPDATED);
                    }
                }
                else {
                    String appointmentEndDate = AgentFields.getAppointmentEndDateString(record);
                    if (DEFAULT_END_DATE.equals(appointmentEndDate)) {
                        AgentFields.setAppointmentEndDate(record, effectiveEndDate);
                        if (!UpdateIndicator.INSERTED.equals(record.getUpdateIndicator())) {
                            record.setUpdateIndicator(UpdateIndicator.UPDATED);
                        }
                    }
                }
            }
        }
        //set end date on contract commission grid
        insertIterator = contractCommissionRecords.getRecords();
        while (insertIterator.hasNext()) {
            Record record = (Record) insertIterator.next();
            if (!record.hasStringValue(AgentFields.CONTRACT_COMMISSION_EFFECTIVE_END_DATE)) {
                AgentFields.setContractCommissionEndDate(record, effectiveEndDate);
                if (!UpdateIndicator.INSERTED.equals(record.getUpdateIndicator())) {
                    record.setUpdateIndicator(UpdateIndicator.UPDATED);
                }
            }else {
                String contractCommissionEffectiveEndDate = AgentFields.getContractCommissionEndDateString(record);
                if (DEFAULT_END_DATE.equals(contractCommissionEffectiveEndDate)) {
                    AgentFields.setContractCommissionEndDate(record, effectiveEndDate);
                    if (!UpdateIndicator.INSERTED.equals(record.getUpdateIndicator())) {
                        record.setUpdateIndicator(UpdateIndicator.UPDATED);
                    }
                }
            }
        }
        l.exiting(getClass().getName(), "setEndDate");
    }


    /**
     * method to load the initial license information for a agent
     *
     * @param policyHeader: summary information about the policy
     * @param inputRecord a record contains licenseClassCode(PRODUCER, SUB_PROD, COUNT_SIGN, AUTH_REP)
     *                    state code, policyTypeCode, policyEffDate, policyId
     * @return record
     */

    public Record getInitialValuesForPolicyAgent(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getInitalValuesForAgent", new Object[]{inputRecord});

        // overwrite with policyHeader
        inputRecord.setFields(policyHeader.toRecord());

        // make sure it contains the valid LicenseClassCode before calling DAO,
        // if not, a AppEception is thrown
        validateLicenseClassCode(inputRecord);

        // apply business rule to change the policyTermEffectiveDate conditionally;
        setTermEffectiveFromDateForAgentLicenseRetrieval(inputRecord);

        // call DAO and get the initial values for the given agent
        Record initRecord = getAgentDAO().getInitalValuesForAgent(inputRecord);

        Record outputRecord = new Record();

        // for Producer, let us clear some null values, so they do not override the wb configuration values
        if (inputRecord.getStringValue(LICENSE_CLASS_CODE_FIELD).equalsIgnoreCase(LICENSE_CLASS_CODE_PRODUCER)) {
            if (StringUtils.isBlank(initRecord.getStringValue("ereCommBasis"))) {
                initRecord.remove("ereCommBasis");
            }
            else {
                // commission limit is defaulted only if commissionbasis is PERCENT
                if (!initRecord.getStringValue("ereCommBasis").equalsIgnoreCase(COMM_BASIS_PERCENT)) {
                    initRecord.setFieldValue("ereCommLimit", "");
                }
            }

            if (StringUtils.isBlank(initRecord.getStringValue("renewalCommBasis"))) {
                initRecord.remove("renewalCommBasis");
            }
            else {
                // commission limit is defaulted only if commissionbasis is PERCENT
                if (!initRecord.getStringValue("renewalCommBasis").equalsIgnoreCase(COMM_BASIS_PERCENT)) {
                    initRecord.setFieldValue("renewalCommLimit", "");
                }
            }

            if (StringUtils.isBlank(initRecord.getStringValue("newbusCommBasis"))) {
                initRecord.remove("newbusCommBasis");
            }
            else {
                // commission limit is defaulted only if commissionbasis is PERCENT
                if (!initRecord.getStringValue("newbusCommBasis").equalsIgnoreCase(COMM_BASIS_PERCENT)) {
                    initRecord.setFieldValue("newbusCommLimit", "");
                }
            }
             // for producer, it gets "all" available fields, others will just get classCode-specific values
            outputRecord.setFields(initRecord);
        }

        Record classCodeSpecificRecord = createDefaultsForClassCode(initRecord);

        // overlay classCodeSpecificRecord on top of outputRecord for return
        outputRecord.setFields(classCodeSpecificRecord,true);

        l.exiting(getClass().toString(), "getInitalValuesForAgent", outputRecord);
        return outputRecord;
    }

    private Record createDefaultsForClassCode(Record record) {
        Logger l = LogUtils.enterLog(getClass(), "createDefaultsForClassCode", new Object[]{record});

        String producer = "producer";
        String contersigner = "countersigner";
        String authorizedrep = "authorizedrep";
        String subproducer = "subproducer";
        String fieldPrefix = "";
        String licenseClassCodeUpper = record.getStringValue(LICENSE_CLASS_CODE_FIELD).toUpperCase();

        if (licenseClassCodeUpper.equalsIgnoreCase(LICENSE_CLASS_CODE_PRODUCER)) {
            fieldPrefix = producer;
        }
        else {
            if (licenseClassCodeUpper.equalsIgnoreCase(LICENSE_CLASS_CODE_SUBPRODUCER)) {
                fieldPrefix = subproducer;
            }
            else {
                if (licenseClassCodeUpper.equalsIgnoreCase(LICENSE_CLASS_CODE_COUNTER_SIGNER)) {
                    fieldPrefix = contersigner;
                }
                else {
                    if (licenseClassCodeUpper.equalsIgnoreCase(LICENSE_CLASS_CODE_AUTHORIZED_REP)) {
                        fieldPrefix = authorizedrep;
                    }
                }
            }
        }
        Record outRecord = new Record();
        outRecord.setFieldValue(fieldPrefix + "LicNo", record.getFieldValue("licenseNo"));
        outRecord.setFieldValue(fieldPrefix + "AgentName", record.getFieldValue("agentName"));
        outRecord.setFieldValue(fieldPrefix + "LicTypeDesc", record.getFieldValue("licenseTypeDesc"));

        l.exiting(getClass().getName(), "createDefaultsForClassCode");
        return outRecord;
    }

    /**
     * method to validate the inputRecord:
     * to make sure it contains a field: licenseClassCode,
     * and the value for the field is one of the followings: (case insensitive.)
     * PRODUCER, SUB_PROD, COUNT_SIGN, AUTH_REP
     * <p/>
     * if found, it will set the value for the field in uppercase.
     * if not found, a validationException is thrown.
     * This field is set by js when user changes one of the 4 drop down fields
     * on the Producer Agent Entry form
     * The correct value for this field is essential for us to get the right set of values back
     * from db
     *
     * @param inputRecord a record to be validate
     */
    private void validateLicenseClassCode(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validateLicenseClassCode", new Object[]{inputRecord});
        String licenseClassCodeUpper = "";
        if (inputRecord.hasStringValue(LICENSE_CLASS_CODE_FIELD)) {
            licenseClassCodeUpper = inputRecord.getStringValue(LICENSE_CLASS_CODE_FIELD).toUpperCase();
        }

        if (!licenseClassCodeUpper.equalsIgnoreCase(LICENSE_CLASS_CODE_PRODUCER) &&
            !licenseClassCodeUpper.equalsIgnoreCase(LICENSE_CLASS_CODE_SUBPRODUCER) &&
            !licenseClassCodeUpper.equalsIgnoreCase(LICENSE_CLASS_CODE_COUNTER_SIGNER) &&
            !licenseClassCodeUpper.equalsIgnoreCase(LICENSE_CLASS_CODE_AUTHORIZED_REP)) {
            l.severe("licenseClassCode is not set prior to retrieving agent defaults");
            AppException ae = new AppException("Invalid licenseClassCode used to retrieve Agent defaults:" + licenseClassCodeUpper);
            throw ae;
        }
        else {
            inputRecord.setFieldValue(LICENSE_CLASS_CODE_FIELD, licenseClassCodeUpper);
        }

        l.exiting(getClass().toString(), "validateLicenseClassCode", licenseClassCodeUpper);
    }

    private void setTermEffectiveFromDateForAgentLicenseRetrieval(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getPolicyEffectiveDateForAgentLicenseRetrieval", new Object[]{inputRecord});

        String usePthEff = SysParmProvider.getInstance().getSysParm("FM_AGENT_USE_PTH_EFF", "N");
        String policyEffDate = inputRecord.getStringValue("termEffectiveFromDate");
        String today = DateUtils.formatDate(new Date());
        try {
            if (usePthEff.equalsIgnoreCase("N")) {
                if (DateUtils.daysDiff(policyEffDate, today) > 0) {
                    policyEffDate = today;
                }
            }
        }
        catch (ParseException pe) {
            l.severe("policyEffDate is not set prior to retrieving agent defaults");
            AppException ae = new AppException(pe.toString());
            throw ae;
        }

        inputRecord.setFieldValue("termEffectiveFromDate",policyEffDate);
        l.exiting(getClass().toString(), "setTermEffectiveFromDateForAgentLicenseRetrieval", policyEffDate);
    }

    /**
     * validate the producer exists or not.
     * @param inputRecord
     */
    public void validateLicensedAgent(Record inputRecord, PolicyHeader policyHeader) {
        Logger l = LogUtils.enterLog(getClass(), "validateLicensedAgent", new Object[]{inputRecord});
        boolean isProducerExists = getAgentDAO().isLicensedAgent(inputRecord);
        MessageManager mm = MessageManager.getInstance();
        if (!isProducerExists) {
            mm.addErrorMessage("pm.agentmgr.saveNewAgent.producer.not.exists", new String[]{policyHeader.getPolicyNo()});
            AppException ae = new AppException("The provided agent does not exist in the system.");
            l.throwing(getClass().getName(), "record did not pass validations:", ae);
            throw ae;
        }
        l.exiting(getClass().toString(), "validateLicensedAgent");
    }
    
    public void verifyConfig() {
        if (getAgentDAO() == null)
            throw new ConfigurationException("The required property 'agentDAO' is missing.");

        if (getWorkbenchConfiguration() == null)
            throw new ConfigurationException("The required property 'workbenchConfiguration' is missing.");
    }

    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return m_workbenchConfiguration;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration) {
        m_workbenchConfiguration = workbenchConfiguration;
    }

    public AgentDAO getAgentDAO() {
        return m_agentDAO;
    }

    public void setAgentDAO(AgentDAO agentDAO) {
        m_agentDAO = agentDAO;
    }


    public DBUtilityManager getDbUtilityManager() {
        return m_dbUtilityManager;
    }

    public void setDbUtilityManager(DBUtilityManager dbUtilityManager) {
        m_dbUtilityManager = dbUtilityManager;
    }

    private DBUtilityManager m_dbUtilityManager;
    private WorkbenchConfiguration m_workbenchConfiguration;
    private AgentDAO m_agentDAO;

    private static final String MAINTAIN_POLICY_AGENT_ACTION_CLASS_NAME = "dti.pm.agentmgr.struts.MaintainPolicyAgentAction";
    private static final String LICENSE_CLASS_CODE_FIELD = "licenseClassCode";
    private static final String LICENSE_CLASS_CODE_PRODUCER = "PRODUCER";
    private static final String LICENSE_CLASS_CODE_SUBPRODUCER = "SUB_PROD";
    private static final String LICENSE_CLASS_CODE_COUNTER_SIGNER = "COUNT_SIGN";
    private static final String LICENSE_CLASS_CODE_AUTHORIZED_REP = "AUTH_REP";

    private static final String COMM_BASIS_PERCENT = "PERCENT";
    private static final String COMM_BASIS_SCHEDULE = "SCHED";
    private static final String COMM_BASIS_FLAT = "FLAT";
    private static final String AGENT_EXISTS_FOR_POLICY_FIELD_NAME = "agentExistsForPolicy";
    private static final String COMM_PAY_CODE_GROSS_PREM = "GROSS_PREM";

    private static final String DEFAULT_END_DATE = "01/01/3000";
}
