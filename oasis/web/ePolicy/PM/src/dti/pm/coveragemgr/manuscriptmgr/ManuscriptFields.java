package dti.pm.coveragemgr.manuscriptmgr;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.pm.busobjs.PMStatusCode;

import java.io.Reader;

/**
 * Constants for Manuscript.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 17, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/26/2007       fcb         75486 - getManuscriptEffectiveToDate added.
 * 10/5/2010        gxc         110250 - More ORIG_... fields added
 * 02/09/2010       gxc         Issue 117088 changed manuscriptOrigEffectiveToDate origEffectiveToDate
 * 02/10/2012       wfu         125055 - Added fields importFilePath, fileContent and rtfFileB.
 * 06/25/2012       tcheng      134650 - Added fields recordModeCode.
 * 07/23/2012       tcheng      135128 - Modified setFileContent support for Oracle Clob type
 * 09/30/2013       xnie        140103 - Added get/set methods for additionalText and origEffectiveFromDate.
 * ---------------------------------------------------
 */
public class ManuscriptFields {
    public static final String MANUSCRIPT_ENDORSEMENT_ID = "manuscriptEndorsementId";
    public static final String RENEWAL_B = "renewB";
    public static final String ORIG_RENEWAL_B = "origRenewB";
    public static final String MANUSCRIPT_EFFECTIVE_FROM_DATE = "manuscriptEffectiveFromDate";
    public static final String MANUSCRIPT_EFFECTIVE_TO_DATE = "manuscriptEffectiveToDate";
    public static final String ORIG_EFFECTIVE_FROM_DATE = "origEffectiveFromDate";
    public static final String ORIG_EFFECTIVE_TO_DATE = "origEffectiveToDate";
    public static final String EFFECTIVE_FROM_DATE = "effectiveFromDate";
    public static final String EFFECTIVE_TO_DATE = "effectiveToDate";
    public static final String FORM_CODE = "formCode";
    public static final String ORIG_FORM_CODE = "origFormCode";
    public static final String TYPE_CODE = "typeCode";
    public static final String VALUE1 = "value1";
    public static final String OFFICIAL_RECORD_ID = "officialRecordId";
    public static final String AFTER_IMAGE_RECORD_B = "afterImageRecordB";
    public static final String ADDITIONAL_TEXT = "additionalText";
    public static final String ORIG_ADDITIONAL_TEXT = "origAdditionalText";
    public static final String ADDL_TEXT = "addlText"; // Indicator for Additional Text field
    public static final String MANUSCRIPT_PREMIUM = "manuscriptPremium"; // maintain manuscript
    public static final String ORIG_MANUSCRIPT_PREMIUM = "origManuscriptPremium"; // maintain manuscript
    public static final String DEFAULT_PREMIUM_AMT = "defaultPremiumAmt"; // add manuscript
    public static final String DURATION = "duration";
    public static final String DURATION_TYPE = "durationType";
    public static final String HAS_DETAIL = "hasDetail";
    public static final String COL_NAME = "colName";
    public static final String COL_LABEL = "colLabel";
    public static final String DATA_TYPE = "dataType";
    public static final String MANUSCRIPT_STATUS = "manuscriptStatus";
    public static final String FILE_NAME = "fileName";
    public static final String ORIG_FILE_NAME = "origFileName";
    public static final String IMPORT_FILE_PATH = "importFilePath";
    public static final String FILE_CONTENT = "fileContent";
    public static final String RTF_FILE_B = "rtfFileB";
    public static final String RECORD_MODE_CODE = "recordModeCode";
    public static final String SAVE_MANUSCRIPT_B = "saveManuscriptB";

    public static String getManuscriptEndorsementId(Record record) {
        return record.getStringValue(MANUSCRIPT_ENDORSEMENT_ID);
    }

    public static void setManuscriptEndorsementId(Record record, String manuscriptEndorsementId) {
        record.setFieldValue(MANUSCRIPT_ENDORSEMENT_ID, manuscriptEndorsementId);
    }

    public static YesNoFlag getRenewalB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(RENEWAL_B));
    }

    public static void setRenewalB(Record record, YesNoFlag renewalB) {
        record.setFieldValue(RENEWAL_B, renewalB);
    }

    public static YesNoFlag getOrigRenewalB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(ORIG_RENEWAL_B));
    }

    public static void setOrigRenewalB(Record record, YesNoFlag origRenewalB) {
        record.setFieldValue(ORIG_RENEWAL_B, origRenewalB);
    }

    public static String getManuscriptEffectiveFromDate(Record record) {
        return record.getStringValue(MANUSCRIPT_EFFECTIVE_FROM_DATE);
    }

    public static void setManuscriptEffectiveFromDate(Record record, String manuscriptEffectiveFromDate) {
        record.setFieldValue(MANUSCRIPT_EFFECTIVE_FROM_DATE, manuscriptEffectiveFromDate);
    }

    public static String getManuscriptEffectiveToDate(Record record) {
        return record.getStringValue(MANUSCRIPT_EFFECTIVE_TO_DATE);
    }

    public static void setManuscriptEffectiveToDate(Record record, String manuscriptEffectiveToDate) {
        record.setFieldValue(MANUSCRIPT_EFFECTIVE_TO_DATE, manuscriptEffectiveToDate);
    }

    public static String getOrigEffectiveFromDate(Record record) {
        return record.getStringValue(ORIG_EFFECTIVE_FROM_DATE);
    }

    public static void setOrigEffectiveFromDate(Record record, String origEffectiveFromDate) {
        record.setFieldValue(ORIG_EFFECTIVE_FROM_DATE, origEffectiveFromDate);
    }

    public static String getOrigEffectiveToDate(Record record) {
        return record.getStringValue(ORIG_EFFECTIVE_TO_DATE);
    }

    public static void setOrigEffectiveToDate(Record record, String origEffectiveToDate) {
        record.setFieldValue(ORIG_EFFECTIVE_TO_DATE, origEffectiveToDate);
    }

    public static void setTypeCode(Record record, String typeCode) {
        record.setFieldValue(TYPE_CODE, typeCode);
    }

    public static void setValue1(Record record, String value1) {
        record.setFieldValue(VALUE1, value1);
    }

    public static String getOfficialRecordId(Record record) {
        return record.getStringValue(OFFICIAL_RECORD_ID);
    }

    public static void setOfficialRecordId(Record record, String officialRecordId) {
        record.setFieldValue(OFFICIAL_RECORD_ID, officialRecordId);
    }

    public static YesNoFlag getAfterImageRecordB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(AFTER_IMAGE_RECORD_B));
    }

    public static void setAfterImageRecordB(Record record, YesNoFlag afterImageRecordB) {
        record.setFieldValue(AFTER_IMAGE_RECORD_B, afterImageRecordB);
    }

    public static YesNoFlag getAddlText(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(ADDL_TEXT).equals("1") ? "Y" : "N");
    }

    public static String getFormCode(Record record) {
        return record.getStringValue(FORM_CODE);
    }

    public static void setFormCode(Record record, String formCode) {
        record.setFieldValue(FORM_CODE, formCode);
    }

    public static String getManuscriptPremium(Record record) {
        return record.getStringValue(MANUSCRIPT_PREMIUM);
    }

    public static void setManuscriptPremium(Record record, String manuscriptPremium) {
        record.setFieldValue(MANUSCRIPT_PREMIUM, manuscriptPremium);
    }

    public static String getDefaultPremiumAmt(Record record) {
        return record.getStringValue(DEFAULT_PREMIUM_AMT);
    }

    public static String getDuration(Record record) {
        return record.getStringValue(DURATION);
    }

    public static String getDurationType(Record record) {
        return record.getStringValue(DURATION_TYPE);
    }

     public static String getEffectiveFromDate(Record record) {
        return record.getStringValue(EFFECTIVE_FROM_DATE);
    }
    public static void setEffectiveFromDate(Record record, String effectiveFromDate) {
        record.setFieldValue(EFFECTIVE_FROM_DATE, effectiveFromDate);
    }

    public static String getEffectiveToDate(Record record) {
        return record.getStringValue(EFFECTIVE_TO_DATE);
    }

    public static void setEffectiveToDate(Record record, String effectiveToDate) {
        record.setFieldValue(EFFECTIVE_TO_DATE, effectiveToDate);
    }

    public static String getOrigFormCode(Record record) {
        return record.getStringValue(ORIG_FORM_CODE);
    }

    public static void setOrigFormCode(Record record, String origFormCode) {
        record.setFieldValue(ORIG_FORM_CODE, origFormCode);
    }

    public static String getFileName(Record record) {
        return record.getStringValue(FILE_NAME);
    }

    public static void setFileName(Record record, String fileName) {
        record.setFieldValue(FILE_NAME, fileName);
    }

    public static String getOrigFileName(Record record) {
        return record.getStringValue(ORIG_FILE_NAME);
    }

    public static void setOrigFileName(Record record, String origFileName) {
        record.setFieldValue(ORIG_FILE_NAME, origFileName);
    }

    public static String getAdditionalText(Record record) {
        return record.getStringValue(ADDITIONAL_TEXT);
    }

    public static void setAdditionalText(Record record, String origAdditionalText) {
        record.setFieldValue(ADDITIONAL_TEXT, origAdditionalText);
    }

    public static String getOrigAdditionalText(Record record) {
        return record.getStringValue(ORIG_ADDITIONAL_TEXT);
    }

    public static void setOrigAdditionalText(Record record, String origAdditionalText) {
        record.setFieldValue(ORIG_ADDITIONAL_TEXT, origAdditionalText);
    }

    public static String getOrigManuscriptPremium(Record record) {
        return record.getStringValue(ORIG_MANUSCRIPT_PREMIUM);
    }

    public static void setOrigManuscriptPremium(Record record, String origManuscriptPremium) {
        record.setFieldValue(ORIG_MANUSCRIPT_PREMIUM, origManuscriptPremium);
    }

    public static YesNoFlag getHasDetail(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(HAS_DETAIL));
    }

    public static String getColLabel(Record record) {
        return record.getStringValue(COL_LABEL);
    }

    public static String getDataType(Record record) {
        return record.getStringValue(DATA_TYPE);
    }

    public static boolean hasManuscriptStatus(Record record) {
        return record.hasStringValue(MANUSCRIPT_STATUS);
    }

    public static PMStatusCode getManuscriptStatus(Record record) {
        Object value = record.getFieldValue(MANUSCRIPT_STATUS);
        PMStatusCode result;
        if (value == null || value instanceof PMStatusCode) {
            result = (PMStatusCode) value;
        }
        else {
            result = PMStatusCode.getInstance(value.toString());
        }
        return result;
    }

    public static void setManuscriptStatus(Record record, PMStatusCode manuscriptStatus) {
        record.setFieldValue(MANUSCRIPT_STATUS, manuscriptStatus);
    }

    public static String getImportFilePath(Record record) {
        return record.getStringValue(IMPORT_FILE_PATH);
    }

    public static void setFileContent(Record record, Reader fileContent) {
        record.setFieldValue(FILE_CONTENT, fileContent);
    }

    public static YesNoFlag getRtfFileB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(RTF_FILE_B));
    }

    public static String getRecordModeCode(Record record) {
        return record.getStringValue(RECORD_MODE_CODE);
    }

    public class DurationTypeValues {
        public static final String YEARS = "YEARS";
        public static final String MONTHS = "MONTHS";
        public static final String WEEKS = "WEEKS";
        public static final String DAYS = "DAYS";
    }
}
