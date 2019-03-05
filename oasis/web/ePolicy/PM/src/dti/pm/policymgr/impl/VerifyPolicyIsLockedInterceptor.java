package dti.pm.policymgr.impl;

import dti.oasis.request.RequestStorageManager;
import dti.oasis.util.LogUtils;
import dti.pm.core.request.RequestStorageIds;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.IllegalSaveWithoutLockException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.util.logging.Logger;

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
 *
 * ---------------------------------------------------
 */
public class VerifyPolicyIsLockedInterceptor implements MethodInterceptor {

    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Logger l = LogUtils.enterLog(getClass(), "invoke", new Object[]{methodInvocation});
        boolean ownLock = false;

        RequestStorageManager rsm = RequestStorageManager.getInstance();

        // Check if we own the lock
        if (rsm.has(RequestStorageIds.POLICY_HEADER)) {
            PolicyHeader policyHeader = (PolicyHeader) rsm.get(RequestStorageIds.POLICY_HEADER);
            if (policyHeader.getPolicyIdentifier().ownLock()) {
                ownLock = true;
            }
        }

        // If we own the lock, proceed.
        if (ownLock) {
            l.exiting(getClass().getName(), "invoke");
            return methodInvocation.proceed();
        }
        else {
            IllegalSaveWithoutLockException e = new IllegalSaveWithoutLockException(methodInvocation.getMethod().getDeclaringClass().getName(), methodInvocation.getMethod().getName());
            l.throwing(getClass().getName(), "invoke", e);
            throw e;
        }
    }
}
