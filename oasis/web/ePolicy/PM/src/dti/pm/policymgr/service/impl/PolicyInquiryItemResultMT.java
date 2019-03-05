package dti.pm.policymgr.service.impl;

import com.delphi_tech.ows.policyinquiryservice.PolicyInquiryResultType;
import dti.oasis.concurrent.OasisTaskInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:   06/19/2016
 *
 * @author cesar valencia
 */

/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/19/2016       cesar       issue #176679 added loadPolicySingleThreading to be used by PolicyInquiryCallable.
 * ---------------------------------------------------
 */

public class PolicyInquiryItemResultMT {

    public List<String> getPartyList() {
        return partyList;
    }

    public void setPartyList(List<String> partyList) {
        this.partyList = partyList;
    }

    public PolicyInquiryResultType getPolicyInquiryResult() {
        return policyInquiryResult;
    }

    public void setPolicyInquiryResult(PolicyInquiryResultType policyInquiryResult) {
        this.policyInquiryResult = policyInquiryResult;
    }

    public OasisTaskInfo getOasisTaskInfo() {
        return oasisTaskInfo;
    }

    private List<String> partyList = new ArrayList<String>();
    private PolicyInquiryResultType policyInquiryResult = new PolicyInquiryResultType();
    private OasisTaskInfo oasisTaskInfo = new OasisTaskInfo();

}
