package dti.pm.policymgr.service.impl;

import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.concurrent.pool.OasisCallable;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
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
 * 06/19/2016       cesar       issue #176679 added loadPolicyMinimalViewSingleThreading to be used by OasisCallable.
 * ---------------------------------------------------
 *
 */

public class PolicyInquiryMinimalViewCallable extends OasisCallable<PolicyInquiryItemResultMT> {

    public PolicyInquiryMinimalViewCallable(String threadName, Record inputRecord){
        super();
        this.threadName = threadName;
        this.inputRecord = inputRecord;

    }

    @Override
    public PolicyInquiryItemResultMT execute() {
        Logger l = LogUtils.enterLog(PolicyInquiryCallable.class, "call", getThreadName());

        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "PolicyInquiryCallable", new Object[]{getThreadName()});
        }

        PolicyInquiryItemResultMT policyInquiryResultTypeMT = new PolicyInquiryItemResultMT();

        try {
            PolicyInquiryServiceManager policyInquiryServiceManager =  (PolicyInquiryServiceManager) ApplicationContext.getInstance().getBean(BEAN_NAME_POLICY_INQUIRY_SERVICE_MANAGER);

            policyInquiryResultTypeMT.getOasisTaskInfo().setStartDate(new Date());
            policyInquiryServiceManager.loadPolicyMinimalViewSingleThreading(inputRecord, policyInquiryResultTypeMT);

            policyInquiryResultTypeMT.getOasisTaskInfo().setEndDate(new Date());
            policyInquiryResultTypeMT.getOasisTaskInfo().setTaskName(getThreadName());

        }
        catch (AppException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR, "Failure invoking the PolicyInquiryMinimalViewCallable: Thread Name: " + this.getThreadName(), e);
            throw ae;
        }

        l.exiting(PolicyInquiryCallable.class.getName(), "execute", getThreadName());
        return policyInquiryResultTypeMT;


    }

    @Override
    public String getThreadName(){
        return  threadName;
    }

    private String threadName;
    private Record inputRecord;

    private final String BEAN_NAME_POLICY_INQUIRY_SERVICE_MANAGER = "PolicyInquiryServiceManager";
}
