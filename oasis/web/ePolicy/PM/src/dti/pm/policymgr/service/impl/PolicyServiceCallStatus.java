package dti.pm.policymgr.service.impl;

import dti.oasis.app.AppException;
import dti.oasis.recordset.RecordSet;
import dti.pm.policymgr.PolicyHeader;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   11/24/14
 *
 * @author awu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/16/2016      lzhang       170647  add get and set method for
 *                                      m_validationFailureB field and
 *                                      remove m_validationErrorSet field.
 * ---------------------------------------------------
 */

public class PolicyServiceCallStatus {
    private PolicyHeader m_policyHeader;

    private boolean m_successStatusB;

    private boolean m_policyLockedB;
    
    private AppException m_appException;

    private boolean m_validationFailureB;

    public void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }

    public void setSuccessStatusB(boolean successStatusB) {
        m_successStatusB = successStatusB;
    }

    public void setValidationFailureB(boolean validationFailureB) {
        m_validationFailureB = validationFailureB;
    }

    public void setPolicyLockedB(boolean policyLockedB) {
        m_policyLockedB = policyLockedB;
    }
    
    public void setAppException(AppException ae) {
        m_appException = ae;
    }

    public PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    public boolean isSuccessStatus() {
        return m_successStatusB;
    }

    public Boolean getValidationFailureB() {
        return m_validationFailureB;
    }

    public boolean isPolicyLocked() {
        return m_policyLockedB;
    }

    public AppException getAppException () {
        return m_appException;
    }
}
