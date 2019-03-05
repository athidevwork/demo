package dti.ci.vendoraddressmgr;

import dti.ci.helpers.ICIAddressConstants;
import dti.ci.helpers.ICIConstants;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;

import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   2/27/2018
 *
 * @author dzhang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/05/2018       dzhang      Issue 109177: vendor address refactor
 * ---------------------------------------------------
 */
public class VendorAddressFields implements ICIAddressConstants, ICIConstants {

    public static final String fieldPrefix = "address_";
    public static final String SOURCE_REC_ID_PROPERTY = "sourceRecordId";
    public static final String ADDRESS_ID = "addressId";

    public static String getProcess(Record record) {

        return record.getStringValue(PROCESS_PROPERTY, "");
    }

    public static String getAddressId(Record record) {

        return record.getStringValue(ADDRESS_ID, "");
    }

    public static String getAddressPk(Record record) {

        return record.getStringValue(ADDR_PK_ID, "");
    }

    public static String getSqlOperation(Record record) {

        return record.getStringValue(SQL_OPERATION_PROPERTY, "");
    }

    public static String getProvince(Record record) {

        return record.getStringValue(PROVINCE_ID, "");
    }

    public static String getSourceRecordId(Record record) {

        return record.getStringValue(SOURCE_REC_ID_PROPERTY, "");
    }

    public static String getSourceTableName(Record record) {
        return record.getStringValue(SOURCE_TBL_NAME_PROPERTY, "");
    }

    public static String getCountyCode(Record record) {

        return record.getStringValue(COUNTY_CODE_ID, "");
    }

    public static String getAddressTypeCodeShortDesc(Record record) {

        return record.getStringValue(ADDR_TYPE_CODE_SHORT_DESC, "");
    }

    public static String getPrimaryAddressB(Record record) {
        return record.getStringValue(PRIMARY_ADDR_B_ID, "");
    }

    public static void setPrimaryAddressB(Record record, String primaryAddressB) {
        record.setFieldValue(PRIMARY_ADDR_B_ID, primaryAddressB);
    }

    public static DataRecordMapping dataRecordMapping;

    static {
        dataRecordMapping = new DataRecordMapping();
        dataRecordMapping.addFieldMapping(new DataRecordFieldMapping("addressTypeCode", ADDR_TYPE_CODE_ID));
        dataRecordMapping.addFieldMapping(new DataRecordFieldMapping("addressName", ADDR_NAME_ID));
        dataRecordMapping.addFieldMapping(new DataRecordFieldMapping("addressLine1", LINE_1_ID));
        dataRecordMapping.addFieldMapping(new DataRecordFieldMapping("addressLine2", LINE_2_ID));
        dataRecordMapping.addFieldMapping(new DataRecordFieldMapping("addressLine3", LINE_3_ID));
        dataRecordMapping.addFieldMapping(new DataRecordFieldMapping("city", CITY_ID));
        dataRecordMapping.addFieldMapping(new DataRecordFieldMapping("countyCode",COUNTY_CODE_ID));
        dataRecordMapping.addFieldMapping(new DataRecordFieldMapping("stateCode", STATE_ID));
        dataRecordMapping.addFieldMapping(new DataRecordFieldMapping("zipCode", ZIP_CODE_ID));
        dataRecordMapping.addFieldMapping(new DataRecordFieldMapping("zipPlusFour", ZIP_PLUS_FOUR_ID));
        dataRecordMapping.addFieldMapping(new DataRecordFieldMapping("primaryAddressB", PRIMARY_ADDR_B_ID));
        dataRecordMapping.addFieldMapping(new DataRecordFieldMapping("usaAddressB", USA_ADDR_B_ID));
        dataRecordMapping.addFieldMapping(new DataRecordFieldMapping("postOfficeAddressB", POST_OFC_ADDR_B_ID));
        dataRecordMapping.addFieldMapping(new DataRecordFieldMapping("effectiveFromDate", EFF_FROM_DATE_ID));
        dataRecordMapping.addFieldMapping(new DataRecordFieldMapping("effectiveToDate", EFF_TO_DATE_ID));
        dataRecordMapping.addFieldMapping(new DataRecordFieldMapping("countryCode", COUNTRY_CODE_ID));
        dataRecordMapping.addFieldMapping(new DataRecordFieldMapping("legacyDataId", ADDR_LEGACY_DATA_ID_ID));
        dataRecordMapping.addFieldMapping(new DataRecordFieldMapping("province", PROVINCE_ID));
    }
}
