package dti.oasis.security;

/**
 * Wrapper for exception from J2EE App server indicating user already exists
 *
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 4, 2004 
 * @author jbe
 */
/* 
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 
 *
 * ---------------------------------------------------
*/

public class UserAlreadyExistsException extends IllegalArgumentException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
