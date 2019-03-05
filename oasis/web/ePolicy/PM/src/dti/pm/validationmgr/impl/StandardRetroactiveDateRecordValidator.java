package dti.pm.validationmgr.impl;

import dti.pm.validationmgr.dao.ValidationDAO;
import dti.pm.policymgr.PolicyHeader;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;
import dti.oasis.validationmgr.RecordValidator;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * This class implements validation of Standard Retroactive Date Rule.
 * <p/>
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 21, 2007
 *
 * @author sma
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/31/2007       sxm         1) Added idFieldName
 *                              2) Pass field ID and row ID to addErrorMessage()
 * ---------------------------------------------------
 */
public class StandardRetroactiveDateRecordValidator implements RecordValidator {

    /**
     * Validate the given record.
     *
     * @param inputRecord    the data Record to validate
     * @return true if the record is valid; otherwise false.
     */
    public boolean validate(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validate", new Object[]{inputRecord});
        boolean isValid = true;

        Record outputRecord = getValidationDAO().validateRetroactiveDate(inputRecord,
            getPolicyHeader().getRiskHeader().getRiskBaseRecordId(),
            getProductCoverageFieldName(), getEffectiveFromDateFieldName(), getRetroDateFieldName());

        int result = outputRecord.getIntegerValue("retCode").intValue();
        String msg = outputRecord.getStringValue("retMsg");

        if (result < 0) {
            isValid = false;
            MessageManager.getInstance().addErrorMessage(getMessageKey(),
                new String[]{String.valueOf(inputRecord.getRecordNumber()+1), msg}, getRetroDateFieldName(),
                inputRecord.getStringValue(getIdFieldName()));
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validate", Boolean.valueOf(isValid));
        }
        return isValid;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getValidationDAO() == null)
            throw new ConfigurationException("The required property 'validationDAO' is missing.");
    }

    public StandardRetroactiveDateRecordValidator() {
    }

    public StandardRetroactiveDateRecordValidator(PolicyHeader policyHeader, String productCoverageFieldName,
                                                  String effectiveFromDateFieldName, String retroDateFieldName,
                                                  String idFieldName) {
        setPolicyHeader(policyHeader);
        setProductCoverageFieldName(productCoverageFieldName);
        setEffectiveFromDateFieldName(effectiveFromDateFieldName);
        setRetroDateFieldName(retroDateFieldName);
        setIdFieldName(idFieldName);
    }

    public StandardRetroactiveDateRecordValidator(PolicyHeader policyHeader, String productCoverageFieldName,
                                                  String effectiveFromDateFieldName, String retroDateFieldName,
                                                  String idFieldName, String messageKey) {
        setPolicyHeader(policyHeader);
        setProductCoverageFieldName(productCoverageFieldName);
        setEffectiveFromDateFieldName(effectiveFromDateFieldName);
        setRetroDateFieldName(retroDateFieldName);
        setIdFieldName(idFieldName);
        setMessageKey(messageKey);
    }

    public ValidationDAO getValidationDAO() {
        if (m_validationDAO == null)
            m_validationDAO = (ValidationDAO) ApplicationContext.getInstance().getBean(ValidationDAO.BEAN_NAME);
        return m_validationDAO;
    }

    public void setValidationDAO(ValidationDAO validationDAO) {
        m_validationDAO = validationDAO;
    }

    public PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    public void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }

    public String getProductCoverageFieldName() {
        return m_productCoverageFieldName;
    }

    public void setProductCoverageFieldName(String productCoverageFieldName) {
        m_productCoverageFieldName = productCoverageFieldName;
    }

    public String getEffectiveFromDateFieldName() {
        return m_effectiveFromDateFieldName;
    }

    public void setEffectiveFromDateFieldName(String effectiveFromDateFieldName) {
        m_effectiveFromDateFieldName = effectiveFromDateFieldName;
    }

    public String getRetroDateFieldName() {
        return m_retroDateFieldName;
    }

    public void setRetroDateFieldName(String retroDateFieldName) {
        m_retroDateFieldName = retroDateFieldName;
    }

    public String getIdFieldName() {
        return m_idFieldName;
    }

    public void setIdFieldName(String idFieldName) {
        m_idFieldName = idFieldName;
    }

    public String getMessageKey() {
        if (m_messageKey == null)
            m_messageKey = "pm.standardRetroactiveDateRecordValidator.error";
        return m_messageKey;
    }

    public void setMessageKey(String messageKey) {
        m_messageKey = messageKey;
    }

    private ValidationDAO m_validationDAO;
    private PolicyHeader m_policyHeader;
    private String m_productCoverageFieldName;
    private String m_retroDateFieldName;
    private String m_effectiveFromDateFieldName;
    private String m_idFieldName;
    private String m_messageKey;
}
