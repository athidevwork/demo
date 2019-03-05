package dti.pm.policymgr.service.impl;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.busobjs.PolicyCycleCode;
import dti.pm.busobjs.PolicyStatus;
import dti.pm.busobjs.PolicyViewMode;
import dti.pm.busobjs.ProcessStatus;
import dti.pm.busobjs.TransactionCode;
import dti.pm.busobjs.TransactionStatus;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyManager;
import dti.pm.policymgr.service.EApplicationInquiryFields;
import dti.pm.policymgr.service.PolicyInquiryFields;
import dti.pm.policymgr.service.PolicyInquiryServiceHelper;
import dti.pm.transactionmgr.transaction.Transaction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:   02/24/2016
 *
 * @author wdang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 02/24/2016       wdang       169197 - Initial version.
 * 01/09/2017       tzeng       166929 - Added getLatestTerm(), setPolicyInfoPair().
 * 02/06/2017       lzhang      190834 - 1) Modified loadPolicyInformation/buildPolicyHeader:
 *                                       add transactionStatusCode parameter and set it
 *                                       to inputRecord/PolicyHeader
 *                                       2) Added validatePolicyNosExist and validateTermBaseRecordIdsExist.
 * 04/12/2018       lzhang      191379 - Added LoadPolicyHeader and Modified buildPolicyHeader:
 *                                       add last transaction info to policyHeader
 * 11/28/2018       eyin        197179 - Added loadPolicyDetailList().
 * ---------------------------------------------------
 */
public class PolicyInquiryServiceHelperImpl implements PolicyInquiryServiceHelper {
    private final Logger l = LogUtils.getLogger(getClass());

    @Override
    public List<String[]> getTermPolicyList(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getTermPolicyList", new Object[]{inputRecord});
        }

        List<String[]> termList = new ArrayList<>();
        RecordSet rs = getPolicyManager().findAllPolicyForWS(inputRecord);
        Iterator it = rs.getRecords();
        while (it.hasNext()) {
            Record record = (Record) it.next();
            String[] pair = setPolicyInfoPair(record);
            termList.add(pair);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getTermPolicyList", termList);
        }

        return termList;
    }

    /**
     * Return array include policy no, policy number id, policy term base id.
     * @param record
     * @return Array[]
     */
    private String[] setPolicyInfoPair(Record record) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setPolicyInfoPair", record);
        }
        String[] pair = new String[3];
        pair[0] = record.getStringValue(PolicyInquiryFields.POLICY_TERM_NUMBER_ID);
        pair[1] = record.getStringValue(PolicyInquiryFields.POL_ID);
        pair[2] = record.getStringValue(PolicyInquiryFields.POL_NUMBER_ID);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setPolicyInfoPair", pair);
        }
        return pair;
    }

    @Override
    public Record loadPolicyInformation(String policyNo, String termBaseRecordId, String transactionStatusCode) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadPolicyInformation", new Object[]{policyNo, termBaseRecordId});
        }

        Record inputRecord = new Record();
        inputRecord.setFieldValue("policyNo", policyNo);
        inputRecord.setFieldValue("termBaseId", termBaseRecordId);
        inputRecord.setFieldValue(PolicyInquiryFields.TRANSACTION_STATUS_CODE, transactionStatusCode);
        Record output = getPolicyManager().loadPolicyDetailForWS(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadPolicyInformation", output);
        }

        return output;
    }

    @Override
    public RecordSet loadPolicyDetailList(String policyNo, String termBaseRecordId, String transactionStatusCode) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadPolicyDetailList", new Object[]{policyNo, termBaseRecordId});
        }

        Record inputRecord = new Record();
        inputRecord.setFieldValue("policyNo", policyNo);
        inputRecord.setFieldValue("termBaseId", termBaseRecordId);
        inputRecord.setFieldValue(PolicyInquiryFields.TRANSACTION_STATUS_CODE, transactionStatusCode);
        RecordSet rs = getPolicyManager().loadPolicyDetailListForWS(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadPolicyDetailList", rs);
        }

        return rs;
    }

    @Override
    public RecordSet loadPolicyTermList(String policyId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadPolicyTermList", new Object[]{policyId});
        }

        RecordSet rs = getPolicyManager().loadPolicyTermList(policyId);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadPolicyInformation", rs);
        }

        return rs;
    }

    @Override
    public PolicyHeader buildPolicyHeader(PolicyHeader policyHeader, Record policyRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "buildPolicyHeader", new Object[]{policyRecord});
        }

        Transaction transaction = new Transaction();
        transaction.setTransactionLogId(policyRecord.getStringValue("lastTransactionId", ""));
        transaction.setPolicyId(policyRecord.getStringValue("polId", ""));
        transaction.setTransEffectiveFromDate(policyRecord.getStringValue("transactionEffectiveDate", ""));
        transaction.setTransactionCode(TransactionCode.getInstance(policyRecord.getStringValue("transactionCode", "")));
        transaction.setTransactionStatusCode(TransactionStatus.getInstance(policyRecord.getStringValue("transactionStatusCode", "")));
        policyHeader.setLastTransactionInfo(transaction);
        policyHeader.setLastTransactionId(transaction.getTransactionLogId());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "buildPolicyHeader", policyHeader);
        }
        return policyHeader;
    }

    @Override
    public String[] getLatestTerm(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getLatestTerm", inputRecord);
        }

        Record record = getPolicyManager().getLatestTerm(inputRecord);
        String[] pair = null;
        if (EApplicationInquiryFields.hasPolicyTermNumberId(record)) {
            pair = setPolicyInfoPair(record);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getLatestTerm", inputRecord);
        }

        return pair;
    }

    /**
     * Identify whether policyNos exist in system
     * <p/>
     *
     * @param policyNos
     * @return invalid policyNo
     */
    public String validatePolicyNosExist(String policyNos) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validatePolicyNosExist", new Object[]{policyNos});
        }

        String invalPolicyNos = "";
        Record inputRecord = new Record();
        inputRecord.setFieldValue(PolicyInquiryFields.POLICY_NOS, policyNos);
        invalPolicyNos = getPolicyManager().validatePolicyNosExist(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validatePolicyNosExist", invalPolicyNos);
        }
        return invalPolicyNos;
    }

    /**
     * Identify whether termBaseRecordIds exist in system
     * <p/>
     *
     * @param termBaseRecordIds
     * @return invalid termBaseRecordIds
     */
    public String validateTermBaseRecordIdsExist(String termBaseRecordIds) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateTermBaseRecordIdsExist", new Object[]{termBaseRecordIds});
        }

        String invaltermBaseRecordIds = "";
        Record inputRecord = new Record();
        inputRecord.setFieldValue(PolicyInquiryFields.TERM_BASE_RECORD_IDS, termBaseRecordIds);
        invaltermBaseRecordIds = getPolicyManager().validateTermBaseRecordIdsExist(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateTermBaseRecordIdsExist", invaltermBaseRecordIds);
        }
        return invaltermBaseRecordIds;
    }

    /**
     * load policy header
     * <p/>
     *
     * @param policyNo
     * @param termBaseRecordId
     * @param transactionStatusCode
     * @return policyHeader
     */
    public PolicyHeader loadPolicyHeader(String policyNo, String termBaseRecordId, String transactionStatusCode) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadPolicyHeader", new Object[]{policyNo});
        }
        PolicyHeader policyHeader = new PolicyHeader();
        policyHeader = getPolicyManager().loadPolicyHeaderForWS(policyNo, termBaseRecordId, transactionStatusCode);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadPolicyHeader", policyNo);
        }
        return policyHeader;
    }

    public PolicyManager getPolicyManager() {
        return m_policyManager;
    }

    public void setPolicyManager(PolicyManager policyManager) {
        m_policyManager = policyManager;
    }

    private PolicyManager m_policyManager;
}
