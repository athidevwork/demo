package dti.pm.validationmgr.impl;

import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.StringUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.validationmgr.RecordValidator;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.RecordMode;
import dti.pm.coveragemgr.CoverageFields;
import dti.pm.policymgr.PolicyFields;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import dti.pm.busobjs.PMRecordSetHelper;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.riskmgr.RiskFields;

/**
 * This class implements validation of Short Term Effective To Date Rule.
 * <p/>
 * <p/>
 * 1.  Expiration date and other fields cannot be changed at the same transaction.
 * <p/>
 * <p>(C) 2014 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 4, 2014
 *
 * @author xnie
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/04/2014       xnie        Issue 156995 Initial version.
 * 02/03/2015       xnie        Issue 156995 Modified validate() to reset offExpirationDate to policy expiration date
 *                                           when offExpirationDate is open date.
 * ---------------------------------------------------
 */
public class ShortTermEffectiveToDateRecordValidator implements RecordValidator {

    /**
     * Validate the given record.
     *
     * @param inputRecord      the data Record to validate
     * @return true if the record is valid; otherwise false.
     */
    public boolean validate(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validate", new Object[]{inputRecord});

        boolean isValid = true;
        String rowNum = "";
        String filterId = getFilterId();
        String level = getLevel();
        String expirationDateName = getExpirationDateName().toUpperCase();
        String rowId = "";
        String id = "";
        String expirationDate = "";
        RecordSet offRecordSet = getOffRecordSet();
        RecordMode recordModeCode = PMCommonFields.getRecordModeCode(inputRecord);
        String offRecId = RiskFields.getOfficialRecordId(inputRecord);
        boolean expirationDateChangedB = false;
        boolean otherFieldsChangedB = false;
        Record offRec = new Record();
        String offExpirationDate = "";
        String fieldList = "";
        String originalExpirationDate = "";

        // Short term Risk
        if (level.equals(PolicyFields.RoleTypeValues.RISK)) {
            rowId = RiskFields.getRiskId(inputRecord);
            id = RiskFields.RISK_ID.toUpperCase();
            expirationDate = RiskFields.getRiskEffectiveToDate(inputRecord);
            originalExpirationDate = RiskFields.getOrigRiskEffectiveToDate(inputRecord);
            rowNum = String.valueOf(inputRecord.getFieldValue(PMRecordSetHelper.DISPLAY_RECORD_NUMBER));
            fieldList = RiskFields.getRiskManualUpdatableFieldsList().get(0).toString().toUpperCase();
        }
        // Short term Coverage
        else {
            rowId = CoverageFields.getCoverageId(inputRecord);
            id = CoverageFields.COVERAGE_ID.toUpperCase();
            expirationDate = CoverageFields.getCoverageEffectiveToDate(inputRecord);
            originalExpirationDate = CoverageFields.getOrigCoverageEffectiveToDate(inputRecord);
            rowNum = String.valueOf(inputRecord.getRecordNumber() + 1);
            fieldList = CoverageFields.getCoverageManualUpdatableFieldsList().get(0).toString().toUpperCase();
        }

        // Temp record from official record
        if (!StringUtils.isBlank(offRecId) && recordModeCode.isTemp()) {
            offRec = offRecordSet.getSubSet(new RecordFilter(filterId, offRecId)).getFirstRecord();
        }
        // Official record
        else {
            offRec = offRecordSet.getSubSet(new RecordFilter(filterId, rowId)).getFirstRecord();
        }

        // Short term Risk
        if (level.equals(PolicyFields.RoleTypeValues.RISK)) {
            offExpirationDate = RiskFields.getRiskEffectiveToDate(offRec);
        }
        // Short term Coverage
        else {
            offExpirationDate = CoverageFields.getCoverageEffectiveToDate(offRec);
        }

        if (offExpirationDate.equals("01/01/3000")) {
            offExpirationDate = getPolicyHeader().getPolicyExpirationDate();
        }

        Iterator fnIter = inputRecord.getFieldNames();
        String fieldName;
        while (fnIter.hasNext()) {
            fieldName = (String) fnIter.next();
            fieldName = fieldName.toUpperCase();

            if (fieldList.contains(fieldName) && !fieldName.equals(id)) {
                if (fieldName.equals(expirationDateName)) {
                    if (!expirationDate.equals(offExpirationDate)) {
                        expirationDateChangedB = true;
                    }
                }
                else {
                    if (offRec.hasField(fieldName) && !otherFieldsChangedB) {
                        String newValue = inputRecord.getStringValue(fieldName, "").trim();
                        String offValue = offRec.getStringValue(fieldName, "").trim();
                        if (!newValue.equals(offValue)) {
                            otherFieldsChangedB = true;
                        }
                    }
                }
            }
        }

        // Change both risk expiration date and other fields value in same transaction is forbidden
        if ((expirationDateChangedB && otherFieldsChangedB)) {
            isValid = false;
            MessageManager.getInstance().addErrorMessage(getMessageKey(), new String[]{rowNum, level});
        }

        // Revert expire/extend operation for a temp record which is from official record is forbidden
        if (!StringUtils.isBlank(offRecId) && recordModeCode.isTemp()
            && (!expirationDateChangedB && !originalExpirationDate.equals(expirationDate))) {
            isValid = false;
            MessageManager.getInstance().addErrorMessage(getMessageKey(), new String[]{rowNum, level});
        }

        // Set value for expire indicator to let back end know user is trying to expire/extend short term record.
        if (expirationDateChangedB && !otherFieldsChangedB) {
            RiskFields.setExpireB(inputRecord, "Y");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validate", Boolean.valueOf(isValid));
        }
        return isValid;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public ShortTermEffectiveToDateRecordValidator() {
    }

    public ShortTermEffectiveToDateRecordValidator(PolicyHeader policyHeader,
                                                   String filterId,
                                                   String level,
                                                   String expirationDateName,
                                                   RecordSet offRecordSet) {
        setPolicyHeader(policyHeader);
        setFilterId(filterId);
        setLevel(level);
        setExpirationDateName(expirationDateName);
        setOffRecordSet(offRecordSet);
    }

    public PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    public void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }

    public String getMessageKey() {
        if (m_messageKey == null)
            m_messageKey = "pm.shortTermEffectiveToDateRecordValidator.rule.error";
        return m_messageKey;
    }

    public void setMessageKey(String messageKey) {
        m_messageKey = messageKey;
    }

    public String getFilterId() {
        return m_filterId;
    }

    public void setFilterId(String filterId) {
        m_filterId = filterId;
    }

    public String getLevel() {
        return m_level;
    }

    public void setLevel(String level) {
        m_level = level;
    }

    public String getExpirationDateName() {
        return m_expirationDateName;
    }

    public void setExpirationDateName(String expirationDateName) {
        m_expirationDateName = expirationDateName;
    }

    public void setOffRecordSet(RecordSet offRecordSet) {
        m_offRecordSet = offRecordSet;
    }

    public RecordSet getOffRecordSet() {
        return m_offRecordSet;
    }

    private PolicyHeader m_policyHeader;
    private String m_messageKey;
    private String m_filterId;
    private String m_level;
    private String m_expirationDateName;
    private RecordSet m_offRecordSet;
}
