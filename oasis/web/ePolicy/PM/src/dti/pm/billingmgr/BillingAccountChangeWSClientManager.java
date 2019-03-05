package dti.pm.billingmgr;

import com.delphi_tech.ows.account.PrincipalBillingAccountInformationType;
import com.delphi_tech.ows.billingaccountchangeservice.BillingAccountChangeRequestType;
import com.delphi_tech.ows.billingaccountchangeservice.BillingAccountChangeResultType;
import dti.oasis.recordset.Record;
import dti.pm.policymgr.PolicyHeader;

/**
 * Created by IntelliJ IDEA.
 * User: AWU
 * Date: 10/27/14
 * Time: 4:22 PM
 * To change this template use File | Settings | File Templates.
 */
public interface BillingAccountChangeWSClientManager {
    public Record saveBillingForEPolicy(PolicyHeader policyHeader, Record inputRecord, boolean isBillingExists);

    public void saveBillingForPolicyChangeService(PrincipalBillingAccountInformationType principalBillingAccountInformation, PolicyHeader policyHeader);
}
