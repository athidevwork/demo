package dti.ci.helpers.data;

import dti.ci.core.error.PersistenceException;
import dti.ci.helpers.CIAddressHelper;
import dti.ci.helpers.CIHelper;
import dti.ci.helpers.ICIAddressConstants;
import dti.ci.helpers.ICIConstants;
import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.util.DisconnectedResultSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.Querier;
import dti.oasis.util.QueryParm;
import dti.oasis.util.StringUtils;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

/**
 * <p>Data Access Object for Address.</p>
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author Gerald C. Carney
 *         Date:   Jan 22, 2004
 *         <p/>
 *         Revision Date    Revised By  Description
 *         -------------------------------------------------------------------
 *         04/01/2005       HXY         Removed singleton implementation.
 *         Changed Statement to PreparedStatement.
 *         04/14/2005       HXY         Removed commit logic back to BO.
 *         04/22/2005       HXY         return address pk when saving address.
 *         04/27/2005       HXY         Added logic for vendor address page.
 *         09/21/2006       ligj        Issue #62554
 *         02/01/2007       kshen       Issue #61440
 *         (1) Added method retrieveAddressMapForExpire,
 *         retrieveAddressMapForExpire, expireNonPrimaryAddress
 *         for expiring addresses.
 *         (2) Added mehtod buildAddressListStmtWithCountyDesc,
 *         retrieveDataResultSetWithCountyDesc for displaying county
 *         in address list page
 *         08/13/2007       FWCH         Added countryCode,province, postal code
 *         12/03/2007       FWCH         Modified  getVendorAddressSQL() and saveVendorAddressUpdate()
 *         to keep the same logic between retrieving vendor address
 *         and adding vendor address.
 *         09/22/2008       Larry       Issue 86826
 *         10/17/2008       kshen       Changed to load all columns of address data.
 *         11/27/2008       Leo         Issue 88568.
 *         06/23/2009       Fred        Check a string variable is empty or not
 *                                      before invoking chatAt method
 *         08/27/2009       Leo         Issue 95363
 *         11/27/2014       bzhu        Issue 159450
 *                                      1. update country_code if usa.
 *                                      2. clear zip_plus_four if Non-USA.
 *         -------------------------------------------------------------------
 */

public class CIAddressDAO extends CIBaseDAO implements ICIConstants,
        ICIAddressConstants, ICIPKDAO {

    /**
     * Gets SQL for retrieving vendor address data.
     * This sql requires binding entity primary key.
     *
     * @return String - The SELECT statement.
     */
    public String getVendorAddressSQL() {
        final String sqlStatement =
                "SELECT " +
                        " TO_CHAR(addr.address_pk) " + ADDR_PK_ID + ", " + /* col 1 */
                        " addr.address_type_code " + ADDR_TYPE_CODE_ID + ", " + /* col 2 */
                        " TO_CHAR(addr.source_record_fk) " + SOURCE_REC_FK_PROPERTY + ", " + /* col 3 */
                        " addr.source_table_name " + SOURCE_TBL_NAME_PROPERTY + ", " + /* col 4 */
                        " addr.address_name " + ADDR_NAME_ID + ", " + /* col 5 */
                        " addr.address_line1 " + LINE_1_ID + ", " + /* col 6 */
                        " addr.address_line2 " + LINE_2_ID + ", " + /* col 7 */
                        " addr.address_line3 " + LINE_3_ID + ", " + /* col 8 */
                        " addr.city " + CITY_ID + ", " + /* col 9 */
                        " addr.county_code " + COUNTY_CODE_ID + ", " + /* col 10 */
                        " addr.state_code " + STATE_ID + ", " + /* col 11 */
                        " addr.zipcode " + ZIP_CODE_ID + ", " + /* col 12 */
                        " addr.zip_plus_four " + ZIP_PLUS_FOUR_ID + ", " + /* col 13 */
                        " NVL(UPPER(addr.primary_address_b), 'N') " + PRIMARY_ADDR_B_ID + ", " + /* col 14 */
                        " NVL(UPPER(addr.usa_address_b), 'Y') " + USA_ADDR_B_ID + ", " + /* col 15 */
                        " NVL(UPPER(addr.post_office_address_b), 'N') " + POST_OFC_ADDR_B_ID + ", " + /* col 16 */
                        " TO_CHAR(addr.effective_from_date, '" + ORACLE_DATE_FORMAT + "') " + EFF_FROM_DATE_ID + ", " + /* col 17 */
                        " TO_CHAR(addr.effective_to_date, '" + ORACLE_DATE_FORMAT + "') " + EFF_TO_DATE_ID + ", " + /* col 18 */
                        " addr.country_code " + COUNTRY_CODE_ID + ", " + /* col 19 */
                        " addr.legacy_data_id " + ADDR_LEGACY_DATA_ID_ID + ", " + /* col 20 */
                        " addr.province " + PROVINCE_ID + ", " + /* col 21 */
                        " TO_CHAR(addr.expired_address_fk) " + EXPIRED_ADDR_FK_ID + ", " + /* col 22 */
                        " TO_CHAR(addr.copied_address_fk) " + COPIED_ADDR_FK_ID + /* col 23 */
                        " FROM address addr " +
                        " WHERE addr.address_pk = " +
                        "nvl((SELECT max(vndaddr.address_pk) addr_pk" +
                        "      FROM address vndaddr" +
                        "     WHERE vndaddr.source_record_fk = ? " +
                        "       AND vndaddr.address_type_code = " +
                        "           NVL(cs_get_system_parameter('CM_VENDOR_PAYADDR'), 'PAYEE')" +
                        "       and ci_client_utility.is_address_expired(vndaddr.address_pk) = 'N')," +
                        "    (select max(addr2.address_pk) " +
                        "       from address addr2   " +
                        "      where addr2.source_record_fk = ?  " +
                        "        and addr2.address_type_code = 'PAYEE'   " +
                        "        and ci_client_utility.is_address_expired(addr2.address_pk) = 'N'))";
        return sqlStatement;
    }

    /**
     * Gets SQL for retrieving vendor address type info.
     *
     * @return String - The SELECT statement.
     */
    public String getVendorAddressTypeSQL() {
        final String sqlStatement =
                "SELECT " +
                        " code " + ADDR_TYPE_CODE_ID + ", " +
                        " NVL(short_Description, code) shortDesc, " +
                        " NVL(long_description, code) longDesc " +
                        " FROM lookup_code " +
                        " where lookup_type_code = 'ADDRESS_TYPE_CODE' " +
                        " and code = NVL(cs_get_system_parameter('CM_VENDOR_PAYADDR'), 'PAYEE')";
        return sqlStatement;
    }

    /**
     * Builds SQL statement for retrieving addresses for a source.
     * This statement has a binding parameter for srcFK.
     *
     * @param srcFK Source record FK.
     * @return String - The SQL statement.
     */
    public String buildAddressListStmt(String srcFK) {
        srcFK = this.checkPK(srcFK);
        String sqlStatement =
                "SELECT " +
                        " TO_CHAR(addr.address_pk) \"" + ADDR_PK_RS_COL_NAME + "\", " + /* col 1 */
                        " addr.source_table_name \"source_table_name\", " + /* col 2 */
                        " TO_CHAR(addr.source_record_fk) \"source_record_fk\", " + /* col 3 */
                        " 'View' \"view_link\", " + /* col 4 */
                        " DECODE( " +
                        " ci_client_utility.is_address_expired(addr.address_pk), " +
                        " 'N', 'Change', " +
                        " 'Expired' " +
                        " ) " +
                        " \"change_link\", " + /* col 5 */
                        " NVL(UPPER(addr.primary_address_b), 'N') \"" + ADDR_PRIM_ADDR_B_RS_COL_NAME + "\", " + /* col 6 */
                        " DECODE( " +
                        " NVL(UPPER(addr.primary_address_b), 'N'), " +
                        " 'Y', 'Yes', " +
                        " 'No' " +
                        " ) \"primary_address_b_desc\", " + /* col 7 */
                        " addr.address_type_code \"address_type_code\", " + /* col 8 */
                        " LTRIM(RTRIM(NVL(cs_report.get_lookup_code('ADDRESS_TYPE_CODE', addr.address_type_code, 'SHORT'), addr.address_type_code))) " +
                        " \"" + ADDR_TYPE_CODE_DESC_RS_COL_NAME + "\" , " + /* col 9 */
                        " NVL(UPPER(addr.usa_address_b), 'Y') \"usa_address_b\", " + /* col 10 */
                        " DECODE( " +
                        " NVL(UPPER(addr.usa_address_b), 'Y'), " +
                        " 'Y', 'Yes', " +
                        " 'No' " +
                        " ) \"usa_address_b_desc\", " + /* col 11 */
                        " ci_report.format_addr_one_ln_for_display(addr.address_pk) \"" + ADDR_SINGLE_LINE_RS_COL_NAME + "\", " + /* col 12 */
                        " ci_report.get_phone(addr.address_pk) \"phone_number\", " + /* col 13 */
                        "  DECODE( LTRIM(RTRIM(addr.zipcode)),  " +
                        "      '', '',    " +
                        "     ' ' ||     " +
                        "      LTRIM(RTRIM(addr.zipcode)) ||  " +
                        "     DECODE(            " +
                        "         LTRIM(RTRIM(addr.zip_plus_four)),   " +
                        "         '', '',   " +
                        "         '-' || LTRIM(RTRIM(addr.zip_plus_four))" +
                        "      )) addressList_addressUSAZIP  , " +
                        " TO_CHAR(addr.effective_from_date, '" + ORACLE_DATE_FORMAT + "') \"" + ADDR_EFF_FR_DT_RS_COL_NAME + "\", " + /* col 14 */
                        " TO_CHAR(addr.effective_to_date, '" + ORACLE_DATE_FORMAT + "') \"" + ADDR_EFF_TO_DT_RS_COL_NAME + "\", " + /* col 15 */
                        " ci_client_utility.is_address_expired(addr.address_pk) " +
                        " \"" + ADDR_EXPIRED_FLAG_RS_COL_NAME + "\", " + /* col 16 */
                        " addr.address_name \"" + ADDR_NAME_ID + "\", " + /* col 17 */
                        " addr.address_line1 \"" + LINE_1_ID + "\", " + /* col 18 */
                        " addr.address_line2 \"" + LINE_2_ID + "\", " + /* col 19 */
                        " addr.address_line3 \"" + LINE_3_ID + "\", " + /* col 20 */
                        " addr.city \"" + CITY_ID + "\", " + /* col 21 */
                        " addr.county_code \"" + COUNTY_CODE_ID + "\", " + /* col 22 */
                        " addr.state_code \"" + STATE_ID + "\", " + /* col 23 */
                        " addr.zipcode \"" + ZIP_CODE_ID + "\", " + /* col 24 */
                        " addr.zip_plus_four \"" + ZIP_PLUS_FOUR_ID + "\", " + /* col 25 */
                        " addr.province \"" + PROVINCE_ID + "\", " + /* col 26 */
                        " addr.country_code \"" + COUNTRY_CODE_ID + "\", " + /* col 27 */
                        " ci_report.get_phone(addr.address_pk) \"" + PHONE_NUM_COMPUTED_ID + "\", " + /* col 28 */
                        " addr.legacy_data_id \"" + ADDR_LEGACY_DATA_ID_ID + "\", " + /* col 29 */
                        " TO_CHAR(addr.expired_address_fk) \"" + EXPIRED_ADDR_FK_ID + "\", " + /* col 30 */
                        " TO_CHAR(addr.copied_address_fk) \"" + COPIED_ADDR_FK_ID + "\", " + /* col 31 */
                        " addr.reins_control_addr \"" + REINS_CTRL_ADDR_ID + "\", " + /* col 32 */
                        " ci_client_utility.is_address_expired(addr.address_pk) " +
                        " \"" + EXPIRED_B_COMPUTED_ID + "\" " + /* col 33 */
                        " FROM address addr " +
//" WHERE (addr.source_record_fk = " + srcFK + " ) " +
                        " WHERE (addr.source_record_fk = ? ) " +
                        " ORDER BY NVL(UPPER(addr.primary_address_b), 'N') DESC, 9, " +
                        "  TRUNC(addr.effective_from_date) DESC, " +
                        "  TRUNC(addr.effective_to_date) DESC ";


        return sqlStatement;
    }

    /**
     * Builds SQL statement for retrieving addresses with county desc.
     *
     * @return String - The SQL statement.
     */
    public String buildAddressListStmtWithCountyDesc() {
        String sqlStatement =
                "SELECT " +
                        " TO_CHAR(addr.address_pk) \"" + ADDR_PK_RS_COL_NAME + "\", " + /* col 1 */
                        " addr.source_table_name \"source_table_name\", " + /* col 2 */
                        " TO_CHAR(addr.source_record_fk) \"source_record_fk\", " + /* col 3 */
                        " 'View' \"view_link\", " + /* col 4 */
                        " DECODE( " +
                        " ci_client_utility.is_address_expired(addr.address_pk), " +
                        " 'N', 'Change', " +
                        " 'Expired' " +
                        " ) " +
                        " \"change_link\", " + /* col 5 */
                        " NVL(UPPER(addr.primary_address_b), 'N') \"" + ADDR_PRIM_ADDR_B_RS_COL_NAME + "\", " + /* col 6 */
                        " DECODE( " +
                        " NVL(UPPER(addr.primary_address_b), 'N'), " +
                        " 'Y', 'Yes', " +
                        " 'No' " +
                        " ) \"primary_address_b_desc\", " + /* col 7 */
                        " NVL(UPPER(addr.post_office_address_b), 'N') " + POST_OFC_ADDR_B_ID + ", " + /* col 8 */
                        " DECODE( " +
                        " NVL(UPPER(addr.post_office_address_b), 'N'), " +
                        " 'Y', 'Yes', " +
                        " 'No' " +
                        " ) \"address_postOfficeAddressBDesc\", " + /* col 9 */
                        " addr.address_type_code \"address_type_code\", " + /* col 10 */
                        " LTRIM(RTRIM(NVL(cs_report.get_lookup_code('ADDRESS_TYPE_CODE', addr.address_type_code, 'SHORT'), addr.address_type_code))) " +
                        " \"" + ADDR_TYPE_CODE_DESC_RS_COL_NAME + "\" , " + /* col 11 */
                        " NVL(UPPER(addr.usa_address_b), 'Y') \"usa_address_b\", " + /* col 12 */
                        " DECODE( " +
                        " NVL(UPPER(addr.usa_address_b), 'Y'), " +
                        " 'Y', 'Yes', " +
                        " 'No' " +
                        " ) \"usa_address_b_desc\", " + /* col 13 */
                        " ci_report.format_addr_one_ln_for_display(addr.address_pk) \"" + ADDR_SINGLE_LINE_RS_COL_NAME + "\", " + /* col 14 */
                        " ci_report.get_phone(addr.address_pk) \"phone_number\", " + /* col 15 */
                        "  DECODE( LTRIM(RTRIM(addr.zipcode)),  " +
                        "      '', '',    " +
                        "     ' ' ||     " +
                        "      LTRIM(RTRIM(addr.zipcode)) ||  " +
                        "     DECODE(            " +
                        "         LTRIM(RTRIM(addr.zip_plus_four)),   " +
                        "         '', '',   " +
                        "         '-' || LTRIM(RTRIM(addr.zip_plus_four))" +
                        "      )) addressList_addressUSAZIP  , " +  /* col 16 */
                        " TO_CHAR(addr.effective_from_date, '" + ORACLE_DATE_FORMAT + "') \"" + ADDR_EFF_FR_DT_RS_COL_NAME + "\", " + /* col 17 */
                        " TO_CHAR(addr.effective_to_date, '" + ORACLE_DATE_FORMAT + "') \"" + ADDR_EFF_TO_DT_RS_COL_NAME + "\", " + /* col 18 */
                        " ci_client_utility.is_address_expired(addr.address_pk) " +
                        " \"" + ADDR_EXPIRED_FLAG_RS_COL_NAME + "\", " + /* col 19 */
                        " addr.address_name \"" + ADDR_NAME_ID + "\", " + /* col 20 */
                        " addr.address_line1 \"" + LINE_1_ID + "\", " + /* col 21 */
                        " addr.address_line2 \"" + LINE_2_ID + "\", " + /* col 22 */
                        " addr.address_line3 \"" + LINE_3_ID + "\", " + /* col 23 */
                        " addr.city \"" + CITY_ID + "\", " + /* col 24 */
                        " addr.county_code \"" + COUNTY_CODE_ID + "\", " + /* col 25 */
                        " addr.state_code \"" + STATE_ID + "\", " + /* col 26 */
                        " addr.zipcode \"" + ZIP_CODE_ID + "\", " + /* col 27 */
                        " addr.zip_plus_four \"" + ZIP_PLUS_FOUR_ID + "\", " + /* col 28 */
                        " addr.province \"" + PROVINCE_ID + "\", " + /* col 29 */
                        " addr.country_code \"" + COUNTRY_CODE_ID + "\", " + /* col 30 */
                        " ci_report.get_phone(addr.address_pk) \"" + PHONE_NUM_COMPUTED_ID + "\", " + /* col 31 */
                        " addr.legacy_data_id \"" + ADDR_LEGACY_DATA_ID_ID + "\", " + /* col 32 */
                        " TO_CHAR(addr.expired_address_fk) \"" + EXPIRED_ADDR_FK_ID + "\", " + /* col 33 */
                        " TO_CHAR(addr.copied_address_fk) \"" + COPIED_ADDR_FK_ID + "\", " + /* col 34 */
                        " addr.reins_control_addr \"" + REINS_CTRL_ADDR_ID + "\", " + /* col 35 */
                        " ci_client_utility.is_address_expired(addr.address_pk) " +
                        " \"" + EXPIRED_B_COMPUTED_ID + "\", " + /* col 36 */
                        " cc.short_description \"" + COUNTY_DESC_ID + "\", " +  /* col 37 */
                        " NVL(UPPER(addr.undeliverable_b), 'N') " + UNDELIVERABLE_B + ", " + /* col 38 */
                        " DECODE( " +
                        " NVL(UPPER(addr.undeliverable_b), 'N'), " +
                        " 'Y', 'Yes', " +
                        " 'No' " +
                        " ) \"address_undeliverableBDesc\" " + /* col 39 */
                        " FROM address addr, " +
                        "      county_code cc" +
//" WHERE (addr.source_record_fk = " + srcFK + " ) " +
                        " WHERE (addr.source_record_fk = ? ) " +
                        " and addr.county_code = cc.code(+) " +
                        " ORDER BY NVL(UPPER(addr.primary_address_b), 'N') DESC, 9, " +
                        "  TRUNC(addr.effective_from_date) DESC, " +
                        "  TRUNC(addr.effective_to_date) DESC ";


        return sqlStatement;
    }

    /**
     * Retrieves a result set of rows of addresses for a specified source.
     *
     * @param conn  JDBC Connection object.
     * @param srcFK Source record FK for the addresses.
     * @return DisconnectedResultSet with the data.
     * @throws Exception
     */
    public DisconnectedResultSet retrieveDataResultSet(Connection conn, String srcFK)
            throws Exception {
        String methodName = "retrieveDataResultSet";
        String methodDesc = "method " + methodName;
        Logger lggr = LogUtils.enterLog(this.getClass(), methodName,
                new Object[]{conn, srcFK});
        srcFK = this.checkPK(srcFK);
        String sqlStatement = this.buildAddressListStmt(srcFK);

        lggr.fine(methodDesc + ":  SQL statement = " + sqlStatement);
        /* Get the result set. */
        try {
            DisconnectedResultSet rs = new DisconnectedResultSet();
            //rs = Querier.doQuery(sqlStatement, conn, false);
            rs = Querier.doQuery(sqlStatement, conn,
                    new Object[]{new QueryParm(Types.BIGINT, Long.parseLong(srcFK))}, false);
            lggr.exiting(this.getClass().getName(), methodName);
            return rs;
        } catch (Exception e1) {
            try {
                lggr.throwing(this.getClass().getName(), methodName, e1);
                String sqlStmtMsg = "Class " + this.getClass().getName() + ",  " +
                        methodDesc + ":  " +
                        "SQL statement:  " +
                        sqlStatement;
                lggr.warning(sqlStmtMsg);
            } catch (Throwable ignore) {
            }
            throw e1;
        }

    }

    /**
     * Retrieves a result set of rows of addresses with county desc for a specified source.
     *
     * @param conn  JDBC Connection object.
     * @param srcFK Source record FK for the addresses.
     * @return DisconnectedResultSet with the data.
     * @throws Exception
     */
    public DisconnectedResultSet retrieveDataResultSetWithCountyDesc(Connection conn, String srcFK)
            throws Exception {
        String methodName = "retrieveDataResultSet";
        String methodDesc = "method " + methodName;
        Logger lggr = LogUtils.enterLog(this.getClass(), methodName,
                new Object[]{conn, srcFK});
        srcFK = this.checkPK(srcFK);
        String sqlStatement = this.buildAddressListStmtWithCountyDesc();

        lggr.fine(methodDesc + ":  SQL statement = " + sqlStatement);
        /* Get the result set. */
        try {
            DisconnectedResultSet rs = new DisconnectedResultSet();
            //rs = Querier.doQuery(sqlStatement, conn, false);
            rs = Querier.doQuery(sqlStatement, conn,
                    new Object[]{new QueryParm(Types.BIGINT, Long.parseLong(srcFK))}, false);
            lggr.exiting(this.getClass().getName(), methodName);
            return rs;
        } catch (Exception e1) {
            try {
                lggr.throwing(this.getClass().getName(), methodName, e1);
                String sqlStmtMsg = "Class " + this.getClass().getName() + ",  " +
                        methodDesc + ":  " +
                        "SQL statement:  " +
                        sqlStatement;
                lggr.warning(sqlStmtMsg);
            } catch (Throwable ignore) {
            }
            throw e1;
        }
    }

    /**
     * Retrieves a HashMap with data for a specified entity.
     *
     * @param conn JDBC Connection object.
     * @param pk   Address PK.
     * @return Map with the entity data.
     * @throws Exception
     */
    public Map retrieveDataMap(Connection conn, String pk)
            throws Exception {
        String methodName = "retrieveDataMap";
        String methodDesc = "method " + methodName;
        Logger lggr = LogUtils.enterLog(this.getClass(), methodName,
                new Object[]{conn, pk});
        Map retMap = new HashMap();
        pk = this.checkPK(pk);
        String sqlStatement =
                "SELECT " +
                        " TO_CHAR(addr.address_pk) \"" + ADDR_PK_ID + "\", " + /* col 1 */
                        " addr.source_table_name \"" + ADDR_SRC_TBL_NAME_ID + "\", " + /* col 2 */
                        " TO_CHAR(addr.source_record_fk) \"" + ADDR_SRC_REC_FK_ID + "\", " + /* col 3 */
                        " addr.address_name \"" + ADDR_NAME_ID + "\", " + /* col 4 */
                        " addr.address_line1 \"" + LINE_1_ID + "\", " + /* col 5 */
                        " addr.address_line2 \"" + LINE_2_ID + "\", " + /* col 6 */
                        " addr.address_line3 \"" + LINE_3_ID + "\", " + /* col 7 */
                        " addr.city \"" + CITY_ID + "\", " + /* col 8 */
                        " addr.county_code \"" + COUNTY_CODE_ID + "\", " + /* col 9 */
                        " addr.state_code \"" + STATE_ID + "\", " + /* col 10 */
                        " addr.zipcode \"" + ZIP_CODE_ID + "\", " + /* col 11 */
                        " addr.zip_plus_four \"" + ZIP_PLUS_FOUR_ID + "\", " + /* col 12 */
                        " addr.address_type_code \"" + ADDR_TYPE_CODE_ID + "\", " + /* col 13 */
                        " addr.province \"" + PROVINCE_ID + "\", " + /* col 14 */
                        " addr.country_code \"" + COUNTRY_CODE_ID + "\", " + /* col 15 */
                        " NVL(UPPER(addr.primary_address_b), 'N') \"" + PRIMARY_ADDR_B_ID + "\", " + /* col 16 */
                        " NVL(UPPER(addr.usa_address_b), 'Y') \"" + USA_ADDR_B_ID + "\", " + /* col 17 */
                        " NVL(UPPER(addr.post_office_address_b), 'N') \"" + POST_OFC_ADDR_B_ID + "\", " + /* col 18 */
                        " TO_CHAR(addr.effective_from_date, '" + ORACLE_DATE_FORMAT + "') \"" + EFF_FROM_DATE_ID + "\", " + /* col 19 */
                        " TO_CHAR(addr.effective_to_date, '" + ORACLE_DATE_FORMAT + "') \"" + EFF_TO_DATE_ID + "\", " + /* col 20 */
                        " ci_report.get_phone(addr.address_pk) \"" + PHONE_NUM_COMPUTED_ID + "\", " + /* col 21 */
                        " addr.legacy_data_id \"" + ADDR_LEGACY_DATA_ID_ID + "\", " + /* col 22 */
                        " TO_CHAR(addr.expired_address_fk) \"" + EXPIRED_ADDR_FK_ID + "\", " + /* col 23 */
                        " TO_CHAR(addr.copied_address_fk) \"" + COPIED_ADDR_FK_ID + "\", " + /* col 24 */
                        " addr.reins_control_addr \"" + REINS_CTRL_ADDR_ID + "\", " + /* col 25 */
                        " ci_client_utility.is_address_expired(addr.address_pk) " +
                        " \"" + EXPIRED_B_COMPUTED_ID + "\", " + /* col 26 */
                        " NVL(UPPER(addr.undeliverable_b), 'N') \"" + UNDELIVERABLE_B + "\" " + /* col 27 */
                        " FROM address addr " +
                        //" WHERE (addr.address_pk = " + pk + " ) ";
                        " WHERE (addr.address_pk = ? ) ";
        lggr.fine("SQL statement = " + sqlStatement + "; binding parameter = " + pk);
        /* Get the result set. */
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sqlStatement);
            stmt.setLong(1, Long.parseLong(pk));
            rs = stmt.executeQuery();
            retMap = this.addressResultSetToMap(rs);
        } catch (Exception e) {
            try {
                lggr.throwing(this.getClass().getName(), methodName, e);

                String sqlStmtMsg = "Class " + this.getClass().getName() + ",  " +
                        methodDesc + ":  " +
                        "SQL statement:  " +
                        sqlStatement;
                lggr.warning(sqlStmtMsg);
            } catch (Exception ignore) {
            }
            throw e;
        } finally {
            if (rs != null) {
                close(rs);
            }
            if (stmt != null) {
                close(stmt);
            }
        }
        lggr.exiting(this.getClass().getName(), methodName, retMap);
        return retMap;
    }

    /**
     * Retrieve Address Info into map
     * For expire an addresse use only
     *
     * @param pk the pk of the address which is supposed to be expired.
     * @return
     * @throws Exception
     */
    public Map retrieveAddressMapForExpire(Connection conn, String pk) throws Exception {
        // added by kshen, 02/01/2007
        String methodName = "retrieveAddressMapForExpire";
        Logger lggr = LogUtils.enterLog(this.getClass(), methodName, new Object[]{conn, pk});
        Map retMap = null;
        pk = this.checkPK(pk);
        String sqlStatement =
                "SELECT TO_CHAR(addr.address_pk) \"" + ADDR_PK_ID + "\", \n" +
                        "       Upper(addr.source_table_name) \"" + ADDR_SRC_TBL_NAME_ID + "\", \n" +
                        "       TO_CHAR(addr.source_record_fk) \"" + ADDR_SRC_REC_FK_ID + "\", \n" +
                        "       NVL(UPPER(addr.primary_address_b), 'N') \"" + PRIMARY_ADDR_B_ID + "\", \n" +
                        "       NVL(UPPER(addr.usa_address_b), 'Y') \"" + USA_ADDR_B_ID + "\", \n" +
                        "       ci_client_utility.is_address_expired(addr.address_pk) \"" + EXPIRED_B_COMPUTED_ID + "\", \n" +
                        "       TO_CHAR(addr.effective_from_date, '" + ORACLE_DATE_FORMAT + "') \"" +
                        EFF_FROM_DATE_ID + "\", \n" +
                        "       addr.city \"" + CITY_ID + "\", \n" +
                        "       ci_report.format_addr_one_ln_for_display(addr.address_pk) \"" + ADDR_SINGLE_LINE_RS_COL_NAME + "\" \n" +
                        "  FROM address addr \n" +
                        " WHERE addr.address_pk = ?";
        lggr.fine("SQL statement = " + sqlStatement + "; binding parameter = " + pk);
        /* Get the result set. */
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sqlStatement);
            stmt.setLong(1, Long.parseLong(pk));
            rs = stmt.executeQuery();
            retMap = this.setAddressDataMapForExpire(rs);
        } catch (Exception e) {
            lggr.throwing(this.getClass().getName(), methodName, e);
            throw e;
        } finally {
            this.close(stmt, rs);
        }
        lggr.exiting(this.getClass().getName(), methodName, retMap);
        return retMap;
    }

    /**
     * Set Address Info from rs into map
     * For expire addresse use only
     *
     * @param rs
     * @return
     * @throws Exception
     */
    private Map setAddressDataMapForExpire(ResultSet rs) throws Exception {
        // added by kshen, 02/01/2007
        String methodName = "setAddressDataMapForExpire";
        Logger lggr = LogUtils.enterLog(this.getClass(), methodName, rs);

        Map retMap = null;
        try {
            if (rs.next()) {
                retMap = new HashMap();
                String[] fieldNames = {ADDR_PK_ID, ADDR_SRC_TBL_NAME_ID, ADDR_SRC_REC_FK_ID,
                        PRIMARY_ADDR_B_ID, USA_ADDR_B_ID, EXPIRED_B_COMPUTED_ID,
                        EFF_FROM_DATE_ID, CITY_ID, ADDR_SINGLE_LINE_RS_COL_NAME};
                int fieldLen = fieldNames.length;
                for (int i = 0; i < fieldLen; i++) {
                    retMap.put(fieldNames[i], rs.getString(fieldNames[i]));
                }
            }
        } catch (Exception e) {
            lggr.throwing(this.getClass().getName(), methodName, e);
            throw e;
        }
        lggr.exiting(this.getClass().getName(), methodName, retMap);
        return retMap;
    }


    /**
     * Builds the update statement for setting an address as the primary address for its source.
     *
     * @param pk Address PK.
     * @return String - The update statement.
     */
    public String buildPrimaryAddressUpdateStmt(String pk) {

        String methodName = "buildPrimaryAddressUpdateStmt";
        Logger lggr = LogUtils.enterLog(this.getClass(), methodName, pk);

        pk = this.checkPK(pk);

        String sqlStatement =
                "BEGIN " +
                        //"ci_client_utility.set_new_primary_address(" + pk + "); " +
                        "ci_client_utility.set_new_primary_address( ? ); " +
                        "END; ";

        lggr.fine("SQL statement = " + sqlStatement);
        lggr.exiting(this.getClass().getName(), methodName);
        return sqlStatement;

    }

    /**
     * Builds the update statement for updating an address.
     *
     * @param pk          Address PK.
     * @param addressData HashMap with the address data.
     * @return ArrayList - the 1st element is the update statement;
     *         the 2nd element is the binding variable arraylist.
     */
    //public String buildAddressUpdateStmt(String pk, Map addressData) {
    public ArrayList buildAddressUpdateStmt(String pk, Map addressData) {
        return this.buildAddressUpdateStmt(pk, addressData, true);
    }

    /**
     * Builds the update statement for updating an address.
     *
     * @param pk            Address PK.
     * @param addressData   HashMap with the address data.
     * @param limitedUpdate Boolean indicating if update is limited to certain columns.
     * @return ArrayList - the 1st element is the update statement;
     *         the 2nd element is the binding variable arraylist.
     */
    //public String buildAddressUpdateStmt(String pk, Map addressData,
    public ArrayList buildAddressUpdateStmt(String pk, Map addressData,
                                            boolean limitedUpdate) {

        String methodName = "buildAddressUpdateStmt";
        Logger lggr = LogUtils.enterLog(this.getClass(), methodName,
                new Object[]{pk, addressData});

        pk = this.checkPK(pk);
        ArrayList result = new ArrayList(2);

        String addressTypeCode = (String) addressData.get(ADDR_TYPE_CODE_ID);
        String addressName = (String) addressData.get(ADDR_NAME_ID);
        String primaryAddressB = (String) addressData.get(PRIMARY_ADDR_B_ID);
        String countyCode = (String) addressData.get(COUNTY_CODE_ID);
        String expiredB = (String) addressData.get(EXPIRED_B_COMPUTED_ID);

        String usaAddrB = (String) addressData.get(USA_ADDR_B_ID);
        String addrLine1 = (String) addressData.get(LINE_1_ID);
        String addrLine2 = (String) addressData.get(LINE_2_ID);
        String addrLine3 = (String) addressData.get(LINE_3_ID);
        String city = (String) addressData.get(CITY_ID);
        String stateCode = (String) addressData.get(STATE_ID);
        String province = (String) addressData.get(PROVINCE_ID);
        String zipCode = (String) addressData.get(ZIP_CODE_ID);
        String zipCodeForeign = (String) addressData.get(ZIP_CODE_FOREIGN_ID);
        String zipPlusFour = (String) addressData.get(ZIP_PLUS_FOUR_ID);
        String countryCode = (String) addressData.get(COUNTRY_CODE_ID);
        String undeliverableB = (String) addressData.get(UNDELIVERABLE_B);
        String updateStmt =
                "UPDATE address " +
                        " SET ";

        ArrayList queryParamList = new ArrayList();
        QueryParm queryParam = null;

        if (limitedUpdate &&
            (StringUtils.isBlank(expiredB) ||
             expiredB.charAt(0) != 'N')) {
            // If we can't tell if the address is expired or not for a limited
            // update, build a dummy UPDATE statement.
            updateStmt += " sys_create_time = sys_create_time ";
        } else {

            //  Same pattern for most columns:
            //
            //  if (colVal != null) {     /* value for column is in the map from the form */
            //    if (StringUtils.isBlank(colVal)) {
            //      colVal = ""           /* eliminate blanks */
            //    }
            //    updateStmt += " columnName = '" + colVal + "', ";  /* put in update statement */
            //  }

            if (addressTypeCode != null) {
                if (StringUtils.isBlank(addressTypeCode)) {
                    addressTypeCode = "";
                }
                //updateStmt += " address_type_code =  '" + addressTypeCode + "', ";
                updateStmt += " address_type_code =  ? , ";
                queryParam = new QueryParm(Types.VARCHAR, addressTypeCode);
                queryParamList.add(queryParam);
            }
            if (addressName != null) {
                if (StringUtils.isBlank(addressName)) {
                    addressName = "";
                }
                //updateStmt += " address_name =  '" + this.formatValueForSQL(addressName) + "', ";
                updateStmt += " address_name =  ? , ";
                queryParam = new QueryParm(Types.VARCHAR, addressName);
                queryParamList.add(queryParam);
            }
            if (countyCode != null) {
                if (StringUtils.isBlank(countyCode)) {
                    countyCode = "";
                }
                //updateStmt += " county_code =  '" + countyCode + "', ";
                updateStmt += " county_code =  ? , ";
                queryParam = new QueryParm(Types.VARCHAR, countyCode);
                queryParamList.add(queryParam);
            }
            if (primaryAddressB != null) {
                /* Set primary_address_b to N if we did not get a value. */
                if (StringUtils.isBlank(primaryAddressB, true)) {
                    primaryAddressB = "N";
                }
                //updateStmt += " primary_address_b =  '" + primaryAddressB.substring(0, 1) + "', ";
                updateStmt += " primary_address_b =  ? , ";
                queryParam = new QueryParm(Types.VARCHAR, primaryAddressB.substring(0, 1));
                queryParamList.add(queryParam);
            }

            if (!limitedUpdate) {
                if (StringUtils.isBlank(usaAddrB) ||
                    (usaAddrB.charAt(0) != 'Y' && usaAddrB.charAt(0) != 'N')) {
                    // Set usa_address_b to Y by default.
                    usaAddrB = "Y";
                }
                //updateStmt += " usa_address_b = '" + usaAddrB.substring(0, 1) + "', ";
                updateStmt += " usa_address_b = ? , ";
                queryParam = new QueryParm(Types.VARCHAR, usaAddrB.substring(0, 1));
                queryParamList.add(queryParam);

                if (addrLine1 != null) {
                    // Do not allow blanking out of address_line_1.
                    if (!StringUtils.isBlank(addrLine1)) {
                        //updateStmt += " address_line1 = '" + this.formatValueForSQL(addrLine1) + "', ";
                        updateStmt += " address_line1 = ? , ";
                        queryParam = new QueryParm(Types.VARCHAR, addrLine1);
                        queryParamList.add(queryParam);
                    }
                }
                if (addrLine2 != null) {
                    if (StringUtils.isBlank(addrLine2)) {
                        addrLine2 = "";
                    }
                    //updateStmt += " address_line2 = '" + this.formatValueForSQL(addrLine2) + "', ";
                    updateStmt += " address_line2 = ? , ";
                    queryParam = new QueryParm(Types.VARCHAR, addrLine2);
                    queryParamList.add(queryParam);
                }
                if (addrLine3 != null) {
                    if (StringUtils.isBlank(addrLine3)) {
                        addrLine3 = "";
                    }
                    //updateStmt += " address_line3 = '" + this.formatValueForSQL(addrLine3) + "', ";
                    updateStmt += " address_line3 = ? , ";
                    queryParam = new QueryParm(Types.VARCHAR, addrLine3);
                    queryParamList.add(queryParam);
                }
                if (city != null) {
                    // Do not allow blanking out of city.
                    if (!StringUtils.isBlank(city)) {
                        //updateStmt += " city = '" + this.formatValueForSQL(city) + "', ";
                        updateStmt += " city = ? , ";
                        queryParam = new QueryParm(Types.VARCHAR, city);
                        queryParamList.add(queryParam);
                    }
                }
                if (countryCode != null) {
                    // Do not allow blanking out of country code.
                    if (!StringUtils.isBlank(countryCode, true)) {
                        //updateStmt += " country_code = '" + countryCode + "', ";
                        updateStmt += " country_code = ? , ";
                        queryParam = new QueryParm(Types.VARCHAR, countryCode);
                        queryParamList.add(queryParam);
                    }
                }
                if (undeliverableB != null) {
                    if (StringUtils.isBlank(undeliverableB)) {
                        undeliverableB = "N";
                    }
                    updateStmt += " undeliverable_b = ? , ";
                    queryParam = new QueryParm(Types.VARCHAR, undeliverableB);
                    queryParamList.add(queryParam);
                }
                if (StringUtils.isBlank(usaAddrB) || usaAddrB.charAt(0) != 'N') {
                    if (stateCode != null) {
                        // Do not allow blanking out of state code.
                        if (!StringUtils.isBlank(stateCode, true)) {
                            //updateStmt += " state_code = '" + stateCode + "', ";
                            updateStmt += " state_code = ? , ";
                            queryParam = new QueryParm(Types.VARCHAR, stateCode);
                            queryParamList.add(queryParam);
                        }
                    }
                    if (zipCode != null) {
                        if (StringUtils.isBlank(zipCode)) {
                            zipCode = "";
                        }
                        //updateStmt += " zipcode = '" + zipCode + "', ";
                        updateStmt += " zipcode = ? , ";
                        queryParam = new QueryParm(Types.VARCHAR, zipCode);
                        queryParamList.add(queryParam);
                    }
                    if (zipPlusFour != null) {
                        if (StringUtils.isBlank(zipPlusFour)) {
                            zipPlusFour = "";
                        }
                        //updateStmt += " zip_plus_four = '" + zipPlusFour + "', ";
                        updateStmt += " zip_plus_four = ? , ";
                        queryParam = new QueryParm(Types.VARCHAR, zipPlusFour);
                        queryParamList.add(queryParam);
                    }
                } else {
                    if (zipCodeForeign != null) {
                        if (StringUtils.isBlank(zipCodeForeign)) {
                            zipCodeForeign = "";
                        }
                        //updateStmt += " zipcode = '" + zipCodeForeign + "', ";
                        updateStmt += " zipcode = ? , ";
                        queryParam = new QueryParm(Types.VARCHAR, zipCodeForeign);
                        queryParamList.add(queryParam);
                    }
                    if (province != null) {
                        if (StringUtils.isBlank(province, true)) {
                            province = "";
                        }
                        //updateStmt += " province = '" + province + "', ";
                        updateStmt += " province = ? , ";
                        queryParam = new QueryParm(Types.VARCHAR, province);
                        queryParamList.add(queryParam);
                    }
                }
            }

            updateStmt = updateStmt.trim();
            // Remove any commas at the end of the SET clause.
            if (updateStmt.charAt(updateStmt.length() - 1) == ',') {
                updateStmt = updateStmt.substring(0, updateStmt.length() - 1);
            }

        }

        // Add the WHERE clause.
        updateStmt +=
                //" WHERE address_pk = " + pk + " ";
                " WHERE address_pk = ? ";
        queryParam = new QueryParm(Types.BIGINT, Long.parseLong(pk));
        queryParamList.add(queryParam);

        result.add(updateStmt);
        result.add(queryParamList);

        lggr.fine("SQL statement = " + updateStmt);
        lggr.exiting(this.getClass().getName(), methodName, result);
        return result;
    }

    /**
     * Save a new address.
     *
     * @param conn             Connection object.
     * @param expiredAddressFK Expired address FK;  -1 if none.
     * @param addressData      HashMap with address data.
     * @return saved address PK
     * @throws Exception
     */
    public String saveInsert(Connection conn, String expiredAddressFK,
                             Map addressData)
            throws Exception {

        String methodName = "saveInsert";
        String methodDesc = "Class " + this.getClass().getName() +
                ", method " + methodName;
        Logger lggr = LogUtils.enterLog(this.getClass(), methodName,
                new Object[]{conn, expiredAddressFK, addressData});
        String sqlStatement = this.getUpdateSql();

        String addressTypeCode = (String) addressData.get(ADDR_TYPE_CODE_ID);
        String sourceRecordFK = (String) addressData.get(SOURCE_REC_FK_PROPERTY);
        String sourceTableName = (String) addressData.get(SOURCE_TBL_NAME_PROPERTY);
        String addressName = (String) addressData.get(ADDR_NAME_ID);
        String addressLine1 = (String) addressData.get(LINE_1_ID);
        String addressLine2 = (String) addressData.get(LINE_2_ID);
        String addressLine3 = (String) addressData.get(LINE_3_ID);
        String city = (String) addressData.get(CITY_ID);
        String countyCode = (String) addressData.get(COUNTY_CODE_ID);
        String province = (String) addressData.get(PROVINCE_ID);
        String stateCode = (String) addressData.get(STATE_ID);
        String zipPlusFour = (String) addressData.get(ZIP_PLUS_FOUR_ID);
        String countryCode = (String) addressData.get(COUNTRY_CODE_ID);
        String primaryAddressB = (String) addressData.get(PRIMARY_ADDR_B_ID);
        String usaAddressB = (String) addressData.get(USA_ADDR_B_ID);
        String postOfficeAddressB = (String) addressData.get(POST_OFC_ADDR_B_ID);
        String legacyDataID = (String) addressData.get(ADDR_LEGACY_DATA_ID_ID);
        String reinsControlAddr = (String) addressData.get(REINS_CTRL_ADDR_ID);
        String effectiveFromDate = (String) addressData.get(EFF_FROM_DATE_ID);
        String undeliverableB = (String) addressData.get(UNDELIVERABLE_B);

        if (StringUtils.isBlank(addressTypeCode, true)) {
            addressTypeCode = "";
        }
        if (StringUtils.isBlank(sourceRecordFK)) {
            sourceRecordFK = "";
        }
        if (StringUtils.isBlank(sourceTableName)) {
            sourceTableName = "";
        }
        if (StringUtils.isBlank(addressName)) {
            addressName = "";
        }
        //addressName = this.formatValueForSQL(addressName);
        if (StringUtils.isBlank(addressLine1)) {
            addressLine1 = "";
        }
        //addressLine1 = this.formatValueForSQL(addressLine1);
        if (StringUtils.isBlank(addressLine2)) {
            addressLine2 = "";
        }
        //addressLine2 = this.formatValueForSQL(addressLine2);
        if (StringUtils.isBlank(addressLine3)) {
            addressLine3 = "";
        }
        //addressLine3 = this.formatValueForSQL(addressLine3);
        if (StringUtils.isBlank(city)) {
            city = "";
        }
        //city = this.formatValueForSQL(city);
        if (StringUtils.isBlank(countyCode, true)) {
            countyCode = "";
        }
        if (StringUtils.isBlank(province)) {
            province = "";
        }
        //province = this.formatValueForSQL(province);
        if (StringUtils.isBlank(stateCode, true)) {
            stateCode = "";
        }
        if (StringUtils.isBlank(zipPlusFour)) {
            zipPlusFour = "";
        }
        if (StringUtils.isBlank(countryCode, true)) {
            countryCode = "";
        }
        if (StringUtils.isBlank(primaryAddressB)) {
            primaryAddressB = "";
        }
        if (StringUtils.isBlank(usaAddressB)) {
            usaAddressB = "";
        }
        if (StringUtils.isBlank(postOfficeAddressB)) {
            postOfficeAddressB = "";
        }
        if (StringUtils.isBlank(legacyDataID)) {
            legacyDataID = "";
        }
        //legacyDataID = this.formatValueForSQL(legacyDataID);
        if (StringUtils.isBlank(reinsControlAddr)) {
            reinsControlAddr = "";
        }
        //reinsControlAddr = this.formatValueForSQL(reinsControlAddr);
        if (StringUtils.isBlank(effectiveFromDate) || !CIHelper.isDate(effectiveFromDate)) {
            effectiveFromDate = "";
        }

        String zipCode = "";
        if (!StringUtils.isBlank(usaAddressB) && usaAddressB.charAt(0) == 'N') {
            zipCode = (String) addressData.get(ZIP_CODE_FOREIGN_ID);
        } else if (!StringUtils.isBlank(usaAddressB) && usaAddressB.charAt(0) == 'Y') {
            zipCode = (String) addressData.get(ZIP_CODE_ID);
        }
        if (StringUtils.isBlank(zipCode)) {
            zipCode = "";
        }

        if (StringUtils.isBlank(undeliverableB)) {
            undeliverableB = "";
        }
        /*
        sqlStatement = sqlStatement.replaceFirst("&1", addressTypeCode);
        if (CIAddressHelper.isLong(sourceRecordFK)) {
          sqlStatement = sqlStatement.replaceFirst("&2", sourceRecordFK);
        }
        else {
          sqlStatement = sqlStatement.replaceFirst("&2", "null");
        }
        sqlStatement = sqlStatement.replaceFirst("&3", sourceTableName);
        sqlStatement = sqlStatement.replaceFirst("&4", addressName);
        sqlStatement = sqlStatement.replaceFirst("&5", addressLine1);
        sqlStatement = sqlStatement.replaceFirst("&6", addressLine2);
        sqlStatement = sqlStatement.replaceFirst("&7", addressLine3);
        sqlStatement = sqlStatement.replaceFirst("&8", city);
        sqlStatement = sqlStatement.replaceFirst("&9", countyCode);
        sqlStatement = sqlStatement.replaceFirst("&10", province);
        sqlStatement = sqlStatement.replaceFirst("&11", stateCode);
        sqlStatement = sqlStatement.replaceFirst("&12", zipCode);
        sqlStatement = sqlStatement.replaceFirst("&13", zipPlusFour);
        sqlStatement = sqlStatement.replaceFirst("&14", countryCode);
        sqlStatement = sqlStatement.replaceFirst("&15", primaryAddressB);
        sqlStatement = sqlStatement.replaceFirst("&16", usaAddressB);
        sqlStatement = sqlStatement.replaceFirst("&17", postOfficeAddressB);
        sqlStatement = sqlStatement.replaceFirst("&18", legacyDataID);
        if (!StringUtils.isBlank(expiredAddressFK, true) && CIAddressHelper.isLong(expiredAddressFK)) {
          sqlStatement = sqlStatement.replaceFirst("&19", expiredAddressFK);
        }
        else {
          sqlStatement = sqlStatement.replaceFirst("&19", "null");
        }
        sqlStatement = sqlStatement.replaceFirst("&20", "null");
        sqlStatement = sqlStatement.replaceFirst("&21", reinsControlAddr);
        sqlStatement = sqlStatement.replaceFirst("&22", effectiveFromDate);

        */
        StringBuffer logSB = new StringBuffer(this.getClass().getName());
        logSB.append(" :: ").append(methodName);
        logSB.append(" :: SQLStatement = ").append(sqlStatement);
        logSB.append(" :: Binding variables :: ");
        logSB.append(" :: 1 addressTypeCode " + addressTypeCode);
        logSB.append(" :: 2 sourceRecordFK " + sourceRecordFK);
        logSB.append(" :: 3 sourceTableName" + sourceTableName);
        logSB.append(" :: 4 addressName " + addressName);
        logSB.append(" :: 5 addressLine1 " + addressLine1);
        logSB.append(" :: 6 addressLine2 " + addressLine2);
        logSB.append(" :: 7 addressLine3 " + addressLine3);
        logSB.append(" :: 8 city " + city);
        logSB.append(" :: 9 countyCode " + countyCode);
        logSB.append(" :: 10 province " + province);
        logSB.append(" :: 11 stateCode " + stateCode);
        logSB.append(" :: 12 zipCode " + zipCode);
        logSB.append(" :: 13 zipPlusFour " + zipPlusFour);
        logSB.append(" :: 14 countryCode " + countryCode);
        logSB.append(" :: 15 primaryAddressB " + primaryAddressB);
        logSB.append(" :: 16 usaAddressB " + usaAddressB);
        logSB.append(" :: 17 postOfficeAddressB " + postOfficeAddressB);
        logSB.append(" :: 18 legacyDataID " + legacyDataID);
        logSB.append(" :: 19 expiredAddressFK " + expiredAddressFK);
        logSB.append(" :: 20 set null");
        logSB.append(" :: 21 reinsControlAddr " + reinsControlAddr);
        logSB.append(" :: 22 effectiveFromDate " + effectiveFromDate);
        logSB.append(" :: 23 undeliverableB " + undeliverableB);
        lggr.fine(logSB.toString());

        CallableStatement stmt = null;
        try {
            stmt = conn.prepareCall(sqlStatement);

            stmt.setString(1, addressTypeCode);
            if (CIAddressHelper.isLong(sourceRecordFK)) {
                stmt.setLong(2, Long.parseLong(sourceRecordFK));
            } else {
                stmt.setNull(2, Types.BIGINT);
            }
            stmt.setString(3, sourceTableName);
            stmt.setString(4, addressName);
            stmt.setString(5, addressLine1);
            stmt.setString(6, addressLine2);
            stmt.setString(7, addressLine3);
            stmt.setString(8, city);
            stmt.setString(9, countyCode);
            stmt.setString(10, province);
            stmt.setString(11, stateCode);
            stmt.setString(12, zipCode);
            stmt.setString(13, zipPlusFour);
            stmt.setString(14, countryCode);
            stmt.setString(15, primaryAddressB);
            stmt.setString(16, usaAddressB);
            stmt.setString(17, postOfficeAddressB);
            stmt.setString(18, legacyDataID);
            if (!StringUtils.isBlank(expiredAddressFK, true) && CIAddressHelper.isLong(expiredAddressFK)) {
                stmt.setLong(19, Long.parseLong(expiredAddressFK));
            } else {
                stmt.setNull(19, Types.BIGINT);
            }
            stmt.setNull(20, Types.BIGINT);
            stmt.setString(21, reinsControlAddr);
            stmt.setString(22, effectiveFromDate);
            stmt.setString(23, undeliverableB);
            stmt.registerOutParameter(24, Types.BIGINT);

            stmt.executeUpdate();
            long addressPk = stmt.getLong(24);
            String addressPkStr = new Long(addressPk).toString();
            lggr.exiting(this.getClass().getName(), methodName, addressPkStr);
            return addressPkStr;
        } catch (SQLException e) {
            String msg = null;
            try {
                conn.rollback();
            } catch (Exception ignore) {
            }
            // Check sql error.
            try {
                // Check exception for Oracle specific errors.
                msg = checkException(e);
            } catch (Exception e1) {
                // It's an unexpected error.
                AppException ae =
                    ExceptionHelper.getInstance().handleException("Unable to save the Address.", e1);
                throw ae;
            }
            PersistenceException pe = new PersistenceException(
                "ci.CIAddressDetail.error", "Failed to save the Address",
                new Object[]{msg},
                e);
            throw pe;
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (Exception ignore) {
            }
            try {
                lggr.throwing(this.getClass().getName(), methodName, e);
                String sqlStmtMsg = methodDesc + ":  " +
                        "SQL statement:  " +
                        sqlStatement;
                lggr.warning(sqlStmtMsg);
            } catch (Throwable ignore) {
            }
            throw e;
        } finally {
            if (stmt != null)
                close(stmt);
        }
    }

    /**
     * Modify an existing address.
     *
     * @param conn        JDBC Connection object.
     * @param pk          Address PK.
     * @param addressData Map with the address data.
     * @return saved address PK
     * @throws Exception
     */
    public String saveUpdate(Connection conn, String pk, Map addressData)
            throws Exception {
        return this.saveUpdate(conn, pk, addressData, true);
    }

    /**
     * Modify an existing address.
     *
     * @param conn          JDBC Connection object.
     * @param pk            Address PK.
     * @param addressData   Map with the address data.
     * @param limitedUpdate Boolean indicating if update is limited to certain columns.
     * @return saved address PK
     * @throws Exception
     */
    public String saveUpdate(Connection conn, String pk, Map addressData,
                             boolean limitedUpdate)
            throws Exception {

        String methodName = "saveUpdate";
        Logger lggr = LogUtils.enterLog(this.getClass(), methodName,
                new Object[]{conn, pk, addressData});

        //String sqlStatement = this.buildAddressUpdateStmt(pk, addressData, limitedUpdate);
        ArrayList updateInfo = this.buildAddressUpdateStmt(pk, addressData, limitedUpdate);
        String sqlStatement = (String) updateInfo.get(0);
        ArrayList queryParamList = (ArrayList) updateInfo.get(1);

        lggr.fine("address update SQL statement = " + sqlStatement + "; binding param = " + queryParamList);
        String primaryAddressB = (String) addressData.get(PRIMARY_ADDR_B_ID);
        if (StringUtils.isBlank(primaryAddressB, true)) {
            primaryAddressB = "N";
        }
        PreparedStatement stmt = null;
        String primAddrSQLStatement = "";
        try {
            stmt = conn.prepareStatement(sqlStatement);

            // set binding variables
            int sz = queryParamList.size();
            for (int i = 0; i < sz; i++) {
                QueryParm parm = (QueryParm) queryParamList.get(i);
                if (parm.value == null)
                    stmt.setNull(i + 1, parm.sqlType);
                else
                    stmt.setObject(i + 1, parm.value, parm.sqlType);
            }

            stmt.executeUpdate();
            if (primaryAddressB.charAt(0) == 'Y') {
                primAddrSQLStatement = this.buildPrimaryAddressUpdateStmt(pk);
                stmt = conn.prepareStatement(primAddrSQLStatement);
                stmt.setLong(1, Long.parseLong(pk));
                lggr.fine("primary address update SQL statement = " + primAddrSQLStatement);
                stmt.executeUpdate();
            } else {
                primAddrSQLStatement = "no update;  not a primary address";
            }
            lggr.exiting(this.getClass().getName(), methodName, pk);
            return pk;
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (Exception ignore) {
            }
            try {
                lggr.throwing(this.getClass().getName(), methodName, e);

                String sqlStmtMsg = "Class " + this.getClass().getName() + ",  " +
                        "method " + methodName + ":  " +
                        "address update SQL statement:  " +
                        sqlStatement;

                lggr.warning(sqlStmtMsg);
                sqlStmtMsg = "Class " + this.getClass().getName() + ",  " +
                        "method " + methodName + ":  " +
                        "primary address update SQL statement:  " +
                        primAddrSQLStatement;

                lggr.warning(sqlStmtMsg);
            } catch (Exception ignore) {
            }
            throw e;
        } finally {
            if (stmt != null)
                close(stmt);
        }
    }

    /**
     * Gets SQL for an update.
     *
     * @return String - Procedure call for inserts only.
     */
    public String getUpdateSql() {
/*
        "BEGIN " +
        "ci_client_utility.insert_new_address ( " +
          "is_address_type_code => '&1', " +
          "in_source_record_fk => &2, " +
          "is_source_table_name => '&3', " +
          "is_address_name => '&4', " +
          "is_address_line1 => '&5', " +
          "is_address_line2 => '&6', " +
          "is_address_line3 => '&7', " +
          "is_city => '&8', " +
          "is_county_code => '&9', " +
          "is_province => '&10', " +
          "is_state_code => '&11', " +
          "is_zipcode => '&12', " +
          "is_zip_plus_four => '&13', " +
          "is_country_code => '&14', " +
          "is_primary_address_b => '&15', " +
          "is_usa_address_b => '&16', " +
          "is_post_office_address_b => '&17', " +
          "is_legacy_data_id => '&18', " +
          "in_expired_address_fk => &19, " +
          "in_copied_address_fk => &20, " +
          "is_reins_control_addr => '&21', " +
          "is_effective_from_date => '&22' " +
        "); " +
      "END; ";
*/
        return
                "BEGIN " +
                        "ci_client_utility.insert_new_address ( " +
                        "is_address_type_code => ?, " +
                        "in_source_record_fk => ?, " +
                        "is_source_table_name => ?, " +
                        "is_address_name => ?, " +
                        "is_address_line1 => ?, " +
                        "is_address_line2 => ?, " +
                        "is_address_line3 => ?, " +
                        "is_city => ?, " +
                        "is_county_code => ?, " +
                        "is_province => ?, " +
                        "is_state_code => ?, " +
                        "is_zipcode => ?, " +
                        "is_zip_plus_four => ?, " +
                        "is_country_code => ?, " +
                        "is_primary_address_b => ?, " +
                        "is_usa_address_b => ?, " +
                        "is_post_office_address_b => ?, " +
                        "is_legacy_data_id => ?, " +
                        "in_expired_address_fk => ?, " +
                        "in_copied_address_fk => ?, " +
                        "is_reins_control_addr => ?, " +
                        "is_effective_from_date => ?, " +
                        "is_undeliverable_b => ?, " +
                        "on_new_address_pk => ? " +
                        "); " +
                        "END; ";
    }

    /**
     * Converts a result set with one row of address data into a HashMap.
     *
     * @param rs Result set containing one row of address data.
     * @return Map with the data.
     */
    public Map addressResultSetToMap(ResultSet rs) {

        String methodName = "addressResultSetToMap";
        Logger lggr = LogUtils.enterLog(this.getClass(),
                methodName, rs);

        HashMap retMap = new HashMap();

        String usaAddrB = "";

        // These are the fields that are common to U.S. and foreign addresses.
        int numCommonFlds = 14;
        String[] fldName = new String[numCommonFlds];

        fldName[0] = ADDR_TYPE_CODE_ID;
        fldName[1] = ADDR_NAME_ID;
        fldName[2] = LINE_1_ID;
        fldName[3] = LINE_2_ID;
        fldName[4] = LINE_3_ID;
        fldName[5] = CITY_ID;
        fldName[6] = EFF_FROM_DATE_ID;
        fldName[7] = EFF_TO_DATE_ID;
        fldName[8] = USA_ADDR_B_ID;
        fldName[9] = PRIMARY_ADDR_B_ID;
        fldName[10] = ADDR_LEGACY_DATA_ID_ID;
        fldName[11] = REINS_CTRL_ADDR_ID;
        fldName[12] = EXPIRED_B_COMPUTED_ID;
        fldName[13] = UNDELIVERABLE_B;

        // These are the fields that are specific to U.S. addresses.
        int numUSAFlds = 5;
        String[] usaFldName = new String[numUSAFlds];

        usaFldName[0] = STATE_ID;
        usaFldName[1] = ZIP_PLUS_FOUR_ID;
        usaFldName[2] = COUNTY_CODE_ID;
        usaFldName[3] = POST_OFC_ADDR_B_ID;
        usaFldName[4] = ZIP_CODE_ID;

        // These are the fields that are specific to foreign addresses.

        int numForeignFlds = 2;
        String[] foreignFldName = new String[numForeignFlds];

        foreignFldName[0] = COUNTRY_CODE_ID;
        foreignFldName[1] = PROVINCE_ID;

        try {
            if (rs != null && rs.next()) {

                // put address_addressPK / sourceTableFK / sourceTableName into map
                try {
                    retMap.put(ADDR_PK_ID, rs.getString(ADDR_PK_ID));
                } catch (Exception ignoreEx) {
                    lggr.fine(ADDR_PK_ID + " is not in the resultset");
                }
                try {
                    retMap.put(SOURCE_REC_FK_PROPERTY, rs.getString(SOURCE_REC_FK_PROPERTY));
                } catch (Exception ignoreEx) {
                    lggr.fine(SOURCE_REC_FK_PROPERTY + " is not in the resultset");
                }
                try {
                    retMap.put(SOURCE_TBL_NAME_PROPERTY, rs.getString(SOURCE_TBL_NAME_PROPERTY));
                } catch (Exception ignoreEx) {
                    lggr.fine(SOURCE_TBL_NAME_PROPERTY + " is not in the resultset");
                }



                // Loop through the common fields.
                for (int i = 0; i < numCommonFlds; i++) {
                    try {
                        retMap.put(fldName[i], rs.getString(fldName[i]));
                    } catch (Exception ignore) {
                    }
                }


                // Loop through the U.S. fields.
                for (int i = 0; i < numUSAFlds; i++) {
                    try {
                        retMap.put(usaFldName[i], rs.getString(usaFldName[i]));
                    } catch (Exception ignore) {
                    }
                }

                // Loop through the foreign fields.
                for (int i = 0; i < numForeignFlds; i++) {
                    try {
                        retMap.put(foreignFldName[i], rs.getString(foreignFldName[i]));
                    } catch (Exception ignore) {
                    }
                }
                try {
                    // Put the ZIP code value into the HashMap with the foreign postal code field name.
                    retMap.put(ZIP_CODE_FOREIGN_ID, rs.getString(ZIP_CODE_ID));
                } catch (Exception ignore) {
                }
             }

        } catch (Exception ignore) {
        }

        lggr.exiting(this.getClass().getName(), methodName, retMap);
        return retMap;

    }

    /**
     * Retrieves a HashMap with vendor address data for a specified entity.
     *
     * @param conn JDBC Connection object.
     * @param pk   address source record FK.
     * @return Map with the entity data.
     * @throws Exception
     */
    public Map retrieveVendorAddressDataMap(Connection conn, String pk)
            throws Exception {
        String methodName = "retrieveVendorAddressDataMap";
        String methodDesc = "method " + methodName;
        Logger lggr = LogUtils.enterLog(this.getClass(), methodName,
                new Object[]{conn, pk});
        Map retMap = new HashMap();
        pk = this.checkPK(pk);
        String sqlStatement = getVendorAddressSQL();
        lggr.fine("SQL statement = " + sqlStatement + "; binding parameter = " + pk);

/* Get the result set. */
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sqlStatement);
            stmt.setLong(1, Long.parseLong(pk));
            stmt.setLong(2, Long.parseLong(pk));
            rs = stmt.executeQuery();
            retMap = this.addressResultSetToMap(rs);
        } catch (Exception e) {
            lggr.throwing(this.getClass().getName(), methodName, e);
            throw e;
        } finally {
            if (rs != null) {
                close(rs);
            }
            if (stmt != null) {
                close(stmt);
            }
        }
        lggr.exiting(this.getClass().getName(), methodName, retMap);
        return retMap;
    }

    /**
     * Retrieves a HashMap with vendor address type info.
     *
     * @param conn JDBC Connection object.
     * @return Map with the entity data.
     * @throws Exception
     */
    public Map retrieveVendorAddressTypeInfo(Connection conn)
            throws Exception {
        String methodName = "retrieveVendorAddressTypeInfo";
        String methodDesc = "method " + methodName;
        Logger lggr = LogUtils.enterLog(this.getClass(), methodName,
                new Object[]{conn});
        Map retMap = null;
        String sqlStatement = getVendorAddressTypeSQL();
        lggr.fine("SQL statement = " + sqlStatement);
/* Get the result set. */
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlStatement);
            if (rs.next()) {
                retMap = new HashMap(3);
                retMap.put(ADDR_TYPE_CODE_ID, rs.getString(ADDR_TYPE_CODE_ID));
                retMap.put(ADDR_TYPE_CODE_SHORT_DESC, rs.getString("shortDesc"));
                retMap.put(ADDR_TYPE_CODE_LONG_DESC, rs.getString("longDesc"));
            }
        } catch (Exception e) {
            lggr.throwing(this.getClass().getName(), methodName, e);
            throw e;
        } finally {
            if (rs != null) {
                close(rs);
            }
            if (stmt != null) {
                close(stmt);
            }
        }

        if (retMap == null) {
            retMap = new HashMap(3);
            String payeeAddressType = "PAYEE";
            retMap.put(ADDR_TYPE_CODE_ID, payeeAddressType);
            retMap.put(ADDR_TYPE_CODE_SHORT_DESC, payeeAddressType);
            retMap.put(ADDR_TYPE_CODE_LONG_DESC, payeeAddressType);
        }
        lggr.exiting(this.getClass().getName(), methodName, retMap);
        return retMap;
    }

    /**
     * Update an existing vendor address.
     *
     * @param conn        JDBC Connection object.
     * @param pk          Address PK.
     * @param addressData Map with the address data.
     * @return saved address PK
     * @throws Exception
     */
    public String saveVendorAddressUpdate(Connection conn, String pk, Map addressData)
            throws Exception {

        String methodName = "saveVendorAddressUpdate";
        Logger lggr = LogUtils.enterLog(this.getClass(), methodName,
                new Object[]{conn, pk, addressData});
        pk = this.checkPK(pk);
        String addressName = (String) addressData.get(ADDR_NAME_ID);
        String POBox = (String) addressData.get(POST_OFC_ADDR_B_ID);
        String usaAddrB = (String) addressData.get(USA_ADDR_B_ID);
        String addrLine1 = (String) addressData.get(LINE_1_ID);
        String addrLine2 = (String) addressData.get(LINE_2_ID);
        String addrLine3 = (String) addressData.get(LINE_3_ID);
        String city = (String) addressData.get(CITY_ID);
        String stateCode = (String) addressData.get(STATE_ID);
        String zipCode = (String) addressData.get(ZIP_CODE_ID);
        String zipPlusFour = (String) addressData.get(ZIP_PLUS_FOUR_ID);
        String effFromDate = (String) addressData.get(EFF_FROM_DATE_ID);
        String countryCode = (String) addressData.get(COUNTRY_CODE_ID);
        //
        String countyCode = (String) addressData.get(COUNTY_CODE_ID);
        String province = (String) addressData.get(PROVINCE_ID);
        String postalCode = (String) addressData.get(ZIP_CODE_FOREIGN_ID);

        ArrayList queryParamList = new ArrayList();
        QueryParm queryParam = null;

        StringBuffer sqlStmtSB = new StringBuffer(" UPDATE ADDRESS ");
        sqlStmtSB.append(" SET ");

        if (addressName != null) {
            if (StringUtils.isBlank(addressName)) {
                addressName = "";
            }
            sqlStmtSB.append(" address_name =  ? , ");
            queryParam = new QueryParm(Types.VARCHAR, addressName);
            queryParamList.add(queryParam);
        }
        if (POBox != null) {
            if (StringUtils.isBlank(POBox)) {
                POBox = "N";
            }
            sqlStmtSB.append(" post_office_address_b =  ? , ");
            queryParam = new QueryParm(Types.VARCHAR, POBox);
            queryParamList.add(queryParam);
        }

        if (StringUtils.isBlank(usaAddrB) ||
                (usaAddrB.charAt(0) != 'Y' && usaAddrB.charAt(0) != 'N')) {
// Set usa_address_b to Y by default.
            usaAddrB = "Y";
        }
        sqlStmtSB.append(" usa_address_b = ? , ");
        queryParam = new QueryParm(Types.VARCHAR, usaAddrB.substring(0, 1));
        queryParamList.add(queryParam);

        if (addrLine1 != null) {
// Do not allow blanking out of address_line_1.
            if (!StringUtils.isBlank(addrLine1)) {
                sqlStmtSB.append(" address_line1 = ? , ");
                queryParam = new QueryParm(Types.VARCHAR, addrLine1);
                queryParamList.add(queryParam);
            }
        }
        if (addrLine2 != null) {
            if (StringUtils.isBlank(addrLine2)) {
                addrLine2 = "";
            }
            sqlStmtSB.append(" address_line2 = ? , ");
            queryParam = new QueryParm(Types.VARCHAR, addrLine2);
            queryParamList.add(queryParam);
        }
        if (addrLine3 != null) {
            if (StringUtils.isBlank(addrLine3)) {
                addrLine3 = "";
            }
            sqlStmtSB.append(" address_line3 = ? , ");
            queryParam = new QueryParm(Types.VARCHAR, addrLine3);
            queryParamList.add(queryParam);
        }
        if (city != null) {
// Do not allow blanking out of city.
            if (!StringUtils.isBlank(city)) {
                sqlStmtSB.append(" city = ? , ");
                queryParam = new QueryParm(Types.VARCHAR, city);
                queryParamList.add(queryParam);
            }
        }
        if (usaAddrB.charAt(0) != 'N') {
            if (stateCode != null) {
                // Do not allow blanking out of state code.
                if (!StringUtils.isBlank(stateCode, true)) {
                    sqlStmtSB.append(" state_code = ? , ");
                    queryParam = new QueryParm(Types.VARCHAR, stateCode);
                    queryParamList.add(queryParam);
                }
            }
            if (zipCode != null) {
                if (StringUtils.isBlank(zipCode)) {
                    zipCode = "";
                }
                sqlStmtSB.append(" zipcode = ? , ");
                queryParam = new QueryParm(Types.VARCHAR, zipCode);
                queryParamList.add(queryParam);
            }
            if (zipPlusFour != null) {
                if (StringUtils.isBlank(zipPlusFour)) {
                    zipPlusFour = "";
                }
                sqlStmtSB.append(" zip_plus_four = ? , ");
                queryParam = new QueryParm(Types.VARCHAR, zipPlusFour);
                queryParamList.add(queryParam);
            }
            if (!StringUtils.isBlank(countryCode, true)) {
                sqlStmtSB.append(" country_code = ? , ");
                queryParam = new QueryParm(Types.VARCHAR, countryCode);
                queryParamList.add(queryParam);
            }
            if (countyCode != null) {
                if (StringUtils.isBlank(countyCode, true)) {
                    countyCode = "";
                }
                sqlStmtSB.append(" county_code = ? , ");
                queryParam = new QueryParm(Types.VARCHAR, countyCode);
                queryParamList.add(queryParam);
            }
        } else
        //Foreign address properties
        {
            if (!StringUtils.isBlank(countryCode, true)) {
                sqlStmtSB.append(" country_code = ? , ");
                queryParam = new QueryParm(Types.VARCHAR, countryCode);
                queryParamList.add(queryParam);
            }
            if (!StringUtils.isBlank(province)) {
                sqlStmtSB.append(" province = ? , ");
                queryParam = new QueryParm(Types.VARCHAR, province);
                queryParamList.add(queryParam);
            }
            if (!StringUtils.isBlank(postalCode)) {
                sqlStmtSB.append(" zipcode = ? , ");
                queryParam = new QueryParm(Types.VARCHAR, postalCode);
                queryParamList.add(queryParam);
            }
            if (zipPlusFour != null) {
                if (StringUtils.isBlank(zipPlusFour)) {
                    zipPlusFour = "";
                }
                sqlStmtSB.append(" zip_plus_four = ? , ");
                queryParam = new QueryParm(Types.VARCHAR, zipPlusFour);
                queryParamList.add(queryParam);
            }
        }
        if (effFromDate != null) {
            if (!StringUtils.isBlank(effFromDate)) {
                sqlStmtSB.append(" effective_from_date = to_date( ?, '").append(ORACLE_DATE_FORMAT).append("')");
                queryParam = new QueryParm(Types.VARCHAR, effFromDate);
                queryParamList.add(queryParam);
            } else {
                sqlStmtSB.append(" effective_from_date = TRUNC( sysdate) ");
            }
        }

        String sqlStmt = sqlStmtSB.toString().trim();
// Remove any commas at the end of the SET clause.
        if (sqlStmt.charAt(sqlStmt.length() - 1) == ',') {
            sqlStmt = sqlStmt.substring(0, sqlStmt.length() - 1);
        }

// Add the WHERE clause.
        sqlStmtSB = new StringBuffer(sqlStmt);
        sqlStmtSB.append(" WHERE address_pk = ? ");

        queryParam = new QueryParm(Types.BIGINT, Long.parseLong(pk));
        queryParamList.add(queryParam);

        sqlStmt = sqlStmtSB.toString();
        lggr.fine("vendor address update SQL statement = " + sqlStmt + "; binding param = " + queryParamList);
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sqlStmt);

            int sz = queryParamList.size();
            for (int i = 0; i < sz; i++) {
                QueryParm parm = (QueryParm) queryParamList.get(i);
                if (parm.value == null)
                    stmt.setNull(i + 1, parm.sqlType);
                else
                    stmt.setObject(i + 1, parm.value, parm.sqlType);
            }

            stmt.executeUpdate();
            lggr.exiting(this.getClass().getName(), methodName, pk);
            return pk;
        } catch (Exception e) {
            conn.rollback();
            lggr.throwing(this.getClass().getName(), methodName, e);
            throw e;
        } finally {
            if (stmt != null)
                close(stmt);
        }
    }

    /**
     * Return address list
     *
     * @param conn                connection
     * @param addressPK           address pk
     * @param sourceRecordFK      source record FK
     * @param dummySourceRecordFK source record FK
     * @param cmClmsOnlyAddrCod   address type code to sort by
     * @return DisconnectedResultSet of address list
     * @throws Exception
     */
    public DisconnectedResultSet getSearchAddAddressList(Connection conn,
                                                         long addressPK,
                                                         long sourceRecordFK,
                                                         long dummySourceRecordFK,
                                                         String cmClmsOnlyAddrCod,
                                                         String cmSelEffAddrOnly,
                                                         String cmAddrSortList) throws Exception {
        String methodName = "getSearchAddAddressList";
        Logger logger = LogUtils.enterLog(this.getClass(), methodName,
                new Object[]{conn, new Long(addressPK), new Long(sourceRecordFK),
                        new Long(dummySourceRecordFK), cmClmsOnlyAddrCod, cmSelEffAddrOnly, cmAddrSortList});
        String sqlString = getSearchAddAddressListSQL(cmClmsOnlyAddrCod, cmSelEffAddrOnly, cmAddrSortList);
        DisconnectedResultSet drs = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sqlString);
            pstmt.setLong(1, sourceRecordFK);
            pstmt.setLong(2, addressPK);
            pstmt.setLong(3, dummySourceRecordFK);
            if (!StringUtils.isBlank(cmAddrSortList)) {
                StringTokenizer st = new StringTokenizer(cmAddrSortList, "^");
                int i = 4;
                while (st.hasMoreTokens()) {
                    String sort = st.nextToken();
                    if (!sort.equals("PRIMARY_B")) {
                        pstmt.setString(i, sort);
                        i++;
                    }
                }
            } else if (StringUtils.isBlank(cmAddrSortList) && !StringUtils.isBlank(cmClmsOnlyAddrCod)) {
                pstmt.setString(4, cmClmsOnlyAddrCod);
            }
            logger.fine(this.getClass().getName() + " :: " +
                    methodName + " :: executing :: " + sqlString +
                    " :: binding parameter :: addressPK = " + addressPK +
                    ", sourceRecordFK = " + sourceRecordFK +
                    ", dummySourceRecordFK = " + dummySourceRecordFK +
                    ", cmClmsOnlyAddrCod = " + cmClmsOnlyAddrCod);
            rs = pstmt.executeQuery();
            drs = new DisconnectedResultSet(rs);
        } catch (Exception e) {
            logger.throwing(this.getClass().getName(), methodName, e);
            throw e;
        } finally {
            if (rs != null)
                close(rs);
            if (pstmt != null)
                close(pstmt);
        }
        logger.exiting(this.getClass().getName(), methodName, drs);
        return drs;
    }

    /**
     * Returns the SQL of getting address search / add list.
     * This should have 4 bound parms:
     * aAddressPK IN NUMERIC
     * sourceRecordFK IN NUMERIC
     * dummySourceRecordFK IN NUMERIC
     * if cmClmsOnlyAddrCod is not null or "", bind it; VARCHAR
     *
     * @return SQL
     */
    private String getSearchAddAddressListSQL(String cmClmsOnlyAddrCod, String cmSelEffAddrOnly, String cmAddrSortList) {
        final String sql =
            "SELECT A.ADDRESS_PK addressPK,   \n" +
                "         '0' SELECT_IND,\n" +
                "         A.SOURCE_RECORD_FK sourceRecordFK,   \n" +
                "         A.SOURCE_TABLE_NAME sourceTableName,   \n" +
                "         A.ADDRESS_TYPE_CODE addressTypeCode,   \n" +
                "         LC.SHORT_DESCRIPTION addressTypeCodeDesc,   \n" +
                "         A.ADDRESS_NAME addressName,   \n" +
                "         A.ADDRESS_LINE1 addressLine1,   \n" +
                "         A.ADDRESS_LINE2 addressLine2,   \n" +
                "         A.ADDRESS_LINE3 addressLine3,   \n" +
                "         nvl(A.PRIMARY_ADDRESS_B,'N') primaryAddressB,   \n" +
                "         decode(nvl(A.PRIMARY_ADDRESS_B,'N'),'N',0,-1) primaryAddressBNumber, \n" +
                "         nvl(A.USA_ADDRESS_B, 'Y') usaAddressB, \n" +
                "         decode(nvl(A.USA_ADDRESS_B,'Y'),'N',0,-1) usaAddressBNumber, \n" +
                "         ci_report.format_addr_one_ln_for_display(a.address_pk) concatenatedAddress,   \n" +
                "         TO_CHAR(A.EFFECTIVE_FROM_DATE, 'mm/dd/yyyy') effectiveFromDate,   \n" +
                "         TO_CHAR(A.EFFECTIVE_TO_DATE, 'mm/dd/yyyy') effectiveToDate,   \n" +
                "         A.CITY city,   \n" +
                "         A.STATE_CODE stateCode,   \n" +
                "         A.ZIPCODE zipCode,   \n" +
                "         A.ZIP_PLUS_FOUR zipPlusFour,   \n" +
                "         A.COUNTY_CODE countyCode,   \n" +
                "         CC.SHORT_DESCRIPTION countyDesc,   \n" +
                "         A.PROVINCE province,\n" +
                "         A.COUNTRY_CODE countryCode,\n" +
                "         nvl(A.POST_OFFICE_ADDRESS_B,'N') postOfficeAddressB, \n" +
                "         decode(nvl(A.POST_OFFICE_ADDRESS_B,'N'),'N',0,-1) postOfficeAddressBNumber, \n" +
                "         0 client \n" +
                "    FROM ADDRESS  A, \n" +
                "         county_code CC,\n" +
                "         lookup_code LC\n" +
                "   WHERE (A.SOURCE_RECORD_FK = ?    \n" +
                "     OR   A.ADDRESS_PK = ?  \n" +
                "     OR   A.SOURCE_RECORD_FK = ? ) \n" +
                "     AND A.STATE_CODE = CC.STATE_CODE(+) \n" +
                "     AND A.COUNTY_CODE = CC.CODE(+) \n" +
                "     and A.ADDRESS_TYPE_CODE = LC.Code(+)\n" +
                "     and LC.lookup_type_code(+) = 'ADDRESS_TYPE_CODE' \n";
        String resultSql = sql;
        if (!StringUtils.isBlank(cmSelEffAddrOnly) && cmSelEffAddrOnly.equalsIgnoreCase("Y")) {
            resultSql += " AND NVL(A.EFFECTIVE_FROM_DATE, to_date('01/01/1900', 'mm/dd/yyyy')) <=\n" +
                "       trunc(sysdate)\n" +
                "   AND NVL(A.EFFECTIVE_TO_DATE, to_date('01/01/3000', 'mm/dd/yyyy')) >\n" +
                "       trunc(sysdate)\n ";
        }
        resultSql += " ORDER BY ";
        if (!StringUtils.isBlank(cmAddrSortList)) {
            //resultSql += " ORDER BY ";
            StringTokenizer st = new StringTokenizer(cmAddrSortList, "^");
            while (st.hasMoreTokens()) {
                String sort = st.nextToken();
                if (!sort.equals("PRIMARY_B")){
                    resultSql += " decode(addressTypeCode, ?, 0, 1), ";
                } else {
                    resultSql += " decode(primaryAddressB, 'Y', 0, 1), ";
                }
            }
        } else if (StringUtils.isBlank(cmAddrSortList) && !StringUtils.isBlank(cmClmsOnlyAddrCod)) {
            resultSql += " decode(addressTypeCode, ?,0,1), ";
        }
        resultSql = resultSql + " primaryAddressB desc, addressName, addressPK ";
        return resultSql;
    }

    /**
     * update address list for search / add  address list grid.
     *
     * @param conn                Connection
     * @param data                XML data
     * @param sourceRecordFK      source record FK
     * @param dummySourceRecordFK source record FK
     * @throws Exception
     */
    public void updateSearchAddAddressList(Connection conn, String data, long sourceRecordFK, long dummySourceRecordFK)
            throws Exception {
        String methodName = "updateSearchAddAddressList";
        Logger logger = LogUtils.enterLog(this.getClass(), methodName,
                new Object[]{conn, data, new Long(sourceRecordFK)});
        String sqlString = "begin wb_client_utility.save_search_add_address_list(?,?, ?); end;";
        CallableStatement stmt = null;
        logger.fine(new StringBuffer().append(":  SQL statement = ")
                .append(sqlString).append(" with ")
                .append(data).toString());
        try {
            stmt = conn.prepareCall(sqlString);
            stmt.setString(1, data);
            stmt.setLong(2, sourceRecordFK);
            stmt.setLong(3, dummySourceRecordFK);
            stmt.execute();
            logger.exiting(this.getClass().getName(), methodName);
        } catch (SQLException e) {
            conn.rollback();
            logger.severe("***Caught exception while updating address list. Rolling transaction back.\n" + e);
            throw e;
        } finally {
            if (stmt != null)
                close(stmt);
        }
    }

    /**
     * Return a new sequence number
     *
     * @param conn connection
     * @return new sequence number
     * @throws Exception
     */
    public long getNewPK(Connection conn) throws Exception {
        String methodName = "getNewPK";
        Logger logger = LogUtils.enterLog(this.getClass(), methodName);
        Statement stmt = null;
        ResultSet rs = null;
        long newPK = -1;
        String sql = "select oasis_sequence.nextval from dual";
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                newPK = rs.getLong(1);
            } else {
                throw new Exception(" There is no sequence number retrieved.");
            }
        } catch (Exception e) {
            logger.throwing(this.getClass().getName(), methodName, e);
            throw e;
        } finally {
            if (rs != null)
                close(rs);
            if (stmt != null)
                close(stmt);
        }

        logger.exiting(this.getClass().getName(), methodName, new Long(newPK));
        return newPK;
    }

    /**
     * Return a new String Addressdec
     *
     * @param conn connection
     * @return new sequence number
     * @throws Exception
     */
    public String getAddressDec(Connection conn, String pk) throws Exception {
        String methodName = "getAddressDec";
        Logger logger = LogUtils.enterLog(this.getClass(), methodName);
        Statement stmt = null;
        ResultSet rs = null;
        String AddressDec = null;
        pk = this.checkPK(pk);
        String sql = "select  e.usa_address_b from address e where e.ADDRESS_PK=?";
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                AddressDec = rs.getString(1);
            } else {
                throw new Exception(" There is no sequence number retrieved.");
            }
        } catch (Exception e) {
            logger.throwing(this.getClass().getName(), methodName, e);
            throw e;
        } finally {
            if (rs != null)
                close(rs);
            if (stmt != null)
                close(stmt);
        }

        logger.exiting(this.getClass().getName(), methodName, new String(AddressDec));
        return AddressDec;
    }

    /**
     * Expire a non-primary address
     *
     * @param conn
     * @param pk
     * @param effectiveToDate
     * @throws Exception
     */
    public void expireNonPrimaryAddress(Connection conn, String pk, String effectiveToDate) throws Exception {
        // added by kshen
        String methodName = "expireAddress";
        Logger logger = LogUtils.enterLog(this.getClass(), methodName, new Object[]{conn, pk, effectiveToDate});
        CallableStatement stmt = null;
        pk = this.checkPK(pk);
        String sql = "{ call WB_CLIENT_UTILITY.expire_address(?,?) }";
        try {
            stmt = conn.prepareCall(sql);
            stmt.setLong(1, Long.parseLong(pk));
            stmt.setString(2, effectiveToDate);
            String sqlMsg = new StringBuffer("Executing: ").append(sql).append(" with ").
                    append(pk).append(",").
                    append(effectiveToDate).append(",").
                    toString();
            logger.fine(sqlMsg);
            stmt.execute();
            logger.exiting(getClass().getName(), methodName);
        } catch (Exception e) {
            logger.throwing(this.getClass().getName(), methodName, e);
            conn.rollback();
            throw e;
        } finally {
            if(stmt!=null)
            this.close(stmt);
        }
    }

    /**
     * Get Entity lock by Policy flag.
     *
     * @param srcFK Source record FK.
     * @param conn  JDBC Connection object.
     * @return String Y/N.
     * @throws Exception
     */
    public String entityLockFlg(String pk, Connection conn) throws Exception {
        String methodName = "entityLockFlg";
        Logger logger = LogUtils.enterLog(this.getClass(), methodName);
        CallableStatement stmt = null;
        ResultSet rs = null;
        String entityLockFlg = "N";
        String sql = "SELECT ENTITY_ROLE.ENTITY_FK \n" +
                "   FROM POLICY, ENTITY_ROLE  \n" +
                "   WHERE POLICY.WIP_B = 'Y'\n" +
                "   AND POLICY.POLICY_NO NOT LIKE '%H'\n" +
                "   AND POLICY.POLICY_PK = ENTITY_ROLE.SOURCE_RECORD_FK\n" +
                "   AND ENTITY_ROLE.SOURCE_TABLE_NAME = 'POLICY'\n" +
                "   AND ENTITY_ROLE.RECORD_MODE_CODE = 'OFFICIAL'\n" +
                "   AND ENTITY_ROLE.ACCOUNTING_TO_DATE = to_date('01/01/3000','mm/dd/yyyy') \n" +
                "   AND ENTITY_ROLE.ROLE_TYPE_CODE = 'POLHOLDER'\n" +
                "   AND ENTITY_ROLE.ENTITY_FK = ?";
        try {
            stmt = conn.prepareCall(sql);
            stmt.setLong(1, Long.parseLong(pk));
            rs = stmt.executeQuery();
            if (rs.next()) {
                entityLockFlg = "Y";
            }
        } catch (Exception e) {
            logger.throwing(this.getClass().getName(), methodName, e);
            throw e;
        } finally {
            if (rs != null)
                close(rs);
            if (stmt != null)
                close(stmt);
        }

        logger.exiting(this.getClass().getName(), methodName, entityLockFlg);
        return entityLockFlg;
    }
}
