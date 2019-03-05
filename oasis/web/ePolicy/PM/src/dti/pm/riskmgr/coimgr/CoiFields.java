package dti.pm.riskmgr.coimgr;

import dti.oasis.recordset.Record;
import dti.oasis.busobjs.YesNoFlag;

/**
 * Helper constants and set/get methods to access COI Fields.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 18, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/02/2011       wfu         127703 - Add field RENEW_B
 * 01/04/2012       wfu         127802 - Add methods for field COI_NAME and RISK_ENTITY_ID.
 * 08/17/2012       xnie        120683 - Added field addressChanges, getAddressChanges(), and setEntityRoleId().
 * 05/31/2013       xnie        145167 - Added field sourceRecordId, sourceIdField, getSourceRecordId(),
 *                                       setSourceRecordId(), getSourceIdRecord(), setSourceIdRecord(),getCoiHolderId(),
 *                                       and setCoiHolderId().
 * 07/30/2014       kxiang      155534 - Added field COI_NAME_GH,COI_NAME_HREF, method setCoiNameHref(),getCoiNameHref().
 * 06/26/2018       dpang       109175 - Modified field id for refactoring 'Entity Role List' page in CIS.
 * ---------------------------------------------------
 */
public class CoiFields {
    public static final String COI_HOLDER_ID = "coiHolderId";
    public static final String COI_EFFECTIVE_FROM_DATE = "effectiveFromDate";
    public static final String COI_EFFECTIVE_TO_DATE = "effectiveToDate";
    public static final String COI_NAME = "coiName";
    public static final String ENTITY_RISK_ID = "entityRiskId";
    public static final String RISK_ENTITY_ID = "riskEntityId"; // the entityId of the COI entity
    public static final String COI_STATUS = "coiStatus";
    public static final String EXCESS_COVERAGE_B = "excessCoverageB";
    public static final String AS_OF_DATE = "coiAsOfDate";
    public static final String SELECT_TO_GENERATE_COI_IDS = "selectToGenerateCoiIds";
    public static final String COI_CUTOFF_DATE = "coiCutoffDate";
    public static final String COI_INCLUDE_EXCLUDE_CLAIM = "coiIncludeExcludeClaim";
    public static final String SELECT_GENERATE_B = "selectGenerateB";
    public static final String RENEW_B = "renewB";
    public static final String ADDRESS_CHANGES = "addressChanges";
    public static final String SOURCE_RECORD_ID = "sourceRecordId";
    public static final String SOURCE_ID_RECORD = "sourceIdField";

    // fields for Generate Client COI
    public static final String EFFECTIVE_FROM_DATE = "effectiveFromDate";
    public static final String EFFECTIVE_TO_DATE = "effectiveToDate";
    public static final String EXTERNAL_ID = "externalId";
    public static final String MINIMUM_DATE = "minimumDate";
    public static final String MAXIMUM_DATE = "maximumDate";
    public static final String ENTITY_ROLE_ID = "entityRoleId";
    public static final String COI_SELECT_LETTER = "coiSelectLetter";
    public static final String COI_NAME_GH = "coiName_GH";
    public static final String COI_NAME_HREF = "coiNameHref";

    public static String getCoiStatus(Record record) {
        return record.getStringValue(COI_STATUS);
    }

    public static void setCoiStatus(Record record, String coiStatus) {
        record.setFieldValue(COI_STATUS, coiStatus);
    }

    public static String getEntityRiskId(Record record) {
        return record.getStringValue(ENTITY_RISK_ID);
    }

    public static void setEntityRiskId(Record record, String entityRiskId) {
        record.setFieldValue(ENTITY_RISK_ID, entityRiskId);
    }

    public static String getCoiEffectiveFromDate(Record record) {
        return record.getStringValue(COI_EFFECTIVE_FROM_DATE);
    }

    public static void setCoiEffectiveFromDate(Record record, String coiEffectiveFromDate) {
        record.setFieldValue(COI_EFFECTIVE_FROM_DATE, coiEffectiveFromDate);
    }

    public static String getCoiEffectiveToDate(Record record) {
        return record.getStringValue(COI_EFFECTIVE_TO_DATE);
    }

    public static void setCoiEffectiveToDate(Record record, String coiEffectiveToDate) {
        record.setFieldValue(COI_EFFECTIVE_TO_DATE, coiEffectiveToDate);
    }

    public static void setExcessCoverageB(Record record, YesNoFlag excessCoverageB) {
        record.setFieldValue(EXCESS_COVERAGE_B, excessCoverageB);
    }

    public static String getAsOfDate(Record record) {
        return record.getStringValue(AS_OF_DATE);
    }

    public static void setAsOfDate(Record record, String asOfDate) {
        record.setFieldValue(AS_OF_DATE, asOfDate);
    }

    public static String getSelectToGenerateCoiIds(Record record) {
        return record.getStringValue(SELECT_TO_GENERATE_COI_IDS);
    }

    public static String getCoiCutoffDate(Record record) {
        return record.getStringValue(COI_CUTOFF_DATE);
    }
    
    public static void setCoiCutoffDate(Record record, String coiCutoffDate) {
        record.setFieldValue(COI_CUTOFF_DATE, coiCutoffDate);
    }

    public static String getCoiIncludeExcludeClaim(Record record) {
        return record.getStringValue(COI_INCLUDE_EXCLUDE_CLAIM);
    }

    public static YesNoFlag getSelectGenerateB(Record record) {
        String selectGenerateB = record.getStringValue(SELECT_GENERATE_B);
        if (selectGenerateB.equals("-1")) {
            selectGenerateB = "Y";
        }
        else if (selectGenerateB.equals("0")) {
            selectGenerateB = "N";
        }
        return YesNoFlag.getInstance(selectGenerateB);
    }

    public static void setSelectGenerateB(Record record, String selectGenerateB) {
        record.setFieldValue(SELECT_GENERATE_B, selectGenerateB);
    }

    public static String getEffectiveFromDate(Record record) {
        return record.getStringValue(EFFECTIVE_FROM_DATE);
    }

    public static String getEffectiveToDate(Record record) {
        return record.getStringValue(EFFECTIVE_TO_DATE);
    }

    public static String getExternalId(Record record) {
        return record.getStringValue(EXTERNAL_ID);
    }

    public static String getMinimumDate(Record record) {
        return record.getStringValue(MINIMUM_DATE);
    }

    public static void setMinimumDate(Record record, String minimumDate) {
        record.setFieldValue(MINIMUM_DATE, minimumDate);
    }

    public static String getMaximumDate(Record record) {
        return record.getStringValue(MAXIMUM_DATE);
    }

    public static void setMaximumDate(Record record, String maximumDate) {
        record.setFieldValue(MAXIMUM_DATE, maximumDate);
    }

    public static String getEntityRoleId(Record record) {
        return record.getStringValue(ENTITY_ROLE_ID);
    }

    public static void setEntityRoleId(Record record, String entityRoleId) {
        record.setFieldValue(ENTITY_ROLE_ID, entityRoleId);
    }

    public static String getCoiSelectLetter(Record record) {
        return record.getStringValue(COI_SELECT_LETTER);
    }

    public static void setRenewB(Record record, YesNoFlag renewB) {
        record.setFieldValue(RENEW_B, renewB);
    }

    public static String getAddressChanges(Record record) {
        return record.getStringValue(ADDRESS_CHANGES);
    }

    public static String getRiskEntityId(Record record) {
        return record.getStringValue(RISK_ENTITY_ID);
    }

    public static void setRiskEntityId(Record record, String riskEntityId) {
        record.setFieldValue(RISK_ENTITY_ID, riskEntityId);
    }

    public static String getCoiName(Record record) {
        return record.getStringValue(COI_NAME);
    }

    public static void setCoiName(Record record, String coiName) {
        record.setFieldValue(COI_NAME, coiName);
    }

    public static String getCoiHolderId(Record record) {
        return record.getStringValue(COI_HOLDER_ID);
    }

    public static void setCoiHolderId(Record record, String coiHolderId) {
        record.setFieldValue(COI_HOLDER_ID, coiHolderId);
    }

    public static String getSourceRecordId(Record record) {
        return record.getStringValue(SOURCE_RECORD_ID);
    }

    public static void setSourceRecordId(Record record, String sourceRecordId) {
        record.setFieldValue(SOURCE_RECORD_ID, sourceRecordId);
    }

    public static String getSourceIdRecord(Record record) {
        return record.getStringValue(SOURCE_ID_RECORD);
    }

    public static void setSourceIdRecord(Record record, String sourceIdRecord) {
        record.setFieldValue(SOURCE_ID_RECORD, sourceIdRecord);
    }

    public static String getCoiNameHref(Record record) {
        return record.getStringValue(COI_NAME_HREF);
    }

    public static void setCoiNameHref(Record record, String coiNameHref) {
        record.setFieldValue(COI_NAME_HREF, coiNameHref);
    }

}
