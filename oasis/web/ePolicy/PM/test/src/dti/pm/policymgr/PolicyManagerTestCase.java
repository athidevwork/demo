package dti.pm.policymgr;

import dti.oasis.app.ApplicationContext;
import dti.oasis.recordset.Record;
import dti.pm.test.PMTestCase;
import dti.pm.busobjs.PolicyCycleCode;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 16, 2006
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
public class PolicyManagerTestCase extends PMTestCase {
    public PolicyManagerTestCase(String testCaseName) {
        super(testCaseName);
    }

    public void testLoadAllPolicy() {

        PolicyHeader policyHeader = new PolicyHeader();
        policyHeader.setPolicyNo("H000079");
        policyHeader.setPolicyCycleCode(PolicyCycleCode.POLICY);

        PolicyIdentifier policyIdentifier = new PolicyIdentifier();
        policyIdentifier.setPolicyNo("H000079");
        policyIdentifier.setPolicyTermHistoryId("14192629");
        policyHeader.setPolicyIdentifier(policyIdentifier);

        Term term = new Term();
        term.setPolicyTermHistoryId("14192629");
        term.setEffectiveFromDate("01/01/2006");
        term.setEffectiveToDate("01/01/2007");
        policyHeader.addPolicyTerm(term);

        Record input = new Record();

        Record output = getPolicyManager().loadPolicyDetail(policyHeader, input);

    }

    public PolicyManager getPolicyManager() {
        return (PolicyManager) ApplicationContext.getInstance().getBean("PolicyManager");
    }
}
