package dti.pm.test;

import dti.oasis.test.TestCase;

import java.util.logging.LogManager;
import java.io.IOException;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 18, 2006
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
public class PMTestCase extends TestCase {

    public PMTestCase(String testCaseName) {
        super(testCaseName);
    }

    protected void setUp() throws Exception {
        super.setUp();
        initializeApplicationConfiguration("PM", "dti/pmTestConfig.xml");
    }
}
