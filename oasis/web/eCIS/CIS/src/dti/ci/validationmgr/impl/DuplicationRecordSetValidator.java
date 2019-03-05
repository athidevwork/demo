package dti.ci.validationmgr.impl;

import dti.oasis.messagemgr.MessageCategory;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.validationmgr.RecordSetValidator;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   1/9/14
 *
 * @author eouyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/13/2014       Elvin       Issue 150225: use outerRec.id instead of innerRec.id
 *                                      since the innerRec.id may be -9999
 * 03/17/2014       Elvin       Issue 151772: add CaseSensitive property
 * 07/03/2018       kshen       Issue 194134. 1. Enhanced to allow only check changed records.
 *                              2. Enhanced to allow skip to add handle error link.
 * ---------------------------------------------------
 */
public class DuplicationRecordSetValidator implements RecordSetValidator {
    /**
     * Validate inputRecords and check if there's duplications
     *
     * @param inputRecords
     * @return boolean
     */
    public boolean validate(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validate", new Object[]{inputRecords});
        }

        boolean isValid = true;
        Iterator outerIt = inputRecords.getRecords();
        while (outerIt.hasNext()) {
            Record outerRec = (Record) outerIt.next();
            int outerRowNum = outerRec.getRecordNumber() + 1;

            if (this.isChangedRecordsOnly() && !outerRec.isUpdateIndicatorInserted() && !outerRec.isUpdateIndicatorUpdated()) {
                // Skip the deleted and unchanged records.
                continue;
            }

            //String outerKeyValue = outerRec.getStringValue(getKeyFieldName());
            StringBuffer outerKeyValues = new StringBuffer();
            for (int i = 0; i < getKeyFieldNames().length; i++) {
                outerKeyValues.append(",");
                outerKeyValues.append(outerRec.getStringValue(getKeyFieldNames()[i]));
            }
            // Loop through record set to find duplications
            Iterator innerIt = inputRecords.getRecords();
            while (innerIt.hasNext()) {
                Record innerRec = (Record) innerIt.next();
                int innerRowNum = innerRec.getRecordNumber() + 1;
                //String innerKeyValue = innerRec.getStringValue(getKeyFieldName());
                StringBuffer innerKeyValues = new StringBuffer();
                for (int i = 0; i < getKeyFieldNames().length; i++) {
                    innerKeyValues.append(",");
                    innerKeyValues.append(innerRec.getStringValue(getKeyFieldNames()[i]));
                }
                String rowId = outerRec.getStringValue(getIdFieldName());
                if (outerRowNum == innerRowNum) {
                    continue;
                }
                if (compareValue(outerKeyValues.toString(), innerKeyValues.toString())) {
                    // Find duplications, throw validation exception
                    if (!StringUtils.isBlank(getParentIds())) {
                        rowId = getParentIds() + "," + rowId;
                    }
                    // Add error messages
                    if (isShowKeyValueInMessage()) {
                        String keyValues = outerKeyValues.toString().substring(1);
                        if (isShowFieldDescriptionInMessage()) {
                            keyValues = "";
                            for (String key : getKeyFieldNames()) {
                                String value = outerRec.getStringValue(key + "LovLabel", outerRec.getStringValue(key));
                                if (!StringUtils.isBlank(keyValues)) {
                                    keyValues += ",";
                                }
                                keyValues += value;
                            }
                        }
                        addMessage(new String[]{keyValues}, rowId);
                    } else {
                        addMessage(new String[]{String.valueOf(outerRowNum), String.valueOf(innerRowNum)}, rowId);
                    }
                    isValid = false;
                }
                // Break when first duplication found
                if (!isValid) {
                    break;
                }
            }
            // Break when first duplication found
            if (!isValid) {
                break;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validate", Boolean.valueOf(isValid));
        }

        return isValid;
    }
    
    private boolean compareValue(String value1, String value2) {
        if (isCaseSensitive()){
            return value1.equals(value2);
        } else {
            return value1.equalsIgnoreCase(value2);
        }
    }

    /**
     * Add message to message manager.
     * @param messageParams
     */
    protected void addMessage(Object[] messageParams, String rowId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addMessage", new Object[]{messageParams});
        }

        if (getMessageCategory() == null || getMessageCategory().isError()) {
            if (this.isHandleError()) {
                MessageManager.getInstance().addErrorMessage(getMessageKey(), messageParams, getKeyFieldName(), rowId, getMessageGridId());
            } else {
                MessageManager.getInstance().addErrorMessage(getMessageKey(), messageParams);
            }
        } else if (getMessageCategory().isConfirmationPrompt()) {
            MessageManager.getInstance().addConfirmationPrompt(getMessageKey(), messageParams, true, getKeyFieldName(), rowId);
        } else if (getMessageCategory().isWarning()) {
            MessageManager.getInstance().addWarningMessage(getMessageKey(), messageParams);
        } else if (getMessageCategory().isInformation()) {
            MessageManager.getInstance().addInfoMessage(getMessageKey(), messageParams);
        }

        l.exiting(getClass().getName(), "addMessage");
    }

    /**
     * Constructor
     *
     * @param keyFieldName
     * @param idFieldName
     * @param messageKey
     */
    public DuplicationRecordSetValidator(String keyFieldName, String idFieldName, String messageKey) {
        this.setKeyFieldName(keyFieldName);
        this.setIdFieldName(idFieldName);
        this.setMessageKey(messageKey);
    }

    /**
     * Constructor
     *
     * @param keyFieldName
     * @param idFieldName
     * @param messageKey
     * @param messageCategory
     */
    public DuplicationRecordSetValidator(String keyFieldName, String idFieldName, String messageKey, MessageCategory messageCategory) {
        this.setKeyFieldName(keyFieldName);
        this.setIdFieldName(idFieldName);
        this.setMessageKey(messageKey);
        this.setMessageCategory(messageCategory);
    }

    /**
     * Constructor
     *
     * @param keyFieldName
     * @param idFieldName
     * @param messageKey
     * @param parentIds
     */
    public DuplicationRecordSetValidator(String keyFieldName, String idFieldName, String messageKey, String parentIds) {
        this.setKeyFieldName(keyFieldName);
        this.setIdFieldName(idFieldName);
        this.setMessageKey(messageKey);
        this.setParentIds(parentIds);
    }

    /**
     * Constructor
     *
     * @param keyFieldName
     * @param idFieldName
     * @param messageKey
     * @param parentIds
     * @param messageCategory
     */
    public DuplicationRecordSetValidator(String keyFieldName, String idFieldName, String messageKey, String parentIds, MessageCategory messageCategory) {
        this.setKeyFieldName(keyFieldName);
        this.setIdFieldName(idFieldName);
        this.setMessageKey(messageKey);
        this.setParentIds(parentIds);
        this.setMessageCategory(messageCategory);
    }

    /**
     * Constructor
     *
     * @param keyFieldName
     * @param idFieldName
     * @param messageKey
     */
    public DuplicationRecordSetValidator(String keyFieldName, String idFieldName, String messageKey, boolean showKeyValueInMessage) {
        this.setKeyFieldName(keyFieldName);
        this.setIdFieldName(idFieldName);
        this.setMessageKey(messageKey);
        this.setShowKeyValueInMessage(showKeyValueInMessage);
    }

    /**
     * Constructor
     *
     * @param keyFieldName
     * @param idFieldName
     * @param messageKey
     * @param messageCategory
     */
    public DuplicationRecordSetValidator(String keyFieldName, String idFieldName, String messageKey, boolean showKeyValueInMessage, MessageCategory messageCategory) {
        this.setKeyFieldName(keyFieldName);
        this.setIdFieldName(idFieldName);
        this.setMessageKey(messageKey);
        this.setShowKeyValueInMessage(showKeyValueInMessage);
        this.setMessageCategory(messageCategory);
    }

    /**
     * Constructor
     *
     * @param keyFieldNames
     * @param idFieldName
     * @param messageKey
     * @param showKeyValueInMessage
     */
    public DuplicationRecordSetValidator(String[] keyFieldNames, String idFieldName, String messageKey, boolean showKeyValueInMessage) {
        this.setKeyFieldNames(keyFieldNames);
        this.setIdFieldName(idFieldName);
        this.setMessageKey(messageKey);
        this.setShowKeyValueInMessage(showKeyValueInMessage);
    }

    /**
     * Constructor
     *
     * @param keyFieldNames
     * @param idFieldName
     * @param messageKey
     * @param showKeyValueInMessage
     * @param messageCategory
     */
    public DuplicationRecordSetValidator(String[] keyFieldNames, String idFieldName, String messageKey, boolean showKeyValueInMessage, MessageCategory messageCategory) {
        this.setKeyFieldNames(keyFieldNames);
        this.setIdFieldName(idFieldName);
        this.setMessageKey(messageKey);
        this.setShowKeyValueInMessage(showKeyValueInMessage);
        this.setMessageCategory(messageCategory);
    }

    /**
     * Get Id field name
     *
     * @return String
     */
    public String getIdFieldName() {
        return m_idFieldName;
    }

    /**
     * Set Id field name
     *
     * @param idFieldName
     */
    public void setIdFieldName(String idFieldName) {
        m_idFieldName = idFieldName;
    }

    /**
     * Get message key
     *
     * @return String
     */
    public String getMessageKey() {
        return m_messageKey;
    }

    /**
     * set message key
     *
     * @param messageKey
     */
    public void setMessageKey(String messageKey) {
        m_messageKey = messageKey;
    }

    /**
     * Get key field name
     *
     * @return String
     */
    public String getKeyFieldName() {
        return m_keyFieldName;
    }

    /**
     * Set key field name
     *
     * @param keyFieldName
     */
    public void setKeyFieldName(String keyFieldName) {
        m_keyFieldName = keyFieldName;
        this.m_keyFieldNames = new String[]{keyFieldName};
    }

    /**
     * Get parent ids
     *
     * @return String
     */
    public String getParentIds() {
        return m_parentIds;
    }

    /**
     * Set parent ids
     *
     * @param parentIds
     */
    public void setParentIds(String parentIds) {
        m_parentIds = parentIds;
    }

    /**
     * Get showKeyValueInMessage
     *
     * @return boolean
     */
    public boolean isShowKeyValueInMessage() {
        return m_showKeyValueInMessage;
    }

    /**
     * Set showKeyValueInMessage
     *
     * @param showKeyValueInMessage
     */
    public void setShowKeyValueInMessage(boolean showKeyValueInMessage) {
        m_showKeyValueInMessage = showKeyValueInMessage;
    }

    /**
     * Get key field Names
     *
     * @return
     */
    public String[] getKeyFieldNames() {
        return m_keyFieldNames;
    }

    /**
     * Set key field names
     *
     * @param m_keyFieldNames
     */
    public void setKeyFieldNames(String[] m_keyFieldNames) {
        this.m_keyFieldNames = m_keyFieldNames;
    }

    public MessageCategory getMessageCategory() {
        return m_messageCategory;
    }

    public void setMessageCategory(MessageCategory messageCategory) {
        m_messageCategory = messageCategory;
    }

    public boolean isShowFieldDescriptionInMessage() {
        return m_showFieldDescriptionInMessage;
    }

    public void setShowFieldDescriptionInMessage(boolean showFieldDescriptionInMessage) {
        m_showFieldDescriptionInMessage = showFieldDescriptionInMessage;
    }

    public String getMessageGridId() {
        return m_messageGridId;
    }

    public void setMessageGridId(String messageGridId) {
        m_messageGridId = messageGridId;
    }

    public boolean isCaseSensitive() {
        return m_caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        m_caseSensitive = caseSensitive;
    }

    public boolean isChangedRecordsOnly() {
        return m_changedRecordsOnly;
    }

    public void setChangedRecordsOnly(boolean changedRecordsOnly) {
        m_changedRecordsOnly = changedRecordsOnly;
    }

    public boolean isHandleError() {
        return m_handleError;
    }

    public void setHandleError(boolean handleError) {
        m_handleError = handleError;
    }

    private String[] m_keyFieldNames;
    private String m_keyFieldName;
    private String m_idFieldName;
    private String m_messageKey;
    private String m_parentIds;
    private boolean m_showKeyValueInMessage;
    private MessageCategory m_messageCategory;
    private boolean m_showFieldDescriptionInMessage;
    private String m_messageGridId;
    private boolean m_caseSensitive = true;
    private boolean m_changedRecordsOnly = false;
    private boolean m_handleError = true;
}

