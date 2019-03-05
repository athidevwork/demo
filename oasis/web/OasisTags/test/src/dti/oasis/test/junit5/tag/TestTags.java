package dti.oasis.test.junit5.tag;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   3/19/2018
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
public enum TestTags {
    INTEGRATION(Constants.INTEGRATION),
    MOCK(Constants.MOCK);

    TestTags(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public static TestTags getTag(String value) {
        for(TestTags tag : values()) {
            if(tag.value().equalsIgnoreCase(value)) {
                return tag;
            }
        }

        throw new IllegalArgumentException("Illegal tag value: " + value);
    }

    private String value;

    public static final String INTEGRATION_VALUE = Constants.INTEGRATION;
    public static final String MOCK_VALUE = Constants.MOCK;

    private static class Constants {
        private static final String INTEGRATION = "Integration";
        private static final String MOCK = "Mock";
    }
}
