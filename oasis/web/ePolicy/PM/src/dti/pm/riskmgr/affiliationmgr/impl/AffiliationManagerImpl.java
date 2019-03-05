package dti.pm.riskmgr.affiliationmgr.impl;

import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.pm.busobjs.PMRecordSetHelper;
import dti.pm.busobjs.SysParmIds;
import dti.pm.busobjs.TransactionCode;
import dti.pm.busobjs.TransactionStatus;
import dti.cs.data.dbutility.DBUtilityManager;
import dti.pm.entitymgr.EntityFields;
import dti.pm.entitymgr.EntityManager;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.Term;
import dti.pm.riskmgr.RiskFields;
import dti.pm.riskmgr.affiliationmgr.AffiliationFields;
import dti.pm.riskmgr.affiliationmgr.AffiliationManager;
import dti.pm.riskmgr.affiliationmgr.dao.AffiliationDAO;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.transactionmgr.transaction.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Class provides the implementation details of AffiliaionManager Interface.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 21, 2008
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/01/2012       xnie        132114: 1) Modified getInitialValuesForAffiliation() to set correct default affiliation
 *                                         expiring date.
 *                                      2) Modified saveAllAffiliation() to replace riskEffectiveToDate with
 *                                         affiliationRiskExpDate for saving.
 *                                      3) Modified validateAllAffiliationForSaving() to replace riskEffectiveToDate
 *                                         with affiliationRiskExpDate for validation.
 * 10/18/12         fcb         137704 - loadAllAffiliation: logic to get and set latestTermExpDate.
 * 11/20/2012       awu         138585 - Modified getInitialValuesForAffiliation() to remove the hard coded line for percent Practice
*  10/31/2017       lzhang      188425 - Add validatePractPercForOOSENonInitTerms() and modified saveAllAffiliation(),
*                                        validateAllAffiliationForSaving() and validateOverLapForAffiliation()
*                                        to support overlap practice percent for OOSE non-initial terms validation
 * 12/07/2017       lzhang      182769 - Modified getInitialValuesForAffiliation to pass policyHeader
 *                                       when invoke AffiliationEntitlementRecordLoadProcessor
 * 01/26/2018       xnie        191085 - Modified saveAllAffiliation to thorw validation error to action.
 * ---------------------------------------------------
 */
public class AffiliationManagerImpl implements AffiliationManager {

    /**
     * validate affiliation copy
     *
     * @param inputRecords
     * @return validate status code statusCode
     */
    public String validateCopyAllAffiliation(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateCopyAllAffiliation", new Object[]{inputRecords});
        }
        Record inputRecord = generateInputRecordForCopyAll(inputRecords);
        String valStatus = getAffiliationDAO().validateCopyAllAffiliation(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateCopyAllAffiliation", valStatus);
        }
        return valStatus;

    }


    /**
     * copy all affliation data to target risk
     *
     * @param inputRecords
     */
    public void copyAllAffiliation(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "copyAllAffiliation", new Object[]{inputRecords});
        }
        Record inputRecord = generateInputRecordForCopyAll(inputRecords);
        getAffiliationDAO().copyAllAffiliation(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "copyAllAffiliation");
        }
    }

    /**
     * construct inputRecord by recordSet for copy all affiliation
     *
     * @param inputRecords
     * @return
     */
    private Record generateInputRecordForCopyAll(RecordSet inputRecords) {
        Iterator affiIter = inputRecords.getRecords();
        int affiNum = 0;
        Record affiRec = null;
        StringBuffer affiPkBuff = new StringBuffer();
        while (affiIter.hasNext()) {
            affiRec = (Record) affiIter.next();
            String affiPk = affiRec.getStringValue("entityRelationId");
            if (affiPkBuff.length() != 0)
                affiPkBuff.append(",");
            affiPkBuff.append(affiPk);
            affiNum++;
        }
        Record parmsRec = new Record();
        parmsRec.setFields(affiRec);
        parmsRec.setFieldValue("affiPks", affiPkBuff.toString());
        parmsRec.setFieldValue("numAffi", String.valueOf(affiNum));
        return parmsRec;
    }

    /**
     * load all affiliations for copy all risk
     *
     * @param policyHeader
     * @param inputRecord
     * @return taget risks recordset
     */
    public RecordSet loadAllAffiliation(PolicyHeader policyHeader, Record inputRecord, RecordLoadProcessor processor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAffiliation", new Object[]{policyHeader, inputRecord});
        }
        inputRecord.setFields(policyHeader.toRecord(), false);
        Record record = new Record();
        record.setFieldValue("policyId", policyHeader.getPolicyId());
        record.setFieldValue("termEffectiveFromDate", policyHeader.getTermEffectiveFromDate());
        record.setFieldValue("termEffectiveToDate", policyHeader.getTermEffectiveToDate());
        record.setFieldValue("riskEntityId", inputRecord.getFieldValue("riskEntityId"));
        RecordSet rs = getAffiliationDAO().loadAllAffiliation(record, processor);
        // Calculate dates
        record.setFieldValue("transactionCode", policyHeader.getLastTransactionInfo().getTransactionCode());
        record.setFieldValue("riskId", inputRecord.getFieldValue("riskId"));
        record.setFieldValue("transEffectiveFromDate", policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        record.setFieldValue("latestTermExpDate", getLatestTermExpDate(policyHeader));

        Record dateRecord = getAffiliationDAO().calculateDateForAffiliation(record);

        Record summaryRecord = rs.getSummaryRecord();
        summaryRecord.setFieldValue(RiskFields.RISK_NAME, policyHeader.getRiskHeader().getRiskName());
        summaryRecord.setFields(dateRecord);
        EntityFields.setEntityId(summaryRecord, policyHeader.getRiskHeader().getRiskEntityId());

        String needToCaptureTransaction = "Y";
        if (policyHeader.isWipB()) {
            needToCaptureTransaction = "N";
        }
        summaryRecord.setFieldValue("needToCaptureTransaction", needToCaptureTransaction);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAffiliation", rs);
        }
        return rs;
    }

    /**
     * Get the latest term expiration date.
     *
     * @param policyHeader
     * @return
     */
    private String getLatestTermExpDate(PolicyHeader policyHeader) {
        String latestTermExpDateStr = "";
        Iterator iterator = policyHeader.getPolicyTerms();
        if (iterator.hasNext()) {
            Term lastTerm = (Term) iterator.next();
            latestTermExpDateStr = lastTerm.getEffectiveToDate();
        }
        return latestTermExpDateStr;
    }

    /**
     * load all affiliations for copy all risk
     *
     * @param policyHeader
     * @param inputRecord
     * @return taget risks recordset
     */
    public RecordSet loadAllAffiliation(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAffiliation", new Object[]{policyHeader, inputRecord});
        }

        RecordLoadProcessor processor = new AffiliationEntitlementRecordLoadProcessor(policyHeader);
        RecordSet rs = loadAllAffiliation(policyHeader, inputRecord, processor);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAffiliation", rs);
        }
        return rs;
    }

    /**
     * Get initial values for affiliation
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a record loaded with user entered data
     * @return Record a Record loaded with initial values.
     */
    public Record getInitialValuesForAffiliation(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForAffiliation", new Object[]{policyHeader, inputRecord});
        }

        Record output = getWorkbenchConfiguration().getDefaultValues(MAINTAIN_AFFILIATION_ACTION_CLASS_NAME);
        if (policyHeader.isWipB()) {
            AffiliationFields.setEffDate(output, policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        }
        else {
            AffiliationFields.setEffDate(output, inputRecord.getStringValue("affiliationStartDate"));
        }
        AffiliationFields.setExpDate(output, inputRecord.getStringValue("affiliationRiskExpDate"));
        AffiliationFields.setRelationTypeCode(output, "AW");
        Long entityRelationId = getDbUtilityManager().getNextSequenceNo();
        AffiliationFields.setEntityRelationId(output, entityRelationId.toString());
        AffiliationFields.setEntityParentId(output, inputRecord.getStringValue("entityParentId"));
        AffiliationFields.setEntityChildId(output, inputRecord.getStringValue("entityChildId"));
        AffiliationFields.setOrganizationName(output, getEntityManager().getEntityName(inputRecord.getStringValue("entityParentId")));
        // Set the initial affiliation Entitlement values
        AffiliationEntitlementRecordLoadProcessor.setInitialEntitlementValuesForAffiliation(policyHeader, output);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForAffiliation", output);
        }
        return output;
    }

    /**
     * Save all inserted/updated affiliation records.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecords a set of Records, each with the updated affiliation info
     */
    public void saveAllAffiliation(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "SaveAllAffiliation", new Object[]{policyHeader, inputRecords});
        int updateCount = 0;

        // Create an new RecordSet to include all added and modified records
        RecordSet modifiedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter
            (new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));

        // If a change has occurred to affiliation data - validate, create/get a trans and save
        if (modifiedRecords.getSize() > 0) {

            // Validate Affiliation Date
            validateAllAffiliationForSaving(policyHeader, inputRecords);

            Transaction trans;
            String transactionLogId;
            boolean isTransCreated = false;
            String failedTerms ="";
            Record inputRecord = inputRecords.getSummaryRecord();
            // If existed a transaction in progress, make use of it
            String policyTransLogId = policyHeader.getLastTransactionInfo().getTransactionLogId();
            if (policyHeader.isWipB()) {
                transactionLogId = policyTransLogId;
                trans = new Transaction();
                trans.setTransactionLogId(transactionLogId);
            }
            // If the policy doesn't have a transaction in progress, an ENDAFFILIA transaction code is created.
            else {
                // For issue 98209. When create transaction for ENDAFFILIA, the effectiveFromDate will be checked again.
                if(inputRecord.hasStringValue("newTransactionEffectiveFromDate")){
                   inputRecord.setFieldValue("effectiveFromDate", inputRecord.getStringValue("newTransactionEffectiveFromDate"));
                }
                trans = getTransactionManager().createTransaction(policyHeader,
                    inputRecord,
                    inputRecord.getStringValue("newTransactionEffectiveFromDate"),
                    TransactionCode.ENDAFFILIA,
                    false);
                transactionLogId = trans.getTransactionLogId();
                isTransCreated = true;
            }

            modifiedRecords.setFieldValueOnAll(TransactionFields.TRANSACTION_LOG_ID, transactionLogId);
            modifiedRecords.setFieldValueOnAll("riskEffectiveToDate", inputRecords.getSummaryRecord().getFieldValue("affiliationRiskExpDate"));
            modifiedRecords.setFieldValueOnAll("policyId", policyHeader.getPolicyId());
            //set RowStatus On ModifiedRecords
            PMRecordSetHelper.setRowStatusOnModifiedRecords(modifiedRecords);

            try {
                updateCount = getAffiliationDAO().saveAllAffiliation(modifiedRecords);
                if(DateUtils.parseDate(AffiliationFields.getMaxAffExpDate(inputRecords.getSummaryRecord()))
                    .after(DateUtils.parseDate(policyHeader.getTermEffectiveToDate()))){
                    // Validate PracticePercent for OOSE non-initial terms
                    failedTerms = validatePractPercForOOSENonInitTerms(policyHeader, inputRecords);
                }
                String failedTermsForCurrentTerm = AffiliationFields.getFailedTermsForPractPerc(inputRecords.getSummaryRecord());
                String totalFailedTerms = "";
                if(!StringUtils.isBlank(failedTerms)){
                    if (!StringUtils.isBlank(failedTermsForCurrentTerm)){
                        totalFailedTerms = failedTermsForCurrentTerm + "," + failedTerms;
                    }else{
                        totalFailedTerms = failedTerms;
                    }
                }else{
                    totalFailedTerms = failedTermsForCurrentTerm;
                }
                if(!StringUtils.isBlank(totalFailedTerms)){
                    MessageManager.getInstance().addErrorMessage("pm.maintainAffiliation.overLapForTotalPractice.error",
                        new String[]{totalFailedTerms});
                    throw new ValidationException("Percent of practice for same time period cannot total over 100%.");
                }
                // If no Existing Transaction
                if (isTransCreated) {
                    // Update the transaction status to OUTPUT
                    getTransactionManager().UpdateTransactionStatusNoLock(trans, TransactionStatus.OUTPUT);
                    // Process output (PM Call to Output)
                    Record record = new Record();
                    record.setFields(policyHeader.toRecord());
                    getTransactionManager().processOutput(record, false);

                    // Update the transaction status to complete
                    getTransactionManager().UpdateTransactionStatusNoLock(trans, TransactionStatus.COMPLETE);

                }
            }
            catch (ValidationException ve) {
                //if there is validation exception throw it
                throw ve;
            }
            catch (Exception ex) {
                // If save failed, roll back all changes and delete wip if transaction is created by the page.
                if (isTransCreated) {
                    getTransactionManager().deleteWipTransaction(policyHeader, inputRecord);
                }
                // Throw the exception
                throw ExceptionHelper.getInstance().handleException("Failed to save affiliation.", ex);
            }
        }

        l.exiting(getClass().getName(), "saveAllAffiliation", new Integer(updateCount));
    }

    /**
     * Validate affiliation data for saving
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecords a set of Records, each with the updated Affiliation Detail info
     */
    protected void validateAllAffiliationForSaving(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAllAffiliationForSaving", new Object[]{policyHeader, inputRecords});
        }
        //get transaction effective date
        Date transactionEffectiveDate = null;
        if (policyHeader.isWipB()) {
            transactionEffectiveDate = DateUtils.parseDate(policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        }
        else {
            transactionEffectiveDate = DateUtils.parseDate(inputRecords.getSummaryRecord().getStringValue("newTransactionEffectiveFromDate"));
        }
        String affiValParam = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_VAL_AFFI_DATES);
        RecordSet changedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));

        Date currentTermEffDate = DateUtils.parseDate(policyHeader.getTermEffectiveFromDate());
        Date currentTermExpDate = DateUtils.parseDate(policyHeader.getTermEffectiveToDate());
        String entityChildId ="";
        Date riskEffDate = DateUtils.parseDate(inputRecords.getSummaryRecord().getStringValue("riskEffectiveFromDate"));
        Date riskExpDate = DateUtils.parseDate(inputRecords.getSummaryRecord().getStringValue("affiliationRiskExpDate"));
        Date maxAffExpDate = currentTermExpDate;
        Iterator it = changedRecords.getRecords();
        while (it.hasNext()) {
            Record r = (Record) it.next();
            String rowNum = String.valueOf(r.getRecordNumber() + 1);
            String rowId = AffiliationFields.getEntityRelationId(r);
            Date affiliationStartDate = DateUtils.parseDate(AffiliationFields.getEffDate(r));
            Date affiliationEndDate = DateUtils.parseDate(AffiliationFields.getExpDate(r));

            String primaryB = AffiliationFields.getPrimaryAffiliationB(r);
            String vapB = AffiliationFields.getVapB(r);
            String updateInd = r.getUpdateIndicator();
            double percentPractice = new Double(AffiliationFields.getPercentPractice(r)).doubleValue();

            if ("Y".equals(affiValParam)) {
                //validation #1
                if (updateInd.equals(UpdateIndicator.INSERTED) || ((updateInd.equals(UpdateIndicator.UPDATED)) && (!affiliationStartDate.equals(DateUtils.parseDate(AffiliationFields.getOrigEffDate(r)))))) {
                    if (!affiliationStartDate.equals(transactionEffectiveDate)) {
                        MessageManager.getInstance().addErrorMessage("pm.maintainAffiliation.startDateTransDateMatch.error",
                            new String[]{rowNum}, AffiliationFields.EFF_DATE, rowId);
                    }
                }

                if ((updateInd.equals(UpdateIndicator.UPDATED)) && (!affiliationEndDate.equals(DateUtils.parseDate(AffiliationFields.getOrigExpDate(r))))) {
                    if (!affiliationEndDate.equals(transactionEffectiveDate)) {
                        MessageManager.getInstance().addErrorMessage("pm.maintainAffiliation.endDateTransDateMatch.error",
                            new String[]{rowNum}, AffiliationFields.EXP_DATE, rowId);
                    }
                }

            }

            // Validation #3: Start and End date of affiliation must be within the risk effective date period
            if (affiliationStartDate.before(riskEffDate) || affiliationEndDate.after(riskExpDate)) {
                MessageManager.getInstance().addErrorMessage("pm.maintainAffiliation.dateOutsideTermDates.error",
                    new String[]{rowNum}, AffiliationFields.EXP_DATE, rowId);

            }
            //Validation #4: transaction eff date should between Start Date and End Date
            if ((transactionEffectiveDate.before(affiliationStartDate)) || (transactionEffectiveDate.after(affiliationEndDate))) {
                MessageManager.getInstance().addErrorMessage("pm.maintainAffiliation.invalidTransactionDate.error",
                    new String[]{rowNum}, null, rowId);
            }
            // Validation #5: End Date cannot be before Start Date
            if (affiliationEndDate.before(affiliationStartDate)) {
                MessageManager.getInstance().addErrorMessage("pm.maintainAffiliation.invalidEndDate.error",
                    new String[]{rowNum}, AffiliationFields.EXP_DATE, rowId);
            }
            //validation #7: Non-primary affilition has vap selected
            if (!(primaryB.equals("Y")) && (vapB.equals("Y"))) {
                MessageManager.getInstance().addErrorMessage("pm.maintainAffiliation.invalidVapStatus.error",
                    new String[]{rowNum}, AffiliationFields.VAP_B, rowId);
            }
            //validation #8: practice percent more than 100
            if (percentPractice > 100) {
                MessageManager.getInstance().addErrorMessage("pm.maintainAffiliation.invalidPercentPractice.error",
                    new String[]{rowNum}, AffiliationFields.PERCENT_PRACTICE, rowId);
            }

            if (maxAffExpDate.before(affiliationEndDate)) {
                maxAffExpDate = affiliationEndDate;
            }

            if (StringUtils.isBlank(entityChildId)){
                entityChildId = AffiliationFields.getEntityChildId(r);
            }
        }
        AffiliationFields.setEntityChildId(inputRecords.getSummaryRecord(), entityChildId);
        AffiliationFields.setMaxAffExpDate(inputRecords.getSummaryRecord(), DateUtils.formatDate(maxAffExpDate));
        Iterator termsItr = policyHeader.getPolicyTerms();
        String termStr = "";
        String currentTermBaseRecordId = policyHeader.getTermBaseRecordId();
        while (termsItr.hasNext()) {
            Term term = (Term) termsItr.next();
            Date termEffDate = DateUtils.parseDate(term.getEffectiveFromDate());
            Date termExpDate = DateUtils.parseDate(term.getEffectiveToDate());
            if (!term.getTermBaseRecordId().equals(currentTermBaseRecordId)
                && currentTermEffDate.before(termExpDate)
                && termEffDate.before(maxAffExpDate)) {
                if (StringUtils.isBlank(termStr)) {
                    termStr = term.getTermBaseRecordId();
                }
                else {
                    termStr = termStr + "," + term.getTermBaseRecordId();
                }
            }
        }
        AffiliationFields.setPolicyTermBaseRecordIds(inputRecords.getSummaryRecord(), termStr);
        //validation #6,#9,#10,#11,#12
        validateOverLapForAffiliation(policyHeader, inputRecords);
        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid Affiliation data.");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateAllAffiliationForSaving");
        }
    }

    //overlap validation for affiliation(including #6,#9,#10,#11,#12)
    private void validateOverLapForAffiliation(PolicyHeader policyHeader, RecordSet inputRecords) {

        //construct a sorted date set
        SortedSet dateSet = new TreeSet();
        Iterator itor = inputRecords.getRecords();
        while (itor.hasNext()) {
            Record record = (Record) itor.next();
            Date effDate = DateUtils.parseDate(AffiliationFields.getEffDate(record));
            Date expDate = DateUtils.parseDate(AffiliationFields.getExpDate(record));
            dateSet.add(effDate);
            dateSet.add(expDate);
        }
        //dateArray is a sorted date array .
        Date[] dateArray = (Date[]) (dateSet.toArray(new Date[0]));
        List rsList = new ArrayList();
        for (int i = 0; i < dateArray.length - 1; i++) {
            RecordSet rs = new RecordSet();
            Record summaryRecord = rs.getSummaryRecord();
            summaryRecord.setFieldValue("beginDate", dateArray[i]);
            summaryRecord.setFieldValue("endDate", dateArray[i + 1]);
            itor = inputRecords.getRecords();
            while (itor.hasNext()) {
                Record record = (Record) itor.next();
                Date effDate = DateUtils.parseDate(AffiliationFields.getEffDate(record));
                Date expDate = DateUtils.parseDate(AffiliationFields.getExpDate(record));
                //if this record contains the time period
                if ((!effDate.after(dateArray[i])) && (!expDate.before(dateArray[i + 1]))) {
                    rs.addRecord(record);
                }
            }
            rsList.add(rs);
        }
        //loop per time period
        itor = rsList.iterator();
        String failedTermsForPractPerc ="";
        while (itor.hasNext()) {
            RecordSet rs = (RecordSet) itor.next();
            if (rs.getSize() > 1) {
                Iterator rsItor = rs.getRecords();
                Set set = new HashSet();
                double totalPracticePercent = 0;
                int awpurchgrpTypeCount = 0;
                int primaryAffiliationCount = 0;
                int vapCount = 0;
                while (rsItor.hasNext()) {
                    Record record = (Record) rsItor.next();
                    String entityParentId = AffiliationFields.getEntityParentId(record);
                    String relationTypeCode = AffiliationFields.getRelationTypeCode(record);
                    String affiliationType = AffiliationFields.getAddlRelationTypeCode(record);
                    String pencentPractice = AffiliationFields.getPercentPractice(record);
                    String primaryAffiliationB = AffiliationFields.getPrimaryAffiliationB(record);
                    String vapB = AffiliationFields.getVapB(record);
                    String key = entityParentId + affiliationType;
                    //validate #6: overlap between affiliations with same affilation type and organization
                    if (set.contains(key)) {
                        MessageManager.getInstance().addErrorMessage("pm.maintainAffiliation.overLapForSameOrgAndAffiType.error");
                    }
                    else {
                        set.add(key);
                    }
                    totalPracticePercent += new Double(pencentPractice).doubleValue();
                    if (("AWPURCHGRP").equals(relationTypeCode)) {
                        awpurchgrpTypeCount++;
                    }
                    if (("Y").equals(primaryAffiliationB)) {
                        primaryAffiliationCount++;
                    }
                    if (("Y").equals(vapB)) {
                        vapCount++;
                    }
                }
                //validation #9: Percent of practice for same time period cannot total over 100%.
                if (totalPracticePercent > 100) {
                    failedTermsForPractPerc = policyHeader.getTermEffectiveFromDate() + '-' + policyHeader.getTermEffectiveToDate();
                }
                //validation #10: Only one affiliation with purchasing group allowed during overlap time range
                if (awpurchgrpTypeCount > 1) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainAffiliation.overLapForAwpurchgrpTypeCount.error");
                }
                //validation #11: Only one primary affiliation allowed during overlap time range. 
                if (primaryAffiliationCount > 1) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainAffiliation.overLapForPrimaryAffiliationCount.error");
                }
                //validation #12: Only one VAP allowed during overlap time range
                if (vapCount > 1) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainAffiliation.overLapForVapCount.error");
                }
            }
        }
        AffiliationFields.setFailedTermsForPractPerc(inputRecords.getSummaryRecord(), failedTermsForPractPerc);
    }

    /**
     * Validate PracticePercent for OOSE non-initial terms
     *
     * @param policyHeader policy header that contains all key policy information.
     */
    protected String validatePractPercForOOSENonInitTerms(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validatePractPercForOOSENonInitTerms", new Object[]{policyHeader, inputRecords});
        }

        String failedTerms ="";
        Record record = new Record();
        AffiliationFields.setEntityChildId(record, AffiliationFields.getEntityChildId(inputRecords.getSummaryRecord()));
        AffiliationFields.setTermEffectiveToDate(record, policyHeader.getTermEffectiveToDate());
        AffiliationFields.setMaxAffExpDate(record, AffiliationFields.getMaxAffExpDate(inputRecords.getSummaryRecord()));
        AffiliationFields.setPolicyTermBaseRecordIds(record, AffiliationFields.getPolicyTermBaseRecordIds(inputRecords.getSummaryRecord()));
        failedTerms = getAffiliationDAO().validatePractPercForOOSENonInitTerms(record);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validatePractPercForOOSENonInitTerms");
        }

        return failedTerms;
    }

    public AffiliationDAO getAffiliationDAO() {
        return m_affiliationDAO;
    }

    public void setAffiliationDAO(AffiliationDAO affiliationDAO) {
        m_affiliationDAO = affiliationDAO;
    }

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
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

    public EntityManager getEntityManager() {
        return m_entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        m_entityManager = entityManager;
    }

    public void verifyConfig() {
        if (getDbUtilityManager() == null)
            throw new ConfigurationException("The required property 'dbUtilityManager' is missing.");
        if (getEntityManager() == null)
            throw new ConfigurationException("The required property 'entityManager' is missing.");
        if (getAffiliationDAO() == null)
            throw new ConfigurationException("The required property 'affiliationDAO' is missing.");
        if (getTransactionManager() == null)
            throw new ConfigurationException("The required property 'transactionManager' is missing.");
        if (getWorkbenchConfiguration() == null)
            throw new ConfigurationException("The required property 'workbenchConfiguration' is missing.");
    }

    private DBUtilityManager m_dbUtilityManager;
    private WorkbenchConfiguration m_workbenchConfiguration;
    private AffiliationDAO m_affiliationDAO;
    private TransactionManager m_transactionManager;
    private EntityManager m_entityManager;
    protected static final String MAINTAIN_AFFILIATION_ACTION_CLASS_NAME = "dti.pm.riskmgr.affiliationmgr.struts.MaintainAffiliationAction";

}
