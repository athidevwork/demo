package dti.ci.rolemgr;

import dti.ci.helpers.ICIConstants;
import dti.oasis.http.RequestIds;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;

import java.util.logging.Logger;

/**
 * Interface for entity role constants.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author Gerald C. Carney
 *         Date:   Apr 12, 2004
 *         <p/>
 *         Revision Date    Revised By  Description
 *         ---------------------------------------------------
 *         05/30/2018       ylu         Issue 109175: refactor change
 *         ---------------------------------------------------
 */

public class RoleFields implements ICIConstants {
  private final Logger l = LogUtils.getLogger(getClass());

  public static final String ENT_ROLE_TYPE_CODE_ID = "roleTypeCode";
  public static final String ENT_ROLE_EXTERNAL_ID = "externalId";
  public static final String ENT_ROLE_EFF_FROM_DT_ID = "effectiveFromDate";
  public static final String ENT_ROLE_EFF_TO_DT_ID = "effectiveToDate";

  public static final String SELECT_IND = "selectInd";
  public static final String SEARCH_CRITERIA_PREFIX = "searchCriteria_";
  public static final String SEARCH_CRITERIA_ROLE_CODE = "searchCriteria_roleTypeCode";
  public static final String SEARCH_CRITERIA_EFFECTIVE_FROM_DATE = "searchCriteria_effectiveFromDate";
  public static final String SEARCH_CRITERIA_EFFECTIVE_TO_DATE = "searchCriteria_effectiveToDate";
  public static final String SEARCH_CRITERIA_EXTERNAL_ID = "searchCriteria_externalId";

  public static String getSearchCriteriaRoleCode(Record inputRecord) {
    return inputRecord.getStringValue(SEARCH_CRITERIA_ROLE_CODE, "");
  }

  public static String getSearchCriteriaEffectiveFromDate(Record inputRecord) {
    return inputRecord.getStringValue(SEARCH_CRITERIA_EFFECTIVE_FROM_DATE, "");
  }

  public static String getSearchCriteriaEffectiveToDate(Record inputRecord) {
    return inputRecord.getStringValue(SEARCH_CRITERIA_EFFECTIVE_TO_DATE, "");
  }

  public static String getSearchCriteriaExternalId(Record inputRecord) {
    return inputRecord.getStringValue(SEARCH_CRITERIA_EXTERNAL_ID, "");
  }

  public static void setSearchCriteriaRoleCode(Record inputRecord, String roleCode) {
    inputRecord.setFieldValue(SEARCH_CRITERIA_ROLE_CODE, roleCode);
  }

  public static void setSearchCriteriaEffectiveFromDate(Record inputRecord, String effectiveFromDate) {
    inputRecord.setFieldValue(SEARCH_CRITERIA_EFFECTIVE_FROM_DATE, effectiveFromDate);
  }

  public static void setSearchCriteriaEffectiveToDate(Record inputRecord, String effectiveToDate) {
    inputRecord.setFieldValue(SEARCH_CRITERIA_EFFECTIVE_TO_DATE, effectiveToDate);
  }

  public static void setSearchCriteriaExternalId(Record inputRecord, String externalId) {
    inputRecord.setFieldValue(SEARCH_CRITERIA_EXTERNAL_ID, externalId);
  }
}
