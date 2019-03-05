package dti.pm.validationmgr.impl;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.validationmgr.RecordValidator;

import java.util.logging.Logger;
import java.util.Date;

/**
 * This class implements validation of dusplicates.
 * <p/>
 * <p>
 * Rule - The given combination of field values cannot have overlapping time periods.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 9, 2007
 *
 * @author sxm
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/04/2007       sxm         Pass field ID and row ID to addErrorMessage()
 * ---------------------------------------------------
 */
public class ContinuityRecordValidator implements RecordValidator {

    /**
     * Validate the given record set.
     *
     * @param inputRecord    a data Record
     * @return true if the RecordSet is valid; otherwise false.
     */
    public boolean validate(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validate", new Object[]{inputRecord});
        boolean isValid = true;

        // continue if there are at least two records
        if (getInputRecords().getSize() > 1) {
            // get current values
            Date effectiveFromDate = inputRecord.getDateValue(getEffectiveFromDateFieldName());
            Date effectiveToDate = inputRecord.getDateValue(getEffectiveToDateFieldName());
            String id = inputRecord.getStringValue(getIdFieldName());

            int numKeys = getKeyFieldNames().length;

            // validate continuity
            for (int j=0; j<getInputRecords().getSize(); j++) {
                // get record
                Record record = getInputRecords().getRecord(j);

                // compare keys
                boolean sameKey = true;
                for (int i=0; i<numKeys; i++) {
                    String keyFieldName = getKeyFieldNames()[i];
                    if (!inputRecord.getFieldValue(keyFieldName).equals(record.getFieldValue(keyFieldName))) {
                        sameKey = false;
                        break;
                    }
                }

                // compare dates
                if (!id.equals(record.getStringValue(getIdFieldName())) && sameKey &&
                        effectiveFromDate.before(record.getDateValue(getEffectiveToDateFieldName())) &&
                        effectiveToDate.after(record.getDateValue(getEffectiveFromDateFieldName()))) {
                    isValid = false;

                    // set row numbers as message parameters first
                    String rowId;
                    int numParms = getParmFieldNames().length;
                    Object [] parms = new Object[numParms+2];
                    int currentRowNumber = record.getRecordNumber() + 1;
                    int inputRowNumber = inputRecord.getRecordNumber() + 1;
                    if (currentRowNumber < inputRowNumber) {
                        parms[0] = String.valueOf(currentRowNumber);
                        parms[1] = String.valueOf(inputRowNumber);
                        rowId = id;
                    }
                    else {
                        parms[0] = String.valueOf(inputRowNumber);
                        parms[1] = String.valueOf(currentRowNumber);
                        rowId = record.getStringValue(getIdFieldName());
                    }

                    // set additional message parameters
                    for (int i=0; i<numParms; i++) {
                        String parmFieldName = getParmFieldNames()[i];
                        if (record.hasStringValue(parmFieldName+"LOVLABEL"))
                            parms[i+2] = record.getFieldValue(parmFieldName+"LOVLABEL");
                        else
                            parms[i+2] = record.getFieldValue(parmFieldName);
                    }

                    // add message
                    MessageManager.getInstance().addErrorMessage(getMessageKey(), parms, "", rowId);
                    break;
                }
            }
        }

        l.exiting(getClass().getName(), "validate", Boolean.valueOf(isValid));
        return isValid;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public ContinuityRecordValidator() {
    }

    public ContinuityRecordValidator(String effectiveFromDateFieldName, String effectiveToDateFieldName,
                                     String idFieldName, String messageKey, RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "ContinuityRecordValidator",
            new Object[]{effectiveFromDateFieldName, effectiveToDateFieldName, idFieldName, messageKey, inputRecords});
        setEffectiveFromDateFieldName(effectiveFromDateFieldName);
        setEffectiveToDateFieldName(effectiveToDateFieldName);
        setIdFieldName(idFieldName);
        setMessageKey(messageKey);
        setInputRecords(inputRecords);
        l.exiting(getClass().getName(), "ContinuityRecordValidator");
    }

    public String getEffectiveFromDateFieldName() {
        return m_effectiveFromDateFieldName;
    }

    public void setEffectiveFromDateFieldName(String effectiveFromDateFieldName) {
        m_effectiveFromDateFieldName = effectiveFromDateFieldName;
    }

    public String getEffectiveToDateFieldName() {
        return m_effectiveToDateFieldName;
    }

    public void setEffectiveToDateFieldName(String effectiveToDateFieldName) {
        m_effectiveToDateFieldName = effectiveToDateFieldName;
    }

    public String getIdFieldName() {
        return m_idFieldName;
    }

    public void setIdFieldName(String idFieldName) {
        m_idFieldName = idFieldName;
    }

    public String getMessageKey() {
        return m_messageKey;
    }

    public void setMessageKey(String messageKey) {
        m_messageKey = messageKey;
    }

    public String[] getKeyFieldNames() {
        return m_keyFieldNames;
    }

    public void setKeyFieldNames(String[] keyFieldNames) {
        m_keyFieldNames = keyFieldNames;
    }

    public String[] getParmFieldNames() {
        return m_parmFieldNames;
    }

    public void setParmFieldNames(String[] parmFieldNames) {
        m_parmFieldNames = parmFieldNames;
    }

    public RecordSet getInputRecords() {
        return m_inputRecords;
    }

    public void setInputRecords(RecordSet inputRecords) {
        m_inputRecords = inputRecords;
    }

    private String m_effectiveFromDateFieldName;
    private String m_effectiveToDateFieldName;
    private String m_idFieldName;
    private String m_messageKey;
    private String[] m_keyFieldNames;
    private String[] m_parmFieldNames;
    private RecordSet m_inputRecords;
}
