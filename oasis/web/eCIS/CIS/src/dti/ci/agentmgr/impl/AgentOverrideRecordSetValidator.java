package dti.ci.agentmgr.impl;

import dti.ci.agentmgr.AgentFields;
import dti.oasis.converter.ConverterFactory;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordComparator;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;

import java.util.Date;
import java.util.logging.Logger;

/**
 * This class implements validation of duplicates.
 * <p/>
 * <p/>
 * Rule - The given combination of field values cannot have overlapping time periods.
 * <p/>
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 18, 2016
 *
 * @author iwang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/24/2017       htwang      Issue 188776 - add message field name that will be used in
 *                              MessageManager.getInstance().addErrorMessage() later
 * ---------------------------------------------------
 */
public class AgentOverrideRecordSetValidator extends ContinuityRecordSetValidator {

    /**
     * Override this method to add rowid for parent grid (Agent Overrides grid)
     *
     * @param currentRecord
     * @param parms
     */
    protected void addErrorMessage(Record currentRecord, Object[] parms) {
        Logger l = LogUtils.enterLog(getClass(), "addErrorMessage", new Object[]{currentRecord, parms});
        String rowId = currentRecord.getStringValue(getIdFieldName());

        String agentStaffId = AgentFields.getAgentStaffId(currentRecord);

        //get override rowid
        for (int i = 0; i < getParentResultSet().getSize(); i++) {
            Record parentRecord = getParentResultSet().getRecord(i);
            String parentId = AgentFields.getAgentStaffId(parentRecord);
            if (agentStaffId.equals(parentId)) {
                rowId = parentId + "," + rowId;
                break;
            }
        }

        MessageManager.getInstance().addErrorMessage(getMessageKey(), parms,
            getMessageFieldName() == null ? "" : getMessageFieldName(), rowId);

        l.exiting(getClass().getName(), "addErrorMessage");
    }

    /**
     * Validate the given record set.
     *
     * @param inputRecords a data RecordSet
     * @return true if the RecordSet is valid; otherwise false.
     */
    public boolean validate(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "validate", new Object[]{inputRecords});
        boolean isValid = true;

        // continue if there are at least two records
        if (inputRecords.getSize() > 1) {
            // sort the records by keys, effective from date, and then effective to date
            RecordComparator rc;
            int numKeys = getKeyFieldNames().length;
            if (numKeys > 0) {
                rc = new RecordComparator(getKeyFieldNames()[0]);
                for (int i = 1; i < numKeys; i++) {
                    rc.addFieldComparator(getKeyFieldNames()[i]);
                }
                rc.addFieldComparator(getEffectiveFromDateFieldName(), ConverterFactory.getInstance().getConverter(Date.class));
            }
            else {
                rc = new RecordComparator(getEffectiveFromDateFieldName(), ConverterFactory.getInstance().getConverter(Date.class));
            }
            rc.addFieldComparator(getEffectiveToDateFieldName(), ConverterFactory.getInstance().getConverter(Date.class));
            RecordSet records = inputRecords.getSortedCopy(rc);

            // validate continuity
            Date effectiveToDate = records.getFirstRecord().getDateValue(getEffectiveToDateFieldName());
            for (int sortIdx = 1; sortIdx < inputRecords.getSize(); sortIdx++) {
                Record currentRecord = records.getRecord(sortIdx);
                Record priorRecord = records.getRecord(sortIdx - 1);

                // compare keys
                boolean keyChanged = false;
                for (int i = 0; i < numKeys; i++) {
                    String keyFieldName = getKeyFieldNames()[i];
                    if (!currentRecord.hasFieldValue(keyFieldName) && !priorRecord.hasFieldValue(keyFieldName)) {
                        continue;
                    } else if ((!currentRecord.hasFieldValue(keyFieldName) && priorRecord.hasFieldValue(keyFieldName))
                                || (currentRecord.hasFieldValue(keyFieldName) && !priorRecord.hasFieldValue(keyFieldName))
                                || (!currentRecord.getFieldValue(keyFieldName).equals(priorRecord.getFieldValue(keyFieldName)))) {
                        keyChanged = true;
                        break;
                    }
                }

                // set effective from date
                Date effectiveFromDate = currentRecord.getDateValue(getEffectiveFromDateFieldName());

                // invalid if there is a overlap or gap within the group of records with same key values
                if (!keyChanged && (effectiveToDate.after(effectiveFromDate) || getValidateGap() && effectiveToDate.before(effectiveFromDate))) {
                    isValid = false;

                    // set row numbers as message parameters first
                    int numParms = getParmFieldNames().length;
                    Object[] parms = new Object[numParms + 2];

                    int currentRowNumber = currentRecord.getRecordNumber() + 1;
                    int priorRowNumber = priorRecord.getRecordNumber() + 1;
                    if (getRowNumberFieldName() != null) {
                        currentRowNumber = currentRecord.getIntegerValue(getRowNumberFieldName()).intValue()+1;
                        priorRowNumber = priorRecord.getIntegerValue(getRowNumberFieldName()).intValue()+1;
                    }
                    if (currentRowNumber < priorRowNumber) {
                        parms[0] = String.valueOf(currentRowNumber);
                        parms[1] = String.valueOf(priorRowNumber);
                    }
                    else {
                        parms[0] = String.valueOf(priorRowNumber);
                        parms[1] = String.valueOf(currentRowNumber);
                    }

                    // set additional message parameters
                    for (int i = 0; i < numParms; i++) {
                        String parmFieldName = getParmFieldNames()[i];
                        if (currentRecord.hasStringValue(parmFieldName + "LOVLABEL"))
                            parms[i + 2] = currentRecord.getFieldValue(parmFieldName + "LOVLABEL");
                        else
                            parms[i + 2] = currentRecord.getFieldValue(parmFieldName);
                    }

                    // add message
                    addErrorMessage(currentRecord, parms);
                }

                // stop if found invalid rows
                if (!isValid)
                    break;

                //  set effecrive to date
                effectiveToDate = records.getRecord(sortIdx).getDateValue(getEffectiveToDateFieldName());
            }
        }

        l.exiting(getClass().getName(), "validate", Boolean.valueOf(isValid));
        return isValid;
    }

    //-------------------------------------------------
    // constructor
    //-------------------------------------------------
    public AgentOverrideRecordSetValidator() {
        super();
    }

    public AgentOverrideRecordSetValidator(String effectiveFromDateFieldName, String effectiveToDateFieldName, String idFieldName, String messageKey, String messageFieldName) {
        super(effectiveFromDateFieldName, effectiveToDateFieldName, idFieldName, messageKey, messageFieldName);
    }

    public AgentOverrideRecordSetValidator(String effectiveFromDateFieldName, String effectiveToDateFieldName, String idFieldName, String rowNumberFieldName, String messageKey, String messageFieldName) {
        super(effectiveFromDateFieldName, effectiveToDateFieldName, idFieldName, rowNumberFieldName, messageKey, messageFieldName);
    }

    public AgentOverrideRecordSetValidator(String effectiveFromDateFieldName, String effectiveToDateFieldName, String idFieldName, String messageKey, boolean validateGap, String messageFieldName) {
        super(effectiveFromDateFieldName, effectiveToDateFieldName, idFieldName, messageKey, validateGap, messageFieldName);
    }

    public AgentOverrideRecordSetValidator(String effectiveFromDateFieldName, String effectiveToDateFieldName, String idFieldName, String messageKey, String[] keyFieldNames, String[] parmFieldNames, String messageFieldName) {
        super(effectiveFromDateFieldName, effectiveToDateFieldName, idFieldName, messageKey, keyFieldNames, parmFieldNames, messageFieldName);
    }

    public AgentOverrideRecordSetValidator(String effectiveFromDateFieldName, String effectiveToDateFieldName, String idFieldName, String messageKey, String[] keyFieldNames, String[] parmFieldNames, boolean validateGap, String messageFieldName) {
        super(effectiveFromDateFieldName, effectiveToDateFieldName, idFieldName, messageKey, keyFieldNames, parmFieldNames, validateGap, messageFieldName);
    }

    public AgentOverrideRecordSetValidator(String effectiveFromDateFieldName, String effectiveToDateFieldName, String idFieldName, String messageKey, String[] keyFieldNames, String[] parmFieldNames, String rowNumberFieldName, boolean validateGap, String messageFieldName) {
        super(effectiveFromDateFieldName, effectiveToDateFieldName, idFieldName, messageKey, keyFieldNames, parmFieldNames, rowNumberFieldName, validateGap, messageFieldName);
    }


    /**
     * get parent resultset
     * @return
     */
    public RecordSet getParentResultSet() {
        return m_parentResultSet;
    }

    /**
     * set parent resultset
     * @param parentResultSet
     */
    public void setParentResultSet(RecordSet parentResultSet) {
        m_parentResultSet = parentResultSet;
    }

    /**
     * parent result set
     */
    private RecordSet m_parentResultSet;

}
