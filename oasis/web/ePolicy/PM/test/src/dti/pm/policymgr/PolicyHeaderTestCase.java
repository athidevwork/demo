package dti.pm.policymgr;

import dti.oasis.test.TestCase;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordBeanMapper;
import dti.oasis.busobjs.YesNoFlag;
import dti.pm.busobjs.PolicyCycleCode;
import dti.pm.busobjs.PolicyStatus;
import dti.pm.busobjs.TransactionStatus;
import dti.pm.busobjs.TransactionTypeCode;
import dti.pm.busobjs.TransactionCode;
import dti.pm.busobjs.PMStatusCode;
import dti.pm.transactionmgr.transaction.Transaction;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.riskmgr.RiskHeader;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 25, 2007
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class PolicyHeaderTestCase extends TestCase {
    public PolicyHeaderTestCase(String testCaseName) {
        super(testCaseName);
    }

    public void testPolicyHeaderMapping() {
        Record rec = new Record();

        String policyHolderName = "Bill Reeder";
        rec.setFieldValue("policyHolderName", policyHolderName);

        PolicyCycleCode policyCycleCode = PolicyCycleCode.POLICY;
        PolicyFields.setPolicyCycleCode(rec, policyCycleCode);

        String wipB = "Y";
        rec.setFieldValue("wipB", wipB);

        String shortTermB = "N";
        rec.setFieldValue("shortTermB", shortTermB);

        String termWrittenPremium = "5";
        rec.setFieldValue("termWrittenPremium", termWrittenPremium);

        String validRenewalCandidate = "N";
        rec.setFieldValue("validRenewalCandidate", validRenewalCandidate);

        String showViewMode = "Y";
        rec.setFieldValue("showViewMode", showViewMode);

        String coveragePartConfigured = "Y";
        rec.setFieldValue("coveragePartConfigured", coveragePartConfigured);

        PolicyStatus policyStatus = PolicyStatus.ACCEPTED;
        PolicyHeaderFields.setPolicyStatus(rec, policyStatus);

        String quoteEndorsementExists = "Y";
        rec.setFieldValue("quoteEndorsementExists", quoteEndorsementExists);

        String quoteTempVersionExists = "N";
        rec.setFieldValue("quoteTempVersionExists", quoteTempVersionExists);

        String initTermB = "N";
        rec.setFieldValue("initTermB", initTermB);

        PolicyHeader policyHeader = new PolicyHeader();
        RecordBeanMapper mapper = new RecordBeanMapper();
        mapper.map(rec, policyHeader);

        assertEquals(policyHolderName, policyHeader.getPolicyHolderName());
        assertEquals(policyCycleCode, policyHeader.getPolicyCycleCode());
        assertEquals(YesNoFlag.getInstance(wipB).booleanValue(), policyHeader.isWipB());
        assertEquals(YesNoFlag.getInstance(shortTermB).booleanValue(), policyHeader.isShortTermB());
        assertEquals(Double.valueOf(termWrittenPremium), policyHeader.getTermWrittenPremium());
        assertEquals(YesNoFlag.getInstance(validRenewalCandidate).booleanValue(), policyHeader.isValidRenewalCandidate());
        assertEquals(YesNoFlag.getInstance(showViewMode).booleanValue(), policyHeader.isShowViewMode());
        assertEquals(YesNoFlag.getInstance(coveragePartConfigured).booleanValue(), policyHeader.isCoveragePartConfigured());
        assertEquals(policyStatus, policyHeader.getPolicyStatus());
        assertEquals(YesNoFlag.getInstance(quoteEndorsementExists).booleanValue(), policyHeader.isQuoteEndorsementExists());
        assertEquals(YesNoFlag.getInstance(quoteTempVersionExists).booleanValue(), policyHeader.isQuoteTempVersionExists());
        assertEquals(YesNoFlag.getInstance(initTermB).booleanValue(), policyHeader.isInitTermB());
    }

    public void testTransactionMapping() {
        Record rec = new Record();

        TransactionStatus transactionStatusCode = TransactionStatus.INPROGRESS;
        rec.setFieldValue("transactionStatusCode", transactionStatusCode);

        TransactionCode transactionCode = TransactionCode.OOSENDORSE;
        TransactionFields.setTransactionCode(rec, transactionCode);

        TransactionTypeCode transactionTypeCode = TransactionTypeCode.CANCEL;
        TransactionFields.setTransactionTypeCode(rec, transactionTypeCode);

        Transaction transaction = new Transaction();
        RecordBeanMapper mapper = new RecordBeanMapper();
        mapper.map(rec, transaction);

        assertEquals(transactionStatusCode, transaction.getTransactionStatusCode());
        assertEquals(transactionCode, transaction.getTransactionCode());
        assertEquals(transactionTypeCode, transaction.getTransactionTypeCode());
    }

    public void testRiskHeaderMapping() {
        Record rec = new Record();

        String rollingIbnrIndicator = "Y";
        rec.setFieldValue("rollingIbnrIndicator", rollingIbnrIndicator);

        PMStatusCode riskStatusCode = PMStatusCode.ACTIVE;
        rec.setFieldValue("riskStatusCode", riskStatusCode);

        RiskHeader riskHeader = new RiskHeader();
        RecordBeanMapper mapper = new RecordBeanMapper();
        mapper.map(rec, riskHeader);

        assertEquals(YesNoFlag.getInstance(rollingIbnrIndicator), riskHeader.getRollingIbnrIndicator());
        assertEquals(riskStatusCode, riskHeader.getRiskStatusCode());
    }
}
