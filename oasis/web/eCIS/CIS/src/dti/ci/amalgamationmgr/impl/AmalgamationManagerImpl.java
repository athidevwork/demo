package dti.ci.amalgamationmgr.impl;

import dti.ci.amalgamationmgr.AmalgamationFields;
import dti.ci.amalgamationmgr.AmalgamationManager;
import dti.ci.amalgamationmgr.dao.AmalgamationDAO;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.busobjs.OasisRecordSetHelper;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides the implementation details of AmalgamationManager Interface.
 * <p/>
 * <p>(C) 2009 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 12, 2009
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class AmalgamationManagerImpl implements AmalgamationManager {

    /**
     * Method to load all amalgamation
     *
     * @param inputRecord a record containing input information
     * @return RecordSet resultset containing amalgamation information
     */
    public RecordSet loadAllAmalgamation(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAmalgamation", new Object[]{inputRecord});
        }
        DefaultRecordLoadProcessor entitlementRLP = new AmalgamationEntitlementRecordLoadProcessor();
        RecordSet rs = getAmalgamationDAO().loadAllAmalgamation(inputRecord, entitlementRLP);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAmalgamation", rs);
        }
        return rs;
    }

    /**
     * Method to save all amalgamation information
     *
     * @param inputRecords
     * @return int
     */
    public int saveAllAmalgamation(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllAmalgamation", new Object[]{inputRecords});
        }
        validateAllAmalgamation(inputRecords);
        // Get the changes
        RecordSet changedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
                new String[]{UpdateIndicator.DELETED, UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));
        changedRecords = OasisRecordSetHelper.setRowStatusOnModifiedRecords(changedRecords);
        int updateCount = 0;
        if (changedRecords.getSize() > 0) {
            updateCount = getAmalgamationDAO().saveAllAmalgamation(changedRecords);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllAmalgamation", new Integer(updateCount));
        }
        return updateCount;
    }

    /**
     * Get initial values for adding amalgamation.
     *
     * @return Record
     */
    public Record getInitialValuesForAmalgamation() {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForAmalgamation");
        }
        // Set initial values.
        Record output = new Record();
        output.setFieldValue(AmalgamationFields.POLICY_AMALGAMATION_ID, String.valueOf(new Date().getTime()));
        output.setFieldValue(IS_DELETE_AVAILABLE, YesNoFlag.Y);
        output.setFieldValue(AmalgamationFields.MANUAL_B, YesNoFlag.Y);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForAmalgamation", output);
        }
        return output;
    }

    /**
     * Validate all amalgamation.
     *
     * @param inputRecords A Set of amalgamation.
     */
    protected void validateAllAmalgamation(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAllAmalgamation", new Object[]{inputRecords});
        }
        // validate inserted, updated rows
        RecordSet rowsForValidation = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(new String[]{UpdateIndicator.INSERTED,UpdateIndicator.UPDATED}));
        Iterator it= rowsForValidation.getRecords();
        while (it.hasNext()) {
            Record record = (Record) it.next();
            String rowId = AmalgamationFields.getPolicyAmalgamationId(record);
            if (StringUtils.isBlank(AmalgamationFields.getSourcePolicyNo(record))) {
                MessageManager.getInstance().addErrorMessage("ci.amalgamation.sourcePolicyNo.required",
                        AmalgamationFields.SOURCE_POLICY_NO, rowId);
            }
            if (StringUtils.isBlank(AmalgamationFields.getDestPolicyNo(record))) {
                MessageManager.getInstance().addErrorMessage("ci.amalgamation.destPolicyNo.required",
                        AmalgamationFields.DEST_POLICY_NO, rowId);
            }
            if (StringUtils.isBlank(AmalgamationFields.getSourceRiskBaseRecordId(record))) {
                MessageManager.getInstance().addErrorMessage("ci.amalgamation.sourceRiskBaseRecordId.required",
                        AmalgamationFields.SOURCE_RISK_BASE_RECORD_ID, rowId);
            }
            if (StringUtils.isBlank(AmalgamationFields.getDestRiskBaseRecordId(record))) {
                MessageManager.getInstance().addErrorMessage("ci.amalgamation.destRiskBaseRecordId.required",
                        AmalgamationFields.DEST_RISK_BASE_RECORD_ID, rowId);
            }
            if (StringUtils.isBlank(AmalgamationFields.getClaimsAccessIndicator(record))) {
                MessageManager.getInstance().addErrorMessage("ci.amalgamation.claimsAccessIndicator.required",
                        AmalgamationFields.CLAIMS_ACCESS_INDICATOR, rowId);
            }
            if (StringUtils.isBlank(AmalgamationFields.getAmalgamationCode(record))) {
                MessageManager.getInstance().addErrorMessage("ci.amalgamation.code.required",
                        AmalgamationFields.AMALGAMATION_CODE, rowId);
            }
            if (StringUtils.isBlank(AmalgamationFields.getAmalgamationDate(record))) {
                MessageManager.getInstance().addErrorMessage("ci.amalgamation.date.required",
                        AmalgamationFields.AMALGAMATION_DATE, rowId);
            }

            if (!StringUtils.isBlank(AmalgamationFields.getSourcePolicyNo(record)) &&
                    AmalgamationFields.getSourcePolicyNo(record).equals(AmalgamationFields.getDestPolicyNo(record))) {
                MessageManager.getInstance().addErrorMessage("ci.amalgamation.samePolicyNo.error",
                        AmalgamationFields.SOURCE_POLICY_NO, rowId);
            }
            // Throw validation exception if there is any error message.
            if (MessageManager.getInstance().hasErrorMessages()) {
                throw new ValidationException("The amalgamation date is error.");
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateAllAmalgamation");
        }
    }

    /**
     *  verifyConfig
     */
    public void verifyConfig() {
        if (getAmalgamationDAO() == null)
            throw new ConfigurationException("The required property 'amalgamationDAO' is missing.");
    }

    public AmalgamationDAO getAmalgamationDAO() {
        return m_amalgamationDAO;
    }

    public void setAmalgamationDAO(AmalgamationDAO amalgamationDAO) {
        m_amalgamationDAO = amalgamationDAO;
    }

    private AmalgamationDAO m_amalgamationDAO;
    private static final String IS_DELETE_AVAILABLE = "isDeleteAvailable";
}
