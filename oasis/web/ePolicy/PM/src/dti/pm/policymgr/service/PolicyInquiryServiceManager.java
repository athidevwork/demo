package dti.pm.policymgr.service;

import com.delphi_tech.ows.policyinquiryservice.PolicyInquiryRequestParametersType;
import com.delphi_tech.ows.policyinquiryservice.PolicyInquiryRequestType;
import com.delphi_tech.ows.policyinquiryservice.PolicyInquiryResultType;
import dti.oasis.recordset.Record;
import dti.pm.policymgr.service.impl.PolicyInquiryItemResultMT;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   2/24/12
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/19/2016       cesar       issue #176679 added loadPolicySingleThreading and loadPolicyMinimalViewSingleThreading
 *                                            to be used by OasisCallable.
 * ---------------------------------------------------
 */

public interface PolicyInquiryServiceManager {
    public PolicyInquiryResultType loadPolicy(PolicyInquiryRequestType policyInquiryRequest);
    public void loadPolicySingleThreading (PolicyInquiryRequestType inquiryRequest, PolicyInquiryRequestParametersType requestParam, String policyNo, String termBaseId, PolicyInquiryItemResultMT policyInquiryItemResultMT);
    public void loadPolicyMinimalViewSingleThreading (Record inputRecord, PolicyInquiryItemResultMT policyInquiryItemResultMT);
}
