package dti.pm.busobjs;

import dti.oasis.test.TestCase;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 26, 2007
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
public class TransactionCodeTestCase extends TestCase {

    public TransactionCodeTestCase(String testCaseName) {
        super(testCaseName);
    }

    public void testTransactionCodeTypes() {

        assertTrue(TransactionCode.ACCEPTPOL.isAcceptPol());
        TransactionCode acceptpol = TransactionCode.getInstance("ACCEPTPOL");
        assertTrue(acceptpol.isAcceptPol());
        assertEquals(1, acceptpol.intValue());
        assertEquals(TransactionCode.ACCEPTPOL, acceptpol);

        assertTrue(TransactionCode.XSRERATE.isXsRerate());
        TransactionCode xsrerate = TransactionCode.getInstance("XSRERATE");
        assertTrue(xsrerate.isXsRerate());
        assertEquals(67, xsrerate.intValue());
        assertEquals(TransactionCode.XSRERATE, xsrerate);

        try {
            TransactionCode invalidCode = TransactionCode.getInstance("Invalid code");
            fail("TransactionCode of \"Invalid code\" incorrectly returned a value: " + invalidCode);
        }
        catch (Exception e) {
            //good
        }
    }
}
