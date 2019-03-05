package dti.pm.renewalquestionnairemgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.StringUtils;
import dti.pm.renewalquestionnairemgr.QuestionnaireMailingEventFields;

import java.util.ArrayList;
import java.util.List;

/**
 * This class extends the default record load processor to enforce entitlements for questionnaire for mailing event web page.
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
 * 09/14/2010       syang       Issue 106500 - Modified postProcessRecord() to set receivedB, resendB and capturedB
 *                                             to -1/0 for checkbox display.
 * ---------------------------------------------------
 */
public class QuestionnaireRecordLoadProcessor implements RecordLoadProcessor {
    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     * @return true if this Record should be added to the RecordSet;
     *         false if this Record should be excluded from the RecordSet.
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        String nameUpcase = "";
        if (record.hasStringValue(QuestionnaireMailingEventFields.POLICY_HOLDER_NAME)) {
            String holderName = record.getStringValue(QuestionnaireMailingEventFields.POLICY_HOLDER_NAME);
            if (!StringUtils.isBlank(holderName)) {
                nameUpcase = holderName.toUpperCase();
            }
        }
        QuestionnaireMailingEventFields.setPolicyHolderNameUpcase(record, nameUpcase);
        if (MAILING_NO != -1 && MAILING_NO < LAST_MAILING) {
            record.setFieldValue("isReceivedBAvailable", "N");
            record.setFieldValue("isCapturedBAvailable", "N");
            record.setFieldValue("isResendBAvailable", "N");
        }
        else {
            record.setFieldValue("isReceivedBAvailable", "Y");
            record.setFieldValue("isCapturedBAvailable", "Y");
            record.setFieldValue("isResendBAvailable", "Y");
        }

        // Set receivedB to -1/0 for checkbox display.
        if (YesNoFlag.getInstance(record.getStringValue("receivedB")).booleanValue()) {
            record.setFieldValue("receivedB", "-1");
        }
        else {
            record.setFieldValue("receivedB", "0");
        }
        // Set resendB to -1/0 for checkbox display..
        if (YesNoFlag.getInstance(record.getStringValue("resendB")).booleanValue()) {
            record.setFieldValue("resendB", "-1");
        }
        else {
            record.setFieldValue("resendB", "0");
        }
        // Set capturedB to -1/0 for checkbox display..
        if (YesNoFlag.getInstance(record.getStringValue("capturedB")).booleanValue()) {
            record.setFieldValue("capturedB", "-1");
        }
        else {
            record.setFieldValue("capturedB", "0");
        }
        return true;
    }

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        if (recordSet.getSize() > 0) {
            Record sumRecord = recordSet.getSummaryRecord();
            sumRecord.setFieldValue("isMarkAllAvailable", "Y");
            sumRecord.setFieldValue("isClearAllAvailable", "Y");
            sumRecord.setFieldValue("isResponseAvailable", "Y");
            sumRecord.setFieldValue("isFilesAvailable", "Y");
            // The Add option availability
            if (TOTAL_MAILINGS == -1) {
                sumRecord.setFieldValue("isAddAvailable", "Y");
            }
            else if (MAILING_NO < LAST_MAILING) {
                sumRecord.setFieldValue("isAddAvailable", "N");
            }
            else if ((MAILING_NO != -1) && (MAILING_NO == TOTAL_MAILINGS)) {
                sumRecord.setFieldValue("isAddAvailable", "Y");
            }
            else if (YesNoFlag.getInstance(ADD_QUEST_B).booleanValue()) {
                sumRecord.setFieldValue("isAddAvailable", "Y");
            }
        }
        else {
            List fieldNameList = new ArrayList();
            fieldNameList.add("rownum");
            fieldNameList.add("isReceivedBAvailable");
            fieldNameList.add("isCapturedBAvailable");
            fieldNameList.add("isResendBAvailable");
            recordSet.addFieldNameCollection(fieldNameList);
        }
    }

    public QuestionnaireRecordLoadProcessor(Record inputRecord) {
        if (inputRecord.hasStringValue("totalMailings")) {
            TOTAL_MAILINGS = inputRecord.getIntegerValue("totalMailings").intValue();
        }
        if (inputRecord.hasStringValue("mailingNo")) {
            MAILING_NO = inputRecord.getIntegerValue("mailingNo").intValue();
        }
        if (inputRecord.hasStringValue("lastMailing")) {
            LAST_MAILING = inputRecord.getIntegerValue("lastMailing").intValue();
        }
        if (inputRecord.hasStringValue("addQuestB")) {
            ADD_QUEST_B = inputRecord.getStringValue("addQuestB");
        }
    }

    private int TOTAL_MAILINGS = -1;
    private int MAILING_NO = -1;
    private int LAST_MAILING = -1;
    private String ADD_QUEST_B = "";
}
