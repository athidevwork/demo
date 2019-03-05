package dti.pm.policymgr.service;

import com.delphi_tech.ows.initiateelectronicapplicationservice.InitiateElectronicApplicationRequestType;
import com.delphi_tech.ows.initiateelectronicapplicationservice.InitiateElectronicApplicationResultType;
import dti.oasis.util.LogUtils;

import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   1/4/2017
 *
 * @author tzeng
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/23/2016       tzeng       166929 - Initial version.
 * ---------------------------------------------------
 */
public interface InitiateElectronicApplicationServiceManager {

    /**
     * The main entrance for initiate EApp web service.
     * @param initiateEAppRequest
     * @param initiateEAppResult
     */
    public void initiateEApp(InitiateElectronicApplicationRequestType initiateEAppRequest, InitiateElectronicApplicationResultType initiateEAppResult);
}
