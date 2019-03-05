package dti.pm.policymgr.reinsurancemgr.impl;

import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.ConfirmationFields;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.recordset.RecordLoadProcessorChainManager;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.pm.busobjs.PMRecordSetHelper;
import dti.pm.busobjs.SysParmIds;
import dti.cs.data.dbutility.DBUtilityManager;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.reinsurancemgr.ReinsuranceFields;
import dti.pm.policymgr.reinsurancemgr.ReinsuranceManager;
import dti.pm.policymgr.reinsurancemgr.dao.ReinsuranceDAO;
import dti.pm.validationmgr.impl.AddOrigFieldsRecordLoadProcessor;

import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides the implementation details for reinsurance.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 30, 2007
 *
 * @author rlli
 */
/*
*
* Revision Date    Revised By  Description
* ---------------------------------------------------
* 07/09/2013       awu      145480 - Modified loadAllReinsurance() to set the default value of PM_REINRTR_USE_PTHDT to Y.
* 02/20/2014       adeng    149313 - Modified saveAllReinsurance & validateAllReinsurance() to set the correct row
*                                    number into validation error message.
* ---------------------------------------------------
*/

public class ReinsuranceManagerImpl implements ReinsuranceManager {

    /**
     * Retrieves all reinsurance's information for one policy
     *
     * @param policyHeader policy header
     * @return RecordSet
     */
    public RecordSet loadAllReinsurance(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllReinsurance", new Object[]{policyHeader});
        }
        String sysPara = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_REINRTR_USE_PTHDT, "Y");
        Record input = new Record();
        input.setFieldValue("policyId", policyHeader.getPolicyId());
        if (YesNoFlag.getInstance(sysPara).booleanValue()) {
            input.setFieldValue("termEffectiveFromDate", policyHeader.getTermEffectiveFromDate());
            input.setFieldValue("termEffectiveToDate", policyHeader.getTermEffectiveToDate());
        }
        else {
            input.setFieldValue("termEffectiveFromDate", null);
            input.setFieldValue("termEffectiveToDate", null);
        }

        RecordLoadProcessor lp =
            RecordLoadProcessorChainManager.getRecordLoadProcessor(origFieldLoadProcessor,
                new ReinsuranceEntitlementRecordLoadProcessor(policyHeader));

        RecordSet rs = getReinsuranceDAO().loadAllReinsurance(input, lp);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllReinsurance", rs);
        }
        return rs;
    }

    /**
     * Save all reinsurance's information
     *
     * @param policyHeader policy header
     * @param inputRecords a set of Records, each with the updated reinsurance info
     * @return the number of rows updated
     */
    public int saveAllReinsurance(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllReinsurance", new Object[]{inputRecords});
        int updateCount = 0;
        updateReinsurerEntityId(inputRecords);

        // Set the displayRecordNumber to all visible records.
        inputRecords = PMRecordSetHelper.setDisplayRecordNumberOnRecords(inputRecords);

        /* Create an new RecordSet to include all added and modified records */
        RecordSet allModifedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter
            (new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));
        RecordSet allDeletedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter
            (new String[]{UpdateIndicator.DELETED}));


        if (allModifedRecords.getSize() > 0) {
            /* validate the input records prior save them */
            validateAllReinsurance(policyHeader, allModifedRecords);
            PMRecordSetHelper.setRowStatusOnModifiedRecords(allModifedRecords);
            updateCount += getReinsuranceDAO().saveAllReinsurance(allModifedRecords);
        }
        if (allDeletedRecords.getSize() > 0) {
            updateCount += getReinsuranceDAO().deleteAllReinsurance(allDeletedRecords);
        }

        l.exiting(getClass().getName(), "saveAllReinsurance", new Integer(updateCount));
        return updateCount;
    }

    //validate each modifed record
    protected void validateAllReinsurance(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAllReinsurance", new Object[]{policyHeader, inputRecords});
        }

        //get validate recordset(inserted and updated) from input records
        RecordSet changedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));

        Iterator it = changedRecords.getRecords();
        while (it.hasNext()) {
            Record r = (Record) it.next();
            String rowNum = String.valueOf(r.getFieldValue(PMRecordSetHelper.DISPLAY_RECORD_NUMBER));
            String rowId = ReinsuranceFields.getPoilcyReinsuranceId(r);

            Date polEffDate = DateUtils.parseDate(policyHeader.getTermEffectiveFromDate());
            Date effDate = DateUtils.parseDate(r.getStringValue("effectiveFromDate"));
            Date expDate = DateUtils.parseDate(r.getStringValue("effectiveToDate"));
            //Validation #1 End Date must be greater than or equal to Start Date
            if (expDate.before(effDate)) {
                // Set back to orignial value
                MessageManager.getInstance().addErrorMessage("pm.maintainReinsurance.invalidEffectiveToDate.error",
                    new String[]{rowNum}, "effectiveToDate", rowId);

            }
            if (r.hasStringValue("date1") && !r.getStringValue("date1").equals(r.getStringValue("origDate1"))
                && YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm(SysParmIds.PM_ACCEPT_COLLATERAL, "N")).booleanValue()) {
                Date collateralEffDate = DateUtils.parseDate(r.getStringValue("date1"));
                String confirmMsg = "pm.maintainReinsurance.invalidCollateralEffDate.confirm";
                if (collateralEffDate.before(polEffDate) &&
                    !ConfirmationFields.isConfirmed(confirmMsg, inputRecords.getSummaryRecord())) {
                    MessageManager.getInstance().addConfirmationPrompt(confirmMsg);
                }
            }

            if (MessageManager.getInstance().hasErrorMessages() || MessageManager.getInstance().hasConfirmationPrompts()) {
                break;
            }
        }
        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages() || MessageManager.getInstance().hasConfirmationPrompts())
            throw new ValidationException("Invalid reinsurance data.");
        l.exiting(getClass().getName(), "validateAllReinsurance");
    }

    //set reinsurerEntityId to "-1"  if it is ""
    private void updateReinsurerEntityId(RecordSet inputRecordSet) {
        Iterator iter = inputRecordSet.getRecords();
        while (iter.hasNext()) {
            Record record = (Record) iter.next();
            if (StringUtils.isBlank(ReinsuranceFields.getReinsurerEntityId(record))) {
                ReinsuranceFields.setReinsurerEntityId(record, "-1");
            }
        }
    }

    /**
     * To get initial values for a new reinsurance record
     *
     * @param policyHeader
     * @param inputRecord
     * @return Record
     */
    public Record getInitialValuesForReinsurance(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForReinsurance", new Object[]{policyHeader, inputRecord});
        }
        //get default record from workbench
        Record output = getWorkbenchConfiguration().getDefaultValues(MAINTAIN_REINSURANCE_ACTION_CLASS_NAME);
        // Get the initial entitlement values
        output.setFields(ReinsuranceEntitlementRecordLoadProcessor.getInitialEntitlementValuesForReinsurance());
        String collateral = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_ACCEPT_COLLATERAL, "N");
        if (collateral.equals("Y")) {
            ReinsuranceFields.setContractNo(output, "NA");
            ReinsuranceFields.setReinsurerEntityId(output, "");
        }
        ReinsuranceFields.setEffectiveFromDate(output, policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        ReinsuranceFields.setEffectiveToDate(output, "01/01/3000");
        ReinsuranceFields.setPolicyType(output, policyHeader.getPolicyTypeCode());
        Long policyReinsuranceId = getDbUtilityManager().getNextSequenceNo();
        ReinsuranceFields.setPoilcyReinsuranceId(output, policyReinsuranceId.toString());
        ReinsuranceFields.setPoilcyId(output, policyHeader.getPolicyId());
        l.exiting(getClass().getName(), "getInitialValuesForReinsurance");
        return output;
    }

    public void verifyConfig() {
        if (getDbUtilityManager() == null)
            throw new ConfigurationException("The required property 'dbUtilityManager' is missing.");
        if (getReinsuranceDAO() == null) {
            throw new ConfigurationException("The required property 'ReinsuranceDAO' is missing.");
        }
        if (getWorkbenchConfiguration() == null)
            throw new ConfigurationException("The required property 'workbenchConfiguration' is missing.");
    }

    public ReinsuranceDAO getReinsuranceDAO() {
        return m_reinsuranceDAO;
    }

    public void setReinsuranceDAO(ReinsuranceDAO reinsuranceDAO) {
        m_reinsuranceDAO = reinsuranceDAO;
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

    private ReinsuranceDAO m_reinsuranceDAO;
    private DBUtilityManager m_dbUtilityManager;
    private WorkbenchConfiguration m_workbenchConfiguration;
    protected static final String MAINTAIN_REINSURANCE_ACTION_CLASS_NAME = "dti.pm.policymgr.reinsurancemgr.struts.MaintainReinsuranceAction";
    private static AddOrigFieldsRecordLoadProcessor origFieldLoadProcessor = new AddOrigFieldsRecordLoadProcessor(
        new String[]{"date1"});
}
