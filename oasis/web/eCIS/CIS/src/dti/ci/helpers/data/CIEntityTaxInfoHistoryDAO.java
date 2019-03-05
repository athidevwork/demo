package dti.ci.helpers.data;

import dti.ci.helpers.ICIConstants;
import dti.ci.helpers.ICIEntityConstants;
import dti.oasis.util.DisconnectedResultSet;
import dti.oasis.util.Querier;
import dti.oasis.util.LogUtils;
import dti.oasis.util.QueryParm;

import java.sql.Connection;
import java.sql.Types;
import java.util.Map;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.io.Serializable;

/**
 * <p>Data Access Object for Entity Tax Info History.</p>
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * @author Gerald C. Carney
 * Date:   Dec 5, 2003
 *
 * Revision Date    Revised By  Description
 * --------------------------------------------------------------------
 *  04/01/2005       HXY        Removed singleton implementation. Changed
 *                              statement to preparedStatement.
 *
 * --------------------------------------------------------------------
 */

public class CIEntityTaxInfoHistoryDAO extends CIBaseDAO
  implements ICIConstants, ICIEntityConstants, ICIPKDAO, Serializable {

  public Map retrieveDataMap(Connection conn, String pk) {
    Map retMap = null;
    return retMap;

  }

  /**
   * Retrieve tax ID history data for a specified entity.
   * @param conn          JDBC Connection object.
   * @param pk            Entity PK.
   * @return DisconnectedResultSet - Result set with tax ID history for entity.
   * @throws Exception
   */
  public DisconnectedResultSet retrieveDataResultSet(Connection conn, String pk) throws Exception {
    String methodName = "retrieveDataResultSet";
    String methodDesc = "method " + methodName;
    Logger lggr = LogUtils.enterLog(this.getClass(),
      methodName, new Object[] { conn, pk } );
    pk = this.checkPK(pk);
    long pkLong = Long.parseLong(pk);
    String sqlStatement =
      "SELECT TO_CHAR(tax.entity_tax_info_history_pk) \"entity_tax_info_history_pk\", " +
            " TO_CHAR(tax.entity_fk) \"entity_fk\", " +
            " wb_client_utility.format_tin_for_display(tax.federal_tax_id) " +
              " \"federal_tax_id\", " +
            " DECODE( " +
              " NVL(UPPER(tax.federal_tax_id_verified_b), 'N'), " +
              " 'Y', 'Yes', " +
              " 'No' " +
            " ) \"federal_tax_id_verified_b\", " +
            " wb_client_utility.format_ssn_for_display(tax.social_security_number) " +
              " \"social_security_number\", " +
            " DECODE( " +
              " NVL(UPPER(tax.ssn_verified_b), 'N'), " +
              " 'Y', 'Yes', " +
              " 'No' " +
            " ) \"ssn_verified_b\", " +
            " LTRIM(RTRIM(UPPER(tax.default_tax_id))) \"default_tax_id\", " +
            " TO_CHAR(tax.effective_from_date, '" + ORACLE_DATE_FORMAT + "') \"tax_hist_effective_from_date\", " +
            " TO_CHAR(tax.effective_to_date, '" + ORACLE_DATE_FORMAT + "') \"tax_hist_effective_to_date\" " +
        "FROM entity_tax_info_history tax " +
      //" WHERE tax.entity_fk = " + pk + " " +
      " WHERE tax.entity_fk = ? " +
      " ORDER BY tax.effective_from_date desc, " +
                "tax.effective_to_date desc, " +
                "tax.entity_tax_info_history_pk DESC "; //147981:

    lggr.fine("SQL statement = " + sqlStatement);
    DisconnectedResultSet rs = null;
    try {
      //rs = Querier.doQuery(sqlStatement, conn, false);
      rs = Querier.doQuery(sqlStatement, conn,
          new Object[] { new QueryParm(Types.BIGINT, pkLong) }, false);
      lggr.exiting(this.getClass().getName(), methodName, rs);
      return rs;
    }
    catch (Exception e) {
      try {
        lggr.throwing(this.getClass().getName(), methodName, e);
        String exceptMsg = "Class " + this.getClass().getName() + ",  " +
          methodDesc + ":  " +
          "exception occurred retrieving tax info history list:  " +
          e.toString();
//        System.out.println(exceptMsg);
//        lggr.info(exceptMsg);
        exceptMsg = "Class " + this.getClass().getName() + ",  " +
          methodDesc + ":  " +
          "SQL statement:  " +
          sqlStatement;
//        System.out.println(exceptMsg);
        lggr.info(exceptMsg);
      }
      catch (Exception ignore) {
      }
      throw e;
    }
  }

  public String getUpdateSql() {
    return "";
  }
}
