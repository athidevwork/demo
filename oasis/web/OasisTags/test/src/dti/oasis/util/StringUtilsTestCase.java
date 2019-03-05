package dti.oasis.util;

import dti.oasis.test.TestCase;

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
public class StringUtilsTestCase extends TestCase {
    public StringUtilsTestCase(String testCaseName) {
        super(testCaseName);
    }

    public void testCapitilizeFirstLetter() {
        assertEquals("PropertyName", StringUtils.capitalizeFirstLetter("propertyName"));
        assertEquals("P", StringUtils.capitalizeFirstLetter("p"));
    }
}
