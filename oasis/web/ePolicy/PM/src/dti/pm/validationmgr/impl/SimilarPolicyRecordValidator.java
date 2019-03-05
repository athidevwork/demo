package dti.pm.validationmgr.impl;

import dti.pm.policymgr.PolicyManager;
import dti.oasis.recordset.Record;
import dti.oasis.app.ConfigurationException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.util.LogUtils;
import dti.oasis.messagemgr.ConfirmationFields;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.validationmgr.RecordValidator;

import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 31, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class SimilarPolicyRecordValidator implements RecordValidator {
    /**
     * Validate the given record.
     *
     * @param inputRecord the data Record to validate
     * @return true if the record is valid; otherwise false.
     */
    public boolean validate(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validate", new Object[]{inputRecord});

        boolean policyExistPropmt = false;
        if (!ConfirmationFields.isConfirmed(getMessageKey(), inputRecord)) {
            policyExistPropmt = YesNoFlag.getInstance(getPolicyManager().checkPolicyExistence(inputRecord)).booleanValue();
            if (policyExistPropmt) {
                MessageManager.getInstance().addConfirmationPrompt(getMessageKey());
            }
        }

        l.exiting(getClass().getName(), "validate", Boolean.valueOf(policyExistPropmt));
        return policyExistPropmt;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getPolicyManager() == null)
            throw new ConfigurationException("The required property 'policyManager' is missing.");
    }

    public SimilarPolicyRecordValidator() {
    }

    public SimilarPolicyRecordValidator(String messageKey) {
        setMessageKey(messageKey);
    }

    public PolicyManager getPolicyManager() {
        if (m_policyManager == null)
            m_policyManager = (PolicyManager) ApplicationContext.getInstance().getBean(PolicyManager.BEAN_NAME);
        return m_policyManager;
    }

    public void setPolicyManager(PolicyManager policyManager) {
        m_policyManager = policyManager;
    }

    public String getMessageKey() {
        if (m_messageKey == null) {
            m_messageKey = "pm.similarPolicyRecordValidator.confirm";
        }
        return m_messageKey;
    }

    public void setMessageKey(String messageKey) {
        m_messageKey = messageKey;
    }

    private PolicyManager m_policyManager;
    private String m_messageKey;
}
