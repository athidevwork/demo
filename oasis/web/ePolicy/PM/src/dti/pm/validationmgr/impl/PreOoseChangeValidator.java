package dti.pm.validationmgr.impl;

import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.PMCommonFields;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.util.LogUtils;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.error.ValidationException;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.validationmgr.RecordSetValidator;

import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 13, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/04/2007       sxm         Pass row ID to addErrorMessage()
 * 04/08/2008       yhchen      fix issue 81163
 * ---------------------------------------------------
 */
public class PreOoseChangeValidator implements RecordSetValidator {

    /**
     * Validate the given record set.
     *
     * @param inputRecords a data RecordSet
     * @return true if the RecordSet is valid; otherwise false.
     */
    public boolean validate(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "validate", new Object[]{inputRecords});
        boolean isValid = true;

        RecordSet ooseRecords = inputRecords.getSubSet(
            new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.REQUEST));
        if (ooseRecords.getSize() <= 0) {
            return isValid;
        }

        RecordSet tmpRecords = inputRecords.getSubSet(
            new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.TEMP));

        RecordSet wipRecords = new RecordSet();
        wipRecords.addRecords(ooseRecords);
        wipRecords.addRecords(tmpRecords);
        ArrayList checkedIds = new ArrayList();
        Record invalidRec = null;
        Iterator wipRecIter = wipRecords.getRecords();
        while (wipRecIter.hasNext()) {
            Record wipRec = (Record) wipRecIter.next();
            String id = wipRec.getStringValue(getPkColumnName());
            // avoid duplicate checking
            if (wipRec.getUpdateIndicator().equals("D") || checkedIds.contains(id)) {
                continue;
            }
            String baseRecId = wipRec.getStringValue(getBaseRecordIdName());
            Iterator secondWipRecIter = wipRecords.getRecords();
            while (secondWipRecIter.hasNext()) {
                Record secondWipRec = (Record) secondWipRecIter.next();
                String secondId = secondWipRec.getStringValue(getPkColumnName());
                if (secondWipRec.getUpdateIndicator().equals("D") ||
                    (secondWipRec.getUpdateIndicator().equals(UpdateIndicator.NOT_CHANGED) && wipRec.getUpdateIndicator().equals(UpdateIndicator.NOT_CHANGED))
                    || id.equals(secondId)) {
                    continue;
                }
                String secondBaseRecId = secondWipRec.getStringValue(getBaseRecordIdName());
                if (baseRecId.equals(secondBaseRecId)) {
                    checkedIds.add(id);
                    checkedIds.add(secondId);
                    if (wipRec.getUpdateIndicator().equals(UpdateIndicator.INSERTED)) {
                        invalidRec = wipRec;
                    }
                    if (secondWipRec.getUpdateIndicator().equals(UpdateIndicator.INSERTED)) {
                        invalidRec = secondWipRec;
                    }
                    int rowNum = getRowNumOfInvalidRecord(inputRecords, invalidRec);
                    MessageManager.getInstance().addErrorMessage(getMessageKey(),
                        new String[]{String.valueOf(rowNum), getPageName()}, "", id);
                }
            }
        }

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Pre-change validation failed.");
        }

        l.exiting(getClass().getName(), "validate", Boolean.valueOf(isValid));
        return isValid;
    }

    /**
     * Get the row number for the invalid record
     *
     * @param inputRecords
     * @param invalidRecord
     * @return
     */
    private int getRowNumOfInvalidRecord(RecordSet inputRecords, Record invalidRecord) {
        String id = invalidRecord.getStringValue(getPkColumnName());
        String baseRecId = invalidRecord.getStringValue(getBaseRecordIdName());
        int k = 0, rowNum = 0;
        Iterator recIter = inputRecords.getRecords();
        while (recIter.hasNext()) {
            k++;
            Record rec = (Record) recIter.next();
            String curId = rec.getStringValue(getPkColumnName());
            if (id.equals(curId)) {
                continue;
            }
            String curBaseRecId = rec.getStringValue(getBaseRecordIdName());
            if (!rec.getUpdateIndicator().equals(UpdateIndicator.INSERTED)
                && !rec.getDisplayIndicator().equals("N")
                && curBaseRecId.equals(baseRecId)) {
                rowNum = k;
                break;
            }
        }
        return rowNum;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {

    }

    public PreOoseChangeValidator() {
    }

    public PreOoseChangeValidator(String messageKey, String pageName, String pkColumnName, String baseRecordIdName) {
        setMessageKey(messageKey);
        setPageName(pageName);
        setPkColumnName(pkColumnName);
        setBaseRecordIdName(baseRecordIdName);
    }

    public String getMessageKey() {
        if (m_messageKey == null) {
            m_messageKey = "pm.oose.modified.record.exist.error";
        }
        return m_messageKey;
    }

    public void setMessageKey(String messageKey) {
        m_messageKey = messageKey;
    }

    public String getPageName() {
        return m_pageName;
    }

    public void setPageName(String pageName) {
        m_pageName = pageName;
    }

    public String getPkColumnName() {
        return m_pkColumnName;
    }

    public void setPkColumnName(String pkColumnName) {
        m_pkColumnName = pkColumnName;
    }

    public String getBaseRecordIdName() {
        return m_baseRecordIdName;
    }

    public void setBaseRecordIdName(String baseRecordIdName) {
        m_baseRecordIdName = baseRecordIdName;
    }

    private String m_messageKey;
    private String m_pageName;
    private String m_pkColumnName;
    private String m_baseRecordIdName;
}
