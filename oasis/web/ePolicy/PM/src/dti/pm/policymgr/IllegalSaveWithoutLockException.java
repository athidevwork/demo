package dti.pm.policymgr;

import dti.oasis.app.AppException;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 14, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/04/2014       awu         159187 - Add a new message key.
 * ---------------------------------------------------
 */
public class IllegalSaveWithoutLockException extends AppException {
    /**
     * Construct this IllegalSaveWithoutLockException with the given debug message.
     * UNEXPECTED_ERROR is used as the message key.
     * The RuntimeException super class is constructed with a message = "messageKey : debugMessage"
     *
     * @param className the name of the class the save is being executed on.
     * @param methodName the name of the method being invoked.
     */
    public IllegalSaveWithoutLockException(String className, String methodName) {
        super("pm.verifyPolicyIsLockedInterceptor.obtain.lock.error",
            "Failed to obtain the policy lock before calling "+className+"."+methodName);
    }
}
