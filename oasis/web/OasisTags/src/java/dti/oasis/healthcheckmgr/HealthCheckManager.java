package dti.oasis.healthcheckmgr;

import dti.oasis.app.ApplicationContext;
import dti.oasis.healthcheckmgr.impl.HealthCheckManagerImpl;
import dti.oasis.messagemgr.MessageManager;

import java.util.Iterator;

/**
 * This class manages the health check of Web Services.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 07, 2010
 *
 * @author fcbibire
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * ---------------------------------------------------
 */

public abstract class HealthCheckManager {

    public static final String BEAN_NAME = "HealthCheckManager";

    /**
     * Returns an instance of HealthCheck manager.
     *
     * @return HealthCheckManager, an instance of HealthCheckManager
     */
    public synchronized static final HealthCheckManager getInstance() {
        if (c_instance == null) {
            if (ApplicationContext.getInstance().hasBean(HealthCheckManager.BEAN_NAME)) {
                c_instance = (HealthCheckManager) ApplicationContext.getInstance().getBean(MessageManager.BEAN_NAME);
            }
            else {
                c_instance = new HealthCheckManagerImpl();
            }
        }
        return c_instance;
    }

    /**
     * Method to get a Module by name of the module.
     * @param name
     * @return
     */
    public abstract HealthCheckWebAppModule getModuleByName(String name);

    /**
     * Method to get an Iterator to the Module list.
     * @return
     */
    public abstract Iterator getModuleIterator();

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    protected HealthCheckManager() {
    }

    private static HealthCheckManager c_instance;
}

