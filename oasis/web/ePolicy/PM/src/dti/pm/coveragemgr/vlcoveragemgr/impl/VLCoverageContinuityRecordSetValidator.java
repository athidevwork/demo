package dti.pm.coveragemgr.vlcoveragemgr.impl;

import dti.oasis.converter.ConverterFactory;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordComparator;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.coveragemgr.vlcoveragemgr.VLCoverageFields;
import dti.pm.validationmgr.impl.ContinuityRecordSetValidator;

import java.util.Date;
import java.util.logging.Logger;

/**
 * Validator extends ContinuityRecordSetValidator, used to validate the duplicated records in VL coverage info page
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 7, 2008
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class VLCoverageContinuityRecordSetValidator extends ContinuityRecordSetValidator {

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
                    }
                    else if (currentRecord.hasFieldValue(keyFieldName) &&
                        !currentRecord.getFieldValue(keyFieldName).equals(priorRecord.getFieldValue(keyFieldName))) {
                        keyChanged = true;
                        break;
                    }
                }

                if (!VLCoverageFields.getCompanyInsuredB(currentRecord).booleanValue()) {
                    if (!(!currentRecord.hasStringValue(VLCoverageFields.OFFICIAL_RECORD_ID) ||
                        currentRecord.getStringValue(VLCoverageFields.OFFICIAL_RECORD_ID).equals("0") ||
                        !priorRecord.hasStringValue(VLCoverageFields.OFFICIAL_RECORD_ID) ||
                        priorRecord.getStringValue(VLCoverageFields.OFFICIAL_RECORD_ID).equals("0") ||
                        !currentRecord.getStringValue(VLCoverageFields.OFFICIAL_RECORD_ID).equals(
                            priorRecord.getStringValue(VLCoverageFields.OFFICIAL_RECORD_ID)))) {
                        keyChanged = true;
                        break;
                    }
                }

                // set effective from date
                Date effectiveFromDate = currentRecord.getDateValue(getEffectiveFromDateFieldName());

                // invalid if there is a overlap or gap within the group of records with same key values
                if (!keyChanged && (effectiveToDate.after(effectiveFromDate) ||
                    getValidateGap() && effectiveToDate.before(effectiveFromDate))) {
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
                    MessageManager.getInstance().addErrorMessage(getMessageKey(), parms,
                        "", currentRecord.getStringValue(getIdFieldName()));
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

    public VLCoverageContinuityRecordSetValidator() {
    }

    public VLCoverageContinuityRecordSetValidator(String effectiveFromDateFieldName, String effectiveToDateFieldName, String idFieldName, String messageKey) {
        super(effectiveFromDateFieldName, effectiveToDateFieldName, idFieldName, messageKey);
    }

    public VLCoverageContinuityRecordSetValidator(String effectiveFromDateFieldName, String effectiveToDateFieldName, String idFieldName, String rowNumberFieldName, String messageKey) {
        super(effectiveFromDateFieldName, effectiveToDateFieldName, idFieldName, rowNumberFieldName, messageKey);
    }

    public VLCoverageContinuityRecordSetValidator(String effectiveFromDateFieldName, String effectiveToDateFieldName, String idFieldName, String messageKey, boolean validateGap) {
        super(effectiveFromDateFieldName, effectiveToDateFieldName, idFieldName, messageKey, validateGap);
    }

    public VLCoverageContinuityRecordSetValidator(String effectiveFromDateFieldName, String effectiveToDateFieldName, String idFieldName, String messageKey, String[] keyFieldNames, String[] parmFieldNames) {
        super(effectiveFromDateFieldName, effectiveToDateFieldName, idFieldName, messageKey, keyFieldNames, parmFieldNames);
    }

    public VLCoverageContinuityRecordSetValidator(String effectiveFromDateFieldName, String effectiveToDateFieldName, String idFieldName, String messageKey, String[] keyFieldNames, String[] parmFieldNames, boolean validateGap) {
        super(effectiveFromDateFieldName, effectiveToDateFieldName, idFieldName, messageKey, keyFieldNames, parmFieldNames, validateGap);
    }

    public VLCoverageContinuityRecordSetValidator(String effectiveFromDateFieldName, String effectiveToDateFieldName, String idFieldName, String messageKey, String[] keyFieldNames, String[] parmFieldNames, String rowNumberFieldName, boolean validateGap) {
        super(effectiveFromDateFieldName, effectiveToDateFieldName, idFieldName, messageKey, keyFieldNames, parmFieldNames, rowNumberFieldName, validateGap);
    }
}
