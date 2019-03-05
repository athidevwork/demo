package dti.pm.policymgr.service;

import com.delphi_tech.ows.policynumbergenerationservice.PolicyNumberGenerationRequestType;
import com.delphi_tech.ows.policynumbergenerationservice.PolicyNumberGenerationResultType;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   06/15/16
 *
 * @author eyin
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/22/2016       eyin        177211 - Created for policy number generation service.
 * ---------------------------------------------------
 */
public interface PolicyNumberGenerationServiceManager {
    public PolicyNumberGenerationResultType generatePolicyNumber(PolicyNumberGenerationRequestType policyNumberGenerationRequest);

}
