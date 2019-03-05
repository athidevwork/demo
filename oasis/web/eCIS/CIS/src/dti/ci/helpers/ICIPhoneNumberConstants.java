package dti.ci.helpers;

/**
 * Interface for phone number constants.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author Gerald C. Carney
 *         Date:   Mar 5, 2004
 *         <p/>
 *         Revision Date    Revised By  Description
 *         ---------------------------------------------------
 *         <p/>
 *         <p/>
 *         ---------------------------------------------------
 */

public interface ICIPhoneNumberConstants {

  public static final String PHONE_NUM_PK_ID         = "phoneNumber_phoneNumberPK";
  public static final String AREA_CODE_ID            = "phoneNumber_areaCode";
  public static final String NOT_RELATED_TO_ADDR_ID  = "phoneNumber_notRelatedToAddressBComputed";
  public static final String EXTENSION_ID            = "phoneNumber_phoneExtension";
  public static final String PHONE_NUM_ID            = "phoneNumber_phoneNumber";
  public static final String PHONE_TYPE_CODE_ID      = "phoneNumber_phoneNumberTypeCode";
  public static final String PRIM_NUM_B_ID           = "phoneNumber_primaryNumberB";
  public static final String USA_NUM_B_ID            = "phoneNumber_usaNumberB";
  public static final String LISTED_NUM_B_ID         = "phoneNumber_listedNumberB";
  public static final String PERMISSION_TO_RLS_B_ID  = "phoneNumber_permissionToReleaseB";
  public static final String PHONE_NUM_SRC_TBL_NAME_ID  = "phoneNumber_sourceTableName";
  public static final String PHONE_NUM_SRC_REC_FK_ID    = "phoneNumber_sourceRecordFK";

  public static final String CUR_PHONE_NUM_SRC_REC_FK_PROPERTY = "currentSourceRecordFK";

  public static final String EXPIRED_SOURCE_PREFIX = "X";

  public static final String PHN_NUMBER_SEPARATOR = "-";
  public static final String AREA_CODE_PREFIX = "(";
  public static final String AREA_CODE_SUFFIX = ")";
  public static final String EXT_PREFIX = "x";

  public static final String ENTITY_ID = "entityId";
  public static final String SOURCE_RECORD_ID = "sourceRecordId";
  public static final String PARM_CODE = "parmcode";

  public static final String CI_SHOW_ALL_PHONENUM = "CI_SHOW_ALL_PHONENUM";
  public static final String SHOW_ALL_SOURCES = "Y";
  public static final String NOT_SHOW_ALL_SOURCES = "N";
  public static final String SELECT_ALL_SOURCES = "All Sources";
  public static final String SELECT_ALL_SOURCES_VALUE = "-2";

  public static final String SELECT_SELECT = "-Select-";
  public static final String SELECT_SELECT_VALUE = "-1";

}
