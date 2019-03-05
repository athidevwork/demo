package dti.ci.test.example;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   3/1/2018
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
public class FooMessageProvider {
    private static FooMessageProvider instance;

    private FooMessageProvider() { }

    public static FooMessageProvider getInstance() {
        if (instance == null) {
            synchronized (FooMessageProvider.class) {
                if (instance == null) {
                    instance = new FooMessageProvider();
                }
            }
        }
        return instance;
    }

    public String foo() {
        return "Foo from message manager";
    }
}
