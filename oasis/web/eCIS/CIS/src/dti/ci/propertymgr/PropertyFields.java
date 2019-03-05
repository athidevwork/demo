package dti.ci.propertymgr;


import dti.oasis.recordset.Record;

/**
 * Constants for Property
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 28, 2006
 *
 * @author bhong
 */

/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 *
 * ---------------------------------------------------
*/
public class PropertyFields {
    public static final String PROPERTY_DESCRIPTION = "propertyDescription";
    public static final String FLD_PROPERTY_DESCRIPTION = "clientProperty_propertyDescription";
    public static final String ACQUISITION_DATE = "acquisitionDate";
    public static final String ACQUISITION_PRICE = "acquisitionPrice";
    public static final String YEAR_BUILT = "yearBuilt";
    public static final String BLDG_SQ_FT = "bldgSqFt";
    public static final String NUMBER_OF_FLOORS = "numberOfFloors";
    public static final String NUMBER_OF_BOILERS = "numberOfBoilers";
    public static final String NFIP_FLOOD_ZONE = "nfipFloodZone";

    public static final String FILTER_STREET_PROPERTY = "flt_addressLine1";
    public static final String FILTER_CITY_PROPERTY = "flt_city";
    public static final String FILTER_STATE_PROPERTY = "flt_stateCode";
    public static final String FILTER_ZIPCODE_PROPERTY = "flt_zipCode";
    public static final String PROPERTY_YEAR_COMPUTED ="clientProperty_yearComputed";
    public static final String PROPERTY_DATE_COMPUTED ="clientProperty_dateComputed";
    
    public static String getPropertyDescription(Record record) {
        return record.getStringValue(PROPERTY_DESCRIPTION, "");
    }
    
    public static void setPropertyDescription(Record record, String propertyDescription) {
        record.setFieldValue(PROPERTY_DESCRIPTION, propertyDescription);
    }
    
    public static String getAcquisitionDate(Record record) {
        return record.getStringValue(ACQUISITION_DATE, "");
    }
    
    public static void setAcquisitionDate(Record record, String acquisitionDate) {
        record.setFieldValue(ACQUISITION_DATE, acquisitionDate);
    }
    
    public static String getAcquisitionPrice(Record record) {
        return record.getStringValue(ACQUISITION_PRICE, "");
    }
    
    public static void setAcquisitionPrice(Record record, String acquisitionPrice) {
        record.setFieldValue(ACQUISITION_PRICE, acquisitionPrice);
    }
    
    public static String getYearBuilt(Record record) {
        return record.getStringValue(YEAR_BUILT, "");
    }
    
    public static void setYearBuilt(Record record, String yearBuilt) {
        record.setFieldValue(YEAR_BUILT, yearBuilt);
    }
    
    public static String getBldgSqFt(Record record) {
        return record.getStringValue(BLDG_SQ_FT, "");
    }

    public static void setBldgSqFt(Record record, String bldgSqFt){
        record.setFieldValue(BLDG_SQ_FT, bldgSqFt);
    }
    
    public static String getNumberOfFloors(Record record) {
        return record.getStringValue(NUMBER_OF_FLOORS, "");
    }
    
    public static void setNumberOfFloors(Record record, String numberOfFloors) {
        record.setFieldValue(NUMBER_OF_FLOORS, numberOfFloors);
    }
    
    public static String getNumberOfBoilers(Record record) {
        return record.getStringValue(NUMBER_OF_BOILERS, "");
    }
    
    public static void setNumberOfBoilers(Record record, String numberOfBoilers) {
        record.setFieldValue(NUMBER_OF_BOILERS, numberOfBoilers);
    }

    public static String getNfipFloodZone(Record record) {
        return record.getStringValue(NFIP_FLOOD_ZONE, "");
    }
    
    public static void setNfipFloodZone(Record record, String nfipFloodZone) {
        record.setFieldValue(NFIP_FLOOD_ZONE, nfipFloodZone);
    }
}
