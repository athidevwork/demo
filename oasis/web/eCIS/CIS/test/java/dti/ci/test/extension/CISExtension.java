package dti.ci.test.extension;

import dti.ci.test.testdata.TestData;
import dti.oasis.app.ApplicationContext;
import dti.oasis.test.app.ApplicationContextHelper;
import dti.oasis.util.LogUtils;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   3/14/2018
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
public class CISExtension implements BeforeAllCallback {
    private final Logger l = LogUtils.getLogger(getClass());

    /**
     * Callback that is invoked once <em>before</em> all tests in the current
     * container.
     *
     * @param context the current extension context; never {@code null}
     */
    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        // Init configuration.
        ApplicationContextHelper.getInstance().init();

        // Init CIS test Data.
        ApplicationContext.getInstance().getBean(TestData.class).init();
    }
}
