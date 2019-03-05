package dti.ci.helpers;

import dti.oasis.util.LogUtils;
import dti.ci.helpers.data.CISystemDAO;

import java.io.Serializable;
import java.sql.Connection;
import java.util.logging.Logger;

/**
 * Helper class for CIS home page action class.
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

public class CIHomeHelper extends CIHelper implements Serializable {

  private static final CIHomeHelper INSTANCE = new CIHomeHelper();

  // Private constructor suppresses
  // default public constructor
  private CIHomeHelper () {
  }

  /**
   * Method to return singleton instance of class.
   * @return Singleton instance.
   */
  public static CIHomeHelper getInstance() {
    return INSTANCE;
  }

  private Object readResolve() {
    return INSTANCE;
  }

  /**
   * Initializes database package-level variables by calling a specific package
   * procedure to do so.
   * @param conn   JDBC connection object.
   * @throws Exception
   */
  public void initializeDBVars (Connection conn)
    throws Exception {
    String methodName = "retrieveSubClassList";
    Logger lggr = LogUtils.enterLog(this.getClass(),
      methodName, new Object[] { conn });
    try {
      CISystemDAO DAO = CISystemDAO.getInstance();
      DAO.initializeDBVars(conn);
    }
    catch (Exception e) {
      try {
        lggr.throwing(this.getClass().getName(), methodName, e);
      }
      catch (Throwable ignore) { }
      throw e;
    }
    finally {
      lggr.exiting(this.getClass().getName(), methodName);
    }

  }
}
