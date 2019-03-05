package dti.oasis.security;

import weblogic.management.utils.NotFoundException;
import weblogic.management.utils.InvalidParameterException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.naming.NamingException;
import java.util.ArrayList;

/**
 * Standard Interface to wrap methods for security
 * in J2EE Containers
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 23, 2003
 *
 * @author jbe
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 3/17/2004        jbe     New methods
 * 7/7/2004         jbe     Added logout
 * 09/11/2006       sxm     Added createUser(HttpServletRequest, String, String, String, ArrayList)
 * 04/09/2008       wer     Changed thrown exceptions to AppExcetion, and removed them from the method declarations
 * 04/21/2016       huixu   Issue#169769 Fix WebLogicSecurity.getAuthenticators to work in WebLogic 12.2.1
 * ---------------------------------------------------
*/

public interface IJ2EESecurity {
    /**
     * Change the users password in the J2EE Security Realm
     * @param request
     * @param response
     * @return
     */
    public boolean changePassword(HttpServletRequest request, HttpServletResponse response);

    /**
     * Reset a user's password in the J2EE Security Realm
     * @param request
     * @param userId userid
     * @param password new password
     */
    void resetPassword(HttpServletRequest request, String userId, String password) ;

    /**
     * Log a user out, invalidating all sessions and J2EE Security data
     * @param request
     */
    void logout(HttpServletRequest request);

    /**
     * Determines if a user already exists in the J2EE Security Realm
     * @param request
     * @param userId userid
     * @return true/false
     */
    boolean userExists(HttpServletRequest request, String userId);

}
