package dti.oasis.test.junit5.extension;

import dti.oasis.request.RequestLifecycleAdvisor;
import dti.oasis.test.app.ApplicationContextHelper;
import dti.oasis.test.injection.BeanInjector;

import dti.oasis.test.mock.MockUtil;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

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
public class OasisExtension implements TestInstancePostProcessor, BeforeTestExecutionCallback, AfterTestExecutionCallback {
    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) {
        // Init configuration.
        ApplicationContextHelper.getInstance().init();
        // Inject beans.
        BeanInjector.getInstance().injectDependency(testInstance);
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        RequestLifecycleAdvisor.getInstance().initialize(MockUtil.mockRequest());
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        RequestLifecycleAdvisor.getInstance().terminate();
    }
}
