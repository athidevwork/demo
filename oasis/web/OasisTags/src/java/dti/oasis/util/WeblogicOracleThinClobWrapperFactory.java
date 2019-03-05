package dti.oasis.util;

import java.sql.Clob;


/**
 * Factory class to return WebLogic OracleThinClob Factory objects
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

public class WeblogicOracleThinClobWrapperFactory implements IClobWrapperFactory {
    public IClobWrapper getInstance(Clob clob) {
        return new WeblogicOracleThinClobWrapper(clob);
    }
}
