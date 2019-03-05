package dti.pm.policymgr.service;

import com.delphi_tech.ows.account.AccountHolderType;
import com.delphi_tech.ows.account.BillingAccountDetailType;
import com.delphi_tech.ows.account.EffectivePeriodType;
import com.delphi_tech.ows.account.IssueCompanyType;
import com.delphi_tech.ows.account.LinkedPolicyType;
import com.delphi_tech.ows.account.PaymentOptionType;
import com.delphi_tech.ows.account.PrincipalBillingAccountInformationType;
import com.delphi_tech.ows.account.ReferredPartyType;
import com.delphi_tech.ows.common.ExtendedStatusType;
import com.delphi_tech.ows.common.MessageStatusType;
import com.delphi_tech.ows.policy.MedicalMalpracticePolicyType;
import com.delphi_tech.ows.policychangeservice.MedicalMalpracticePolicyChangeRequestType;
import com.delphi_tech.ows.policychangeservice.MedicalMalpracticePolicyChangeResultType;
import dti.oasis.app.AppException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.request.service.WebServiceClientHelper;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.ows.common.MessageStatusHelper;
import dti.pm.billingmgr.BillingAccountChangeWSClientManager;
import dti.pm.billingmgr.BillingFields;
import dti.pm.billingmgr.BillingManager;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.workflowmgr.jobqueuemgr.impl.JobProcessor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2015 Delphi Technology, inc. (dti)</p>
 * Date:   01/22/2016
 *
 * @author ssheng
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/22/2016        ssheng      168559 - Created this interface for reusing the method.
 * 05/16/2016        lzhang      170647 - Modify performRateAction(), performIssuePolicy():
 *                                        add messageStatusType input parameter
 *                                        and boolean return value type
 * 01/19/2017        wrong       166929 - Added parameter isIgnoreSoftValidationActionB in
 *                                        performIssuePolicy()
 * 04/17/2017        tzeng       166929 - Added parameter isIgnoreSoftValidationToRateB in performRateAction
 * ---------------------------------------------------
 */
public interface PolicyChangeServiceHelper {


    /*
     * Perform Rate.
     */
    public MessageStatusType performRateAction(PolicyHeader policyHeader, TransactionManager transactionManager, Boolean isIgnoreSoftValidationToRateB);

    public void performBillingSetupTransaction(PolicyHeader policyHeader, MedicalMalpracticePolicyChangeRequestType policyChangeRequest,
                                               boolean isIssueActionB);

    public MessageStatusType performIssuePolicy(PolicyHeader policyHeader, TransactionManager transactionManager, Boolean isIgnoreSoftValidationActionB);

}
