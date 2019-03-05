package dti.oasis.security;

import dti.oasis.security.IJ2EESecurity;

/**
 * Standard Interface to return an IJ2EESecurity object
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

public interface IJ2EESecurityFactory {
    /**
     * Call this method to get an IJ2EESecurity implementation
     * @return
     */
    public IJ2EESecurity getInstance();
}
