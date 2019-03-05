package dti.oasis.test;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 23, 2007
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

public class ApplicationConfigTestCase extends TestCase {
    private static final String APPLICATION_NAME = "application.name";
    private static final String APPLICATION_DEFAULT_NAME = "ApplicationConfigTest";

    public ApplicationConfigTestCase(String testCaseName) {
        super(testCaseName);
    }

    protected void setUp() throws Exception {
        super.setUp();

    }

    public void testEnv() {
        String appName = System.getProperty(APPLICATION_NAME);
        if (appName == null || appName.length() <= 0)
            appName = APPLICATION_DEFAULT_NAME;
        initializeApplicationConfiguration(appName);
    }


}
