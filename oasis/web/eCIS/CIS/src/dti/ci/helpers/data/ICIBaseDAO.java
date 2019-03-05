package dti.ci.helpers.data;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * <p>Interface for Base Data Access Object.</p>
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * @author Gerald C. Carney
 * Date:   Dec 4, 2003
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 *
 * ---------------------------------------------------
 */

public interface ICIBaseDAO {

  public void save(Connection conn, String data) throws SQLException;

  public String getUpdateSql();

  public String checkException(Exception e) throws Exception;

  public void closeConnection(Connection conn);

}
