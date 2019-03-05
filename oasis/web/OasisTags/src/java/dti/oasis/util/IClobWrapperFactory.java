package dti.oasis.util;

import java.sql.Clob;

/**
 * Standard Interface to return an IClobWrapper object
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author jbe
 */
/* Date:   Dec 23, 2003
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 *
 * ---------------------------------------------------
 */

public interface IClobWrapperFactory {
    /**
     * Call this method to get an IClobWrapper implementation
     *
     * @return
     */
    public IClobWrapper getInstance(Clob clob);
}
