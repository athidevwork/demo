package dti.ci.validationmgr.impl;

import dti.oasis.converter.ConverterFactory;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordComparator;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.validationmgr.RecordSetValidator;

import java.util.Date;
import java.util.logging.Logger;

/**
 * This class implements validation of dusplicates.
 * <p/>
 * <p/>
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
 * 09/04/2007       sxm         1) Added idFieldName
 *                              2) Pass field ID and row ID to addErrorMessage()
 * 01/13/2014       Elvin       Issue 150225: Add messageGridId and parentIdField for multiple grids
 * 04/02/2014       ldong       Issue 152335
 * ---------------------------------------------------
 */
public class ContinuityRecordSetValidator implements RecordSetValidator {

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
            else
                rc = new RecordComparator(getEffectiveFromDateFieldName(), ConverterFactory.getInstance().getConverter(Date.class));
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
                        break;
                    } else if (currentRecord.hasFieldValue(keyFieldName) &&
                            !currentRecord.getFieldValue(keyFieldName).equals(priorRecord.getFieldValue(keyFieldName))) {
                        keyChanged = true;
                        break;
                    }
                }

                // set effective from date
                Date effectiveFromDate = currentRecord.getDateValue(getEffectiveFromDateFieldName());

                // invalid if there is a overlap or gap within the group of records with same key values
                if (!keyChanged && (effectiveToDate.after(effectiveFromDate) || effectiveToDate.equals(effectiveFromDate)
                        || (getValidateGap() && (effectiveToDate.before(effectiveFromDate))))) {
                    isValid = false;

                    // set row numbers as message parameters first
                    int numParms = getParmFieldNames().length;
                    Object[] parms = new Object[numParms + 2];

                    int currentRowNumber = currentRecord.getRecordNumber() + 1;
                    int priorRowNumber = priorRecord.getRecordNumber() + 1;
                    if (getRowNumberFieldName() != null) {
                        currentRowNumber = currentRecord.getIntegerValue(getRowNumberFieldName()).intValue() + 1;
                        priorRowNumber = priorRecord.getIntegerValue(getRowNumberFieldName()).intValue() + 1;
                    }
                    if (currentRowNumber < priorRowNumber) {
                        parms[0] = String.valueOf(currentRowNumber);
                        parms[1] = String.valueOf(priorRowNumber);
                    } else {
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
                    
                    String rowId = currentRecord.getStringValue(getIdFieldName());
                    if (!StringUtils.isBlank(getParentIdField())) {
                        rowId = currentRecord.getStringValue(getParentIdField()) + "," + rowId;
                    }

                    // add message
                    MessageManager.getInstance().addErrorMessage(getMessageKey(), parms,
                            "", rowId, getMessageGridId());
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
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public ContinuityRecordSetValidator() {
    }

    public ContinuityRecordSetValidator(String effectiveFromDateFieldName, String effectiveToDateFieldName,
                                        String idFieldName, String messageKey) {
        setEffectiveFromDateFieldName(effectiveFromDateFieldName);
        setEffectiveToDateFieldName(effectiveToDateFieldName);
        setIdFieldName(idFieldName);
        setMessageKey(messageKey);
        setKeyFieldNames(new String[0]);
        setParmFieldNames(new String[0]);
        setValidateGap(false);
    }

    public ContinuityRecordSetValidator(String effectiveFromDateFieldName, String effectiveToDateFieldName,
                                        String idFieldName, String rowNumberFieldName, String messageKey) {
        setEffectiveFromDateFieldName(effectiveFromDateFieldName);
        setEffectiveToDateFieldName(effectiveToDateFieldName);
        setIdFieldName(idFieldName);
        setMessageKey(messageKey);
        setKeyFieldNames(new String[0]);
        setParmFieldNames(new String[0]);
        setRowNumberFieldName(rowNumberFieldName);
        setValidateGap(false);
    }

    public ContinuityRecordSetValidator(String effectiveFromDateFieldName, String effectiveToDateFieldName,
                                        String idFieldName, String messageKey, boolean validateGap) {
        setEffectiveFromDateFieldName(effectiveFromDateFieldName);
        setEffectiveToDateFieldName(effectiveToDateFieldName);
        setIdFieldName(idFieldName);
        setMessageKey(messageKey);
        setKeyFieldNames(new String[0]);
        setParmFieldNames(new String[0]);
        setValidateGap(validateGap);
    }

    public ContinuityRecordSetValidator(String effectiveFromDateFieldName, String effectiveToDateFieldName,
                                        String idFieldName, String messageKey, String[] keyFieldNames, String[] parmFieldNames) {
        setEffectiveFromDateFieldName(effectiveFromDateFieldName);
        setEffectiveToDateFieldName(effectiveToDateFieldName);
        setIdFieldName(idFieldName);
        setMessageKey(messageKey);
        setKeyFieldNames(keyFieldNames);
        setParmFieldNames(parmFieldNames);
        setValidateGap(false);
    }

    public ContinuityRecordSetValidator(String effectiveFromDateFieldName, String effectiveToDateFieldName,
                                        String idFieldName, String messageKey, String[] keyFieldNames, String[] parmFieldNames, boolean validateGap) {
        setEffectiveFromDateFieldName(effectiveFromDateFieldName);
        setEffectiveToDateFieldName(effectiveToDateFieldName);
        setIdFieldName(idFieldName);
        setMessageKey(messageKey);
        setKeyFieldNames(keyFieldNames);
        setParmFieldNames(parmFieldNames);
        setValidateGap(validateGap);
    }

    public ContinuityRecordSetValidator(String effectiveFromDateFieldName, String effectiveToDateFieldName,
                                        String idFieldName, String messageKey, String[] keyFieldNames, String[] parmFieldNames, String rowNumberFieldName, boolean validateGap) {
        setEffectiveFromDateFieldName(effectiveFromDateFieldName);
        setEffectiveToDateFieldName(effectiveToDateFieldName);
        setIdFieldName(idFieldName);
        setMessageKey(messageKey);
        setKeyFieldNames(keyFieldNames);
        setParmFieldNames(parmFieldNames);
        setRowNumberFieldName(rowNumberFieldName);
        setValidateGap(validateGap);
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

    public boolean getValidateGap() {
        return m_validateGap;
    }

    public void setValidateGap(boolean validateGap) {
        m_validateGap = validateGap;
    }


    public String getRowNumberFieldName() {
        return m_rowNumberFieldName;
    }

    public void setRowNumberFieldName(String rowNumberFieldName) {
        m_rowNumberFieldName = rowNumberFieldName;
    }

    public String getMessageGridId() {
        return m_messageGridId;
    }

    public void setMessageGridId(String messageGridId) {
        m_messageGridId = messageGridId;
    }

    public String getParentIdField() {
        return m_parentIdField;
    }

    public void setParentIdField(String parentIdField) {
        m_parentIdField = parentIdField;
    }

    private String m_effectiveFromDateFieldName;
    private String m_effectiveToDateFieldName;
    private String m_rowNumberFieldName;
    private String m_idFieldName;
    private String m_messageKey;
    private String[] m_keyFieldNames;
    private String[] m_parmFieldNames;
    private boolean m_validateGap;
    private String m_messageGridId;
    private String m_parentIdField;
}