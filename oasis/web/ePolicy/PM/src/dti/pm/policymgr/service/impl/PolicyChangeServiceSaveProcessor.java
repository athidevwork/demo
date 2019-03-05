package dti.pm.policymgr.service.impl;

import com.delphi_tech.ows.policychangeservice.MedicalMalpracticePolicyChangeRequestType;
import com.delphi_tech.ows.policychangeservice.MedicalMalpracticePolicyChangeResultType;
import dti.pm.busobjs.PolicyViewMode;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.transactionmgr.transaction.Transaction;
import dti.pm.busobjs.TransactionCode;
import dti.oasis.recordset.Record;


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
 * 11/20/2014       awu         Renamed savePolicy to changePolicy.
 * ---------------------------------------------------
 */
public interface PolicyChangeServiceSaveProcessor {
    public MedicalMalpracticePolicyChangeResultType changePolicy(MedicalMalpracticePolicyChangeRequestType policyChangeRequest, MedicalMalpracticePolicyChangeResultType policyChangeResult);

    public void owsLockPolicy(PolicyHeader policyHeader);

    public void owsUnlockPolicy(PolicyHeader policyHeader);

    public PolicyHeader owsLoadPolicyHeader(String policyNo, String policyNumberId, String termBaseRecordId, PolicyViewMode desiredViewMode);

    public Transaction owsCreateTransaction(PolicyHeader policyHeader, Record inputRecord, String transactionEffectiveFromDate, TransactionCode transactionCode);


}
