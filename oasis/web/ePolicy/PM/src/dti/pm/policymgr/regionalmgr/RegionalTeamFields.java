package dti.pm.policymgr.regionalmgr;

import dti.oasis.recordset.Record;

/**
 * Regional team fields.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 19, 2008
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/15/2013       awu         138241 - Added UNDERWRITING_TYPE_CODE and  UNDERWRITER.
 * 11/25/2014       kxiang      158853 - Added fields ENTITY_ID_GH and ENTITY_ID_HREF to get href value in WebWB.
 * ---------------------------------------------------
 */
public class RegionalTeamFields {

    public static final String UNDERWERITER_SEQUENCE_ID = "underwriterSequenceId";
    public static final String REGIONAL_TEAM_ID = "regionalTeamId";
    public static final String UNDERWRITER_ID = "underwriterId";
    public static final String ENTITY_ID = "entityId";
    public static final String EFFECTIVE_FROM_DATE = "effectiveFromDate";
    public static final String EFFECTIVE_TO_DATE = "effectiveToDate";
    public static final String REGIONAL_TEAM_CODE = "regionalTeamCode";
    public static final String DESCRIPTION = "description";
    public static final String UNDERWRITING_TYPE_CODE = "uwTypeCode";
    public static final String ENTITY_ID_GH = "entityId_GH";
    public static final String ENTITY_ID_HREF = "entityIdHref";

    public static String getUnderwritingTypeCode(Record record) {
        return record.getStringValue(UNDERWRITING_TYPE_CODE);
    }

    public static void setUnderwritingTypeCode(Record record, String underwritingTypeCode) {
        record.setFieldValue(UNDERWRITING_TYPE_CODE, underwritingTypeCode);
    }

    public static String getUnderwriterSequenceId(Record record) {
        return record.getStringValue(UNDERWERITER_SEQUENCE_ID);
    }

    public static void setUnderwriterSequenceId(Record record, String underwriterSequenceId) {
        record.setFieldValue(UNDERWERITER_SEQUENCE_ID, underwriterSequenceId);
    }

    public static String getRegionalTeamId(Record record) {
        return record.getStringValue(REGIONAL_TEAM_ID);
    }

    public static void setRegionalTeamId(Record record, String regionalTeamId) {
        record.setFieldValue(REGIONAL_TEAM_ID, regionalTeamId);
    }

    public static String getUnderwriterId(Record record) {
        return record.getStringValue(UNDERWRITER_ID);
    }

    public static void setUnderwriterId(Record record, String underwriterId) {
        record.setFieldValue(UNDERWRITER_ID, underwriterId);
    }

    public static String getEntityId(Record record) {
        return record.getStringValue(ENTITY_ID);
    }

    public static void setEntityId(Record record, String entityId) {
        record.setFieldValue(ENTITY_ID, entityId);
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

    public static String getRegionalTeamCode(Record record) {
        return record.getStringValue(REGIONAL_TEAM_CODE);
    }

    public static void setRegionalTeamCode(Record record, String regionalTeamCode) {
        record.setFieldValue(REGIONAL_TEAM_CODE, regionalTeamCode);
    }

    public static String getDescription(Record record) {
        return record.getStringValue(DESCRIPTION);
    }

    public static void setDescription(Record record, String description) {
        record.setFieldValue(DESCRIPTION, description);
    }

    public class RegionalTeamCodeValues {
        public static final String UNDERWRITER = "UNDWRITER";
    }
    
    public static void setEntityIdHref(Record record, String entityIdHref) {
        record.setFieldValue(ENTITY_ID_HREF, entityIdHref);
    }

    public static String getEntityIdHref(Record record) {
        return record.getStringValue(ENTITY_ID_HREF);
    }
}

