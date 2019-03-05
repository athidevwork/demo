package dti.oasis.util;

import java.io.Writer;
import java.sql.SQLException;

/**
 * Standard Interface to wrap methods for Clobs
 * in different J2EE Containers
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

public interface IClobWrapper {
    public Writer setCharacterStream(long pos) throws SQLException;
}
