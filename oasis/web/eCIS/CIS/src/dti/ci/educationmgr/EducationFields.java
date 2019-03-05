package dti.ci.educationmgr;

import dti.oasis.recordset.Record;


/**
 * Constants for Education
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:  May 16, 2006
 *
 * @author gjli
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * 02/05/2009       hxk        Added dateOfDeath
 * ---------------------------------------------------
*/

public class EducationFields {
    public static final String TXT_XML_DATA_UNCHANGED_VALUE = "<RS></RS>";
    public static final String Education_GRIDHEADER_FILENAME = "ciEducationListGrid.xml";
    public static final String SEL_INSTITUTION_FK = "selinsname";
    public static final String LOV_ENTITY_ROLE = "lovEntityRole";
    public static final String DATE_OF_BIRTH = "dateOfBirth";
    public static final String DATE_OF_DEATH = "dateOfDeath";
    public static final String STATE_CODE = "stateCode";
    public static final String COUNTRY_CODE = "countryCode";

    public static String getDateOfBirth(Record record) {
        return record.getStringValue(DATE_OF_BIRTH, "");
    }

    public static void setDateOfBirth(Record record, String dateOfBirth) {
        record.setFieldValue(DATE_OF_BIRTH, dateOfBirth);
    }

    public static String getDateOfDeath(Record record) {
        return record.getStringValue(DATE_OF_DEATH, "");
    }

    public static void setDateOfDeath(Record record, String dateOfDeath) {
        record.setFieldValue(DATE_OF_DEATH, dateOfDeath);
    }

    public static String getStateCode(Record record) {
        return record.getStringValue(STATE_CODE);
    }

    public static void setStateCode(Record record, String stateCode) {
        record.setFieldValue(STATE_CODE, stateCode);
    }

    public static String getCountryCode(Record record) {
        return record.getStringValue(COUNTRY_CODE);
    }

    public static void setCountryCode(Record record, String countryCode) {
        record.setFieldValue(COUNTRY_CODE, countryCode);
    }

}
