package dti.pm.policymgr.service.impl;

import com.delphi_tech.ows.policyinquiryservice.PolicyInquiryRequestParametersType;
import com.delphi_tech.ows.policyinquiryservice.PolicyInquiryRequestType;
import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.concurrent.pool.OasisCallable;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.util.LogUtils;
import dti.ows.common.MessageStatusAppException;
import dti.ows.common.MessageStatusHelper;
import dti.pm.policymgr.service.PolicyInquiryServiceManager;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

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
 * 06/19/2016       cesar       issue #176679 added loadPolicySingleThreading to be used by OasisCallable.
 * ---------------------------------------------------
 *
 */

public  class PolicyInquiryCallable extends OasisCallable<PolicyInquiryItemResultMT> {

    public PolicyInquiryCallable(String threadName,
                                 PolicyInquiryRequestParametersType requestParam,
                                 PolicyInquiryRequestType inquiryRequest,
                                 String policyNo,
                                 String termBaseId){
        super();
        this.requestParam = requestParam;
        this.inquiryRequest = inquiryRequest;
        this.policyNo = policyNo;
        this.termBaseId = termBaseId;
        this.threadName = threadName;
    }

    @Override
    public PolicyInquiryItemResultMT execute() {
        PolicyInquiryItemResultMT policyInquiryResultTypeMT = new PolicyInquiryItemResultMT();

        try {
            PolicyInquiryServiceManager policyInquiryServiceManager =  (PolicyInquiryServiceManager)ApplicationContext.getInstance().getBean(BEAN_NAME_POLICY_INQUIRY_SERVICE_MANAGER);


            policyInquiryResultTypeMT.getOasisTaskInfo().setStartDate(new Date());
            policyInquiryServiceManager.loadPolicySingleThreading(inquiryRequest,
                requestParam,
                policyNo,
                termBaseId,
                policyInquiryResultTypeMT);
            policyInquiryResultTypeMT.getOasisTaskInfo().setEndDate(new Date());
            policyInquiryResultTypeMT.getOasisTaskInfo().setTaskName(getThreadName());
            
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR, "Failure invoking the PolicyInquiryCallable: Thread Name: " + this.getThreadName(), e);
            throw ae;
        }

        return policyInquiryResultTypeMT;
    }

    @Override
    public String getThreadName(){
        return  threadName;
    }

    private String threadName;
    private String termBaseId;
    private String policyNo;
    PolicyInquiryRequestParametersType requestParam;
    PolicyInquiryRequestType inquiryRequest;
    private final String BEAN_NAME_POLICY_INQUIRY_SERVICE_MANAGER = "PolicyInquiryServiceManager";
}
