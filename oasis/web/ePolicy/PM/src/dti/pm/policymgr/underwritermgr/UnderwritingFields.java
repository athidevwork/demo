package dti.pm.policymgr.underwritermgr;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import org.omg.CORBA.PUBLIC_MEMBER;

/**
 * Constants for Coverage.
 * <p/>
 * <p>(C) 2013 Delphi Technology, inc. (dti)</p>
 * Date:   06/05/2013
 *
 * @author Awu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/25/2014       kxiang      158853 - Added fields ENTITY_ID_GH and ENTITY_ID_HREF to get href value in WebWB.
 * ---------------------------------------------------
 */
public class UnderwritingFields {
    public static final String ENTITY_ID = "entityId";
    public static final String TYPE = "uwTypeCode";
    public static final String EFFECTIVE_FROM_DATE = "effectiveFromDate";
    public static final String EFFECTIVE_TO_DATE = "effectiveToDate";
    public static final String RENEWAL_B = "renewalB";
    public static final String ADD_TEAM_B = "addTeamB";
    public static final String UNDERWRITER_PARAMS = "underwriterParams";
    public static final String UNDERWRITING_TEAM = "regionalTeamCode";
    public static final String ENTITY_ROLE_ID = "entityRoleId";
    public static final String TRANSFER_TEAM_B = "transferTeamB";
    public static final String FROM_ENTITY_ID = "fromEntityId";
    public static final String TO_ENTITY_ID = "toEntityId";
    public static final String EXPIRED_B = "expiredB";
    public static final String ENTITY_ID_GH = "entityId_GH";
    public static final String ENTITY_ID_HREF = "entityIdHref";

    public static void setEntityId(Record record, String entityId) {
        record.setFieldValue(ENTITY_ID, entityId);
    }

    public static String getEntityId(Record record) {
        return record.getStringValue(ENTITY_ID);
    }

    public static void setToEntityId(Record record, String toEntityId) {
        record.setFieldValue(TO_ENTITY_ID, toEntityId);
    }

    public static String getToEntityId(Record record) {
        return record.getStringValue(TO_ENTITY_ID);
    }

    public static void setFromEntityId(Record record, String fromEntityId) {
        record.setFieldValue(FROM_ENTITY_ID, fromEntityId);
    }

    public static String getFromEntityId(Record record) {
        return record.getStringValue(FROM_ENTITY_ID);
    }
    
    public static void setTransferTeam(Record record, String transferTeamB) {
        record.setFieldValue(TRANSFER_TEAM_B, transferTeamB);
    }

    public static String getTransferTeam(Record record) {
        return record.getStringValue(TRANSFER_TEAM_B);
    }
    
    public static void setEntityRoleId(Record record, String entityRoleId) {
        record.setFieldValue(ENTITY_ROLE_ID, entityRoleId);
    }

    public static String getEntityRoleId(Record record) {
        return record.getStringValue(ENTITY_ROLE_ID);
    }

    public static void setUnderwritingTeam(Record record, String underwritingTeam) {
        record.setFieldValue(UNDERWRITING_TEAM, underwritingTeam);
    }

    public static String getUnderwritingTeam(Record record) {
        return record.getStringValue(UNDERWRITING_TEAM);
    }

    public static void setType(Record record, String type) {
        record.setFieldValue(TYPE, type);
    }

    public static String getType(Record record) {
        return record.getStringValue(TYPE);
    }

    public static void setEffectiveFromDate(Record record, String effectiveFromDate) {
        record.setFieldValue(EFFECTIVE_FROM_DATE, effectiveFromDate);
    }

    public static String getEffectiveFromDate(Record record) {
        return record.getStringValue(EFFECTIVE_FROM_DATE);
    }

    public static void setEffectiveToDate(Record record, String effectiveToDate) {
        record.setFieldValue(EFFECTIVE_TO_DATE, effectiveToDate);
    }

    public static String getEffectiveToDate(Record record) {
        return record.getStringValue(EFFECTIVE_TO_DATE);
    }

    public static void setRenewalB(Record record, YesNoFlag renewalB) {
        record.setFieldValue(RENEWAL_B, renewalB);
    }

    public static YesNoFlag getRenewalB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(RENEWAL_B));
    }

    public static void setAddTeamB(Record record, String addTeamB) {
        record.setFieldValue(ADD_TEAM_B, addTeamB);
    }

    public static String getAddTeamB(Record record) {
        return record.getStringValue(ADD_TEAM_B);
    }

    public static void setUnderwriterParams(Record record, String underwriterParams) {
        record.setFieldValue(UNDERWRITER_PARAMS, underwriterParams);
    }

    public static String getUnderwriterParams(Record record) {
        return record.getStringValue(UNDERWRITER_PARAMS);
    }

    public static YesNoFlag getExpiredB(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(EXPIRED_B));
    }

    public static void setExpiredB(Record record, YesNoFlag expiredB) {
        record.setFieldValue(EXPIRED_B, expiredB);
    }


    public class UnderwritingCodeValues {
        public static final String UNDERWRITER = "UNDWRITER";
    }
    
    public static void setEntityIdHref(Record record, String entityIdHref) {
        record.setFieldValue(ENTITY_ID_HREF, entityIdHref);
    }

    public static String getEntityIdHref(Record record) {
        return record.getStringValue(ENTITY_ID_HREF);
    }
}
