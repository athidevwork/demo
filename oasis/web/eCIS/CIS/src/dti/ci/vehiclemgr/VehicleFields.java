package dti.ci.vehiclemgr;

import dti.oasis.recordset.Record;

/**
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * User: cyzhao
 * Date: Jan 12, 2011
 */
/*
 *
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class VehicleFields {
    public static final String PK = "pk";
    public static final String ENTITY_ID = "entityId";
    public static final String ENTITY_DESCRIPTION = "entityDescription";
    public static final String VEHICLE_ID = "vehicleId";
    public static final String VEHICLE_DESCRIPTION = "vehicleDescription";
    public static final String YEAR = "year";
    public static final String VEHICLE_NET_WEIGHT = "vehicleNetWeight";
    public static final String VEHICLE_GROSS_WEIGHT = "vehicleGrossWeight";
    public static final String ACQUISITION_DATE = "acquisitionDate";
    public static final String SYSTIME = "systime";
    public static final String VEHICLE_YEAR_LIMIT = "vehicle_vehicleyearlimit";

    public static final String VEHICLE_DESCRIPTION_FIELD_ID = "vehicle_vehicleDescription";
    public static final String YEAR_FIELD_ID = "vehicle_year";
    public static final String VEHICLE_NET_WEIGHT_FIELD_ID = "vehicle_vehicleNetWeight";
    public static final String VEHICLE_GROSS_WEIGHT_FIELD_ID = "vehicle_vehicleGrossWeight";

    public static final int YEAR_START_LIMIT = 1920;

    public static String getEntityDescription(Record record) {
        return record.getStringValue(ENTITY_DESCRIPTION);
    }

    public static void setEntityDescription(Record record, String entityDescription) {
        record.setFieldValue(ENTITY_DESCRIPTION, entityDescription);
    }

    public static String getEntityId(Record record) {
        return record.getStringValue(ENTITY_ID);
    }

    public static void setEntityId(Record record, String entityId) {
        record.setFieldValue(ENTITY_ID, entityId);
    }

    public static String getPk(Record record) {
        return record.getStringValue(PK, "");
    }

    public static void setPk(Record record, String pk) {
        record.setFieldValue(PK, pk);
    }

    public static String getVehicleId(Record record) {
        return record.getStringValue(VEHICLE_ID, "");
    }

    public static void setVehicleId(Record record, String vehicleId) {
        record.setFieldValue(VEHICLE_ID, vehicleId);
    }

    public static String getVehicleDescription(Record record) {
        return record.getStringValue(VEHICLE_DESCRIPTION, "");
    }

    public static void setVehicleDescription(Record record, String vehicleDescription) {
        record.setFieldValue(VEHICLE_DESCRIPTION, vehicleDescription);
    }

    public static String getYear(Record record) {
        return record.getStringValue(YEAR, "");
    }

    public static void setYear(Record record, String year) {
        record.setFieldValue(YEAR, year);
    }

    public static String getVehicleNetWeight(Record record) {
        return record.getStringValue(VEHICLE_NET_WEIGHT, "");
    }

    public static void setVehicleNetWeight(Record record, String vehicleNetWeight) {
        record.setFieldValue(VEHICLE_NET_WEIGHT, vehicleNetWeight);
    }

    public static String getVehicleGrossWeight(Record record) {
        return record.getStringValue(VEHICLE_GROSS_WEIGHT, "");
    }

    public static void setVehicleGrossWeight(Record record, String vehicleGrossWeight) {
        record.setFieldValue(VEHICLE_GROSS_WEIGHT, vehicleGrossWeight);
    }

    public static String getAcquisitionDate(Record record) {
        return record.getStringValue(ACQUISITION_DATE, "");
    }

    public static void setAcquisitionDate(Record record, String acquisitionDate) {
        record.setFieldValue(ACQUISITION_DATE, acquisitionDate);
    }

    public static String getSystime(Record record) {
        return record.getStringValue(SYSTIME, "");
    }

    public static void setSystime(Record record, String systime) {
        record.setFieldValue(SYSTIME, systime);
    }

    public static String getVehicleYearLimit(Record record) {
        return record.getStringValue(VEHICLE_YEAR_LIMIT, "");
    }

    public static void setVehicleYearLimit(Record record, String vehicleYearLimit) {
        record.setFieldValue(VEHICLE_YEAR_LIMIT, vehicleYearLimit);
    }
}
