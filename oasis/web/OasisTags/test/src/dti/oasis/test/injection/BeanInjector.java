package dti.oasis.test.injection;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   2/27/2018
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
public abstract class BeanInjector {
    private static BeanInjector instance;

    public static BeanInjector getInstance() {
        if (instance == null) {
            synchronized (BeanInjector.class) {
                if (instance == null) {
                    instance = new AutoWiredBeanInjector();
                }
            }
        }

        return instance;
    }

    public abstract void injectDependency(Object testInstance);
}
