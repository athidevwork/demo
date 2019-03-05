package dti.pm.policymgr.additionalinsuredmgr.impl;

import dti.oasis.recordset.RecordLoadProcessorChainManager;
import dti.oasis.util.FormatUtils;
import dti.pm.entitymgr.EntityManager;
import dti.pm.policymgr.PolicyFields;
import dti.pm.policymgr.additionalinsuredmgr.AdditionalInsuredManager;
import dti.pm.policymgr.additionalinsuredmgr.AdditionalInsuredFields;
import dti.pm.policymgr.additionalinsuredmgr.dao.AdditionalInsuredDAO;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.busobjs.PMRecordSetHelper;
import dti.pm.busobjs.TransactionCode;
import dti.pm.busobjs.SysParmIds;
import dti.pm.busobjs.TransactionStatus;
import dti.pm.validationmgr.impl.ContinuityRecordSetValidator;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.transactionmgr.transaction.Transaction;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;
import dti.oasis.workflowmgr.WorkflowAgent;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.util.LogUtils;
import dti.oasis.util.DateUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.error.ValidationException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.app.ApplicationContext;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Iterator;
import java.util.Date;

/**
 * This Class provides the implementation details of AdditionalInsuredManager Interface.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 15, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/20/2008       fcb         chanegd logic for saveAllAdditionalInsured
 * 04/03/2008       fcb         saveAllAdditionalInsured: refactored logic to determine if
 *                              it is a long running transactions.
 * 10/23/2008      GXC         Issue 86879 - Modify saveAllAdditionalInsured to set renewalB if
 *                             a record is expired.  Also add logic to default the field initially
 * 07/01/2010      syang       Issue - 108651. Modified getInitialValuesForAdditionalInsured to set the initial End Date
 *                             as the policyExpirationDate. Modified validateAllAdditionalInsured to validate End Date
 *                             and policyExpirationDate.
 * 09/17/2010      syang       Issue 111445 - Added getAddInsCoverageData() to retrieve coverage data.
 * 10/19/2010      syang       Issue 113283 - Added transactionLogId to default values.
 * 08/30/2011      ryzhao      124458 - Modified validateAllAdditionalInsured to use FormatUtils.formatDateForDisplay()
 *                                      to format date when adding error messages.
 * 01/04/2012      wfu         127802 - Modified getInitialValuesForAdditionalInsured to set correct format entity name.
 * 02/15/2011      xnie        129417 - Modified performSaveAllAdditionalInsured() to set newEndorsementCode field null
 *                                      when we are not in a WIP transaction.
 * 02/27/2012      xnie        129417 - Roll backed prior version fix.        
 * 02/27/2013      xnie        138026 - 1) Added validateAsOfDateForGenerateAddIns() to check if the As of Date is valid.
 *                                      2) Added generateAllAddIns() to call DAO's method generateAllAddIns to generate
 *                                         Additional Insured.
 *                                      3) Modified getInitialValuesForAdditionalInsured() to set default Additional
 *                                         insured status for Generate button page entitlement.
 *                                      4) Modified loadAllAdditionalInsured() to display select checkbox.
 * 06/03/2013      tcheng      145028 - Modified saveAllAdditionalInsured to move validateAllAdditionalInsured logic before
 *                                     performSaveAllAdditionalInsured.
 * 08/07/2013      jshen       145027 - Modified validateAllAdditionalInsured() method to call new constructor of ContinuityRecordSetValidator
 *                                      to check if the key field's value is contained by another record key field.
 * 04/29/2014      sxm         154081 - Modified validation of dup rows per re-stated customer requirement.
 * 08/27/2014      jyang       156599 - Revert 76098's change on the condition of setting needToCaptureTransaction's value.
 * ---------------------------------------------------
 */

public class AdditionalInsuredManagerImpl implements AdditionalInsuredManager, AdditionalInsuredSaveProcessor {


    /**
     * load all additioanl insured
     *
     * @param policyHeader
     * @param inputRecord
     * @param loadProcessor an instance of data load processor
     * @return recordset of additional insured
     */
    public RecordSet loadAllAdditionalInsured(PolicyHeader policyHeader, Record inputRecord, RecordLoadProcessor loadProcessor) {

        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAdditionalInsured", new Object[]{policyHeader, inputRecord, loadProcessor});
        }
        RecordSet rs = null;

        inputRecord.setFields(policyHeader.toRecord(), false);

        RecordLoadProcessor entitlementLP = new AdditionalInsuredEntitlementRecordLoadProcessor(policyHeader, inputRecord);
        entitlementLP = RecordLoadProcessorChainManager.getRecordLoadProcessor(loadProcessor, entitlementLP);
        rs = getAdditionalInsuredDAO().loadAllAdditionalInsured(inputRecord, entitlementLP);

        //set needToCaptureTransaction field
        YesNoFlag addlinsWipTrans = YesNoFlag.getInstance(
            SysParmProvider.getInstance().getSysParm(SysParmIds.PM_ADDLINS_WIP_TRANS,"N"));
        YesNoFlag needToCaptureTransaction = YesNoFlag.Y;
        if (addlinsWipTrans.booleanValue()
            && policyHeader.getPolicyIdentifier().getPolicyViewMode().isWIP()) {
            needToCaptureTransaction = YesNoFlag.N;
        }
        AdditionalInsuredFields.setNeedToCaptureTransaction(rs.getSummaryRecord(), needToCaptureTransaction);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAdditionalInsured", rs);
        }

        return rs;
    }

    /**
     * Processes Save for Additional Insured
     *
     * @param policyHeader
     * @param inputRecords
     */
    public void saveAllAdditionalInsured(PolicyHeader policyHeader, RecordSet inputRecords, Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllAdditionalInsured", new Object[]{inputRecords});

        Date termExp = DateUtils.parseDate(PolicyHeaderFields.getTermEffectiveToDate(inputRecords.getSummaryRecord()));
        Iterator iter = inputRecords.getRecords();
        while (iter.hasNext()) {
            Record rec = (Record) iter.next();
            if (rec.isUpdateIndicatorInserted() || rec.isUpdateIndicatorUpdated()) {
                Date expDate = DateUtils.parseDate(AdditionalInsuredFields.getEndDate(rec));
                if (expDate.before(termExp)) {
                    AdditionalInsuredFields.setRenewalB(rec,"N");
                }
            }
        }
        RecordSet insertedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.INSERTED));
        RecordSet updatedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));
        if (insertedRecords.getSize() > 0 || updatedRecords.getSize() > 0) {
            //validate AdditionalInsured data
            validateAllAdditionalInsured(policyHeader, inputRecords);
        }

        if(getTransactionManager().isRatingLongRunning()) {
            WorkflowAgent wa = WorkflowAgentImpl.getInstance();
            String policyNumber = PolicyHeaderFields.getPolicyNo(inputRecords.getSummaryRecord());
            wa.initializeWorkflow(policyNumber,
                        SAVE_ADDITIONAL_INSURED_PROCESS,
                        SAVE_ADDITIONAL_INSURED_INITIAL_STATE);
            wa.setWorkflowAttribute(policyNumber, "inputRecords", inputRecords);
            wa.setWorkflowAttribute(policyNumber, "inputRecord", inputRecord);
        }
        else {
            AdditionalInsuredSaveProcessor saveProcessor = (AdditionalInsuredSaveProcessor) ApplicationContext.getInstance().getBean(SAVE_PROCESSOR);
            saveProcessor.performSaveAllAdditionalInsured(policyHeader, inputRecords, inputRecord);
        }

        l.exiting(getClass().getName(), "saveAllAdditionalInsured");
         
    }

    /**
     * save all additional insured data
     *
     * @param policyHeader
     * @param inputRecords
     * @param inputRecord
     */
    public void performSaveAllAdditionalInsured(PolicyHeader policyHeader, RecordSet inputRecords, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performSaveAllAdditionalInsured", new Object[]{policyHeader, inputRecords, inputRecord});
        }
        int processedCount = 0;

        //get changed recordset(inserted and updated) from input records
        /* Determine if anything has changed */
        RecordSet insertedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.INSERTED));
        RecordSet updatedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));
        if (insertedRecords.getSize() > 0 || updatedRecords.getSize() > 0) {
            YesNoFlag addlinsWipTrans = YesNoFlag.getInstance(
                SysParmProvider.getInstance().getSysParm(SysParmIds.PM_ADDLINS_WIP_TRANS,"N"));
            String transactionLogId = null;
            
            //decide if it is needed to create a new transaction
            YesNoFlag needToCaptureTransaction = YesNoFlag.Y;
            if (addlinsWipTrans.booleanValue()
                && policyHeader.getPolicyIdentifier().getPolicyViewMode().isWIP()) {
                needToCaptureTransaction = YesNoFlag.N;
            }

            Transaction lastTransaction = null;
            //use current transaction log id
            if (!needToCaptureTransaction.booleanValue()) {
                lastTransaction = policyHeader.getLastTransactionInfo();
            }
            //create transaction with Transaction Code ENDADDTLIN
            else {
                lastTransaction = getTransactionManager().createTransaction(
                    policyHeader, inputRecord, policyHeader.getTermEffectiveFromDate(), TransactionCode.ENDADDTLIN, false);
            }
            transactionLogId = lastTransaction.getTransactionLogId();
            inputRecords.setFieldValueOnAll(TransactionFields.TRANSACTION_LOG_ID, transactionLogId);

            //set rowStatus for updated/inserted records
            PMRecordSetHelper.setRowStatusOnModifiedRecords(inputRecords);
            inputRecords.setFieldsOnAll(policyHeader.toRecord(), false);
            //set row status
            RecordSet changedRecords = PMRecordSetHelper.setRowStatusOnModifiedRecords(inputRecords);
            //for newly inserted records, set additionalInsuredId and entityRoleId as NULL
            insertedRecords.setFieldValueOnAll(AdditionalInsuredFields.ADDITIONAL_INSURED_ID, null);
            insertedRecords.setFieldValueOnAll("entityRoleId", null);

            //call DAO to save data
            processedCount = getAdditionalInsuredDAO().saveAllAdditionalInsured(changedRecords);

            if (needToCaptureTransaction.booleanValue()) {
                //set transaction log ID
                TransactionFields.setTransactionLogId(inputRecord, transactionLogId);
                inputRecord.setFields(policyHeader.toRecord(),false);
                inputRecord.setFieldValue(TransactionFields.TRANSACTION_LOG_ID, transactionLogId);
                // process output
                getTransactionManager().processOutput(inputRecord, true);
                //update transaction to UPDATE
                getTransactionManager().UpdateTransactionStatusNoLock(lastTransaction, TransactionStatus.COMPLETE);
            }            

        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performSaveAllAdditionalInsured", new Integer(processedCount));
        }
    }


    /**
     * To get initial values for a newly inserted additional insured record
     *
     * @param policyHeader
     * @param inputRecord
     * @return Record with initial vlaues
     */
    public Record getInitialValuesForAdditionalInsured(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForAdditionalInsured", new Object[]{policyHeader, inputRecord});
        }

        Record output = getWorkbenchConfiguration().getDefaultValues(MAINTAIN_ADDLINFO_ACTION_CLASS_NAME);

        //get default record from entitlement load processor
        output.setFields(AdditionalInsuredEntitlementRecordLoadProcessor.getInitialEntitlementValuesForAddtionalInsured());        

        // Default term effective and expiration dates based on current policy term
        AdditionalInsuredFields.setStartDate(output, policyHeader.getTermEffectiveFromDate());
        // Issue - 108651, the end date should be the policy expiration date.
        AdditionalInsuredFields.setEndDate(output, policyHeader.getPolicyExpirationDate());

        AdditionalInsuredFields.setAddInsStatus(output, "PENDING");

        //set externalId with policy #
        AdditionalInsuredFields.setExternalId(output, policyHeader.getPolicyNo());
        // Default policy Id
        PolicyHeaderFields.setPolicyId(output, policyHeader.getPolicyId());
        // Issue 113283 - Default transaction log id
        TransactionFields.setTransactionLogId(output, policyHeader.getLastTransactionId());

        String entityId = AdditionalInsuredFields.getEntityId(inputRecord);
        AdditionalInsuredFields.setEntityId(output, entityId);
        AdditionalInsuredFields.setName(output, getEntityManager().getEntityName(entityId));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForAdditionalInsured", output);
        }
        return output;
    }

   /**
     * To get coverage data
     *
     * @param policyHeader
     * @param inputRecord
     * @return Record
     */
   public Record getAddInsCoverageData(PolicyHeader policyHeader, Record inputRecord) {
       Logger l = LogUtils.getLogger(getClass());
       if (l.isLoggable(Level.FINER)) {
           l.entering(getClass().getName(), "getAddInsCoverageData", new Object[]{inputRecord});
       }
       Record record = new Record();
       record.setFields(inputRecord);
       // If policy view mode is "OFFICIAL", the lastTransId should be 0.
       if (policyHeader.getPolicyIdentifier().getPolicyViewMode().isOfficial()) {
           record.setFieldValue("lastTransId", "0");
       }
       else {
           record.setFieldValue("lastTransId", policyHeader.getLastTransactionId());
       }
       Record outputRecord = getAdditionalInsuredDAO().getAddInsCoverageData(record);

       if (l.isLoggable(Level.FINER)) {
           l.exiting(getClass().getName(), "getAddInsCoverageData", outputRecord);
       }
       return outputRecord;
   }

    /**
     * validate all additional insured data
     *
     * @param policyHeader
     * @param inputRecords
     */
    protected void validateAllAdditionalInsured(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAllAdditionalInsured", new Object[]{policyHeader, inputRecords});
        }

        //get validate recordset(inserted and updated) from input records
        RecordSet changedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));

        Iterator it = changedRecords.getRecords();
        while (it.hasNext()) {
            Record r = (Record) it.next();
            String rowNum = String.valueOf(r.getRecordNumber() + 1);
            String rowId = AdditionalInsuredFields.getAdditionalInsuredId(r);

            //validate required fields
            if(!r.hasStringValue(AdditionalInsuredFields.START_DATE)){
              MessageManager.getInstance().addErrorMessage("pm.maintainAdditionalInsured.startDate.required.error",
                    new String[]{rowNum}, AdditionalInsuredFields.START_DATE, rowId);
            }

            if(!r.hasStringValue(AdditionalInsuredFields.END_DATE)){
              MessageManager.getInstance().addErrorMessage("pm.maintainAdditionalInsured.endDate.required.error",
                    new String[]{rowNum}, AdditionalInsuredFields.END_DATE, rowId);
            }

            if(!r.hasStringValue(AdditionalInsuredFields.ADDITIONAL_INSURED_CODE)){
              MessageManager.getInstance().addErrorMessage("pm.maintainAdditionalInsured.type.required.error",
                    new String[]{rowNum}, AdditionalInsuredFields.ADDITIONAL_INSURED_CODE, rowId);  
            }

            // stop validating the remaining records if we found problem(s) already
            if (MessageManager.getInstance().hasErrorMessages())
                break;

            Date effDate = DateUtils.parseDate(AdditionalInsuredFields.getStartDate(r));
            Date expDate = DateUtils.parseDate(AdditionalInsuredFields.getEndDate(r));

            // Validation #1 Start/End Date must be greater than or equal to Start Date
            if (expDate.before(effDate)) {
                MessageManager.getInstance().addErrorMessage("pm.maintainAdditionalInsured.invalidEffectiveToDate.error",
                    new String[]{rowNum}, AdditionalInsuredFields.START_DATE, rowId);
            }
            // Issue 108651. End Date must be before the policyExpirationDate.
            if (expDate.after(DateUtils.parseDate(policyHeader.getPolicyExpirationDate()))) {
                MessageManager.getInstance().addErrorMessage("pm.maintainAdditionalInsured.endDateOutsideTermDates.error",
                    new String[]{rowNum,
                        FormatUtils.formatDateForDisplay(policyHeader.getTermEffectiveFromDate()),
                        FormatUtils.formatDateForDisplay(policyHeader.getPolicyExpirationDate())},
                    AdditionalInsuredFields.END_DATE, rowId);
            }
            // Validation #2:  special handling dates must be within term dates
            if (effDate.before(DateUtils.parseDate(policyHeader.getTermEffectiveFromDate()))) {
                MessageManager.getInstance().addErrorMessage("pm.maintainAdditionalInsured.startDateOutsideTermDates.error",
                    new String[]{rowNum,
                        FormatUtils.formatDateForDisplay(policyHeader.getTermEffectiveFromDate()),
                        FormatUtils.formatDateForDisplay(policyHeader.getTermEffectiveToDate())},
                    AdditionalInsuredFields.START_DATE, rowId);
            }

            // stop validating the remaining records if we found problem(s) already
            if (MessageManager.getInstance().hasErrorMessages())
                break;
        }

        //Validation #3:  Validate continuity
        String[] keyFieldNames = null;
        String[] parmFieldNames = new String[]{};
        String messageKey = null;
        int addiInsPolTypeCount = getAdditionalInsuredDAO().getAdditionslInsuredPolicyTypeCount(policyHeader.toRecord());

        if (addiInsPolTypeCount > 0) {
            keyFieldNames = new String[]{AdditionalInsuredFields.ENTITY_ID,
                AdditionalInsuredFields.ADDITIONAL_INSURED_CODE,
                AdditionalInsuredFields.ADDRESS_ID, AdditionalInsuredFields.RISK_ID};
            messageKey = "pm.maintainAdditionalInsured.invalidContinuityForRisk.error";
        }
        else {
            keyFieldNames = new String[]{AdditionalInsuredFields.ENTITY_ID,
                AdditionalInsuredFields.ADDITIONAL_INSURED_CODE};
            messageKey = "pm.maintainAdditionalInsured.invalidContinuityForType.error";
        }
        ContinuityRecordSetValidator continuityValidator = new ContinuityRecordSetValidator(
            AdditionalInsuredFields.START_DATE, AdditionalInsuredFields.END_DATE,
            AdditionalInsuredFields.ADDITIONAL_INSURED_ID,
            messageKey, keyFieldNames, parmFieldNames, null, false, true);
        continuityValidator.validate(inputRecords);

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages())
            throw new ValidationException("Invalid Additional Insured data.");

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateAllAdditionalInsured", String.valueOf(MessageManager.getInstance().hasErrorMessages()));
        }
    }

    /**
     * To validate the As of Date
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a record loaded with the as of date value
     */
    public void validateAsOfDateForGenerateAddIns(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAsOfDateForGenerateAddIns", new Object[]{policyHeader, inputRecord});
        }

        if (!inputRecord.hasStringValue(AdditionalInsuredFields.AS_OF_DATE)) {
            MessageManager.getInstance().addErrorMessage("pm.generateAddIns.asOfDate.empty.error", AdditionalInsuredFields.AS_OF_DATE);
            throw new ValidationException("As of Date is empty.");
        }

        String sAsOfDate = AdditionalInsuredFields.getAsOfDate(inputRecord);
        Date asOfDate = DateUtils.parseDate(sAsOfDate);

        String sTermEffDate = policyHeader.getTermEffectiveFromDate();
        String sTermExpDate = policyHeader.getTermEffectiveToDate();
        Date termEffDate = DateUtils.parseDate(sTermEffDate);
        Date termExpDate = DateUtils.parseDate(sTermExpDate);

        // Validation #1: the As of Date cannot be prior to the term effective date
        if (asOfDate.before(termEffDate)) {
            MessageManager.getInstance().addErrorMessage("pm.generateAddIns.asOfDate.error1", AdditionalInsuredFields.AS_OF_DATE);
        }
        else if (asOfDate.after(termExpDate) || asOfDate.equals(termExpDate)) {
            MessageManager.getInstance().addErrorMessage("pm.generateAddIns.asOfDate.error2", AdditionalInsuredFields.AS_OF_DATE);
        }

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages())
            throw new ValidationException("Invalid Additional Insured As of Date.");

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateAsOfDateForGenerateAddIns");
        }
    }

    /**
     * Generate all Additional Insured.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a Record with the details of Additional Insured data.
     */
    public void generateAllAddIns(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "generateAllAddIns", new Object[]{policyHeader, inputRecord});
        }

        Record record = new Record();
        record.setFields(inputRecord);

        // Get As of Date from UserSession
        String asOfDate = AdditionalInsuredFields.getAsOfDate(inputRecord);

        // Create an new transaction
        Record transRecord = new Record();
        transRecord.setFieldValue(PolicyHeaderFields.POLICY_TERM_HISTORY_ID, policyHeader.getPolicyTermHistoryId());
        transRecord.setFieldValue(PolicyFields.ACCOUNTING_DATE, new Date());
        transRecord.setFieldValue(TransactionFields.ENDORSEMENT_CODE, "");
        transRecord.setFieldValue(TransactionFields.TRANSACTION_COMMENT, null);
        Transaction trans = getTransactionManager().createTransaction(policyHeader,
            transRecord,
            asOfDate,
            TransactionCode.GENADDINS,
            false);

        // transactionLogId
        String transactionLogId = trans.getTransactionLogId();
        TransactionFields.setTransactionLogId(record, transactionLogId);
        // termBaseRecordId
        PolicyHeaderFields.setTermBaseRecordId(record, policyHeader.getTermBaseRecordId());

        // Generate Additional Insured
        getAdditionalInsuredDAO().generateAllAddIns(record);

        // Update the transaction status to complete
        getTransactionManager().UpdateTransactionStatusNoLock(trans, TransactionStatus.COMPLETE);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "generateAllAddIns");
        }
    }

    public void verifyConfig() {
        if (getTransactionManager() == null)
            throw new ConfigurationException("The required property 'transactionManager' is missing.");
        if (getEntityManager() == null)
            throw new ConfigurationException("The required property 'entityManager' is missing.");
        if (getAdditionalInsuredDAO() == null)
            throw new ConfigurationException("The required property 'additionalInsuredDAO' is missing.");
        if (getWorkbenchConfiguration() == null)
            throw new ConfigurationException("The required property 'WorkbenchConfiguration' is missing.");

    }

    public AdditionalInsuredDAO getAdditionalInsuredDAO() {
        return m_additionalInsuredDAO;
    }

    public void setAdditionalInsuredDAO(AdditionalInsuredDAO additionalInsuredDAO) {
        m_additionalInsuredDAO = additionalInsuredDAO;
    }

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    public EntityManager getEntityManager() {
        return m_entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        m_entityManager = entityManager;
    }

     public WorkbenchConfiguration getWorkbenchConfiguration() {
        return m_workbenchConfiguration;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration) {
        m_workbenchConfiguration = workbenchConfiguration;
    }

    private AdditionalInsuredDAO m_additionalInsuredDAO;
    private TransactionManager m_transactionManager;
    private EntityManager m_entityManager;
    private WorkbenchConfiguration m_workbenchConfiguration;

    private static final String SAVE_ADDITIONAL_INSURED_PROCESS = "SaveAdditionalInsuredWorkflow";
    private static final String SAVE_ADDITIONAL_INSURED_INITIAL_STATE = "invokeSaveAdditionalInsured";
    protected static final String SAVE_PROCESSOR = "AdditionalInsuredManager";
    protected static final String MAINTAIN_ADDLINFO_ACTION_CLASS_NAME = "dti.pm.policymgr.additionalinsuredmgr.struts.MaintainAdditionalInsuredAction";
}
