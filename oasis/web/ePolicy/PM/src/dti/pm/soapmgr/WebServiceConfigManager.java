package dti.pm.soapmgr;

import dti.oasis.app.ApplicationContext;
import dti.oasis.messagemgr.MessageManager;

import java.util.Iterator;

/**
 * This class manages the Web Services Configuration.
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 01, 2011
 *
 * @author fcbibire
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * ---------------------------------------------------
 */
public abstract class WebServiceConfigManager {

    public static final String BEAN_NAME = "WebServiceConfigManager";

    /**
     * Returns an instance of WebServiceConfigManager manager.
     *
     * @return HealthCheckManager, an instance of HealthCheckManager
     */
    public synchronized static final WebServiceConfigManager getInstance() {
        if (c_instance == null) {
            if (ApplicationContext.getInstance().hasBean(WebServiceConfigManager.BEAN_NAME)) {
                c_instance = (WebServiceConfigManager) ApplicationContext.getInstance().getBean(MessageManager.BEAN_NAME);
            }
            else {
                c_instance = new WebServiceConfigManagerImpl();
            }
        }
        return c_instance;
    }

    /**
     * Method to get a Module by name of the module.
     * @param name
     * @return
     */
    public abstract WebServiceModule getWebServiceModuleByName(String name);

    /**
     * Method to get an Iterator to the Module list.
     * @return
     */
    public abstract Iterator getModuleIterator();

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    protected WebServiceConfigManager() {
    }

    private static WebServiceConfigManager c_instance;

}
