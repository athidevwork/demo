package dti.ci.trainingmgr;

import dti.ci.helpers.ICIConstants;
import dti.oasis.recordset.Record;

/**
 * Interface for CIS training constants.
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 12, 2006
 *
 * @author HXY
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
*/

public interface TrainingFields extends ICIConstants {
    public String TRAINING_LIST_LAYER = "TRAINING_LIST_LAYER";
    public String TRAINING_DETAIL_LAYER = "TRAINING_DETAIL_LAYER";

    public static final String SEL_INSTITUTION_FK = "selinsname";
    public static final String LOV_ENTITY_ROLE = "lovEntityRole";

    public String DATE_OF_BIRTH = "dateOfBirth";
    public static final String STATE_CODE = "stateCode";
    public static final String COUNTRY_CODE = "countryCode";
    public String POLICY_NO = "policyNo";
    public String INSTITUTION_NAME = "institutionName";
    public String TRAINING_TYPE_CODE = "trainingTypeCode";

    public static String getDateOfBirth(Record record) {
        return record.getStringValueDefaultEmpty(DATE_OF_BIRTH);
    }

    public static void setDateOfBirth(Record record, String dateOfBirth) {
        record.setFieldValue(DATE_OF_BIRTH, dateOfBirth);
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
