package dti.pm.test;

import dti.oasis.test.TestCase;


/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 16, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public class EnvTestCase extends TestCase {
    public EnvTestCase(String testCaseName) {
        super(testCaseName);
    }

    protected void setUp() throws Exception {
        super.setUp();
        initializeApplicationConfiguration("PMTest", "dti/applicationConfig.xml");
    }

    public void testEnv(){
    }

}
