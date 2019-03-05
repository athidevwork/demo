package dti.pm.validationmgr.impl;

import dti.pm.policymgr.PolicyManager;
import dti.pm.entitymgr.EntityManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.util.LogUtils;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.app.ConfigurationException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.validationmgr.RecordValidator;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * This class implements validation of Available Policy Type.
 * <p/>
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 23, 2007
 *
 * @author sma
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/04/2007       sxm         Pass filed ID "termEffectiveFromDate" to addErrorMessage().
 * 09/11/2007       sxm         Allow Passing filed ID in as parameter.
 * ---------------------------------------------------
 */
public class AvailablePolicyTypeRecordValidator implements RecordValidator {

    /**
     * Validate the given record.
     *
     * @param inputRecord    the data Record to validate
     * @return true if the record is valid; otherwise false.
     */
    public boolean validate(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validate", new Object[]{inputRecord});

        // get policyholder entity type
        String entityType = getEntityManager().getEntityType(inputRecord.getStringValue("policyHolderNameEntityId"));

        // get available policy types
        inputRecord.setFieldValue("policyHolderEntityType", entityType);
        RecordSet recordSet = getPolicyManager().findAllPolicyType(inputRecord, false);

        // check if the current policy type is valid
        boolean isValid = recordSet.getSubSet(new RecordFilter("policyTypeCode",
            inputRecord.getStringValue("policyTypeCode"))).getSize() == 1;

        if (!isValid) {
            String policyType = inputRecord.hasStringValue("policyTypeCodeLOVLABEL")?
                inputRecord.getStringValue("policyTypeCodeLOVLABEL") : inputRecord.getStringValue("policyTypeCode");
            MessageManager.getInstance().addErrorMessage(getMessageKey(),
                new String[]{String.valueOf(inputRecord.getRecordNumber()+1), policyType}, getFieldId());
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
        if (getEntityManager() == null)
            throw new ConfigurationException("The required property 'entityManager' is missing.");
        if (getPolicyManager() == null)
            throw new ConfigurationException("The required property 'policyManager' is missing.");
    }

    public AvailablePolicyTypeRecordValidator() {
    }

    public AvailablePolicyTypeRecordValidator(String messageKey) {
        setMessageKey(messageKey);
    }

    public AvailablePolicyTypeRecordValidator(String messageKey, String fieldId) {
        setMessageKey(messageKey);
        setFieldId(fieldId);
    }

    public EntityManager getEntityManager() {
        if (m_entityManager == null) {
            m_entityManager = (EntityManager) ApplicationContext.getInstance().getBean(EntityManager.BEAN_NAME);
        }

        return m_entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        m_entityManager = entityManager;
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
        if (m_messageKey == null)
            m_messageKey = "pm.availablePolicyTypeRecordValidator.error";
        return m_messageKey;
    }

    public void setMessageKey(String messageKey) {
        m_messageKey = messageKey;
    }

    public String getFieldId() {
        if (m_fieldId == null)
            m_fieldId = "termEffectiveFromDate";
        return m_fieldId;
    }

    public void setFieldId(String fieldId) {
        m_fieldId = fieldId;
    }

    private EntityManager m_entityManager;
    private PolicyManager m_policyManager;
    private String m_messageKey;
    private String m_fieldId;
}
