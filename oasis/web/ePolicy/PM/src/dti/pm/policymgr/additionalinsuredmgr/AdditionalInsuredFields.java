package dti.pm.policymgr.additionalinsuredmgr;

import dti.oasis.recordset.Record;
import dti.oasis.busobjs.YesNoFlag;

/**
 * Constants for Addional Insured.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 15, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/23/2008       GXC         Issue 86879: add renewalB field
 * 02/27/2013       xnie        Issue 138026 - Added fields and methods for check if As Of Date is valid.
 * 07/30/2014       kxiang      Issue 155534 - Added fields NAME_GH and NAME_HREF to get href value in WebWB.
 * ---------------------------------------------------
 */
public class AdditionalInsuredFields {


    public static final String EXTERNAL_ID = "externalId";
    public static final String ADDITIONAL_INSURED_ID = "additionalInsuredId";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
    public static final String NEED_TO_CAPTURE_TRANSACTION = "needToCaptureTransaction";
    public static final String ADDRESS_ID = "addressId";
    public static final String RISK_ID = "riskId";
    public static final String ENTITY_ID = "entityId";
    public static final String ADDITIONAL_INSURED_CODE = "additionalInsuredCode";
    public static final String NAME = "name";
    public static final String RENEWAL_B = "renewalB";
    public static final String AS_OF_DATE = "addInsAsOfDate";
    public static final String ADDINS_STATUS = "addInsStatus";
    public static final String SELECT_TO_GENERATE_ADDINS_IDS = "selectToGenerateAddInsIds";
    public static final String NAME_GH = "name_GH";
    public static final String NAME_HREF = "nameHref";

    public static String getName(Record record) {
        return record.getStringValue(NAME);
    }

    public static void setName(Record record, String name) {
        record.setFieldValue(NAME, name);
    }

    public static String getAddressId(Record record) {
        return record.getStringValue(ADDRESS_ID);
    }

    public static void setAddressId(Record record, String addressId) {
        record.setFieldValue(ADDRESS_ID, addressId);
    }

    public static String getRiskId(Record record) {
        return record.getStringValue(RISK_ID);
    }

    public static void setRiskId(Record record, String riskId) {
        record.setFieldValue(RISK_ID, riskId);
    }

    public static String getEntityId(Record record) {
        return record.getStringValue(ENTITY_ID);
    }

    public static void setEntityId(Record record, String entityId) {
        record.setFieldValue(ENTITY_ID, entityId);
    }

    public static String getAdditionalInsuredCode(Record record) {
        return record.getStringValue(ADDITIONAL_INSURED_CODE);
    }

    public static void setAdditionalInsuredCode(Record record, String additionalInsuredCode) {
        record.setFieldValue(ADDITIONAL_INSURED_CODE, additionalInsuredCode);
    }

    public static YesNoFlag getNeedToCaptureTransaction(Record record) {
        return YesNoFlag.getInstance(record.getBooleanValue(NEED_TO_CAPTURE_TRANSACTION, true).booleanValue());
    }

    public static void setNeedToCaptureTransaction(Record record, YesNoFlag needToCaptureTransaction) {
        record.setFieldValue(NEED_TO_CAPTURE_TRANSACTION, needToCaptureTransaction);
    }

    public static String getAdditionalInsuredId(Record record) {
        return record.getStringValue(ADDITIONAL_INSURED_ID);
    }

    public static void setAdditionalInsuredId(Record record, String additionalInsuredId) {
        record.setFieldValue(ADDITIONAL_INSURED_ID, additionalInsuredId);
    }

    public static String getExternalId(Record record) {
        return record.getStringValue(EXTERNAL_ID);
    }

    public static void setExternalId(Record record, String externalId) {
        record.setFieldValue(EXTERNAL_ID, externalId);
    }


    public static String getStartDate(Record record) {
        return record.getStringValue(START_DATE);
    }

    public static void setStartDate(Record record, String startDate) {
        record.setFieldValue(START_DATE, startDate);
    }

    public static String getEndDate(Record record) {
        return record.getStringValue(END_DATE);
    }

    public static void setEndDate(Record record, String endDate) {
        record.setFieldValue(END_DATE, endDate);
    }

    public static void setRenewalB(Record record, String renewalB) {
            record.setFieldValue(RENEWAL_B, renewalB);
    }

    public static String getRenewalB(Record record) {
            return record.getStringValue(RENEWAL_B);
    }

    public static String getAsOfDate(Record record) {
        return record.getStringValue(AS_OF_DATE);
    }

    public static void setAsOfDate(Record record, String asOfDate) {
        record.setFieldValue(AS_OF_DATE, asOfDate);
    }

    public static String getAddInsStatus(Record record) {
        return record.getStringValue(ADDINS_STATUS);
    }

    public static void setAddInsStatus(Record record, String addInsStatus) {
        record.setFieldValue(ADDINS_STATUS, addInsStatus);
    }

    public static String getNameHref(Record record) {
        return record.getStringValue(NAME_HREF);
    }

    public static void setNameHref(Record record, String nameHref) {
        record.setFieldValue(NAME_HREF, nameHref);
    }

}
