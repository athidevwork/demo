package dti.oasis.test;

import dti.oasis.app.ApplicationContext;
import dti.oasis.app.impl.ApplicationLifecycleAdvisorImpl;
import dti.oasis.request.RequestLifecycleAdvisor;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 17, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/14/2007       wer         Added ability initialize/fail/terminate the RequestLifecycleAdvisor
 * ---------------------------------------------------
 */
public abstract class TestCase extends junit.framework.TestCase {
    public TestCase(String testCaseName) {
        super(testCaseName);
    }

    protected void fail(Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        fail(sw.toString());
    }

    protected void fail(String msg, Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        fail(msg + ":\n" + sw.toString());
    }

    protected void initializeApplicationConfiguration(String applicationName, String applicationConfigFilename) {
        System.setProperty(ApplicationContext.APPLICATION_CONTEXT_CONFIG_FILE_SYSTEM_PROPERTY, applicationConfigFilename);
        initializeApplicationConfiguration(applicationName);
    }
    
    protected void initializeApplicationConfiguration(String applicationName) {
        if (m_advisor == null) {

            // Load the Application Configuration
            m_advisor = new ApplicationLifecycleAdvisorImpl();
            m_advisor.initialize(applicationName);
        }
    }

    protected void initializeRequest(String userId) {
        getRequestLifecycleAdvisor().initializeByUser(userId);
    }

    protected boolean failRequest(Throwable e) {
        return getRequestLifecycleAdvisor().failure(e);
    }

    protected void terminateRequest() {
        getRequestLifecycleAdvisor().terminate();
    }

    public RequestLifecycleAdvisor getRequestLifecycleAdvisor() {
        return (RequestLifecycleAdvisor) ApplicationContext.getInstance().getBean("RequestLifecycleAdvisor");
    }


    private static ApplicationLifecycleAdvisorImpl m_advisor;
}
