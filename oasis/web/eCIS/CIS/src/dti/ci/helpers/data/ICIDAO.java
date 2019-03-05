package dti.ci.helpers.data;

import dti.oasis.util.DisconnectedResultSet;

import java.sql.Connection;
import java.util.Map;

/**
 * <p>Interface for CIS Data Access Object</p>
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

public interface ICIDAO extends ICIBaseDAO {

  public DisconnectedResultSet retrieveDataResultSet(Connection conn) throws Exception;

  public Map retrieveDataMap(Connection conn) throws Exception;

}
