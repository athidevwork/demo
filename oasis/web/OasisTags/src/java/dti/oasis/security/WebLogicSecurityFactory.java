package dti.oasis.security;

import dti.oasis.security.WebLogicSecurity;

/**
 * Factory class to return WebLogic J2EE Security objects
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p> 
 * @author jbe
 * Date:   Dec 23, 2003
 * 
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 
 *
 * ---------------------------------------------------
 */

public class WebLogicSecurityFactory implements IJ2EESecurityFactory {
    /**
     * Returns a WebLogicSecurity object
     * @return
     */
    public IJ2EESecurity getInstance() {
        return WebLogicSecurity.getInstance();
    }


}
