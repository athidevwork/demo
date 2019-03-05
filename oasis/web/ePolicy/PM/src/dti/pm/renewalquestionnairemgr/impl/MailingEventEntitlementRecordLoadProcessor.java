package dti.pm.renewalquestionnairemgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.StringUtils;
import dti.pm.renewalquestionnairemgr.QuestionnaireMailingEventFields;

import java.util.ArrayList;
import java.util.List;

/**
 * This class extends the default record load processor to enforce entitlements for mailing event web page.
 * This class works in conjunction with pageEntitlements.xml configuration.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   June 23, 2008
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/30/2014       kxiang     When no records found,set "N" to both isPrintAvailable and isSaveAvailable.
 * ---------------------------------------------------
 */
public class MailingEventEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {
    /**
     * Process the given record after it's been loaded.
     * If the comments of current record is not empty,set note to Yes, else default it to No.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     * @return true              if this Record should be added to the RecordSet;
     *         false             if this Record should be excluded from the RecordSet.
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        QuestionnaireMailingEventFields.setNote(record, "No");
        if (record.hasStringValue(QuestionnaireMailingEventFields.COMMENTS)) {
            String comments = record.getStringValue(QuestionnaireMailingEventFields.COMMENTS);
            if (!StringUtils.isBlank(comments)) {
                QuestionnaireMailingEventFields.setNote(record, "Yes");
            }
        }
        int totalMailings = -1;
        int mailingNo = -1;
        int lastMailing = -1;
        String addQuestB = "N";
        if (record.hasStringValue("totalMailings")) {
            totalMailings = record.getIntegerValue("totalMailings").intValue();
        }
        if (record.hasStringValue("mailingNo")) {
            mailingNo = record.getIntegerValue("mailingNo").intValue();
        }
        if (record.hasStringValue("lastMailing")) {
            lastMailing = record.getIntegerValue("lastMailing").intValue();
        }
        if (record.hasStringValue("addQuestB")) {
            addQuestB = record.getStringValue("addQuestB");
        }
        // The Mail option availability
        if (totalMailings == -1) {
            record.setFieldValue("isMailAvailable", "N");
        }
        else if (mailingNo < lastMailing) {
            record.setFieldValue("isMailAvailable", "N");
        }
        else if ((mailingNo != -1) && (mailingNo == totalMailings)) {
            record.setFieldValue("isMailAvailable", "N");
        }
        else if (YesNoFlag.getInstance(addQuestB).booleanValue()) {
            record.setFieldValue("isMailAvailable", "Y");
        }
        return true;
    }

    /**
     * Process the RecordSet after all records have been loaded and processed.
     * If the size of RecordSet is more than zero, show these options.
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        // If recordSet's size is more than zero, show these options.
        if (recordSet.getSize() > 0) {
            Record sumRecord = recordSet.getSummaryRecord();
            sumRecord.setFieldValue("isPrintAvailable", "Y");
            sumRecord.setFieldValue("isSaveAvailable", "Y");
        }
        else {
            Record sumRecord = recordSet.getSummaryRecord();
            sumRecord.setFieldValue("isPrintAvailable", "N");
            sumRecord.setFieldValue("isSaveAvailable", "N");
            List fieldNameList = new ArrayList();
            fieldNameList.add("rownum");
            fieldNameList.add("isMailAvailable");
            recordSet.addFieldNameCollection(fieldNameList);
        }
    }
}
