package dti.oasis.jpa.mock;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   12/10/2015
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class MockIdGenerator {
    private static MockIdGenerator mockIdGenerator = new MockIdGenerator();
    private long currentId = 10000000l;

    public static MockIdGenerator getInstance() {
        return mockIdGenerator;
    }

    private MockIdGenerator() {
    }

    public synchronized Long getNextValue() {
        return currentId++;
    }
}
