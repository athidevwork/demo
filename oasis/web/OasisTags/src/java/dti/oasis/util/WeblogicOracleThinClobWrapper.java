package dti.oasis.util;

import java.io.Writer;
import java.sql.Clob;
import java.sql.SQLException;

/**
 * Handles security using the WebLogic wrappers for Clobs
 *
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 22, 2004
 *
 * @author jbe
 */
/* 
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 
 *
 * ---------------------------------------------------
*/

public class WeblogicOracleThinClobWrapper implements IClobWrapper {

    /**
     * WebLogic clob
     */
    private weblogic.jdbc.vendor.oracle.OracleThinClob clob;

    /**
     * Constructor with Clob
     * @param clob
     */
    public WeblogicOracleThinClobWrapper(Clob clob) {
        this.clob = (weblogic.jdbc.vendor.oracle.OracleThinClob) clob;
    }

    /**
     * The standard JDBC method to retrieve a stream to be used to write a stream
     * of Unicode characters to the CLOB value that this Clob object represents,
     * at position pos.  pos is ignored in this implementation.
     * @param pos ignored in this implementation
     * @return a Writer
     * @throws SQLException
     */
    public Writer setCharacterStream(long pos) throws SQLException {
        return clob.getCharacterOutputStream();
    }

}
