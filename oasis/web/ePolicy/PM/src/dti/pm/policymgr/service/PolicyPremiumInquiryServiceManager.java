package dti.pm.policymgr.service;

import com.delphi_tech.ows.policypremiuminquiryservice.PolicyPremiumInquiryRequestType;
import com.delphi_tech.ows.policypremiuminquiryservice.PolicyPremiumInquiryResultType;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   01/15/2013
 *
 * @author awu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface PolicyPremiumInquiryServiceManager {
    public PolicyPremiumInquiryResultType loadPremium(PolicyPremiumInquiryRequestType policyPremiumInquiryRequest);
}
