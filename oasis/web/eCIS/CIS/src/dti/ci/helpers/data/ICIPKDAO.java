package dti.ci.helpers.data;

import dti.oasis.util.DisconnectedResultSet;

import java.util.Map;
import java.sql.Connection;

/**
 * <p>Interface for Data Access Object Using a PK.</p>
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

public interface ICIPKDAO extends ICIBaseDAO {

//  public ArrayList getPKLov(Connection conn) throws SQLException;

  public DisconnectedResultSet retrieveDataResultSet(Connection conn, String pk) throws Exception;

  public Map retrieveDataMap (Connection conn, String pk) throws Exception;

}
