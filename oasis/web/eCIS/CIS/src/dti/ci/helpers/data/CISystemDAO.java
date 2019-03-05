package dti.ci.helpers.data;

import dti.oasis.util.LogUtils;

import java.util.logging.Logger;
import java.sql.Connection;
import java.sql.CallableStatement;

/**
 * Data Access Object for System-Level (CIS) Operations.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author Gerald C. Carney
 *         Date:   Apr 27, 2004
 *         <p/>
 *         Revision Date    Revised By  Description
 *         ---------------------------------------------------
 *         <p/>
 *         <p/>
 *         ---------------------------------------------------
 */

public class CISystemDAO extends CIBaseDAO {

  private static final CISystemDAO INSTANCE = new CISystemDAO();

  // Private constructor supresses
  // default public constructor
  private CISystemDAO() {
  }

  public static CISystemDAO getInstance( ) {
    return INSTANCE;
  }

  private Object readResolve() {
    return INSTANCE;
  }


  public String getUpdateSql() {
    return "";
  }

  /**
   * Calls a package procedure to initialize (or re-initialize) package-level variables.
   * @param conn   JDBC connection object.
   * @throws Exception
   */
  public void initializeDBVars (Connection conn) throws Exception {
    String methodName = "initializeDBVars";
    String methodDesc = "Class" + this.getClass().getName() +
      ", method " + methodName;
    Logger lggr = LogUtils.enterLog(this.getClass(), methodName);
    String sqlStatement =
      "BEGIN " +
        "wb_client_utility.init_vars; " +
      "END; ";
    lggr.fine(methodDesc + ":  SQL statement:  " + sqlStatement);
    CallableStatement cs = null;
    try {
      cs = conn.prepareCall(sqlStatement);
      cs.execute();
    }
    catch (Exception e) {
      try {
        lggr.throwing(this.getClass().getName(), methodName, e);
      }
      catch (Throwable ignore) { }
      throw e;
    }
    finally {
      if (cs != null) {
        close(cs);
      }
    }

  }
}
