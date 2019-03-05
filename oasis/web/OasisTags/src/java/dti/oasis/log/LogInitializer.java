package dti.oasis.log;

import dti.oasis.app.ApplicationContext;
import dti.oasis.app.DefaultApplicationLifecycleHandler;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

/**
 * The LogInitializer initializes the JDK logging
 * <p/>
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * Date:   April 19, 2010
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
public class LogInitializer extends DefaultApplicationLifecycleHandler {

    /** The bean name of a LogInitializer extension if it is configured in the ApplicationContext. */
    public static final String BEAN_NAME = "LogInitializer";

    /**
     * Return an instance of the LogInitializer.
     */
    public synchronized static final LogInitializer getInstance() {
        if (c_instance == null) {
            if (ApplicationContext.getInstance().hasBean(BEAN_NAME)) {
                c_instance = (LogInitializer) ApplicationContext.getInstance().getBean(BEAN_NAME);
            }
            else{
                c_instance = new LogInitializer();
            }
        }
        return c_instance;
    }

    @Override
    public void initialize() {
        if (!StringUtils.isBlank(m_loggingFormatter)) {
            LogUtils.setFormatter(getLoggingFormatter());
        }
    }

    public String getLoggingFormatter() {
        return m_loggingFormatter;
    }

    public void setLoggingFormatter(String loggingFormatter) {
        m_loggingFormatter = loggingFormatter;
    }

    private String m_loggingFormatter;

    private static LogInitializer c_instance;
}
