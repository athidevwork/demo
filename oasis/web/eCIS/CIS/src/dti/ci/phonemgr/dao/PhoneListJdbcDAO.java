package dti.ci.phonemgr.dao;

import dti.ci.core.dao.BaseDAO;
import dti.ci.core.dao.StoredProcedureTemplate;
import dti.ci.core.error.ExpMsgConvertor;
import dti.ci.helpers.ICIPhoneNumberConstants;
import dti.oasis.struts.AddSelectIndLoadProcessor;
import dti.oasis.util.*;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Data Access Object for Phone Number.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author Gerald C. Carney
 * Date:   Mar 23, 2004
 * <p/>
 * Revision Date    Revised By  Description
 * ------------------------------------------------------------------
 * 04/01/2005       HXY         Removed singleton implementation. Changed
 *                              Statement to PreparedStatement.
 * 20/07/2006       gjli        Issue No. 61441
 * 04/27/2009       Fred        Changed the phone-number-formatted placeholder
 *  7/2/2010         Blake       Add All source function for issue 103463
 * 05/30/2016       dpang       Issue 149588: retrieve hub data if needed.
 * ------------------------------------------------------------------
 */

public class PhoneListJdbcDAO extends BaseDAO implements PhoneListDAO {
//  /**
//   * Retrieves phone numbers for a given source.
//   * @param conn        Connection object.
//   * @param entityPK    PK of the entity who is either the source of the phone
//   * numbers or one of whose addreseses is the source of the phone numbers.
//   * @param srcRecFK    Source record FK of the source of the phone numbers.
//   * @return DisconnectedResultSet - The list of phone numbers.
//   * @throws Exception
//   */
//  public DisconnectedResultSet retrieveDataResultSet(Connection conn,
//    String entityPK, String srcRecFK)
//    throws Exception {
//    String methodName = "retrieveDataResultSet";
//    String methodDesc = "Class " + this.getClass().getName() +
//      " method " + methodName;
//    Logger lggr = LogUtils.enterLog(this.getClass(), methodName,
//      new Object[] {conn, entityPK, srcRecFK} );
//    entityPK = this.checkPK(entityPK);
//    srcRecFK = this.checkPK(srcRecFK);
//    String phnNumSeparator = "-";
//    String areaCodePrefix = "";
//    String areaCodeSuffix = "";
//    String extPrefix = "x";
//
//
//      String sqlStatement =
//        "SELECT TO_CHAR(phn.phone_number_pk) \"phoneNumberId\", " +  /* col 1 */
//              " TO_CHAR(phn.source_record_fk) \"sourceRecordId\", " + /* col 2 */
//              " TO_CHAR(" + entityPK + ") \"entity_pk\", " + /* col 3 */
//              " '0' \"select_ind\", " + /* col 4 */
//              " phn.phone_number_type_code \"phoneNumberTypeCode\", " + /* col 6 */
//              " TRIM(NVL(cs_report.get_lookup_code('PHONE_NUMBER_TYPE_CODE', phn.phone_number_type_code, 'LONG'), phn.phone_number_type_code)) " +
//              " \"phoneNumberTypeCodeDesc\" , " + /* col 7 */
//              " phn.area_code \"areaCode\", " + /* col 8 */
//              " phn.phone_number \"phoneNumber\", " + /* col 9 */
//              " ci_report.format_phone_num_for_display(phn.phone_number, '', '', " +
//                " '" + PHN_NUMBER_SEPARATOR + "', " +
//                " '', '', '') \"phoneNumberFormatted\", " + /* col 10 */
//              " phn.phone_extension \"phoneExtension\", " + /* col 11 */
//              " ci_report.format_phone_num_for_display(phn.phone_number, phn.area_code, phn.phone_extension, " +
//                " '" + PHN_NUMBER_SEPARATOR + "', " +
//                " '" + AREA_CODE_PREFIX + "', " +
//                " '" + AREA_CODE_SUFFIX + "', " +
//                " '" + EXT_PREFIX + "') \"fullPhoneNumberFormatted\", " + /* col 12 */
//              " NVL(phn.primary_number_b, 'N') \"primaryNumberB\", " + /* col 13 */
//              " DECODE( " +
//                " NVL(phn.primary_number_b, 'N'), " +
//                " 'Y', 'Yes', " +
//                " 'No' " +
//              " ) \"primaryNumberBDesc\", " + /* col 14 */
//              " NVL(phn.usa_number_b, 'Y') \"usaNumberB\", " + /* col 15 */
//              " DECODE( " +
//                " NVL(phn.usa_number_b, 'Y'), " +
//                " 'Y', 'Yes', " +
//                " 'No' " +
//              " ) \"usaNumberBDesc\", " + /* col 16 */
//              " NVL(phn.listed_number_b, 'Y') \"listedNumberB\", " + /* col 17 */
//              " DECODE( " +
//                " NVL(phn.listed_number_b, 'Y'), " +
//                " 'Y', 'Yes', " +
//                " 'No' " +
//              " ) \"listedBDesc\", " + /* col 18 */
//                " NVL(phn.permission_to_release_b, 'N') \"permissionToReleaseB\", " + /* col 19 */
//                " DECODE( " +
//                  " NVL(phn.permission_to_release_b, 'N'), " +
//                  " 'Y', 'Yes', " +
//                  " 'No' " +
//                " ) \"permissionToReleaseBDesc\", " + /* col 19 */
//               " phn.source_table_name \"sourceTableName\", " + /* col 20 */
//                " DECODE( " +
//                " NVL(phn.SOURCE_TABLE_NAME, 'ENTITY'), " +
//                " 'ADDRESS', 'No', " +
//                " 'Yes' " +
//                " ) \"sourceTableNameDesc\" " + /* col 21 */
//            " FROM phone_number phn " +
//          //" WHERE phn.source_record_fk =  " + srcRecFK +
//          " WHERE phn.source_record_fk = ? " +
//          " ORDER BY 12 DESC, 6 ";
//
//
//
//    lggr.fine(methodDesc + ":  SQL statement = " + sqlStatement);
//    try {
//      DisconnectedResultSet rs = new DisconnectedResultSet();
//      //rs = Querier.doQuery(sqlStatement, conn, false);
//      rs = Querier.doQuery(sqlStatement, conn,
//          new Object[] { new QueryParm(Types.BIGINT, Long.parseLong(srcRecFK)) }, false);
//      lggr.exiting(this.getClass().getName(), methodName, rs);
//      return rs;
//    }
//    catch (Exception e) {
//      try {
//        lggr.throwing(this.getClass().getName(), methodName, e);
//        String sqlStmtMsg = methodDesc + ":  " +
//          "SQL statement:  " +
//          sqlStatement;
//        lggr.info(sqlStmtMsg);
//      }
//      catch (Throwable ignore) {
//      }
//      throw e;
//    }
//  }
//
//  /**
//   * Retrieves a result set of all phone numbers for all addresses of a given entity.
//   * @param conn   JDBC connection object.
//   * @param entityPK  Entity PK for which to retrieve all phone numbers for all addresses.
//   * @return DisconnectedResultSet - The result set.
//   * @throws Exception
//   */
//  public DisconnectedResultSet retrieveAllNumsForEntAddresses(Connection conn,
//    String entityPK)
//    throws Exception {
//    String methodName = "retrieveAllNumsForEntAddresses";
//    String methodDesc = "Class " + this.getClass().getName() +
//      " method " + methodName;
//    Logger lggr = LogUtils.enterLog(this.getClass(), methodName,
//      new Object[] {conn, entityPK} );
//    entityPK = this.checkPK(entityPK);
//    String phnNumSeparator = "-";
//    String areaCodePrefix = "";
//    String areaCodeSuffix = "";
//    String extPrefix = "x";
//    String sqlStatement =
//      "SELECT TO_CHAR(phn.phone_number_pk) \"phone_number_pk\", " + /* col 1 */
//            " TRIM(NVL(cs_report.get_lookup_code('PHONE_NUMBER_TYPE_CODE', phn.phone_number_type_code, 'LONG'), phn.phone_number_type_code)) " +
//            " \"phone_number_type_long_desc\", " + /* col 2 */
//            " ci_report.format_phone_num_for_display(phn.phone_number, phn.area_code, phn.phone_extension, " +
//            " '" + phnNumSeparator + "', " +
//            " '" + areaCodePrefix + "', " +
//            " '" + areaCodeSuffix + "', " +
//            " '" + extPrefix + "') \"full_phone_number_formatted\", " + /* col 3 */
//            " DECODE( " +
//              " NVL(phn.primary_number_b, 'N'), " +
//              " 'Y', 'Yes', " +
//              " 'No' " +
//            " ) \"primary_number_b\", " + /* col 4 */
//            " DECODE( " +
//              " NVL(phn.usa_number_b, 'Y'), " +
//              " 'Y', 'Yes', " +
//              " 'No' " +
//            " ) \"usa_number_b\", " + /* col 5 */
//            " DECODE( " +
//              " NVL(phn.listed_number_b, 'Y'), " +
//              " 'Y', 'Yes', " +
//              " 'No' " +
//            " ) \"listed_number_b\", " + /* col 6 */
//            " DECODE( " +
//              " NVL(phn.permission_to_release_b, 'N'), " +
//              " 'Y', 'Yes', " +
//              " 'No' " +
//            " ) \"permission_to_release_b\"," + /* col 7 */
//            " TRIM(NVL(cs_report.get_lookup_code('ADDRESS_TYPE_CODE', addr.address_type_code, 'SHORT'), addr.address_type_code)) " +
//            "  || DECODE( " +
//                  " ci_client_utility.is_address_expired(addr.address_pk), " +
//                  " 'Y', ' (Expired)', " +
//                  " '' " +
//                " ) \"address_type_code_desc_all_num\", " + /* col 8 */
//            " DECODE( " +
//              " NVL(UPPER(addr.primary_address_b), 'N'), " +
//              " 'Y', 'Yes', " +
//              " 'No' " +
//            " ) \"primary_address_b\", " + /* col 9 */
//            " ci_report.format_addr_one_ln_for_display(addr.address_pk) \"address_single_line_all_num\" " + /* col 10 */
//        "FROM address addr, " +
//            " phone_number phn " +
//      " WHERE addr.address_pk = phn.source_record_fk " +
//        //" AND addr.source_record_fk = " + entityPK +
//        " AND addr.source_record_fk = ? " +
//      " ORDER BY NVL(UPPER(addr.primary_address_b), 'N') DESC, " +
//            " TRIM(NVL(cs_report.get_lookup_code('ADDRESS_TYPE_CODE', addr.address_type_code, 'SHORT'), addr.address_type_code)), " +
//            " TRUNC(addr.effective_from_date) DESC, " +
//            " TRUNC(addr.effective_to_date) DESC, " +
//            " NVL(UPPER(phn.primary_number_b), 'N') DESC";
//
//    lggr.fine(methodDesc + ":  SQL statement = " + sqlStatement);
//    try {
//      DisconnectedResultSet rs = new DisconnectedResultSet();
//      //rs = Querier.doQuery(sqlStatement, conn, false);
//      rs = Querier.doQuery(sqlStatement, conn,
//          new Object[] { new QueryParm(Types.BIGINT, Long.parseLong(entityPK)) }, false);
//      lggr.exiting(this.getClass().getName(), methodName, rs);
//      return rs;
//    }
//    catch (Exception e) {
//      try {
//        lggr.throwing(this.getClass().getName(), methodName, e);
//        String sqlStmtMsg = methodDesc + ":  " +
//          "SQL statement:  " +
//          sqlStatement;
//        lggr.info(sqlStmtMsg);
//      }
//      catch (Throwable ignore) {
//      }
//      throw e;
//    }
//  }

    /**
     * save all phone number
     *
     * @param inputRecords
     * @return int
     */
    public int saveAllPhoneNumber(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllPhoneNumber", new Object[]{inputRecords,});
        }

        int updateCount = 0;
        updateCount = StoredProcedureTemplate.doBatchUpdate("CI_WEB_DEMOGRAPHIC.save_all_phone_number", inputRecords);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllPhoneNumber", new Integer(updateCount));
        }
        return updateCount;
    }

     /**
     * To load PhoneNumberList
     *
     * @param inputRecord
     * @return
     */
     public RecordSet getPhoneNumberList(Record inputRecord){
        Logger l = LogUtils.enterLog(getClass(), "getPhoneNumberList", inputRecord);

        RecordSet rs=null;
        StoredProcedureDAO spDao = null;

        try {
            if(inputRecord.getStringValue(ICIPhoneNumberConstants.SOURCE_RECORD_ID).equals(ICIPhoneNumberConstants.SELECT_ALL_SOURCES_VALUE)){
                spDao = StoredProcedureDAO.getInstance("CI_WEB_DEMOGRAPHIC.get_all_phone_number_list");
            }else{
                spDao = StoredProcedureDAO.getInstance(isHubEnabled() ? "ci_web_phone_number_h.get_phone_number_list" : "CI_WEB_DEMOGRAPHIC.get_phone_number_list");
            }
            rs = spDao.execute(inputRecord, AddSelectIndLoadProcessor.getInstance());
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get Phone Number List: " + inputRecord, se);
            l.throwing(getClass().getName(), "getPhoneNumberList", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "getPhoneNumberList", rs);

        return rs;
    }

    @Override
    public RecordSet createSourceRecordLOV(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "createSourceRecordLOV", inputRecord);

        RecordSet rs=null;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CI_WEB_DEMOGRAPHIC.get_source_list_for_phone");
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get Phone Number List: " + inputRecord, se);
            l.throwing(getClass().getName(), "getPhoneNumberList", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "getPhoneNumberList", rs);

        return rs;
    }

    /**
     * Save phone number
     *
     * @param inputRecord
     */
    public Record savePhoneNumber(Record inputRecord){
        Logger l = LogUtils.getLogger(getClass());
        String methodName = "savePhoneNumber";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{inputRecord});
        }
        Record recResult = new Record();
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CI_WEB_DEMOGRAPHIC.save_phone_number");
        try {
            recResult = spDao.executeUpdate(inputRecord);
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to save Phone Number : " + inputRecord, se);
            l.throwing(getClass().getName(), "savePhoneNumber", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName);
        }
        return recResult;
    }

    /**
     * Save phone number
     *
     * @param inputRecord
     */
    public Record savePhoneNumberWs(Record inputRecord){
        Logger l = LogUtils.getLogger(getClass());
        String methodName = "savePhoneNumberWs";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{inputRecord});
        }
        Record recResult = new Record();
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CI_WEB_DEMOGRAPHIC.save_phone_number_ws");
        try {
            recResult = spDao.executeUpdate(inputRecord);
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to save Phone Number : " + inputRecord, se);
            l.throwing(getClass().getName(), "savePhoneNumberWs", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName);
        }
        return recResult;
    }
}
