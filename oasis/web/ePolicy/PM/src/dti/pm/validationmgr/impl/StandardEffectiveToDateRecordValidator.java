package dti.pm.validationmgr.impl;

import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.util.DateUtils;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.validationmgr.RecordValidator;
import dti.pm.policymgr.PolicyHeader;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements validation of Standard Effective To Date Rule.
 * <p/>
 * <p/>
 * 1.  New expiration date cannot be prior to current effective date.
 * Error message "Expiration date cannot be prior to or equal to effective date."
 * <p/>
 * 2.  Date must be between the transaction effective date and the current term expiration date
 * Error message "Invalid Date.  Date range must be within <transaction effective date here> and <current term expiration date here>."
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
 * 09/22/2011       ryzhao      124458 - Modified validate to use FormatUtils.formatDateForDisplay()
 *                                       to format date when adding error messages.
 * 07/19/2012       awu         134738 - 1) Modified validation to add the logic to check the effextiveToDate if it is null.
 *                                       2) Added originalEffectiveToDateFieldName 
 * ---------------------------------------------------
 */
public class StandardEffectiveToDateRecordValidator implements RecordValidator {

    /**
     * Validate the given record.
     *
     * @param inputRecord    the data Record to validate
     * @return true if the record is valid; otherwise false.
     */
    public boolean validate(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validate", new Object[]{inputRecord});

        boolean isValid = true;

        // do nothing if the date is null
        Date effectiveToDate = inputRecord.getDateValue(getEffectiveToDateFieldName());
        String rowNumber = String.valueOf(inputRecord.getRecordNumber()+1);
        if (effectiveToDate != null) {
            // 1. New expiration date cannot be prior to current effective date.
            Date effectiveFromDate = inputRecord.getDateValue(getEffectiveFromDateFieldName());

            if (effectiveToDate.before(effectiveFromDate)) {
                isValid = false;
                MessageManager.getInstance().addErrorMessage(getMessageKey1(), new String[]{rowNumber},
                    getEffectiveToDateFieldName(), inputRecord.getStringValue(getIdFieldName()));
            }

            // 2. Expiration date must be between the transaction effective date and
            //    the current term expiration date
            String sTransEffectiveDate = getPolicyHeader().getLastTransactionInfo().getTransEffectiveFromDate();
            String sCurTermExpirationDate = getPolicyHeader().getTermEffectiveToDate();

            if (effectiveToDate.before(DateUtils.parseDate(sTransEffectiveDate)) ||
                effectiveToDate.after(DateUtils.parseDate(sCurTermExpirationDate))) {
                isValid = false;
                MessageManager.getInstance().addErrorMessage(getMessageKey2(),
                    new String[]{rowNumber,
                        FormatUtils.formatDateForDisplay(sTransEffectiveDate),
                        FormatUtils.formatDateForDisplay(sCurTermExpirationDate)},
                    getEffectiveToDateFieldName(), inputRecord.getStringValue(getIdFieldName()));
            }
        }
        else {
            isValid = false;
            MessageManager.getInstance().addErrorMessage(getMessageKey3(),
                new String[]{rowNumber},
                getEffectiveToDateFieldName(), inputRecord.getStringValue(getIdFieldName()));
            if (getOriginalEffectiveToDateFieldName() != null) {
                inputRecord.setFieldValue(getEffectiveToDateFieldName(),
                    inputRecord.getStringValue(getOriginalEffectiveToDateFieldName()));
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validate", Boolean.valueOf(isValid));
        }
        return isValid;
    }


    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public StandardEffectiveToDateRecordValidator() {
    }

    public StandardEffectiveToDateRecordValidator(PolicyHeader policyHeader, String effectiveFromDateFieldName,
                                                  String effectiveToDateFieldName, String idFieldName) {
        setPolicyHeader(policyHeader);
        setEffectiveFromDateFieldName(effectiveFromDateFieldName);
        setEffectiveToDateFieldName(effectiveToDateFieldName);
        setIdFieldName(idFieldName);
    }

     public StandardEffectiveToDateRecordValidator(PolicyHeader policyHeader, String effectiveFromDateFieldName,
                                                  String effectiveToDateFieldName, String idFieldName,
                                                  String originalEffectiveToDateFieldName) {
        setPolicyHeader(policyHeader);
        setEffectiveFromDateFieldName(effectiveFromDateFieldName);
        setEffectiveToDateFieldName(effectiveToDateFieldName);
        setIdFieldName(idFieldName);
        setOriginalEffectiveToDateFieldName(originalEffectiveToDateFieldName);
    }

    public StandardEffectiveToDateRecordValidator(PolicyHeader policyHeader, String effectiveFromDateFieldName,
                                                  String effectiveToDateFieldName, String idFieldName,
                                                  String messageKey1, String messageKey2) {
        setPolicyHeader(policyHeader);
        setEffectiveFromDateFieldName(effectiveFromDateFieldName);
        setEffectiveToDateFieldName(effectiveToDateFieldName);
        setIdFieldName(idFieldName);
        setMessageKey1(messageKey1);
        setMessageKey2(messageKey2);
    }

    public PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    public void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
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

    public void setOriginalEffectiveToDateFieldName(String originalEffectiveToDateFieldName) {
        m_originalEffectiveToDateFieldName = originalEffectiveToDateFieldName;
    }

    public String getOriginalEffectiveToDateFieldName() {
        return m_originalEffectiveToDateFieldName;
    }

    public String getIdFieldName() {
        return m_idFieldName;
    }

    public void setIdFieldName(String idFieldName) {
        m_idFieldName = idFieldName;
    }

    public String getMessageKey1() {
        if (m_messageKey1 == null)
            m_messageKey1 = "pm.standardEffectiveToDateRecordValidator.rule1.error";
        return m_messageKey1;
    }

    public void setMessageKey1(String messageKey) {
        m_messageKey1 = messageKey;
    }


    public String getMessageKey2() {
        if (m_messageKey2 == null)
            m_messageKey2 = "pm.standardEffectiveToDateRecordValidator.rule2.error";
        return m_messageKey2;
    }

    public void setMessageKey2(String messageKey2) {
        m_messageKey2 = messageKey2;
    }

    public String getMessageKey3() {
        if (m_messageKey3 == null)
            m_messageKey3 = "pm.standardEffectiveToDateRecordValidator.required.error";
        return m_messageKey3;
    }

    public void setMessageKey3(String messageKey3) {
        m_messageKey3 = messageKey3;
    }

    private PolicyHeader m_policyHeader;
    private String m_effectiveFromDateFieldName;
    private String m_effectiveToDateFieldName;
    private String m_idFieldName;
    private String m_messageKey1;
    private String m_messageKey2;
    private String m_messageKey3;
    private String m_originalEffectiveToDateFieldName;
}
